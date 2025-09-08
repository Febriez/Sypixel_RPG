package com.febrie.rpg.quest.impl.main.chapter6;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
                        new InteractNPCObjective("legacy_keeper", "legacy_keeper", 1),
                        new VisitLocationObjective("heroes_academy", "heroes_academy_area"),
                        new CollectItemObjective("wisdom_scroll", Material.MAP, 20),
                        new CollectItemObjective("heroic_relic", Material.NETHERITE_INGOT, 10),
                        new VisitLocationObjective("monument_site", "monument_site_area"),
                        new CollectItemObjective("marble_block", Material.QUARTZ_BLOCK, 50),
                        new CollectItemObjective("eternal_flame", Material.SOUL_TORCH, 12),
                        new VisitLocationObjective("hall_of_legends", "hall_of_legends_area"),
                        new InteractNPCObjective("master_chronicler", "master_chronicler", 1)
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
        return LangManager.text(LangKey.QUEST_MAIN_LEGACY_OF_HEROES_NAME, who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_LEGACY_OF_HEROES_INFO, who);
    }
    
    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "legacy_keeper" -> LangManager.list(LangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_LEGACY_KEEPER, who);
            case "heroes_academy" -> LangManager.list(LangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_HEROES_ACADEMY, who);
            case "wisdom_scroll" -> LangManager.list(LangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_WISDOM_SCROLL, who);
            case "heroic_relic" -> LangManager.list(LangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_HEROIC_RELIC, who);
            case "monument_site" -> LangManager.list(LangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_MONUMENT_SITE, who);
            case "marble_block" -> LangManager.list(LangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_MARBLE_BLOCK, who);
            case "eternal_flame" -> LangManager.list(LangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_ETERNAL_FLAME, who);
            case "hall_of_legends" -> LangManager.list(LangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_HALL_OF_LEGENDS, who);
            case "master_chronicler" -> LangManager.list(LangKey.QUEST_MAIN_LEGACY_OF_HEROES_OBJECTIVES_MASTER_CHRONICLER, who);
            default -> List.of(Component.text("Objective: " + objective.getId()));
        };
    }
    
    @Override
    public int getDialogCount() {
        return 5;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_LEGACY_OF_HEROES_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_LEGACY_OF_HEROES_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_LEGACY_OF_HEROES_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_LEGACY_OF_HEROES_DECLINE, who);
    }
}