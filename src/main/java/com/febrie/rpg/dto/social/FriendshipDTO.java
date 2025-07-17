package com.febrie.rpg.dto.social;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 친구 관계 데이터 전송 객체
 *
 * @author Febrie
 */
public class FriendshipDTO {
    
    private String id; // Firestore 문서 ID
    private UUID player1Id;
    private String player1Name;
    private UUID player2Id;
    private String player2Name;
    private LocalDateTime friendsSince;
    private boolean isBlocked; // 차단 여부
    private UUID blockedBy; // 누가 차단했는지 (null이면 차단 안됨)
    
    public FriendshipDTO() {
        // Firebase 역직렬화를 위한 기본 생성자
    }
    
    public FriendshipDTO(@NotNull UUID player1Id, @NotNull String player1Name,
                        @NotNull UUID player2Id, @NotNull String player2Name) {
        this.player1Id = player1Id;
        this.player1Name = player1Name;
        this.player2Id = player2Id;
        this.player2Name = player2Name;
        this.friendsSince = LocalDateTime.now();
        this.isBlocked = false;
    }
    
    /**
     * 특정 플레이어가 이 친구 관계에 포함되는지 확인
     */
    public boolean containsPlayer(@NotNull UUID playerId) {
        return player1Id.equals(playerId) || player2Id.equals(playerId);
    }
    
    /**
     * 특정 플레이어의 친구 정보 가져오기
     */
    @NotNull
    public FriendInfo getFriendInfo(@NotNull UUID playerId) {
        if (player1Id.equals(playerId)) {
            return new FriendInfo(player2Id, player2Name);
        } else if (player2Id.equals(playerId)) {
            return new FriendInfo(player1Id, player1Name);
        } else {
            throw new IllegalArgumentException("Player not found in this friendship");
        }
    }
    
    /**
     * 친구 정보 내부 클래스
     */
    public static class FriendInfo {
        private final UUID playerId;
        private final String playerName;
        
        public FriendInfo(@NotNull UUID playerId, @NotNull String playerName) {
            this.playerId = playerId;
            this.playerName = playerName;
        }
        
        public UUID getPlayerId() {
            return playerId;
        }
        
        public String getPlayerName() {
            return playerName;
        }
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public UUID getPlayer1Id() {
        return player1Id;
    }
    
    public void setPlayer1Id(UUID player1Id) {
        this.player1Id = player1Id;
    }
    
    public String getPlayer1Name() {
        return player1Name;
    }
    
    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }
    
    public UUID getPlayer2Id() {
        return player2Id;
    }
    
    public void setPlayer2Id(UUID player2Id) {
        this.player2Id = player2Id;
    }
    
    public String getPlayer2Name() {
        return player2Name;
    }
    
    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }
    
    public LocalDateTime getFriendsSince() {
        return friendsSince;
    }
    
    public void setFriendsSince(LocalDateTime friendsSince) {
        this.friendsSince = friendsSince;
    }
    
    public boolean isBlocked() {
        return isBlocked;
    }
    
    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
    
    public UUID getBlockedBy() {
        return blockedBy;
    }
    
    public void setBlockedBy(UUID blockedBy) {
        this.blockedBy = blockedBy;
    }
    
    @Override
    public String toString() {
        return String.format("Friendship{%s ↔ %s, since=%s, blocked=%s}", 
                player1Name, player2Name, friendsSince, isBlocked);
    }
}