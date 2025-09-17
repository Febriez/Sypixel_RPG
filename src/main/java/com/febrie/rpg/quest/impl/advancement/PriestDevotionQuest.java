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
 * 성직자 헌신 승급 퀘스트
 * 성직자 클래스의 신성한 헌신 경로를 따르기 위한 퀘스트
 *
 * @author Febrie
 */
public class PriestDevotionQuest extends Quest {

    /**
     * 기본 생성자
     */
    public PriestDevotionQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.CLASS_PRIEST_DEVOTION)
                .objectives(List.of(
                        new InteractNPCObjective("high_priest", "high_priest"),
                        new InteractNPCObjective("prayer_ritual", "divine_prayer_ritual"),
                        new InteractNPCObjective("healing_mastery", "divine_healing_mastery"),
                        new InteractNPCObjective("divine_blessing", "priest_divine_blessing"),
                        new InteractNPCObjective("priest_ordination", "priest_ceremony")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 20000)
                        .addCurrency(CurrencyType.EXP, 4000)
                        .addExperience(6000)
                        .build())
                .sequential(true)
                .category(QuestCategory.ADVANCEMENT)
                .minLevel(25)
                .maxLevel(100);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_PRIEST_DEVOTION_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_PRIEST_DEVOTION_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "high_priest" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_PRIEST_DEVOTION_OBJECTIVES_HIGH_PRIEST, who);
            case "prayer_ritual" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_PRIEST_DEVOTION_OBJECTIVES_PRAYER_RITUAL, who);
            case "healing_mastery" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_PRIEST_DEVOTION_OBJECTIVES_HEALING_MASTERY, who);
            case "divine_blessing" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_PRIEST_DEVOTION_OBJECTIVES_DIVINE_BLESSING, who);
            case "priest_ordination" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_PRIEST_DEVOTION_OBJECTIVES_PRIEST_ORDINATION, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_PRIEST_DEVOTION_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_PRIEST_DEVOTION_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_PRIEST_DEVOTION_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_PRIEST_DEVOTION_DECLINE, who);
    }
}