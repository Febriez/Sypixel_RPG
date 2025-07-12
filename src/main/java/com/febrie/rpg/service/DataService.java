package com.febrie.rpg.service;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.FirebaseService;
import com.febrie.rpg.dto.LeaderboardEntryDTO;
import com.febrie.rpg.dto.ServerStatsDTO;
import com.febrie.rpg.util.LogUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 데이터 접근 추상화 서비스
 * Firebase와 로컬 저장소 간의 하이브리드 데이터 관리
 *
 * @author Febrie, CoffeeTory
 */
public class DataService {

    private final RPGMain plugin;
    private final FirebaseService firebaseService;
    private final PlayerService playerService;

    // 서버 통계 캐시
    private ServerStatsDTO serverStatsCache;
    private long serverStatsCacheTime = 0;
    private static final long STATS_CACHE_DURATION = 60000; // 1분

    // 순위표 캐시
    private final ConcurrentHashMap<String, List<LeaderboardEntryDTO>> leaderboardCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> leaderboardCacheTime = new ConcurrentHashMap<>();
    private static final long LEADERBOARD_CACHE_DURATION = 300000; // 5분

    // 데이터 저장 모드
    public enum DataMode {
        ONLINE,     // Firebase 사용
        OFFLINE,    // 로컬 저장소만 사용
        HYBRID      // 둘 다 사용 (Firebase 실패시 로컬 폴백)
    }

    private DataMode currentMode = DataMode.HYBRID;

    public DataService(@NotNull RPGMain plugin, @NotNull PlayerService playerService) {
        this.plugin = plugin;
        this.playerService = playerService;
        this.firebaseService = new FirebaseService(plugin);

        // Firebase 연결 확인
        checkConnectionAndSetMode();

        // 서버 통계 초기화
        initializeServerStats();

        // 주기적 동기화
        startSyncScheduler();
    }

    /**
     * 연결 상태 확인 및 모드 설정
     */
    private void checkConnectionAndSetMode() {
        if (firebaseService.isConnected()) {
            currentMode = DataMode.HYBRID;
            LogUtil.info("데이터 서비스가 하이브리드 모드로 실행됩니다.");
        } else {
            currentMode = DataMode.OFFLINE;
            LogUtil.warning("Firebase 연결 실패. 오프라인 모드로 실행됩니다.");
        }
    }

    /**
     * 서버 통계 초기화
     */
    private void initializeServerStats() {
        if (serverStatsCache == null) {
            serverStatsCache = new ServerStatsDTO();
            serverStatsCache.setServerName(plugin.getServer().getName());
            serverStatsCache.setServerVersion(plugin.getServer().getVersion());
            serverStatsCache.setPluginVersion(plugin.getDescription().getVersion());
            serverStatsCache.setStartTime(System.currentTimeMillis());
        }
    }

    /**
     * 서버 통계 가져오기 (캐시 활용)
     */
    @NotNull
    public CompletableFuture<ServerStatsDTO> getServerStats() {
        // 캐시 확인
        if (System.currentTimeMillis() - serverStatsCacheTime < STATS_CACHE_DURATION) {
            return CompletableFuture.completedFuture(serverStatsCache);
        }

        return CompletableFuture.supplyAsync(() -> {
            updateServerStats();
            serverStatsCacheTime = System.currentTimeMillis();
            return serverStatsCache;
        });
    }

    /**
     * 서버 통계 업데이트
     */
    private void updateServerStats() {
        serverStatsCache.setTotalPlayers(plugin.getServer().getOfflinePlayers().length);
        serverStatsCache.setOnlinePlayers(plugin.getServer().getOnlinePlayers().size());
        serverStatsCache.setMaxPlayers(plugin.getServer().getMaxPlayers());

        // 메모리 정보
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        long usedMemory = totalMemory - freeMemory;

        serverStatsCache.addData("maxMemoryMB", maxMemory);
        serverStatsCache.addData("usedMemoryMB", usedMemory);
        serverStatsCache.addData("memoryUsagePercent", (usedMemory * 100) / maxMemory);

        // TPS 정보 (Paper API 사용 시)
        if (plugin.getServer().getClass().getName().contains("Paper")) {
            try {
                double[] tps = plugin.getServer().getTPS();
                serverStatsCache.addData("tps1m", tps[0]);
                serverStatsCache.addData("tps5m", tps[1]);
                serverStatsCache.addData("tps15m", tps[2]);
            } catch (Exception e) {
                // TPS 정보 사용 불가
            }
        }

        serverStatsCache.markUpdated();

        // Firebase에 저장 (비동기)
        if (currentMode != DataMode.OFFLINE) {
            saveServerStatsToFirebase();
        }
    }

    /**
     * 서버 통계 Firebase 저장
     */
    private void saveServerStatsToFirebase() {
        // TODO: Firebase에 서버 통계 저장 구현
        // firebaseService.saveServerStats(serverStatsCache);
    }

