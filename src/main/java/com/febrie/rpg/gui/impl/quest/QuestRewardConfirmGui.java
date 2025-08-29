package com.febrie.rpg.gui.impl.quest;

import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.SoundUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * 퀘스트 보상 파괴 확인 GUI
 * 
 * @author Febrie
 */
public class QuestRewardConfirmGui extends BaseGui {
    
    private static final int GUI_SIZE = 27; // 3 rows
    private static final int YES_SLOT = 11;
    private static final int NO_SLOT = 15;
    
    private final Quest quest;
    private final String instanceId;
    private final QuestManager questManager;
    private final QuestRewardGui previousGui;
    
    private QuestRewardConfirmGui(@NotNull GuiManager guiManager,
                                  @NotNull Player viewer, @NotNull Quest quest, @NotNull String instanceId, @NotNull QuestRewardGui previousGui) {
        super(viewer, guiManager, GUI_SIZE, LangManager.getComponent("gui.quest_reward.confirm.title", viewer.locale()));
        this.quest = quest;
        this.instanceId = instanceId;
        this.questManager = QuestManager.getInstance();
        this.previousGui = previousGui;
    }
    
    public static QuestRewardConfirmGui create(@NotNull GuiManager guiManager,
                                               @NotNull Player viewer, @NotNull Quest quest, @NotNull String instanceId, @NotNull QuestRewardGui previousGui) {
        return new QuestRewardConfirmGui(guiManager, viewer, quest, instanceId, previousGui);
    }
    
    @Override
    public @NotNull Component getTitle() {
        return LangManager.getComponent("gui.quest_reward.confirm.title", viewer.locale()).color(UnifiedColorUtil.ERROR);
    }
    
    @Override
    protected void setupLayout() {
        setupDecorations();
        setupMessage();
        setupButtons();
    }
    private void setupDecorations() {
        // 배경을 검은 유리판으로 채우기
        ItemBuilder blackGlass = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE)
                .displayName(Component.empty())
                .hideAllFlags();
        
        GuiItem glassPane = GuiItem.display(blackGlass.build());
        
        for (int i = 0; i < GUI_SIZE; i++) {
            if (i != YES_SLOT && i != NO_SLOT && i != 13) {
                setItem(i, glassPane);
            }
        }
    }
    private void setupMessage() {
        // 중앙에 경고 메시지 표시
        ItemBuilder warningBuilder = ItemBuilder.of(Material.BARRIER)
                .displayNameTranslated("items.quest.reward-confirm.warning.name")
                .addLore(Component.empty())
                .addLoreTranslated("items.quest.reward-confirm.warning.lore1")
                .addLoreTranslated("items.quest.reward-confirm.warning.lore2")
                .addLore(Component.empty())
                .addLoreTranslated("items.quest.reward-confirm.warning.question")
                .hideAllFlags();
        
        setItem(13, GuiItem.display(warningBuilder.build()));
    }
    
    private void setupButtons() {
        // 예 버튼
        ItemBuilder yesBuilder = ItemBuilder.of(Material.RED_WOOL)
                .displayNameTranslated("items.quest.reward-confirm.yes.name")
                .addLoreTranslated("items.quest.reward-confirm.yes.lore")
                .hideAllFlags();
        
        GuiItem yesButton = GuiItem.clickable(yesBuilder.build(), p -> {
            // 보상 파괴 - 퀘스트를 ClaimedQuestData로 이동
            questManager.markQuestAsRewarded(p.getUniqueId(), instanceId);
            
            p.closeInventory();
            p.sendMessage(Component.empty());
            p.sendMessage(LangManager.getComponent("gui.quest_reward.destroyed", p.locale()).color(UnifiedColorUtil.ERROR)
                    .decoration(TextDecoration.BOLD, true));
            p.sendMessage(LangManager.getComponent("gui.quest_reward.destroyed_desc", p.locale(), quest.getDisplayName(p))
                    .color(UnifiedColorUtil.WARNING));
            
            SoundUtil.playDeleteSound(p);
        });
        
        setItem(YES_SLOT, yesButton);
        
        // 아니요 버튼
        ItemBuilder noBuilder = ItemBuilder.of(Material.LIME_WOOL)
                .displayNameTranslated("items.quest.reward-confirm.no.name")
                .addLoreTranslated("items.quest.reward-confirm.no.lore")
                .hideAllFlags();
        
        GuiItem noButton = GuiItem.clickable(noBuilder.build(), p -> {
            // 이전 GUI로 돌아가기
            guiManager.openGui(p, previousGui);
            SoundUtil.playClickSound(p);
        });
        
        setItem(NO_SLOT, noButton);
    }
    
    @Override
    public GuiFramework getBackTarget() {
        return previousGui;
    }
}
