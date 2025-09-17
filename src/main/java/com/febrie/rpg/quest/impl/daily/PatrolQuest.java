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

public class PatrolQuest extends Quest {
    public PatrolQuest() { super(createBuilder()); }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.DAILY_PATROL)
                .objectives(List.of(
                        new InteractNPCObjective("guard_captain", "city_guard_captain"),
                        new VisitLocationObjective("patrol_north", "north_gate"),
                        new VisitLocationObjective("patrol_south", "south_gate"),
                        new KillMobObjective("eliminate_threats", EntityType.CREEPER, 5)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 190)
                        .addCurrency(CurrencyType.EMERALD, 5)
                        .addItem(new ItemStack(Material.IRON_BOOTS))
                        .addExperience(95)
                        .build())
                .sequential(true)
                .category(QuestCategory.DAILY)
                .minLevel(6)
                .repeatable(true)
                .daily(true);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_PATROL_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_PATROL_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "guard_captain" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_PATROL_OBJECTIVES_GUARD_CAPTAIN, who);
            case "patrol_north" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_PATROL_OBJECTIVES_PATROL_NORTH, who);
            case "patrol_south" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_PATROL_OBJECTIVES_PATROL_SOUTH, who);
            case "eliminate_threats" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_PATROL_OBJECTIVES_ELIMINATE_THREATS, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override public int getDialogCount() { return 6; }
    @Override public @NotNull List<Component> getDialogs(@NotNull Player who) { return LangManager.list(QuestCommonLangKey.QUEST_DAILY_PATROL_DIALOGS, who); }
    @Override public @NotNull Component getDialog(int index, @NotNull Player who) { return getDialogs(who).get(index); }
    @Override public @NotNull Component getNPCName(@NotNull Player who) { return LangManager.text(QuestCommonLangKey.QUEST_DAILY_PATROL_NPC_NAME, who); }
    @Override public @NotNull Component getAcceptDialog(@NotNull Player who) { return LangManager.text(QuestCommonLangKey.QUEST_DAILY_PATROL_ACCEPT, who); }
    @Override public @NotNull Component getDeclineDialog(@NotNull Player who) { return LangManager.text(QuestCommonLangKey.QUEST_DAILY_PATROL_DECLINE, who); }
}