package com.febrie.rpg.quest.impl.special;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
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
 * 차원 여행자 - 특수 퀘스트
 * 다양한 차원을 여행하며 차원의 균열을 봉인하는 퀘스트
 *
 * @author Febrie
 */
public class DimensionTravelerQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class DimensionTravelerBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new DimensionTravelerQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DimensionTravelerQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private DimensionTravelerQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new DimensionTravelerBuilder()
                .id(QuestID.SPECIAL_DIMENSION_TRAVELER)
                .objectives(Arrays.asList(
                        // 차원 여행의 시작
                        new InteractNPCObjective("dimension_sage", "dimension_sage"), // 차원 현자
                        new CollectItemObjective("dimensional_key", Material.END_CRYSTAL, 1),
                        new CollectItemObjective("void_map", Material.FILLED_MAP, 1),
                        
                        // 첫 번째 차원 - 거울 세계
                        new VisitLocationObjective("mirror_portal", "mirror_dimension_portal"),
                        new PlaceBlockObjective("activate_mirror", Material.GLASS, 20),
                        new KillMobObjective("mirror_copies", EntityType.ZOMBIE, 50),
                        new KillMobObjective("reflection_boss", EntityType.IRON_GOLEM, 5),
                        new CollectItemObjective("mirror_shards", Material.GLASS_PANE, 100),
                        new BreakBlockObjective("shatter_illusion", Material.GLASS, 50),
                        new CollectItemObjective("reality_fragment", Material.QUARTZ, 30),
                        new PlaceBlockObjective("seal_rift_1", Material.OBSIDIAN, 9),
                        
                        // 두 번째 차원 - 무중력 공간
                        new VisitLocationObjective("zero_gravity", "void_dimension_portal"),
                        new CollectItemObjective("gravity_dust", Material.GLOWSTONE_DUST, 50),
                        new KillMobObjective("void_creatures", EntityType.ENDERMAN, 40),
                        new KillMobObjective("space_phantoms", EntityType.PHANTOM, 60),
                        new SurviveObjective("float_survival", 600), // 10분간 무중력 생존
                        new CollectItemObjective("void_crystals", Material.END_CRYSTAL, 5),
                        new PlaceBlockObjective("gravity_anchors", Material.ANVIL, 10),
                        new PlaceBlockObjective("seal_rift_2", Material.CRYING_OBSIDIAN, 9),
                        
                        // 세 번째 차원 - 시간 정지 영역
                        new VisitLocationObjective("frozen_time", "temporal_dimension_portal"),
                        new CollectItemObjective("frozen_moments", Material.PACKED_ICE, 50),
                        new BreakBlockObjective("break_time_ice", Material.BLUE_ICE, 30),
                        new SurviveObjective("time_freeze", 900), // 15분간 시간 정지
                        new CollectItemObjective("temporal_ice", Material.BLUE_ICE, 20),
                        new KillMobObjective("time_guardians", EntityType.STRAY, 50),
                        new CraftItemObjective("time_device", Material.CLOCK, 10),
                        new PlaceBlockObjective("seal_rift_3", Material.PACKED_ICE, 9),
                        
                        // 네 번째 차원 - 원소 혼돈
                        new VisitLocationObjective("elemental_chaos", "chaos_dimension_portal"),
                        new KillMobObjective("fire_elementals", EntityType.BLAZE, 30),
                        new KillMobObjective("water_elementals", EntityType.DROWNED, 30),
                        new KillMobObjective("earth_elementals", EntityType.IRON_GOLEM, 10),
                        new KillMobObjective("air_elementals", EntityType.VEX, 50),
                        new CollectItemObjective("elemental_cores", Material.MAGMA_CREAM, 20),
                        new PayCurrencyObjective("balance_cost", CurrencyType.DIAMOND, 100),
                        new PlaceBlockObjective("elemental_altar", Material.BEACON, 1),
                        new PlaceBlockObjective("seal_rift_4", Material.NETHERITE_BLOCK, 9),
                        
                        // 다섯 번째 차원 - 꿈과 악몽
                        new VisitLocationObjective("dream_realm", "nightmare_dimension_portal"),
                        new KillMobObjective("nightmares", EntityType.PHANTOM, 100),
                        new CollectItemObjective("dream_essence", Material.GHAST_TEAR, 20),
                        new PlaceBlockObjective("dream_catchers", Material.COBWEB, 30),
                        new SurviveObjective("lucid_nightmare", 600), // 10분간 악몽 생존
                        new CollectItemObjective("nightmare_fuel", Material.WITHER_SKELETON_SKULL, 5),
                        new KillMobObjective("dream_eater", EntityType.WITHER, 1),
                        new PlaceBlockObjective("seal_rift_5", Material.SOUL_SAND, 9),
                        
                        // 여섯 번째 차원 - 퓨어 보이드
                        new VisitLocationObjective("pure_void", "absolute_void_portal"),
                        new CollectItemObjective("void_essence", Material.ENDER_EYE, 16),
                        new KillMobObjective("void_spawn", EntityType.ENDERMITE, 200),
                        new KillMobObjective("void_lord", EntityType.ENDER_DRAGON, 1),
                        new SurviveObjective("void_exposure", 1200), // 20분간 공허 노출
                        new CollectItemObjective("nothingness", Material.BARRIER, 1),
                        new PlaceBlockObjective("seal_rift_6", Material.END_PORTAL_FRAME, 9),
                        
                        // 최종 - 차원 융합
                        new VisitLocationObjective("nexus_point", "dimensional_nexus"),
                        new DeliverItemObjective("place_mirror", "nexus_altar", Material.QUARTZ, 30),
                        new DeliverItemObjective("place_void", "nexus_altar", Material.END_CRYSTAL, 5),
                        new DeliverItemObjective("place_time", "nexus_altar", Material.BLUE_ICE, 20),
                        new DeliverItemObjective("place_chaos", "nexus_altar", Material.MAGMA_CREAM, 20),
                        new DeliverItemObjective("place_dream", "nexus_altar", Material.GHAST_TEAR, 20),
                        new DeliverItemObjective("place_pure", "nexus_altar", Material.ENDER_EYE, 16),
                        
                        // 차원 안정화
                        new PayCurrencyObjective("stabilize_cost", CurrencyType.GOLD, 100000),
                        new SurviveObjective("dimension_merge", 2400), // 40분간 차원 융합
                        new CollectItemObjective("dimensional_core", Material.NETHER_STAR, 6),
                        new InteractNPCObjective("seal_complete", "dimension_sage")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 150000)
                        .addCurrency(CurrencyType.DIAMOND, 1500)
                        .addItem(new ItemStack(Material.END_PORTAL_FRAME, 12)) // 차원 포탈 프레임
                        .addItem(new ItemStack(Material.ENDER_CHEST, 5)) // 차원 저장소
                        .addItem(new ItemStack(Material.SHULKER_BOX, 10)) // 차원 상자
                        .addItem(new ItemStack(Material.CHORUS_FRUIT, 64)) // 차원 이동 과일
                        .addItem(new ItemStack(Material.ELYTRA, 2)) // 차원 날개
                        .addItem(new ItemStack(Material.NETHER_STAR, 6)) // 차원 핵심
                        .addExperience(75000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SPECIAL)
                .minLevel(65)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.getMessage(who, "special.dimension_traveler.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getComponentList(who, "special.dimension_traveler.description");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return LangManager.getMessage(who, "special.dimension_traveler.objectives." + id);
    }

    @Override
    public QuestDialog getDialog(@NotNull Player player) {
        QuestDialog dialog = new QuestDialog("dimension_traveler_dialog");
        
        dialog.addLine("quest.special_dimension_traveler.npcs.dimension_sage", "quest.special_dimension_traveler.dialogs.start_line1");
        dialog.addLine("quest.special_dimension_traveler.npcs.dimension_sage", "quest.special_dimension_traveler.dialogs.start_line2");
        dialog.addLine("quest.dialog.player", "quest.special_dimension_traveler.dialogs.start_line3");
        dialog.addLine("quest.special_dimension_traveler.npcs.dimension_sage", "quest.special_dimension_traveler.dialogs.start_line4");
        dialog.addLine("quest.special_dimension_traveler.npcs.dimension_sage", "quest.special_dimension_traveler.dialogs.mirror_world");
        dialog.addLine("quest.special_dimension_traveler.npcs.dimension_sage", "quest.special_dimension_traveler.dialogs.zero_gravity");
        dialog.addLine("quest.special_dimension_traveler.npcs.dimension_sage", "quest.special_dimension_traveler.dialogs.frozen_time");
        dialog.addLine("quest.special_dimension_traveler.npcs.dimension_sage", "quest.special_dimension_traveler.dialogs.elemental_chaos");
        dialog.addLine("quest.special_dimension_traveler.npcs.dimension_sage", "quest.special_dimension_traveler.dialogs.dream_realm");
        dialog.addLine("quest.special_dimension_traveler.npcs.dimension_sage", "quest.special_dimension_traveler.dialogs.pure_void");
        dialog.addLine("quest.special_dimension_traveler.npcs.dimension_sage", "quest.special_dimension_traveler.dialogs.dimension_merge_line1");
        dialog.addLine("quest.special_dimension_traveler.npcs.dimension_sage", "quest.special_dimension_traveler.dialogs.dimension_merge_line2");
        dialog.addLine("quest.special_dimension_traveler.npcs.dimension_sage", "quest.special_dimension_traveler.dialogs.complete_line1");
        dialog.addLine("quest.special_dimension_traveler.npcs.dimension_sage", "quest.special_dimension_traveler.dialogs.complete_line2");

        return dialog;
    }
}