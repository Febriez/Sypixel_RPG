package com.febrie.rpg.quest.reward;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 퀘스트 보상 인터페이스
 * 모든 퀘스트 보상이 구현해야 하는 인터페이스
 *
 * @author Febrie
 */
public interface QuestReward {

    /**
     * 보상 지급
     *
     * @param player 보상을 받을 플레이어
     */
    void grant(@NotNull Player player);

    /**
     * 보상 설명 번역 키
     * LangManager의 getComponentList로 표시
     *
     * @return 번역 키
     */
    @NotNull String getDescriptionKey();

    /**
     * 보상 미리보기용 설명
     * 실제 수량을 포함한 구체적인 설명
     *
     * @return 보상 설명 배열 (각 줄은 별도의 번역 키)
     */
    @NotNull String[] getPreviewKeys();

    /**
     * 보상 타입
     *
     * @return 보상 타입
     */
    @NotNull RewardType getType();

    /**
     * 보상 정보를 Component로 표시
     *
     * @param player 플레이어 (언어 설정 확인용)
     * @return 보상 정보 Component
     */
    @NotNull Component getDisplayInfo(@NotNull Player player);

    /**
     * 로어용 보상 정보 목록 생성
     * 각 보상 항목을 개별 Component로 반환
     *
     * @param player 플레이어 (언어 설정 확인용)
     * @return 로어용 보상 정보 Component 목록
     */
    default @NotNull List<Component> getLoreComponents(@NotNull Player player) {
        // 기본 구현: 단일 Component를 목록으로 반환
        return List.of(getDisplayInfo(player));
    }

    /**
     * 보상 가능 여부 확인
     *
     * @param player 플레이어
     * @return 받을 수 있으면 true
     */
    default boolean canReceive(@NotNull Player player) {
        return true;
    }

    /**
     * 보상 타입 열거형
     */
    enum RewardType {
        ITEM("quest.reward.type.item"),
        EXPERIENCE("quest.reward.type.experience"),
        CURRENCY("quest.reward.type.currency"),
        MIXED("quest.reward.type.mixed"),
        SPECIAL("quest.reward.type.special");

        private final String translationKey;

        RewardType(String translationKey) {
            this.translationKey = translationKey;
        }

        public String getTranslationKey() {
            return translationKey;
        }
    }
}