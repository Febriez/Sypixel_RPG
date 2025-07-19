package com.febrie.rpg.quest;

import org.jetbrains.annotations.NotNull;
import java.util.Arrays;

/**
 * 모든 퀘스트의 ID를 정의하는 Enum
 * 타입 안전성과 관리 편의성을 위해 사용
 *
 * @author Febrie
 */
public enum QuestID {

    // ===== 튜토리얼 퀘스트 =====
    TUTORIAL_FIRST_STEPS("첫 걸음", QuestCategory.TUTORIAL),
    TUTORIAL_BASIC_COMBAT("기초 전투", QuestCategory.TUTORIAL),

    // ===== 메인 퀘스트 =====
    MAIN_HEROES_JOURNEY("영웅의 여정", QuestCategory.MAIN),
    MAIN_PATH_OF_LIGHT("빛의 길", QuestCategory.MAIN),
    MAIN_PATH_OF_DARKNESS("어둠의 길", QuestCategory.MAIN),
    
    // Chapter 1: The Awakening
    MAIN_ANCIENT_PROPHECY("고대의 예언", QuestCategory.MAIN),
    MAIN_CHOSEN_ONE("선택받은 자", QuestCategory.MAIN),
    MAIN_FIRST_TRIAL("첫 번째 시험", QuestCategory.MAIN),
    MAIN_ELEMENTAL_STONES("원소의 돌", QuestCategory.MAIN),
    MAIN_GUARDIAN_AWAKENING("수호자의 각성", QuestCategory.MAIN),
    
    // Chapter 2: Rise of Darkness
    MAIN_SHADOW_INVASION("그림자의 침략", QuestCategory.MAIN),
    MAIN_CORRUPTED_LANDS("타락한 땅", QuestCategory.MAIN),
    MAIN_LOST_KINGDOM("잃어버린 왕국", QuestCategory.MAIN),
    MAIN_ANCIENT_EVIL("고대의 악", QuestCategory.MAIN),
    MAIN_HEROES_ALLIANCE("영웅들의 동맹", QuestCategory.MAIN),
    
    // Chapter 3: The Dragon's Return
    MAIN_DRAGON_AWAKENING("용의 각성", QuestCategory.MAIN),
    MAIN_DRAGON_TRIALS("용의 시련", QuestCategory.MAIN),
    MAIN_DRAGON_PACT("용의 계약", QuestCategory.MAIN),
    MAIN_SKY_FORTRESS("하늘의 요새", QuestCategory.MAIN),
    MAIN_DRAGON_HEART("용의 심장", QuestCategory.MAIN),
    
    // Chapter 4: War of Realms
    MAIN_REALM_PORTAL("차원의 문", QuestCategory.MAIN),
    MAIN_VOID_INVASION("공허의 침공", QuestCategory.MAIN),
    MAIN_REALM_DEFENDERS("차원의 수호자", QuestCategory.MAIN),
    MAIN_CHAOS_STORM("혼돈의 폭풍", QuestCategory.MAIN),
    MAIN_DIMENSIONAL_RIFT("차원의 균열", QuestCategory.MAIN),
    
    // Chapter 5: Final Destiny
    MAIN_GATHERING_STORM("다가오는 폭풍", QuestCategory.MAIN),
    MAIN_LAST_STAND("최후의 저항", QuestCategory.MAIN),
    MAIN_FINAL_BATTLE("최종 전투", QuestCategory.MAIN),
    MAIN_SACRIFICE_OF_HEROES("영웅의 희생", QuestCategory.MAIN),
    MAIN_NEW_ERA("새로운 시대", QuestCategory.MAIN),
    
    // Chapter 6: Epilogue
    MAIN_RESTORATION("복구", QuestCategory.MAIN),
    MAIN_LEGACY_OF_HEROES("영웅의 유산", QuestCategory.MAIN),
    MAIN_ETERNAL_GUARDIAN("영원한 수호자", QuestCategory.MAIN),

