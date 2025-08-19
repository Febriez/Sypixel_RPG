package com.febrie.rpg.listener;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.impl.system.MainMenuGui;
import com.febrie.rpg.gui.impl.quest.QuestListGui;
import com.febrie.rpg.gui.impl.quest.QuestDetailGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.npc.trait.RPGGuideTrait;
import com.febrie.rpg.npc.trait.RPGQuestTrait;
import com.febrie.rpg.npc.trait.RPGShopTrait;
import com.febrie.rpg.npc.trait.RPGDialogTrait;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.util.LogUtil;
import com.febrie.rpg.util.SoundUtil;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.gui.impl.quest.QuestSelectionGui;
import com.febrie.rpg.gui.impl.quest.QuestRewardGui;
import com.febrie.rpg.gui.impl.shop.NPCShopGui;
import com.febrie.rpg.npc.NPCTraitSetter;
import com.febrie.rpg.npc.trait.RPGQuestRewardTrait;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Citizens NPC 상호작용 리스너
 * 퀘스트 NPC 클릭 처리
 *
 * @author Febrie
 */
public class NPCInteractListener implements Listener {

    private final RPGMain plugin;
    private final GuiManager guiManager;
    private final QuestManager questManager;

    public NPCInteractListener(@NotNull RPGMain plugin, @NotNull GuiManager guiManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
        this.questManager = QuestManager.getInstance();
    }

    /**
     * Citizens NPC 우클릭 이벤트 처리
     */
    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getClicker();

