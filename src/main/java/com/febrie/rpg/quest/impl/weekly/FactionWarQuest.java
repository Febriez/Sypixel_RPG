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
 * 주간 진영 전쟁 - 주간 퀘스트
 * 매주 진행되는 진영 간 전쟁에 참여하는 퀘스트
 *
 * @author Febrie
 */
public class FactionWarQuest extends Quest {

    /**
     * 기본 생성자
     */
    public FactionWarQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.WEEKLY_FACTION_WAR)
                .objectives(List.of(
                    new InteractNPCObjective("join_faction", "faction_recruiter"),
                    new BreakBlockObjective("capture_outposts", Material.BEACON, 3),
                    new KillPlayerObjective("defeat_enemies", 20),
                    new SurviveObjective("defend_base", 600), // 10 minutes in seconds
                    new InteractNPCObjective("win_war", "faction_commander")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 6000)
                        .addItem(new ItemStack(Material.NETHERITE_CHESTPLATE))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 2))
                        .addExperience(2500)
                        .build())
                .sequential(true)
                .weekly(true)
                .category(QuestCategory.WEEKLY)
                .minLevel(45)
                .completionLimit(1);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_FACTION_WAR_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_FACTION_WAR_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "join_faction" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_FACTION_WAR_OBJECTIVES_JOIN_FACTION, who);
            case "capture_outposts" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_FACTION_WAR_OBJECTIVES_CAPTURE_OUTPOSTS, who);
            case "defeat_enemies" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_FACTION_WAR_OBJECTIVES_DEFEAT_ENEMIES, who);
            case "defend_base" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_FACTION_WAR_OBJECTIVES_DEFEND_BASE, who);
            case "win_war" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_FACTION_WAR_OBJECTIVES_WIN_WAR, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }

    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_FACTION_WAR_DIALOGS, who);
    }

    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_FACTION_WAR_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_FACTION_WAR_ACCEPT, who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_FACTION_WAR_DECLINE, who);
    }
}