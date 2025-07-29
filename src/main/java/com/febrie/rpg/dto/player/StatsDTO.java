package com.febrie.rpg.dto.player;

import com.febrie.rpg.util.JsonUtil;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * 플레이어 스탯 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record StatsDTO(
        int strength,
        int intelligence,
        int dexterity,
        int vitality,
        int wisdom,
        int luck
) {
    /**
     * 기본 생성자 - 초기 스탯
     */
    public StatsDTO() {
        this(10, 10, 10, 10, 10, 1);
    }
    
    /**
     * JsonObject로 변환
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        fields.add("strength", JsonUtil.createIntegerValue(strength));
        fields.add("intelligence", JsonUtil.createIntegerValue(intelligence));
        fields.add("dexterity", JsonUtil.createIntegerValue(dexterity));
        fields.add("vitality", JsonUtil.createIntegerValue(vitality));
        fields.add("wisdom", JsonUtil.createIntegerValue(wisdom));
        fields.add("luck", JsonUtil.createIntegerValue(luck));
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 StatsDTO 생성
     */
    @NotNull
    public static StatsDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return new StatsDTO(); // 기본값 반환
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        int strength = (int) JsonUtil.getLongValue(fields, "strength", 10L);
        int intelligence = (int) JsonUtil.getLongValue(fields, "intelligence", 10L);
        int dexterity = (int) JsonUtil.getLongValue(fields, "dexterity", 10L);
        int vitality = (int) JsonUtil.getLongValue(fields, "vitality", 10L);
        int wisdom = (int) JsonUtil.getLongValue(fields, "wisdom", 10L);
        int luck = (int) JsonUtil.getLongValue(fields, "luck", 1L);
        
        return new StatsDTO(strength, intelligence, dexterity, vitality, wisdom, luck);
    }
}