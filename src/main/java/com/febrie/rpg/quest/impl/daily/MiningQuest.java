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

public class MiningQuest extends Quest {
    public MiningQuest() { super(createBuilder()); }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.DAILY_MINING)
                .objectives(List.of(
                        new InteractNPCObjective("miner", "master_miner"),
                        new CollectItemObjective("coal_collect", Material.COAL, 20),
                        new CollectItemObjective("iron_ore_collect", Material.IRON_ORE, 15),
                        new DeliverItemObjective("coal_deliver", Material.COAL, 10, "master_miner")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 170)
                        .addCurrency(CurrencyType.EMERALD, 4)
                        .addItem(new ItemStack(Material.IRON_PICKAXE))
                        .addExperience(90)
                        .build())
                .sequential(false)
                .category(QuestCategory.DAILY)
                .minLevel(3)
                .repeatable(true)
                .daily(true);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_MINING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_MINING_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "miner" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_MINING_OBJECTIVES_MINER, who);
            case "coal_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_MINING_OBJECTIVES_COAL_COLLECT, who);
            case "iron_ore_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_MINING_OBJECTIVES_IRON_ORE_COLLECT, who);
            case "coal_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_MINING_OBJECTIVES_COAL_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override public int getDialogCount() { return 5; }
    @Override public @NotNull List<Component> getDialogs(@NotNull Player who) { return LangManager.list(QuestCommonLangKey.QUEST_DAILY_MINING_DIALOGS, who); }
    @Override public @NotNull Component getDialog(int index, @NotNull Player who) { return getDialogs(who).get(index); }
    @Override public @NotNull Component getNPCName(@NotNull Player who) { return LangManager.text(QuestCommonLangKey.QUEST_DAILY_MINING_NPC_NAME, who); }
    @Override public @NotNull Component getAcceptDialog(@NotNull Player who) { return LangManager.text(QuestCommonLangKey.QUEST_DAILY_MINING_ACCEPT, who); }
    @Override public @NotNull Component getDeclineDialog(@NotNull Player who) { return LangManager.text(QuestCommonLangKey.QUEST_DAILY_MINING_DECLINE, who); }
}