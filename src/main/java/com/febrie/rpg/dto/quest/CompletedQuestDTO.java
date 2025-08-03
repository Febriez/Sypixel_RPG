package com.febrie.rpg.dto.quest;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.HashMap;

/**
 * 완료된 퀘스트 정보 DTO
 * 
 * @author Febrie
 */
public record CompletedQuestDTO(
        @NotNull String questId,
        long completedAt,      // 퀘스트 완료 시간
        int completionCount,   // 완료 횟수 (반복 퀘스트용)
        boolean rewarded       // 보상 수령 여부
) {
    /**
     * 퀘스트 완료 생성 팩토리 메소드
     */
    @NotNull
    public static CompletedQuestDTO createCompleted(@NotNull String questId) {
        return new CompletedQuestDTO(questId, System.currentTimeMillis(), 1, false);
    }
    
    /**
     * Map으로 변환
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("questId", questId);
        map.put("completedAt", completedAt);
        map.put("completionCount", completionCount);
        map.put("rewarded", rewarded);
        return map;
    }
    
    /**
     * Map에서 생성
     */
    @NotNull
    public static CompletedQuestDTO fromMap(@NotNull Map<String, Object> map) {
        String questId = (String) map.getOrDefault("questId", "");
        long completedAt = FirestoreUtils.getLong(map, "completedAt", System.currentTimeMillis());
        int completionCount = FirestoreUtils.getInt(map, "completionCount", 1);
        boolean rewarded = (Boolean) map.getOrDefault("rewarded", false);
        
        return new CompletedQuestDTO(questId, completedAt, completionCount, rewarded);
    }
}