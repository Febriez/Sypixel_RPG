package com.febrie.rpg.quest.impl.weekly;

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
 * 주간 월드 보스 - 주간 퀘스트
 * 서버 전체가 협력하여 거대한 월드 보스를 처치하는 대규모 이벤트
 *
 * @author Febrie
 */
public class WeeklyWorldBossQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class WeeklyWorldBossBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new WeeklyWorldBossQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public WeeklyWorldBossQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private WeeklyWorldBossQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new WeeklyWorldBossBuilder()
                .id(QuestID.WEEKLY_WORLD_BOSS)
                .objectives(Arrays.asList(
                        // 월드 보스 출현 준비
                        new InteractNPCObjective("world_herald", "world_boss_herald"), // 월드 전령
                        new VisitLocationObjective("boss_spawn", "titan_summoning_grounds"),
                        
                        // 소환 의식 준비
                        new CollectItemObjective("summoning_crystals", Material.END_CRYSTAL, 8),
                        new CollectItemObjective("titan_essence", Material.ECHO_SHARD, 20),
                        new CollectItemObjective("ancient_runes", Material.ENCHANTED_BOOK, 5),
                        new PayCurrencyObjective("ritual_cost", CurrencyType.GOLD, 10000),
                        
                        // 제단 활성화
                        new PlaceBlockObjective("place_crystals", Material.END_CRYSTAL, 8),
                        new PlaceBlockObjective("place_beacons", Material.BEACON, 4),
                        new InteractNPCObjective("start_ritual", "world_boss_herald"),
                        
                        // 첫 번째 단계 - 타이탄의 하수인들
                        new KillMobObjective("titan_minions", EntityType.GIANT, 10),
                        new KillMobObjective("elemental_guards", EntityType.BLAZE, 50),
                        new KillMobObjective("shadow_priests", EntityType.EVOKER, 20),
                        new SurviveObjective("first_wave", 600), // 10분간 생존
                        
                        // 두 번째 단계 - 타이탄의 장군들
                        new KillMobObjective("fire_general", EntityType.MAGMA_CUBE, 5),
                        new KillMobObjective("ice_general", EntityType.STRAY, 30),
                        new KillMobObjective("earth_general", EntityType.IRON_GOLEM, 10),
                        new KillMobObjective("wind_general", EntityType.PHANTOM, 40),
                        new CollectItemObjective("general_cores", Material.NETHER_STAR, 4),
                        
                        // 세 번째 단계 - 타이탄 각성
                        new VisitLocationObjective("titan_arena", "world_boss_arena"),
                        new PlaceBlockObjective("activate_cores", Material.NETHER_STAR, 4),
                        new SurviveObjective("titan_roar", 300), // 5분간 타이탄의 포효 견디기
                        
                        // 최종 전투 - 세계의 타이탄
                        new KillMobObjective("world_titan_phase1", EntityType.WITHER, 1),
                        new CollectItemObjective("titan_heart", Material.BEACON, 1),
                        new KillMobObjective("world_titan_phase2", EntityType.ENDER_DRAGON, 1),
                        new CollectItemObjective("titan_soul", Material.DRAGON_EGG, 1),
                        new KillMobObjective("world_titan_final", EntityType.WARDEN, 3),
                        
                        // 전리품 수집
                        new CollectItemObjective("titan_scales", Material.NETHERITE_SCRAP, 10),
                        new CollectItemObjective("titan_blood", Material.REDSTONE_BLOCK, 20),
                        new CollectItemObjective("titan_bones", Material.BONE_BLOCK, 30),
                        
                        // 보상 수령
                        new DeliverItemObjective("deliver_heart", "world_herald", Material.BEACON, 1),
                        new DeliverItemObjective("deliver_soul", "world_herald", Material.DRAGON_EGG, 1),
                        new InteractNPCObjective("claim_rewards", "world_boss_herald")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 30000)
                        .addCurrency(CurrencyType.DIAMOND, 200)
                        .addItem(new ItemStack(Material.NETHERITE_CHESTPLATE))
                        .addItem(new ItemStack(Material.ELYTRA))
                        .addItem(new ItemStack(Material.NETHER_STAR, 5))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 10))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 3))
                        .addExperience(15000)
                        .build())
                .sequential(true)
                .repeatable(true)
                .weekly(true)      // 주간 퀘스트
                .category(QuestCategory.WEEKLY)
                .minLevel(45)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "세계의 타이탄 토벌" : "World Titan Subjugation";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "서버 전체가 힘을 합쳐 고대의 타이탄을 물리치세요!",
                    "이번 주의 타이탄: 혼돈의 군주 카오스",
                    "",
                    "🌍 월드 보스 특징:",
                    "• 서버 전체 공동 목표",
                    "• 3단계 전투 시스템",
                    "• 다중 형태 변신",
                    "• 막대한 체력 (1,000,000 HP)",
                    "• 특수 공격 패턴",
                    "",
                    "전투 단계:",
                    "• 1단계: 타이탄의 군단",
                    "• 2단계: 4원소 장군",
                    "• 3단계: 타이탄 본체",
                    "",
                    "권장 사항:",
                    "• 최소 20명 이상의 공격대",
                    "• 네더라이트 장비 필수",
                    "• 회복 물약 다량 준비",
                    "• 역할 분담 (탱커/딜러/힐러)",
                    "",
                    "주요 목표:",
                    "• 소환 의식 준비",
                    "• 타이탄의 하수인 처치",
                    "• 4원소 장군 격파",
                    "• 타이탄 3단계 형태 모두 처치",
                    "• 전설적인 전리품 획득",
                    "",
                    "보상:",
                    "• 골드 30,000",
                    "• 다이아몬드 200개",
                    "• 네더라이트 흉갑",
                    "• 겉날개",
                    "• 네더의 별 5개",
                    "• 마법이 부여된 책 10개",
                    "• 불사의 토템 3개",
                    "• 경험치 15,000"
            );
        } else {
            return Arrays.asList(
                    "The entire server must unite to defeat the ancient Titan!",
                    "This week's Titan: Chaos, Lord of Discord",
                    "",
                    "🌍 World Boss Features:",
                    "• Server-wide cooperative goal",
                    "• 3-phase combat system",
                    "• Multiple form transformations",
                    "• Massive health pool (1,000,000 HP)",
                    "• Special attack patterns",
                    "",
                    "Combat Phases:",
                    "• Phase 1: Titan's Legion",
                    "• Phase 2: Four Elemental Generals",
                    "• Phase 3: Titan Core",
                    "",
                    "Recommendations:",
                    "• Minimum 20+ player raid",
                    "• Netherite equipment required",
                    "• Plenty of healing potions",
                    "• Role distribution (Tank/DPS/Healer)",
                    "",
                    "Main Objectives:",
                    "• Prepare summoning ritual",
                    "• Defeat Titan's minions",
                    "• Destroy 4 elemental generals",
                    "• Kill all 3 Titan phases",
                    "• Collect legendary loot",
                    "",
                    "Rewards:",
                    "• 30,000 Gold",
                    "• 200 Diamonds",
                    "• Netherite Chestplate",
                    "• Elytra",
                    "• 5 Nether Stars",
                    "• 10 Enchanted Books",
                    "• 3 Totems of Undying",
                    "• 15,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "world_herald" -> isKorean ? "월드 전령과 대화" : "Talk to World Herald";
            case "boss_spawn" -> isKorean ? "타이탄 소환장 도착" : "Arrive at Titan Summoning Grounds";
            case "summoning_crystals" -> isKorean ? "소환 수정 8개 수집" : "Collect 8 Summoning Crystals";
            case "titan_essence" -> isKorean ? "타이탄 정수 20개 수집" : "Collect 20 Titan Essence";
            case "ancient_runes" -> isKorean ? "고대 룬 5개 수집" : "Collect 5 Ancient Runes";
            case "ritual_cost" -> isKorean ? "의식 비용 10,000골드 지불" : "Pay 10,000 gold ritual cost";
            case "place_crystals" -> isKorean ? "수정 8개 배치" : "Place 8 crystals";
            case "place_beacons" -> isKorean ? "신호기 4개 설치" : "Place 4 beacons";
            case "start_ritual" -> isKorean ? "소환 의식 시작" : "Start summoning ritual";
            case "titan_minions" -> isKorean ? "타이탄 하수인(거인) 10마리 처치" : "Kill 10 Titan Minions (Giants)";
            case "elemental_guards" -> isKorean ? "원소 수호자 50마리 처치" : "Kill 50 Elemental Guards";
            case "shadow_priests" -> isKorean ? "그림자 사제 20명 처치" : "Kill 20 Shadow Priests";
            case "first_wave" -> isKorean ? "첫 번째 공격파 10분간 생존" : "Survive first wave for 10 minutes";
            case "fire_general" -> isKorean ? "화염 장군 5마리 처치" : "Kill 5 Fire Generals";
            case "ice_general" -> isKorean ? "얼음 장군 30마리 처치" : "Kill 30 Ice Generals";
            case "earth_general" -> isKorean ? "대지 장군 10마리 처치" : "Kill 10 Earth Generals";
            case "wind_general" -> isKorean ? "바람 장군 40마리 처치" : "Kill 40 Wind Generals";
            case "general_cores" -> isKorean ? "장군의 핵 4개 수집" : "Collect 4 General Cores";
            case "titan_arena" -> isKorean ? "타이탄 결투장 진입" : "Enter Titan Arena";
            case "activate_cores" -> isKorean ? "핵 4개 활성화" : "Activate 4 cores";
            case "titan_roar" -> isKorean ? "타이탄의 포효 5분간 견디기" : "Endure Titan's Roar for 5 minutes";
            case "world_titan_phase1" -> isKorean ? "월드 타이탄 1단계 처치" : "Defeat World Titan Phase 1";
            case "titan_heart" -> isKorean ? "타이탄의 심장 획득" : "Obtain Titan's Heart";
            case "world_titan_phase2" -> isKorean ? "월드 타이탄 2단계 처치" : "Defeat World Titan Phase 2";
            case "titan_soul" -> isKorean ? "타이탄의 영혼 획득" : "Obtain Titan's Soul";
            case "world_titan_final" -> isKorean ? "월드 타이탄 최종형태 처치" : "Defeat World Titan Final Form";
            case "titan_scales" -> isKorean ? "타이탄의 비늘 10개 수집" : "Collect 10 Titan Scales";
            case "titan_blood" -> isKorean ? "타이탄의 피 20개 수집" : "Collect 20 Titan Blood";
            case "titan_bones" -> isKorean ? "타이탄의 뼈 30개 수집" : "Collect 30 Titan Bones";
            case "deliver_heart" -> isKorean ? "타이탄의 심장 제출" : "Deliver Titan's Heart";
            case "deliver_soul" -> isKorean ? "타이탄의 영혼 제출" : "Deliver Titan's Soul";
            case "claim_rewards" -> isKorean ? "보상 수령" : "Claim rewards";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("weekly_world_boss_dialog");

        // 시작
        dialog.addLine("월드 전령",
                "들으라! 고대의 타이탄이 깨어나고 있다! 모든 용사들이여, 집결하라!",
                "Hear me! The ancient Titan is awakening! All warriors, assemble!");

        dialog.addLine("월드 전령",
                "이번 주는 혼돈의 군주 카오스가 나타날 차례다. 서버 전체가 힘을 합쳐야 한다.",
                "This week, Chaos the Lord of Discord will appear. The entire server must unite.");

        dialog.addLine("플레이어",
                "어떻게 준비해야 하나요?",
                "How should we prepare?");

        dialog.addLine("월드 전령",
                "먼저 소환 의식을 준비해야 한다. 수정과 정수, 그리고 많은 용사들이 필요하지.",
                "First, prepare the summoning ritual. We need crystals, essence, and many warriors.");

        // 소환 의식
        dialog.addLine("월드 전령",
                "의식이 시작되면 타이탄의 군단이 먼저 나타날 것이다. 준비하라!",
                "When the ritual begins, the Titan's legion will appear first. Be ready!");

        // 전투 중
        dialog.addLine("월드 전령",
                "놀랍군! 첫 번째 파동을 막아냈다! 하지만 이제 시작일 뿐이다.",
                "Amazing! You've repelled the first wave! But this is just the beginning.");

        dialog.addLine("월드 전령",
                "4원소 장군들이 나타났다! 화염, 얼음, 대지, 바람의 힘을 조심하라!",
                "The Four Elemental Generals have appeared! Beware the powers of fire, ice, earth, and wind!");

        // 타이탄 등장
        dialog.addLine("월드 전령",
                "드디어... 타이탄이 깨어났다! 모든 화력을 집중하라!",
                "Finally... the Titan has awakened! Focus all firepower!");

        dialog.addLine("월드 전령",
                "타이탄이 형태를 바꾸고 있다! 각 단계마다 다른 전략이 필요하다!",
                "The Titan is changing forms! Each phase requires different strategies!");

        // 승리
        dialog.addLine("월드 전령",
                "믿을 수 없다... 정말로 타이탄을 쓰러뜨렸군! 서버의 영웅들이여!",
                "Unbelievable... you really defeated the Titan! Heroes of the server!");

        dialog.addLine("월드 전령",
                "이 보상은 당신들의 용기에 대한 대가다. 다음 주에는 더 강력한 타이탄이 올 것이다.",
                "These rewards are payment for your courage. Next week, an even stronger Titan will come.");

        return dialog;
    }
}