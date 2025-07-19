package com.febrie.rpg.dto.social;

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
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        JsonObject player1UuidValue = new JsonObject();
        player1UuidValue.addProperty("stringValue", player1Uuid.toString());
        fields.add("player1Uuid", player1UuidValue);
        
        JsonObject player1NameValue = new JsonObject();
        player1NameValue.addProperty("stringValue", player1Name);
        fields.add("player1Name", player1NameValue);
        
        JsonObject player2UuidValue = new JsonObject();
        player2UuidValue.addProperty("stringValue", player2Uuid.toString());
        fields.add("player2Uuid", player2UuidValue);
        
        JsonObject player2NameValue = new JsonObject();
        player2NameValue.addProperty("stringValue", player2Name);
        fields.add("player2Name", player2NameValue);
        
        JsonObject createdAtValue = new JsonObject();
        createdAtValue.addProperty("integerValue", createdAt);
        fields.add("createdAt", createdAtValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 FriendshipDTO 생성
     */
    @NotNull
    public static FriendshipDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            throw new IllegalArgumentException("Invalid FriendshipDTO JSON: missing fields");
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        String player1UuidStr = fields.has("player1Uuid") && fields.getAsJsonObject("player1Uuid").has("stringValue")
                ? fields.getAsJsonObject("player1Uuid").get("stringValue").getAsString()
                : UUID.randomUUID().toString();
        UUID player1Uuid = UUID.fromString(player1UuidStr);
        
        String player1Name = fields.has("player1Name") && fields.getAsJsonObject("player1Name").has("stringValue")
                ? fields.getAsJsonObject("player1Name").get("stringValue").getAsString()
                : "";
                
        String player2UuidStr = fields.has("player2Uuid") && fields.getAsJsonObject("player2Uuid").has("stringValue")
                ? fields.getAsJsonObject("player2Uuid").get("stringValue").getAsString()
                : UUID.randomUUID().toString();
        UUID player2Uuid = UUID.fromString(player2UuidStr);
        
        String player2Name = fields.has("player2Name") && fields.getAsJsonObject("player2Name").has("stringValue")
                ? fields.getAsJsonObject("player2Name").get("stringValue").getAsString()
                : "";
                
        long createdAt = fields.has("createdAt") && fields.getAsJsonObject("createdAt").has("integerValue")
                ? fields.getAsJsonObject("createdAt").get("integerValue").getAsLong()
                : System.currentTimeMillis();
        
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