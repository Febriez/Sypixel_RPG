package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import net.kyori.adventure.text.Component;
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
        long totalContribution, // 총 기여도
        long lastJoined,
        long lastActivity
) {
    /**
     * 새 플레이어 데이터 생성 (섬 없음)
     */
    public static PlayerIslandDataDTO createNew(String playerUuid) {
        long now = System.currentTimeMillis();
        return new PlayerIslandDataDTO(
                playerUuid,
                null,
                null,
                0,
                0L, // totalContribution
                now,
                now
        );
    }
    
    /**
     * 섬에 가입
     */
    public PlayerIslandDataDTO joinIsland(String islandId, IslandRole role) {
        long now = System.currentTimeMillis();
        return new PlayerIslandDataDTO(
                playerUuid,
                islandId,
                role,
                totalIslandResets,
                totalContribution,
                now,
                now
        );
    }
    
    /**
     * 섬 떠나기
     */
    public PlayerIslandDataDTO leaveIsland() {
        long now = System.currentTimeMillis();
        return new PlayerIslandDataDTO(
                playerUuid,
                null,
                null,
                totalIslandResets,
                totalContribution,
                lastJoined,
                now
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
                totalContribution,
                lastJoined,
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
    @NotNull
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
        map.put("totalContribution", totalContribution);
        map.put("lastJoined", lastJoined);
        map.put("lastActivity", lastActivity);
        
        return map;
    }
    
    /**
     * Map에서 생성
     */
    @NotNull
    public static PlayerIslandDataDTO fromMap(@NotNull Map<String, Object> map) {
        try {
            String playerUuid = (String) map.getOrDefault("playerUuid", "");
            
            if (playerUuid.isEmpty()) {
                throw new IllegalArgumentException(
                    "Invalid PlayerIslandDataDTO: playerUuid cannot be empty"
                );
            }
            
            String currentIslandId = (String) map.get("currentIslandId");
            
            IslandRole role = null;
            String roleStr = (String) map.get("role");
            if (roleStr != null && !roleStr.isEmpty()) {
                try {
                    role = IslandRole.valueOf(roleStr);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(
                        String.format("Invalid PlayerIslandDataDTO: unknown role '%s'. Valid roles are: %s",
                            roleStr, 
                            java.util.Arrays.toString(IslandRole.values()))
                    );
                }
            }
            
            int totalIslandResets = FirestoreUtils.getInt(map, "totalIslandResets", 0);
            long totalContribution = FirestoreUtils.getLong(map, "totalContribution", 0L);
            long lastJoined = FirestoreUtils.getLong(map, "lastJoined", System.currentTimeMillis());
            long lastActivity = FirestoreUtils.getLong(map, "lastActivity", System.currentTimeMillis());
            
            // 유효성 검증
            if (totalIslandResets < 0) {
                throw new IllegalArgumentException(
                    String.format("Invalid PlayerIslandDataDTO: totalIslandResets cannot be negative, but found: %d",
                        totalIslandResets)
                );
            }
            
            if (totalContribution < 0) {
                throw new IllegalArgumentException(
                    String.format("Invalid PlayerIslandDataDTO: totalContribution cannot be negative, but found: %d",
                        totalContribution)
                );
            }
            
            if (currentIslandId != null && role == null) {
                throw new IllegalArgumentException(
                    "Invalid PlayerIslandDataDTO: player has currentIslandId but no role assigned"
                );
            }
            
            return new PlayerIslandDataDTO(playerUuid, currentIslandId, role, totalIslandResets, totalContribution, lastJoined, lastActivity);
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new IllegalArgumentException(
                String.format("Failed to parse PlayerIslandDataDTO: %s. Map structure: %s", 
                    e.getMessage(), 
                    map.toString().length() > 200 ? map.toString().substring(0, 200) + "..." : map.toString())
            );
        }
    }
    
}