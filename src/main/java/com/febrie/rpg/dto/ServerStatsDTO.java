package com.febrie.rpg.dto;

/**
 * 서버 통계 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record ServerStatsDTO(
        int onlinePlayers,
        int maxPlayers,
        int totalPlayers,
        long uptime,
        double tps,
        long totalPlaytime,
        String version,
        long lastUpdated
) {
    /**
     * 기본 생성자
     */
    public ServerStatsDTO() {
        this(0, 0, 0, 0L, 20.0, 0L, "1.21.7", System.currentTimeMillis());
    }

    /**
     * 업데이트 시간 제외 생성자
     */
    public ServerStatsDTO(int onlinePlayers, int maxPlayers, int totalPlayers,
                          long uptime, double tps, long totalPlaytime, String version) {
        this(onlinePlayers, maxPlayers, totalPlayers, uptime, tps, totalPlaytime, version, System.currentTimeMillis());
    }
}