package com.febrie.rpg.island;

import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.island.world.IslandWorldManager;
import com.febrie.rpg.util.LogUtil;
import com.febrie.rpg.util.LangManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 섬 엔티티 클래스
 * 섬의 핵심 로직과 데이터를 관리
 *
 * @author Febrie, CoffeeTory
 */
public class Island {
    
    private final IslandWorldManager worldManager;
    private IslandDTO islandData;
    
    /**
     * 기존 섬 로드
     */
    public Island(@NotNull IslandWorldManager worldManager, @NotNull IslandDTO islandData) {
        this.worldManager = worldManager;
        this.islandData = islandData;
    }
    
    /**
     * 새 섬 생성
     */
    public static CompletableFuture<Island> create(
            @NotNull IslandWorldManager worldManager,
            @NotNull String ownerUuid,
            @NotNull String ownerName,
            @NotNull String islandName) {
        
        String islandId = UUID.randomUUID().toString();
        
        // 물리적 섬 생성
        return worldManager.createNewIsland(85).thenApply(location -> {
            // 섬 DTO 생성
            IslandDTO baseIsland = IslandDTO.createNew(islandId, ownerUuid, ownerName, islandName);
            
            // 위치 정보를 포함한 새 섬 DTO 생성
            IslandDTO islandWithLocation = new IslandDTO(
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
                            IslandSpawnPointDTO.fromLocation(
                                    location.getCenter(worldManager.getIslandWorld()),
                                    "섬 중앙"
                            ),
                            null,
                            List.of(),
                            Map.of()
                    ),
                    baseIsland.upgradeData(),
                    baseIsland.permissions(),
                    baseIsland.pendingInvites(),
                    baseIsland.recentVisits(),
                    baseIsland.totalResets(),
                    baseIsland.deletionScheduledAt(),
                    baseIsland.settings()
            );
            
            
            return new Island(worldManager, islandWithLocation);
        });
    }
    
    /**
     * 섬 삭제
     */
    public CompletableFuture<Boolean> delete() {
        // 삭제 가능 여부 확인
        if (!islandData.canDelete()) {
            return CompletableFuture.completedFuture(false);
        }
        
        // 섬에서 중앙 좌표 가져오기
        IslandSpawnPointDTO centerSpawn = islandData.spawnData().defaultSpawn();
        int centerX = (int) centerSpawn.x();
        int centerZ = (int) centerSpawn.z();
        
        // 물리적 섬 삭제
        return worldManager.deleteIsland(centerX, centerZ, islandData.size())
                .thenApply(v -> {
                    return true;
                });
    }
    
    /**
     * 섬 초기화
     */
    public CompletableFuture<Boolean> reset() {
        // 섬 중앙 좌표
        IslandSpawnPointDTO centerSpawn = islandData.spawnData().defaultSpawn();
        int centerX = (int) centerSpawn.x();
        int centerZ = (int) centerSpawn.z();
        
        // 물리적 섬 초기화
        return worldManager.resetIsland(centerX, centerZ, islandData.size(), 85)
                .thenApply(v -> {
                    // 새로운 섬 데이터 생성 (초기 상태로)
                    islandData = new IslandDTO(
                            islandData.islandId(),
                            islandData.ownerUuid(),
                            islandData.ownerName(),
                            islandData.islandName(),
                            85, // 초기 크기로 리셋
                            false, // 비공개로 리셋
                            islandData.createdAt(), // 생성 시간은 유지
                            System.currentTimeMillis(),
                            List.of(), // 멤버 초기화
                            List.of(), // 알바 초기화
                            Map.of(islandData.ownerUuid(), 0L), // 기여도 초기화
                            IslandSpawnDTO.createDefault(), // 스폰 초기화
                            IslandUpgradeDTO.createDefault(), // 업그레이드 초기화
                            IslandPermissionDTO.createDefault(), // 권한 초기화
                            List.of(), // 초대 초기화
                            List.of(), // 방문 기록 초기화
                            islandData.totalResets() + 1,
                            null,
                            islandData.settings() // 설정 유지
                    );
                    
                    return true;
                });
    }
    
    /**
     * 위치가 이 섬 내부인지 확인
     */
    public boolean contains(@NotNull Location location) {
        if (!worldManager.isIslandWorld(location.getWorld())) {
            return false;
        }
        
        IslandSpawnPointDTO center = islandData.spawnData().defaultSpawn();
        IslandLocationDTO islandLoc = new IslandLocationDTO(
                (int) center.x(),
                (int) center.z(),
                islandData.size()
        );
        
        return islandLoc.contains(location);
    }
    
    /**
     * 섬 스폰 위치 가져오기
     */
    public Location getSpawnLocation() {
        World world = worldManager.getIslandWorld();
        IslandSpawnPointDTO spawn = islandData.spawnData().defaultSpawn();
        
        Location loc = new Location(world, spawn.x(), spawn.y(), spawn.z(), spawn.yaw(), spawn.pitch());
        loc.setY(loc.getY() + 4); // 약간 위로
        
        return loc;
    }
    
    /**
     * 멤버 추가
     */
    public void addMember(@NotNull String playerUuid, @NotNull String playerName, @NotNull IslandRole role) {
        List<IslandMemberDTO> currentMembers = new java.util.ArrayList<>(islandData.members());
        
        // 이미 멤버인지 확인
        boolean isMember = currentMembers.stream()
                .anyMatch(m -> m.uuid().equals(playerUuid));
        if (isMember) {
            return;
        }
        
        // 새 멤버 추가
        IslandMemberDTO newMember = IslandMemberDTO.createNew(
                playerUuid,
                playerName,
                role == IslandRole.CO_OWNER
        );
        currentMembers.add(newMember);
        
        // 기여도 맵에도 추가
        Map<String, Long> contributions = new java.util.HashMap<>(islandData.contributions());
        contributions.putIfAbsent(playerUuid, 0L);
        
        // 새로운 섬 데이터 생성
        islandData = new IslandDTO(
                islandData.islandId(),
                islandData.ownerUuid(),
                islandData.ownerName(),
                islandData.islandName(),
                islandData.size(),
                islandData.isPublic(),
                islandData.createdAt(),
                System.currentTimeMillis(),
                currentMembers,
                islandData.workers(),
                contributions,
                islandData.spawnData(),
                islandData.upgradeData(),
                islandData.permissions(),
                islandData.pendingInvites(),
                islandData.recentVisits(),
                islandData.totalResets(),
                islandData.deletionScheduledAt(),
                islandData.settings()
        );
    }
    
    /**
     * 멤버 제거
     */
    public void removeMember(@NotNull String playerUuid) {
        List<IslandMemberDTO> currentMembers = new java.util.ArrayList<>(islandData.members());
        
        // 멤버 제거
        currentMembers.removeIf(m -> m.uuid().equals(playerUuid));
        
        // 알바 목록에서도 제거
        List<IslandWorkerDTO> currentWorkers = new java.util.ArrayList<>(islandData.workers());
        currentWorkers.removeIf(w -> w.uuid().equals(playerUuid));
        
        // 기여도는 유지 (기록 보존)
        
        // 새로운 섬 데이터 생성
        islandData = new IslandDTO(
                islandData.islandId(),
                islandData.ownerUuid(),
                islandData.ownerName(),
                islandData.islandName(),
                islandData.size(),
                islandData.isPublic(),
                islandData.createdAt(),
                System.currentTimeMillis(),
                currentMembers,
                currentWorkers,
                islandData.contributions(),
                islandData.spawnData(),
                islandData.upgradeData(),
                islandData.permissions(),
                islandData.pendingInvites(),
                islandData.recentVisits(),
                islandData.totalResets(),
                islandData.deletionScheduledAt(),
                islandData.settings()
        );
    }
    
    /**
     * 모든 구성원 UUID 목록 (소유자 + 멤버 + 알바)
     */
    public List<String> getAllMemberUuids() {
        List<String> uuids = new java.util.ArrayList<>();
        uuids.add(islandData.ownerUuid());
        
        for (IslandMemberDTO member : islandData.members()) {
            uuids.add(member.uuid());
        }
        
        for (IslandWorkerDTO worker : islandData.workers()) {
            uuids.add(worker.uuid());
        }
        
        return uuids;
    }
    
    // Getters
    public String getId() { return islandData.islandId(); }
    public String getOwnerUuid() { return islandData.ownerUuid(); }
    public String getOwnerName() { return islandData.ownerName(); }
    public String getName() { return islandData.islandName(); }
    public int getSize() { return islandData.size(); }
    public boolean isPublic() { return islandData.isPublic(); }
    public IslandDTO getData() { return islandData; }
    
    /**
     * 섬 이름 색상 반환
     */
    public String getNameColorHex() {
        return islandData.settings().nameColorHex();
    }
    
    /**
     * 섬 바이옴 반환
     */
    public String getBiome() {
        return islandData.settings().biome();
    }
    
    /**
     * 섬 설정 반환
     */
    public IslandSettingsDTO getSettings() {
        return islandData.settings();
    }
    
    // Setters
    public void setData(@NotNull IslandDTO data) { this.islandData = data; }
    public void setPublic(boolean isPublic) {
        islandData = new IslandDTO(
                islandData.islandId(),
                islandData.ownerUuid(),
                islandData.ownerName(),
                islandData.islandName(),
                islandData.size(),
                isPublic,
                islandData.createdAt(),
                System.currentTimeMillis(),
                islandData.members(),
                islandData.workers(),
                islandData.contributions(),
                islandData.spawnData(),
                islandData.upgradeData(),
                islandData.permissions(),
                islandData.pendingInvites(),
                islandData.recentVisits(),
                islandData.totalResets(),
                islandData.deletionScheduledAt(),
                islandData.settings()
        );
    }
}