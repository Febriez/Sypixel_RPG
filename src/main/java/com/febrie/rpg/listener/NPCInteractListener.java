package com.febrie.rpg.listener;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.util.LangManager;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

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

        // NPC 타입 확인
        if (!npc.data().has("rpg_npc_type")) {
            return;
        }

        String npcType = npc.data().get("rpg_npc_type");
        
        switch (npcType) {
            case "QUEST" -> handleQuestNPC(npc, player);
            case "SHOP" -> handleShopNPC(npc, player);
            case "GUIDE" -> handleGuideNPC(npc, player);
        }
    }
    
    /**
     * 퀘스트 NPC 처리
     */
    private void handleQuestNPC(NPC npc, Player player) {
        // 퀘스트 ID 가져오기 (설정되어 있는 경우)
        String questIdStr = npc.data().get("quest_id", "");
        if (questIdStr.isEmpty()) {
            // 퀘스트 목록 GUI 열기
            com.febrie.rpg.gui.impl.QuestListGui questListGui = 
                new com.febrie.rpg.gui.impl.QuestListGui(player, guiManager, langManager);
            guiManager.openGui(player, questListGui);
            return;
        }

        // QuestID enum으로 변환
        QuestID questId;
        try {
            questId = QuestID.valueOf(questIdStr);
        } catch (IllegalArgumentException e) {
            // legacy ID로 시도
            try {
                questId = QuestID.fromLegacyId(questIdStr);
            } catch (IllegalArgumentException ex) {
                langManager.sendMessage(player, "quest.npc.invalid-quest");
                return;
            }
        }

        // 퀘스트 가져오기
        Quest quest = questManager.getQuest(questId);
        if (quest == null) {
            langManager.sendMessage(player, "quest.npc.invalid-quest");
            return;
        }

        // 이미 퀘스트를 진행 중인지 확인
        QuestID finalQuestId = questId;
        boolean hasActiveQuest = questManager.getActiveQuests(player.getUniqueId()).stream()
                .anyMatch(p -> p.getQuestId().equals(finalQuestId));

        if (hasActiveQuest) {
            langManager.sendMessage(player, "quest.npc.already-active");
            return;
        }

        // 이미 완료했고 반복 불가능한지 확인
        boolean hasCompleted = questManager.getCompletedQuests(player.getUniqueId())
                .contains(questId);

        if (hasCompleted && !quest.isRepeatable()) {
            langManager.sendMessage(player, "quest.npc.already-completed");
            return;
        }
        
        // 퀘스트 요구사항 확인
        com.febrie.rpg.player.RPGPlayer rpgPlayer = plugin.getRPGPlayerManager().getOrCreatePlayer(player);
        
        // 레벨 요구사항 확인
        if (quest.getMinLevel() > 0 && rpgPlayer.getLevel() < quest.getMinLevel()) {
            langManager.sendMessage(player, "quest.npc.level-requirement", 
                "level", String.valueOf(quest.getMinLevel()));
            return;
        }
        
        // TODO: 직업 요구사항은 Quest에 직접적인 필드가 없음
        // 필요시 Quest.Builder에 추가 필요
        
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

        // 퀘스트 수락 GUI 열기
        guiManager.openQuestAcceptGui(player, quest);
    }
    
    /**
     * 상점 NPC 처리
     */
    private void handleShopNPC(NPC npc, Player player) {
        // TODO: 상점 GUI 구현 후 열기
        langManager.sendMessage(player, "general.coming-soon");
    }
    
    /**
     * 가이드 NPC 처리
     */
    private void handleGuideNPC(NPC npc, Player player) {
        // 메인 메뉴 열기
        com.febrie.rpg.gui.impl.MainMenuGui mainMenu = 
            new com.febrie.rpg.gui.impl.MainMenuGui(guiManager, langManager, player);
        guiManager.openGui(player, mainMenu);
        com.febrie.rpg.util.SoundUtil.playOpenSound(player);
    }
    
}