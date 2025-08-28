package com.febrie.rpg.quest.impl.side;

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
 * Side Quest: Ancient Ruins
 * Explore mysterious ancient ruins and uncover their secrets
 *
 * @author Febrie
 */
public class AncientRuinsQuest extends Quest {
    
    /**
     * Quest builder
     */
    private static class AncientRuinsBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new AncientRuinsQuest(this);
        }
    }
    
    /**
     * Default constructor
     */
    public AncientRuinsQuest() {
        this(createBuilder());
    }
    
    /**
     * Builder constructor
     */
    private AncientRuinsQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new AncientRuinsBuilder()
                .id(QuestID.SIDE_ANCIENT_RUINS)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("archaeologist", "archaeologist"),
                        new VisitLocationObjective("ruined_entrance", "ruined_entrance_area"),
                        new KillMobObjective("kill_spiders", EntityType.SPIDER, 20),
                        new CollectItemObjective("ancient_stone", Material.STONE_BRICKS, 15),
                        new VisitLocationObjective("inner_chamber", "inner_chamber_area"),
                        new CollectItemObjective("runic_tablet", Material.STONE, 3),
                        new KillMobObjective("kill_silverfish", EntityType.SILVERFISH, 25),
                        new InteractNPCObjective("archaeologist_complete", "archaeologist")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 500)
                        .addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 10))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK))
                        .addExperience(2000)
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(15);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.side.ancient-ruins.name");
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            description.add(Component.translatable("quest.side.ancient-ruins.description." + i));
        }
        return description;
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return Component.translatable("quest.side.ancient-ruins.objectives." + objective.getId());
    }
    
    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.ancient-ruins.dialogs.0");
            case 1 -> Component.translatable("quest.side.ancient-ruins.dialogs.1");
            case 2 -> Component.translatable("quest.side.ancient-ruins.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.ancient-ruins.npc-name");
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.ancient-ruins.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.ancient-ruins.decline");
    }
}