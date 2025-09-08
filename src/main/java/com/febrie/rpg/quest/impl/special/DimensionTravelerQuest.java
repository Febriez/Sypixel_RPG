package com.febrie.rpg.quest.impl.special;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
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

import java.util.ArrayList;
import java.util.List;

/**
 * 차원 여행자 - 특별 퀘스트
 * 다양한 차원을 여행하며 차원간 이동 능력을 습득하는 퀘스트
 *
 * @author Febrie
 */
public class DimensionTravelerQuest extends Quest {

    public DimensionTravelerQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SPECIAL_DIMENSION_TRAVELER)
                .objectives(List.of(
                        // 차원 학자와 만남
                        new InteractNPCObjective("dimension_scholar", "void_researcher", 1),
                        new CollectItemObjective("dimension_theory", Material.WRITTEN_BOOK, 3),
                        new VisitLocationObjective("dimension_lab", "interdimensional_laboratory"),
                        
                        // 네더 차원 탐험
                        new VisitLocationObjective("nether_dimension", "nether_realm"),
                        new CollectItemObjective("nether_essence", Material.BLAZE_POWDER, 20),
                        new KillMobObjective("nether_creatures", EntityType.GHAST, 5),
                        new CollectItemObjective("soul_fragments", Material.SOUL_SAND, 32),
                        
                        // 엔드 차원 정복
                        new VisitLocationObjective("end_dimension", "end_realm"),
                        new KillMobObjective("ender_dragon", EntityType.ENDER_DRAGON, 1),
                        new CollectItemObjective("dragon_breath", Material.DRAGON_BREATH, 5),
                        new CollectItemObjective("end_crystals", Material.END_CRYSTAL, 4),
                        
                        // 차원 포털 제작
                        new CraftItemObjective("dimension_portal", Material.END_PORTAL_FRAME, 12),
                        new CollectItemObjective("ender_pearls", Material.ENDER_PEARL, 64),
                        new PlaceBlockObjective("portal_frame", Material.END_PORTAL_FRAME, 12),
                        new InteractNPCObjective("activate_portal", "dimension_portal", 1),
                        
                        // 미지의 차원 탐험
                        new VisitLocationObjective("void_dimension", "void_realm"),
                        new SurviveObjective("void_survival", 600), // 10분간 생존
                        new CollectItemObjective("void_crystals", Material.AMETHYST_SHARD, 30),
                        new KillMobObjective("void_entities", EntityType.VEX, 20),
                        
                        // 시간 차원 여행
                        new VisitLocationObjective("time_dimension", "temporal_realm"),
                        new CollectItemObjective("time_fragments", Material.CLOCK, 8),
                        new InteractNPCObjective("fix_timeline", "temporal_anomaly", 1),
                        new CollectItemObjective("temporal_essence", Material.REDSTONE, 64),
                        
                        // 차원 융합 실험
                        new CraftItemObjective("dimension_catalyst", Material.NETHER_STAR, 3),
                        new InteractNPCObjective("fusion_experiment", "dimension_merge", 1),
                        new InteractNPCObjective("stabilize_rift", "dimensional_rift", 1),
                        
                        // 차원 지배자와 대결
                        new VisitLocationObjective("dimension_throne", "interdimensional_nexus"),
                        new KillMobObjective("dimension_lord", EntityType.WITHER, 1),
                        new CollectItemObjective("dimension_crown", Material.NETHERITE_HELMET, 1),
                        
                        // 차원 여행 기술 습득
                        new InteractNPCObjective("dimension_travel", "interdimensional_teleport", 1),
                        new InteractNPCObjective("master_graduation", "void_researcher", 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 5000)
                        .addCurrency(CurrencyType.DIAMOND, 75)
                        .addItem(new ItemStack(Material.ELYTRA, 1)) // 차원 날개
                        .addItem(new ItemStack(Material.ENDER_PEARL, 64)) // 순간이동 구슬
                        .addItem(new ItemStack(Material.END_CRYSTAL, 10))
                        .addItem(new ItemStack(Material.NETHER_STAR, 5))
                        .addExperience(8000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SPECIAL)
                .minLevel(45)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "dimension_scholar" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_DIMENSION_SCHOLAR, who);
            case "dimension_theory" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_DIMENSION_THEORY, who);
            case "dimension_lab" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_DIMENSION_LAB, who);
            case "nether_dimension" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_NETHER_DIMENSION, who);
            case "nether_essence" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_NETHER_ESSENCE, who);
            case "nether_creatures" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_NETHER_CREATURES, who);
            case "soul_fragments" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_SOUL_FRAGMENTS, who);
            case "end_dimension" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_END_DIMENSION, who);
            case "ender_dragon" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_ENDER_DRAGON, who);
            case "dragon_breath" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_DRAGON_BREATH, who);
            case "end_crystals" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_END_CRYSTALS, who);
            case "dimension_portal" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_DIMENSION_PORTAL, who);
            case "ender_pearls" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_ENDER_PEARLS, who);
            case "portal_frame" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_PORTAL_FRAME, who);
            case "activate_portal" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_ACTIVATE_PORTAL, who);
            case "void_dimension" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_VOID_DIMENSION, who);
            case "void_survival" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_VOID_SURVIVAL, who);
            case "void_crystals" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_VOID_CRYSTALS, who);
            case "void_entities" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_VOID_ENTITIES, who);
            case "time_dimension" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_TIME_DIMENSION, who);
            case "time_fragments" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_TIME_FRAGMENTS, who);
            case "fix_timeline" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_FIX_TIMELINE, who);
            case "temporal_essence" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_TEMPORAL_ESSENCE, who);
            case "dimension_catalyst" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_DIMENSION_CATALYST, who);
            case "fusion_experiment" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_FUSION_EXPERIMENT, who);
            case "stabilize_rift" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_STABILIZE_RIFT, who);
            case "dimension_throne" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_DIMENSION_THRONE, who);
            case "dimension_lord" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_DIMENSION_LORD, who);
            case "dimension_crown" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_DIMENSION_CROWN, who);
            case "dimension_travel" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_DIMENSION_TRAVEL, who);
            case "master_graduation" -> LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_OBJECTIVES_MASTER_GRADUATION, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() {
        return 12;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_DIMENSION_TRAVELER_DECLINE, who);
    }
}