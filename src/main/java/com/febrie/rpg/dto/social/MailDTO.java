package com.febrie.rpg.dto.social;

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
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        JsonObject mailIdValue = new JsonObject();
        mailIdValue.addProperty("stringValue", mailId);
        fields.add("mailId", mailIdValue);
        
        JsonObject senderUuidValue = new JsonObject();
        senderUuidValue.addProperty("stringValue", senderUuid.toString());
        fields.add("senderUuid", senderUuidValue);
        
        JsonObject senderNameValue = new JsonObject();
        senderNameValue.addProperty("stringValue", senderName);
        fields.add("senderName", senderNameValue);
        
        JsonObject receiverUuidValue = new JsonObject();
        receiverUuidValue.addProperty("stringValue", receiverUuid.toString());
        fields.add("receiverUuid", receiverUuidValue);
        
        JsonObject receiverNameValue = new JsonObject();
        receiverNameValue.addProperty("stringValue", receiverName);
        fields.add("receiverName", receiverNameValue);
        
        JsonObject subjectValue = new JsonObject();
        subjectValue.addProperty("stringValue", subject);
        fields.add("subject", subjectValue);
        
        JsonObject contentValue = new JsonObject();
        contentValue.addProperty("stringValue", content);
        fields.add("content", contentValue);
        
        JsonObject sentAtValue = new JsonObject();
        sentAtValue.addProperty("integerValue", sentAt);
        fields.add("sentAt", sentAtValue);
        
        if (readAt != null) {
            JsonObject readAtValue = new JsonObject();
            readAtValue.addProperty("integerValue", readAt);
            fields.add("readAt", readAtValue);
        }
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 MailDTO 생성
     */
    @NotNull
    public static MailDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            throw new IllegalArgumentException("Invalid MailDTO JSON: missing fields");
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        String mailId = fields.has("mailId") && fields.getAsJsonObject("mailId").has("stringValue")
                ? fields.getAsJsonObject("mailId").get("stringValue").getAsString()
                : UUID.randomUUID().toString();
                
        String senderUuidStr = fields.has("senderUuid") && fields.getAsJsonObject("senderUuid").has("stringValue")
                ? fields.getAsJsonObject("senderUuid").get("stringValue").getAsString()
                : UUID.randomUUID().toString();
        UUID senderUuid = UUID.fromString(senderUuidStr);
        
        String senderName = fields.has("senderName") && fields.getAsJsonObject("senderName").has("stringValue")
                ? fields.getAsJsonObject("senderName").get("stringValue").getAsString()
                : "";
                
        String receiverUuidStr = fields.has("receiverUuid") && fields.getAsJsonObject("receiverUuid").has("stringValue")
                ? fields.getAsJsonObject("receiverUuid").get("stringValue").getAsString()
                : UUID.randomUUID().toString();
        UUID receiverUuid = UUID.fromString(receiverUuidStr);
        
        String receiverName = fields.has("receiverName") && fields.getAsJsonObject("receiverName").has("stringValue")
                ? fields.getAsJsonObject("receiverName").get("stringValue").getAsString()
                : "";
                
        String subject = fields.has("subject") && fields.getAsJsonObject("subject").has("stringValue")
                ? fields.getAsJsonObject("subject").get("stringValue").getAsString()
                : "";
                
        String content = fields.has("content") && fields.getAsJsonObject("content").has("stringValue")
                ? fields.getAsJsonObject("content").get("stringValue").getAsString()
                : "";
                
        long sentAt = fields.has("sentAt") && fields.getAsJsonObject("sentAt").has("integerValue")
                ? fields.getAsJsonObject("sentAt").get("integerValue").getAsLong()
                : System.currentTimeMillis();
                
        Long readAt = null;
        if (fields.has("readAt") && fields.getAsJsonObject("readAt").has("integerValue")) {
            readAt = fields.getAsJsonObject("readAt").get("integerValue").getAsLong();
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