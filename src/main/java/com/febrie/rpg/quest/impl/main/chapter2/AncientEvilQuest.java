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
 * Chapter 2: Ancient Evil Quest
 * Confront the awakening ancient evil that threatens the world
 *
 * @author Febrie
 */
public class AncientEvilQuest extends Quest {
    
    /**
     * Quest builder
     */
    private static class AncientEvilBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new AncientEvilQuest(this);
        }
    }
    
    /**
     * Default constructor
     */
    public AncientEvilQuest() {
        this(createBuilder());
    }
    
    /**
     * Builder constructor
     */
    private AncientEvilQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new AncientEvilBuilder()
                .id(QuestID.MAIN_ANCIENT_EVIL)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("ancient_sage", "ancient_sage"),
                        new VisitLocationObjective("forbidden_temple", "forbidden_temple_area"),
                        new KillMobObjective("kill_wither_skeletons", EntityType.WITHER_SKELETON, 20),
                        new KillMobObjective("kill_blazes", EntityType.BLAZE, 15),
                        new CollectItemObjective("dark_crystal", Material.END_CRYSTAL, 4),
                        new VisitLocationObjective("evil_altar", "evil_altar_area"),
                        new CollectItemObjective("purified_soul", Material.SOUL_LANTERN, 6),
                        new KillMobObjective("kill_ravagers", EntityType.RAVAGER, 5),
                        new InteractNPCObjective("light_guardian", "light_guardian")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 4000)
                        .addItem(new ItemStack(Material.NETHERITE_HELMET))
                        .addItem(new ItemStack(Material.BEACON))
                        .addItem(new ItemStack(Material.NETHER_STAR, 2))
                        .addExperience(8000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(26)
                .addPrerequisite(QuestID.MAIN_LOST_KINGDOM);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.main.ancient-evil.name");
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            description.add(Component.translatable("quest.main.ancient-evil.description." + i));
        }
        return description;
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return Component.translatable("quest.main.ancient-evil.objectives." + objective.getId());
    }
    
    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.main.ancient-evil.dialogs.0");
            case 1 -> Component.translatable("quest.main.ancient-evil.dialogs.1");
            case 2 -> Component.translatable("quest.main.ancient-evil.dialogs.2");
            case 3 -> Component.translatable("quest.main.ancient-evil.dialogs.3");
            case 4 -> Component.translatable("quest.main.ancient-evil.dialogs.4");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.main.ancient-evil.npc-name");
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.main.ancient-evil.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.main.ancient-evil.decline");
    }
}