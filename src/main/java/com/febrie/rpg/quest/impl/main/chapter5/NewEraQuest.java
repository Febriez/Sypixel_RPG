package com.febrie.rpg.quest.impl.main.chapter5;

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
                .objectives(Arrays.asList(
                        new InteractNPCObjective("kingdom_herald", "kingdom_herald"),
                        new VisitLocationObjective("new_capital", "new_capital_area"),
                        new CollectItemObjective("foundation_stone", Material.SMOOTH_STONE, 25),
                        new VisitLocationObjective("unity_plaza", "unity_plaza_area"),
                        new CollectItemObjective("peace_treaty", Material.PAPER, 5),
                        new InteractNPCObjective("peace_ambassador", "peace_ambassador"),
                        new CollectItemObjective("prosperity_crystal", Material.EMERALD, 20),
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
        return LangManager.get("quest.main.new_era.name", who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.main.new_era.info", who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.main.new_era.objectives." + objective.getId(), who);
    }
    
    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.main.new_era.dialogs.0", who);
            case 1 -> LangManager.get("quest.main.new_era.dialogs.1", who);
            case 2 -> LangManager.get("quest.main.new_era.dialogs.2", who);
            case 3 -> LangManager.get("quest.main.new_era.dialogs.3", who);
            case 4 -> LangManager.get("quest.main.new_era.dialogs.4", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.main.new_era.npc_name", who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.main.new_era.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.main.new_era.decline", who);
    }
}