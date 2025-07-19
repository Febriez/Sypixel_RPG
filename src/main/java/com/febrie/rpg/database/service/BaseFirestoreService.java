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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    protected final Cache<String, T> cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    
    // 캐시 활성화 여부
    protected boolean cacheEnabled = true;
    
    /**
     * 생성자
     *
     * @param plugin 플러그인 인스턴스
     * @param firestore Firestore 인스턴스
     * @param collectionName 컬렉션 이름
     * @param dtoClass DTO 클래스
     */
    protected BaseFirestoreService(@NotNull RPGMain plugin, 
                                   @NotNull Firestore firestore,
                                   @NotNull String collectionName, 
                                   @NotNull Class<T> dtoClass) {
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
     * 하위 클래스에서 구현 필요
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
        ApiFuture<DocumentSnapshot> future = firestore
                .collection(collectionName)
                .document(documentId)
                .get();
        
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
            LogUtil.warning(String.format("%s 조회 실패 [%s]: %s", 
                    collectionName, documentId, ex.getMessage()));
            return null;
        });
    }
    
    @Override
    @NotNull
    public CompletableFuture<Void> save(@NotNull String documentId, @NotNull T data) {
        Map<String, Object> map = toMap(data);
        
        ApiFuture<WriteResult> future = firestore
                .collection(collectionName)
                .document(documentId)
                .set(map);
        
        return toCompletableFuture(future).thenAccept(result -> {
            if (cacheEnabled) {
                cache.put(documentId, data);
            }
            LogUtil.info(String.format("%s 저장 성공 [%s]", collectionName, documentId));
        }).exceptionally(ex -> {
            LogUtil.warning(String.format("%s 저장 실패 [%s]: %s", 
                    collectionName, documentId, ex.getMessage()));
            return null;
        });
    }
    
    @Override
    @NotNull
    public CompletableFuture<Void> delete(@NotNull String documentId) {
        ApiFuture<WriteResult> future = firestore
                .collection(collectionName)
                .document(documentId)
                .delete();
        
        return toCompletableFuture(future).thenAccept(result -> {
            cache.invalidate(documentId);
            LogUtil.info(String.format("%s 삭제 성공 [%s]", collectionName, documentId));
        }).exceptionally(ex -> {
            LogUtil.warning(String.format("%s 삭제 실패 [%s]: %s", 
                    collectionName, documentId, ex.getMessage()));
            return null;
        });
    }
    
    @Override
    @NotNull
    public CompletableFuture<List<T>> query(@NotNull String field, @NotNull Object value) {
        Query query = firestore.collection(collectionName)
                .whereEqualTo(field, value);
        
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
            LogUtil.warning(String.format("%s 쿼리 실패 [%s=%s]: %s", 
                    collectionName, field, value, ex.getMessage()));
            return new ArrayList<>();
        });
    }
    
    @Override
    @NotNull
    public CompletableFuture<List<T>> queryMultiple(@NotNull Map<String, Object> filters) {
        Query query = firestore.collection(collectionName);
        
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            query = query.whereEqualTo(entry.getKey(), entry.getValue());
        }
        
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
            LogUtil.warning(String.format("%s 다중 쿼리 실패: %s", 
                    collectionName, ex.getMessage()));
            return new ArrayList<>();
        });
    }
    
    @Override
    @NotNull
    public CompletableFuture<Boolean> exists(@NotNull String documentId) {
        ApiFuture<DocumentSnapshot> future = firestore
                .collection(collectionName)
                .document(documentId)
                .get();
        
        return toCompletableFuture(future).thenApply(DocumentSnapshot::exists)
                .exceptionally(ex -> {
                    LogUtil.warning(String.format("%s 존재 확인 실패 [%s]: %s", 
                            collectionName, documentId, ex.getMessage()));
                    return false;
                });
    }
    
    @Override
    @NotNull
    public CompletableFuture<Void> batchSave(@NotNull Map<String, T> documents) {
        WriteBatch batch = firestore.batch();
        
        documents.forEach((id, data) -> {
            DocumentReference ref = firestore.collection(collectionName).document(id);
            batch.set(ref, toMap(data));
        });
        
        ApiFuture<List<WriteResult>> future = batch.commit();
        
        return toCompletableFuture(future).thenAccept(results -> {
            if (cacheEnabled) {
                documents.forEach(cache::put);
            }
            LogUtil.info(String.format("%s 배치 저장 성공: %d개", 
                    collectionName, documents.size()));
        }).exceptionally(ex -> {
            LogUtil.warning(String.format("%s 배치 저장 실패: %s", 
                    collectionName, ex.getMessage()));
            return null;
        });
    }
    
    @Override
    @NotNull
    public CompletableFuture<Void> batchDelete(@NotNull List<String> documentIds) {
        WriteBatch batch = firestore.batch();
        
        documentIds.forEach(id -> {
            DocumentReference ref = firestore.collection(collectionName).document(id);
            batch.delete(ref);
        });
        
        ApiFuture<List<WriteResult>> future = batch.commit();
        
        return toCompletableFuture(future).thenAccept(results -> {
            documentIds.forEach(cache::invalidate);
            LogUtil.info(String.format("%s 배치 삭제 성공: %d개", 
                    collectionName, documentIds.size()));
        }).exceptionally(ex -> {
            LogUtil.warning(String.format("%s 배치 삭제 실패: %s", 
                    collectionName, ex.getMessage()));
            return null;
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
        }, plugin.getServer().getScheduler().getMainThreadExecutor(plugin));
        
        return completableFuture;
    }
}