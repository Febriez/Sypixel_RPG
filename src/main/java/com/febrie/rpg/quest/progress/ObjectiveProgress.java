package com.febrie.rpg.quest.progress;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * 퀘스트 목표 진행도
 * 각 목표의 현재 진행 상태를 추적
 *
 * @author Febrie
 */
public class ObjectiveProgress {

    private final String objectiveId;
    private final UUID playerId;
    private int currentValue;
    private final int requiredValue;
    private boolean completed;
    private final long startedAt;
    private long completedAt;
    private long lastUpdated;


    /**
     * 새로운 진행도 생성
     *
     * @param objectiveId   목표 ID
     * @param playerId      플레이어 UUID
     * @param requiredValue 필요 수치
     */
    public ObjectiveProgress(@NotNull String objectiveId, @NotNull UUID playerId, int requiredValue) {
        this(objectiveId, playerId, 0, requiredValue, false, System.currentTimeMillis(), 0);
    }

    /**
     * 전체 데이터로 생성
     *
     * @param objectiveId   목표 ID
     * @param playerId      플레이어 UUID
     * @param currentValue  현재 수치
     * @param requiredValue 필요 수치
     * @param completed     완료 여부
     */
    public ObjectiveProgress(@NotNull String objectiveId, @NotNull UUID playerId,
                             int currentValue, int requiredValue, boolean completed) {
        this(objectiveId, playerId, currentValue, requiredValue, completed,
                System.currentTimeMillis(), completed ? System.currentTimeMillis() : 0);
    }

    /**
     * 모든 필드 포함 생성자
     *
     * @param objectiveId   목표 ID
     * @param playerId      플레이어 UUID
     * @param currentValue  현재 수치
     * @param requiredValue 필요 수치
     * @param completed     완료 여부
     * @param startedAt     시작 시간
     * @param completedAt   완료 시간
     */
    public ObjectiveProgress(@NotNull String objectiveId, @NotNull UUID playerId,
                             int currentValue, int requiredValue, boolean completed,
                             long startedAt, long completedAt) {
        this.objectiveId = Objects.requireNonNull(objectiveId);
        this.playerId = Objects.requireNonNull(playerId);
        this.currentValue = currentValue;
        this.requiredValue = requiredValue;
        this.completed = completed;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.lastUpdated = System.currentTimeMillis();

        if (requiredValue <= 0) {
            throw new IllegalArgumentException("Required value must be positive");
        }
    }

    /**
     * 진행도 업데이트
     *
     * @param newValue 새로운 값
     */
    public void update(int newValue) {
        if (newValue < 0) {
            throw new IllegalArgumentException("Progress cannot be negative");
        }

        this.currentValue = Math.min(newValue, requiredValue);
        this.lastUpdated = System.currentTimeMillis();

        if (this.currentValue >= requiredValue && !this.completed) {
            this.completed = true;
            this.completedAt = System.currentTimeMillis();
        }
    }

    /**
     * 진행도 증가
     *
     * @param amount 증가량
     */
    public void increment(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Increment cannot be negative");
        }

        update(currentValue + amount);
    }


    /**
     * 진행도 리셋
     */
    public void reset() {
        this.currentValue = 0;
        this.completed = false;
        this.completedAt = 0;
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * 진행률 계산 (0.0 ~ 1.0)
     *
     * @return 진행률
     */
    public double getProgress() {
        return (double) currentValue / requiredValue;
    }

    /**
     * 진행률 퍼센트 (0 ~ 100)
     *
     * @return 퍼센트
     */
    public int getProgressPercentage() {
        return (int) (getProgress() * 100);
    }

    /**
     * 남은 수치
     *
     * @return 완료까지 필요한 수치
     */
    public int getRemaining() {
        return Math.max(0, requiredValue - currentValue);
    }

    // Getters
    public @NotNull String getObjectiveId() {
        return objectiveId;
    }

    public @NotNull UUID getPlayerId() {
        return playerId;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public int getRequiredValue() {
        return requiredValue;
    }

    /**
     * RequiredAmount와 동일 (인터페이스 호환성)
     */
    public int getRequiredAmount() {
        return requiredValue;
    }

    public boolean isCompleted() {
        return completed;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public long getCompletedAt() {
        return completedAt;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectiveProgress that)) return false;
        return Objects.equals(objectiveId, that.objectiveId) &&
                Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectiveId, playerId);
    }

    @Override
    public String toString() {
        return String.format("ObjectiveProgress{id=%s, player=%s, progress=%d/%d, completed=%s}",
                objectiveId, playerId, currentValue, requiredValue, completed);
    }
}