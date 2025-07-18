package com.febrie.rpg.quest.manager;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.FirestoreRestService;
import com.febrie.rpg.dto.quest.CompletedQuestDTO;
import com.febrie.rpg.dto.quest.PlayerQuestDTO;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.quest.registry.QuestRegistry;
import com.febrie.rpg.quest.task.LocationCheckTask;
import com.febrie.rpg.util.LogUtil;
import com.febrie.rpg.util.ToastUtil;
import com.febrie.rpg.util.SoundUtil;
import com.febrie.rpg.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitTask;
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
    private final FirestoreRestService firestoreService;

    // 고정 퀘스트 맵 - enum으로 관리
    private final Map<QuestID, Quest> quests = new EnumMap<>(QuestID.class);

    // 플레이어별 퀘스트 데이터 캐시 (진행도 관리)
    private final Map<UUID, PlayerQuestData> playerDataCache = new ConcurrentHashMap<>();

    // 저장 대기열
    private final Set<UUID> pendingSaves = ConcurrentHashMap.newKeySet();
    
    // 지역 방문 체크 태스크
    private LocationCheckTask locationCheckTask;
    private BukkitTask locationCheckScheduler;

    /**
     * 플레이어별 퀘스트 데이터 (진행도 포함)
     */
    private static class PlayerQuestData {
        private final Map<QuestID, QuestProgress> activeQuests = new EnumMap<>(QuestID.class);
        private final Map<QuestID, CompletedQuestDTO> completedQuests = new EnumMap<>(QuestID.class);
        private long lastUpdated;

        PlayerQuestData() {
            this.lastUpdated = System.currentTimeMillis();
        }
    }

    /**
     * 프라이빗 생성자
     */
    private QuestManager(@NotNull RPGMain plugin, @NotNull FirestoreRestService firestoreService) {
        this.plugin = plugin;
        this.firestoreService = firestoreService;

        // 모든 퀘스트 초기화
        initializeQuests();
    }
    
    /**
     * 스케줄러 시작 (instance 설정 후 호출되어야 함)
     */
    private void startSchedulers() {
        // 자동 저장 스케줄러 시작
        startAutoSaveScheduler();
        
        // 지역 방문 체크 스케줄러 시작
        startLocationCheckScheduler();
    }

    /**
     * 모든 퀘스트 초기화 및 등록
     */
    private void initializeQuests() {
        // QuestRegistry에서 모든 퀘스트 생성 및 등록
        Map<QuestID, Quest> allQuests = QuestRegistry.createAllQuests();
        quests.putAll(allQuests);

        LogUtil.info("Initialized " + quests.size() + " quests");
    }


    /**
     * 싱글톤 인스턴스 초기화
     */
    public static void initialize(@NotNull RPGMain plugin, @NotNull FirestoreRestService firestoreService) {
        if (instance == null) {
            // instance를 먼저 설정하여 LocationCheckTask가 참조할 수 있도록 함
            instance = new QuestManager(plugin, firestoreService);
            // instance 설정 후 스케줄러 시작
            instance.startSchedulers();
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
     * 등록된 퀘스트 조회
     */
    @Nullable
    public Quest getQuest(@NotNull QuestID questId) {
        return quests.get(questId);
    }

    /**
     * 모든 등록된 퀘스트 반환
     */
    @NotNull
    public Collection<Quest> getAllQuests() {
        return Collections.unmodifiableCollection(quests.values());
    }

    /**
     * 카테고리별 퀘스트 조회
     */
    @NotNull
    public List<Quest> getQuestsByCategory(@NotNull Quest.QuestCategory category) {
        return quests.values().stream()
                .filter(quest -> quest.getCategory() == category)
                .collect(Collectors.toList());
    }

    /**
     * 플레이어의 퀘스트 데이터 가져오기
     */
    @NotNull
    private PlayerQuestData getPlayerData(@NotNull UUID playerId) {
        return playerDataCache.computeIfAbsent(playerId, id -> new PlayerQuestData());
    }

    /**
     * 플레이어의 활성 퀘스트 목록
     */
    @NotNull
    public List<QuestProgress> getActiveQuests(@NotNull UUID playerId) {
        PlayerQuestData data = getPlayerData(playerId);
        return new ArrayList<>(data.activeQuests.values());
    }

    /**
     * 플레이어의 완료된 퀘스트 ID 목록
     */
    @NotNull
    public List<QuestID> getCompletedQuests(@NotNull UUID playerId) {
        PlayerQuestData data = getPlayerData(playerId);
        return new ArrayList<>(data.completedQuests.keySet());
    }

    /**
     * 플레이어의 특정 퀘스트 진행도 조회
     */
    @Nullable
    public QuestProgress getQuestProgress(@NotNull UUID playerId, @NotNull QuestID questId) {
        PlayerQuestData data = getPlayerData(playerId);
        return data.activeQuests.get(questId);
    }

    /**
     * 특정 퀘스트 완료 여부
     */
    public boolean hasCompletedQuest(@NotNull UUID playerId, @NotNull QuestID questId) {
        PlayerQuestData data = getPlayerData(playerId);
        return data.completedQuests.containsKey(questId);
    }

    /**
     * 특정 퀘스트 진행 중 여부
     */
    public boolean hasActiveQuest(@NotNull UUID playerId, @NotNull QuestID questId) {
        PlayerQuestData data = getPlayerData(playerId);
        return data.activeQuests.containsKey(questId);
    }

    /**
     * 퀘스트 시작
     */
    public boolean startQuest(@NotNull Player player, @NotNull QuestID questId) {
        UUID playerId = player.getUniqueId();
        PlayerQuestData playerData = getPlayerData(playerId);

        // 이미 진행 중인지 확인
        if (playerData.activeQuests.containsKey(questId)) {
            return false;
        }

        Quest quest = getQuest(questId);
        if (quest == null) {
            LogUtil.warning("Attempted to start unknown quest: " + questId);
            return false;
        }

        // 이미 완료한 퀘스트인지 확인
        if (playerData.completedQuests.containsKey(questId) && !quest.isRepeatable()) {
            return false;
        }

        // 시작 조건 확인
        if (!quest.canStart(playerId)) {
            return false;
        }

        // 선행 퀘스트 확인
        if (!quest.arePrerequisitesComplete(playerData.completedQuests.keySet())) {
            return false;
        }

        // 양자택일 퀘스트 확인
        if (quest.hasCompletedExclusiveQuests(playerData.completedQuests.keySet())) {
            return false;
        }

        // 퀘스트 진행도 생성
        QuestProgress progress = quest.createProgress(playerId);
        playerData.activeQuests.put(questId, progress);
        playerData.lastUpdated = System.currentTimeMillis();

        // 저장 예약
        markForSave(playerId);
        
        
        // 퀘스트 시작 알림
        ToastUtil.showQuestProgressToast(player, quest, progress);
        
        // 채팅 메시지
        boolean isKorean = plugin.getLangManager().getPlayerLanguage(player).startsWith("ko");
        player.sendMessage(Component.text(isKorean ? "📋 새로운 퀘스트 시작: " : "📋 New quest started: ", ColorUtil.GOLD)
                .append(Component.text(quest.getDisplayName(isKorean), ColorUtil.RARE)));
        
        // 소리 재생
        SoundUtil.playOpenSound(player);

        LogUtil.info("Player " + player.getName() + " started quest: " + questId.getDisplayName());
        return true;
    }

    /**
     * 퀘스트 목표 진행
     */
    public void progressObjective(@NotNull Event event, @NotNull Player player) {
        UUID playerId = player.getUniqueId();
        PlayerQuestData playerData = getPlayerData(playerId);
        boolean dataChanged = false;

        for (Map.Entry<QuestID, QuestProgress> entry : playerData.activeQuests.entrySet()) {
            QuestProgress questProgress = entry.getValue();
            Quest quest = getQuest(entry.getKey());
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
                                // 토스트 알림 표시
                                ToastUtil.showQuestProgressToast(player, quest, questProgress);
                                
                                // 채팅 메시지
                                boolean isKorean = plugin.getLangManager().getPlayerLanguage(player).startsWith("ko");
                                player.sendMessage(Component.text("✓ " + quest.getObjectiveDescription(objective, isKorean), ColorUtil.SUCCESS));
                                
                                // 소리 재생
                                SoundUtil.playSuccessSound(player);
                                
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
                completeQuest(player, entry.getKey(), questProgress);
                dataChanged = true;
            }
        }

        if (dataChanged) {
            playerData.lastUpdated = System.currentTimeMillis();
            markForSave(playerId);
        }
    }

    /**
     * 퀘스트 완료 여부 확인
     */
    private boolean isQuestComplete(@NotNull Quest quest, @NotNull QuestProgress progress) {
        return progress.areAllObjectivesComplete();
    }

    /**
     * 퀘스트 완료 처리
     */
    private void completeQuest(@NotNull Player player, @NotNull QuestID questId, @NotNull QuestProgress progress) {
        UUID playerId = player.getUniqueId();
        PlayerQuestData playerData = getPlayerData(playerId);

        // 진행중 목록에서 제거
        playerData.activeQuests.remove(questId);

        // 완료 목록에 추가 (보상 미수령 상태)
        CompletedQuestDTO completed = new CompletedQuestDTO(
                questId.name(),
                Instant.now().toEpochMilli(),
                1,  // TODO: 완료 횟수 추적
                false  // 보상 미수령 상태
        );
        playerData.completedQuests.put(questId, completed);

        // 보상은 지급하지 않음 (NPC를 통해 수령)
        Quest quest = getQuest(questId);
        if (quest != null) {
            // 토스트 알림 표시
            ToastUtil.showQuestProgressToast(player, quest, progress);
            
            // 채팅 메시지
            boolean isKorean = plugin.getLangManager().getPlayerLanguage(player).startsWith("ko");
            player.sendMessage(Component.text("🎉 ", ColorUtil.GOLD)
                    .append(Component.text(quest.getDisplayName(isKorean), ColorUtil.LEGENDARY))
                    .append(Component.text(isKorean ? " 퀘스트를 완료했습니다!" : " quest completed!", ColorUtil.SUCCESS)));
            player.sendMessage(Component.text(isKorean ? "🎁 보상 NPC를 방문하여 보상을 수령하세요!" : "🎁 Visit the reward NPC to claim your rewards!", ColorUtil.INFO));
            
            // 소리 재생 (레벨업 사운드)
            SoundUtil.playSuccessSound(player);
            
            LogUtil.info("Player " + player.getName() + " completed quest: " + questId.name());
        }

        progress.complete();
    }

    /**
     * 플레이어 데이터 로드
     */
    public CompletableFuture<Void> loadPlayerData(@NotNull UUID playerId) {
        return firestoreService.loadPlayerQuestData(playerId.toString())
                .thenAccept(dto -> {
                    if (dto != null) {
                        PlayerQuestData data = new PlayerQuestData();

                        // 활성 퀘스트 변환
                        dto.activeQuests().forEach((idStr, progress) -> {
                            try {
                                QuestID questId = QuestID.valueOf(idStr);
                                data.activeQuests.put(questId, progress);
                            } catch (IllegalArgumentException e) {
                                LogUtil.warning("Unknown quest ID in player data: " + idStr);
                            }
                        });

                        // 완료된 퀘스트 변환
                        dto.completedQuests().forEach((idStr, completed) -> {
                            try {
                                QuestID questId = QuestID.valueOf(idStr);
                                data.completedQuests.put(questId, completed);
                            } catch (IllegalArgumentException e) {
                                LogUtil.warning("Unknown completed quest ID: " + idStr);
                            }
                        });

                        data.lastUpdated = dto.lastUpdated();
                        playerDataCache.put(playerId, data);
                    }
                })
                .exceptionally(ex -> {
                    LogUtil.error("Failed to load quest data for player: " + playerId, ex);
                    playerDataCache.put(playerId, new PlayerQuestData());
                    return null;
                });
    }

    /**
     * 플레이어 데이터 저장
     */
    public void savePlayerData(@NotNull UUID playerId) {
        PlayerQuestData data = playerDataCache.get(playerId);
        if (data == null) {
            return;
        }

        // DTO로 변환
        Map<String, QuestProgress> activeQuestsDto = new HashMap<>();
        data.activeQuests.forEach((id, progress) ->
                activeQuestsDto.put(id.name(), progress));

        Map<String, CompletedQuestDTO> completedQuestsDto = new HashMap<>();
        data.completedQuests.forEach((id, completed) ->
                completedQuestsDto.put(id.name(), completed));

        PlayerQuestDTO dto = new PlayerQuestDTO(
                playerId.toString(),
                activeQuestsDto,
                completedQuestsDto,
                data.lastUpdated
        );

        firestoreService.savePlayerQuestData(playerId.toString(), dto)
                .thenApply(success -> {
                    if (success) {
                        pendingSaves.remove(playerId);
                    } else {
                        LogUtil.error("Failed to save quest data for player: " + playerId);
                    }
                    return success;
                });
    }

    /**
     * 저장 예약
     */
    public void markForSave(@NotNull UUID playerId) {
        pendingSaves.add(playerId);
    }
    
    /**
     * 퀘스트 완료 체크
     * NPCInteractListener에서 호출됨
     */
    public boolean checkQuestCompletion(@NotNull UUID playerId, @NotNull QuestID questId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        QuestProgress progress = playerData.activeQuests.get(questId);
        if (progress == null) return false;
        
        Quest quest = getQuest(questId);
        if (quest == null) return false;
        
        // 모든 목표가 완료되었는지 확인
        boolean allObjectivesComplete = quest.getObjectives().stream()
                .allMatch(obj -> {
                    ObjectiveProgress objProgress = progress.getObjective(obj.getId());
                    return objProgress != null && objProgress.isCompleted();
                });
        
        if (allObjectivesComplete) {
            // 퀘스트 완료 처리
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && progress != null) {
                completeQuest(player, questId, progress);
                return true;
            }
        }
        return false;
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
     * 지역 방문 체크 스케줄러 시작
     */
    private void startLocationCheckScheduler() {
        locationCheckTask = new LocationCheckTask(plugin);
        // 3초마다 실행 (60틱 = 3초)
        locationCheckScheduler = Bukkit.getScheduler().runTaskTimer(plugin, locationCheckTask, 60L, 60L);
    }

    /**
     * 매니저 종료
     */
    public void shutdown() {
        // 지역 체크 스케줄러 중지
        if (locationCheckScheduler != null && !locationCheckScheduler.isCancelled()) {
            locationCheckScheduler.cancel();
        }
        
        // 지역 체크 태스크 캐시 정리
        if (locationCheckTask != null) {
            locationCheckTask.clearAllCaches();
        }
        
        // 모든 데이터 저장
        saveAllPendingData();

        // 캐시 정리
        playerDataCache.clear();
        pendingSaves.clear();

        LogUtil.info("QuestManager shutdown complete");
    }
    
    /**
     * 퀘스트를 보상 수령으로 표시
     */
    public void markQuestAsRewarded(@NotNull UUID playerId, @NotNull QuestID questId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        CompletedQuestDTO completed = playerData.completedQuests.get(questId);
        if (completed != null) {
            completed.setRewarded(true);
            markForSave(playerId);
        }
    }
    
    /**
     * 퀘스트 보상을 받았는지 확인
     */
    public boolean hasReceivedReward(@NotNull UUID playerId, @NotNull QuestID questId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        CompletedQuestDTO completed = playerData.completedQuests.get(questId);
        return completed != null && completed.isRewarded();
    }
    
    /**
     * 보상 미수령 퀘스트 목록 가져오기
     */
    public List<QuestID> getUnclaimedRewardQuests(@NotNull UUID playerId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        List<QuestID> unclaimed = new ArrayList<>();
        for (Map.Entry<QuestID, CompletedQuestDTO> entry : playerData.completedQuests.entrySet()) {
            if (!entry.getValue().isRewarded()) {
                unclaimed.add(entry.getKey());
            }
        }
        return unclaimed;
    }
}