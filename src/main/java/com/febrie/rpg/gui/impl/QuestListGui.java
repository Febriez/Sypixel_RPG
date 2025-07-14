package com.febrie.rpg.gui.impl;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.quest.util.QuestDisplayUtil;
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
    private static final int ACTIVE_QUESTS_START = 10;
    private static final int COMPLETED_QUESTS_START = 28;
    private static final int QUESTS_PER_ROW = 7;

    // 카테고리 표시 슬롯
    private static final int ACTIVE_LABEL_SLOT = 9;
    private static final int COMPLETED_LABEL_SLOT = 27;

    private final QuestManager questManager;
    private final QuestDisplayUtil displayUtil;

    public QuestListGui(@NotNull Player player, @NotNull GuiManager guiManager,
                        @NotNull LangManager langManager) {
        super(player, guiManager, langManager, GUI_SIZE, "gui.quest-list.title");
        this.questManager = QuestManager.getInstance();
        this.displayUtil = new QuestDisplayUtil();
        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return trans("gui.quest-list.title");
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
        setupQuestCategories();
        displayActiveQuests();
        displayCompletedQuests();
        setupStandardNavigation(true, true);
    }

    /**
     * 장식 요소 설정
     */
    private void setupDecorations() {
        // 상단과 하단 경계선
        for (int i = 0; i < 9; i++) {
            setItem(i, GuiFactory.createDecoration());
            setItem(45 + i, GuiFactory.createDecoration());
        }

        // 카테고리 구분선
        for (int i = 18; i < 27; i++) {
            setItem(i, GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE));
        }
    }

    /**
     * 퀘스트 카테고리 라벨 설정
     */
    private void setupQuestCategories() {
        // 진행 중인 퀘스트 라벨
        GuiItem activeLabel = GuiItem.display(
                new ItemBuilder(Material.BOOK)
                        .displayName(trans("gui.quest-list.active-quests"))
                        .addLore(trans("gui.quest-list.active-quests-desc"))
                        .addItemFlags(ItemFlag.values())
                        .build()
        );
        setItem(ACTIVE_LABEL_SLOT, activeLabel);

        // 완료된 퀘스트 라벨
        GuiItem completedLabel = GuiItem.display(
                new ItemBuilder(Material.ENCHANTED_BOOK)
                        .displayName(trans("gui.quest-list.completed-quests"))
                        .addLore(trans("gui.quest-list.completed-quests-desc"))
                        .addItemFlags(ItemFlag.values())
                        .build()
        );
        setItem(COMPLETED_LABEL_SLOT, completedLabel);
    }

    /**
     * 진행 중인 퀘스트 표시
     */
    private void displayActiveQuests() {
        List<QuestProgress> activeQuests = questManager.getActiveQuests(viewer.getUniqueId());

        int slot = ACTIVE_QUESTS_START;
        for (QuestProgress progress : activeQuests) {
            if (slot >= ACTIVE_QUESTS_START + QUESTS_PER_ROW) break;

            Quest quest = questManager.getQuest(progress.getQuestId());
            if (quest == null) continue;

            GuiItem questItem = createActiveQuestItem(quest, progress);
            setItem(slot++, questItem);
        }

        // 빈 슬롯은 유리판으로 채우기
        while (slot < ACTIVE_QUESTS_START + QUESTS_PER_ROW) {
            setItem(slot++, GuiFactory.createDecoration(Material.BLACK_STAINED_GLASS_PANE));
        }
    }

    /**
     * 완료된 퀘스트 표시
     */
    private void displayCompletedQuests() {
        List<String> completedQuests = questManager.getCompletedQuests(viewer.getUniqueId());

        int slot = COMPLETED_QUESTS_START;
        int count = 0;

        for (String questId : completedQuests) {
            if (count >= QUESTS_PER_ROW * 2) break; // 2줄까지만 표시

            Quest quest = questManager.getQuest(questId);
            if (quest == null) continue;

            GuiItem questItem = createCompletedQuestItem(quest);
            setItem(slot++, questItem);
            count++;

            // 다음 줄로 이동
            if (count == QUESTS_PER_ROW) {
                slot = COMPLETED_QUESTS_START + 9; // 다음 줄 시작
            }
        }
    }

    /**
     * 진행 중인 퀘스트 아이템 생성
     */
    private GuiItem createActiveQuestItem(@NotNull Quest quest, @NotNull QuestProgress progress) {
        ItemBuilder builder = new ItemBuilder(Material.PAPER)
                .displayName(displayUtil.getQuestName(viewer, quest)
                        .color(ColorUtil.UNCOMMON)
                        .decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();

        // 퀘스트 설명
        lore.add(displayUtil.getQuestDescription(viewer, quest));
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
                        : Component.text(" [" + objProgress.getCurrent() + "/" + objProgress.getRequired() + "]", ColorUtil.GRAY);

                lore.add(Component.text("  • ", ColorUtil.GRAY)
                        .append(Component.text(objective.getStatusInfo(objProgress), ColorUtil.WHITE))
                        .append(status));
            }
        });

        lore.add(Component.empty());
        lore.add(trans("gui.quest-list.click-details").color(ColorUtil.GRAY));

        builder.lore(lore);

        return GuiItem.clickable(builder.build(), p -> {
            // TODO: 퀘스트 상세 정보 GUI 열기
            langManager.sendMessage(p, "general.coming-soon");
            playClickSound(p);
        });
    }

    /**
     * 완료된 퀘스트 아이템 생성
     */
    private GuiItem createCompletedQuestItem(@NotNull Quest quest) {
        ItemBuilder builder = new ItemBuilder(Material.MAP)
                .displayName(displayUtil.getQuestName(viewer, quest)
                        .color(ColorUtil.SUCCESS)
                        .decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();

        // 퀘스트 설명
        lore.add(displayUtil.getQuestDescription(viewer, quest));
        lore.add(Component.empty());

        // 완료 표시
        lore.add(trans("gui.quest-list.completed-status")
                .color(ColorUtil.SUCCESS));

        // 보상 정보
        lore.add(Component.empty());
        lore.add(trans("gui.quest-list.rewards").color(ColorUtil.YELLOW));

        Component rewardInfo = quest.getReward().getDisplayInfo(viewer);
        lore.add(Component.text("  • ", ColorUtil.GRAY).append(rewardInfo));

        if (quest.isRepeatable()) {
            lore.add(Component.empty());
            lore.add(trans("gui.quest-list.repeatable").color(ColorUtil.INFO));
        }

        builder.lore(lore);

        return GuiItem.display(builder.build());
    }


    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
}