package com.febrie.rpg.dto.island;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

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
    
    /**
     * Map으로 변환 (Firebase 저장용)
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("playerUuid", playerUuid);
        if (currentIslandId != null) {
            map.put("currentIslandId", currentIslandId);
        }
        if (role != null) {
            map.put("role", role.name());
        }
        map.put("totalIslandResets", totalIslandResets);
        map.put("lastIslandActivity", lastIslandActivity);
        return map;
    }
    
    /**
     * Map에서 생성
     */
    public static PlayerIslandDataDTO fromMap(Map<String, Object> map) {
        if (map == null) return null;
        
        String playerUuid = (String) map.get("playerUuid");
        String currentIslandId = (String) map.get("currentIslandId");
        
        IslandRole role = null;
        if (map.containsKey("role")) {
            try {
                role = IslandRole.valueOf((String) map.get("role"));
            } catch (IllegalArgumentException e) {
                // 잘못된 역할 이름은 무시
            }
        }
        
        return new PlayerIslandDataDTO(
                playerUuid,
                currentIslandId,
                role,
                ((Number) map.get("totalIslandResets")).intValue(),
                ((Number) map.get("lastIslandActivity")).longValue()
        );
    }
}