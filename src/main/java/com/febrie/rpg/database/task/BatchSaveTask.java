package com.febrie.rpg.database.task;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.constants.DatabaseConstants;
import com.febrie.rpg.database.helper.FirestoreHelper.DataPriority;
import com.febrie.rpg.database.sync.DataSyncManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashSet;
/**
 * 배치 저장 작업 스케줄러
 * 플레이어 데이터를 주기적으로 Firestore에 저장
 * 
 * @author Febrie, CoffeeTory
 */
public class BatchSaveTask extends BukkitRunnable {
    
    private final RPGMain plugin;
    private final DataSyncManager syncManager;
    private final RPGPlayerManager playerManager;
    // 플레이어별 마지막 저장 시간 추적
    private final Map<UUID, SaveTimeTracker> saveTrackers = new ConcurrentHashMap<>();
    // 저장 간격 (밀리초)
    private static final long HIGH_PRIORITY_INTERVAL = DatabaseConstants.SAVE_INTERVAL_HIGH_PRIORITY;     // 1분
    private static final long MEDIUM_PRIORITY_INTERVAL = DatabaseConstants.SAVE_INTERVAL_MEDIUM_PRIORITY;  // 3분
    private static final long LOW_PRIORITY_INTERVAL = DatabaseConstants.SAVE_INTERVAL_LOW_PRIORITY;     // 5분
    // 통계
    private long totalSaves = 0;
    private long failedSaves = 0;
    public BatchSaveTask(@NotNull RPGMain plugin, @NotNull DataSyncManager syncManager, 
                        @NotNull RPGPlayerManager playerManager) {
        this.plugin = plugin;
        this.syncManager = syncManager;
        this.playerManager = playerManager;
    }
    @Override
    public void run() {
        long currentTime = System.currentTimeMillis();
        int savedCount = 0;
        
        // 모든 온라인 플레이어 처리
        for (RPGPlayer player : playerManager.getAllPlayers()) {
            UUID playerId = player.getPlayerId();
            SaveTimeTracker tracker = saveTrackers.computeIfAbsent(playerId, k -> new SaveTimeTracker());
            
            // 각 데이터 타입별로 저장 필요 여부 확인
            if (shouldSave(tracker, currentTime, DataPriority.HIGH)) {
                savePlayerData(player, DataPriority.HIGH);
                tracker.lastHighPrioritySave = currentTime;
                savedCount++;
            }
            if (shouldSave(tracker, currentTime, DataPriority.MEDIUM)) {
                savePlayerData(player, DataPriority.MEDIUM);
                tracker.lastMediumPrioritySave = currentTime;
            }
            if (shouldSave(tracker, currentTime, DataPriority.LOW)) {
                savePlayerData(player, DataPriority.LOW);
                tracker.lastLowPrioritySave = currentTime;
            }
        }
        // 로그아웃한 플레이어의 추적 정보 정리
        cleanupOfflineTrackers();
        if (savedCount > 0) {
            LogUtil.debug("배치 저장 완료: " + savedCount + "개 작업");
        }
    }
    
    /**
     * 플레이어 데이터 저장
     */
    private void savePlayerData(@NotNull RPGPlayer player, @NotNull DataPriority priority) {
        // 데이터가 수정되지 않았으면 저장하지 않음
        if (!player.isDataModified()) {
            return;
        }
        try {
            syncManager.savePlayerData(player, priority);
            totalSaves++;
        } catch (Exception e) {
            LogUtil.severe("플레이어 데이터 저장 실패 [" + player.getPlayerId() + "]: " + e.getMessage());
            failedSaves++;
        }
    }
    
