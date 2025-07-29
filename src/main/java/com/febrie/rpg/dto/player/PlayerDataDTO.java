package com.febrie.rpg.dto.player;

import com.febrie.rpg.util.JsonUtil;
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
        fields.add("profile", JsonUtil.createMapValue(profile.toJsonObject()));
        
        // Wallet
        fields.add("wallet", JsonUtil.createMapValue(wallet.toJsonObject()));
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static PlayerDataDTO fromJsonObject(@NotNull JsonObject json) {
        JsonUtil.validateDTOJson(json, "PlayerDataDTO");
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        // Profile
        JsonObject profileMap = JsonUtil.getMapValue(fields, "profile");
        PlayerProfileDTO profile;
        try {
            profile = profileMap.size() > 0 
                    ? PlayerProfileDTO.fromJsonObject(profileMap)
                    : new PlayerProfileDTO(UUID.randomUUID(), "", 1, 0, 0, System.currentTimeMillis());
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse PlayerDataDTO.profile: " + e.getMessage(), e);
        }
        
        // Wallet
        JsonObject walletMap = JsonUtil.getMapValue(fields, "wallet");
        WalletDTO wallet;
        try {
            wallet = walletMap.size() > 0 
                    ? WalletDTO.fromJsonObject(walletMap)
                    : new WalletDTO();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse PlayerDataDTO.wallet: " + e.getMessage(), e);
        }
        
        return new PlayerDataDTO(profile, wallet);
    }
}