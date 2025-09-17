package com.febrie.rpg.quest.impl.clazz;

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
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ArrayList;

/**
 * 전사 승급 퀘스트
 * 전사 클래스로 전직하기 위한 퀘스트
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
                        new ReachLevelObjective("warrior_level", 30),
                        new InteractNPCObjective("warrior_master", "warrior_master"),
                        new KillMobObjective("prove_combat", EntityType.IRON_GOLEM, 20),
                        new KillPlayerObjective("prove_pvp", 10),
                        new CollectItemObjective("iron_ingot_collect", Material.IRON_INGOT, 50),
                        new CraftItemObjective("diamond_sword_craft", Material.DIAMOND_SWORD, 1),
                        new CraftItemObjective("diamond_chestplate_craft", Material.DIAMOND_CHESTPLATE, 1),
                        new SurviveObjective("endurance_test", 600), // 10 minutes
                        new KillMobObjective("final_trial", EntityType.RAVAGER, 5),
                        new DeliverItemObjective("diamond_sword_deliver", Material.DIAMOND_SWORD, 1, "warrior_master")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 10000)
                        .addCurrency(CurrencyType.EXP, 2500)
                        .addExperience(3000)
                        .build())
                .sequential(true)
                .category(QuestCategory.ADVANCEMENT)
                .minLevel(30)
                .maxLevel(100)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_CLAZZ_WARRIOR_ADVANCEMENT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_CLAZZ_WARRIOR_ADVANCEMENT_INFO, who);
    }

        @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "warrior_level" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_WARRIOR_ADVANCEMENT_OBJECTIVES_WARRIOR_LEVEL, who);
            case "warrior_master" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_WARRIOR_ADVANCEMENT_OBJECTIVES_WARRIOR_MASTER, who);
            case "prove_combat" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_WARRIOR_ADVANCEMENT_OBJECTIVES_PROVE_COMBAT, who);
            case "prove_pvp" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_WARRIOR_ADVANCEMENT_OBJECTIVES_PROVE_PVP, who);
            case "iron_ingot_collect" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_WARRIOR_ADVANCEMENT_OBJECTIVES_IRON_INGOT_COLLECT, who);
            case "diamond_sword_craft" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_WARRIOR_ADVANCEMENT_OBJECTIVES_DIAMOND_SWORD_CRAFT, who);
            case "diamond_chestplate_craft" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_WARRIOR_ADVANCEMENT_OBJECTIVES_DIAMOND_CHESTPLATE_CRAFT, who);
            case "endurance_test" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_WARRIOR_ADVANCEMENT_OBJECTIVES_ENDURANCE_TEST, who);
            case "final_trial" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_WARRIOR_ADVANCEMENT_OBJECTIVES_FINAL_TRIAL, who);
            case "diamond_sword_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_CLASS_WARRIOR_ADVANCEMENT_OBJECTIVES_DIAMOND_SWORD_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_CLAZZ_WARRIOR_ADVANCEMENT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_CLAZZ_WARRIOR_ADVANCEMENT_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_CLAZZ_WARRIOR_ADVANCEMENT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_CLAZZ_WARRIOR_ADVANCEMENT_DECLINE, who);
    }
}