    // ===== 사이드 퀘스트 =====
    SIDE_FARMERS_REQUEST("농부의 부탁", QuestCategory.SIDE),
    SIDE_COLLECT_HERBS("약초 수집", QuestCategory.SIDE),
    SIDE_LOST_TREASURE("잃어버린 보물", QuestCategory.SIDE),
    
    // Exploration & Discovery
    SIDE_ANCIENT_RUINS("고대 유적 탐험", QuestCategory.SIDE),
    SIDE_HIDDEN_VALLEY("숨겨진 계곡", QuestCategory.SIDE),
    SIDE_MYSTERIOUS_CAVE("신비한 동굴", QuestCategory.SIDE),
    SIDE_FORGOTTEN_TEMPLE("잊혀진 사원", QuestCategory.SIDE),
    SIDE_SUNKEN_CITY("가라앉은 도시", QuestCategory.SIDE),
    SIDE_CRYSTAL_CAVERN("수정 동굴", QuestCategory.SIDE),
    SIDE_ENCHANTED_FOREST("마법의 숲", QuestCategory.SIDE),
    SIDE_DESERT_OASIS("사막의 오아시스", QuestCategory.SIDE),
    SIDE_FROZEN_PEAKS("얼어붙은 봉우리", QuestCategory.SIDE),
    SIDE_VOLCANIC_DEPTHS("화산의 깊이", QuestCategory.SIDE),
    
    // NPC Stories
    SIDE_BLACKSMITH_APPRENTICE("대장장이의 제자", QuestCategory.SIDE),
    SIDE_MERCHANTS_DILEMMA("상인의 딜레마", QuestCategory.SIDE),
    SIDE_HEALERS_REQUEST("치유사의 부탁", QuestCategory.SIDE),
    SIDE_THIEVES_GUILD("도둑 길드", QuestCategory.SIDE),
    SIDE_ROYAL_MESSENGER("왕실 전령", QuestCategory.SIDE),
    SIDE_LIBRARIAN_MYSTERY("사서의 미스터리", QuestCategory.SIDE),
    SIDE_INNKEEPER_TROUBLE("여관주인의 고민", QuestCategory.SIDE),
    SIDE_FISHERMAN_TALE("어부의 이야기", QuestCategory.SIDE),
    SIDE_MINERS_PLIGHT("광부의 곤경", QuestCategory.SIDE),
    SIDE_ALCHEMIST_EXPERIMENT("연금술사의 실험", QuestCategory.SIDE),
    
    // Monster Hunting
    SIDE_WOLF_PACK_MENACE("늑대 무리의 위협", QuestCategory.SIDE),
    SIDE_SPIDER_INFESTATION("거미 침입", QuestCategory.SIDE),
    SIDE_UNDEAD_UPRISING("언데드 봉기", QuestCategory.SIDE),
    SIDE_GOBLIN_RAIDERS("고블린 약탈자", QuestCategory.SIDE),
    SIDE_PHANTOM_HAUNTING("유령의 저주", QuestCategory.SIDE),
    SIDE_ELEMENTAL_CHAOS("원소의 혼돈", QuestCategory.SIDE),
    SIDE_BEAST_TAMING("야수 길들이기", QuestCategory.SIDE),
    SIDE_DEMON_HUNTERS("악마 사냥꾼", QuestCategory.SIDE),
    SIDE_GIANT_SLAYER("거인 처치자", QuestCategory.SIDE),
    SIDE_DRAGON_SCOUT("용의 정찰병", QuestCategory.SIDE),
    
