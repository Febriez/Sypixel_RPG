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
import com.febrie.rpg.util.lang.quest.side.FarmersRequestLangKey;

import com.febrie.rpg.util.LangKey;
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
                        new InteractNPCObjective("talk_worried_farmer", "worried_farmer"),
                        new VisitLocationObjective("visit_damaged_farmland", "damaged_farmland"),
                        new KillMobObjective("kill_rabbits", EntityType.RABBIT, 15),
                        new CollectItemObjective("wheat_seeds_collect", Material.WHEAT_SEEDS, 32),
                        new CollectItemObjective("bone_meal_collect", Material.BONE_MEAL, 16),
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
        return LangManager.text(FarmersRequestLangKey.QUEST_SIDE_FARMERS_REQUEST_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(FarmersRequestLangKey.QUEST_SIDE_FARMERS_REQUEST_INFO, who);
    }

        @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_worried_farmer" -> LangManager.text(FarmersRequestLangKey.QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_TALK_WORRIED_FARMER, who);
            case "visit_damaged_farmland" -> LangManager.text(FarmersRequestLangKey.QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_VISIT_DAMAGED_FARMLAND, who);
            case "kill_rabbits" -> LangManager.text(FarmersRequestLangKey.QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_KILL_RABBITS, who);
            case "wheat_seeds_collect" -> LangManager.text(FarmersRequestLangKey.QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_WHEAT_SEEDS_COLLECT, who);
            case "bone_meal_collect" -> LangManager.text(FarmersRequestLangKey.QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_BONE_MEAL_COLLECT, who);
            case "visit_irrigation_canal" -> LangManager.text(FarmersRequestLangKey.QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_VISIT_IRRIGATION_CANAL, who);
            case "return_worried_farmer" -> LangManager.text(FarmersRequestLangKey.QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_RETURN_WORRIED_FARMER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(FarmersRequestLangKey.QUEST_SIDE_FARMERS_REQUEST_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(FarmersRequestLangKey.QUEST_SIDE_FARMERS_REQUEST_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(FarmersRequestLangKey.QUEST_SIDE_FARMERS_REQUEST_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(FarmersRequestLangKey.QUEST_SIDE_FARMERS_REQUEST_DECLINE, who);
    }
}