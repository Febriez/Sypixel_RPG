package com.febrie.rpg.island.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 섬원 정보 DTO (Record)
 *
 * @author Febrie, CoffeeTory
 */
public record IslandMemberDTO(
        @NotNull String uuid,
        @NotNull String name,
        boolean isCoOwner, // 부섬장 여부
        long joinedAt,
        long lastActivity,
        @Nullable IslandSpawnPointDTO personalSpawn // 개인 스폰 위치
) {
    /**
     * 새 섬원 생성
     */
    public static IslandMemberDTO createNew(String uuid, String name, boolean isCoOwner) {
        return new IslandMemberDTO(
                uuid,
                name,
                isCoOwner,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                null
        );
    }
}