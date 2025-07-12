package com.febrie.rpg.dto;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어 특성 정보 DTO
 * Firebase 저장용
 *
 * @author Febrie, CoffeeTory
 */
public class TalentDTO {

    private int availablePoints = 0;
    private Map<String, Integer> learnedTalents = new HashMap<>();

    public TalentDTO() {
        // 기본 생성자
    }

    // Getters and Setters
    public int getAvailablePoints() {
        return availablePoints;
    }

    public void setAvailablePoints(int availablePoints) {
        this.availablePoints = availablePoints;
    }

    @NotNull
    public Map<String, Integer> getLearnedTalents() {
        return new HashMap<>(learnedTalents);
    }

    public void setLearnedTalents(@NotNull Map<String, Integer> learnedTalents) {
        this.learnedTalents = new HashMap<>(learnedTalents);
    }

    /**
     * 특정 특성 레벨 가져오기
     */
    public int getTalentLevel(@NotNull String talentId) {
        return learnedTalents.getOrDefault(talentId, 0);
    }

    /**
     * 특정 특성 레벨 설정
     */
    public void setTalentLevel(@NotNull String talentId, int level) {
        if (level > 0) {
            learnedTalents.put(talentId, level);
        } else {
            learnedTalents.remove(talentId);
        }
    }
}