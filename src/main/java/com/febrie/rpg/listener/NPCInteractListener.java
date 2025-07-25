package com.febrie.rpg.listener;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.impl.system.MainMenuGui;
import com.febrie.rpg.gui.impl.quest.QuestListGui;
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
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LogUtil;
import com.febrie.rpg.util.SoundUtil;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.gui.impl.quest.QuestSelectionGui;
import com.febrie.rpg.gui.impl.quest.QuestRewardGui;
import com.febrie.rpg.gui.impl.shop.NPCShopGui;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Citizens NPC 상호작용 리스너
 * 퀘스트 NPC 클릭 처리
 *
 * @author Febrie
 */
public class NPCInteractListener implements Listener {

    private final RPGMain plugin;
    private final GuiManager guiManager;
    private final LangManager langManager;
    private final QuestManager questManager;

    public NPCInteractListener(@NotNull RPGMain plugin, @NotNull GuiManager guiManager,
                               @NotNull LangManager langManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.questManager = QuestManager.getInstance();
    }

    /**
     * Citizens NPC 우클릭 이벤트 처리
     */
    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getClicker();

        
        // 대기 중인 trait 설정이 있는지 확인
        com.febrie.rpg.npc.NPCTraitSetter.PendingTrait pending = 
            com.febrie.rpg.npc.NPCTraitSetter.getInstance().getPendingTrait(player);
        
        if (pending != null) {
            // 관리자 권한 확인
            if (!player.hasPermission("rpg.admin")) {
                player.sendMessage(Component.text("권한이 없습니다.", ColorUtil.ERROR));
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
                    
                    player.sendMessage(Component.text("NPC에 퀘스트가 설정되었습니다: " + questId.name(), ColorUtil.SUCCESS));
                    player.sendMessage(Component.text("NPC 이름: " + npc.getName(), ColorUtil.INFO));
                }
                case SHOP -> {
                    String shopType = (String) pending.getData();
                    RPGShopTrait shopTrait = npc.getOrAddTrait(RPGShopTrait.class);
                    shopTrait.setNpcType("SHOP");
                    shopTrait.setShopType(shopType);
                    
                    // 에메랄드 아이템 설정
                    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.EMERALD));
                    
