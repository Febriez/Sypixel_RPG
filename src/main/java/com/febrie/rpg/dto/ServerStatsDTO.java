package com.febrie.rpg.dto;

/**
 * 서버 통계 정보 DTO
 *
 * @author Febrie, CoffeeTory
 */
public class ServerStatsDTO {

    private int onlinePlayers = 0;
    private int maxPlayers = 0;
    private int totalPlayers = 0;
    private long uptime = 0; // 밀리초
    private double tps = 20.0;
    private long totalPlaytime = 0; // 모든 플레이어의 총 플레이시간
    private String version = "1.21.7";

    public ServerStatsDTO() {
        // 기본 생성자
    }

    // Getters and Setters
    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = Math.max(0, onlinePlayers);
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = Math.max(0, maxPlayers);
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = Math.max(0, totalPlayers);
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = Math.max(0, uptime);
    }

    public double getTps() {
        return tps;
    }

    public void setTps(double tps) {
        this.tps = Math.max(0.0, Math.min(20.0, tps));
    }

    public long getTotalPlaytime() {
        return totalPlaytime;
    }

    public void setTotalPlaytime(long totalPlaytime) {
        this.totalPlaytime = Math.max(0, totalPlaytime);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 가동률 계산 (시간 단위)
     */
    public double getUptimeHours() {
        return uptime / 3600000.0;
    }

    /**
     * 서버 사용률
     */
    public double getServerLoad() {
        if (maxPlayers == 0) return 0.0;
        return (double) onlinePlayers / maxPlayers * 100.0;
    }
}