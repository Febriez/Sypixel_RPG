package com.febrie.rpg.dto;

import com.febrie.rpg.stat.Stat;
import com.google.cloud.Timestamp;
import com.google.firebase.firestore.annotation.ServerTimestamp;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Firestore players/{uuid}/stats 문서 DTO
 * 플레이어의 스탯 정보를 저장
 *
 * @author Febrie, CoffeeTory
 */
public class StatsDTO {

    private Map<String, Integer> baseStats = new HashMap<>();
    private Map<String, Integer> bonusStats = new HashMap<>();
    private int availableStatPoints = 0;
    private int totalStatPointsUsed = 0;

    @ServerTimestamp
    private Timestamp lastUpdated;

    // 기본 생성자 (Firestore 필수)
    public StatsDTO() {
        initializeDefaultStats();
    }

    /**
     * 기본 스탯값으로 초기화
     */
    private void initializeDefaultStats() {
        for (Stat stat : Stat.getAllStats().values()) {
            baseStats.put(stat.getId(), stat.getDefaultValue());
            bonusStats.put(stat.getId(), 0);
        }
    }

    // Stat 관련 메소드들
    public int getBaseStat(@NotNull String statId) {
        return baseStats.getOrDefault(statId, 0);
    }

    public void setBaseStat(@NotNull String statId, int value) {
        baseStats.put(statId, value);
    }

    public int getBonusStat(@NotNull String statId) {
        return bonusStats.getOrDefault(statId, 0);
    }

    public void setBonusStat(@NotNull String statId, int value) {
        bonusStats.put(statId, value);
    }

    public int getTotalStat(@NotNull String statId) {
        return getBaseStat(statId) + getBonusStat(statId);
    }

    // Getters and Setters
    public Map<String, Integer> getBaseStats() {
        return baseStats;
    }

    public void setBaseStats(Map<String, Integer> baseStats) {
        this.baseStats = baseStats;
    }

    public Map<String, Integer> getBonusStats() {
        return bonusStats;
    }

    public void setBonusStats(Map<String, Integer> bonusStats) {
        this.bonusStats = bonusStats;
    }

    public int getAvailableStatPoints() {
        return availableStatPoints;
    }

    public void setAvailableStatPoints(int availableStatPoints) {
        this.availableStatPoints = availableStatPoints;
    }

    public int getTotalStatPointsUsed() {
        return totalStatPointsUsed;
    }

    public void setTotalStatPointsUsed(int totalStatPointsUsed) {
        this.totalStatPointsUsed = totalStatPointsUsed;
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
        map.put("baseStats", baseStats);
        map.put("bonusStats", bonusStats);
        map.put("availableStatPoints", availableStatPoints);
        map.put("totalStatPointsUsed", totalStatPointsUsed);
        return map;
    }

    /**
     * Map에서 생성
     */
    @SuppressWarnings("unchecked")
    public static StatsDTO fromMap(Map<String, Object> map) {
        StatsDTO dto = new StatsDTO();

        Object baseStatsObj = map.get("baseStats");
        if (baseStatsObj instanceof Map) {
            Map<String, Object> baseStatsMap = (Map<String, Object>) baseStatsObj;
            Map<String, Integer> baseStats = new HashMap<>();
            baseStatsMap.forEach((k, v) -> {
                if (v instanceof Long) {
                    baseStats.put(k, ((Long) v).intValue());
                }
            });
            dto.setBaseStats(baseStats);
        }

        Object bonusStatsObj = map.get("bonusStats");
        if (bonusStatsObj instanceof Map) {
            Map<String, Object> bonusStatsMap = (Map<String, Object>) bonusStatsObj;
            Map<String, Integer> bonusStats = new HashMap<>();
            bonusStatsMap.forEach((k, v) -> {
                if (v instanceof Long) {
                    bonusStats.put(k, ((Long) v).intValue());
                }
            });
            dto.setBonusStats(bonusStats);
        }

        Object availablePoints = map.get("availableStatPoints");
        if (availablePoints instanceof Long) {
            dto.setAvailableStatPoints(((Long) availablePoints).intValue());
        }

        Object totalUsed = map.get("totalStatPointsUsed");
        if (totalUsed instanceof Long) {
            dto.setTotalStatPointsUsed(((Long) totalUsed).intValue());
        }

        return dto;
    }
}