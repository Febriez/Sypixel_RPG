package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.BaseFirestoreService;
import com.febrie.rpg.dto.player.PlayerDataDTO;
import com.febrie.rpg.dto.player.PlayerProfileDTO;
import com.febrie.rpg.dto.player.WalletDTO;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.gson.JsonObject;
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
        return convertJsonToMap(dto.toJsonObject());
    }

    @Override
    @Nullable
    protected PlayerDataDTO fromDocument(@NotNull DocumentSnapshot document) {
        if (!document.exists()) {
            return null;
        }

        try {
            JsonObject json = convertMapToJson(document.getData());
            return PlayerDataDTO.fromJsonObject(json);

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


    /**
     * 플레이어 프로필만 업데이트
     */
    @NotNull
    public CompletableFuture<Void> updateProfile(@NotNull UUID uuid, @NotNull PlayerProfileDTO profile) {
        return getByUuid(uuid).thenCompose(data -> {
            PlayerDataDTO updated = new PlayerDataDTO(profile, data.wallet());
            return saveByUuid(uuid, updated);
        });
    }

    /**
     * 플레이어 지갑만 업데이트
     */
    @NotNull
    public CompletableFuture<Void> updateWallet(@NotNull UUID uuid, @NotNull WalletDTO wallet) {
        return getByUuid(uuid).thenCompose(data -> {
            PlayerDataDTO updated = new PlayerDataDTO(data.profile(), wallet);
            return saveByUuid(uuid, updated);
        });
    }

    /**
     * 플레이어 레벨 업데이트
     */
    @NotNull
    public CompletableFuture<Void> updateLevel(@NotNull UUID uuid, int level) {
        return getByUuid(uuid).thenCompose(data -> {
            PlayerProfileDTO updatedProfile = new PlayerProfileDTO(
                    data.profile().uuid(),
                    data.profile().name(),
                    level,
                    data.profile().exp(),
                    data.profile().totalExp(),
                    System.currentTimeMillis()
            );
            return updateProfile(uuid, updatedProfile);
        });
    }

    /**
     * 플레이어 경험치 추가
     */
    @NotNull
    public CompletableFuture<Void> addExp(@NotNull UUID uuid, long exp) {
        return getByUuid(uuid).thenCompose(data -> {
            PlayerProfileDTO profile = data.profile();
            PlayerProfileDTO updatedProfile = new PlayerProfileDTO(
                    profile.uuid(),
                    profile.name(),
                    profile.level(),
                    profile.exp() + exp,
                    profile.totalExp() + exp,
                    System.currentTimeMillis()
            );
            return updateProfile(uuid, updatedProfile);
        });
    }

    /**
     * 재화 추가/차감
     */
    @NotNull
    public CompletableFuture<Boolean> modifyCurrency(@NotNull UUID uuid, @NotNull String currencyType, long amount) {
        return getByUuid(uuid).thenCompose(data -> {
            Map<String, Long> currencies = new HashMap<>(data.wallet().currencies());
            long current = currencies.getOrDefault(currencyType, 0L);
            long newAmount = current + amount;

            if (newAmount < 0) {
                // 잔액 부족
                return CompletableFuture.completedFuture(false);
            }

            currencies.put(currencyType, newAmount);
            WalletDTO updatedWallet = new WalletDTO(currencies, System.currentTimeMillis());

            return updateWallet(uuid, updatedWallet).thenApply(v -> true);
        });
    }

    /**
     * 재화 설정
     */
    @NotNull
    public CompletableFuture<Void> setCurrency(@NotNull UUID uuid, @NotNull String currencyType, long amount) {
        return getByUuid(uuid).thenCompose(data -> {
            Map<String, Long> currencies = new HashMap<>(data.wallet().currencies());
            currencies.put(currencyType, Math.max(0, amount));
            WalletDTO updatedWallet = new WalletDTO(currencies, System.currentTimeMillis());
            return updateWallet(uuid, updatedWallet);
        });
    }

    /**
     * 재화 조회
     */
    @NotNull
    public CompletableFuture<Long> getCurrency(@NotNull UUID uuid, @NotNull String currencyType) {
        return getByUuid(uuid).thenApply(data ->
                data.wallet().currencies().getOrDefault(currencyType, 0L));
    }
}