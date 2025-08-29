package com.febrie.rpg.gui.impl.quest;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

    private QuestDetailGui(@NotNull GuiManager guiManager,
                          @NotNull Player viewer, @NotNull Quest quest, @NotNull QuestProgress progress) {
        super(viewer, guiManager, GUI_SIZE, LangManager.getComponent("gui.quest_detail.title", viewer.locale()));
        this.quest = quest;
        this.progress = progress;
    }

    /**
     * QuestDetailGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param viewer 보는 플레이어
     * @param quest 퀘스트
     * @param progress 퀘스트 진행도
     * @return 초기화된 QuestDetailGui 인스턴스
     */
    public static QuestDetailGui create(@NotNull GuiManager guiManager,
                                       @NotNull Player viewer, @NotNull Quest quest, @NotNull QuestProgress progress) {
        return new QuestDetailGui(guiManager, viewer, quest, progress);
    }

    @Override
    public @NotNull Component getTitle() {
        return LangManager.getComponent("gui.quest_detail.title", viewer.locale());
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
        ItemBuilder builder = ItemBuilder.of(Material.PAPER)
                .displayName(quest.getDisplayName(viewer)
                        .color(UnifiedColorUtil.LEGENDARY)
                        .decoration(TextDecoration.BOLD, true))
                .addLore(Component.empty());
        
        // 퀘스트 설명
        List<Component> descriptions = quest.getDisplayInfo(viewer);
        for (Component desc : descriptions) {
            builder.addLore(desc.color(UnifiedColorUtil.GRAY));
        }
        
        builder.addLore(Component.empty())
                // 전체 진행도
                .addLore(LangManager.getComponent("quest.total_progress", viewer.locale())
                        .append(Component.text(": " + progress.getCompletionPercentage() + "%"))
                        .color(UnifiedColorUtil.EMERALD))
                .hideAllFlags();

        setItem(QUEST_INFO_SLOT, GuiItem.display(builder.build()));
    }

    /**
     * 상세 목표 진행도 설정
     */
    private void setupObjectives() {
        ItemBuilder builder = ItemBuilder.of(Material.MAP)
                .displayNameTranslated("items.quest.detail.objectives.name")
                .addLore(Component.empty());
        
        quest.getObjectives().forEach(objective -> {
            var objProgress = progress.getObjectiveProgress(objective.getId());
            if (objProgress != null) {
                Component status = objProgress.isCompleted()
                        ? Component.text(" ✓", UnifiedColorUtil.SUCCESS)
                        : Component.text(" " + objective.getProgressString(objProgress), UnifiedColorUtil.GRAY);

                builder.addLore(Component.text("• ", UnifiedColorUtil.GRAY)
                        .append(quest.getObjectiveDescription(objective, viewer))
                        .append(status));
            }
        });
        
        builder.addLore(Component.empty())
                .addLore(LangManager.getComponent("quest.overall_progress", viewer.locale())
                        .append(Component.text(": " + progress.getCompletionPercentage() + "%"))
                        .color(UnifiedColorUtil.YELLOW))
                .hideAllFlags();

        setItem(OBJECTIVES_SLOT, GuiItem.display(builder.build()));
    }

    /**
     * 보상 설정
     */
    private void setupRewards() {
        ItemBuilder builder = ItemBuilder.of(Material.CHEST)
                .displayNameTranslated("items.quest.detail.rewards.name")
                .addLore(Component.empty());
        
        // 보상 정보 추가
        for (Component rewardLine : quest.getReward().getLoreComponents(viewer)) {
            builder.addLore(rewardLine);
        }
        
        builder.hideAllFlags();

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
            
            ItemBuilder builder = ItemBuilder.of(material)
                    .displayNameTranslated("items.quest.detail.progress.name")
                    .addLore(Component.text(completionPercentage + "%", UnifiedColorUtil.GRAY));
            
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
        setItem(getCloseButtonSlot(), GuiFactory.createCloseButton(viewer));
    }

    @Override
    public GuiFramework getBackTarget() {
        return QuestListGui.create(guiManager, viewer);
    }
}
