package com.febrie.rpg.service;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.FirebaseService;
import com.febrie.rpg.dto.*;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.level.LevelSystem;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 플레이어 관련 비즈니스 로직 중앙화 서비스
 * RPGPlayer와 DTO 간의 변환 및 데이터 관리 담당
 *
 * @author Febrie, CoffeeTory
 */
public class PlayerService {

    private final RPGMain plugin;
    private final RPGPlayerManager playerManager;
    private final FirebaseService firebaseService;

    // 저장 대기열 (성능 최적화)
    private final ConcurrentHashMap<UUID, Long> pendingSaves = new ConcurrentHashMap<>();
    private static final long SAVE_COOLDOWN = 30000; // 30초

    public PlayerService(@NotNull RPGMain plugin, @NotNull FirebaseService firebaseService) {
        this.plugin = plugin;
        this.playerManager = plugin.getRPGPlayerManager();
        this.firebaseService = firebaseService;

        // 자동 저장 스케줄러
        startAutoSaveScheduler();
    }

    /**
     * 플레이어 데이터 로드 (비동기)
     */
    public CompletableFuture<RPGPlayer> loadPlayerData(@NotNull Player player) {
        String uuid = player.getUniqueId().toString();

        return CompletableFuture.supplyAsync(() -> {
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
                RPGPlayer rpgPlayer = playerManager.getOrCreatePlayer(player);
                applyDTOsToPlayer(rpgPlayer, playerDTO, statsDTO, talentDTO, progressDTO, walletDTO);

                LogUtil.logPerformance("플레이어 데이터 로드: " + player.getName(), startTime);
                return rpgPlayer;

            } catch (Exception e) {
                LogUtil.error("플레이어 데이터 로드 실패: " + player.getName(), e);
                // 오프라인 모드로 폴백
                return playerManager.getOrCreatePlayer(player);
            }
        });
    }

    /**
     * 플레이어 데이터 저장 (비동기, 쿨다운 적용)
     */
    public CompletableFuture<Boolean> savePlayerData(@NotNull RPGPlayer rpgPlayer, boolean force) {
        UUID uuid = rpgPlayer.getPlayerId();

        // 쿨다운 체크 (강제 저장이 아닌 경우)
        if (!force) {
            Long lastSave = pendingSaves.get(uuid);
            if (lastSave != null && (System.currentTimeMillis() - lastSave) < SAVE_COOLDOWN) {
                return CompletableFuture.completedFuture(true);
            }
        }

        pendingSaves.put(uuid, System.currentTimeMillis());

        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();

            try {
                // RPGPlayer를 DTO로 변환
                PlayerDTO playerDTO = convertToPlayerDTO(rpgPlayer);
                StatsDTO statsDTO = convertToStatsDTO(rpgPlayer);
                TalentDTO talentDTO = convertToTalentDTO(rpgPlayer);
                ProgressDTO progressDTO = convertToProgressDTO(rpgPlayer);
                WalletDTO walletDTO = convertToWalletDTO(rpgPlayer);

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
     * 신규 플레이어 생성
     */
    private RPGPlayer createNewPlayer(@NotNull Player player) {
        RPGPlayer rpgPlayer = new RPGPlayer(player);

        // 기본 데이터로 DTO 생성
        PlayerDTO playerDTO = new PlayerDTO(player.getUniqueId().toString(), player.getName());
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
        if (playerDTO.getJob() != null) {
            rpgPlayer.setJob(playerDTO.getJob());
        }

        // 스탯 적용
        rpgPlayer.getStats().applyFromDTO(statsDTO);

        // 특성 적용
        rpgPlayer.getTalents().applyFromDTO(talentDTO);

        // 진행도 적용
        if (progressDTO.getCurrentLevel() > 1) {
            // 레벨에 맞는 경험치 설정
            long totalExp = calculateTotalExpForLevel(progressDTO.getCurrentLevel(), rpgPlayer.getJob());
            rpgPlayer.addExperience(totalExp - rpgPlayer.getExperience());
        }

        // 재화 적용
        if (walletDTO != null) {
            rpgPlayer.getWallet().applyFromDTO(walletDTO);
        }
    }

    /**
     * RPGPlayer를 PlayerDTO로 변환
     */
    private PlayerDTO convertToPlayerDTO(@NotNull RPGPlayer rpgPlayer) {
        PlayerDTO dto = new PlayerDTO(rpgPlayer.getUuid().toString(), rpgPlayer.getName());

        if (rpgPlayer.hasJob()) {
            dto.setJob(rpgPlayer.getJob());
        }

        // 플레이타임 업데이트
        Player bukkitPlayer = rpgPlayer.getPlayer();
        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            long currentPlaytime = dto.getTotalPlaytime();
            long sessionTime = System.currentTimeMillis() - rpgPlayer.getSessionStartTime();
            dto.setTotalPlaytime(currentPlaytime + sessionTime);
        }

        return dto;
    }

    /**
     * RPGPlayer를 StatsDTO로 변환
     */
    private StatsDTO convertToStatsDTO(@NotNull RPGPlayer rpgPlayer) {
        return rpgPlayer.getStats().toDTO();
    }

    /**
     * RPGPlayer를 TalentDTO로 변환
     */
    private TalentDTO convertToTalentDTO(@NotNull RPGPlayer rpgPlayer) {
        return rpgPlayer.getTalents().toDTO();
    }

    /**
     * RPGPlayer를 ProgressDTO로 변환
     */
    private ProgressDTO convertToProgressDTO(@NotNull RPGPlayer rpgPlayer) {
        ProgressDTO dto = new ProgressDTO();

        dto.setCurrentLevel(rpgPlayer.getLevel());
        dto.setTotalExperience(rpgPlayer.getExperience());
        dto.setLevelProgress(rpgPlayer.getLevelProgress());

        // 전투 통계는 아직 구현되지 않음

        return dto;
    }

    /**
     * RPGPlayer를 WalletDTO로 변환
     */
    private WalletDTO convertToWalletDTO(@NotNull RPGPlayer rpgPlayer) {
        return rpgPlayer.getWallet().toDTO();
    }

    /**
     * 직업 변경 (관리자 명령어용)
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
     * 모든 온라인 플레이어 데이터 저장
     */
    public CompletableFuture<Void> saveAllOnlinePlayers() {
        CompletableFuture<?>[] futures = plugin.getServer().getOnlinePlayers().stream()
                .map(player -> {
                    RPGPlayer rpgPlayer = playerManager.getPlayer(player);
                    if (rpgPlayer != null) {
                        return savePlayerData(rpgPlayer, true);
                    }
                    return CompletableFuture.completedFuture(true);
                })
                .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(futures);
    }

    /**
     * 자동 저장 스케줄러
     */
    private void startAutoSaveScheduler() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            saveAllOnlinePlayers().thenRun(() ->
                    LogUtil.debug("모든 플레이어 데이터 자동 저장 완료")
            );
        }, 12000L, 12000L); // 10분마다
    }

    /**
     * 레벨에 필요한 총 경험치 계산
     */
    private long calculateTotalExpForLevel(int level, JobType job) {
        if (job == null || level <= 1) {
            return 0;
        }

        return LevelSystem.getTotalExpForLevel(level, job);
    }

    /**
     * 플레이어 정리 (로그아웃 시)
     */
    public void onPlayerQuit(@NotNull Player player) {
        pendingSaves.remove(player.getUniqueId());
    }
}