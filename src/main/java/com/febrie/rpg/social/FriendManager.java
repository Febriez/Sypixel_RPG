package com.febrie.rpg.social;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.FirestoreRestService;
import com.febrie.rpg.dto.social.FriendRequestDTO;
import com.febrie.rpg.dto.social.FriendshipDTO;
import com.febrie.rpg.player.PlayerSettings;
import com.febrie.rpg.player.RPGPlayer;
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
 * @author Febrie
 */
public class FriendManager {
    
    private static FriendManager instance;
    
    private final RPGMain plugin;
    private final FirestoreRestService firestoreService;
    private final Gson gson = new Gson();
    
    // 캐시
    private final Map<UUID, Set<FriendshipDTO>> friendsCache = new ConcurrentHashMap<>();
    private final Map<UUID, Set<FriendRequestDTO>> pendingRequestsCache = new ConcurrentHashMap<>();
    
    // Firestore 컬렉션 이름
    private static final String FRIENDS_COLLECTION = "friendships";
    private static final String FRIEND_REQUESTS_COLLECTION = "friend_requests";
    
    public FriendManager(@NotNull RPGMain plugin, @NotNull FirestoreRestService firestoreService) {
        this.plugin = plugin;
        this.firestoreService = firestoreService;
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
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 받는 사람이 온라인인지 확인
                Player toPlayer = Bukkit.getPlayer(toPlayerName);
                if (toPlayer == null) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        from.sendMessage("§c해당 플레이어를 찾을 수 없습니다: " + toPlayerName);
                    });
                    return false;
                }
                
                // 자기 자신에게 친구 요청 방지
                if (from.equals(toPlayer)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        from.sendMessage("§c자기 자신에게는 친구 요청을 보낼 수 없습니다.");
                    });
                    return false;
                }
                
                // 받는 사람의 친구 요청 설정 확인
                RPGPlayer toRPGPlayer = plugin.getRPGPlayerManager().getOrCreatePlayer(toPlayer);
                PlayerSettings toSettings = toRPGPlayer.getPlayerSettings();
                
                if (!toSettings.isFriendRequestsEnabled()) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        from.sendMessage("§c해당 플레이어는 친구 요청을 받지 않습니다.");
                    });
                    return false;
                }
                
                // 이미 친구인지 확인
                if (areFriends(from.getUniqueId(), toPlayer.getUniqueId()).join()) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        from.sendMessage("§c이미 친구입니다: " + toPlayerName);
                    });
                    return false;
                }
                
                // 이미 친구 요청이 있는지 확인
                if (hasPendingRequest(from.getUniqueId(), toPlayer.getUniqueId()).join()) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        from.sendMessage("§c이미 친구 요청을 보냈습니다: " + toPlayerName);
                    });
                    return false;
                }
                
                // 친구 요청 생성
                FriendRequestDTO request = new FriendRequestDTO(
                    from.getUniqueId(), from.getName(),
                    toPlayer.getUniqueId(), toPlayer.getName(),
                    message
                );
                
                // Firestore에 저장
                Map<String, Object> requestData = convertToMap(request);
                String documentId = UUID.randomUUID().toString();
                
                boolean success = firestoreService.setDocument(FRIEND_REQUESTS_COLLECTION, documentId, requestData);
                
                if (success) {
                    request.setId(documentId);
                    
                    // 캐시 업데이트
                    pendingRequestsCache.computeIfAbsent(toPlayer.getUniqueId(), k -> ConcurrentHashMap.newKeySet()).add(request);
                    
                    // 메시지 전송
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        from.sendMessage("§a친구 요청을 보냈습니다: " + toPlayerName);
                        
                        // 알림 설정에 따라 받는 사람에게 알림
                        if (toSettings.getInviteNotificationsMode().equals("ALL") || 
                            toSettings.getInviteNotificationsMode().equals("FRIEND_ONLY")) {
                            toPlayer.sendMessage("§e" + from.getName() + "님이 친구 요청을 보냈습니다!");
                            toPlayer.sendMessage("§e'/친구 목록'에서 확인하세요.");
                        }
                    });
                    
                    return true;
                } else {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        from.sendMessage("§c친구 요청 전송에 실패했습니다.");
                    });
                    return false;
                }
                
            } catch (Exception e) {
                LogUtil.error("친구 요청 전송 중 오류 발생", e);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    from.sendMessage("§c친구 요청 전송 중 오류가 발생했습니다.");
                });
                return false;
            }
        });
    }
    
    /**
     * 친구 요청 수락
     */
    @NotNull
    public CompletableFuture<Boolean> acceptFriendRequest(@NotNull Player player, @NotNull String requestId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 요청 정보 가져오기
                Map<String, Object> requestData = firestoreService.getDocument(FRIEND_REQUESTS_COLLECTION, requestId);
                if (requestData == null) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.sendMessage("§c친구 요청을 찾을 수 없습니다.");
                    });
                    return false;
                }
                
                FriendRequestDTO request = convertFromMap(requestData, FriendRequestDTO.class);
                request.setId(requestId);
                
                // 요청의 대상자가 맞는지 확인
                if (!request.getToPlayerId().equals(player.getUniqueId())) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.sendMessage("§c잘못된 친구 요청입니다.");
                    });
                    return false;
                }
                
                // 이미 처리된 요청인지 확인
                if (!"PENDING".equals(request.getStatus())) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.sendMessage("§c이미 처리된 친구 요청입니다.");
                    });
                    return false;
                }
                
                // 친구 관계 생성
                FriendshipDTO friendship = new FriendshipDTO(
                    request.getFromPlayerId(), request.getFromPlayerName(),
                    request.getToPlayerId(), request.getToPlayerName()
                );
                
                Map<String, Object> friendshipData = convertToMap(friendship);
                String friendshipId = UUID.randomUUID().toString();
                
                boolean friendshipCreated = firestoreService.setDocument(FRIENDS_COLLECTION, friendshipId, friendshipData);
                
                if (friendshipCreated) {
                    friendship.setId(friendshipId);
                    
                    // 요청 상태 업데이트
                    request.setStatus("ACCEPTED");
                    Map<String, Object> updatedRequestData = convertToMap(request);
                    firestoreService.setDocument(FRIEND_REQUESTS_COLLECTION, requestId, updatedRequestData);
                    
                    // 캐시 업데이트
                    friendsCache.computeIfAbsent(player.getUniqueId(), k -> ConcurrentHashMap.newKeySet()).add(friendship);
                    friendsCache.computeIfAbsent(request.getFromPlayerId(), k -> ConcurrentHashMap.newKeySet()).add(friendship);
                    
                    // 요청 캐시에서 제거
                    Set<FriendRequestDTO> requests = pendingRequestsCache.get(player.getUniqueId());
                    if (requests != null) {
                        requests.removeIf(r -> r.getId().equals(requestId));
                    }
                    
                    // 메시지 전송
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.sendMessage("§a" + request.getFromPlayerName() + "님과 친구가 되었습니다!");
                        
                        // 요청자에게도 알림
                        Player fromPlayer = Bukkit.getPlayer(request.getFromPlayerId());
                        if (fromPlayer != null) {
                            fromPlayer.sendMessage("§a" + player.getName() + "님이 친구 요청을 수락했습니다!");
                        }
                    });
                    
                    return true;
                } else {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.sendMessage("§c친구 요청 수락에 실패했습니다.");
                    });
                    return false;
                }
                
            } catch (Exception e) {
                LogUtil.error("친구 요청 수락 중 오류 발생", e);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.sendMessage("§c친구 요청 수락 중 오류가 발생했습니다.");
                });
                return false;
            }
        });
    }
    
    /**
     * 친구 요청 거절
     */
    @NotNull
    public CompletableFuture<Boolean> rejectFriendRequest(@NotNull Player player, @NotNull String requestId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 요청 정보 가져오기
                Map<String, Object> requestData = firestoreService.getDocument(FRIEND_REQUESTS_COLLECTION, requestId);
                if (requestData == null) {
                    return false;
                }
                
                FriendRequestDTO request = convertFromMap(requestData, FriendRequestDTO.class);
                request.setId(requestId);
                
                // 요청 상태 업데이트
                request.setStatus("REJECTED");
                Map<String, Object> updatedRequestData = convertToMap(request);
                boolean success = firestoreService.setDocument(FRIEND_REQUESTS_COLLECTION, requestId, updatedRequestData);
                
                if (success) {
                    // 캐시에서 제거
                    Set<FriendRequestDTO> requests = pendingRequestsCache.get(player.getUniqueId());
                    if (requests != null) {
                        requests.removeIf(r -> r.getId().equals(requestId));
                    }
                    
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.sendMessage("§7친구 요청을 거절했습니다: " + request.getFromPlayerName());
                    });
                    
                    return true;
                }
                
            } catch (Exception e) {
                LogUtil.error("친구 요청 거절 중 오류 발생", e);
            }
            return false;
        });
    }
    
    /**
     * 친구 삭제
     */
    @NotNull
    public CompletableFuture<Boolean> removeFriend(@NotNull Player player, @NotNull UUID friendId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 친구 관계 찾기
                Set<FriendshipDTO> friendships = getFriends(player.getUniqueId()).join();
                FriendshipDTO targetFriendship = null;
                
                for (FriendshipDTO friendship : friendships) {
                    if (friendship.containsPlayer(friendId)) {
                        targetFriendship = friendship;
                        break;
                    }
                }
                
                if (targetFriendship == null) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.sendMessage("§c친구 관계를 찾을 수 없습니다.");
                    });
                    return false;
                }
                
                // Firestore에서 삭제
                boolean success = firestoreService.deleteDocument(FRIENDS_COLLECTION, targetFriendship.getId());
                
                if (success) {
                    // 캐시에서 제거
                    Set<FriendshipDTO> playerFriends = friendsCache.get(player.getUniqueId());
                    if (playerFriends != null) {
                        playerFriends.remove(targetFriendship);
                    }
                    
                    Set<FriendshipDTO> friendFriends = friendsCache.get(friendId);
                    if (friendFriends != null) {
                        friendFriends.remove(targetFriendship);
                    }
                    
                    String friendName = targetFriendship.getFriendInfo(player.getUniqueId()).getPlayerName();
                    
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.sendMessage("§7친구에서 삭제했습니다: " + friendName);
                    });
                    
                    return true;
                }
                
            } catch (Exception e) {
                LogUtil.error("친구 삭제 중 오류 발생", e);
            }
            return false;
        });
    }
    
    /**
     * 플레이어의 친구 목록 가져오기
     */
    @NotNull
    public CompletableFuture<Set<FriendshipDTO>> getFriends(@NotNull UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            // 캐시에서 먼저 확인
            Set<FriendshipDTO> cached = friendsCache.get(playerId);
            if (cached != null) {
                return new HashSet<>(cached);
            }
            
            // Firestore에서 조회
            try {
                String query = String.format("player1Id==\"%s\" OR player2Id==\"%s\"", 
                    playerId.toString(), playerId.toString());
                
                Map<String, Object> response = firestoreService.queryDocuments(FRIENDS_COLLECTION, query);
                Set<FriendshipDTO> friends = new HashSet<>();
                
                if (response != null && response.containsKey("documents")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");
                    
                    for (Map<String, Object> doc : documents) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> fields = (Map<String, Object>) doc.get("fields");
                        FriendshipDTO friendship = parseFirestoreDocument(fields, FriendshipDTO.class);
                        String documentId = extractDocumentId((String) doc.get("name"));
                        friendship.setId(documentId);
                        friends.add(friendship);
                    }
                }
                
                // 캐시 업데이트
                friendsCache.put(playerId, friends);
                return friends;
                
            } catch (Exception e) {
                LogUtil.error("친구 목록 조회 중 오류 발생", e);
                return new HashSet<>();
            }
        });
    }
    
    /**
     * 플레이어의 받은 친구 요청 목록 가져오기
     */
    @NotNull
    public CompletableFuture<Set<FriendRequestDTO>> getPendingRequests(@NotNull UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            // 캐시에서 먼저 확인
            Set<FriendRequestDTO> cached = pendingRequestsCache.get(playerId);
            if (cached != null) {
                return new HashSet<>(cached);
            }
            
            // Firestore에서 조회
            try {
                String query = String.format("toPlayerId==\"%s\" AND status==\"PENDING\"", playerId.toString());
                
                Map<String, Object> response = firestoreService.queryDocuments(FRIEND_REQUESTS_COLLECTION, query);
                Set<FriendRequestDTO> requests = new HashSet<>();
                
                if (response != null && response.containsKey("documents")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");
                    
                    for (Map<String, Object> doc : documents) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> fields = (Map<String, Object>) doc.get("fields");
                        FriendRequestDTO request = parseFirestoreDocument(fields, FriendRequestDTO.class);
                        String documentId = extractDocumentId((String) doc.get("name"));
                        request.setId(documentId);
                        requests.add(request);
                    }
                }
                
                // 캐시 업데이트
                pendingRequestsCache.put(playerId, requests);
                return requests;
                
            } catch (Exception e) {
                LogUtil.error("친구 요청 목록 조회 중 오류 발생", e);
                return new HashSet<>();
            }
        });
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
        return CompletableFuture.supplyAsync(() -> {
            try {
                String query = String.format("fromPlayerId==\"%s\" AND toPlayerId==\"%s\" AND status==\"PENDING\"", 
                    fromId.toString(), toId.toString());
                
                Map<String, Object> response = firestoreService.queryDocuments(FRIEND_REQUESTS_COLLECTION, query);
                
                if (response != null && response.containsKey("documents")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");
                    return !documents.isEmpty();
                }
                
                return false;
                
            } catch (Exception e) {
                LogUtil.error("친구 요청 확인 중 오류 발생", e);
                return false;
            }
        });
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
    
    // Helper methods
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object obj) {
        String json = gson.toJson(obj);
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return gson.fromJson(json, type);
    }
    
    private <T> T convertFromMap(Map<String, Object> map, Class<T> clazz) {
        String json = gson.toJson(map);
        return gson.fromJson(json, clazz);
    }
    
    private <T> T parseFirestoreDocument(Map<String, Object> fields, Class<T> clazz) {
        Map<String, Object> converted = new HashMap<>();
        
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> fieldValue = (Map<String, Object>) entry.getValue();
            Object value = fieldValue.values().iterator().next();
            converted.put(entry.getKey(), value);
        }
        
        return convertFromMap(converted, clazz);
    }
    
    private String extractDocumentId(String documentPath) {
        String[] parts = documentPath.split("/");
        return parts[parts.length - 1];
    }
}