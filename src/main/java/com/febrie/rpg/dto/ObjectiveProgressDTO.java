package com.febrie.rpg.dto;

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
}