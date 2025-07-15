package com.febrie.rpg.quest.registry;

import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 퀘스트 레지스트리
 * QuestID와 실제 Quest 구현체를 매핑
 *
 * @author Febrie
 */
public class QuestRegistry {

    private static final Map<QuestID, Supplier<Quest>> questSuppliers = new EnumMap<>(QuestID.class);

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

        // 주간 퀘스트
        // register(QuestID.WEEKLY_RAID_BOSS, WeeklyRaidBossQuest::new);
        // register(QuestID.WEEKLY_DUNGEON_CLEAR, WeeklyDungeonClearQuest::new);

        // 이벤트 퀘스트
        // register(QuestID.EVENT_CHRISTMAS_2024, ChristmasEventQuest::new);
        // register(QuestID.EVENT_HALLOWEEN_2024, HalloweenEventQuest::new);
    }

    /**
     * 퀘스트 공급자 등록
     */
    private static void register(@NotNull QuestID id, @NotNull Supplier<Quest> supplier) {
        questSuppliers.put(id, supplier);
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
            }
        }

        return quests;
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