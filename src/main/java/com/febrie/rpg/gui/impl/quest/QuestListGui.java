package com.febrie.rpg.gui.impl.quest;
import com.febrie.rpg.util.lang.GeneralLangKey;

import com.febrie.rpg.util.lang.GuiLangKey;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.player.ProfileGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.manager.QuestManager;
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
import java.util.Locale;

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
    private static final int COMPLETED_QUESTS_START = 14;   // 2번째 줄 중간부터
    private static final int QUESTS_PER_ROW = 7;            // 한 줄에 7개
    private static final int MAX_DISPLAY_QUESTS = 14;       // 최대 표시 개수 (7개 x 2줄)

    // 카테고리 표시 슬롯
    private static final int ACTIVE_LABEL_SLOT = 2;        // 첫 줄 2번 슬롯
    private static final int COMPLETED_LABEL_SLOT = 6;      // 첫 줄 6번 슬롯

    private final QuestManager questManager;

    private QuestListGui(@NotNull GuiManager guiManager,
                        @NotNull Player viewer) {
        super(viewer, guiManager, GUI_SIZE, LangManager.text(GuiLangKey.GUI_QUEST_LIST_TITLE, viewer.locale()));
        this.questManager = QuestManager.getInstance();
    }

    /**
     * QuestListGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param viewer 보는 플레이어
     * @return 초기화된 QuestListGui 인스턴스
     */
    public static QuestListGui create(@NotNull GuiManager guiManager,
                                     @NotNull Player viewer) {
        return new QuestListGui(guiManager, viewer);
    }

    @Override
    public @NotNull Component getTitle() {
        return LangManager.text(GuiLangKey.GUI_QUEST_LIST_TITLE, viewer.locale());
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
        setItem(18, GuiFactory.createDecoration());
        setItem(22, GuiFactory.createDecoration());
        setItem(26, GuiFactory.createDecoration());

        // 중앙 세로 구분선 (4번 슬롯)
        setItem(4, GuiFactory.createDecoration());
        setItem(13, GuiFactory.createDecoration());
        setItem(31, GuiFactory.createDecoration());
        setItem(40, GuiFactory.createDecoration());

    }
    /**
     * 라벨 아이템 생성 - 퀘스트 개수에 따라 클릭 가능 여부 결정
     *
     * @param builder     아이템 빌더
     * @param questCount  퀘스트 개수
     * @param clickAction 클릭 시 실행할 동작 (퀘스트가 MAX_DISPLAY_QUESTS보다 많을 때만 사용)
     * @return 생성된 GuiItem
     */
    private GuiItem createLabelItem(ItemBuilder builder, int questCount, Runnable clickAction) {
        // 12개 이상이면 우클릭 안내 추가
        if (questCount > MAX_DISPLAY_QUESTS) {
            builder.addLore(Component.empty());
            builder.addLore(LangManager.list(GeneralLangKey.ITEMS_QUEST_LIST_VIEW_ALL, viewer.locale()));
        }

        // GUI 아이템 표준 설정 적용
        builder.hideAllFlags();

        // 클릭 가능 여부에 따라 GuiItem 생성
        if (questCount > MAX_DISPLAY_QUESTS) {
            return GuiItem.clickable(builder.build(), p -> {
                clickAction.run();
                playClickSound(p);
            });
        } else {
            return GuiItem.display(builder.build());
        }
    }
    
    /**
     * 카테고리 라벨 설정
     */
    private void setupCategoryLabels() {
        var activeQuests = questManager.getActiveQuests(viewer.getUniqueId());
        var completedQuests = questManager.getCompletedQuests(viewer.getUniqueId());

        // 진행 중인 퀘스트 라벨
        Locale locale = viewer.locale();
        ItemBuilder activeBuilder = ItemBuilder.of(Material.ENCHANTED_BOOK)
                .displayName(LangManager.text(GeneralLangKey.ITEMS_QUEST_LIST_ACTIVE_NAME, viewer.locale()))
                .addLore(LangManager.list(GeneralLangKey.ITEMS_QUEST_LIST_ACTIVE_LORE, viewer.locale()));

        GuiItem activeLabel = createLabelItem(activeBuilder, activeQuests.size(), () -> {
            // 모든 진행 중 퀘스트 보기 GUI 열기
            AllQuestsGui.create(guiManager, viewer, AllQuestsGui.QuestFilter.ACTIVE).open(viewer);
        });
        setItem(ACTIVE_LABEL_SLOT, activeLabel);

        // 완료된 퀘스트 라벨
        ItemBuilder completedBuilder = ItemBuilder.of(Material.BOOK)
                .displayName(LangManager.text(GeneralLangKey.ITEMS_QUEST_LIST_COMPLETED_NAME, viewer.locale()))
                .addLore(LangManager.list(GeneralLangKey.ITEMS_QUEST_LIST_COMPLETED_LORE, viewer.locale()));

        GuiItem completedLabel = createLabelItem(completedBuilder, completedQuests.size(), () -> {
            // 모든 완료된 퀘스트 보기 GUI 열기
            AllQuestsGui.create(guiManager, viewer, AllQuestsGui.QuestFilter.COMPLETED).open(viewer);
        });
        setItem(COMPLETED_LABEL_SLOT, completedLabel);
    }
    
    /**
     * 진행 중인 퀘스트 표시
     */
    private void displayActiveQuests() {
        java.util.Map<String, com.febrie.rpg.dto.quest.ActiveQuestDTO> activeQuests = questManager.getActiveQuests(viewer.getUniqueId());

        int count = 0;
        int row = 1; // 시작 행 (0부터 시작)
        int col = 1; // 시작 열 (1번 슬롯 = 왼쪽 3개 영역의 시작)

        for (java.util.Map.Entry<String, com.febrie.rpg.dto.quest.ActiveQuestDTO> entry : activeQuests.entrySet()) {
            if (count >= MAX_DISPLAY_QUESTS) break; // 최대 14개까지만 표시

            String instanceId = entry.getKey();
            com.febrie.rpg.dto.quest.ActiveQuestDTO activeData = entry.getValue();
            Quest quest = questManager.getQuest(QuestID.valueOf(activeData.questId()));
            if (quest == null) continue;
            
            QuestProgress progress = questManager.getQuestProgress(viewer.getUniqueId(), instanceId);
            if (progress == null) continue;

            int slot = row * 9 + col;
            GuiItem questItem = createActiveQuestItem(quest, progress);
            setItem(slot, questItem);
            count++;

            // 다음 위치 계산 (한 줄에 3개씩)
            col++;
            if (col >= 4) { // 왼쪽 3개 배치 완료 (1,2,3번 슬롯)
                col = 1;
                row++;
            }
        }
    }
    
    /**
     * 완료된 퀘스트 표시
     */
    private void displayCompletedQuests() {
        java.util.Map<String, com.febrie.rpg.dto.quest.CompletedQuestDTO> completedQuests = questManager.getCompletedQuests(viewer.getUniqueId());

        int count = 0;
        int row = 1; // 시작 행 (0부터 시작)
        int col = 5; // 시작 열 (5번 슬롯 = 오른쪽 3개 영역의 시작)

        for (com.febrie.rpg.dto.quest.CompletedQuestDTO completedData : completedQuests.values()) {
            if (count >= MAX_DISPLAY_QUESTS) break; // 최대 14개까지만 표시

            Quest quest = questManager.getQuest(QuestID.valueOf(completedData.questId()));
            if (quest == null) continue;

            int slot = row * 9 + col;
            GuiItem questItem = createCompletedQuestItem(quest);
            setItem(slot, questItem);
            count++;

            // 다음 위치 계산 (한 줄에 3개씩)
            col++;
            if (col >= 8) { // 오른쪽 3개 배치 완료 (5,6,7번 슬롯)
                col = 5;
                row++;
            }
        }
    }
    
    /**
     * 진행 중인 퀘스트 아이템 생성
     */
    private GuiItem createActiveQuestItem(@NotNull Quest quest, @NotNull QuestProgress progress) {
        Locale locale = viewer.locale();
        ItemBuilder builder = ItemBuilder.of(Material.PAPER)
                .displayName(quest.getDisplayName(viewer)
                        .color(UnifiedColorUtil.UNCOMMON)
                        .decoration(TextDecoration.ITALIC, false));

        // 퀘스트 설명
        List<Component> descriptions = quest.getDisplayInfo(viewer);
        for (Component desc : descriptions) {
            builder.addLore(desc.color(UnifiedColorUtil.GRAY));
        }
        
        builder.addLore(Component.empty())
                // 진행도 표시
                .addLore(LangManager.text(GuiLangKey.GUI_QUEST_LIST_PROGRESS, viewer.locale())
                        .append(Component.text(" " + progress.getCompletionPercentage() + "%", UnifiedColorUtil.EMERALD)))
                // 클릭 안내
                .addLore(Component.empty())
                .addLore(LangManager.text(GuiLangKey.GUI_QUEST_LIST_CLICK_DETAILS, viewer.locale()).color(UnifiedColorUtil.GRAY));

        return GuiItem.clickable(builder.build(), p -> {
            // 퀘스트 상세 정보 GUI 열기
            guiManager.openGui(p, QuestDetailGui.create(guiManager, p, quest, progress));
            playClickSound(p);
        });
    }
    
    /**
     * 완료된 퀘스트 아이템 생성
     */
    private GuiItem createCompletedQuestItem(@NotNull Quest quest) {
        Locale locale = viewer.locale();
        ItemBuilder builder = ItemBuilder.of(Material.MAP)
                .displayName(quest.getDisplayName(viewer)
                        .color(UnifiedColorUtil.SUCCESS)
                        .decoration(TextDecoration.ITALIC, false));

        // 퀘스트 설명
        List<Component> descriptions = quest.getDisplayInfo(viewer);
        for (Component desc : descriptions) {
            builder.addLore(desc.color(UnifiedColorUtil.GRAY));
        }
        
        builder.addLore(Component.empty())
                // 완료 표시
                .addLore(LangManager.text(GuiLangKey.GUI_QUEST_LIST_COMPLETED_LABEL, viewer.locale())
                        .color(UnifiedColorUtil.SUCCESS)
                        .decoration(TextDecoration.BOLD, true));

        // 반복 가능 여부
        if (quest.isRepeatable()) {
            builder.addLore(Component.empty())
                    .addLore(LangManager.text(GuiLangKey.GUI_QUEST_LIST_REPEATABLE, viewer.locale()).color(UnifiedColorUtil.AQUA));
        }

        builder.hideAllFlags();

        return GuiItem.display(builder.build());
    }
    
    /**
     * 뒤로가기 버튼 설정
     */
    private void setupBackButton() {
        // BaseGui의 표준 뒤로가기 버튼 사용
        updateNavigationButtons();

        // 닫기 버튼도 추가
        setItem(getCloseButtonSlot(), GuiFactory.createCloseButton(viewer));
    }

    @Override
    public GuiFramework getBackTarget() {
        return ProfileGui.create(guiManager, viewer);
    }
}
    
