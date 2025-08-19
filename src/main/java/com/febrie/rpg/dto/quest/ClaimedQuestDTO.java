package com.febrie.rpg.dto.quest;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import net.kyori.adventure.text.Component;
/**
 * 완료하고 모든 보상을 수령한 퀘스트 데이터 DTO
 * 
 * @author Febrie
 */
public record ClaimedQuestDTO(
        @NotNull String questId,
        @NotNull String instanceId,
        long completedAt,
        long claimedAt,
        int completionCount
) {
    /**
     * 완료된 퀘스트에서 변환
     */
    public static ClaimedQuestDTO from(@NotNull CompletedQuestDTO completedData) {
        return new ClaimedQuestDTO(
                completedData.questId(),
                completedData.instanceId(),
                completedData.completedAt(),
                System.currentTimeMillis(),
                completedData.completionCount()
        );
    }
    
    /**
     * Map으로 변환 (Firestore 저장용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("questId", questId);
        map.put("instanceId", instanceId);
        map.put("completedAt", completedAt);
        map.put("claimedAt", claimedAt);
        map.put("completionCount", completionCount);
        return map;
    }
    
    /**
     * Map에서 생성 (Firestore 로드용)
     */
    @NotNull
    public static ClaimedQuestDTO fromMap(@NotNull Map<String, Object> map) {
        String questId = (String) map.getOrDefault("questId", "");
        String instanceId = (String) map.getOrDefault("instanceId", "");
        long completedAt = FirestoreUtils.getLong(map, "completedAt", System.currentTimeMillis());
        long claimedAt = FirestoreUtils.getLong(map, "claimedAt", System.currentTimeMillis());
        int completionCount = FirestoreUtils.getInt(map, "completionCount", 1);
        
        return new ClaimedQuestDTO(questId, instanceId, completedAt, claimedAt, completionCount);
    }
}