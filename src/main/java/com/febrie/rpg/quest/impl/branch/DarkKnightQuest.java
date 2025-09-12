package com.febrie.rpg.quest.impl.branch;

import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 어둠의 기사 전직 퀘스트
 * 어둠의 길을 선택한 플레이어가 다크나이트가 되는 퀘스트
 *
 * @author Febrie
 */
public class DarkKnightQuest extends Quest {

    /**
     * 기본 생성자
     */
    public DarkKnightQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.BRANCH_DARK_KNIGHT)
                .objectives(List.of(
                        new InteractNPCObjective("dark_master", "dark_knight_master"),
                        new CollectItemObjective("coal_collect", Material.COAL, 15),
                        new KillMobObjective("corrupt_souls", EntityType.ZOMBIE_VILLAGER, 30),
                        new KillMobObjective("feast_on_fear", EntityType.PHANTOM, 40),
                        new VisitLocationObjective("shadow_altar", "dark_shrine"),
                        new SurviveObjective("embrace_darkness", 900), // 15 minutes
                        new CraftItemObjective("netherite_sword_craft", Material.NETHERITE_SWORD, 1),
                        new PlaceBlockObjective("build_throne", Material.OBSIDIAN, 12),
                        new KillMobObjective("eliminate_light", EntityType.BLAZE, 25),
                        new CollectItemObjective("nether_star_collect", Material.NETHER_STAR, 3),
                        new DeliverItemObjective("oath_of_darkness", Material.NETHERITE_SWORD, 1, "dark_knight_master")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 15000)
                        .addExperience(3000)
                        .build())
                .sequential(true)
                .category(QuestCategory.BRANCH)
                .minLevel(40)
                .maxLevel(100)
                .addPrerequisite(QuestID.MAIN_PATH_OF_DARKNESS)
                .addExclusive(QuestID.BRANCH_LIGHT_PALADIN)
                .addExclusive(QuestID.BRANCH_NEUTRAL_GUARDIAN);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_BRANCH_DARK_KNIGHT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_BRANCH_DARK_KNIGHT_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "dark_master" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_DARK_KNIGHT_OBJECTIVES_DARK_MASTER, who);
            case "coal_collect" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_DARK_KNIGHT_OBJECTIVES_COAL_COLLECT, who);
            case "corrupt_souls" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_DARK_KNIGHT_OBJECTIVES_CORRUPT_SOULS, who);
            case "feast_on_fear" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_DARK_KNIGHT_OBJECTIVES_FEAST_ON_FEAR, who);
            case "shadow_altar" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_DARK_KNIGHT_OBJECTIVES_SHADOW_ALTAR, who);
            case "embrace_darkness" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_DARK_KNIGHT_OBJECTIVES_EMBRACE_DARKNESS, who);
            case "netherite_sword_craft" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_DARK_KNIGHT_OBJECTIVES_NETHERITE_SWORD_CRAFT, who);
            case "build_throne" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_DARK_KNIGHT_OBJECTIVES_BUILD_THRONE, who);
            case "eliminate_light" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_DARK_KNIGHT_OBJECTIVES_ELIMINATE_LIGHT, who);
            case "nether_star_collect" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_DARK_KNIGHT_OBJECTIVES_NETHER_STAR_COLLECT, who);
            case "oath_of_darkness" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_DARK_KNIGHT_OBJECTIVES_OATH_OF_DARKNESS, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_BRANCH_DARK_KNIGHT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_BRANCH_DARK_KNIGHT_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_BRANCH_DARK_KNIGHT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_BRANCH_DARK_KNIGHT_DECLINE, who);
    }
}