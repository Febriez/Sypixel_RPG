package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.GenericFirestoreService;
import com.febrie.rpg.dto.player.PlayerDataDTO;
import com.google.cloud.firestore.Firestore;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import net.kyori.adventure.text.Component;
/**
 * 플레이어 데이터 Firestore 서비스
 * players 컬렉션 관리
 *
 * @author Febrie, CoffeeTory
 */
public class PlayerFirestoreService {

    private static final String COLLECTION_NAME = "Player";
    private final GenericFirestoreService<PlayerDataDTO> service;

    public PlayerFirestoreService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        this.service = GenericFirestoreService.create(
            plugin, 
            firestore, 
            COLLECTION_NAME, 
            PlayerDataDTO.class,
            PlayerDataDTO::toMap,
            PlayerDataDTO::fromMap,
            id -> {
                try {
                    UUID uuid = UUID.fromString(id);
                    return PlayerDataDTO.createNew(uuid, "");
                } catch (IllegalArgumentException e) {
                    return PlayerDataDTO.createNew(UUID.randomUUID(), "");
                }
            }
        );
    }

    /**
     * 문서 ID로 데이터 조회
     */
    @NotNull
    public CompletableFuture<PlayerDataDTO> get(@NotNull String id) {
        return service.get(id);
    }

    /**
     * 데이터 저장
     */
    @NotNull
    public CompletableFuture<Void> save(@NotNull String id, @NotNull PlayerDataDTO data) {
        return service.save(id, data);
    }

    /**
     * 데이터 삭제
     */
    @NotNull
    public CompletableFuture<Void> delete(@NotNull String id) {
        return service.delete(id);
    }

    /**
     * 문서 존재 여부 확인
     */
    @NotNull
    public CompletableFuture<Boolean> exists(@NotNull String id) {
        return service.exists(id);
    }

    /**
     * UUID로 플레이어 데이터 조회
     */
    @NotNull
    public CompletableFuture<PlayerDataDTO> getByUuid(@NotNull UUID uuid) {
        return service.get(uuid.toString()).thenApply(data -> 
            Objects.requireNonNullElseGet(data, () -> PlayerDataDTO.createNew(uuid, "")));
    }

    // 중복 메소드 제거 - DataSyncManager를 사용하세요
    // - 재화 관련: DataSyncManager.modifyCurrency()
    // - 프로필 업데이트: DataSyncManager.updateNickname() 또는 savePlayerData()
    // - 레벨/경험치: RPGPlayer 메소드 + DataSyncManager.savePlayerData()
}