    /**
     * 순위표 가져오기 (캐시 활용)
     */
    @NotNull
    public CompletableFuture<List<LeaderboardEntryDTO>> getLeaderboard(@NotNull String type, int limit) {
        String cacheKey = type + "_" + limit;

        // 캐시 확인
        Long cacheTime = leaderboardCacheTime.get(cacheKey);
        if (cacheTime != null && (System.currentTimeMillis() - cacheTime) < LEADERBOARD_CACHE_DURATION) {
            List<LeaderboardEntryDTO> cached = leaderboardCache.get(cacheKey);
            if (cached != null) {
                return CompletableFuture.completedFuture(cached);
            }
        }

        // Firebase에서 로드
        if (currentMode != DataMode.OFFLINE) {
            return firebaseService.getTopPlayers(type, limit)
                    .thenApply(entries -> {
                        // 캐시에 저장
                        leaderboardCache.put(cacheKey, entries);
                        leaderboardCacheTime.put(cacheKey, System.currentTimeMillis());
                        return entries;
                    })
                    .exceptionally(e -> {
                        LogUtil.error("순위표 로드 실패: " + type, e);
                        return getLocalLeaderboard(type, limit);
                    });
        }

        // 오프라인 모드
        return CompletableFuture.completedFuture(getLocalLeaderboard(type, limit));
    }

    /**
     * 로컬 순위표 가져오기
     */
    @NotNull
    private List<LeaderboardEntryDTO> getLocalLeaderboard(@NotNull String type, int limit) {
        // 온라인 플레이어 기준으로 순위 생성
        return plugin.getServer().getOnlinePlayers().stream()
                .map(playerService.getPlayerManager()::getPlayer)
                .filter(rpgPlayer -> rpgPlayer != null)
                .sorted((a, b) -> {
                    switch (type) {
                        case "level":
                            return Integer.compare(b.getLevel(), a.getLevel());
                        case "combat_power":
                            return Integer.compare(b.getCombatPower(), a.getCombatPower());
                        default:
                            return 0;
                    }
                })
                .limit(limit)
                .map(rpgPlayer -> {
                    String uuid = rpgPlayer.getUuid().toString();
                    String name = rpgPlayer.getName();
                    String job = rpgPlayer.hasJob() ? rpgPlayer.getJob().name() : null;

                    switch (type) {
                        case "level":
                            return LeaderboardEntryDTO.createLevelEntry(
                                    uuid, name, rpgPlayer.getLevel(), rpgPlayer.getExperience(), job
                            );
                        case "combat_power":
                            return LeaderboardEntryDTO.createCombatPowerEntry(
                                    uuid, name, rpgPlayer.getCombatPower(), rpgPlayer.getLevel(), job
                            );
                        default:
                            return new LeaderboardEntryDTO(uuid, name, 0);
                    }
                })
                .toList();
    }

    /**
     * 주기적 동기화 스케줄러
     */
    private void startSyncScheduler() {
        // 5분마다 서버 통계 업데이트
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            updateServerStats();
        }, 20L * 300, 20L * 300);

        // 10분마다 캐시 정리
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            cleanupCaches();
        }, 20L * 600, 20L * 600);

        // 30분마다 연결 상태 확인
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            checkConnectionAndSetMode();
        }, 20L * 1800, 20L * 1800);
    }

    /**
     * 캐시 정리
     */
    private void cleanupCaches() {
        long now = System.currentTimeMillis();

        // 순위표 캐시 정리
        leaderboardCacheTime.entrySet().removeIf(entry -> {
            if (now - entry.getValue() > LEADERBOARD_CACHE_DURATION * 2) {
                leaderboardCache.remove(entry.getKey());
                return true;
            }
            return false;
        });

        LogUtil.debug("데이터 서비스 캐시 정리 완료");
    }

    /**
     * 모든 데이터 강제 동기화
     */
    public CompletableFuture<Boolean> forceSyncAll() {
        if (currentMode == DataMode.OFFLINE) {
            LogUtil.warning("오프라인 모드에서는 동기화할 수 없습니다.");
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                // 모든 온라인 플레이어 데이터 저장
                playerService.saveAllOnlinePlayers().get(30, TimeUnit.SECONDS);

                // 서버 통계 저장
                updateServerStats();

                // 캐시 정리
                clearAllCaches();

                LogUtil.info("전체 데이터 동기화 완료");
                return true;

            } catch (Exception e) {
                LogUtil.error("전체 데이터 동기화 실패", e);
                return false;
            }
        });
    }

    /**
     * 모든 캐시 제거
     */
    public void clearAllCaches() {
        leaderboardCache.clear();
        leaderboardCacheTime.clear();
        serverStatsCacheTime = 0;

        if (firebaseService != null) {
            firebaseService.clearCache();
        }

        LogUtil.info("모든 데이터 캐시가 제거되었습니다.");
    }

    /**
     * 데이터 모드 가져오기
     */
    @NotNull
    public DataMode getDataMode() {
        return currentMode;
    }

    /**
     * 데이터 모드 설정
     */
    public void setDataMode(@NotNull DataMode mode) {
        this.currentMode = mode;
        LogUtil.info("데이터 모드가 " + mode + "으로 변경되었습니다.");
    }

    /**
     * Firebase 서비스 가져오기
     */
    @NotNull
    public FirebaseService getFirebaseService() {
        return firebaseService;
    }

    /**
     * 통계 정보
     */
    public void logStats() {
        LogUtil.info("=== 데이터 서비스 통계 ===");
        LogUtil.info("현재 모드: " + currentMode);
        LogUtil.info("순위표 캐시: " + leaderboardCache.size() + "개");
        LogUtil.info("Firebase 연결: " + (firebaseService.isConnected() ? "연결됨" : "연결 안됨"));

        if (firebaseService.isConnected()) {
            firebaseService.getCacheStats().forEach((key, value) ->
                    LogUtil.info("  " + key + ": " + value));
        }
    }
}