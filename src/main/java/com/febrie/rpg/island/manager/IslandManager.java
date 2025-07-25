package com.febrie.rpg.island.manager;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.impl.IslandFirestoreService;
import com.febrie.rpg.database.service.impl.PlayerIslandDataService;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.island.IslandPlayer;
import com.febrie.rpg.island.*;
import com.febrie.rpg.island.world.IslandWorldManager;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
    private final IslandVisitTracker visitTracker;
    
    public IslandManager(@NotNull RPGMain plugin, @Nullable IslandFirestoreService firestoreService, @Nullable PlayerIslandDataService playerDataService) {
        this.plugin = plugin;
        this.worldManager = new IslandWorldManager(plugin);
        
        this.islandService = new IslandService(firestoreService, playerDataService);
        this.cache = new IslandCache();
        this.visitTracker = new IslandVisitTracker(plugin, this);
    }
    
    /**
     * 초기화
     */
    public void initialize() {
        worldManager.initialize();
        visitTracker.startTracking();
    }
    
    /**
     * 종료 처리
     */
    public void shutdown() {
        visitTracker.stopTracking();
    }
    
    /**
     * WorldManager 가져오기 (Island 클래스용)
     */
    private IslandWorldManager getWorldManagerForIsland() {
        return worldManager;
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
     * 새 섬 생성 (확장 버전 - 색상, 바이옴, 템플릿 지정)
     */
    public CompletableFuture<Boolean> createIsland(@NotNull Player owner, @NotNull String islandName, 
                                                   @NotNull String colorHex, @NotNull String biome, 
                                                   @NotNull String template) {
        String ownerUuid = owner.getUniqueId().toString();
        String ownerName = owner.getName();
        
        // 플레이어 로드
        return loadIslandPlayer(ownerUuid, ownerName).thenCompose(islandPlayer -> {
            // 이미 섬을 가지고 있는지 확인
            if (islandPlayer.hasIsland()) {
                LogUtil.warning("섬 생성 실패: 플레이어가 이미 섬을 소유하고 있음 - " + ownerName);
                return CompletableFuture.completedFuture(false);
            }
            
            // 물리적 섬 생성
            CompletableFuture<IslandLocationDTO> locationFuture = worldManager.createNewIsland(85);
            
            return locationFuture.thenCompose(location -> {
                if (location == null) {
                    LogUtil.error("물리적 섬 생성 실패");
                    return CompletableFuture.completedFuture(false);
                }
                
                String islandId = UUID.randomUUID().toString();
                
                // 기본 섬 데이터 생성
                IslandDTO baseIsland = IslandDTO.createNew(islandId, ownerUuid, ownerName, islandName);
                
                // 설정을 포함한 새로운 섬 데이터 생성
                IslandSettingsDTO settings = new IslandSettingsDTO(colorHex, biome, template);
                IslandDTO islandData = new IslandDTO(
                    baseIsland.islandId(),
                    baseIsland.ownerUuid(),
                    baseIsland.ownerName(),
                    baseIsland.islandName(),
                    baseIsland.size(),
                    baseIsland.isPublic(),
                    baseIsland.createdAt(),
                    baseIsland.lastActivity(),
                    baseIsland.members(),
                    baseIsland.workers(),
                    baseIsland.contributions(),
                    new IslandSpawnDTO(
                        new IslandSpawnPointDTO(location.centerX(), 66.0, location.centerZ(), 0.0f, 0.0f, "섬 중앙"),
                        List.of(),
                        Map.of()
                    ),
                    baseIsland.upgradeData(),
                    baseIsland.permissions(),
                    baseIsland.pendingInvites(),
                    baseIsland.recentVisits(),
                    baseIsland.totalResets(),
                    baseIsland.deletionScheduledAt(),
                    settings
                );
                
                // 섬 저장
                return islandService.saveIsland(islandData).thenCompose(saved -> {
                    if (!saved) {
                        return CompletableFuture.completedFuture(false);
                    }
                    
                    // 플레이어 데이터 업데이트
                    PlayerIslandDataDTO playerData = new PlayerIslandDataDTO(
                        islandPlayer.getUuid(),
                        islandId,
                        IslandRole.OWNER,
                        0, // totalIslandResets
                        0L, // totalContribution
                        System.currentTimeMillis(), // lastJoined
                        System.currentTimeMillis() // lastActivity
                    );
                    
                    return islandService.savePlayerData(playerData).thenCompose(playerSaved -> {
                        if (!playerSaved) {
                            return CompletableFuture.completedFuture(false);
                        }
                        
                        // 캐시 업데이트
                        cache.putIsland(islandId, islandData);
                        cache.putPlayerData(ownerUuid, playerData);
                        
                        // 바이옴 설정 적용
                        return applyBiomeToIsland(location.centerX(), location.centerZ(), baseIsland.size(), biome)
                            .thenApply(biomeApplied -> {
                                if (biomeApplied) {
                                    LogUtil.info("섬 바이옴 설정 성공: " + biome);
                                } else {
                                    LogUtil.warning("섬 바이옴 설정 실패: " + biome);
                                }
                                return true;
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
            return CompletableFuture.completedFuture(new Island(getWorldManagerForIsland(), cached));
        }
        
        // Firestore에서 로드
        return islandService.loadIsland(islandId).thenApply(islandData -> {
            if (islandData != null) {
                cache.putIsland(islandId, islandData);
                cache.updateIslandMembers(islandData);
                return new Island(getWorldManagerForIsland(), islandData);
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
        if (!getWorldManager().isIslandWorld(location.getWorld())) {
            return null;
        }
        
        // 캐시된 모든 섬 확인
        for (IslandDTO islandData : cache.getAllIslands()) {
            Island island = new Island(getWorldManagerForIsland(), islandData);
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
     * VisitTracker 가져오기
     */
    @NotNull
    public IslandVisitTracker getVisitTracker() {
        return visitTracker;
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
    
    /**
     * 캐시에서 플레이어 섬 데이터 가져오기
     */
    @Nullable
    public PlayerIslandDataDTO getPlayerIslandDataFromCache(@NotNull String playerUuid) {
        return cache.getPlayerData(playerUuid);
    }
    
    /**
     * 캐시에서 섬 데이터 가져오기
     */
    @Nullable
    public IslandDTO getIslandFromCache(@NotNull String islandId) {
        return cache.getIsland(islandId);
    }
    
    /**
     * 섬에 바이옴 적용
     */
    private CompletableFuture<Boolean> applyBiomeToIsland(int centerX, int centerZ, int size, String biome) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                org.bukkit.World world = worldManager.getIslandWorld();
                
                if (world == null) {
                    return false;
                }
                
                // 바이옴 문자열을 직접 매칭하여 찾기
                org.bukkit.block.Biome bukkitBiome = null;
                String biomeUpper = biome.toUpperCase().replace("-", "_");
                
                // 일반적인 바이옴 매핑
                switch (biomeUpper) {
                    case "PLAINS" -> bukkitBiome = org.bukkit.block.Biome.PLAINS;
                    case "FOREST" -> bukkitBiome = org.bukkit.block.Biome.FOREST;
                    case "DESERT" -> bukkitBiome = org.bukkit.block.Biome.DESERT;
                    case "JUNGLE" -> bukkitBiome = org.bukkit.block.Biome.JUNGLE;
                    case "TAIGA" -> bukkitBiome = org.bukkit.block.Biome.TAIGA;
                    case "SNOWY_TAIGA" -> bukkitBiome = org.bukkit.block.Biome.SNOWY_TAIGA;
                    case "SAVANNA" -> bukkitBiome = org.bukkit.block.Biome.SAVANNA;
                    case "SWAMP" -> bukkitBiome = org.bukkit.block.Biome.SWAMP;
                    case "OCEAN" -> bukkitBiome = org.bukkit.block.Biome.OCEAN;
                    case "BEACH" -> bukkitBiome = org.bukkit.block.Biome.BEACH;
                    case "MUSHROOM_FIELDS" -> bukkitBiome = org.bukkit.block.Biome.MUSHROOM_FIELDS;
                    case "BADLANDS" -> bukkitBiome = org.bukkit.block.Biome.BADLANDS;
                    case "FLOWER_FOREST" -> bukkitBiome = org.bukkit.block.Biome.FLOWER_FOREST;
                    case "CHERRY_GROVE" -> bukkitBiome = org.bukkit.block.Biome.CHERRY_GROVE;
                    case "DARK_FOREST" -> bukkitBiome = org.bukkit.block.Biome.DARK_FOREST;
                    case "BIRCH_FOREST" -> bukkitBiome = org.bukkit.block.Biome.BIRCH_FOREST;
                    case "BAMBOO_JUNGLE" -> bukkitBiome = org.bukkit.block.Biome.BAMBOO_JUNGLE;
                    default -> {
                        LogUtil.warning("지원하지 않는 바이옴: " + biome);
                        return false;
                    }
                }
                
                // 섬 크기에 따라 바이옴 설정 범위 결정
                int radius = size / 2;
                final org.bukkit.block.Biome finalBiome = bukkitBiome;
                
                // 비동기로 바이옴 설정
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    for (int x = centerX - radius; x <= centerX + radius; x++) {
                        for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                            // 모든 높이에 대해 바이옴 설정
                            for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
                                world.setBiome(x, y, z, finalBiome);
                            }
                        }
                    }
                });
                
                return true;
            } catch (Exception e) {
                LogUtil.error("바이옴 설정 중 오류", e);
                return false;
            }
        });
    }
}