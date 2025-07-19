package com.febrie.rpg.island;

import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.PlayerIslandDataDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 섬 캐시 관리 클래스
 * 메모리 캐싱을 통한 성능 최적화
 *
 * @author Febrie, CoffeeTory
 */
public class IslandCache {
    
    // 캐시
    private final Map<String, IslandDTO> islandCache = new ConcurrentHashMap<>(); // islandId -> IslandDTO
    private final Map<String, String> playerIslandMap = new ConcurrentHashMap<>(); // playerUuid -> islandId
    private final Map<String, PlayerIslandDataDTO> playerDataCache = new ConcurrentHashMap<>();
    
    /**
     * 섬 캐시에 추가
     */
    public void putIsland(@NotNull String islandId, @NotNull IslandDTO island) {
        islandCache.put(islandId, island);
    }
    
    /**
     * 섬 캐시에서 가져오기
     */
    @Nullable
    public IslandDTO getIsland(@NotNull String islandId) {
        return islandCache.get(islandId);
    }
    
    /**
     * 섬 캐시에서 제거
     */
    public void removeIsland(@NotNull String islandId) {
        islandCache.remove(islandId);
    }
    
    /**
     * 플레이어 데이터 캐시에 추가
     */
    public void putPlayerData(@NotNull String playerUuid, @NotNull PlayerIslandDataDTO data) {
        playerDataCache.put(playerUuid, data);
        
        // 플레이어-섬 매핑 업데이트
        if (data.currentIslandId() != null) {
            playerIslandMap.put(playerUuid, data.currentIslandId());
        } else {
            playerIslandMap.remove(playerUuid);
        }
    }
    
    /**
     * 플레이어 데이터 캐시에서 가져오기
     */
    @Nullable
    public PlayerIslandDataDTO getPlayerData(@NotNull String playerUuid) {
        return playerDataCache.get(playerUuid);
    }
    
    /**
     * 플레이어 데이터 캐시에서 제거
     */
    public void removePlayerData(@NotNull String playerUuid) {
        playerDataCache.remove(playerUuid);
        playerIslandMap.remove(playerUuid);
    }
    
    /**
     * 플레이어의 섬 ID 가져오기
     */
    @Nullable
    public String getPlayerIslandId(@NotNull String playerUuid) {
        return playerIslandMap.get(playerUuid);
    }
    
    /**
     * 섬 멤버 매핑 업데이트
     */
    public void updateIslandMembers(@NotNull IslandDTO island) {
        // 소유자
        playerIslandMap.put(island.ownerUuid(), island.islandId());
        
        // 멤버들
        island.members().forEach(member -> 
            playerIslandMap.put(member.uuid(), island.islandId())
        );
        
        // 알바생들
        island.workers().forEach(worker ->
            playerIslandMap.put(worker.uuid(), island.islandId())
        );
    }
    
    /**
     * 섬 멤버 매핑 제거
     */
    public void removeIslandMembers(@NotNull IslandDTO island) {
        // 소유자
        playerIslandMap.remove(island.ownerUuid());
        
        // 멤버들
        island.members().forEach(member -> 
            playerIslandMap.remove(member.uuid())
        );
        
        // 알바생들
        island.workers().forEach(worker ->
            playerIslandMap.remove(worker.uuid())
        );
    }
    
    /**
     * 모든 캐시된 섬 가져오기
     */
    public Collection<IslandDTO> getAllIslands() {
        return new ArrayList<>(islandCache.values());
    }
    
    /**
     * 캐시 초기화
     */
    public void clear() {
        islandCache.clear();
        playerIslandMap.clear();
        playerDataCache.clear();
    }
    
    /**
     * 캐시 통계
     */
    public String getStats() {
        return String.format("섬 캐시: %d개, 플레이어 데이터: %d개, 플레이어-섬 매핑: %d개",
                islandCache.size(),
                playerDataCache.size(),
                playerIslandMap.size()
        );
    }
}