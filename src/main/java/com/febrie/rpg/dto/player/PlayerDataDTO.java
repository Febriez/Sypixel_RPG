package com.febrie.rpg.dto.player;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
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
     * Map으로 변환 (Firestore SDK용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("profile", profile.toMap());
        map.put("wallet", wallet.toMap());
        return map;
    }
    
    /**
     * Map에서 생성 (Firestore SDK용)
     */
    @NotNull
    public static PlayerDataDTO fromMap(@NotNull Map<String, Object> map) {
        Map<String, Object> profileMap = FirestoreUtils.getMap(map, "profile", new HashMap<>());
        Map<String, Object> walletMap = FirestoreUtils.getMap(map, "wallet", new HashMap<>());
        
        PlayerProfileDTO profile = profileMap.isEmpty() 
            ? new PlayerProfileDTO(UUID.randomUUID(), "", 1, 0, 0, System.currentTimeMillis())
            : PlayerProfileDTO.fromMap(profileMap);
            
        WalletDTO wallet = walletMap.isEmpty() 
            ? new WalletDTO()
            : WalletDTO.fromMap(walletMap);
        
        return new PlayerDataDTO(profile, wallet);
    }
    
}