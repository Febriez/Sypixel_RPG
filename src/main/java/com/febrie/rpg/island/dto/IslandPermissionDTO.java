package com.febrie.rpg.island.dto;

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
    }
}