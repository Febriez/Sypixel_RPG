package com.febrie.rpg.island.dto;

import org.jetbrains.annotations.NotNull;

/**
 * 섬 알바생 정보 DTO (Record)
 *
 * @author Febrie, CoffeeTory
 */
public record IslandWorkerDTO(
        @NotNull String uuid,
        @NotNull String name,
        long hiredAt,
        long lastActivity
) {
    /**
     * 새 알바생 생성
     */
    public static IslandWorkerDTO createNew(String uuid, String name) {
        return new IslandWorkerDTO(
                uuid,
                name,
                System.currentTimeMillis(),
                System.currentTimeMillis()
        );
    }
}