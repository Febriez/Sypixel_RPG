package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.BaseFirestoreService;
import com.febrie.rpg.database.service.GenericFirestoreService;
import com.febrie.rpg.dto.quest.PlayerQuestDTO;
import com.febrie.rpg.dto.quest.ActiveQuestDTO;
import com.febrie.rpg.dto.quest.CompletedQuestDTO;
import com.google.cloud.firestore.Firestore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.DocumentSnapshot;
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
        // Use the DTO's built-in toMap method directly
        return dto.toMap();
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
            
            // Use the DTO's built-in fromMap method directly
            return PlayerQuestDTO.fromMap(data);

        } catch (Exception e) {
            LogUtil.warning("퀘스트 데이터 파싱 실패 [" + document.getId() + "]: " + e.getMessage());
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
    public CompletableFuture<Void> updateActiveQuest(@NotNull UUID playerId, @NotNull String instanceId, 
                                                    @NotNull ActiveQuestDTO activeData) {
        return getPlayerQuests(playerId).thenCompose(data -> {
            Map<String, ActiveQuestDTO> updatedActiveQuests = new HashMap<>(data.activeQuests());
            updatedActiveQuests.put(instanceId, activeData);
            return savePlayerQuests(playerId, new PlayerQuestDTO(
                    data.playerId(), 
                    updatedActiveQuests, 
                    data.completedQuests(), 
                    data.claimedQuests(),
                    System.currentTimeMillis()));
        });
    }

    /**
     * 퀘스트 완료 처리
     */
    @NotNull
    public CompletableFuture<Void> completeQuest(@NotNull UUID playerId, @NotNull String instanceId, 
                                                 @NotNull CompletedQuestDTO completed) {
        return getPlayerQuests(playerId).thenCompose(data -> {
            Map<String, ActiveQuestDTO> updatedActiveQuests = new HashMap<>(data.activeQuests());
            Map<String, CompletedQuestDTO> updatedCompletedQuests = new HashMap<>(data.completedQuests());
            updatedActiveQuests.remove(instanceId);
            updatedCompletedQuests.put(instanceId, completed);
            return savePlayerQuests(playerId, new PlayerQuestDTO(
                    data.playerId(), 
                    updatedActiveQuests, 
                    updatedCompletedQuests, 
                    data.claimedQuests(),
                    System.currentTimeMillis()));
        });
    }

    /**
     * 활성 퀘스트 제거
     */
    @NotNull
    public CompletableFuture<Void> removeActiveQuest(@NotNull UUID playerId, @NotNull String instanceId) {
        return getPlayerQuests(playerId).thenCompose(data -> {
            Map<String, ActiveQuestDTO> updatedActiveQuests = new HashMap<>(data.activeQuests());
            updatedActiveQuests.remove(instanceId);
            return savePlayerQuests(playerId, new PlayerQuestDTO(
                    data.playerId(), 
                    updatedActiveQuests, 
                    data.completedQuests(), 
                    data.claimedQuests(),
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