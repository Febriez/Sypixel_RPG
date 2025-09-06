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
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_village_healer", "village_healer"),
                        new VisitLocationObjective("visit_herb_garden", "herb_garden"),
                        new CollectItemObjective("collect_medicinal_herbs", Material.SWEET_BERRIES, 25),
                        new CollectItemObjective("collect_spider_eyes", Material.SPIDER_EYE, 8),
                        new CollectItemObjective("collect_ghast_tears", Material.GHAST_TEAR, 3),
                        new VisitLocationObjective("visit_sacred_spring", "sacred_spring"),
                        new CollectItemObjective("collect_holy_water", Material.POTION, 5)
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
        return LangManager.text(LangKey.QUEST_SIDE_HEALERS_REQUEST_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_HEALERS_REQUEST_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.side.healers_request.objectives." + objective.getId(), who);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(LangKey.QUEST_SIDE_HEALERS_REQUEST_DIALOGS, who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_HEALERS_REQUEST_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_HEALERS_REQUEST_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_HEALERS_REQUEST_DECLINE, who);
    }
}