        // 막대기를 들고 있는지 확인
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem != null && heldItem.getType() != Material.AIR) {
            // 막대기 종류 확인
            if (heldItem.getType() == Material.STICK || 
                heldItem.getType() == Material.BLAZE_ROD || 
                heldItem.getType() == Material.END_ROD) {
                
                // 대기 중인 trait 설정이 있는지 확인
                NPCTraitSetter.PendingTrait pending = 
                    NPCTraitSetter.getInstance().getPendingTrait(player);
                
                if (pending != null) {
                    // 관리자 권한 확인
                    if (!player.hasPermission("rpg.admin")) {
                        player.sendMessage(Component.text("권한이 없습니다.", UnifiedColorUtil.ERROR));
                        return;
                    }
                    
                    // trait 설정
                    switch (pending.getType()) {
                        case QUEST -> {
                            QuestID questId = (QuestID) pending.getData();
                            RPGQuestTrait questTrait = npc.getOrAddTrait(RPGQuestTrait.class);
                            questTrait.addQuest(questId);
                            
                            // 책 아이템 설정
                            npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.BOOK));
                            
                            player.sendMessage(Component.text("✓ NPC에 퀘스트가 설정되었습니다: " + questId.name(), UnifiedColorUtil.SUCCESS));
                            player.sendMessage(Component.text("NPC 이름: " + npc.getName(), UnifiedColorUtil.INFO));
                            
                            // 막대기 제거
                            heldItem.setAmount(heldItem.getAmount() - 1);
                        }
                        case REWARD -> {
                            QuestID questId = (QuestID) pending.getData();
                            RPGQuestRewardTrait rewardTrait = npc.getOrAddTrait(RPGQuestRewardTrait.class);
                            rewardTrait.addQuest(questId);
                            
                            // 에메랄드 아이템 설정
                            npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.EMERALD));
                            
                            player.sendMessage(Component.text("✓ NPC에 보상 설정이 완료되었습니다: " + questId.name(), UnifiedColorUtil.SUCCESS));
                            player.sendMessage(Component.text("NPC 이름: " + npc.getName(), UnifiedColorUtil.INFO));
                            
                            // 막대기 제거
                            heldItem.setAmount(heldItem.getAmount() - 1);
                        }
                        case OBJECTIVE -> {
                            String npcCode = (String) pending.getData();
                            RPGQuestTrait questTrait = npc.getOrAddTrait(RPGQuestTrait.class);
                            questTrait.setNpcId(npcCode);
                            
                            // 나침반 아이템 설정
                            npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.COMPASS));
                            
                            player.sendMessage(Component.text("✓ NPC에 퀘스트 목표 코드가 설정되었습니다: " + npcCode, UnifiedColorUtil.SUCCESS));
                            player.sendMessage(Component.text("NPC 이름: " + npc.getName(), UnifiedColorUtil.INFO));
                            
                            // 막대기 제거
                            heldItem.setAmount(heldItem.getAmount() - 1);
                        }
                case SHOP -> {
                    String shopType = (String) pending.getData();
                    RPGShopTrait shopTrait = npc.getOrAddTrait(RPGShopTrait.class);
                    shopTrait.setNpcType("SHOP");
                    shopTrait.setShopType(shopType);
                    
                    // 에메랄드 아이템 설정
                    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.EMERALD));
                    
                    player.sendMessage(Component.text("NPC에 상점이 설정되었습니다: " + shopType, UnifiedColorUtil.SUCCESS));
                }
                case GUIDE -> {
                    String guideType = (String) pending.getData();
                    RPGGuideTrait guideTrait = npc.getOrAddTrait(RPGGuideTrait.class);
                    guideTrait.setNpcType("GUIDE");
                    guideTrait.setGuideType(guideType);
                    
                    // 나침반 아이템 설정
                    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.COMPASS));
                    
                    player.sendMessage(Component.text("NPC에 가이드가 설정되었습니다: " + guideType, UnifiedColorUtil.SUCCESS));
                }
                case DIALOG -> {
                    String dialogId = (String) pending.getData();
                    RPGDialogTrait dialogTrait = npc.getOrAddTrait(RPGDialogTrait.class);
                    dialogTrait.setDialogId(dialogId);
                    
                    // 말풍선 아이템 설정
                    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.PAPER));
                    
                    player.sendMessage(Component.text("NPC에 대화가 설정되었습니다.", UnifiedColorUtil.SUCCESS));
                    player.sendMessage(Component.text("대화 ID: " + dialogId, UnifiedColorUtil.INFO));
                }
            }
            
            // 대기 중인 trait 제거
            NPCTraitSetter.getInstance().removePendingTrait(player);
            SoundUtil.playSuccessSound(player);
            return;
                }
            }
        }
        
        // 보상 처리를 먼저 확인 - NPC가 보상을 가지고 있고 플레이어가 받을 수 있는 경우
        if (npc.hasTrait(RPGQuestRewardTrait.class)) {
            RPGQuestRewardTrait rewardTrait = npc.getOrAddTrait(RPGQuestRewardTrait.class);
            
            // 보상 수령 가능한 퀘스트가 있는지 확인
            List<String> unclaimedQuestInstances = questManager.getUnclaimedRewardQuests(player.getUniqueId());
            boolean hasRewardsToClaimFromThisNPC = false;
            
            if (!unclaimedQuestInstances.isEmpty()) {
                // 이 NPC가 특정 퀘스트만 담당하는 경우
                if (!rewardTrait.getQuestIds().isEmpty()) {
                    List<QuestID> npcQuests = rewardTrait.getQuestIds();
                    // instanceId로부터 questId 추출하여 비교
                    hasRewardsToClaimFromThisNPC = unclaimedQuestInstances.stream()
                            .anyMatch(instanceId -> {
                                var completed = questManager.getCompletedQuestData(player.getUniqueId(), instanceId);
                                if (completed != null) {
                                    QuestID questId = QuestID.valueOf(completed.questId());
                                    return npcQuests.contains(questId);
                                }
                                return false;
                            });
                } else {
                    // 모든 퀘스트 보상을 담당하는 경우
                    hasRewardsToClaimFromThisNPC = true;
                }
            }
            
            // 받을 보상이 있으면 보상 처리를 우선적으로 수행
            if (hasRewardsToClaimFromThisNPC) {
                handleQuestRewardNPCWithTrait(npc, player, rewardTrait);
                return;
            }
        }
        
        if (npc.hasTrait(RPGQuestTrait.class)) {
            RPGQuestTrait questTrait = npc.getOrAddTrait(RPGQuestTrait.class);
            handleQuestNPCWithTrait(npc, player, questTrait, event);
            return;
        }

        if (npc.hasTrait(RPGShopTrait.class)) {
            RPGShopTrait shopTrait = npc.getOrAddTrait(RPGShopTrait.class);
            handleShopNPCWithTrait(npc, player, shopTrait);
            return;
        }

        if (npc.hasTrait(RPGGuideTrait.class)) {
            RPGGuideTrait guideTrait = npc.getOrAddTrait(RPGGuideTrait.class);
            handleGuideNPCWithTrait(npc, player, guideTrait);
            return;
        }
        
        // 아무 trait도 없거나 dialog trait만 있는 경우
        if (npc.hasTrait(RPGDialogTrait.class)) {
            RPGDialogTrait dialogTrait = npc.getOrAddTrait(RPGDialogTrait.class);
            dialogTrait.onInteract(player);
        }
    }

    /**
     * Trait를 사용하는 퀘스트 NPC 처리
     */
    private void handleQuestNPCWithTrait(NPC npc, Player player, RPGQuestTrait trait, NPCRightClickEvent event) {
        // 먼저 NPC ID 기반 퀘스트 목표 체크
        if (trait.hasNpcId()) {
            String npcId = trait.getNpcId();
            
            // InteractNPC 목표 처리 (기존 로직)
            questManager.handleNPCInteraction(player, npcId);
            
            
            // 퀘스트 목표 처리가 완료되면 다른 처리를 하지 않음
            return;
        }
        
        List<QuestID> questIds = trait.getQuestIds();
        
        if (questIds.isEmpty()) {
            // 퀘스트 목록 GUI 열기
            QuestListGui questListGui = 
                QuestListGui.create(guiManager, player);
            guiManager.openGui(player, questListGui);
            return;
        }

        // 진행 중인 퀘스트가 있는지 먼저 확인
        List<QuestID> activeQuestIds = new ArrayList<>();
        for (QuestID questId : questIds) {
            boolean hasActiveQuest = questManager.getActiveQuests(player.getUniqueId()).values().stream()
                    .anyMatch(p -> p.questId().equals(questId.name()));
            if (hasActiveQuest) {
                activeQuestIds.add(questId);
            }
        }
        
        // 진행 중인 퀘스트가 있으면 아무 동작도 하지 않음
        if (!activeQuestIds.isEmpty()) {
            // 진행 중인 퀘스트가 있으면 이벤트 취소하고 리턴 (dialog trait가 작동하지 않도록)
            event.setCancelled(true);
            return;
        }

        // 단일 퀘스트인 경우 직접 처리
        if (questIds.size() == 1) {
            QuestID questId = questIds.get(0);
            Quest quest = questManager.getQuest(questId);
            if (quest == null) {
                player.sendMessage(Component.translatable("quest.npc.invalid-quest"));
                return;
            }
            handleSingleQuest(npc, player, quest, event);
        } else {
            // 여러 퀘스트가 있는 경우 선택 GUI 표시
            List<Quest> quests = new ArrayList<>();
            for (QuestID questId : questIds) {
                Quest quest = questManager.getQuest(questId);
                if (quest != null) {
                    quests.add(quest);
                }
            }
            QuestSelectionGui questSelectionGui = QuestSelectionGui.create(guiManager, player, quests, npc.getName());
            guiManager.openGui(player, questSelectionGui);
        }

    }
    
    /**
     * 단일 퀘스트 처리
     */
    private void handleSingleQuest(NPC npc, Player player, Quest quest, NPCRightClickEvent event) {
        QuestID questId = quest.getId();
        
        // 이미 퀘스트를 진행 중인지 확인
        boolean hasActiveQuest = questManager.getActiveQuests(player.getUniqueId()).values().stream()
                .anyMatch(p -> p.questId().equals(questId.name()));

        if (hasActiveQuest) {
            // 진행 중인 퀘스트는 이벤트 취소하고 리턴 (dialog trait가 작동하지 않도록)
            event.setCancelled(true); 
            return;
        }

        // 완료 제한 확인 (QuestManager.startQuest와 동일한 로직)
        boolean hasCompleted = questManager.getCompletedQuests(player.getUniqueId()).values().stream()
                .anyMatch(c -> c.questId().equals(questId.name()));
        
        if (hasCompleted) {
            // 먼저 보상을 받지 않은 경우 확인
            // 미수령 보상 확인
            List<String> unclaimedInstances = questManager.getUnclaimedRewardQuests(player.getUniqueId());
            boolean hasUnclaimedReward = unclaimedInstances.stream()
                .anyMatch(instanceId -> {
                    var completed = questManager.getCompletedQuestData(player.getUniqueId(), instanceId);
                    return completed != null && completed.questId().equals(questId.name());
                });
            
            if (hasUnclaimedReward) {
                player.sendMessage(Component.text("이 퀘스트는 완료했습니다. 보상 NPC를 찾아가세요.", UnifiedColorUtil.INFO));
                return;
            }
            
            // 완료 제한 확인
            int completionLimit = quest.getCompletionLimit();
            
            // 완료 불가 퀘스트
            if (completionLimit == 0) {
                player.sendMessage(Component.translatable("quest.npc.already-completed"));
                return;
            }
            
            // 완료 횟수 제한 확인 (-1은 무제한)
            if (completionLimit > 0) {
                // 실제로 startQuest를 호출하면 내부에서 체크하므로 여기서는 간단한 메시지만 표시
                player.sendMessage(Component.translatable("quest.npc.already-completed"));
                return;
            }
        }
        
        // 퀘스트 요구사항 확인
        RPGPlayer rpgPlayer = plugin.getRPGPlayerManager().getOrCreatePlayer(player);
        
        // 레벨 요구사항 확인
        if (quest.getMinLevel() > 1 && rpgPlayer.getLevel() < quest.getMinLevel()) {
            player.sendMessage(Component.translatable("quest.npc.level-requirement", Component.text(String.valueOf(quest.getMinLevel()))));
            return;
        }
        

        // 퀘스트 대화 GUI 열기 (대화가 없어도 수락/거절 선택 표시)
        guiManager.openQuestDialogGui(player, quest);
    }

    /**
     * Trait를 사용하는 상점 NPC 처리
     */
    private void handleShopNPCWithTrait(NPC npc, Player player, RPGShopTrait trait) {
        // 상점 GUI 열기
        NPCShopGui shopGui = NPCShopGui.create(guiManager, player, trait, npc.getName());
        guiManager.openGui(player, shopGui);
    }

    /**
     * Trait를 사용하는 가이드 NPC 처리
     */
    private void handleGuideNPCWithTrait(NPC npc, Player player, RPGGuideTrait trait) {
        // 메인 메뉴 열기
        MainMenuGui mainMenu = 
            MainMenuGui.create(guiManager, player);
        guiManager.openGui(player, mainMenu);
        SoundUtil.playOpenSound(player);
    }
    
    /**
     * Trait를 사용하는 퀘스트 보상 NPC 처리
     */
    private void handleQuestRewardNPCWithTrait(NPC npc, Player player, RPGQuestRewardTrait trait) {
        // 보상 수령 가능한 퀘스트 인스턴스 목록 가져오기
        List<String> unclaimedInstances = questManager.getUnclaimedRewardQuests(player.getUniqueId());
        
        // 퀘스트 ID로 변환
        Set<QuestID> availableQuests = new HashSet<>();
        for (String instanceId : unclaimedInstances) {
            var completed = questManager.getCompletedQuestData(player.getUniqueId(), instanceId);
            if (completed != null) {
                try {
                    QuestID questId = QuestID.valueOf(completed.questId());
                    availableQuests.add(questId);
                } catch (IllegalArgumentException ignored) {}
            }
        }
        
        // 특정 퀘스트 ID가 설정된 경우
        if (!trait.getQuestIds().isEmpty()) {
            List<QuestID> npcQuests = trait.getQuestIds();
            availableQuests.retainAll(npcQuests); // NPC가 담당하는 퀘스트만 필터링
        }
        
        // 보상 수령 가능한 퀘스트가 없는 경우
        if (availableQuests.isEmpty()) {
            player.sendMessage(Component.translatable("quest.reward.no-rewards"));
            SoundUtil.playErrorSound(player);
            return;
        }
        
        // 보상 수령 가능한 퀘스트가 1개인 경우 바로 보상 GUI 열기
        if (availableQuests.size() == 1) {
            QuestID questId = availableQuests.iterator().next();
            Quest quest = questManager.getQuest(questId);
            if (quest != null) {
                // 해당 퀘스트의 첫 번째 인스턴스 찾기
                String instanceId = unclaimedInstances.stream()
                    .filter(id -> {
                        var completed = questManager.getCompletedQuestData(player.getUniqueId(), id);
                        return completed != null && completed.questId().equals(questId.name());
                    })
                    .findFirst()
                    .orElse(null);
                    
                if (instanceId != null) {
                    QuestRewardGui rewardGui = 
                        QuestRewardGui.create(guiManager, player, quest, instanceId);
                    guiManager.openGui(player, rewardGui);
                    SoundUtil.playOpenSound(player);
                }
            }
        } else {
            // 여러 개인 경우 선택 GUI 표시
            List<Quest> questsWithRewards = new ArrayList<>();
            for (QuestID questId : availableQuests) {
                Quest quest = questManager.getQuest(questId);
                if (quest != null) {
                    questsWithRewards.add(quest);
                }
            }
            
            if (!questsWithRewards.isEmpty()) {
                QuestSelectionGui rewardSelectionGui = QuestSelectionGui.create(guiManager, player, questsWithRewards, npc.getName());
                guiManager.openGui(player, rewardSelectionGui);
            }
        }
    }
    
    
}