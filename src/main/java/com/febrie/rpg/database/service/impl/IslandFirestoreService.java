package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.BaseFirestoreService;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 섬 데이터 Firestore 서비스
 * islands 컬렉션 관리
 *
 * @author Febrie, CoffeeTory
 */
public class IslandFirestoreService extends BaseFirestoreService<IslandDTO> {
    
    private static final String COLLECTION_NAME = "islands";
    
    public IslandFirestoreService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        super(plugin, firestore, COLLECTION_NAME, IslandDTO.class);
    }
    
    @Override
    protected Map<String, Object> toMap(@NotNull IslandDTO dto) {
        Map<String, Object> map = new HashMap<>();
        
        // 기본 정보
        map.put("islandId", dto.islandId());
        map.put("ownerUuid", dto.ownerUuid());
        map.put("ownerName", dto.ownerName());
        map.put("islandName", dto.islandName());
        map.put("size", dto.size());
        map.put("isPublic", dto.isPublic());
        map.put("createdAt", dto.createdAt());
        map.put("lastActivity", dto.lastActivity());
        
        // 멤버 목록
        List<Map<String, Object>> membersList = dto.members().stream()
                .map(this::memberToMap)
                .collect(Collectors.toList());
        map.put("members", membersList);
        
        // 알바생 목록
        List<Map<String, Object>> workersList = dto.workers().stream()
                .map(this::workerToMap)
                .collect(Collectors.toList());
        map.put("workers", workersList);
        
        // 기여도
        map.put("contributions", dto.contributions());
        
        // 스폰 데이터
        map.put("spawnData", spawnDataToMap(dto.spawnData()));
        
        // 업그레이드 데이터
        map.put("upgradeData", upgradeDataToMap(dto.upgradeData()));
        
        // 권한 설정
        map.put("permissions", permissionsToMap(dto.permissions()));
        
        // 대기중인 초대
        List<Map<String, Object>> invitesList = dto.pendingInvites().stream()
                .map(this::inviteToMap)
                .collect(Collectors.toList());
        map.put("pendingInvites", invitesList);
        
        // 최근 방문 기록
        List<Map<String, Object>> visitsList = dto.recentVisits().stream()
                .map(this::visitToMap)
                .collect(Collectors.toList());
        map.put("recentVisits", visitsList);
        
        map.put("totalResets", dto.totalResets());
        
        if (dto.deletionScheduledAt() != null) {
            map.put("deletionScheduledAt", dto.deletionScheduledAt());
        }
        
        return map;
    }
    
    @Override
    @Nullable
    protected IslandDTO fromDocument(@NotNull DocumentSnapshot document) {
        if (!document.exists()) {
            return null;
        }
        
        try {
            // 기본 정보
            String islandId = document.getString("islandId");
            String ownerUuid = document.getString("ownerUuid");
            String ownerName = document.getString("ownerName");
            String islandName = document.getString("islandName");
            int size = document.getLong("size").intValue();
            boolean isPublic = Boolean.TRUE.equals(document.getBoolean("isPublic"));
            long createdAt = document.getLong("createdAt");
            long lastActivity = document.getLong("lastActivity");
            
            // 멤버 목록
            List<IslandMemberDTO> members = new ArrayList<>();
            List<Map<String, Object>> membersList = (List<Map<String, Object>>) document.get("members");
            if (membersList != null) {
                members = membersList.stream()
                        .map(this::memberFromMap)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
            
            // 알바생 목록
            List<IslandWorkerDTO> workers = new ArrayList<>();
            List<Map<String, Object>> workersList = (List<Map<String, Object>>) document.get("workers");
            if (workersList != null) {
                workers = workersList.stream()
                        .map(this::workerFromMap)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
            
            // 기여도
            Map<String, Long> contributions = new HashMap<>();
            Map<String, Object> contributionsData = document.get("contributions", Map.class);
            if (contributionsData != null) {
                contributionsData.forEach((key, value) -> {
                    contributions.put(key, ((Number) value).longValue());
                });
            }
            
            // 스폰 데이터
            IslandSpawnDTO spawnData = spawnDataFromMap(document.get("spawnData", Map.class));
            
            // 업그레이드 데이터
            IslandUpgradeDTO upgradeData = upgradeDataFromMap(document.get("upgradeData", Map.class));
            
            // 권한 설정
            IslandPermissionDTO permissions = permissionsFromMap(document.get("permissions", Map.class));
            
            // 대기중인 초대
            List<IslandInviteDTO> pendingInvites = new ArrayList<>();
            List<Map<String, Object>> invitesList = (List<Map<String, Object>>) document.get("pendingInvites");
            if (invitesList != null) {
                pendingInvites = invitesList.stream()
                        .map(this::inviteFromMap)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
            
            // 최근 방문 기록
            List<IslandVisitDTO> recentVisits = new ArrayList<>();
            List<Map<String, Object>> visitsList = (List<Map<String, Object>>) document.get("recentVisits");
            if (visitsList != null) {
                recentVisits = visitsList.stream()
                        .map(this::visitFromMap)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
            
            int totalResets = document.getLong("totalResets").intValue();
            Long deletionScheduledAt = document.getLong("deletionScheduledAt");
            
            return new IslandDTO(islandId, ownerUuid, ownerName, islandName, size, isPublic,
                    createdAt, lastActivity, members, workers, contributions, spawnData,
                    upgradeData, permissions, pendingInvites, recentVisits, totalResets, deletionScheduledAt);
            
        } catch (Exception e) {
            LogUtil.warning("섬 데이터 파싱 실패 [" + document.getId() + "]: " + e.getMessage());
            return null;
        }
    }
    
    // ===== 헬퍼 메소드들 =====
    
    private Map<String, Object> memberToMap(@NotNull IslandMemberDTO member) {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", member.uuid());
        map.put("name", member.name());
        map.put("isCoOwner", member.isCoOwner());
        map.put("joinedAt", member.joinedAt());
        map.put("lastActivity", member.lastActivity());
        return map;
    }
    
    private IslandMemberDTO memberFromMap(@NotNull Map<String, Object> map) {
        return new IslandMemberDTO(
                (String) map.get("uuid"),
                (String) map.get("name"),
                (Boolean) map.get("isCoOwner"),
                ((Number) map.get("joinedAt")).longValue(),
                ((Number) map.get("lastActivity")).longValue()
        );
    }
    
    private Map<String, Object> workerToMap(@NotNull IslandWorkerDTO worker) {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", worker.uuid());
        map.put("name", worker.name());
        map.put("hiredAt", worker.hiredAt());
        map.put("lastActivity", worker.lastActivity());
        return map;
    }
    
    private IslandWorkerDTO workerFromMap(@NotNull Map<String, Object> map) {
        return new IslandWorkerDTO(
                (String) map.get("uuid"),
                (String) map.get("name"),
                ((Number) map.get("hiredAt")).longValue(),
                ((Number) map.get("lastActivity")).longValue()
        );
    }
    
    private Map<String, Object> spawnDataToMap(@NotNull IslandSpawnDTO spawn) {
        Map<String, Object> map = new HashMap<>();
        
        // Default spawn
        map.put("defaultSpawn", spawnPointToMap(spawn.defaultSpawn()));
        
        // Owner spawns
        List<Map<String, Object>> ownerSpawnsList = spawn.ownerSpawns().stream()
                .map(this::spawnPointToMap)
                .collect(Collectors.toList());
        map.put("ownerSpawns", ownerSpawnsList);
        
        // Member spawns
        Map<String, Map<String, Object>> memberSpawnsMap = new HashMap<>();
        spawn.memberSpawns().forEach((uuid, point) -> {
            memberSpawnsMap.put(uuid, spawnPointToMap(point));
        });
        map.put("memberSpawns", memberSpawnsMap);
        
        map.put("lastUpdated", spawn.lastUpdated());
        
        return map;
    }
    
    private IslandSpawnDTO spawnDataFromMap(@Nullable Map<String, Object> map) {
        if (map == null) {
            return IslandSpawnDTO.createDefault();
        }
        
        // Default spawn
        IslandSpawnPointDTO defaultSpawn = spawnPointFromMap((Map<String, Object>) map.get("defaultSpawn"));
        
        // Owner spawns
        List<IslandSpawnPointDTO> ownerSpawns = new ArrayList<>();
        List<Map<String, Object>> ownerSpawnsList = (List<Map<String, Object>>) map.get("ownerSpawns");
        if (ownerSpawnsList != null) {
            ownerSpawns = ownerSpawnsList.stream()
                    .map(this::spawnPointFromMap)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        
        // Member spawns
        Map<String, IslandSpawnPointDTO> memberSpawns = new HashMap<>();
        Map<String, Map<String, Object>> memberSpawnsData = (Map<String, Map<String, Object>>) map.get("memberSpawns");
        if (memberSpawnsData != null) {
            memberSpawnsData.forEach((uuid, pointData) -> {
                IslandSpawnPointDTO point = spawnPointFromMap(pointData);
                if (point != null) {
                    memberSpawns.put(uuid, point);
                }
            });
        }
        
        long lastUpdated = ((Number) map.getOrDefault("lastUpdated", System.currentTimeMillis())).longValue();
        
        return new IslandSpawnDTO(defaultSpawn, ownerSpawns, memberSpawns, lastUpdated);
    }
    
    private Map<String, Object> spawnPointToMap(@NotNull IslandSpawnPointDTO point) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", point.name());
        map.put("worldName", point.worldName());
        map.put("x", point.x());
        map.put("y", point.y());
        map.put("z", point.z());
        map.put("yaw", point.yaw());
        map.put("pitch", point.pitch());
        return map;
    }
    
    private IslandSpawnPointDTO spawnPointFromMap(@Nullable Map<String, Object> map) {
        if (map == null) return null;
        
        return new IslandSpawnPointDTO(
                (String) map.get("name"),
                (String) map.get("worldName"),
                ((Number) map.get("x")).doubleValue(),
                ((Number) map.get("y")).doubleValue(),
                ((Number) map.get("z")).doubleValue(),
                ((Number) map.get("yaw")).floatValue(),
                ((Number) map.get("pitch")).floatValue()
        );
    }
    
    private Map<String, Object> upgradeDataToMap(@NotNull IslandUpgradeDTO upgrade) {
        Map<String, Object> map = new HashMap<>();
        map.put("currentSize", upgrade.currentSize());
        map.put("maxSize", upgrade.maxSize());
        map.put("sizeUpgrades", upgrade.sizeUpgrades());
        map.put("memberSlots", upgrade.memberSlots());
        map.put("workerSlots", upgrade.workerSlots());
        map.put("spawnSlots", upgrade.spawnSlots());
        map.put("lastUpgraded", upgrade.lastUpgraded());
        return map;
    }
    
    private IslandUpgradeDTO upgradeDataFromMap(@Nullable Map<String, Object> map) {
        if (map == null) {
            return IslandUpgradeDTO.createDefault();
        }
        
        return new IslandUpgradeDTO(
                ((Number) map.get("currentSize")).intValue(),
                ((Number) map.get("maxSize")).intValue(),
                ((Number) map.get("sizeUpgrades")).intValue(),
                ((Number) map.get("memberSlots")).intValue(),
                ((Number) map.get("workerSlots")).intValue(),
                ((Number) map.get("spawnSlots")).intValue(),
                ((Number) map.get("lastUpgraded")).longValue()
        );
    }
    
    private Map<String, Object> permissionsToMap(@NotNull IslandPermissionDTO permissions) {
        Map<String, Object> map = new HashMap<>();
        
        // Role permissions
        Map<String, Map<String, Object>> rolePermsMap = new HashMap<>();
        permissions.rolePermissions().forEach((role, perms) -> {
            Map<String, Object> permsData = new HashMap<>();
            permsData.put("canBuild", perms.canBuild());
            permsData.put("canBreak", perms.canBreak());
            permsData.put("canInteract", perms.canInteract());
            permsData.put("canInvite", perms.canInvite());
            permsData.put("canKick", perms.canKick());
            permsData.put("canSetSpawn", perms.canSetSpawn());
            permsData.put("canManageWorkers", perms.canManageWorkers());
            permsData.put("canManagePermissions", perms.canManagePermissions());
            permsData.put("canUpgrade", perms.canUpgrade());
            permsData.put("canReset", perms.canReset());
            rolePermsMap.put(role.name(), permsData);
        });
        map.put("rolePermissions", rolePermsMap);
        
        map.put("lastUpdated", permissions.lastUpdated());
        
        return map;
    }
    
    private IslandPermissionDTO permissionsFromMap(@Nullable Map<String, Object> map) {
        if (map == null) {
            return IslandPermissionDTO.createDefault();
        }
        
        Map<IslandRole, IslandPermissionDTO.RolePermissions> rolePermissions = new HashMap<>();
        Map<String, Map<String, Object>> rolePermsData = 
                (Map<String, Map<String, Object>>) map.get("rolePermissions");
        
        if (rolePermsData != null) {
            rolePermsData.forEach((roleName, permsData) -> {
                IslandRole role = IslandRole.valueOf(roleName);
                IslandPermissionDTO.RolePermissions perms = new IslandPermissionDTO.RolePermissions(
                        (Boolean) permsData.get("canBuild"),
                        (Boolean) permsData.get("canBreak"),
                        (Boolean) permsData.get("canInteract"),
                        (Boolean) permsData.get("canInvite"),
                        (Boolean) permsData.get("canKick"),
                        (Boolean) permsData.get("canSetSpawn"),
                        (Boolean) permsData.get("canManageWorkers"),
                        (Boolean) permsData.get("canManagePermissions"),
                        (Boolean) permsData.get("canUpgrade"),
                        (Boolean) permsData.get("canReset")
                );
                rolePermissions.put(role, perms);
            });
        }
        
        long lastUpdated = ((Number) map.getOrDefault("lastUpdated", System.currentTimeMillis())).longValue();
        
        return new IslandPermissionDTO(rolePermissions, lastUpdated);
    }
    
    private Map<String, Object> inviteToMap(@NotNull IslandInviteDTO invite) {
        Map<String, Object> map = new HashMap<>();
        map.put("inviteId", invite.inviteId());
        map.put("islandId", invite.islandId());
        map.put("inviterUuid", invite.inviterUuid());
        map.put("inviterName", invite.inviterName());
        map.put("invitedUuid", invite.invitedUuid());
        map.put("invitedName", invite.invitedName());
        map.put("invitedAt", invite.invitedAt());
        map.put("expiresAt", invite.expiresAt());
        map.put("message", invite.message());
        return map;
    }
    
    private IslandInviteDTO inviteFromMap(@NotNull Map<String, Object> map) {
        return new IslandInviteDTO(
                (String) map.get("inviteId"),
                (String) map.get("islandId"),
                (String) map.get("inviterUuid"),
                (String) map.get("inviterName"),
                (String) map.get("invitedUuid"),
                (String) map.get("invitedName"),
                ((Number) map.get("invitedAt")).longValue(),
                ((Number) map.get("expiresAt")).longValue(),
                (String) map.get("message")
        );
    }
    
    private Map<String, Object> visitToMap(@NotNull IslandVisitDTO visit) {
        Map<String, Object> map = new HashMap<>();
        map.put("visitorUuid", visit.visitorUuid());
        map.put("visitorName", visit.visitorName());
        map.put("visitedAt", visit.visitedAt());
        map.put("duration", visit.duration());
        return map;
    }
    
    private IslandVisitDTO visitFromMap(@NotNull Map<String, Object> map) {
        return new IslandVisitDTO(
                (String) map.get("visitorUuid"),
                (String) map.get("visitorName"),
                ((Number) map.get("visitedAt")).longValue(),
                ((Number) map.get("duration")).longValue()
        );
    }
    
    // ===== 추가 기능 메소드들 =====
    
    /**
     * 플레이어가 소유한 섬 찾기
     */
    @NotNull
    public CompletableFuture<IslandDTO> findByOwner(@NotNull String ownerUuid) {
        return query("ownerUuid", ownerUuid).thenApply(islands -> 
                islands.isEmpty() ? null : islands.get(0));
    }
    
    /**
     * 공개 섬 목록 조회
     */
    @NotNull
    public CompletableFuture<List<IslandDTO>> getPublicIslands() {
        return query("isPublic", true);
    }
    
    /**
     * 섬 이름으로 검색 (부분 일치)
     * 참고: Firestore는 부분 문자열 검색을 네이티브로 지원하지 않으므로
     * 전체 목록을 가져온 후 필터링
     */
    @NotNull
    public CompletableFuture<List<IslandDTO>> searchByName(@NotNull String namePart) {
        String lowerSearch = namePart.toLowerCase();
        
        return getPublicIslands().thenApply(islands -> 
                islands.stream()
                        .filter(island -> island.islandName().toLowerCase().contains(lowerSearch))
                        .collect(Collectors.toList()));
    }
}