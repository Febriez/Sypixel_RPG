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
 * 고대의 힘 - 특수 히든 퀘스트
 * 잊혀진 고대 신들의 힘을 얻는 비밀 퀘스트
 *
 * @author Febrie
 */
public class AncientPowerQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class AncientPowerBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new AncientPowerQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public AncientPowerQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private AncientPowerQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new AncientPowerBuilder()
                .id(QuestID.SPECIAL_ANCIENT_POWER)
                .objectives(Arrays.asList(
                        // 숨겨진 시작
                        new CollectItemObjective("mysterious_rune", Material.CRYING_OBSIDIAN, 1),
                        new VisitLocationObjective("hidden_shrine", "forgotten_god_shrine"),
                        new InteractNPCObjective("ancient_spirit", 220), // 고대 영혼
                        
                        // 첫 번째 신 - 전쟁의 신
                        new VisitLocationObjective("war_temple", "temple_of_war"),
                        new KillMobObjective("prove_strength", EntityType.IRON_GOLEM, 20),
                        new KillMobObjective("defeat_warriors", EntityType.VINDICATOR, 50),
                        new KillPlayerObjective("pvp_kills", 10), // PvP 승리
                        new CollectItemObjective("warrior_souls", Material.IRON_NUGGET, 100),
                        new SurviveObjective("endless_battle", 1200), // 20분간 전투
                        new InteractNPCObjective("war_god_altar", 221), // 전쟁신 제단
                        new CollectItemObjective("war_blessing", Material.IRON_SWORD, 1),
                        
                        // 두 번째 신 - 지혜의 신
                        new VisitLocationObjective("wisdom_temple", "temple_of_wisdom"),
                        new CollectItemObjective("ancient_books", Material.WRITTEN_BOOK, 20),
                        new BreakBlockObjective("uncover_secrets", Material.BOOKSHELF, 50),
                        new CraftItemObjective("wisdom_elixir", Material.POTION, 30),
                        new PlaceBlockObjective("arrange_puzzle", Material.REDSTONE_LAMP, 16),
                        new SurviveObjective("mental_trial", 600), // 10분간 정신 시험
                        new InteractNPCObjective("wisdom_god_altar", 222), // 지혜신 제단
                        new CollectItemObjective("wisdom_blessing", Material.ENCHANTED_BOOK, 1),
                        
                        // 세 번째 신 - 자연의 신
                        new VisitLocationObjective("nature_temple", "temple_of_nature"),
                        new HarvestObjective("nature_offering", Material.WHEAT, 100),
                        new CollectItemObjective("sacred_seeds", Material.WHEAT_SEEDS, 50),
                        new PlaceBlockObjective("plant_trees", Material.OAK_SAPLING, 20),
                        new KillMobObjective("protect_nature", EntityType.PILLAGER, 30),
                        new CollectItemObjective("nature_essence", Material.EMERALD, 30),
                        new InteractNPCObjective("nature_god_altar", 223), // 자연신 제단
                        new CollectItemObjective("nature_blessing", Material.GOLDEN_APPLE, 1),
                        
                        // 네 번째 신 - 죽음의 신
                        new VisitLocationObjective("death_temple", "temple_of_death"),
                        new KillMobObjective("undead_army", EntityType.ZOMBIE, 100),
                        new KillMobObjective("skeleton_legion", EntityType.SKELETON, 80),
                        new KillMobObjective("death_knights", EntityType.WITHER_SKELETON, 40),
                        new CollectItemObjective("soul_fragments", Material.SOUL_SAND, 50),
                        new CollectItemObjective("death_tokens", Material.WITHER_SKELETON_SKULL, 5),
                        new KillMobObjective("death_avatar", EntityType.WITHER, 2),
                        new InteractNPCObjective("death_god_altar", 224), // 죽음신 제단
                        new CollectItemObjective("death_blessing", Material.TOTEM_OF_UNDYING, 1),
                        
                        // 다섯 번째 신 - 시간의 신
                        new VisitLocationObjective("time_temple", "temple_of_time"),
                        new CollectItemObjective("temporal_shards", Material.CLOCK, 10),
                        new PlaceBlockObjective("time_mechanism", Material.REPEATER, 20),
                        new SurviveObjective("time_loop", 900), // 15분간 시간 고리
                        new CollectItemObjective("past_artifact", Material.ANCIENT_DEBRIS, 5),
                        new CollectItemObjective("future_artifact", Material.NETHERITE_SCRAP, 5),
                        new InteractNPCObjective("time_god_altar", 225), // 시간신 제단
                        new CollectItemObjective("time_blessing", Material.ENDER_PEARL, 1),
                        
                        // 최종 의식 - 모든 축복 통합
                        new VisitLocationObjective("convergence_altar", "altar_of_convergence"),
                        new DeliverItemObjective("place_war", "convergence_altar", Material.IRON_SWORD, 1),
                        new DeliverItemObjective("place_wisdom", "convergence_altar", Material.ENCHANTED_BOOK, 1),
                        new DeliverItemObjective("place_nature", "convergence_altar", Material.GOLDEN_APPLE, 1),
                        new DeliverItemObjective("place_death", "convergence_altar", Material.TOTEM_OF_UNDYING, 1),
                        new DeliverItemObjective("place_time", "convergence_altar", Material.ENDER_PEARL, 1),
                        
                        // 고대의 힘 각성
                        new PayCurrencyObjective("final_offering", CurrencyType.DIAMOND, 100),
                        new SurviveObjective("power_awakening", 1800), // 30분간 각성 의식
                        new KillMobObjective("ancient_guardian", EntityType.ELDER_GUARDIAN, 5),
                        new CollectItemObjective("ancient_power_core", Material.NETHER_STAR, 5),
                        
                        // 완료
                        new InteractNPCObjective("power_granted", 220)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 100000)
                        .addCurrency(CurrencyType.DIAMOND, 1000)
                        .addItem(new ItemStack(Material.NETHER_STAR, 10)) // 고대의 힘 핵심
                        .addItem(new ItemStack(Material.NETHERITE_BLOCK, 5))
                        .addItem(new ItemStack(Material.BEACON, 5))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 10))
                        .addItem(new ItemStack(Material.DRAGON_EGG)) // 특별한 증표
                        .addExperience(50000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SPECIAL)
                .minLevel(60)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "잊혀진 신들의 축복" : "Blessings of Forgotten Gods";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "전설로만 전해지던 5명의 고대 신들의 축복을 받아",
                    "궁극의 힘을 얻는 비밀스러운 여정입니다.",
                    "",
                    "⚡ 고대의 힘 특성:",
                    "• 모든 능력치 대폭 상승",
                    "• 특별한 신의 가호 효과",
                    "• 죽음 회피 능력",
                    "• 시간 조작 능력",
                    "• 자연과의 교감",
                    "",
                    "5명의 고대 신:",
                    "• 전쟁의 신 - 무한한 전투력",
                    "• 지혜의 신 - 모든 지식과 마법",
                    "• 자연의 신 - 생명과 치유",
                    "• 죽음의 신 - 불멸과 부활",
                    "• 시간의 신 - 시공간 조작",
                    "",
                    "각 신의 시험:",
                    "• 전쟁 - 20분간 끝없는 전투",
                    "• 지혜 - 고대의 퍼즐 해결",
                    "• 자연 - 100개의 작물 재배",
                    "• 죽음 - 언데드 군단 섬멸",
                    "• 시간 - 15분간 시간 고리",
                    "",
                    "경고:",
                    "• 이 퀘스트는 극도로 어렵습니다",
                    "• 레벨 60 이상 권장",
                    "• 실패 시 큰 페널티",
                    "• 한 번만 도전 가능",
                    "",
                    "최종 의식:",
                    "• 5개의 축복 통합",
                    "• 다이아몬드 100개 봉헌",
                    "• 30분간 각성 의식",
                    "• 고대 수호자 5마리 처치",
                    "",
                    "전설적인 보상:",
                    "• 골드 100,000",
                    "• 다이아몬드 1,000개",
                    "• 고대의 힘 핵심 10개",
                    "• 네더라이트 블록 5개",
                    "• 신호기 5개",
                    "• 인챈트된 황금 사과 10개",
                    "• 드래곤 알 (특별 증표)",
                    "• 경험치 50,000"
            );
        } else {
            return Arrays.asList(
                    "Receive blessings from 5 ancient gods of legend",
                    "and embark on a secret journey to obtain ultimate power.",
                    "",
                    "⚡ Ancient Power Features:",
                    "• Massive increase in all stats",
                    "• Special divine protection effects",
                    "• Death avoidance ability",
                    "• Time manipulation ability",
                    "• Communion with nature",
                    "",
                    "5 Ancient Gods:",
                    "• God of War - Infinite combat power",
                    "• God of Wisdom - All knowledge and magic",
                    "• God of Nature - Life and healing",
                    "• God of Death - Immortality and resurrection",
                    "• God of Time - Space-time manipulation",
                    "",
                    "Each God's Trial:",
                    "• War - 20 minutes of endless battle",
                    "• Wisdom - Solve ancient puzzles",
                    "• Nature - Cultivate 100 crops",
                    "• Death - Annihilate undead legion",
                    "• Time - 15 minutes time loop",
                    "",
                    "Warning:",
                    "• This quest is extremely difficult",
                    "• Level 60+ recommended",
                    "• Heavy penalty on failure",
                    "• Can only attempt once",
                    "",
                    "Final Ritual:",
                    "• Merge 5 blessings",
                    "• Offer 100 diamonds",
                    "• 30-minute awakening ritual",
                    "• Defeat 5 ancient guardians",
                    "",
                    "Legendary Rewards:",
                    "• 100,000 Gold",
                    "• 1,000 Diamonds",
                    "• 10 Ancient Power Cores",
                    "• 5 Netherite Blocks",
                    "• 5 Beacons",
                    "• 10 Enchanted Golden Apples",
                    "• Dragon Egg (Special Token)",
                    "• 50,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "mysterious_rune" -> isKorean ? "신비한 룬 발견" : "Discover mysterious rune";
            case "hidden_shrine" -> isKorean ? "숨겨진 신전 찾기" : "Find hidden shrine";
            case "ancient_spirit" -> isKorean ? "고대 영혼과 대화" : "Talk to ancient spirit";
            case "war_temple" -> isKorean ? "전쟁의 신전 방문" : "Visit Temple of War";
            case "prove_strength" -> isKorean ? "힘 증명 (철 골렘 20마리)" : "Prove strength (20 Iron Golems)";
            case "defeat_warriors" -> isKorean ? "전사 50명 처치" : "Defeat 50 warriors";
            case "pvp_kills" -> isKorean ? "PvP 승리 10회" : "10 PvP victories";
            case "warrior_souls" -> isKorean ? "전사의 영혼 100개 수집" : "Collect 100 warrior souls";
            case "endless_battle" -> isKorean ? "20분간 끝없는 전투" : "20 minutes endless battle";
            case "war_god_altar" -> isKorean ? "전쟁신 제단 활성화" : "Activate War God altar";
            case "war_blessing" -> isKorean ? "전쟁의 축복 받기" : "Receive War blessing";
            case "wisdom_temple" -> isKorean ? "지혜의 신전 방문" : "Visit Temple of Wisdom";
            case "ancient_books" -> isKorean ? "고대 서적 20권 수집" : "Collect 20 ancient books";
            case "uncover_secrets" -> isKorean ? "비밀 발견 (책장 50개 조사)" : "Uncover secrets (50 bookshelves)";
            case "wisdom_elixir" -> isKorean ? "지혜의 영약 30개 제조" : "Brew 30 wisdom elixirs";
            case "arrange_puzzle" -> isKorean ? "퍼즐 배치 (레드스톤 램프 16개)" : "Arrange puzzle (16 redstone lamps)";
            case "mental_trial" -> isKorean ? "10분간 정신 시험" : "10 minutes mental trial";
            case "wisdom_god_altar" -> isKorean ? "지혜신 제단 활성화" : "Activate Wisdom God altar";
            case "wisdom_blessing" -> isKorean ? "지혜의 축복 받기" : "Receive Wisdom blessing";
            case "nature_temple" -> isKorean ? "자연의 신전 방문" : "Visit Temple of Nature";
            case "nature_offering" -> isKorean ? "자연에 바칠 작물 100개 수확" : "Harvest 100 crops for offering";
            case "sacred_seeds" -> isKorean ? "신성한 씨앗 50개 수집" : "Collect 50 sacred seeds";
            case "plant_trees" -> isKorean ? "나무 20그루 심기" : "Plant 20 trees";
            case "protect_nature" -> isKorean ? "자연 파괴자 30명 처치" : "Defeat 30 nature destroyers";
            case "nature_essence" -> isKorean ? "자연의 정수 30개 수집" : "Collect 30 nature essence";
            case "nature_god_altar" -> isKorean ? "자연신 제단 활성화" : "Activate Nature God altar";
            case "nature_blessing" -> isKorean ? "자연의 축복 받기" : "Receive Nature blessing";
            case "death_temple" -> isKorean ? "죽음의 신전 방문" : "Visit Temple of Death";
            case "undead_army" -> isKorean ? "언데드 군단 100마리 처치" : "Defeat 100 undead army";
            case "skeleton_legion" -> isKorean ? "스켈레톤 군단 80마리 처치" : "Defeat 80 skeleton legion";
            case "death_knights" -> isKorean ? "죽음의 기사 40명 처치" : "Defeat 40 death knights";
            case "soul_fragments" -> isKorean ? "영혼 조각 50개 수집" : "Collect 50 soul fragments";
            case "death_tokens" -> isKorean ? "죽음의 증표 5개 수집" : "Collect 5 death tokens";
            case "death_avatar" -> isKorean ? "죽음의 화신 2마리 처치" : "Defeat 2 death avatars";
            case "death_god_altar" -> isKorean ? "죽음신 제단 활성화" : "Activate Death God altar";
            case "death_blessing" -> isKorean ? "죽음의 축복 받기" : "Receive Death blessing";
            case "time_temple" -> isKorean ? "시간의 신전 방문" : "Visit Temple of Time";
            case "temporal_shards" -> isKorean ? "시간 조각 10개 수집" : "Collect 10 temporal shards";
            case "time_mechanism" -> isKorean ? "시간 장치 설치 (중계기 20개)" : "Install time mechanism (20 repeaters)";
            case "time_loop" -> isKorean ? "15분간 시간 고리" : "15 minutes time loop";
            case "past_artifact" -> isKorean ? "과거의 유물 5개 수집" : "Collect 5 artifacts of past";
            case "future_artifact" -> isKorean ? "미래의 유물 5개 수집" : "Collect 5 artifacts of future";
            case "time_god_altar" -> isKorean ? "시간신 제단 활성화" : "Activate Time God altar";
            case "time_blessing" -> isKorean ? "시간의 축복 받기" : "Receive Time blessing";
            case "convergence_altar" -> isKorean ? "융합의 제단 도달" : "Reach Altar of Convergence";
            case "place_war" -> isKorean ? "전쟁의 축복 배치" : "Place War blessing";
            case "place_wisdom" -> isKorean ? "지혜의 축복 배치" : "Place Wisdom blessing";
            case "place_nature" -> isKorean ? "자연의 축복 배치" : "Place Nature blessing";
            case "place_death" -> isKorean ? "죽음의 축복 배치" : "Place Death blessing";
            case "place_time" -> isKorean ? "시간의 축복 배치" : "Place Time blessing";
            case "final_offering" -> isKorean ? "최종 봉헌 (다이아몬드 100개)" : "Final offering (100 diamonds)";
            case "power_awakening" -> isKorean ? "30분간 힘의 각성" : "30 minutes power awakening";
            case "ancient_guardian" -> isKorean ? "고대 수호자 5마리 처치" : "Defeat 5 ancient guardians";
            case "ancient_power_core" -> isKorean ? "고대의 힘 핵심 5개 획득" : "Obtain 5 ancient power cores";
            case "power_granted" -> isKorean ? "힘의 부여 완료" : "Power granting complete";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("ancient_power_dialog");

        // 시작
        dialog.addLine("고대 영혼",
                "오랜 세월... 누군가 이곳을 찾아올 줄 알았다...",
                "After ages... I knew someone would find this place...");

        dialog.addLine("고대 영혼",
                "너는 신들의 축복을 원하는가? 그 길은 험난하고 위험하다.",
                "Do you seek the gods' blessings? The path is arduous and dangerous.");

        dialog.addLine("플레이어",
                "어떤 시험이든 받아들이겠습니다.",
                "I will accept any trial.");

        dialog.addLine("고대 영혼",
                "5명의 고대 신을 찾아가 그들의 축복을 받아라. 모든 축복이 하나가 될 때...",
                "Visit the 5 ancient gods and receive their blessings. When all blessings become one...");

        // 전쟁의 신
        dialog.addLine("전쟁의 신",
                "전투를 원하는가? 끝없는 전쟁 속에서 살아남아라!",
                "You seek battle? Survive in endless war!");

        // 지혜의 신
        dialog.addLine("지혜의 신",
                "지식은 힘이다. 하지만 모든 지식에는 대가가 따른다.",
                "Knowledge is power. But all knowledge comes with a price.");

        // 자연의 신
        dialog.addLine("자연의 신",
                "자연과 하나가 되어라. 파괴하는 자가 아닌 창조하는 자가 되어라.",
                "Become one with nature. Be a creator, not a destroyer.");

        // 죽음의 신
        dialog.addLine("죽음의 신",
                "죽음을 두려워하지 않는 자만이 진정한 생명을 얻는다.",
                "Only those who do not fear death gain true life.");

        // 시간의 신
        dialog.addLine("시간의 신",
                "과거와 미래, 그리고 현재... 모든 시간은 하나다.",
                "Past and future, and present... all time is one.");

        // 융합
        dialog.addLine("고대 영혼",
                "모든 축복을 모았구나. 이제 그것들을 하나로 융합시켜라.",
                "You've gathered all blessings. Now merge them into one.");

        // 각성
        dialog.addLine("고대 영혼",
                "느껴지는가? 고대의 힘이 깨어나고 있다!",
                "Do you feel it? The ancient power is awakening!");

        // 완료
        dialog.addLine("고대 영혼",
                "축하한다... 이제 너는 신들의 축복을 받은 자다.",
                "Congratulations... You are now blessed by the gods.");

        dialog.addLine("고대 영혼",
                "이 힘을 현명하게 사용하라. 세상의 균형을 지키는 것이 너의 사명이다.",
                "Use this power wisely. Your mission is to maintain the world's balance.");

        return dialog;
    }
}