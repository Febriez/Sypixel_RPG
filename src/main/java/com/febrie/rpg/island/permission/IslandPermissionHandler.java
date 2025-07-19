package com.febrie.rpg.island.permission;

import com.febrie.rpg.dto.island.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 섬 권한 처리기
 * 플레이어의 역할에 따른 권한 확인
 *
 * @author Febrie, CoffeeTory
 */
public class IslandPermissionHandler {
    
    /**
     * 플레이어가 특정 권한을 가지고 있는지 확인
     */
    public static boolean hasPermission(@NotNull IslandDTO island, @NotNull Player player, @NotNull String permission) {
        return hasPermission(island, player.getUniqueId().toString(), permission);
    }
    
    /**
     * 플레이어가 특정 권한을 가지고 있는지 확인
     */
    public static boolean hasPermission(@NotNull IslandDTO island, @NotNull String playerUuid, @NotNull String permission) {
        IslandRole role = getPlayerRole(island, playerUuid);
        if (role == null) {
            return false;
        }
        
        // 섬장은 모든 권한 보유
        if (role == IslandRole.OWNER) {
            return true;
        }
        
        // 권한 설정에서 역할별 권한 확인
        IslandPermissionDTO permissions = island.permissions();
        IslandPermissionDTO.RolePermissions rolePerms = permissions.rolePermissions().get(role.name());
        
        if (rolePerms == null) {
            return false;
        }
        
        // 권한 확인
        return switch (permission) {
            case "BUILD" -> rolePerms.canBuild();
            case "USE_ITEMS" -> rolePerms.canInteract();
            case "OPEN_CONTAINERS" -> rolePerms.canInteract();
            case "INVITE_MEMBERS" -> rolePerms.canInvite();
            case "KICK_MEMBERS" -> rolePerms.canKick();
            case "MANAGE_WORKERS" -> rolePerms.canManageWorkers();
            case "MODIFY_SPAWNS" -> rolePerms.canSetSpawn();
            case "CHANGE_SETTINGS" -> rolePerms.canManagePermissions();
            default -> false;
        };
    }
    
    /**
     * 플레이어의 섬 역할 가져오기
     */
    @Nullable
    public static IslandRole getPlayerRole(@NotNull IslandDTO island, @NotNull Player player) {
        return getPlayerRole(island, player.getUniqueId().toString());
    }
    
    /**
     * 플레이어의 섬 역할 가져오기
     */
    @Nullable
    public static IslandRole getPlayerRole(@NotNull IslandDTO island, @NotNull String playerUuid) {
        // 섬장 확인
        if (island.ownerUuid().equals(playerUuid)) {
            return IslandRole.OWNER;
        }
        
        // 섬원 확인
        for (IslandMemberDTO member : island.members()) {
            if (member.uuid().equals(playerUuid)) {
                return member.isCoOwner() ? IslandRole.CO_OWNER : IslandRole.MEMBER;
            }
        }
        
        // 알바 확인
        for (IslandWorkerDTO worker : island.workers()) {
            if (worker.uuid().equals(playerUuid)) {
                return IslandRole.WORKER;
            }
        }
        
        // 방문자
        return IslandRole.VISITOR;
    }
    
    /**
     * 플레이어가 섬원인지 확인 (섬장 포함)
     */
    public static boolean isMember(@NotNull IslandDTO island, @NotNull Player player) {
        return isMember(island, player.getUniqueId().toString());
    }
    
    /**
     * 플레이어가 섬원인지 확인 (섬장 포함)
     */
    public static boolean isMember(@NotNull IslandDTO island, @NotNull String playerUuid) {
        // 섬장 확인
        if (island.ownerUuid().equals(playerUuid)) {
            return true;
        }
        
        // 섬원 확인
        return island.members().stream()
                .anyMatch(member -> member.uuid().equals(playerUuid));
    }
    
    /**
     * 플레이어가 알바인지 확인
     */
    public static boolean isWorker(@NotNull IslandDTO island, @NotNull Player player) {
        return isWorker(island, player.getUniqueId().toString());
    }
    
    /**
     * 플레이어가 알바인지 확인
     */
    public static boolean isWorker(@NotNull IslandDTO island, @NotNull String playerUuid) {
        return island.workers().stream()
                .anyMatch(worker -> worker.uuid().equals(playerUuid));
    }
    
    /**
     * 플레이어가 섬장인지 확인
     */
    public static boolean isOwner(@NotNull IslandDTO island, @NotNull Player player) {
        return island.ownerUuid().equals(player.getUniqueId().toString());
    }
    
    /**
     * 플레이어가 부섬장인지 확인
     */
    public static boolean isCoOwner(@NotNull IslandDTO island, @NotNull Player player) {
        return isCoOwner(island, player.getUniqueId().toString());
    }
    
    /**
     * 플레이어가 부섬장인지 확인
     */
    public static boolean isCoOwner(@NotNull IslandDTO island, @NotNull String playerUuid) {
        return island.members().stream()
                .anyMatch(member -> member.uuid().equals(playerUuid) && member.isCoOwner());
    }
    
    /**
     * 플레이어가 섬장 또는 부섬장인지 확인
     */
    public static boolean isOwnerOrCoOwner(@NotNull IslandDTO island, @NotNull Player player) {
        return isOwner(island, player) || isCoOwner(island, player);
    }
    
    /**
     * 권한 문자열을 사용자 친화적인 이름으로 변환
     */
    public static String getPermissionDisplayName(@NotNull String permission) {
        return switch (permission) {
            case "BUILD" -> "건설";
            case "USE_ITEMS" -> "아이템 사용";
            case "OPEN_CONTAINERS" -> "상자 열기";
            case "INVITE_MEMBERS" -> "멤버 초대";
            case "KICK_MEMBERS" -> "멤버 추방";
            case "MANAGE_WORKERS" -> "알바 관리";
            case "MODIFY_SPAWNS" -> "스폰 설정";
            case "CHANGE_SETTINGS" -> "섬 설정 변경";
            default -> permission;
        };
    }
    
    /**
     * 역할을 사용자 친화적인 이름으로 변환
     */
    public static String getRoleDisplayName(@NotNull IslandRole role) {
        return switch (role) {
            case OWNER -> "섬장";
            case CO_OWNER -> "부섬장";
            case MEMBER -> "섬원";
            case WORKER -> "알바";
            case VISITOR -> "방문자";
        };
    }
}