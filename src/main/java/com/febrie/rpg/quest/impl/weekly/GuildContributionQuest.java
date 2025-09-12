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
 * 주간 길드 기여 - 주간 퀘스트
 * 매주 길드 발전을 위한 다양한 기여 활동을 수행하는 퀘스트
 *
 * @author Febrie
 */
public class GuildContributionQuest extends Quest {

    /**
     * 기본 생성자
     */
    public GuildContributionQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.WEEKLY_GUILD_CONTRIBUTION)
                .objectives(List.of(
                    new PayCurrencyObjective("donate_gold", CurrencyType.GOLD, 10000),
                    new InteractNPCObjective("complete_missions", "guild_mission_board"),
                    new InteractNPCObjective("recruit_members", "guild_recruiter"),
                    new SurviveObjective("defend_territory", 3600), // 1 hour in seconds
                    new CollectItemObjective("experience_bottle_collect", Material.EXPERIENCE_BOTTLE, 1000)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 3500)
                        .addItem(new ItemStack(Material.DIAMOND_BLOCK, 5))
                        .addItem(new ItemStack(Material.EMERALD_BLOCK, 3))
                        .addExperience(2200)
                        .build())
                .sequential(false)
                .weekly(true)
                .category(QuestCategory.WEEKLY)
                .minLevel(30)
                .completionLimit(1);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_GUILD_CONTRIBUTION_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_GUILD_CONTRIBUTION_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "donate_gold" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_GUILD_CONTRIBUTION_OBJECTIVES_DONATE_GOLD, who);
            case "complete_missions" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_GUILD_CONTRIBUTION_OBJECTIVES_COMPLETE_MISSIONS, who);
            case "recruit_members" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_GUILD_CONTRIBUTION_OBJECTIVES_RECRUIT_MEMBERS, who);
            case "defend_territory" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_GUILD_CONTRIBUTION_OBJECTIVES_DEFEND_TERRITORY, who);
            case "experience_bottle_collect" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_GUILD_CONTRIBUTION_OBJECTIVES_EXPERIENCE_BOTTLE_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }

    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_GUILD_CONTRIBUTION_DIALOGS, who);
    }

    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_GUILD_CONTRIBUTION_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_GUILD_CONTRIBUTION_ACCEPT, who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_GUILD_CONTRIBUTION_DECLINE, who);
    }
}