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
 * 네크로맨서 계약 승급 퀘스트
 * 네크로맨서 클래스의 어둠의 계약을 맺기 위한 퀘스트
 *
 * @author Febrie
 */
public class NecromancerPactQuest extends Quest {

    /**
     * 기본 생성자
     */
    public NecromancerPactQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.CLASS_NECROMANCER_PACT)
                .objectives(List.of(
                        new InteractNPCObjective("necromancer_lord", "dark_master"),
                        new InteractNPCObjective("death_magic", "necromancy_basics"),
                        new KillMobObjective("raise_undead", EntityType.ZOMBIE, 25), // 좀비 25마리 처치
                        new InteractNPCObjective("soul_pact", "death_realm_pact"),
                        new InteractNPCObjective("master_death", "death_magic_mastery")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 30000)
                        .addCurrency(CurrencyType.EXP, 6000)
                        .addExperience(9000)
                        .build())
                .sequential(true)
                .category(QuestCategory.ADVANCEMENT)
                .minLevel(30)
                .maxLevel(100);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_NECROMANCER_PACT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_NECROMANCER_PACT_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "necromancer_lord" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_NECROMANCER_PACT_OBJECTIVES_NECROMANCER_LORD, who);
            case "death_magic" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_NECROMANCER_PACT_OBJECTIVES_DEATH_MAGIC, who);
            case "raise_undead" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_NECROMANCER_PACT_OBJECTIVES_RAISE_UNDEAD, who);
            case "soul_pact" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_NECROMANCER_PACT_OBJECTIVES_SOUL_PACT, who);
            case "master_death" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_NECROMANCER_PACT_OBJECTIVES_MASTER_DEATH, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_ADVANCEMENT_NECROMANCER_PACT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_NECROMANCER_PACT_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_NECROMANCER_PACT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_ADVANCEMENT_NECROMANCER_PACT_DECLINE, who);
    }
}