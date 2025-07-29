package com.febrie.rpg.dto.social;

import com.febrie.rpg.util.JsonUtil;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * JsonObject로 변환
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject fields = new JsonObject();
        
        fields.add("mailId", JsonUtil.createStringValue(mailId));
        fields.add("senderUuid", JsonUtil.createStringValue(senderUuid.toString()));
        fields.add("senderName", JsonUtil.createStringValue(senderName));
        fields.add("receiverUuid", JsonUtil.createStringValue(receiverUuid.toString()));
        fields.add("receiverName", JsonUtil.createStringValue(receiverName));
        fields.add("subject", JsonUtil.createStringValue(subject));
        fields.add("content", JsonUtil.createStringValue(content));
        fields.add("sentAt", JsonUtil.createIntegerValue(sentAt));
        
        if (readAt != null) {
            fields.add("readAt", JsonUtil.createIntegerValue(readAt));
        }
        
        return JsonUtil.wrapInDocument(fields);
    }
    
    /**
     * JsonObject에서 MailDTO 생성
     */
    @NotNull
    public static MailDTO fromJsonObject(@NotNull JsonObject json) {
        JsonObject fields = JsonUtil.unwrapDocument(json);
        
        String mailId = JsonUtil.getStringValue(fields, "mailId", UUID.randomUUID().toString());
        
        String senderUuidStr = JsonUtil.getStringValue(fields, "senderUuid", UUID.randomUUID().toString());
        UUID senderUuid = UUID.fromString(senderUuidStr);
        
        String senderName = JsonUtil.getStringValue(fields, "senderName", "");
        
        String receiverUuidStr = JsonUtil.getStringValue(fields, "receiverUuid", UUID.randomUUID().toString());
        UUID receiverUuid = UUID.fromString(receiverUuidStr);
        
        String receiverName = JsonUtil.getStringValue(fields, "receiverName", "");
        
        String subject = JsonUtil.getStringValue(fields, "subject", "");
        
        String content = JsonUtil.getStringValue(fields, "content", "");
        
        long sentAt = JsonUtil.getLongValue(fields, "sentAt", System.currentTimeMillis());
        
        Long readAt = null;
        if (fields.has("readAt") && fields.getAsJsonObject("readAt").has("integerValue")) {
            readAt = JsonUtil.getLongValue(fields, "readAt");
        }
        
        return new MailDTO(mailId, senderUuid, senderName, receiverUuid, receiverName, subject, content, sentAt, readAt);
    }
    
    /**
     * 읽음 처리된 새 MailDTO 반환
     */
    @NotNull
    public MailDTO markAsRead() {
        return new MailDTO(mailId, senderUuid, senderName, receiverUuid, receiverName, 
                          subject, content, sentAt, System.currentTimeMillis());
    }
    
    /**
     * 읽지 않은 메일인지 확인
     */
    public boolean isUnread() {
        return readAt == null;
    }
}