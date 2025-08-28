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
 * Side Quest: Hidden Valley
 * Discover a secret valley protected by ancient magic
 *
 * @author Febrie
 */
public class HiddenValleyQuest extends Quest {

    /**
     * Quest builder
     */
    private static class HiddenValleyBuilder extends QuestBuilder {
        @Override
        public @NotNull Quest build() {
            return new HiddenValleyQuest(this);
        }
    }

    /**
     * Default constructor
     */
    public HiddenValleyQuest() {
        this(createBuilder());
    }

    /**
     * Builder constructor
     */
    private HiddenValleyQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * Quest setup
     */
    private static QuestBuilder createBuilder() {
        return new HiddenValleyBuilder()
                .id(QuestID.SIDE_HIDDEN_VALLEY)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_valley_scout", "valley_scout"),
                        new VisitLocationObjective("visit_mountain_pass", "mountain_pass"),
                        new CollectItemObjective("collect_mountain_flower", Material.AZURE_BLUET, 12),
                        new VisitLocationObjective("visit_hidden_entrance", "hidden_entrance"),
                        new KillMobObjective("kill_wolves", EntityType.WOLF, 8),
                        new VisitLocationObjective("visit_valley_heart", "valley_heart"),
                        new CollectItemObjective("collect_valley_crystal", Material.EMERALD, 3),
                        new InteractNPCObjective("talk_valley_guardian", "valley_guardian")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(2500)
                        .addCurrency(CurrencyType.GOLD, 600)
                        .addItem(new ItemStack(Material.DIAMOND_SWORD, 1))
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 3))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(18);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.side.hidden-valley.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return Arrays.asList(
                Component.translatable("quest.side.hidden-valley.description.0"),
                Component.translatable("quest.side.hidden-valley.description.1"),
                Component.translatable("quest.side.hidden-valley.description.2"),
                Component.translatable("quest.side.hidden-valley.description.3")
        );
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.side.hidden-valley.objectives." + objective.getId();
        return Component.translatable(key);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.hidden-valley.dialogs.0");
            case 1 -> Component.translatable("quest.side.hidden-valley.dialogs.1");
            case 2 -> Component.translatable("quest.side.hidden-valley.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.hidden-valley.npc-name");
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.hidden-valley.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.hidden-valley.decline");
    }
}