                    player.sendMessage(Component.text("NPC에 상점이 설정되었습니다: " + shopType, ColorUtil.SUCCESS));
                }
                case GUIDE -> {
                    String guideType = (String) pending.getData();
                    RPGGuideTrait guideTrait = npc.getOrAddTrait(RPGGuideTrait.class);
                    guideTrait.setNpcType("GUIDE");
                    guideTrait.setGuideType(guideType);
                    
                    // 나침반 아이템 설정
                    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.COMPASS));
                    
                    player.sendMessage(Component.text("NPC에 가이드가 설정되었습니다: " + guideType, ColorUtil.SUCCESS));
                }
                case DIALOG -> {
                    @SuppressWarnings("unchecked")
                    List<String> dialogues = (List<String>) pending.getData();
                    RPGDialogTrait dialogTrait = npc.getOrAddTrait(RPGDialogTrait.class);
                    
                    // 대사 추가
                    for (String dialogue : dialogues) {
                        dialogTrait.addDialogue(dialogue);
                    }
                    
                    // 말풍선 아이템 설정
                    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.PAPER));
                    
                    player.sendMessage(Component.text("NPC에 대화가 설정되었습니다.", ColorUtil.SUCCESS));
                    player.sendMessage(Component.text("대사 개수: " + dialogues.size() + "개", ColorUtil.INFO));
                }
            }
            
            // 대기 중인 trait 제거
            com.febrie.rpg.npc.NPCTraitSetter.getInstance().removePendingTrait(player);
            SoundUtil.playSuccessSound(player);
            return;
        }

        // 보상 처리를 먼저 확인 - NPC가 보상을 가지고 있고 플레이어가 받을 수 있는 경우
        if (npc.hasTrait(com.febrie.rpg.npc.trait.RPGQuestRewardTrait.class)) {
            com.febrie.rpg.npc.trait.RPGQuestRewardTrait rewardTrait = npc.getOrAddTrait(com.febrie.rpg.npc.trait.RPGQuestRewardTrait.class);
            
            // 보상 수령 가능한 퀘스트가 있는지 확인
            List<QuestID> unclaimedQuests = questManager.getUnclaimedRewardQuests(player.getUniqueId());
            boolean hasRewardsToClaimFromThisNPC = false;
            
            if (!unclaimedQuests.isEmpty()) {
                // 이 NPC가 특정 퀘스트만 담당하는 경우
                if (!rewardTrait.getQuestIds().isEmpty()) {
                    List<QuestID> npcQuests = rewardTrait.getQuestIds();
                    hasRewardsToClaimFromThisNPC = unclaimedQuests.stream()
                            .anyMatch(npcQuests::contains);
                } else {
                    // 모든 퀘스트 보상을 담당하는 경우
                    hasRewardsToClaimFromThisNPC = true;
                }
            }
            
            // 받을 보상이 있으면 보상 처리를 우선적으로 수행
            if (hasRewardsToClaimFromThisNPC) {
                rewardTrait.onInteract(player);
                handleQuestRewardNPCWithTrait(npc, player, rewardTrait);
                return;
            }
        }
        
        // 기존 trait 처리 - 퀘스트를 먼저 처리
        if (npc.hasTrait(RPGQuestTrait.class)) {
            RPGQuestTrait questTrait = npc.getOrAddTrait(RPGQuestTrait.class);
            questTrait.onInteract(player);
            
            
            handleQuestNPCWithTrait(npc, player, questTrait);
            return;
        }

        if (npc.hasTrait(RPGShopTrait.class)) {
            RPGShopTrait shopTrait = npc.getOrAddTrait(RPGShopTrait.class);
            shopTrait.onInteract(player);
            handleShopNPCWithTrait(npc, player, shopTrait);
            return;
        }

        if (npc.hasTrait(RPGGuideTrait.class)) {
            RPGGuideTrait guideTrait = npc.getOrAddTrait(RPGGuideTrait.class);
            guideTrait.onInteract(player);
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
    private void handleQuestNPCWithTrait(NPC npc, Player player, RPGQuestTrait trait) {
        // 먼저 NPC ID 기반 퀘스트 목표 체크
        if (trait.hasNpcId()) {
            // 현재 진행 중인 퀘스트에서 이 NPC와 관련된 목표 찾기
            List<QuestProgress> activeQuests = questManager.getActiveQuests(player.getUniqueId());
            
            for (QuestProgress progress : activeQuests) {
                Quest quest = questManager.getQuest(progress.getQuestId());
                if (quest == null) continue;
                
                
                // 순차 진행인 경우 현재 목표만, 자유 진행인 경우 모든 미완료 목표 확인
                List<QuestObjective> objectivesToCheck = new ArrayList<>();
                
                if (quest.isSequential()) {
                    // 순차 진행 - 현재 목표만
                    int currentObjectiveIndex = progress.getCurrentObjectiveIndex();
                    if (currentObjectiveIndex < quest.getObjectives().size()) {
                        QuestObjective currentObj = quest.getObjectives().get(currentObjectiveIndex);
                        objectivesToCheck.add(currentObj);
                    }
                } else {
                    // 자유 진행 - 모든 미완료 목표
                    for (QuestObjective objective : quest.getObjectives()) {
                        ObjectiveProgress objProgress = progress.getObjective(objective.getId());
                        if (objProgress != null && !objProgress.isCompleted()) {
                            objectivesToCheck.add(objective);
                        }
                    }
                }
                
                
                // 각 목표 확인
                for (QuestObjective objective : objectivesToCheck) {
                    if (objective instanceof InteractNPCObjective interactObjective) {
                        String npcId = interactObjective.getNpcId();
                        
                        if (npcId != null && npcId.equals(trait.getNpcId())) {
                            // 퀘스트 목표 진행을 위해 원본 이벤트를 생성하여 전달
                            // NPCRightClickEvent를 PlayerInteractEntityEvent로 전환할 수 없으므로
                            // QuestManager에서 직접 처리하도록 수정
                            ObjectiveProgress objProgress = progress.getObjective(objective.getId());
                            if (objProgress != null && !objProgress.isCompleted()) {
                                // 목표 진행도 증가
                                objProgress.increment(1);
                                
                                // 목표 완료 체크
                                if (objProgress.isCompleted()) {
                                    // 목표 완료 알림
                                    player.sendMessage(Component.text("✓ ", ColorUtil.SUCCESS)
                                            .append(Component.text(objective.getStatusInfo(objProgress), ColorUtil.SUCCESS)));
                                    SoundUtil.playSuccessSound(player);
                                    
                                    // 순차 진행인 경우 다음 목표로
                                    if (quest.isSequential()) {
                                        progress.setCurrentObjectiveIndex(progress.getCurrentObjectiveIndex() + 1);
                                    }
                                    
                                    // 퀘스트 완료 체크
                                    questManager.checkQuestCompletion(player.getUniqueId(), progress.getQuestId());
                                } else {
                                    // 진행도 알림
                                    player.sendMessage(Component.text("퀘스트 진행: ", ColorUtil.INFO)
                                            .append(Component.text(objective.getStatusInfo(objProgress), ColorUtil.YELLOW)));
                                    SoundUtil.playClickSound(player);
                                }
                                
                                // 데이터 저장 예약
                                questManager.markForSave(player.getUniqueId());
                                
                                return;
                            }
                        }
                    }
                }
            }
            
            // 퀘스트 목표가 아닌 경우 아무 동작도 하지 않음
            return;
        }
        
        // 기존 퀘스트 ID 기반 처리 (일반 퀘스트 NPC)
        List<QuestID> questIds = trait.getQuestIds();
        
        if (questIds.isEmpty()) {
            // 퀘스트 목록 GUI 열기
            QuestListGui questListGui = 
                QuestListGui.create(guiManager, langManager, player);
            guiManager.openGui(player, questListGui);
            return;
        }

        // 단일 퀘스트인 경우 직접 처리
        if (questIds.size() == 1) {
            QuestID questId = questIds.get(0);
            Quest quest = questManager.getQuest(questId);
            if (quest == null) {
                langManager.sendMessage(player, "quest.npc.invalid-quest");
                return;
            }
            handleSingleQuest(npc, player, quest);
        } else {
            // 여러 퀘스트가 있는 경우 선택 GUI 표시
            List<Quest> quests = new ArrayList<>();
            for (QuestID questId : questIds) {
                Quest quest = questManager.getQuest(questId);
                if (quest != null) {
                    quests.add(quest);
                }
            }
            QuestSelectionGui.create(plugin, player, quests, npc.getName()).open();
        }

    }
    
    /**
     * 단일 퀘스트 처리
     */
    private void handleSingleQuest(NPC npc, Player player, Quest quest) {
        QuestID questId = quest.getId();
        
        // 이미 퀘스트를 진행 중인지 확인
        boolean hasActiveQuest = questManager.getActiveQuests(player.getUniqueId()).stream()
                .anyMatch(p -> p.getQuestId().equals(questId));

        if (hasActiveQuest) {
            // 진행 중인 퀘스트의 진행도 표시
            showQuestProgress(player, quest);
            return;
        }

        // 이미 완료했고 반복 불가능한지 확인
        boolean hasCompleted = questManager.getCompletedQuests(player.getUniqueId())
                .contains(questId);
        
        if (hasCompleted && !quest.isRepeatable()) {
            // 보상을 받지 않은 경우는 보상 NPC로 안내
            if (questManager.getUnclaimedRewardQuests(player.getUniqueId()).contains(questId)) {
                player.sendMessage(Component.text("이 퀘스트는 완료했습니다. 보상 NPC를 찾아가세요.", ColorUtil.INFO));
                return;
            }
            // 보상까지 모두 받은 경우만 완료 메시지 표시
            langManager.sendMessage(player, "quest.npc.already-completed");
            return;
        }
        
        // 퀘스트 요구사항 확인
        RPGPlayer rpgPlayer = plugin.getRPGPlayerManager().getOrCreatePlayer(player);
        
        // 레벨 요구사항 확인
        if (quest.getMinLevel() > 1 && rpgPlayer.getLevel() < quest.getMinLevel()) {
            langManager.sendMessage(player, "quest.npc.level-requirement", 
                "level", String.valueOf(quest.getMinLevel()));
            return;
        }
        
        // 선행 퀘스트 요구사항 확인
        if (!quest.getPrerequisiteQuests().isEmpty()) {
            boolean hasCompletedAllPrereqs = true;
            for (QuestID prereqId : quest.getPrerequisiteQuests()) {
                if (!questManager.getCompletedQuests(player.getUniqueId()).contains(prereqId)) {
                    hasCompletedAllPrereqs = false;
                    break;
                }
            }
            if (!hasCompletedAllPrereqs) {
                langManager.sendMessage(player, "quest.npc.prerequisite-requirement");
                return;
            }
        }
        
        // 양자택일 퀘스트 확인
        if (!quest.getExclusiveQuests().isEmpty()) {
            for (QuestID exclusiveId : quest.getExclusiveQuests()) {
                if (questManager.getCompletedQuests(player.getUniqueId()).contains(exclusiveId)) {
                    langManager.sendMessage(player, "quest.npc.mutually-exclusive");
                    return;
                }
            }
        }

        // 퀘스트 대화 GUI 열기 (대화가 없어도 수락/거절 선택 표시)
        guiManager.openQuestDialogGui(player, quest);
    }

    /**
     * Trait를 사용하는 상점 NPC 처리
     */
    private void handleShopNPCWithTrait(NPC npc, Player player, RPGShopTrait trait) {
        // 상점 GUI 열기
        NPCShopGui.create(plugin, player, trait, npc.getName()).open();
    }

    /**
     * Trait를 사용하는 가이드 NPC 처리
     */
    private void handleGuideNPCWithTrait(NPC npc, Player player, RPGGuideTrait trait) {
        // 메인 메뉴 열기
        MainMenuGui mainMenu = 
            MainMenuGui.create(guiManager, langManager, player);
        guiManager.openGui(player, mainMenu);
        SoundUtil.playOpenSound(player);
    }
    
    /**
     * Trait를 사용하는 퀘스트 보상 NPC 처리
     */
    private void handleQuestRewardNPCWithTrait(NPC npc, Player player, com.febrie.rpg.npc.trait.RPGQuestRewardTrait trait) {
        // 보상 수령 가능한 퀘스트 목록 가져오기
        List<QuestID> unclaimedQuests = questManager.getUnclaimedRewardQuests(player.getUniqueId());
        
        // 미수령 보상이 있는 퀘스트도 추가
        List<QuestID> questsWithUnclaimedItems = new ArrayList<>();
        for (QuestID questId : QuestID.values()) {
            if (questManager.getUnclaimedReward(player.getUniqueId(), questId) != null) {
                questsWithUnclaimedItems.add(questId);
            }
        }
        
        // 중복 제거하고 합치기
        java.util.Set<QuestID> allUnclaimedQuests = new java.util.HashSet<>(unclaimedQuests);
        allUnclaimedQuests.addAll(questsWithUnclaimedItems);
        
        // 특정 퀘스트 ID가 설정된 경우
        if (!trait.getQuestIds().isEmpty()) {
            List<QuestID> npcQuests = trait.getQuestIds();
            allUnclaimedQuests.retainAll(npcQuests); // NPC가 담당하는 퀘스트만 필터링
        }
        
        // 보상 수령 가능한 퀘스트가 없는 경우
        if (allUnclaimedQuests.isEmpty()) {
            langManager.sendMessage(player, "quest.reward.no-rewards");
            SoundUtil.playErrorSound(player);
            return;
        }
        
        // 보상 수령 가능한 퀘스트가 1개인 경우 바로 보상 GUI 열기
        if (allUnclaimedQuests.size() == 1) {
            QuestID questId = allUnclaimedQuests.iterator().next();
            Quest quest = questManager.getQuest(questId);
            if (quest != null) {
                com.febrie.rpg.gui.impl.quest.QuestRewardGui rewardGui = 
                    com.febrie.rpg.gui.impl.quest.QuestRewardGui.create(guiManager, langManager, player, quest);
                guiManager.openGui(player, rewardGui);
                SoundUtil.playOpenSound(player);
            }
        } else {
            // 여러 개인 경우 선택 GUI 표시
            List<Quest> questsWithRewards = new ArrayList<>();
            for (QuestID questId : unclaimedQuests) {
                Quest quest = questManager.getQuest(questId);
                if (quest != null) {
                    questsWithRewards.add(quest);
                }
            }
            QuestSelectionGui.create(plugin, player, questsWithRewards, npc.getName()).open();
            for (QuestID questId : allUnclaimedQuests) {
                Quest quest = questManager.getQuest(questId);
                if (quest != null) {
                    boolean isKorean = player.locale().getLanguage().equals("ko");
                    String questName = quest.getDisplayName(isKorean);
                    
                    // 미수령 아이템이 있는 경우 표시
                    if (questsWithUnclaimedItems.contains(questId)) {
                        com.febrie.rpg.quest.reward.UnclaimedReward unclaimed = 
                                questManager.getUnclaimedReward(player.getUniqueId(), questId);
                        if (unclaimed != null) {
                            long remainingMinutes = unclaimed.getRemainingTime() / 1000 / 60;
                            player.sendMessage(Component.text("- " + questName, ColorUtil.UNCOMMON)
                                    .append(Component.text(" (미수령 보상 ", ColorUtil.WARNING))
                                    .append(Component.text(remainingMinutes + "분 남음)", ColorUtil.ERROR)));
                        }
                    } else {
                        player.sendMessage(Component.text("- " + questName, ColorUtil.UNCOMMON));
                    }
                }
            }
        }
    }
    
    /**
     * 퀘스트 진행도 표시
     */
    private void showQuestProgress(Player player, Quest quest) {
        var activeQuests = questManager.getActiveQuests(player.getUniqueId());
        var progress = activeQuests.stream()
                .filter(p -> p.getQuestId().equals(quest.getId()))
                .findFirst()
                .orElse(null);
                
        if (progress == null) return;
        
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", ColorUtil.GRAY));
        player.sendMessage(Component.text("📋 ", ColorUtil.GOLD)
                .append(Component.text(quest.getDisplayName(true), ColorUtil.LEGENDARY))
                .append(Component.text(" 진행도", ColorUtil.COMMON)));
        player.sendMessage(Component.empty());
        
        // 각 목표별 진행도 표시
        List<QuestObjective> objectives = quest.getObjectives();
        for (int i = 0; i < objectives.size(); i++) {
            QuestObjective objective = objectives.get(i);
            var objProgress = progress.getObjective(objective.getId());
            
            if (objProgress == null) continue;
            
            boolean isComplete = objProgress.isCompleted();
            int current = objProgress.getCurrentValue();
            int required = objProgress.getRequiredValue();
            
            // 체크박스 아이콘
            String checkBox = isComplete ? "✅" : "☐";
            
            // 진행도 바
            int barLength = 20;
            int filledLength = (int) ((double) current / required * barLength);
            StringBuilder progressBar = new StringBuilder();
            progressBar.append("[");
            for (int j = 0; j < barLength; j++) {
                if (j < filledLength) {
                    progressBar.append("█");
                } else {
                    progressBar.append("░");
                }
            }
            progressBar.append("]");
            
            // 목표 설명
            String description = quest.getObjectiveDescription(objective, true);
            
            // 진행도 텍스트 (미완료: 노란색, 완료: 초록색)
            Component progressText = Component.text(checkBox + " ", isComplete ? ColorUtil.SUCCESS : ColorUtil.YELLOW)
                    .append(Component.text(description, isComplete ? ColorUtil.SUCCESS : ColorUtil.YELLOW))
                    .append(Component.text(" ", ColorUtil.COMMON))
                    .append(Component.text(progressBar.toString(), isComplete ? ColorUtil.SUCCESS : ColorUtil.YELLOW))
                    .append(Component.text(" (" + current + "/" + required + ")", isComplete ? ColorUtil.SUCCESS : ColorUtil.YELLOW));
                    
            player.sendMessage(progressText);
        }
        
        // 전체 진행률
        int completedCount = (int) objectives.stream()
                .filter(obj -> {
                    var objProgress = progress.getObjective(obj.getId());
                    return objProgress != null && objProgress.isCompleted();
                })
                .count();
        
        double totalProgress = (double) completedCount / objectives.size() * 100;
        
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("전체 진행률: ", ColorUtil.COMMON)
                .append(Component.text(String.format("%.1f%%", totalProgress), ColorUtil.GOLD)));
        player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", ColorUtil.GRAY));
        
        SoundUtil.playClickSound(player);
    }
    
}