    /**
     * 저장 필요 여부 확인
     */
    private boolean shouldSave(@NotNull SaveTimeTracker tracker, long currentTime, @NotNull DataPriority priority) {
        long lastSave = switch (priority) {
            case HIGH -> tracker.lastHighPrioritySave;
            case MEDIUM -> tracker.lastMediumPrioritySave;
            case LOW -> tracker.lastLowPrioritySave;
        };
        long interval = switch (priority) {
            case HIGH -> HIGH_PRIORITY_INTERVAL;
            case MEDIUM -> MEDIUM_PRIORITY_INTERVAL + getRandomOffset();
            case LOW -> LOW_PRIORITY_INTERVAL + getRandomOffset();
        };
        return currentTime - lastSave >= interval;
    }
    
    /**
     * 서버 부하 분산을 위한 랜덤 오프셋 (0-2분)
     */
    private long getRandomOffset() {
        return ThreadLocalRandom.current().nextLong(0, DatabaseConstants.SAVE_INTERVAL_RANDOM_OFFSET);
    }
    
    /**
     * 오프라인 플레이어 추적 정보 정리
     */
    private void cleanupOfflineTrackers() {
        Set<UUID> onlinePlayerIds = new HashSet<>();
        playerManager.getAllPlayers().forEach(p -> onlinePlayerIds.add(p.getPlayerId()));
        saveTrackers.keySet().removeIf(playerId -> !onlinePlayerIds.contains(playerId));
    }
    
    /**
     * 강제 저장 (모든 플레이어)
     */
    public void forceSaveAll() {
        LogUtil.info("모든 플레이어 데이터 강제 저장 시작...");
        List<RPGPlayer> players = new ArrayList<>(playerManager.getAllPlayers());
        int saved = 0;
        for (RPGPlayer player : players) {
            try {
                // 즉시 저장
                player.saveImmediate().join();
                saved++;
            } catch (Exception e) {
                LogUtil.severe("강제 저장 실패 [" + player.getPlayerId() + "]: " + e.getMessage());
            }
        }
        LogUtil.info("강제 저장 완료: " + saved + "/" + players.size() + "명");
    }
    
    /**
     * 특정 플레이어 강제 저장
     */
    public void forceSavePlayer(@NotNull UUID playerId) {
        RPGPlayer player = playerManager.getPlayer(playerId);
        if (player != null) {
            player.saveImmediate()
                .thenRun(() -> LogUtil.debug("플레이어 강제 저장 완료: " + playerId))
                .exceptionally(ex -> {
                    LogUtil.severe("플레이어 강제 저장 실패 [" + playerId + "]: " + ex.getMessage());
                    return null;
                });
        }
    }
    
    /**
     * 작업 시작
     */
    public void start() {
        // 20틱 = 1초, 600틱 = 30초마다 실행
        this.runTaskTimerAsynchronously(plugin, 
                DatabaseConstants.BATCH_SAVE_INITIAL_DELAY_TICKS, 
                DatabaseConstants.BATCH_SAVE_INTERVAL_TICKS);
        LogUtil.info("배치 저장 작업 시작됨 (30초 간격)");
    }
    
    /**
     * 작업 종료
     */
    public void stop() {
        this.cancel();
        forceSaveAll();
        LogUtil.info("배치 저장 작업 종료됨 (총 저장: " + totalSaves + ", 실패: " + failedSaves + ")");
    }
    
    /**
     * 통계 조회
     */
    public SaveStatistics getStatistics() {
        return new SaveStatistics(totalSaves, failedSaves, saveTrackers.size());
    }
    
    /**
     * 저장 시간 추적기
     */
    private static class SaveTimeTracker {
        long lastHighPrioritySave = 0;
        long lastMediumPrioritySave = 0;
        long lastLowPrioritySave = 0;
    }
    
    /**
     * 저장 통계
     */
    public record SaveStatistics(long totalSaves, long failedSaves, int trackedPlayers) {
        public double getSuccessRate() {
            return totalSaves == 0 ? 0.0 : (double) (totalSaves - failedSaves) / totalSaves;
        }
        
        @Override
        public String toString() {
            return String.format("SaveStats{total=%d, failed=%d, successRate=%.2f%%, tracked=%d}",
                totalSaves, failedSaves, getSuccessRate() * 100, trackedPlayers);
        }
    }
}
