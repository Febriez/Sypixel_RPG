package com.febrie.rpg.quest.registry;

import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.impl.daily.DailyHuntingQuest;
import com.febrie.rpg.quest.impl.daily.DailyMiningQuest;
import com.febrie.rpg.quest.impl.daily.DailyFishingQuest;
import com.febrie.rpg.quest.impl.main.HeroesJourneyQuest;
import com.febrie.rpg.quest.impl.main.PathOfDarknessQuest;
import com.febrie.rpg.quest.impl.main.PathOfLightQuest;
import com.febrie.rpg.quest.impl.main.GuardianAwakeningQuest;
import com.febrie.rpg.quest.impl.tutorial.BasicCombatQuest;
import com.febrie.rpg.quest.impl.tutorial.FirstStepsQuest;
import com.febrie.rpg.quest.impl.side.*;
import com.febrie.rpg.quest.impl.main.chapter1.*;
import com.febrie.rpg.quest.impl.main.chapter2.*;
import com.febrie.rpg.quest.impl.main.chapter3.*;
import com.febrie.rpg.quest.impl.main.chapter4.*;
import com.febrie.rpg.quest.impl.main.chapter5.*;
import com.febrie.rpg.quest.impl.main.chapter6.*;
// import com.febrie.rpg.quest.impl.weekly.*;
// import com.febrie.rpg.quest.impl.exploration.*;
// import com.febrie.rpg.quest.impl.combat.*;
import com.febrie.rpg.quest.impl.crafting.*;
// import com.febrie.rpg.quest.impl.guild.*;
// import com.febrie.rpg.quest.impl.life.*;
// import com.febrie.rpg.quest.impl.special.*;
// import com.febrie.rpg.quest.impl.seasonal.*;
import com.febrie.rpg.quest.impl.event.*;
import com.febrie.rpg.quest.impl.clazz.*;
// import com.febrie.rpg.quest.impl.advancement.*;
import com.febrie.rpg.quest.impl.branch.*;
// import com.febrie.rpg.quest.impl.repeatable.*;
import com.febrie.rpg.quest.impl.daily.*;
import com.febrie.rpg.util.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 퀘스트 레지스트리
 * QuestID와 실제 Quest 구현체를 매핑
 *
 * @author Febrie
 */
public class QuestRegistry {

    private static final QuestRegistry INSTANCE = new QuestRegistry();
    private static final Map<QuestID, Supplier<Quest>> questSuppliers = new EnumMap<>(QuestID.class);
    
    /**
     * 싱글톤 인스턴스 반환
     */
    @NotNull
    public static QuestRegistry getInstance() {
        return INSTANCE;
    }

