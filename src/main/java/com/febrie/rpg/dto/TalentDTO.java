package com.febrie.rpg.dto;

import com.google.cloud.Timestamp;
import com.google.firebase.firestore.annotation.ServerTimestamp;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Firestore players/{uuid}/talents 문서 DTO
 * 플레이어의 특성(탤런트) 정보를 저장
 *
 * @author Febrie, CoffeeTory
 */
public class TalentDTO {

    // 특성 ID와 레벨을 저장 (talentId -> level)
    private Map<String, Integer> talentLevels = new HashMap<>();

    private int availableTalentPoints = 0;
    private int totalTalentPointsUsed = 0;

    @ServerTimestamp
    private Timestamp lastUpdated;

    // 기본 생성자 (Firestore 필수)
    public TalentDTO() {
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
    }

    public int getAvailableTalentPoints() {
        return availableTalentPoints;
    }

    public void setAvailableTalentPoints(int availableTalentPoints) {
        this.availableTalentPoints = availableTalentPoints;
    }

    public int getTotalTalentPointsUsed() {
        return totalTalentPointsUsed;
    }

    public void setTotalTalentPointsUsed(int totalTalentPointsUsed) {
        this.totalTalentPointsUsed = totalTalentPointsUsed;
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
        map.put("talentLevels", talentLevels);
        map.put("availableTalentPoints", availableTalentPoints);
        map.put("totalTalentPointsUsed", totalTalentPointsUsed);
        return map;
    }

    /**
     * Map에서 생성
     */
    @SuppressWarnings("unchecked")
    public static TalentDTO fromMap(Map<String, Object> map) {
        TalentDTO dto = new TalentDTO();

        Object talentLevelsObj = map.get("talentLevels");
        if (talentLevelsObj instanceof Map) {
            Map<String, Object> talentLevelsMap = (Map<String, Object>) talentLevelsObj;
            Map<String, Integer> talentLevels = new HashMap<>();
            talentLevelsMap.forEach((k, v) -> {
                if (v instanceof Long) {
                    talentLevels.put(k, ((Long) v).intValue());
                }
            });
            dto.setTalentLevels(talentLevels);
        }

        Object availablePoints = map.get("availableTalentPoints");
        if (availablePoints instanceof Long) {
            dto.setAvailableTalentPoints(((Long) availablePoints).intValue());
        }

        Object totalUsed = map.get("totalTalentPointsUsed");
        if (totalUsed instanceof Long) {
            dto.setTotalTalentPointsUsed(((Long) totalUsed).intValue());
        }

        return dto;
    }
}