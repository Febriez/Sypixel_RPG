package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.FirestoreUtils;
import com.febrie.rpg.validation.Validator;
import com.febrie.rpg.database.constants.DatabaseConstants;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import net.kyori.adventure.text.Component;
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
                now + DatabaseConstants.ISLAND_INVITE_EXPIRES_MS, // 60초 후 만료
                message
        );
    }
    
    /**
     * 새 초대 생성 (기본 메시지)
     */
    public static IslandInviteDTO createNew(String inviteId, String targetUuid, String targetName,
                                            String inviterUuid, String inviterName) {
        return createNew(inviteId, "", targetUuid, targetName, inviterUuid, inviterName, "island.invite.default-message");
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
     * Map으로 변환 (Firestore SDK용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("inviteId", inviteId);
        map.put("islandId", islandId);
        map.put("invitedUuid", invitedUuid);
        map.put("invitedName", invitedName);
        map.put("inviterUuid", inviterUuid);
        map.put("inviterName", inviterName);
        map.put("invitedAt", invitedAt);
        map.put("message", message);
        map.put("expiresAt", expiresAt);
        return map;
    }
    
    /**
     * Map에서 생성 (Firestore SDK용)
     */
    @NotNull
    public static IslandInviteDTO fromMap(@NotNull Map<String, Object> map) {
        try {
            String inviteId = FirestoreUtils.getString(map, "inviteId", null);
            String islandId = FirestoreUtils.getString(map, "islandId");
            String invitedUuid = FirestoreUtils.getString(map, "invitedUuid", null);
            String invitedName = FirestoreUtils.getString(map, "invitedName");
            String inviterUuid = FirestoreUtils.getString(map, "inviterUuid", null);
            String inviterName = FirestoreUtils.getString(map, "inviterName");
            long invitedAt = FirestoreUtils.getLong(map, "invitedAt", System.currentTimeMillis());
            long expiresAt = FirestoreUtils.getLong(map, "expiresAt", System.currentTimeMillis() + DatabaseConstants.ISLAND_INVITE_EXPIRES_MS);
            String message = FirestoreUtils.getString(map, "message", "island.invite.default-message");
            
            // 유효성 검증 - Validator 사용
            Validator.validateIslandInviteData(inviteId, invitedUuid, inviterUuid, invitedAt, expiresAt);
            
            return new IslandInviteDTO(inviteId, islandId, inviterUuid, inviterName, invitedUuid, invitedName, invitedAt, expiresAt, message);
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new IllegalArgumentException(
                String.format("Failed to parse IslandInviteDTO from Map: %s", e.getMessage())
            );
        }
    }
    
}