package com.febrie.rpg.cache;

import com.febrie.rpg.util.LogUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 통합 캐시 매니저
 * 모든 캐시를 Caffeine 기반으로 통일
 *
 * @author Febrie, CoffeeTory
 */
public class UnifiedCacheManager {

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(5);
    private static final long DEFAULT_MAX_SIZE = 1000;
    
    private final Map<String, Cache<?, ?>> caches = new ConcurrentHashMap<>();
    
    /**
     * 기본 설정으로 캐시 생성
     */
    @NotNull
    public <K, V> Cache<K, V> createCache(@NotNull String name) {
        return createCache(name, DEFAULT_TTL, DEFAULT_MAX_SIZE);
    }
    
    /**
     * TTL 지정하여 캐시 생성
     */
    @NotNull
    public <K, V> Cache<K, V> createCache(@NotNull String name, @NotNull Duration ttl) {
        return createCache(name, ttl, DEFAULT_MAX_SIZE);
    }
    
    /**
     * 전체 설정 지정하여 캐시 생성
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> createCache(@NotNull String name, @NotNull Duration ttl, long maxSize) {
        return (Cache<K, V>) caches.computeIfAbsent(name, k -> {
            LogUtil.info("Creating cache: " + name + " (TTL: " + ttl.toMinutes() + "min, MaxSize: " + maxSize + ")");
            
            return Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(ttl)
                .removalListener((RemovalListener<K, V>) (key, value, cause) -> {
                    if (cause == RemovalCause.SIZE) {
                        LogUtil.debug("Cache " + name + " evicted entry due to size limit: " + key);
                    }
                })
                .build();
        });
    }
    
    /**
     * 캐시 가져오기
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getCache(@NotNull String name) {
        return (Cache<K, V>) caches.get(name);
    }
    
    /**
     * 캐시에서 값 가져오기 (없으면 로더 함수 실행)
     */
    @Nullable
    public <K, V> V get(@NotNull String cacheName, @NotNull K key, @NotNull Function<K, V> loader) {
        Cache<K, V> cache = getCache(cacheName);
        if (cache == null) {
            cache = createCache(cacheName);
        }
        return cache.get(key, loader);
    }
    
    /**
     * 캐시에 값 저장
     */
    public <K, V> void put(@NotNull String cacheName, @NotNull K key, @NotNull V value) {
        Cache<K, V> cache = getCache(cacheName);
        if (cache == null) {
            cache = createCache(cacheName);
        }
        cache.put(key, value);
    }
    
    /**
     * 캐시에서 값 제거
     */
    public <K> void invalidate(@NotNull String cacheName, @NotNull K key) {
        Cache<K, ?> cache = getCache(cacheName);
        if (cache != null) {
            cache.invalidate(key);
        }
    }
    
    /**
     * 캐시 전체 초기화
     */
    public void invalidateAll(@NotNull String cacheName) {
        Cache<?, ?> cache = getCache(cacheName);
        if (cache != null) {
            cache.invalidateAll();
            LogUtil.info("Cache " + cacheName + " cleared");
        }
    }
    
    /**
     * 모든 캐시 초기화
     */
    public void invalidateAll() {
        caches.values().forEach(Cache::invalidateAll);
        LogUtil.info("All caches cleared");
    }
    
    /**
     * 캐시 통계 가져오기
     */
    @NotNull
    public CacheStats getStats(@NotNull String cacheName) {
        Cache<?, ?> cache = getCache(cacheName);
        if (cache != null) {
            var stats = cache.stats();
            return new CacheStats(
                stats.hitCount(),
                stats.missCount(),
                stats.loadSuccessCount(),
                stats.loadFailureCount(),
                stats.evictionCount(),
                cache.estimatedSize()
            );
        }
        return new CacheStats(0, 0, 0, 0, 0, 0);
    }
    
    /**
     * 캐시 통계 DTO
     */
    public record CacheStats(
        long hitCount,
        long missCount,
        long loadSuccessCount,
        long loadFailureCount,
        long evictionCount,
        long estimatedSize
    ) {
        public double hitRate() {
            long total = hitCount + missCount;
            return total == 0 ? 0.0 : (double) hitCount / total;
        }
    }
    
    // 싱글톤 인스턴스
    private static UnifiedCacheManager instance;
    
    public static UnifiedCacheManager getInstance() {
        if (instance == null) {
            instance = new UnifiedCacheManager();
        }
        return instance;
    }
}