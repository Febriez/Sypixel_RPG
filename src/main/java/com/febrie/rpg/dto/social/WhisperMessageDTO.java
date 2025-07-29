package com.febrie.rpg.dto.social;

import com.febrie.rpg.util.JsonUtil;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 귓말 메시지 데이터 전송 객체
 *
 * @author Febrie
 */
public record WhisperMessageDTO(
        @Nullable String id, // Firestore 문서 ID
        @NotNull UUID fromPlayerId,
        @NotNull String fromPlayerName,
        @NotNull UUID toPlayerId,
        @NotNull String toPlayerName,
        @NotNull String message,
        @NotNull LocalDateTime sentTime,
        boolean isRead
) {
    /**
     * 새 메시지 생성용 생성자
     */
    public WhisperMessageDTO(@NotNull UUID fromPlayerId, @NotNull String fromPlayerName,
                            @NotNull UUID toPlayerId, @NotNull String toPlayerName,
                            @NotNull String message) {
        this(null, fromPlayerId, fromPlayerName, toPlayerId, toPlayerName, 
             message, LocalDateTime.now(), false);
    }
    
    /**
     * JsonObject로 변환
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject fields = new JsonObject();
        
        if (id != null) {
            fields.add("id", JsonUtil.createStringValue(id));
        }
        fields.add("fromPlayerId", JsonUtil.createStringValue(fromPlayerId.toString()));
        fields.add("fromPlayerName", JsonUtil.createStringValue(fromPlayerName));
        fields.add("toPlayerId", JsonUtil.createStringValue(toPlayerId.toString()));
        fields.add("toPlayerName", JsonUtil.createStringValue(toPlayerName));
        fields.add("message", JsonUtil.createStringValue(message));
        fields.add("sentTime", JsonUtil.createStringValue(sentTime.toString()));
        fields.add("isRead", JsonUtil.createBooleanValue(isRead));
        
        return JsonUtil.wrapInDocument(fields);
    }
    
    /**
     * JsonObject에서 WhisperMessageDTO 생성
     */
    @NotNull
    public static WhisperMessageDTO fromJsonObject(@NotNull JsonObject json) {
        JsonObject fields = JsonUtil.unwrapDocument(json);
        
        String id = JsonUtil.getStringValue(fields, "id", null);
        
        String fromPlayerIdStr = JsonUtil.getStringValue(fields, "fromPlayerId", UUID.randomUUID().toString());
        UUID fromPlayerId = UUID.fromString(fromPlayerIdStr);
        
        String fromPlayerName = JsonUtil.getStringValue(fields, "fromPlayerName", "");
        
        String toPlayerIdStr = JsonUtil.getStringValue(fields, "toPlayerId", UUID.randomUUID().toString());
        UUID toPlayerId = UUID.fromString(toPlayerIdStr);
        
        String toPlayerName = JsonUtil.getStringValue(fields, "toPlayerName", "");
        
        String message = JsonUtil.getStringValue(fields, "message", "");
        
        String sentTimeStr = JsonUtil.getStringValue(fields, "sentTime", LocalDateTime.now().toString());
        LocalDateTime sentTime = LocalDateTime.parse(sentTimeStr);
        
        boolean isRead = JsonUtil.getBooleanValue(fields, "isRead", false);
        
        return new WhisperMessageDTO(id, fromPlayerId, fromPlayerName, toPlayerId, 
                                    toPlayerName, message, sentTime, isRead);
    }
    
    /**
     * ID 설정을 위한 새 인스턴스 생성
     */
    public WhisperMessageDTO withId(@NotNull String newId) {
        return new WhisperMessageDTO(newId, fromPlayerId, fromPlayerName, toPlayerId, 
                                    toPlayerName, message, sentTime, isRead);
    }
    
    /**
     * 읽음 상태 변경을 위한 새 인스턴스 생성
     */
    public WhisperMessageDTO markAsRead() {
        return new WhisperMessageDTO(id, fromPlayerId, fromPlayerName, toPlayerId, 
                                    toPlayerName, message, sentTime, true);
    }
    
    @Override
    public String toString() {
        return String.format("Whisper{from=%s, to=%s, message='%s', time=%s}", 
                fromPlayerName, toPlayerName, message, sentTime);
    }
}