    // Mysteries & Puzzles  
    SIDE_ANCIENT_CIPHER("고대 암호", QuestCategory.SIDE),
    SIDE_TIME_PARADOX("시간의 역설", QuestCategory.SIDE),
    SIDE_MIRROR_WORLD("거울 세계", QuestCategory.SIDE),
    SIDE_DREAM_WALKER("꿈의 방랑자", QuestCategory.SIDE),
    SIDE_SOUL_FRAGMENTS("영혼의 조각", QuestCategory.SIDE),
    SIDE_MEMORY_THIEF("기억 도둑", QuestCategory.SIDE),
    SIDE_SHADOW_REALM("그림자 영역", QuestCategory.SIDE),
    SIDE_ASTRAL_PROJECTION("영체 투사", QuestCategory.SIDE),
    SIDE_CURSED_ARTIFACT("저주받은 유물", QuestCategory.SIDE),
    SIDE_ETERNAL_FLAME("영원한 불꽃", QuestCategory.SIDE),

    // ===== 일일 퀘스트 =====
    DAILY_HUNTING("일일 사냥", QuestCategory.DAILY),
    DAILY_MINING("일일 채광", QuestCategory.DAILY),
    DAILY_FISHING("일일 낚시", QuestCategory.DAILY),
    DAILY_GATHERING("일일 채집", QuestCategory.DAILY),
    DAILY_CRAFTING("일일 제작", QuestCategory.DAILY),
    DAILY_DELIVERY("일일 배달", QuestCategory.DAILY),
    DAILY_PATROL("일일 순찰", QuestCategory.DAILY),
    DAILY_TRAINING("일일 훈련", QuestCategory.DAILY),
    DAILY_EXPLORATION("일일 탐험", QuestCategory.DAILY),
    DAILY_ALCHEMY("일일 연금술", QuestCategory.DAILY),
    DAILY_BOUNTY_HUNTER("일일 현상금 사냥", QuestCategory.DAILY),
    DAILY_ARENA_CHAMPION("일일 투기장 챔피언", QuestCategory.DAILY),
    DAILY_TREASURE_HUNTER("일일 보물 사냥", QuestCategory.DAILY),
    DAILY_MERCHANT_ESCORT("일일 상인 호위", QuestCategory.DAILY),
    DAILY_DUNGEON_CLEAR("일일 던전 정리", QuestCategory.DAILY),
    
    // ===== 주간 퀘스트 =====
    WEEKLY_RAID_BOSS("주간 레이드 보스", QuestCategory.WEEKLY),
    WEEKLY_GUILD_CONTRIBUTION("주간 길드 기여", QuestCategory.WEEKLY),
    WEEKLY_PVP_TOURNAMENT("주간 PVP 토너먼트", QuestCategory.WEEKLY),
    WEEKLY_WORLD_BOSS("주간 월드 보스", QuestCategory.WEEKLY),
    WEEKLY_RESOURCE_GATHERING("주간 자원 수집", QuestCategory.WEEKLY),
    WEEKLY_ELITE_HUNTING("주간 엘리트 사냥", QuestCategory.WEEKLY),
    WEEKLY_ANCIENT_RUINS("주간 고대 유적", QuestCategory.WEEKLY),
    WEEKLY_FACTION_WAR("주간 진영 전쟁", QuestCategory.WEEKLY),
    WEEKLY_LEGENDARY_CRAFT("주간 전설 제작", QuestCategory.WEEKLY),
    WEEKLY_SURVIVAL_CHALLENGE("주간 생존 도전", QuestCategory.WEEKLY),
    
    // ===== 반복 퀘스트 (Repeatable) =====
    REPEAT_MONSTER_EXTERMINATION("몬스터 토벌", QuestCategory.REPEATABLE),
    REPEAT_RESOURCE_COLLECTION("자원 수집", QuestCategory.REPEATABLE),
    REPEAT_EQUIPMENT_UPGRADE("장비 강화", QuestCategory.REPEATABLE),
    REPEAT_TRADE_ROUTE("무역로", QuestCategory.REPEATABLE),
    REPEAT_GUARD_DUTY("경비 임무", QuestCategory.REPEATABLE),
    REPEAT_SCOUT_MISSION("정찰 임무", QuestCategory.REPEATABLE),
    REPEAT_SUPPLY_RUN("보급품 운송", QuestCategory.REPEATABLE),
    REPEAT_RESEARCH_ASSISTANCE("연구 지원", QuestCategory.REPEATABLE),
    REPEAT_TRAINING_DUMMY("훈련용 더미", QuestCategory.REPEATABLE),
    REPEAT_ARTIFACT_COLLECTION("유물 수집", QuestCategory.REPEATABLE),
    
