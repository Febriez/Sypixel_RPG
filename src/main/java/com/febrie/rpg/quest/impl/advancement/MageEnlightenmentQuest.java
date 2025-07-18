package com.febrie.rpg.quest.impl.advancement;

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
 * 마법사의 깨달음 - 직업 전직 퀘스트
 * 견습 마법사에서 대마법사로 승급하는 깨달음의 여정
 *
 * @author Febrie
 */
public class MageEnlightenmentQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class MageEnlightenmentBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new MageEnlightenmentQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public MageEnlightenmentQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private MageEnlightenmentQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new MageEnlightenmentBuilder()
                .id(QuestID.CLASS_MAGE_ENLIGHTENMENT)
                .objectives(Arrays.asList(
                        // 마법사의 길 시작
                        new InteractNPCObjective("archmage_mentor", 42), // 대마법사 스승
                        new ReachLevelObjective("mage_mastery", 30),
                        new CollectItemObjective("magic_essence", Material.LAPIS_LAZULI, 64),
                        
                        // 첫 번째 시험 - 원소 마법
                        new VisitLocationObjective("elemental_sanctum", "elemental_magic_hall"),
                        new InteractNPCObjective("fire_elemental", 43), // 불의 정령
                        new KillMobObjective("fire_test", EntityType.BLAZE, 30),
                        new CollectItemObjective("fire_essence", Material.BLAZE_POWDER, 20),
                        
                        new InteractNPCObjective("water_elemental", 44), // 물의 정령
                        new CollectItemObjective("water_essence", Material.PRISMARINE_CRYSTALS, 20),
                        new CollectItemObjective("ice_shards", Material.PACKED_ICE, 10),
                        
                        new InteractNPCObjective("earth_elemental", 45), // 대지의 정령
                        new BreakBlockObjective("earth_test", Material.STONE, 100),
                        new CollectItemObjective("earth_essence", Material.CLAY_BALL, 30),
                        
                        new InteractNPCObjective("air_elemental", 46), // 바람의 정령
                        new KillMobObjective("air_test", EntityType.PHANTOM, 20),
                        new CollectItemObjective("air_essence", Material.PHANTOM_MEMBRANE, 15),
                        
                        // 두 번째 시험 - 마나 제어
                        new VisitLocationObjective("mana_chamber", "arcane_meditation_room"),
                        new PlaceBlockObjective("mana_crystals", Material.SEA_LANTERN, 8),
                        new SurviveObjective("mana_overflow", 600), // 10분간 마나 폭주 견디기
                        new CollectItemObjective("pure_mana", Material.GLOWSTONE, 30),
                        new CraftItemObjective("mana_potion", Material.POTION, 20),
                        
                        // 세 번째 시험 - 금지된 지식
                        new VisitLocationObjective("forbidden_library", "restricted_magic_archive"),
                        new InteractNPCObjective("knowledge_keeper", 47), // 지식의 수호자
                        new CollectItemObjective("ancient_tomes", Material.ENCHANTED_BOOK, 10),
                        new KillMobObjective("knowledge_guardians", EntityType.VEX, 50),
                        new CollectItemObjective("forbidden_scroll", Material.WRITTEN_BOOK, 1),
                        new PayCurrencyObjective("knowledge_price", CurrencyType.DIAMOND, 30),
                        
                        // 네 번째 시험 - 마법 창조
                        new VisitLocationObjective("creation_altar", "spell_creation_altar"),
                        new CollectItemObjective("spell_components", Material.ENDER_PEARL, 10),
                        new CollectItemObjective("magic_ink", Material.INK_SAC, 20),
                        new CraftItemObjective("create_wand", Material.STICK, 1),
                        new PlaceBlockObjective("enchant_altar", Material.ENCHANTING_TABLE, 1),
                        new CollectItemObjective("new_spell", Material.ENCHANTED_BOOK, 1),
                        
                        // 최종 시험 - 마법 대결
                        new VisitLocationObjective("arcane_arena", "magical_duel_arena"),
                        new InteractNPCObjective("rival_mage", 48), // 라이벌 마법사
                        new KillMobObjective("illusion_army", EntityType.EVOKER, 10),
                        new KillMobObjective("summoned_vex", EntityType.VEX, 100),
                        new SurviveObjective("magic_duel", 900), // 15분간 마법 대결
                        new KillMobObjective("rival_defeated", EntityType.WITCH, 5),
                        
                        // 깨달음의 순간
                        new VisitLocationObjective("enlightenment_peak", "mystic_mountain_peak"),
                        new PlaceBlockObjective("meditation_circle", Material.WHITE_CARPET, 9),
                        new SurviveObjective("final_meditation", 600), // 10분간 최종 명상
                        new CollectItemObjective("enlightenment_orb", Material.NETHER_STAR, 1),
                        
                        // 대마법사 승급
                        new DeliverItemObjective("deliver_orb", "archmage_mentor", Material.NETHER_STAR, 1),
                        new InteractNPCObjective("graduation_ceremony", 42)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 15000)
                        .addCurrency(CurrencyType.DIAMOND, 100)
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 10)) // 고급 마법서
                        .addItem(new ItemStack(Material.BLAZE_ROD)) // 대마법사 지팡이
                        .addItem(new ItemStack(Material.ELYTRA)) // 마법사 날개
                        .addItem(new ItemStack(Material.ENDER_EYE, 16))
                        .addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 64))
                        .addExperience(8000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.ADVANCEMENT)
                .minLevel(30)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "대마법사의 깨달음" : "Archmage's Enlightenment";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "견습 마법사에서 대마법사로 승급하는 깨달음의 여정입니다.",
                    "4원소를 마스터하고 궁극의 마법을 창조하세요.",
                    "",
                    "🧙 대마법사 특성:",
                    "• 모든 원소 마법 마스터",
                    "• 새로운 마법 창조 능력",
                    "• 시공간 조작 가능",
                    "• 마나 무한 재생",
                    "• 전설적인 마법 스킬",
                    "",
                    "시험 단계:",
                    "• 1단계: 4원소 마법 습득",
                    "• 2단계: 마나 제어 마스터",
                    "• 3단계: 금지된 지식 획득",
                    "• 4단계: 새로운 마법 창조",
                    "• 5단계: 마법 대결 승리",
                    "• 6단계: 깨달음 달성",
                    "",
                    "원소 시험:",
                    "• 불 - 화염 정령 30마리 처치",
                    "• 물 - 프리즈마린 수정 수집",
                    "• 대지 - 100개의 돌 파괴",
                    "• 바람 - 팬텀 20마리 처치",
                    "",
                    "특별 도전:",
                    "• 10분간 마나 폭주 견디기",
                    "• 금지된 도서관 침투",
                    "• 15분간 마법 대결",
                    "• 명상을 통한 깨달음",
                    "",
                    "목표:",
                    "• 레벨 30 달성 (마법사)",
                    "• 4원소 정수 수집",
                    "• 마나 제어 완성",
                    "• 금지된 지식 습득",
                    "• 새로운 마법 창조",
                    "• 라이벌 마법사 격파",
                    "• 최종 깨달음 달성",
                    "",
                    "보상:",
                    "• 대마법사 직업 전직",
                    "• 골드 15,000",
                    "• 다이아몬드 100개",
                    "• 고급 마법서 10권",
                    "• 대마법사 지팡이",
                    "• 마법사 날개",
                    "• 엔더의 눈 16개",
                    "• 경험치 병 64개",
                    "• 경험치 8,000"
            );
        } else {
            return Arrays.asList(
                    "Journey of enlightenment from apprentice mage to archmage.",
                    "Master the four elements and create ultimate magic.",
                    "",
                    "🧙 Archmage Features:",
                    "• Master of all elemental magic",
                    "• Ability to create new spells",
                    "• Space-time manipulation",
                    "• Infinite mana regeneration",
                    "• Legendary magic skills",
                    "",
                    "Trial Stages:",
                    "• Stage 1: Learn 4 elemental magics",
                    "• Stage 2: Master mana control",
                    "• Stage 3: Acquire forbidden knowledge",
                    "• Stage 4: Create new magic",
                    "• Stage 5: Win magical duel",
                    "• Stage 6: Achieve enlightenment",
                    "",
                    "Elemental Trials:",
                    "• Fire - Defeat 30 flame spirits",
                    "• Water - Collect prismarine crystals",
                    "• Earth - Break 100 stones",
                    "• Air - Defeat 20 phantoms",
                    "",
                    "Special Challenges:",
                    "• Endure mana overflow for 10 minutes",
                    "• Infiltrate forbidden library",
                    "• 15-minute magical duel",
                    "• Enlightenment through meditation",
                    "",
                    "Objectives:",
                    "• Reach Level 30 (Mage)",
                    "• Collect 4 elemental essences",
                    "• Complete mana control",
                    "• Acquire forbidden knowledge",
                    "• Create new magic",
                    "• Defeat rival mage",
                    "• Achieve final enlightenment",
                    "",
                    "Rewards:",
                    "• Archmage class advancement",
                    "• 15,000 Gold",
                    "• 100 Diamonds",
                    "• 10 Advanced Spellbooks",
                    "• Archmage Staff",
                    "• Mage Wings",
                    "• 16 Eyes of Ender",
                    "• 64 Experience Bottles",
                    "• 8,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "archmage_mentor" -> isKorean ? "대마법사 스승과 대화" : "Talk to Archmage Mentor";
            case "mage_mastery" -> isKorean ? "마법사 레벨 30 달성" : "Reach Mage Level 30";
            case "magic_essence" -> isKorean ? "마법 정수 64개 수집" : "Collect 64 magic essence";
            case "elemental_sanctum" -> isKorean ? "원소의 성소 방문" : "Visit Elemental Sanctum";
            case "fire_elemental" -> isKorean ? "불의 정령과 대화" : "Talk to Fire Elemental";
            case "fire_test" -> isKorean ? "화염 정령 30마리 처치" : "Defeat 30 flame spirits";
            case "fire_essence" -> isKorean ? "불의 정수 20개 수집" : "Collect 20 fire essence";
            case "water_elemental" -> isKorean ? "물의 정령과 대화" : "Talk to Water Elemental";
            case "water_essence" -> isKorean ? "물의 정수 20개 수집" : "Collect 20 water essence";
            case "ice_shards" -> isKorean ? "얼음 조각 10개 수집" : "Collect 10 ice shards";
            case "earth_elemental" -> isKorean ? "대지의 정령과 대화" : "Talk to Earth Elemental";
            case "earth_test" -> isKorean ? "대지 시험 (돌 100개 파괴)" : "Earth test (break 100 stones)";
            case "earth_essence" -> isKorean ? "대지의 정수 30개 수집" : "Collect 30 earth essence";
            case "air_elemental" -> isKorean ? "바람의 정령과 대화" : "Talk to Air Elemental";
            case "air_test" -> isKorean ? "바람 정령 20마리 처치" : "Defeat 20 wind spirits";
            case "air_essence" -> isKorean ? "바람의 정수 15개 수집" : "Collect 15 air essence";
            case "mana_chamber" -> isKorean ? "마나의 방 진입" : "Enter Mana Chamber";
            case "mana_crystals" -> isKorean ? "마나 수정 8개 설치" : "Place 8 mana crystals";
            case "mana_overflow" -> isKorean ? "10분간 마나 폭주 견디기" : "Endure mana overflow for 10 minutes";
            case "pure_mana" -> isKorean ? "순수 마나 30개 수집" : "Collect 30 pure mana";
            case "mana_potion" -> isKorean ? "마나 물약 20개 제조" : "Brew 20 mana potions";
            case "forbidden_library" -> isKorean ? "금지된 도서관 침투" : "Infiltrate forbidden library";
            case "knowledge_keeper" -> isKorean ? "지식의 수호자와 대화" : "Talk to Knowledge Keeper";
            case "ancient_tomes" -> isKorean ? "고대 마법서 10권 수집" : "Collect 10 ancient tomes";
            case "knowledge_guardians" -> isKorean ? "지식 수호자 50마리 처치" : "Defeat 50 knowledge guardians";
            case "forbidden_scroll" -> isKorean ? "금지된 두루마리 획득" : "Obtain forbidden scroll";
            case "knowledge_price" -> isKorean ? "지식의 대가 (다이아몬드 30개)" : "Price of knowledge (30 diamonds)";
            case "creation_altar" -> isKorean ? "창조의 제단 방문" : "Visit Creation Altar";
            case "spell_components" -> isKorean ? "주문 재료 10개 수집" : "Collect 10 spell components";
            case "magic_ink" -> isKorean ? "마법 잉크 20개 수집" : "Collect 20 magic ink";
            case "create_wand" -> isKorean ? "마법 지팡이 제작" : "Create magic wand";
            case "enchant_altar" -> isKorean ? "마법 부여대 설치" : "Place enchanting table";
            case "new_spell" -> isKorean ? "새로운 주문 창조" : "Create new spell";
            case "arcane_arena" -> isKorean ? "비전 투기장 진입" : "Enter Arcane Arena";
            case "rival_mage" -> isKorean ? "라이벌 마법사와 대면" : "Face rival mage";
            case "illusion_army" -> isKorean ? "환영 군단 10마리 처치" : "Defeat 10 illusion army";
            case "summoned_vex" -> isKorean ? "소환된 벡스 100마리 처치" : "Defeat 100 summoned vexes";
            case "magic_duel" -> isKorean ? "15분간 마법 대결" : "Magic duel for 15 minutes";
            case "rival_defeated" -> isKorean ? "라이벌 마법사단 5명 격파" : "Defeat 5 rival mages";
            case "enlightenment_peak" -> isKorean ? "깨달음의 봉우리 도달" : "Reach Enlightenment Peak";
            case "meditation_circle" -> isKorean ? "명상 원 설치 (카펫 9개)" : "Set meditation circle (9 carpets)";
            case "final_meditation" -> isKorean ? "10분간 최종 명상" : "Final meditation for 10 minutes";
            case "enlightenment_orb" -> isKorean ? "깨달음의 구슬 획득" : "Obtain Enlightenment Orb";
            case "deliver_orb" -> isKorean ? "깨달음의 구슬 전달" : "Deliver Enlightenment Orb";
            case "graduation_ceremony" -> isKorean ? "졸업식 참석" : "Attend graduation ceremony";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("mage_enlightenment_dialog");

        // 시작
        dialog.addLine("대마법사 스승",
                "견습 마법사여, 더 높은 경지를 추구하는가?",
                "Apprentice mage, do you seek a higher realm?");

        dialog.addLine("대마법사 스승",
                "대마법사가 되는 길은 험난하다. 마법의 본질을 이해하고 창조해야 한다.",
                "The path to archmage is arduous. You must understand and create the essence of magic.");

        dialog.addLine("플레이어",
                "저는 준비되었습니다, 스승님.",
                "I am ready, Master.");

        dialog.addLine("대마법사 스승",
                "먼저 4원소를 완전히 이해해야 한다. 각 정령을 찾아가 시험을 통과하라.",
                "First, you must fully understand the four elements. Visit each elemental and pass their tests.");

        // 원소 정령들
        dialog.addLine("불의 정령",
                "불꽃처럼 타오르는 열정이 있는가? 나의 불길을 견뎌보아라!",
                "Do you have passion that burns like flame? Endure my fire!");

        dialog.addLine("물의 정령",
                "물처럼 유연하면서도 강한가? 얼음과 물의 조화를 보여라.",
                "Are you flexible yet strong like water? Show the harmony of ice and water.");

        dialog.addLine("대지의 정령",
                "대지처럼 굳건한 의지가 있는가? 바위를 부수고 본질을 찾아라.",
                "Do you have will as solid as earth? Break rocks and find the essence.");

        dialog.addLine("바람의 정령",
                "바람처럼 자유로운가? 하늘을 나는 자들과 함께 춤춰라.",
                "Are you free like the wind? Dance with those who fly in the sky.");

        // 마나 제어
        dialog.addLine("대마법사 스승",
                "원소를 이해했다면, 이제 마나를 완전히 제어해야 한다.",
                "If you understand the elements, now you must fully control mana.");

        // 금지된 지식
        dialog.addLine("지식의 수호자",
                "이곳의 지식은 위험하다. 준비되지 않은 자는 미쳐버린다.",
                "The knowledge here is dangerous. The unprepared go mad.");

        // 마법 대결
        dialog.addLine("라이벌 마법사",
                "흥, 네가 차기 대마법사 후보라고? 실력을 증명해봐라!",
                "Hmph, you're the next archmage candidate? Prove your skill!");

        // 깨달음
        dialog.addLine("대마법사 스승",
                "마지막 시험이다. 깨달음의 봉우리에서 명상하고 진정한 마법의 본질을 깨달아라.",
                "The final test. Meditate at Enlightenment Peak and realize the true essence of magic.");

        // 완료
        dialog.addLine("대마법사 스승",
                "축하한다, 대마법사여! 이제 너는 마법의 창조자다.",
                "Congratulations, Archmage! You are now a creator of magic.");

        dialog.addLine("대마법사 스승",
                "이 지팡이와 날개를 받아라. 지혜롭게 사용하여 세상을 밝혀라.",
                "Take this staff and wings. Use them wisely to illuminate the world.");

        return dialog;
    }
}