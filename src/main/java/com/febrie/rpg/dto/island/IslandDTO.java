package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
public record IslandDTO(
        @NotNull String islandId,
        @NotNull String ownerUuid,
        @NotNull String ownerName,
        @NotNull String islandName,
        int size, // 현재 섬 크기
        boolean isPublic, // 공개/비공개 설정
        long createdAt,
        long lastActivity,
        @NotNull List<IslandMemberDTO> members, // 섬원 목록 (부섬장, 일반섬원)
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
        return new IslandDTO(
                islandId,
                ownerUuid,
                ownerName,
                islandName,
                85, // 초기 크기
                false, // 기본 비공개
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                List.of(),
                List.of(),
                Map.of(ownerUuid, 0L),
                IslandSpawnDTO.createDefault(),
                IslandUpgradeDTO.createDefault(),
                IslandPermissionDTO.createDefault(),
                List.of(),
                List.of(),
                0,
                null,
                IslandSettingsDTO.createDefault()
        );
    }
    
    /**
     * 섬 삭제 가능 여부 확인 (생성 후 1주일 경과)
     */
    public boolean canDelete() {
        long oneWeekInMillis = 7L * 24 * 60 * 60 * 1000;
        return System.currentTimeMillis() - createdAt >= oneWeekInMillis;
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
        int minSize = Math.max(size, 500);
        return ((minSize + 15) / 16) * 16;
    }
    
    /**
     * JsonObject로 변환 (Firebase 저장용)
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject fields = new JsonObject();
        
        // 기본 필드들
        fields.add("islandId", JsonUtil.createStringValue(islandId));
        fields.add("ownerUuid", JsonUtil.createStringValue(ownerUuid));
        fields.add("ownerName", JsonUtil.createStringValue(ownerName));
        fields.add("islandName", JsonUtil.createStringValue(islandName));
        fields.add("size", JsonUtil.createIntegerValue(size));
        fields.add("isPublic", JsonUtil.createBooleanValue(isPublic));
        fields.add("createdAt", JsonUtil.createIntegerValue(createdAt));
        fields.add("lastActivity", JsonUtil.createIntegerValue(lastActivity));
        
        // 배열 필드들
        fields.add("members", JsonUtil.createArrayValue(members, IslandMemberDTO::toJsonObject));
        fields.add("workers", JsonUtil.createArrayValue(workers, IslandWorkerDTO::toJsonObject));
        fields.add("pendingInvites", JsonUtil.createArrayValue(pendingInvites, IslandInviteDTO::toJsonObject));
        fields.add("recentVisits", JsonUtil.createArrayValue(recentVisits, IslandVisitDTO::toJsonObject));
        
        // contributions 맵
        fields.add("contributions", JsonUtil.createMapField(contributions, value -> JsonUtil.createIntegerValue(value)));
        
        // 중첩 객체들
        fields.add("spawnData", JsonUtil.createMapValue(spawnData.toJsonObject()));
        fields.add("upgradeData", JsonUtil.createMapValue(upgradeData.toJsonObject()));
        fields.add("permissions", JsonUtil.createMapValue(permissions.toJsonObject()));
        fields.add("settings", JsonUtil.createMapValue(settings.toJsonObject()));
        
        fields.add("totalResets", JsonUtil.createIntegerValue(totalResets));
        
        if (deletionScheduledAt != null) {
            fields.add("deletionScheduledAt", JsonUtil.createIntegerValue(deletionScheduledAt));
        }
        
        return JsonUtil.wrapInDocument(fields);
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static IslandDTO fromJsonObject(@NotNull JsonObject json) {
        JsonObject fields = JsonUtil.unwrapDocument(json);
        
        // 기본 필드 파싱
        String islandId = JsonUtil.getStringValue(fields, "islandId", "");
        String ownerUuid = JsonUtil.getStringValue(fields, "ownerUuid", "");
        String ownerName = JsonUtil.getStringValue(fields, "ownerName", "");
        String islandName = JsonUtil.getStringValue(fields, "islandName", "");
        int size = JsonUtil.getIntegerValue(fields, "size", 85);
        boolean isPublic = JsonUtil.getBooleanValue(fields, "isPublic", false);
        long createdAt = JsonUtil.getLongValue(fields, "createdAt", System.currentTimeMillis());
        long lastActivity = JsonUtil.getLongValue(fields, "lastActivity", System.currentTimeMillis());
        
        // 배열 필드 파싱
        List<IslandMemberDTO> members = JsonUtil.getArrayValue(fields, "members", IslandMemberDTO::fromJsonObject);
        List<IslandWorkerDTO> workers = JsonUtil.getArrayValue(fields, "workers", IslandWorkerDTO::fromJsonObject);
        List<IslandVisitDTO> recentVisits = JsonUtil.getArrayValue(fields, "recentVisits", IslandVisitDTO::fromJsonObject);
        
        // pendingInvites - 만료된 초대 필터링
        List<IslandInviteDTO> pendingInvites = JsonUtil.getArrayValue(fields, "pendingInvites", IslandInviteDTO::fromJsonObject)
                .stream()
                .filter(invite -> !invite.isExpired())
                .collect(Collectors.toList());
        
        // contributions 맵 파싱
        Map<String, Long> contributions = JsonUtil.getMapField(fields, "contributions", 
                key -> key, 
                obj -> JsonUtil.getLongValue(obj, "integerValue", 0L));
        
        // 중첩 객체 파싱
        JsonObject spawnDataJson = JsonUtil.getMapValue(fields, "spawnData");
        IslandSpawnDTO spawnData = spawnDataJson.entrySet().isEmpty() 
                ? IslandSpawnDTO.createDefault() 
                : IslandSpawnDTO.fromJsonObject(spawnDataJson);
        
        JsonObject upgradeDataJson = JsonUtil.getMapValue(fields, "upgradeData");
        IslandUpgradeDTO upgradeData = upgradeDataJson.entrySet().isEmpty()
                ? IslandUpgradeDTO.createDefault()
                : IslandUpgradeDTO.fromJsonObject(upgradeDataJson);
        
        JsonObject permissionsJson = JsonUtil.getMapValue(fields, "permissions");
        IslandPermissionDTO permissions = permissionsJson.entrySet().isEmpty()
                ? IslandPermissionDTO.createDefault()
                : IslandPermissionDTO.fromJsonObject(permissionsJson);
        
        JsonObject settingsJson = JsonUtil.getMapValue(fields, "settings");
        IslandSettingsDTO settings = settingsJson.entrySet().isEmpty()
                ? IslandSettingsDTO.createDefault()
                : IslandSettingsDTO.fromJsonObject(settingsJson);
        
        int totalResets = JsonUtil.getIntegerValue(fields, "totalResets", 0);
        Long deletionScheduledAt = fields.has("deletionScheduledAt") 
                ? JsonUtil.getLongValue(fields, "deletionScheduledAt") 
                : null;
        
        return new IslandDTO(
                islandId,
                ownerUuid,
                ownerName,
                islandName,
                size,
                isPublic,
                createdAt,
                lastActivity,
                members,
                workers,
                contributions,
                spawnData,
                upgradeData,
                permissions,
                pendingInvites,
                recentVisits,
                totalResets,
                deletionScheduledAt,
                settings
        );
    }
    
}