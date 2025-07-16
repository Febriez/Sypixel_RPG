package com.febrie.rpg.dto;

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
}