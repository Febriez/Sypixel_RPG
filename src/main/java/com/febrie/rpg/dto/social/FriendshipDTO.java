package com.febrie.rpg.dto.social;

import com.febrie.rpg.util.JsonUtil;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * 친구 관계 데이터 전송 객체 (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record FriendshipDTO(
        @NotNull UUID player1Uuid,
        @NotNull String player1Name,
        @NotNull UUID player2Uuid,
        @NotNull String player2Name,
        long createdAt
) {
    
    /**
     * 기본 생성자 - 신규 친구 관계용
     */
    public FriendshipDTO(@NotNull UUID player1Uuid, @NotNull String player1Name,
                        @NotNull UUID player2Uuid, @NotNull String player2Name) {
        this(player1Uuid, player1Name, player2Uuid, player2Name, System.currentTimeMillis());
    }
    
    /**
     * JsonObject로 변환
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject fields = new JsonObject();
        
        fields.add("player1Uuid", JsonUtil.createStringValue(player1Uuid.toString()));
        fields.add("player1Name", JsonUtil.createStringValue(player1Name));
        fields.add("player2Uuid", JsonUtil.createStringValue(player2Uuid.toString()));
        fields.add("player2Name", JsonUtil.createStringValue(player2Name));
        fields.add("createdAt", JsonUtil.createIntegerValue(createdAt));
        
        return JsonUtil.wrapInDocument(fields);
    }
    
    /**
     * JsonObject에서 FriendshipDTO 생성
     */
    @NotNull
    public static FriendshipDTO fromJsonObject(@NotNull JsonObject json) {
        JsonObject fields = JsonUtil.unwrapDocument(json);
        
        String player1UuidStr = JsonUtil.getStringValue(fields, "player1Uuid", UUID.randomUUID().toString());
        UUID player1Uuid = UUID.fromString(player1UuidStr);
        
        String player1Name = JsonUtil.getStringValue(fields, "player1Name", "");
        
        String player2UuidStr = JsonUtil.getStringValue(fields, "player2Uuid", UUID.randomUUID().toString());
        UUID player2Uuid = UUID.fromString(player2UuidStr);
        
        String player2Name = JsonUtil.getStringValue(fields, "player2Name", "");
        
        long createdAt = JsonUtil.getLongValue(fields, "createdAt", System.currentTimeMillis());
        
        return new FriendshipDTO(player1Uuid, player1Name, player2Uuid, player2Name, createdAt);
    }
    
    /**
     * 특정 플레이어가 이 친구 관계에 포함되는지 확인
     */
    public boolean containsPlayer(@NotNull UUID playerId) {
        return player1Uuid.equals(playerId) || player2Uuid.equals(playerId);
    }
    
    /**
     * 특정 플레이어의 친구 UUID 가져오기
     */
    @Nullable
    public UUID getFriendUuid(@NotNull UUID playerId) {
        if (player1Uuid.equals(playerId)) {
            return player2Uuid;
        } else if (player2Uuid.equals(playerId)) {
            return player1Uuid;
        }
        return null;
    }
    
    /**
     * 특정 플레이어의 친구 이름 가져오기
     */
    @Nullable
    public String getFriendName(@NotNull UUID playerId) {
        if (player1Uuid.equals(playerId)) {
            return player2Name;
        } else if (player2Uuid.equals(playerId)) {
            return player1Name;
        }
        return null;
    }
}