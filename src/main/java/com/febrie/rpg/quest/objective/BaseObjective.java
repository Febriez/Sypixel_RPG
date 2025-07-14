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
    protected final String descriptionKey;
    protected final String[] placeholders;

    /**
     * 기본 생성자
     *
     * @param id             목표 ID
     * @param requiredAmount 필요 수량
     * @param descriptionKey 목표 설명 번역 키
     * @param placeholders   플레이스홀더 배열
     */
    protected BaseObjective(@NotNull String id, int requiredAmount,
                            @NotNull String descriptionKey, @NotNull String... placeholders) {
        this.id = Objects.requireNonNull(id, "Objective ID cannot be null");
        this.requiredAmount = requiredAmount;
        this.descriptionKey = Objects.requireNonNull(descriptionKey, "Description key cannot be null");
        this.placeholders = placeholders;

        if (requiredAmount <= 0) {
            throw new IllegalArgumentException("Required amount must be positive: " + requiredAmount);
        }
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @NotNull String getDescriptionKey() {
        return descriptionKey;
    }

    @Override
    public @NotNull String[] getDescriptionPlaceholders() {
        return placeholders.clone();
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

    @Override
    public @NotNull String getDescription(boolean isKorean) {
        // 기본 구현 - 하위 클래스에서 오버라이드
        return isKorean ? "퀘스트 목표입니다." : "Quest objective.";
    }

    @Override
    public @NotNull String getGiverName(boolean isKorean) {
        // 기본 구현 - 하위 클래스에서 오버라이드
        return isKorean ? "알 수 없음" : "Unknown";
    }
}