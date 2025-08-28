package com.febrie.rpg.quest.impl.main.chapter5;

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
 * Chapter 5: Gathering Storm Quest
 * Storm clouds gather as the final confrontation approaches
 *
 * @author Febrie
 */
public class GatheringStormQuest extends Quest {
    
    /**
     * Quest builder
     */
    private static class GatheringStormBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new GatheringStormQuest(this);
        }
    }
    
    /**
     * Default constructor
     */
    public GatheringStormQuest() {
        this(createBuilder());
    }
    
    /**
     * Builder constructor
     */
    private GatheringStormQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new GatheringStormBuilder()
                .id(QuestID.MAIN_GATHERING_STORM)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("storm_warden", "storm_warden"),
                        new VisitLocationObjective("storm_peaks", "storm_peaks_area"),
                        new CollectItemObjective("storm_crystal", Material.AMETHYST_CLUSTER, 8),
                        new KillMobObjective("kill_phantoms", EntityType.PHANTOM, 25),
                        new CollectItemObjective("lightning_rod", Material.LIGHTNING_ROD, 5),
                        new KillMobObjective("kill_charged_creepers", EntityType.CREEPER, 10),
                        new VisitLocationObjective("thunder_spire", "thunder_spire_area"),
                        new InteractNPCObjective("storm_keeper", "storm_keeper")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 6000)
                        .addItem(new ItemStack(Material.NETHERITE_CHESTPLATE))
                        .addItem(new ItemStack(Material.TRIDENT))
                        .addItem(new ItemStack(Material.CONDUIT, 2))
                        .addExperience(12000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(40)
                .addPrerequisite(QuestID.MAIN_DIMENSIONAL_RIFT);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.main.gathering-storm.name");
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            description.add(Component.translatable("quest.main.gathering-storm.description." + i));
        }
        return description;
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return Component.translatable("quest.main.gathering-storm.objectives." + objective.getId());
    }
    
    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.main.gathering-storm.dialogs.0");
            case 1 -> Component.translatable("quest.main.gathering-storm.dialogs.1");
            case 2 -> Component.translatable("quest.main.gathering-storm.dialogs.2");
            case 3 -> Component.translatable("quest.main.gathering-storm.dialogs.3");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.main.gathering-storm.npc-name");
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.main.gathering-storm.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.main.gathering-storm.decline");
    }
}