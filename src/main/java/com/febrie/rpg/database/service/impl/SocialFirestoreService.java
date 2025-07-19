package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.social.FriendshipDTO;
import com.febrie.rpg.dto.social.MailDTO;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 소셜 기능 Firestore 서비스
 * friendships, mail 컬렉션 관리
 *
 * @author Febrie, CoffeeTory
 */
public class SocialFirestoreService {
    
    private final RPGMain plugin;
    private final Firestore firestore;
    
    // Friendship 서비스
    private final FriendshipService friendshipService;
    
    // Mail 서비스
    private final MailService mailService;
    
    public SocialFirestoreService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        this.plugin = plugin;
        this.firestore = firestore;
        this.friendshipService = new FriendshipService();
        this.mailService = new MailService();
    }
    
    // ===== Friendship 관련 메소드 =====
    
    @NotNull
    public CompletableFuture<List<FriendshipDTO>> getFriendships(@NotNull UUID playerUuid) {
        return friendshipService.getFriendships(playerUuid);
    }
    
    @NotNull
    public CompletableFuture<Void> createFriendship(@NotNull FriendshipDTO friendship) {
        return friendshipService.createFriendship(friendship);
    }
    
    @NotNull
    public CompletableFuture<Void> removeFriendship(@NotNull String friendshipId) {
        return friendshipService.removeFriendship(friendshipId);
    }
    
    @NotNull
    public CompletableFuture<Boolean> areFriends(@NotNull UUID player1, @NotNull UUID player2) {
        return friendshipService.areFriends(player1, player2);
    }
    
    // ===== Mail 관련 메소드 =====
    
    @NotNull
    public CompletableFuture<List<MailDTO>> getPlayerMail(@NotNull UUID playerUuid) {
        return mailService.getPlayerMail(playerUuid);
    }
    
    @NotNull
    public CompletableFuture<List<MailDTO>> getUnreadMail(@NotNull UUID playerUuid) {
        return mailService.getUnreadMail(playerUuid);
    }
    
    @NotNull
    public CompletableFuture<Void> sendMail(@NotNull MailDTO mail) {
        return mailService.sendMail(mail);
    }
    
    @NotNull
    public CompletableFuture<Void> markAsRead(@NotNull String mailId) {
        return mailService.markAsRead(mailId);
    }
    
    @NotNull
    public CompletableFuture<Void> deleteMail(@NotNull String mailId) {
        return mailService.deleteMail(mailId);
    }
    
    /**
     * Friendship 내부 서비스 클래스
     */
    private class FriendshipService {
        private static final String COLLECTION_NAME = "friendships";
        
        @NotNull
        CompletableFuture<List<FriendshipDTO>> getFriendships(@NotNull UUID playerUuid) {
            String playerUuidStr = playerUuid.toString();
            
            // player1 또는 player2가 해당 플레이어인 모든 친구 관계 조회
            CompletableFuture<QuerySnapshot> query1Future = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("player1Uuid", playerUuidStr)
                    .get();
            
            CompletableFuture<QuerySnapshot> query2Future = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("player2Uuid", playerUuidStr)
                    .get();
            
            return CompletableFuture.allOf(query1Future, query2Future).thenApply(v -> {
                List<FriendshipDTO> friendships = new ArrayList<>();
                
                try {
                    QuerySnapshot snapshot1 = query1Future.get();
                    QuerySnapshot snapshot2 = query2Future.get();
                    
                    snapshot1.getDocuments().forEach(doc -> {
                        FriendshipDTO friendship = fromDocument(doc);
                        if (friendship != null) {
                            friendships.add(friendship);
                        }
                    });
                    
                    snapshot2.getDocuments().forEach(doc -> {
                        FriendshipDTO friendship = fromDocument(doc);
                        if (friendship != null) {
                            friendships.add(friendship);
                        }
                    });
                    
                } catch (Exception e) {
                    LogUtil.warning("친구 목록 조회 실패: " + e.getMessage());
                }
                
                return friendships;
            });
        }
        
        @NotNull
        CompletableFuture<Void> createFriendship(@NotNull FriendshipDTO friendship) {
            Map<String, Object> data = toMap(friendship);
            
            return CompletableFuture.runAsync(() -> {
                try {
                    firestore.collection(COLLECTION_NAME)
                            .document(friendship.friendshipId())
                            .set(data)
                            .get();
                    LogUtil.info("친구 관계 생성 성공: " + friendship.friendshipId());
                } catch (Exception e) {
                    LogUtil.warning("친구 관계 생성 실패: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            });
        }
        
        @NotNull
        CompletableFuture<Void> removeFriendship(@NotNull String friendshipId) {
            return CompletableFuture.runAsync(() -> {
                try {
                    firestore.collection(COLLECTION_NAME)
                            .document(friendshipId)
                            .delete()
                            .get();
                    LogUtil.info("친구 관계 삭제 성공: " + friendshipId);
                } catch (Exception e) {
                    LogUtil.warning("친구 관계 삭제 실패: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            });
        }
        
        @NotNull
        CompletableFuture<Boolean> areFriends(@NotNull UUID player1, @NotNull UUID player2) {
            return getFriendships(player1).thenApply(friendships -> 
                    friendships.stream().anyMatch(f -> 
                            f.player1Uuid().equals(player2.toString()) || 
                            f.player2Uuid().equals(player2.toString())));
        }
        
        private Map<String, Object> toMap(@NotNull FriendshipDTO dto) {
            Map<String, Object> map = new HashMap<>();
            map.put("friendshipId", dto.friendshipId());
            map.put("player1Uuid", dto.player1Uuid());
            map.put("player1Name", dto.player1Name());
            map.put("player2Uuid", dto.player2Uuid());
            map.put("player2Name", dto.player2Name());
            map.put("createdAt", dto.createdAt());
            return map;
        }
        
        private FriendshipDTO fromDocument(@NotNull DocumentSnapshot doc) {
            if (!doc.exists()) return null;
            
            try {
                return new FriendshipDTO(
                        doc.getString("friendshipId"),
                        doc.getString("player1Uuid"),
                        doc.getString("player1Name"),
                        doc.getString("player2Uuid"),
                        doc.getString("player2Name"),
                        doc.getLong("createdAt")
                );
            } catch (Exception e) {
                LogUtil.warning("FriendshipDTO 파싱 실패: " + e.getMessage());
                return null;
            }
        }
    }
    
    /**
     * Mail 내부 서비스 클래스
     */
    private class MailService {
        private static final String COLLECTION_NAME = "mail";
        
        @NotNull
        CompletableFuture<List<MailDTO>> getPlayerMail(@NotNull UUID playerUuid) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    QuerySnapshot snapshot = firestore.collection(COLLECTION_NAME)
                            .whereEqualTo("receiverUuid", playerUuid.toString())
                            .orderBy("sentAt", Query.Direction.DESCENDING)
                            .limit(100)  // 최근 100개만
                            .get()
                            .get();
                    
                    return snapshot.getDocuments().stream()
                            .map(this::fromDocument)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    
                } catch (Exception e) {
                    LogUtil.warning("메일 조회 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            });
        }
        
        @NotNull
        CompletableFuture<List<MailDTO>> getUnreadMail(@NotNull UUID playerUuid) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    QuerySnapshot snapshot = firestore.collection(COLLECTION_NAME)
                            .whereEqualTo("receiverUuid", playerUuid.toString())
                            .whereEqualTo("isRead", false)
                            .orderBy("sentAt", Query.Direction.DESCENDING)
                            .get()
                            .get();
                    
                    return snapshot.getDocuments().stream()
                            .map(this::fromDocument)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    
                } catch (Exception e) {
                    LogUtil.warning("읽지 않은 메일 조회 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            });
        }
        
        @NotNull
        CompletableFuture<Void> sendMail(@NotNull MailDTO mail) {
            Map<String, Object> data = toMap(mail);
            
            return CompletableFuture.runAsync(() -> {
                try {
                    firestore.collection(COLLECTION_NAME)
                            .document(mail.mailId())
                            .set(data)
                            .get();
                    LogUtil.info("메일 전송 성공: " + mail.mailId());
                } catch (Exception e) {
                    LogUtil.warning("메일 전송 실패: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            });
        }
        
        @NotNull
        CompletableFuture<Void> markAsRead(@NotNull String mailId) {
            return CompletableFuture.runAsync(() -> {
                try {
                    firestore.collection(COLLECTION_NAME)
                            .document(mailId)
                            .update("isRead", true, "readAt", System.currentTimeMillis())
                            .get();
                } catch (Exception e) {
                    LogUtil.warning("메일 읽음 처리 실패: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            });
        }
        
        @NotNull
        CompletableFuture<Void> deleteMail(@NotNull String mailId) {
            return CompletableFuture.runAsync(() -> {
                try {
                    firestore.collection(COLLECTION_NAME)
                            .document(mailId)
                            .delete()
                            .get();
                    LogUtil.info("메일 삭제 성공: " + mailId);
                } catch (Exception e) {
                    LogUtil.warning("메일 삭제 실패: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            });
        }
        
        private Map<String, Object> toMap(@NotNull MailDTO dto) {
            Map<String, Object> map = new HashMap<>();
            map.put("mailId", dto.mailId());
            map.put("senderUuid", dto.senderUuid());
            map.put("senderName", dto.senderName());
            map.put("receiverUuid", dto.receiverUuid());
            map.put("receiverName", dto.receiverName());
            map.put("subject", dto.subject());
            map.put("content", dto.content());
            map.put("sentAt", dto.sentAt());
            map.put("isRead", dto.isRead());
            
            if (dto.readAt() != null) {
                map.put("readAt", dto.readAt());
            }
            
            if (dto.expiresAt() != null) {
                map.put("expiresAt", dto.expiresAt());
            }
            
            if (!dto.attachments().isEmpty()) {
                map.put("attachments", dto.attachments());
            }
            
            return map;
        }
        
        private MailDTO fromDocument(@NotNull DocumentSnapshot doc) {
            if (!doc.exists()) return null;
            
            try {
                List<String> attachments = new ArrayList<>();
                Object attachmentsObj = doc.get("attachments");
                if (attachmentsObj instanceof List) {
                    attachments = (List<String>) attachmentsObj;
                }
                
                return new MailDTO(
                        doc.getString("mailId"),
                        doc.getString("senderUuid"),
                        doc.getString("senderName"),
                        doc.getString("receiverUuid"),
                        doc.getString("receiverName"),
                        doc.getString("subject"),
                        doc.getString("content"),
                        doc.getLong("sentAt"),
                        doc.getBoolean("isRead"),
                        doc.getLong("readAt"),
                        doc.getLong("expiresAt"),
                        attachments
                );
            } catch (Exception e) {
                LogUtil.warning("MailDTO 파싱 실패: " + e.getMessage());
                return null;
            }
        }
    }
}