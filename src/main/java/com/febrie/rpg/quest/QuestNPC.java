package com.febrie.rpg.quest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Enum containing all NPCs used in quest objectives
 * This centralizes NPC management and makes it easier to track and extend quest NPCs
 */
public enum QuestNPC {
    // Tutorial NPCs
    MERCHANT(1, "Merchant", "상인", QuestCategory.TUTORIAL),
    
    // Main Story Chapter 1 NPCs
    ANCIENT_ELDER(101, "Ancient Elder", "고대의 장로", QuestCategory.MAIN),
    TRIAL_TRAINER(102, "Trial Trainer", "시련의 훈련관", QuestCategory.MAIN),
    ELEMENTAL_SAGE(103, "Elemental Sage", "원소의 현자", QuestCategory.MAIN),
    
    // Main Story Chapter 2 NPCs
    SCOUT(121, "Scout", "정찰병", QuestCategory.MAIN),
    DEFENSE_COMMANDER(122, "Defense Commander", "방어 사령관", QuestCategory.MAIN),
    SHADOW_GENERAL(123, "Shadow General", "그림자 장군", QuestCategory.MAIN),
    
    // Main Story Chapter 3 NPCs
    DRAGON_SAGE(124, "Dragon Sage", "용의 현자", QuestCategory.MAIN),
    SLEEPING_DRAGON(125, "Sleeping Dragon", "잠든 고대 용", QuestCategory.MAIN),
    
    // Main Story Special NPCs
    ANCIENT_SCHOLAR(110, "Ancient Scholar", "고대 학자", QuestCategory.MAIN),
    SLEEPING_GUARDIAN(111, "Sleeping Guardian", "잠든 수호자", QuestCategory.MAIN),
    AWAKENED_GUARDIAN(111, "Awakened Guardian", "깨어난 수호자", QuestCategory.MAIN), // Same ID, different state
    
    // Combat Quest NPCs
    ARENA_MASTER(107, "Arena Master", "투기장 관리인", QuestCategory.COMBAT),
    CURRENT_CHAMPION(108, "Current Champion", "현 챔피언", QuestCategory.COMBAT),
    
    // Weekly Quest NPCs
    RAID_COMMANDER(104, "Raid Commander", "레이드 사령관", QuestCategory.WEEKLY),
    WORLD_HERALD(160, "World Herald", "월드 전령", QuestCategory.WEEKLY),
    
    // Daily Quest NPCs
    GATHERING_FOREMAN(105, "Gathering Foreman", "채집 감독관", QuestCategory.DAILY),
    BOUNTY_OFFICER(150, "Bounty Officer", "현상금 담당관", QuestCategory.DAILY),
    INFORMANT(151, "Informant", "정보원", QuestCategory.DAILY),
    EXPLORER_GUILD_MASTER(200, "Explorer Guild Master", "탐험가 길드장", QuestCategory.DAILY),
    DAILY_BLACKSMITH(230, "Blacksmith", "대장장이", QuestCategory.DAILY),
    DELIVERY_MASTER(240, "Delivery Master", "배달부 마스터", QuestCategory.DAILY),
    BAKER(241, "Baker", "제빵사", QuestCategory.DAILY),
    PHARMACIST(242, "Pharmacist", "약사", QuestCategory.DAILY),
    DELIVERY_BLACKSMITH(243, "Blacksmith", "대장장이", QuestCategory.DAILY),
    LIBRARIAN(244, "Librarian", "사서", QuestCategory.DAILY),
    FARMER(245, "Farmer", "농부", QuestCategory.DAILY),
    GUARD(246, "Guard", "경비병", QuestCategory.DAILY),
    
    // Guild Quest NPCs
    GUILD_REGISTRAR(116, "Guild Registrar", "길드 등록관", QuestCategory.GUILD),
    FIRST_APPLICANT(117, "First Applicant", "첫 번째 지원자", QuestCategory.GUILD),
    SECOND_APPLICANT(118, "Second Applicant", "두 번째 지원자", QuestCategory.GUILD),
    THIRD_APPLICANT(119, "Third Applicant", "세 번째 지원자", QuestCategory.GUILD),
    SIEGE_COMMANDER(170, "Siege Commander", "공성 사령관", QuestCategory.GUILD),
    FORTRESS_MANAGER(171, "Fortress Manager", "요새 관리인", QuestCategory.GUILD),
    
