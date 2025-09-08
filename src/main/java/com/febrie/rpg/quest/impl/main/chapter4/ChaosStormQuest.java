package com.febrie.rpg.quest.impl.main.chapter4;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
                        new InteractNPCObjective("storm_prophet", "chaos_storm_prophet", 1),
                        new VisitLocationObjective("chaos_epicenter", "chaos_storm_epicenter"),
                        new CollectItemObjective("chaos_fragments", Material.CRYING_OBSIDIAN, 30),
                        new KillMobObjective("chaos_spawns", EntityType.ENDERMAN, 50),
                        
                        // 폭풍의 핵심 추적
                        new CollectItemObjective("storm_tracker", Material.COMPASS, 1),
                        new VisitLocationObjective("storm_eye", "chaos_storm_eye"),
                        new SurviveObjective("survive_storm", 600), // 10분 폭풍 생존
                        new CollectItemObjective("storm_essence", Material.ENDER_PEARL, 100),
                        
                        // 안정화 장치 제작
                        new InteractNPCObjective("stability_engineer", "dimensional_engineer", 1),
                        new CollectItemObjective("stabilizer_parts", Material.REDSTONE_BLOCK, 20),
                        new CollectItemObjective("power_cores", Material.SEA_LANTERN, 15),
                        new CollectItemObjective("control_crystals", Material.END_CRYSTAL, 10),
                        new DeliverItemObjective("build_stabilizer", "stability_engineer", Material.BEACON, 1),
                        
                        // 폭풍 지역 진입
                        new VisitLocationObjective("inner_storm", "chaos_storm_inner"),
                        new PlaceBlockObjective("place_stabilizers", Material.BEACON, 4),
                        new KillMobObjective("chaos_elementals", EntityType.VEX, 100),
                        new KillMobObjective("chaos_guardians", EntityType.RAVAGER, 10),
                        
                        // 혼돈의 군주
                        new InteractNPCObjective("chaos_lord", "chaos_lord", 1),
                        new KillMobObjective("chaos_lord_minions", EntityType.VINDICATOR, 50),
                        new KillMobObjective("chaos_lord_battle", EntityType.WITHER, 5),
                        new CollectItemObjective("chaos_crown", Material.WITHER_SKELETON_SKULL, 3),
                        
                        // 폭풍 진압
                        new VisitLocationObjective("storm_core", "chaos_storm_core"),
                        new PlaceBlockObjective("activate_stabilizers", Material.END_ROD, 20),
                        new SurviveObjective("stabilization_process", 900), // 15분
                        new CollectItemObjective("stability_core", Material.NETHER_STAR, 1),
                        
                        // 평화 획득
                        new InteractNPCObjective("storm_prophet_calm", "chaos_storm_prophet", 1),
                        new CollectItemObjective("storm_control_staff", Material.END_ROD, 1),
                        new CollectItemObjective("dimensional_stabilizer", Material.BEACON, 1)
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
        return LangManager.text(LangKey.QUEST_MAIN_CHAOS_STORM_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "storm_prophet" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_STORM_PROPHET, who);
            case "chaos_epicenter" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CHAOS_EPICENTER, who);
            case "chaos_fragments" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CHAOS_FRAGMENTS, who);
            case "chaos_spawns" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CHAOS_SPAWNS, who);
            case "storm_tracker" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_STORM_TRACKER, who);
            case "storm_eye" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_STORM_EYE, who);
            case "survive_storm" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_SURVIVE_STORM, who);
            case "storm_essence" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_STORM_ESSENCE, who);
            case "stability_engineer" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_STABILITY_ENGINEER, who);
            case "stabilizer_parts" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_STABILIZER_PARTS, who);
            case "power_cores" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_POWER_CORES, who);
            case "control_crystals" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CONTROL_CRYSTALS, who);
            case "build_stabilizer" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_BUILD_STABILIZER, who);
            case "inner_storm" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_INNER_STORM, who);
            case "place_stabilizers" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_PLACE_STABILIZERS, who);
            case "chaos_elementals" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CHAOS_ELEMENTALS, who);
            case "chaos_guardians" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CHAOS_GUARDIANS, who);
            case "chaos_lord" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CHAOS_LORD, who);
            case "chaos_lord_minions" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CHAOS_LORD_MINIONS, who);
            case "chaos_lord_battle" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CHAOS_LORD_BATTLE, who);
            case "chaos_crown" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_CHAOS_CROWN, who);
            case "storm_core" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_STORM_CORE, who);
            case "activate_stabilizers" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_ACTIVATE_STABILIZERS, who);
            case "stabilization_process" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_STABILIZATION_PROCESS, who);
            case "stability_core" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_STABILITY_CORE, who);
            case "storm_prophet_calm" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_STORM_PROPHET_CALM, who);
            case "storm_control_staff" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_STORM_CONTROL_STAFF, who);
            case "dimensional_stabilizer" -> LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_OBJECTIVES_DIMENSIONAL_STABILIZER, who);
            default -> List.of(Component.text("Objective: " + objective.getId()));
        };
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_CHAOS_STORM_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_CHAOS_STORM_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_CHAOS_STORM_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_CHAOS_STORM_DECLINE, who);
    }
}