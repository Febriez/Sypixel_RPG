package com.febrie.rpg.quest.impl.weekly;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
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
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import java.util.ArrayList;
import java.util.List;

/**
 * 주간 PvP 토너먼트 - 주간 퀘스트
 * 매주 개최되는 PvP 토너먼트에 참가하여 우승을 노리는 퀘스트
 *
 * @author Febrie
 */
public class PvpTournamentQuest extends Quest {

    /**
     * 기본 생성자
     */
    public PvpTournamentQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.WEEKLY_PVP_TOURNAMENT)
                .objectives(List.of(
                    new InteractNPCObjective("register_tournament", "tournament_registrar"),
                    new KillPlayerObjective("win_preliminaries", 5),
                    new InteractNPCObjective("reach_semifinals", "tournament_official"),
                    new KillPlayerObjective("defeat_champion", 1),
                    new CollectItemObjective("golden_apple_collect", Material.GOLDEN_APPLE, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 8000)
                        .addItem(new ItemStack(Material.NETHERITE_HELMET))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 5))
                        .addExperience(3500)
                        .build())
                .sequential(true)
                .weekly(true)
                .category(QuestCategory.WEEKLY)
                .minLevel(35)
                .completionLimit(1);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_PVP_TOURNAMENT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_PVP_TOURNAMENT_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "register_tournament" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_PVP_TOURNAMENT_OBJECTIVES_REGISTER_TOURNAMENT, who);
            case "win_preliminaries" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_PVP_TOURNAMENT_OBJECTIVES_WIN_PRELIMINARIES, who);
            case "reach_semifinals" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_PVP_TOURNAMENT_OBJECTIVES_REACH_SEMIFINALS, who);
            case "defeat_champion" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_PVP_TOURNAMENT_OBJECTIVES_DEFEAT_CHAMPION, who);
            case "golden_apple_collect" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_PVP_TOURNAMENT_OBJECTIVES_GOLDEN_APPLE_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }

    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_PVP_TOURNAMENT_DIALOGS, who);
    }

    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_PVP_TOURNAMENT_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_PVP_TOURNAMENT_ACCEPT, who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_PVP_TOURNAMENT_DECLINE, who);
    }
}