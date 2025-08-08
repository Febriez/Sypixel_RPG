package com.febrie.rpg.dto.quest;

import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 진행 중인 퀘스트 데이터 DTO
 * 
 * @author Febrie
 */
public record ActiveQuestDTO(
        @NotNull String questId,
        @NotNull String instanceId,
        @NotNull Map<String, ObjectiveProgressDTO> progress,
        long startedAt
) {
    /**
     * 새로운 활성 퀘스트 생성
     */
    public static ActiveQuestDTO create(@NotNull String questId, @NotNull String instanceId,
                                       @NotNull Map<String, ObjectiveProgress> progress) {
        Map<String, ObjectiveProgressDTO> progressDTOs = new HashMap<>();
        progress.forEach((key, value) -> progressDTOs.put(key, ObjectiveProgressDTO.from(value)));
        
        return new ActiveQuestDTO(questId, instanceId, progressDTOs, System.currentTimeMillis());
    }
    
    /**
     * 방어적 복사를 위한 생성자
     */
    public ActiveQuestDTO(@NotNull String questId, @NotNull String instanceId,
                         @NotNull Map<String, ObjectiveProgressDTO> progress, long startedAt) {
        this.questId = questId;
        this.instanceId = instanceId;
        this.progress = new HashMap<>(progress);
        this.startedAt = startedAt;
    }
    
    /**
     * progress의 불변 뷰 반환
     */
    @Override
    public Map<String, ObjectiveProgressDTO> progress() {
        return new HashMap<>(progress);
    }
    
    /**
     * Map으로 변환 (Firestore 저장용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("questId", questId);
        map.put("instanceId", instanceId);
        map.put("startedAt", startedAt);
        
        // Progress 직렬화
        Map<String, Object> progressMap = new HashMap<>();
        progress.forEach((key, value) -> progressMap.put(key, value.toMap()));
        map.put("progress", progressMap);
        
        return map;
    }
    
    /**
     * Map에서 생성 (Firestore 로드용)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static ActiveQuestDTO fromMap(@NotNull Map<String, Object> map) {
        String questId = (String) map.getOrDefault("questId", "");
        String instanceId = (String) map.getOrDefault("instanceId", "");
        long startedAt = FirestoreUtils.getLong(map, "startedAt", System.currentTimeMillis());
        
        // Progress 역직렬화
        Map<String, ObjectiveProgressDTO> progress = new HashMap<>();
        Object progressObj = map.get("progress");
        if (progressObj instanceof Map) {
            Map<String, Object> progressMap = (Map<String, Object>) progressObj;
            progressMap.forEach((key, value) -> {
                if (value instanceof Map) {
                    progress.put(key, ObjectiveProgressDTO.fromMap((Map<String, Object>) value));
                }
            });
        }
        
        return new ActiveQuestDTO(questId, instanceId, progress, startedAt);
    }
}