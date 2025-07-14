package com.febrie.rpg.quest;

import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.quest.reward.QuestReward;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 퀘스트 기본 추상 클래스
 * 모든 퀘스트가 상속받아야 하는 기본 클래스
 *
 * @author Febrie
 */
public abstract class Quest {

    protected final String id;
    protected final String nameKey;
    protected final String descriptionKey;

    // 선행 퀘스트 시스템
    private final List<String> prerequisiteQuests = new ArrayList<>();

    // 양자택일 퀘스트 시스템
    private final List<String> exclusiveQuests = new ArrayList<>();

    /**
     * 기본 생성자
     *
     * @param id             퀘스트 고유 ID
     * @param nameKey        퀘스트 이름 번역 키
     * @param descriptionKey 퀘스트 설명 번역 키
     */
    protected Quest(@NotNull String id, @NotNull String nameKey, @NotNull String descriptionKey) {
        this.id = Objects.requireNonNull(id, "Quest ID cannot be null");
        this.nameKey = Objects.requireNonNull(nameKey, "Name key cannot be null");
        this.descriptionKey = Objects.requireNonNull(descriptionKey, "Description key cannot be null");
    }

    /**
     * 퀘스트 ID 반환
     *
     * @return 고유 ID
     */
    public @NotNull String getId() {
        return id;
    }

    /**
     * 퀘스트 이름 번역 키 반환
     *
     * @return 번역 키
     */
    public @NotNull String getNameKey() {
        return nameKey;
    }

    /**
     * 퀘스트 설명 번역 키 반환
     *
     * @return 번역 키
     */
    public @NotNull String getDescriptionKey() {
        return descriptionKey;
    }

    /**
     * 퀘스트 목표 목록 반환
     *
     * @return 목표 리스트
     */
    public abstract @NotNull List<QuestObjective> getObjectives();

    /**
     * 순차적 진행 여부
     * true면 목표를 순서대로 완료해야 함
     *
     * @return 순차 진행 여부
     */
    public abstract boolean isSequential();

    /**
     * 퀘스트 보상
     *
     * @return 보상 객체
     */
    public abstract @NotNull QuestReward getReward();

    /**
     * 퀘스트 시작 가능 여부 확인
     *
     * @param playerId 플레이어 UUID
     * @return 시작 가능 여부
     */
    public abstract boolean canStart(@NotNull UUID playerId);

    /**
     * 최소 레벨 요구사항
     *
     * @return 최소 레벨
     */
    public abstract int getMinLevel();

    /**
     * 최대 레벨 제한
     *
     * @return 최대 레벨 (0이면 제한 없음)
     */
    public abstract int getMaxLevel();

    /**
     * 선행 퀘스트 ID 목록
     *
     * @return 선행 퀘스트 ID 리스트
     */
    public @NotNull List<String> getPrerequisiteQuests() {
        return new ArrayList<>(prerequisiteQuests);
    }

    /**
     * 선행 퀘스트 추가
     *
     * @param questId 선행 퀘스트 ID
     */
    protected void addPrerequisiteQuest(@NotNull String questId) {
        if (!prerequisiteQuests.contains(questId)) {
            prerequisiteQuests.add(questId);
        }
    }

    /**
     * 선행 퀘스트가 있는지 확인
     *
     * @return 선행 퀘스트 존재 여부
     */
    public boolean hasPrerequisiteQuests() {
        return !prerequisiteQuests.isEmpty();
    }

    /**
     * 양자택일 퀘스트 ID 목록
     * 이 목록의 퀘스트를 완료하면 현재 퀘스트를 시작할 수 없음
     *
     * @return 양자택일 퀘스트 ID 리스트
     */
    public @NotNull List<String> getExclusiveQuests() {
        return new ArrayList<>(exclusiveQuests);
    }

    /**
     * 양자택일 퀘스트 추가
     *
     * @param questId 양자택일 퀘스트 ID
     */
    protected void addExclusiveQuest(@NotNull String questId) {
        if (!exclusiveQuests.contains(questId)) {
            exclusiveQuests.add(questId);
        }
    }

    /**
     * 양자택일 퀘스트가 있는지 확인
     *
     * @return 양자택일 퀘스트 존재 여부
     */
    public boolean hasExclusiveQuests() {
        return !exclusiveQuests.isEmpty();
    }

    /**
     * 플레이어가 선행 퀘스트를 모두 완료했는지 확인
     *
     * @param completedQuests 플레이어가 완료한 퀘스트 ID 목록
     * @return 모든 선행 퀘스트 완료 여부
     */
    public boolean arePrerequisitesCompleted(@NotNull List<String> completedQuests) {
        return completedQuests.containsAll(prerequisiteQuests);
    }

    /**
     * 플레이어가 양자택일 퀘스트를 완료했는지 확인
     *
     * @param completedQuests 플레이어가 완료한 퀘스트 ID 목록
     * @return 양자택일 퀘스트 완료 여부
     */
    public boolean hasCompletedExclusiveQuests(@NotNull List<String> completedQuests) {
        for (String exclusiveQuest : exclusiveQuests) {
            if (completedQuests.contains(exclusiveQuest)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 퀘스트 카테고리
     *
     * @return 카테고리
     */
    public @NotNull QuestCategory getCategory() {
        return QuestCategory.NORMAL;
    }

    /**
     * 반복 가능 여부
     *
     * @return 반복 가능 여부
     */
    public boolean isRepeatable() {
        return false;
    }

    /**
     * 일일 퀘스트 여부
     *
     * @return 일일 퀘스트 여부
     */
    public boolean isDaily() {
        return false;
    }

    /**
     * 주간 퀘스트 여부
     *
     * @return 주간 퀘스트 여부
     */
    public boolean isWeekly() {
        return false;
    }

    /**
     * 퀘스트 진행도 생성
     *
     * @param playerId 플레이어 UUID
     * @return 새로운 진행도 객체
     */
    public @NotNull QuestProgress createProgress(@NotNull UUID playerId) {
        Map<String, ObjectiveProgress> objectives = new HashMap<>();

        for (QuestObjective objective : getObjectives()) {
            objectives.put(objective.getId(),
                    new ObjectiveProgress(objective.getId(), playerId, objective.getRequiredAmount()));
        }

        return new QuestProgress(id, playerId, objectives);
    }

    /**
     * 퀘스트 카테고리
     */
    public enum QuestCategory {
        MAIN("quest.category.main"),
        SIDE("quest.category.side"),
        DAILY("quest.category.daily"),
        WEEKLY("quest.category.weekly"),
        EVENT("quest.category.event"),
        TUTORIAL("quest.category.tutorial"),
        NORMAL("quest.category.normal");

        private final String translationKey;

        QuestCategory(String translationKey) {
            this.translationKey = translationKey;
        }

        public String getTranslationKey() {
            return translationKey;
        }
    }
}