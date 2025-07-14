package com.febrie.rpg.quest.manager;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.FirebaseService;
import com.febrie.rpg.dto.CompletedQuestDTO;
import com.febrie.rpg.dto.PlayerQuestDTO;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 퀘스트 시스템 매니저 (싱글톤)
 * 퀘스트의 등록, 진행, 완료 등을 관리
 * 플레이어별로 개별 퀘스트 인스턴스 생성
 *
 * @author Febrie
 */
public class QuestManager {

    private static QuestManager instance;

    private final RPGMain plugin;
    private final FirebaseService firebaseService;

    // 플레이어별 활성 퀘스트 목록 (각 플레이어가 고유한 퀘스트 인스턴스를 가짐)
    private final Map<UUID, List<Quest>> playerQuests = new ConcurrentHashMap<>();

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

        // 데일리 퀘스트 리셋 스케줄러 시작
        startDailyQuestResetScheduler();
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
     * 플레이어의 튜토리얼 퀘스트 시작
     */
    public void startTutorialQuests(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        List<Quest> quests = playerQuests.computeIfAbsent(playerId, k -> new ArrayList<>());

        // 튜토리얼 퀘스트 생성
        Quest firstSteps = new com.febrie.rpg.quest.impl.tutorial.FirstStepsQuest();
        Quest basicCombat = new com.febrie.rpg.quest.impl.tutorial.BasicCombatQuest();

        quests.add(firstSteps);
        quests.add(basicCombat);

        // 첫 번째 튜토리얼 퀘스트 자동 시작
        startQuest(player, firstSteps.getId());

        LogUtil.info("Started tutorial quests for player: " + player.getName());
    }

    /**
     * 플레이어의 메인 퀘스트 초기화 (직업 선택 시)
     */
    public void initializeMainQuests(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        List<Quest> quests = playerQuests.computeIfAbsent(playerId, k -> new ArrayList<>());

        // 메인 퀘스트 생성
        quests.add(new com.febrie.rpg.quest.impl.main.HeroesJourneyQuest());
        quests.add(new com.febrie.rpg.quest.impl.main.PathOfLightQuest());
        quests.add(new com.febrie.rpg.quest.impl.main.PathOfDarknessQuest());

        LogUtil.info("Initialized main quests for player: " + player.getName());
    }

    /**
     * 플레이어의 데일리 퀘스트 생성 (매일 리셋)
     */
    public void generateDailyQuests(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        List<Quest> quests = playerQuests.computeIfAbsent(playerId, k -> new ArrayList<>());

        // 기존 데일리 퀘스트 제거
        quests.removeIf(quest -> quest.isDaily() && !isQuestActive(player, quest.getId()));

        // 새로운 데일리 퀘스트 생성 (랜덤 요소 포함)
        quests.add(createRandomDailyMiningQuest());
        quests.add(createRandomDailyHuntingQuest());

        LogUtil.info("Generated daily quests for player: " + player.getName());
    }

    /**
     * 랜덤 일일 채광 퀘스트 생성
     */
    private Quest createRandomDailyMiningQuest() {
        Random random = new Random();

        // 기본 50에서 ±20 범위로 랜덤
        int stoneAmount = 30 + random.nextInt(41); // 30-70
        int coalAmount = 10 + random.nextInt(21);  // 10-30
        int ironAmount = 5 + random.nextInt(11);   // 5-15

        return new com.febrie.rpg.quest.impl.daily.RandomDailyMiningQuest(
                stoneAmount, coalAmount, ironAmount
        );
    }

    /**
     * 랜덤 일일 사냥 퀘스트 생성
     */
    private Quest createRandomDailyHuntingQuest() {
        Random random = new Random();

        // 랜덤하게 몬스터 타입과 수량 결정
        EntityType[] possibleMobs = {
                EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER,
                EntityType.CREEPER, EntityType.WITCH, EntityType.PHANTOM
        };

        EntityType targetMob = possibleMobs[random.nextInt(possibleMobs.length)];
        int amount = 10 + random.nextInt(21); // 10-30

        return new com.febrie.rpg.quest.impl.daily.RandomDailyHuntingQuest(targetMob, amount);
    }

