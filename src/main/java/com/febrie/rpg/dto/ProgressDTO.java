package com.febrie.rpg.dto;

import com.google.cloud.Timestamp;
import com.google.firebase.firestore.annotation.ServerTimestamp;

import java.util.HashMap;
import java.util.Map;

/**
 * Firestore players/{uuid}/progress 문서 DTO
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

    @ServerTimestamp
    private Timestamp lastUpdated;

    // 기본 생성자 (Firestore 필수)
    public ProgressDTO() {
    }

    // 경험치 관련 메소드
    public void addExperience(long exp) {
        this.totalExperience += exp;
    }

    // 전투 통계 메소드
    public void addKill() {
        this.totalKills++;
    }

    public void addDeath() {
        this.totalDeaths++;
    }

    public double getKDRatio() {
        if (totalDeaths == 0) return totalKills;
        return (double) totalKills / totalDeaths;
    }

    // 던전 통계 메소드
    public void addDungeonClear(String dungeonId) {
        dungeonClears.merge(dungeonId, 1, Integer::sum);
    }

    public void updateDungeonBestTime(String dungeonId, long timeMs) {
        Long currentBest = dungeonBestTimes.get(dungeonId);
        if (currentBest == null || timeMs < currentBest) {
            dungeonBestTimes.put(dungeonId, timeMs);
        }
    }

    // Getters and Setters
    public long getTotalExperience() {
        return totalExperience;
    }

    public void setTotalExperience(long totalExperience) {
        this.totalExperience = totalExperience;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public double getLevelProgress() {
        return levelProgress;
    }

    public void setLevelProgress(double levelProgress) {
        this.levelProgress = levelProgress;
    }

    public long getTotalKills() {
        return totalKills;
    }

    public void setTotalKills(long totalKills) {
        this.totalKills = totalKills;
    }

    public long getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(long totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    public long getTotalDamageDealt() {
        return totalDamageDealt;
    }

    public void setTotalDamageDealt(long totalDamageDealt) {
        this.totalDamageDealt = totalDamageDealt;
    }

    public long getTotalDamageTaken() {
        return totalDamageTaken;
    }

    public void setTotalDamageTaken(long totalDamageTaken) {
        this.totalDamageTaken = totalDamageTaken;
    }

    public long getTotalCoinsEarned() {
        return totalCoinsEarned;
    }

    public void setTotalCoinsEarned(long totalCoinsEarned) {
        this.totalCoinsEarned = totalCoinsEarned;
    }

    public long getTotalCoinsSpent() {
        return totalCoinsSpent;
    }

    public void setTotalCoinsSpent(long totalCoinsSpent) {
        this.totalCoinsSpent = totalCoinsSpent;
    }

    public long getCurrentCoins() {
        return currentCoins;
    }

    public void setCurrentCoins(long currentCoins) {
        this.currentCoins = currentCoins;
    }

    public Map<String, Integer> getDungeonClears() {
        return dungeonClears;
    }

    public void setDungeonClears(Map<String, Integer> dungeonClears) {
        this.dungeonClears = dungeonClears;
    }

    public Map<String, Long> getDungeonBestTimes() {
        return dungeonBestTimes;
    }

    public void setDungeonBestTimes(Map<String, Long> dungeonBestTimes) {
        this.dungeonBestTimes = dungeonBestTimes;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
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
        return map;
    }

    /**
     * Map에서 생성
     */
    @SuppressWarnings("unchecked")
    public static ProgressDTO fromMap(Map<String, Object> map) {
        ProgressDTO dto = new ProgressDTO();

        // Long 타입 필드들
        Object exp = map.get("totalExperience");
        if (exp instanceof Long) dto.setTotalExperience((Long) exp);

        Object level = map.get("currentLevel");
        if (level instanceof Long) dto.setCurrentLevel(((Long) level).intValue());

        Object progress = map.get("levelProgress");
        if (progress instanceof Double) dto.setLevelProgress((Double) progress);

        Object kills = map.get("totalKills");
        if (kills instanceof Long) dto.setTotalKills((Long) kills);

        Object deaths = map.get("totalDeaths");
        if (deaths instanceof Long) dto.setTotalDeaths((Long) deaths);

        Object damageDealt = map.get("totalDamageDealt");
        if (damageDealt instanceof Long) dto.setTotalDamageDealt((Long) damageDealt);

        Object damageTaken = map.get("totalDamageTaken");
        if (damageTaken instanceof Long) dto.setTotalDamageTaken((Long) damageTaken);

        Object coinsEarned = map.get("totalCoinsEarned");
        if (coinsEarned instanceof Long) dto.setTotalCoinsEarned((Long) coinsEarned);

        Object coinsSpent = map.get("totalCoinsSpent");
        if (coinsSpent instanceof Long) dto.setTotalCoinsSpent((Long) coinsSpent);

        Object currentCoins = map.get("currentCoins");
        if (currentCoins instanceof Long) dto.setCurrentCoins((Long) currentCoins);

        // Map 타입 필드들
        Object dungeonClearsObj = map.get("dungeonClears");
        if (dungeonClearsObj instanceof Map) {
            Map<String, Object> dungeonClearsMap = (Map<String, Object>) dungeonClearsObj;
            Map<String, Integer> dungeonClears = new HashMap<>();
            dungeonClearsMap.forEach((k, v) -> {
                if (v instanceof Long) {
                    dungeonClears.put(k, ((Long) v).intValue());
                }
            });
            dto.setDungeonClears(dungeonClears);
        }

        Object dungeonBestTimesObj = map.get("dungeonBestTimes");
        if (dungeonBestTimesObj instanceof Map) {
            Map<String, Object> dungeonBestTimesMap = (Map<String, Object>) dungeonBestTimesObj;
            Map<String, Long> dungeonBestTimes = new HashMap<>();
            dungeonBestTimesMap.forEach((k, v) -> {
                if (v instanceof Long) {
                    dungeonBestTimes.put(k, (Long) v);
                }
            });
            dto.setDungeonBestTimes(dungeonBestTimes);
        }

        return dto;
    }
}