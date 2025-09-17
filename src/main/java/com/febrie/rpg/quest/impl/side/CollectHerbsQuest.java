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
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.collect.herbs.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.collect.herbs.info"), who);
    }

        @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_village_healer" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.collect.herbs.objectives.talk.village.healer"), who);
            case "herb_meadow" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.collect.herbs.objectives.herb.meadow"), who);
            case "sweet_berries_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.collect.herbs.objectives.sweet.berries.collect"), who);
            case "poppy_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.collect.herbs.objectives.poppy.collect"), who);
            case "mountain_herbs" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.collect.herbs.objectives.mountain.herbs"), who);
            case "fern_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.collect.herbs.objectives.fern.collect"), who);
            case "return_village_healer" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.collect.herbs.objectives.return.village.healer"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.collect.herbs.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.collect.herbs.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.collect.herbs.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.collect.herbs.decline"), who);
    }
}