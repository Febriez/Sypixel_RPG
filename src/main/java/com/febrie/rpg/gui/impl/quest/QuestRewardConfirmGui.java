package com.febrie.rpg.gui.impl.quest;

import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.SoundUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    
    private QuestRewardConfirmGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                                  @NotNull Player viewer, @NotNull Quest quest, @NotNull String instanceId, @NotNull QuestRewardGui previousGui) {
        super(viewer, guiManager, langManager, GUI_SIZE, "gui.quest-reward.confirm.title");
        this.quest = quest;
        this.instanceId = instanceId;
        this.questManager = QuestManager.getInstance();
        this.previousGui = previousGui;
    }
    
    public static QuestRewardConfirmGui create(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                                               @NotNull Player viewer, @NotNull Quest quest, @NotNull String instanceId, @NotNull QuestRewardGui previousGui) {
        QuestRewardConfirmGui gui = new QuestRewardConfirmGui(guiManager, langManager, viewer, quest, instanceId, previousGui);
        gui.initialize("gui.quest-reward.confirm.title");
        return gui;
    }
    
    @Override
    public @NotNull Component getTitle() {
        return trans("gui.quest-reward.confirm.title").color(ColorUtil.ERROR);
    }
    
    @Override
    protected void setupLayout() {
        setupDecorations();
        setupMessage();
        setupButtons();
    }
    
    private void setupDecorations() {
        // 배경을 검은 유리판으로 채우기
        ItemBuilder blackGlass = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .displayName(Component.empty());
        
        GuiItem glassPane = GuiItem.display(blackGlass.build());
        
        for (int i = 0; i < GUI_SIZE; i++) {
            if (i != YES_SLOT && i != NO_SLOT && i != 13) {
                setItem(i, glassPane);
            }
        }
    }
    
    private void setupMessage() {
        // 중앙에 경고 메시지 표시
        ItemBuilder warningBuilder = new ItemBuilder(Material.BARRIER)
                .displayName(trans("gui.quest-reward.confirm.message").color(ColorUtil.ERROR)
                        .decoration(TextDecoration.BOLD, true))
                .addLore(Component.empty())
                .addLore(trans("gui.quest-reward.confirm.warning1").color(ColorUtil.WARNING))
                .addLore(trans("gui.quest-reward.confirm.warning2").color(ColorUtil.WARNING))
                .addLore(Component.empty())
                .addLore(trans("gui.quest-reward.confirm.question").color(ColorUtil.YELLOW)
                        .decoration(TextDecoration.BOLD, true));
        
        setItem(13, GuiItem.display(warningBuilder.build()));
    }
    
    private void setupButtons() {
        // 예 버튼
        ItemBuilder yesBuilder = new ItemBuilder(Material.RED_WOOL)
                .displayName(trans("gui.quest-reward.confirm.yes").color(ColorUtil.ERROR)
                        .decoration(TextDecoration.BOLD, true))
                .addLore(trans("gui.quest-reward.confirm.yes-desc").color(ColorUtil.GRAY));
        
        GuiItem yesButton = GuiItem.clickable(yesBuilder.build(), p -> {
            // 보상 파괴 - 퀘스트를 ClaimedQuestData로 이동
            questManager.markQuestAsRewarded(p.getUniqueId(), instanceId);
            
            p.closeInventory();
            p.sendMessage(Component.empty());
            p.sendMessage(trans("gui.quest-reward.destroyed").color(ColorUtil.ERROR)
                    .decoration(TextDecoration.BOLD, true));
            p.sendMessage(trans("gui.quest-reward.destroyed-desc", 
                    "quest", quest.getDisplayName(p.locale().getLanguage().equals("ko")))
                    .color(ColorUtil.WARNING));
            
            SoundUtil.playDeleteSound(p);
        });
        
        setItem(YES_SLOT, yesButton);
        
        // 아니요 버튼
        ItemBuilder noBuilder = new ItemBuilder(Material.LIME_WOOL)
                .displayName(trans("gui.quest-reward.confirm.no").color(ColorUtil.SUCCESS)
                        .decoration(TextDecoration.BOLD, true))
                .addLore(trans("gui.quest-reward.confirm.no-desc").color(ColorUtil.GRAY));
        
        GuiItem noButton = GuiItem.clickable(noBuilder.build(), p -> {
            // 이전 GUI로 돌아가기
            guiManager.openGui(p, previousGui);
            SoundUtil.playClickSound(p);
        });
        
        setItem(NO_SLOT, noButton);
    }
    
    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
    
    @Override
    public GuiFramework getBackTarget() {
        return previousGui;
    }
}