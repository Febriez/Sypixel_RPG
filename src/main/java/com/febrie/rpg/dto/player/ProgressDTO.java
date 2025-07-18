package com.febrie.rpg.dto.player;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * 플레이어 진행도 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record ProgressDTO(
        int currentLevel,
        long totalExperience,
        double levelProgress,
        int mobsKilled,
        int playersKilled,
        int deaths
) {
    /**
     * 기본 생성자 - 신규 플레이어용
     */
    public ProgressDTO() {
        this(1, 0L, 0.0, 0, 0, 0);
    }
    
    /**
     * JsonObject로 변환
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        JsonObject currentLevelValue = new JsonObject();
        currentLevelValue.addProperty("integerValue", currentLevel);
        fields.add("currentLevel", currentLevelValue);
        
        JsonObject totalExperienceValue = new JsonObject();
        totalExperienceValue.addProperty("integerValue", totalExperience);
        fields.add("totalExperience", totalExperienceValue);
        
        JsonObject levelProgressValue = new JsonObject();
        levelProgressValue.addProperty("doubleValue", levelProgress);
        fields.add("levelProgress", levelProgressValue);
        
        JsonObject mobsKilledValue = new JsonObject();
        mobsKilledValue.addProperty("integerValue", mobsKilled);
        fields.add("mobsKilled", mobsKilledValue);
        
        JsonObject playersKilledValue = new JsonObject();
        playersKilledValue.addProperty("integerValue", playersKilled);
        fields.add("playersKilled", playersKilledValue);
        
        JsonObject deathsValue = new JsonObject();
        deathsValue.addProperty("integerValue", deaths);
        fields.add("deaths", deathsValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 ProgressDTO 생성
     */
    @NotNull
    public static ProgressDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return new ProgressDTO();
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        int currentLevel = fields.has("currentLevel") && fields.getAsJsonObject("currentLevel").has("integerValue")
                ? fields.getAsJsonObject("currentLevel").get("integerValue").getAsInt()
                : 1;
                
        long totalExperience = fields.has("totalExperience") && fields.getAsJsonObject("totalExperience").has("integerValue")
                ? fields.getAsJsonObject("totalExperience").get("integerValue").getAsLong()
                : 0L;
                
        double levelProgress = fields.has("levelProgress") && fields.getAsJsonObject("levelProgress").has("doubleValue")
                ? fields.getAsJsonObject("levelProgress").get("doubleValue").getAsDouble()
                : 0.0;
                
        int mobsKilled = fields.has("mobsKilled") && fields.getAsJsonObject("mobsKilled").has("integerValue")
                ? fields.getAsJsonObject("mobsKilled").get("integerValue").getAsInt()
                : 0;
                
        int playersKilled = fields.has("playersKilled") && fields.getAsJsonObject("playersKilled").has("integerValue")
                ? fields.getAsJsonObject("playersKilled").get("integerValue").getAsInt()
                : 0;
                
        int deaths = fields.has("deaths") && fields.getAsJsonObject("deaths").has("integerValue")
                ? fields.getAsJsonObject("deaths").get("integerValue").getAsInt()
                : 0;
        
        return new ProgressDTO(currentLevel, totalExperience, levelProgress, mobsKilled, playersKilled, deaths);
    }
}