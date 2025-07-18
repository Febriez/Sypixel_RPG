package com.febrie.rpg.dto.player;

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
        
        JsonObject strengthValue = new JsonObject();
        strengthValue.addProperty("integerValue", strength);
        fields.add("strength", strengthValue);
        
        JsonObject intelligenceValue = new JsonObject();
        intelligenceValue.addProperty("integerValue", intelligence);
        fields.add("intelligence", intelligenceValue);
        
        JsonObject dexterityValue = new JsonObject();
        dexterityValue.addProperty("integerValue", dexterity);
        fields.add("dexterity", dexterityValue);
        
        JsonObject vitalityValue = new JsonObject();
        vitalityValue.addProperty("integerValue", vitality);
        fields.add("vitality", vitalityValue);
        
        JsonObject wisdomValue = new JsonObject();
        wisdomValue.addProperty("integerValue", wisdom);
        fields.add("wisdom", wisdomValue);
        
        JsonObject luckValue = new JsonObject();
        luckValue.addProperty("integerValue", luck);
        fields.add("luck", luckValue);
        
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
        
        int strength = fields.has("strength") && fields.getAsJsonObject("strength").has("integerValue")
                ? fields.getAsJsonObject("strength").get("integerValue").getAsInt()
                : 10;
                
        int intelligence = fields.has("intelligence") && fields.getAsJsonObject("intelligence").has("integerValue")
                ? fields.getAsJsonObject("intelligence").get("integerValue").getAsInt()
                : 10;
                
        int dexterity = fields.has("dexterity") && fields.getAsJsonObject("dexterity").has("integerValue")
                ? fields.getAsJsonObject("dexterity").get("integerValue").getAsInt()
                : 10;
                
        int vitality = fields.has("vitality") && fields.getAsJsonObject("vitality").has("integerValue")
                ? fields.getAsJsonObject("vitality").get("integerValue").getAsInt()
                : 10;
                
        int wisdom = fields.has("wisdom") && fields.getAsJsonObject("wisdom").has("integerValue")
                ? fields.getAsJsonObject("wisdom").get("integerValue").getAsInt()
                : 10;
                
        int luck = fields.has("luck") && fields.getAsJsonObject("luck").has("integerValue")
                ? fields.getAsJsonObject("luck").get("integerValue").getAsInt()
                : 1;
        
        return new StatsDTO(strength, intelligence, dexterity, vitality, wisdom, luck);
    }
}