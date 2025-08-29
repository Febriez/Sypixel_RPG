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
                .objectives(Arrays.asList(
                        new InteractNPCObjective("eternal_sage", "eternal_sage"),
                        new VisitLocationObjective("guardian_sanctum", "guardian_sanctum_area"),
                        new CollectItemObjective("guardian_sigil", Material.SHIELD, 5),
                        new CollectItemObjective("essence_of_protection", Material.TURTLE_HELMET, 3),
                        new VisitLocationObjective("watchtower_peak", "watchtower_peak_area"),
                        new CollectItemObjective("vigilant_crystal", Material.OBSERVER, 8),
                        new CollectItemObjective("eternal_oath", Material.WRITTEN_BOOK, 1),
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
        return LangManager.get("quest.main.eternal_guardian.name", who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.main.eternal_guardian.info", who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.main.eternal_guardian.objectives." + objective.getId(), who);
    }
    
    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.main.eternal_guardian.dialogs.0", who);
            case 1 -> LangManager.get("quest.main.eternal_guardian.dialogs.1", who);
            case 2 -> LangManager.get("quest.main.eternal_guardian.dialogs.2", who);
            case 3 -> LangManager.get("quest.main.eternal_guardian.dialogs.3", who);
            case 4 -> LangManager.get("quest.main.eternal_guardian.dialogs.4", who);
            case 5 -> LangManager.get("quest.main.eternal_guardian.dialogs.5", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.main.eternal_guardian.npc_name", who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.main.eternal_guardian.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.main.eternal_guardian.decline", who);
    }
}