package com.febrie.rpg.system;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.FirestoreManager;
import com.febrie.rpg.database.service.impl.SystemFirestoreService;
import com.febrie.rpg.dto.system.ServerStatsDTO;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 서버 통계 관리자 - RPGMain에서 분리
 * 서버 통계 수집, TPS 모니터링, 일일 통계 저장을 담당
 *
 * @author Febrie
 */
public class ServerStatsManager {
    
    private final RPGMain plugin;
    private final FirestoreManager firestoreManager;
    private final RPGPlayerManager playerManager;
    private final SystemFirestoreService systemService;
    
    // TPS 측정
    private final long[] tpsHistory = new long[20]; // 최근 20번의 틱 시간 저장
    private int tpsIndex = 0;
    
    // 서버 통계 태스크
    private BukkitTask serverStatsTask;
    private BukkitTask dailyStatsTask;
    private BukkitTask tpsTask;
    
    // 서버 시작 시간
    private final long startTime;
    private String lastSavedDate = "";
    
    public ServerStatsManager(@NotNull RPGMain plugin, 
                              @NotNull FirestoreManager firestoreManager,
                              @NotNull RPGPlayerManager playerManager) {
        this.plugin = plugin;
        this.firestoreManager = firestoreManager;
        this.playerManager = playerManager;
        this.systemService = null; // TODO: SystemFirestoreService 추가 필요
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * 서버 통계 시스템 시작
     */
    public void start() {
        // TPS 측정 시작
        startTpsMonitoring();
        
        // 5분마다 통계 업데이트 (메모리에서만)
        serverStatsTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin,
                this::updateInMemoryStats,
                6000L,  // 5분 후 시작
                6000L   // 5분마다 반복
        );
        
        // 매일 자정에 통계 저장
        scheduleDailyStatsSave();
        
        LogUtil.info("서버 통계 시스템이 시작되었습니다.");
    }
    
    /**
     * 서버 통계 시스템 종료
     */
    public void shutdown() {
        if (serverStatsTask != null) {
            serverStatsTask.cancel();
        }
        if (dailyStatsTask != null) {
            dailyStatsTask.cancel();
        }
        if (tpsTask != null) {
            tpsTask.cancel();
        }
        
        // 마지막 통계 동기 저장
        saveCurrentServerStatsSync();
    }
    
