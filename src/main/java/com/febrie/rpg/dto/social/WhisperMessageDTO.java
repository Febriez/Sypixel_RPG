package com.febrie.rpg.dto.social;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 귓말 메시지 데이터 전송 객체
 *
 * @author Febrie
 */
public class WhisperMessageDTO {
    
    private String id; // Firestore 문서 ID
    private UUID fromPlayerId;
    private String fromPlayerName;
    private UUID toPlayerId;
    private String toPlayerName;
    private String message;
    private LocalDateTime sentTime;
    private boolean isRead;
    
    public WhisperMessageDTO() {
        // Firebase 역직렬화를 위한 기본 생성자
    }
    
    public WhisperMessageDTO(@NotNull UUID fromPlayerId, @NotNull String fromPlayerName,
                            @NotNull UUID toPlayerId, @NotNull String toPlayerName,
                            @NotNull String message) {
        this.fromPlayerId = fromPlayerId;
        this.fromPlayerName = fromPlayerName;
        this.toPlayerId = toPlayerId;
        this.toPlayerName = toPlayerName;
        this.message = message;
        this.sentTime = LocalDateTime.now();
        this.isRead = false;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public UUID getFromPlayerId() {
        return fromPlayerId;
    }
    
    public void setFromPlayerId(UUID fromPlayerId) {
        this.fromPlayerId = fromPlayerId;
    }
    
    public String getFromPlayerName() {
        return fromPlayerName;
    }
    
    public void setFromPlayerName(String fromPlayerName) {
        this.fromPlayerName = fromPlayerName;
    }
    
    public UUID getToPlayerId() {
        return toPlayerId;
    }
    
    public void setToPlayerId(UUID toPlayerId) {
        this.toPlayerId = toPlayerId;
    }
    
    public String getToPlayerName() {
        return toPlayerName;
    }
    
    public void setToPlayerName(String toPlayerName) {
        this.toPlayerName = toPlayerName;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getSentTime() {
        return sentTime;
    }
    
    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
    
    @Override
    public String toString() {
        return String.format("Whisper{from=%s, to=%s, message='%s', time=%s}", 
                fromPlayerName, toPlayerName, message, sentTime);
    }
}