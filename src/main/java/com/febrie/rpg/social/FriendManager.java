package com.febrie.rpg.social;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.cache.UnifiedCacheManager;
import com.febrie.rpg.database.FirestoreManager;
import com.febrie.rpg.database.service.impl.FriendshipFirestoreService;
import com.febrie.rpg.dto.social.FriendRequestDTO;
import com.febrie.rpg.dto.social.FriendshipDTO;
import com.febrie.rpg.util.LogUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.cloud.firestore.Firestore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 친구 시스템 관리자
 * 친구 요청, 친구 목록 등을 관리
 *
 * @author Febrie
 */
public class FriendManager {

    private static FriendManager instance;

    private final RPGMain plugin;
    private final FriendshipFirestoreService friendshipService;
    private final UnifiedCacheManager cacheManager;

    // Caffeine 캐시
    private final Cache<UUID, Set<FriendshipDTO>> friendsCache;
    private final Cache<UUID, Set<FriendRequestDTO>> pendingRequestsCache;

    // 캐시 유효 시간 (5분)
    private static final Duration CACHE_DURATION = Duration.ofMinutes(5);

    public FriendManager(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        Firestore firestore = plugin.getFirestoreManager().getFirestore();
        this.friendshipService = new FriendshipFirestoreService(plugin, firestore);
        this.cacheManager = UnifiedCacheManager.getInstance();
        
        // Caffeine 캐시 초기화
        this.friendsCache = cacheManager.createCache("friends", CACHE_DURATION);
        this.pendingRequestsCache = cacheManager.createCache("friendRequests", CACHE_DURATION);
        
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
        // 대상 플레이어 조회
        Player toPlayer = Bukkit.getPlayerExact(toPlayerName);
        if (toPlayer == null) {
            from.sendMessage("§c해당 플레이어를 찾을 수 없습니다: " + toPlayerName);
            return CompletableFuture.completedFuture(false);
        }

        UUID fromId = from.getUniqueId();
        UUID toId = toPlayer.getUniqueId();

        // 자기 자신에게 친구 요청 방지
        if (fromId.equals(toId)) {
            from.sendMessage("§c자기 자신에게는 친구 요청을 보낼 수 없습니다.");
            return CompletableFuture.completedFuture(false);
        }

        // 이미 친구인지 확인
        return areFriends(fromId, toId).thenCompose(alreadyFriends -> {
            if (alreadyFriends) {
                from.sendMessage("§c이미 " + toPlayerName + "님과 친구입니다.");
                return CompletableFuture.completedFuture(false);
            }

            // 친구 관계 생성 (요청 즉시 수락으로 간소화)
            return friendshipService.createFriendship(
                fromId, from.getName(),
                toId, toPlayer.getName()
            ).thenApply(v -> {
                // 캐시 무효화
                friendsCache.invalidate(fromId);
                friendsCache.invalidate(toId);

                // 알림
                from.sendMessage("§a" + toPlayerName + "님과 친구가 되었습니다!");
                if (toPlayer.isOnline()) {
                    toPlayer.sendMessage("§a" + from.getName() + "님과 친구가 되었습니다!");
                }

                LogUtil.info("친구 관계 생성: " + from.getName() + " <-> " + toPlayerName);
                return true;
            }).exceptionally(ex -> {
                LogUtil.warning("친구 요청 실패: " + ex.getMessage());
                from.sendMessage("§c친구 요청 중 오류가 발생했습니다.");
                return false;
            });
        });
    }

    /**
     * 친구 삭제
     */
    @NotNull
    public CompletableFuture<Boolean> removeFriend(@NotNull Player player, @NotNull UUID friendId) {
        UUID playerId = player.getUniqueId();

        return friendshipService.deleteFriendship(playerId, friendId)
            .thenApply(v -> {
                // 캐시 무효화
                friendsCache.invalidate(playerId);
                friendsCache.invalidate(friendId);

                // 알림
                player.sendMessage("§e친구 관계가 해제되었습니다.");
                
                // 상대방이 온라인이면 알림
                Player friend = Bukkit.getPlayer(friendId);
                if (friend != null) {
                    friend.sendMessage("§e" + player.getName() + "님과의 친구 관계가 해제되었습니다.");
                }

                LogUtil.info("친구 관계 해제: " + player.getName() + " <-> " + friendId);
                return true;
            })
            .exceptionally(ex -> {
                LogUtil.warning("친구 삭제 실패: " + ex.getMessage());
                player.sendMessage("§c친구 삭제 중 오류가 발생했습니다.");
                return false;
            });
    }