    /**
     * TPS 모니터링 시작
     */
    private void startTpsMonitoring() {
        tpsTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            tpsHistory[tpsIndex] = currentTime;
            tpsIndex = (tpsIndex + 1) % tpsHistory.length;
        }, 0L, 1L); // 매 틱마다 실행
    }
    
    /**
     * 매일 통계 저장 스케줄 설정
     */
    private void scheduleDailyStatsSave() {
        // 다음 자정까지의 시간 계산
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay();
        long delayTicks = ChronoUnit.SECONDS.between(now, nextMidnight) * 20;
        
        // 자정에 저장 후 24시간마다 반복
        dailyStatsTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin,
                this::saveCurrentServerStatsAsync,
                delayTicks,     // 다음 자정까지 대기
                24 * 60 * 60 * 20L  // 24시간마다 반복
        );
    }
    
    /**
     * 메모리 내 통계 업데이트 (저장하지 않음)
     */
    private void updateInMemoryStats() {
        try {
            ServerStatsDTO currentStats = collectCurrentServerStats();
            // 실시간 통계를 비동기로 저장
            if (systemService != null) {
                systemService.updateServerStats(currentStats)
                    .exceptionally(throwable -> {
                        LogUtil.error("실시간 서버 통계 업데이트 실패", throwable);
                        return null;
                    });
            }
        } catch (Exception e) {
            LogUtil.error("서버 통계 업데이트 중 오류 발생", e);
        }
    }
    
    /**
     * 현재 서버 통계 비동기 저장 (주기적 저장용)
     */
    private void saveCurrentServerStatsAsync() {
        // Firebase 연결 확인
        if (!firestoreManager.isInitialized() || systemService == null) {
            return;
        }
        
        try {
            ServerStatsDTO stats = collectCurrentServerStats();
            String today = LocalDate.now().toString();
            
            // 오늘 날짜가 마지막 저장 날짜와 다른 경우에만 일일 통계 저장
            if (!today.equals(lastSavedDate)) {
                // 일일 통계 저장 (비동기)
                systemService.saveDailyStats(today, stats)
                    .thenRun(() -> {
                        lastSavedDate = today;
                        LogUtil.info("일일 서버 통계가 저장되었습니다: " + today);
                    })
                    .exceptionally(throwable -> {
                        LogUtil.error("일일 서버 통계 저장 실패: " + today, throwable);
                        return null;
                    });
            }
            
            // 실시간 통계도 업데이트
            systemService.updateServerStats(stats)
                .exceptionally(throwable -> {
                    LogUtil.error("실시간 서버 통계 업데이트 실패", throwable);
                    return null;
                });
                
        } catch (Exception e) {
            LogUtil.error("서버 통계 저장 중 오류 발생", e);
        }
    }
    
    /**
     * 현재 서버 통계 동기 저장 (서버 종료 시 사용)
     */
    private void saveCurrentServerStatsSync() {
        // Firebase 연결 확인
        if (!firestoreManager.isInitialized() || systemService == null) {
            return;
        }
        
        try {
            ServerStatsDTO stats = collectCurrentServerStats();
            LogUtil.info("서버 통계: " + stats);
            
            String today = LocalDate.now().toString();
            
            // 일일 통계 저장 (동기)
            if (!today.equals(lastSavedDate)) {
                systemService.saveDailyStats(today, stats).get(10, TimeUnit.SECONDS);
                LogUtil.info("일일 서버 통계가 저장되었습니다: " + today);
            }
            
            // 실시간 통계도 업데이트 (동기)
            systemService.updateServerStats(stats).get(10, TimeUnit.SECONDS);
            LogUtil.info("실시간 서버 통계가 저장되었습니다.");
            
        } catch (Exception e) {
            LogUtil.error("서버 통계 동기 저장 중 오류 발생", e);
        }
    }
    
    /**
     * 현재 서버 통계 수집
     */
    public ServerStatsDTO collectCurrentServerStats() {
        int onlinePlayers = plugin.getServer().getOnlinePlayers().size();
        int maxPlayers = plugin.getServer().getMaxPlayers();
        int totalPlayers = playerManager.getOnlinePlayerCount();
        long uptime = System.currentTimeMillis() - startTime;
        double tps = calculateCurrentTPS();
        long totalPlaytime = calculateTotalPlaytime();
        String version = plugin.getServer().getVersion();
        
        return new ServerStatsDTO(
                onlinePlayers, maxPlayers, totalPlayers,
                uptime, tps, totalPlaytime, version
        );
    }
    
    /**
     * 현재 TPS 계산
     */
    public double calculateCurrentTPS() {
        if (tpsHistory[0] == 0) {
            return 20.0; // 아직 충분한 데이터가 없으면 이상적인 값 반환
        }
        
        try {
            long newest = tpsHistory[(tpsIndex - 1 + tpsHistory.length) % tpsHistory.length];
            long oldest = tpsHistory[tpsIndex];
            
            if (oldest == 0 || newest <= oldest) {
                return 20.0;
            }
            
            long timeDiff = newest - oldest;
            double secondsDiff = timeDiff / 1000.0; // double (primitive)
            
            if (secondsDiff <= 0) {
                return 20.0;
            }
            
            // 20틱 동안의 시간을 측정했으므로
            double actualTps = (tpsHistory.length - 1) / secondsDiff; // double (primitive)
            
            // TPS는 최대 20으로 제한
            return Math.min(20.0, Math.max(0.0, actualTps));
        } catch (Exception e) {
            return 20.0;
        }
    }
    
    /**
     * 총 플레이타임 계산
     */
    private long calculateTotalPlaytime() {
        // 모든 온라인 플레이어의 플레이타임 합계
        // RPGPlayer에서 플레이타임 정보를 가져와야 함
        return plugin.getServer().getOnlinePlayers().stream()
                .mapToLong(player -> {
                    // RPGPlayer에서 실제 플레이타임 가져오기
                    RPGPlayer rpgPlayer = plugin.getRPGPlayerManager().getPlayer(player);
                    return rpgPlayer != null ? rpgPlayer.getTotalPlaytime() : 0L;
                })
                .sum();
    }
    
    /**
     * 서버 가동 시간 반환
     */
    public long getUptime() {
        return System.currentTimeMillis() - startTime;
    }
}