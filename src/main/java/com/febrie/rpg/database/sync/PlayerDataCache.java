package com.febrie.rpg.database.sync;

import com.febrie.rpg.cache.UnifiedCacheManager;
import com.febrie.rpg.database.constants.DatabaseConstants;
import com.febrie.rpg.dto.player.PlayerDataDTO;
import com.github.benmanes.caffeine.cache.Cache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * 플레이어 데이터 캐시
 * UnifiedCacheManager 기반 Caffeine 캐시 사용
 *
 * @author Febrie, CoffeeTory
 */
public class PlayerDataCache {

    private final Cache<UUID, PlayerDataDTO> cache;
    private final UnifiedCacheManager cacheManager;
    
    // 캐시 이름
    private static final String CACHE_NAME = "playerData";
    
    // 캐시 유효 시간 (5분)
    private static final Duration CACHE_DURATION = Duration.ofMinutes(5);

    public PlayerDataCache() {
        this.cacheManager = UnifiedCacheManager.getInstance();
        // Caffeine 캐시 초기화 (TTL 5분, 최대 5000개)
        this.cache = cacheManager.createCache(
            CACHE_NAME, 
            CACHE_DURATION, 
            DatabaseConstants.PLAYER_CACHE_MAX_SIZE
        );
    }

    /**
     * 캐시에 데이터 저장
     */
    public void put(@NotNull UUID playerId, @NotNull PlayerDataDTO data) {
        cache.put(playerId, data);
    }

    /**
     * 캐시에서 데이터 조회
     */
    @Nullable
    public PlayerDataDTO get(@NotNull UUID playerId) {
        return cache.getIfPresent(playerId);
    }

    /**
     * 캐시에서 데이터 제거
     */
    public void invalidate(@NotNull UUID playerId) {
        cache.invalidate(playerId);
    }

    /**
     * 모든 캐시 데이터 제거
     */
    public void invalidateAll() {
        cache.invalidateAll();
    }

    /**
     * 캐시 정리 (Caffeine이 자동으로 처리)
     */
    public void cleanUp() {
        cache.cleanUp();
    }

    /**
     * 캐시 크기
     */
    public long size() {
        return cache.estimatedSize();
    }

    /**
     * 캐시 통계
     */
    @NotNull
    public CacheStats getStats() {
        var stats = cache.stats();
        return new CacheStats(
            stats.hitCount(),
            stats.missCount(),
            cache.estimatedSize(),
            stats.evictionCount(),
            stats.totalLoadTime(),
            stats.loadSuccessCount(),
            stats.loadFailureCount()
        );
    }

    /**
     * 캐시 히트율
     */
    public double getHitRate() {
        var stats = cache.stats();
        return stats.hitRate();
    }

    /**
     * 현재 캐시된 모든 항목 조회
     */
    @NotNull
    public ConcurrentMap<UUID, PlayerDataDTO> asMap() {
        return cache.asMap();
    }

    /**
     * 리소스 정리 (Caffeine은 자동 관리되므로 특별한 정리 불필요)
     */
    public void shutdown() {
        invalidateAll();
    }

    /**
     * 캐시 통계 정보
     */
    public record CacheStats(
        long hitCount, 
        long missCount, 
        long size, 
        long evictionCount, 
        long totalLoadTime,
        long loadSuccessCount, 
        long loadFailureCount
    ) {

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