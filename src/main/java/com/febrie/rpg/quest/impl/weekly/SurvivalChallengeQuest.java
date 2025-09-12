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
 * 주간 생존 도전 - 주간 퀘스트
 * 매주 진행되는 극한의 야생 생존 도전 퀘스트
 *
 * @author Febrie
 */
public class SurvivalChallengeQuest extends Quest {

    /**
     * 기본 생성자
     */
    public SurvivalChallengeQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.WEEKLY_SURVIVAL_CHALLENGE)
                .objectives(List.of(
                    new VisitLocationObjective("enter_wilderness", "survival_arena"),
                    new KillMobObjective("survive_waves", EntityType.ZOMBIE, 100), // 10 waves x 10 enemies
                    new SurviveObjective("maintain_health", 604800), // 7 days in seconds
                    new CollectItemObjective("bread_collect", Material.BREAD, 50),
                    new SurviveObjective("last_seven_days", 604800) // 7 days in seconds
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 12000)
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 3))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 10))
                        .addExperience(4500)
                        .build())
                .sequential(true)
                .weekly(true)
                .category(QuestCategory.WEEKLY)
                .minLevel(45)
                .completionLimit(1);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_SURVIVAL_CHALLENGE_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_SURVIVAL_CHALLENGE_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "enter_wilderness" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_SURVIVAL_CHALLENGE_OBJECTIVES_ENTER_WILDERNESS, who);
            case "survive_waves" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_SURVIVAL_CHALLENGE_OBJECTIVES_SURVIVE_WAVES, who);
            case "maintain_health" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_SURVIVAL_CHALLENGE_OBJECTIVES_MAINTAIN_HEALTH, who);
            case "bread_collect" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_SURVIVAL_CHALLENGE_OBJECTIVES_BREAD_COLLECT, who);
            case "last_seven_days" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_SURVIVAL_CHALLENGE_OBJECTIVES_LAST_SEVEN_DAYS, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }

    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_SURVIVAL_CHALLENGE_DIALOGS, who);
    }

    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_SURVIVAL_CHALLENGE_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_SURVIVAL_CHALLENGE_ACCEPT, who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_SURVIVAL_CHALLENGE_DECLINE, who);
    }
}