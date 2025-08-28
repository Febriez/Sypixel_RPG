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
 * Chapter 2: Corrupted Lands Quest
 * Venture into the corrupted territories to cleanse the dark influence
 *
 * @author Febrie
 */
public class CorruptedLandsQuest extends Quest {
    
    /**
     * Quest builder
     */
    private static class CorruptedLandsBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new CorruptedLandsQuest(this);
        }
    }
    
    /**
     * Default constructor
     */
    public CorruptedLandsQuest() {
        this(createBuilder());
    }
    
    /**
     * Builder constructor
     */
    private CorruptedLandsQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new CorruptedLandsBuilder()
                .id(QuestID.MAIN_CORRUPTED_LANDS)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("druid_elder", "druid_elder"),
                        new VisitLocationObjective("corrupted_grove", "corrupted_grove_area"),
                        new KillMobObjective("kill_zombies", EntityType.ZOMBIE, 20),
                        new KillMobObjective("kill_skeletons", EntityType.SKELETON, 15),
                        new CollectItemObjective("corrupted_essence", Material.GHAST_TEAR, 8),
                        new VisitLocationObjective("cleansing_shrine", "cleansing_shrine_area"),
                        new InteractNPCObjective("nature_guardian", "nature_guardian")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 3000)
                        .addItem(new ItemStack(Material.DIAMOND_CHESTPLATE))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 3))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING))
                        .addExperience(6000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(22)
                .addPrerequisite(QuestID.MAIN_SHADOW_INVASION);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.main.corrupted-lands.name");
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            description.add(Component.translatable("quest.main.corrupted-lands.description." + i));
        }
        return description;
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return Component.translatable("quest.main.corrupted-lands.objectives." + objective.getId());
    }
    
    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.main.corrupted-lands.dialogs.0");
            case 1 -> Component.translatable("quest.main.corrupted-lands.dialogs.1");
            case 2 -> Component.translatable("quest.main.corrupted-lands.dialogs.2");
            case 3 -> Component.translatable("quest.main.corrupted-lands.dialogs.3");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.main.corrupted-lands.npc-name");
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.main.corrupted-lands.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.main.corrupted-lands.decline");
    }
}