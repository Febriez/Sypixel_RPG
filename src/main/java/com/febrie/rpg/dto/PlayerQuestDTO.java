package com.febrie.rpg.dto;

import com.febrie.rpg.quest.progress.QuestProgress;

import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어 퀘스트 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie
 */
public record PlayerQuestDTO(
        String playerId,
        Map<String, QuestProgress> activeQuests,
        Map<String, CompletedQuestDTO> completedQuests,
        long lastUpdated
) {
    /**
     * 기본 생성자 - 신규 플레이어용
     */
    public PlayerQuestDTO(String playerId) {
        this(playerId, new HashMap<>(), new HashMap<>(), System.currentTimeMillis());
    }

    /**
     * 방어적 복사를 위한 생성자
     */
    public PlayerQuestDTO(String playerId, Map<String, QuestProgress> activeQuests,
                          Map<String, CompletedQuestDTO> completedQuests, long lastUpdated) {
        this.playerId = playerId;
        this.activeQuests = new HashMap<>(activeQuests);
        this.completedQuests = new HashMap<>(completedQuests);
        this.lastUpdated = lastUpdated;
    }

    /**
     * 활성 퀘스트 맵의 불변 뷰 반환
     */
    @Override
    public Map<String, QuestProgress> activeQuests() {
        return new HashMap<>(activeQuests);
    }

    /**
     * 완료된 퀘스트 맵의 불변 뷰 반환
     */
    @Override
    public Map<String, CompletedQuestDTO> completedQuests() {
        return new HashMap<>(completedQuests);
    }
}