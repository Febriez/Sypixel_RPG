package com.febrie.rpg.quest.impl.main.chapter4;

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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 혼돈의 폭풍 - 메인 스토리 퀘스트 (Chapter 4)
 * 차원간 혼돈의 폭풍을 진압하는 퀘스트
 *
 * @author Febrie
 */
public class ChaosStormQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public ChaosStormQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_CHAOS_STORM)
                .objectives(List.of(
                        // 혼돈의 징조
                        new InteractNPCObjective("storm_prophet", "chaos_storm_prophet"),
                        new VisitLocationObjective("chaos_epicenter", "chaos_storm_epicenter"),
                        new CollectItemObjective("crying_obsidian_collect", Material.CRYING_OBSIDIAN, 30),
                        new KillMobObjective("chaos_spawns", EntityType.ENDERMAN, 50),
                        
                        // 폭풍의 핵심 추적
                        new CollectItemObjective("compass_collect", Material.COMPASS, 1),
                        new VisitLocationObjective("storm_eye", "chaos_storm_eye"),
                        new SurviveObjective("survive_storm", 600), // 10분 폭풍 생존
                        new CollectItemObjective("ender_pearl_collect", Material.ENDER_PEARL, 100),
                        
                        // 안정화 장치 제작
                        new InteractNPCObjective("stability_engineer", "dimensional_engineer"),
                        new CollectItemObjective("redstone_block_collect", Material.REDSTONE_BLOCK, 20),
                        new CollectItemObjective("sea_lantern_collect", Material.SEA_LANTERN, 15),
                        new CollectItemObjective("end_crystal_collect", Material.END_CRYSTAL, 10),
                        new DeliverItemObjective("beacon_deliver", Material.BEACON, 1, "stability_engineer"),
                        
                        // 폭풍 지역 진입
                        new VisitLocationObjective("inner_storm", "chaos_storm_inner"),
                        new PlaceBlockObjective("place_stabilizers", Material.BEACON, 4),
                        new KillMobObjective("chaos_elementals", EntityType.VEX, 100),
                        new KillMobObjective("chaos_guardians", EntityType.RAVAGER, 10),
                        
                        // 혼돈의 군주
                        new InteractNPCObjective("chaos_lord", "chaos_lord"),
                        new KillMobObjective("chaos_lord_minions", EntityType.VINDICATOR, 50),
                        new KillMobObjective("chaos_lord_battle", EntityType.WITHER, 5),
                        new CollectItemObjective("wither_skeleton_skull_collect", Material.WITHER_SKELETON_SKULL, 3),
                        
                        // 폭풍 진압
                        new VisitLocationObjective("storm_core", "chaos_storm_core"),
                        new PlaceBlockObjective("activate_stabilizers", Material.END_ROD, 20),
                        new SurviveObjective("stabilization_process", 900), // 15분
                        new CollectItemObjective("nether_star_collect", Material.NETHER_STAR, 1),
                        
                        // 평화 획득
                        new InteractNPCObjective("storm_prophet_calm", "chaos_storm_prophet"),
                        new CollectItemObjective("end_rod_collect", Material.END_ROD, 1),
                        new CollectItemObjective("beacon_collect", Material.BEACON, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 55000)
                        .addCurrency(CurrencyType.DIAMOND, 500)
                        .addItem(new ItemStack(Material.NETHERITE_BOOTS))
                        .addItem(new ItemStack(Material.END_ROD, 10))
                        .addItem(new ItemStack(Material.WITHER_SKELETON_SKULL, 3))
                        .addItem(new ItemStack(Material.BEACON, 2))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 6))
                        .addExperience(28000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_REALM_DEFENDERS)
                .minLevel(85)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "storm_prophet" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_STORM_PROPHET, who);
            case "chaos_epicenter" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CHAOS_EPICENTER, who);
            case "crying_obsidian_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CRYING_OBSIDIAN_COLLECT, who);
            case "chaos_spawns" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CHAOS_SPAWNS, who);
            case "compass_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_COMPASS_COLLECT, who);
            case "storm_eye" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_STORM_EYE, who);
            case "survive_storm" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_SURVIVE_STORM, who);
            case "ender_pearl_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_ENDER_PEARL_COLLECT, who);
            case "stability_engineer" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_STABILITY_ENGINEER, who);
            case "redstone_block_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_REDSTONE_BLOCK_COLLECT, who);
            case "sea_lantern_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_SEA_LANTERN_COLLECT, who);
            case "end_crystal_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_END_CRYSTAL_COLLECT, who);
            case "beacon_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_BEACON_DELIVER, who);
            case "inner_storm" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_INNER_STORM, who);
            case "place_stabilizers" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_PLACE_STABILIZERS, who);
            case "chaos_elementals" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CHAOS_ELEMENTALS, who);
            case "chaos_guardians" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CHAOS_GUARDIANS, who);
            case "chaos_lord" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CHAOS_LORD, who);
            case "chaos_lord_minions" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CHAOS_LORD_MINIONS, who);
            case "chaos_lord_battle" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CHAOS_LORD_BATTLE, who);
            case "wither_skeleton_skull_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_WITHER_SKELETON_SKULL_COLLECT, who);
            case "storm_core" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_STORM_CORE, who);
            case "activate_stabilizers" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_ACTIVATE_STABILIZERS, who);
            case "stabilization_process" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_STABILIZATION_PROCESS, who);
            case "nether_star_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_NETHER_STAR_COLLECT, who);
            case "storm_prophet_calm" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_STORM_PROPHET_CALM, who);
            case "end_rod_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_END_ROD_COLLECT, who);
            case "beacon_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_BEACON_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHAOS_STORM_DECLINE, who);
    }
}