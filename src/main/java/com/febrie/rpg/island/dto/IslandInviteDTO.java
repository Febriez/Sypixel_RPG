package com.febrie.rpg.island.dto;

import org.jetbrains.annotations.NotNull;

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
}