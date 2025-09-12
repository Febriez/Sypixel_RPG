package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

public class GatheringQuest extends Quest {
    public GatheringQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.DAILY_GATHERING)
                .objectives(List.of(
                        new InteractNPCObjective("herbalist", "master_herbalist"),
                        new CollectItemObjective("poppy_collect", Material.POPPY, 12),
                        new CollectItemObjective("brown_mushroom_collect", Material.BROWN_MUSHROOM, 8),
                        new DeliverItemObjective("wheat_deliver", Material.WHEAT, 10, "master_herbalist")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 110)
                        .addCurrency(CurrencyType.EMERALD, 3)
                        .addItem(new ItemStack(Material.BONE_MEAL, 16))
                        .addExperience(65)
                        .build())
                .sequential(false)
                .category(QuestCategory.DAILY)
                .minLevel(2)
                .repeatable(true)
                .daily(true);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_GATHERING_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "herbalist" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_OBJECTIVES_HERBALIST, who);
            case "poppy_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_OBJECTIVES_POPPY_COLLECT, who);
            case "brown_mushroom_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_OBJECTIVES_BROWN_MUSHROOM_COLLECT, who);
            case "wheat_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_OBJECTIVES_WHEAT_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() { return 6; }

    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_GATHERING_DIALOGS, who);
    }

    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_ACCEPT, who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_DECLINE, who);
    }
}