package com.febrie.rpg.dto.player;

import com.febrie.rpg.util.JsonUtil;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

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
        this(new HashMap<>(), System.currentTimeMillis());
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
     * JsonObject로 변환 (서브필드용)
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject walletData = new JsonObject();
        JsonObject fields = new JsonObject();
        currencies.forEach((key, value) -> {
            fields.add(key, JsonUtil.createIntegerValue(value));
        });
        walletData.add("fields", fields);
        return walletData;
    }
    
    /**
     * JsonObject에서 WalletDTO 생성 (서브필드용)
     */
    @NotNull
    public static WalletDTO fromJsonObject(@NotNull JsonObject json) {
        Map<String, Long> currencies = new HashMap<>();
        long lastUpdated = System.currentTimeMillis();
        
        // fields 구조가 있는 경우와 없는 경우 모두 처리
        JsonObject fieldsObj = json;
        if (json.has("fields") && json.get("fields").isJsonObject()) {
            fieldsObj = json.getAsJsonObject("fields");
        }
        
        fieldsObj.entrySet().forEach(entry -> {
            if (entry.getValue().isJsonObject()) {
                currencies.put(entry.getKey(), JsonUtil.getLongValue(entry.getValue().getAsJsonObject(), "integerValue", 0L));
            }
        });
        
        return new WalletDTO(currencies, lastUpdated);
    }
}