    // Event Quest NPCs
    PUMPKIN_KING(180, "Pumpkin King", "호박 왕", QuestCategory.EVENT),
    WITCH(181, "Witch", "마녀", QuestCategory.EVENT),
    PARTY_HOST(182, "Party Host", "파티 주최자", QuestCategory.EVENT),
    
    // Side Quest NPCs
    TREASURE_GUARDIAN(2, "Treasure Guardian", "보물 수호자", QuestCategory.SIDE),
    VILLAGE_BLACKSMITH(112, "Village Blacksmith", "마을 대장장이", QuestCategory.SIDE),
    ARCHAEOLOGIST(190, "Archaeologist", "고고학자", QuestCategory.SIDE),
    SIDE_LIBRARIAN(191, "Librarian", "사서", QuestCategory.SIDE),
    GHOST_ALCHEMIST(192, "Ghost Alchemist", "유령 연금술사", QuestCategory.SIDE),
    
    // Special Quest NPCs
    SHADOW_MASTER(114, "Shadow Master", "그림자 마스터", QuestCategory.SPECIAL),
    SPY_MERCHANT(115, "Spy Merchant", "정보상", QuestCategory.SPECIAL),
    ANCIENT_BLACKSMITH(130, "Ancient Blacksmith", "고대 대장장이", QuestCategory.SPECIAL),
    DRAGON_KEEPER(131, "Dragon Keeper", "용의 수호자", QuestCategory.SPECIAL),
    ANCIENT_SPIRIT(220, "Ancient Spirit", "고대 영혼", QuestCategory.SPECIAL),
    WAR_GOD_ALTAR(221, "War God Altar", "전쟁신 제단", QuestCategory.SPECIAL),
    WISDOM_GOD_ALTAR(222, "Wisdom God Altar", "지혜신 제단", QuestCategory.SPECIAL),
    NATURE_GOD_ALTAR(223, "Nature God Altar", "자연신 제단", QuestCategory.SPECIAL),
    DEATH_GOD_ALTAR(224, "Death God Altar", "죽음신 제단", QuestCategory.SPECIAL),
    TIME_GOD_ALTAR(225, "Time God Altar", "시간신 제단", QuestCategory.SPECIAL),
    BEAST_SCHOLAR(240, "Beast Scholar", "신수 학자", QuestCategory.SPECIAL),
    AZURE_DRAGON(241, "Azure Dragon", "청룡", QuestCategory.SPECIAL),
    WHITE_TIGER(242, "White Tiger", "백호", QuestCategory.SPECIAL),
    VERMILLION_BIRD(243, "Vermillion Bird", "주작", QuestCategory.SPECIAL),
    BLACK_TORTOISE(244, "Black Tortoise", "현무", QuestCategory.SPECIAL),
    WORLD_TREE_GUARDIAN(250, "World Tree Guardian", "세계수 수호자", QuestCategory.SPECIAL),
    DYING_TREE(251, "Dying Tree", "죽어가는 세계수", QuestCategory.SPECIAL),
    REVIVED_TREE(251, "Revived Tree", "부활한 세계수", QuestCategory.SPECIAL), // Same ID, different state
    WELL_KEEPER(252, "Well Keeper", "우물 지기", QuestCategory.SPECIAL),
    DIMENSION_SAGE(260, "Dimension Sage", "차원 현자", QuestCategory.SPECIAL),
    
