package com.febrie.rpg.dto;

import com.febrie.rpg.stat.Stat;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Firestore players/{uuid}/stats 문서 DTO (순수 POJO)
 * 플레이어의 스탯 정보를 저장
 *
 * @author Febrie, CoffeeTory
 */
public class StatsDTO {

    private Map<String, Integer> baseStats = new HashMap<>();
    private Map<String, Integer> bonusStats = new HashMap<>();
    private int availableStatPoints = 0;
    private int totalStatPointsUsed = 0;

    private long lastUpdated;

    // 기본 생성자 (Firestore 필수)
    public StatsDTO() {
        initializeDefaultStats();
        this.lastUpdated = System.currentTimeMillis();
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

    /**
     * 데이터 업데이트 시 타임스탬프도 업데이트
     */
    public void markUpdated() {
        this.lastUpdated = System.currentTimeMillis();
    }

    // Stat 관련 메소드들
    public int getBaseStat(@NotNull String statId) {
        return baseStats.getOrDefault(statId, 0);
    }

    public void setBaseStat(@NotNull String statId, int value) {
        baseStats.put(statId, value);
        markUpdated();
    }

    public int getBonusStat(@NotNull String statId) {
        return bonusStats.getOrDefault(statId, 0);
    }

    public void setBonusStat(@NotNull String statId, int value) {
        bonusStats.put(statId, value);
        markUpdated();
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
        markUpdated();
    }

    public Map<String, Integer> getBonusStats() {
        return bonusStats;
    }

    public void setBonusStats(Map<String, Integer> bonusStats) {
        this.bonusStats = bonusStats;
        markUpdated();
    }

    public int getAvailableStatPoints() {
        return availableStatPoints;
    }

    public void setAvailableStatPoints(int availableStatPoints) {
        this.availableStatPoints = availableStatPoints;
        markUpdated();
    }

    public int getTotalStatPointsUsed() {
        return totalStatPointsUsed;
    }

    public void setTotalStatPointsUsed(int totalStatPointsUsed) {
        this.totalStatPointsUsed = totalStatPointsUsed;
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
        map.put("baseStats", baseStats);
        map.put("bonusStats", bonusStats);
        map.put("availableStatPoints", availableStatPoints);
        map.put("totalStatPointsUsed", totalStatPointsUsed);
        map.put("lastUpdated", lastUpdated);
        return map;
    }

    /**
     * Map에서 생성
     */
    public static StatsDTO fromMap(Map<String, Object> map) {
        StatsDTO dto = new StatsDTO();

        dto.setBaseStats(DTOUtil.toIntegerMap(map.get("baseStats")));
        dto.setBonusStats(DTOUtil.toIntegerMap(map.get("bonusStats")));

        DTOUtil.setIntFromMap(map, "availableStatPoints", dto::setAvailableStatPoints);
        DTOUtil.setIntFromMap(map, "totalStatPointsUsed", dto::setTotalStatPointsUsed);
        DTOUtil.setLongFromMap(map, "lastUpdated", dto::setLastUpdated);

        return dto;
    }
}