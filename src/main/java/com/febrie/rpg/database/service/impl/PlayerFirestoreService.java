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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 플레이어 데이터 Firestore 서비스
 * players 컬렉션 관리
 *
 * @author Febrie, CoffeeTory
 */
public class PlayerFirestoreService extends BaseFirestoreService<PlayerDataDTO> {
    
    private static final String COLLECTION_NAME = "players";
    
    public PlayerFirestoreService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        super(plugin, firestore, COLLECTION_NAME, PlayerDataDTO.class);
    }
    
    @Override
    protected Map<String, Object> toMap(@NotNull PlayerDataDTO dto) {
        Map<String, Object> map = new HashMap<>();
        
        // Profile 정보
        Map<String, Object> profileMap = new HashMap<>();
        profileMap.put("uuid", dto.profile().uuid().toString());
        profileMap.put("name", dto.profile().name());
        profileMap.put("level", dto.profile().level());
        profileMap.put("exp", dto.profile().exp());
        profileMap.put("totalExp", dto.profile().totalExp());
        profileMap.put("lastPlayed", dto.profile().lastPlayed());
        map.put("profile", profileMap);
        
        // Wallet 정보
        Map<String, Object> walletMap = new HashMap<>();
        walletMap.put("currencies", dto.wallet().currencies());
        walletMap.put("lastUpdated", dto.wallet().lastUpdated());
        map.put("wallet", walletMap);
        
        return map;
    }
    
    @Override
    @Nullable
    protected PlayerDataDTO fromDocument(@NotNull DocumentSnapshot document) {
        if (!document.exists()) {
            return null;
        }
        
        try {
            // Profile 파싱
            Map<String, Object> profileData = document.get("profile", Map.class);
            PlayerProfileDTO profile = null;
            
            if (profileData != null) {
                UUID uuid = UUID.fromString((String) profileData.get("uuid"));
                String name = (String) profileData.getOrDefault("name", "");
                int level = ((Number) profileData.getOrDefault("level", 1)).intValue();
                long exp = ((Number) profileData.getOrDefault("exp", 0L)).longValue();
                long totalExp = ((Number) profileData.getOrDefault("totalExp", 0L)).longValue();
                long lastPlayed = ((Number) profileData.getOrDefault("lastPlayed", System.currentTimeMillis())).longValue();
                
                profile = new PlayerProfileDTO(uuid, name, level, exp, totalExp, lastPlayed);
            }
            
            // Wallet 파싱
            Map<String, Object> walletData = document.get("wallet", Map.class);
            WalletDTO wallet = null;
            
            if (walletData != null) {
                Map<String, Long> currencies = new HashMap<>();
                Map<String, Object> currencyData = (Map<String, Object>) walletData.get("currencies");
                if (currencyData != null) {
                    currencyData.forEach((key, value) -> {
                        currencies.put(key, ((Number) value).longValue());
                    });
                }
                long lastUpdated = ((Number) walletData.getOrDefault("lastUpdated", System.currentTimeMillis())).longValue();
                
                wallet = new WalletDTO(currencies, lastUpdated);
            }
            
            // 기본값 처리
            if (profile == null) {
                String docId = document.getId();
                UUID uuid = UUID.fromString(docId);
                profile = new PlayerProfileDTO(uuid, "", 1, 0, 0, System.currentTimeMillis());
            }
            if (wallet == null) {
                wallet = new WalletDTO();
            }
            
            return new PlayerDataDTO(profile, wallet);
            
        } catch (Exception e) {
            LogUtil.warning("플레이어 데이터 파싱 실패 [" + document.getId() + "]: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * UUID로 플레이어 데이터 조회
     */
    @NotNull
    public CompletableFuture<PlayerDataDTO> getByUuid(@NotNull UUID uuid) {
        return get(uuid.toString()).thenApply(data -> {
            if (data == null) {
                // 신규 플레이어 데이터 생성
                return PlayerDataDTO.createNew(uuid, "");
            }
            return data;
        });
    }
    
    /**
     * 플레이어 데이터 저장
     */
    @NotNull
    public CompletableFuture<Void> savePlayer(@NotNull UUID uuid, @NotNull PlayerDataDTO data) {
        return save(uuid.toString(), data);
    }
    
    /**
     * 플레이어 프로필만 업데이트
     */
    @NotNull
    public CompletableFuture<Void> updateProfile(@NotNull UUID uuid, @NotNull PlayerProfileDTO profile) {
        return getByUuid(uuid).thenCompose(data -> {
            PlayerDataDTO updated = new PlayerDataDTO(profile, data.wallet());
            return savePlayer(uuid, updated);
        });
    }
    
    /**
     * 플레이어 지갑만 업데이트
     */
    @NotNull
    public CompletableFuture<Void> updateWallet(@NotNull UUID uuid, @NotNull WalletDTO wallet) {
        return getByUuid(uuid).thenCompose(data -> {
            PlayerDataDTO updated = new PlayerDataDTO(data.profile(), wallet);
            return savePlayer(uuid, updated);
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