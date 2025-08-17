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
            IslandDTO islandWithLocation = IslandDTO.fromFields(
                    baseIsland.core().islandId(),
                    baseIsland.core().ownerUuid(),
                    baseIsland.core().ownerName(),
                    baseIsland.core().islandName(),
                    baseIsland.core().size(),
                    baseIsland.core().isPublic(),
                    baseIsland.core().createdAt(),
                    baseIsland.core().lastActivity(),
                    baseIsland.membership().members(),
                    baseIsland.membership().workers(),
                    baseIsland.membership().contributions(),
                    new IslandSpawnDTO(
                            IslandSpawnPointDTO.fromLocation(
                                    location.getCenter(worldManager.getIslandWorld()),
                                    "섬 중앙"
                            ),
                            null,
                            List.of(),
                            Map.of()
                    ),
                    baseIsland.configuration().upgradeData(),
                    baseIsland.configuration().permissions(),
                    baseIsland.social().pendingInvites(),
                    baseIsland.social().recentVisits(),
                    baseIsland.core().totalResets(),
                    baseIsland.core().deletionScheduledAt(),
                    baseIsland.configuration().settings()
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
            long remainingTime = (islandData.core().createdAt() + (7 * 24 * 60 * 60 * 1000L)) - System.currentTimeMillis();
            long remainingDays = remainingTime / (24 * 60 * 60 * 1000L);
            long remainingHours = (remainingTime % (24 * 60 * 60 * 1000L)) / (60 * 60 * 1000L);
            
            LogUtil.warning("섬 삭제 실패 - 생성 후 7일이 지나지 않음: " + islandData.core().islandId() + 
                          " (남은 시간: " + remainingDays + "일 " + remainingHours + "시간)");
            return CompletableFuture.completedFuture(false);
        }
        
        // 섬에서 중앙 좌표 가져오기
        IslandSpawnPointDTO centerSpawn = islandData.configuration().spawnData().defaultSpawn();
        int centerX = (int) centerSpawn.x();
        int centerZ = (int) centerSpawn.z();
        
        LogUtil.debug("물리적 섬 삭제 요청 - ID: " + islandData.core().islandId() + 
                     ", 좌표: (" + centerX + ", " + centerZ + "), 크기: " + islandData.core().size());
        
        // 물리적 섬 삭제
        return worldManager.deleteIsland(centerX, centerZ, islandData.core().size())
                .thenApply(v -> {
                    LogUtil.debug("물리적 섬 삭제 완료: " + islandData.core().islandId());
                    return true;
                })
                .exceptionally(ex -> {
                    LogUtil.error("물리적 섬 삭제 중 예외 발생: " + islandData.core().islandId(), ex);
                    return false;
                });
    }
    
    /**
     * 섬 초기화
     */
    public CompletableFuture<Boolean> reset() {
        // 섬 중앙 좌표
        IslandSpawnPointDTO centerSpawn = islandData.configuration().spawnData().defaultSpawn();
        int centerX = (int) centerSpawn.x();
        int centerZ = (int) centerSpawn.z();
        
        // 물리적 섬 초기화
        return worldManager.resetIsland(centerX, centerZ, islandData.core().size(), 85)
                .thenApply(v -> {
                    // 새로운 섬 데이터 생성 (초기 상태로)
                    islandData = IslandDTO.fromFields(
                            islandData.core().islandId(),
                            islandData.core().ownerUuid(),
                            islandData.core().ownerName(),
                            islandData.core().islandName(),
                            85, // 초기 크기로 리셋
                            false, // 비공개로 리셋
                            islandData.core().createdAt(), // 생성 시간은 유지
                            System.currentTimeMillis(),
                            List.of(), // 멤버 초기화
                            List.of(), // 알바 초기화
                            Map.of(islandData.core().ownerUuid(), 0L), // 기여도 초기화
                            IslandSpawnDTO.createDefault(), // 스폰 초기화
                            IslandUpgradeDTO.createDefault(), // 업그레이드 초기화
                            IslandPermissionDTO.createDefault(), // 권한 초기화
                            List.of(), // 초대 초기화
                            List.of(), // 방문 기록 초기화
                            islandData.core().totalResets() + 1,
                            null,
                            islandData.configuration().settings() // 설정 유지
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
        
        IslandSpawnPointDTO center = islandData.configuration().spawnData().defaultSpawn();
        IslandLocationDTO islandLoc = new IslandLocationDTO(
                (int) center.x(),
                (int) center.z(),
                islandData.core().size()
        );
        
        return islandLoc.contains(location);
    }
    
    /**
     * 섬 스폰 위치 가져오기
     */
    public Location getSpawnLocation() {
        World world = worldManager.getIslandWorld();
        IslandSpawnPointDTO spawn = islandData.configuration().spawnData().defaultSpawn();
        
        Location loc = new Location(world, spawn.x(), spawn.y(), spawn.z(), spawn.yaw(), spawn.pitch());
        loc.setY(loc.getY() + 4); // 약간 위로
        
        return loc;
    }
    
    /**
     * 멤버 추가
     */
    public void addMember(@NotNull String playerUuid, @NotNull String playerName, @NotNull IslandRole role) {
        List<IslandMemberDTO> currentMembers = new java.util.ArrayList<>(islandData.membership().members());
        
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
        Map<String, Long> contributions = new java.util.HashMap<>(islandData.membership().contributions());
        contributions.putIfAbsent(playerUuid, 0L);
        
        // 새로운 섬 데이터 생성
        islandData = IslandDTO.fromFields(
                islandData.core().islandId(),
                islandData.core().ownerUuid(),
                islandData.core().ownerName(),
                islandData.core().islandName(),
                islandData.core().size(),
                islandData.core().isPublic(),
                islandData.core().createdAt(),
                System.currentTimeMillis(),
                currentMembers,
                islandData.membership().workers(),
                contributions,
                islandData.configuration().spawnData(),
                islandData.configuration().upgradeData(),
                islandData.configuration().permissions(),
                islandData.social().pendingInvites(),
                islandData.social().recentVisits(),
                islandData.core().totalResets(),
                islandData.core().deletionScheduledAt(),
                islandData.configuration().settings()
        );
    }
    
    /**
     * 멤버 제거
     */
    public void removeMember(@NotNull String playerUuid) {
        List<IslandMemberDTO> currentMembers = new java.util.ArrayList<>(islandData.membership().members());
        
        // 멤버 제거
        currentMembers.removeIf(m -> m.uuid().equals(playerUuid));
        
        // 알바 목록에서도 제거
        List<IslandWorkerDTO> currentWorkers = new java.util.ArrayList<>(islandData.membership().workers());
        currentWorkers.removeIf(w -> w.uuid().equals(playerUuid));
        
        // 기여도는 유지 (기록 보존)
        
        // 새로운 섬 데이터 생성
        islandData = IslandDTO.fromFields(
                islandData.core().islandId(),
                islandData.core().ownerUuid(),
                islandData.core().ownerName(),
                islandData.core().islandName(),
                islandData.core().size(),
                islandData.core().isPublic(),
                islandData.core().createdAt(),
                System.currentTimeMillis(),
                currentMembers,
                currentWorkers,
                islandData.membership().contributions(),
                islandData.configuration().spawnData(),
                islandData.configuration().upgradeData(),
                islandData.configuration().permissions(),
                islandData.social().pendingInvites(),
                islandData.social().recentVisits(),
                islandData.core().totalResets(),
                islandData.core().deletionScheduledAt(),
                islandData.configuration().settings()
        );
    }
    
    /**
     * 모든 구성원 UUID 목록 (소유자 + 멤버 + 알바)
     */
    public List<String> getAllMemberUuids() {
        List<String> uuids = new java.util.ArrayList<>();
        uuids.add(islandData.core().ownerUuid());
        
        for (IslandMemberDTO member : islandData.membership().members()) {
            uuids.add(member.uuid());
        }
        
        for (IslandWorkerDTO worker : islandData.membership().workers()) {
            uuids.add(worker.uuid());
        }
        
        return uuids;
    }
    
    // Getters
    public String getId() { return islandData.core().islandId(); }
    public String getOwnerUuid() { return islandData.core().ownerUuid(); }
    public String getOwnerName() { return islandData.core().ownerName(); }
    public String getName() { return islandData.core().islandName(); }
    public int getSize() { return islandData.core().size(); }
    public boolean isPublic() { return islandData.core().isPublic(); }
    public IslandDTO getData() { return islandData; }
    
    /**
     * 섬 이름 색상 반환
     */
    public String getNameColorHex() {
        return islandData.configuration().settings().nameColorHex();
    }
    
    /**
     * 섬 바이옴 반환
     */
    public String getBiome() {
        return islandData.configuration().settings().biome();
    }
    
    /**
     * 섬 설정 반환
     */
    public IslandSettingsDTO getSettings() {
        return islandData.configuration().settings();
    }
    
    // Setters
    public void setData(@NotNull IslandDTO data) { this.islandData = data; }
    public void setPublic(boolean isPublic) {
        islandData = IslandDTO.fromFields(
                islandData.core().islandId(),
                islandData.core().ownerUuid(),
                islandData.core().ownerName(),
                islandData.core().islandName(),
                islandData.core().size(),
                isPublic,
                islandData.core().createdAt(),
                System.currentTimeMillis(),
                islandData.membership().members(),
                islandData.membership().workers(),
                islandData.membership().contributions(),
                islandData.configuration().spawnData(),
                islandData.configuration().upgradeData(),
                islandData.configuration().permissions(),
                islandData.social().pendingInvites(),
                islandData.social().recentVisits(),
                islandData.core().totalResets(),
                islandData.core().deletionScheduledAt(),
                islandData.configuration().settings()
        );
    }
}