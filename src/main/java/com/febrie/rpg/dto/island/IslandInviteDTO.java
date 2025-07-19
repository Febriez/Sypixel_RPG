package com.febrie.rpg.dto.island;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 섬 초대 정보 DTO (Record)
 *
 * @author Febrie, CoffeeTory
 */
public record IslandInviteDTO(
        @NotNull String inviteId,
        @NotNull String islandId,
        @NotNull String inviterUuid,
        @NotNull String inviterName,
        @NotNull String invitedUuid,
        @NotNull String invitedName,
        long invitedAt,
        long expiresAt,
        @NotNull String message
) {
    /**
     * 새 초대 생성
     */
    public static IslandInviteDTO createNew(String inviteId, String islandId, String targetUuid, String targetName,
                                            String inviterUuid, String inviterName, String message) {
        long now = System.currentTimeMillis();
        return new IslandInviteDTO(
                inviteId,
                islandId,
                inviterUuid,
                inviterName,
                targetUuid,
                targetName,
                now,
                now + (60 * 1000), // 1분 후 만료
                message
        );
    }
    
    /**
     * 새 초대 생성 (기본 메시지)
     */
    public static IslandInviteDTO createNew(String inviteId, String targetUuid, String targetName,
                                            String inviterUuid, String inviterName) {
        return createNew(inviteId, "", targetUuid, targetName, inviterUuid, inviterName, "섬에 초대되었습니다!");
    }
    
    // ===== 호환성을 위한 Alias 메서드들 =====
    
    public String targetUuid() { return invitedUuid; }
    public String targetName() { return invitedName; }
    public long createdAt() { return invitedAt; }
    
    /**
     * 초대 만료 여부 확인
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
    
    /**
     * 남은 시간 (초)
     */
    public int getRemainingSeconds() {
        long remaining = expiresAt - System.currentTimeMillis();
        return remaining > 0 ? (int) (remaining / 1000) : 0;
    }
    
    /**
     * JsonObject로 변환 (Firebase 저장용)
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        JsonObject inviteIdValue = new JsonObject();
        inviteIdValue.addProperty("stringValue", inviteId);
        fields.add("inviteId", inviteIdValue);
        
        JsonObject islandIdValue = new JsonObject();
        islandIdValue.addProperty("stringValue", islandId);
        fields.add("islandId", islandIdValue);
        
        JsonObject invitedUuidValue = new JsonObject();
        invitedUuidValue.addProperty("stringValue", invitedUuid);
        fields.add("invitedUuid", invitedUuidValue);
        
        JsonObject invitedNameValue = new JsonObject();
        invitedNameValue.addProperty("stringValue", invitedName);
        fields.add("invitedName", invitedNameValue);
        
        JsonObject inviterUuidValue = new JsonObject();
        inviterUuidValue.addProperty("stringValue", inviterUuid);
        fields.add("inviterUuid", inviterUuidValue);
        
        JsonObject inviterNameValue = new JsonObject();
        inviterNameValue.addProperty("stringValue", inviterName);
        fields.add("inviterName", inviterNameValue);
        
        JsonObject invitedAtValue = new JsonObject();
        invitedAtValue.addProperty("integerValue", invitedAt);
        fields.add("invitedAt", invitedAtValue);
        
        JsonObject messageValue = new JsonObject();
        messageValue.addProperty("stringValue", message);
        fields.add("message", messageValue);
        
        JsonObject expiresAtValue = new JsonObject();
        expiresAtValue.addProperty("integerValue", expiresAt);
        fields.add("expiresAt", expiresAtValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static IslandInviteDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            throw new IllegalArgumentException("Invalid IslandInviteDTO JSON: missing fields");
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        String inviteId = fields.has("inviteId") && fields.getAsJsonObject("inviteId").has("stringValue")
                ? fields.getAsJsonObject("inviteId").get("stringValue").getAsString()
                : "";
                
        String islandId = fields.has("islandId") && fields.getAsJsonObject("islandId").has("stringValue")
                ? fields.getAsJsonObject("islandId").get("stringValue").getAsString()
                : "";
                
        String invitedUuid = fields.has("invitedUuid") && fields.getAsJsonObject("invitedUuid").has("stringValue")
                ? fields.getAsJsonObject("invitedUuid").get("stringValue").getAsString()
                : "";
                
        String invitedName = fields.has("invitedName") && fields.getAsJsonObject("invitedName").has("stringValue")
                ? fields.getAsJsonObject("invitedName").get("stringValue").getAsString()
                : "";
                
        String inviterUuid = fields.has("inviterUuid") && fields.getAsJsonObject("inviterUuid").has("stringValue")
                ? fields.getAsJsonObject("inviterUuid").get("stringValue").getAsString()
                : "";
                
        String inviterName = fields.has("inviterName") && fields.getAsJsonObject("inviterName").has("stringValue")
                ? fields.getAsJsonObject("inviterName").get("stringValue").getAsString()
                : "";
                
        long invitedAt = fields.has("invitedAt") && fields.getAsJsonObject("invitedAt").has("integerValue")
                ? fields.getAsJsonObject("invitedAt").get("integerValue").getAsLong()
                : System.currentTimeMillis();
                
        long expiresAt = fields.has("expiresAt") && fields.getAsJsonObject("expiresAt").has("integerValue")
                ? fields.getAsJsonObject("expiresAt").get("integerValue").getAsLong()
                : System.currentTimeMillis() + (60 * 1000);
                
        String message = fields.has("message") && fields.getAsJsonObject("message").has("stringValue")
                ? fields.getAsJsonObject("message").get("stringValue").getAsString()
                : "섬에 초대되었습니다!";
        
        return new IslandInviteDTO(inviteId, islandId, inviterUuid, inviterName, invitedUuid, invitedName, invitedAt, expiresAt, message);
    }
    
}