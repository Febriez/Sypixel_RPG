package com.febrie.rpg.island.manager;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.FirestoreRestService;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.island.world.IslandWorldManager;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 섬 시스템 관리자
 * 섬 생성, 삭제, 관리 등 모든 섬 관련 기능 총괄
 *
 * @author Febrie, CoffeeTory
 */
public class IslandManager {
    
    private final RPGMain plugin;
    private final FirestoreRestService firestoreService;
    private final IslandWorldManager worldManager;
    
    // 캐시
    private final Map<String, IslandDTO> islandCache = new ConcurrentHashMap<>(); // islandId -> IslandDTO
    private final Map<String, String> playerIslandMap = new ConcurrentHashMap<>(); // playerUuid -> islandId
    private final Map<String, PlayerIslandDataDTO> playerDataCache = new ConcurrentHashMap<>();
    
    public IslandManager(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        this.firestoreService = plugin.getFirestoreService();
        this.worldManager = new IslandWorldManager(plugin);
    }
    
    /**
     * 초기화
     */
    public void initialize() {
        worldManager.initialize();
        LogUtil.info("섬 관리자 초기화 완료");
    }
    
    /**
     * 새 섬 생성
     */
    public CompletableFuture<IslandDTO> createIsland(@NotNull Player owner, @NotNull String islandName) {
        String ownerUuid = owner.getUniqueId().toString();
        String ownerName = owner.getName();
        
        return loadPlayerIslandData(ownerUuid).thenCompose(playerData -> {
            // 이미 섬이 있는지 확인
            if (playerData != null && playerData.hasIsland()) {
                LogUtil.warning(ownerName + "님은 이미 섬을 소유하고 있습니다.");
                return CompletableFuture.completedFuture(null);
            }
            
            // 섬 ID 생성
            String islandId = UUID.randomUUID().toString();
            
            // 섬 월드에 물리적 섬 생성
            return worldManager.createNewIsland(85).thenCompose(location -> {
                // 섬 DTO 생성
                IslandDTO island = IslandDTO.createNew(islandId, ownerUuid, ownerName, islandName);
                
                // 위치 정보를 포함한 새 섬 DTO 생성
                IslandDTO islandWithLocation = new IslandDTO(
                        island.islandId(),
                        island.ownerUuid(),
                        island.ownerName(),
                        island.islandName(),
                        island.size(),
                        island.isPublic(),
                        island.createdAt(),
                        island.lastActivity(),
                        island.members(),
                        island.workers(),
                        island.contributions(),
                        new IslandSpawnDTO(
                                IslandSpawnPointDTO.fromLocation(
                                        location.getCenter(worldManager.getIslandWorld()),
                                        "섬 중앙"
                                ),
                                List.of(),
                                Map.of()
                        ),
                        island.upgradeData(),
                        island.permissions(),
                        island.pendingInvites(),
                        island.recentVisits(),
                        island.totalResets(),
                        island.deletionScheduledAt()
                );
                
                // Firestore에 저장
                return firestoreService.saveIsland(islandWithLocation).thenCompose(saved -> {
                    if (!saved) {
                        LogUtil.error("섬 데이터 저장 실패: " + islandId);
                        return CompletableFuture.completedFuture(null);
                    }
                    
                    // 플레이어 데이터 업데이트
                    PlayerIslandDataDTO newPlayerData;
                    if (playerData == null) {
                        newPlayerData = PlayerIslandDataDTO.createNew(ownerUuid)
                                .joinIsland(islandId, IslandRole.OWNER);
                    } else {
                        newPlayerData = playerData.joinIsland(islandId, IslandRole.OWNER);
                    }
                    
                    return firestoreService.savePlayerIslandData(newPlayerData).thenApply(playerSaved -> {
                        if (playerSaved) {
                            // 캐시 업데이트
                            islandCache.put(islandId, islandWithLocation);
                            playerIslandMap.put(ownerUuid, islandId);
                            playerDataCache.put(ownerUuid, newPlayerData);
                            
                            LogUtil.info(String.format("새 섬 생성 완료 - 소유자: %s, 섬 이름: %s", 
                                    ownerName, islandName));
                            
                            // 플레이어를 섬으로 텔레포트
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                Location spawnLoc = location.getCenter(worldManager.getIslandWorld());
                                spawnLoc.setY(spawnLoc.getY() + 4); // 약간 위로
                                owner.teleport(spawnLoc);
                            });
                            
                            return islandWithLocation;
                        } else {
                            LogUtil.error("플레이어 섬 데이터 저장 실패: " + ownerUuid);
                            return null;
                        }
                    });
                });
            });
        });
    }
    
    /**
     * 섬 정보 로드
     */
    public CompletableFuture<IslandDTO> loadIsland(@NotNull String islandId) {
        // 캐시 확인
        IslandDTO cached = islandCache.get(islandId);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }
        
        // Firestore에서 로드
        return firestoreService.loadIsland(islandId).thenApply(island -> {
            if (island != null) {
                islandCache.put(islandId, island);
                
                // 플레이어 맵 업데이트
                playerIslandMap.put(island.ownerUuid(), islandId);
                for (IslandMemberDTO member : island.members()) {
                    playerIslandMap.put(member.uuid(), islandId);
                }
            }
            return island;
        });
    }
    
    /**
     * 플레이어의 섬 데이터 로드
     */
    public CompletableFuture<PlayerIslandDataDTO> loadPlayerIslandData(@NotNull String playerUuid) {
        // 캐시 확인
        PlayerIslandDataDTO cached = playerDataCache.get(playerUuid);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }
        
        // Firestore에서 로드
        return firestoreService.loadPlayerIslandData(playerUuid).thenApply(data -> {
            if (data != null) {
                playerDataCache.put(playerUuid, data);
                if (data.currentIslandId() != null) {
                    playerIslandMap.put(playerUuid, data.currentIslandId());
                }
            }
            return data;
        });
    }
    
    /**
     * 플레이어의 섬 가져오기
     */
    public CompletableFuture<IslandDTO> getPlayerIsland(@NotNull String playerUuid) {
        String islandId = playerIslandMap.get(playerUuid);
        if (islandId != null) {
            return loadIsland(islandId);
        }
        
        return loadPlayerIslandData(playerUuid).thenCompose(data -> {
            if (data != null && data.currentIslandId() != null) {
                return loadIsland(data.currentIslandId());
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
            
            // 삭제 가능 여부 확인
            if (!island.canDelete()) {
                LogUtil.warning("섬 삭제 불가 - 생성 후 1주일이 지나지 않음: " + islandId);
                return CompletableFuture.completedFuture(false);
            }
            
            // 섬에서 중앙 좌표 가져오기
            IslandSpawnPointDTO centerSpawn = island.spawnData().defaultSpawn();
            int centerX = (int) centerSpawn.x();
            int centerZ = (int) centerSpawn.z();
            
            // 물리적 섬 삭제
            return worldManager.deleteIsland(centerX, centerZ, island.size()).thenCompose(v -> {
                // 모든 멤버의 섬 데이터 제거
                List<CompletableFuture<Boolean>> memberUpdates = new ArrayList<>();
                
                // 섬장
                memberUpdates.add(updatePlayerLeaveIsland(island.ownerUuid()));
                
                // 멤버들
                for (IslandMemberDTO member : island.members()) {
                    memberUpdates.add(updatePlayerLeaveIsland(member.uuid()));
                }
                
                // 알바생들
                for (IslandWorkerDTO worker : island.workers()) {
                    memberUpdates.add(updatePlayerLeaveIsland(worker.uuid()));
                }
                
                return CompletableFuture.allOf(memberUpdates.toArray(new CompletableFuture[0]))
                        .thenCompose(v2 -> {
                            // Firestore에서 삭제
                            return firestoreService.deleteIsland(islandId);
                        })
                        .thenApply(deleted -> {
                            if (deleted) {
                                // 캐시 제거
                                islandCache.remove(islandId);
                                LogUtil.info("섬 삭제 완료: " + islandId);
                            }
                            return deleted;
                        });
            });
        });
    }
    
    /**
     * 플레이어가 섬을 떠날 때 데이터 업데이트
     */
    private CompletableFuture<Boolean> updatePlayerLeaveIsland(@NotNull String playerUuid) {
        return loadPlayerIslandData(playerUuid).thenCompose(data -> {
            if (data == null || !data.hasIsland()) {
                return CompletableFuture.completedFuture(true);
            }
            
            PlayerIslandDataDTO updatedData = data.leaveIsland();
            return firestoreService.savePlayerIslandData(updatedData).thenApply(saved -> {
                if (saved) {
                    playerDataCache.put(playerUuid, updatedData);
                    playerIslandMap.remove(playerUuid);
                }
                return saved;
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
            return loadPlayerIslandData(island.ownerUuid()).thenCompose(ownerData -> {
                if (ownerData == null || !ownerData.canResetIsland()) {
                    LogUtil.warning("섬 초기화 불가 - 이미 초기화를 사용함: " + island.ownerName());
                    return CompletableFuture.completedFuture(false);
                }
                
                // 섬 중앙 좌표
                IslandSpawnPointDTO centerSpawn = island.spawnData().defaultSpawn();
                int centerX = (int) centerSpawn.x();
                int centerZ = (int) centerSpawn.z();
                
                // 물리적 섬 초기화
                return worldManager.resetIsland(centerX, centerZ, island.size(), 85).thenCompose(v -> {
                    // 새로운 섬 데이터 생성 (초기 상태로)
                    IslandDTO resetIsland = new IslandDTO(
                            island.islandId(),
                            island.ownerUuid(),
                            island.ownerName(),
                            island.islandName(),
                            85, // 초기 크기로 리셋
                            false, // 비공개로 리셋
                            island.createdAt(), // 생성 시간은 유지
                            System.currentTimeMillis(),
                            List.of(), // 멤버 초기화
                            List.of(), // 알바 초기화
                            Map.of(island.ownerUuid(), 0L), // 기여도 초기화
                            IslandSpawnDTO.createDefault(), // 스폰 초기화
                            IslandUpgradeDTO.createDefault(), // 업그레이드 초기화
                            IslandPermissionDTO.createDefault(), // 권한 초기화
                            List.of(), // 초대 초기화
                            List.of(), // 방문 기록 초기화
                            island.totalResets() + 1,
                            null
                    );
                    
                    // 섬장의 초기화 횟수 증가
                    PlayerIslandDataDTO updatedOwnerData = ownerData.incrementResetCount();
                    
                    // 저장
                    return CompletableFuture.allOf(
                            firestoreService.saveIsland(resetIsland),
                            firestoreService.savePlayerIslandData(updatedOwnerData)
                    ).thenApply(v2 -> {
                        // 캐시 업데이트
                        islandCache.put(islandId, resetIsland);
                        playerDataCache.put(island.ownerUuid(), updatedOwnerData);
                        
                        LogUtil.info("섬 초기화 완료: " + island.islandName());
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
        return firestoreService.loadPublicIslands(limit);
    }
    
    /**
     * 섬 업데이트
     */
    public CompletableFuture<Boolean> updateIsland(@NotNull IslandDTO island) {
        return firestoreService.saveIsland(island).thenApply(saved -> {
            if (saved) {
                islandCache.put(island.islandId(), island);
            }
            return saved;
        });
    }
    
    /**
     * 플레이어가 현재 있는 섬 찾기
     */
    @Nullable
    public IslandDTO getIslandAt(@NotNull Location location) {
        if (!worldManager.isIslandWorld(location.getWorld())) {
            return null;
        }
        
        // 캐시된 모든 섬 확인
        for (IslandDTO island : islandCache.values()) {
            IslandSpawnPointDTO center = island.spawnData().defaultSpawn();
            IslandLocationDTO islandLoc = new IslandLocationDTO(
                    (int) center.x(),
                    (int) center.z(),
                    island.size()
            );
            
            if (islandLoc.contains(location)) {
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
        islandCache.clear();
        playerIslandMap.clear();
        playerDataCache.clear();
    }
}