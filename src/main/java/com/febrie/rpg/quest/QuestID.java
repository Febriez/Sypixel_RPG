package com.febrie.rpg.quest;

import org.jetbrains.annotations.NotNull;

/**
 * 모든 퀘스트의 ID를 정의하는 Enum
 * 타입 안전성과 관리 편의성을 위해 사용
 *
 * @author Febrie
 */
public enum QuestID {

    // ===== 튜토리얼 퀘스트 =====
    TUTORIAL_FIRST_STEPS("tutorial_first_steps", "첫 걸음", Quest.QuestCategory.TUTORIAL),
    TUTORIAL_BASIC_COMBAT("tutorial_basic_combat", "기초 전투", Quest.QuestCategory.TUTORIAL),

    // ===== 메인 퀘스트 =====
    MAIN_HEROES_JOURNEY("main_heroes_journey", "영웅의 여정", Quest.QuestCategory.MAIN),
    MAIN_PATH_OF_LIGHT("main_path_of_light", "빛의 길", Quest.QuestCategory.MAIN),
    MAIN_PATH_OF_DARKNESS("main_path_of_darkness", "어둠의 길", Quest.QuestCategory.MAIN),

    // ===== 사이드 퀘스트 =====
    SIDE_COLLECT_HERBS("side_collect_herbs", "약초 수집", Quest.QuestCategory.SIDE),
    SIDE_LOST_TREASURE("side_lost_treasure", "잃어버린 보물", Quest.QuestCategory.SIDE),

    // ===== 일일 퀘스트 =====
    DAILY_HUNTING("daily_hunting", "일일 사냥", Quest.QuestCategory.DAILY),
    DAILY_MINING("daily_mining", "일일 채광", Quest.QuestCategory.DAILY),
    DAILY_FISHING("daily_fishing", "일일 낚시", Quest.QuestCategory.DAILY);

    // ===== 주간 퀘스트 (구현 예정) =====
    //WEEKLY_RAID_BOSS("weekly_raid_boss", "주간 레이드", Quest.QuestCategory.WEEKLY),
    //WEEKLY_DUNGEON_CLEAR("weekly_dungeon_clear", "던전 클리어", Quest.QuestCategory.WEEKLY);

    private final String legacyId;
    private final String displayName;
    private final Quest.QuestCategory category;

    QuestID(@NotNull String legacyId, @NotNull String displayName, @NotNull Quest.QuestCategory category) {
        this.legacyId = legacyId;
        this.displayName = displayName;
        this.category = category;
    }

    /**
     * 기존 String ID 반환 (하위 호환성용)
     */
    @NotNull
    public String getLegacyId() {
        return legacyId;
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
        return "quest." + legacyId.replace("_", ".") + ".name";
    }

    /**
     * 설명 번역 키 반환
     */
    @NotNull
    public String getDescriptionKey() {
        return "quest." + legacyId.replace("_", ".") + ".description";
    }

    /**
     * String ID로 QuestID 찾기
     */
    @NotNull
    public static QuestID fromLegacyId(@NotNull String legacyId) {
        for (QuestID id : values()) {
            if (id.legacyId.equals(legacyId)) {
                return id;
            }
        }
        throw new IllegalArgumentException("Unknown quest ID: " + legacyId);
    }

    /**
     * 카테고리별 퀘스트 ID 반환
     */
    @NotNull
    public static QuestID[] getByCategory(@NotNull Quest.QuestCategory category) {
        return java.util.Arrays.stream(values())
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