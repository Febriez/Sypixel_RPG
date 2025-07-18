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
        @NotNull String targetUuid,
        @NotNull String targetName,
        @NotNull String inviterUuid,
        @NotNull String inviterName,
        long createdAt,
        long expiresAt // 만료 시간 (1분)
) {
    /**
     * 새 초대 생성
     */
    public static IslandInviteDTO createNew(String inviteId, String targetUuid, String targetName,
                                            String inviterUuid, String inviterName) {
        long now = System.currentTimeMillis();
        return new IslandInviteDTO(
                inviteId,
                targetUuid,
                targetName,
                inviterUuid,
                inviterName,
                now,
                now + (60 * 1000) // 1분 후 만료
        );
    }
    
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
        
        JsonObject targetUuidValue = new JsonObject();
        targetUuidValue.addProperty("stringValue", targetUuid);
        fields.add("targetUuid", targetUuidValue);
        
        JsonObject targetNameValue = new JsonObject();
        targetNameValue.addProperty("stringValue", targetName);
        fields.add("targetName", targetNameValue);
        
        JsonObject inviterUuidValue = new JsonObject();
        inviterUuidValue.addProperty("stringValue", inviterUuid);
        fields.add("inviterUuid", inviterUuidValue);
        
        JsonObject inviterNameValue = new JsonObject();
        inviterNameValue.addProperty("stringValue", inviterName);
        fields.add("inviterName", inviterNameValue);
        
        JsonObject createdAtValue = new JsonObject();
        createdAtValue.addProperty("integerValue", createdAt);
        fields.add("createdAt", createdAtValue);
        
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
                
        String targetUuid = fields.has("targetUuid") && fields.getAsJsonObject("targetUuid").has("stringValue")
                ? fields.getAsJsonObject("targetUuid").get("stringValue").getAsString()
                : "";
                
        String targetName = fields.has("targetName") && fields.getAsJsonObject("targetName").has("stringValue")
                ? fields.getAsJsonObject("targetName").get("stringValue").getAsString()
                : "";
                
        String inviterUuid = fields.has("inviterUuid") && fields.getAsJsonObject("inviterUuid").has("stringValue")
                ? fields.getAsJsonObject("inviterUuid").get("stringValue").getAsString()
                : "";
                
        String inviterName = fields.has("inviterName") && fields.getAsJsonObject("inviterName").has("stringValue")
                ? fields.getAsJsonObject("inviterName").get("stringValue").getAsString()
                : "";
                
        long createdAt = fields.has("createdAt") && fields.getAsJsonObject("createdAt").has("integerValue")
                ? fields.getAsJsonObject("createdAt").get("integerValue").getAsLong()
                : System.currentTimeMillis();
                
        long expiresAt = fields.has("expiresAt") && fields.getAsJsonObject("expiresAt").has("integerValue")
                ? fields.getAsJsonObject("expiresAt").get("integerValue").getAsLong()
                : System.currentTimeMillis() + (60 * 1000);
        
        return new IslandInviteDTO(inviteId, targetUuid, targetName, inviterUuid, inviterName, createdAt, expiresAt);
    }
    
    /**
     * Map으로 변환 (Firebase 저장용)
     */
    @Deprecated
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("inviteId", inviteId);
        map.put("targetUuid", targetUuid);
        map.put("targetName", targetName);
        map.put("inviterUuid", inviterUuid);
        map.put("inviterName", inviterName);
        map.put("createdAt", createdAt);
        map.put("expiresAt", expiresAt);
        return map;
    }
    
    /**
     * Map에서 생성
     */
    @Deprecated
    public static IslandInviteDTO fromMap(Map<String, Object> map) {
        if (map == null) return null;
        
        return new IslandInviteDTO(
                (String) map.get("inviteId"),
                (String) map.get("targetUuid"),
                (String) map.get("targetName"),
                (String) map.get("inviterUuid"),
                (String) map.get("inviterName"),
                ((Number) map.get("createdAt")).longValue(),
                ((Number) map.get("expiresAt")).longValue()
        );
    }
}