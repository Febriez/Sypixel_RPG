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
 * 궁수 정밀함 승급 퀘스트
 * 궁수 클래스의 정밀 사격 기술을 마스터하기 위한 퀘스트
 *
 * @author Febrie
 */
public class ArcherPrecisionQuest extends Quest {

    /**
     * 기본 생성자
     */
    public ArcherPrecisionQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.CLASS_ARCHER_PRECISION)
                .objectives(List.of(
                        new InteractNPCObjective("archery_master", "master_archer"),
                        new InteractNPCObjective("precision_training", "precision_archery"),
                        new KillMobObjective("moving_targets", EntityType.SKELETON, 20), // 스켈레톤 20마리 처치
                        new KillMobObjective("long_range", EntityType.CREEPER, 50), // 크리퍼 50마리 처치
                        new InteractNPCObjective("final_test", "precision_trial")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 15000)
                        .addCurrency(CurrencyType.EXP, 3000)
                        .addExperience(5000)
                        .build())
                .sequential(true)
                .category(QuestCategory.ADVANCEMENT)
                .minLevel(25)
                .maxLevel(100);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_ARCHER_PRECISION_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_ARCHER_PRECISION_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "archery_master" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_ARCHER_PRECISION_OBJECTIVES_ARCHERY_MASTER, who);
            case "precision_training" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_ARCHER_PRECISION_OBJECTIVES_PRECISION_TRAINING, who);
            case "moving_targets" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_ARCHER_PRECISION_OBJECTIVES_MOVING_TARGETS, who);
            case "long_range" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_ARCHER_PRECISION_OBJECTIVES_LONG_RANGE, who);
            case "final_test" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_ARCHER_PRECISION_OBJECTIVES_FINAL_TEST, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_ARCHER_PRECISION_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_ARCHER_PRECISION_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_ARCHER_PRECISION_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_ARCHER_PRECISION_DECLINE, who);
    }
}