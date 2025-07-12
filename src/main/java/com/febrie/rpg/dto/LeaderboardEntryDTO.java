package com.febrie.rpg.dto;

import org.jetbrains.annotations.NotNull;

/**
 * 순위표 항목 DTO
 *
 * @author Febrie, CoffeeTory
 */
public class LeaderboardEntryDTO {

    private String playerUuid;
    private String playerName;
    private int rank;
    private long value; // 순위 기준 값 (레벨, 코인, 킬 수 등)
    private String type; // 순위표 타입
    private long lastUpdated = System.currentTimeMillis();

    public LeaderboardEntryDTO() {
        // 기본 생성자
    }

    public LeaderboardEntryDTO(@NotNull String playerUuid, @NotNull String playerName,
                               int rank, long value, @NotNull String type) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.rank = rank;
        this.value = value;
        this.type = type;
    }

    // Getters and Setters
    @NotNull
    public String getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(@NotNull String playerUuid) {
        this.playerUuid = playerUuid;
    }

    @NotNull
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(@NotNull String playerName) {
        this.playerName = playerName;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = Math.max(1, rank);
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @NotNull
    public String getType() {
        return type;
    }

    public void setType(@NotNull String type) {
        this.type = type;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * 순위에 따른 메달 이모티콘
     */
    @NotNull
    public String getRankMedal() {
        return switch (rank) {
            case 1 -> "🥇";
            case 2 -> "🥈";
            case 3 -> "🥉";
            default -> "🏅";
        };
    }
}