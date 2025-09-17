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

public class TrainingQuest extends Quest {
    public TrainingQuest() { super(createBuilder()); }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.DAILY_TRAINING)
                .objectives(List.of(
                        new InteractNPCObjective("trainer", "combat_trainer"),
                        new KillMobObjective("practice_combat", EntityType.ZOMBIE, 10),
                        new SurviveObjective("endurance_test", 240),
                        new CollectItemObjective("iron_ingot_collect", Material.IRON_INGOT, 5)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 160)
                        .addCurrency(CurrencyType.EMERALD, 4)
                        .addItem(new ItemStack(Material.IRON_SWORD))
                        .addExperience(100)
                        .build())
                .sequential(false)
                .category(QuestCategory.DAILY)
                .minLevel(5)
                .repeatable(true)
                .daily(true);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_TRAINING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_TRAINING_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "trainer" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_TRAINING_OBJECTIVES_TRAINER, who);
            case "practice_combat" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_TRAINING_OBJECTIVES_PRACTICE_COMBAT, who);
            case "endurance_test" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_TRAINING_OBJECTIVES_ENDURANCE_TEST, who);
            case "iron_ingot_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_TRAINING_OBJECTIVES_IRON_INGOT_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override public int getDialogCount() { return 5; }
    @Override public @NotNull List<Component> getDialogs(@NotNull Player who) { return LangManager.list(QuestCommonLangKey.QUEST_DAILY_TRAINING_DIALOGS, who); }
    @Override public @NotNull Component getDialog(int index, @NotNull Player who) { return getDialogs(who).get(index); }
    @Override public @NotNull Component getNPCName(@NotNull Player who) { return LangManager.text(QuestCommonLangKey.QUEST_DAILY_TRAINING_NPC_NAME, who); }
    @Override public @NotNull Component getAcceptDialog(@NotNull Player who) { return LangManager.text(QuestCommonLangKey.QUEST_DAILY_TRAINING_ACCEPT, who); }
    @Override public @NotNull Component getDeclineDialog(@NotNull Player who) { return LangManager.text(QuestCommonLangKey.QUEST_DAILY_TRAINING_DECLINE, who); }
}