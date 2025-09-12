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

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ArrayList;

/**
 * 소환사 유대 승급 퀘스트
 * 소환사 클래스의 신비한 유대를 형성하기 위한 퀘스트
 *
 * @author Febrie
 */
public class SummonerBondQuest extends Quest {

    /**
     * 기본 생성자
     */
    public SummonerBondQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.CLASS_SUMMONER_BOND)
                .objectives(List.of(
                        new InteractNPCObjective("summoner_sage", "spirit_master"),
                        new InteractNPCObjective("summon_familiar", "first_familiar_bond"),
                        new KillMobObjective("elemental_summons", EntityType.BLAZE, 4), // 4가지 원소 소환수 마스터
                        new InteractNPCObjective("bond_strength", "mystical_bond_enhancement"),
                        new InteractNPCObjective("summoner_pact", "eternal_summoner_pact")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 16000)
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
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_SUMMONER_BOND_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_SUMMONER_BOND_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "summoner_sage" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_SUMMONER_BOND_OBJECTIVES_SUMMONER_SAGE, who);
            case "summon_familiar" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_SUMMONER_BOND_OBJECTIVES_SUMMON_FAMILIAR, who);
            case "elemental_summons" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_SUMMONER_BOND_OBJECTIVES_ELEMENTAL_SUMMONS, who);
            case "bond_strength" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_SUMMONER_BOND_OBJECTIVES_BOND_STRENGTH, who);
            case "summoner_pact" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_SUMMONER_BOND_OBJECTIVES_SUMMONER_PACT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_SUMMONER_BOND_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_SUMMONER_BOND_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_SUMMONER_BOND_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_SUMMONER_BOND_DECLINE, who);
    }
}