package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import com.febrie.rpg.util.LangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.ArrayList;

/**
 * 일일 탐험 - 일일 퀘스트
 * 매일 새로운 지역을 탐험하고 발견하는 퀘스트
 *
 * @author Febrie
 */
public class ExplorationQuest extends Quest {

    /**
     * 기본 생성자
     */
    public ExplorationQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        List<QuestObjective> objectives = new ArrayList<>();
        
        // 탐험 목표
        objectives.add(new InteractNPCObjective("explorer_guide", "master_explorer"));
        objectives.add(new VisitLocationObjective("explore_forest", "mysterious_forest"));
        objectives.add(new VisitLocationObjective("explore_mountain", "high_peaks"));
        objectives.add(new CollectItemObjective("book_collect", Material.BOOK, 3));

        return new QuestBuilder()
                .id(QuestID.DAILY_EXPLORATION)
                .objectives(objectives)
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 140)
                        .addCurrency(CurrencyType.EMERALD, 3)
                        .addItem(new ItemStack(Material.MAP))
                        .addItem(new ItemStack(Material.COMPASS))
                        .addExperience(85)
                        .build())
                .sequential(false)
                .category(QuestCategory.DAILY)
                .minLevel(6)
                .repeatable(true)
                .daily(true)
                .addPrerequisite(QuestID.TUTORIAL_FIRST_STEPS);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "explorer_guide" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_EXPLORER_GUIDE, who);
            case "explore_forest" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_EXPLORE_FOREST, who);
            case "explore_mountain" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_EXPLORE_MOUNTAIN, who);
            case "book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_BOOK_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_DECLINE, who);
    }
}