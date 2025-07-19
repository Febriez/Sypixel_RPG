package com.febrie.rpg.player;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.impl.PlayerFirestoreService;
import com.febrie.rpg.dto.player.PlayerDataDTO;
import com.febrie.rpg.dto.player.PlayerDTO;
import com.febrie.rpg.dto.player.PlayerProfileDTO;
import com.febrie.rpg.dto.player.ProgressDTO;
import com.febrie.rpg.dto.player.StatsDTO;
import com.febrie.rpg.dto.player.TalentDTO;
import com.febrie.rpg.dto.player.WalletDTO;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.level.LevelSystem;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.TimeUnit;

/**
 * RPG 플레이어 데이터 관리자
 * 플레이어의 RPG 데이터를 메모리에 캐싱하고 관리
 * PlayerService 기능 통합
 *
 * @author Febrie, CoffeeTory
 */
public class RPGPlayerManager implements Listener {

    private final RPGMain plugin;
    private final PlayerFirestoreService playerService;
    private final Map<UUID, RPGPlayer> players = new ConcurrentHashMap<>();

    // 저장 쿨다운 관리 - AtomicLong으로 thread-safe 보장
    private final Map<UUID, AtomicLong> lastSaveTime = new ConcurrentHashMap<>();
    private static final long SAVE_COOLDOWN = 30000; // 30초

    private RPGPlayerManager(@NotNull RPGMain plugin, @Nullable PlayerFirestoreService playerService) {
        this.plugin = plugin;
        this.playerService = playerService;
        if (playerService == null) {
        }
    }
    
    /**
     * Factory method to create and initialize RPGPlayerManager
     */
    public static RPGPlayerManager create(@NotNull RPGMain plugin, @Nullable PlayerFirestoreService playerService) {
        RPGPlayerManager manager = new RPGPlayerManager(plugin, playerService);
        manager.initialize();
        return manager;
    }
    
    /**
     * Initialize the manager after construction
     */
    private void initialize() {
        // 이미 접속중인 플레이어들 로드
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            loadPlayerAsync(player);
        }

