package com.febrie.rpg.npc;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.util.UnifiedColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

/**
 * NPC Trait 설정 대기 관리자
 * 플레이어가 NPC에 trait를 설정할 준비를 하고 10초 타임아웃을 관리
 *
 * @author Febrie
 */
public class NPCTraitSetter {
    
    private static NPCTraitSetter instance;
    
    private final RPGMain plugin;
    private final Map<UUID, PendingTrait> pendingTraits = new HashMap<>();
    
    public NPCTraitSetter(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        instance = this;
    }
    
    public static NPCTraitSetter getInstance() {
        return instance;
    }
    
    /**
     * 플레이어가 퀘스트 trait를 설정할 준비를 함 (막대기 방식)
     */
    public void setPendingQuestTrait(@NotNull Player player, @NotNull QuestID questId) {
        cancelPending(player);
        
        BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pendingTraits.remove(player.getUniqueId()) != null) {
                player.sendMessage(Component.text("NPC 설정 시간이 초과되었습니다.", UnifiedColorUtil.ERROR));
            }
        }, 600L); // 30초
        
        PendingTrait pending = new PendingTrait(TraitType.QUEST, questId, timeoutTask);
        pendingTraits.put(player.getUniqueId(), pending);
    }
    
    /**
     * 플레이어가 보상 trait를 설정할 준비를 함 (막대기 방식)
     */
    public void setPendingRewardTrait(@NotNull Player player, @NotNull QuestID questId) {
        cancelPending(player);
        
        BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pendingTraits.remove(player.getUniqueId()) != null) {
                player.sendMessage(Component.text("NPC 설정 시간이 초과되었습니다.", UnifiedColorUtil.ERROR));
            }
        }, 600L); // 30초
        
        PendingTrait pending = new PendingTrait(TraitType.REWARD, questId, timeoutTask);
        pendingTraits.put(player.getUniqueId(), pending);
    }
    
    /**
     * 플레이어가 목표 NPC trait를 설정할 준비를 함 (막대기 방식)
     */
    public void setPendingObjectiveTrait(@NotNull Player player, @NotNull String npcCode) {
        cancelPending(player);
        
        BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pendingTraits.remove(player.getUniqueId()) != null) {
                player.sendMessage(Component.text("NPC 설정 시간이 초과되었습니다.", UnifiedColorUtil.ERROR));
            }
        }, 600L); // 30초
        
        PendingTrait pending = new PendingTrait(TraitType.OBJECTIVE, npcCode, timeoutTask);
        pendingTraits.put(player.getUniqueId(), pending);
    }
    
    /**
     * 플레이어가 퀘스트 trait를 설정할 준비를 함
     */
    public void prepareQuestTrait(@NotNull Player player, @NotNull QuestID questId) {
        cancelPending(player);
        
        // 퀘스트 확인
        com.febrie.rpg.quest.Quest quest = com.febrie.rpg.quest.manager.QuestManager.getInstance().getQuest(questId);
        if (quest == null) {
            player.sendMessage(Component.text("구현되지 않은 퀘스트입니다: " + questId.name(), UnifiedColorUtil.ERROR));
            return;
        }
        
        // 새로운 대기 설정
        BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pendingTraits.remove(player.getUniqueId()) != null) {
                player.sendMessage(Component.text("NPC 설정 시간이 초과되었습니다.", UnifiedColorUtil.ERROR));
            }
        }, 200L); // 10초 (20틱 * 10)
        
        PendingTrait pending = new PendingTrait(TraitType.QUEST, questId, timeoutTask);
        pendingTraits.put(player.getUniqueId(), pending);
        
        // 퀘스트 Trait 등록 아이템 지급 (OP나 admin 권한 있는 경우)
        if (player.hasPermission("sypixelrpg.admin") || player.isOp()) {
            // 퀘스트에서 필요한 NPC ID 추출
            Set<String> requiredNpcIds = new HashSet<>();
            for (com.febrie.rpg.quest.objective.QuestObjective objective : quest.getObjectives()) {
                if (objective instanceof com.febrie.rpg.quest.objective.impl.InteractNPCObjective interactObj) {
                    String npcId = interactObj.getNpcId();
                    if (npcId != null && !npcId.isEmpty()) {
                        requiredNpcIds.add(npcId);
                    }
                }
            }
            
            if (requiredNpcIds.isEmpty()) {
                player.sendMessage(Component.text("이 퀘스트는 NPC 상호작용 목표가 없습니다.", UnifiedColorUtil.WARNING));
            } else {
                // 각 NPC ID별로 개별 아이템 생성
                for (String npcId : requiredNpcIds) {
                    ItemStack item = com.febrie.rpg.quest.trait.QuestTraitRegistrationItem.create(
                        npcId, 
                        quest.getDisplayName(player) + " - " + npcId
                    );
                    player.getInventory().addItem(item);
                }
                player.sendMessage(Component.text(requiredNpcIds.size() + "개의 NPC Trait 등록 아이템이 지급되었습니다.", UnifiedColorUtil.SUCCESS));
                player.sendMessage(Component.text("NPC ID: " + String.join(", ", requiredNpcIds), UnifiedColorUtil.GRAY));
            }
            
            // 기본 보상 NPC 등록 아이템도 자동 지급
            String rewardNpcId = "reward_" + questId.name().toLowerCase();
            ItemStack rewardItem = com.febrie.rpg.quest.trait.RewardTraitRegistrationItem.create(
                rewardNpcId,
                quest.getDisplayName(player) + " 보상 NPC"
            );
            player.getInventory().addItem(rewardItem);
            player.sendMessage(Component.text("보상 NPC 등록 아이템도 지급되었습니다.", UnifiedColorUtil.SUCCESS));
            player.sendMessage(Component.text("보상 NPC ID: " + rewardNpcId, UnifiedColorUtil.GRAY));
        }
        
        player.sendMessage(Component.text("10초 내에 설정할 NPC를 우클릭하세요.", UnifiedColorUtil.INFO));
        player.sendMessage(Component.text("퀘스트: " + questId.name() + " - " + quest.getDisplayName(player), UnifiedColorUtil.YELLOW));
    }
    
    /**
     * 플레이어가 상점 trait를 설정할 준비를 함
     */
    public void prepareShopTrait(@NotNull Player player, @NotNull String shopType) {
        cancelPending(player);
        
        // 새로운 대기 설정
        BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pendingTraits.remove(player.getUniqueId()) != null) {
                player.sendMessage(Component.text("NPC 설정 시간이 초과되었습니다.", UnifiedColorUtil.ERROR));
            }
        }, 200L); // 10초
        
        PendingTrait pending = new PendingTrait(TraitType.SHOP, shopType, timeoutTask);
        pendingTraits.put(player.getUniqueId(), pending);
        
        player.sendMessage(Component.text("10초 내에 설정할 NPC를 우클릭하세요.", UnifiedColorUtil.INFO));
        player.sendMessage(Component.text("상점 타입: " + shopType, UnifiedColorUtil.YELLOW));
    }
    
    /**
     * 플레이어가 가이드 trait를 설정할 준비를 함
     */
    public void prepareGuideTrait(@NotNull Player player, @NotNull String guideType) {
        cancelPending(player);
        
        // 새로운 대기 설정
        BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pendingTraits.remove(player.getUniqueId()) != null) {
                player.sendMessage(Component.text("NPC 설정 시간이 초과되었습니다.", UnifiedColorUtil.ERROR));
            }
        }, 200L); // 10초
        
        PendingTrait pending = new PendingTrait(TraitType.GUIDE, guideType, timeoutTask);
        pendingTraits.put(player.getUniqueId(), pending);
        
        player.sendMessage(Component.text("10초 내에 설정할 NPC를 우클릭하세요.", UnifiedColorUtil.INFO));
        player.sendMessage(Component.text("가이드 타입: " + guideType, UnifiedColorUtil.YELLOW));
    }
    
    /**
     * 플레이어가 대화 trait를 설정할 준비를 함
     */
    public void prepareDialogTrait(@NotNull Player player, @NotNull List<String> dialogues) {
        cancelPending(player);
        
        // 새로운 대기 설정
        BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pendingTraits.remove(player.getUniqueId()) != null) {
                player.sendMessage(Component.text("NPC 설정 시간이 초과되었습니다.", UnifiedColorUtil.ERROR));
            }
        }, 200L); // 10초
        
        PendingTrait pending = new PendingTrait(TraitType.DIALOG, dialogues, timeoutTask);
        pendingTraits.put(player.getUniqueId(), pending);
        
        player.sendMessage(Component.text("10초 내에 설정할 NPC를 우클릭하세요.", UnifiedColorUtil.INFO));
        player.sendMessage(Component.text("대사 개수: " + dialogues.size() + "개", UnifiedColorUtil.YELLOW));
    }
    
    /**
     * 플레이어의 대기 중인 trait 가져오기
     */
    @Nullable
    public PendingTrait getPendingTrait(@NotNull Player player) {
        return pendingTraits.get(player.getUniqueId());
    }
    
    /**
     * 플레이어의 대기 중인 trait 제거
     */
    public void removePendingTrait(@NotNull Player player) {
        PendingTrait pending = pendingTraits.remove(player.getUniqueId());
        if (pending != null) {
            pending.cancelTimeout();
        }
    }
    
    /**
     * 대기 중인 trait 취소
     */
    public void cancelPending(@NotNull Player player) {
        PendingTrait pending = pendingTraits.remove(player.getUniqueId());
        if (pending != null) {
            pending.cancelTimeout();
        }
    }
    
    /**
     * 모든 대기 중인 trait 정리
     */
    public void cleanup() {
        pendingTraits.values().forEach(PendingTrait::cancelTimeout);
        pendingTraits.clear();
    }
    
    /**
     * Trait 타입
     */
    public enum TraitType {
        QUEST, SHOP, GUIDE, REWARD, DIALOG, OBJECTIVE
    }
    
    /**
     * 대기 중인 trait 정보
     */
    public static class PendingTrait {
        private final TraitType type;
        private final Object data;
        private final BukkitTask timeoutTask;
        
        public PendingTrait(@NotNull TraitType type, @NotNull Object data, @NotNull BukkitTask timeoutTask) {
            this.type = type;
            this.data = data;
            this.timeoutTask = timeoutTask;
        }
        
        public TraitType getType() {
            return type;
        }
        
        public Object getData() {
            return data;
        }
        
        public void cancelTimeout() {
            if (timeoutTask != null && !timeoutTask.isCancelled()) {
                timeoutTask.cancel();
            }
        }
    }
}