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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ArrayList;

/**
 * 드루이드 자연 승급 퀘스트
 * 드루이드 클래스의 자연과의 조화를 마스터하기 위한 퀘스트
 *
 * @author Febrie
 */
public class DruidNatureQuest extends Quest {

    /**
     * 기본 생성자
     */
    public DruidNatureQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.CLASS_DRUID_NATURE)
                .objectives(List.of(
                        new InteractNPCObjective("druid_elder", "elder_druid"),
                        new InteractNPCObjective("nature_bond", "nature_spirit_bond"),
                        new InteractNPCObjective("animal_forms", "transformation_teacher"), // 3가지 동물 형태 학습
                        new InteractNPCObjective("nature_magic", "elemental_teacher"),
                        new InteractNPCObjective("druid_circle", "ancient_druid_circle")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 18000)
                        .addCurrency(CurrencyType.EXP, 3500)
                        .addExperience(5500)
                        .build())
                .sequential(true)
                .category(QuestCategory.ADVANCEMENT)
                .minLevel(25)
                .maxLevel(100);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_DRUID_NATURE_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_DRUID_NATURE_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "druid_elder" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_DRUID_NATURE_OBJECTIVES_DRUID_ELDER, who);
            case "nature_bond" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_DRUID_NATURE_OBJECTIVES_NATURE_BOND, who);
            case "animal_forms" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_DRUID_NATURE_OBJECTIVES_ANIMAL_FORMS, who);
            case "nature_magic" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_DRUID_NATURE_OBJECTIVES_NATURE_MAGIC, who);
            case "druid_circle" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_DRUID_NATURE_OBJECTIVES_DRUID_CIRCLE, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_DRUID_NATURE_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_DRUID_NATURE_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_DRUID_NATURE_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_DRUID_NATURE_DECLINE, who);
    }
}