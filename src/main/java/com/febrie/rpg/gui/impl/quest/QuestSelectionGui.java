package com.febrie.rpg.gui.impl.quest;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.BaseGui;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.quest.reward.UnclaimedReward;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
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
    private final Player viewer;
    private final List<Quest> quests;
    private final String npcName;
    
    private QuestSelectionGui(@NotNull RPGMain plugin, @NotNull Player viewer,
                             @NotNull List<Quest> quests, @NotNull String npcName) {
        super(plugin, Math.min(54, ((quests.size() - 1) / 9 + 1) * 9 + 18)); // 동적 크기
        this.questManager = plugin.getQuestManager();
        this.viewer = viewer;
        this.quests = quests;
        this.npcName = npcName;
    }
    
    /**
     * Factory method to create and open the quest selection GUI
     */
    public static QuestSelectionGui create(@NotNull RPGMain plugin, @NotNull Player viewer,
                                          @NotNull List<Quest> quests, @NotNull String npcName) {
        QuestSelectionGui gui = new QuestSelectionGui(plugin, viewer, quests, npcName);
        return BaseGui.create(gui, ColorUtil.parseComponent("&e&l" + npcName + " - 퀘스트 선택"));
    }
    
    @Override
    protected void setupItems() {
        fillBorder(Material.YELLOW_STAINED_GLASS_PANE);
        
        // 퀘스트 아이템들 배치
        int slot = 10;
        for (int i = 0; i < quests.size(); i++) {
            if (slot % 9 == 8) slot += 2; // 다음 줄로
            
            Quest quest = quests.get(i);
            setItem(slot, createQuestItem(quest));
            slot++;
        }
        
        // 닫기 버튼
        setItem(size - 5, createCloseButton());
    }
    
    private ItemStack createQuestItem(Quest quest) {
        QuestProgress progress = questManager.getQuestProgress(viewer.getUniqueId(), quest.getId());
        boolean isCompleted = progress != null && progress.isCompleted();
        Material material = isCompleted ? Material.ENCHANTED_BOOK : Material.BOOK;
        
        ItemBuilder builder = new ItemBuilder(material)
                .displayName(ColorUtil.parseComponent("&e&l" + quest.getDisplayName(plugin.getLangManager().getPlayerLanguage(viewer).equals("ko"))));
        
        builder.addLore(ColorUtil.parseComponent(""));
        
        // 퀘스트 설명
        for (String line : quest.getDescription(plugin.getLangManager().getPlayerLanguage(viewer).equals("ko"))) {
            builder.addLore(ColorUtil.parseComponent("&7" + line));
        }
        
        builder.addLore(ColorUtil.parseComponent(""));
        
        // 상태 표시
        if (isCompleted) {
            builder.addLore(ColorUtil.parseComponent("&a✔ 완료됨"));
            UnclaimedReward reward = questManager.getUnclaimedReward(viewer.getUniqueId(), quest.getId());
            if (reward != null) {
                builder.addLore(ColorUtil.parseComponent("&6⚡ 보상 수령 가능!"));
            }
        } else if (progress != null) {
            builder.addLore(ColorUtil.parseComponent("&e진행 중..."));
        } else {
            builder.addLore(ColorUtil.parseComponent("&7새로운 퀘스트"));
        }
        
        builder.addLore(ColorUtil.parseComponent(""));
        builder.addLore(ColorUtil.parseComponent("&e▶ 클릭하여 선택"));
        
        return builder.build();
    }
    
    private ItemStack createCloseButton() {
        return new ItemBuilder(Material.BARRIER)
                .displayName(ColorUtil.parseComponent("&c닫기"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7GUI를 닫습니다"))
                .build();
    }
    
    @Override
    protected void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        int slot = event.getSlot();
        
        // 닫기 버튼
        if (slot == size - 5) {
            player.closeInventory();
            return;
        }
        
        // 퀘스트 선택
        int index = -1;
        int checkSlot = 10;
        for (int i = 0; i < quests.size(); i++) {
            if (checkSlot % 9 == 8) checkSlot += 2;
            if (checkSlot == slot) {
                index = i;
                break;
            }
            checkSlot++;
        }
        
        if (index >= 0 && index < quests.size()) {
            Quest quest = quests.get(index);
            player.closeInventory();
            
            QuestProgress progress = questManager.getQuestProgress(player.getUniqueId(), quest.getId());
            boolean isCompleted = progress != null && progress.isCompleted();
            UnclaimedReward reward = questManager.getUnclaimedReward(player.getUniqueId(), quest.getId());
            
            if (isCompleted && reward != null) {
                // 보상 수령
                QuestRewardGui.create(plugin.getGuiManager(), plugin.getLangManager(), viewer, quest).open(viewer);
            } else if (progress != null) {
                // 진행 상황 표시
                player.sendMessage(Component.text("퀘스트 '" + quest.getDisplayName(viewer.locale().toString().startsWith("ko")) + "'를 진행 중입니다.", ColorUtil.YELLOW));
            } else {
                // 새 퀘스트 시작
                questManager.startQuest(viewer, quest.getId());
            }
        }
    }
}