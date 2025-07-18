package com.febrie.rpg.dto.player;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어 특성 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record TalentDTO(
        int availablePoints,
        Map<String, Integer> learnedTalents
) {
    /**
     * 기본 생성자 - 신규 플레이어용
     */
    public TalentDTO() {
        this(0, new HashMap<>());
    }

    /**
     * 방어적 복사를 위한 생성자
     */
    public TalentDTO(int availablePoints, Map<String, Integer> learnedTalents) {
        this.availablePoints = availablePoints;
        this.learnedTalents = new HashMap<>(learnedTalents);
    }

    /**
     * 학습한 특성 맵의 불변 뷰 반환
     */
    @Override
    public Map<String, Integer> learnedTalents() {
        return new HashMap<>(learnedTalents);
    }
    
    /**
     * JsonObject로 변환
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        // availablePoints
        JsonObject availablePointsValue = new JsonObject();
        availablePointsValue.addProperty("integerValue", availablePoints);
        fields.add("availablePoints", availablePointsValue);
        
        // learnedTalents 맵
        JsonObject learnedTalentsValue = new JsonObject();
        JsonObject mapValue = new JsonObject();
        JsonObject talentsFields = new JsonObject();
        learnedTalents.forEach((key, value) -> {
            JsonObject intValue = new JsonObject();
            intValue.addProperty("integerValue", value);
            talentsFields.add(key, intValue);
        });
        mapValue.add("fields", talentsFields);
        learnedTalentsValue.add("mapValue", mapValue);
        fields.add("learnedTalents", learnedTalentsValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 TalentDTO 생성
     */
    @NotNull
    public static TalentDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return new TalentDTO();
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        int availablePoints = fields.has("availablePoints") && fields.getAsJsonObject("availablePoints").has("integerValue")
                ? fields.getAsJsonObject("availablePoints").get("integerValue").getAsInt()
                : 0;
        
        Map<String, Integer> learnedTalents = new HashMap<>();
        if (fields.has("learnedTalents") && fields.getAsJsonObject("learnedTalents").has("mapValue")) {
            JsonObject mapValue = fields.getAsJsonObject("learnedTalents").getAsJsonObject("mapValue");
            if (mapValue.has("fields")) {
                JsonObject talentFields = mapValue.getAsJsonObject("fields");
                talentFields.entrySet().forEach(entry -> {
                    if (entry.getValue().isJsonObject() && entry.getValue().getAsJsonObject().has("integerValue")) {
                        learnedTalents.put(entry.getKey(), entry.getValue().getAsJsonObject().get("integerValue").getAsInt());
                    }
                });
            }
        }
        
        return new TalentDTO(availablePoints, learnedTalents);
    }
}