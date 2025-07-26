package com.febrie.rpg.quest.manager;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.impl.QuestFirestoreService;
import com.febrie.rpg.dto.quest.CompletedQuestDTO;
import com.febrie.rpg.dto.quest.PlayerQuestDTO;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.quest.registry.QuestRegistry;
import com.febrie.rpg.quest.reward.UnclaimedReward;
import com.febrie.rpg.quest.reward.QuestReward;
import com.febrie.rpg.quest.reward.MixedReward;
import com.febrie.rpg.quest.task.LocationCheckTask;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.SoundUtil;
import com.febrie.rpg.util.ToastUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
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
        private final Map<QuestID, UnclaimedReward> unclaimedRewards = new EnumMap<>(QuestID.class);
        private long lastUpdated;

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
    }

    /**
     * 스케줄러 시작 (instance 설정 후 호출되어야 함)
     */
    private void startSchedulers() {
        // 자동 저장 스케줄러 시작
        startAutoSaveScheduler();

        // 지역 방문 체크 스케줄러 시작
        startLocationCheckScheduler();
        
        // 만료된 보상 체크
        checkAllExpiredRewards();
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
        
        // 기존 퀘스트 맵 클리어
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
        player.sendMessage(Component.text(plugin.getLangManager().getMessage(player, "quest.started"), ColorUtil.GOLD)
                .append(Component.text(quest.getDisplayName(isKorean), ColorUtil.RARE)));

        // 소리 재생
        SoundUtil.playOpenSound(player);

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
                1,  // 완료 횟수 추적 구현 필요
                false  // 보상 미수령 상태
        );
        playerData.completedQuests.put(questId, completed);

        // 보상은 지급하지 않음 (NPC를 통해 수령)
        Quest quest = getQuest(questId);
        if (quest != null) {
            // 보상 아이템 저장
            QuestReward questReward = quest.getReward();
            if (questReward instanceof MixedReward mixedReward) {
                List<ItemStack> rewardItems = mixedReward.getItems();
                
                // UnclaimedReward 생성 및 저장
                if (!rewardItems.isEmpty()) {
                    saveUnclaimedReward(playerId, questId, rewardItems);
                }
            }
            
            // 토스트 알림 표시
            ToastUtil.showQuestProgressToast(player, quest, progress);

            // 채팅 메시지
            boolean isKorean = plugin.getLangManager().getPlayerLanguage(player).startsWith("ko");
            player.sendMessage(Component.text("🎉 ", ColorUtil.GOLD)
                    .append(Component.text(quest.getDisplayName(isKorean), ColorUtil.LEGENDARY))
                    .append(Component.text(plugin.getLangManager().getMessage(player, "quest.completed"), ColorUtil.SUCCESS)));
            player.sendMessage(Component.text(plugin.getLangManager().getMessage(player, "quest.reward-npc-visit"), ColorUtil.INFO));

            // 소리 재생 (레벨업 사운드)
            SoundUtil.playSuccessSound(player);

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
        
        return questService.getPlayerQuests(playerId)
                .thenAccept(dto -> {
                    PlayerQuestData data = new PlayerQuestData();
                    
                    // 활성 퀘스트 변환
                    dto.activeQuests().forEach((idStr, progress) -> {
                        try {
                            QuestID questId = QuestID.valueOf(idStr);
                            data.activeQuests.put(questId, progress);
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning("알 수 없는 퀘스트 ID: " + idStr);
                        }
                    });
                    
                    // 완료된 퀘스트 변환
                    dto.completedQuests().forEach((idStr, completed) -> {
                        try {
                            QuestID questId = QuestID.valueOf(idStr);
                            data.completedQuests.put(questId, completed);
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning("알 수 없는 완료 퀘스트 ID: " + idStr);
                        }
                    });
                    
                    data.lastUpdated = dto.lastUpdated();
                    playerDataCache.put(playerId, data);
                    
                    plugin.getLogger().info("퀘스트 데이터 로드 완료 [" + playerId + "]: " + 
                            "활성 퀘스트 " + data.activeQuests.size() + "개, " +
                            "완료 퀘스트 " + data.completedQuests.size() + "개");
                })
                .exceptionally(ex -> {
                    plugin.getLogger().severe("퀘스트 데이터 로드 실패 [" + playerId + "]: " + ex.getMessage());
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
            pendingSaves.remove(playerId);
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

        // QuestFirestoreService를 사용하여 저장
        if (questService != null) {
            questService.savePlayerQuests(playerId, dto)
                    .thenRun(() -> {
                        pendingSaves.remove(playerId);
                        plugin.getLogger().info("퀘스트 데이터 저장 완료: " + playerId);
                    })
                    .exceptionally(throwable -> {
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
                        System.currentTimeMillis()
                );
                
                CompletableFuture<Void> saveFuture = questService.savePlayerQuests(playerId, dto)
                        .thenRun(() -> plugin.getLogger().info("퀘스트 데이터 저장 완료: " + playerId))
                        .exceptionally(throwable -> {
                            plugin.getLogger().severe("퀘스트 데이터 저장 실패: " + playerId + " - " + throwable.getMessage());
                            return null;
                        });
                
                saveFutures.add(saveFuture);
            }
        }
        
        // 모든 저장 작업이 완료될 때까지 대기 (최대 10초)
        try {
            CompletableFuture.allOf(saveFutures.toArray(new CompletableFuture<?>[0]))
                    .get(10, TimeUnit.SECONDS);
            plugin.getLogger().info("모든 퀘스트 데이터 저장 완료!");
        } catch (Exception e) {
            plugin.getLogger().severe("퀘스트 데이터 저장 중 오류 발생: " + e.getMessage());
        }

        // 캐시 정리
        playerDataCache.clear();
        pendingSaves.clear();
        
        plugin.getLogger().info("QuestManager 종료 완료.");
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
    
    /**
     * 미수령 보상 저장
     */
    public void saveUnclaimedReward(@NotNull UUID playerId, @NotNull QuestID questId, @NotNull List<ItemStack> items) {
        if (items.isEmpty()) {
            return;
        }
        
        PlayerQuestData playerData = getPlayerData(playerId);
        UnclaimedReward unclaimedReward = new UnclaimedReward(playerId, questId, items);
        playerData.unclaimedRewards.put(questId, unclaimedReward);
        markForSave(playerId);
        
        // 만료 타이머 설정
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            removeExpiredReward(playerId, questId);
        }, 20L * 60 * 60); // 1시간
    }
    
    /**
     * 미수령 보상 가져오기
     */
    @Nullable
    public UnclaimedReward getUnclaimedReward(@NotNull UUID playerId, @NotNull QuestID questId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        UnclaimedReward reward = playerData.unclaimedRewards.get(questId);
        
        // 만료 체크
        if (reward != null && reward.isExpired()) {
            playerData.unclaimedRewards.remove(questId);
            markForSave(playerId);
            return null;
        }
        
        return reward;
    }
    
    /**
     * 미수령 아이템 가져오기
     */
    @NotNull
    public List<ItemStack> getUnclaimedItems(@NotNull UUID playerId, @NotNull QuestID questId) {
        UnclaimedReward reward = getUnclaimedReward(playerId, questId);
        return reward != null ? reward.getRemainingItems() : new ArrayList<>();
    }
    
    /**
     * 미수령 보상 제거
     */
    public void removeUnclaimedReward(@NotNull UUID playerId, @NotNull QuestID questId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        if (playerData.unclaimedRewards.remove(questId) != null) {
            markForSave(playerId);
        }
    }
    
    /**
     * 만료된 보상 제거
     */
    private void removeExpiredReward(@NotNull UUID playerId, @NotNull QuestID questId) {
        PlayerQuestData playerData = getPlayerData(playerId);
        UnclaimedReward reward = playerData.unclaimedRewards.get(questId);
        
        if (reward != null && reward.isExpired()) {
            playerData.unclaimedRewards.remove(questId);
            markForSave(playerId);
            
            // 플레이어가 온라인인 경우 알림
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                Quest quest = getQuest(questId);
                if (quest != null) {
                    player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", ColorUtil.ERROR));
                    player.sendMessage(Component.text("미수령 보상이 파괴되었습니다!", ColorUtil.ERROR)
                            .decoration(net.kyori.adventure.text.format.TextDecoration.BOLD, true));
                    player.sendMessage(Component.text("퀘스트: ", ColorUtil.WARNING)
                            .append(Component.text(quest.getDisplayName(player.locale().getLanguage().equals("ko")), ColorUtil.YELLOW)));
                    player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", ColorUtil.ERROR));
                    SoundUtil.playDeleteSound(player);
                }
            }
        }
    }
    
    /**
     * 모든 미수령 보상 체크 (서버 시작 시)
     */
    private void checkAllExpiredRewards() {
        for (Map.Entry<UUID, PlayerQuestData> entry : playerDataCache.entrySet()) {
            UUID playerId = entry.getKey();
            PlayerQuestData data = entry.getValue();
            
            List<QuestID> toRemove = new ArrayList<>();
            for (Map.Entry<QuestID, UnclaimedReward> rewardEntry : data.unclaimedRewards.entrySet()) {
                if (rewardEntry.getValue().isExpired()) {
                    toRemove.add(rewardEntry.getKey());
                }
            }
            
            for (QuestID questId : toRemove) {
                removeExpiredReward(playerId, questId);
            }
        }
    }
}