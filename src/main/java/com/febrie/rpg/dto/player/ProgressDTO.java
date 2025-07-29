package com.febrie.rpg.dto.player;

import com.febrie.rpg.util.JsonUtil;
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
        
        fields.add("currentLevel", JsonUtil.createIntegerValue(currentLevel));
        fields.add("totalExperience", JsonUtil.createIntegerValue(totalExperience));
        fields.add("levelProgress", JsonUtil.createDoubleValue(levelProgress));
        fields.add("mobsKilled", JsonUtil.createIntegerValue(mobsKilled));
        fields.add("playersKilled", JsonUtil.createIntegerValue(playersKilled));
        fields.add("deaths", JsonUtil.createIntegerValue(deaths));
        
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
        
        int currentLevel = (int) JsonUtil.getLongValue(fields, "currentLevel", 1L);
        long totalExperience = JsonUtil.getLongValue(fields, "totalExperience", 0L);
        double levelProgress = JsonUtil.getDoubleValue(fields, "levelProgress", 0.0);
        int mobsKilled = (int) JsonUtil.getLongValue(fields, "mobsKilled", 0L);
        int playersKilled = (int) JsonUtil.getLongValue(fields, "playersKilled", 0L);
        int deaths = (int) JsonUtil.getLongValue(fields, "deaths", 0L);
        
        return new ProgressDTO(currentLevel, totalExperience, levelProgress, mobsKilled, playersKilled, deaths);
    }
}