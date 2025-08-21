package com.febrie.rpg.gui.impl.quest;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.dto.quest.ActiveQuestDTO;
import com.febrie.rpg.dto.quest.CompletedQuestDTO;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.UnifiedColorUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;

import java.util.List;

/**
 * 퀘스트 선택 GUI
 * NPC가 여러 퀘스트를 가지고 있을 때 표시
 *
 * @author Febrie, CoffeeTory
 */
public class QuestSelectionGui extends BaseGui {
    
    private final QuestManager questManager;
    private final List<Quest> quests;
    private final String npcName;
    
    private QuestSelectionGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                             @NotNull List<Quest> quests, @NotNull String npcName) {
        super(viewer, guiManager, Math.min(54, ((quests.size() - 1) / 9 + 1) * 9 + 18), "gui.quest-selection.title", npcName);
        this.questManager = guiManager.getPlugin().getQuestManager();
        this.quests = quests;
        this.npcName = npcName;
    }
    
    /**
     * Factory method to create and open the quest selection GUI
     */
    public static QuestSelectionGui create(@NotNull GuiManager guiManager,
                                          @NotNull Player viewer, @NotNull List<Quest> quests, @NotNull String npcName) {
        return new QuestSelectionGui(viewer, guiManager, quests, npcName);
    }
    
    @Override
    public @NotNull Component getTitle() {
        return trans("gui.quest-selection.title", npcName);
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        // 퀘스트 선택은 NPC 대화에서 시작되므로 뒤로가기 없음
        return null;
    }
    
    @Override
    protected void setupLayout() {
        createBorder();
        setupStandardNavigation(false, true);
        
        // 퀘스트 아이템들 배치
        int slot = 10;
        for (Quest quest : quests) {
            if (slot % 9 == 8) slot += 2; // 다음 줄로

            setItem(slot, createQuestItem(quest));
            slot++;
        }
    }
    
    private GuiItem createQuestItem(Quest quest) {
        // 현재 진행 중인지 확인
        java.util.Map<String, ActiveQuestDTO> activeQuests = questManager.getActiveQuests(viewer.getUniqueId());
        boolean isActive = activeQuests.values().stream()
                .anyMatch(data -> data.questId().equals(quest.getId().name()));
        
        // 완료된 퀘스트인지 확인
        java.util.Map<String, CompletedQuestDTO> completedQuests = questManager.getCompletedQuests(viewer.getUniqueId());
        boolean hasReward = completedQuests.values().stream()
                .anyMatch(data -> data.questId().equals(quest.getId().name()));
        
        Material material = hasReward ? Material.ENCHANTED_BOOK : Material.BOOK;
        
        ItemBuilder builder = new ItemBuilder(material)
                .displayName(quest.getDisplayName(viewer).color(UnifiedColorUtil.GOLD).decorate(net.kyori.adventure.text.format.TextDecoration.BOLD));
        
        builder.addLore(Component.empty());
        
        // 퀘스트 설명
        for (Component line : quest.getDescription(viewer)) {
            builder.addLore(line.color(UnifiedColorUtil.GRAY));
        }
        
        builder.addLore(Component.empty());
        
        // 상태 표시
        if (hasReward) {
            builder.addLore(trans("gui.quest.status.completed"));
            builder.addLore(trans("gui.quest.status.reward_available"));
        } else if (isActive) {
            builder.addLore(trans("gui.quest.status.in_progress"));
        } else {
            builder.addLore(trans("gui.quest.status.new_quest"));
        }
        
        builder.addLore(Component.empty());
        builder.addLore(trans("gui.quest.click_to_select"));
        
        return GuiItem.clickable(builder.build(), player -> {
            handleQuestSelection(player, quest);
            playClickSound(player);
        });
    }
    
    
    private void handleQuestSelection(Player player, Quest quest) {
        player.closeInventory();
        
        // 현재 진행 중인지 확인
        java.util.Map<String, ActiveQuestDTO> activeQuests = questManager.getActiveQuests(player.getUniqueId());
        java.util.Optional<java.util.Map.Entry<String, ActiveQuestDTO>> activeEntry = activeQuests.entrySet().stream()
                .filter(entry -> entry.getValue().questId().equals(quest.getId().name()))
                .findFirst();
        
        // 완료된 퀘스트인지 확인
        java.util.Map<String, CompletedQuestDTO> completedQuests = questManager.getCompletedQuests(player.getUniqueId());
        java.util.Optional<java.util.Map.Entry<String, CompletedQuestDTO>> completedEntry = completedQuests.entrySet().stream()
                .filter(entry -> entry.getValue().questId().equals(quest.getId().name()))
                .findFirst();
        
        if (completedEntry.isPresent()) {
            // 보상 수령
            String instanceId = completedEntry.get().getKey();
            QuestRewardGui.create(guiManager, viewer, quest, instanceId).open(viewer);
        } else if (activeEntry.isPresent()) {
            // 진행 상황 표시
            player.sendMessage(Component.text("퀘스트 '").append(quest.getDisplayName(viewer)).append(Component.text("'를 진행 중입니다.")).color(UnifiedColorUtil.YELLOW));
        } else {
            // 새 퀘스트 시작
            questManager.startQuest(viewer, quest.getId());
        }
    }
    
}