    static {
        // Only register quests that compile successfully
        // TODO: Restore other quest registrations after fixing compilation errors
        
        // 튜토리얼 퀘스트
        register(QuestID.TUTORIAL_FIRST_STEPS, FirstStepsQuest::new);
        register(QuestID.TUTORIAL_BASIC_COMBAT, BasicCombatQuest::new);

        // 메인 퀘스트
        register(QuestID.MAIN_HEROES_JOURNEY, HeroesJourneyQuest::new);
        register(QuestID.MAIN_PATH_OF_LIGHT, PathOfLightQuest::new);
        register(QuestID.MAIN_PATH_OF_DARKNESS, PathOfDarknessQuest::new);

        // 데일리 퀘스트 (working ones only)
        register(QuestID.DAILY_HUNTING, DailyHuntingQuest::new);
        register(QuestID.DAILY_MINING, DailyMiningQuest::new);
        register(QuestID.DAILY_FISHING, DailyFishingQuest::new);
        register(QuestID.DAILY_GATHERING, DailyGatheringQuest::new);
        register(QuestID.DAILY_CRAFTING, DailyCraftingQuest::new);
        register(QuestID.DAILY_DELIVERY, DailyDeliveryQuest::new);
        register(QuestID.DAILY_EXPLORATION, DailyExplorationQuest::new);
        register(QuestID.DAILY_BOUNTY_HUNTER, DailyBountyHunterQuest::new);

        // Chapter 1 퀘스트
        register(QuestID.MAIN_ANCIENT_PROPHECY, AncientProphecyQuest::new);
        register(QuestID.MAIN_CHOSEN_ONE, ChosenOneQuest::new);
        register(QuestID.MAIN_ELEMENTAL_STONES, ElementalStonesQuest::new);
        register(QuestID.MAIN_FIRST_TRIAL, FirstTrialQuest::new);
        register(QuestID.MAIN_GUARDIAN_AWAKENING, GuardianAwakeningQuest::new);
        
        // Chapter 2 퀘스트
        register(QuestID.MAIN_SHADOW_INVASION, ShadowInvasionQuest::new);
        register(QuestID.MAIN_CORRUPTED_LANDS, CorruptedLandsQuest::new);
        register(QuestID.MAIN_LOST_KINGDOM, LostKingdomQuest::new);
        register(QuestID.MAIN_ANCIENT_EVIL, AncientEvilQuest::new);
        register(QuestID.MAIN_HEROES_ALLIANCE, HeroesAllianceQuest::new);

        // Chapter 3 퀘스트
        register(QuestID.MAIN_DRAGON_AWAKENING, DragonAwakeningQuest::new);
        register(QuestID.MAIN_DRAGON_TRIALS, DragonTrialsQuest::new);
        register(QuestID.MAIN_DRAGON_PACT, DragonPactQuest::new);
        register(QuestID.MAIN_SKY_FORTRESS, SkyFortressQuest::new);
        register(QuestID.MAIN_DRAGON_HEART, DragonHeartQuest::new);

        // Chapter 4 퀘스트
        register(QuestID.MAIN_REALM_PORTAL, RealmPortalQuest::new);
        register(QuestID.MAIN_VOID_INVASION, VoidInvasionQuest::new);
        register(QuestID.MAIN_REALM_DEFENDERS, RealmDefendersQuest::new);
        register(QuestID.MAIN_CHAOS_STORM, ChaosStormQuest::new);
        register(QuestID.MAIN_DIMENSIONAL_RIFT, DimensionalRiftQuest::new);
        
        // Chapter 5 퀘스트
        register(QuestID.MAIN_GATHERING_STORM, GatheringStormQuest::new);
        register(QuestID.MAIN_LAST_STAND, LastStandQuest::new);
        register(QuestID.MAIN_FINAL_BATTLE, FinalBattleQuest::new);
        register(QuestID.MAIN_SACRIFICE_OF_HEROES, SacrificeOfHeroesQuest::new);
        register(QuestID.MAIN_NEW_ERA, NewEraQuest::new);

        // Chapter 6 퀘스트
        register(QuestID.MAIN_RESTORATION, RestorationQuest::new);
        register(QuestID.MAIN_LEGACY_OF_HEROES, LegacyOfHeroesQuest::new);
        register(QuestID.MAIN_ETERNAL_GUARDIAN, EternalGuardianQuest::new);

        // 클래스 퀘스트 (working ones only)
        register(QuestID.CLASS_WARRIOR_ADVANCEMENT, WarriorAdvancementQuest::new);

        // 브랜치 퀘스트 (working ones only)  
        register(QuestID.BRANCH_LIGHT_PALADIN, LightPaladinQuest::new);

        // 사이드 퀘스트
        register(QuestID.SIDE_BLACKSMITH_APPRENTICE, BlacksmithApprenticeQuest::new);
        register(QuestID.SIDE_COLLECT_HERBS, CollectHerbsQuest::new);
        register(QuestID.SIDE_FARMERS_REQUEST, FarmersRequestQuest::new);
        register(QuestID.SIDE_LOST_TREASURE, LostTreasureQuest::new);
        register(QuestID.SIDE_SUNKEN_CITY, SunkenCityQuest::new);
        register(QuestID.SIDE_THIEVES_GUILD, ThievesGuildQuest::new);
        register(QuestID.SIDE_VOLCANIC_DEPTHS, VolcanicDepthsQuest::new);
        register(QuestID.SIDE_ALCHEMIST_EXPERIMENT, AlchemistExperimentQuest::new);
        register(QuestID.SIDE_ANCIENT_RUINS, AncientRuinsQuest::new);
        register(QuestID.SIDE_CRYSTAL_CAVERN, CrystalCavernQuest::new);
        register(QuestID.SIDE_DESERT_OASIS, DesertOasisQuest::new);
        register(QuestID.SIDE_ENCHANTED_FOREST, EnchantedForestQuest::new);
        register(QuestID.SIDE_FISHERMAN_TALE, FishermanTaleQuest::new);
        register(QuestID.SIDE_FORGOTTEN_TEMPLE, ForgottenTempleQuest::new);
        register(QuestID.SIDE_FROZEN_PEAKS, FrozenPeaksQuest::new);
        register(QuestID.SIDE_HEALERS_REQUEST, HealersRequestQuest::new);
        register(QuestID.SIDE_HIDDEN_VALLEY, HiddenValleyQuest::new);
        register(QuestID.SIDE_INNKEEPER_TROUBLE, InnkeeperTroubleQuest::new);
        register(QuestID.SIDE_LIBRARIAN_MYSTERY, LibrarianMysteryQuest::new);
        register(QuestID.SIDE_MERCHANTS_DILEMMA, MerchantsDilemmaQuest::new);
        register(QuestID.SIDE_MINERS_PLIGHT, MinersPlightQuest::new);
        register(QuestID.SIDE_MYSTERIOUS_CAVE, MysteriousCaveQuest::new);
        register(QuestID.SIDE_ROYAL_MESSENGER, RoyalMessengerQuest::new);

        // Event 퀘스트 - TODO: Check if this quest ID exists
        // register(QuestID.EVENT_HALLOWEEN_NIGHT, HalloweenNightQuest::new);

        // All other quest categories are temporarily disabled due to compilation errors
        // They can be re-enabled after fixing missing objective classes and other issues
    }

