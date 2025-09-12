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

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Chapter 6: Eternal Guardian Quest
 * Take on the mantle of eternal guardian to protect the world
 *
 * @author Febrie
 */
public class EternalGuardianQuest extends Quest {
    
    /**
     * Default constructor
     */
    public EternalGuardianQuest() {
        super(createBuilder());
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_ETERNAL_GUARDIAN)
                .objectives(List.of(
                        new InteractNPCObjective("eternal_sage", "eternal_sage"),
                        new VisitLocationObjective("guardian_sanctum", "guardian_sanctum_area"),
                        new CollectItemObjective("shield_collect", Material.SHIELD, 5),
                        new CollectItemObjective("turtle_helmet_collect", Material.TURTLE_HELMET, 3),
                        new VisitLocationObjective("watchtower_peak", "watchtower_peak_area"),
                        new CollectItemObjective("observer_collect", Material.OBSERVER, 8),
                        new CollectItemObjective("written_book_collect", Material.WRITTEN_BOOK, 1),
                        new VisitLocationObjective("nexus_of_worlds", "nexus_of_worlds_area"),
                        new InteractNPCObjective("world_spirit", "world_spirit")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 30000)
                        .addItem(new ItemStack(Material.NETHERITE_CHESTPLATE))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 20))
                        .addItem(new ItemStack(Material.NETHER_STAR, 5))
                        .addExperience(50000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(55)
                .addPrerequisite(QuestID.MAIN_LEGACY_OF_HEROES);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_ETERNAL_GUARDIAN_NAME, who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_ETERNAL_GUARDIAN_INFO, who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "eternal_sage" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ETERNAL_GUARDIAN_OBJECTIVES_ETERNAL_SAGE, who);
            case "guardian_sanctum" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ETERNAL_GUARDIAN_OBJECTIVES_GUARDIAN_SANCTUM, who);
            case "shield_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ETERNAL_GUARDIAN_OBJECTIVES_SHIELD_COLLECT, who);
            case "turtle_helmet_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ETERNAL_GUARDIAN_OBJECTIVES_TURTLE_HELMET_COLLECT, who);
            case "watchtower_peak" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ETERNAL_GUARDIAN_OBJECTIVES_WATCHTOWER_PEAK, who);
            case "observer_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ETERNAL_GUARDIAN_OBJECTIVES_OBSERVER_COLLECT, who);
            case "written_book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ETERNAL_GUARDIAN_OBJECTIVES_WRITTEN_BOOK_COLLECT, who);
            case "nexus_of_worlds" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ETERNAL_GUARDIAN_OBJECTIVES_NEXUS_OF_WORLDS, who);
            case "world_spirit" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ETERNAL_GUARDIAN_OBJECTIVES_WORLD_SPIRIT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_ETERNAL_GUARDIAN_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_ETERNAL_GUARDIAN_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_ETERNAL_GUARDIAN_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_ETERNAL_GUARDIAN_DECLINE, who);
    }
}