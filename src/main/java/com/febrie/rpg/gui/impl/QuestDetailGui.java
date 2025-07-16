package com.febrie.rpg.gui.impl;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 퀘스트 상세 정보 GUI
 * 진행 중인 퀘스트의 상세 정보를 표시
 *
 * @author CoffeeTory
 */
public class QuestDetailGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6 rows

    // 레이아웃 상수
    private static final int QUEST_INFO_SLOT = 4;
    private static final int OBJECTIVES_SLOT = 20;
    private static final int REWARDS_SLOT = 24;
    
    // 진행도 바 슬롯 (37-43)
    private static final int PROGRESS_BAR_START = 37;
    private static final int PROGRESS_BAR_END = 43;
    private static final int PROGRESS_BAR_SIZE = 7;

    private final Quest quest;
    private final QuestProgress progress;
    private final boolean isKorean;

    public QuestDetailGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                          @NotNull Player viewer, @NotNull Quest quest, @NotNull QuestProgress progress) {
        super(viewer, guiManager, langManager, GUI_SIZE, "gui.quest-detail.title");
        this.quest = quest;
        this.progress = progress;
        this.isKorean = viewer.locale().toString().startsWith("ko");
        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return trans("gui.quest-detail.title");
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
        setupQuestInfo();
        setupObjectives();
        setupRewards();
        setupProgressBar();
        setupBackButton();
    }

    /**
     * 장식 요소 설정
     */
    private void setupDecorations() {
        // 전체 경계선
        createBorder();
        
        // 기능 슬롯들을 제외한 나머지 슬롯을 장식으로 채움
        for (int i = 0; i < size; i++) {
            if (isDecorationSlot(i)) {
                setItem(i, GuiFactory.createDecoration());
            }
        }
    }

    /**
     * 장식 슬롯인지 확인
     */
    private boolean isDecorationSlot(int slot) {
        // 기능 슬롯들 제외
        if (slot == QUEST_INFO_SLOT || slot == OBJECTIVES_SLOT || slot == REWARDS_SLOT) {
            return false;
        }
        
        // 진행도 바 슬롯들 제외
        if (slot >= PROGRESS_BAR_START && slot <= PROGRESS_BAR_END) {
            return false;
        }
        
        // 네비게이션 버튼 슬롯들 제외
        if (slot == getBackButtonSlot() || slot == getCloseButtonSlot()) {
            return false;
        }
        
        // 경계선 슬롯이 아닌 내부 슬롯들만 장식으로 채움
        return !isBorderSlot(slot);
    }

    /**
     * 경계선 슬롯인지 확인
     */
    private boolean isBorderSlot(int slot) {
        int row = slot / 9;
        int col = slot % 9;
        return row == 0 || row == 5 || col == 0 || col == 8;
    }

    /**
     * 퀘스트 정보 설정
     */
    private void setupQuestInfo() {
        ItemBuilder builder = new ItemBuilder(Material.PAPER)
                .displayName(Component.text(quest.getDisplayName(isKorean))
                        .color(ColorUtil.LEGENDARY)
                        .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        
        // 퀘스트 설명
        lore.add(Component.empty());
        List<String> descriptions = quest.getDisplayInfo(isKorean);
        for (String desc : descriptions) {
            lore.add(Component.text(desc, ColorUtil.GRAY));
        }
        lore.add(Component.empty());
        
        // 전체 진행도
        lore.add(Component.text("전체 진행도: " + progress.getCompletionPercentage() + "%", ColorUtil.EMERALD));
        
        builder.addLore(lore);
        builder.asGuiItem(false);

        setItem(QUEST_INFO_SLOT, GuiItem.display(builder.build()));
    }

    /**
     * 상세 목표 진행도 설정
     */
    private void setupObjectives() {
        ItemBuilder builder = new ItemBuilder(Material.MAP)
                .displayName(Component.text("상세 목표 진행도", ColorUtil.YELLOW)
                        .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        
        quest.getObjectives().forEach(objective -> {
            var objProgress = progress.getObjectiveProgress(objective.getId());
            if (objProgress != null) {
                Component status = objProgress.isCompleted()
                        ? Component.text(" ✓", ColorUtil.SUCCESS)
                        : Component.text(" " + objective.getProgressString(objProgress), ColorUtil.GRAY);

                lore.add(Component.text("• ", ColorUtil.GRAY)
                        .append(Component.text(quest.getObjectiveDescription(objective, isKorean)))
                        .append(status));
            }
        });
        
        builder.addLore(lore);
        builder.asGuiItem(false);

        setItem(OBJECTIVES_SLOT, GuiItem.display(builder.build()));
    }

    /**
     * 보상 설정
     */
    private void setupRewards() {
        ItemBuilder builder = new ItemBuilder(Material.CHEST)
                .displayName(Component.text("보상", ColorUtil.GOLD)
                        .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        
        // 보상 정보 추가
        lore.addAll(quest.getReward().getLoreComponents(viewer));
        
        builder.addLore(lore);
        builder.asGuiItem(false);

        setItem(REWARDS_SLOT, GuiItem.display(builder.build()));
    }

    /**
     * 진행도 바 설정
     */
    private void setupProgressBar() {
        int completionPercentage = progress.getCompletionPercentage();
        int greenSlots = calculateGreenSlots(completionPercentage);
        
        for (int i = 0; i < PROGRESS_BAR_SIZE; i++) {
            int slot = PROGRESS_BAR_START + i;
            Material material = i < greenSlots ? Material.LIME_STAINED_GLASS_PANE : Material.YELLOW_STAINED_GLASS_PANE;
            
            ItemBuilder builder = new ItemBuilder(material)
                    .displayName(Component.text("진행도", ColorUtil.WHITE))
                    .addLore(Component.text(completionPercentage + "%", ColorUtil.GRAY));
            
            setItem(slot, GuiItem.display(builder.build()));
        }
    }
    
    /**
     * 진행도에 따른 초록색 슬롯 개수 계산
     */
    private int calculateGreenSlots(int completionPercentage) {
        if (completionPercentage == 0) return 0;
        if (completionPercentage == 100) return PROGRESS_BAR_SIZE;
        
        // 0%는 0개, 100%는 전체, 나머지는 비례 계산 (최소 1개는 보장)
        int greenSlots = (completionPercentage * PROGRESS_BAR_SIZE) / 100;
        return Math.max(1, Math.min(greenSlots, PROGRESS_BAR_SIZE - 1));
    }

    /**
     * 뒤로가기 버튼 설정
     */
    private void setupBackButton() {
        updateNavigationButtons();
        setItem(getCloseButtonSlot(), GuiFactory.createCloseButton(langManager, viewer));
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }

    @Override
    public GuiFramework getBackTarget() {
        return new QuestListGui(guiManager, langManager, viewer);
    }
}