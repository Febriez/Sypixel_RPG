package com.febrie.rpg.quest.impl.main.chapter5;

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
import java.util.List;

/**
 * Chapter 5: Sacrifice of Heroes Quest
 * Honor the fallen and ensure their sacrifice was not in vain
 *
 * @author Febrie
 */
public class SacrificeOfHeroesQuest extends Quest {
    
    /**
     * Default constructor
     */
    public SacrificeOfHeroesQuest() {
        super(createBuilder());
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_SACRIFICE_OF_HEROES)
                .objectives(List.of(
                        new InteractNPCObjective("memorial_keeper", "memorial_keeper", 1),
                        new VisitLocationObjective("heroes_memorial", "heroes_memorial_area"),
                        new CollectItemObjective("hero_medallion", Material.GOLDEN_APPLE, 10),
                        new VisitLocationObjective("eternal_flame_altar", "eternal_flame_altar_area"),
                        new CollectItemObjective("essence_of_valor", Material.GLOWSTONE_DUST, 50),
                        new KillMobObjective("kill_vexes", EntityType.VEX, 30),
                        new CollectItemObjective("spirit_crystal", Material.SOUL_LANTERN, 15),
                        new VisitLocationObjective("sanctuary_of_light", "sanctuary_of_light_area"),
                        new InteractNPCObjective("spirit_guide", "spirit_guide", 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 12500)
                        .addItem(new ItemStack(Material.NETHERITE_SWORD))
                        .addItem(new ItemStack(Material.BEACON, 2))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 10))
                        .addExperience(25000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(46)
                .addPrerequisite(QuestID.MAIN_FINAL_BATTLE);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_NAME, who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_INFO, who);
    }
    
    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "memorial_keeper" -> LangManager.list(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_OBJECTIVES_MEMORIAL_KEEPER, who);
            case "heroes_memorial" -> LangManager.list(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_OBJECTIVES_HEROES_MEMORIAL, who);
            case "hero_medallion" -> LangManager.list(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_OBJECTIVES_HERO_MEDALLION, who);
            case "eternal_flame_altar" -> LangManager.list(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_OBJECTIVES_ETERNAL_FLAME_ALTAR, who);
            case "essence_of_valor" -> LangManager.list(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_OBJECTIVES_ESSENCE_OF_VALOR, who);
            case "kill_vexes" -> LangManager.list(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_OBJECTIVES_KILL_VEXES, who);
            case "spirit_crystal" -> LangManager.list(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_OBJECTIVES_SPIRIT_CRYSTAL, who);
            case "sanctuary_of_light" -> LangManager.list(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_OBJECTIVES_SANCTUARY_OF_LIGHT, who);
            case "spirit_guide" -> LangManager.list(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_OBJECTIVES_SPIRIT_GUIDE, who);
            default -> List.of(Component.text("Objective: " + objective.getId()));
        };
    }
    
    @Override
    public int getDialogCount() {
        return 6;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_DECLINE, who);
    }
}