package com.febrie.rpg.gui.impl.quest;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.BackableGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.reward.UnclaimedReward;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 모든 퀘스트 목록을 보여주는 GUI
 * 진행 중 또는 완료된 퀘스트를 필터링하여 표시
 *
 * @author Febrie, CoffeeTory
 */
public class AllQuestsGui extends BaseGui implements BackableGui {
    
    public enum QuestFilter {
        ACTIVE("진행 중인 퀘스트"),
        COMPLETED("완료된 퀘스트");
        
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
                        @NotNull LangManager langManager,
                        @NotNull Player player,
                        @NotNull QuestFilter filter) {
        super(player, guiManager, langManager, GUI_SIZE, "gui.all-quests.title");
        this.questManager = guiManager.getPlugin().getQuestManager();
        this.filter = filter;
        
        // 필터에 따라 퀘스트 목록 가져오기
        this.quests = new ArrayList<>();
        if (filter == QuestFilter.ACTIVE) {
            List<QuestProgress> activeProgress = questManager.getActiveQuests(player.getUniqueId());
            for (QuestProgress progress : activeProgress) {
                Quest quest = questManager.getQuest(progress.getQuestId());
                if (quest != null) {
                    quests.add(quest);
                }
            }
        } else {
            // Get completed quests from QuestManager
            List<QuestID> completedIds = questManager.getCompletedQuests(player.getUniqueId());
            for (QuestID questId : completedIds) {
                Quest quest = questManager.getQuest(questId);
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
                                     @NotNull LangManager langManager,
                                     @NotNull Player player,
                                     @NotNull QuestFilter filter) {
        AllQuestsGui gui = new AllQuestsGui(guiManager, langManager, player, filter);
        return createAndInitialize(gui, "gui.all-quests.title");
    }
    
    @Override
    public @NotNull Component getTitle() {
        return Component.text(filter.getDisplayName(), ColorUtil.PRIMARY);
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        return QuestListGui.create(guiManager, langManager, viewer);
    }
    
    @Override
    public GuiFramework getBackDestination() {
        return getBackTarget();
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
                new ItemBuilder(Material.HOPPER)
                        .displayName(Component.text("필터: " + filter.getDisplayName(), ColorUtil.PRIMARY))
                        .addLore(Component.text("총 " + quests.size() + "개의 퀘스트", ColorUtil.GRAY))
                        );
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
    
    private GuiItem createQuestItem(Quest quest) {
        QuestProgress progress = questManager.getQuestProgress(viewer.getUniqueId(), quest.getId());
        Material material = (progress != null && progress.isCompleted()) ? Material.ENCHANTED_BOOK : Material.BOOK;
        
        ItemBuilder builder = new ItemBuilder(material)
                .displayName(Component.text(quest.getDisplayName(langManager.getPlayerLanguage(viewer).equals("ko")), ColorUtil.PRIMARY));
        
        // 퀘스트 설명
        for (String line : quest.getDescription(langManager.getPlayerLanguage(viewer).equals("ko"))) {
            builder.addLore(Component.text(line, ColorUtil.GRAY));
        }
        
        builder.addLore(Component.empty());
        
        // 진행 상황
        if (progress != null && progress.isCompleted()) {
            builder.addLore(Component.text("✔ 완료됨", ColorUtil.SUCCESS));
            UnclaimedReward reward = questManager.getUnclaimedReward(viewer.getUniqueId(), quest.getId());
            if (reward != null) {
                builder.addLore(Component.text("⚡ 보상 수령 가능!", ColorUtil.GOLD));
            }
        } else if (progress != null) {
            int completed = (int) progress.getObjectives().values().stream()
                    .filter(ObjectiveProgress::isCompleted)
                    .count();
            int total = quest.getObjectives().size();
            builder.addLore(Component.text(langManager.getMessage(viewer, "quest.progress") + ": " + completed + "/" + total, ColorUtil.YELLOW));
        }
        
        builder.addLore(Component.empty());
        builder.addLore(Component.text("클릭하여 상세 정보 보기", ColorUtil.YELLOW));
        
        return GuiItem.clickable(
                builder.build(),
                p -> {
                    QuestProgress qProgress = questManager.getQuestProgress(p.getUniqueId(), quest.getId());
                    QuestDetailGui.create(guiManager, langManager, p, quest, qProgress).open(p);
                    playClickSound(p);
                }
        );
    }
    
    private void setupNavigationButtons() {
        // 이전 페이지
        if (currentPage > 1) {
            GuiItem prevButton = GuiItem.clickable(
                    new ItemBuilder(Material.ARROW)
                            .displayName(Component.text("이전 페이지", ColorUtil.YELLOW))
                            .addLore(Component.text("페이지 " + (currentPage - 1) + "로 이동", ColorUtil.GRAY))
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
                    new ItemBuilder(Material.ARROW)
                            .displayName(Component.text("다음 페이지", ColorUtil.YELLOW))
                            .addLore(Component.text("페이지 " + (currentPage + 1) + "로 이동", ColorUtil.GRAY))
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
                new ItemBuilder(Material.ARROW)
                        .displayName(trans("gui.common.back"))
                        .build(),
                p -> {
                    guiManager.openGui(p, getBackTarget());
                    playClickSound(p);
                }
        );
        setItem(49, backButton);
    }
    
    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
}