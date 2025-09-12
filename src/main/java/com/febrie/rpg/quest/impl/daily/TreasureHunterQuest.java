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

public class TreasureHunterQuest extends Quest {
    public TreasureHunterQuest() { super(createBuilder()); }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.DAILY_TREASURE_HUNTER)
                .objectives(List.of(
                        new InteractNPCObjective("treasure_seeker", "master_treasure_hunter"),
                        new VisitLocationObjective("search_ruins", "ancient_ruins"),
                        new CollectItemObjective("diamond_collect", Material.DIAMOND, 3),
                        new CollectItemObjective("emerald_collect", Material.EMERALD, 5)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 240)
                        .addCurrency(CurrencyType.EMERALD, 6)
                        .addItem(new ItemStack(Material.DIAMOND_PICKAXE))
                        .addExperience(120)
                        .build())
                .sequential(false)
                .category(QuestCategory.DAILY)
                .minLevel(10)
                .repeatable(true)
                .daily(true);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_TREASURE_HUNTER_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_TREASURE_HUNTER_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "treasure_seeker" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_TREASURE_HUNTER_OBJECTIVES_TREASURE_SEEKER, who);
            case "search_ruins" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_TREASURE_HUNTER_OBJECTIVES_SEARCH_RUINS, who);
            case "diamond_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_TREASURE_HUNTER_OBJECTIVES_DIAMOND_COLLECT, who);
            case "emerald_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_TREASURE_HUNTER_OBJECTIVES_EMERALD_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override public int getDialogCount() { return 6; }
    @Override public @NotNull List<Component> getDialogs(@NotNull Player who) { return LangManager.list(QuestCommonLangKey.QUEST_DAILY_TREASURE_HUNTER_DIALOGS, who); }
    @Override public @NotNull Component getDialog(int index, @NotNull Player who) { return getDialogs(who).get(index); }
    @Override public @NotNull Component getNPCName(@NotNull Player who) { return LangManager.text(QuestCommonLangKey.QUEST_DAILY_TREASURE_HUNTER_NPC_NAME, who); }
    @Override public @NotNull Component getAcceptDialog(@NotNull Player who) { return LangManager.text(QuestCommonLangKey.QUEST_DAILY_TREASURE_HUNTER_ACCEPT, who); }
    @Override public @NotNull Component getDeclineDialog(@NotNull Player who) { return LangManager.text(QuestCommonLangKey.QUEST_DAILY_TREASURE_HUNTER_DECLINE, who); }
}