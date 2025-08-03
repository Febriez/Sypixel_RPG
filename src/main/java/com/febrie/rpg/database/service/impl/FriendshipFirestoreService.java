package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.BaseFirestoreService;
import com.febrie.rpg.dto.social.FriendshipDTO;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 친구 관계 Firestore 서비스
 * Friendship 컬렉션 관리
 *
 * @author Febrie, CoffeeTory
 */
public class FriendshipFirestoreService extends BaseFirestoreService<FriendshipDTO> {
    
    private static final String COLLECTION_NAME = "Friendship";
    
    public FriendshipFirestoreService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        super(plugin, firestore, COLLECTION_NAME, FriendshipDTO.class);
    }
    
    @Override
    protected Map<String, Object> toMap(@NotNull FriendshipDTO dto) {
        return dto.toMap();
    }
    
    @Override
    @Nullable
    protected FriendshipDTO fromDocument(@NotNull DocumentSnapshot document) {
        if (!document.exists()) return null;
        try {
            Map<String, Object> data = document.getData();
            if (data != null) {
                return FriendshipDTO.fromMap(data);
            }
            return null;
        } catch (Exception e) {
            LogUtil.warning("친구관계 데이터 파싱 실패 [" + document.getId() + "]: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 플레이어의 친구 목록 조회
     */
    @NotNull
    public CompletableFuture<List<FriendshipDTO>> getFriendships(@NotNull UUID playerId) {
        // 두 필드 중 하나라도 매치되는 경우를 찾기 위해 별도 쿼리 필요
        return query("player1Uuid", playerId.toString())
                .thenCompose(friendships1 -> 
                    query("player2Uuid", playerId.toString())
                        .thenApply(friendships2 -> {
                            List<FriendshipDTO> all = new ArrayList<>(friendships1);
                            all.addAll(friendships2);
                            return all;
                        })
                );
    }
    
    /**
     * 친구 관계 생성
     */
    @NotNull
    public CompletableFuture<Void> createFriendship(@NotNull UUID player1, @NotNull String player1Name,
                                                   @NotNull UUID player2, @NotNull String player2Name) {
        String friendshipId = generateFriendshipId(player1, player2);
        FriendshipDTO friendship = new FriendshipDTO(player1, player1Name, player2, player2Name);
        return save(friendshipId, friendship);
    }
    
    /**
     * 친구 관계 삭제
     */
    @NotNull
    public CompletableFuture<Void> deleteFriendship(@NotNull UUID player1, @NotNull UUID player2) {
        String friendshipId = generateFriendshipId(player1, player2);
        return delete(friendshipId);
    }
    
    /**
     * 친구 여부 확인
     */
    @NotNull
    public CompletableFuture<Boolean> areFriends(@NotNull UUID player1, @NotNull UUID player2) {
        return getFriendships(player1).thenApply(friendships -> 
                friendships.stream().anyMatch(f -> 
                        f.player1Uuid().equals(player2) || 
                        f.player2Uuid().equals(player2)));
    }
    
    /**
     * 친구 수 조회
     */
    @NotNull
    public CompletableFuture<Integer> getFriendCount(@NotNull UUID playerId) {
        return getFriendships(playerId).thenApply(List::size);
    }
    
    /**
     * 친구 ID 생성 (일관성 유지를 위해 정렬)
     */
    private String generateFriendshipId(UUID player1, UUID player2) {
        // 항상 일관된 ID를 생성하기 위해 UUID를 정렬
        if (player1.toString().compareTo(player2.toString()) < 0) {
            return player1.toString() + "_" + player2.toString();
        } else {
            return player2.toString() + "_" + player1.toString();
        }
    }
}