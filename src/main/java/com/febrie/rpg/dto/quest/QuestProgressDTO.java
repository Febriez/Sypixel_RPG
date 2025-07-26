package com.febrie.rpg.dto.quest;

import com.febrie.rpg.util.JsonUtil;
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
        JsonObject fields = new JsonObject();
        
        fields.add("questId", JsonUtil.createStringValue(questId));
        fields.add("playerId", JsonUtil.createStringValue(playerId));
        fields.add("state", JsonUtil.createStringValue(state));
        fields.add("currentObjectiveIndex", JsonUtil.createIntegerValue(currentObjectiveIndex));
        fields.add("startedAt", JsonUtil.createIntegerValue(startedAt));
        fields.add("lastUpdatedAt", JsonUtil.createIntegerValue(lastUpdatedAt));
        fields.add("completedAt", JsonUtil.createIntegerValue(completedAt));
        
        // objectives 맵
        fields.add("objectives", JsonUtil.createMapField(objectives,
                value -> value.toJsonObject()));
        
        return JsonUtil.wrapInDocument(fields);
    }
    
    /**
     * JsonObject에서 QuestProgressDTO 생성
     */
    @NotNull
    public static QuestProgressDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return new QuestProgressDTO("", "");
        }
        
        JsonObject fields = JsonUtil.unwrapDocument(json);
        
        String questId = JsonUtil.getStringValue(fields, "questId", "");
        String playerId = JsonUtil.getStringValue(fields, "playerId", "");
        String state = JsonUtil.getStringValue(fields, "state", "ACTIVE");
        int currentObjectiveIndex = JsonUtil.getIntegerValue(fields, "currentObjectiveIndex", 0);
        long startedAt = JsonUtil.getLongValue(fields, "startedAt", System.currentTimeMillis());
        long lastUpdatedAt = JsonUtil.getLongValue(fields, "lastUpdatedAt", System.currentTimeMillis());
        long completedAt = JsonUtil.getLongValue(fields, "completedAt", 0L);
        
        // objectives 맵
        Map<String, ObjectiveProgressDTO> objectives = JsonUtil.getMapField(fields, "objectives",
                key -> key,
                obj -> {
                    JsonObject nestedDoc = new JsonObject();
                    nestedDoc.add("fields", obj.get("fields"));
                    return ObjectiveProgressDTO.fromJsonObject(nestedDoc);
                });
        
        return new QuestProgressDTO(questId, playerId, state, currentObjectiveIndex, 
                                   startedAt, lastUpdatedAt, completedAt, objectives);
    }
}