package com.febrie.rpg.quest.manager;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.FirebaseService;
import com.febrie.rpg.dto.CompletedQuestDTO;
import com.febrie.rpg.dto.PlayerQuestDTO;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 퀘스트 시스템 매니저 (싱글톤)
 * 퀘스트의 등록, 진행, 완료 등을 관리
 *
 * @author Febrie
 */
public class QuestManager {

    private static QuestManager instance;

    private final RPGMain plugin;
    private final FirebaseService firebaseService;

    // 등록된 퀘스트 목록
    private final Map<String, Quest> registeredQuests = new ConcurrentHashMap<>();

    // 플레이어별 퀘스트 데이터 캐시
    private final Map<UUID, PlayerQuestDTO> playerQuestCache = new ConcurrentHashMap<>();

    // 저장 대기열
    private final Set<UUID> pendingSaves = ConcurrentHashMap.newKeySet();

    /**
     * 프라이빗 생성자
     */
    private QuestManager(@NotNull RPGMain plugin, @NotNull FirebaseService firebaseService) {
        this.plugin = plugin;
        this.firebaseService = firebaseService;

        // 자동 저장 스케줄러 시작
        startAutoSaveScheduler();
    }

    /**
     * 싱글톤 인스턴스 초기화
     */
    public static void initialize(@NotNull RPGMain plugin, @NotNull FirebaseService firebaseService) {
        if (instance == null) {
            instance = new QuestManager(plugin, firebaseService);
            LogUtil.info("QuestManager initialized");
        }
    }

