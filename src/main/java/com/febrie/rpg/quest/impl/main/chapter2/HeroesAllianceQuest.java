package com.febrie.rpg.quest.impl.main.chapter2;

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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * Chapter 2: Heroes Alliance Quest
 * Unite with legendary heroes to form an alliance against the darkness
 *
 * @author Febrie
 */
public class HeroesAllianceQuest extends Quest {
    
    /**
     * Default constructor
     */
    public HeroesAllianceQuest() {
        super(createBuilder());
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_HEROES_ALLIANCE)
                .objectives(List.of(
                        new InteractNPCObjective("alliance_messenger", "alliance_messenger", 1),
                        new VisitLocationObjective("heroes_stronghold", "heroes_stronghold_area"),
                        new InteractNPCObjective("legendary_warrior", "legendary_warrior", 1),
                        new InteractNPCObjective("arcane_mage", "arcane_mage", 1),
                        new InteractNPCObjective("shadow_assassin", "shadow_assassin", 1),
                        new CollectItemObjective("alliance_token", Material.NETHERITE_INGOT, 10),
                        new KillMobObjective("kill_ender_dragon", EntityType.ENDER_DRAGON, 1),
                        new KillMobObjective("kill_withers", EntityType.WITHER, 2),
                        new VisitLocationObjective("alliance_hall", "alliance_hall_area"),
                        new InteractNPCObjective("heroes_council", "heroes_council", 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 5000)
                        .addItem(new ItemStack(Material.NETHERITE_CHESTPLATE))
                        .addItem(new ItemStack(Material.ELYTRA))
                        .addItem(new ItemStack(Material.DRAGON_HEAD))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 8))
                        .addExperience(10000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(28)
                .addPrerequisite(QuestID.MAIN_ANCIENT_EVIL);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_HEROES_ALLIANCE_NAME, who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_HEROES_ALLIANCE_INFO, who);
    }
    
    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "alliance_messenger" -> LangManager.list(LangKey.QUEST_MAIN_HEROES_ALLIANCE_OBJECTIVES_ALLIANCE_MESSENGER, who);
            case "heroes_stronghold" -> LangManager.list(LangKey.QUEST_MAIN_HEROES_ALLIANCE_OBJECTIVES_HEROES_STRONGHOLD, who);
            case "legendary_warrior" -> LangManager.list(LangKey.QUEST_MAIN_HEROES_ALLIANCE_OBJECTIVES_LEGENDARY_WARRIOR, who);
            case "arcane_mage" -> LangManager.list(LangKey.QUEST_MAIN_HEROES_ALLIANCE_OBJECTIVES_ARCANE_MAGE, who);
            case "shadow_assassin" -> LangManager.list(LangKey.QUEST_MAIN_HEROES_ALLIANCE_OBJECTIVES_SHADOW_ASSASSIN, who);
            case "alliance_token" -> LangManager.list(LangKey.QUEST_MAIN_HEROES_ALLIANCE_OBJECTIVES_ALLIANCE_TOKEN, who);
            case "kill_ender_dragon" -> LangManager.list(LangKey.QUEST_MAIN_HEROES_ALLIANCE_OBJECTIVES_KILL_ENDER_DRAGON, who);
            case "kill_withers" -> LangManager.list(LangKey.QUEST_MAIN_HEROES_ALLIANCE_OBJECTIVES_KILL_WITHERS, who);
            case "alliance_hall" -> LangManager.list(LangKey.QUEST_MAIN_HEROES_ALLIANCE_OBJECTIVES_ALLIANCE_HALL, who);
            case "heroes_council" -> LangManager.list(LangKey.QUEST_MAIN_HEROES_ALLIANCE_OBJECTIVES_HEROES_COUNCIL, who);
            default -> List.of(Component.text("Objective: " + objective.getId()));
        };
    }
    
    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_HEROES_ALLIANCE_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_HEROES_ALLIANCE_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_HEROES_ALLIANCE_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_HEROES_ALLIANCE_DECLINE, who);
    }
}