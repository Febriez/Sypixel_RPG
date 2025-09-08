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

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

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
                .objectives(List.of(
                        new InteractNPCObjective("talk_worried_farmer", "worried_farmer", 1),
                        new VisitLocationObjective("visit_damaged_farmland", "damaged_farmland"),
                        new KillMobObjective("kill_rabbits", EntityType.RABBIT, 15),
                        new CollectItemObjective("collect_fresh_seeds", Material.WHEAT_SEEDS, 32),
                        new CollectItemObjective("collect_bone_meal", Material.BONE_MEAL, 16),
                        new VisitLocationObjective("visit_irrigation_canal", "irrigation_canal"),
                        new InteractNPCObjective("return_worried_farmer", "worried_farmer", 1)
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
        return LangManager.text(LangKey.QUEST_SIDE_FARMERS_REQUEST_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_FARMERS_REQUEST_INFO, who);
    }

        @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_worried_farmer" -> LangManager.list(LangKey.QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_TALK_WORRIED_FARMER, who);
            case "visit_damaged_farmland" -> LangManager.list(LangKey.QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_VISIT_DAMAGED_FARMLAND, who);
            case "kill_rabbits" -> LangManager.list(LangKey.QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_KILL_RABBITS, who);
            case "collect_fresh_seeds" -> LangManager.list(LangKey.QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_COLLECT_FRESH_SEEDS, who);
            case "collect_bone_meal" -> LangManager.list(LangKey.QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_COLLECT_BONE_MEAL, who);
            case "visit_irrigation_canal" -> LangManager.list(LangKey.QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_VISIT_IRRIGATION_CANAL, who);
            case "return_worried_farmer" -> LangManager.list(LangKey.QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_RETURN_WORRIED_FARMER, who);
            default -> List.of(Component.text("Unknown objective: " + objective.getId()));
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_FARMERS_REQUEST_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_FARMERS_REQUEST_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_FARMERS_REQUEST_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_FARMERS_REQUEST_DECLINE, who);
    }
}