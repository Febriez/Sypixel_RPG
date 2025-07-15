package com.febrie.rpg.gui.impl;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 퀘스트 목록 GUI
 * 플레이어의 진행 중인 퀘스트와 완료된 퀘스트를 표시
 *
 * @author Febrie
 */
public class QuestListGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6 rows

    // 레이아웃 상수
    private static final int ACTIVE_QUESTS_START = 10;      // 2번째 줄 1번 슬롯부터
    private static final int COMPLETED_QUESTS_START = 28;   // 4번째 줄 1번 슬롯부터
    private static final int QUESTS_PER_ROW = 7;            // 한 줄에 7개
    private static final int MAX_DISPLAY_QUESTS = 14;       // 최대 표시 개수 (7개 x 2줄)

    // 카테고리 표시 슬롯
    private static final int ACTIVE_LABEL_SLOT = 2;        // 첫 줄 2번 슬롯
    private static final int COMPLETED_LABEL_SLOT = 6;      // 첫 줄 6번 슬롯

    private final QuestManager questManager;

    public QuestListGui(@NotNull Player player, @NotNull GuiManager guiManager,
                        @NotNull LangManager langManager) {
        super(player, guiManager, langManager, GUI_SIZE, "gui.quest-list.title");
        this.questManager = QuestManager.getInstance();
        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return trans("gui.quest-list.title");
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
        setupCategoryLabels();
        displayActiveQuests();
        displayCompletedQuests();
        setupBackButton();
    }

    /**
     * 장식 요소 설정
     */
    private void setupDecorations() {
        // 전체 경계선
        createBorder();
        
        // 진행 중/완료 퀘스트 구분선 (3번째 줄에서 퀘스트 표시 영역 제외)
        setItem(18, GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE));
        setItem(22, GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE));
        setItem(26, GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE));
        
        // 중앙 세로 구분선 (4번 슬롯)
        setItem(4, GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE));
        setItem(13, GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE));
        setItem(31, GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE));
        setItem(40, GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE));
    }

    /**
     * 카테고리 라벨 설정
     */
    private void setupCategoryLabels() {
        List<QuestProgress> activeQuests = questManager.getActiveQuests(viewer.getUniqueId());
        List<QuestID> completedQuests = questManager.getCompletedQuests(viewer.getUniqueId());
        
        // 진행 중인 퀘스트 라벨
        ItemBuilder activeBuilder = new ItemBuilder(Material.BOOK)
                .displayName(trans("gui.quest-list.active-quests"))
                .addLore(trans("gui.quest-list.active-quests-desc"));
        
        // 12개 이상이면 우클릭 안내 추가
        if (activeQuests.size() > MAX_DISPLAY_QUESTS) {
            activeBuilder.addLore(Component.empty());
            activeBuilder.addLore(Component.text("▶ 우클릭하여 모든 진행 중 퀘스트를 확인합니다", ColorUtil.YELLOW));
        }
        
        GuiItem activeLabel;
        if (activeQuests.size() > MAX_DISPLAY_QUESTS) {
            activeLabel = GuiItem.clickable(
                    activeBuilder.addItemFlags(ItemFlag.values()).build(),
                    p -> {
                        // TODO: 모든 진행 중 퀘스트 보기 GUI 열기
                        playClickSound(p);
                    }
            );
        } else {
            activeLabel = GuiItem.display(
                    activeBuilder.addItemFlags(ItemFlag.values()).build()
            );
        }
        setItem(ACTIVE_LABEL_SLOT, activeLabel);

        // 완료된 퀘스트 라벨
        ItemBuilder completedBuilder = new ItemBuilder(Material.ENCHANTED_BOOK)
                .displayName(trans("gui.quest-list.completed-quests"))
                .addLore(trans("gui.quest-list.completed-quests-desc"));
                
        // 12개 이상이면 우클릭 안내 추가
        if (completedQuests.size() > MAX_DISPLAY_QUESTS) {
            completedBuilder.addLore(Component.empty());
            completedBuilder.addLore(Component.text("▶ 우클릭하여 모든 완료된 퀘스트를 확인합니다", ColorUtil.YELLOW));
        }
        
        GuiItem completedLabel;
        if (completedQuests.size() > MAX_DISPLAY_QUESTS) {
            completedLabel = GuiItem.clickable(
                    completedBuilder.addItemFlags(ItemFlag.values()).build(),
                    p -> {
                        // TODO: 모든 완료된 퀘스트 보기 GUI 열기
                        playClickSound(p);
                    }
            );
        } else {
            completedLabel = GuiItem.display(
                    completedBuilder.addItemFlags(ItemFlag.values()).build()
            );
        }
        setItem(COMPLETED_LABEL_SLOT, completedLabel);
    }

    /**
     * 진행 중인 퀘스트 표시
     */
    private void displayActiveQuests() {
        List<QuestProgress> activeQuests = questManager.getActiveQuests(viewer.getUniqueId());

        int slot = ACTIVE_QUESTS_START;
        int count = 0;
        
        for (QuestProgress progress : activeQuests) {
            if (count >= MAX_DISPLAY_QUESTS) break; // 최대 18개까지만 표시

            Quest quest = questManager.getQuest(progress.getQuestId());
            if (quest == null) continue;

            GuiItem questItem = createActiveQuestItem(quest, progress);
            setItem(slot++, questItem);
            count++;
            
            // 다음 줄로 이동 (7개마다)
            if (count == 7) {
                slot = ACTIVE_QUESTS_START + 9; // 세 번째 줄로
            }
        }
    }

    /**
     * 완료된 퀘스트 표시
     */
    private void displayCompletedQuests() {
        List<QuestID> completedQuests = questManager.getCompletedQuests(viewer.getUniqueId());

        int slot = COMPLETED_QUESTS_START;
        int count = 0;

        for (QuestID questId : completedQuests) {
            if (count >= MAX_DISPLAY_QUESTS) break; // 최대 18개까지만 표시

            Quest quest = questManager.getQuest(questId);
            if (quest == null) continue;

            GuiItem questItem = createCompletedQuestItem(quest);
            setItem(slot++, questItem);
            count++;

            // 다음 줄로 이동 (7개마다)
            if (count == 7) {
                slot = COMPLETED_QUESTS_START + 9; // 다섯 번째 줄로
            }
        }
    }

    /**
     * 진행 중인 퀘스트 아이템 생성
     */
    private GuiItem createActiveQuestItem(@NotNull Quest quest, @NotNull QuestProgress progress) {
        ItemBuilder builder = new ItemBuilder(Material.PAPER)
                .displayName(Component.text(quest.getDisplayName(viewer.locale().toString().startsWith("ko")))
                        .color(ColorUtil.UNCOMMON)
                        .decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();

        // 퀘스트 설명
        List<String> descriptions = quest.getDescription(viewer.locale().toString().startsWith("ko"));
        for (String desc : descriptions) {
            lore.add(Component.text(desc, ColorUtil.GRAY));
        }
        lore.add(Component.empty());

        // 진행도 표시
        lore.add(trans("gui.quest-list.progress")
                .append(Component.text(": " + progress.getCompletionPercentage() + "%", ColorUtil.EMERALD)));

        // 목표 진행 상황
        lore.add(Component.empty());
        lore.add(trans("gui.quest-list.objectives").color(ColorUtil.YELLOW));

        quest.getObjectives().forEach(objective -> {
            var objProgress = progress.getObjectiveProgress(objective.getId());
            if (objProgress != null) {
                Component status = objProgress.isCompleted()
                        ? Component.text(" ✓", ColorUtil.SUCCESS)
                        : Component.text(" " + objective.getProgressString(objProgress), ColorUtil.GRAY);

                lore.add(Component.text("• ", ColorUtil.GRAY)
                        .append(Component.text(quest.getObjectiveDescription(objective, viewer.locale().toString().startsWith("ko"))))
                        .append(status));
            }
        });

        // 클릭 안내
        lore.add(Component.empty());
        lore.add(trans("gui.quest-list.click-details").color(ColorUtil.GRAY));

        builder.addLore(lore);

        return GuiItem.clickable(builder.build(), p -> {
            // TODO: 퀘스트 상세 정보 GUI 열기
            playClickSound(p);
        });
    }

    /**
     * 완료된 퀘스트 아이템 생성
     */
    private GuiItem createCompletedQuestItem(@NotNull Quest quest) {
        ItemBuilder builder = new ItemBuilder(Material.MAP)
                .displayName(Component.text(quest.getDisplayName(viewer.locale().toString().startsWith("ko")))
                        .color(ColorUtil.SUCCESS)
                        .decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();

        // 퀘스트 설명
        List<String> descriptions = quest.getDescription(viewer.locale().toString().startsWith("ko"));
        for (String desc : descriptions) {
            lore.add(Component.text(desc, ColorUtil.GRAY));
        }
        lore.add(Component.empty());

        // 완료 표시
        lore.add(trans("gui.quest-list.completed-label")
                .color(ColorUtil.SUCCESS)
                .decoration(TextDecoration.BOLD, true));

        // 반복 가능 여부
        if (quest.isRepeatable()) {
            lore.add(Component.empty());
            lore.add(trans("gui.quest-list.repeatable").color(ColorUtil.AQUA));
        }

        builder.addLore(lore);
        builder.addItemFlags(ItemFlag.values());

        return GuiItem.display(builder.build());
    }

    /**
     * 뒤로가기 버튼 설정
     */
    private void setupBackButton() {
        // BaseGui의 표준 뒤로가기 버튼 사용
        updateNavigationButtons();
        
        // 닫기 버튼도 추가
        setItem(CLOSE_BUTTON_SLOT, GuiFactory.createCloseButton(langManager, viewer));
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
}