package com.febrie.rpg.dto;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Firestore server_stats 컬렉션의 문서 DTO (순수 POJO)
 * 서버 통계 및 상태 정보를 저장
 *
 * @author Febrie, CoffeeTory
 */
public class ServerStatsDTO {

    private String serverName;
    private String serverVersion;
    private String pluginVersion;

    private int totalPlayers;
    private int onlinePlayers;
    private int maxPlayers;

    private long startTime;
    private long lastUpdated;

    // 추가 데이터 (메모리, TPS 등)
    private Map<String, Object> additionalData = new HashMap<>();

    // 기본 생성자 (Firestore 필수)
    public ServerStatsDTO() {
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * 데이터 업데이트 시 타임스탬프 갱신
     */
    public void markUpdated() {
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * 추가 데이터 설정
     */
    public void addData(@NotNull String key, @NotNull Object value) {
        additionalData.put(key, value);
        markUpdated();
    }

    /**
     * 추가 데이터 가져오기
     */
    public Object getData(@NotNull String key) {
        return additionalData.get(key);
    }

    /**
     * 업타임 계산 (밀리초)
     */
    public long getUptime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 업타임 포맷 (일:시:분:초)
     */
    @NotNull
    public String getFormattedUptime() {
        long uptime = getUptime();
        long days = uptime / (24 * 60 * 60 * 1000);
        long hours = (uptime % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (uptime % (60 * 60 * 1000)) / (60 * 1000);
        long seconds = (uptime % (60 * 1000)) / 1000;

        return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
    }

    // Getters and Setters
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
        markUpdated();
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
        markUpdated();
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
        markUpdated();
    }

    /**
     * Map으로 변환 (Firestore 저장용)
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("serverName", serverName);
        map.put("serverVersion", serverVersion);
        map.put("pluginVersion", pluginVersion);
        map.put("totalPlayers", totalPlayers);
        map.put("onlinePlayers", onlinePlayers);
        map.put("maxPlayers", maxPlayers);
        map.put("startTime", startTime);
        map.put("lastUpdated", lastUpdated);
        map.put("additionalData", additionalData);
        map.put("formattedUptime", getFormattedUptime());
        return map;
    }

    /**
     * Map에서 생성
     */
    @SuppressWarnings("unchecked")
    public static ServerStatsDTO fromMap(Map<String, Object> map) {
        ServerStatsDTO dto = new ServerStatsDTO();

        dto.setServerName(DTOUtil.toString(map.get("serverName")));
        dto.setServerVersion(DTOUtil.toString(map.get("serverVersion")));
        dto.setPluginVersion(DTOUtil.toString(map.get("pluginVersion")));

        DTOUtil.setIntFromMap(map, "totalPlayers", dto::setTotalPlayers);
        DTOUtil.setIntFromMap(map, "onlinePlayers", dto::setOnlinePlayers);
        DTOUtil.setIntFromMap(map, "maxPlayers", dto::setMaxPlayers);

        DTOUtil.setLongFromMap(map, "startTime", dto::setStartTime);
        DTOUtil.setLongFromMap(map, "lastUpdated", dto::setLastUpdated);

        Object additionalData = map.get("additionalData");
        if (additionalData instanceof Map) {
            dto.setAdditionalData((Map<String, Object>) additionalData);
        }

        return dto;
    }

    /**
     * 통계 요약 생성
     */
    @NotNull
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("serverName", serverName);
        summary.put("onlinePlayers", onlinePlayers + "/" + maxPlayers);
        summary.put("uptime", getFormattedUptime());

        // 메모리 정보
        Long usedMemory = DTOUtil.toLong(additionalData.get("usedMemoryMB"));
        Long maxMemory = DTOUtil.toLong(additionalData.get("maxMemoryMB"));
        if (usedMemory != null && maxMemory != null) {
            summary.put("memory", usedMemory + "/" + maxMemory + " MB");
        }

        // TPS 정보
        Double tps = DTOUtil.toDouble(additionalData.get("tps1m"));
        if (tps != null) {
            summary.put("tps", String.format("%.2f", tps));
        }

        return summary;
    }
}