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
import com.febrie.rpg.quest.impl.tutorial.BasicCombatQuest;
import com.febrie.rpg.quest.impl.tutorial.FirstStepsQuest;
import com.febrie.rpg.quest.impl.side.FarmersRequestQuest;
import com.febrie.rpg.quest.impl.side.CollectHerbsQuest;
import com.febrie.rpg.quest.impl.side.LostTreasureQuest;
import com.febrie.rpg.quest.impl.main.chapter1.*;
import com.febrie.rpg.quest.impl.main.chapter2.*;
import com.febrie.rpg.quest.impl.main.chapter3.*;
import com.febrie.rpg.quest.impl.weekly.*;
import com.febrie.rpg.quest.impl.exploration.*;
import com.febrie.rpg.quest.impl.combat.*;
import com.febrie.rpg.quest.impl.crafting.*;
import com.febrie.rpg.quest.impl.guild.*;
import com.febrie.rpg.quest.impl.life.*;
import com.febrie.rpg.quest.impl.special.*;
import com.febrie.rpg.quest.impl.seasonal.*;
import com.febrie.rpg.quest.impl.clazz.*;
import com.febrie.rpg.quest.impl.repeatable.*;
import com.febrie.rpg.quest.impl.daily.*;
import com.febrie.rpg.util.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import net.kyori.adventure.text.Component;
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
        // 튜토리얼 퀘스트
        register(QuestID.TUTORIAL_FIRST_STEPS, FirstStepsQuest::new);
        register(QuestID.TUTORIAL_BASIC_COMBAT, BasicCombatQuest::new);

        // 메인 퀘스트
        register(QuestID.MAIN_HEROES_JOURNEY, HeroesJourneyQuest::new);
        register(QuestID.MAIN_PATH_OF_LIGHT, PathOfLightQuest::new);
        register(QuestID.MAIN_PATH_OF_DARKNESS, PathOfDarknessQuest::new);

        // 사이드 퀘스트
        register(QuestID.SIDE_FARMERS_REQUEST, FarmersRequestQuest::new);
        register(QuestID.SIDE_COLLECT_HERBS, CollectHerbsQuest::new);
        register(QuestID.SIDE_LOST_TREASURE, LostTreasureQuest::new);

        // 일일 퀘스트
        register(QuestID.DAILY_HUNTING, DailyHuntingQuest::new);
        register(QuestID.DAILY_MINING, DailyMiningQuest::new);
        register(QuestID.DAILY_FISHING, DailyFishingQuest::new);
        register(QuestID.DAILY_GATHERING, DailyGatheringQuest::new);

        // 주간 퀘스트
        register(QuestID.WEEKLY_RAID_BOSS, WeeklyRaidBossQuest::new);

        // Chapter 1 메인 퀘스트
        register(QuestID.MAIN_ANCIENT_PROPHECY, AncientProphecyQuest::new);
        register(QuestID.MAIN_CHOSEN_ONE, ChosenOneQuest::new);
        register(QuestID.MAIN_FIRST_TRIAL, FirstTrialQuest::new);

        // Chapter 2 메인 퀘스트
        register(QuestID.MAIN_SHADOW_INVASION, ShadowInvasionQuest::new);

        // Chapter 3 메인 퀘스트
        register(QuestID.MAIN_DRAGON_AWAKENING, DragonAwakeningQuest::new);

        // 탐험 사이드 퀘스트
        register(QuestID.SIDE_ANCIENT_RUINS, AncientRuinsQuest::new);

        // 전투 퀘스트
        register(QuestID.COMBAT_ARENA_GLADIATOR, ArenaGladiatorQuest::new);

        // 제작 퀘스트
        register(QuestID.CRAFT_MASTER_BLACKSMITH, MasterBlacksmithQuest::new);

        // 길드 퀘스트
        register(QuestID.GUILD_ESTABLISHMENT, GuildEstablishmentQuest::new);

        // 생활 퀘스트
        register(QuestID.LIFE_MASTER_CHEF, MasterChefQuest::new);

        // 특수 퀘스트
        register(QuestID.SPECIAL_HIDDEN_CLASS, HiddenClassQuest::new);

        // 시즌 퀘스트
        register(QuestID.SEASON_SPRING_FESTIVAL, SpringFestivalQuest::new);

        // 직업 퀘스트
        register(QuestID.CLASS_WARRIOR_ADVANCEMENT, WarriorAdvancementQuest::new);

        // 반복 퀘스트
        register(QuestID.REPEAT_MONSTER_EXTERMINATION, MonsterExterminationQuest::new);
    }

    /**
     * 퀘스트 공급자 등록
     */
    private static void register(@NotNull QuestID id, @NotNull Supplier<Quest> supplier) {
        questSuppliers.put(id, supplier);
    }

    /**
     * QuestID로 퀘스트 인스턴스 가져오기 (인스턴스 메소드)
     */
    @Nullable
    public Quest getQuest(@NotNull QuestID id) {
        return createQuest(id);
    }
    
    /**
     * QuestID로 퀘스트 인스턴스 생성
     */
    @Nullable
    public static Quest createQuest(@NotNull QuestID id) {
        Supplier<Quest> supplier = questSuppliers.get(id);
        if (supplier == null) {
            return null;
        }

        Quest quest = supplier.get();

        // 생성된 퀘스트의 ID가 매개변수와 일치하는지 검증
        if (!quest.getId().equals(id)) {
            throw new IllegalStateException("Quest ID mismatch: expected " + id + " but got " + quest.getId());
        }

        return quest;
    }

    /**
     * 모든 퀘스트 인스턴스 생성
     */
    @NotNull
    public static Map<QuestID, Quest> createAllQuests() {
        Map<QuestID, Quest> quests = new EnumMap<>(QuestID.class);

        for (QuestID id : QuestID.values()) {
            Quest quest = createQuest(id);
            if (quest != null) {
                quests.put(id, quest);
                validateQuest(quest);
            }
        }

        return quests;
    }
    
    /**
     * 퀘스트 검증 및 경고 출력
     */
    private static void validateQuest(@NotNull Quest quest) {
        // 각 목표 검증
        for (QuestObjective objective : quest.getObjectives()) {
            String error = objective.validate();
            if (error != null) {
                LogUtil.warning("[퀘스트 검증] " + quest.getId() + " - " + error);
            }
        }
    }

    /**
     * 퀘스트가 구현되어 있는지 확인
     */
    public static boolean isImplemented(@NotNull QuestID id) {
        return questSuppliers.containsKey(id);
    }

    /**
     * 구현된 퀘스트 개수
     */
    public static int getImplementedCount() {
        return questSuppliers.size();
    }
}