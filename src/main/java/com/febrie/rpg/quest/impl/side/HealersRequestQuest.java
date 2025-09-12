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
import com.febrie.rpg.util.lang.quest.side.HealersRequestLangKey;

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
 * Side Quest: Healer's Request
 * Assist the village healer in gathering rare ingredients for life-saving potions
 *
 * @author Febrie
 */
public class HealersRequestQuest extends Quest {

    /**
     * Default constructor
     */
    public HealersRequestQuest() {
        super(createBuilder());
    }

    /**
     * Quest setup
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_HEALERS_REQUEST)
                .objectives(List.of(
                        new InteractNPCObjective("talk_village_healer", "village_healer"),
                        new VisitLocationObjective("visit_herb_garden", "herb_garden"),
                        new CollectItemObjective("sweet_berries_collect", Material.SWEET_BERRIES, 25),
                        new CollectItemObjective("spider_eye_collect", Material.SPIDER_EYE, 8),
                        new CollectItemObjective("ghast_tear_collect", Material.GHAST_TEAR, 3),
                        new VisitLocationObjective("visit_sacred_spring", "sacred_spring"),
                        new CollectItemObjective("potion_collect", Material.POTION, 5)
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1200)
                        .addCurrency(CurrencyType.GOLD, 300)
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 3))
                        .addItem(new ItemStack(Material.POTION, 5))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(10);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(HealersRequestLangKey.QUEST_SIDE_HEALERS_REQUEST_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(HealersRequestLangKey.QUEST_SIDE_HEALERS_REQUEST_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_village_healer" -> LangManager.text(HealersRequestLangKey.QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_TALK_VILLAGE_HEALER, who);
            case "visit_herb_garden" -> LangManager.text(HealersRequestLangKey.QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_VISIT_HERB_GARDEN, who);
            case "sweet_berries_collect" -> LangManager.text(HealersRequestLangKey.QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_SWEET_BERRIES_COLLECT, who);
            case "spider_eye_collect" -> LangManager.text(HealersRequestLangKey.QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_SPIDER_EYE_COLLECT, who);
            case "ghast_tear_collect" -> LangManager.text(HealersRequestLangKey.QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_GHAST_TEAR_COLLECT, who);
            case "visit_sacred_spring" -> LangManager.text(HealersRequestLangKey.QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_VISIT_SACRED_SPRING, who);
            case "potion_collect" -> LangManager.text(HealersRequestLangKey.QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_POTION_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(HealersRequestLangKey.QUEST_SIDE_HEALERS_REQUEST_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(HealersRequestLangKey.QUEST_SIDE_HEALERS_REQUEST_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(HealersRequestLangKey.QUEST_SIDE_HEALERS_REQUEST_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(HealersRequestLangKey.QUEST_SIDE_HEALERS_REQUEST_DECLINE, who);
    }
}