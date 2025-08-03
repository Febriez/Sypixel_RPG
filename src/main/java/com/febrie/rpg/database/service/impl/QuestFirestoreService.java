package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.BaseFirestoreService;
import com.febrie.rpg.dto.quest.CompletedQuestDTO;
import com.febrie.rpg.dto.quest.PlayerQuestDTO;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.quest.reward.ClaimedRewardData;
import com.febrie.rpg.util.FirestoreUtils;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 퀘스트 데이터 Firestore 서비스
 * player-quests 컬렉션 관리
 *
 * @author Febrie, CoffeeTory
 */
public class QuestFirestoreService extends BaseFirestoreService<PlayerQuestDTO> {

    private static final String COLLECTION_NAME = "PlayerQuest";

    public QuestFirestoreService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        super(plugin, firestore, COLLECTION_NAME, PlayerQuestDTO.class);
    }

    @Override
    protected Map<String, Object> toMap(@NotNull PlayerQuestDTO dto) {
        // Use the DTO's built-in toMap method, but handle QuestProgress serialization
        Map<String, Object> map = new HashMap<>();
        map.put("playerId", dto.playerId());
        
        // Active quests - serialize QuestProgress to Map
        Map<String, Map<String, Object>> activeQuestsMap = new HashMap<>();
        dto.activeQuests().forEach((questId, progress) -> {
            activeQuestsMap.put(questId, questProgressToMap(progress));
        });
        map.put("activeQuests", activeQuestsMap);
        
        // Completed quests - use DTO's toMap
        Map<String, Map<String, Object>> completedQuestsMap = new HashMap<>();
        dto.completedQuests().forEach((questId, completed) -> {
            completedQuestsMap.put(questId, completed.toMap());
        });
        map.put("completedQuests", completedQuestsMap);
        
        // Claimed reward data - use toMap
        Map<String, Map<String, Object>> claimedRewardDataMap = new HashMap<>();
        dto.claimedRewardData().forEach((questId, rewardData) -> {
            claimedRewardDataMap.put(questId, rewardData.toMap());
        });
        map.put("claimedRewardData", claimedRewardDataMap);
        
        map.put("lastUpdated", dto.lastUpdated());
        return map;
    }

    @Override
    @Nullable
    protected PlayerQuestDTO fromDocument(@NotNull DocumentSnapshot document) {
        if (!document.exists()) {
            return null;
        }

        try {
            Map<String, Object> data = document.getData();
            if (data == null) {
                return null;
            }
            
            // Parse data
            String playerId = (String) data.get("playerId");
            if (playerId == null) {
                playerId = document.getId();
            }
            
            // Parse active quests
            Map<String, QuestProgress> activeQuests = new HashMap<>();
            @SuppressWarnings("unchecked")
            Map<String, Object> activeData = (Map<String, Object>) data.get("activeQuests");
            if (activeData != null) {
                activeData.forEach((questId, progressData) -> {
                    if (progressData instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> progressMap = (Map<String, Object>) progressData;
                        QuestProgress progress = questProgressFromMap(questId, progressMap);
                        if (progress != null) {
                            activeQuests.put(questId, progress);
                        }
                    }
                });
            }
            
            // Parse completed quests
            Map<String, CompletedQuestDTO> completedQuests = new HashMap<>();
            @SuppressWarnings("unchecked")
            Map<String, Object> completedData = (Map<String, Object>) data.get("completedQuests");
            if (completedData != null) {
                completedData.forEach((questId, completedObj) -> {
                    if (completedObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> completedMap = (Map<String, Object>) completedObj;
                        completedQuests.put(questId, CompletedQuestDTO.fromMap(completedMap));
                    }
                });
            }
            
            // Parse claimed reward data
            Map<String, ClaimedRewardData> claimedRewardData = new HashMap<>();
            @SuppressWarnings("unchecked")
            Map<String, Object> claimedData = (Map<String, Object>) data.get("claimedRewardData");
            if (claimedData != null) {
                claimedData.forEach((questId, rewardObj) -> {
                    if (rewardObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> rewardMap = (Map<String, Object>) rewardObj;
                        claimedRewardData.put(questId, ClaimedRewardData.fromMap(rewardMap));
                    }
                });
            }
            
            Long lastUpdated = FirestoreUtils.getLong(data, "lastUpdated", System.currentTimeMillis());
            
            return new PlayerQuestDTO(playerId, activeQuests, completedQuests, claimedRewardData, lastUpdated);

        } catch (Exception e) {
            LogUtil.warning("퀘스트 데이터 파싱 실패 [" + document.getId() + "]: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * QuestProgress를 Map으로 변환
     */
    private Map<String, Object> questProgressToMap(@NotNull QuestProgress progress) {
        Map<String, Object> map = new HashMap<>();
        
        map.put("questId", progress.getQuestId().name());
        map.put("playerId", progress.getPlayerId().toString());
        
        // Objectives
        Map<String, Map<String, Object>> objectivesMap = new HashMap<>();
        progress.getObjectives().forEach((objId, objProgress) -> {
            objectivesMap.put(objId, objectiveProgressToMap(objProgress));
        });
        map.put("objectives", objectivesMap);
        
        map.put("state", progress.getState().name());
        map.put("currentObjectiveIndex", progress.getCurrentObjectiveIndex());
        map.put("startedAt", progress.getStartedAt().toEpochMilli());
        
        if (progress.getCompletedAt() != null) {
            map.put("completedAt", progress.getCompletedAt().toEpochMilli());
        }
        
        map.put("lastUpdatedAt", progress.getLastUpdatedAt().toEpochMilli());
        
        return map;
    }
    
    /**
     * ObjectiveProgress를 Map으로 변환
     */
    private Map<String, Object> objectiveProgressToMap(@NotNull ObjectiveProgress progress) {
        Map<String, Object> map = new HashMap<>();
        
        map.put("objectiveId", progress.getObjectiveId());
        map.put("playerId", progress.getPlayerId().toString());
        map.put("currentValue", progress.getCurrentValue());
        map.put("requiredValue", progress.getRequiredValue());
        map.put("completed", progress.isCompleted());
        map.put("startedAt", progress.getStartedAt());
        map.put("completedAt", progress.getCompletedAt());
        map.put("lastUpdated", progress.getLastUpdated());
        
        return map;
    }
    
    /**
     * Map에서 QuestProgress 생성
     */
    @Nullable
    private QuestProgress questProgressFromMap(@NotNull String questIdStr, @NotNull Map<String, Object> map) {
        try {
            QuestID questId = QuestID.valueOf(questIdStr);
            UUID playerId = UUID.fromString(FirestoreUtils.getString(map, "playerId", ""));
            
            // Objectives 파싱
            Map<String, ObjectiveProgress> objectives = new HashMap<>();
            @SuppressWarnings("unchecked")
            Map<String, Object> objectivesData = (Map<String, Object>) map.get("objectives");
            if (objectivesData != null) {
                objectivesData.forEach((objId, objData) -> {
                    if (objData instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> objMap = (Map<String, Object>) objData;
                        ObjectiveProgress objProgress = objectiveProgressFromMap(objMap);
                        if (objProgress != null) {
                            objectives.put(objId, objProgress);
                        }
                    }
                });
            }
            
            QuestProgress.QuestState state = QuestProgress.QuestState.valueOf(FirestoreUtils.getString(map, "state", "IN_PROGRESS"));
            int currentObjectiveIndex = FirestoreUtils.getInt(map, "currentObjectiveIndex");
            
            long startedAtMs = FirestoreUtils.getLong(map, "startedAt", System.currentTimeMillis());
            Instant startedAt = Instant.ofEpochMilli(startedAtMs);
            
            Instant completedAt = null;
            if (map.containsKey("completedAt")) {
                long completedAtMs = FirestoreUtils.getLong(map, "completedAt", 0L);
                completedAt = Instant.ofEpochMilli(completedAtMs);
            }
            
            long lastUpdatedAtMs = FirestoreUtils.getLong(map, "lastUpdatedAt", System.currentTimeMillis());
            Instant lastUpdatedAt = Instant.ofEpochMilli(lastUpdatedAtMs);
            
            return new QuestProgress(questId, playerId, objectives, state, 
                    currentObjectiveIndex, startedAt, completedAt, lastUpdatedAt);
                    
        } catch (Exception e) {
            LogUtil.warning("QuestProgress 파싱 실패: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Map에서 ObjectiveProgress 생성
     */
    @Nullable
    private ObjectiveProgress objectiveProgressFromMap(@NotNull Map<String, Object> map) {
        try {
            String objectiveId = FirestoreUtils.getString(map, "objectiveId", "");
            UUID playerId = UUID.fromString(FirestoreUtils.getString(map, "playerId", ""));
            int currentValue = FirestoreUtils.getInt(map, "currentValue");
            int requiredValue = FirestoreUtils.getInt(map, "requiredValue");
            boolean completed = FirestoreUtils.getBoolean(map, "completed", false);
            long startedAt = FirestoreUtils.getLong(map, "startedAt", System.currentTimeMillis());
            long completedAt = FirestoreUtils.getLong(map, "completedAt", 0L);
            
            return new ObjectiveProgress(objectiveId, playerId, currentValue, 
                    requiredValue, completed, startedAt, completedAt);
                    
        } catch (Exception e) {
            LogUtil.warning("ObjectiveProgress 파싱 실패: " + e.getMessage());
            return null;
        }
    }

    /**
     * 플레이어의 퀘스트 데이터 조회
     */
    @NotNull
    public CompletableFuture<PlayerQuestDTO> getPlayerQuests(@NotNull UUID playerId) {
        return get(playerId.toString()).thenApply(data -> {
            if (data == null) {
                return new PlayerQuestDTO(playerId.toString());
            }
            return data;
        });
    }

    /**
     * 플레이어의 퀘스트 데이터 저장
     */
    @NotNull
    public CompletableFuture<Void> savePlayerQuests(@NotNull UUID playerId, @NotNull PlayerQuestDTO data) {
        return save(playerId.toString(), data);
    }

    /**
     * 퀘스트 진행도 업데이트
     */
    @NotNull
    public CompletableFuture<Void> updateQuestProgress(@NotNull UUID playerId, @NotNull QuestProgress progress) {
        return getPlayerQuests(playerId).thenCompose(data -> {
            Map<String, QuestProgress> updatedActiveQuests = new HashMap<>(data.activeQuests());
            updatedActiveQuests.put(progress.getQuestId().name(), progress);
            return savePlayerQuests(playerId, new PlayerQuestDTO(
                    data.playerId(), 
                    updatedActiveQuests, 
                    data.completedQuests(), 
                    data.claimedRewardData(),
                    System.currentTimeMillis()));
        });
    }

    /**
     * 퀘스트 완료 처리
     */
    @NotNull
    public CompletableFuture<Void> completeQuest(@NotNull UUID playerId, @NotNull String questId, @NotNull CompletedQuestDTO completed) {
        return getPlayerQuests(playerId).thenCompose(data -> {
            Map<String, QuestProgress> updatedActiveQuests = new HashMap<>(data.activeQuests());
            Map<String, CompletedQuestDTO> updatedCompletedQuests = new HashMap<>(data.completedQuests());
            updatedActiveQuests.remove(questId);
            updatedCompletedQuests.put(questId, completed);
            return savePlayerQuests(playerId, new PlayerQuestDTO(
                    data.playerId(), 
                    updatedActiveQuests, 
                    updatedCompletedQuests, 
                    data.claimedRewardData(),
                    System.currentTimeMillis()));
        });
    }

    /**
     * 활성 퀘스트 제거
     */
    @NotNull
    public CompletableFuture<Void> removeActiveQuest(@NotNull UUID playerId, @NotNull String questId) {
        return getPlayerQuests(playerId).thenCompose(data -> {
            Map<String, QuestProgress> updatedActiveQuests = new HashMap<>(data.activeQuests());
            updatedActiveQuests.remove(questId);
            return savePlayerQuests(playerId, new PlayerQuestDTO(
                    data.playerId(), 
                    updatedActiveQuests, 
                    data.completedQuests(), 
                    data.claimedRewardData(),
                    System.currentTimeMillis()));
        });
    }

    /**
     * 퀘스트 진행 중인지 확인
     */
    @NotNull
    public CompletableFuture<Boolean> hasActiveQuest(@NotNull UUID playerId, @NotNull String questId) {
        return getPlayerQuests(playerId).thenApply(data -> data.activeQuests().containsKey(questId));
    }

    /**
     * 퀘스트 완료 여부 확인
     */
    @NotNull
    public CompletableFuture<Boolean> hasCompletedQuest(@NotNull UUID playerId, @NotNull String questId) {
        return getPlayerQuests(playerId).thenApply(data -> data.completedQuests().containsKey(questId));
    }
}