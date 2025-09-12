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
import com.febrie.rpg.util.lang.quest.side.CollectHerbsLangKey;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

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
                        new InteractNPCObjective("talk_village_healer", "village_healer"),
                        new VisitLocationObjective("herb_meadow", "Herb_Meadow"),
                        new CollectItemObjective("sweet_berries_collect", Material.SWEET_BERRIES, 20),
                        new CollectItemObjective("poppy_collect", Material.POPPY, 10),
                        new VisitLocationObjective("mountain_herbs", "Mountain_Herbs"),
                        new CollectItemObjective("fern_collect", Material.FERN, 8),
                        new InteractNPCObjective("return_village_healer", "village_healer")
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
        return LangManager.text(CollectHerbsLangKey.QUEST_SIDE_COLLECT_HERBS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(CollectHerbsLangKey.QUEST_SIDE_COLLECT_HERBS_INFO, who);
    }

        @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_village_healer" -> LangManager.text(CollectHerbsLangKey.QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_TALK_VILLAGE_HEALER, who);
            case "herb_meadow" -> LangManager.text(CollectHerbsLangKey.QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_HERB_MEADOW, who);
            case "sweet_berries_collect" -> LangManager.text(CollectHerbsLangKey.QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_SWEET_BERRIES_COLLECT, who);
            case "poppy_collect" -> LangManager.text(CollectHerbsLangKey.QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_POPPY_COLLECT, who);
            case "mountain_herbs" -> LangManager.text(CollectHerbsLangKey.QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_MOUNTAIN_HERBS, who);
            case "fern_collect" -> LangManager.text(CollectHerbsLangKey.QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_FERN_COLLECT, who);
            case "return_village_healer" -> LangManager.text(CollectHerbsLangKey.QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_RETURN_VILLAGE_HEALER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(CollectHerbsLangKey.QUEST_SIDE_COLLECT_HERBS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(CollectHerbsLangKey.QUEST_SIDE_COLLECT_HERBS_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(CollectHerbsLangKey.QUEST_SIDE_COLLECT_HERBS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(CollectHerbsLangKey.QUEST_SIDE_COLLECT_HERBS_DECLINE, who);
    }
}