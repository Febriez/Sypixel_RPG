package com.febrie.rpg.dto.social;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 우편 데이터 전송 객체 (Record)
 * Firebase 저장용 불변 데이터 구조
 * 
 * @author Febrie, CoffeeTory
 */
public record MailDTO(
        @NotNull String mailId,
        @NotNull UUID senderUuid,
        @NotNull String senderName,
        @NotNull UUID receiverUuid,
        @NotNull String receiverName,
        @NotNull String subject,
        @NotNull String content,
        long sentAt,
        @Nullable Long readAt
) {
    
    /**
     * 기본 생성자 - 신규 메일용
     */
    public MailDTO(@NotNull String mailId, @NotNull UUID senderUuid, @NotNull String senderName,
                   @NotNull UUID receiverUuid, @NotNull String receiverName,
                   @NotNull String subject, @NotNull String content) {
        this(mailId, senderUuid, senderName, receiverUuid, receiverName, subject, content, System.currentTimeMillis(), null);
    }
    
    /**
     * Map으로 변환
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("mailId", mailId);
        map.put("senderUuid", senderUuid.toString());
        map.put("senderName", senderName);
        map.put("receiverUuid", receiverUuid.toString());
        map.put("receiverName", receiverName);
        map.put("subject", subject);
        map.put("content", content);
        map.put("sentAt", sentAt);
        
        if (readAt != null) {
            map.put("readAt", readAt);
        }
        
        return map;
    }
    
    /**
     * Map에서 MailDTO 생성
     */
    @NotNull
    public static MailDTO fromMap(@NotNull Map<String, Object> map) {
        String mailId = FirestoreUtils.getString(map, "mailId", UUID.randomUUID().toString());
        // Ensure non-null mailId
        if (mailId == null) {
            mailId = UUID.randomUUID().toString();
        }
        
        String senderUuidStr = FirestoreUtils.getString(map, "senderUuid", UUID.randomUUID().toString());
        // Ensure non-null UUID strings
        if (senderUuidStr == null) {
            senderUuidStr = UUID.randomUUID().toString();
        }
        UUID senderUuid = UUID.fromString(senderUuidStr);
        
        String senderName = FirestoreUtils.getString(map, "senderName");
        String receiverUuidStr = FirestoreUtils.getString(map, "receiverUuid", UUID.randomUUID().toString());
        if (receiverUuidStr == null) {
            receiverUuidStr = UUID.randomUUID().toString();
        }
        UUID receiverUuid = UUID.fromString(receiverUuidStr);
        
        String receiverName = FirestoreUtils.getString(map, "receiverName");
        String subject = FirestoreUtils.getString(map, "subject");
        String content = FirestoreUtils.getString(map, "content");
        
        long sentAt = FirestoreUtils.getLong(map, "sentAt", System.currentTimeMillis());
        Long readAt = FirestoreUtils.getLongOrNull(map, "readAt");
        
        return new MailDTO(mailId, senderUuid, senderName, receiverUuid, receiverName, subject, content, sentAt, readAt);
    }
    
    /**
     * 읽지 않은 메일인지 확인
     */
    public boolean isUnread() {
        return readAt == null;
    }
    
    /**
     * 읽음 처리된 새 MailDTO 반환
     */
    @NotNull
    public MailDTO markAsRead() {
        return new MailDTO(mailId, senderUuid, senderName, receiverUuid, receiverName, 
                          subject, content, sentAt, System.currentTimeMillis());
    }
}