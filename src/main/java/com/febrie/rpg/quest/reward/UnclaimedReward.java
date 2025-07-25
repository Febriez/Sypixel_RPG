package com.febrie.rpg.quest.reward;

import com.febrie.rpg.quest.QuestID;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 미수령 보상 데이터
 * 플레이어가 부분적으로 수령한 퀘스트 보상을 추적
 * 
 * @author Febrie
 */
public class UnclaimedReward {
    
    private final UUID playerId;
    private final QuestID questId;
    private final List<ItemStack> remainingItems;
    private final long createdAt;
    private final long expiresAt;
    
    public UnclaimedReward(@NotNull UUID playerId, @NotNull QuestID questId, 
                          @NotNull List<ItemStack> remainingItems) {
        this.playerId = playerId;
        this.questId = questId;
        this.remainingItems = new ArrayList<>(remainingItems);
        this.createdAt = System.currentTimeMillis();
        this.expiresAt = createdAt + (60 * 60 * 1000); // 1시간 후
    }
    
    // 복원용 생성자
    public UnclaimedReward(@NotNull UUID playerId, @NotNull QuestID questId,
                          @NotNull List<ItemStack> remainingItems, long createdAt, long expiresAt) {
        this.playerId = playerId;
        this.questId = questId;
        this.remainingItems = new ArrayList<>(remainingItems);
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
    
    @NotNull
    public UUID getPlayerId() {
        return playerId;
    }
    
    @NotNull
    public QuestID getQuestId() {
        return questId;
    }
    
    @NotNull
    public List<ItemStack> getRemainingItems() {
        return new ArrayList<>(remainingItems);
    }
    
    public void removeItem(@NotNull ItemStack item) {
        remainingItems.removeIf(stack -> stack.isSimilar(item));
    }
    
    public boolean isEmpty() {
        return remainingItems.isEmpty();
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public long getExpiresAt() {
        return expiresAt;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
    
    public long getRemainingTime() {
        return Math.max(0, expiresAt - System.currentTimeMillis());
    }
}