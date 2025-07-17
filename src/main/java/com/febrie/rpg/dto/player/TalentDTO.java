package com.febrie.rpg.dto.player;

import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어 특성 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record TalentDTO(
        int availablePoints,
        Map<String, Integer> learnedTalents
) {
    /**
     * 기본 생성자 - 신규 플레이어용
     */
    public TalentDTO() {
        this(0, new HashMap<>());
    }

    /**
     * 방어적 복사를 위한 생성자
     */
    public TalentDTO(int availablePoints, Map<String, Integer> learnedTalents) {
        this.availablePoints = availablePoints;
        this.learnedTalents = new HashMap<>(learnedTalents);
    }

    /**
     * 학습한 특성 맵의 불변 뷰 반환
     */
    @Override
    public Map<String, Integer> learnedTalents() {
        return new HashMap<>(learnedTalents);
    }
}