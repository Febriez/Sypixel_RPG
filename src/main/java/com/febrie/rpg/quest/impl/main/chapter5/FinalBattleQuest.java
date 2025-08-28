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
 * Chapter 5: Final Battle Quest
 * The ultimate confrontation with the forces of darkness
 *
 * @author Febrie
 */
public class FinalBattleQuest extends Quest {
    
    /**
     * Quest builder
     */
    private static class FinalBattleBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new FinalBattleQuest(this);
        }
    }
    
    /**
     * Default constructor
     */
    public FinalBattleQuest() {
        this(createBuilder());
    }
    
    /**
     * Builder constructor
     */
    private FinalBattleQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new FinalBattleBuilder()
                .id(QuestID.MAIN_FINAL_BATTLE)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("chosen_champion", "chosen_champion"),
                        new VisitLocationObjective("battlefield_gates", "battlefield_gates_area"),
                        new KillMobObjective("kill_withers", EntityType.WITHER, 3),
                        new CollectItemObjective("heart_of_darkness", Material.WITHER_SKELETON_SKULL, 5),
                        new KillMobObjective("kill_ender_dragon", EntityType.ENDER_DRAGON, 1),
                        new CollectItemObjective("dragon_essence", Material.DRAGON_EGG, 1),
                        new VisitLocationObjective("void_nexus", "void_nexus_area"),
                        new KillMobObjective("kill_endermen", EntityType.ENDERMAN, 50),
                        new InteractNPCObjective("ancient_oracle", "ancient_oracle")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 10000)
                        .addItem(new ItemStack(Material.NETHERITE_BOOTS))
                        .addItem(new ItemStack(Material.ELYTRA))
                        .addItem(new ItemStack(Material.DRAGON_HEAD))
                        .addExperience(20000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(44)
                .addPrerequisite(QuestID.MAIN_LAST_STAND);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.main.final-battle.name");
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            description.add(Component.translatable("quest.main.final-battle.description." + i));
        }
        return description;
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return Component.translatable("quest.main.final-battle.objectives." + objective.getId());
    }
    
    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.main.final-battle.dialogs.0");
            case 1 -> Component.translatable("quest.main.final-battle.dialogs.1");
            case 2 -> Component.translatable("quest.main.final-battle.dialogs.2");
            case 3 -> Component.translatable("quest.main.final-battle.dialogs.3");
            case 4 -> Component.translatable("quest.main.final-battle.dialogs.4");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.main.final-battle.npc-name");
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.main.final-battle.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.main.final-battle.decline");
    }
}