    // ===== 특수 퀘스트 (Special) =====
    SPECIAL_HIDDEN_CLASS("숨겨진 직업", QuestCategory.SPECIAL),
    SPECIAL_LEGENDARY_WEAPON("전설의 무기", QuestCategory.SPECIAL),
    SPECIAL_ANCIENT_POWER("고대의 힘", QuestCategory.SPECIAL),
    SPECIAL_TIME_LIMITED("시간 제한", QuestCategory.SPECIAL),
    SPECIAL_SECRET_SOCIETY("비밀 결사", QuestCategory.SPECIAL),
    SPECIAL_DIVINE_BLESSING("신의 축복", QuestCategory.SPECIAL),
    SPECIAL_CURSE_REMOVAL("저주 해제", QuestCategory.SPECIAL),
    SPECIAL_DIMENSION_TRAVELER("차원 여행자", QuestCategory.SPECIAL),
    SPECIAL_MYTHIC_BEAST("신화의 야수", QuestCategory.SPECIAL),
    SPECIAL_WORLD_TREE("세계수", QuestCategory.SPECIAL),
    
    // ===== 직업 퀘스트 (Class) =====
    CLASS_WARRIOR_ADVANCEMENT("전사 승급", QuestCategory.ADVANCEMENT),
    CLASS_MAGE_ENLIGHTENMENT("마법사 깨달음", QuestCategory.ADVANCEMENT),
    CLASS_ARCHER_PRECISION("궁수 정밀", QuestCategory.ADVANCEMENT),
    CLASS_ROGUE_SHADOWS("도적 그림자", QuestCategory.ADVANCEMENT),
    CLASS_PRIEST_DEVOTION("사제 헌신", QuestCategory.ADVANCEMENT),
    CLASS_PALADIN_OATH("성기사 서약", QuestCategory.ADVANCEMENT),
    CLASS_NECROMANCER_PACT("네크로맨서 계약", QuestCategory.ADVANCEMENT),
    CLASS_DRUID_NATURE("드루이드 자연", QuestCategory.ADVANCEMENT),
    CLASS_BERSERKER_RAGE("광전사 분노", QuestCategory.ADVANCEMENT),
    CLASS_SUMMONER_BOND("소환사 유대", QuestCategory.ADVANCEMENT),
    
    // ===== 시즌 퀘스트 (Seasonal) =====
    SEASON_SPRING_FESTIVAL("봄 축제", QuestCategory.EVENT),
    SEASON_SUMMER_SOLSTICE("여름 지점", QuestCategory.EVENT),
    SEASON_AUTUMN_HARVEST("가을 수확", QuestCategory.EVENT),
    SEASON_WINTER_FROST("겨울 서리", QuestCategory.EVENT),
    SEASON_NEW_YEAR("새해", QuestCategory.EVENT),
    SEASON_HALLOWEEN_NIGHT("할로윈 밤", QuestCategory.EVENT),
    SEASON_VALENTINE_LOVE("발렌타인 사랑", QuestCategory.EVENT),
    SEASON_EASTER_EGGS("부활절 달걀", QuestCategory.EVENT),
    SEASON_THANKSGIVING("추수감사절", QuestCategory.EVENT),
    SEASON_CHRISTMAS_SPIRIT("크리스마스 정신", QuestCategory.EVENT),
    
