package com.febrie.rpg.dto.player;

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
        JsonObject json = new JsonObject();
        JsonObject mapValue = new JsonObject();
        
        JsonObject currenciesMap = new JsonObject();
        currencies.forEach((key, value) -> {
            JsonObject longValue = new JsonObject();
            longValue.addProperty("integerValue", value);
            currenciesMap.add(key, longValue);
        });
        mapValue.add("fields", currenciesMap);
        json.add("mapValue", mapValue);
        
        return json;
    }
    
    /**
     * Firebase Document 형식의 JsonObject로 변환
     */
    @NotNull
    public JsonObject toFirestoreDocument() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        // currencies 맵
        JsonObject currenciesValue = new JsonObject();
        JsonObject mapValue = new JsonObject();
        JsonObject currenciesFields = new JsonObject();
        currencies.forEach((key, value) -> {
            JsonObject longValue = new JsonObject();
            longValue.addProperty("integerValue", value);
            currenciesFields.add(key, longValue);
        });
        mapValue.add("fields", currenciesFields);
        currenciesValue.add("mapValue", mapValue);
        fields.add("currencies", currenciesValue);
        
        // lastUpdated
        JsonObject lastUpdatedValue = new JsonObject();
        lastUpdatedValue.addProperty("integerValue", lastUpdated);
        fields.add("lastUpdated", lastUpdatedValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 WalletDTO 생성 (서브필드용)
     */
    @NotNull
    public static WalletDTO fromJsonObject(@NotNull JsonObject json) {
        Map<String, Long> currencies = new HashMap<>();
        long lastUpdated = System.currentTimeMillis();
        
        if (json.has("mapValue") && json.getAsJsonObject("mapValue").has("fields")) {
            JsonObject fields = json.getAsJsonObject("mapValue").getAsJsonObject("fields");
            fields.entrySet().forEach(entry -> {
                if (entry.getValue().isJsonObject() && entry.getValue().getAsJsonObject().has("integerValue")) {
                    currencies.put(entry.getKey(), entry.getValue().getAsJsonObject().get("integerValue").getAsLong());
                }
            });
        }
        
        return new WalletDTO(currencies, lastUpdated);
    }
    
    /**
     * Firebase Document 형식의 JsonObject에서 WalletDTO 생성
     */
    @NotNull
    public static WalletDTO fromFirestoreDocument(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return new WalletDTO();
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        Map<String, Long> currencies = new HashMap<>();
        
        if (fields.has("currencies") && fields.getAsJsonObject("currencies").has("mapValue")) {
            JsonObject mapValue = fields.getAsJsonObject("currencies").getAsJsonObject("mapValue");
            if (mapValue.has("fields")) {
                JsonObject currencyFields = mapValue.getAsJsonObject("fields");
                currencyFields.entrySet().forEach(entry -> {
                    if (entry.getValue().isJsonObject() && entry.getValue().getAsJsonObject().has("integerValue")) {
                        currencies.put(entry.getKey(), entry.getValue().getAsJsonObject().get("integerValue").getAsLong());
                    }
                });
            }
        }
        
        long lastUpdated = fields.has("lastUpdated") && fields.getAsJsonObject("lastUpdated").has("integerValue")
                ? fields.getAsJsonObject("lastUpdated").get("integerValue").getAsLong()
                : System.currentTimeMillis();
        
        return new WalletDTO(currencies, lastUpdated);
    }
}