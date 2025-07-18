package com.febrie.rpg.island.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 플레이어의 섬 관련 데이터 DTO (Record)
 * 플레이어가 어떤 섬에 속해있는지 추적
 *
 * @author Febrie, CoffeeTory
 */
public record PlayerIslandDataDTO(
        @NotNull String playerUuid,
        @Nullable String currentIslandId, // 현재 속한 섬 ID (null = 섬 없음)
        @Nullable IslandRole role, // 섬에서의 역할
        int totalIslandResets, // 총 섬 초기화 횟수
        long lastIslandActivity
) {
    /**
     * 새 플레이어 데이터 생성 (섬 없음)
     */
    public static PlayerIslandDataDTO createNew(String playerUuid) {
        return new PlayerIslandDataDTO(
                playerUuid,
                null,
                null,
                0,
                System.currentTimeMillis()
        );
    }
    
    /**
     * 섬에 가입
     */
    public PlayerIslandDataDTO joinIsland(String islandId, IslandRole role) {
        return new PlayerIslandDataDTO(
                playerUuid,
                islandId,
                role,
                totalIslandResets,
                System.currentTimeMillis()
        );
    }
    
    /**
     * 섬 떠나기
     */
    public PlayerIslandDataDTO leaveIsland() {
        return new PlayerIslandDataDTO(
                playerUuid,
                null,
                null,
                totalIslandResets,
                System.currentTimeMillis()
        );
    }
    
    /**
     * 섬 초기화 카운트 증가
     */
    public PlayerIslandDataDTO incrementResetCount() {
        return new PlayerIslandDataDTO(
                playerUuid,
                currentIslandId,
                role,
                totalIslandResets + 1,
                System.currentTimeMillis()
        );
    }
    
    /**
     * 섬이 있는지 확인
     */
    public boolean hasIsland() {
        return currentIslandId != null;
    }
    
    /**
     * 섬 초기화 가능 여부 (평생 1번만)
     */
    public boolean canResetIsland() {
        return totalIslandResets < 1;
    }
}