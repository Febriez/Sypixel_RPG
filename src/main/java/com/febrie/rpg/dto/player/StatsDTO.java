package com.febrie.rpg.dto.player;

/**
 * 플레이어 스탯 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record StatsDTO(
        int strength,
        int intelligence,
        int dexterity,
        int vitality,
        int wisdom,
        int luck
) {
    /**
     * 기본 생성자 - 초기 스탯
     */
    public StatsDTO() {
        this(10, 10, 10, 10, 10, 1);
    }
}