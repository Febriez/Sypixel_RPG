package com.febrie.rpg.quest.manager;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.impl.QuestFirestoreService;
import com.febrie.rpg.dto.quest.PlayerQuestDTO;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.dto.quest.ActiveQuestDTO;
import com.febrie.rpg.dto.quest.ClaimedQuestDTO;
import com.febrie.rpg.dto.quest.CompletedQuestDTO;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.quest.registry.QuestRegistry;
import com.febrie.rpg.quest.reward.MixedReward;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.quest.service.QuestProgressService;
import com.febrie.rpg.quest.task.LocationCheckTask;
import com.febrie.rpg.quest.util.QuestUtil;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;
import com.febrie.rpg.util.SoundUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
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
    private final QuestFirestoreService questService;

    // 고정 퀘스트 맵 - enum으로 관리
    private final Map<QuestID, Quest> quests = new EnumMap<>(QuestID.class);

    // 플레이어별 퀘스트 데이터 캐시 (진행도 관리)
    private final Map<UUID, PlayerQuestData> playerDataCache = new ConcurrentHashMap<>();
    private QuestProgressService progressService;

    // 저장 대기열
    private final Set<UUID> pendingSaves = ConcurrentHashMap.newKeySet();

    // 지역 방문 체크 태스크
    private LocationCheckTask locationCheckTask;
    private BukkitTask locationCheckScheduler;
    
    // NPC 상호작용 목표 인덱스 (NPC ID -> 퀘스트 목표 참조)
    private final Map<String, Set<NPCObjectiveRef>> npcObjectiveIndex = new ConcurrentHashMap<>();
    
    // 플레이어별 활성 NPC 목표 캐시 (플레이어 UUID -> NPC ID -> 퀘스트 인스턴스 ID)
    private final Map<UUID, Map<String, Set<String>>> playerNPCObjectives = new ConcurrentHashMap<>();

    /**
     * NPC 목표 참조 클래스
     */
    public static class NPCObjectiveRef {
        public final String questInstanceId;
        public final String objectiveId;
        public final QuestID questId;
        public final UUID playerId;
        
        public NPCObjectiveRef(String questInstanceId, String objectiveId, QuestID questId, UUID playerId) {
            this.questInstanceId = questInstanceId;
            this.objectiveId = objectiveId;
            this.questId = questId;
            this.playerId = playerId;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NPCObjectiveRef that = (NPCObjectiveRef) o;
            return Objects.equals(questInstanceId, that.questInstanceId) &&
                   Objects.equals(objectiveId, that.objectiveId) &&
                   Objects.equals(playerId, that.playerId);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(questInstanceId, objectiveId, playerId);
        }
    }
    
    /**
     * 플레이어별 퀘스트 데이터 (진행도 포함)
     */
    private static class PlayerQuestData {
        private final Map<String, ActiveQuestDTO> activeQuests = new ConcurrentHashMap<>();
        private final Map<String, CompletedQuestDTO> completedQuests = new ConcurrentHashMap<>();
        private final Map<String, ClaimedQuestDTO> claimedQuests = new ConcurrentHashMap<>();
        private long lastUpdated; // long (primitive)

        PlayerQuestData() {
            this.lastUpdated = System.currentTimeMillis();
        }
    }

    /**
     * 프라이빗 생성자
     */
    private QuestManager(@NotNull RPGMain plugin, @Nullable QuestFirestoreService questService) {
        this.plugin = plugin;
        this.questService = questService;

        // 모든 퀘스트 초기화
        initializeQuests();
        
        // Progress Service 초기화
        this.progressService = new QuestProgressService(plugin);
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

    }

    /**
     * 퀘스트 데이터 리로드
     * 언어 파일이나 설정이 변경되었을 때 호출
     */
    public void reloadQuests() {
        plugin.getLogger().info("퀘스트 데이터 리로드 중...");

        quests.clear();

        // 퀘스트 재초기화
        initializeQuests();

        // 지역 체크 태스크 캐시 정리 (위치 관련 목표가 변경될 수 있으므로)
        if (locationCheckTask != null) {
            locationCheckTask.clearAllCaches();
        }

        plugin.getLogger().info("퀘스트 데이터 리로드 완료! 총 " + quests.size() + "개의 퀘스트가 로드되었습니다.");
    }


    /**
     * 싱글톤 인스턴스 초기화
     */
    public static void initialize(@NotNull RPGMain plugin, @Nullable QuestFirestoreService questService) {
        if (instance == null) {
            // instance를 먼저 설정하여 LocationCheckTask가 참조할 수 있도록 함
            instance = new QuestManager(plugin, questService);
            // instance 설정 후 스케줄러 시작
            instance.startSchedulers();
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
    public List<Quest> getQuestsByCategory(@NotNull QuestCategory category) {
        return quests.values().stream().filter(quest -> quest.getCategory() == category).collect(Collectors.toList());
    }

    /**
     * 플레이어의 퀘스트 데이터 가져오기
     */
    @NotNull
    private PlayerQuestData getPlayerData(@NotNull UUID playerId) {
        return playerDataCache.computeIfAbsent(playerId, id -> new PlayerQuestData());
    }

    /**
     * QuestProgressService 반환
     */
    public QuestProgressService getProgressService() {
        return progressService;
    }
    
    /**
     * 활성 퀘스트 업데이트 (QuestProgressService에서 호출)
     */
    public void updateActiveQuest(@NotNull UUID playerId, @NotNull String instanceId, @NotNull ActiveQuestDTO updatedData) {
        PlayerQuestData playerData = getPlayerData(playerId);
        playerData.activeQuests.put(instanceId, updatedData);
        playerData.lastUpdated = System.currentTimeMillis();
        markForSave(playerId);
    }
    
    /**
     * 플레이어의 활성 퀘스트 목록
     */
    @NotNull
    public Map<String, ActiveQuestDTO> getActiveQuests(@NotNull UUID playerId) {
        PlayerQuestData data = getPlayerData(playerId);
        return new HashMap<>(data.activeQuests);
    }

    /**
     * 플레이어의 완료된 퀘스트 목록 (보상 미수령)
     */
    @NotNull
    public Map<String, CompletedQuestDTO> getCompletedQuests(@NotNull UUID playerId) {
        PlayerQuestData data = getPlayerData(playerId);
        return new HashMap<>(data.completedQuests);
    }
    
    /**
     * 플레이어의 보상을 모두 수령한 퀘스트 목록
     */
    @NotNull
    public Map<String, ClaimedQuestDTO> getClaimedQuests(@NotNull UUID playerId) {
        PlayerQuestData data = getPlayerData(playerId);
        return new HashMap<>(data.claimedQuests);
    }

    /**
     * 플레이어의 특정 퀘스트 진행도 조회
     */
    @Nullable
    public QuestProgress getQuestProgress(@NotNull UUID playerId, @NotNull String instanceId) {
        PlayerQuestData data = getPlayerData(playerId);
        ActiveQuestDTO activeData = data.activeQuests.get(instanceId);
        if (activeData == null) return null;
        
        // ActiveQuestDTO에서 QuestProgress 생성
        QuestID questId = QuestID.valueOf(activeData.questId());
        Map<String, ObjectiveProgress> progress = new HashMap<>();
        activeData.progress().forEach((key, value) -> progress.put(key, ObjectiveProgress.from(value, playerId)));
        return new QuestProgress(questId, playerId, progress);
    }

    /**
     * 특정 퀘스트 완료 여부
     */
    public boolean hasCompletedQuest(@NotNull UUID playerId, @NotNull QuestID questId) {
        PlayerQuestData data = getPlayerData(playerId);
        return QuestManagerHelper.hasCompletedQuest(data.completedQuests, data.claimedQuests, questId);
    }

    /**
     * 특정 퀘스트 진행 중 여부
     */
    public boolean hasActiveQuest(@NotNull UUID playerId, @NotNull QuestID questId) {
        PlayerQuestData data = getPlayerData(playerId);
        return QuestManagerHelper.hasActiveQuest(data.activeQuests, questId);
    }

    /**
     * 퀘스트 시작
     */
    public boolean startQuest(@NotNull Player player, @NotNull QuestID questId) {
        UUID playerId = player.getUniqueId();
        PlayerQuestData playerData = getPlayerData(playerId);

        // 이미 진행 중인지 확인
        if (QuestManagerHelper.hasActiveQuest(playerData.activeQuests, questId)) {
            plugin.getLogger().info("퀘스트 시작 실패 [" + player.getName() + "]: " + questId.name() + " - 이미 진행 중");
            return false;
        }

        Quest quest = getQuest(questId);
        if (quest == null) {
            plugin.getLogger().warning("퀘스트 시작 실패 [" + player.getName() + "]: " + questId.name() + " - 퀘스트를 찾을 수 없음");
            return false;
        }

        // 완료 제한 확인
        int totalCompletions = QuestManagerHelper.getTotalCompletionCount(
                playerData.completedQuests, playerData.claimedQuests, questId);
        int completionLimit = quest.getCompletionLimit();
        
        // 완료 불가 퀘스트
        if (completionLimit == 0) {
            plugin.getLogger().info("퀘스트 시작 실패 [" + player.getName() + "]: " + questId.name() + " - 완료 불가 퀘스트");
            return false;
        }
        
        // 완료 횟수 제한 확인 (-1은 무제한)
        if (completionLimit > 0 && totalCompletions >= completionLimit) {
            plugin.getLogger().info("퀘스트 시작 실패 [" + player.getName() + "]: " + questId.name() + " - 완료 횟수 제한 초과 (" + totalCompletions + "/" + completionLimit + ")");
            return false;
        }

        // 시작 조건 확인 - Player 객체는 이미 매개변수로 전달됨
        if (!quest.canStart(player)) {
            plugin.getLogger().info("퀘스트 시작 실패 [" + player.getName() + "]: " + questId.name() + " - 시작 조건 미충족 (레벨: " + player.getLevel() + ", 필요 레벨: " + quest.getMinLevel() + ")");
            return false;
        }

        // 완료한 퀘스트 ID 목록 생성
        Set<QuestID> completedQuestIds = new HashSet<>();
        playerData.completedQuests.values().forEach(c -> {
            try {
                completedQuestIds.add(QuestID.valueOf(c.questId()));
            } catch (IllegalArgumentException ignored) {}
        });
        playerData.claimedQuests.values().forEach(c -> {
            try {
                completedQuestIds.add(QuestID.valueOf(c.questId()));
            } catch (IllegalArgumentException ignored) {}
        });
        
        // 선행 퀘스트 확인
        if (!quest.arePrerequisitesComplete(completedQuestIds)) {
            plugin.getLogger().info("퀘스트 시작 실패 [" + player.getName() + "]: " + questId.name() + " - 선행 퀘스트 미완료");
            return false;
        }

        // 양자택일 퀘스트 확인
        if (quest.hasCompletedExclusiveQuests(completedQuestIds)) {
            plugin.getLogger().info("퀘스트 시작 실패 [" + player.getName() + "]: " + questId.name() + " - 양자택일 퀘스트 이미 완료");
            return false;
        }

        // 퀘스트 진행도 생성 - 각 플레이어마다 고유한 instanceId 생성
        String instanceId = UUID.randomUUID().toString();
        QuestProgress progress = quest.createProgress(playerId);
        ActiveQuestDTO activeData = ActiveQuestDTO.create(
            questId.name(),
            instanceId,
            progress.getObjectives()
        );
        playerData.activeQuests.put(instanceId, activeData);
        playerData.lastUpdated = System.currentTimeMillis();

        // NPC 목표 인덱스 등록
        registerNPCObjectives(playerId, instanceId, quest);
        
        // 저장 예약
        markForSave(playerId);


        // 퀘스트 시작 알림
        QuestUtil.notifyQuestStart(player, quest, progress, plugin);
        
        plugin.getLogger().info("퀘스트 시작 완료 [" + player.getName() + "]: " + questId.name() + " (Instance: " + instanceId + ")");
        return true;
    }

    /**
     * 퀘스트 목표 진행
     */
    public void progressObjective(@NotNull Event event, @NotNull Player player) {
        UUID playerId = player.getUniqueId();
        PlayerQuestData playerData = getPlayerData(playerId);
        boolean dataChanged = false;

        for (Map.Entry<String, ActiveQuestDTO> entry : playerData.activeQuests.entrySet()) {
            String instanceId = entry.getKey();
            ActiveQuestDTO activeData = entry.getValue();
            Quest quest = getQuest(QuestID.valueOf(activeData.questId()));
            if (quest == null) continue;
            
            // DTO에서 QuestProgress 복원
            Map<String, ObjectiveProgress> progressMap = new HashMap<>();
            activeData.progress().forEach((key, value) -> progressMap.put(key, ObjectiveProgress.from(value, playerId)));
            QuestProgress questProgress = new QuestProgress(QuestID.valueOf(activeData.questId()), playerId, progressMap);

            List<String> objectivesToProgress = new ArrayList<>();

            if (quest.isSequential()) {
                // 순차 진행 - 현재 목표만
                int currentIndex = questProgress.getCurrentObjectiveIndex();
                if (currentIndex < quest.getObjectives().size()) {
                    objectivesToProgress.add(quest.getObjectives().get(currentIndex).getId());
                }
            } else {
                // 자유 진행 - 모든 미완료 목표
                objectivesToProgress = quest.getObjectives().stream().map(QuestObjective::getId).filter(id -> !questProgress.isObjectiveComplete(id)).collect(Collectors.toList());
            }

            // 각 목표에 대해 진행도 체크
            for (String objectiveId : objectivesToProgress) {
                var objective = quest.getObjectives().stream().filter(obj -> obj.getId().equals(objectiveId)).findFirst().orElse(null);

                if (objective == null) continue;

                if (objective.canProgress(event, player)) {
                    int increment = objective.calculateIncrement(event, player);
                    if (increment > 0) {
                        ObjectiveProgress objProgress = questProgress.getObjective(objectiveId);
                        if (objProgress != null) {
                            objProgress.increment(increment);

                            // 목표 완료 체크
                            if (objective.isComplete(objProgress)) {
                                // 목표 완료 알림
                                QuestUtil.notifyObjectiveComplete(player, quest, questProgress, objective, plugin);

                                // 순차 진행인 경우 다음 목표로
                                if (quest.isSequential()) {
                                    questProgress.setCurrentObjectiveIndex(questProgress.getCurrentObjectiveIndex() + 1);
                                }
                            }

                            dataChanged = true;
                        }
                    }
                }
            }

            // 진행도가 변경된 경우 새로운 DTO 생성
            if (dataChanged) {
                ActiveQuestDTO updatedData = ActiveQuestDTO.create(
                    activeData.questId(),
                    activeData.instanceId(),
                    questProgress.getObjectives()
                );
                playerData.activeQuests.put(instanceId, updatedData);
            }
            
            // 퀘스트 완료 체크
            if (isQuestComplete(quest, questProgress)) {
                completeQuest(player, instanceId);
                dataChanged = true;
            }
        }

        if (dataChanged) {
            playerData.lastUpdated = System.currentTimeMillis();
            markForSave(playerId);
        }
    }

    /**
     * 퀘스트 취소
     */
    public boolean cancelQuest(@NotNull UUID playerId, @NotNull String instanceId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        ActiveQuestDTO activeData = playerData.activeQuests.get(instanceId);
        
        if (activeData == null) {
            return false;
        }
        
        try {
            QuestID questId = QuestID.valueOf(activeData.questId());
            Quest quest = getQuest(questId);
            
            // 퀘스트 제거
            playerData.activeQuests.remove(instanceId);
            
            // NPC 인덱스 정리
            if (quest != null) {
                unregisterNPCObjectives(playerId, instanceId, quest);
            }
            
            // 저장 예약
            markForSave(playerId);
            
            return true;
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("잘못된 퀘스트 ID로 취소 시도: " + activeData.questId());
            return false;
        }
    }
    
    /**
     * 퀘스트 완료 여부 확인
     */
    private boolean isQuestComplete(@NotNull Quest quest, @NotNull QuestProgress progress) {
        return progress.areAllObjectivesComplete();
    }

    /**
     * 퀘스트 완료 처리 (QuestProgressService에서 호출)
     */
    public void completeQuest(@NotNull Player player, @NotNull String instanceId) {
        UUID playerId = player.getUniqueId();
        PlayerQuestData playerData = getPlayerData(playerId);
        ActiveQuestDTO activeData = playerData.activeQuests.get(instanceId);
        if (activeData == null) return;
        
        // ActiveQuestDTO에서 QuestProgress 생성
        Map<String, ObjectiveProgress> progressMap = new HashMap<>();
        activeData.progress().forEach((key, value) -> progressMap.put(key, ObjectiveProgress.from(value, playerId)));
        QuestProgress progress = new QuestProgress(QuestID.valueOf(activeData.questId()), playerId, progressMap);
        QuestID questId = QuestID.valueOf(activeData.questId());

        // 진행중 목록에서 제거
        playerData.activeQuests.remove(instanceId);
        
        // NPC 목표 인덱스 제거
        Quest quest = getQuest(questId);
        if (quest != null) {
            unregisterNPCObjectives(playerId, instanceId, quest);
        }

        // 총 완료 횟수 계산
        int totalCompletions = QuestManagerHelper.getTotalCompletionCount(
                playerData.completedQuests, playerData.claimedQuests, questId);
        int newCompletionCount = totalCompletions + 1;
        
        // 보상 아이템 개수 확인
        int totalItemCount = 0;
        if (quest != null && quest.getReward() instanceof com.febrie.rpg.quest.reward.impl.BasicReward basicReward) {
            totalItemCount = basicReward.getItems().size();
        } else if (quest != null && quest.getReward() instanceof MixedReward mixedReward) {
            totalItemCount = mixedReward.getItems().size();
        }
        
        // 완료 목록에 추가 (보상 미수령 상태)
        CompletedQuestDTO completed = CompletedQuestDTO.create(
                questId.name(),
                instanceId,
                newCompletionCount,
                totalItemCount
        );
        playerData.completedQuests.put(instanceId, completed);

        // 저장 예약
        markForSave(playerId);
        
        if (quest != null) {
            // 퀘스트 완료 알림
            QuestUtil.notifyQuestComplete(player, quest, progress, plugin);
        }

        progress.complete();
    }

    /**
     * 플레이어 데이터 로드
     */
    public CompletableFuture<Void> loadPlayerData(@NotNull UUID playerId) {
        if (questService == null) {
            plugin.getLogger().warning("QuestFirestoreService가 null입니다. 빈 퀘스트 데이터를 사용합니다.");
            playerDataCache.put(playerId, new PlayerQuestData());
            return CompletableFuture.completedFuture(null);
        }

        return questService.getPlayerQuests(playerId).thenAccept(dto -> {
            PlayerQuestData data = new PlayerQuestData();

            // 활성 퀘스트 변환
            data.activeQuests.putAll(dto.activeQuests());

            // 완료된 퀘스트 변환 (보상 미수령)
            data.completedQuests.putAll(dto.completedQuests());

            // 보상 수령 완료 퀘스트 변환
            data.claimedQuests.putAll(dto.claimedQuests());

            data.lastUpdated = dto.lastUpdated();
            playerDataCache.put(playerId, data);
            
            // 활성 퀘스트의 NPC 목표 인덱스 재구성
            rebuildNPCIndexForPlayer(playerId, data);

            plugin.getLogger().info("퀘스트 데이터 로드 완료 [" + playerId + "]: " + 
                "활성 퀘스트 " + data.activeQuests.size() + "개, " + 
                "완료 퀘스트 " + data.completedQuests.size() + "개, " +
                "보상 수령 완료 " + data.claimedQuests.size() + "개");
        }).exceptionally(ex -> {
            plugin.getLogger().severe("퀘스트 데이터 로드 실패 [" + playerId + "]: " + ex.getMessage());
            playerDataCache.put(playerId, new PlayerQuestData());
            return null;
        });
    }

    /**
     * 플레이어 데이터 언로드 (로그아웃 시)
     */
    public void unloadPlayerData(@NotNull UUID playerId) {
        // 데이터 저장
        if (pendingSaves.contains(playerId)) {
            savePlayerData(playerId);
        }
        
        // NPC 인덱스 정리
        cleanupPlayerNPCIndex(playerId);
        
        // 캐시에서 제거
        playerDataCache.remove(playerId);
        pendingSaves.remove(playerId);
    }
    
    /**
     * 플레이어의 NPC 인덱스 정리
     */
    private void cleanupPlayerNPCIndex(@NotNull UUID playerId) {
        // 플레이어별 NPC 목표 캐시 제거
        playerNPCObjectives.remove(playerId);
        
        // 전체 NPC 인덱스에서 해당 플레이어 참조 제거
        npcObjectiveIndex.values().forEach(refs -> 
            refs.removeIf(ref -> ref.playerId.equals(playerId))
        );
        
        // 빈 인덱스 제거
        npcObjectiveIndex.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
    
    /**
     * PlayerQuestData를 PlayerQuestDTO로 변환
     */
    @NotNull
    private PlayerQuestDTO convertToDTO(@NotNull UUID playerId, @NotNull PlayerQuestData data) {
        PlayerQuestDTO dto = new PlayerQuestDTO(
            playerId.toString(),
            new HashMap<>(data.activeQuests),
            new HashMap<>(data.completedQuests),
            new HashMap<>(data.claimedQuests),
            data.lastUpdated
        );
        
        // 디버그 로그
        plugin.getLogger().info("DTO 변환 완료 [" + playerId + "]: " + 
            "활성 퀘스트 " + data.activeQuests.size() + "개, " +
            "완료 퀘스트 " + data.completedQuests.size() + "개, " +
            "보상 수령 완료 " + data.claimedQuests.size() + "개");
        
        return dto;
    }

    /**
     * 플레이어 데이터 저장
     */
    public void savePlayerData(@NotNull UUID playerId) {
        PlayerQuestData data = playerDataCache.get(playerId);
        if (data == null) {
            pendingSaves.remove(playerId);
            return;
        }

        // DTO로 변환
        PlayerQuestDTO dto = convertToDTO(playerId, data);

        // QuestFirestoreService를 사용하여 저장
        if (questService != null) {
            questService.savePlayerQuests(playerId, dto).thenRun(() -> {
                pendingSaves.remove(playerId);
                plugin.getLogger().info("퀘스트 데이터 저장 완료: " + playerId);
            }).exceptionally(throwable -> {
                plugin.getLogger().severe("퀘스트 데이터 저장 실패 [" + playerId + "]: " + throwable.getMessage());
                // 실패한 경우 다시 저장 대기열에 추가
                pendingSaves.add(playerId);
                return null;
            });
        } else {
            plugin.getLogger().warning("QuestFirestoreService가 null입니다. 퀘스트 데이터를 저장할 수 없습니다.");
            pendingSaves.remove(playerId);
        }
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
    public boolean checkQuestCompletion(@NotNull UUID playerId, @NotNull String instanceId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        ActiveQuestDTO activeData = playerData.activeQuests.get(instanceId);
        if (activeData == null) return false;

        QuestID questId = QuestID.valueOf(activeData.questId());
        Quest quest = getQuest(questId);
        if (quest == null) return false;

        // ActiveQuestDTO에서 QuestProgress 생성
        Map<String, ObjectiveProgress> progressMap = new HashMap<>();
        activeData.progress().forEach((key, value) -> progressMap.put(key, ObjectiveProgress.from(value, playerId)));
        QuestProgress progress = new QuestProgress(questId, playerId, progressMap);

        // 모든 목표가 완료되었는지 확인
        boolean allObjectivesComplete = quest.getObjectives().stream().allMatch(obj -> {
            ObjectiveProgress objProgress = progress.getObjective(obj.getId());
            return objProgress != null && objProgress.isCompleted();
        });

        if (allObjectivesComplete) {
            // 퀘스트 완료 처리
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                completeQuest(player, instanceId);
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
     * 플레이어 퇴장 시 캐시 정리
     */
    public void clearPlayerCache(@NotNull UUID playerId) {
        // LocationCheckTask 캐시 정리
        if (locationCheckTask != null) {
            locationCheckTask.clearPlayerCache(playerId);
        }
    }
    
    /**
     * 매니저 종료
     */
    public void shutdown() {
        plugin.getLogger().info("QuestManager 종료 중...");

        // 지역 체크 스케줄러 중지
        if (locationCheckScheduler != null && !locationCheckScheduler.isCancelled()) {
            locationCheckScheduler.cancel();
        }

        // 지역 체크 태스크 캐시 정리
        if (locationCheckTask != null) {
            locationCheckTask.clearAllCaches();
        }

        // 모든 데이터를 동기적으로 저장
        plugin.getLogger().info("퀘스트 데이터 저장 중... (대기 중인 플레이어: " + pendingSaves.size() + "명)");

        // 캐시에 있는 모든 플레이어 데이터도 저장
        Set<UUID> allPlayers = new HashSet<>(playerDataCache.keySet());
        allPlayers.addAll(pendingSaves);

        List<CompletableFuture<Void>> saveFutures = new ArrayList<>();

        for (UUID playerId : allPlayers) {
            PlayerQuestData data = playerDataCache.get(playerId);
            if (data != null && questService != null) {
                // DTO로 변환 (마지막 업데이트 시간 갱신)
                data.lastUpdated = System.currentTimeMillis();
                PlayerQuestDTO dto = convertToDTO(playerId, data);

                CompletableFuture<Void> saveFuture = questService.savePlayerQuests(playerId, dto).thenRun(() -> plugin.getLogger().info("퀘스트 데이터 저장 완료: " + playerId)).exceptionally(throwable -> {
                    plugin.getLogger().severe("퀘스트 데이터 저장 실패: " + playerId + " - " + throwable.getMessage());
                    return null;
                });

                saveFutures.add(saveFuture);
            }
        }

        // 모든 저장 작업이 완료될 때까지 대기 (최대 10초)
        try {
            CompletableFuture.allOf(saveFutures.toArray(new CompletableFuture<?>[0])).get(10, TimeUnit.SECONDS);
            plugin.getLogger().info("모든 퀘스트 데이터 저장 완료!");
        } catch (Exception e) {
            plugin.getLogger().severe("퀘스트 데이터 저장 중 오류 발생: " + e.getMessage());
        }

        // 캐시 및 인덱스 정리
        playerDataCache.clear();
        pendingSaves.clear();
        npcObjectiveIndex.clear();
        playerNPCObjectives.clear();

        plugin.getLogger().info("QuestManager 종료 완료.");
    }

    /**
     * 퀘스트를 보상 수령으로 표시 (모든 보상이 수령되었을 때만 호출)
     */
    public void markQuestAsRewarded(@NotNull UUID playerId, @NotNull String instanceId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        CompletedQuestDTO completed = playerData.completedQuests.get(instanceId);
        if (completed != null) {
            // CompletedQuestDTO에서 ClaimedQuestDTO로 이동
            ClaimedQuestDTO claimed = ClaimedQuestDTO.from(completed);
            playerData.completedQuests.remove(instanceId);
            playerData.claimedQuests.put(instanceId, claimed);
            markForSave(playerId);
        }
    }

    /**
     * 퀘스트 보상을 모두 받았는지 확인
     */
    public boolean hasReceivedAllRewards(@NotNull UUID playerId, @NotNull String instanceId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        // ClaimedQuests에 있으면 모든 보상을 받은 것
        return playerData.claimedQuests.containsKey(instanceId);
    }
    
    /**
     * 특정 보상을 받았는지 확인 (부분 수령 체크)
     */
    public boolean hasReceivedReward(@NotNull UUID playerId, @NotNull String instanceId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        
        // ClaimedQuests에 있으면 모든 보상을 받은 것
        if (playerData.claimedQuests.containsKey(instanceId)) {
            return true;
        }
        
        // CompletedQuests에서 부분 수령 확인
        CompletedQuestDTO completed = playerData.completedQuests.get(instanceId);
        if (completed != null) {
            // 즉시 보상과 모든 아이템이 수령되었는지 확인
            return completed.areAllRewardsClaimed();
        }
        
        return false;
    }
    
    /**
     * 완료된 퀘스트 데이터 가져오기 (인스턴스 ID로)
     */
    @Nullable
    public CompletedQuestDTO getCompletedQuestData(@NotNull UUID playerId, @NotNull String instanceId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        return playerData.completedQuests.get(instanceId);
    }
    
    /**
     * 퀘스트 ID로 최신 완료된 퀘스트 찾기
     */
    @Nullable
    public CompletedQuestDTO findLatestCompletedQuest(@NotNull UUID playerId, @NotNull QuestID questId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        return playerData.completedQuests.values().stream()
                .filter(c -> c.questId().equals(questId.name()))
                .max((a, b) -> Long.compare(a.completedAt(), b.completedAt()))
                .orElse(null);
    }
    
    /**
     * 즉시 보상 수령 처리
     */
    public void markInstantRewardsClaimed(@NotNull UUID playerId, @NotNull String instanceId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        CompletedQuestDTO completed = playerData.completedQuests.get(instanceId);
        if (completed != null) {
            // DTO는 불변이므로 새 인스턴스 생성
            CompletedQuestDTO updated = completed.withInstantRewardsClaimed();
            playerData.completedQuests.put(instanceId, updated);
            markForSave(playerId);
        }
    }
    
    /**
     * 아이템 보상 수령 처리
     */
    public void markItemRewardClaimed(@NotNull UUID playerId, @NotNull String instanceId, int itemIndex) {
        PlayerQuestData playerData = getPlayerData(playerId);
        CompletedQuestDTO completed = playerData.completedQuests.get(instanceId);
        if (completed != null) {
            // DTO는 불변이므로 새 인스턴스 생성
            CompletedQuestDTO updated = completed.withItemClaimed(itemIndex);
            playerData.completedQuests.put(instanceId, updated);
            
            // 모든 보상이 수령되었는지 확인
            if (updated.areAllRewardsClaimed()) {
                // 모든 보상이 수령되었으므로 ClaimedQuestDTO로 이동
                markQuestAsRewarded(playerId, instanceId);
            } else {
                markForSave(playerId);
            }
        }
    }
    
    /**
     * 특정 아이템 보상이 수령되었는지 확인
     */
    public boolean hasClaimedItem(@NotNull UUID playerId, @NotNull String instanceId, int itemIndex) {
        PlayerQuestData playerData = getPlayerData(playerId);
        CompletedQuestDTO completed = playerData.completedQuests.get(instanceId);
        return completed != null && completed.isItemClaimed(itemIndex);
    }
    
    /**
     * 수령하지 않은 아이템 인덱스 목록 가져오기
     */
    @NotNull
    public List<Integer> getUnclaimedItemIndices(@NotNull UUID playerId, @NotNull String instanceId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        CompletedQuestDTO completed = playerData.completedQuests.get(instanceId);
        if (completed != null) {
            return new ArrayList<>(completed.unclaimedItemIndices());
        }
        return new ArrayList<>();
    }

    /**
     * 보상 미수령 퀘스트 목록 가져오기
     */
    public List<String> getUnclaimedRewardQuests(@NotNull UUID playerId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        // completedQuests에 있는 모든 퀘스트가 보상 미수령 상태
        return new ArrayList<>(playerData.completedQuests.keySet());
    }

    /**
     * 인벤토리 부족으로 받지 못한 아이템 처리
     * 현재 구조에서는 CompletedQuestData의 unclaimedItemIndices로 관리하므로 필요 없음
     */

    /**
     * 특정 퀘스트의 미수령 아이템 가져오기
     */
    @NotNull
    public List<ItemStack> getUnclaimedItems(@NotNull UUID playerId, @NotNull String instanceId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        CompletedQuestDTO completed = playerData.completedQuests.get(instanceId);
        if (completed != null) {
            QuestID questId = QuestID.valueOf(completed.questId());
            Quest quest = getQuest(questId);
            if (quest != null) {
                List<ItemStack> unclaimedItems = new ArrayList<>();
                
                if (quest.getReward() instanceof BasicReward basicReward) {
                    List<ItemStack> allItems = basicReward.getItems();
                    for (int index : completed.unclaimedItemIndices()) {
                        if (index < allItems.size()) {
                            unclaimedItems.add(allItems.get(index));
                        }
                    }
                } else if (quest.getReward() instanceof MixedReward mixedReward) {
                    List<ItemStack> allItems = mixedReward.getItems();
                    for (int index : completed.unclaimedItemIndices()) {
                        if (index < allItems.size()) {
                            unclaimedItems.add(allItems.get(index));
                        }
                    }
                }
                
                return unclaimedItems;
            }
        }
        return new ArrayList<>();
    }
    
    /**
     * 퀘스트 진행도 업데이트 (내부용)
     */
    void updateQuestProgress(@NotNull UUID playerId, @NotNull String instanceId, @NotNull QuestProgress progress) {
        PlayerQuestData playerData = getPlayerData(playerId);
        ActiveQuestDTO activeData = playerData.activeQuests.get(instanceId);
        
        if (activeData == null) {
            return;
        }
        
        // QuestProgress를 ActiveQuestDTO로 변환하여 저장
        ActiveQuestDTO updatedData = ActiveQuestDTO.create(
            activeData.questId(),
            instanceId,
            progress.getObjectives()
        );
        
        playerData.activeQuests.put(instanceId, updatedData);
        playerData.lastUpdated = System.currentTimeMillis();
    }
    
    /**
     * NPC 목표 인덱스 등록 (퀘스트 시작 시)
     */
    private void registerNPCObjectives(@NotNull UUID playerId, @NotNull String instanceId, @NotNull Quest quest) {
        for (QuestObjective objective : quest.getObjectives()) {
            if (objective instanceof com.febrie.rpg.quest.objective.impl.InteractNPCObjective npcObjective) {
                String npcId = npcObjective.getNpcId();
                
                // NPC 전체 인덱스에 추가
                NPCObjectiveRef ref = new NPCObjectiveRef(instanceId, objective.getId(), quest.getId(), playerId);
                npcObjectiveIndex.computeIfAbsent(npcId, k -> ConcurrentHashMap.newKeySet()).add(ref);
                
                // 플레이어별 NPC 목표 캐시에 추가
                playerNPCObjectives.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(npcId, k -> ConcurrentHashMap.newKeySet())
                    .add(instanceId);
            }
        }
    }
    
    /**
     * 플레이어의 NPC 인덱스 재구성 (데이터 로드 시)
     */
    private void rebuildNPCIndexForPlayer(@NotNull UUID playerId, @NotNull PlayerQuestData data) {
        playerNPCObjectives.remove(playerId);
        
        // 플레이어의 모든 활성 퀘스트 인덱싱
        for (Map.Entry<String, ActiveQuestDTO> entry : data.activeQuests.entrySet()) {
            String instanceId = entry.getKey();
            ActiveQuestDTO activeData = entry.getValue();
            
            try {
                QuestID questId = QuestID.valueOf(activeData.questId());
                Quest quest = getQuest(questId);
                if (quest != null) {
                    registerNPCObjectives(playerId, instanceId, quest);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("잘못된 퀘스트 ID: " + activeData.questId());
            }
        }
    }
    
    /**
     * NPC 목표 인덱스 제거 (퀘스트 완료/취소 시)
     */
    private void unregisterNPCObjectives(@NotNull UUID playerId, @NotNull String instanceId, @NotNull Quest quest) {
        for (QuestObjective objective : quest.getObjectives()) {
            if (objective instanceof com.febrie.rpg.quest.objective.impl.InteractNPCObjective npcObjective) {
                String npcId = npcObjective.getNpcId();
                
                // NPC 전체 인덱스에서 제거
                Set<NPCObjectiveRef> refs = npcObjectiveIndex.get(npcId);
                if (refs != null) {
                    refs.removeIf(ref -> ref.questInstanceId.equals(instanceId) && ref.playerId.equals(playerId));
                    if (refs.isEmpty()) {
                        npcObjectiveIndex.remove(npcId);
                    }
                }
                
                // 플레이어별 NPC 목표 캐시에서 제거
                Map<String, Set<String>> playerNPCs = playerNPCObjectives.get(playerId);
                if (playerNPCs != null) {
                    Set<String> instances = playerNPCs.get(npcId);
                    if (instances != null) {
                        instances.remove(instanceId);
                        if (instances.isEmpty()) {
                            playerNPCs.remove(npcId);
                        }
                    }
                    if (playerNPCs.isEmpty()) {
                        playerNPCObjectives.remove(playerId);
                    }
                }
            }
        }
    }
    
    /**
     * NPC 상호작용 처리 (최적화 버전)
     */
    public void handleNPCInteraction(@NotNull Player player, @NotNull String npcId) {
        UUID playerId = player.getUniqueId();
        
        // 플레이어의 해당 NPC 관련 퀘스트 직접 조회 (O(1))
        Map<String, Set<String>> playerNPCs = playerNPCObjectives.get(playerId);
        if (playerNPCs == null) return;
        
        Set<String> relatedInstances = playerNPCs.get(npcId);
        if (relatedInstances == null || relatedInstances.isEmpty()) return;
        
        PlayerQuestData playerData = getPlayerData(playerId);
        boolean anyProgressMade = false;
        
        // 관련된 모든 퀘스트 처리
        for (String instanceId : new ArrayList<>(relatedInstances)) {
            ActiveQuestDTO activeData = playerData.activeQuests.get(instanceId);
            if (activeData == null) continue;
            
            Quest quest = getQuest(QuestID.valueOf(activeData.questId()));
            if (quest == null) continue;
            
            // QuestProgress 복원
            Map<String, ObjectiveProgress> progressMap = new HashMap<>();
            activeData.progress().forEach((key, value) -> 
                progressMap.put(key, ObjectiveProgress.from(value, playerId)));
            QuestProgress progress = new QuestProgress(QuestID.valueOf(activeData.questId()), playerId, progressMap);
            
            // 해당 NPC와 관련된 목표 처리
            boolean questProgressMade = processNPCObjectives(player, quest, progress, npcId, instanceId);
            
            if (questProgressMade) {
                anyProgressMade = true;
                
                // 진행도를 DTO로 다시 변환하여 저장
                playerData.activeQuests.put(instanceId, ActiveQuestDTO.create(
                    activeData.questId(),
                    instanceId,
                    progress.getObjectives()
                ));
            }
        }
        
        if (anyProgressMade) {
            playerData.lastUpdated = System.currentTimeMillis();
            markForSave(playerId);
        }
    }
    
    /**
     * NPC 관련 목표 처리 (내부 메서드)
     */
    private boolean processNPCObjectives(@NotNull Player player, @NotNull Quest quest, 
                                        @NotNull QuestProgress progress, @NotNull String npcId,
                                        @NotNull String instanceId) {
        boolean progressMade = false;
        
        // 순차/자유 진행 모드 확인
        List<QuestObjective> objectivesToCheck = new ArrayList<>();
        
        if (quest.isSequential()) {
            int currentIndex = progress.getCurrentObjectiveIndex();
            if (currentIndex < quest.getObjectives().size()) {
                objectivesToCheck.add(quest.getObjectives().get(currentIndex));
            }
        } else {
            // 자유 진행 - 모든 미완료 목표
            for (QuestObjective obj : quest.getObjectives()) {
                ObjectiveProgress objProgress = progress.getObjective(obj.getId());
                if (objProgress != null && !objProgress.isCompleted()) {
                    objectivesToCheck.add(obj);
                }
            }
        }
        
        // NPC 목표 처리
        for (QuestObjective objective : objectivesToCheck) {
            if (objective instanceof com.febrie.rpg.quest.objective.impl.InteractNPCObjective npcObjective) {
                if (npcObjective.getNpcId().equals(npcId)) {
                    ObjectiveProgress objProgress = progress.getObjective(objective.getId());
                    if (objProgress != null && !objProgress.isCompleted()) {
                        // 진행도 증가
                        objProgress.increment(1);
                        progressMade = true;
                        
                        // 완료 체크
                        if (objProgress.isCompleted()) {
                            handleObjectiveCompletion(player, quest, objective, progress, instanceId);
                        } else {
                            // 진행도 알림
                            notifyObjectiveProgress(player, quest, objective, objProgress);
                        }
                    }
                }
            }
        }
        
        return progressMade;
    }
    
    /**
     * 목표 완료 처리
     */
    private void handleObjectiveCompletion(@NotNull Player player, @NotNull Quest quest, 
                                          @NotNull QuestObjective objective, @NotNull QuestProgress progress,
                                          @NotNull String instanceId) {
        // 완료 알림
        Component objectiveDesc = quest.getObjectiveDescription(objective, player);
        Component message = Component.text("✓ ", UnifiedColorUtil.SUCCESS)
                .append(objectiveDesc.color(UnifiedColorUtil.SUCCESS));
        player.sendMessage(message);
        SoundUtil.playSuccessSound(player);
        
        // 순차 진행인 경우 다음 목표로
        if (quest.isSequential()) {
            progress.setCurrentObjectiveIndex(progress.getCurrentObjectiveIndex() + 1);
        }
        
        // 퀘스트 전체 완료 체크
        checkQuestCompletion(player.getUniqueId(), instanceId);
    }
    
    /**
     * 목표 진행도 알림
     */
    private void notifyObjectiveProgress(@NotNull Player player, @NotNull Quest quest,
                                        @NotNull QuestObjective objective, @NotNull ObjectiveProgress progress) {
        Component progressMsg = LangManager.text(QuestCommonLangKey.QUEST_PROGRESS);
        
        Component objectiveDesc = quest.getObjectiveDescription(objective, player);
        Component message = progressMsg.color(UnifiedColorUtil.INFO)
            .append(Component.text(": ", UnifiedColorUtil.INFO))
            .append(objectiveDesc.color(UnifiedColorUtil.YELLOW));
        
        message = message.append(Component.text(objective.getProgressString(progress), UnifiedColorUtil.YELLOW));
        player.sendMessage(message);
        SoundUtil.playClickSound(player);
    }
    
    /**
     * 보상을 받을 수 있는지 확인  
     */
    public boolean hasReceivedAllRewards(@NotNull UUID playerId, @NotNull QuestID questId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        // claimedQuests에 있는지 확인
        return playerData.claimedQuests.values().stream()
                .anyMatch(data -> data.questId().equals(questId.name()));
    }
    
    /**
     * 미수령 아이템 목록 가져오기
     */
    public List<ItemStack> getUnclaimedItems(@NotNull UUID playerId, @NotNull QuestID questId) {
        // 이 메서드는 더 이상 사용되지 않음
        return new ArrayList<>();
    }
    
    /**
     * 플레이어의 모든 퀘스트 리셋
     */
    public void resetAllQuests(@NotNull UUID playerId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        
        // 모든 활성 퀘스트 취소
        playerData.activeQuests.clear();
        
        // 모든 완료된 퀘스트 제거
        playerData.completedQuests.clear();
        
        // 모든 보상 받은 퀘스트 제거
        playerData.claimedQuests.clear();
        
        // NPC 인덱스 초기화
        npcObjectiveIndex.clear();
        
        // 저장 예약
        markForSave(playerId);
    }
    
    /**
     * 특정 퀘스트 리셋
     */
    public void resetQuest(@NotNull UUID playerId, @NotNull QuestID questId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        
        // 활성 퀘스트에서 제거
        playerData.activeQuests.entrySet().removeIf(entry -> 
            entry.getValue().questId().equals(questId.name()));
        
        // 완료된 퀘스트에서 제거
        playerData.completedQuests.entrySet().removeIf(entry -> 
            entry.getValue().questId().equals(questId.name()));
        
        // 보상 받은 퀘스트에서 제거
        playerData.claimedQuests.entrySet().removeIf(entry -> 
            entry.getValue().questId().equals(questId.name()));
        
        // 저장 예약
        markForSave(playerId);
    }
}