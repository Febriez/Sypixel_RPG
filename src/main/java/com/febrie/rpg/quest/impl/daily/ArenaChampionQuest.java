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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.ArrayList;

/**
 * 일일 투기장 챔피언 - 일일 퀘스트
 * 매일 투기장에서 전투를 벌이고 승리하는 퀘스트
 *
 * @author Febrie
 */
public class ArenaChampionQuest extends Quest {

    /**
     * 기본 생성자
     */
    public ArenaChampionQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        List<QuestObjective> objectives = new ArrayList<>();
        
        // 투기장 목표
        objectives.add(new InteractNPCObjective("arena_master", "gladiator_master"));
        objectives.add(new KillMobObjective("defeat_opponents", EntityType.ZOMBIE, 8));
        objectives.add(new KillMobObjective("champion_battles", EntityType.SKELETON, 5));
        objectives.add(new SurviveObjective("arena_survival", 300)); // 5 minutes

        return new QuestBuilder()
                .id(QuestID.DAILY_ARENA_CHAMPION)
                .objectives(objectives)
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 250)
                        .addCurrency(CurrencyType.EMERALD, 6)
                        .addItem(new ItemStack(Material.IRON_SWORD))
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 3))
                        .addExperience(120)
                        .build())
                .sequential(false)
                .category(QuestCategory.DAILY)
                .minLevel(8)
                .repeatable(true)
                .daily(true)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_ARENA_CHAMPION_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_ARENA_CHAMPION_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "arena_master" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_ARENA_CHAMPION_OBJECTIVES_ARENA_MASTER, who);
            case "defeat_opponents" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_ARENA_CHAMPION_OBJECTIVES_DEFEAT_OPPONENTS, who);
            case "champion_battles" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_ARENA_CHAMPION_OBJECTIVES_CHAMPION_BATTLES, who);
            case "arena_survival" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_ARENA_CHAMPION_OBJECTIVES_ARENA_SURVIVAL, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_ARENA_CHAMPION_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_ARENA_CHAMPION_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_ARENA_CHAMPION_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_ARENA_CHAMPION_DECLINE, who);
    }
}