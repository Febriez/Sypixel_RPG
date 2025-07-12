package com.febrie.rpg.dto;

/**
 * 순위표 항목 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record LeaderboardEntryDTO(
        String playerUuid,
        String playerName,
        int rank,
        long value,
        String type,
        long lastUpdated
) {
    /**
     * 간편 생성자
     */
    public LeaderboardEntryDTO(String playerUuid, String playerName, int rank, long value, String type) {
        this(playerUuid, playerName, rank, value, type, System.currentTimeMillis());
    }
}