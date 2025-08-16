package com.febrie.rpg.island;

import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.PlayerIslandDataDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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
    
    // 캐시 통계
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);
    
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
        IslandDTO island = islandCache.get(islandId);
        if (island != null) {
            cacheHits.incrementAndGet();
        } else {
            cacheMisses.incrementAndGet();
        }
        return island;
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
        PlayerIslandDataDTO data = playerDataCache.get(playerUuid);
        if (data != null) {
            cacheHits.incrementAndGet();
        } else {
            cacheMisses.incrementAndGet();
        }
        return data;
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
     * 섬 개수 반환
     */
    public int getIslandCount() {
        return islandCache.size();
    }
    
    /**
     * 플레이어 데이터 개수 반환
     */
    public int getPlayerCount() {
        return playerDataCache.size();
    }
    
    /**
     * 예상 메모리 사용량 (KB)
     */
    public long getEstimatedMemoryUsage() {
        // 예상 메모리 사용량 계산
        long islandMemory = islandCache.size() * 3L; // 3KB per island
        long playerMemory = playerDataCache.size() / 5; // ~200 bytes per player
        return islandMemory + playerMemory;
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
    
    /**
     * 캐시 상세 통계
     */
    public String getCacheStats() {
        long totalRequests = cacheHits.get() + cacheMisses.get();
        double hitRate = totalRequests > 0 ? 
            (cacheHits.get() / (double)totalRequests * 100) : 0;
        
        return String.format(
            "캐시 통계: 섬 %d개, 플레이어 %d개, 메모리 ~%d KB, 적중률 %.1f%% (%d/%d)",
            getIslandCount(),
            getPlayerCount(),
            getEstimatedMemoryUsage(),
            hitRate,
            cacheHits.get(),
            totalRequests
        );
    }
    
    /**
     * 캐시 통계 리셋
     */
    public void resetStats() {
        cacheHits.set(0);
        cacheMisses.set(0);
    }
}