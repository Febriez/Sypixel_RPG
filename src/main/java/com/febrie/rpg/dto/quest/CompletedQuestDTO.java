package com.febrie.rpg.dto.quest;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
/**
 * 완료했지만 보상을 받지 않은 퀘스트 데이터 DTO
 * 
 * @author Febrie
 */
public record CompletedQuestDTO(
        @NotNull String questId,
        @NotNull String instanceId,
        long completedAt,
        int completionCount,
        boolean instantRewardsClaimed,
        @NotNull Set<Integer> unclaimedItemIndices
) {
    /**
     * 새로운 완료 퀘스트 생성
     */
    public static CompletedQuestDTO create(@NotNull String questId, @NotNull String instanceId, 
                                         int completionCount, int totalItemCount) {
        // 초기에는 모든 아이템이 미수령 상태
        Set<Integer> unclaimedItemIndices = new HashSet<>();
        for (int i = 0; i < totalItemCount; i++) {
            unclaimedItemIndices.add(i);
        }
        
        return new CompletedQuestDTO(questId, instanceId, System.currentTimeMillis(), 
                                   completionCount, false, unclaimedItemIndices);
    }
    
    /**
     * 방어적 복사를 위한 생성자
     */
    public CompletedQuestDTO(@NotNull String questId, @NotNull String instanceId,
                           long completedAt, int completionCount, boolean instantRewardsClaimed,
                           @NotNull Set<Integer> unclaimedItemIndices) {
        this.questId = questId;
        this.instanceId = instanceId;
        this.completedAt = completedAt;
        this.completionCount = completionCount;
        this.instantRewardsClaimed = instantRewardsClaimed;
        this.unclaimedItemIndices = new HashSet<>(unclaimedItemIndices);
    }
    
    /**
     * unclaimedItemIndices의 불변 뷰 반환
     */
    @Override
    public Set<Integer> unclaimedItemIndices() {
        return new HashSet<>(unclaimedItemIndices);
    }
    
    /**
     * 즉시 보상 수령 처리된 새 인스턴스 반환
     */
    public CompletedQuestDTO withInstantRewardsClaimed() {
        return new CompletedQuestDTO(questId, instanceId, completedAt, 
                                   completionCount, true, unclaimedItemIndices);
    }
    
    /**
     * 아이템 수령 처리된 새 인스턴스 반환
     */
    public CompletedQuestDTO withItemClaimed(int index) {
        Set<Integer> newIndices = new HashSet<>(unclaimedItemIndices);
        newIndices.remove(index);
        return new CompletedQuestDTO(questId, instanceId, completedAt,
                                   completionCount, instantRewardsClaimed, newIndices);
    }
    
    /**
     * 특정 아이템이 수령되었는지 확인
     */
    public boolean isItemClaimed(int index) {
        return !unclaimedItemIndices.contains(index);
    }
    
    /**
     * 모든 아이템이 수령되었는지 확인
     */
    public boolean areAllItemsClaimed() {
        return unclaimedItemIndices.isEmpty();
    }
    
    /**
     * 모든 보상이 수령되었는지 확인 (즉시 보상 + 아이템)
     */
    public boolean areAllRewardsClaimed() {
        return instantRewardsClaimed && areAllItemsClaimed();
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
        map.put("completionCount", completionCount);
        map.put("instantRewardsClaimed", instantRewardsClaimed);
        // Set을 List로 변환하여 저장
        map.put("unclaimedItemIndices", new ArrayList<>(unclaimedItemIndices));
        return map;
    }
    
    /**
     * Map에서 생성 (Firestore 로드용)
     */
    @SuppressWarnings("unchecked")
    public static CompletedQuestDTO fromMap(@NotNull Map<String, Object> map) {
        String questId = (String) map.getOrDefault("questId", "");
        String instanceId = (String) map.getOrDefault("instanceId", "");
        long completedAt = FirestoreUtils.getLong(map, "completedAt", System.currentTimeMillis());
        int completionCount = FirestoreUtils.getInt(map, "completionCount", 1);
        boolean instantRewardsClaimed = (Boolean) map.getOrDefault("instantRewardsClaimed", false);
        // List에서 Set으로 변환
        Set<Integer> unclaimedItemIndices = new HashSet<>();
        Object indicesObj = map.get("unclaimedItemIndices");
        if (indicesObj instanceof List) {
            List<Object> indicesList = (List<Object>) indicesObj;
            for (Object index : indicesList) {
                if (index instanceof Number) {
                    unclaimedItemIndices.add(((Number) index).intValue());
                }
            }
        }
        
        return new CompletedQuestDTO(questId, instanceId, completedAt, 
                                   completionCount, instantRewardsClaimed, 
                                   unclaimedItemIndices);
    }
}
