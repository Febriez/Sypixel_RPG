package com.febrie.rpg.dto.island;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어의 섬 관련 데이터 DTO (Record)
 * 플레이어가 어떤 섬에 속해있는지 추적
 *
 * @author Febrie, CoffeeTory
 */
public record PlayerIslandDataDTO(
        @NotNull String playerUuid,
        @Nullable String currentIslandId, // 현재 속한 섬 ID (null = 섬 없음)
        @Nullable IslandRole role, // 섬에서의 역할
        int totalIslandResets, // 총 섬 초기화 횟수
        long totalContribution, // 총 기여도
        long lastJoined,
        long lastActivity
) {
    /**
     * 새 플레이어 데이터 생성 (섬 없음)
     */
    public static PlayerIslandDataDTO createNew(String playerUuid) {
        long now = System.currentTimeMillis();
        return new PlayerIslandDataDTO(
                playerUuid,
                null,
                null,
                0,
                0L, // totalContribution
                now,
                now
        );
    }
    
    /**
     * 섬에 가입
     */
    public PlayerIslandDataDTO joinIsland(String islandId, IslandRole role) {
        long now = System.currentTimeMillis();
        return new PlayerIslandDataDTO(
                playerUuid,
                islandId,
                role,
                totalIslandResets,
                totalContribution,
                now,
                now
        );
    }
    
    /**
     * 섬 떠나기
     */
    public PlayerIslandDataDTO leaveIsland() {
        long now = System.currentTimeMillis();
        return new PlayerIslandDataDTO(
                playerUuid,
                null,
                null,
                totalIslandResets,
                totalContribution,
                lastJoined,
                now
        );
    }
    
    /**
     * 섬 초기화 카운트 증가
     */
    public PlayerIslandDataDTO incrementResetCount() {
        return new PlayerIslandDataDTO(
                playerUuid,
                currentIslandId,
                role,
                totalIslandResets + 1,
                totalContribution,
                lastJoined,
                System.currentTimeMillis()
        );
    }
    
    /**
     * 섬이 있는지 확인
     */
    public boolean hasIsland() {
        return currentIslandId != null;
    }
    
    /**
     * 섬 초기화 가능 여부 (평생 1번만)
     */
    public boolean canResetIsland() {
        return totalIslandResets < 1;
    }
    
    /**
     * JsonObject로 변환 (Firebase 저장용)
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        JsonObject playerUuidValue = new JsonObject();
        playerUuidValue.addProperty("stringValue", playerUuid);
        fields.add("playerUuid", playerUuidValue);
        
        if (currentIslandId != null) {
            JsonObject currentIslandIdValue = new JsonObject();
            currentIslandIdValue.addProperty("stringValue", currentIslandId);
            fields.add("currentIslandId", currentIslandIdValue);
        }
        
        if (role != null) {
            JsonObject roleValue = new JsonObject();
            roleValue.addProperty("stringValue", role.name());
            fields.add("role", roleValue);
        }
        
        JsonObject totalIslandResetsValue = new JsonObject();
        totalIslandResetsValue.addProperty("integerValue", totalIslandResets);
        fields.add("totalIslandResets", totalIslandResetsValue);
        
        JsonObject totalContributionValue = new JsonObject();
        totalContributionValue.addProperty("integerValue", totalContribution);
        fields.add("totalContribution", totalContributionValue);
        
        JsonObject lastJoinedValue = new JsonObject();
        lastJoinedValue.addProperty("integerValue", lastJoined);
        fields.add("lastJoined", lastJoinedValue);
        
        JsonObject lastActivityValue = new JsonObject();
        lastActivityValue.addProperty("integerValue", lastActivity);
        fields.add("lastActivity", lastActivityValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static PlayerIslandDataDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            throw new IllegalArgumentException("Invalid PlayerIslandDataDTO JSON: missing fields");
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        String playerUuid = fields.has("playerUuid") && fields.getAsJsonObject("playerUuid").has("stringValue")
                ? fields.getAsJsonObject("playerUuid").get("stringValue").getAsString()
                : "";
                
        String currentIslandId = null;
        if (fields.has("currentIslandId") && fields.getAsJsonObject("currentIslandId").has("stringValue")) {
            currentIslandId = fields.getAsJsonObject("currentIslandId").get("stringValue").getAsString();
        }
        
        IslandRole role = null;
        if (fields.has("role") && fields.getAsJsonObject("role").has("stringValue")) {
            try {
                role = IslandRole.valueOf(fields.getAsJsonObject("role").get("stringValue").getAsString());
            } catch (IllegalArgumentException e) {
                // 잘못된 역할 이름은 무시
            }
        }
        
        int totalIslandResets = fields.has("totalIslandResets") && fields.getAsJsonObject("totalIslandResets").has("integerValue")
                ? fields.getAsJsonObject("totalIslandResets").get("integerValue").getAsInt()
                : 0;
                
        long totalContribution = fields.has("totalContribution") && fields.getAsJsonObject("totalContribution").has("integerValue")
                ? fields.getAsJsonObject("totalContribution").get("integerValue").getAsLong()
                : 0L;
                
        long lastJoined = fields.has("lastJoined") && fields.getAsJsonObject("lastJoined").has("integerValue")
                ? fields.getAsJsonObject("lastJoined").get("integerValue").getAsLong()
                : System.currentTimeMillis();
                
        long lastActivity = fields.has("lastActivity") && fields.getAsJsonObject("lastActivity").has("integerValue")
                ? fields.getAsJsonObject("lastActivity").get("integerValue").getAsLong()
                : System.currentTimeMillis();
        
        return new PlayerIslandDataDTO(playerUuid, currentIslandId, role, totalIslandResets, totalContribution, lastJoined, lastActivity);
    }
    
}