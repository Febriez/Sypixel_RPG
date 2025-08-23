package com.febrie.rpg.dto.quest;

import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 목표 진행도 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author CoffeeTory
 */
public record ObjectiveProgressDTO(
        String objectiveId,
        boolean completed,
        int progress,
        int target,
        long lastUpdated
) {
    /**
     * 기본 생성자 - 신규 목표용
     */
    public ObjectiveProgressDTO(String objectiveId, int target) {
        this(objectiveId, false, 0, target, System.currentTimeMillis());
    }
    
    /**
     * Map으로 변환
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("objectiveId", objectiveId);
        map.put("completed", completed);
        map.put("progress", progress);
        map.put("target", target);
        map.put("lastUpdated", lastUpdated);
        
        return map;
    }
    
    /**
     * ObjectiveProgress에서 DTO 생성
     */
    @NotNull
    public static ObjectiveProgressDTO from(@NotNull ObjectiveProgress progress) {
        return new ObjectiveProgressDTO(
                progress.getObjectiveId(),
                progress.isCompleted(),
                progress.getCurrentValue(),
                progress.getRequiredValue(),
                progress.getLastUpdated()
        );
    }
    
    /**
     * Map에서 ObjectiveProgressDTO 생성
     */
    @NotNull
    public static ObjectiveProgressDTO fromMap(@NotNull Map<String, Object> map) {
        String objectiveId = (String) map.getOrDefault("objectiveId", "");
        
        Object completedObj = map.get("completed");
        boolean completed = completedObj instanceof Boolean ? (Boolean) completedObj : false;
        
        int progress = FirestoreUtils.getInt(map, "progress", 0);
        int target = FirestoreUtils.getInt(map, "target", 0);
        long lastUpdated = FirestoreUtils.getLong(map, "lastUpdated", System.currentTimeMillis());
        
        return new ObjectiveProgressDTO(objectiveId, completed, progress, target, lastUpdated);
    }
}