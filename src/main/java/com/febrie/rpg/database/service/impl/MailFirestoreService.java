package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.BaseFirestoreService;
import com.febrie.rpg.dto.social.MailDTO;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 메일 Firestore 서비스
 * Mail 컬렉션 관리
 *
 * @author Febrie, CoffeeTory
 */
public class MailFirestoreService extends BaseFirestoreService<MailDTO> {

    private static final String COLLECTION_NAME = "Mail";

    public MailFirestoreService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        super(plugin, firestore, COLLECTION_NAME, MailDTO.class);
    }

    @Override
    protected Map<String, Object> toMap(@NotNull MailDTO dto) {
        return dto.toMap();
    }

    @Override
    @Nullable
    protected MailDTO fromDocument(@NotNull DocumentSnapshot document) {
        if (!document.exists()) return null;
        try {
            Map<String, Object> data = document.getData();
            if (data != null) {
                return MailDTO.fromMap(data);
            }
            return null;
        } catch (Exception e) {
            LogUtil.warning("메일 데이터 파싱 실패 [" + document.getId() + "]: " + e.getMessage());
            return null;
        }
    }

    /**
     * 메일 전송
     */
    @NotNull
    public CompletableFuture<Void> sendMail(@NotNull MailDTO mail) {
        return save(mail.mailId(), mail);
    }

    /**
     * 받은 메일함 조회
     */
    @NotNull
    public CompletableFuture<List<MailDTO>> getReceivedMails(@NotNull UUID playerId) {
        return query("receiverUuid", playerId.toString());
    }

    /**
     * 보낸 메일함 조회
     */
    @NotNull
    public CompletableFuture<List<MailDTO>> getSentMails(@NotNull UUID playerId) {
        return query("senderUuid", playerId.toString());
    }

    /**
     * 메일 읽음 처리
     */
    @NotNull
    public CompletableFuture<Void> markMailAsRead(@NotNull String mailId) {
        return get(mailId).thenCompose(mail -> {
            if (mail != null && mail.isUnread()) {
                MailDTO readMail = mail.markAsRead();
                return save(mailId, readMail);
            }
            return CompletableFuture.completedFuture(null);
        });
    }

    /**
     * 메일 삭제
     */
    @NotNull
    public CompletableFuture<Void> deleteMail(@NotNull String mailId) {
        return delete(mailId);
    }

    /**
     * 읽지 않은 메일 개수
     */
    @NotNull
    public CompletableFuture<Integer> getUnreadMailCount(@NotNull UUID playerId) {
        return getReceivedMails(playerId).thenApply(mails -> (int) mails.stream().filter(MailDTO::isUnread).count());
    }

    /**
     * 특정 메일 조회
     */
    @NotNull
    public CompletableFuture<@Nullable MailDTO> getMail(@NotNull String mailId) {
        return get(mailId);
    }

    /**
     * 받은 메일함 조회 (페이징)
     */
    @NotNull
    public CompletableFuture<List<MailDTO>> getReceivedMailsWithLimit(@NotNull UUID playerId, int limit) {
        return queryWithLimit("receiverUuid", playerId.toString(), limit);
    }

    /**
     * 보낸 메일함 조회 (페이징)
     */
    @NotNull
    public CompletableFuture<List<MailDTO>> getSentMailsWithLimit(@NotNull UUID playerId, int limit) {
        return queryWithLimit("senderUuid", playerId.toString(), limit);
    }

    /**
     * 플레이어의 모든 메일 삭제 (받은 메일)
     */
    @NotNull
    public CompletableFuture<Void> deleteAllReceivedMails(@NotNull UUID playerId) {
        return getReceivedMails(playerId).thenCompose(mails -> {
            CompletableFuture<?>[] deleteFutures = mails.stream().map(mail -> delete(mail.mailId())).toArray(CompletableFuture[]::new);
            return CompletableFuture.allOf(deleteFutures);
        });
    }

    /**
     * 오래된 메일 정리
     *
     * @param daysOld 며칠 이상 된 메일 삭제
     */
    @NotNull
    public CompletableFuture<Void> deleteOldMails(int daysOld) {
        long cutoffTime = System.currentTimeMillis() - (daysOld * 24L * 60L * 60L * 1000L);
        return queryOrdered("sentTime", com.google.cloud.firestore.Query.Direction.ASCENDING, 1000).thenCompose(mails -> {
            CompletableFuture<?>[] deleteFutures = mails.stream().filter(mail -> mail.sentAt() < cutoffTime).map(mail -> delete(mail.mailId())).toArray(CompletableFuture[]::new);
            return CompletableFuture.allOf(deleteFutures);
        });
    }
}