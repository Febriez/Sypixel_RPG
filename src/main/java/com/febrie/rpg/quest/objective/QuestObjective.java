package com.febrie.rpg.quest.objective;

import com.febrie.rpg.quest.progress.ObjectiveProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 퀘스트 목표 인터페이스
 * 모든 퀘스트 목표가 구현해야 하는 기본 인터페이스
 *
 * @author Febrie
 */
public interface QuestObjective {

    /**
     * 목표 ID 반환
     *
     * @return 고유 목표 ID
     */
    @NotNull String getId();

    /**
     * 목표 타입 반환
     *
     * @return 목표 타입
     */
    @NotNull ObjectiveType getType();

    /**
     * 목표 설명 번역 키 반환
     *
     * @return 번역 키
     */
    @NotNull String getDescriptionKey();

    /**
     * 목표 설명에 사용할 플레이스홀더
     *
     * @return 플레이스홀더 배열 (key, value 순서)
     */
    @NotNull String[] getDescriptionPlaceholders();

    /**
     * 목표 완료에 필요한 수량
     *
     * @return 필요 수량
     */
    int getRequiredAmount();

    /**
     * 현재 진행도 확인
     *
     * @param progress 진행도 객체
     * @return 현재 진행 수치
     */
    int getCurrentProgress(@NotNull ObjectiveProgress progress);

    /**
     * 목표 완료 여부 확인
     *
     * @param progress 진행도 객체
     * @return 완료 여부
     */
    default boolean isComplete(@NotNull ObjectiveProgress progress) {
        return getCurrentProgress(progress) >= getRequiredAmount();
    }

    /**
     * 진행도 업데이트 가능 여부 확인
     *
     * @param event  발생한 이벤트
     * @param player 플레이어
     * @return 업데이트 가능 여부
     */
    boolean canProgress(@NotNull Event event, @NotNull Player player);

    /**
     * 진행도 증가량 계산
     *
     * @param event  발생한 이벤트
     * @param player 플레이어
     * @return 증가량 (0 이상)
     */
    int calculateIncrement(@NotNull Event event, @NotNull Player player);

    /**
     * 목표 진행도 표시용 문자열
     *
     * @param progress 진행도 객체
     * @return 진행도 문자열 (예: "5/10")
     */
    default @NotNull String getProgressString(@NotNull ObjectiveProgress progress) {
        return getCurrentProgress(progress) + "/" + getRequiredAmount();
    }

    /**
     * 목표 진행도 퍼센트
     *
     * @param progress 진행도 객체
     * @return 0.0 ~ 1.0 사이의 진행도
     */
    default double getProgressPercentage(@NotNull ObjectiveProgress progress) {
        if (getRequiredAmount() <= 0) return 1.0;
        return Math.min(1.0, (double) getCurrentProgress(progress) / getRequiredAmount());
    }

    /**
     * 목표 데이터 직렬화
     *
     * @return 직렬화된 데이터
     */
    @NotNull String serialize();

    /**
     * 목표 데이터 역직렬화
     *
     * @param data 직렬화된 데이터
     * @return 목표 객체
     */
    static @Nullable QuestObjective deserialize(@NotNull String data) {
        // 구현체에서 처리
        return null;
    }
}