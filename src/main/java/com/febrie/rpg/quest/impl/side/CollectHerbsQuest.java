package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Side Quest: Collect Herbs
 * Gather medicinal herbs for the village healer
 *
 * @author Febrie
 */
public class CollectHerbsQuest extends Quest {

    /**
     * 기본 생성자
     */
    public CollectHerbsQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_COLLECT_HERBS)
                .objectives(List.of(
                        new InteractNPCObjective("talk_village_healer", "village_healer", 1),
                        new VisitLocationObjective("herb_meadow", "Herb_Meadow"),
                        new CollectItemObjective("healing_herbs", Material.SWEET_BERRIES, 20),
                        new CollectItemObjective("rare_flowers", Material.POPPY, 10),
                        new VisitLocationObjective("mountain_herbs", "Mountain_Herbs"),
                        new CollectItemObjective("mountain_sage", Material.FERN, 8),
                        new InteractNPCObjective("return_village_healer", "village_healer", 1)
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(600)
                        .addCurrency(CurrencyType.GOLD, 120)
                        .addItem(new ItemStack(Material.POTION, 3))
                        .addItem(new ItemStack(Material.HONEY_BOTTLE, 5))
                        .build())
                .sequential(false)
                .category(QuestCategory.SIDE)
                .minLevel(5);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_COLLECT_HERBS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_COLLECT_HERBS_INFO, who);
    }

        @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_village_healer" -> LangManager.list(LangKey.QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_TALK_VILLAGE_HEALER, who);
            case "herb_meadow" -> LangManager.list(LangKey.QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_HERB_MEADOW, who);
            case "healing_herbs" -> LangManager.list(LangKey.QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_HEALING_HERBS, who);
            case "rare_flowers" -> LangManager.list(LangKey.QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_RARE_FLOWERS, who);
            case "mountain_herbs" -> LangManager.list(LangKey.QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_MOUNTAIN_HERBS, who);
            case "mountain_sage" -> LangManager.list(LangKey.QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_MOUNTAIN_SAGE, who);
            case "return_village_healer" -> LangManager.list(LangKey.QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_RETURN_VILLAGE_HEALER, who);
            default -> List.of(Component.text("Unknown objective: " + objective.getId()));
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_COLLECT_HERBS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_COLLECT_HERBS_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_COLLECT_HERBS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_COLLECT_HERBS_DECLINE, who);
    }
}