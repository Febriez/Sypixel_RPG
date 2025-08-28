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
 * Side Quest: Forgotten Temple
 * Help a scholar explore an ancient forgotten temple and recover sacred relics
 *
 * @author Febrie
 */
public class ForgottenTempleQuest extends Quest {

    /**
     * Quest builder
     */
    private static class ForgottenTempleBuilder extends QuestBuilder {
        @Override
        public @NotNull Quest build() {
            return new ForgottenTempleQuest(this);
        }
    }

    /**
     * Default constructor
     */
    public ForgottenTempleQuest() {
        this(createBuilder());
    }

    /**
     * Builder constructor
     */
    private ForgottenTempleQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * Quest setup
     */
    private static QuestBuilder createBuilder() {
        return new ForgottenTempleBuilder()
                .id(QuestID.SIDE_FORGOTTEN_TEMPLE)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_temple_scholar", "temple_scholar"),
                        new VisitLocationObjective("visit_temple_ruins", "temple_ruins"),
                        new KillMobObjective("kill_zombies", EntityType.ZOMBIE, 15),
                        new CollectItemObjective("collect_temple_key", Material.GOLDEN_SWORD, 1),
                        new VisitLocationObjective("visit_inner_sanctum", "inner_sanctum"),
                        new CollectItemObjective("collect_sacred_relic", Material.GOLDEN_APPLE, 2)
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(3000)
                        .addCurrency(CurrencyType.GOLD, 750)
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 2))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(20);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.side.forgotten-temple.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return Arrays.asList(
                Component.translatable("quest.side.forgotten-temple.description.0"),
                Component.translatable("quest.side.forgotten-temple.description.1"),
                Component.translatable("quest.side.forgotten-temple.description.2"),
                Component.translatable("quest.side.forgotten-temple.description.3")
        );
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.side.forgotten-temple.objectives." + objective.getId();
        return Component.translatable(key);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.forgotten-temple.dialogs.0");
            case 1 -> Component.translatable("quest.side.forgotten-temple.dialogs.1");
            case 2 -> Component.translatable("quest.side.forgotten-temple.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.forgotten-temple.npc-name");
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.forgotten-temple.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.forgotten-temple.decline");
    }
}