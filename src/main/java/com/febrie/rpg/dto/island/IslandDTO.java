package com.febrie.rpg.dto.island;

import com.febrie.rpg.database.constants.DatabaseConstants;
import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 섬 전체 정보 DTO (Composite Pattern)
 * 작은 DTO들을 조합하여 전체 섬 정보를 관리
 *
 * @author Febrie, CoffeeTory
 */
public record IslandDTO(
        @NotNull IslandCoreDTO core,                // 기본 정보
        @NotNull IslandMembershipDTO membership,     // 멤버 관리
        @NotNull IslandSocialDTO social,            // 소셜 정보
        @NotNull IslandConfigurationDTO configuration // 설정 정보
) {
    /**
     * 신규 섬 생성용 기본 생성자
     */
    public static IslandDTO createNew(String islandId, String ownerUuid, String ownerName, String islandName) {
        IslandCoreDTO core = IslandCoreDTO.createNew(islandId, ownerUuid, ownerName, islandName);
        IslandMembershipDTO membership = IslandMembershipDTO.createEmpty(islandId, ownerUuid);
        IslandSocialDTO social = IslandSocialDTO.createEmpty(islandId);
        IslandConfigurationDTO configuration = IslandConfigurationDTO.createDefault(islandId);
        
        return new IslandDTO(core, membership, social, configuration);
    }

    
    /**
     * 섬 삭제 가능 여부 확인 (생성 후 1주일 경과)
     */
    public boolean canDelete() {
        return core.canDelete();
    }

    /**
     * 현재 섬원 수 (섬장 포함)
     */
    public int getMemberCount() {
        return 1 + membership.getMemberCount(); // 섬장 + 섬원들
    }

    /**
     * 특정 플레이어의 역할 확인
     */
    public IslandRole getPlayerRole(String playerUuid) {
        if (core.ownerUuid().equals(playerUuid)) {
            return IslandRole.OWNER;
        }
        
        for (IslandMemberDTO member : membership.members()) {
            if (member.uuid().equals(playerUuid)) {
                return member.isCoOwner() ? IslandRole.CO_OWNER : IslandRole.MEMBER;
            }
        }
        
        for (IslandWorkerDTO worker : membership.workers()) {
            if (worker.uuid().equals(playerUuid)) {
                return IslandRole.WORKER;
            }
        }
        
        return IslandRole.VISITOR;
    }

    /**
     * 섬에 속한 플레이어인지 확인 (알바 제외)
     */
    public boolean isMember(String playerUuid) {
        if (core.ownerUuid().equals(playerUuid)) {
            return true;
        }
        return membership.members().stream()
                .anyMatch(member -> member.uuid().equals(playerUuid));
    }

    /**
     * 섬 크기를 16의 배수로 반올림 (바이옴 설정용)
     */
    public int getBiomeSize() {
        return configuration.getBiomeSize(core.size());
    }


    /**
     * Map으로 변환 (Firebase 저장용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        
        // Core 정보
        map.putAll(core.toMap());
        
        // Membership 정보
        List<Map<String, Object>> membersList = membership.members().stream()
                .map(IslandMemberDTO::toMap)
                .collect(Collectors.toList());
        map.put("members", membersList);
        
        List<Map<String, Object>> workersList = membership.workers().stream()
                .map(IslandWorkerDTO::toMap)
                .collect(Collectors.toList());
        map.put("workers", workersList);
        
        map.put("contributions", membership.contributions());
        
        // Social 정보
        List<Map<String, Object>> pendingInvitesList = social.pendingInvites().stream()
                .map(IslandInviteDTO::toMap)
                .collect(Collectors.toList());
        map.put("pendingInvites", pendingInvitesList);
        
        List<Map<String, Object>> recentVisitsList = social.recentVisits().stream()
                .map(IslandVisitDTO::toMap)
                .collect(Collectors.toList());
        map.put("recentVisits", recentVisitsList);
        
        // Configuration 정보
        map.put("spawnData", configuration.spawnData().toMap());
        map.put("upgradeData", configuration.upgradeData().toMap());
        map.put("permissions", configuration.permissions().toMap());
        map.put("settings", configuration.settings().toMap());
        
        return map;
    }

    /**
     * Map에서 생성
     */
    public static IslandDTO fromMap(@NotNull Map<String, Object> map) {
        // Core DTO 생성
        IslandCoreDTO core = IslandCoreDTO.fromMap(map);
        
        // Membership DTO 생성
        List<IslandMemberDTO> members = new ArrayList<>();
        List<Map<String, Object>> membersList = FirestoreUtils.getList(map, "members", new ArrayList<>());
        for (Map<String, Object> item : membersList) {
            members.add(IslandMemberDTO.fromMap(item));
        }

        List<IslandWorkerDTO> workers = new ArrayList<>();
        List<Map<String, Object>> workersList = FirestoreUtils.getList(map, "workers", new ArrayList<>());
        for (Map<String, Object> item : workersList) {
            workers.add(IslandWorkerDTO.fromMap(item));
        }

        Map<String, Long> contributions = new HashMap<>();
        Map<String, Object> contributionsMap = FirestoreUtils.getMap(map, "contributions", new HashMap<>());
        for (Map.Entry<String, Object> entry : contributionsMap.entrySet()) {
            if (entry.getValue() instanceof Number) {
                contributions.put(entry.getKey(), FirestoreUtils.getLong(contributionsMap, entry.getKey()));
            }
        }
        
        IslandMembershipDTO membership = new IslandMembershipDTO(
                core.islandId(), members, workers, contributions
        );
        
        // Social DTO 생성
        List<IslandInviteDTO> pendingInvites = new ArrayList<>();
        List<Map<String, Object>> pendingInvitesList = FirestoreUtils.getList(map, "pendingInvites", new ArrayList<>());
        for (Map<String, Object> item : pendingInvitesList) {
            IslandInviteDTO invite = IslandInviteDTO.fromMap(item);
            if (!invite.isExpired()) {
                pendingInvites.add(invite);
            }
        }

        List<IslandVisitDTO> recentVisits = new ArrayList<>();
        List<Map<String, Object>> recentVisitsList = FirestoreUtils.getList(map, "recentVisits", new ArrayList<>());
        for (Map<String, Object> item : recentVisitsList) {
            recentVisits.add(IslandVisitDTO.fromMap(item));
        }
        
        IslandSocialDTO social = new IslandSocialDTO(
                core.islandId(), pendingInvites, recentVisits
        );
        
        // Configuration DTO 생성
        Map<String, Object> spawnDataMap = FirestoreUtils.getMapOrNull(map, "spawnData", null);
        IslandSpawnDTO spawnData = spawnDataMap != null ? 
                IslandSpawnDTO.fromMap(spawnDataMap) : IslandSpawnDTO.createDefault();
        
        Map<String, Object> upgradeDataMap = FirestoreUtils.getMapOrNull(map, "upgradeData", null);
        IslandUpgradeDTO upgradeData = upgradeDataMap != null ? 
                IslandUpgradeDTO.fromMap(upgradeDataMap) : IslandUpgradeDTO.createDefault();
        
        Map<String, Object> permissionsMap = FirestoreUtils.getMapOrNull(map, "permissions", null);
        IslandPermissionDTO permissions = permissionsMap != null ? 
                IslandPermissionDTO.fromMap(permissionsMap) : IslandPermissionDTO.createDefault();
        
        Map<String, Object> settingsMap = FirestoreUtils.getMapOrNull(map, "settings", null);
        IslandSettingsDTO settings = settingsMap != null ? 
                IslandSettingsDTO.fromMap(settingsMap) : IslandSettingsDTO.createDefault();
        
        IslandConfigurationDTO configuration = new IslandConfigurationDTO(
                core.islandId(), spawnData, upgradeData, permissions, settings
        );
        
        return new IslandDTO(core, membership, social, configuration);
    }
}