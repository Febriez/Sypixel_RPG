package com.febrie.rpg.quest.impl.advancement;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ArrayList;

/**
 * 버서커 분노 승급 퀘스트
 * 버서커 클래스의 분노 조절과 광폭전투를 마스터하기 위한 퀘스트
 *
 * @author Febrie
 */
public class BerserkerRageQuest extends Quest {

    /**
     * 기본 생성자
     */
    public BerserkerRageQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.CLASS_BERSERKER_RAGE)
                .objectives(List.of(
                        new InteractNPCObjective("berserker_trainer", "rage_master"),
                        new InteractNPCObjective("rage_control", "berserker_rage_control"),
                        new SurviveObjective("combat_frenzy", 600), // 10분간 생존
                        new KillMobObjective("berserk_trial", EntityType.IRON_GOLEM, 5),
                        new InteractNPCObjective("master_rage", "berserker_mastery")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 20000)
                        .addCurrency(CurrencyType.EXP, 4000)
                        .addExperience(6000)
                        .build())
                .sequential(true)
                .category(QuestCategory.ADVANCEMENT)
                .minLevel(30)
                .maxLevel(100);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_BERSERKER_RAGE_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_BERSERKER_RAGE_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "berserker_trainer" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_BERSERKER_RAGE_OBJECTIVES_BERSERKER_TRAINER, who);
            case "rage_control" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_BERSERKER_RAGE_OBJECTIVES_RAGE_CONTROL, who);
            case "combat_frenzy" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_BERSERKER_RAGE_OBJECTIVES_COMBAT_FRENZY, who);
            case "berserk_trial" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_BERSERKER_RAGE_OBJECTIVES_BERSERK_TRIAL, who);
            case "master_rage" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_BERSERKER_RAGE_OBJECTIVES_MASTER_RAGE, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_BERSERKER_RAGE_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_BERSERKER_RAGE_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_BERSERKER_RAGE_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_BERSERKER_RAGE_DECLINE, who);
    }
}