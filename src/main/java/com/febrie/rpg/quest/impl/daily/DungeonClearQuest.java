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

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.ArrayList;

/**
 * 일일 던전 클리어 - 일일 퀘스트
 * 매일 던전을 클리어하고 보물을 수집하는 퀘스트
 *
 * @author Febrie
 */
public class DungeonClearQuest extends Quest {

    /**
     * 기본 생성자
     */
    public DungeonClearQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        List<QuestObjective> objectives = new ArrayList<>();
        
        // 던전 클리어 목표
        objectives.add(new InteractNPCObjective("dungeon_guide", "dungeon_master"));
        objectives.add(new VisitLocationObjective("enter_dungeon", "ancient_dungeon"));
        objectives.add(new KillMobObjective("clear_monsters", EntityType.CAVE_SPIDER, 15));
        objectives.add(new CollectItemObjective("gold_ingot_collect", Material.GOLD_INGOT, 5));

        return new QuestBuilder()
                .id(QuestID.DAILY_DUNGEON_CLEAR)
                .objectives(objectives)
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 280)
                        .addCurrency(CurrencyType.EMERALD, 7)
                        .addItem(new ItemStack(Material.DIAMOND))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK))
                        .addExperience(150)
                        .build())
                .sequential(true)
                .category(QuestCategory.DAILY)
                .minLevel(12)
                .repeatable(true)
                .daily(true)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_DUNGEON_CLEAR_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_DUNGEON_CLEAR_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "dungeon_guide" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DUNGEON_CLEAR_OBJECTIVES_DUNGEON_GUIDE, who);
            case "enter_dungeon" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DUNGEON_CLEAR_OBJECTIVES_ENTER_DUNGEON, who);
            case "clear_monsters" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DUNGEON_CLEAR_OBJECTIVES_CLEAR_MONSTERS, who);
            case "gold_ingot_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DUNGEON_CLEAR_OBJECTIVES_GOLD_INGOT_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_DUNGEON_CLEAR_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_DUNGEON_CLEAR_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_DUNGEON_CLEAR_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_DUNGEON_CLEAR_DECLINE, who);
    }
}