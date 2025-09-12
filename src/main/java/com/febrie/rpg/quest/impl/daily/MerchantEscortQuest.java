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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

public class MerchantEscortQuest extends Quest {
    public MerchantEscortQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.DAILY_MERCHANT_ESCORT)
                .objectives(List.of(
                        new InteractNPCObjective("merchant", "traveling_merchant"),
                        new VisitLocationObjective("escort_route", "trade_route_checkpoint"),
                        new KillMobObjective("protect_caravan", EntityType.PILLAGER, 4),
                        new DeliverItemObjective("chest_deliver", Material.CHEST, 1, "destination_merchant")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 220)
                        .addCurrency(CurrencyType.EMERALD, 5)
                        .addItem(new ItemStack(Material.SHIELD))
                        .addExperience(110)
                        .build())
                .sequential(true)
                .category(QuestCategory.DAILY)
                .minLevel(8)
                .repeatable(true)
                .daily(true);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_MERCHANT_ESCORT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_MERCHANT_ESCORT_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "merchant" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_MERCHANT_ESCORT_OBJECTIVES_MERCHANT, who);
            case "escort_route" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_MERCHANT_ESCORT_OBJECTIVES_ESCORT_ROUTE, who);
            case "protect_caravan" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_MERCHANT_ESCORT_OBJECTIVES_PROTECT_CARAVAN, who);
            case "chest_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_MERCHANT_ESCORT_OBJECTIVES_CHEST_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() { return 6; }

    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_MERCHANT_ESCORT_DIALOGS, who);
    }

    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_MERCHANT_ESCORT_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_MERCHANT_ESCORT_ACCEPT, who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_MERCHANT_ESCORT_DECLINE, who);
    }
}