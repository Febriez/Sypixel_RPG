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
 * 팔라딘 맹세 승급 퀘스트
 * 팔라딘 클래스의 신성한 맹세를 맺기 위한 퀘스트
 *
 * @author Febrie
 */
public class PaladinOathQuest extends Quest {

    /**
     * 기본 생성자
     */
    public PaladinOathQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.CLASS_PALADIN_OATH)
                .objectives(List.of(
                        new InteractNPCObjective("paladin_commander", "high_paladin"),
                        new InteractNPCObjective("sacred_oath", "paladin_sacred_oath"),
                        new InteractNPCObjective("holy_training", "holy_combat_training"),
                        new InteractNPCObjective("divine_power", "divine_blessing"),
                        new InteractNPCObjective("paladin_ceremony", "paladin_ordination")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 22000)
                        .addCurrency(CurrencyType.EXP, 4500)
                        .addExperience(6500)
                        .build())
                .sequential(true)
                .category(QuestCategory.ADVANCEMENT)
                .minLevel(25)
                .maxLevel(100);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_PALADIN_OATH_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_PALADIN_OATH_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "paladin_commander" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_PALADIN_OATH_OBJECTIVES_PALADIN_COMMANDER, who);
            case "sacred_oath" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_PALADIN_OATH_OBJECTIVES_SACRED_OATH, who);
            case "holy_training" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_PALADIN_OATH_OBJECTIVES_HOLY_TRAINING, who);
            case "divine_power" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_PALADIN_OATH_OBJECTIVES_DIVINE_POWER, who);
            case "paladin_ceremony" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_PALADIN_OATH_OBJECTIVES_PALADIN_CEREMONY, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_PALADIN_OATH_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_PALADIN_OATH_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_PALADIN_OATH_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_PALADIN_OATH_DECLINE, who);
    }
}