    /**
     * 플레이어의 친구 목록 가져오기
     */
    @NotNull
    public CompletableFuture<Set<FriendshipDTO>> getFriends(@NotNull UUID playerId) {
        // Caffeine 캐시에서 확인 (없으면 로더 실행)
        Set<FriendshipDTO> cached = friendsCache.get(playerId, key -> {
            try {
                // DB에서 동기적으로 조회
                return friendshipService.getFriendships(key)
                    .thenApply(HashSet::new)
                    .exceptionally(ex -> {
                        LogUtil.warning("친구 목록 조회 실패 [" + key + "]: " + ex.getMessage());
                        return new HashSet<>();
                    })
                    .join(); // CompletableFuture를 동기화
            } catch (Exception e) {
                LogUtil.warning("친구 목록 캐시 로드 실패 [" + key + "]: " + e.getMessage());
                return new HashSet<>();
            }
        });
        
        return CompletableFuture.completedFuture(cached != null ? new HashSet<>(cached) : new HashSet<>());
    }

    /**
     * 두 플레이어가 친구인지 확인
     */
    @NotNull
    public CompletableFuture<Boolean> areFriends(@NotNull UUID player1Id, @NotNull UUID player2Id) {
        return friendshipService.areFriends(player1Id, player2Id);
    }

    /**
     * 친구 수 조회
     */
    @NotNull
    public CompletableFuture<Integer> getFriendCount(@NotNull UUID playerId) {
        return friendshipService.getFriendCount(playerId);
    }

    /**
     * 친구 요청 수락 (현재 미지원)
     */
    @NotNull
    public CompletableFuture<Boolean> acceptFriendRequest(@NotNull Player player, @NotNull String requestId) {
        player.sendMessage("§c친구 요청 기능은 현재 지원하지 않습니다.");
        return CompletableFuture.completedFuture(false);
    }
    
    /**
     * 친구 요청 거절 (현재 미지원)
     */
    @NotNull
    public CompletableFuture<Boolean> rejectFriendRequest(@NotNull Player player, @NotNull String requestId) {
        player.sendMessage("§c친구 요청 기능은 현재 지원하지 않습니다.");
        return CompletableFuture.completedFuture(false);
    }
    
    /**
     * 받은 친구 요청 목록 조회 (현재 미지원)
     */
    @NotNull
    public CompletableFuture<Set<FriendRequestDTO>> getPendingRequests(@NotNull UUID playerId) {
        return CompletableFuture.completedFuture(new HashSet<>());
    }

    /**
     * 온라인 친구 목록 조회
     */
    @NotNull
    public CompletableFuture<List<Player>> getOnlineFriends(@NotNull UUID playerId) {
        return getFriends(playerId).thenApply(friendships -> 
            friendships.stream()
                .map(friendship -> {
                    UUID friendId = friendship.player1Uuid().equals(playerId) 
                        ? friendship.player2Uuid() 
                        : friendship.player1Uuid();
                    return Bukkit.getPlayer(friendId);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
        );
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
        friendsCache.invalidate(playerId);
        pendingRequestsCache.invalidate(playerId);
    }

    /**
     * 모든 캐시 정리
     */
    public void clearAllCache() {
        friendsCache.invalidateAll();
        pendingRequestsCache.invalidateAll();
    }

    /**
     * 서비스 종료
     */
    public void shutdown() {
        clearAllCache();
        // Firestore 서비스는 GenericFirestoreService를 사용하므로 별도 shutdown 불필요
    }
}