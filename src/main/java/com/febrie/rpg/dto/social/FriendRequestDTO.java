package com.febrie.rpg.dto.social;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 친구 요청 데이터 전송 객체
 *
 * @author Febrie
 */
public class FriendRequestDTO {
    
    private String id; // Firestore 문서 ID
    private UUID fromPlayerId;
    private String fromPlayerName;
    private UUID toPlayerId;
    private String toPlayerName;
    private LocalDateTime requestTime;
    private String status; // PENDING, ACCEPTED, REJECTED, EXPIRED
    private String message; // 친구 요청 메시지 (선택사항)
    
    public FriendRequestDTO() {
        // Firebase 역직렬화를 위한 기본 생성자
    }
    
    public FriendRequestDTO(@NotNull UUID fromPlayerId, @NotNull String fromPlayerName,
                           @NotNull UUID toPlayerId, @NotNull String toPlayerName) {
        this.fromPlayerId = fromPlayerId;
        this.fromPlayerName = fromPlayerName;
        this.toPlayerId = toPlayerId;
        this.toPlayerName = toPlayerName;
        this.requestTime = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    public FriendRequestDTO(@NotNull UUID fromPlayerId, @NotNull String fromPlayerName,
                           @NotNull UUID toPlayerId, @NotNull String toPlayerName,
                           @NotNull String message) {
        this(fromPlayerId, fromPlayerName, toPlayerId, toPlayerName);
        this.message = message;
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
    
    public LocalDateTime getRequestTime() {
        return requestTime;
    }
    
    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return String.format("FriendRequest{from=%s, to=%s, status=%s, time=%s}", 
                fromPlayerName, toPlayerName, status, requestTime);
    }
}