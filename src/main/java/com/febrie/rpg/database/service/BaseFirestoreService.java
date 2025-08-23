package com.febrie.rpg.database.service;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.util.LogUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
/**
 * Firestore 서비스 기본 구현체
 * 캐싱, 에러 처리, 공통 로직 제공
 *
 * @param <T> DTO 타입
 * @author Febrie, CoffeeTory
 */
public abstract class BaseFirestoreService<T> implements FirestoreService<T> {
    protected final RPGMain plugin;
    protected final Firestore firestore;
    protected final String collectionName;
    protected final Class<T> dtoClass;
    // 캐시 설정 (5분 만료, 최대 1000개)
    protected final Cache<String, T> cache = Caffeine.newBuilder().maximumSize(1000).expireAfterWrite(5, TimeUnit.MINUTES).build();
    // 캐시 활성화 여부
    protected boolean cacheEnabled = true;
    /**
     * 생성자
     *
     * @param plugin         플러그인 인스턴스
     * @param firestore      Firestore 인스턴스
     * @param collectionName 컬렉션 이름
     * @param dtoClass       DTO 클래스
     */
    protected BaseFirestoreService(@NotNull RPGMain plugin, @NotNull Firestore firestore, @NotNull String collectionName, @NotNull Class<T> dtoClass) {
        this.plugin = plugin;
        this.firestore = firestore;
        this.collectionName = collectionName;
        this.dtoClass = dtoClass;
    }
    
    /**
     * DTO를 Map으로 변환
     * 하위 클래스에서 구현 필요
     */
    protected abstract Map<String, Object> toMap(@NotNull T dto);
    
    /**
     * DocumentSnapshot을 DTO로 변환
     */
    @Nullable
    protected abstract T fromDocument(@NotNull DocumentSnapshot document);
    @Override
    @NotNull
    public CompletableFuture<@Nullable T> get(@NotNull String documentId) {
        // 캐시 확인
        if (cacheEnabled) {
            T cached = cache.getIfPresent(documentId);
            if (cached != null) {
                return CompletableFuture.completedFuture(cached);
            }
        }
        // Firestore에서 조회
        ApiFuture<DocumentSnapshot> future = firestore.collection(collectionName).document(documentId).get();
        return toCompletableFuture(future).thenApply(document -> {
            if (document.exists()) {
                T data = fromDocument(document);
                if (data != null && cacheEnabled) {
                    cache.put(documentId, data);
                }
                return data;
            }
            return null;
        }).exceptionally(ex -> {
            LogUtil.warning(String.format("%s 조회 실패 [%s]: %s", collectionName, documentId, ex.getMessage()));
            return null;
        });
    }
    
    @Override
    @NotNull
    public CompletableFuture<Void> save(@NotNull String documentId, @NotNull T data) {
        Map<String, Object> map = toMap(data);
        ApiFuture<WriteResult> future = firestore.collection(collectionName).document(documentId).set(map);
        return toCompletableFuture(future).thenAccept(result -> {
            if (cacheEnabled) {
                cache.put(documentId, data);
            }
            LogUtil.info(String.format("%s 저장 성공 [%s]", collectionName, documentId));
        }).exceptionally(ex -> {
            LogUtil.error(String.format("%s 저장 실패 [%s]", collectionName, documentId), ex);
            return null;
        });
    }
    
    @Override
    @NotNull
    public CompletableFuture<Void> delete(@NotNull String documentId) {
        ApiFuture<WriteResult> future = firestore.collection(collectionName).document(documentId).delete();
        return toCompletableFuture(future).thenAccept(result -> {
            cache.invalidate(documentId);
            LogUtil.info(String.format("%s 삭제 성공 [%s]", collectionName, documentId));
        }).exceptionally(ex -> {
            LogUtil.warning(String.format("%s 삭제 실혨 [%s]: %s", collectionName, documentId, ex.getMessage()));
            return null;
        });
    }
    
    @Override
    @NotNull
    public CompletableFuture<List<T>> query(@NotNull String field, @NotNull Object value) {
        Query query = firestore.collection(collectionName).whereEqualTo(field, value);
        return executeQuery(query);
    }
    
