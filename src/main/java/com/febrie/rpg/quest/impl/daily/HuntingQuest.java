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
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

public class HuntingQuest extends Quest {
    public HuntingQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.DAILY_HUNTING)
                .objectives(List.of(
                        new InteractNPCObjective("hunter", "master_hunter"),
                        new KillMobObjective("hunt_animals", EntityType.COW, 6),
                        new KillMobObjective("hunt_hostile", EntityType.SPIDER, 8),
                        new CollectItemObjective("beef_collect", Material.BEEF, 10)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 150)
                        .addCurrency(CurrencyType.EMERALD, 4)
                        .addItem(new ItemStack(Material.BOW))
                        .addExperience(80)
                        .build())
                .sequential(false)
                .category(QuestCategory.DAILY)
                .minLevel(4)
                .repeatable(true)
                .daily(true);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_HUNTING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_HUNTING_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "hunter" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_HUNTING_OBJECTIVES_HUNTER, who);
            case "hunt_animals" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_HUNTING_OBJECTIVES_HUNT_ANIMALS, who);
            case "hunt_hostile" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_HUNTING_OBJECTIVES_HUNT_HOSTILE, who);
            case "beef_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_HUNTING_OBJECTIVES_BEEF_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() { return 5; }

    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_HUNTING_DIALOGS, who);
    }

    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_HUNTING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_HUNTING_ACCEPT, who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_HUNTING_DECLINE, who);
    }
}