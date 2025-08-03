package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 섬 권한 설정 DTO (Record)
 *
 * @author Febrie, CoffeeTory
 */
public record IslandPermissionDTO(
        @NotNull Map<IslandRole, RolePermissions> rolePermissions,
        long lastUpdated
) {
    /**
     * 기본 생성자
     */
    public IslandPermissionDTO(@NotNull Map<IslandRole, RolePermissions> rolePermissions) {
        this(rolePermissions, System.currentTimeMillis());
    }
    
    /**
     * 기본 권한 설정 생성
     */
    public static IslandPermissionDTO createDefault() {
        Map<IslandRole, RolePermissions> permissions = new HashMap<>();
        
        // 섬장은 모든 권한
        permissions.put(IslandRole.OWNER, RolePermissions.all());
        
        // 부섬장 기본 권한
        permissions.put(IslandRole.CO_OWNER, new RolePermissions(
                true,  // canBuild
                true,  // canBreak
                true,  // canInteract
                true,  // canInvite
                false, // canKick
                true,  // canSetSpawn
                true,  // canManageWorkers
                false, // canManagePermissions
                false, // canUpgrade
                false  // canReset
        ));
        
        // 일반 섬원 기본 권한
        permissions.put(IslandRole.MEMBER, new RolePermissions(
                true,  // canBuild
                true,  // canBreak
                true,  // canInteract
                false, // canInvite
                false, // canKick
                false, // canSetSpawn
                false, // canManageWorkers
                false, // canManagePermissions
                false, // canUpgrade
                false  // canReset
        ));
        
        // 알바생 기본 권한
        permissions.put(IslandRole.WORKER, new RolePermissions(
                true,  // canBuild
                true,  // canBreak
                false, // canInteract
                false, // canInvite
                false, // canKick
                false, // canSetSpawn
                false, // canManageWorkers
                false, // canManagePermissions
                false, // canUpgrade
                false  // canReset
        ));
        
        // 방문자 기본 권한 (모두 비활성화)
        permissions.put(IslandRole.VISITOR, RolePermissions.none());
        
        return new IslandPermissionDTO(permissions);
    }
    
    /**
     * 특정 역할의 권한 가져오기
     */
    public RolePermissions getPermissions(IslandRole role) {
        return rolePermissions.getOrDefault(role, RolePermissions.none());
    }
    
    /**
     * Map으로 변환 (Firebase 저장용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        
        // rolePermissions 맵
        Map<String, Object> rolePermissionsMap = new HashMap<>();
        for (Map.Entry<IslandRole, RolePermissions> entry : rolePermissions.entrySet()) {
            rolePermissionsMap.put(entry.getKey().name(), entry.getValue().toMap());
        }
        map.put("rolePermissions", rolePermissionsMap);
        
        // lastUpdated
        map.put("lastUpdated", lastUpdated);
        
        return map;
    }
    
    /**
     * Map에서 생성
     */
    @NotNull
    public static IslandPermissionDTO fromMap(@NotNull Map<String, Object> map) {
        if (map.isEmpty()) {
            return createDefault();
        }
        
        Map<IslandRole, RolePermissions> permissions = new HashMap<>();
        
        if (map.containsKey("rolePermissions") && map.get("rolePermissions") instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> rolePermissionsMap = (Map<String, Object>) map.get("rolePermissions");
            
            for (Map.Entry<String, Object> entry : rolePermissionsMap.entrySet()) {
                try {
                    IslandRole role = IslandRole.valueOf(entry.getKey());
                    if (entry.getValue() instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> permsMap = (Map<String, Object>) entry.getValue();
                        RolePermissions perms = RolePermissions.fromMap(permsMap);
                        permissions.put(role, perms);
                    }
                } catch (IllegalArgumentException e) {
                    // 잘못된 역할 이름은 무시
                }
            }
        }
        
        long lastUpdated = FirestoreUtils.getLong(map, "lastUpdated");
        
        // 누락된 역할에 대한 기본 권한 추가
        for (IslandRole role : IslandRole.values()) {
            if (!permissions.containsKey(role)) {
                permissions.put(role, createDefault().getPermissions(role));
            }
        }
        
        return new IslandPermissionDTO(permissions, lastUpdated);
    }
    
    
    /**
     * 역할별 권한 정의
     */
    public record RolePermissions(
            boolean canBuild,
            boolean canBreak,
            boolean canInteract,
            boolean canInvite,
            boolean canKick,
            boolean canSetSpawn,
            boolean canManageWorkers,
            boolean canManagePermissions,
            boolean canUpgrade,
            boolean canReset
    ) {
        /**
         * 모든 권한 활성화
         */
        public static RolePermissions all() {
            return new RolePermissions(true, true, true, true, true, true, true, true, true, true);
        }
        
        /**
         * 모든 권한 비활성화
         */
        public static RolePermissions none() {
            return new RolePermissions(false, false, false, false, false, false, false, false, false, false);
        }
        
        /**
         * Map으로 변환
         */
        @NotNull
        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            
            map.put("canBuild", canBuild);
            map.put("canBreak", canBreak);
            map.put("canInteract", canInteract);
            map.put("canInvite", canInvite);
            map.put("canKick", canKick);
            map.put("canSetSpawn", canSetSpawn);
            map.put("canManageWorkers", canManageWorkers);
            map.put("canManagePermissions", canManagePermissions);
            map.put("canUpgrade", canUpgrade);
            map.put("canReset", canReset);
            
            return map;
        }
        
        /**
         * Map에서 생성
         */
        @NotNull
        public static RolePermissions fromMap(@NotNull Map<String, Object> map) {
            if (map.isEmpty()) {
                return none();
            }
            
            boolean canBuild = FirestoreUtils.getBoolean(map, "canBuild", false);
            boolean canBreak = FirestoreUtils.getBoolean(map, "canBreak", false);
            boolean canInteract = FirestoreUtils.getBoolean(map, "canInteract", false);
            boolean canInvite = FirestoreUtils.getBoolean(map, "canInvite", false);
            boolean canKick = FirestoreUtils.getBoolean(map, "canKick", false);
            boolean canSetSpawn = FirestoreUtils.getBoolean(map, "canSetSpawn", false);
            boolean canManageWorkers = FirestoreUtils.getBoolean(map, "canManageWorkers", false);
            boolean canManagePermissions = FirestoreUtils.getBoolean(map, "canManagePermissions", false);
            boolean canUpgrade = FirestoreUtils.getBoolean(map, "canUpgrade", false);
            boolean canReset = FirestoreUtils.getBoolean(map, "canReset", false);
            
            return new RolePermissions(canBuild, canBreak, canInteract, canInvite, canKick, 
                    canSetSpawn, canManageWorkers, canManagePermissions, canUpgrade, canReset);
        }
    }
}