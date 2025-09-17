package com.febrie.rpg.quest.impl.main.chapter2;

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
 * Chapter 2: Corrupted Lands Quest
 * Venture into the corrupted territories to cleanse the dark influence
 *
 * @author Febrie
 */
public class CorruptedLandsQuest extends Quest {
    
    /**
     * Default constructor
     */
    public CorruptedLandsQuest() {
        super(createBuilder());
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_CORRUPTED_LANDS)
                .objectives(List.of(
                        new InteractNPCObjective("druid_elder", "druid_elder"),
                        new VisitLocationObjective("corrupted_grove", "corrupted_grove_area"),
                        new KillMobObjective("kill_zombies", EntityType.ZOMBIE, 20),
                        new KillMobObjective("kill_skeletons", EntityType.SKELETON, 15),
                        new CollectItemObjective("ghast_tear_collect", Material.GHAST_TEAR, 8),
                        new VisitLocationObjective("cleansing_shrine", "cleansing_shrine_area"),
                        new InteractNPCObjective("nature_guardian", "nature_guardian")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 3000)
                        .addItem(new ItemStack(Material.DIAMOND_CHESTPLATE))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 3))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING))
                        .addExperience(6000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(22)
                .addPrerequisite(QuestID.MAIN_SHADOW_INVASION);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_CORRUPTED_LANDS_NAME, who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_CORRUPTED_LANDS_INFO, who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "druid_elder" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CORRUPTED_LANDS_OBJECTIVES_DRUID_ELDER, who);
            case "corrupted_grove" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CORRUPTED_LANDS_OBJECTIVES_CORRUPTED_GROVE, who);
            case "kill_zombies" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CORRUPTED_LANDS_OBJECTIVES_KILL_ZOMBIES, who);
            case "kill_skeletons" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CORRUPTED_LANDS_OBJECTIVES_KILL_SKELETONS, who);
            case "ghast_tear_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CORRUPTED_LANDS_OBJECTIVES_GHAST_TEAR_COLLECT, who);
            case "cleansing_shrine" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CORRUPTED_LANDS_OBJECTIVES_CLEANSING_SHRINE, who);
            case "nature_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CORRUPTED_LANDS_OBJECTIVES_NATURE_GUARDIAN, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_CORRUPTED_LANDS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_CORRUPTED_LANDS_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_CORRUPTED_LANDS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_CORRUPTED_LANDS_DECLINE, who);
    }
}