package com.febrie.rpg.player;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.impl.PlayerFirestoreService;
import com.febrie.rpg.database.sync.DataSyncManager;
import com.febrie.rpg.database.task.BatchSaveTask;
import com.febrie.rpg.dto.island.PlayerIslandDataDTO;
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
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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
    private final DataSyncManager syncManager;
    private final BatchSaveTask batchSaveTask;
    private final Map<UUID, RPGPlayer> players = new ConcurrentHashMap<>();

    // 저장 쿨다운 관리 - AtomicLong으로 thread-safe 보장
    private final Map<UUID, AtomicLong> lastSaveTime = new ConcurrentHashMap<>();
    private static final long SAVE_COOLDOWN = 30000; // 30초

    private RPGPlayerManager(@NotNull RPGMain plugin, @Nullable PlayerFirestoreService playerService) {
        this.plugin = plugin;
        this.playerService = playerService;
        
        // DataSyncManager 초기화
        if (plugin.getFirestore() != null) {
            this.syncManager = new DataSyncManager(plugin, plugin.getFirestore());
            this.batchSaveTask = new BatchSaveTask(plugin, syncManager, this);
            this.batchSaveTask.start();
        } else {
            this.syncManager = null;
            this.batchSaveTask = null;
        }
        
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
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        
        // RPG 데이터 로드
        loadPlayerAsync(player);
        
        // 섬 데이터는 이미 캐시에 있음 - 확인만
        if (plugin.getIslandManager() != null) {
            PlayerIslandDataDTO islandData = plugin.getIslandManager()
                .getPlayerIslandDataFromCache(uuid);
            
            if (islandData != null) {
                LogUtil.debug("플레이어 " + player.getName() + " 섬 데이터 캐시 적중");
            } else {
                LogUtil.debug("플레이어 " + player.getName() + " 섬 데이터 없음");
            }
        }
    }

    /**
     * 플레이어 퇴장 시 데이터 저장 및 정리
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        handlePlayerDisconnect(event.getPlayer());
    }
    
    /**
     * 플레이어 강퇴 시 데이터 저장 및 정리
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKick(@NotNull PlayerKickEvent event) {
        handlePlayerDisconnect(event.getPlayer());
    }
    
    /**
     * 플레이어 연결 해제 처리
     */
    private void handlePlayerDisconnect(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        RPGPlayer rpgPlayer = players.remove(uuid);
        
        // QuestManager 데이터 언로드 (저장 포함)
        try {
            com.febrie.rpg.quest.manager.QuestManager questManager = com.febrie.rpg.quest.manager.QuestManager.getInstance();
            if (questManager != null) {
                questManager.unloadPlayerData(uuid);
                questManager.clearPlayerCache(uuid);
            }
        } catch (IllegalStateException e) {
            LogUtil.warning("QuestManager가 초기화되지 않음 - 퀘스트 데이터 언로드 스킵: " + player.getName());
        }
        
        if (rpgPlayer != null) {
            // DataSyncManager를 사용하여 즉시 저장
            if (syncManager != null) {
                syncManager.saveOnLogout(rpgPlayer)
                    .orTimeout(5, TimeUnit.SECONDS)
                    .exceptionally(ex -> {
                        LogUtil.error("플레이어 데이터 저장 실패: " + player.getName(), ex);
                        // 실패 시 로컬 백업 또는 대기열에 추가
                        addToFailedSaveQueue(uuid, rpgPlayer);
                        return null;
                    })
                    .thenRun(() -> LogUtil.debug("플레이어 데이터 저장 완료: " + player.getName()));
            } else {
                // 대체 저장 방식
                savePlayerDataAsync(rpgPlayer, true)
                    .orTimeout(5, TimeUnit.SECONDS)
                    .exceptionally(ex -> {
                        LogUtil.error("플레이어 데이터 저장 실패: " + player.getName(), ex);
                        addToFailedSaveQueue(uuid, rpgPlayer);
                        return false;
                    });
            }
        }
        
        lastSaveTime.remove(uuid);
    }
    
    /**
     * 저장 실패 큐에 추가 (나중에 재시도)
     */
    private void addToFailedSaveQueue(@NotNull UUID uuid, @NotNull RPGPlayer rpgPlayer) {
        // TODO: Implement failed save queue for retry mechanism
        // 실패한 저장을 대기열에 넣고 나중에 재시도 필요
        LogUtil.warning("Failed to save player data, retry not implemented yet: " + uuid);
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
            // DataSyncManager 설정
            if (syncManager != null) {
                rpgPlayer.setSyncManager(syncManager);
            }
            players.put(player.getUniqueId(), rpgPlayer);
            
            // 퀘스트 데이터 로드
            try {
                com.febrie.rpg.quest.manager.QuestManager questManager = com.febrie.rpg.quest.manager.QuestManager.getInstance();
                if (questManager != null) {
                    questManager.loadPlayerData(player.getUniqueId()).thenRun(() -> {
                        LogUtil.debug("퀘스트 데이터 로드 완료: " + player.getName());
                    }).exceptionally(ex -> {
                        LogUtil.error("퀘스트 데이터 로드 실패: " + player.getName(), ex);
                        return null;
                    });
                }
            } catch (IllegalStateException e) {
                LogUtil.warning("QuestManager가 초기화되지 않음 - 퀘스트 데이터 로드 스킵: " + player.getName());
            }
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
        ProgressDTO progressDTO = new ProgressDTO(
                1, // 레벨 1로 시작 (기본값)
                0L, // 경험치 0
                0.0, // 진행도 0%
                0, // mobsKilled
                0, // playersKilled
                0  // deaths
        );
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
        } else if (progressDTO.currentLevel() == 0) {
            // 레벨 0인 경우 레벨 1로 설정
            if (rpgPlayer.getJob() != null) {
                long level1Exp = calculateTotalExpForLevel(1, rpgPlayer.getJob());
                rpgPlayer.setExperience(level1Exp);
            }
        }
        rpgPlayer.setMobsKilled(progressDTO.mobsKilled());
        rpgPlayer.setPlayersKilled(progressDTO.playersKilled());
        rpgPlayer.setDeaths(progressDTO.deaths());

        // 재화 적용
        rpgPlayer.getWallet().applyFromDTO(walletDTO);
    }

    /**
     * 플레이어 데이터 비동기 저장 (주기적 저장용)
     */
    public CompletableFuture<Boolean> savePlayerDataAsync(@NotNull RPGPlayer rpgPlayer, boolean force) {
        // 플러그인이 비활성화된 경우 저장하지 않음
        if (!plugin.isEnabled()) {
            return CompletableFuture.completedFuture(false);
        }
        
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

        // PlayerDataDTO 생성
        PlayerDataDTO playerData = createPlayerDataDTO(rpgPlayer);

        if (playerService != null) {
            LogUtil.info("플레이어 데이터 비동기 저장 시도: " + uuid);
            return playerService.save(uuid.toString(), playerData)
                .thenApply(result -> {
                    LogUtil.info("플레이어 데이터 비동기 저장 성공: " + uuid);
                    return true;
                })
                .exceptionally(throwable -> {
                    LogUtil.error("플레이어 데이터 비동기 저장 실패: " + uuid, throwable);
                    return false;
                });
        } else {
            LogUtil.warning("PlayerService가 null입니다. 플레이어 데이터를 저장할 수 없습니다.");
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * 플레이어 데이터 동기 저장 (서버 종료 시 사용)
     */
    public boolean savePlayerDataSync(@NotNull RPGPlayer rpgPlayer) {
        UUID uuid = rpgPlayer.getPlayerId();
        
        try {
            // PlayerDataDTO 생성
            PlayerDataDTO playerData = createPlayerDataDTO(rpgPlayer);

            if (playerService != null) {
                LogUtil.info("플레이어 데이터 동기 저장 시도: " + uuid);
                playerService.save(uuid.toString(), playerData)
                    .get(10, TimeUnit.SECONDS);
                LogUtil.info("플레이어 데이터 동기 저장 성공: " + uuid);
                return true;
            } else {
                LogUtil.warning("PlayerService가 null입니다. 플레이어 데이터를 저장할 수 없습니다.");
                return false;
            }
        } catch (Exception e) {
            LogUtil.error("플레이어 데이터 동기 저장 실패: " + rpgPlayer.getName(), e);
            return false;
        }
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
     * RPGPlayer를 PlayerDataDTO로 변환
     */
    private PlayerDataDTO createPlayerDataDTO(@NotNull RPGPlayer rpgPlayer) {
        UUID uuid = rpgPlayer.getPlayerId();
        
        // RPGPlayer를 DTO로 변환
        PlayerDTO playerDTO = convertToPlayerDTO(rpgPlayer);
        StatsDTO statsDTO = rpgPlayer.getStats().toDTO();
        TalentDTO talentDTO = rpgPlayer.getTalents().toDTO();
        ProgressDTO progressDTO = convertToProgressDTO(rpgPlayer);
        WalletDTO walletDTO = rpgPlayer.getWallet().toDTO();

        // PlayerDataDTO 생성
        return new PlayerDataDTO(
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
                .map(rpgPlayer -> savePlayerDataAsync(rpgPlayer, false))
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
        return savePlayerDataAsync(rpgPlayer, true).thenApply(success -> {
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
        plugin.getLogger().info("모든 플레이어 데이터 저장 중... (온라인: " + players.size() + "명)");
        
        try {
            // 모든 플레이어 데이터를 동기적으로 저장
            int successCount = 0;
            int totalCount = players.size();
            
            for (RPGPlayer rpgPlayer : players.values()) {
                try {
                    boolean success = savePlayerDataSync(rpgPlayer);
                    if (success) {
                        successCount++;
                    } else {
                        plugin.getLogger().severe("플레이어 데이터 저장 실패: " + rpgPlayer.getPlayerId());
                    }
                } catch (Exception e) {
                    plugin.getLogger().severe("플레이어 데이터 저장 중 오류 발생: " + 
                            rpgPlayer.getPlayerId() + " - " + e.getMessage());
                }
            }
            
            plugin.getLogger().info("플레이어 데이터 저장 완료! (성공: " + successCount + 
                    "/" + totalCount + ")");
            
        } catch (Exception e) {
            LogUtil.error("플레이어 데이터 저장 중 오류 발생", e);
        }
    }

    /**
     * 접속 중인 플레이어 수
     */
    public int getOnlinePlayerCount() {
        return players.size();
    }
    
    /**
     * 서버 종료 시 모든 데이터 저장
     */
    public void shutdown() {
        LogUtil.info("RPGPlayerManager 종료 중...");
        
        // 배치 작업 중지
        if (batchSaveTask != null) {
            batchSaveTask.stop();
        }
        
        // 모든 플레이어 데이터 즉시 저장
        List<CompletableFuture<Void>> saveFutures = new ArrayList<>();
        for (RPGPlayer player : players.values()) {
            if (syncManager != null) {
                saveFutures.add(syncManager.saveOnLogout(player));
            }
        }
        
        // 모든 저장 완료 대기
        CompletableFuture.allOf(saveFutures.toArray(new CompletableFuture<?>[0]))
            .orTimeout(10, TimeUnit.SECONDS)
            .join();
            
        // DataSyncManager 종료
        if (syncManager != null) {
            syncManager.shutdown();
        }
        
        players.clear();
        LogUtil.info("RPGPlayerManager 종료 완료");
    }
}