package com.febrie.rpg.dto.social;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.kyori.adventure.text.Component;
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
        map.put("message", message);
        map.put("sentTime", sentTime.toString());
        map.put("isRead", isRead);
        
        return map;
    }
    
    /**
     * Map에서 WhisperMessageDTO 생성
     */
    @NotNull
    public static WhisperMessageDTO fromMap(@NotNull Map<String, Object> map) {
        String id = (String) map.get("id");
        
        String fromPlayerIdStr = (String) map.getOrDefault("fromPlayerId", UUID.randomUUID().toString());
        UUID fromPlayerId = UUID.fromString(fromPlayerIdStr);
        
        String fromPlayerName = (String) map.getOrDefault("fromPlayerName", "");
        
        String toPlayerIdStr = (String) map.getOrDefault("toPlayerId", UUID.randomUUID().toString());
        UUID toPlayerId = UUID.fromString(toPlayerIdStr);
        
        String toPlayerName = (String) map.getOrDefault("toPlayerName", "");
        
        String message = (String) map.getOrDefault("message", "");
        
        String sentTimeStr = (String) map.getOrDefault("sentTime", LocalDateTime.now().toString());
        LocalDateTime sentTime = LocalDateTime.parse(sentTimeStr);
        
        Object isReadObj = map.get("isRead");
        boolean isRead = isReadObj instanceof Boolean ? (Boolean) isReadObj : false;
        
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