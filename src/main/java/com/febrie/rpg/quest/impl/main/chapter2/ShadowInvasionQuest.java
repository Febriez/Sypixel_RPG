package com.febrie.rpg.quest.impl.main.chapter2;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
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
 * Chapter 2: Shadow Invasion Quest
 * The darkness begins to spread across the land
 *
 * @author Febrie
 */
public class ShadowInvasionQuest extends Quest {
    
    /**
     * Default constructor
     */
    public ShadowInvasionQuest() {
        super(createBuilder());
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_SHADOW_INVASION)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("scout_captain", "scout_captain"),
                        new VisitLocationObjective("shadow_portal", "shadow_portal_area"),
                        new KillMobObjective("kill_wither_skeletons", EntityType.WITHER_SKELETON, 15),
                        new KillMobObjective("kill_phantoms", EntityType.PHANTOM, 10),
                        new VisitLocationObjective("corrupted_fortress", "corrupted_fortress_area"),
                        new InteractNPCObjective("resistance_leader", "resistance_leader")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 2500)
                        .addItem(new ItemStack(Material.NETHERITE_SWORD))
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 5))
                        .addExperience(5000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(20)
                .addPrerequisite(QuestID.MAIN_GUARDIAN_AWAKENING);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.main.shadow-invasion.name");
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            description.add(Component.translatable("quest.main.shadow-invasion.description." + i));
        }
        return description;
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return Component.translatable("quest.main.shadow-invasion.objectives." + objective.getId());
    }
    
    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.main.shadow-invasion.dialogs.0");
            case 1 -> Component.translatable("quest.main.shadow-invasion.dialogs.1");
            case 2 -> Component.translatable("quest.main.shadow-invasion.dialogs.2");
            case 3 -> Component.translatable("quest.main.shadow-invasion.dialogs.3");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.main.shadow-invasion.npc-name");
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.main.shadow-invasion.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.main.shadow-invasion.decline");
    }
}