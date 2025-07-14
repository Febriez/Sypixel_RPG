package com.febrie.rpg.quest.progress;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 퀘스트 전체 진행도
 * 플레이어의 퀘스트 진행 상태를 추적
 *
 * @author Febrie
 */
public class QuestProgress {

    public enum QuestState {
        NOT_STARTED("quest.state.not_started"),
        ACTIVE("quest.state.active"),
        COMPLETED("quest.state.completed"),
        FAILED("quest.state.failed"),
        ABANDONED("quest.state.abandoned");

        private final String translationKey;

        QuestState(String translationKey) {
            this.translationKey = translationKey;
        }

        public String getTranslationKey() {
            return translationKey;
        }
    }

    private final String questId;
    private final UUID playerId;
    private QuestState state;
    private final Map<String, ObjectiveProgress> objectives;
    private final LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastUpdated;
    private int currentObjectiveIndex; // 순차적 퀘스트용

    /**
     * 새로운 퀘스트 진행도 생성
     *
     * @param questId    퀘스트 ID
     * @param playerId   플레이어 UUID
     * @param objectives 목표별 진행도 맵
     */
    public QuestProgress(@NotNull String questId, @NotNull UUID playerId,
                         @NotNull Map<String, ObjectiveProgress> objectives) {
        this.questId = Objects.requireNonNull(questId);
        this.playerId = Objects.requireNonNull(playerId);
        this.state = QuestState.ACTIVE;
        this.objectives = new HashMap<>(objectives);
        this.startedAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.currentObjectiveIndex = 0;
    }

    /**
     * 목표 진행도 업데이트
     *
     * @param objectiveId 목표 ID
     * @param progress    새로운 진행도
     */
    public void updateObjective(@NotNull String objectiveId, int progress) {
        ObjectiveProgress objProgress = objectives.get(objectiveId);
        if (objProgress == null) {
            throw new IllegalArgumentException("Unknown objective: " + objectiveId);
        }

        objProgress.update(progress);
        this.lastUpdated = LocalDateTime.now();

        // 모든 목표 완료 확인
        if (areAllObjectivesComplete()) {
            complete();
        }
    }

    /**
     * 목표 진행도 증가
     *
     * @param objectiveId 목표 ID
     * @param amount      증가량
     */
    public void incrementObjective(@NotNull String objectiveId, int amount) {
        ObjectiveProgress objProgress = objectives.get(objectiveId);
        if (objProgress == null) {
            throw new IllegalArgumentException("Unknown objective: " + objectiveId);
        }

        objProgress.increment(amount);
        this.lastUpdated = LocalDateTime.now();

        // 모든 목표 완료 확인
        if (areAllObjectivesComplete()) {
            complete();
        }
    }

    /**
     * 퀘스트 완료 처리
     */
    public void complete() {
        if (state != QuestState.ACTIVE) {
            throw new IllegalStateException("Can only complete active quests");
        }

        this.state = QuestState.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * 퀘스트 포기 처리
     */
    public void abandon() {
        if (state != QuestState.ACTIVE) {
            throw new IllegalStateException("Can only abandon active quests");
        }

        this.state = QuestState.ABANDONED;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * 퀘스트 실패 처리
     */
    public void fail() {
        if (state != QuestState.ACTIVE) {
            throw new IllegalStateException("Can only fail active quests");
        }

        this.state = QuestState.FAILED;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * 모든 목표 완료 여부 확인
     */
    public boolean areAllObjectivesComplete() {
        return objectives.values().stream()
                .allMatch(ObjectiveProgress::isCompleted);
    }

    /**
     * 전체 진행률 계산 (0.0 ~ 1.0)
     */
    public double getOverallProgress() {
        if (objectives.isEmpty()) return 0.0;

        double totalProgress = objectives.values().stream()
                .mapToDouble(ObjectiveProgress::getProgress)
                .sum();

        return totalProgress / objectives.size();
    }

    /**
     * 전체 진행률 퍼센트 (0 ~ 100)
     */
    public int getOverallProgressPercentage() {
        return (int) (getOverallProgress() * 100);
    }

    /**
     * 특정 목표의 진행도 가져오기
     */
    public @Nullable ObjectiveProgress getObjectiveProgress(@NotNull String objectiveId) {
        return objectives.get(objectiveId);
    }

    /**
     * 현재 활성 목표 인덱스 설정 (순차적 퀘스트용)
     */
    public void setCurrentObjectiveIndex(int index) {
        this.currentObjectiveIndex = index;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * 다음 목표로 진행 (순차적 퀘스트용)
     */
    public void progressToNextObjective() {
        this.currentObjectiveIndex++;
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters
    public @NotNull String getQuestId() {
        return questId;
    }

    public @NotNull UUID getPlayerId() {
        return playerId;
    }

    public @NotNull QuestState getState() {
        return state;
    }

    public @NotNull Map<String, ObjectiveProgress> getObjectives() {
        return new HashMap<>(objectives);
    }

    public @NotNull LocalDateTime getStartedAt() {
        return startedAt;
    }

    public @Nullable LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public @NotNull LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public int getCurrentObjectiveIndex() {
        return currentObjectiveIndex;
    }

    public boolean isActive() {
        return state == QuestState.ACTIVE;
    }

    public boolean isCompleted() {
        return state == QuestState.COMPLETED;
    }

    public boolean isFailed() {
        return state == QuestState.FAILED;
    }

    public boolean isAbandoned() {
        return state == QuestState.ABANDONED;
    }

    /**
     * 진행 시간 계산
     */
    public long getElapsedSeconds() {
        LocalDateTime endTime = completedAt != null ? completedAt : LocalDateTime.now();
        return java.time.Duration.between(startedAt, endTime).getSeconds();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestProgress that)) return false;
        return Objects.equals(questId, that.questId) &&
                Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questId, playerId);
    }

    @Override
    public String toString() {
        return String.format("QuestProgress{quest=%s, player=%s, state=%s, progress=%d%%}",
                questId, playerId, state, getOverallProgressPercentage());
    }
}