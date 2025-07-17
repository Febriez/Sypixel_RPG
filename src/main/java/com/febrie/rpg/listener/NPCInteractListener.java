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
import com.febrie.rpg.util.ColorUtil;
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
                    questTrait.setNpcType("QUEST");
                    questTrait.setQuestId(questId);
                    
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
            }
            
            // 대기 중인 trait 제거
            com.febrie.rpg.npc.NPCTraitSetter.getInstance().removePendingTrait(player);
            SoundUtil.playSuccessSound(player);
            return;
        }

        // 기존 trait 처리
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