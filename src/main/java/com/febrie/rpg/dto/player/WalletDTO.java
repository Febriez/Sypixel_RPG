package com.febrie.rpg.dto.player;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import net.kyori.adventure.text.Component;
/**
 * 플레이어 재화 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record WalletDTO(
        Map<String, Long> currencies,
        long lastUpdated
) {
    /**
     * 기본 생성자 - 신규 플레이어용
     */
    public WalletDTO() {
        this(getDefaultCurrencies(), System.currentTimeMillis());
    }
    
    /**
     * 기본 재화 값 가져오기
     */
    private static Map<String, Long> getDefaultCurrencies() {
        Map<String, Long> defaults = new HashMap<>();
        // CurrencyType enum의 실제 ID 사용
        defaults.put("gold", 100L);
        defaults.put("diamond", 0L);
        defaults.put("emerald", 0L);
        defaults.put("ghast_tear", 0L);
        defaults.put("nether_star", 0L);
        defaults.put("exp", 0L);
        return defaults;
    }

    /**
     * 방어적 복사를 위한 생성자
     */
    public WalletDTO(Map<String, Long> currencies, long lastUpdated) {
        this.currencies = new HashMap<>(currencies);
        this.lastUpdated = lastUpdated;
    }

    /**
     * 재화 맵의 불변 뷰 반환
     */
    @Override
    public Map<String, Long> currencies() {
        return new HashMap<>(currencies);
    }
    
    
    /**
     * Map으로 변환 (Firestore SDK용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("gold", currencies.getOrDefault("gold", 0L));
        map.put("diamond", currencies.getOrDefault("diamond", 0L));
        map.put("emerald", currencies.getOrDefault("emerald", 0L));
        map.put("ghast_tear", currencies.getOrDefault("ghast_tear", 0L));
        map.put("nether_star", currencies.getOrDefault("nether_star", 0L));
        map.put("exp", currencies.getOrDefault("exp", 0L));
        map.put("lastUpdated", lastUpdated);
        return map;
    }
    
    /**
     * Map에서 생성 (Firestore SDK용)
     */
    @NotNull
    public static WalletDTO fromMap(@NotNull Map<String, Object> map) {
        Map<String, Long> currencies = new HashMap<>();
        currencies.put("gold", FirestoreUtils.getLong(map, "gold", 100L));
        currencies.put("diamond", FirestoreUtils.getLong(map, "diamond", 0L));
        currencies.put("emerald", FirestoreUtils.getLong(map, "emerald", 0L));
        currencies.put("ghast_tear", FirestoreUtils.getLong(map, "ghast_tear", 0L));
        currencies.put("nether_star", FirestoreUtils.getLong(map, "nether_star", 0L));
        currencies.put("exp", FirestoreUtils.getLong(map, "exp", 0L));
        
        long lastUpdated = FirestoreUtils.getLong(map, "lastUpdated", System.currentTimeMillis());
        
        return new WalletDTO(currencies, lastUpdated);
    }
    
}