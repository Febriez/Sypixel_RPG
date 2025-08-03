package com.febrie.rpg.dto.quest;

import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.quest.reward.ClaimedRewardData;
import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어 퀘스트 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie
 */
public record PlayerQuestDTO(String playerId, Map<String, QuestProgress> activeQuests,
                             Map<String, CompletedQuestDTO> completedQuests, 
                             Map<String, ClaimedRewardData> claimedRewardData, long lastUpdated) {
    /**
     * 기본 생성자 - 신규 플레이어용
     */
    public PlayerQuestDTO(String playerId) {
        this(playerId, new HashMap<>(), new HashMap<>(), new HashMap<>(), System.currentTimeMillis());
    }

    /**
     * 방어적 복사를 위한 생성자
     */
    public PlayerQuestDTO(String playerId, Map<String, QuestProgress> activeQuests, 
                         Map<String, CompletedQuestDTO> completedQuests, 
                         Map<String, ClaimedRewardData> claimedRewardData, long lastUpdated) {
        this.playerId = playerId;
        this.activeQuests = new HashMap<>(activeQuests);
        this.completedQuests = new HashMap<>(completedQuests);
        this.claimedRewardData = new HashMap<>(claimedRewardData);
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

    /**
     * 수령한 보상 데이터 맵의 불변 뷰 반환
     */
    @Override
    public Map<String, ClaimedRewardData> claimedRewardData() {
        return new HashMap<>(claimedRewardData);
    }

    /**
     * Map으로 변환 (Firestore SDK용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("playerId", playerId);
        
        // activeQuests - 비워두기 (QuestProgress가 Map 지원 안함)
        map.put("activeQuests", new HashMap<>());
        
        // completedQuests
        Map<String, Object> completedQuestsMap = new HashMap<>();
        completedQuests.forEach((key, value) -> completedQuestsMap.put(key, value.toMap()));
        map.put("completedQuests", completedQuestsMap);
        
        // claimedRewardData
        Map<String, Object> claimedRewardDataMap = new HashMap<>();
        claimedRewardData.forEach((key, value) -> claimedRewardDataMap.put(key, value.toMap()));
        map.put("claimedRewardData", claimedRewardDataMap);
        
        map.put("lastUpdated", lastUpdated);
        return map;
    }
    
    /**
     * Map에서 생성 (Firestore SDK용)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static PlayerQuestDTO fromMap(@NotNull Map<String, Object> map) {
        String playerId = (String) map.getOrDefault("playerId", "");
        
        // activeQuests - 비워두기 (QuestProgress가 Map 지원 안함)
        Map<String, QuestProgress> activeQuests = new HashMap<>();
        
        // completedQuests
        Map<String, CompletedQuestDTO> completedQuests = new HashMap<>();
        Object completedObj = map.get("completedQuests");
        if (completedObj instanceof Map) {
            Map<String, Object> completedMap = (Map<String, Object>) completedObj;
            completedMap.forEach((key, value) -> {
                if (value instanceof Map) {
                    completedQuests.put(key, CompletedQuestDTO.fromMap((Map<String, Object>) value));
                }
            });
        }
        
        // claimedRewardData
        Map<String, ClaimedRewardData> claimedRewardData = new HashMap<>();
        Object claimedObj = map.get("claimedRewardData");
        if (claimedObj instanceof Map) {
            Map<String, Object> claimedMap = (Map<String, Object>) claimedObj;
            claimedMap.forEach((key, value) -> {
                if (value instanceof Map) {
                    claimedRewardData.put(key, ClaimedRewardData.fromMap((Map<String, Object>) value));
                }
            });
        }
        
        long lastUpdated = FirestoreUtils.getLong(map, "lastUpdated", System.currentTimeMillis());
        
        return new PlayerQuestDTO(playerId, activeQuests, completedQuests, claimedRewardData, lastUpdated);
    }
}