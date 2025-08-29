package com.febrie.rpg.gui.impl.quest;

import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.dto.quest.ActiveQuestDTO;
import com.febrie.rpg.dto.quest.CompletedQuestDTO;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 모든 퀘스트 목록을 보여주는 GUI
 * 진행 중 또는 완료된 퀘스트를 필터링하여 표시
 *
 * @author Febrie, CoffeeTory
 */
public class AllQuestsGui extends BaseGui {
    
    public enum QuestFilter {
        ACTIVE("active"),
        COMPLETED("completed");
        
        private final String displayName;
        
        QuestFilter(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private static final int GUI_SIZE = 54;
    private static final int ITEMS_PER_PAGE = 28;
    
    private final QuestManager questManager;
    private final QuestFilter filter;
    private final List<Quest> quests;
    private final int maxPage;
    private int currentPage = 1;
    
    private AllQuestsGui(@NotNull GuiManager guiManager,
                        @NotNull Player player,
                        @NotNull QuestFilter filter) {
        super(player, guiManager, GUI_SIZE, LangManager.getComponent("gui.all_quests.title", player));
        this.questManager = guiManager.getPlugin().getQuestManager();
        this.filter = filter;
        
        // 필터에 따라 퀘스트 목록 가져오기
        this.quests = new ArrayList<>();
        if (filter == QuestFilter.ACTIVE) {
            java.util.Map<String, ActiveQuestDTO> activeQuests = questManager.getActiveQuests(player.getUniqueId());
            for (ActiveQuestDTO activeData : activeQuests.values()) {
                Quest quest = questManager.getQuest(QuestID.valueOf(activeData.questId()));
                if (quest != null) {
                    quests.add(quest);
                }
            }
        } else {
            // Get completed quests from QuestManager
            java.util.Map<String, CompletedQuestDTO> completedQuests = questManager.getCompletedQuests(player.getUniqueId());
            for (CompletedQuestDTO completedData : completedQuests.values()) {
                Quest quest = questManager.getQuest(QuestID.valueOf(completedData.questId()));
                if (quest != null) {
                    quests.add(quest);
                }
            }
        }
        
        this.maxPage = Math.max(1, (quests.size() - 1) / ITEMS_PER_PAGE + 1);
    }
    
    /**
     * Factory method to create the GUI
     */
    public static AllQuestsGui create(@NotNull GuiManager guiManager,
                                     @NotNull Player player,
                                     @NotNull QuestFilter filter) {
        return new AllQuestsGui(guiManager, player, filter);
    }
    
    @Override
    public @NotNull Component getTitle() {
        return LangManager.getComponent("quest.filter." + filter.getDisplayName(), viewer);
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        return QuestListGui.create(guiManager, viewer);
    }
    
    @Override
    protected void setupLayout() {
        setupDecorations();
        displayQuests();
        setupNavigationButtons();
    }
    
    private void setupDecorations() {
        createBorder();
        
        // 필터 정보
        GuiItem filterInfo = GuiItem.of(
                ItemBuilder.of(Material.HOPPER, getViewerLocale())
                        .displayName(LangManager.getComponent("quest.filter", viewer).append(LangManager.getComponent("quest.filter." + filter.getDisplayName(), viewer)))
                        .addLore(LangManager.get("quest.total-count", viewer, Component.text(String.valueOf(quests.size()))))
                        .build());
        setItem(4, filterInfo);
    }
    
    private void displayQuests() {
        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, quests.size());
        
        int[] slots = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
        };
        
        for (int i = 0; i < slots.length; i++) {
            int questIndex = startIndex + i;
            if (questIndex < endIndex) {
                Quest quest = quests.get(questIndex);
                setItem(slots[i], createQuestItem(quest));
            }
        }
    }
    
