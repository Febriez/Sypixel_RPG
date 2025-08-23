package com.febrie.rpg.quest.objective;

import com.febrie.rpg.quest.progress.ObjectiveProgress;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 퀘스트 목표 기본 추상 클래스
 * 모든 목표의 공통 기능 구현
 *
 * @author Febrie
 */
public abstract class BaseObjective implements QuestObjective {

    protected final String id;
    protected final int requiredAmount;

    /**
     * 기본 생성자
     *
     * @param id             목표 ID
     * @param requiredAmount 필요 수량
     */
    protected BaseObjective(@NotNull String id, int requiredAmount) {
        this.id = Objects.requireNonNull(id, "Objective ID cannot be null");
        this.requiredAmount = requiredAmount;

        if (requiredAmount <= 0) {
            throw new IllegalArgumentException("Required amount must be positive: " + requiredAmount);
        }
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public int getRequiredAmount() {
        return requiredAmount;
    }

    @Override
    public int getCurrentProgress(@NotNull ObjectiveProgress progress) {
        return progress.getCurrentValue();
    }

    @Override
    public @NotNull String getProgressString(@NotNull ObjectiveProgress progress) {
        int current = getCurrentProgress(progress);
        return current + "/" + requiredAmount;
    }

    @Override
    public double getProgressPercentage(@NotNull ObjectiveProgress progress) {
        return Math.min(1.0, (double) getCurrentProgress(progress) / requiredAmount);
    }

    @Override
    public boolean isComplete(@NotNull ObjectiveProgress progress) {
        return getCurrentProgress(progress) >= requiredAmount;
    }

    /**
     * 직렬화를 위한 기본 형식
     *
     * @return type:id:amount:data 형식의 문자열
     */
    @Override
    public @NotNull String serialize() {
        return String.format("%s:%s:%d:%s",
                getType().name(),
                id,
                requiredAmount,
                serializeData());
    }

    /**
     * 구현체별 추가 데이터 직렬화
     *
     * @return 직렬화된 데이터
     */
    protected abstract @NotNull String serializeData();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseObjective that)) return false;
        return requiredAmount == that.requiredAmount &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, requiredAmount);
    }
}