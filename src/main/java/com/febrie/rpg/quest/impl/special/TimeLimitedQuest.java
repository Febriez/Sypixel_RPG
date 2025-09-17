package com.febrie.rpg.quest.impl.special;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
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
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import java.util.ArrayList;
import java.util.List;

public class TimeLimitedQuest extends Quest {

    public TimeLimitedQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SPECIAL_TIME_LIMITED)
                .objectives(List.of(
                        new InteractNPCObjective("urgent_messenger", "time_keeper"),
                        new SurviveObjective("speed_challenge", 3600), // 1시간 제한 - survive for the duration
                        new CollectItemObjective("amethyst_shard_collect", Material.AMETHYST_SHARD, 30),
                        new KillMobObjective("temporal_enemies", EntityType.ENDERMAN, 20),
                        new DeliverItemObjective("diamond_deliver", Material.DIAMOND, 10, "villager")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 3000)
                        .addItem(new ItemStack(Material.CLOCK, 5)) // 시간의 시계
                        .addExperience(5000)
                        .build())
                .sequential(true)
                .repeatable(true)
                .category(QuestCategory.SPECIAL)
                .minLevel(25);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_TIME_LIMITED_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SPECIAL_TIME_LIMITED_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "urgent_messenger" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_TIME_LIMITED_OBJECTIVES_URGENT_MESSENGER, who);
            case "speed_challenge" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_TIME_LIMITED_OBJECTIVES_SPEED_CHALLENGE, who);
            case "amethyst_shard_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_TIME_LIMITED_OBJECTIVES_AMETHYST_SHARD_COLLECT, who);
            case "temporal_enemies" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_TIME_LIMITED_OBJECTIVES_TEMPORAL_ENEMIES, who);
            case "diamond_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_TIME_LIMITED_OBJECTIVES_DIAMOND_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() { return 5; }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SPECIAL_TIME_LIMITED_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_TIME_LIMITED_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_TIME_LIMITED_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_TIME_LIMITED_DECLINE, who);
    }
}