    private QuestProgress findQuestProgress(Player player, Quest quest) {
        java.util.Map<String, ActiveQuestDTO> activeQuests = questManager.getActiveQuests(player.getUniqueId());
        for (java.util.Map.Entry<String, ActiveQuestDTO> entry : activeQuests.entrySet()) {
            ActiveQuestDTO activeData = entry.getValue();
            if (activeData.questId().equals(quest.getId().name())) {
                return questManager.getQuestProgress(player.getUniqueId(), entry.getKey());
            }
        }
        return null;
    }
    
    private GuiItem createQuestItem(Quest quest) {
        // 현재 진행 중인 퀘스트 찾기
        QuestProgress progress = findQuestProgress(viewer, quest);
        Material material = (progress != null && progress.isCompleted()) ? Material.ENCHANTED_BOOK : Material.BOOK;
        
        ItemBuilder builder = ItemBuilder.of(material, getViewerLocale())
                .displayName(quest.getDisplayName(viewer).color(UnifiedColorUtil.PRIMARY));
        
        // 퀘스트 설명
        for (Component line : quest.getDescription(viewer)) {
            builder.addLore(line.color(UnifiedColorUtil.GRAY));
        }
        
        builder.addLore(Component.empty());
        
        // 진행 상황
        if (progress != null && progress.isCompleted()) {
            builder.addLoreTranslated("quest.completed");
            // 완료된 퀘스트 확인
            java.util.Map<String, CompletedQuestDTO> completedQuests = questManager.getCompletedQuests(viewer.getUniqueId());
            boolean hasReward = completedQuests.values().stream()
                    .anyMatch(data -> data.questId().equals(quest.getId().name()));
            if (hasReward) {
                builder.addLoreTranslated("quest.reward-available");
            }
        } else if (progress != null) {
            int completed = (int) progress.getObjectives().values().stream()
                    .filter(ObjectiveProgress::isCompleted)
                    .count();
            int total = quest.getObjectives().size();
            Component progressText = LangManager.getComponent("quest.progress", viewer);
            builder.addLore(progressText.append(Component.text(": " + completed + "/" + total)).color(UnifiedColorUtil.YELLOW));
        }
        
        builder.addLore(Component.empty());
        builder.addLoreTranslated("quest.click-for-details");
        
        return GuiItem.clickable(
                builder.build(),
                p -> {
                    // 현재 진행 중인 퀘스트 찾기
                    QuestProgress qProgress = findQuestProgress(p, quest);
                    QuestDetailGui.create(guiManager, p, quest, qProgress).open(p);
                    playClickSound(p);
                }
        );
    }
    
    private void setupNavigationButtons() {
        // 이전 페이지
        if (currentPage > 1) {
            GuiItem prevButton = GuiItem.clickable(
                    ItemBuilder.of(Material.ARROW, getViewerLocale())
                            .displayNameTranslated("quest.previous-page")
                            .addLore(LangManager.get("quest.go-to-page", viewer, Component.text(String.valueOf(currentPage - 1))))
                            .build(),
                    p -> {
                        currentPage--;
                        setupLayout();
                        playClickSound(p);
                    }
            );
            setItem(45, prevButton);
        }
        
        // 다음 페이지
        if (currentPage < maxPage) {
            GuiItem nextButton = GuiItem.clickable(
                    ItemBuilder.of(Material.ARROW, getViewerLocale())
                            .displayNameTranslated("quest.next-page")
                            .addLore(LangManager.get("quest.go-to-page", viewer, Component.text(String.valueOf(currentPage + 1))))
                            .build(),
                    p -> {
                        currentPage++;
                        setupLayout();
                        playClickSound(p);
                    }
            );
            setItem(53, nextButton);
        }
        
        // 뒤로가기
        GuiItem backButton = GuiItem.clickable(
                ItemBuilder.of(Material.ARROW, getViewerLocale())
                        .displayNameTranslated("gui.common.back")
                        .build(),
                p -> {
                    guiManager.openGui(p, getBackTarget());
                    playClickSound(p);
                }
        );
        setItem(49, backButton);
    }
    
}