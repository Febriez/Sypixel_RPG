package com.febrie.rpg.database.sync;

import com.febrie.rpg.dto.player.PlayerDataDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 플레이어 데이터 캐시
 * 5분 TTL로 플레이어 데이터 캐싱
 * 
 * @author Febrie, CoffeeTory
 */
public class PlayerDataCache {
    
    private final ConcurrentHashMap<UUID, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupExecutor;
    private final long ttlMillis;
    private final int maxSize;
    
    private final AtomicLong hitCount = new AtomicLong(0);
    private final AtomicLong missCount = new AtomicLong(0);
    private final AtomicLong evictionCount = new AtomicLong(0);
    
    public PlayerDataCache(long duration, @NotNull TimeUnit unit) {
        this.ttlMillis = unit.toMillis(duration);
        this.maxSize = 10_000; // 최대 10,000명 캐싱
        
        // 정리 스케줄러 (1분마다 실행)
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "PlayerDataCache-Cleanup");
            t.setDaemon(true);
            return t;
        });
        
        cleanupExecutor.scheduleWithFixedDelay(this::cleanupExpired, 1, 1, TimeUnit.MINUTES);
    }
    
    /**
     * 캐시에 데이터 저장
     */
    public void put(@NotNull UUID playerId, @NotNull PlayerDataDTO data) {
        // 크기 제한 체크
        if (cache.size() >= maxSize) {
            // 가장 오래된 항목 제거
            evictOldestEntry();
        }
        
        cache.put(playerId, new CacheEntry(data, System.currentTimeMillis()));
    }
    
    /**
     * 캐시에서 데이터 조회
     */
    @Nullable
    public PlayerDataDTO get(@NotNull UUID playerId) {
        CacheEntry entry = cache.get(playerId);
        
        if (entry == null) {
            missCount.incrementAndGet();
            return null;
        }
        
        // TTL 체크
        if (System.currentTimeMillis() - entry.timestamp > ttlMillis) {
            cache.remove(playerId);
            missCount.incrementAndGet();
            evictionCount.incrementAndGet();
            return null;
        }
        
        hitCount.incrementAndGet();
        return entry.data;
    }
    
    /**
     * 캐시에서 데이터 제거
     */
    public void invalidate(@NotNull UUID playerId) {
        cache.remove(playerId);
    }
    
    /**
     * 모든 캐시 데이터 제거
     */
    public void invalidateAll() {
        cache.clear();
    }
    
    /**
     * 캐시 정리
     */
    public void cleanUp() {
        cleanupExpired();
    }
    
    /**
     * 캐시 크기
     */
    public long size() {
        return cache.size();
    }
    
    /**
     * 캐시 통계
     */
    @NotNull
    public CacheStats getStats() {
        return new CacheStats(
            hitCount.get(),
            missCount.get(), 
            size(),
            evictionCount.get(),
            0L, // totalLoadTime 
            0L, // loadSuccessCount
            0L  // loadFailureCount
        );
    }
    
    /**
     * 캐시 히트율
     */
    public double getHitRate() {
        long hits = hitCount.get();
        long total = hits + missCount.get();
        return total == 0 ? 0.0 : (double) hits / total;
    }
    
    /**
     * 현재 캐시된 모든 항목 조회
     */
    @NotNull
    public ConcurrentMap<UUID, PlayerDataDTO> asMap() {
        ConcurrentMap<UUID, PlayerDataDTO> result = new ConcurrentHashMap<>();
        long now = System.currentTimeMillis();
        
        cache.forEach((key, entry) -> {
            if (now - entry.timestamp <= ttlMillis) {
                result.put(key, entry.data);
            }
        });
        
        return result;
    }
    
    /**
     * 만료된 항목 정리
     */
    private void cleanupExpired() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<UUID, CacheEntry>> iterator = cache.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<UUID, CacheEntry> entry = iterator.next();
            if (now - entry.getValue().timestamp > ttlMillis) {
                iterator.remove();
                evictionCount.incrementAndGet();
            }
        }
    }
    
    /**
     * 가장 오래된 항목 제거
     */
    private void evictOldestEntry() {
        UUID oldestKey = null;
        long oldestTime = Long.MAX_VALUE;
        
        for (Map.Entry<UUID, CacheEntry> entry : cache.entrySet()) {
            if (entry.getValue().timestamp < oldestTime) {
                oldestTime = entry.getValue().timestamp;
                oldestKey = entry.getKey();
            }
        }
        
        if (oldestKey != null) {
            cache.remove(oldestKey);
            evictionCount.incrementAndGet();
        }
    }
    
    /**
     * 리소스 정리
     */
    public void shutdown() {
        cleanupExecutor.shutdown();
        try {
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 캐시 엔트리
     */
    private static class CacheEntry {
        final PlayerDataDTO data;
        final long timestamp;
        
        CacheEntry(PlayerDataDTO data, long timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }
    }

    /**
         * 캐시 통계 정보
         */
        public record CacheStats(long hitCount, long missCount, long size, long evictionCount, long totalLoadTime,
                                 long loadSuccessCount, long loadFailureCount) {

        public double getHitRate() {
                long total = hitCount + missCount;
                return total == 0 ? 0.0 : (double) hitCount / total;
            }

        @Override
            public @NotNull String toString() {
                return String.format(
                        "CacheStats{size=%d, hits=%d, misses=%d, hitRate=%.2f%%, evictions=%d}",
                        size, hitCount, missCount, getHitRate() * 100, evictionCount
                );
            }
        }
}