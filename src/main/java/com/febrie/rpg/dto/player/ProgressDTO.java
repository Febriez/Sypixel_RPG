package com.febrie.rpg.dto.player;

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
}