    // Advancement Quest NPCs
    PALADIN_MENTOR(140, "Paladin Mentor", "성기사 스승", QuestCategory.ADVANCEMENT),
    JUDGE_NPC(141, "Judge NPC", "정의의 심판관", QuestCategory.ADVANCEMENT),
    HOLY_WEAPONSMITH(142, "Holy Weaponsmith", "신성 대장장이", QuestCategory.ADVANCEMENT),
    ARCHMAGE_MENTOR(210, "Archmage Mentor", "대마법사 스승", QuestCategory.ADVANCEMENT),
    FIRE_ELEMENTAL(211, "Fire Elemental", "불의 정령", QuestCategory.ADVANCEMENT),
    WATER_ELEMENTAL(212, "Water Elemental", "물의 정령", QuestCategory.ADVANCEMENT),
    EARTH_ELEMENTAL(213, "Earth Elemental", "대지의 정령", QuestCategory.ADVANCEMENT),
    AIR_ELEMENTAL(214, "Air Elemental", "바람의 정령", QuestCategory.ADVANCEMENT),
    KNOWLEDGE_KEEPER(215, "Knowledge Keeper", "지식의 수호자", QuestCategory.ADVANCEMENT),
    RIVAL_MAGE(216, "Rival Mage", "라이벌 마법사", QuestCategory.ADVANCEMENT),
    
    // Life/Crafting Quest NPCs
    ARCHAEOLOGIST_HENRY(106, "Archaeologist Henry", "고고학자 헨리", QuestCategory.LIFE),
    BLACKSMITH_MASTER(109, "Blacksmith Master", "대장장이 마스터", QuestCategory.LIFE),
    MASTER_CHEF(120, "Master Chef", "요리 마스터", QuestCategory.LIFE),
    
    // Legacy NPCs (from broken quests - will be remapped)
    GUARD_CAPTAIN(1001, "Guard Captain", "경비대장", QuestCategory.REPEATABLE),
    FESTIVAL_HOST(1002, "Festival Host", "축제 진행자", QuestCategory.SEASONAL),
    PALADIN_MASTER(1003, "Paladin Master", "성기사단장", QuestCategory.BRANCH),
    WARRIOR_MASTER(1004, "Warrior Master", "전사 대가", QuestCategory.BRANCH);
    
    private final int npcId;
    private final String englishName;
    private final String koreanName;
    private final QuestCategory defaultCategory;
    
    QuestNPC(int npcId, @NotNull String englishName, @NotNull String koreanName, @NotNull QuestCategory defaultCategory) {
        this.npcId = npcId;
        this.englishName = englishName;
        this.koreanName = koreanName;
        this.defaultCategory = defaultCategory;
    }
    
    public int getNpcId() {
        return npcId;
    }
    
    @NotNull
    public String getEnglishName() {
        return englishName;
    }
    
    @NotNull
    public String getKoreanName() {
        return koreanName;
    }
    
    @NotNull
    public String getDisplayName(boolean isKorean) {
        return isKorean ? koreanName : englishName;
    }
    
    @NotNull
    public QuestCategory getDefaultCategory() {
        return defaultCategory;
    }
    
    /**
     * Find QuestNPC by NPC ID
     * @param npcId The Citizens NPC ID
     * @return The QuestNPC or null if not found
     */
    @Nullable
    public static QuestNPC getByNpcId(int npcId) {
        for (QuestNPC npc : values()) {
            if (npc.npcId == npcId) {
                return npc;
            }
        }
        return null;
    }
    
    /**
     * Find QuestNPC by name (English or Korean)
     * @param name The NPC name to search
     * @return The QuestNPC or null if not found
     */
    @Nullable
    public static QuestNPC getByName(@NotNull String name) {
        for (QuestNPC npc : values()) {
            if (npc.englishName.equalsIgnoreCase(name) || npc.koreanName.equals(name)) {
                return npc;
            }
        }
        return null;
    }
    
    /**
     * Check if an NPC ID is registered as a quest NPC
     * @param npcId The Citizens NPC ID
     * @return true if the NPC is a quest NPC
     */
    public static boolean isQuestNPC(int npcId) {
        return getByNpcId(npcId) != null;
    }
    
    /**
     * Register this NPC with the Citizens trait system
     * This should be called during plugin initialization
     */
    public void registerTrait() {
        // This will be implemented to register the NPC with the trait system
        // The implementation depends on how traits are managed in the project
    }
}