package com.febrie.rpg.island.manager;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.impl.IslandFirestoreService;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.island.*;
import com.febrie.rpg.island.world.IslandWorldManager;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 섬 시스템 관리자
 * 섬 생성, 삭제, 관리 등 모든 섬 관련 기능 총괄
 *
 * @author Febrie, CoffeeTory
 */
public class IslandManager {
    
    private final RPGMain plugin;
    private final IslandWorldManager worldManager;
    private final IslandService islandService;
    private final IslandCache cache;
    
    public IslandManager(@NotNull RPGMain plugin, @Nullable IslandFirestoreService firestoreService) {
        this.plugin = plugin;
        this.worldManager = new IslandWorldManager(plugin);
        this.islandService = new IslandService(firestoreService);
        this.cache = new IslandCache();
        
    }
    
    /**
     * 초기화
     */
    public void initialize() {
        worldManager.initialize();
    }
    
    /**
     * 새 섬 생성
     */
    public CompletableFuture<IslandDTO> createIsland(@NotNull Player owner, @NotNull String islandName) {
        String ownerUuid = owner.getUniqueId().toString();
        String ownerName = owner.getName();
        
        // 플레이어 로드
        return loadIslandPlayer(ownerUuid, ownerName).thenCompose(islandPlayer -> {
            // 이미 섬이 있는지 확인
            if (islandPlayer.hasIsland()) {
                return CompletableFuture.completedFuture(null);
            }
            
            // 새 섬 생성
            return Island.create(worldManager, ownerUuid, ownerName, islandName)
                    .thenCompose(island -> {
                        // Firestore에 저장
                        return islandService.saveIsland(island.getData()).thenCompose(saved -> {
                            if (!saved) {
                                return CompletableFuture.completedFuture(null);
                            }
                            
                            // 플레이어를 섬에 가입시킴
                            return islandPlayer.joinIsland(island.getId(), IslandRole.OWNER)
                                    .thenCompose(joined -> {
                                        if (!joined) {
                                            return CompletableFuture.completedFuture(null);
                                        }
                                        
                                        // 플레이어 데이터 저장
                                        return islandService.savePlayerData(islandPlayer.getData())
                                                .thenApply(playerSaved -> {
                                                    if (playerSaved) {
                                                        // 캐시 업데이트
                                                        cache.putIsland(island.getId(), island.getData());
                                                        cache.putPlayerData(ownerUuid, islandPlayer.getData());
                                                        cache.updateIslandMembers(island.getData());
                                                        
                                                        // 플레이어를 섬으로 텔레포트
                                                        Bukkit.getScheduler().runTask(plugin, () -> 
                                                            owner.teleport(island.getSpawnLocation())
                                                        );
                                                        
                                                        return island.getData();
                                                    } else {
                                                        return null;
                                                    }
                                                });
                                    });
                        });
                    });
        });
    }
    
    /**
     * 섬 정보 로드
     */
    public CompletableFuture<Island> loadIsland(@NotNull String islandId) {
        // 캐시 확인
        IslandDTO cached = cache.getIsland(islandId);
        if (cached != null) {
            return CompletableFuture.completedFuture(new Island(worldManager, cached));
        }
        
        // Firestore에서 로드
        return islandService.loadIsland(islandId).thenApply(islandData -> {
            if (islandData != null) {
                cache.putIsland(islandId, islandData);
                cache.updateIslandMembers(islandData);
                return new Island(worldManager, islandData);
            }
            return null;
        });
    }
    
    /**
     * 플레이어의 섬 데이터 로드
     */
    public CompletableFuture<IslandPlayer> loadIslandPlayer(@NotNull String playerUuid, @NotNull String playerName) {
        // 캐시 확인
        PlayerIslandDataDTO cached = cache.getPlayerData(playerUuid);
        if (cached != null) {
            return CompletableFuture.completedFuture(new IslandPlayer(playerUuid, playerName, cached));
        }
        
        // Firestore에서 로드
        return islandService.loadPlayerData(playerUuid).thenApply(data -> {
            if (data != null) {
                cache.putPlayerData(playerUuid, data);
            }
            return new IslandPlayer(playerUuid, playerName, data);
        });
    }
    
    /**
     * 플레이어의 섬 가져오기
     */
    public CompletableFuture<Island> getPlayerIsland(@NotNull String playerUuid, @NotNull String playerName) {
        String islandId = cache.getPlayerIslandId(playerUuid);
        if (islandId != null) {
            return loadIsland(islandId);
        }
        
        return loadIslandPlayer(playerUuid, playerName).thenCompose(islandPlayer -> {
            String currentIslandId = islandPlayer.getCurrentIslandId();
            if (currentIslandId != null) {
                return loadIsland(currentIslandId);
            }
            return CompletableFuture.completedFuture(null);
        });
    }
    
