package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.BaseFirestoreService;
import com.febrie.rpg.dto.social.FriendshipDTO;
import com.febrie.rpg.dto.social.MailDTO;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 소셜 기능 Firestore 서비스
 * friendships, mail 컬렉션 관리
 *
 * @author Febrie, CoffeeTory
 */
public class SocialFirestoreService extends BaseFirestoreService<FriendshipDTO> {
    
    private static final String FRIENDSHIP_COLLECTION = "friendships";
    private static final String MAIL_COLLECTION = "mail";
    
    // Mail 서비스를 위한 별도 BaseFirestoreService
    private final BaseFirestoreService<MailDTO> mailService;
    
    public SocialFirestoreService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        super(plugin, firestore, FRIENDSHIP_COLLECTION, FriendshipDTO.class);
        
        // Mail 서비스 초기화
        this.mailService = new MailBaseFirestoreService(plugin, firestore);
    }
    
    @Override
    protected Map<String, Object> toMap(@NotNull FriendshipDTO dto) {
        return convertJsonToMap(dto.toJsonObject());
    }
    
    @Override
    @Nullable
    protected FriendshipDTO fromDocument(@NotNull DocumentSnapshot document) {
        if (!document.exists()) return null;
        try {
            JsonObject json = convertMapToJson(document.getData());
            return FriendshipDTO.fromJsonObject(json);
        } catch (Exception e) {
            LogUtil.warning("친구관계 데이터 파싱 실패 [" + document.getId() + "]: " + e.getMessage());
            return null;
        }
    }
    
    // ===== 친구 관계 메서드들 =====
    
    /**
     * 플레이어의 친구 목록 조회
     */
    @NotNull
    public CompletableFuture<List<FriendshipDTO>> getFriendships(@NotNull UUID playerId) {
        Map<String, Object> filters = new HashMap<>();
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
    
    private String generateFriendshipId(UUID player1, UUID player2) {
        // 항상 일관된 ID를 생성하기 위해 UUID를 정렬
        if (player1.toString().compareTo(player2.toString()) < 0) {
            return player1.toString() + "_" + player2.toString();
        } else {
            return player2.toString() + "_" + player1.toString();
        }
    }
    
    // ===== 메일 관련 메서드들 =====
    
    /**
     * 메일 전송
     */
    @NotNull
    public CompletableFuture<Void> sendMail(@NotNull MailDTO mail) {
        return mailService.save(mail.mailId(), mail);
    }
    
    /**
     * 받은 메일함 조회
     */
    @NotNull
    public CompletableFuture<List<MailDTO>> getReceivedMails(@NotNull UUID playerId) {
        return mailService.query("receiverUuid", playerId.toString());
    }
    
    /**
     * 보낸 메일함 조회
     */
    @NotNull
    public CompletableFuture<List<MailDTO>> getSentMails(@NotNull UUID playerId) {
        return mailService.query("senderUuid", playerId.toString());
    }
    
    /**
     * 메일 읽음 처리
     */
    @NotNull
    public CompletableFuture<Void> markMailAsRead(@NotNull String mailId) {
        return mailService.get(mailId).thenCompose(mail -> {
            if (mail != null && mail.isUnread()) {
                MailDTO readMail = mail.markAsRead();
                return mailService.save(mailId, readMail);
            }
            return CompletableFuture.completedFuture(null);
        });
    }
    
    /**
     * 메일 삭제
     */
    @NotNull
    public CompletableFuture<Void> deleteMail(@NotNull String mailId) {
        return mailService.delete(mailId);
    }
    
    /**
     * 읽지 않은 메일 개수
     */
    @NotNull
    public CompletableFuture<Integer> getUnreadMailCount(@NotNull UUID playerId) {
        return getReceivedMails(playerId).thenApply(mails -> 
                (int) mails.stream().filter(MailDTO::isUnread).count());
    }
    
    /**
     * Mail 서비스를 위한 내부 클래스
     */
    private static class MailBaseFirestoreService extends BaseFirestoreService<MailDTO> {
        public MailBaseFirestoreService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
            super(plugin, firestore, MAIL_COLLECTION, MailDTO.class);
        }
        
        @Override
        protected Map<String, Object> toMap(@NotNull MailDTO dto) {
            return convertJsonToMap(dto.toJsonObject());
        }
        
        @Override
        @Nullable
        protected MailDTO fromDocument(@NotNull DocumentSnapshot document) {
            if (!document.exists()) return null;
            try {
                JsonObject json = convertMapToJson(document.getData());
                return MailDTO.fromJsonObject(json);
            } catch (Exception e) {
                LogUtil.warning("메일 데이터 파싱 실패 [" + document.getId() + "]: " + e.getMessage());
                return null;
            }
        }
    }
}