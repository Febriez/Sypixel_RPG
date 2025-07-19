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
        
        // lastUpdated
        JsonObject lastUpdatedValue = new JsonObject();
        lastUpdatedValue.addProperty("integerValue", lastUpdated);
        fields.add("lastUpdated", lastUpdatedValue);
        
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
        
        long lastUpdated = fields.has("lastUpdated") && fields.getAsJsonObject("lastUpdated").has("integerValue")
                ? fields.getAsJsonObject("lastUpdated").get("integerValue").getAsLong()
                : System.currentTimeMillis();
        
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
            
            JsonObject canBuildValue = new JsonObject();
            canBuildValue.addProperty("booleanValue", canBuild);
            fields.add("canBuild", canBuildValue);
            
            JsonObject canBreakValue = new JsonObject();
            canBreakValue.addProperty("booleanValue", canBreak);
            fields.add("canBreak", canBreakValue);
            
            JsonObject canInteractValue = new JsonObject();
            canInteractValue.addProperty("booleanValue", canInteract);
            fields.add("canInteract", canInteractValue);
            
            JsonObject canInviteValue = new JsonObject();
            canInviteValue.addProperty("booleanValue", canInvite);
            fields.add("canInvite", canInviteValue);
            
            JsonObject canKickValue = new JsonObject();
            canKickValue.addProperty("booleanValue", canKick);
            fields.add("canKick", canKickValue);
            
            JsonObject canSetSpawnValue = new JsonObject();
            canSetSpawnValue.addProperty("booleanValue", canSetSpawn);
            fields.add("canSetSpawn", canSetSpawnValue);
            
            JsonObject canManageWorkersValue = new JsonObject();
            canManageWorkersValue.addProperty("booleanValue", canManageWorkers);
            fields.add("canManageWorkers", canManageWorkersValue);
            
            JsonObject canManagePermissionsValue = new JsonObject();
            canManagePermissionsValue.addProperty("booleanValue", canManagePermissions);
            fields.add("canManagePermissions", canManagePermissionsValue);
            
            JsonObject canUpgradeValue = new JsonObject();
            canUpgradeValue.addProperty("booleanValue", canUpgrade);
            fields.add("canUpgrade", canUpgradeValue);
            
            JsonObject canResetValue = new JsonObject();
            canResetValue.addProperty("booleanValue", canReset);
            fields.add("canReset", canResetValue);
            
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
            
            boolean canBuild = fields.has("canBuild") && fields.getAsJsonObject("canBuild").has("booleanValue") && fields.getAsJsonObject("canBuild").get("booleanValue").getAsBoolean();
                    
            boolean canBreak = fields.has("canBreak") && fields.getAsJsonObject("canBreak").has("booleanValue") && fields.getAsJsonObject("canBreak").get("booleanValue").getAsBoolean();
                    
            boolean canInteract = fields.has("canInteract") && fields.getAsJsonObject("canInteract").has("booleanValue") && fields.getAsJsonObject("canInteract").get("booleanValue").getAsBoolean();
                    
            boolean canInvite = fields.has("canInvite") && fields.getAsJsonObject("canInvite").has("booleanValue") && fields.getAsJsonObject("canInvite").get("booleanValue").getAsBoolean();
                    
            boolean canKick = fields.has("canKick") && fields.getAsJsonObject("canKick").has("booleanValue") && fields.getAsJsonObject("canKick").get("booleanValue").getAsBoolean();
                    
            boolean canSetSpawn = fields.has("canSetSpawn") && fields.getAsJsonObject("canSetSpawn").has("booleanValue") && fields.getAsJsonObject("canSetSpawn").get("booleanValue").getAsBoolean();
                    
            boolean canManageWorkers = fields.has("canManageWorkers") && fields.getAsJsonObject("canManageWorkers").has("booleanValue") && fields.getAsJsonObject("canManageWorkers").get("booleanValue").getAsBoolean();
                    
            boolean canManagePermissions = fields.has("canManagePermissions") && fields.getAsJsonObject("canManagePermissions").has("booleanValue") && fields.getAsJsonObject("canManagePermissions").get("booleanValue").getAsBoolean();
                    
            boolean canUpgrade = fields.has("canUpgrade") && fields.getAsJsonObject("canUpgrade").has("booleanValue") && fields.getAsJsonObject("canUpgrade").get("booleanValue").getAsBoolean();
                    
            boolean canReset = fields.has("canReset") && fields.getAsJsonObject("canReset").has("booleanValue") && fields.getAsJsonObject("canReset").get("booleanValue").getAsBoolean();
            
            return new RolePermissions(canBuild, canBreak, canInteract, canInvite, canKick, 
                    canSetSpawn, canManageWorkers, canManagePermissions, canUpgrade, canReset);
        }
    }
}