package com.febrie.rpg.listener;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.impl.system.MainMenuGui;
import com.febrie.rpg.gui.impl.quest.QuestListGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.npc.trait.RPGGuideTrait;
import com.febrie.rpg.npc.trait.RPGQuestTrait;
import com.febrie.rpg.npc.trait.RPGShopTrait;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.SoundUtil;
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

        // Trait 시스템 확인
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
    }
    

    /**
     * Trait를 사용하는 퀘스트 NPC 처리
     */
    private void handleQuestNPCWithTrait(NPC npc, Player player, RPGQuestTrait trait) {
        QuestID questId = trait.getQuestId();
        
        if (questId == null) {
            // 퀘스트 목록 GUI 열기
            QuestListGui questListGui = 
                new QuestListGui(guiManager, langManager, player);
            guiManager.openGui(player, questListGui);
            return;
        }

        // 퀘스트 가져오기
        Quest quest = questManager.getQuest(questId);
        if (quest == null) {
            langManager.sendMessage(player, "quest.npc.invalid-quest");
            return;
        }

        // 이미 퀘스트를 진행 중인지 확인
        boolean hasActiveQuest = questManager.getActiveQuests(player.getUniqueId()).stream()
                .anyMatch(p -> p.getQuestId().equals(questId));

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
        RPGPlayer rpgPlayer = plugin.getRPGPlayerManager().getOrCreatePlayer(player);
        
        // 레벨 요구사항 확인
        if (quest.getMinLevel() > 0 && rpgPlayer.getLevel() < quest.getMinLevel()) {
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
        // TODO: 상점 GUI 구현 후 열기
        langManager.sendMessage(player, "general.coming-soon");
    }

    /**
     * Trait를 사용하는 가이드 NPC 처리
     */
    private void handleGuideNPCWithTrait(NPC npc, Player player, RPGGuideTrait trait) {
        // 메인 메뉴 열기
        MainMenuGui mainMenu = 
            new MainMenuGui(guiManager, langManager, player);
        guiManager.openGui(player, mainMenu);
        SoundUtil.playOpenSound(player);
    }
    
}