    /**
     * 플레이어별 퀘스트 조회
     */
    @Nullable
    public Quest getPlayerQuest(@NotNull Player player, @NotNull String questId) {
        List<Quest> quests = playerQuests.get(player.getUniqueId());
        if (quests == null) return null;

        return quests.stream()
                .filter(quest -> quest.getId().equals(questId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 플레이어의 모든 퀘스트 조회
     */
    @NotNull
    public List<Quest> getPlayerQuests(@NotNull Player player) {
        return new ArrayList<>(playerQuests.getOrDefault(player.getUniqueId(), new ArrayList<>()));
    }

    /**
     * 플레이어의 가능한 퀘스트 조회
     */
    @NotNull
    public List<Quest> getAvailableQuests(@NotNull Player player) {
        List<Quest> allQuests = getPlayerQuests(player);
        PlayerQuestDTO questData = getOrLoadPlayerData(player.getUniqueId());

        return allQuests.stream()
                .filter(quest -> canStartQuest(player, quest))
                .collect(Collectors.toList());
    }

    /**
     * 퀘스트 시작 가능 여부 확인 (개별 퀘스트 인스턴스 사용)
     */
    private boolean canStartQuest(@NotNull Player player, @NotNull Quest quest) {
        PlayerQuestDTO questData = getOrLoadPlayerData(player.getUniqueId());

        // 이미 진행 중이거나 완료했는지 확인
        if (questData.activeQuests().containsKey(quest.getId()) ||
                questData.completedQuests().containsKey(quest.getId())) {
            return false;
        }

        // 레벨 확인
        int playerLevel = plugin.getRPGPlayerManager().getOrCreatePlayer(player).getLevel();
        if (playerLevel < quest.getMinLevel() ||
                (quest.getMaxLevel() > 0 && playerLevel > quest.getMaxLevel())) {
            return false;
        }

        // 선행 퀘스트 확인
        List<String> completedIds = new ArrayList<>(questData.completedQuests().keySet());
        if (!quest.arePrerequisitesCompleted(completedIds)) {
            return false;
        }

        // 양자택일 퀘스트 확인
        if (quest.hasCompletedExclusiveQuests(completedIds)) {
            return false;
        }

        // 퀘스트 자체 조건 확인
        return quest.canStart(player.getUniqueId());
    }

    /**
     * 플레이어가 퀘스트 시작
     */
    public boolean startQuest(@NotNull Player player, @NotNull String questId) {
        Quest quest = getQuest(questId);
        if (quest == null) {
            LogUtil.warning("Attempted to start non-existent quest: " + questId);
            return false;
        }

        UUID playerId = player.getUniqueId();
        PlayerQuestDTO questData = getOrLoadPlayerData(playerId);

        // 이미 진행 중이거나 완료한 퀘스트인지 확인
        if (questData.activeQuests().containsKey(questId) ||
                questData.completedQuests().containsKey(questId)) {
            return false;
        }

        // 퀘스트 시작 가능 여부 확인
        if (!canStartQuest(player, questId)) {
            return false;
        }

        // 퀘스트 진행도 생성
        QuestProgress progress = quest.createProgress(playerId);
        questData.activeQuests().put(questId, progress);

        // 저장 예약
        markForSave(playerId);

        LogUtil.info("Player " + player.getName() + " started quest: " + questId);
        return true;
    }

    /**
     * 플레이어가 퀘스트 포기
     */
    public boolean abandonQuest(@NotNull Player player, @NotNull String questId) {
        UUID playerId = player.getUniqueId();
        PlayerQuestDTO questData = getOrLoadPlayerData(playerId);

        QuestProgress removed = questData.activeQuests().remove(questId);
        if (removed != null) {
            markForSave(playerId);
            LogUtil.info("Player " + player.getName() + " abandoned quest: " + questId);
            return true;
        }

        return false;
    }

    /**
     * 플레이어의 활성 퀘스트 목록 조회
     */
    @NotNull
    public List<QuestProgress> getActiveQuests(@NotNull Player player) {
        PlayerQuestDTO questData = getOrLoadPlayerData(player.getUniqueId());
        return new ArrayList<>(questData.activeQuests().values());
    }

    /**
     * 플레이어의 특정 퀘스트 진행도 조회
     */
    @NotNull
    public Optional<QuestProgress> getQuestProgress(@NotNull Player player, @NotNull String questId) {
        PlayerQuestDTO questData = getOrLoadPlayerData(player.getUniqueId());
        return Optional.ofNullable(questData.activeQuests().get(questId));
    }

    /**
     * 플레이어의 완료된 퀘스트 목록 조회
     */
    @NotNull
    public List<String> getCompletedQuests(@NotNull Player player) {
        PlayerQuestDTO questData = getOrLoadPlayerData(player.getUniqueId());
        return new ArrayList<>(questData.completedQuests().keySet());
    }

    /**
     * 플레이어의 완료된 퀘스트 상세 정보 조회
     */
    @NotNull
    public List<CompletedQuestDTO> getCompletedQuestDetails(@NotNull Player player) {
        PlayerQuestDTO questData = getOrLoadPlayerData(player.getUniqueId());
        return new ArrayList<>(questData.completedQuests().values());
    }

    /**
     * 이벤트 처리 (퀘스트 진행도 업데이트)
     */
    public void handleEvent(@NotNull Event event, @NotNull Player player) {
        UUID playerId = player.getUniqueId();
        PlayerQuestDTO questData = getOrLoadPlayerData(playerId);

        boolean updated = false;

        // 활성 퀘스트들의 목표 체크
        for (Map.Entry<String, QuestProgress> entry : questData.activeQuests().entrySet()) {
            String questId = entry.getKey();
            QuestProgress progress = entry.getValue();
            Quest quest = getQuest(questId);

            if (quest == null) continue;

            // 각 목표에 대해 진행도 체크
            if (quest.isSequential()) {
                // 순차적 퀘스트 - 현재 목표만 체크
                int currentIndex = progress.getCurrentObjectiveIndex();
                if (currentIndex < quest.getObjectives().size()) {
                    var objective = quest.getObjectives().get(currentIndex);
                    if (updateObjectiveProgress(objective, progress, event, player)) {
                        updated = true;

                        // 현재 목표 완료시 다음 목표로
                        ObjectiveProgress objProgress = progress.getObjectiveProgress(objective.getId());
                        if (objProgress != null && objective.isComplete(objProgress)) {
                            progress.setCurrentObjectiveIndex(currentIndex + 1);
                        }
                    }
                }
            } else {
                // 비순차적 퀘스트 - 모든 목표 체크
                for (var objective : quest.getObjectives()) {
                    if (updateObjectiveProgress(objective, progress, event, player)) {
                        updated = true;
                    }
                }
            }

            // 퀘스트 완료 체크
            if (isQuestComplete(quest, progress)) {
                completeQuest(player, questId, progress);
                updated = true;
            }
        }

        if (updated) {
            markForSave(playerId);
        }
    }

    /**
     * 목표 진행도 업데이트
     */
    private boolean updateObjectiveProgress(@NotNull com.febrie.rpg.quest.objective.QuestObjective objective,
                                            @NotNull QuestProgress progress,
                                            @NotNull Event event,
                                            @NotNull Player player) {
        ObjectiveProgress objProgress = progress.getObjectiveProgress(objective.getId());
        if (objProgress == null || objective.isComplete(objProgress)) {
            return false;
        }

        if (objective.canProgress(event, player)) {
            int increment = objective.calculateIncrement(event, player);
            if (increment > 0) {
                objProgress.increment(increment);
                return true;
            }
        }

        return false;
    }

    /**
     * 퀘스트 완료 여부 확인
     */
    private boolean isQuestComplete(@NotNull Quest quest, @NotNull QuestProgress progress) {
        for (var objective : quest.getObjectives()) {
            ObjectiveProgress objProgress = progress.getObjectiveProgress(objective.getId());
            if (objProgress == null || !objective.isComplete(objProgress)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 퀘스트 완료 처리
     */
    private void completeQuest(@NotNull Player player, @NotNull String questId, @NotNull QuestProgress progress) {
        UUID playerId = player.getUniqueId();
        PlayerQuestDTO questData = getOrLoadPlayerData(playerId);

        // 활성 퀘스트에서 제거
        questData.activeQuests().remove(questId);

        // 완료 퀘스트에 추가
        long completedAt = System.currentTimeMillis();
        CompletedQuestDTO completed = new CompletedQuestDTO(
                questId,
                progress.getStartedAt().toEpochMilli(),
                completedAt
        );
        questData.completedQuests().put(questId, completed);

        // 보상 지급
        Quest quest = getQuest(questId);
        if (quest != null) {
            quest.getReward().grant(player);
        }

        LogUtil.info("Player " + player.getName() + " completed quest: " + questId);
    }

    /**
     * 플레이어의 퀘스트 데이터 로드
     */
    public void loadPlayerData(@NotNull UUID playerId) {
        CompletableFuture<PlayerQuestDTO> future = firebaseService.loadPlayerQuestData(playerId.toString());

        future.thenAccept(data -> {
            if (data != null) {
                playerQuestCache.put(playerId, data);
                LogUtil.debug("Loaded quest data for player: " + playerId);
            } else {
                // 새 플레이어
                PlayerQuestDTO newData = new PlayerQuestDTO(playerId.toString());
                playerQuestCache.put(playerId, newData);
                LogUtil.debug("Created new quest data for player: " + playerId);
            }
        }).exceptionally(ex -> {
            LogUtil.error("Failed to load quest data for player: " + playerId, ex);
            // 오류 시 빈 데이터 생성
            playerQuestCache.put(playerId, new PlayerQuestDTO(playerId.toString()));
            return null;
        });
    }

    /**
     * 플레이어의 퀘스트 데이터 저장
     */
    public void savePlayerData(@NotNull UUID playerId) {
        PlayerQuestDTO data = playerQuestCache.get(playerId);
        if (data == null) return;

        firebaseService.savePlayerQuestData(playerId.toString(), data)
                .thenAccept(success -> {
                    if (success) {
                        pendingSaves.remove(playerId);
                        LogUtil.debug("Saved quest data for player: " + playerId);
                    } else {
                        LogUtil.error("Failed to save quest data for player: " + playerId);
                    }
                });
    }

    /**
     * 모든 플레이어의 퀘스트 데이터 저장
     */
    public void saveAllData() {
        Set<UUID> toSave = new HashSet<>(pendingSaves);
        toSave.forEach(this::savePlayerData);
    }

    /**
     * 매니저 종료 (리소스 정리)
     */
    public void shutdown() {
        // 모든 데이터 저장
        saveAllData();

        // 캐시 정리
        playerQuestCache.clear();
        pendingSaves.clear();

        LogUtil.info("QuestManager shutdown complete");
    }

    /**
     * 플레이어가 특정 퀘스트를 시작할 수 있는지 확인
     */
    public boolean canStartQuest(@NotNull Player player, @NotNull String questId) {
        Quest quest = getQuest(questId);
        if (quest == null) return false;

        PlayerQuestDTO questData = getOrLoadPlayerData(player.getUniqueId());

        // 이미 진행 중이거나 완료했는지 확인
        if (questData.activeQuests().containsKey(questId) ||
                questData.completedQuests().containsKey(questId)) {
            return false;
        }

        // 레벨 확인
        int playerLevel = plugin.getRPGPlayerManager().getOrCreatePlayer(player).getLevel();
        if (playerLevel < quest.getMinLevel() ||
                (quest.getMaxLevel() > 0 && playerLevel > quest.getMaxLevel())) {
            return false;
        }

        // 선행 퀘스트 확인
        List<String> completedIds = new ArrayList<>(questData.completedQuests().keySet());
        if (!quest.arePrerequisitesCompleted(completedIds)) {
            return false;
        }

        // 양자택일 퀘스트 확인
        if (quest.hasCompletedExclusiveQuests(completedIds)) {
            return false;
        }

        // 퀘스트 자체 조건 확인
        return quest.canStart(player.getUniqueId());
    }

    /**
     * 플레이어에게 가용한 퀘스트 목록 조회
     */
    @NotNull
    public List<Quest> getAvailableQuests(@NotNull Player player) {
        return getAllQuests().stream()
                .filter(quest -> canStartQuest(player, quest.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 플레이어 데이터 가져오기 (없으면 로드)
     */
    private PlayerQuestDTO getOrLoadPlayerData(@NotNull UUID playerId) {
        PlayerQuestDTO data = playerQuestCache.get(playerId);
        if (data == null) {
            loadPlayerData(playerId);
            // 로드 중에는 임시 데이터 반환
            return new PlayerQuestDTO(playerId.toString());
        }
        return data;
    }

    /**
     * 저장 예약
     */
    private void markForSave(@NotNull UUID playerId) {
        pendingSaves.add(playerId);
    }

    /**
     * 자동 저장 스케줄러 시작
     */
    private void startAutoSaveScheduler() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (!pendingSaves.isEmpty()) {
                saveAllData();
            }
        }, 20L * 60, 20L * 60); // 1분마다 실행
    }
}