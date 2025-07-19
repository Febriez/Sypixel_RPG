package com.febrie.rpg.dto.island;

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
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        // 기본 필드들
        JsonObject islandIdValue = new JsonObject();
        islandIdValue.addProperty("stringValue", islandId);
        fields.add("islandId", islandIdValue);
        
        JsonObject ownerUuidValue = new JsonObject();
        ownerUuidValue.addProperty("stringValue", ownerUuid);
        fields.add("ownerUuid", ownerUuidValue);
        
        JsonObject ownerNameValue = new JsonObject();
        ownerNameValue.addProperty("stringValue", ownerName);
        fields.add("ownerName", ownerNameValue);
        
        JsonObject islandNameValue = new JsonObject();
        islandNameValue.addProperty("stringValue", islandName);
        fields.add("islandName", islandNameValue);
        
        JsonObject sizeValue = new JsonObject();
        sizeValue.addProperty("integerValue", size);
        fields.add("size", sizeValue);
        
        JsonObject isPublicValue = new JsonObject();
        isPublicValue.addProperty("booleanValue", isPublic);
        fields.add("isPublic", isPublicValue);
        
        JsonObject createdAtValue = new JsonObject();
        createdAtValue.addProperty("integerValue", createdAt);
        fields.add("createdAt", createdAtValue);
        
        JsonObject lastActivityValue = new JsonObject();
        lastActivityValue.addProperty("integerValue", lastActivity);
        fields.add("lastActivity", lastActivityValue);
        
        // members 배열
        JsonObject membersValue = new JsonObject();
        JsonObject membersArray = new JsonObject();
        JsonArray membersValues = new JsonArray();
        for (IslandMemberDTO member : members) {
            JsonObject memberValue = new JsonObject();
            memberValue.add("mapValue", member.toJsonObject());
            membersValues.add(memberValue);
        }
        membersArray.add("values", membersValues);
        membersValue.add("arrayValue", membersArray);
        fields.add("members", membersValue);
        
        // workers 배열
        JsonObject workersValue = new JsonObject();
        JsonObject workersArray = new JsonObject();
        JsonArray workersValues = new JsonArray();
        for (IslandWorkerDTO worker : workers) {
            JsonObject workerValue = new JsonObject();
            workerValue.add("mapValue", worker.toJsonObject());
            workersValues.add(workerValue);
        }
        workersArray.add("values", workersValues);
        workersValue.add("arrayValue", workersArray);
        fields.add("workers", workersValue);
        
        // contributions 맵
        JsonObject contributionsValue = new JsonObject();
        JsonObject contributionsMap = new JsonObject();
        JsonObject contributionsFields = new JsonObject();
        contributions.forEach((uuid, value) -> {
            JsonObject contribValue = new JsonObject();
            contribValue.addProperty("integerValue", value);
            contributionsFields.add(uuid, contribValue);
        });
        contributionsMap.add("fields", contributionsFields);
        contributionsValue.add("mapValue", contributionsMap);
        fields.add("contributions", contributionsValue);
        
        // spawnData
        JsonObject spawnDataValue = new JsonObject();
        spawnDataValue.add("mapValue", spawnData.toJsonObject());
        fields.add("spawnData", spawnDataValue);
        
        // upgradeData
        JsonObject upgradeDataValue = new JsonObject();
        upgradeDataValue.add("mapValue", upgradeData.toJsonObject());
        fields.add("upgradeData", upgradeDataValue);
        
        // permissions
        JsonObject permissionsValue = new JsonObject();
        permissionsValue.add("mapValue", permissions.toJsonObject());
        fields.add("permissions", permissionsValue);
        
        // pendingInvites 배열
        JsonObject invitesValue = new JsonObject();
        JsonObject invitesArray = new JsonObject();
        JsonArray invitesValues = new JsonArray();
        for (IslandInviteDTO invite : pendingInvites) {
            JsonObject inviteValue = new JsonObject();
            inviteValue.add("mapValue", invite.toJsonObject());
            invitesValues.add(inviteValue);
        }
        invitesArray.add("values", invitesValues);
        invitesValue.add("arrayValue", invitesArray);
        fields.add("pendingInvites", invitesValue);
        
        // recentVisits 배열
        JsonObject visitsValue = new JsonObject();
        JsonObject visitsArray = new JsonObject();
        JsonArray visitsValues = new JsonArray();
        for (IslandVisitDTO visit : recentVisits) {
            JsonObject visitValue = new JsonObject();
            visitValue.add("mapValue", visit.toJsonObject());
            visitsValues.add(visitValue);
        }
        visitsArray.add("values", visitsValues);
        visitsValue.add("arrayValue", visitsArray);
        fields.add("recentVisits", visitsValue);
        
        JsonObject totalResetsValue = new JsonObject();
        totalResetsValue.addProperty("integerValue", totalResets);
        fields.add("totalResets", totalResetsValue);
        
        if (deletionScheduledAt != null) {
            JsonObject deletionValue = new JsonObject();
            deletionValue.addProperty("integerValue", deletionScheduledAt);
            fields.add("deletionScheduledAt", deletionValue);
        }
        
        // settings
        JsonObject settingsValue = new JsonObject();
        settingsValue.add("mapValue", settings.toJsonObject());
        fields.add("settings", settingsValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static IslandDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            throw new IllegalArgumentException("Invalid IslandDTO JSON: missing fields");
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        // 기본 필드 파싱
        String islandId = fields.has("islandId") && fields.getAsJsonObject("islandId").has("stringValue")
                ? fields.getAsJsonObject("islandId").get("stringValue").getAsString()
                : "";
                
        String ownerUuid = fields.has("ownerUuid") && fields.getAsJsonObject("ownerUuid").has("stringValue")
                ? fields.getAsJsonObject("ownerUuid").get("stringValue").getAsString()
                : "";
                
        String ownerName = fields.has("ownerName") && fields.getAsJsonObject("ownerName").has("stringValue")
                ? fields.getAsJsonObject("ownerName").get("stringValue").getAsString()
                : "";
                
        String islandName = fields.has("islandName") && fields.getAsJsonObject("islandName").has("stringValue")
                ? fields.getAsJsonObject("islandName").get("stringValue").getAsString()
                : "";
                
        int size = fields.has("size") && fields.getAsJsonObject("size").has("integerValue")
                ? fields.getAsJsonObject("size").get("integerValue").getAsInt()
                : 85;
                
        boolean isPublic = fields.has("isPublic") && fields.getAsJsonObject("isPublic").has("booleanValue")
                ? fields.getAsJsonObject("isPublic").get("booleanValue").getAsBoolean()
                : false;
                
        long createdAt = fields.has("createdAt") && fields.getAsJsonObject("createdAt").has("integerValue")
                ? fields.getAsJsonObject("createdAt").get("integerValue").getAsLong()
                : System.currentTimeMillis();
                
        long lastActivity = fields.has("lastActivity") && fields.getAsJsonObject("lastActivity").has("integerValue")
                ? fields.getAsJsonObject("lastActivity").get("integerValue").getAsLong()
                : System.currentTimeMillis();
        
        // members 배열 파싱
        List<IslandMemberDTO> members = new ArrayList<>();
        if (fields.has("members") && fields.getAsJsonObject("members").has("arrayValue")) {
            JsonObject membersArray = fields.getAsJsonObject("members").getAsJsonObject("arrayValue");
            if (membersArray.has("values")) {
                JsonArray membersValues = membersArray.getAsJsonArray("values");
                for (JsonElement element : membersValues) {
                    if (element.isJsonObject() && element.getAsJsonObject().has("mapValue")) {
                        members.add(IslandMemberDTO.fromJsonObject(element.getAsJsonObject().getAsJsonObject("mapValue")));
                    }
                }
            }
        }
        
        // workers 배열 파싱
        List<IslandWorkerDTO> workers = new ArrayList<>();
        if (fields.has("workers") && fields.getAsJsonObject("workers").has("arrayValue")) {
            JsonObject workersArray = fields.getAsJsonObject("workers").getAsJsonObject("arrayValue");
            if (workersArray.has("values")) {
                JsonArray workersValues = workersArray.getAsJsonArray("values");
                for (JsonElement element : workersValues) {
                    if (element.isJsonObject() && element.getAsJsonObject().has("mapValue")) {
                        workers.add(IslandWorkerDTO.fromJsonObject(element.getAsJsonObject().getAsJsonObject("mapValue")));
                    }
                }
            }
        }
        
        // contributions 맵 파싱
        Map<String, Long> contributions = new HashMap<>();
        if (fields.has("contributions") && fields.getAsJsonObject("contributions").has("mapValue")) {
            JsonObject contribMap = fields.getAsJsonObject("contributions").getAsJsonObject("mapValue");
            if (contribMap.has("fields")) {
                JsonObject contribFields = contribMap.getAsJsonObject("fields");
                for (Map.Entry<String, JsonElement> entry : contribFields.entrySet()) {
                    if (entry.getValue().isJsonObject() && entry.getValue().getAsJsonObject().has("integerValue")) {
                        contributions.put(entry.getKey(), entry.getValue().getAsJsonObject().get("integerValue").getAsLong());
                    }
                }
            }
        }
        
        // spawnData 파싱
        IslandSpawnDTO spawnData = fields.has("spawnData") && fields.getAsJsonObject("spawnData").has("mapValue")
                ? IslandSpawnDTO.fromJsonObject(fields.getAsJsonObject("spawnData").getAsJsonObject("mapValue"))
                : IslandSpawnDTO.createDefault();
        
        // upgradeData 파싱
        IslandUpgradeDTO upgradeData = fields.has("upgradeData") && fields.getAsJsonObject("upgradeData").has("mapValue")
                ? IslandUpgradeDTO.fromJsonObject(fields.getAsJsonObject("upgradeData").getAsJsonObject("mapValue"))
                : IslandUpgradeDTO.createDefault();
        
        // permissions 파싱
        IslandPermissionDTO permissions = fields.has("permissions") && fields.getAsJsonObject("permissions").has("mapValue")
                ? IslandPermissionDTO.fromJsonObject(fields.getAsJsonObject("permissions").getAsJsonObject("mapValue"))
                : IslandPermissionDTO.createDefault();
        
        // pendingInvites 배열 파싱
        List<IslandInviteDTO> pendingInvites = new ArrayList<>();
        if (fields.has("pendingInvites") && fields.getAsJsonObject("pendingInvites").has("arrayValue")) {
            JsonObject invitesArray = fields.getAsJsonObject("pendingInvites").getAsJsonObject("arrayValue");
            if (invitesArray.has("values")) {
                JsonArray invitesValues = invitesArray.getAsJsonArray("values");
                for (JsonElement element : invitesValues) {
                    if (element.isJsonObject() && element.getAsJsonObject().has("mapValue")) {
                        IslandInviteDTO invite = IslandInviteDTO.fromJsonObject(element.getAsJsonObject().getAsJsonObject("mapValue"));
                        if (!invite.isExpired()) {
                            pendingInvites.add(invite);
                        }
                    }
                }
            }
        }
        
        // recentVisits 배열 파싱
        List<IslandVisitDTO> recentVisits = new ArrayList<>();
        if (fields.has("recentVisits") && fields.getAsJsonObject("recentVisits").has("arrayValue")) {
            JsonObject visitsArray = fields.getAsJsonObject("recentVisits").getAsJsonObject("arrayValue");
            if (visitsArray.has("values")) {
                JsonArray visitsValues = visitsArray.getAsJsonArray("values");
                for (JsonElement element : visitsValues) {
                    if (element.isJsonObject() && element.getAsJsonObject().has("mapValue")) {
                        recentVisits.add(IslandVisitDTO.fromJsonObject(element.getAsJsonObject().getAsJsonObject("mapValue")));
                    }
                }
            }
        }
        
        int totalResets = fields.has("totalResets") && fields.getAsJsonObject("totalResets").has("integerValue")
                ? fields.getAsJsonObject("totalResets").get("integerValue").getAsInt()
                : 0;
        
        Long deletionScheduledAt = null;
        if (fields.has("deletionScheduledAt") && fields.getAsJsonObject("deletionScheduledAt").has("integerValue")) {
            deletionScheduledAt = fields.getAsJsonObject("deletionScheduledAt").get("integerValue").getAsLong();
        }
        
        // settings 파싱
        IslandSettingsDTO settings = fields.has("settings") && fields.getAsJsonObject("settings").has("mapValue")
                ? IslandSettingsDTO.fromJsonObject(fields.getAsJsonObject("settings").getAsJsonObject("mapValue"))
                : IslandSettingsDTO.createDefault();
        
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