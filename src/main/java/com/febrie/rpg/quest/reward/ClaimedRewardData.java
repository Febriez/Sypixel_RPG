package com.febrie.rpg.quest.reward;

import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 퀘스트 보상 수령 상태를 추적하는 데이터 클래스
 * 
 * @author Febrie
 */
public class ClaimedRewardData {
    
    private final UUID playerId;
    private final QuestID questId;
    private boolean instantRewardsClaimed; // 경험치, 돈 등 즉시 지급 보상
    private final Map<String, Boolean> itemClaimStatus; // 아이템별 수령 여부
    private final Map<String, Long> claimTimestamps; // 수령 시간 기록
    private final long createdAt;
    
    /**
     * 새로운 보상 수령 데이터 생성
     */
    public ClaimedRewardData(@NotNull UUID playerId, @NotNull QuestID questId) {
        this.playerId = playerId;
        this.questId = questId;
        this.itemClaimStatus = new HashMap<>();
        this.claimTimestamps = new HashMap<>();
        this.createdAt = System.currentTimeMillis();
        this.instantRewardsClaimed = false;
    }
    
    /**
     * Map에서 복원용 생성자
     */
    private ClaimedRewardData(@NotNull UUID playerId, @NotNull QuestID questId,
                           boolean instantRewardsClaimed,
                           @NotNull Map<String, Boolean> itemClaimStatus,
                           @NotNull Map<String, Long> claimTimestamps,
                           long createdAt) {
        this.playerId = playerId;
        this.questId = questId;
        this.instantRewardsClaimed = instantRewardsClaimed;
        this.itemClaimStatus = new HashMap<>(itemClaimStatus);
        this.claimTimestamps = new HashMap<>(claimTimestamps);
        this.createdAt = createdAt;
    }
    
    /**
     * 즉시 보상 수령 처리
     */
    public void markInstantRewardsClaimed() {
        this.instantRewardsClaimed = true;
    }
    
    /**
     * 즉시 보상 수령 여부 확인
     */
    public boolean isInstantRewardsClaimed() {
        return instantRewardsClaimed;
    }
    
    /**
     * 특정 인덱스의 아이템 수령 처리
     */
    public void markItemClaimed(int index) {
        itemClaimStatus.put(String.valueOf(index), true);
        claimTimestamps.put(String.valueOf(index), System.currentTimeMillis());
    }
    
    /**
     * 특정 인덱스의 아이템 수령 여부 확인
     */
    public boolean isItemClaimed(int index) {
        return itemClaimStatus.getOrDefault(String.valueOf(index), false);
    }
    
    /**
     * 수령하지 않은 아이템 인덱스 목록 반환
     */
    @NotNull
    public List<Integer> getUnclaimedItemIndices(int totalItems) {
        List<Integer> unclaimed = new ArrayList<>();
        for (int i = 0; i < totalItems; i++) {
            if (!isItemClaimed(i)) {
                unclaimed.add(i);
            }
        }
        return unclaimed;
    }
    
    /**
     * 모든 아이템이 수령되었는지 확인
     */
    public boolean areAllItemsClaimed(int totalItems) {
        for (int i = 0; i < totalItems; i++) {
            if (!isItemClaimed(i)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 전체 보상이 수령되었는지 확인 (즉시 보상 + 모든 아이템)
     */
    public boolean isFullyClaimed(int totalItems) {
        return instantRewardsClaimed && areAllItemsClaimed(totalItems);
    }
    
    /**
     * 수령한 아이템 개수 반환
     */
    public int getClaimedItemCount() {
        return (int) itemClaimStatus.values().stream()
                .filter(claimed -> claimed)
                .count();
    }
    
    // Getters
    @NotNull
    public UUID getPlayerId() {
        return playerId;
    }
    
    @NotNull
    public QuestID getQuestId() {
        return questId;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Map으로 변환
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("playerId", playerId.toString());
        map.put("questId", questId.name());
        map.put("instantRewardsClaimed", instantRewardsClaimed);
        map.put("itemClaimStatus", new HashMap<>(itemClaimStatus));
        map.put("claimTimestamps", new HashMap<>(claimTimestamps));
        map.put("createdAt", createdAt);
        return map;
    }
    
    /**
     * Map에서 생성
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static ClaimedRewardData fromMap(@NotNull Map<String, Object> map) {
        UUID playerId = UUID.fromString((String) map.get("playerId"));
        QuestID questId = QuestID.valueOf((String) map.get("questId"));
        boolean instantRewardsClaimed = (Boolean) map.getOrDefault("instantRewardsClaimed", false);
        
        // 아이템 수령 상태
        Map<String, Boolean> itemClaimStatus = new HashMap<>();
        Object itemClaimObj = map.get("itemClaimStatus");
        if (itemClaimObj instanceof Map) {
            Map<String, Object> itemClaimMap = (Map<String, Object>) itemClaimObj;
            itemClaimMap.forEach((key, value) -> {
                itemClaimStatus.put(key, (Boolean) value);
            });
        }
        
        // 수령 시간
        Map<String, Long> claimTimestamps = new HashMap<>();
        Object timestampObj = map.get("claimTimestamps");
        if (timestampObj instanceof Map) {
            Map<String, Object> timestampMap = (Map<String, Object>) timestampObj;
            timestampMap.forEach((key, value) -> {
                claimTimestamps.put(key, ((Number) value).longValue());
            });
        }
        
        long createdAt = FirestoreUtils.getLong(map, "createdAt", System.currentTimeMillis());
        
        return new ClaimedRewardData(playerId, questId, instantRewardsClaimed, 
                                   itemClaimStatus, claimTimestamps, createdAt);
    }
}