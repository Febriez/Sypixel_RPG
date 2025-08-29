package com.febrie.rpg.quest.impl.main.chapter6;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
                .objectives(Arrays.asList(
                        new InteractNPCObjective("legacy_keeper", "legacy_keeper"),
                        new VisitLocationObjective("heroes_academy", "heroes_academy_area"),
                        new CollectItemObjective("wisdom_scroll", Material.MAP, 20),
                        new CollectItemObjective("heroic_relic", Material.NETHERITE_INGOT, 10),
                        new VisitLocationObjective("monument_site", "monument_site_area"),
                        new CollectItemObjective("marble_block", Material.QUARTZ_BLOCK, 50),
                        new CollectItemObjective("eternal_flame", Material.SOUL_TORCH, 12),
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
        return LangManager.get("quest.main.legacy_of_heroes.name", who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.main.legacy_of_heroes.info", who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.main.legacy_of_heroes.objectives." + objective.getId(), who);
    }
    
    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.main.legacy_of_heroes.dialogs.0", who);
            case 1 -> LangManager.get("quest.main.legacy_of_heroes.dialogs.1", who);
            case 2 -> LangManager.get("quest.main.legacy_of_heroes.dialogs.2", who);
            case 3 -> LangManager.get("quest.main.legacy_of_heroes.dialogs.3", who);
            case 4 -> LangManager.get("quest.main.legacy_of_heroes.dialogs.4", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.main.legacy_of_heroes.npc_name", who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.main.legacy_of_heroes.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.main.legacy_of_heroes.decline", who);
    }
}