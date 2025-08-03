package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.BaseFirestoreService;
import com.febrie.rpg.dto.player.PlayerDataDTO;
import com.febrie.rpg.dto.player.PlayerProfileDTO;
import com.febrie.rpg.dto.player.WalletDTO;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 플레이어 데이터 Firestore 서비스
 * players 컬렉션 관리
 *
 * @author Febrie, CoffeeTory
 */
public class PlayerFirestoreService extends BaseFirestoreService<PlayerDataDTO> {

    private static final String COLLECTION_NAME = "Player";

    public PlayerFirestoreService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        super(plugin, firestore, COLLECTION_NAME, PlayerDataDTO.class);
    }

    @Override
    protected Map<String, Object> toMap(@NotNull PlayerDataDTO dto) {
        return dto.toMap();
    }

    @Override
    @Nullable
    protected PlayerDataDTO fromDocument(@NotNull DocumentSnapshot document) {
        if (!document.exists()) {
            return null;
        }

        try {
            Map<String, Object> data = document.getData();
            
            if (data != null) {
                return PlayerDataDTO.fromMap(data);
            }
            
            throw new IllegalStateException("No data found");

        } catch (Exception e) {
            LogUtil.warning("플레이어 데이터 파싱 실패 [" + document.getId() + "]: " + e.getMessage());
            // 기본값 반환
            String docId = document.getId();
            try {
                UUID uuid = UUID.fromString(docId);
                return PlayerDataDTO.createNew(uuid, "");
            } catch (IllegalArgumentException uuidEx) {
                return PlayerDataDTO.createNew(UUID.randomUUID(), "");
            }
        }
    }

    /**
     * UUID로 플레이어 데이터 조회
     */
    @NotNull
    public CompletableFuture<PlayerDataDTO> getByUuid(@NotNull UUID uuid) {
        return get(uuid.toString()).thenApply(data -> Objects.requireNonNullElseGet(data, () -> PlayerDataDTO.createNew(uuid, "")));
    }


    // 중복 메소드 제거 - DataSyncManager를 사용하세요
    // - 재화 관련: DataSyncManager.modifyCurrency()
    // - 프로필 업데이트: DataSyncManager.updateNickname() 또는 savePlayerData()
    // - 레벨/경험치: RPGPlayer 메소드 + DataSyncManager.savePlayerData()
}