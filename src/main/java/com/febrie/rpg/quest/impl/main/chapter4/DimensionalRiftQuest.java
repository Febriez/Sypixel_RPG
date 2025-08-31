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
import com.febrie.rpg.util.LangHelper;
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
                .objectives(Arrays.asList(
                        // 균열 발견
                        new InteractNPCObjective("rift_detector", "dimensional_scientist"),
                        new VisitLocationObjective("rift_location", "dimensional_rift_site"),
                        new CollectItemObjective("rift_readings", Material.WRITTEN_BOOK, 5),
                        new KillMobObjective("rift_creatures", EntityType.ENDERMAN, 100),
                        
                        // 균열 분석
                        new CollectItemObjective("analysis_equipment", Material.SPYGLASS, 3),
                        new CollectItemObjective("rift_samples", Material.CRYING_OBSIDIAN, 50),
                        new InteractNPCObjective("rift_analyst", "void_researcher"),
                        new SurviveObjective("rift_exposure", 600), // 10분 균열 노출
                        
                        // 봉인 준비
                        new CollectItemObjective("sealing_crystals", Material.END_CRYSTAL, 20),
                        new CollectItemObjective("dimensional_anchors", Material.LODESTONE, 10),
                        new CollectItemObjective("void_essence", Material.ENDER_EYE, 50),
                        new CollectItemObjective("stability_matrix", Material.BEACON, 8),
                        
                        // 수호자 소환
                        new InteractNPCObjective("summon_defenders", "alliance_emissary"),
                        new PlaceBlockObjective("place_beacons", Material.BEACON, 5),
                        new SurviveObjective("defender_arrival", 300), // 5분 대기
                        new InteractNPCObjective("coordinate_defense", "defense_commander"),
                        
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
                        new InteractNPCObjective("void_overlord", "void_overlord"),
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
                        new InteractNPCObjective("scientist_victory", "dimensional_scientist"),
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
        return LangHelper.text(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.main.dimensional_rift.objectives." + objective.getId();
        return LangManager.get(key, who);
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangHelper.text(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_DIALOGS_0, who);
            case 1 -> LangHelper.text(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_DIALOGS_1, who);
            case 2 -> LangHelper.text(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_DIALOGS_2, who);
            case 3 -> LangHelper.text(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_DIALOGS_3, who);
            case 4 -> LangHelper.text(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_DIALOGS_4, who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_DIMENSIONAL_RIFT_DECLINE, who);
    }
}