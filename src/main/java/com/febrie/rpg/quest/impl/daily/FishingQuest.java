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

public class FishingQuest extends Quest {
    public FishingQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.DAILY_FISHING)
                .objectives(List.of(
                        new InteractNPCObjective("fisherman", "master_fisherman"),
                        new FishingObjective("catch_fish", FishingObjective.FishType.ANY, 8),
                        new FishingObjective("catch_salmon", FishingObjective.FishType.SPECIFIC, 3, Material.SALMON),
                        new DeliverItemObjective("cod_deliver", Material.COD, 5, "master_fisherman")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 130)
                        .addCurrency(CurrencyType.EMERALD, 3)
                        .addItem(new ItemStack(Material.FISHING_ROD))
                        .addExperience(75)
                        .build())
                .sequential(false)
                .category(QuestCategory.DAILY)
                .minLevel(3)
                .repeatable(true)
                .daily(true);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_FISHING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_FISHING_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "fisherman" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_FISHING_OBJECTIVES_FISHERMAN, who);
            case "catch_fish" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_FISHING_OBJECTIVES_CATCH_FISH, who);
            case "catch_salmon" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_FISHING_OBJECTIVES_CATCH_SALMON, who);
            case "cod_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_FISHING_OBJECTIVES_COD_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }

    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_FISHING_DIALOGS, who);
    }

    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_FISHING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_FISHING_ACCEPT, who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_FISHING_DECLINE, who);
    }
}