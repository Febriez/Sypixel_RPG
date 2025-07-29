package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.JsonUtil;
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
        
        fields.add("inviteId", JsonUtil.createStringValue(inviteId));
        fields.add("islandId", JsonUtil.createStringValue(islandId));
        fields.add("invitedUuid", JsonUtil.createStringValue(invitedUuid));
        fields.add("invitedName", JsonUtil.createStringValue(invitedName));
        fields.add("inviterUuid", JsonUtil.createStringValue(inviterUuid));
        fields.add("inviterName", JsonUtil.createStringValue(inviterName));
        fields.add("invitedAt", JsonUtil.createIntegerValue(invitedAt));
        fields.add("message", JsonUtil.createStringValue(message));
        fields.add("expiresAt", JsonUtil.createIntegerValue(expiresAt));
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static IslandInviteDTO fromJsonObject(@NotNull JsonObject json) {
        JsonUtil.validateDTOJson(json, "IslandInviteDTO");
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        try {
            // 필수 필드 검증
            JsonUtil.validateRequiredField(fields, "inviteId", "IslandInviteDTO");
            JsonUtil.validateRequiredField(fields, "inviterUuid", "IslandInviteDTO");
            JsonUtil.validateRequiredField(fields, "inviterName", "IslandInviteDTO");
            JsonUtil.validateRequiredField(fields, "invitedUuid", "IslandInviteDTO");
            JsonUtil.validateRequiredField(fields, "invitedName", "IslandInviteDTO");
            
            String inviteId = JsonUtil.getStringValue(fields, "inviteId");
            String islandId = JsonUtil.getStringValue(fields, "islandId", "");
            String invitedUuid = JsonUtil.getStringValue(fields, "invitedUuid");
            String invitedName = JsonUtil.getStringValue(fields, "invitedName");
            String inviterUuid = JsonUtil.getStringValue(fields, "inviterUuid");
            String inviterName = JsonUtil.getStringValue(fields, "inviterName");
            long invitedAt = JsonUtil.getLongValue(fields, "invitedAt", System.currentTimeMillis());
            long expiresAt = JsonUtil.getLongValue(fields, "expiresAt", System.currentTimeMillis() + (60 * 1000));
            String message = JsonUtil.getStringValue(fields, "message", "섬에 초대되었습니다!");
            
            // 유효성 검증
            if (inviteId.isEmpty()) {
                throw new IllegalArgumentException(
                    "Invalid IslandInviteDTO: inviteId cannot be empty"
                );
            }
            
            if (invitedUuid.isEmpty() || inviterUuid.isEmpty()) {
                throw new IllegalArgumentException(
                    String.format("Invalid IslandInviteDTO: UUID fields cannot be empty. invitedUuid='%s', inviterUuid='%s'",
                        invitedUuid, inviterUuid)
                );
            }
            
            if (invitedAt > expiresAt) {
                throw new IllegalArgumentException(
                    String.format("Invalid IslandInviteDTO: invitedAt (%d) cannot be after expiresAt (%d)",
                        invitedAt, expiresAt)
                );
            }
            
            return new IslandInviteDTO(inviteId, islandId, inviterUuid, inviterName, invitedUuid, invitedName, invitedAt, expiresAt, message);
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new IllegalArgumentException(
                String.format("Failed to parse IslandInviteDTO: %s. JSON structure: %s", 
                    e.getMessage(), 
                    json.toString().length() > 200 ? json.toString().substring(0, 200) + "..." : json.toString())
            );
        }
    }
    
}