package com.febrie.rpg.player;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.FirebaseService;
import com.febrie.rpg.dto.*;
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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
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
    private final FirebaseService firebaseService;
    private final Map<UUID, RPGPlayer> players = new ConcurrentHashMap<>();

    // 저장 쿨다운 관리 (PlayerService에서 이동)
    private final Map<UUID, Long> lastSaveTime = new ConcurrentHashMap<>();
    private static final long SAVE_COOLDOWN = 30000; // 30초

    public RPGPlayerManager(@NotNull RPGMain plugin, @NotNull FirebaseService firebaseService) {
        this.plugin = plugin;
        this.firebaseService = firebaseService;

        // 이미 접속중인 플레이어들 로드
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            loadPlayerAsync(player);
        }

        // 자동 저장 스케줄러 시작
        startAutoSaveScheduler();
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
            // 동기적으로 저장 (서버 종료 시 안전성)
            savePlayerData(rpgPlayer, true).join();
        }

        lastSaveTime.remove(uuid);
    }

    /**
     * 플레이어 데이터 비동기 로드 (PlayerService에서 이동)
     */
    private void loadPlayerAsync(@NotNull Player player) {
        String uuid = player.getUniqueId().toString();

        CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();

            try {
                // Firebase에서 데이터 로드
                PlayerDTO playerDTO = firebaseService.loadPlayer(uuid).get(5, TimeUnit.SECONDS);

                if (playerDTO == null) {
                    // 신규 플레이어
                    LogUtil.info("신규 플레이어 생성: " + player.getName());
                    return createNewPlayer(player);
                }

                // 기존 플레이어 데이터 로드
                StatsDTO statsDTO = firebaseService.loadStats(uuid).get(5, TimeUnit.SECONDS);
                TalentDTO talentDTO = firebaseService.loadTalents(uuid).get(5, TimeUnit.SECONDS);
                ProgressDTO progressDTO = firebaseService.loadProgress(uuid).get(5, TimeUnit.SECONDS);
                WalletDTO walletDTO = firebaseService.loadWallet(uuid).get(5, TimeUnit.SECONDS);

                // RPGPlayer 생성 및 DTO 데이터 적용
                RPGPlayer rpgPlayer = new RPGPlayer(player);
                applyDTOsToPlayer(rpgPlayer, playerDTO, statsDTO, talentDTO, progressDTO, walletDTO);

                LogUtil.logPerformance("플레이어 데이터 로드: " + player.getName(), startTime);
                return rpgPlayer;

            } catch (Exception e) {
                LogUtil.error("플레이어 데이터 로드 실패: " + player.getName(), e);
                // 오프라인 모드로 폴백
                return new RPGPlayer(player);
            }
        }).thenAccept(rpgPlayer -> {
            players.put(player.getUniqueId(), rpgPlayer);
            LogUtil.info("플레이어 데이터 로드 완료: " + player.getName());
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
                null                         // job
        );
        StatsDTO statsDTO = new StatsDTO();
        TalentDTO talentDTO = new TalentDTO();
        ProgressDTO progressDTO = new ProgressDTO();
        WalletDTO walletDTO = new WalletDTO();

        // Firebase에 초기 데이터 저장 (비동기)
        firebaseService.saveAllPlayerDataWithWallet(
                player.getUniqueId().toString(),
                playerDTO, statsDTO, talentDTO, progressDTO, walletDTO
        );

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

        // 쿨다운 체크 (강제 저장이 아닌 경우)
        if (!force) {
            Long lastSave = lastSaveTime.get(uuid);
            if (lastSave != null && (System.currentTimeMillis() - lastSave) < SAVE_COOLDOWN) {
                return CompletableFuture.completedFuture(true);
            }
        }

        lastSaveTime.put(uuid, System.currentTimeMillis());

        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();

            try {
                // RPGPlayer를 DTO로 변환
                PlayerDTO playerDTO = convertToPlayerDTO(rpgPlayer);
                StatsDTO statsDTO = rpgPlayer.getStats().toDTO();
                TalentDTO talentDTO = rpgPlayer.getTalents().toDTO();
                ProgressDTO progressDTO = convertToProgressDTO(rpgPlayer);
                WalletDTO walletDTO = rpgPlayer.getWallet().toDTO();

                // Firebase에 저장
                boolean success = firebaseService.saveAllPlayerDataWithWallet(
                        uuid.toString(), playerDTO, statsDTO, talentDTO,
                        progressDTO, walletDTO
                ).get(10, TimeUnit.SECONDS);

                if (success) {
                    LogUtil.logPerformance("플레이어 데이터 저장: " + rpgPlayer.getName(), startTime);
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
                rpgPlayer.getJob()
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
            saveAllOnlinePlayers().thenRun(() ->
                    LogUtil.debug("모든 플레이어 데이터 자동 저장 완료")
            );
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
                LogUtil.info(rpgPlayer.getName() + "의 직업이 " +
                        (oldJob != null ? oldJob.name() : "없음") + "에서 " +
                        newJob.name() + "으로 변경되었습니다.");
            }
            return success;
        });
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