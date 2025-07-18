package com.febrie.rpg.dto.quest;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 퀘스트 진행도 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author CoffeeTory
 */
public record QuestProgressDTO(
        String questId,
        String playerId,
        String state,
        int currentObjectiveIndex,
        long startedAt,
        long lastUpdatedAt,
        long completedAt,
        Map<String, ObjectiveProgressDTO> objectives
) {
    /**
     * 기본 생성자 - 신규 퀘스트용
     */
    public QuestProgressDTO(String questId, String playerId) {
        this(questId, playerId, "ACTIVE", 0, 
             System.currentTimeMillis(), System.currentTimeMillis(), 0L, 
             new HashMap<>());
    }

    /**
     * 방어적 복사를 위한 생성자
     */
    public QuestProgressDTO(String questId, String playerId, String state,
                           int currentObjectiveIndex, long startedAt, long lastUpdatedAt,
                           long completedAt, Map<String, ObjectiveProgressDTO> objectives) {
        this.questId = questId;
        this.playerId = playerId;
        this.state = state;
        this.currentObjectiveIndex = currentObjectiveIndex;
        this.startedAt = startedAt;
        this.lastUpdatedAt = lastUpdatedAt;
        this.completedAt = completedAt;
        this.objectives = new HashMap<>(objectives);
    }

    /**
     * 목표 진행도 맵의 불변 뷰 반환
     */
    @Override
    public Map<String, ObjectiveProgressDTO> objectives() {
        return new HashMap<>(objectives);
    }
    
    /**
     * JsonObject로 변환
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        JsonObject questIdValue = new JsonObject();
        questIdValue.addProperty("stringValue", questId);
        fields.add("questId", questIdValue);
        
        JsonObject playerIdValue = new JsonObject();
        playerIdValue.addProperty("stringValue", playerId);
        fields.add("playerId", playerIdValue);
        
        JsonObject stateValue = new JsonObject();
        stateValue.addProperty("stringValue", state);
        fields.add("state", stateValue);
        
        JsonObject currentObjectiveIndexValue = new JsonObject();
        currentObjectiveIndexValue.addProperty("integerValue", currentObjectiveIndex);
        fields.add("currentObjectiveIndex", currentObjectiveIndexValue);
        
        JsonObject startedAtValue = new JsonObject();
        startedAtValue.addProperty("integerValue", startedAt);
        fields.add("startedAt", startedAtValue);
        
        JsonObject lastUpdatedAtValue = new JsonObject();
        lastUpdatedAtValue.addProperty("integerValue", lastUpdatedAt);
        fields.add("lastUpdatedAt", lastUpdatedAtValue);
        
        JsonObject completedAtValue = new JsonObject();
        completedAtValue.addProperty("integerValue", completedAt);
        fields.add("completedAt", completedAtValue);
        
        // objectives 맵
        JsonObject objectivesValue = new JsonObject();
        JsonObject mapValue = new JsonObject();
        JsonObject objectivesFields = new JsonObject();
        objectives.forEach((key, value) -> {
            JsonObject nestedMapValue = new JsonObject();
            nestedMapValue.add("fields", value.toJsonObject().getAsJsonObject("fields"));
            objectivesFields.add(key, nestedMapValue);
        });
        mapValue.add("fields", objectivesFields);
        objectivesValue.add("mapValue", mapValue);
        fields.add("objectives", objectivesValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 QuestProgressDTO 생성
     */
    @NotNull
    public static QuestProgressDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return new QuestProgressDTO("", "");
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        String questId = fields.has("questId") && fields.getAsJsonObject("questId").has("stringValue")
                ? fields.getAsJsonObject("questId").get("stringValue").getAsString()
                : "";
                
        String playerId = fields.has("playerId") && fields.getAsJsonObject("playerId").has("stringValue")
                ? fields.getAsJsonObject("playerId").get("stringValue").getAsString()
                : "";
                
        String state = fields.has("state") && fields.getAsJsonObject("state").has("stringValue")
                ? fields.getAsJsonObject("state").get("stringValue").getAsString()
                : "ACTIVE";
                
        int currentObjectiveIndex = fields.has("currentObjectiveIndex") && fields.getAsJsonObject("currentObjectiveIndex").has("integerValue")
                ? fields.getAsJsonObject("currentObjectiveIndex").get("integerValue").getAsInt()
                : 0;
                
        long startedAt = fields.has("startedAt") && fields.getAsJsonObject("startedAt").has("integerValue")
                ? fields.getAsJsonObject("startedAt").get("integerValue").getAsLong()
                : System.currentTimeMillis();
                
        long lastUpdatedAt = fields.has("lastUpdatedAt") && fields.getAsJsonObject("lastUpdatedAt").has("integerValue")
                ? fields.getAsJsonObject("lastUpdatedAt").get("integerValue").getAsLong()
                : System.currentTimeMillis();
                
        long completedAt = fields.has("completedAt") && fields.getAsJsonObject("completedAt").has("integerValue")
                ? fields.getAsJsonObject("completedAt").get("integerValue").getAsLong()
                : 0L;
                
        Map<String, ObjectiveProgressDTO> objectives = new HashMap<>();
        if (fields.has("objectives") && fields.getAsJsonObject("objectives").has("mapValue")) {
            JsonObject mapValue = fields.getAsJsonObject("objectives").getAsJsonObject("mapValue");
            if (mapValue.has("fields")) {
                JsonObject objectivesFields = mapValue.getAsJsonObject("fields");
                objectivesFields.entrySet().forEach(entry -> {
                    if (entry.getValue().isJsonObject()) {
                        JsonObject nestedDoc = new JsonObject();
                        nestedDoc.add("fields", entry.getValue().getAsJsonObject().get("fields"));
                        objectives.put(entry.getKey(), ObjectiveProgressDTO.fromJsonObject(nestedDoc));
                    }
                });
            }
        }
        
        return new QuestProgressDTO(questId, playerId, state, currentObjectiveIndex, 
                                   startedAt, lastUpdatedAt, completedAt, objectives);
    }
}