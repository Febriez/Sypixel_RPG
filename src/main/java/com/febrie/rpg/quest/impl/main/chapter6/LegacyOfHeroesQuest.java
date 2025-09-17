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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Chapter 6: Legacy of Heroes Quest
 * Establish monuments and institutions to preserve heroic deeds
 *
 * @author Febrie
 */
public class LegacyOfHeroesQuest extends Quest {
    
    /**
     * Default constructor
     */
    public LegacyOfHeroesQuest() {
        super(createBuilder());
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_LEGACY_OF_HEROES)
                .objectives(List.of(
                        new InteractNPCObjective("legacy_keeper", "legacy_keeper"),
                        new VisitLocationObjective("heroes_academy", "heroes_academy_area"),
                        new CollectItemObjective("map_collect", Material.MAP, 20),
                        new CollectItemObjective("netherite_ingot_collect", Material.NETHERITE_INGOT, 10),
                        new VisitLocationObjective("monument_site", "monument_site_area"),
                        new CollectItemObjective("quartz_block_collect", Material.QUARTZ_BLOCK, 50),
                        new CollectItemObjective("soul_torch_collect", Material.SOUL_TORCH, 12),
                        new VisitLocationObjective("hall_of_legends", "hall_of_legends_area"),
                        new InteractNPCObjective("master_chronicler", "master_chronicler")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 25000)
                        .addItem(new ItemStack(Material.NETHERITE_PICKAXE))
                        .addItem(new ItemStack(Material.BOOK, 64))
                        .addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 50))
                        .addExperience(40000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(52)
                .addPrerequisite(QuestID.MAIN_RESTORATION);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_LEGACY_OF_HEROES_NAME, who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_LEGACY_OF_HEROES_INFO, who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "legacy_keeper" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_LEGACY_KEEPER, who);
            case "heroes_academy" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_HEROES_ACADEMY, who);
            case "map_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_MAP_COLLECT, who);
            case "netherite_ingot_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_NETHERITE_INGOT_COLLECT, who);
            case "monument_site" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_MONUMENT_SITE, who);
            case "quartz_block_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_QUARTZ_BLOCK_COLLECT, who);
            case "soul_torch_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_SOUL_TORCH_COLLECT, who);
            case "hall_of_legends" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_HALL_OF_LEGENDS, who);
            case "master_chronicler" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_MASTER_CHRONICLER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_LEGACY_OF_HEROES_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_LEGACY_OF_HEROES_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_LEGACY_OF_HEROES_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_LEGACY_OF_HEROES_DECLINE, who);
    }
}