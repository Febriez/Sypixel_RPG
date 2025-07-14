package com.febrie.rpg.quest.manager;

import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.progress.QuestProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 퀘스트 시스템 매니저 인터페이스
 * 퀘스트의 등록, 진행, 완료 등을 관리
 *
 * @author Febrie
 */
public interface QuestManager {

    /**
     * 퀘스트 등록
     *
     * @param quest 등록할 퀘스트
     */
    void registerQuest(@NotNull Quest quest);

    /**
     * 퀘스트 등록 해제
     *
     * @param questId 퀘스트 ID
     */
    void unregisterQuest(@NotNull String questId);

    /**
     * 퀘스트 조회
     *
     * @param questId 퀘스트 ID
     * @return 퀘스트 (없으면 null)
     */
    @Nullable Quest getQuest(@NotNull String questId);

    /**
     * 모든 등록된 퀘스트 조회
     *
     * @return 퀘스트 목록
     */
    @NotNull List<Quest> getAllQuests();

    /**
     * 플레이어가 퀘스트 시작
     *
     * @param player  플레이어
     * @param questId 퀘스트 ID
     * @return 시작 성공 여부
     */
    boolean startQuest(@NotNull Player player, @NotNull String questId);

    /**
     * 플레이어가 퀘스트 포기
     *
     * @param player  플레이어
     * @param questId 퀘스트 ID
     * @return 포기 성공 여부
     */
    boolean abandonQuest(@NotNull Player player, @NotNull String questId);

    /**
     * 플레이어의 활성 퀘스트 목록 조회
     *
     * @param player 플레이어
     * @return 활성 퀘스트 진행도 목록
     */
    @NotNull List<QuestProgress> getActiveQuests(@NotNull Player player);

    /**
     * 플레이어의 특정 퀘스트 진행도 조회
     *
     * @param player  플레이어
     * @param questId 퀘스트 ID
     * @return 퀘스트 진행도
     */
    @NotNull Optional<QuestProgress> getQuestProgress(@NotNull Player player, @NotNull String questId);

    /**
     * 플레이어의 완료된 퀘스트 목록 조회
     *
     * @param player 플레이어
     * @return 완료된 퀘스트 ID 목록
     */
    @NotNull List<String> getCompletedQuests(@NotNull Player player);

    /**
     * 이벤트 처리 (퀘스트 진행도 업데이트)
     *
     * @param event  발생한 이벤트
     * @param player 관련 플레이어
     */
    void handleEvent(@NotNull Event event, @NotNull Player player);

    /**
     * 플레이어의 퀘스트 데이터 로드
     *
     * @param playerId 플레이어 UUID
     */
    void loadPlayerData(@NotNull UUID playerId);

    /**
     * 플레이어의 퀘스트 데이터 저장
     *
     * @param playerId 플레이어 UUID
     */
    void savePlayerData(@NotNull UUID playerId);

    /**
     * 모든 플레이어의 퀘스트 데이터 저장
     */
    void saveAllData();

    /**
     * 매니저 종료 (리소스 정리)
     */
    void shutdown();

    /**
     * 플레이어가 특정 퀘스트를 시작할 수 있는지 확인
     *
     * @param player  플레이어
     * @param questId 퀘스트 ID
     * @return 시작 가능 여부
     */
    default boolean canStartQuest(@NotNull Player player, @NotNull String questId) {
        Quest quest = getQuest(questId);
        if (quest == null) return false;

        // 이미 진행 중이거나 완료했는지 확인
        if (getQuestProgress(player, questId).isPresent()) return false;
        if (getCompletedQuests(player).contains(questId)) return false;

        // 퀘스트 자체 조건 확인
        return quest.canStart(player.getUniqueId());
    }

    /**
     * 플레이어에게 가용한 퀘스트 목록 조회
     *
     * @param player 플레이어
     * @return 시작 가능한 퀘스트 목록
     */
    default @NotNull List<Quest> getAvailableQuests(@NotNull Player player) {
        return getAllQuests().stream()
                .filter(quest -> canStartQuest(player, quest.getId()))
                .toList();
    }
}