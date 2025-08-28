package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Side Quest: Frozen Peaks
 * Brave the treacherous frozen peaks to help a climber reach the summit
 *
 * @author Febrie
 */
public class FrozenPeaksQuest extends Quest {

    /**
     * Quest builder
     */
    private static class FrozenPeaksBuilder extends QuestBuilder {
        @Override
        public @NotNull Quest build() {
            return new FrozenPeaksQuest(this);
        }
    }

    /**
     * Default constructor
     */
    public FrozenPeaksQuest() {
        this(createBuilder());
    }

    /**
     * Builder constructor
     */
    private FrozenPeaksQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * Quest setup
     */
    private static QuestBuilder createBuilder() {
        return new FrozenPeaksBuilder()
                .id(QuestID.SIDE_FROZEN_PEAKS)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_mountain_climber", "mountain_climber"),
                        new VisitLocationObjective("visit_ice_cliffs", "ice_cliffs"),
                        new KillMobObjective("kill_polar_bears", EntityType.POLAR_BEAR, 10),
                        new CollectItemObjective("collect_ice_shards", Material.ICE, 25),
                        new VisitLocationObjective("visit_frozen_summit", "frozen_summit"),
                        new CollectItemObjective("collect_eternal_ice", Material.PACKED_ICE, 8)
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(4500)
                        .addCurrency(CurrencyType.GOLD, 1200)
                        .addItem(new ItemStack(Material.DIAMOND_BOOTS, 1))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(28);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.side.frozen-peaks.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return Arrays.asList(
                Component.translatable("quest.side.frozen-peaks.description.0"),
                Component.translatable("quest.side.frozen-peaks.description.1"),
                Component.translatable("quest.side.frozen-peaks.description.2"),
                Component.translatable("quest.side.frozen-peaks.description.3")
        );
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.side.frozen-peaks.objectives." + objective.getId();
        return Component.translatable(key);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.frozen-peaks.dialogs.0");
            case 1 -> Component.translatable("quest.side.frozen-peaks.dialogs.1");
            case 2 -> Component.translatable("quest.side.frozen-peaks.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.frozen-peaks.npc-name");
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.frozen-peaks.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.frozen-peaks.decline");
    }
}