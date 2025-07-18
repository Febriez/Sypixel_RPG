package com.febrie.rpg.quest.impl.special;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
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
    private static class DimensionTravelerBuilder extends Quest.Builder {
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
    private DimensionTravelerQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new DimensionTravelerBuilder()
                .id(QuestID.SPECIAL_DIMENSION_TRAVELER)
                .objectives(Arrays.asList(
                        // 차원 여행의 시작
                        new InteractNPCObjective("dimension_sage", 260), // 차원 현자
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
                        new InteractNPCObjective("seal_complete", 260)
                ))
                .reward(BasicReward.builder()
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
                .category(QuestCategory.NORMAL)
                .minLevel(65)
                .maxLevel(0);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "차원의 수호자" : "Guardian of Dimensions";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "멀티버스의 균열을 봉인하고 차원의 균형을 되찾으세요!",
                    "6개의 불안정한 차원을 여행하며 각 차원의 균열을 봉인하세요.",
                    "",
                    "🌌 차원 목록:",
                    "• 거울 세계 - 모든 것이 반대인 차원",
                    "• 무중력 공간 - 중력이 없는 공허",
                    "• 시간 정지 영역 - 시간이 멈춘 세계",
                    "• 원소 혼돈 - 4원소가 충돌하는 곳",
                    "• 꿈과 악몽 - 현실과 환상의 경계",
                    "• 퓨어 보이드 - 절대 무의 공간",
                    "",
                    "각 차원의 특징:",
                    "• 독특한 물리 법칙",
                    "• 차원별 특수 몬스터",
                    "• 균열 봉인 필요",
                    "• 특별한 생존 도전",
                    "",
                    "주요 도전:",
                    "• 거울 복사본 50마리 처치",
                    "• 10분간 무중력 생존",
                    "• 15분간 시간 정지 견디기",
                    "• 4원소 정령 처치",
                    "• 100마리 악몽 퇴치",
                    "• 엔더 드래곤 처치",
                    "• 40분간 차원 융합 생존",
                    "",
                    "필요 자원:",
                    "• 다이아몬드 100개 (원소 균형)",
                    "• 골드 100,000 (안정화 비용)",
                    "• 각종 차원 재료",
                    "",
                    "경고:",
                    "• 극한의 난이도",
                    "• 레벨 65 이상 필수",
                    "• 차원 여행 경험 필요",
                    "• 실패 시 차원 붕괴 위험",
                    "",
                    "차원적 보상:",
                    "• 골드 150,000",
                    "• 다이아몬드 1,500개",
                    "• 차원 포탈 프레임 12개",
                    "• 엔더 상자 5개",
                    "• 셜커 상자 10개",
                    "• 코러스 과일 64개",
                    "• 차원 날개 2개",
                    "• 차원 핵심 6개",
                    "• 경험치 75,000"
            );
        } else {
            return Arrays.asList(
                    "Seal the rifts in the multiverse and restore dimensional balance!",
                    "Travel through 6 unstable dimensions and seal each dimensional rift.",
                    "",
                    "🌌 Dimension List:",
                    "• Mirror World - Dimension where everything is reversed",
                    "• Zero Gravity - Void without gravity",
                    "• Frozen Time - World where time stopped",
                    "• Elemental Chaos - Where 4 elements collide",
                    "• Dreams and Nightmares - Border of reality and fantasy",
                    "• Pure Void - Space of absolute nothingness",
                    "",
                    "Each Dimension Features:",
                    "• Unique physics laws",
                    "• Dimension-specific monsters",
                    "• Rifts need sealing",
                    "• Special survival challenges",
                    "",
                    "Major Challenges:",
                    "• Defeat 50 mirror copies",
                    "• Survive 10 minutes in zero gravity",
                    "• Endure 15 minutes of time freeze",
                    "• Defeat 4 elemental spirits",
                    "• Banish 100 nightmares",
                    "• Defeat Ender Dragon",
                    "• Survive 40 minutes dimension merge",
                    "",
                    "Required Resources:",
                    "• 100 Diamonds (elemental balance)",
                    "• 100,000 Gold (stabilization cost)",
                    "• Various dimensional materials",
                    "",
                    "Warning:",
                    "• Extreme difficulty",
                    "• Level 65+ required",
                    "• Dimension travel experience needed",
                    "• Risk of dimensional collapse on failure",
                    "",
                    "Dimensional Rewards:",
                    "• 150,000 Gold",
                    "• 1,500 Diamonds",
                    "• 12 Dimension Portal Frames",
                    "• 5 Ender Chests",
                    "• 10 Shulker Boxes",
                    "• 64 Chorus Fruit",
                    "• 2 Dimensional Wings",
                    "• 6 Dimensional Cores",
                    "• 75,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "dimension_sage" -> isKorean ? "차원 현자와 대화" : "Talk to Dimension Sage";
            case "dimensional_key" -> isKorean ? "차원 열쇠 획득" : "Obtain dimensional key";
            case "void_map" -> isKorean ? "공허 지도 획득" : "Obtain void map";
            case "mirror_portal" -> isKorean ? "거울 차원 포탈 진입" : "Enter mirror dimension portal";
            case "activate_mirror" -> isKorean ? "거울 활성화 (유리 20개)" : "Activate mirror (20 glass)";
            case "mirror_copies" -> isKorean ? "거울 복사본 50마리 처치" : "Defeat 50 mirror copies";
            case "reflection_boss" -> isKorean ? "반사 보스 5마리 처치" : "Defeat 5 reflection bosses";
            case "mirror_shards" -> isKorean ? "거울 조각 100개 수집" : "Collect 100 mirror shards";
            case "shatter_illusion" -> isKorean ? "환상 파괴 (유리 50개)" : "Shatter illusion (50 glass)";
            case "reality_fragment" -> isKorean ? "현실 조각 30개 수집" : "Collect 30 reality fragments";
            case "seal_rift_1" -> isKorean ? "첫 번째 균열 봉인" : "Seal first rift";
            case "zero_gravity" -> isKorean ? "무중력 차원 진입" : "Enter zero gravity dimension";
            case "gravity_dust" -> isKorean ? "중력 가루 50개 수집" : "Collect 50 gravity dust";
            case "void_creatures" -> isKorean ? "공허 생물 40마리 처치" : "Defeat 40 void creatures";
            case "space_phantoms" -> isKorean ? "우주 팬텀 60마리 처치" : "Defeat 60 space phantoms";
            case "float_survival" -> isKorean ? "10분간 무중력 생존" : "Survive 10 minutes in zero gravity";
            case "void_crystals" -> isKorean ? "공허 수정 5개 수집" : "Collect 5 void crystals";
            case "gravity_anchors" -> isKorean ? "중력 닻 10개 설치" : "Place 10 gravity anchors";
            case "seal_rift_2" -> isKorean ? "두 번째 균열 봉인" : "Seal second rift";
            case "frozen_time" -> isKorean ? "시간 정지 차원 진입" : "Enter frozen time dimension";
            case "frozen_moments" -> isKorean ? "얼어붙은 순간 50개 수집" : "Collect 50 frozen moments";
            case "break_time_ice" -> isKorean ? "시간 얼음 30개 파괴" : "Break 30 time ice";
            case "time_freeze" -> isKorean ? "15분간 시간 정지 견디기" : "Endure 15 minutes time freeze";
            case "temporal_ice" -> isKorean ? "시간 얼음 20개 수집" : "Collect 20 temporal ice";
            case "time_guardians" -> isKorean ? "시간 수호자 50마리 처치" : "Defeat 50 time guardians";
            case "time_device" -> isKorean ? "시간 장치 10개 제작" : "Craft 10 time devices";
            case "seal_rift_3" -> isKorean ? "세 번째 균열 봉인" : "Seal third rift";
            case "elemental_chaos" -> isKorean ? "원소 혼돈 차원 진입" : "Enter elemental chaos dimension";
            case "fire_elementals" -> isKorean ? "화염 정령 30마리 처치" : "Defeat 30 fire elementals";
            case "water_elementals" -> isKorean ? "물 정령 30마리 처치" : "Defeat 30 water elementals";
            case "earth_elementals" -> isKorean ? "대지 정령 10마리 처치" : "Defeat 10 earth elementals";
            case "air_elementals" -> isKorean ? "바람 정령 50마리 처치" : "Defeat 50 air elementals";
            case "elemental_cores" -> isKorean ? "원소 핵심 20개 수집" : "Collect 20 elemental cores";
            case "balance_cost" -> isKorean ? "균형 비용 (다이아몬드 100개)" : "Balance cost (100 diamonds)";
            case "elemental_altar" -> isKorean ? "원소 제단 설치" : "Place elemental altar";
            case "seal_rift_4" -> isKorean ? "네 번째 균열 봉인" : "Seal fourth rift";
            case "dream_realm" -> isKorean ? "꿈의 영역 진입" : "Enter dream realm";
            case "nightmares" -> isKorean ? "악몽 100마리 처치" : "Defeat 100 nightmares";
            case "dream_essence" -> isKorean ? "꿈의 정수 20개 수집" : "Collect 20 dream essence";
            case "dream_catchers" -> isKorean ? "드림캐처 30개 설치" : "Place 30 dream catchers";
            case "lucid_nightmare" -> isKorean ? "10분간 자각몽 악몽" : "10 minutes lucid nightmare";
            case "nightmare_fuel" -> isKorean ? "악몽 연료 5개 수집" : "Collect 5 nightmare fuel";
            case "dream_eater" -> isKorean ? "꿈 포식자 처치" : "Defeat dream eater";
            case "seal_rift_5" -> isKorean ? "다섯 번째 균열 봉인" : "Seal fifth rift";
            case "pure_void" -> isKorean ? "순수 공허 진입" : "Enter pure void";
            case "void_essence" -> isKorean ? "공허 정수 16개 수집" : "Collect 16 void essence";
            case "void_spawn" -> isKorean ? "공허 스폰 200마리 처치" : "Defeat 200 void spawn";
            case "void_lord" -> isKorean ? "공허의 군주 처치" : "Defeat void lord";
            case "void_exposure" -> isKorean ? "20분간 공허 노출" : "20 minutes void exposure";
            case "nothingness" -> isKorean ? "무(無) 획득" : "Obtain nothingness";
            case "seal_rift_6" -> isKorean ? "여섯 번째 균열 봉인" : "Seal sixth rift";
            case "nexus_point" -> isKorean ? "차원 연결점 도달" : "Reach dimensional nexus";
            case "place_mirror" -> isKorean ? "거울 정수 배치" : "Place mirror essence";
            case "place_void" -> isKorean ? "공허 수정 배치" : "Place void crystals";
            case "place_time" -> isKorean ? "시간 얼음 배치" : "Place time ice";
            case "place_chaos" -> isKorean ? "혼돈 핵심 배치" : "Place chaos cores";
            case "place_dream" -> isKorean ? "꿈의 정수 배치" : "Place dream essence";
            case "place_pure" -> isKorean ? "순수 정수 배치" : "Place pure essence";
            case "stabilize_cost" -> isKorean ? "안정화 비용 100,000골드" : "Stabilization cost 100,000 gold";
            case "dimension_merge" -> isKorean ? "40분간 차원 융합" : "40 minutes dimension merge";
            case "dimensional_core" -> isKorean ? "차원 핵심 6개 획득" : "Obtain 6 dimensional cores";
            case "seal_complete" -> isKorean ? "봉인 완료 보고" : "Report seal completion";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("dimension_traveler_dialog");

        // 시작
        dialog.addLine("차원 현자",
                "멀티버스에 큰 위기가 닥쳤습니다. 차원 간 균열이 생기고 있어요.",
                "A great crisis has come to the multiverse. Rifts between dimensions are forming.");

        dialog.addLine("차원 현자",
                "당신만이 이 균열들을 봉인할 수 있습니다. 6개 차원을 여행해야 합니다.",
                "Only you can seal these rifts. You must travel through 6 dimensions.");

        dialog.addLine("플레이어",
                "어떤 차원들인가요?",
                "What dimensions?");

        dialog.addLine("차원 현자",
                "거울, 무중력, 시간 정지, 원소 혼돈, 꿈과 악몽, 그리고... 순수한 공허.",
                "Mirror, zero gravity, frozen time, elemental chaos, dreams and nightmares, and... pure void.");

        // 거울 세계
        dialog.addLine("차원 현자",
                "거울 세계에서는 모든 것이 반대입니다. 자신과 싸워야 할 수도 있어요.",
                "In the mirror world, everything is reversed. You might have to fight yourself.");

        // 무중력 공간
        dialog.addLine("차원 현자",
                "무중력 공간은 방향 감각을 잃기 쉽습니다. 중력 닻을 설치하세요.",
                "Zero gravity space makes you lose direction easily. Install gravity anchors.");

        // 시간 정지
        dialog.addLine("차원 현자",
                "시간이 멈춘 곳에서는 당신만 움직일 수 있습니다. 하지만 오래 있으면 위험해요.",
                "In frozen time, only you can move. But staying too long is dangerous.");

        // 원소 혼돈
        dialog.addLine("차원 현자",
                "네 원소가 충돌하는 곳입니다. 균형을 맞춰야 균열을 봉인할 수 있어요.",
                "Where four elements collide. You must balance them to seal the rift.");

        // 꿈과 악몽
        dialog.addLine("차원 현자",
                "꿈의 영역은 현실과 환상이 뒤섞입니다. 정신을 똑바로 차리세요.",
                "The dream realm mixes reality and fantasy. Keep your mind clear.");

        // 순수 공허
        dialog.addLine("차원 현자",
                "순수한 공허는... 아무것도 없습니다. 그 무(無)를 견뎌야 합니다.",
                "Pure void is... nothing. You must endure that nothingness.");

        // 차원 융합
        dialog.addLine("차원 현자",
                "모든 균열을 봉인했군요! 이제 차원들을 안정시켜야 합니다.",
                "You've sealed all rifts! Now we must stabilize the dimensions.");

        dialog.addLine("차원 현자",
                "40분간 차원 융합을 견뎌내세요. 실패하면 모든 차원이 붕괴합니다.",
                "Endure 40 minutes of dimension merge. Failure means all dimensions collapse.");

        // 완료
        dialog.addLine("차원 현자",
                "해냈습니다! 멀티버스가 안정되었어요! 당신은 진정한 차원의 수호자입니다!",
                "You did it! The multiverse is stable! You are the true Guardian of Dimensions!");

        dialog.addLine("차원 현자",
                "이 차원 포탈들을 가져가세요. 이제 당신은 모든 차원을 자유롭게 여행할 수 있습니다.",
                "Take these dimension portals. Now you can travel freely through all dimensions.");

        return dialog;
    }
}