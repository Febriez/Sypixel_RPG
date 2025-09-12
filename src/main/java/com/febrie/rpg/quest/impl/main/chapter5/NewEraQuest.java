package com.febrie.rpg.quest.impl.main.chapter5;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Chapter 5: New Era Quest
 * Begin the dawn of a new age of peace and prosperity
 *
 * @author Febrie
 */
public class NewEraQuest extends Quest {
    
    /**
     * Default constructor
     */
    public NewEraQuest() {
        super(createBuilder());
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_NEW_ERA)
                .objectives(List.of(
                        new InteractNPCObjective("kingdom_herald", "kingdom_herald"),
                        new VisitLocationObjective("new_capital", "new_capital_area"),
                        new CollectItemObjective("smooth_stone_collect", Material.SMOOTH_STONE, 25),
                        new VisitLocationObjective("unity_plaza", "unity_plaza_area"),
                        new CollectItemObjective("paper_collect", Material.PAPER, 5),
                        new InteractNPCObjective("peace_ambassador", "peace_ambassador"),
                        new CollectItemObjective("emerald_collect", Material.EMERALD, 20),
                        new VisitLocationObjective("harmony_gardens", "harmony_gardens_area"),
                        new InteractNPCObjective("era_chronicler", "era_chronicler")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 15000)
                        .addItem(new ItemStack(Material.NETHERITE_AXE))
                        .addItem(new ItemStack(Material.EMERALD_BLOCK, 10))
                        .addItem(new ItemStack(Material.DIAMOND_BLOCK, 5))
                        .addExperience(30000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(48)
                .addPrerequisite(QuestID.MAIN_SACRIFICE_OF_HEROES);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_NEW_ERA_NAME, who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_NEW_ERA_INFO, who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "kingdom_herald" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_NEW_ERA_OBJECTIVES_KINGDOM_HERALD, who);
            case "new_capital" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_NEW_ERA_OBJECTIVES_NEW_CAPITAL, who);
            case "smooth_stone_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_NEW_ERA_OBJECTIVES_SMOOTH_STONE_COLLECT, who);
            case "unity_plaza" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_NEW_ERA_OBJECTIVES_UNITY_PLAZA, who);
            case "paper_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_NEW_ERA_OBJECTIVES_PAPER_COLLECT, who);
            case "peace_ambassador" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_NEW_ERA_OBJECTIVES_PEACE_AMBASSADOR, who);
            case "emerald_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_NEW_ERA_OBJECTIVES_EMERALD_COLLECT, who);
            case "harmony_gardens" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_NEW_ERA_OBJECTIVES_HARMONY_GARDENS, who);
            case "era_chronicler" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_NEW_ERA_OBJECTIVES_ERA_CHRONICLER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_NEW_ERA_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_NEW_ERA_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_NEW_ERA_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_NEW_ERA_DECLINE, who);
    }
}