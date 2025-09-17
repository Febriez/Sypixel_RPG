package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;

import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

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
                .objectives(List.of(
                        new InteractNPCObjective("mad_alchemist", "mad_alchemist"),
                        new VisitLocationObjective("alchemy_lab", "alchemy_lab_area"),
                        new CollectItemObjective("collect_blaze_powder", Material.BLAZE_POWDER, 10),
                        new CollectItemObjective("collect_shulker_shell", Material.SHULKER_SHELL, 5),
                        new KillMobObjective("kill_witches", EntityType.WITCH, 8),
                        new CollectItemObjective("collect_nether_star", Material.NETHER_STAR, 1),
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
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.alchemist_experiment.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.alchemist_experiment.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "mad_alchemist" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.alchemist_experiment.objectives.mad_alchemist"), who);
            case "alchemy_lab" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.alchemist_experiment.objectives.alchemy_lab"), who);
            case "collect_blaze_powder" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.alchemist_experiment.objectives.collect_blaze_powder"), who);
            case "collect_shulker_shell" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.alchemist_experiment.objectives.collect_shulker_shell"), who);
            case "kill_witches" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.alchemist_experiment.objectives.kill_witches"), who);
            case "collect_nether_star" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.alchemist_experiment.objectives.collect_nether_star"), who);
            case "mad_alchemist_complete" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.alchemist_experiment.objectives.mad_alchemist_complete"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.alchemist_experiment.dialogs"), who);
    }

    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.alchemist_experiment.npc_name"), who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.alchemist_experiment.accept"), who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.alchemist_experiment.decline"), who);
    }
}