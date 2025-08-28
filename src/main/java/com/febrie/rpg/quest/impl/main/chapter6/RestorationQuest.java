package com.febrie.rpg.quest.impl.main.chapter6;

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
 * Chapter 6: Restoration Quest
 * Begin the great work of restoring the world after the darkness
 *
 * @author Febrie
 */
public class RestorationQuest extends Quest {
    
    /**
     * Quest builder
     */
    private static class RestorationBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new RestorationQuest(this);
        }
    }
    
    /**
     * Default constructor
     */
    public RestorationQuest() {
        this(createBuilder());
    }
    
    /**
     * Builder constructor
     */
    private RestorationQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new RestorationBuilder()
                .id(QuestID.MAIN_RESTORATION)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("restoration_overseer", "restoration_overseer"),
                        new VisitLocationObjective("scarred_lands", "scarred_lands_area"),
                        new CollectItemObjective("healing_herbs", Material.SWEET_BERRIES, 100),
                        new CollectItemObjective("fertile_soil", Material.DIRT, 200),
                        new VisitLocationObjective("poisoned_springs", "poisoned_springs_area"),
                        new CollectItemObjective("purification_crystal", Material.PRISMARINE_CRYSTALS, 15),
                        new KillMobObjective("kill_zombies", EntityType.ZOMBIE, 40),
                        new VisitLocationObjective("renewal_grove", "renewal_grove_area"),
                        new InteractNPCObjective("nature_guardian", "nature_guardian")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 20000)
                        .addItem(new ItemStack(Material.NETHERITE_HOE))
                        .addItem(new ItemStack(Material.BONE_MEAL, 64))
                        .addItem(new ItemStack(Material.GOLDEN_CARROT, 32))
                        .addExperience(35000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(50)
                .addPrerequisite(QuestID.MAIN_NEW_ERA);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.main.restoration.name");
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            description.add(Component.translatable("quest.main.restoration.description." + i));
        }
        return description;
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return Component.translatable("quest.main.restoration.objectives." + objective.getId());
    }
    
    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.main.restoration.dialogs.0");
            case 1 -> Component.translatable("quest.main.restoration.dialogs.1");
            case 2 -> Component.translatable("quest.main.restoration.dialogs.2");
            case 3 -> Component.translatable("quest.main.restoration.dialogs.3");
            case 4 -> Component.translatable("quest.main.restoration.dialogs.4");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.main.restoration.npc-name");
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.main.restoration.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.main.restoration.decline");
    }
}