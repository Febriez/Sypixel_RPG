package com.febrie.rpg.quest;

import net.kyori.adventure.text.Component;
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
    TUTORIAL_FIRST_STEPS(QuestCategory.TUTORIAL),
    TUTORIAL_BASIC_COMBAT(QuestCategory.TUTORIAL),

    // ===== 메인 퀘스트 =====
    MAIN_HEROES_JOURNEY(QuestCategory.MAIN),
    MAIN_PATH_OF_LIGHT(QuestCategory.MAIN),
    MAIN_PATH_OF_DARKNESS(QuestCategory.MAIN),
    
    // Chapter 1: The Awakening
    MAIN_ANCIENT_PROPHECY(QuestCategory.MAIN),
    MAIN_CHOSEN_ONE(QuestCategory.MAIN),
    MAIN_FIRST_TRIAL(QuestCategory.MAIN),
    MAIN_ELEMENTAL_STONES(QuestCategory.MAIN),
    MAIN_GUARDIAN_AWAKENING(QuestCategory.MAIN),
    
    // Chapter 2: Rise of Darkness
    MAIN_SHADOW_INVASION(QuestCategory.MAIN),
    MAIN_CORRUPTED_LANDS(QuestCategory.MAIN),
    MAIN_LOST_KINGDOM(QuestCategory.MAIN),
    MAIN_ANCIENT_EVIL(QuestCategory.MAIN),
    MAIN_HEROES_ALLIANCE(QuestCategory.MAIN),
    
    // Chapter 3: The Dragon's Return
    MAIN_DRAGON_AWAKENING(QuestCategory.MAIN),
    MAIN_DRAGON_TRIALS(QuestCategory.MAIN),
    MAIN_DRAGON_PACT(QuestCategory.MAIN),
    MAIN_SKY_FORTRESS(QuestCategory.MAIN),
    MAIN_DRAGON_HEART(QuestCategory.MAIN),
    
    // Chapter 4: War of Realms
    MAIN_REALM_PORTAL(QuestCategory.MAIN),
    MAIN_VOID_INVASION(QuestCategory.MAIN),
    MAIN_REALM_DEFENDERS(QuestCategory.MAIN),
    MAIN_CHAOS_STORM(QuestCategory.MAIN),
    MAIN_DIMENSIONAL_RIFT(QuestCategory.MAIN),
    
    // Chapter 5: Final Destiny
    MAIN_GATHERING_STORM(QuestCategory.MAIN),
    MAIN_LAST_STAND(QuestCategory.MAIN),
    MAIN_FINAL_BATTLE(QuestCategory.MAIN),
    MAIN_SACRIFICE_OF_HEROES(QuestCategory.MAIN),
    MAIN_NEW_ERA(QuestCategory.MAIN),
    
    // Chapter 6: Epilogue
    MAIN_RESTORATION(QuestCategory.MAIN),
    MAIN_LEGACY_OF_HEROES(QuestCategory.MAIN),
    MAIN_ETERNAL_GUARDIAN(QuestCategory.MAIN),

    // ===== 사이드 퀘스트 =====
    SIDE_FARMERS_REQUEST(QuestCategory.SIDE),
    SIDE_COLLECT_HERBS(QuestCategory.SIDE),
    SIDE_LOST_TREASURE(QuestCategory.SIDE),
    
    // Exploration & Discovery
    SIDE_ANCIENT_RUINS(QuestCategory.SIDE),
    SIDE_HIDDEN_VALLEY(QuestCategory.SIDE),
    SIDE_MYSTERIOUS_CAVE(QuestCategory.SIDE),
    SIDE_FORGOTTEN_TEMPLE(QuestCategory.SIDE),
    SIDE_SUNKEN_CITY(QuestCategory.SIDE),
    SIDE_CRYSTAL_CAVERN(QuestCategory.SIDE),
    SIDE_ENCHANTED_FOREST(QuestCategory.SIDE),
    SIDE_DESERT_OASIS(QuestCategory.SIDE),
    SIDE_FROZEN_PEAKS(QuestCategory.SIDE),
    SIDE_VOLCANIC_DEPTHS(QuestCategory.SIDE),
    
    // NPC Stories
    SIDE_BLACKSMITH_APPRENTICE(QuestCategory.SIDE),
    SIDE_MERCHANTS_DILEMMA(QuestCategory.SIDE),
    SIDE_HEALERS_REQUEST(QuestCategory.SIDE),
    SIDE_THIEVES_GUILD(QuestCategory.SIDE),
    SIDE_ROYAL_MESSENGER(QuestCategory.SIDE),
    SIDE_LIBRARIAN_MYSTERY(QuestCategory.SIDE),
    SIDE_INNKEEPER_TROUBLE(QuestCategory.SIDE),
    SIDE_FISHERMAN_TALE(QuestCategory.SIDE),
    SIDE_MINERS_PLIGHT(QuestCategory.SIDE),
    SIDE_ALCHEMIST_EXPERIMENT(QuestCategory.SIDE),
    
    // Monster Hunting
    SIDE_WOLF_PACK_MENACE(QuestCategory.SIDE),
    SIDE_SPIDER_INFESTATION(QuestCategory.SIDE),
    SIDE_UNDEAD_UPRISING(QuestCategory.SIDE),
    SIDE_GOBLIN_RAIDERS(QuestCategory.SIDE),
    SIDE_PHANTOM_HAUNTING(QuestCategory.SIDE),
    SIDE_ELEMENTAL_CHAOS(QuestCategory.SIDE),
    SIDE_BEAST_TAMING(QuestCategory.SIDE),
    SIDE_DEMON_HUNTERS(QuestCategory.SIDE),
    SIDE_GIANT_SLAYER(QuestCategory.SIDE),
    SIDE_DRAGON_SCOUT(QuestCategory.SIDE),
    
    // Mysteries & Puzzles  
    SIDE_ANCIENT_CIPHER(QuestCategory.SIDE),
    SIDE_TIME_PARADOX(QuestCategory.SIDE),
    SIDE_MIRROR_WORLD(QuestCategory.SIDE),
    SIDE_DREAM_WALKER(QuestCategory.SIDE),
    SIDE_SOUL_FRAGMENTS(QuestCategory.SIDE),
    SIDE_MEMORY_THIEF(QuestCategory.SIDE),
    SIDE_SHADOW_REALM(QuestCategory.SIDE),
    SIDE_ASTRAL_PROJECTION(QuestCategory.SIDE),
    SIDE_CURSED_ARTIFACT(QuestCategory.SIDE),
    SIDE_ETERNAL_FLAME(QuestCategory.SIDE),

    // ===== 일일 퀘스트 =====
    DAILY_HUNTING(QuestCategory.DAILY),
    DAILY_MINING(QuestCategory.DAILY),
    DAILY_FISHING(QuestCategory.DAILY),
    DAILY_GATHERING(QuestCategory.DAILY),
    DAILY_CRAFTING(QuestCategory.DAILY),
    DAILY_DELIVERY(QuestCategory.DAILY),
    DAILY_PATROL(QuestCategory.DAILY),
    DAILY_TRAINING(QuestCategory.DAILY),
    DAILY_EXPLORATION(QuestCategory.DAILY),
    DAILY_ALCHEMY(QuestCategory.DAILY),
    DAILY_BOUNTY_HUNTER(QuestCategory.DAILY),
    DAILY_ARENA_CHAMPION(QuestCategory.DAILY),
    DAILY_TREASURE_HUNTER(QuestCategory.DAILY),
    DAILY_MERCHANT_ESCORT(QuestCategory.DAILY),
    DAILY_DUNGEON_CLEAR(QuestCategory.DAILY),
    
    // ===== 주간 퀘스트 =====
    WEEKLY_RAID_BOSS(QuestCategory.WEEKLY),
    WEEKLY_GUILD_CONTRIBUTION(QuestCategory.WEEKLY),
    WEEKLY_PVP_TOURNAMENT(QuestCategory.WEEKLY),
    WEEKLY_WORLD_BOSS(QuestCategory.WEEKLY),
    WEEKLY_RESOURCE_GATHERING(QuestCategory.WEEKLY),
    WEEKLY_ELITE_HUNTING(QuestCategory.WEEKLY),
    WEEKLY_ANCIENT_RUINS(QuestCategory.WEEKLY),
    WEEKLY_FACTION_WAR(QuestCategory.WEEKLY),
    WEEKLY_LEGENDARY_CRAFT(QuestCategory.WEEKLY),
    WEEKLY_SURVIVAL_CHALLENGE(QuestCategory.WEEKLY),
    
    // ===== 반복 퀘스트 (Repeatable) =====
    REPEAT_MONSTER_EXTERMINATION(QuestCategory.REPEATABLE),
    REPEAT_RESOURCE_COLLECTION(QuestCategory.REPEATABLE),
    REPEAT_EQUIPMENT_UPGRADE(QuestCategory.REPEATABLE),
    REPEAT_TRADE_ROUTE(QuestCategory.REPEATABLE),
    REPEAT_GUARD_DUTY(QuestCategory.REPEATABLE),
    REPEAT_SCOUT_MISSION(QuestCategory.REPEATABLE),
    REPEAT_SUPPLY_RUN(QuestCategory.REPEATABLE),
    REPEAT_RESEARCH_ASSISTANCE(QuestCategory.REPEATABLE),
    REPEAT_TRAINING_DUMMY(QuestCategory.REPEATABLE),
    REPEAT_ARTIFACT_COLLECTION(QuestCategory.REPEATABLE),
    
    // ===== 특수 퀘스트 (Special) =====
    SPECIAL_HIDDEN_CLASS(QuestCategory.SPECIAL),
    SPECIAL_LEGENDARY_WEAPON(QuestCategory.SPECIAL),
    SPECIAL_ANCIENT_POWER(QuestCategory.SPECIAL),
    SPECIAL_TIME_LIMITED(QuestCategory.SPECIAL),
    SPECIAL_SECRET_SOCIETY(QuestCategory.SPECIAL),
    SPECIAL_DIVINE_BLESSING(QuestCategory.SPECIAL),
    SPECIAL_CURSE_REMOVAL(QuestCategory.SPECIAL),
    SPECIAL_DIMENSION_TRAVELER(QuestCategory.SPECIAL),
    SPECIAL_MYTHIC_BEAST(QuestCategory.SPECIAL),
    SPECIAL_WORLD_TREE(QuestCategory.SPECIAL),
    
    // ===== 직업 퀘스트 (Class) =====
    CLASS_WARRIOR_ADVANCEMENT(QuestCategory.ADVANCEMENT),
    CLASS_MAGE_ENLIGHTENMENT(QuestCategory.ADVANCEMENT),
    CLASS_ARCHER_PRECISION(QuestCategory.ADVANCEMENT),
    CLASS_ROGUE_SHADOWS(QuestCategory.ADVANCEMENT),
    CLASS_PRIEST_DEVOTION(QuestCategory.ADVANCEMENT),
    CLASS_PALADIN_OATH(QuestCategory.ADVANCEMENT),
    CLASS_NECROMANCER_PACT(QuestCategory.ADVANCEMENT),
    CLASS_DRUID_NATURE(QuestCategory.ADVANCEMENT),
    CLASS_BERSERKER_RAGE(QuestCategory.ADVANCEMENT),
    CLASS_SUMMONER_BOND(QuestCategory.ADVANCEMENT),
    
    // ===== 시즌 퀘스트 (Seasonal) =====
    SEASON_SPRING_FESTIVAL(QuestCategory.EVENT),
    SEASON_SUMMER_SOLSTICE(QuestCategory.EVENT),
    SEASON_AUTUMN_HARVEST(QuestCategory.EVENT),
    SEASON_WINTER_FROST(QuestCategory.EVENT),
    SEASON_NEW_YEAR(QuestCategory.EVENT),
    SEASON_HALLOWEEN_NIGHT(QuestCategory.EVENT),
    SEASON_VALENTINE_LOVE(QuestCategory.EVENT),
    SEASON_EASTER_EGGS(QuestCategory.EVENT),
    SEASON_THANKSGIVING(QuestCategory.EVENT),
    SEASON_CHRISTMAS_SPIRIT(QuestCategory.EVENT),
    
    // ===== 길드 퀘스트 (Guild) =====
    GUILD_ESTABLISHMENT(QuestCategory.GUILD),
    GUILD_FORTRESS_SIEGE(QuestCategory.GUILD),
    GUILD_RESOURCE_WAR(QuestCategory.GUILD),
    GUILD_ALLIANCE_FORMATION(QuestCategory.GUILD),
    GUILD_REPUTATION_BUILDING(QuestCategory.GUILD),
    GUILD_MERCHANT_CONTRACT(QuestCategory.GUILD),
    GUILD_TERRITORY_EXPANSION(QuestCategory.GUILD),
    GUILD_DIPLOMATIC_MISSION(QuestCategory.GUILD),
    GUILD_TREASURY_HEIST(QuestCategory.GUILD),
    GUILD_LEGENDARY_ACHIEVEMENT(QuestCategory.GUILD),
    
    // ===== 탐험 퀘스트 (Exploration) =====
    EXPLORE_LOST_CONTINENT(QuestCategory.EXPLORATION),
    EXPLORE_SKY_ISLANDS(QuestCategory.EXPLORATION),
    EXPLORE_UNDERGROUND_KINGDOM(QuestCategory.EXPLORATION),
    EXPLORE_DIMENSIONAL_NEXUS(QuestCategory.EXPLORATION),
    EXPLORE_ANCIENT_LIBRARY(QuestCategory.EXPLORATION),
    EXPLORE_TITAN_REMAINS(QuestCategory.EXPLORATION),
    EXPLORE_ELEMENTAL_PLANES(QuestCategory.EXPLORATION),
    EXPLORE_VOID_FRONTIER(QuestCategory.EXPLORATION),
    EXPLORE_CELESTIAL_REALM(QuestCategory.EXPLORATION),
    EXPLORE_ABYSSAL_DEPTHS(QuestCategory.EXPLORATION),
    
    // ===== 제작 퀘스트 (Crafting) =====
    CRAFT_MASTER_BLACKSMITH(QuestCategory.CRAFTING),
    CRAFT_POTION_BREWING(QuestCategory.CRAFTING),
    CRAFT_ENCHANTMENT_MASTERY(QuestCategory.CRAFTING),
    CRAFT_RUNE_INSCRIPTION(QuestCategory.CRAFTING),
    CRAFT_JEWEL_CUTTING(QuestCategory.CRAFTING),
    CRAFT_ARMOR_FORGING(QuestCategory.CRAFTING),
    CRAFT_WEAPON_TEMPERING(QuestCategory.CRAFTING),
    CRAFT_ACCESSORY_DESIGN(QuestCategory.CRAFTING),
    CRAFT_CONSUMABLE_CREATION(QuestCategory.CRAFTING),
    CRAFT_LEGENDARY_RECIPE(QuestCategory.CRAFTING),
    
    // ===== 생활 퀘스트 (Life Skills) =====
    LIFE_MASTER_CHEF(QuestCategory.LIFE),
    LIFE_FARMING_EXPERT(QuestCategory.LIFE),
    LIFE_ANIMAL_TAMER(QuestCategory.LIFE),
    LIFE_MERCHANT_TYCOON(QuestCategory.LIFE),
    LIFE_ARCHAEOLOGIST(QuestCategory.LIFE),
    LIFE_TREASURE_APPRAISER(QuestCategory.LIFE),
    LIFE_MONSTER_RESEARCHER(QuestCategory.LIFE),
    LIFE_CARTOGRAPHER(QuestCategory.LIFE),
    LIFE_DIPLOMAT(QuestCategory.LIFE),
    LIFE_COLLECTOR(QuestCategory.LIFE),
    
    // ===== 전투 퀘스트 (Combat) =====
    COMBAT_ARENA_GLADIATOR(QuestCategory.COMBAT),
    COMBAT_BOSS_SLAYER(QuestCategory.COMBAT),
    COMBAT_SURVIVAL_EXPERT(QuestCategory.COMBAT),
    COMBAT_COMBO_MASTER(QuestCategory.COMBAT),
    COMBAT_DEFENSE_SPECIALIST(QuestCategory.COMBAT),
    COMBAT_ELEMENTAL_WARRIOR(QuestCategory.COMBAT),
    COMBAT_CRITICAL_STRIKER(QuestCategory.COMBAT),
    COMBAT_SPEED_DEMON(QuestCategory.COMBAT),
    COMBAT_TANK_DESTROYER(QuestCategory.COMBAT),
    COMBAT_ASSASSIN_SHADOW(QuestCategory.COMBAT),
    
    // ===== 스토리 브랜치 퀘스트 (Story Branch) =====
    BRANCH_LIGHT_PALADIN(QuestCategory.BRANCH),
    BRANCH_DARK_KNIGHT(QuestCategory.BRANCH),
    BRANCH_NEUTRAL_GUARDIAN(QuestCategory.BRANCH),
    BRANCH_CHAOS_BRINGER(QuestCategory.BRANCH),
    BRANCH_ORDER_KEEPER(QuestCategory.BRANCH),
    BRANCH_NATURE_PROTECTOR(QuestCategory.BRANCH),
    BRANCH_TECHNOLOGY_PIONEER(QuestCategory.BRANCH),
    BRANCH_MAGIC_SEEKER(QuestCategory.BRANCH),
    BRANCH_BALANCE_MAINTAINER(QuestCategory.BRANCH),
    BRANCH_FATE_DEFIER(QuestCategory.BRANCH);

    private final QuestCategory category;

    QuestID(@NotNull QuestCategory category) {
        this.category = category;
    }

    /**
     * 표시 이름 반환 (언어별)
     * @param player 플레이어 (언어 설정 확인용)
     * @return 번역된 퀘스트 이름
     */
    @NotNull
    public String getDisplayName(@NotNull org.bukkit.entity.Player player) {
        net.kyori.adventure.text.Component comp = Component.translatable(getNameKey());
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(comp);
    }
    
    /**
     * 표시 이름 반환 (언어 키 지정)
     * @param langKey 언어 키 (예: "ko_KR", "en_US") 
     * @return 번역된 퀘스트 이름
     */
    @NotNull
    public String getDisplayName(@NotNull String langKey) {
        // 더미 플레이어 없이는 언어를 직접 설정할 수 없으므로,
        // 간단히 키를 반환하거나 기본 영어로 처리
        return name(); // 퀘스트 ID 이름 반환
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