    /**
     * 섬 삭제
     */
    public CompletableFuture<Boolean> deleteIsland(@NotNull String islandId) {
        return loadIsland(islandId).thenCompose(island -> {
            if (island == null) {
                return CompletableFuture.completedFuture(false);
            }
            
            // 물리적 섬 삭제
            return island.delete().thenCompose(deleted -> {
                if (!deleted) {
                    return CompletableFuture.completedFuture(false);
                }
                
                // 모든 멤버의 섬 데이터 제거
                List<CompletableFuture<Boolean>> memberUpdates = new ArrayList<>();
                
                for (String memberUuid : island.getAllMemberUuids()) {
                    memberUpdates.add(
                        loadIslandPlayer(memberUuid, "Unknown").thenCompose(islandPlayer -> 
                            islandPlayer.leaveIsland().thenCompose(left -> 
                                islandService.savePlayerData(islandPlayer.getData())
                            )
                        )
                    );
                }
                
                return CompletableFuture.allOf(memberUpdates.toArray(new CompletableFuture<?>[0]))
                        .thenCompose(v -> islandService.deleteIsland(islandId))
                        .thenApply(firestoreDeleted -> {
                            if (firestoreDeleted) {
                                // 캐시 제거
                                cache.removeIslandMembers(island.getData());
                                cache.removeIsland(islandId);
                            }
                            return firestoreDeleted;
                        });
            });
        });
    }
    
    /**
     * 섬 초기화
     */
    public CompletableFuture<Boolean> resetIsland(@NotNull String islandId) {
        return loadIsland(islandId).thenCompose(island -> {
            if (island == null) {
                return CompletableFuture.completedFuture(false);
            }
            
            // 섬장의 초기화 가능 여부 확인
            return loadIslandPlayer(island.getOwnerUuid(), island.getOwnerName())
                    .thenCompose(ownerPlayer -> {
                        if (!ownerPlayer.canResetIsland()) {
                                            return CompletableFuture.completedFuture(false);
                        }
                        
                        // 물리적 섬 초기화
                        return island.reset().thenCompose(reset -> {
                            if (!reset) {
                                return CompletableFuture.completedFuture(false);
                            }
                            
                            // 섬장의 초기화 횟수 증가
                            ownerPlayer.incrementResetCount();
                            
                            // 저장
                            return CompletableFuture.allOf(
                                    islandService.saveIsland(island.getData()),
                                    islandService.savePlayerData(ownerPlayer.getData())
                            ).thenApply(v -> {
                                // 캐시 업데이트
                                cache.putIsland(islandId, island.getData());
                                cache.putPlayerData(island.getOwnerUuid(), ownerPlayer.getData());
                                
                                return true;
                            });
                        });
                    });
        });
    }
    
    /**
     * 공개 섬 목록 조회
     */
    public CompletableFuture<List<IslandDTO>> getPublicIslands(int limit) {
        return islandService.loadPublicIslands(limit);
    }
    
    /**
     * 섬 업데이트
     */
    public CompletableFuture<Boolean> updateIsland(@NotNull IslandDTO island) {
        return islandService.saveIsland(island).thenApply(saved -> {
            if (saved) {
                cache.putIsland(island.islandId(), island);
                cache.updateIslandMembers(island);
            }
            return saved;
        });
    }
    
    /**
     * 플레이어가 현재 있는 섬 찾기
     */
    @Nullable
    public Island getIslandAt(@NotNull Location location) {
        if (!worldManager.isIslandWorld(location.getWorld())) {
            return null;
        }
        
        // 캐시된 모든 섬 확인
        for (IslandDTO islandData : cache.getAllIslands()) {
            Island island = new Island(worldManager, islandData);
            if (island.contains(location)) {
                return island;
            }
        }
        
        return null;
    }
    
    /**
     * 섬 월드 관리자 가져오기
     */
    public IslandWorldManager getWorldManager() {
        return worldManager;
    }
    
    /**
     * 캐시 초기화
     */
    public void clearCache() {
        cache.clear();
    }
    
    /**
     * 캐시 통계
     */
    public String getCacheStats() {
        return cache.getStats();
    }
    
    /**
     * 플레이어 캐시 업데이트
     */
    public void updatePlayerCache(@NotNull String playerUuid, @NotNull PlayerIslandDataDTO data) {
        cache.putPlayerData(playerUuid, data);
    }
}