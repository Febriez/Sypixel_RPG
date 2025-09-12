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
 * 전사 승급 퀘스트 (승급 카테고리)
 * 전사 클래스의 전사의 길을 걷기 위한 승급 퀘스트
 *
 * @author Febrie
 */
public class WarriorAdvancementQuest extends Quest {

    /**
     * 기본 생성자
     */
    public WarriorAdvancementQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.CLASS_WARRIOR_ADVANCEMENT)
                .objectives(List.of(
                        new InteractNPCObjective("warrior_general", "warrior_master"),
                        new InteractNPCObjective("combat_mastery", "advanced_combat_techniques"),
                        new KillMobObjective("weapon_expertise", EntityType.IRON_GOLEM, 3), // 3가지 무기 전문화
                        new InteractNPCObjective("tactical_training", "military_tactical_training"),
                        new InteractNPCObjective("warrior_honor", "warrior_code_honor")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 12000)
                        .addCurrency(CurrencyType.EXP, 2500)
                        .addExperience(4000)
                        .build())
                .sequential(true)
                .category(QuestCategory.ADVANCEMENT)
                .minLevel(20)
                .maxLevel(100);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_WARRIOR_ADVANCEMENT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_WARRIOR_ADVANCEMENT_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "warrior_general" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_WARRIOR_ADVANCEMENT_OBJECTIVES_WARRIOR_GENERAL, who);
            case "combat_mastery" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_WARRIOR_ADVANCEMENT_OBJECTIVES_COMBAT_MASTERY, who);
            case "weapon_expertise" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_WARRIOR_ADVANCEMENT_OBJECTIVES_WEAPON_EXPERTISE, who);
            case "tactical_training" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_WARRIOR_ADVANCEMENT_OBJECTIVES_TACTICAL_TRAINING, who);
            case "warrior_honor" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_WARRIOR_ADVANCEMENT_OBJECTIVES_WARRIOR_HONOR, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_WARRIOR_ADVANCEMENT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_WARRIOR_ADVANCEMENT_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_WARRIOR_ADVANCEMENT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_WARRIOR_ADVANCEMENT_DECLINE, who);
    }
}