    // ===== 길드 퀘스트 (Guild) =====
    GUILD_ESTABLISHMENT("길드 설립", QuestCategory.GUILD),
    GUILD_FORTRESS_SIEGE("길드 요새 공성", QuestCategory.GUILD),
    GUILD_RESOURCE_WAR("길드 자원 전쟁", QuestCategory.GUILD),
    GUILD_ALLIANCE_FORMATION("길드 동맹 결성", QuestCategory.GUILD),
    GUILD_REPUTATION_BUILDING("길드 명성 쌓기", QuestCategory.GUILD),
    GUILD_MERCHANT_CONTRACT("길드 상인 계약", QuestCategory.GUILD),
    GUILD_TERRITORY_EXPANSION("길드 영토 확장", QuestCategory.GUILD),
    GUILD_DIPLOMATIC_MISSION("길드 외교 임무", QuestCategory.GUILD),
    GUILD_TREASURY_HEIST("길드 금고 강탈", QuestCategory.GUILD),
    GUILD_LEGENDARY_ACHIEVEMENT("길드 전설 업적", QuestCategory.GUILD),
    
    // ===== 탐험 퀘스트 (Exploration) =====
    EXPLORE_LOST_CONTINENT("잃어버린 대륙", QuestCategory.EXPLORATION),
    EXPLORE_SKY_ISLANDS("하늘 섬", QuestCategory.EXPLORATION),
    EXPLORE_UNDERGROUND_KINGDOM("지하 왕국", QuestCategory.EXPLORATION),
    EXPLORE_DIMENSIONAL_NEXUS("차원 결절점", QuestCategory.EXPLORATION),
    EXPLORE_ANCIENT_LIBRARY("고대 도서관", QuestCategory.EXPLORATION),
    EXPLORE_TITAN_REMAINS("타이탄 유적", QuestCategory.EXPLORATION),
    EXPLORE_ELEMENTAL_PLANES("원소 차원", QuestCategory.EXPLORATION),
    EXPLORE_VOID_FRONTIER("공허 개척지", QuestCategory.EXPLORATION),
    EXPLORE_CELESTIAL_REALM("천상계", QuestCategory.EXPLORATION),
    EXPLORE_ABYSSAL_DEPTHS("심연의 깊이", QuestCategory.EXPLORATION),
    
    // ===== 제작 퀘스트 (Crafting) =====
    CRAFT_MASTER_BLACKSMITH("대장장이 마스터", QuestCategory.CRAFTING),
    CRAFT_POTION_BREWING("물약 양조", QuestCategory.CRAFTING),
    CRAFT_ENCHANTMENT_MASTERY("인챈트 숙달", QuestCategory.CRAFTING),
    CRAFT_RUNE_INSCRIPTION("룬 각인", QuestCategory.CRAFTING),
    CRAFT_JEWEL_CUTTING("보석 세공", QuestCategory.CRAFTING),
    CRAFT_ARMOR_FORGING("갑옷 단조", QuestCategory.CRAFTING),
    CRAFT_WEAPON_TEMPERING("무기 담금질", QuestCategory.CRAFTING),
    CRAFT_ACCESSORY_DESIGN("액세서리 디자인", QuestCategory.CRAFTING),
    CRAFT_CONSUMABLE_CREATION("소모품 제작", QuestCategory.CRAFTING),
    CRAFT_LEGENDARY_RECIPE("전설 레시피", QuestCategory.CRAFTING),
    
    // ===== 생활 퀘스트 (Life Skills) =====
    LIFE_MASTER_CHEF("요리 달인", QuestCategory.LIFE),
    LIFE_FARMING_EXPERT("농사 전문가", QuestCategory.LIFE),
    LIFE_ANIMAL_TAMER("동물 조련사", QuestCategory.LIFE),
    LIFE_MERCHANT_TYCOON("상인 거물", QuestCategory.LIFE),
    LIFE_ARCHAEOLOGIST("고고학자", QuestCategory.LIFE),
    LIFE_TREASURE_APPRAISER("보물 감정사", QuestCategory.LIFE),
    LIFE_MONSTER_RESEARCHER("몬스터 연구가", QuestCategory.LIFE),
    LIFE_CARTOGRAPHER("지도 제작자", QuestCategory.LIFE),
    LIFE_DIPLOMAT("외교관", QuestCategory.LIFE),
    LIFE_COLLECTOR("수집가", QuestCategory.LIFE),
    
