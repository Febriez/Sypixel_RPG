package com.febrie.rpg.framework;

import com.febrie.rpg.database.service.FirestoreService;
import com.febrie.rpg.util.LogUtil;
import com.febrie.rpg.util.RetryUtil;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 데이터 관리자를 위한 추상 클래스
 * 캐싱, 자동 저장, 더티 체크 등의 공통 기능 제공
 * 
 * @param <K> 키 타입 (예: UUID)
 * @param <V> 값 타입 (예: PlayerData)
 * @author CoffeeTory
 */
public abstract class AbstractDataManager<K, V> {
    
    protected final Plugin plugin;
    protected final FirestoreService<V> firestoreService;
    
    // 캐시 관리
    private final Map<K, V> cache = new ConcurrentHashMap<>();
    private final Set<K> dirtyEntries = ConcurrentHashMap.newKeySet();
    
    // 저장 관리
    private final Map<K, AtomicLong> lastSaveTime = new ConcurrentHashMap<>();
    private final AtomicBoolean isSaving = new AtomicBoolean(false);
    private BukkitTask autoSaveTask;
    
    // 설정
    private final long saveInterval;
    private final long saveCooldown;
    private final boolean enableAutoSave;
    
    /**
     * 생성자
     */
    protected AbstractDataManager(@NotNull Plugin plugin,
                                  @NotNull FirestoreService<V> firestoreService,
                                  long saveInterval,
                                  long saveCooldown,
                                  boolean enableAutoSave) {
        this.plugin = plugin;
        this.firestoreService = firestoreService;
        this.saveInterval = saveInterval;
        this.saveCooldown = saveCooldown;
        this.enableAutoSave = enableAutoSave;
    }
    
    /**
     * 매니저 초기화 - 하위 클래스에서 생성 후 호출해야 함
     */
    protected void initialize() {
        if (enableAutoSave) {
            startAutoSave();
        }
    }
    
    /**
     * Firestore에서 데이터 로드
     */
    protected CompletableFuture<V> loadFromFirestore(@NotNull K key) {
        String documentId = convertKeyToId(key);
        return firestoreService.get(documentId);
    }
    
    /**
     * Firestore에 데이터 저장
     */
    protected CompletableFuture<Boolean> saveToFirestore(@NotNull K key, @NotNull V value) {
        String documentId = convertKeyToId(key);
        return firestoreService.save(documentId, value)
            .thenApply(v -> true)
            .exceptionally(ex -> {
                LogUtil.error(getManagerName() + " - Firestore 저장 실패: " + documentId, ex);
                return false;
            });
    }
    
    /**
     * 키를 Firestore 문서 ID로 변환
     * 
     * @param key 변환할 키
     * @return Firestore 문서 ID
     */
    protected abstract String convertKeyToId(@NotNull K key);
    
    /**
     * 관리자 이름 (로깅용)
     */
    protected abstract String getManagerName();
    
    /**
     * 데이터 가져오기
     */
    public CompletableFuture<V> get(@NotNull K key) {
        V cached = cache.get(key);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }
        
        return loadFromFirestore(key)
            .thenApply(value -> {
                if (value != null) {
                    cache.put(key, value);
                }
                return value;
            })
            .exceptionally(ex -> {
                LogUtil.error(getManagerName() + " - 데이터 로드 실패: " + key, ex);
                return null;
            });
    }
    
    /**
     * 데이터 설정
     */
    public void put(@NotNull K key, @NotNull V value) {
        cache.put(key, value);
        markDirty(key);
    }
    
    /**
     * 데이터 제거
     */
    public void remove(@NotNull K key) {
        cache.remove(key);
        dirtyEntries.remove(key);
        lastSaveTime.remove(key);
    }
    
    /**
     * 캐시에서 직접 가져오기 (로드하지 않음)
     */
    @Nullable
    public V getFromCache(@NotNull K key) {
        return cache.get(key);
    }
    
    /**
     * 더티 마킹
     */
    public void markDirty(@NotNull K key) {
        dirtyEntries.add(key);
    }
    
    /**
     * 단일 엔트리 저장
     */
    public CompletableFuture<Boolean> save(@NotNull K key, boolean force) {
        // 플러그인이 비활성화된 경우 저장하지 않음
        if (!plugin.isEnabled()) {
            return CompletableFuture.completedFuture(false);
        }
        
        V value = cache.get(key);
        if (value == null) {
            return CompletableFuture.completedFuture(false);
        }
        
        // 쿨다운 체크
        if (!force && !canSave(key)) {
            return CompletableFuture.completedFuture(true);
        }
        
        // 마지막 저장 시간 업데이트
        updateLastSaveTime(key);
        
        return RetryUtil.executeWithRetry(() -> 
            saveToFirestore(key, value)
                .thenApply(success -> {
                    if (success) {
                        dirtyEntries.remove(key);
                    } else {
                        LogUtil.error(getManagerName() + " - 데이터 저장 실패: " + key);
                    }
                    return success;
                })
        , false);
    }
    
    /**
     * 모든 더티 엔트리 저장
     */
    public CompletableFuture<Void> saveAll(boolean force) {
        if (!force && isSaving.get()) {
            return CompletableFuture.completedFuture(null);
        }
        
        isSaving.set(true);
        
        Set<K> entriesToSave = force ? cache.keySet() : Set.copyOf(dirtyEntries);
        
        var futures = entriesToSave.stream()
            .map(key -> save(key, force))
            .collect(Collectors.toList());
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0]))
            .whenComplete((result, error) -> {
                isSaving.set(false);
                if (error != null) {
                    LogUtil.error(getManagerName() + " - 전체 저장 중 오류", error);
                }
            });
    }
    
    /**
     * 저장 가능 여부 확인
     */
    private boolean canSave(@NotNull K key) {
        AtomicLong lastSave = lastSaveTime.computeIfAbsent(key, k -> new AtomicLong(0));
        long currentTime = System.currentTimeMillis();
        long lastTime = lastSave.get();
        
        return lastTime == 0 || (currentTime - lastTime) >= saveCooldown;
    }
    
    /**
     * 마지막 저장 시간 업데이트
     */
    private void updateLastSaveTime(@NotNull K key) {
        lastSaveTime.computeIfAbsent(key, k -> new AtomicLong(0))
            .set(System.currentTimeMillis());
    }
    
    /**
     * 자동 저장 시작
     */
    private void startAutoSave() {
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
        }
        
        autoSaveTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(
            plugin,
            () -> {
                if (!dirtyEntries.isEmpty()) {
                    saveAll(false);
                }
            },
            saveInterval,
            saveInterval
        );
    }
    
    /**
     * 자동 저장 중지
     */
    public void stopAutoSave() {
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
            autoSaveTask = null;
        }
    }
    
    /**
     * 종료 처리
     */
    public CompletableFuture<Void> shutdown() {
        stopAutoSave();
        return saveAll(true);
    }
    
    /**
     * 통계 정보
     */
    public String getStats() {
        return String.format("%s 통계 - 캐시: %d개, 더티: %d개, 저장 중: %s",
            getManagerName(),
            cache.size(),
            dirtyEntries.size(),
            isSaving.get() ? "예" : "아니오"
        );
    }
    
    /**
     * 캐시 크기
     */
    public int getCacheSize() {
        return cache.size();
    }
    
    /**
     * 더티 엔트리 수
     */
    public int getDirtyCount() {
        return dirtyEntries.size();
    }
    
    /**
     * 캐시 지우기
     */
    public void clearCache() {
        cache.clear();
        dirtyEntries.clear();
        lastSaveTime.clear();
    }
}