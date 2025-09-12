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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import java.util.ArrayList;
import java.util.List;

/**
 * 주간 고대 유적 탐험 - 주간 퀘스트
 * 매주 새롭게 등장하는 고대 유적을 탐험하는 퀘스트
 *
 * @author Febrie
 */
public class AncientRuinsQuest extends Quest {

    /**
     * 기본 생성자
     */
    public AncientRuinsQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.WEEKLY_ANCIENT_RUINS)
                .objectives(List.of(
                    new VisitLocationObjective("enter_ruins", "ancient_ruins"),
                    new InteractNPCObjective("solve_puzzles", "ancient_puzzle"),
                    new KillMobObjective("defeat_guardian", EntityType.WARDEN, 1),
                    new CollectItemObjective("nether_star_collect", Material.NETHER_STAR, 1),
                    new VisitLocationObjective("escape_ruins", "ruins_exit")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 5000)
                        .addItem(new ItemStack(Material.DIAMOND_BLOCK, 3))
                        .addItem(new ItemStack(Material.ANCIENT_DEBRIS, 2))
                        .addExperience(2000)
                        .build())
                .sequential(true)
                .weekly(true)
                .category(QuestCategory.WEEKLY)
                .minLevel(40)
                .completionLimit(1);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_ANCIENT_RUINS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_ANCIENT_RUINS_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "enter_ruins" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_ANCIENT_RUINS_OBJECTIVES_ENTER_RUINS, who);
            case "solve_puzzles" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_ANCIENT_RUINS_OBJECTIVES_SOLVE_PUZZLES, who);
            case "defeat_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_ANCIENT_RUINS_OBJECTIVES_DEFEAT_GUARDIAN, who);
            case "nether_star_collect" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_ANCIENT_RUINS_OBJECTIVES_NETHER_STAR_COLLECT, who);
            case "escape_ruins" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_ANCIENT_RUINS_OBJECTIVES_ESCAPE_RUINS, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }

    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_ANCIENT_RUINS_DIALOGS, who);
    }

    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_ANCIENT_RUINS_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_ANCIENT_RUINS_ACCEPT, who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_ANCIENT_RUINS_DECLINE, who);
    }
}