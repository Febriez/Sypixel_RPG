package com.febrie.rpg.quest.impl.main.chapter6;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Chapter 6: Restoration Quest
 * Begin the great work of restoring the world after the darkness
 *
 * @author Febrie
 */
public class RestorationQuest extends Quest {
    
    /**
     * Default constructor
     */
    public RestorationQuest() {
        super(createBuilder());
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_RESTORATION)
                .objectives(List.of(
                        new InteractNPCObjective("restoration_overseer", "restoration_overseer"),
                        new VisitLocationObjective("scarred_lands", "scarred_lands_area"),
                        new CollectItemObjective("sweet_berries_collect", Material.SWEET_BERRIES, 100),
                        new CollectItemObjective("dirt_collect", Material.DIRT, 200),
                        new VisitLocationObjective("poisoned_springs", "poisoned_springs_area"),
                        new CollectItemObjective("prismarine_crystals_collect", Material.PRISMARINE_CRYSTALS, 15),
                        new KillMobObjective("kill_zombies", EntityType.ZOMBIE, 40),
                        new VisitLocationObjective("renewal_grove", "renewal_grove_area"),
                        new InteractNPCObjective("nature_guardian", "nature_guardian")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 20000)
                        .addItem(new ItemStack(Material.NETHERITE_HOE))
                        .addItem(new ItemStack(Material.BONE_MEAL, 64))
                        .addItem(new ItemStack(Material.GOLDEN_CARROT, 32))
                        .addExperience(35000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(50)
                .addPrerequisite(QuestID.MAIN_NEW_ERA);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_RESTORATION_NAME, who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_RESTORATION_INFO, who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "restoration_overseer" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_RESTORATION_OBJECTIVES_RESTORATION_OVERSEER, who);
            case "scarred_lands" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_RESTORATION_OBJECTIVES_SCARRED_LANDS, who);
            case "sweet_berries_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_RESTORATION_OBJECTIVES_SWEET_BERRIES_COLLECT, who);
            case "dirt_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_RESTORATION_OBJECTIVES_DIRT_COLLECT, who);
            case "poisoned_springs" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_RESTORATION_OBJECTIVES_POISONED_SPRINGS, who);
            case "prismarine_crystals_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_RESTORATION_OBJECTIVES_PRISMARINE_CRYSTALS_COLLECT, who);
            case "kill_zombies" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_RESTORATION_OBJECTIVES_KILL_ZOMBIES, who);
            case "renewal_grove" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_RESTORATION_OBJECTIVES_RENEWAL_GROVE, who);
            case "nature_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_RESTORATION_OBJECTIVES_NATURE_GUARDIAN, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_RESTORATION_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_RESTORATION_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_RESTORATION_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_RESTORATION_DECLINE, who);
    }
}