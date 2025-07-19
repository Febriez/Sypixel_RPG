package com.febrie.rpg.dto.player;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * 플레이어 데이터 DTO
 * 프로필과 지갑 정보를 포함
 */
public record PlayerDataDTO(
        @NotNull PlayerProfileDTO profile,
        @NotNull WalletDTO wallet
) {
    /**
     * 신규 플레이어 생성
     */
    public static PlayerDataDTO createNew(@NotNull UUID playerId, @NotNull String playerName) {
        return new PlayerDataDTO(
                new PlayerProfileDTO(playerId, playerName, 1, 0, 0, System.currentTimeMillis()),
                new WalletDTO()
        );
    }
    
    /**
     * JsonObject로 변환
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        // Profile
        JsonObject profileValue = new JsonObject();
        profileValue.add("mapValue", profile.toJsonObject());
        fields.add("profile", profileValue);
        
        // Wallet
        JsonObject walletValue = new JsonObject();
        walletValue.add("mapValue", wallet.toJsonObject());
        fields.add("wallet", walletValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static PlayerDataDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            throw new IllegalArgumentException("Invalid PlayerDataDTO JSON: missing fields");
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        PlayerProfileDTO profile = null;
        if (fields.has("profile") && fields.getAsJsonObject("profile").has("mapValue")) {
            profile = PlayerProfileDTO.fromJsonObject(
                    fields.getAsJsonObject("profile").getAsJsonObject("mapValue")
            );
        }
        
        WalletDTO wallet = null;
        if (fields.has("wallet") && fields.getAsJsonObject("wallet").has("mapValue")) {
            wallet = WalletDTO.fromJsonObject(
                    fields.getAsJsonObject("wallet").getAsJsonObject("mapValue")
            );
        }
        
        // 기본값 처리
        if (profile == null) {
            profile = new PlayerProfileDTO(UUID.randomUUID(), "", 1, 0, 0, System.currentTimeMillis());
        }
        if (wallet == null) {
            wallet = new WalletDTO();
        }
        
        return new PlayerDataDTO(profile, wallet);
    }
}