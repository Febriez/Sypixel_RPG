package com.febrie.rpg.dto.island;

import com.febrie.rpg.database.constants.DatabaseConstants;
import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 섬 기본 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record IslandDTO(@NotNull String islandId, @NotNull String ownerUuid, @NotNull String ownerName,
                        @NotNull String islandName, int size, // 현재 섬 크기
                        boolean isPublic, // 공개/비공개 설정
                        long createdAt, long lastActivity, @NotNull List<IslandMemberDTO> members, // 섬원 목록 (부섬장, 일반섬원)
                        @NotNull List<IslandWorkerDTO> workers, // 알바생 목록
                        @NotNull Map<String, Long> contributions, // UUID -> 기여도
                        @NotNull IslandSpawnDTO spawnData, // 스폰 위치 정보
                        @NotNull IslandUpgradeDTO upgradeData, // 업그레이드 정보
                        @NotNull IslandPermissionDTO permissions, // 권한 설정
                        @NotNull List<IslandInviteDTO> pendingInvites, // 대기중인 초대
                        @NotNull List<IslandVisitDTO> recentVisits, // 최근 방문 기록
                        int totalResets, // 총 초기화 횟수
                        @Nullable Long deletionScheduledAt, // 삭제 예정 시간 (null이면 삭제 예정 없음)
                        @NotNull IslandSettingsDTO settings // 섬 설정 (색상, 바이옴, 템플릿)
) {
    /**
     * 신규 섬 생성용 기본 생성자
     */
    public static IslandDTO createNew(String islandId, String ownerUuid, String ownerName, String islandName) {
        return new IslandDTO(islandId, ownerUuid, ownerName, islandName, DatabaseConstants.ISLAND_INITIAL_SIZE, // 초기 크기
                false, // 기본 비공개
                System.currentTimeMillis(), System.currentTimeMillis(), List.of(), List.of(), Map.of(ownerUuid, 0L), IslandSpawnDTO.createDefault(), IslandUpgradeDTO.createDefault(), IslandPermissionDTO.createDefault(), List.of(), List.of(), 0, null, IslandSettingsDTO.createDefault());
    }

    /**
     * 섬 삭제 가능 여부 확인 (생성 후 1주일 경과)
     */
    public boolean canDelete() {
        return System.currentTimeMillis() - createdAt >= DatabaseConstants.ISLAND_DELETE_COOLDOWN_MS;
    }

    /**
     * 현재 섬원 수 (섬장 포함)
     */
    public int getMemberCount() {
        return 1 + members.size(); // 섬장 + 섬원들
    }

    /**
     * 특정 플레이어의 역할 확인
     */
    public IslandRole getPlayerRole(String playerUuid) {
        if (ownerUuid.equals(playerUuid)) {
            return IslandRole.OWNER;
        }

        for (IslandMemberDTO member : members) {
            if (member.uuid().equals(playerUuid)) {
                return member.isCoOwner() ? IslandRole.CO_OWNER : IslandRole.MEMBER;
            }
        }

        for (IslandWorkerDTO worker : workers) {
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
        if (ownerUuid.equals(playerUuid)) {
            return true;
        }
        return members.stream().anyMatch(member -> member.uuid().equals(playerUuid));
    }

    /**
     * 섬 크기를 16의 배수로 반올림 (바이옴 설정용)
     */
    public int getBiomeSize() {
        // 500을 넘는 가장 가까운 16의 배수 찾기
        int minSize = Math.max(size, DatabaseConstants.ISLAND_MIN_BIOME_SIZE);
        return ((minSize + 15) / DatabaseConstants.ISLAND_BIOME_SIZE_MULTIPLE) * DatabaseConstants.ISLAND_BIOME_SIZE_MULTIPLE;
    }

    /**
     * Map으로 변환 (Firebase 저장용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        // 기본 필드들
        map.put("islandId", islandId);
        map.put("ownerUuid", ownerUuid);
        map.put("ownerName", ownerName);
        map.put("islandName", islandName);
        map.put("size", size);
        map.put("isPublic", isPublic);
        map.put("createdAt", createdAt);
        map.put("lastActivity", lastActivity);

        // 배열 필드들
        List<Map<String, Object>> membersList = members.stream()
                .map(IslandMemberDTO::toMap)
                .collect(Collectors.toList());
        map.put("members", membersList);

        List<Map<String, Object>> workersList = workers.stream()
                .map(IslandWorkerDTO::toMap)
                .collect(Collectors.toList());
        map.put("workers", workersList);

        List<Map<String, Object>> pendingInvitesList = pendingInvites.stream()
                .map(IslandInviteDTO::toMap)
                .collect(Collectors.toList());
        map.put("pendingInvites", pendingInvitesList);

        List<Map<String, Object>> recentVisitsList = recentVisits.stream()
                .map(IslandVisitDTO::toMap)
                .collect(Collectors.toList());
        map.put("recentVisits", recentVisitsList);

        // contributions 맵
        map.put("contributions", contributions);

        // 중첩 객체들
        map.put("spawnData", spawnData.toMap());
        map.put("upgradeData", upgradeData.toMap());
        map.put("permissions", permissions.toMap());
        map.put("settings", settings.toMap());

        map.put("totalResets", totalResets);

        if (deletionScheduledAt != null) {
            map.put("deletionScheduledAt", deletionScheduledAt);
        }

        return map;
    }

    /**
     * Map에서 생성
     */
    @NotNull
    public static IslandDTO fromMap(@NotNull Map<String, Object> map) {
        // 기본 필드 파싱 및 검증
        @NotNull String islandId = Objects.requireNonNull(FirestoreUtils.getString(map, "islandId", ""));
        @NotNull String ownerUuid = Objects.requireNonNull(FirestoreUtils.getString(map, "ownerUuid", ""));
        @NotNull String ownerName = Objects.requireNonNull(FirestoreUtils.getString(map, "ownerName", ""));
        @NotNull String islandName = Objects.requireNonNull(FirestoreUtils.getString(map, "islandName", ""));
        
        // 빈 문자열 검증
        if (islandId.isEmpty()) {
            throw new IllegalArgumentException("IslandDTO: islandId cannot be empty");
        }
        if (ownerUuid.isEmpty()) {
            throw new IllegalArgumentException("IslandDTO: ownerUuid cannot be empty");
        }
        if (ownerName.isEmpty()) {
            throw new IllegalArgumentException("IslandDTO: ownerName cannot be empty");
        }
        if (islandName.isEmpty()) {
            throw new IllegalArgumentException("IslandDTO: islandName cannot be empty");
        }
        
        int size = FirestoreUtils.getInt(map, "size", DatabaseConstants.ISLAND_INITIAL_SIZE);
        boolean isPublic = FirestoreUtils.getBoolean(map, "isPublic", false);
        long createdAt = FirestoreUtils.getLong(map, "createdAt", System.currentTimeMillis());
        long lastActivity = FirestoreUtils.getLong(map, "lastActivity", System.currentTimeMillis());

        // 배열 필드 파싱
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

        List<IslandVisitDTO> recentVisits = new ArrayList<>();
        List<Map<String, Object>> recentVisitsList = FirestoreUtils.getList(map, "recentVisits", new ArrayList<>());
        for (Map<String, Object> item : recentVisitsList) {
            recentVisits.add(IslandVisitDTO.fromMap(item));
        }

        // pendingInvites - 만료된 초대 필터링
        List<IslandInviteDTO> pendingInvites = new ArrayList<>();
        List<Map<String, Object>> pendingInvitesList = FirestoreUtils.getList(map, "pendingInvites", new ArrayList<>());
        for (Map<String, Object> item : pendingInvitesList) {
            IslandInviteDTO invite = IslandInviteDTO.fromMap(item);
            if (!invite.isExpired()) {
                pendingInvites.add(invite);
            }
        }

        // contributions 맵 파싱
        Map<String, Long> contributions = new HashMap<>();
        Map<String, Object> contributionsMap = FirestoreUtils.getMap(map, "contributions", new HashMap<>());
        if (contributionsMap != null) {
            for (Map.Entry<String, Object> entry : contributionsMap.entrySet()) {
                if (entry.getValue() instanceof Number) {
                    contributions.put(entry.getKey(), FirestoreUtils.getLong(contributionsMap, entry.getKey()));
                }
            }
        }

        // 중첩 객체 파싱
        Map<String, Object> spawnDataMap = FirestoreUtils.getMap(map, "spawnData", null);
        IslandSpawnDTO spawnData = spawnDataMap != null ? IslandSpawnDTO.fromMap(spawnDataMap) : IslandSpawnDTO.createDefault();

        Map<String, Object> upgradeDataMap = FirestoreUtils.getMap(map, "upgradeData", null);
        IslandUpgradeDTO upgradeData = upgradeDataMap != null ? IslandUpgradeDTO.fromMap(upgradeDataMap) : IslandUpgradeDTO.createDefault();

        Map<String, Object> permissionsMap = FirestoreUtils.getMap(map, "permissions", null);
        IslandPermissionDTO permissions = permissionsMap != null ? IslandPermissionDTO.fromMap(permissionsMap) : IslandPermissionDTO.createDefault();

        Map<String, Object> settingsMap = FirestoreUtils.getMap(map, "settings", null);
        IslandSettingsDTO settings = settingsMap != null ? IslandSettingsDTO.fromMap(settingsMap) : IslandSettingsDTO.createDefault();

        int totalResets = FirestoreUtils.getInt(map, "totalResets", 0);
        Long deletionScheduledAt = FirestoreUtils.getLong(map, "deletionScheduledAt", null);

        return new IslandDTO(islandId, ownerUuid, ownerName, islandName, size, isPublic, createdAt, lastActivity, members, workers, contributions, spawnData, upgradeData, permissions, pendingInvites, recentVisits, totalResets, deletionScheduledAt, settings);
    }

}