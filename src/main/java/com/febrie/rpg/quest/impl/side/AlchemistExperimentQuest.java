package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangHelper;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
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
 * Alchemist Experiment Side Quest
 * Assist a mad alchemist in conducting dangerous experiments
 *
 * @author Febrie
 */
public class AlchemistExperimentQuest extends Quest {
    
    /**
     * Default constructor
     */
    public AlchemistExperimentQuest() {
        super(createBuilder());
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_ALCHEMIST_EXPERIMENT)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("mad_alchemist", "mad_alchemist"),
                        new VisitLocationObjective("alchemy_lab", "alchemy_lab_area"),
                        new CollectItemObjective("rare_reagents", Material.BLAZE_POWDER, 10),
                        new CollectItemObjective("dragon_scales", Material.SHULKER_SHELL, 5),
                        new KillMobObjective("kill_witches", EntityType.WITCH, 8),
                        new CollectItemObjective("philosopher_stone", Material.NETHER_STAR, 1),
                        new InteractNPCObjective("mad_alchemist_complete", "mad_alchemist")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1100)
                        .addItem(new ItemStack(Material.BREWING_STAND, 2))
                        .addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 25))
                        .addExperience(4200)
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(26);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_ALCHEMIST_EXPERIMENT_NAME, who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_ALCHEMIST_EXPERIMENT_INFO, who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.side.alchemist_experiment.objectives." + objective.getId(), who);
    }
    
    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangHelper.text(LangKey.QUEST_SIDE_ALCHEMIST_EXPERIMENT_DIALOGS_0, who);
            case 1 -> LangHelper.text(LangKey.QUEST_SIDE_ALCHEMIST_EXPERIMENT_DIALOGS_1, who);
            case 2 -> LangHelper.text(LangKey.QUEST_SIDE_ALCHEMIST_EXPERIMENT_DIALOGS_2, who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_ALCHEMIST_EXPERIMENT_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_ALCHEMIST_EXPERIMENT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_ALCHEMIST_EXPERIMENT_DECLINE, who);
    }
}