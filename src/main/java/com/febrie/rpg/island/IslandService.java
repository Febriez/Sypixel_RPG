package com.febrie.rpg.island;

import com.febrie.rpg.database.service.impl.IslandFirestoreService;
import com.febrie.rpg.database.service.impl.PlayerIslandDataService;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.PlayerIslandDataDTO;
import com.febrie.rpg.util.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.kyori.adventure.text.Component;
/**
 * 섬 서비스 래퍼 클래스
 * Firestore 서비스와의 통신을 관리
 *
 * @author Febrie, CoffeeTory
 */
public class IslandService {
    
    private final IslandFirestoreService firestoreService;
    private final PlayerIslandDataService playerDataService;
    private final boolean isOfflineMode;
    
    public IslandService(@Nullable IslandFirestoreService firestoreService, @Nullable PlayerIslandDataService playerDataService) {
        this.firestoreService = firestoreService;
        this.playerDataService = playerDataService;
        this.isOfflineMode = (firestoreService == null || playerDataService == null);
        
    }
    
    /**
     * 섬 저장
     */
    public CompletableFuture<Boolean> saveIsland(@NotNull IslandDTO island) {
        if (isOfflineMode) {
            return CompletableFuture.completedFuture(true);
        }
        
        return firestoreService.save(island.core().islandId(), island)
                .thenApply(v -> {
                    return true;
                })
                .exceptionally(ex -> {
                    LogUtil.error("섬 저장 실패: " + island.core().islandId(), ex);
                    return false;
                });
    }
    
    /**
     * 섬 로드
     */
    public CompletableFuture<IslandDTO> loadIsland(@NotNull String islandId) {
        if (isOfflineMode) {
            return CompletableFuture.completedFuture(null);
        }
        
        return firestoreService.get(islandId)
                .thenApply(island -> {
                    if (island != null) {
                    }
                    return island;
                })
                .exceptionally(ex -> {
                    LogUtil.error("섬 로드 실패: " + islandId, ex);
                    return null;
                });
    }
    
    /**
     * 섬 삭제
     */
    public CompletableFuture<Boolean> deleteIsland(@NotNull String islandId) {
        if (isOfflineMode) {
            return CompletableFuture.completedFuture(true);
        }
        
        return firestoreService.delete(islandId)
                .thenApply(v -> {
                    return true;
                })
                .exceptionally(ex -> {
                    LogUtil.error("섬 삭제 실패: " + islandId, ex);
                    return false;
                });
    }
    
    /**
     * 플레이어 데이터 저장
     */
    public CompletableFuture<Boolean> savePlayerData(@NotNull PlayerIslandDataDTO playerData) {
        if (isOfflineMode) {
            return CompletableFuture.completedFuture(true);
        }
        
        return playerDataService.save(playerData.playerUuid(), playerData)
                .thenApply(v -> {
                    return true;
                })
                .exceptionally(ex -> {
                    LogUtil.error("플레이어 데이터 저장 실패: " + playerData.playerUuid(), ex);
                    return false;
                });
    }
    
    /**
     * 플레이어 데이터 로드
     */
    public CompletableFuture<PlayerIslandDataDTO> loadPlayerData(@NotNull String playerUuid) {
        if (isOfflineMode) {
            return CompletableFuture.completedFuture(null);
        }
        
        return playerDataService.get(playerUuid)
                .exceptionally(ex -> {
                    LogUtil.error("플레이어 데이터 로드 실패: " + playerUuid, ex);
                    return null;
                });
    }
    
    /**
     * 공개 섬 목록 조회
     */
    public CompletableFuture<List<IslandDTO>> loadPublicIslands(int limit) {
        if (isOfflineMode) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        
        return firestoreService.getPublicIslands()
                .thenApply(islands -> {
                    return islands;
                })
                .exceptionally(ex -> {
                    LogUtil.error("공개 섬 목록 로드 실패", ex);
                    return new ArrayList<>();
                });
    }
    
    /**
     * 모든 섬 데이터 로드
     */
    public CompletableFuture<List<IslandDTO>> loadAllIslands() {
        if (isOfflineMode) {
            LogUtil.info("오프라인 모드: 섬 데이터를 로드하지 않습니다.");
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        
        // getAllIslands()가 이미 CompletableFuture<List<IslandDTO>>를 반환하므로 직접 반환
        return firestoreService.getAllIslands();
    }
    
    /**
     * 모든 플레이어 섬 데이터 로드
     */
    public CompletableFuture<List<PlayerIslandDataDTO>> loadAllPlayerData() {
        if (isOfflineMode) {
            LogUtil.info("오프라인 모드: 플레이어 데이터를 로드하지 않습니다.");
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        
        // getAllPlayerData()가 이미 CompletableFuture<List<PlayerIslandDataDTO>>를 반환하므로 직접 반환
        return playerDataService.getAllPlayerData();
    }
    
    /**
     * 오프라인 모드 여부
     */
    public boolean isOfflineMode() {
        return isOfflineMode;
    }
}