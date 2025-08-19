package com.febrie.rpg.dto.social;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.kyori.adventure.text.Component;
/**
 * 친구 요청 데이터 전송 객체
 *
 * @author Febrie
 */
public record FriendRequestDTO(
        @Nullable String id, // Firestore 문서 ID
        @NotNull UUID fromPlayerId,
        @NotNull String fromPlayerName,
        @NotNull UUID toPlayerId,
        @NotNull String toPlayerName,
        @NotNull LocalDateTime requestTime,
        @NotNull String status, // PENDING, ACCEPTED, REJECTED, EXPIRED
        @Nullable String message // 친구 요청 메시지 (선택사항)
) {
    /**
     * 새 친구 요청 생성용 생성자 (메시지 없음)
     */
    public FriendRequestDTO(@NotNull UUID fromPlayerId, @NotNull String fromPlayerName,
                           @NotNull UUID toPlayerId, @NotNull String toPlayerName) {
        this(null, fromPlayerId, fromPlayerName, toPlayerId, toPlayerName,
             LocalDateTime.now(), "PENDING", null);
    }
    
    /**
     * 새 친구 요청 생성용 생성자 (메시지 포함)
     */
    public FriendRequestDTO(@NotNull UUID fromPlayerId, @NotNull String fromPlayerName,
                           @NotNull UUID toPlayerId, @NotNull String toPlayerName,
                           @NotNull String message) {
        this(null, fromPlayerId, fromPlayerName, toPlayerId, toPlayerName,
             LocalDateTime.now(), "PENDING", message);
    }
    
    /**
     * Map으로 변환
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        
        if (id != null) {
            map.put("id", id);
        }
        map.put("fromPlayerId", fromPlayerId.toString());
        map.put("fromPlayerName", fromPlayerName);
        map.put("toPlayerId", toPlayerId.toString());
        map.put("toPlayerName", toPlayerName);
        map.put("requestTime", requestTime.toString());
        map.put("status", status);
        if (message != null) {
            map.put("message", message);
        }
        
        return map;
    }
    
    /**
     * Map에서 FriendRequestDTO 생성
     */
    @NotNull
    public static FriendRequestDTO fromMap(@NotNull Map<String, Object> map) {
        String id = (String) map.get("id");
        
        String fromPlayerIdStr = (String) map.getOrDefault("fromPlayerId", UUID.randomUUID().toString());
        UUID fromPlayerId = UUID.fromString(fromPlayerIdStr);
        
        String fromPlayerName = (String) map.getOrDefault("fromPlayerName", "");
        
        String toPlayerIdStr = (String) map.getOrDefault("toPlayerId", UUID.randomUUID().toString());
        UUID toPlayerId = UUID.fromString(toPlayerIdStr);
        
        String toPlayerName = (String) map.getOrDefault("toPlayerName", "");
        
        String requestTimeStr = (String) map.getOrDefault("requestTime", LocalDateTime.now().toString());
        LocalDateTime requestTime = LocalDateTime.parse(requestTimeStr);
        
        String status = (String) map.getOrDefault("status", "PENDING");
        
        String message = (String) map.get("message");
        
        return new FriendRequestDTO(id, fromPlayerId, fromPlayerName, toPlayerId, 
                                   toPlayerName, requestTime, status, message);
    }
    
    /**
     * ID 설정을 위한 새 인스턴스 생성
     */
    public FriendRequestDTO withId(@NotNull String newId) {
        return new FriendRequestDTO(newId, fromPlayerId, fromPlayerName, toPlayerId, 
                                   toPlayerName, requestTime, status, message);
    }
    
    /**
     * 상태 변경을 위한 새 인스턴스 생성
     */
    public FriendRequestDTO withStatus(@NotNull String newStatus) {
        return new FriendRequestDTO(id, fromPlayerId, fromPlayerName, toPlayerId, 
                                   toPlayerName, requestTime, newStatus, message);
    }
    
    /**
     * 친구 요청 수락
     */
    public FriendRequestDTO accept() {
        return withStatus("ACCEPTED");
    }
    
    /**
     * 친구 요청 거절
     */
    public FriendRequestDTO reject() {
        return withStatus("REJECTED");
    }
    
    /**
     * 친구 요청 만료
     */
    public FriendRequestDTO expire() {
        return withStatus("EXPIRED");
    }
    
    @Override
    public String toString() {
        return String.format("FriendRequest{from=%s, to=%s, status=%s, time=%s}", 
                fromPlayerName, toPlayerName, status, requestTime);
    }
}