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
        LogUtil.debug("Quest registered: " + quest.getId());
    }

    /**
     * 퀘스트 등록 해제
     */
    public void unregisterQuest(@NotNull String questId) {
        registeredQuests.remove(questId);
        LogUtil.debug("Quest unregistered: " + questId);
    }

    /**
     * 등록된 퀘스트 조회
     */
    @Nullable
    public Quest getQuest(@NotNull String questId) {
        return registeredQuests.get(questId);
    }

    /**
     * 모든 등록된 퀘스트 반환
     */
    @NotNull
    public Collection<Quest> getAllQuests() {
        return Collections.unmodifiableCollection(registeredQuests.values());
    }

    /**
     * 플레이어의 퀘스트 데이터 가져오기
     */
    @NotNull
    private PlayerQuestDTO getPlayerQuestData(@NotNull UUID playerId) {
        return playerQuestCache.computeIfAbsent(playerId,
                id -> new PlayerQuestDTO(id.toString()));
    }

    /**
     * 플레이어의 활성 퀘스트 목록
     */
    @NotNull
    public List<QuestProgress> getActiveQuests(@NotNull UUID playerId) {
        PlayerQuestDTO data = getPlayerQuestData(playerId);
        return new ArrayList<>(data.activeQuests().values());
    }

    /**
     * 플레이어의 완료된 퀘스트 목록
     */
    @NotNull
    public List<String> getCompletedQuests(@NotNull UUID playerId) {
        PlayerQuestDTO data = getPlayerQuestData(playerId);
        return new ArrayList<>(data.completedQuests().keySet());
    }

    /**
     * 특정 퀘스트 완료 여부
     */
    public boolean hasCompletedQuest(@NotNull UUID playerId, @NotNull String questId) {
        PlayerQuestDTO data = getPlayerQuestData(playerId);
        return data.completedQuests().containsKey(questId);
    }

    /**
     * 특정 퀘스트 진행 중 여부
     */
    public boolean hasActiveQuest(@NotNull UUID playerId, @NotNull String questId) {
        PlayerQuestDTO data = getPlayerQuestData(playerId);
        return data.activeQuests().containsKey(questId);
    }

    /**
     * 퀘스트 시작
     */
    public boolean startQuest(@NotNull Player player, @NotNull String questId) {
        UUID playerId = player.getUniqueId();
        PlayerQuestDTO currentData = getPlayerQuestData(playerId);

        // 이미 진행 중인지 확인
        if (currentData.activeQuests().containsKey(questId)) {
            return false;
        }

        Quest quest = getQuest(questId);
        if (quest == null) {
            LogUtil.warning("Attempted to start unknown quest: " + questId);
            return false;
        }

        // 이미 완료한 퀘스트인지 확인
        if (currentData.completedQuests().containsKey(questId) && !quest.isRepeatable()) {
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

        // 새로운 activeQuests 맵 생성
        Map<String, QuestProgress> newActiveQuests = new HashMap<>(currentData.activeQuests());
        newActiveQuests.put(questId, progress);

        // 새로운 PlayerQuestDTO 생성
        PlayerQuestDTO newData = new PlayerQuestDTO(
                currentData.playerId(),
                newActiveQuests,
                currentData.completedQuests(),
                System.currentTimeMillis()
        );

        // 캐시 업데이트
        playerQuestCache.put(playerId, newData);

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
        PlayerQuestDTO currentData = getPlayerQuestData(playerId);
        boolean dataChanged = false;

        // 수정을 위한 임시 맵 생성
        Map<String, QuestProgress> updatedActiveQuests = new HashMap<>(currentData.activeQuests());
        Map<String, CompletedQuestDTO> updatedCompletedQuests = new HashMap<>(currentData.completedQuests());

        for (Map.Entry<String, QuestProgress> entry : updatedActiveQuests.entrySet()) {
            QuestProgress questProgress = entry.getValue();
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
                        .map(QuestObjective::getId)
                        .filter(id -> !questProgress.isObjectiveComplete(id))
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
                        if (objProgress != null) {
                            objProgress.increment(increment);

                            // 목표 완료 체크
                            if (objective.isComplete(objProgress)) {
                                // 순차 진행인 경우 다음 목표로
                                if (quest.isSequential()) {
                                    questProgress.setCurrentObjectiveIndex(
                                            questProgress.getCurrentObjectiveIndex() + 1);
                                }
                            }

                            dataChanged = true;
                        }
                    }
                }
            }

            // 퀘스트 완료 체크
            if (isQuestComplete(quest, questProgress)) {
                // 완료 처리
                questProgress.setCompletedAt(Instant.now());
                questProgress.setState(QuestProgress.QuestState.COMPLETED);

                // 완료 정보 생성
                CompletedQuestDTO completed = new CompletedQuestDTO(
                        questProgress.getQuestId(),
                        questProgress.getStartedAt().toEpochMilli(),
                        questProgress.getCompletedAt().toEpochMilli(),
                        questProgress.getCompletedAt().toEpochMilli() - questProgress.getStartedAt().toEpochMilli(),
                        1 // 보상 획득 횟수
                );

                // 활성 퀘스트에서 제거하고 완료 목록에 추가
                updatedActiveQuests.remove(questProgress.getQuestId());
                updatedCompletedQuests.put(questProgress.getQuestId(), completed);

                // 보상 지급
                quest.getReward().grant(player);

                LogUtil.info("Player " + player.getName() + " completed quest: " + questProgress.getQuestId());
                dataChanged = true;
            }
        }

        // 데이터가 변경된 경우에만 새 DTO 생성
        if (dataChanged) {
            PlayerQuestDTO newData = new PlayerQuestDTO(
                    currentData.playerId(),
                    updatedActiveQuests,
                    updatedCompletedQuests,
                    System.currentTimeMillis()
            );

            playerQuestCache.put(playerId, newData);
            markForSave(playerId);
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
    public void savePlayerData(@NotNull UUID playerId) {
        PlayerQuestDTO data = playerQuestCache.get(playerId);
        if (data == null) {
            CompletableFuture.completedFuture(false);
            return;
        }

        // 저장 시 최신 타임스탬프로 새 DTO 생성
        PlayerQuestDTO dataToSave = new PlayerQuestDTO(
                data.playerId(),
                data.activeQuests(),
                data.completedQuests(),
                System.currentTimeMillis()
        );

        firebaseService.savePlayerQuestData(playerId.toString(), dataToSave)
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