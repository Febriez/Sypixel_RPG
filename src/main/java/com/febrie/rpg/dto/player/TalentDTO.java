package com.febrie.rpg.dto.player;

import com.febrie.rpg.util.JsonUtil;
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
        JsonObject fields = new JsonObject();
        
        fields.add("availablePoints", JsonUtil.createIntegerValue(availablePoints));
        fields.add("learnedTalents", JsonUtil.createMapField(learnedTalents, 
                value -> JsonUtil.createIntegerValue(value)));
        
        return JsonUtil.wrapInDocument(fields);
    }
    
    /**
     * JsonObject에서 TalentDTO 생성
     */
    @NotNull
    public static TalentDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return new TalentDTO();
        }
        
        JsonObject fields = JsonUtil.unwrapDocument(json);
        
        int availablePoints = JsonUtil.getIntegerValue(fields, "availablePoints", 0);
        
        Map<String, Integer> learnedTalents = JsonUtil.getMapField(fields, "learnedTalents",
                key -> key,
                obj -> JsonUtil.getIntegerValue(obj, "integerValue", 0));
        
        return new TalentDTO(availablePoints, learnedTalents);
    }
}