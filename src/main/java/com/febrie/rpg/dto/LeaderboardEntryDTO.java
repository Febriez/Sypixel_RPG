package com.febrie.rpg.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Firestore leaderboards/{type}/{uuid} 문서 DTO (순수 POJO)
 * 순위표 엔트리 정보를 저장
 *
 * @author Febrie, CoffeeTory
 */
public class LeaderboardEntryDTO {

    private String uuid;
    private String playerName;
    private String jobType;
    private long score; // 정렬 기준값 (레벨, 전투력, 플레이시간 등)
    private int rank; // 현재 순위

    // 추가 정보 (순위표 타입에 따라 다름)
    private Map<String, Object> additionalData = new HashMap<>();

    private long lastUpdated;

    // 기본 생성자 (Firestore 필수)
    public LeaderboardEntryDTO() {
        this.lastUpdated = System.currentTimeMillis();
    }

    // 생성자
    public LeaderboardEntryDTO(String uuid, String playerName, long score) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.score = score;
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * 데이터 업데이트 시 타임스탬프도 업데이트
     */
    public void markUpdated() {
        this.lastUpdated = System.currentTimeMillis();
    }

    // 순위표 타입별 팩토리 메소드들
    public static LeaderboardEntryDTO createLevelEntry(String uuid, String playerName,
                                                       int level, long experience, String jobType) {
        LeaderboardEntryDTO entry = new LeaderboardEntryDTO(uuid, playerName, level);
        entry.setJobType(jobType);
        entry.addData("experience", experience);
        return entry;
    }

    public static LeaderboardEntryDTO createCombatPowerEntry(String uuid, String playerName,
                                                             int combatPower, int level, String jobType) {
        LeaderboardEntryDTO entry = new LeaderboardEntryDTO(uuid, playerName, combatPower);
        entry.setJobType(jobType);
        entry.addData("level", level);
        return entry;
    }

    public static LeaderboardEntryDTO createPlaytimeEntry(String uuid, String playerName,
                                                          long playtimeMillis, int level) {
        LeaderboardEntryDTO entry = new LeaderboardEntryDTO(uuid, playerName, playtimeMillis);
        entry.addData("level", level);
        entry.addData("formattedTime", formatPlaytime(playtimeMillis));
        return entry;
    }

    // 유틸리티 메소드
    public void addData(String key, Object value) {
        additionalData.put(key, value);
        markUpdated();
    }

    public Object getData(String key) {
        return additionalData.get(key);
    }

    private static String formatPlaytime(long millis) {
        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        return String.format("%dh %dm", hours, minutes);
    }

    // Getters and Setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
        markUpdated();
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
        markUpdated();
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * Map으로 변환 (Firestore 저장용)
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", uuid);
        map.put("playerName", playerName);
        map.put("jobType", jobType);
        map.put("score", score);
        map.put("rank", rank);
        map.put("additionalData", additionalData);
        map.put("lastUpdated", lastUpdated);
        return map;
    }

    /**
     * Map에서 생성
     */
    @SuppressWarnings("unchecked")
    public static LeaderboardEntryDTO fromMap(Map<String, Object> map) {
        LeaderboardEntryDTO dto = new LeaderboardEntryDTO();

        dto.setUuid(DTOUtil.toString(map.get("uuid")));
        dto.setPlayerName(DTOUtil.toString(map.get("playerName")));
        dto.setJobType(DTOUtil.toString(map.get("jobType")));

        DTOUtil.setLongFromMap(map, "score", dto::setScore);
        DTOUtil.setIntFromMap(map, "rank", dto::setRank);
        DTOUtil.setLongFromMap(map, "lastUpdated", dto::setLastUpdated);

        Object additionalData = map.get("additionalData");
        if (additionalData instanceof Map) {
            dto.setAdditionalData((Map<String, Object>) additionalData);
        }

        return dto;
    }
}