    @Override
    @NotNull
    public CompletableFuture<List<T>> queryMultiple(@NotNull Map<String, Object> filters) {
        Query query = firestore.collection(collectionName);
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            query = query.whereEqualTo(entry.getKey(), entry.getValue());
        }
        return executeQuery(query);
    }
    
    @Override
    @NotNull
    public CompletableFuture<Boolean> exists(@NotNull String documentId) {
        ApiFuture<DocumentSnapshot> future = firestore.collection(collectionName).document(documentId).get();
        return toCompletableFuture(future).thenApply(DocumentSnapshot::exists).exceptionally(ex -> {
            LogUtil.warning(String.format("%s 존재 확인 실패 [%s]: %s", collectionName, documentId, ex.getMessage()));
            return false;
        });
    }
    
    /**
     * 캐시 비우기
     */
    public void clearCache() {
        cache.invalidateAll();
    }
    
    /**
     * 캐시 활성화/비활성화
     */
    public void setCacheEnabled(boolean enabled) {
        this.cacheEnabled = enabled;
        if (!enabled) {
            clearCache();
        }
    }
    
    /**
     * 서비스 종료 시 정리 작업
     */
    public void shutdown() {
        clearCache();
    }
    
    /**
     * UUID로 데이터 조회
     */
    public CompletableFuture<@Nullable T> getByUuid(@NotNull UUID uuid) {
        return get(uuid.toString());
    }
    
    /**
     * UUID로 데이터 저장
     */
    public CompletableFuture<Void> saveByUuid(@NotNull UUID uuid, @NotNull T data) {
        return save(uuid.toString(), data);
    }
    
    /**
     * UUID로 데이터 삭제
     */
    public CompletableFuture<Void> deleteByUuid(@NotNull UUID uuid) {
        return delete(uuid.toString());
    }
    
    /**
     * 특정 필드 업데이트
     */
    public CompletableFuture<Void> updateField(@NotNull String documentId, @NotNull String field, @NotNull Object value) {
        ApiFuture<WriteResult> future = firestore.collection(collectionName).document(documentId).update(field, value);
        return toCompletableFuture(future).thenAccept(result -> {
            cache.invalidate(documentId); // 캐시 무효화
            LogUtil.info(String.format("%s 필드 업데이트 성공 [%s.%s]", collectionName, documentId, field));
        }).exceptionally(ex -> {
            LogUtil.warning(String.format("%s 필드 업데이트 실패 [%s.%s]: %s", collectionName, documentId, field, ex.getMessage()));
            return null;
        });
    }
    
    /**
     * 숫자 필드 증가
     */
    public CompletableFuture<Void> incrementField(@NotNull String documentId, @NotNull String field, long amount) {
        ApiFuture<WriteResult> future = firestore.collection(collectionName).document(documentId).update(field, FieldValue.increment(amount));
        return toCompletableFuture(future).thenAccept(result -> {
            cache.invalidate(documentId); // 캐시 무효화
            LogUtil.info(String.format("%s 필드 증가 성공 [%s.%s += %d]", collectionName, documentId, field, amount));
        }).exceptionally(ex -> {
            LogUtil.warning(String.format("%s 필드 증가 실패 [%s.%s]: %s", collectionName, documentId, field, ex.getMessage()));
            return null;
        });
    }
    
    /**
     * 제한된 쿼리
     */
    public CompletableFuture<List<T>> queryWithLimit(@NotNull String field, @NotNull Object value, int limit) {
        Query query = firestore.collection(collectionName).whereEqualTo(field, value).limit(limit);
        return executeQuery(query);
    }
    
    /**
     * 정렬된 쿼리
     */
    public CompletableFuture<List<T>> queryOrdered(@NotNull String orderByField, @NotNull Query.Direction direction, int limit) {
        Query query = firestore.collection(collectionName).orderBy(orderByField, direction).limit(limit);
        return executeQuery(query);
    }
    
    /**
     * 쿼리 실행 헬퍼 메소드
     */
    protected CompletableFuture<List<T>> executeQuery(@NotNull Query query) {
        ApiFuture<QuerySnapshot> future = query.get();
        return toCompletableFuture(future).thenApply(snapshot -> {
            List<T> results = new ArrayList<>();
            for (DocumentSnapshot document : snapshot.getDocuments()) {
                T data = fromDocument(document);
                if (data != null) {
                    results.add(data);
                    if (cacheEnabled) {
                        cache.put(document.getId(), data);
                    }
                }
            }
            return results;
        }).exceptionally(ex -> {
            LogUtil.warning(String.format("%s 쿼리 실패: %s", collectionName, ex.getMessage()));
            return new ArrayList<>();
        });
    }
    
    /**
     * ApiFuture를 CompletableFuture로 변환
     */
    protected <V> CompletableFuture<V> toCompletableFuture(ApiFuture<V> apiFuture) {
        CompletableFuture<V> completableFuture = new CompletableFuture<>();
        apiFuture.addListener(() -> {
            try {
                completableFuture.complete(apiFuture.get());
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        }, command -> {
            // 플러그인이 활성화되어 있을 때만 스케줄러 사용
            if (plugin.isEnabled()) {
                plugin.getServer().getScheduler().runTask(plugin, command);
            } else {
                // 플러그인이 비활성화된 경우 직접 실행
                command.run();
            }
        });
        return completableFuture;
    }
}
