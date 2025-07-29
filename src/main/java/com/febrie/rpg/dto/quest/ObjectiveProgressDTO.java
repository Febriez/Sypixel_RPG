package com.febrie.rpg.dto.quest;

import com.febrie.rpg.util.JsonUtil;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * 목표 진행도 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author CoffeeTory
 */
public record ObjectiveProgressDTO(
        String objectiveId,
        boolean completed,
        int progress,
        int target,
        long lastUpdated
) {
    /**
     * 기본 생성자 - 신규 목표용
     */
    public ObjectiveProgressDTO(String objectiveId, int target) {
        this(objectiveId, false, 0, target, System.currentTimeMillis());
    }
    
    /**
     * JsonObject로 변환
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        fields.add("objectiveId", JsonUtil.createStringValue(objectiveId));
        fields.add("completed", JsonUtil.createBooleanValue(completed));
        fields.add("progress", JsonUtil.createIntegerValue(progress));
        fields.add("target", JsonUtil.createIntegerValue(target));
        fields.add("lastUpdated", JsonUtil.createIntegerValue(lastUpdated));
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 ObjectiveProgressDTO 생성
     */
    @NotNull
    public static ObjectiveProgressDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return new ObjectiveProgressDTO("", 0);
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        String objectiveId = JsonUtil.getStringValue(fields, "objectiveId", "");
        boolean completed = JsonUtil.getBooleanValue(fields, "completed", false);
        int progress = (int) JsonUtil.getLongValue(fields, "progress", 0L);
        int target = (int) JsonUtil.getLongValue(fields, "target", 0L);
        long lastUpdated = JsonUtil.getLongValue(fields, "lastUpdated", System.currentTimeMillis());
        
        return new ObjectiveProgressDTO(objectiveId, completed, progress, target, lastUpdated);
    }
}