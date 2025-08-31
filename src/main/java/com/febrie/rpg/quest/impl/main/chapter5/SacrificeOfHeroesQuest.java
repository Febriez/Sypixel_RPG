package com.febrie.rpg.quest.impl.main.chapter5;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangHelper;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
                .objectives(Arrays.asList(
                        new InteractNPCObjective("memorial_keeper", "memorial_keeper"),
                        new VisitLocationObjective("heroes_memorial", "heroes_memorial_area"),
                        new CollectItemObjective("hero_medallion", Material.GOLDEN_APPLE, 10),
                        new VisitLocationObjective("eternal_flame_altar", "eternal_flame_altar_area"),
                        new CollectItemObjective("essence_of_valor", Material.GLOWSTONE_DUST, 50),
                        new KillMobObjective("kill_vexes", EntityType.VEX, 30),
                        new CollectItemObjective("spirit_crystal", Material.SOUL_LANTERN, 15),
                        new VisitLocationObjective("sanctuary_of_light", "sanctuary_of_light_area"),
                        new InteractNPCObjective("spirit_guide", "spirit_guide")
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
        return LangHelper.text(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_NAME, who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_INFO, who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.main.sacrifice_of_heroes.objectives." + objective.getId(), who);
    }
    
    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangHelper.text(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_DIALOGS_0, who);
            case 1 -> LangHelper.text(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_DIALOGS_1, who);
            case 2 -> LangHelper.text(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_DIALOGS_2, who);
            case 3 -> LangHelper.text(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_DIALOGS_3, who);
            case 4 -> LangHelper.text(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_DIALOGS_4, who);
            case 5 -> LangHelper.text(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_DIALOGS_5, who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_SACRIFICE_OF_HEROES_DECLINE, who);
    }
}