package com.febrie.rpg.dto.quest;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 퀘스트 진행도 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author CoffeeTory
 */
public record QuestProgressDTO(String questId, String playerId, String state, int currentObjectiveIndex, long startedAt,
                               long lastUpdatedAt, long completedAt, Map<String, ObjectiveProgressDTO> objectives) {
    /**
     * 기본 생성자 - 신규 퀘스트용
     */
    public QuestProgressDTO(String questId, String playerId) {
        this(questId, playerId, "ACTIVE", 0, System.currentTimeMillis(), System.currentTimeMillis(), 0L, new HashMap<>());
    }

    /**
     * 방어적 복사를 위한 생성자
     */
    public QuestProgressDTO(String questId, String playerId, String state, int currentObjectiveIndex, long startedAt, long lastUpdatedAt, long completedAt, Map<String, ObjectiveProgressDTO> objectives) {
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
     * Map으로 변환
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("questId", questId);
        map.put("playerId", playerId);
        map.put("state", state);
        map.put("currentObjectiveIndex", currentObjectiveIndex);
        map.put("startedAt", startedAt);
        map.put("lastUpdatedAt", lastUpdatedAt);
        map.put("completedAt", completedAt);

        // objectives 맵
        Map<String, Map<String, Object>> objectivesMap = objectives.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().toMap()
                ));
        map.put("objectives", objectivesMap);

        return map;
    }

    /**
     * Map에서 QuestProgressDTO 생성
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static QuestProgressDTO fromMap(@NotNull Map<String, Object> map) {
        String questId = (String) map.getOrDefault("questId", "");
        String playerId = (String) map.getOrDefault("playerId", "");
        String state = (String) map.getOrDefault("state", "ACTIVE");
        
        int currentObjectiveIndex = FirestoreUtils.getInt(map, "currentObjectiveIndex", 0);
        long startedAt = FirestoreUtils.getLong(map, "startedAt", System.currentTimeMillis());
        long lastUpdatedAt = FirestoreUtils.getLong(map, "lastUpdatedAt", System.currentTimeMillis());
        long completedAt = FirestoreUtils.getLong(map, "completedAt", 0L);

        // objectives 맵
        Map<String, ObjectiveProgressDTO> objectives = new HashMap<>();
        Object objectivesObj = map.get("objectives");
        if (objectivesObj instanceof Map) {
            Map<String, Object> objectivesMap = (Map<String, Object>) objectivesObj;
            for (Map.Entry<String, Object> entry : objectivesMap.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    objectives.put(entry.getKey(), ObjectiveProgressDTO.fromMap((Map<String, Object>) entry.getValue()));
                }
            }
        }

        return new QuestProgressDTO(questId, playerId, state, currentObjectiveIndex, startedAt, lastUpdatedAt, completedAt, objectives);
    }
}