package com.febrie.rpg.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Firestore players/{uuid}/progress 문서 DTO (순수 POJO)
 * 플레이어의 진행도 정보를 저장 (경험치, 레벨, 업적 등)
 *
 * @author Febrie, CoffeeTory
 */
public class ProgressDTO {

    private long totalExperience = 0;
    private int currentLevel = 1;
    private double levelProgress = 0.0; // 0.0 ~ 1.0

    // 전투 통계
    private long totalKills = 0;
    private long totalDeaths = 0;
    private long totalDamageDealt = 0;
    private long totalDamageTaken = 0;

    // 경제 통계
    private long totalCoinsEarned = 0;
    private long totalCoinsSpent = 0;
    private long currentCoins = 0;

    // 던전 통계
    private Map<String, Integer> dungeonClears = new HashMap<>(); // dungeonId -> clear count
    private Map<String, Long> dungeonBestTimes = new HashMap<>(); // dungeonId -> best time (ms)

    private long lastUpdated;

    // 기본 생성자 (Firestore 필수)
    public ProgressDTO() {
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * 데이터 업데이트 시 타임스탬프도 업데이트
     */
    public void markUpdated() {
        this.lastUpdated = System.currentTimeMillis();
    }

    // 경험치 관련 메소드
    public void addExperience(long exp) {
        this.totalExperience += exp;
        markUpdated();
    }

    // 전투 통계 메소드
    public void addKill() {
        this.totalKills++;
        markUpdated();
    }

    public void addDeath() {
        this.totalDeaths++;
        markUpdated();
    }

    public double getKDRatio() {
        if (totalDeaths == 0) return totalKills;
        return (double) totalKills / totalDeaths;
    }

    // 던전 통계 메소드
    public void addDungeonClear(String dungeonId) {
        dungeonClears.merge(dungeonId, 1, Integer::sum);
        markUpdated();
    }

    public void updateDungeonBestTime(String dungeonId, long timeMs) {
        Long currentBest = dungeonBestTimes.get(dungeonId);
        if (currentBest == null || timeMs < currentBest) {
            dungeonBestTimes.put(dungeonId, timeMs);
            markUpdated();
        }
    }

    // Getters and Setters
    public long getTotalExperience() {
        return totalExperience;
    }

    public void setTotalExperience(long totalExperience) {
        this.totalExperience = totalExperience;
        markUpdated();
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
        markUpdated();
    }

    public double getLevelProgress() {
        return levelProgress;
    }

    public void setLevelProgress(double levelProgress) {
        this.levelProgress = levelProgress;
        markUpdated();
    }

    public long getTotalKills() {
        return totalKills;
    }

    public void setTotalKills(long totalKills) {
        this.totalKills = totalKills;
        markUpdated();
    }

    public long getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(long totalDeaths) {
        this.totalDeaths = totalDeaths;
        markUpdated();
    }

    public long getTotalDamageDealt() {
        return totalDamageDealt;
    }

    public void setTotalDamageDealt(long totalDamageDealt) {
        this.totalDamageDealt = totalDamageDealt;
        markUpdated();
    }

    public long getTotalDamageTaken() {
        return totalDamageTaken;
    }

    public void setTotalDamageTaken(long totalDamageTaken) {
        this.totalDamageTaken = totalDamageTaken;
        markUpdated();
    }

    public long getTotalCoinsEarned() {
        return totalCoinsEarned;
    }

    public void setTotalCoinsEarned(long totalCoinsEarned) {
        this.totalCoinsEarned = totalCoinsEarned;
        markUpdated();
    }

    public long getTotalCoinsSpent() {
        return totalCoinsSpent;
    }

    public void setTotalCoinsSpent(long totalCoinsSpent) {
        this.totalCoinsSpent = totalCoinsSpent;
        markUpdated();
    }

    public long getCurrentCoins() {
        return currentCoins;
    }

    public void setCurrentCoins(long currentCoins) {
        this.currentCoins = currentCoins;
        markUpdated();
    }

    public Map<String, Integer> getDungeonClears() {
        return dungeonClears;
    }

    public void setDungeonClears(Map<String, Integer> dungeonClears) {
        this.dungeonClears = dungeonClears;
        markUpdated();
    }

    public Map<String, Long> getDungeonBestTimes() {
        return dungeonBestTimes;
    }

    public void setDungeonBestTimes(Map<String, Long> dungeonBestTimes) {
        this.dungeonBestTimes = dungeonBestTimes;
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
        map.put("totalExperience", totalExperience);
        map.put("currentLevel", currentLevel);
        map.put("levelProgress", levelProgress);
        map.put("totalKills", totalKills);
        map.put("totalDeaths", totalDeaths);
        map.put("totalDamageDealt", totalDamageDealt);
        map.put("totalDamageTaken", totalDamageTaken);
        map.put("totalCoinsEarned", totalCoinsEarned);
        map.put("totalCoinsSpent", totalCoinsSpent);
        map.put("currentCoins", currentCoins);
        map.put("dungeonClears", dungeonClears);
        map.put("dungeonBestTimes", dungeonBestTimes);
        map.put("lastUpdated", lastUpdated);
        return map;
    }

    /**
     * Map에서 생성
     */
    public static ProgressDTO fromMap(Map<String, Object> map) {
        ProgressDTO dto = new ProgressDTO();

        // Long 타입 필드들
        DTOUtil.setLongFromMap(map, "totalExperience", dto::setTotalExperience);
        DTOUtil.setIntFromMap(map, "currentLevel", dto::setCurrentLevel);
        DTOUtil.setLongFromMap(map, "totalKills", dto::setTotalKills);
        DTOUtil.setLongFromMap(map, "totalDeaths", dto::setTotalDeaths);
        DTOUtil.setLongFromMap(map, "totalDamageDealt", dto::setTotalDamageDealt);
        DTOUtil.setLongFromMap(map, "totalDamageTaken", dto::setTotalDamageTaken);
        DTOUtil.setLongFromMap(map, "totalCoinsEarned", dto::setTotalCoinsEarned);
        DTOUtil.setLongFromMap(map, "totalCoinsSpent", dto::setTotalCoinsSpent);
        DTOUtil.setLongFromMap(map, "currentCoins", dto::setCurrentCoins);
        DTOUtil.setLongFromMap(map, "lastUpdated", dto::setLastUpdated);

        // Double 타입 필드
        Double progress = DTOUtil.toDouble(map.get("levelProgress"));
        if (progress != null) {
            dto.setLevelProgress(progress);
        }

        // Map 타입 필드들
        dto.setDungeonClears(DTOUtil.toIntegerMap(map.get("dungeonClears")));
        dto.setDungeonBestTimes(DTOUtil.toLongMap(map.get("dungeonBestTimes")));

        return dto;
    }
}