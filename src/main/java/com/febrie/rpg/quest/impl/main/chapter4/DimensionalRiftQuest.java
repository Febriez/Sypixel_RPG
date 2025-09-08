package com.febrie.rpg.quest.impl.main.chapter4;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 차원의 균열 - 메인 스토리 퀘스트 (Chapter 4 Finale)
 * 거대한 차원 균열을 봉인하는 최종 퀘스트
 *
 * @author Febrie
 */
public class DimensionalRiftQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DimensionalRiftQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_DIMENSIONAL_RIFT)
                .objectives(List.of(
                        // 균열 발견
                        new InteractNPCObjective("rift_detector", "dimensional_scientist", 1),
                        new VisitLocationObjective("rift_location", "dimensional_rift_site"),
                        new CollectItemObjective("rift_readings", Material.WRITTEN_BOOK, 5),
                        new KillMobObjective("rift_creatures", EntityType.ENDERMAN, 100),
                        
                        // 균열 분석
                        new CollectItemObjective("analysis_equipment", Material.SPYGLASS, 3),
                        new CollectItemObjective("rift_samples", Material.CRYING_OBSIDIAN, 50),
                        new InteractNPCObjective("rift_analyst", "void_researcher", 1),
                        new SurviveObjective("rift_exposure", 600), // 10분 균열 노출
                        
                        // 봉인 준비
                        new CollectItemObjective("sealing_crystals", Material.END_CRYSTAL, 20),
                        new CollectItemObjective("dimensional_anchors", Material.LODESTONE, 10),
                        new CollectItemObjective("void_essence", Material.ENDER_EYE, 50),
                        new CollectItemObjective("stability_matrix", Material.BEACON, 8),
                        
                        // 수호자 소환
                        new InteractNPCObjective("summon_defenders", "alliance_emissary", 1),
                        new PlaceBlockObjective("place_beacons", Material.BEACON, 5),
                        new SurviveObjective("defender_arrival", 300), // 5분 대기
                        new InteractNPCObjective("coordinate_defense", "defense_commander", 1),
                        
                        // 첫 번째 봉인 시도
                        new VisitLocationObjective("rift_core", "dimensional_rift_core"),
                        new PlaceBlockObjective("place_anchors", Material.LODESTONE, 10),
                        new KillMobObjective("rift_guardians", EntityType.ELDER_GUARDIAN, 15),
                        new CollectItemObjective("activate_seal_crystals", Material.END_CRYSTAL, 10),
                        new SurviveObjective("first_seal_attempt", 900), // 15분
                        
                        // 균열 확장 대응
                        new KillMobObjective("void_titans", EntityType.RAVAGER, 20),
                        new KillMobObjective("chaos_entities", EntityType.WITHER, 10),
                        new CollectItemObjective("titan_cores", Material.NETHER_STAR, 10),
                        new DeliverItemObjective("reinforce_seal", "dimensional_scientist", Material.NETHER_STAR, 10),
                        
                        // 공허 군주의 반격
                        new InteractNPCObjective("void_overlord", "void_overlord", 1),
                        new KillMobObjective("overlord_army", EntityType.ENDERMAN, 200),
                        new KillMobObjective("void_overlord_battle", EntityType.ENDER_DRAGON, 5),
                        new CollectItemObjective("overlord_crown", Material.DRAGON_HEAD, 1),
                        
                        // 최종 봉인
                        new VisitLocationObjective("final_seal_location", "rift_seal_altar"),
                        new PlaceBlockObjective("complete_seal_array", Material.BEDROCK, 50),
                        new CollectItemObjective("master_seal", Material.NETHER_STAR, 20),
                        new PlaceBlockObjective("activate_master_seal", Material.BEACON, 1),
                        new SurviveObjective("final_sealing", 1200), // 20분 최종 봉인
                        
                        // 차원 안정화
                        new InteractNPCObjective("scientist_victory", "dimensional_scientist", 1),
                        new CollectItemObjective("rift_controller", Material.END_ROD, 1),
                        new CollectItemObjective("dimensional_key", Material.NETHER_STAR, 1),
                        new CollectItemObjective("realm_protector_title", Material.WRITTEN_BOOK, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 75000)
                        .addCurrency(CurrencyType.DIAMOND, 750)
                        .addItem(new ItemStack(Material.NETHERITE_INGOT, 20))
                        .addItem(new ItemStack(Material.DRAGON_EGG, 2))
                        .addItem(new ItemStack(Material.NETHER_STAR, 10))
                        .addItem(new ItemStack(Material.BEACON, 5))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 10))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 20))
                        .addExperience(40000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_CHAOS_STORM)
                .minLevel(90)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "rift_detector" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_RIFT_DETECTOR, who);
            case "rift_location" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_RIFT_LOCATION, who);
            case "rift_readings" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_RIFT_READINGS, who);
            case "rift_creatures" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_RIFT_CREATURES, who);
            case "analysis_equipment" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_ANALYSIS_EQUIPMENT, who);
            case "rift_samples" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_RIFT_SAMPLES, who);
            case "rift_analyst" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_RIFT_ANALYST, who);
            case "rift_exposure" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_RIFT_EXPOSURE, who);
            case "sealing_crystals" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_SEALING_CRYSTALS, who);
            case "dimensional_anchors" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_DIMENSIONAL_ANCHORS, who);
            case "void_essence" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_VOID_ESSENCE, who);
            case "stability_matrix" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_STABILITY_MATRIX, who);
            case "summon_defenders" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_SUMMON_DEFENDERS, who);
            case "place_beacons" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_PLACE_BEACONS, who);
            case "defender_arrival" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_DEFENDER_ARRIVAL, who);
            case "coordinate_defense" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_COORDINATE_DEFENSE, who);
            case "rift_core" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_RIFT_CORE, who);
            case "place_anchors" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_PLACE_ANCHORS, who);
            case "rift_guardians" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_RIFT_GUARDIANS, who);
            case "activate_seal_crystals" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_ACTIVATE_SEAL_CRYSTALS, who);
            case "first_seal_attempt" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_FIRST_SEAL_ATTEMPT, who);
            case "void_titans" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_VOID_TITANS, who);
            case "chaos_entities" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_CHAOS_ENTITIES, who);
            case "titan_cores" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_TITAN_CORES, who);
            case "reinforce_seal" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_REINFORCE_SEAL, who);
            case "void_overlord" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_VOID_OVERLORD, who);
            case "overlord_army" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_OVERLORD_ARMY, who);
            case "void_overlord_battle" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_VOID_OVERLORD_BATTLE, who);
            case "overlord_crown" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_OVERLORD_CROWN, who);
            case "final_seal_location" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_FINAL_SEAL_LOCATION, who);
            case "complete_seal_array" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_COMPLETE_SEAL_ARRAY, who);
            case "master_seal" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_MASTER_SEAL, who);
            case "activate_master_seal" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_ACTIVATE_MASTER_SEAL, who);
            case "final_sealing" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_FINAL_SEALING, who);
            case "scientist_victory" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_SCIENTIST_VICTORY, who);
            case "rift_controller" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_RIFT_CONTROLLER, who);
            case "dimensional_key" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_DIMENSIONAL_KEY, who);
            case "realm_protector_title" -> LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_OBJECTIVES_REALM_PROTECTOR_TITLE, who);
            default -> List.of(Component.text("Objective: " + objective.getId()));
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_DECLINE, who);
    }
}