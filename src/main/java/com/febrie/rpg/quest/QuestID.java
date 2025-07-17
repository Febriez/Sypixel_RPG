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
    TUTORIAL_FIRST_STEPS("첫 걸음", Quest.QuestCategory.TUTORIAL),
    TUTORIAL_BASIC_COMBAT("기초 전투", Quest.QuestCategory.TUTORIAL),

    // ===== 메인 퀘스트 =====
    MAIN_HEROES_JOURNEY("영웅의 여정", Quest.QuestCategory.MAIN),
    MAIN_PATH_OF_LIGHT("빛의 길", Quest.QuestCategory.MAIN),
    MAIN_PATH_OF_DARKNESS("어둠의 길", Quest.QuestCategory.MAIN),

    // ===== 사이드 퀘스트 =====
    SIDE_FARMERS_REQUEST("농부의 부탁", Quest.QuestCategory.SIDE),
    SIDE_COLLECT_HERBS("약초 수집", Quest.QuestCategory.SIDE),
    SIDE_LOST_TREASURE("잃어버린 보물", Quest.QuestCategory.SIDE),

    // ===== 일일 퀘스트 =====
    DAILY_HUNTING("일일 사냥", Quest.QuestCategory.DAILY),
    DAILY_MINING("일일 채광", Quest.QuestCategory.DAILY),
    DAILY_FISHING("일일 낚시", Quest.QuestCategory.DAILY);

    private final String displayName;
    private final Quest.QuestCategory category;

    QuestID(@NotNull String displayName, @NotNull Quest.QuestCategory category) {
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
    public Quest.QuestCategory getCategory() {
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
    public static QuestID[] getByCategory(@NotNull Quest.QuestCategory category) {
        return Arrays.stream(values())
                .filter(id -> id.category == category)
                .toArray(QuestID[]::new);
    }

    /**
     * 퀘스트가 일일 퀘스트인지 확인
     */
    public boolean isDaily() {
        return category == Quest.QuestCategory.DAILY;
    }

    /**
     * 퀘스트가 주간 퀘스트인지 확인
     */
    public boolean isWeekly() {
        return category == Quest.QuestCategory.WEEKLY;
    }

    /**
     * 퀘스트가 이벤트 퀘스트인지 확인
     */
    public boolean isEvent() {
        return category == Quest.QuestCategory.EVENT;
    }
}