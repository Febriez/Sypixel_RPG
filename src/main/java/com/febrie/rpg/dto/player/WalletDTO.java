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
        JsonObject fields = new JsonObject();
        currencies.forEach((key, value) -> {
            fields.add(key, JsonUtil.createIntegerValue(value));
        });
        return fields;
    }
    
    /**
     * Firebase Document 형식의 JsonObject로 변환
     */
    @NotNull
    public JsonObject toFirestoreDocument() {
        JsonObject fields = new JsonObject();
        
        fields.add("currencies", JsonUtil.createMapField(currencies, 
                value -> JsonUtil.createIntegerValue(value)));
        fields.add("lastUpdated", JsonUtil.createIntegerValue(lastUpdated));
        
        return JsonUtil.wrapInDocument(fields);
    }
    
    /**
     * JsonObject에서 WalletDTO 생성 (서브필드용)
     */
    @NotNull
    public static WalletDTO fromJsonObject(@NotNull JsonObject json) {
        Map<String, Long> currencies = new HashMap<>();
        long lastUpdated = System.currentTimeMillis();
        
        json.entrySet().forEach(entry -> {
            if (entry.getValue().isJsonObject()) {
                currencies.put(entry.getKey(), JsonUtil.getLongValue(entry.getValue().getAsJsonObject(), "integerValue", 0L));
            }
        });
        
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
        
        JsonObject fields = JsonUtil.unwrapDocument(json);
        
        Map<String, Long> currencies = JsonUtil.getMapField(fields, "currencies",
                key -> key,
                obj -> JsonUtil.getLongValue(obj, "integerValue", 0L));
        
        long lastUpdated = JsonUtil.getLongValue(fields, "lastUpdated", System.currentTimeMillis());
        
        return new WalletDTO(currencies, lastUpdated);
    }
}