        // 자동 저장 스케줄러 시작
        startAutoSaveScheduler();
    }

    /**
     * 플레이어 전체 플레이총합
     */
    public long getTotalPlaytime() {
        return players.values().stream()
                .mapToLong(RPGPlayer::getTotalPlaytime)
                .sum();
    }

    /**
     * 플레이어 접속 시 데이터 로드
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        loadPlayerAsync(event.getPlayer());
    }

    /**
     * 플레이어 퇴장 시 데이터 저장 및 정리
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        RPGPlayer rpgPlayer = players.remove(uuid);
        if (rpgPlayer != null) {
            // 비동기로 저장하되 시간 제한 설정
            savePlayerData(rpgPlayer, true)
                .orTimeout(5, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    LogUtil.error("플레이어 데이터 저장 실패: " + player.getName(), ex);
                    // 실패 시 로컬 백업 또는 대기열에 추가
                    addToFailedSaveQueue(uuid, rpgPlayer);
                    return false;
                });
        }

        lastSaveTime.remove(uuid);
    }
    
    /**
     * 저장 실패 큐에 추가 (나중에 재시도)
     */
    private void addToFailedSaveQueue(@NotNull UUID uuid, @NotNull RPGPlayer rpgPlayer) {
        // 실패한 저장을 대기열에 넣고 나중에 재시도 필요
    }

    /**
     * 플레이어 데이터 비동기 로드 (PlayerService에서 이동)
     */
    private void loadPlayerAsync(@NotNull Player player) {
        String uuid = player.getUniqueId().toString();

        CompletableFuture.supplyAsync(() -> {
            try {
                // Firestore에서 데이터 로드
                if (playerService == null) {
                    return createNewPlayer(player);
                }
                
                PlayerDataDTO playerData = playerService.getByUuid(player.getUniqueId()).get(5, TimeUnit.SECONDS);
                
                if (playerData == null || playerData.profile().lastPlayed() == 0) {
                    // 신규 플레이어
                    return createNewPlayer(player);
                }

                // 기존 플레이어 데이터 로드
                
                // PlayerDataDTO에서 각 DTO 추출
                // 현재 PlayerDataDTO는 profile과 wallet만 포함하므로
                // 다른 데이터는 기본값으로 생성
                PlayerDTO playerDTO = new PlayerDTO(
                    playerData.profile().uuid().toString(),
                    playerData.profile().name(),
                    playerData.profile().lastPlayed(),
                    0L,  // totalPlaytime - 기본값
                    null,  // job - 기본값
                    false  // isAdmin - 기본값
                );
                StatsDTO statsDTO = new StatsDTO();  // 기본값
                TalentDTO talentDTO = new TalentDTO();  // 기본값
                ProgressDTO progressDTO = new ProgressDTO();  // 기본값
                WalletDTO walletDTO = playerData.wallet();

                // RPGPlayer 생성 및 DTO 데이터 적용
                RPGPlayer rpgPlayer = new RPGPlayer(player);
                applyDTOsToPlayer(rpgPlayer, playerDTO, statsDTO, talentDTO, progressDTO, walletDTO);

                return rpgPlayer;

            } catch (Exception e) {
                LogUtil.error("플레이어 데이터 로드 실패: " + player.getName(), e);
                // 오프라인 모드로 폴백
                return new RPGPlayer(player);
            }
        }).thenAccept(rpgPlayer -> {
            players.put(player.getUniqueId(), rpgPlayer);
        });
    }

    /**
     * 신규 플레이어 생성
     */
    private RPGPlayer createNewPlayer(@NotNull Player player) {
        RPGPlayer rpgPlayer = new RPGPlayer(player);

        // 기본 데이터로 DTO 생성
        PlayerDTO playerDTO = new PlayerDTO(
                player.getUniqueId().toString(),
                player.getName(),
                System.currentTimeMillis(),  // lastLogin
                0L,                          // totalPlaytime
                null,                        // job
                false                        // isAdmin
        );
        StatsDTO statsDTO = new StatsDTO();
        TalentDTO talentDTO = new TalentDTO();
        ProgressDTO progressDTO = new ProgressDTO();
        WalletDTO walletDTO = new WalletDTO();

        // Firestore에 초기 데이터 저장 (비동기)
        if (playerService != null) {
            PlayerDataDTO newPlayerData = PlayerDataDTO.createNew(
                player.getUniqueId(),
                player.getName()
            );
            playerService.save(player.getUniqueId().toString(), newPlayerData)
                .exceptionally(ex -> {
                    LogUtil.error("신규 플레이어 데이터 저장 실패: " + player.getName(), ex);
                    return null;
                });
        }

        return rpgPlayer;
    }

    /**
     * DTO 데이터를 RPGPlayer에 적용
     */
    private void applyDTOsToPlayer(@NotNull RPGPlayer rpgPlayer,
                                   @NotNull PlayerDTO playerDTO,
                                   @NotNull StatsDTO statsDTO,
                                   @NotNull TalentDTO talentDTO,
                                   @NotNull ProgressDTO progressDTO,
                                   @NotNull WalletDTO walletDTO) {

        // 기본 정보
        if (playerDTO.job() != null) {
            rpgPlayer.setJob(playerDTO.job());
        }
        rpgPlayer.setTotalPlaytime(playerDTO.totalPlaytime());

        // 스탯 적용
        rpgPlayer.getStats().applyFromDTO(statsDTO);

        // 특성 적용
        rpgPlayer.getTalents().applyFromDTO(talentDTO);

        // 진행도 적용
        if (progressDTO.currentLevel() > 1) {
            // 레벨에 맞는 경험치 설정
            long totalExp = calculateTotalExpForLevel(progressDTO.currentLevel(), rpgPlayer.getJob());
            rpgPlayer.setExperience(totalExp);
        }
        rpgPlayer.setMobsKilled(progressDTO.mobsKilled());
        rpgPlayer.setPlayersKilled(progressDTO.playersKilled());
        rpgPlayer.setDeaths(progressDTO.deaths());

        // 재화 적용
        rpgPlayer.getWallet().applyFromDTO(walletDTO);
    }

    /**
     * 플레이어 데이터 저장
     */
    public CompletableFuture<Boolean> savePlayerData(@NotNull RPGPlayer rpgPlayer, boolean force) {
        UUID uuid = rpgPlayer.getPlayerId();

        // 쿨다운 체크 (강제 저장이 아닌 경우) - Thread-safe
        if (!force) {
            AtomicLong lastSave = lastSaveTime.computeIfAbsent(uuid, k -> new AtomicLong(0));
            long currentTime = System.currentTimeMillis();
            long lastSaveTime = lastSave.get();
            
            if (lastSaveTime != 0 && (currentTime - lastSaveTime) < SAVE_COOLDOWN) {
                return CompletableFuture.completedFuture(true);
            }
            
            // CAS (Compare-And-Set)로 원자적 업데이트
            if (!lastSave.compareAndSet(lastSaveTime, currentTime)) {
                // 다른 스레드가 이미 업데이트했음
                return CompletableFuture.completedFuture(true);
            }
        } else {
            // 강제 저장 시에도 마지막 저장 시간 업데이트
            lastSaveTime.computeIfAbsent(uuid, k -> new AtomicLong(0)).set(System.currentTimeMillis());
        }

        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();

            try {
                // RPGPlayer를 DTO로 변환
                PlayerDTO playerDTO = convertToPlayerDTO(rpgPlayer);
                StatsDTO statsDTO = rpgPlayer.getStats().toDTO();
                TalentDTO talentDTO = rpgPlayer.getTalents().toDTO();
                ProgressDTO progressDTO = convertToProgressDTO(rpgPlayer);
                WalletDTO walletDTO = rpgPlayer.getWallet().toDTO();

                // Firestore에 저장
                boolean success = true;
                if (playerService != null) {
                    // PlayerDataDTO 생성 및 저장
                    // 현재 PlayerDataDTO는 profile과 wallet만 포함하므로 최소한의 데이터만 저장
                    PlayerDataDTO playerData = new PlayerDataDTO(
                        new PlayerProfileDTO(
                            UUID.fromString(uuid.toString()),
                            rpgPlayer.getName(),
                            rpgPlayer.getLevel(),
                            rpgPlayer.getExperience(),
                            rpgPlayer.getExperience(),  // totalExp
                            System.currentTimeMillis()  // lastPlayed
                        ),
                        walletDTO
                    );
                    
                    try {
                        playerService.save(uuid.toString(), playerData)
                            .get(10, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        LogUtil.error("플레이어 데이터 저장 실패", e);
                        success = false;
                    }
                } else {
                }

                if (success) {
                }

                return success;

            } catch (Exception e) {
                LogUtil.error("플레이어 데이터 저장 실패: " + rpgPlayer.getName(), e);
                return false;
            }
        });
    }

    /**
     * RPGPlayer를 PlayerDTO로 변환
     */
    private PlayerDTO convertToPlayerDTO(@NotNull RPGPlayer rpgPlayer) {
        Player bukkitPlayer = rpgPlayer.getPlayer();
        long totalPlaytime = rpgPlayer.getTotalPlaytime();

        if (bukkitPlayer.isOnline()) {
            long sessionTime = System.currentTimeMillis() - rpgPlayer.getSessionStartTime();
            totalPlaytime += sessionTime;
        }

        return new PlayerDTO(
                rpgPlayer.getUuid().toString(),
                rpgPlayer.getName(),
                System.currentTimeMillis(),  // lastLogin
                totalPlaytime,
                rpgPlayer.getJob(),
                false  // isAdmin - not tracked in RPGPlayer
        );
    }

    /**
     * RPGPlayer를 ProgressDTO로 변환
     */
    private ProgressDTO convertToProgressDTO(@NotNull RPGPlayer rpgPlayer) {
        return new ProgressDTO(
                rpgPlayer.getLevel(),
                rpgPlayer.getExperience(),
                rpgPlayer.getLevelProgress(),
                rpgPlayer.getMobsKilled(),
                rpgPlayer.getPlayersKilled(),
                rpgPlayer.getDeaths()
        );
    }

    /**
     * 레벨에 필요한 총 경험치 계산 (PlayerService에서 이동)
     */
    private long calculateTotalExpForLevel(int level, JobType job) {
        if (job == null || level <= 1) {
            return 0;
        }
        return LevelSystem.getTotalExpForLevel(level, job);
    }

    /**
     * 자동 저장 스케줄러 (PlayerService에서 이동)
     */
    private void startAutoSaveScheduler() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            saveAllOnlinePlayers();
        }, 12000L, 12000L); // 10분마다
    }

    /**
     * 모든 온라인 플레이어 데이터 저장 (PlayerService에서 이동)
     */
    public CompletableFuture<Void> saveAllOnlinePlayers() {
        CompletableFuture<?>[] futures = players.values().stream()
                .map(rpgPlayer -> savePlayerData(rpgPlayer, false))
                .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(futures);
    }

    /**
     * 직업 변경 (PlayerService에서 이동)
     */
    public CompletableFuture<Boolean> changePlayerJob(@NotNull RPGPlayer rpgPlayer, @NotNull JobType newJob) {
        JobType oldJob = rpgPlayer.getJob();
        rpgPlayer.setJob(newJob);

        // 즉시 저장
        return savePlayerData(rpgPlayer, true).thenApply(success -> {
            if (success) {
            }
            return success;
        });
    }

    /**
     * 모든 플레이어 데이터 가져오기
     */
    @NotNull
    public Collection<RPGPlayer> getAllPlayers() {
        return new ArrayList<>(players.values());
    }

    /**
     * RPG 플레이어 데이터 가져오기
     */
    @Nullable
    public RPGPlayer getPlayer(@NotNull Player player) {
        return players.get(player.getUniqueId());
    }

    /**
     * RPG 플레이어 데이터 가져오기 (UUID)
     */
    @Nullable
    public RPGPlayer getPlayer(@NotNull UUID uuid) {
        return players.get(uuid);
    }

    /**
     * RPG 플레이어 데이터 가져오기 (없으면 생성)
     */
    @NotNull
    public RPGPlayer getOrCreatePlayer(@NotNull Player player) {
        return players.computeIfAbsent(player.getUniqueId(), uuid -> new RPGPlayer(player));
    }

    /**
     * 모든 플레이어 데이터 저장
     */
    public void saveAll() {
        saveAllOnlinePlayers().join();
    }

    /**
     * 접속 중인 플레이어 수
     */
    public int getOnlinePlayerCount() {
        return players.size();
    }
}