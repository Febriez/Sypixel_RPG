package com.febrie.rpg.dto.island;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 섬 권한 설정 DTO (Record)
 *
 * @author Febrie, CoffeeTory
 */
public record IslandPermissionDTO(
        @NotNull Map<IslandRole, RolePermissions> rolePermissions
) {
    /**
     * 기본 권한 설정 생성
     */
    public static IslandPermissionDTO createDefault() {
        Map<IslandRole, RolePermissions> permissions = new HashMap<>();
        
        // 섬장은 모든 권한
        permissions.put(IslandRole.OWNER, RolePermissions.all());
        
        // 부섬장 기본 권한
        permissions.put(IslandRole.CO_OWNER, new RolePermissions(
                true,  // 블록 설치/파괴
                true,  // 상자 접근
                true,  // 몬스터 처치
                true,  // 아이템 줍기
                true,  // 섬원 초대
                false, // 섬원 추방
                true,  // 기여도 획득
                true   // 스폰 설정
        ));
        
        // 일반 섬원 기본 권한
        permissions.put(IslandRole.MEMBER, new RolePermissions(
                true,  // 블록 설치/파괴
                true,  // 상자 접근
                true,  // 몬스터 처치
                true,  // 아이템 줍기
                false, // 섬원 초대
                false, // 섬원 추방
                true,  // 기여도 획득
                true   // 스폰 설정
        ));
        
        // 알바생 기본 권한
        permissions.put(IslandRole.WORKER, new RolePermissions(
                true,  // 블록 설치/파괴
                false, // 상자 접근
                true,  // 몬스터 처치
                false, // 아이템 줍기
                false, // 섬원 초대
                false, // 섬원 추방
                false, // 기여도 획득
                false  // 스폰 설정
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
     * JsonObject로 변환 (Firebase 저장용)
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        // rolePermissions 맵
        JsonObject rolePermissionsValue = new JsonObject();
        JsonObject rolePermissionsMap = new JsonObject();
        JsonObject rolePermissionsFields = new JsonObject();
        
        rolePermissions.forEach((role, perms) -> {
            JsonObject permsValue = new JsonObject();
            permsValue.add("mapValue", perms.toJsonObject());
            rolePermissionsFields.add(role.name(), permsValue);
        });
        
        rolePermissionsMap.add("fields", rolePermissionsFields);
        rolePermissionsValue.add("mapValue", rolePermissionsMap);
        fields.add("rolePermissions", rolePermissionsValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static IslandPermissionDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return createDefault();
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        Map<IslandRole, RolePermissions> permissions = new HashMap<>();
        
        if (fields.has("rolePermissions") && fields.getAsJsonObject("rolePermissions").has("mapValue")) {
            JsonObject rolePermissionsMap = fields.getAsJsonObject("rolePermissions").getAsJsonObject("mapValue");
            if (rolePermissionsMap.has("fields")) {
                JsonObject rolePermissionsFields = rolePermissionsMap.getAsJsonObject("fields");
                for (Map.Entry<String, JsonElement> entry : rolePermissionsFields.entrySet()) {
                    try {
                        IslandRole role = IslandRole.valueOf(entry.getKey());
                        if (entry.getValue().isJsonObject() && entry.getValue().getAsJsonObject().has("mapValue")) {
                            RolePermissions perms = RolePermissions.fromJsonObject(entry.getValue().getAsJsonObject().getAsJsonObject("mapValue"));
                            permissions.put(role, perms);
                        }
                    } catch (IllegalArgumentException e) {
                        // 잘못된 역할 이름은 무시
                    }
                }
            }
        }
        
        // 누락된 역할에 대한 기본 권한 추가
        for (IslandRole role : IslandRole.values()) {
            if (!permissions.containsKey(role)) {
                permissions.put(role, createDefault().getPermissions(role));
            }
        }
        
        return new IslandPermissionDTO(permissions);
    }
    
    /**
     * Map으로 변환 (Firebase 저장용)
     */
    @Deprecated
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Map<String, Object>> permissionsMap = new HashMap<>();
        
        rolePermissions.forEach((role, perms) -> {
            permissionsMap.put(role.name(), perms.toMap());
        });
        
        map.put("rolePermissions", permissionsMap);
        return map;
    }
    
    /**
     * Map에서 생성
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static IslandPermissionDTO fromMap(Map<String, Object> map) {
        if (map == null) return createDefault();
        
        Map<IslandRole, RolePermissions> permissions = new HashMap<>();
        Map<String, Map<String, Object>> permissionsMap = 
                (Map<String, Map<String, Object>>) map.get("rolePermissions");
        
        if (permissionsMap != null) {
            permissionsMap.forEach((roleName, permsMap) -> {
                try {
                    IslandRole role = IslandRole.valueOf(roleName);
                    RolePermissions perms = RolePermissions.fromMap(permsMap);
                    permissions.put(role, perms);
                } catch (IllegalArgumentException e) {
                    // 잘못된 역할 이름은 무시
                }
            });
        }
        
        // 누락된 역할에 대한 기본 권한 추가
        for (IslandRole role : IslandRole.values()) {
            if (!permissions.containsKey(role)) {
                permissions.put(role, createDefault().getPermissions(role));
            }
        }
        
        return new IslandPermissionDTO(permissions);
    }
    
    /**
     * 역할별 권한 정의
     */
    public record RolePermissions(
            boolean canBuild,
            boolean canAccessChest,
            boolean canKillMobs,
            boolean canPickupItems,
            boolean canInviteMembers,
            boolean canKickMembers,
            boolean canEarnContribution,
            boolean canSetSpawn
    ) {
        /**
         * 모든 권한 활성화
         */
        public static RolePermissions all() {
            return new RolePermissions(true, true, true, true, true, true, true, true);
        }
        
        /**
         * 모든 권한 비활성화
         */
        public static RolePermissions none() {
            return new RolePermissions(false, false, false, false, false, false, false, false);
        }
        
        /**
         * JsonObject로 변환
         */
        @NotNull
        public JsonObject toJsonObject() {
            JsonObject json = new JsonObject();
            JsonObject fields = new JsonObject();
            
            JsonObject canBuildValue = new JsonObject();
            canBuildValue.addProperty("booleanValue", canBuild);
            fields.add("canBuild", canBuildValue);
            
            JsonObject canAccessChestValue = new JsonObject();
            canAccessChestValue.addProperty("booleanValue", canAccessChest);
            fields.add("canAccessChest", canAccessChestValue);
            
            JsonObject canKillMobsValue = new JsonObject();
            canKillMobsValue.addProperty("booleanValue", canKillMobs);
            fields.add("canKillMobs", canKillMobsValue);
            
            JsonObject canPickupItemsValue = new JsonObject();
            canPickupItemsValue.addProperty("booleanValue", canPickupItems);
            fields.add("canPickupItems", canPickupItemsValue);
            
            JsonObject canInviteMembersValue = new JsonObject();
            canInviteMembersValue.addProperty("booleanValue", canInviteMembers);
            fields.add("canInviteMembers", canInviteMembersValue);
            
            JsonObject canKickMembersValue = new JsonObject();
            canKickMembersValue.addProperty("booleanValue", canKickMembers);
            fields.add("canKickMembers", canKickMembersValue);
            
            JsonObject canEarnContributionValue = new JsonObject();
            canEarnContributionValue.addProperty("booleanValue", canEarnContribution);
            fields.add("canEarnContribution", canEarnContributionValue);
            
            JsonObject canSetSpawnValue = new JsonObject();
            canSetSpawnValue.addProperty("booleanValue", canSetSpawn);
            fields.add("canSetSpawn", canSetSpawnValue);
            
            json.add("fields", fields);
            return json;
        }
        
        /**
         * JsonObject에서 생성
         */
        @NotNull
        public static RolePermissions fromJsonObject(@NotNull JsonObject json) {
            if (!json.has("fields")) {
                return none();
            }
            
            JsonObject fields = json.getAsJsonObject("fields");
            
            boolean canBuild = fields.has("canBuild") && fields.getAsJsonObject("canBuild").has("booleanValue")
                    ? fields.getAsJsonObject("canBuild").get("booleanValue").getAsBoolean()
                    : false;
                    
            boolean canAccessChest = fields.has("canAccessChest") && fields.getAsJsonObject("canAccessChest").has("booleanValue")
                    ? fields.getAsJsonObject("canAccessChest").get("booleanValue").getAsBoolean()
                    : false;
                    
            boolean canKillMobs = fields.has("canKillMobs") && fields.getAsJsonObject("canKillMobs").has("booleanValue")
                    ? fields.getAsJsonObject("canKillMobs").get("booleanValue").getAsBoolean()
                    : false;
                    
            boolean canPickupItems = fields.has("canPickupItems") && fields.getAsJsonObject("canPickupItems").has("booleanValue")
                    ? fields.getAsJsonObject("canPickupItems").get("booleanValue").getAsBoolean()
                    : false;
                    
            boolean canInviteMembers = fields.has("canInviteMembers") && fields.getAsJsonObject("canInviteMembers").has("booleanValue")
                    ? fields.getAsJsonObject("canInviteMembers").get("booleanValue").getAsBoolean()
                    : false;
                    
            boolean canKickMembers = fields.has("canKickMembers") && fields.getAsJsonObject("canKickMembers").has("booleanValue")
                    ? fields.getAsJsonObject("canKickMembers").get("booleanValue").getAsBoolean()
                    : false;
                    
            boolean canEarnContribution = fields.has("canEarnContribution") && fields.getAsJsonObject("canEarnContribution").has("booleanValue")
                    ? fields.getAsJsonObject("canEarnContribution").get("booleanValue").getAsBoolean()
                    : false;
                    
            boolean canSetSpawn = fields.has("canSetSpawn") && fields.getAsJsonObject("canSetSpawn").has("booleanValue")
                    ? fields.getAsJsonObject("canSetSpawn").get("booleanValue").getAsBoolean()
                    : false;
            
            return new RolePermissions(canBuild, canAccessChest, canKillMobs, canPickupItems, 
                    canInviteMembers, canKickMembers, canEarnContribution, canSetSpawn);
        }
        
        /**
         * Map으로 변환
         */
        @Deprecated
        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("canBuild", canBuild);
            map.put("canAccessChest", canAccessChest);
            map.put("canKillMobs", canKillMobs);
            map.put("canPickupItems", canPickupItems);
            map.put("canInviteMembers", canInviteMembers);
            map.put("canKickMembers", canKickMembers);
            map.put("canEarnContribution", canEarnContribution);
            map.put("canSetSpawn", canSetSpawn);
            return map;
        }
        
        /**
         * Map에서 생성
         */
        @Deprecated
        public static RolePermissions fromMap(Map<String, Object> map) {
            if (map == null) return none();
            
            return new RolePermissions(
                    (Boolean) map.getOrDefault("canBuild", false),
                    (Boolean) map.getOrDefault("canAccessChest", false),
                    (Boolean) map.getOrDefault("canKillMobs", false),
                    (Boolean) map.getOrDefault("canPickupItems", false),
                    (Boolean) map.getOrDefault("canInviteMembers", false),
                    (Boolean) map.getOrDefault("canKickMembers", false),
                    (Boolean) map.getOrDefault("canEarnContribution", false),
                    (Boolean) map.getOrDefault("canSetSpawn", false)
            );
        }
    }
}