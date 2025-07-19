package com.febrie.rpg.database.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Firestore 서비스 기본 인터페이스
 * 모든 Firestore 서비스가 구현해야 하는 기본 CRUD 작업 정의
 *
 * @param <T> DTO 타입
 * @author Febrie, CoffeeTory
 */
public interface FirestoreService<T> {
    
    /**
     * 단일 문서 조회
     *
     * @param documentId 문서 ID
     * @return 문서 데이터 (없으면 null)
     */
    @NotNull
    CompletableFuture<@Nullable T> get(@NotNull String documentId);
    
    /**
     * 단일 문서 저장 (업데이트 포함)
     *
     * @param documentId 문서 ID
     * @param data 저장할 데이터
     * @return 완료 Future
     */
    @NotNull
    CompletableFuture<Void> save(@NotNull String documentId, @NotNull T data);
    
    /**
     * 단일 문서 삭제
     *
     * @param documentId 문서 ID
     * @return 완료 Future
     */
    @NotNull
    CompletableFuture<Void> delete(@NotNull String documentId);
    
    /**
     * 조건에 맞는 문서들 조회
     *
     * @param field 필드명
     * @param value 값
     * @return 문서 목록
     */
    @NotNull
    CompletableFuture<List<T>> query(@NotNull String field, @NotNull Object value);
    
    /**
     * 여러 조건으로 문서들 조회
     *
     * @param filters 필터 맵 (필드명 -> 값)
     * @return 문서 목록
     */
    @NotNull
    CompletableFuture<List<T>> queryMultiple(@NotNull Map<String, Object> filters);
    
    /**
     * 문서 존재 여부 확인
     *
     * @param documentId 문서 ID
     * @return 존재 여부
     */
    @NotNull
    CompletableFuture<Boolean> exists(@NotNull String documentId);
    
    /**
     * 배치 저장
     *
     * @param documents 문서 맵 (ID -> 데이터)
     * @return 완료 Future
     */
    @NotNull
    CompletableFuture<Void> batchSave(@NotNull Map<String, T> documents);
    
    /**
     * 배치 삭제
     *
     * @param documentIds 삭제할 문서 ID 목록
     * @return 완료 Future
     */
    @NotNull
    CompletableFuture<Void> batchDelete(@NotNull List<String> documentIds);
}