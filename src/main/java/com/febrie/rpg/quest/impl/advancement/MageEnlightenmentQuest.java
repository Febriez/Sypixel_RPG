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
 * 법사 깨달음 승급 퀘스트
 * 법사 클래스의 마법 깨달음을 얻기 위한 퀘스트
 *
 * @author Febrie
 */
public class MageEnlightenmentQuest extends Quest {

    /**
     * 기본 생성자
     */
    public MageEnlightenmentQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.CLASS_MAGE_ENLIGHTENMENT)
                .objectives(List.of(
                        new InteractNPCObjective("archmage", "grand_archmage"),
                        new InteractNPCObjective("magical_theory", "arcane_knowledge"),
                        new InteractNPCObjective("elemental_magic", "elemental_teacher"),
                        new InteractNPCObjective("arcane_power", "advanced_arcane_mastery"),
                        new InteractNPCObjective("mage_trial", "enlightenment_trial")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 25000)
                        .addCurrency(CurrencyType.EXP, 5000)
                        .addExperience(7500)
                        .build())
                .sequential(true)
                .category(QuestCategory.ADVANCEMENT)
                .minLevel(30)
                .maxLevel(100);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_MAGE_ENLIGHTENMENT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_MAGE_ENLIGHTENMENT_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "archmage" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_MAGE_ENLIGHTENMENT_OBJECTIVES_ARCHMAGE, who);
            case "magical_theory" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_MAGE_ENLIGHTENMENT_OBJECTIVES_MAGICAL_THEORY, who);
            case "elemental_magic" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_MAGE_ENLIGHTENMENT_OBJECTIVES_ELEMENTAL_MAGIC, who);
            case "arcane_power" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_MAGE_ENLIGHTENMENT_OBJECTIVES_ARCANE_POWER, who);
            case "mage_trial" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_MAGE_ENLIGHTENMENT_OBJECTIVES_MAGE_TRIAL, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_MAGE_ENLIGHTENMENT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_MAGE_ENLIGHTENMENT_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_MAGE_ENLIGHTENMENT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_MAGE_ENLIGHTENMENT_DECLINE, who);
    }
}