    // ===== 전투 퀘스트 (Combat) =====
    COMBAT_ARENA_GLADIATOR("투기장 검투사", QuestCategory.COMBAT),
    COMBAT_BOSS_SLAYER("보스 처치자", QuestCategory.COMBAT),
    COMBAT_SURVIVAL_EXPERT("생존 전문가", QuestCategory.COMBAT),
    COMBAT_COMBO_MASTER("콤보 마스터", QuestCategory.COMBAT),
    COMBAT_DEFENSE_SPECIALIST("방어 전문가", QuestCategory.COMBAT),
    COMBAT_ELEMENTAL_WARRIOR("원소 전사", QuestCategory.COMBAT),
    COMBAT_CRITICAL_STRIKER("치명타 공격자", QuestCategory.COMBAT),
    COMBAT_SPEED_DEMON("스피드 데몬", QuestCategory.COMBAT),
    COMBAT_TANK_DESTROYER("탱크 파괴자", QuestCategory.COMBAT),
    COMBAT_ASSASSIN_SHADOW("암살자의 그림자", QuestCategory.COMBAT),
    
    // ===== 스토리 브랜치 퀘스트 (Story Branch) =====
    BRANCH_LIGHT_PALADIN("빛의 성기사", QuestCategory.BRANCH),
    BRANCH_DARK_KNIGHT("어둠의 기사", QuestCategory.BRANCH),
    BRANCH_NEUTRAL_GUARDIAN("중립 수호자", QuestCategory.BRANCH),
    BRANCH_CHAOS_BRINGER("혼돈의 인도자", QuestCategory.BRANCH),
    BRANCH_ORDER_KEEPER("질서의 수호자", QuestCategory.BRANCH),
    BRANCH_NATURE_PROTECTOR("자연의 보호자", QuestCategory.BRANCH),
    BRANCH_TECHNOLOGY_PIONEER("기술의 개척자", QuestCategory.BRANCH),
    BRANCH_MAGIC_SEEKER("마법의 탐구자", QuestCategory.BRANCH),
    BRANCH_BALANCE_MAINTAINER("균형의 유지자", QuestCategory.BRANCH),
    BRANCH_FATE_DEFIER("운명의 도전자", QuestCategory.BRANCH);

    private final String displayName;
    private final QuestCategory category;

    QuestID(@NotNull String displayName, @NotNull QuestCategory category) {
        this.displayName = displayName;
        this.category = category;
    }

    /**
     * 표시 이름 반환
     */
    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 카테고리 반환
     */
    @NotNull
    public QuestCategory getCategory() {
        return category;
    }

    /**
     * 번역 키 반환
     */
    @NotNull
    public String getNameKey() {
        return "quest." + name().toLowerCase().replace("_", ".") + ".name";
    }

    /**
     * 설명 번역 키 반환
     */
    @NotNull
    public String getDescriptionKey() {
        return "quest." + name().toLowerCase().replace("_", ".") + ".description";
    }

    /**
     * 카테고리별 퀘스트 ID 반환
     */
    @NotNull
    public static QuestID[] getByCategory(@NotNull QuestCategory category) {
        return Arrays.stream(values())
                .filter(id -> id.category == category)
                .toArray(QuestID[]::new);
    }

    /**
     * 퀘스트가 일일 퀘스트인지 확인
     */
    public boolean isDaily() {
        return category == QuestCategory.DAILY;
    }

    /**
     * 퀘스트가 주간 퀘스트인지 확인
     */
    public boolean isWeekly() {
        return category == QuestCategory.WEEKLY;
    }

    /**
     * 퀘스트가 이벤트 퀘스트인지 확인
     */
    public boolean isEvent() {
        return category == QuestCategory.EVENT;
    }
}