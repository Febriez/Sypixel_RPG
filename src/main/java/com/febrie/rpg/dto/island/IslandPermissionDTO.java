package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.JsonUtil;
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
     * JsonObject로 변환 (Firebase 저장용)
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        // rolePermissions 맵
        fields.add("rolePermissions", JsonUtil.createMapField(rolePermissions, 
            perms -> JsonUtil.createMapValue(perms.toJsonObject().getAsJsonObject("fields"))));
        
        // lastUpdated
        fields.add("lastUpdated", JsonUtil.createIntegerValue(lastUpdated));
        
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
        
        long lastUpdated = JsonUtil.getLongValue(fields, "lastUpdated", System.currentTimeMillis());
        
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
         * JsonObject로 변환
         */
        @NotNull
        public JsonObject toJsonObject() {
            JsonObject json = new JsonObject();
            JsonObject fields = new JsonObject();
            
            fields.add("canBuild", JsonUtil.createBooleanValue(canBuild));
            fields.add("canBreak", JsonUtil.createBooleanValue(canBreak));
            fields.add("canInteract", JsonUtil.createBooleanValue(canInteract));
            fields.add("canInvite", JsonUtil.createBooleanValue(canInvite));
            fields.add("canKick", JsonUtil.createBooleanValue(canKick));
            fields.add("canSetSpawn", JsonUtil.createBooleanValue(canSetSpawn));
            fields.add("canManageWorkers", JsonUtil.createBooleanValue(canManageWorkers));
            fields.add("canManagePermissions", JsonUtil.createBooleanValue(canManagePermissions));
            fields.add("canUpgrade", JsonUtil.createBooleanValue(canUpgrade));
            fields.add("canReset", JsonUtil.createBooleanValue(canReset));
            
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
            
            boolean canBuild = JsonUtil.getBooleanValue(fields, "canBuild", false);
            boolean canBreak = JsonUtil.getBooleanValue(fields, "canBreak", false);
            boolean canInteract = JsonUtil.getBooleanValue(fields, "canInteract", false);
            boolean canInvite = JsonUtil.getBooleanValue(fields, "canInvite", false);
            boolean canKick = JsonUtil.getBooleanValue(fields, "canKick", false);
            boolean canSetSpawn = JsonUtil.getBooleanValue(fields, "canSetSpawn", false);
            boolean canManageWorkers = JsonUtil.getBooleanValue(fields, "canManageWorkers", false);
            boolean canManagePermissions = JsonUtil.getBooleanValue(fields, "canManagePermissions", false);
            boolean canUpgrade = JsonUtil.getBooleanValue(fields, "canUpgrade", false);
            boolean canReset = JsonUtil.getBooleanValue(fields, "canReset", false);
            
            return new RolePermissions(canBuild, canBreak, canInteract, canInvite, canKick, 
                    canSetSpawn, canManageWorkers, canManagePermissions, canUpgrade, canReset);
        }
    }
}