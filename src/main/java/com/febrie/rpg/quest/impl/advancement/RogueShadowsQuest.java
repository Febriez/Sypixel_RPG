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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ArrayList;

/**
 * 로그 그림자 승급 퀘스트
 * 로그 클래스의 그림자 기술을 마스터하기 위한 퀘스트
 *
 * @author Febrie
 */
public class RogueShadowsQuest extends Quest {

    /**
     * 기본 생성자
     */
    public RogueShadowsQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.CLASS_ROGUE_SHADOWS)
                .objectives(List.of(
                        new InteractNPCObjective("shadow_master", "shadow_master"),
                        new InteractNPCObjective("stealth_training", "advanced_stealth_training"),
                        new InteractNPCObjective("assassination_techniques", "shadow_assassination"),
                        new InteractNPCObjective("shadow_walk", "shadow_manipulation"),
                        new InteractNPCObjective("rogue_guild", "shadow_rogues_guild")
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
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_ROGUE_SHADOWS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_ROGUE_SHADOWS_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "shadow_master" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_ROGUE_SHADOWS_OBJECTIVES_SHADOW_MASTER, who);
            case "stealth_training" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_ROGUE_SHADOWS_OBJECTIVES_STEALTH_TRAINING, who);
            case "assassination_techniques" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_ROGUE_SHADOWS_OBJECTIVES_ASSASSINATION_TECHNIQUES, who);
            case "shadow_walk" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_ROGUE_SHADOWS_OBJECTIVES_SHADOW_WALK, who);
            case "rogue_guild" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_ROGUE_SHADOWS_OBJECTIVES_ROGUE_GUILD, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_ROGUE_SHADOWS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_ROGUE_SHADOWS_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_ROGUE_SHADOWS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_ROGUE_SHADOWS_DECLINE, who);
    }
}