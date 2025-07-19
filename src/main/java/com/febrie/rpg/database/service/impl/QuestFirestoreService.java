package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.BaseFirestoreService;
import com.febrie.rpg.dto.quest.CompletedQuestDTO;
import com.febrie.rpg.dto.quest.PlayerQuestDTO;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
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
import java.util.stream.Collectors;

/**
 * 퀘스트 데이터 Firestore 서비스
 * player-quests 컬렉션 관리
 *
 * @author Febrie, CoffeeTory
 */
public class QuestFirestoreService extends BaseFirestoreService<PlayerQuestDTO> {
    
    private static final String COLLECTION_NAME = "player-quests";
    
    public QuestFirestoreService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        super(plugin, firestore, COLLECTION_NAME, PlayerQuestDTO.class);
    }
    
    @Override
    protected Map<String, Object> toMap(@NotNull PlayerQuestDTO dto) {
        Map<String, Object> map = new HashMap<>();
        
        map.put("playerId", dto.playerId());
        
        // Active quests
        Map<String, Map<String, Object>> activeQuestsMap = new HashMap<>();
        dto.activeQuests().forEach((questId, progress) -> {
            activeQuestsMap.put(questId, questProgressToMap(progress));
        });
        map.put("activeQuests", activeQuestsMap);
        
        // Completed quests
        Map<String, Map<String, Object>> completedQuestsMap = new HashMap<>();
        dto.completedQuests().forEach((questId, completed) -> {
            completedQuestsMap.put(questId, completedQuestToMap(completed));
        });
        map.put("completedQuests", completedQuestsMap);
        
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
            String playerId = document.getString("playerId");
            if (playerId == null) {
                playerId = document.getId();
            }
            
            // Active quests 파싱
            Map<String, QuestProgress> activeQuests = new HashMap<>();
            @SuppressWarnings("unchecked")
            Map<String, Object> activeData = document.get("activeQuests", Map.class);
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
            
            // Completed quests 파싱
            Map<String, CompletedQuestDTO> completedQuests = new HashMap<>();
            @SuppressWarnings("unchecked")
            Map<String, Object> completedData = document.get("completedQuests", Map.class);
            if (completedData != null) {
                completedData.forEach((questId, completedObj) -> {
                    if (completedObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> completedMap = (Map<String, Object>) completedObj;
                        CompletedQuestDTO completed = completedQuestFromMap(completedMap);
                        if (completed != null) {
                            completedQuests.put(questId, completed);
                        }
                    }
                });
            }
            
            Long lastUpdated = document.getLong("lastUpdated");
            if (lastUpdated == null) {
                lastUpdated = System.currentTimeMillis();
            }
            
            return new PlayerQuestDTO(playerId, activeQuests, completedQuests, lastUpdated);
            
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
     * CompletedQuestDTO를 Map으로 변환
     */
    private Map<String, Object> completedQuestToMap(@NotNull CompletedQuestDTO completed) {
        Map<String, Object> map = new HashMap<>();
        
        map.put("questId", completed.getQuestId());
        map.put("completedAt", completed.getCompletedAt());
        map.put("completionCount", completed.getCompletionCount());
        map.put("rewarded", completed.isRewarded());
        
        return map;
    }
    
    /**
     * Map에서 QuestProgress 생성
     */
    @Nullable
    private QuestProgress questProgressFromMap(@NotNull String questIdStr, @NotNull Map<String, Object> map) {
        try {
            QuestID questId = QuestID.valueOf(questIdStr);
            UUID playerId = UUID.fromString((String) map.get("playerId"));
            
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
            
            QuestProgress.QuestState state = QuestProgress.QuestState.valueOf((String) map.get("state"));
            int currentObjectiveIndex = ((Number) map.getOrDefault("currentObjectiveIndex", 0)).intValue();
            
            long startedAtMs = ((Number) map.get("startedAt")).longValue();
            Instant startedAt = Instant.ofEpochMilli(startedAtMs);
            
            Instant completedAt = null;
            if (map.containsKey("completedAt")) {
                long completedAtMs = ((Number) map.get("completedAt")).longValue();
                completedAt = Instant.ofEpochMilli(completedAtMs);
            }
            
            long lastUpdatedAtMs = ((Number) map.get("lastUpdatedAt")).longValue();
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
            String objectiveId = (String) map.get("objectiveId");
            UUID playerId = UUID.fromString((String) map.get("playerId"));
            int currentValue = ((Number) map.get("currentValue")).intValue();
            int requiredValue = ((Number) map.get("requiredValue")).intValue();
            boolean completed = (Boolean) map.get("completed");
            long startedAt = ((Number) map.get("startedAt")).longValue();
            long completedAt = ((Number) map.get("completedAt")).longValue();
            
            return new ObjectiveProgress(objectiveId, playerId, currentValue, 
                    requiredValue, completed, startedAt, completedAt);
            
        } catch (Exception e) {
            LogUtil.warning("ObjectiveProgress 파싱 실패: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Map에서 CompletedQuestDTO 생성
     */
    @Nullable
    private CompletedQuestDTO completedQuestFromMap(@NotNull Map<String, Object> map) {
        try {
            String questId = (String) map.get("questId");
            long completedAt = ((Number) map.get("completedAt")).longValue();
            int completionCount = ((Number) map.getOrDefault("completionCount", 1)).intValue();
            boolean rewarded = (Boolean) map.getOrDefault("rewarded", false);
            
            return new CompletedQuestDTO(questId, completedAt, completionCount, rewarded);
            
        } catch (Exception e) {
            LogUtil.warning("CompletedQuestDTO 파싱 실패: " + e.getMessage());
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
            data.activeQuests().put(progress.getQuestId().name(), progress);
            return savePlayerQuests(playerId, new PlayerQuestDTO(
                    data.playerId(),
                    data.activeQuests(),
                    data.completedQuests(),
                    System.currentTimeMillis()
            ));
        });
    }
    
    /**
     * 퀘스트 완료 처리
     */
    @NotNull
    public CompletableFuture<Void> completeQuest(@NotNull UUID playerId, @NotNull String questId,
                                                  @NotNull CompletedQuestDTO completed) {
        return getPlayerQuests(playerId).thenCompose(data -> {
            data.activeQuests().remove(questId);
            data.completedQuests().put(questId, completed);
            return savePlayerQuests(playerId, new PlayerQuestDTO(
                    data.playerId(),
                    data.activeQuests(),
                    data.completedQuests(),
                    System.currentTimeMillis()
            ));
        });
    }
    
    /**
     * 활성 퀘스트 제거
     */
    @NotNull
    public CompletableFuture<Void> removeActiveQuest(@NotNull UUID playerId, @NotNull String questId) {
        return getPlayerQuests(playerId).thenCompose(data -> {
            data.activeQuests().remove(questId);
            return savePlayerQuests(playerId, new PlayerQuestDTO(
                    data.playerId(),
                    data.activeQuests(),
                    data.completedQuests(),
                    System.currentTimeMillis()
            ));
        });
    }
    
    /**
     * 퀘스트 진행 중인지 확인
     */
    @NotNull
    public CompletableFuture<Boolean> hasActiveQuest(@NotNull UUID playerId, @NotNull String questId) {
        return getPlayerQuests(playerId).thenApply(data -> 
                data.activeQuests().containsKey(questId));
    }
    
    /**
     * 퀘스트 완료 여부 확인
     */
    @NotNull
    public CompletableFuture<Boolean> hasCompletedQuest(@NotNull UUID playerId, @NotNull String questId) {
        return getPlayerQuests(playerId).thenApply(data -> 
                data.completedQuests().containsKey(questId));
    }
}