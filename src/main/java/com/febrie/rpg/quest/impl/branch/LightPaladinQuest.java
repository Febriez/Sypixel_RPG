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

import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 빛의 성기사 전직 퀘스트
 * 빛의 길을 선택한 플레이어가 성기사가 되는 퀘스트
 *
 * @author Febrie
 */
public class LightPaladinQuest extends Quest {

    /**
     * 기본 생성자
     */
    public LightPaladinQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.BRANCH_LIGHT_PALADIN)
                .objectives(List.of(
                        new InteractNPCObjective("paladin_master", "light_paladin_master"),
                        new CollectItemObjective("potion_collect", Material.POTION, 10),
                        new KillMobObjective("purge_undead", EntityType.ZOMBIE, 50),
                        new KillMobObjective("purge_skeletons", EntityType.SKELETON, 50),
                        new VisitLocationObjective("holy_shrine", "light_shrine"),
                        new SurviveObjective("meditation", 600), // 10 minutes
                        new CraftItemObjective("golden_sword_craft", Material.GOLDEN_SWORD, 1),
                        new PlaceBlockObjective("build_altar", Material.GLOWSTONE, 9),
                        new KillMobObjective("defeat_darkness", EntityType.WITHER_SKELETON, 20),
                        new CollectItemObjective("glowstone_dust_collect", Material.GLOWSTONE_DUST, 30),
                        new DeliverItemObjective("golden_sword_deliver", Material.GOLDEN_SWORD, 1, "paladin_master")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 12000)
                        .addExperience(2500)
                        .build())
                .sequential(true)
                .category(QuestCategory.BRANCH)
                .minLevel(40)
                .maxLevel(100)
                .addPrerequisite(QuestID.MAIN_PATH_OF_LIGHT)
                .addExclusive(QuestID.BRANCH_DARK_KNIGHT)
                .addExclusive(QuestID.BRANCH_NEUTRAL_GUARDIAN);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_BRANCH_LIGHT_PALADIN_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_BRANCH_LIGHT_PALADIN_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "paladin_master" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_PALADIN_MASTER, who);
            case "potion_collect" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_POTION_COLLECT, who);
            case "purge_undead" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_PURGE_UNDEAD, who);
            case "purge_skeletons" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_PURGE_SKELETONS, who);
            case "holy_shrine" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_HOLY_SHRINE, who);
            case "meditation" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_MEDITATION, who);
            case "golden_sword_craft" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_GOLDEN_SWORD_CRAFT, who);
            case "build_altar" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_BUILD_ALTAR, who);
            case "defeat_darkness" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_DEFEAT_DARKNESS, who);
            case "glowstone_dust_collect" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_GLOWSTONE_DUST_COLLECT, who);
            case "golden_sword_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_GOLDEN_SWORD_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_BRANCH_LIGHT_PALADIN_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_BRANCH_LIGHT_PALADIN_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_BRANCH_LIGHT_PALADIN_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_BRANCH_LIGHT_PALADIN_DECLINE, who);
    }
}