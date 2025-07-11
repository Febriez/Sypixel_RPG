package com.febrie.rpg.dto;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Firestore players/{uuid}/talents 문서 DTO (순수 POJO)
 * 플레이어의 특성(탤런트) 정보를 저장
 *
 * @author Febrie, CoffeeTory
 */
public class TalentDTO {

    // 특성 ID와 레벨을 저장 (talentId -> level)
    private Map<String, Integer> talentLevels = new HashMap<>();

    private int availableTalentPoints = 0;
    private int totalTalentPointsUsed = 0;

    private long lastUpdated;

    // 기본 생성자 (Firestore 필수)
    public TalentDTO() {
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * 데이터 업데이트 시 타임스탬프도 업데이트
     */
    public void markUpdated() {
        this.lastUpdated = System.currentTimeMillis();
    }

    // 특성 관련 메소드들
    public int getTalentLevel(@NotNull String talentId) {
        return talentLevels.getOrDefault(talentId, 0);
    }

    public void setTalentLevel(@NotNull String talentId, int level) {
        if (level > 0) {
            talentLevels.put(talentId, level);
        } else {
            talentLevels.remove(talentId);
        }
        markUpdated();
    }

    public boolean hasTalent(@NotNull String talentId) {
        return talentLevels.containsKey(talentId) && talentLevels.get(talentId) > 0;
    }

    // Getters and Setters
    public Map<String, Integer> getTalentLevels() {
        return talentLevels;
    }

    public void setTalentLevels(Map<String, Integer> talentLevels) {
        this.talentLevels = talentLevels;
        markUpdated();
    }

    public int getAvailableTalentPoints() {
        return availableTalentPoints;
    }

    public void setAvailableTalentPoints(int availableTalentPoints) {
        this.availableTalentPoints = availableTalentPoints;
        markUpdated();
    }

    public int getTotalTalentPointsUsed() {
        return totalTalentPointsUsed;
    }

    public void setTotalTalentPointsUsed(int totalTalentPointsUsed) {
        this.totalTalentPointsUsed = totalTalentPointsUsed;
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
        map.put("talentLevels", talentLevels);
        map.put("availableTalentPoints", availableTalentPoints);
        map.put("totalTalentPointsUsed", totalTalentPointsUsed);
        map.put("lastUpdated", lastUpdated);
        return map;
    }

    /**
     * Map에서 생성
     */
    public static TalentDTO fromMap(Map<String, Object> map) {
        TalentDTO dto = new TalentDTO();

        dto.setTalentLevels(DTOUtil.toIntegerMap(map.get("talentLevels")));

        DTOUtil.setIntFromMap(map, "availableTalentPoints", dto::setAvailableTalentPoints);
        DTOUtil.setIntFromMap(map, "totalTalentPointsUsed", dto::setTotalTalentPointsUsed);
        DTOUtil.setLongFromMap(map, "lastUpdated", dto::setLastUpdated);

        return dto;
    }
}