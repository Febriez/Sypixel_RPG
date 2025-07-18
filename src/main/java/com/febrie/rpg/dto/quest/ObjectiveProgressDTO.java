package com.febrie.rpg.dto.quest;

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
        
        JsonObject objectiveIdValue = new JsonObject();
        objectiveIdValue.addProperty("stringValue", objectiveId);
        fields.add("objectiveId", objectiveIdValue);
        
        JsonObject completedValue = new JsonObject();
        completedValue.addProperty("booleanValue", completed);
        fields.add("completed", completedValue);
        
        JsonObject progressValue = new JsonObject();
        progressValue.addProperty("integerValue", progress);
        fields.add("progress", progressValue);
        
        JsonObject targetValue = new JsonObject();
        targetValue.addProperty("integerValue", target);
        fields.add("target", targetValue);
        
        JsonObject lastUpdatedValue = new JsonObject();
        lastUpdatedValue.addProperty("integerValue", lastUpdated);
        fields.add("lastUpdated", lastUpdatedValue);
        
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
        
        String objectiveId = fields.has("objectiveId") && fields.getAsJsonObject("objectiveId").has("stringValue")
                ? fields.getAsJsonObject("objectiveId").get("stringValue").getAsString()
                : "";
                
        boolean completed = fields.has("completed") && fields.getAsJsonObject("completed").has("booleanValue")
                ? fields.getAsJsonObject("completed").get("booleanValue").getAsBoolean()
                : false;
                
        int progress = fields.has("progress") && fields.getAsJsonObject("progress").has("integerValue")
                ? fields.getAsJsonObject("progress").get("integerValue").getAsInt()
                : 0;
                
        int target = fields.has("target") && fields.getAsJsonObject("target").has("integerValue")
                ? fields.getAsJsonObject("target").get("integerValue").getAsInt()
                : 0;
                
        long lastUpdated = fields.has("lastUpdated") && fields.getAsJsonObject("lastUpdated").has("integerValue")
                ? fields.getAsJsonObject("lastUpdated").get("integerValue").getAsLong()
                : System.currentTimeMillis();
        
        return new ObjectiveProgressDTO(objectiveId, completed, progress, target, lastUpdated);
    }
}