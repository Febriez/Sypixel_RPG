package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.GenericFirestoreService;
import com.febrie.rpg.dto.social.FriendshipDTO;
import com.google.cloud.firestore.Firestore;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
/**
 * 친구 관계 Firestore 서비스
 * Friendship 컬렉션 관리
 *
 * @author Febrie, CoffeeTory
 */
public class FriendshipFirestoreService {
    
    private static final String COLLECTION_NAME = "Friendship";
    private final GenericFirestoreService<FriendshipDTO> service;
    public FriendshipFirestoreService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        this.service = GenericFirestoreService.create(
            plugin,
            firestore,
            COLLECTION_NAME,
            FriendshipDTO.class,
            FriendshipDTO::toMap,
            FriendshipDTO::fromMap,
            id -> null // 친구관계는 기본값이 없음
        );
    }
    /**
     * 문서 ID로 데이터 조회
     */
    @NotNull
    public CompletableFuture<FriendshipDTO> get(@NotNull String id) {
        return service.get(id);
    }
    
    /**
     * 데이터 저장
     */
    public CompletableFuture<Void> save(@NotNull String id, @NotNull FriendshipDTO data) {
        return service.save(id, data);
    }
    
    /**
     * 데이터 삭제
     */
    public CompletableFuture<Void> delete(@NotNull String id) {
        return service.delete(id);
    }
    
    /**
     * 플레이어의 친구 목록 조회
     */
    public CompletableFuture<List<FriendshipDTO>> getFriendships(@NotNull UUID playerId) {
        // 두 필드 중 하나라도 매치되는 경우를 찾기 위해 별도 쿼리 필요
        return service.query("player1Uuid", playerId.toString())
                .thenCompose(friendships1 -> 
                    service.query("player2Uuid", playerId.toString())
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
    public CompletableFuture<Void> createFriendship(@NotNull UUID player1, @NotNull String player1Name,
                                                   @NotNull UUID player2, @NotNull String player2Name) {
        String friendshipId = generateFriendshipId(player1, player2);
        FriendshipDTO friendship = new FriendshipDTO(player1, player1Name, player2, player2Name);
        return save(friendshipId, friendship);
    }
    
    /**
     * 친구 관계 삭제
     */
    public CompletableFuture<Void> deleteFriendship(@NotNull UUID player1, @NotNull UUID player2) {
        String friendshipId = generateFriendshipId(player1, player2);
        return delete(friendshipId);
    }
    
    /**
     * 친구 여부 확인
     */
    public CompletableFuture<Boolean> areFriends(@NotNull UUID player1, @NotNull UUID player2) {
        return getFriendships(player1).thenApply(friendships -> 
                friendships.stream().anyMatch(f -> 
                        f.player1Uuid().equals(player2) || 
                        f.player2Uuid().equals(player2)));
    }
    
    /**
     * 친구 수 조회
     */
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