    /**
     * 퀘스트 공급자 등록
     */
    private static void register(@NotNull QuestID id, @NotNull Supplier<Quest> supplier) {
        questSuppliers.put(id, supplier);
        LogUtil.debug("퀘스트 등록됨: " + id.name());
    }

    /**
     * 퀘스트 인스턴스 생성
     */
    @Nullable
    public Quest getQuest(@NotNull QuestID id) {
        Supplier<Quest> supplier = questSuppliers.get(id);
        if (supplier == null) {
            LogUtil.error("등록되지 않은 퀘스트: " + id.name());
            return null;
        }
        
        try {
            return supplier.get();
        } catch (Exception e) {
            LogUtil.error("퀘스트 생성 실패: " + id.name(), e);
            return null;
        }
    }

    /**
     * 모든 등록된 퀘스트 ID 반환
     */
    @NotNull
    public Set<QuestID> getRegisteredQuests() {
        return questSuppliers.keySet();
    }

    /**
     * 퀘스트가 등록되어 있는지 확인
     */
    public boolean hasQuest(@NotNull QuestID id) {
        return questSuppliers.containsKey(id);
    }

    /**
     * 등록된 퀘스트 수
     */
    public int getQuestCount() {
        return questSuppliers.size();
    }

    /**
     * 모든 퀘스트 생성하여 맵으로 반환
     */
    @NotNull
    public static Map<QuestID, Quest> createAllQuests() {
        Map<QuestID, Quest> allQuests = new EnumMap<>(QuestID.class);
        for (Map.Entry<QuestID, Supplier<Quest>> entry : questSuppliers.entrySet()) {
            try {
                Quest quest = entry.getValue().get();
                if (quest != null) {
                    allQuests.put(entry.getKey(), quest);
                }
            } catch (Exception e) {
                LogUtil.error("퀘스트 생성 실패: " + entry.getKey().name(), e);
            }
        }
        return allQuests;
    }

    /**
     * 구현된 퀘스트 수 반환
     */
    public static int getImplementedCount() {
        return questSuppliers.size();
    }
}