    /**
     * 싱글톤 인스턴스 반환
     */
    public static QuestManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("QuestManager is not initialized!");
        }
        return instance;
    }

    /**
     * 퀘스트 등록
     */
    public void registerQuest(@NotNull Quest quest) {
        registeredQuests.put(quest.getId(), quest);
        LogUtil.debug("Registered quest: " + quest.getId());
    }

    /**
     * 퀘스트 가져오기
     */
    @Nullable
    public Quest getQuest(@NotNull String questId) {
        return registeredQuests.get(questId);
    }

    /**
     * 등록된 모든 퀘스트
     */
    public Collection<Quest> getAllQuests() {
        return new ArrayList<>(registeredQuests.values());
    }

    /**
     * 플레이어의 퀘스트 데이터 가져오기
     */
    @NotNull
    public PlayerQuestDTO getPlayerQuestData(@NotNull UUID playerId) {
        return playerQuestCache.computeIfAbsent(playerId,
                id -> new PlayerQuestDTO(id.toString()));
    }

    /**
     * 플레이어의 활성 퀘스트 목록
     */
    public List<QuestProgress> getActiveQuests(@NotNull UUID playerId) {
        PlayerQuestDTO data = getPlayerQuestData(playerId);
        return new ArrayList<>(data.activeQuests().values());
    }

    /**
     * 플레이어의 완료된 퀘스트 목록
     */
    public List<String> getCompletedQuests(@NotNull UUID playerId) {
        PlayerQuestDTO data = getPlayerQuestData(playerId);
        return new ArrayList<>(data.completedQuests().keySet());
    }

    /**
     * 퀘스트 시작
     */
    public boolean startQuest(@NotNull Player player, @NotNull String questId) {
        Quest quest = getQuest(questId);
        if (quest == null) {
            LogUtil.warning("Attempted to start unknown quest: " + questId);
            return false;
        }

        UUID playerId = player.getUniqueId();
        PlayerQuestDTO data = getPlayerQuestData(playerId);

        // 이미 진행 중인지 확인
        if (data.activeQuests().containsKey(questId)) {
            return false;
        }

        // 이미 완료했는지 확인 (반복 불가능한 경우)
        if (data.completedQuests().containsKey(questId) && !quest.isRepeatable()) {
            return false;
        }

        // 시작 조건 확인
        if (!quest.canStart(playerId)) {
            return false;
        }

        // 선행 퀘스트 확인
        if (!quest.arePrerequisitesComplete(getCompletedQuests(playerId))) {
            return false;
        }

        // 양자택일 퀘스트 확인
        if (quest.hasCompletedExclusiveQuests(getCompletedQuests(playerId))) {
            return false;
        }

        // 퀘스트 진행도 생성
        QuestProgress progress = quest.createProgress(playerId);
        data.activeQuests().put(questId, progress);

        // 저장 예약
        markForSave(playerId);

        LogUtil.info("Player " + player.getName() + " started quest: " + questId);
        return true;
    }

    /**
     * 퀘스트 목표 진행
     */
    public void progressObjective(@NotNull Player player, @NotNull Event event) {
        UUID playerId = player.getUniqueId();
        List<QuestProgress> activeQuests = getActiveQuests(playerId);

        for (QuestProgress questProgress : activeQuests) {
            Quest quest = getQuest(questProgress.getQuestId());
            if (quest == null) continue;

            List<String> objectivesToProgress = new ArrayList<>();

            if (quest.isSequential()) {
                // 순차 진행 - 현재 목표만
                int currentIndex = questProgress.getCurrentObjectiveIndex();
                if (currentIndex < quest.getObjectives().size()) {
                    objectivesToProgress.add(quest.getObjectives().get(currentIndex).getId());
                }
            } else {
                // 자유 진행 - 모든 미완료 목표
                objectivesToProgress = quest.getObjectives().stream()
                        .filter(obj -> !questProgress.isObjectiveComplete(obj.getId()))
                        .map(QuestObjective::getId)
                        .collect(Collectors.toList());
            }

            // 각 목표에 대해 진행도 체크
            for (String objectiveId : objectivesToProgress) {
                var objective = quest.getObjectives().stream()
                        .filter(obj -> obj.getId().equals(objectiveId))
                        .findFirst()
                        .orElse(null);

                if (objective == null) continue;

                if (objective.canProgress(event, player)) {
                    int increment = objective.calculateIncrement(event, player);
                    if (increment > 0) {
                        ObjectiveProgress objProgress = questProgress.getObjective(objectiveId);
                        objProgress.increment(increment);

                        // 목표 완료 체크
                        if (objective.isComplete(objProgress) && !objProgress.isCompleted()) {
                            objProgress.setCompleted(true);
                            objProgress.setCompletedAt(System.currentTimeMillis());

                            // 순차 진행인 경우 다음 목표로
                            if (quest.isSequential()) {
                                questProgress.setCurrentObjectiveIndex(
                                        questProgress.getCurrentObjectiveIndex() + 1);
                            }
                        }

                        markForSave(playerId);
                    }
                }
            }

            // 퀘스트 완료 체크
            if (isQuestComplete(quest, questProgress)) {
                completeQuest(player, questProgress.getQuestId());
            }
        }
    }

    /**
     * 퀘스트 완료 여부 확인
     */
    private boolean isQuestComplete(@NotNull Quest quest, @NotNull QuestProgress progress) {
        return quest.getObjectives().stream()
                .allMatch(obj -> progress.isObjectiveComplete(obj.getId()));
    }

    /**
     * 퀘스트 완료
     */
    private void completeQuest(@NotNull Player player, @NotNull String questId) {
        UUID playerId = player.getUniqueId();
        PlayerQuestDTO data = getPlayerQuestData(playerId);

        QuestProgress progress = data.activeQuests().remove(questId);
        if (progress == null) return;

        // 완료 시간 설정
        progress.setCompletedAt(Instant.now());
        progress.setState(QuestProgress.QuestState.COMPLETED);

        // 완료 정보 생성
        CompletedQuestDTO completed = new CompletedQuestDTO(
                questId,
                progress.getStartedAt().toEpochMilli(),
                progress.getCompletedAt().toEpochMilli(),
                progress.getCompletedAt().toEpochMilli() - progress.getStartedAt().toEpochMilli(),
                1 // 보상 획득 횟수
        );

        data.completedQuests().put(questId, completed);

        // 보상 지급
        Quest quest = getQuest(questId);
        if (quest != null) {
            quest.getReward().grant(player);
        }

        markForSave(playerId);
        LogUtil.info("Player " + player.getName() + " completed quest: " + questId);
    }

    /**
     * 플레이어 데이터 로드
     */
    public CompletableFuture<Void> loadPlayerData(@NotNull UUID playerId) {
        return firebaseService.loadPlayerQuestData(playerId.toString())
                .thenAccept(data -> {
                    if (data != null) {
                        playerQuestCache.put(playerId, data);
                        LogUtil.debug("Loaded quest data for player: " + playerId);
                    } else {
                        playerQuestCache.put(playerId, new PlayerQuestDTO(playerId.toString()));
                        LogUtil.debug("Created new quest data for player: " + playerId);
                    }
                })
                .exceptionally(ex -> {
                    LogUtil.error("Failed to load quest data for player: " + playerId, ex);
                    playerQuestCache.put(playerId, new PlayerQuestDTO(playerId.toString()));
                    return null;
                });
    }

    /**
     * 플레이어 데이터 저장
     */
    public CompletableFuture<Boolean> savePlayerData(@NotNull UUID playerId) {
        PlayerQuestDTO data = playerQuestCache.get(playerId);
        if (data == null) {
            return CompletableFuture.completedFuture(false);
        }

        // 마지막 업데이트 시간 갱신
        data.setLastUpdated(System.currentTimeMillis());

        return firebaseService.savePlayerQuestData(playerId.toString(), data)
                .thenApply(success -> {
                    if (success) {
                        pendingSaves.remove(playerId);
                        LogUtil.debug("Saved quest data for player: " + playerId);
                    } else {
                        LogUtil.error("Failed to save quest data for player: " + playerId);
                    }
                    return success;
                });
    }

    /**
     * 저장 예약
     */
    private void markForSave(@NotNull UUID playerId) {
        pendingSaves.add(playerId);
    }

    /**
     * 모든 대기 중인 데이터 저장
     */
    public void saveAllPendingData() {
        Set<UUID> toSave = new HashSet<>(pendingSaves);
        toSave.forEach(this::savePlayerData);
    }

    /**
     * 자동 저장 스케줄러
     */
    private void startAutoSaveScheduler() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (!pendingSaves.isEmpty()) {
                saveAllPendingData();
            }
        }, 20L * 60, 20L * 60); // 1분마다
    }

    /**
     * 매니저 종료
     */
    public void shutdown() {
        // 모든 데이터 저장
        saveAllPendingData();

        // 캐시 정리
        playerQuestCache.clear();
        pendingSaves.clear();
        registeredQuests.clear();

        LogUtil.info("QuestManager shutdown complete");
    }
}