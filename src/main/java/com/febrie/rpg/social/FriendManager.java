package com.febrie.rpg.social;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.social.FriendRequestDTO;
import com.febrie.rpg.dto.social.FriendshipDTO;
import com.febrie.rpg.player.PlayerSettings;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.FirestoreUtil;
import com.febrie.rpg.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 친구 시스템 관리자
 * 친구 요청, 친구 목록, 차단 등을 관리
 * 
 * NOTE: 현재 모든 기능이 비활성화됨 - Firestore 서비스 재구현 필요
 *
 * @author Febrie
 */
public class FriendManager {
    
    private static FriendManager instance;
    
    private final RPGMain plugin;
    private final Gson gson = new Gson();
    
    // 캐시
    private final Map<UUID, Set<FriendshipDTO>> friendsCache = new ConcurrentHashMap<>();
    private final Map<UUID, Set<FriendRequestDTO>> pendingRequestsCache = new ConcurrentHashMap<>();
    
    // Firestore 컬렉션 이름
    private static final String FRIENDS_COLLECTION = "friendships";
    private static final String FRIEND_REQUESTS_COLLECTION = "friend_requests";
    
    public FriendManager(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        instance = this;
    }
    
    public static FriendManager getInstance() {
        return instance;
    }
    
    /**
     * 친구 요청 보내기
     */
    @NotNull
    public CompletableFuture<Boolean> sendFriendRequest(@NotNull Player from, @NotNull String toPlayerName, @Nullable String message) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            from.sendMessage("§c친구 요청 기능이 현재 비활성화되어 있습니다. (FirestoreRestService 없음)");
        });
        return CompletableFuture.completedFuture(false);
    }
    
    /**
     * 친구 요청 수락
     */
    @NotNull
    public CompletableFuture<Boolean> acceptFriendRequest(@NotNull Player player, @NotNull String requestId) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.sendMessage("§c친구 요청 수락 기능이 현재 비활성화되어 있습니다. (FirestoreRestService 없음)");
        });
        return CompletableFuture.completedFuture(false);
    }
    
    /**
     * 친구 요청 거절
     */
    @NotNull
    public CompletableFuture<Boolean> rejectFriendRequest(@NotNull Player player, @NotNull String requestId) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.sendMessage("§c친구 요청 거절 기능이 현재 비활성화되어 있습니다. (FirestoreRestService 없음)");
        });
        return CompletableFuture.completedFuture(false);
    }
    
    /**
     * 친구 삭제
     */
    @NotNull
    public CompletableFuture<Boolean> removeFriend(@NotNull Player player, @NotNull UUID friendId) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.sendMessage("§c친구 삭제 기능이 현재 비활성화되어 있습니다. (FirestoreRestService 없음)");
        });
        return CompletableFuture.completedFuture(false);
    }
    
    /**
     * 플레이어의 친구 목록 가져오기
     */
    @NotNull
    public CompletableFuture<Set<FriendshipDTO>> getFriends(@NotNull UUID playerId) {
        return CompletableFuture.completedFuture(new HashSet<>());
    }
    
    /**
     * 플레이어의 받은 친구 요청 목록 가져오기
     */
    @NotNull
    public CompletableFuture<Set<FriendRequestDTO>> getPendingRequests(@NotNull UUID playerId) {
        return CompletableFuture.completedFuture(new HashSet<>());
    }
    
    /**
     * 두 플레이어가 친구인지 확인
     */
    @NotNull
    public CompletableFuture<Boolean> areFriends(@NotNull UUID player1Id, @NotNull UUID player2Id) {
        return getFriends(player1Id).thenApply(friends -> {
            return friends.stream().anyMatch(friendship -> friendship.containsPlayer(player2Id));
        });
    }
    
    /**
     * 진행 중인 친구 요청이 있는지 확인
     */
    @NotNull
    public CompletableFuture<Boolean> hasPendingRequest(@NotNull UUID fromId, @NotNull UUID toId) {
        return CompletableFuture.completedFuture(false);
    }
    
    /**
     * 플레이어가 온라인인지 확인
     */
    public boolean isPlayerOnline(@NotNull UUID playerId) {
        return Bukkit.getPlayer(playerId) != null;
    }
    
    /**
     * 캐시 정리
     */
    public void clearCache(@NotNull UUID playerId) {
        friendsCache.remove(playerId);
        pendingRequestsCache.remove(playerId);
    }
    
    /**
     * 모든 캐시 정리
     */
    public void clearAllCache() {
        friendsCache.clear();
        pendingRequestsCache.clear();
    }
}