package com.febrie.rpg.quest.impl.side;

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
 * 고대 암호 - 미스터리/퍼즐 사이드 퀘스트
 * 고대 문명의 비밀을 풀어내는 탐정 스타일 퀘스트
 *
 * @author Febrie
 */
public class AncientCipherQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class AncientCipherBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new AncientCipherQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public AncientCipherQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private AncientCipherQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new AncientCipherBuilder()
                .id(QuestID.SIDE_ANCIENT_CIPHER)
                .objectives(Arrays.asList(
                        // 미스터리 시작
                        new InteractNPCObjective("archaeologist", 190), // 고고학자
                        new CollectItemObjective("ancient_tablet", Material.CHISELED_STONE_BRICKS, 1),
                        new VisitLocationObjective("research_lab", "archaeology_lab"),
                        
                        // 첫 번째 단서 - 도서관
                        new VisitLocationObjective("ancient_library", "forbidden_library"),
                        new InteractNPCObjective("librarian", 191), // 사서
                        new CollectItemObjective("old_books", Material.WRITTEN_BOOK, 5),
                        new BreakBlockObjective("search_shelves", Material.BOOKSHELF, 20),
                        new CollectItemObjective("cipher_key_1", Material.PAPER, 1),
                        
                        // 두 번째 단서 - 천문대
                        new VisitLocationObjective("observatory", "ancient_observatory"),
                        new PlaceBlockObjective("align_telescopes", Material.SPYGLASS, 3),
                        new SurviveObjective("night_observation", 600), // 10분간 밤 관측
                        new CollectItemObjective("star_map", Material.MAP, 1),
                        new KillMobObjective("phantom_guards", EntityType.PHANTOM, 20),
                        new CollectItemObjective("cipher_key_2", Material.COMPASS, 1),
                        
                        // 세 번째 단서 - 지하 묘지
                        new VisitLocationObjective("catacombs", "underground_crypt"),
                        new KillMobObjective("crypt_keepers", EntityType.HUSK, 30),
                        new KillMobObjective("ancient_guardians", EntityType.WITHER_SKELETON, 15),
                        new BreakBlockObjective("open_tombs", Material.STONE_BRICKS, 50),
                        new CollectItemObjective("burial_relics", Material.GOLD_NUGGET, 20),
                        new CollectItemObjective("cipher_key_3", Material.CLOCK, 1),
                        
                        // 네 번째 단서 - 연금술사의 탑
                        new VisitLocationObjective("alchemist_tower", "abandoned_alchemy_tower"),
                        new InteractNPCObjective("ghost_alchemist", 192), // 유령 연금술사
                        new CollectItemObjective("rare_ingredients", Material.GLISTERING_MELON_SLICE, 5),
                        new CollectItemObjective("mystic_dust", Material.GLOWSTONE_DUST, 20),
                        new CraftItemObjective("brew_potion", Material.POTION, 10),
                        new CollectItemObjective("cipher_key_4", Material.BREWING_STAND, 1),
                        
                        // 암호 해독
                        new DeliverItemObjective("deliver_key1", "archaeologist", Material.PAPER, 1),
                        new DeliverItemObjective("deliver_key2", "archaeologist", Material.COMPASS, 1),
                        new DeliverItemObjective("deliver_key3", "archaeologist", Material.CLOCK, 1),
                        new DeliverItemObjective("deliver_key4", "archaeologist", Material.BREWING_STAND, 1),
                        new InteractNPCObjective("decode_cipher", 190),
                        
                        // 숨겨진 방 발견
                        new VisitLocationObjective("hidden_chamber", "secret_ancient_vault"),
                        new PlaceBlockObjective("insert_keys", Material.LEVER, 4),
                        new SurviveObjective("puzzle_room", 300), // 5분간 퍼즐 룸
                        
                        // 보물 방 진입
                        new BreakBlockObjective("break_seal", Material.OBSIDIAN, 20),
                        new KillMobObjective("treasure_guardian", EntityType.ELDER_GUARDIAN, 1),
                        new CollectItemObjective("ancient_artifact", Material.HEART_OF_THE_SEA, 1),
                        new CollectItemObjective("wisdom_scrolls", Material.ENCHANTED_BOOK, 5),
                        
                        // 탈출
                        new KillMobObjective("awakened_mummies", EntityType.ZOMBIE_VILLAGER, 40),
                        new SurviveObjective("escape_trap", 600), // 10분간 함정 탈출
                        new VisitLocationObjective("escape_route", "archaeology_lab"),
                        
                        // 완료
                        new DeliverItemObjective("deliver_artifact", "archaeologist", Material.HEART_OF_THE_SEA, 1),
                        new InteractNPCObjective("quest_complete", 190)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 8000)
                        .addCurrency(CurrencyType.DIAMOND, 50)
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 5))
                        .addItem(new ItemStack(Material.SPYGLASS))
                        .addItem(new ItemStack(Material.COMPASS))
                        .addItem(new ItemStack(Material.CLOCK))
                        .addItem(new ItemStack(Material.MAP))
                        .addExperience(4000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SIDE)
                .minLevel(25)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "고대 문명의 비밀" : "Secrets of Ancient Civilization";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "신비한 석판에 새겨진 고대 암호를 해독하고",
                    "잃어버린 문명의 보물을 찾아내세요!",
                    "",
                    "🔍 미스터리 요소:",
                    "• 4개의 암호 열쇠 수집",
                    "• 각 장소에 숨겨진 단서",
                    "• 복잡한 퍼즐 해결",
                    "• 고대의 함정과 수호자",
                    "",
                    "탐험 장소:",
                    "• 금지된 도서관",
                    "• 고대 천문대",
                    "• 지하 묘지",
                    "• 연금술사의 탑",
                    "• 비밀의 보물 방",
                    "",
                    "주요 단서:",
                    "• 도서관 - 지식의 열쇠",
                    "• 천문대 - 별의 열쇠",
                    "• 묘지 - 시간의 열쇠",
                    "• 연금술 탑 - 변화의 열쇠",
                    "",
                    "목표:",
                    "• 고고학자의 의뢰 수락",
                    "• 4개의 암호 열쇠 수집",
                    "• 암호 해독",
                    "• 숨겨진 방 발견",
                    "• 고대 유물 획득",
                    "• 함정에서 탈출",
                    "",
                    "도전 과제:",
                    "• 밤 10분간 천체 관측",
                    "• 퍼즐 룸 5분 해결",
                    "• 함정 10분간 생존",
                    "",
                    "보상:",
                    "• 골드 8,000",
                    "• 다이아몬드 50개",
                    "• 마법이 부여된 책 5개",
                    "• 망원경",
                    "• 나침반",
                    "• 시계",
                    "• 지도",
                    "• 경험치 4,000"
            );
        } else {
            return Arrays.asList(
                    "Decode the ancient cipher engraved on a mysterious tablet",
                    "and discover the treasures of a lost civilization!",
                    "",
                    "🔍 Mystery Elements:",
                    "• Collect 4 cipher keys",
                    "• Hidden clues in each location",
                    "• Complex puzzle solving",
                    "• Ancient traps and guardians",
                    "",
                    "Exploration Sites:",
                    "• Forbidden Library",
                    "• Ancient Observatory",
                    "• Underground Catacombs",
                    "• Alchemist's Tower",
                    "• Secret Treasure Vault",
                    "",
                    "Key Clues:",
                    "• Library - Key of Knowledge",
                    "• Observatory - Key of Stars",
                    "• Catacombs - Key of Time",
                    "• Alchemy Tower - Key of Change",
                    "",
                    "Objectives:",
                    "• Accept archaeologist's request",
                    "• Collect 4 cipher keys",
                    "• Decode the cipher",
                    "• Discover hidden chamber",
                    "• Obtain ancient artifact",
                    "• Escape from traps",
                    "",
                    "Challenges:",
                    "• Observe stars for 10 minutes at night",
                    "• Solve puzzle room in 5 minutes",
                    "• Survive traps for 10 minutes",
                    "",
                    "Rewards:",
                    "• 8,000 Gold",
                    "• 50 Diamonds",
                    "• 5 Enchanted Books",
                    "• Spyglass",
                    "• Compass",
                    "• Clock",
                    "• Map",
                    "• 4,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "archaeologist" -> isKorean ? "고고학자와 대화" : "Talk to the Archaeologist";
            case "ancient_tablet" -> isKorean ? "고대 석판 획득" : "Obtain ancient tablet";
            case "research_lab" -> isKorean ? "고고학 연구소 방문" : "Visit archaeology lab";
            case "ancient_library" -> isKorean ? "금지된 도서관 진입" : "Enter forbidden library";
            case "librarian" -> isKorean ? "사서와 대화" : "Talk to the Librarian";
            case "old_books" -> isKorean ? "오래된 책 5권 수집" : "Collect 5 old books";
            case "search_shelves" -> isKorean ? "책장 20개 조사" : "Search 20 bookshelves";
            case "cipher_key_1" -> isKorean ? "첫 번째 암호 열쇠 획득" : "Obtain first cipher key";
            case "observatory" -> isKorean ? "고대 천문대 방문" : "Visit ancient observatory";
            case "align_telescopes" -> isKorean ? "망원경 3개 정렬" : "Align 3 telescopes";
            case "night_observation" -> isKorean ? "밤 10분간 천체 관측" : "Observe stars for 10 minutes at night";
            case "star_map" -> isKorean ? "별자리 지도 획득" : "Obtain star map";
            case "phantom_guards" -> isKorean ? "팬텀 경비 20마리 처치" : "Kill 20 phantom guards";
            case "cipher_key_2" -> isKorean ? "두 번째 암호 열쇠 획득" : "Obtain second cipher key";
            case "catacombs" -> isKorean ? "지하 묘지 진입" : "Enter underground catacombs";
            case "crypt_keepers" -> isKorean ? "묘지 관리인 30마리 처치" : "Kill 30 crypt keepers";
            case "ancient_guardians" -> isKorean ? "고대 수호자 15마리 처치" : "Kill 15 ancient guardians";
            case "open_tombs" -> isKorean ? "무덤 50개 개방" : "Open 50 tombs";
            case "burial_relics" -> isKorean ? "매장 유물 20개 수집" : "Collect 20 burial relics";
            case "cipher_key_3" -> isKorean ? "세 번째 암호 열쇠 획득" : "Obtain third cipher key";
            case "alchemist_tower" -> isKorean ? "연금술사의 탑 방문" : "Visit alchemist's tower";
            case "ghost_alchemist" -> isKorean ? "유령 연금술사와 대화" : "Talk to ghost alchemist";
            case "rare_ingredients" -> isKorean ? "희귀 재료 5개 수집" : "Collect 5 rare ingredients";
            case "mystic_dust" -> isKorean ? "신비한 가루 20개 수집" : "Collect 20 mystic dust";
            case "brew_potion" -> isKorean ? "물약 10개 제조" : "Brew 10 potions";
            case "cipher_key_4" -> isKorean ? "네 번째 암호 열쇠 획득" : "Obtain fourth cipher key";
            case "deliver_key1" -> isKorean ? "첫 번째 열쇠 전달" : "Deliver first key";
            case "deliver_key2" -> isKorean ? "두 번째 열쇠 전달" : "Deliver second key";
            case "deliver_key3" -> isKorean ? "세 번째 열쇠 전달" : "Deliver third key";
            case "deliver_key4" -> isKorean ? "네 번째 열쇠 전달" : "Deliver fourth key";
            case "decode_cipher" -> isKorean ? "암호 해독 시작" : "Begin cipher decoding";
            case "hidden_chamber" -> isKorean ? "숨겨진 방 도달" : "Reach hidden chamber";
            case "insert_keys" -> isKorean ? "열쇠 장치 4개 작동" : "Activate 4 key mechanisms";
            case "puzzle_room" -> isKorean ? "퍼즐 룸 5분간 해결" : "Solve puzzle room for 5 minutes";
            case "break_seal" -> isKorean ? "봉인 20개 파괴" : "Break 20 seals";
            case "treasure_guardian" -> isKorean ? "보물 수호자 처치" : "Defeat treasure guardian";
            case "ancient_artifact" -> isKorean ? "고대 유물 획득" : "Obtain ancient artifact";
            case "wisdom_scrolls" -> isKorean ? "지혜의 두루마리 5개 수집" : "Collect 5 wisdom scrolls";
            case "awakened_mummies" -> isKorean ? "깨어난 미라 40마리 처치" : "Kill 40 awakened mummies";
            case "escape_trap" -> isKorean ? "함정 10분간 탈출" : "Escape traps for 10 minutes";
            case "escape_route" -> isKorean ? "탈출로 통과" : "Pass through escape route";
            case "deliver_artifact" -> isKorean ? "유물 전달" : "Deliver artifact";
            case "quest_complete" -> isKorean ? "퀘스트 완료" : "Complete quest";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("ancient_cipher_dialog");

        // 시작
        dialog.addLine("고고학자",
                "드디어 찾았어! 전설로만 전해지던 고대 석판이야!",
                "Finally found it! The ancient tablet that was only known in legends!");

        dialog.addLine("고고학자",
                "하지만 이 암호는... 내 능력으로는 해독할 수 없어. 도와주겠나?",
                "But this cipher... I can't decode it with my abilities. Will you help?");

        dialog.addLine("플레이어",
                "어떻게 해독하죠?",
                "How do we decode it?");

        dialog.addLine("고고학자",
                "고대 문헌에 따르면, 4개의 열쇠가 필요해. 각각 다른 장소에 숨겨져 있지.",
                "According to ancient texts, we need 4 keys. Each hidden in different places.");

        // 도서관 사서
        dialog.addLine("사서",
                "쉿... 소리를 낮춰. 이 도서관엔 위험한 지식이 잠들어 있어.",
                "Shh... keep quiet. Dangerous knowledge sleeps in this library.");

        dialog.addLine("사서",
                "지식의 열쇠를 찾는다고? 금지된 구역의 책장을 조사해봐.",
                "Looking for the Key of Knowledge? Search the bookshelves in the forbidden section.");

        // 유령 연금술사
        dialog.addLine("유령 연금술사",
                "오랜만에 방문객이군... 변화의 열쇠를 원하나?",
                "A visitor after so long... do you seek the Key of Change?");

        dialog.addLine("유령 연금술사",
                "나의 미완성 실험을 완성시켜준다면 열쇠를 주겠네.",
                "Complete my unfinished experiment and I'll give you the key.");

        // 암호 해독
        dialog.addLine("고고학자",
                "놀라워! 모든 열쇠를 모았군! 이제 암호를 해독할 수 있어!",
                "Amazing! You've gathered all the keys! Now we can decode the cipher!");

        dialog.addLine("고고학자",
                "암호가 가리키는 곳은... 여기 연구소 지하에 숨겨진 방이야!",
                "The cipher points to... a hidden chamber beneath this lab!");

        // 함정 탈출
        dialog.addLine("플레이어",
                "함정이 작동했어요!",
                "The trap has been triggered!");

        dialog.addLine("고고학자",
                "빨리! 미라들이 깨어나고 있어! 탈출해야 해!",
                "Hurry! The mummies are awakening! We must escape!");

        // 완료
        dialog.addLine("고고학자",
                "정말 대단해! 이 유물은 고대 문명 연구에 혁명을 일으킬 거야!",
                "Truly amazing! This artifact will revolutionize ancient civilization research!");

        dialog.addLine("고고학자",
                "이 보상을 받아줘. 그리고... 다른 미스터리도 풀어줄 수 있겠나?",
                "Take these rewards. And... could you help solve other mysteries too?");

        return dialog;
    }
}