package com.febrie.rpg.dto.quest;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어 퀘스트 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie
 */
public record PlayerQuestDTO(String playerId, 
                             Map<String, ActiveQuestDTO> activeQuests,
                             Map<String, CompletedQuestDTO> completedQuests,
                             Map<String, ClaimedQuestDTO> claimedQuests,
                             long lastUpdated) {
    /**
     * 기본 생성자 - 신규 플레이어용
     */
    public PlayerQuestDTO(String playerId) {
        this(playerId, new HashMap<>(), new HashMap<>(), new HashMap<>(), System.currentTimeMillis());
    }

    /**
     * 방어적 복사를 위한 생성자
     */
    public PlayerQuestDTO(String playerId, 
                         Map<String, ActiveQuestDTO> activeQuests,
                         Map<String, CompletedQuestDTO> completedQuests,
                         Map<String, ClaimedQuestDTO> claimedQuests,
                         long lastUpdated) {
        this.playerId = playerId;
        this.activeQuests = new HashMap<>(activeQuests);
        this.completedQuests = new HashMap<>(completedQuests);
        this.claimedQuests = new HashMap<>(claimedQuests);
        this.lastUpdated = lastUpdated;
    }

    /**
     * 활성 퀘스트 맵의 불변 뷰 반환
     */
    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull Map<String, ActiveQuestDTO> activeQuests() {
        return new HashMap<>(activeQuests);
    }

    /**
     * 완료된 퀘스트 맵의 불변 뷰 반환
     */
    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull Map<String, CompletedQuestDTO> completedQuests() {
        return new HashMap<>(completedQuests);
    }

    /**
     * 보상을 모두 수령한 퀘스트 맵의 불변 뷰 반환
     */
    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull Map<String, ClaimedQuestDTO> claimedQuests() {
        return new HashMap<>(claimedQuests);
    }

    /**
     * Map으로 변환 (Firestore SDK용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("playerId", playerId);
        
        // activeQuests
        Map<String, Object> activeQuestsMap = new HashMap<>();
        activeQuests.forEach((key, value) -> activeQuestsMap.put(key, value.toMap()));
        map.put("activeQuests", activeQuestsMap);
        
        // completedQuests
        Map<String, Object> completedQuestsMap = new HashMap<>();
        completedQuests.forEach((key, value) -> completedQuestsMap.put(key, value.toMap()));
        map.put("completedQuests", completedQuestsMap);
        
        // claimedQuests
        Map<String, Object> claimedQuestsMap = new HashMap<>();
        claimedQuests.forEach((key, value) -> claimedQuestsMap.put(key, value.toMap()));
        map.put("claimedQuests", claimedQuestsMap);
        
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
        
        // activeQuests
        Map<String, ActiveQuestDTO> activeQuests = new HashMap<>();
        Object activeObj = map.get("activeQuests");
        if (activeObj instanceof Map) {
            Map<String, Object> activeMap = (Map<String, Object>) activeObj;
            activeMap.forEach((key, value) -> {
                if (value instanceof Map) {
                    activeQuests.put(key, ActiveQuestDTO.fromMap((Map<String, Object>) value));
                }
            });
        }
        
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
        
        // claimedQuests
        Map<String, ClaimedQuestDTO> claimedQuests = new HashMap<>();
        Object claimedObj = map.get("claimedQuests");
        if (claimedObj instanceof Map) {
            Map<String, Object> claimedMap = (Map<String, Object>) claimedObj;
            claimedMap.forEach((key, value) -> {
                if (value instanceof Map) {
                    claimedQuests.put(key, ClaimedQuestDTO.fromMap((Map<String, Object>) value));
                }
            });
        }
        
        long lastUpdated = FirestoreUtils.getLong(map, "lastUpdated", System.currentTimeMillis());
        
        return new PlayerQuestDTO(playerId, activeQuests, completedQuests, claimedQuests, lastUpdated);
    }
}