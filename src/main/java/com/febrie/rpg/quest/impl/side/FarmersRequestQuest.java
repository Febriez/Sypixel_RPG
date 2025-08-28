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
 * Side Quest: Farmer's Request
 * Help a local farmer with crop problems and pest control
 *
 * @author Febrie
 */
public class FarmersRequestQuest extends Quest {

    /**
     * Default constructor
     */
    public FarmersRequestQuest() {
        super(createBuilder());
    }

    /**
     * Quest setup
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_FARMERS_REQUEST)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_worried_farmer", "worried_farmer"),
                        new VisitLocationObjective("visit_damaged_farmland", "damaged_farmland"),
                        new KillMobObjective("kill_rabbits", EntityType.RABBIT, 15),
                        new CollectItemObjective("collect_fresh_seeds", Material.WHEAT_SEEDS, 32),
                        new CollectItemObjective("collect_bone_meal", Material.BONE_MEAL, 16),
                        new VisitLocationObjective("visit_irrigation_canal", "irrigation_canal"),
                        new InteractNPCObjective("return_worried_farmer", "worried_farmer")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(800)
                        .addCurrency(CurrencyType.GOLD, 150)
                        .addItem(new ItemStack(Material.BREAD, 16))
                        .addItem(new ItemStack(Material.CARROT, 8))
                        .build())
                .sequential(false)
                .category(QuestCategory.SIDE)
                .minLevel(8);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.side.farmers-request.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return Arrays.asList(
                Component.translatable("quest.side.farmers-request.description.0"),
                Component.translatable("quest.side.farmers-request.description.1"),
                Component.translatable("quest.side.farmers-request.description.2"),
                Component.translatable("quest.side.farmers-request.description.3")
        );
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.side.farmers-request.objectives." + objective.getId();
        return Component.translatable(key);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.farmers-request.dialogs.0");
            case 1 -> Component.translatable("quest.side.farmers-request.dialogs.1");
            case 2 -> Component.translatable("quest.side.farmers-request.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.farmers-request.npc-name");
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.farmers-request.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.farmers-request.decline");
    }
}