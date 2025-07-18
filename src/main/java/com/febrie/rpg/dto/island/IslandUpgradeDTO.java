package com.febrie.rpg.dto.island;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 섬 업그레이드 정보 DTO (Record)
 *
 * @author Febrie, CoffeeTory
 */
public record IslandUpgradeDTO(
        int sizeLevel, // 섬 크기 업그레이드 레벨 (0 = 85x85, max = 26 for 475x475)
        int memberLimit, // 현재 섬원 최대치 (기본 3)
        int workerLimit, // 현재 알바생 최대치 (기본 1)
        long lastUpgradeAt
) {
    /**
     * 기본 업그레이드 정보 생성
     */
    public static IslandUpgradeDTO createDefault() {
        return new IslandUpgradeDTO(
                0, // 초기 크기 레벨
                3, // 기본 섬원 3명
                1, // 기본 알바 1명
                System.currentTimeMillis()
        );
    }
    
    /**
     * 현재 섬 크기 계산
     */
    public int getCurrentSize() {
        if (sizeLevel >= 26) {
            return 500; // 최대 크기
        }
        return 85 + (sizeLevel * 15);
    }
    
    /**
     * 다음 업그레이드 후 크기
     */
    public int getNextSize() {
        if (sizeLevel >= 25) {
            return 500; // 475 -> 500 (마지막 업그레이드는 +25)
        } else if (sizeLevel >= 26) {
            return -1; // 더 이상 업그레이드 불가
        }
        return getCurrentSize() + 15;
    }
    
    /**
     * 크기 업그레이드 가능 여부
     */
    public boolean canUpgradeSize() {
        return sizeLevel < 26;
    }
    
    /**
     * 섬원 업그레이드 가능 여부
     */
    public boolean canUpgradeMemberLimit() {
        return memberLimit < 16;
    }
    
    /**
     * JsonObject로 변환 (Firebase 저장용)
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        JsonObject sizeLevelValue = new JsonObject();
        sizeLevelValue.addProperty("integerValue", sizeLevel);
        fields.add("sizeLevel", sizeLevelValue);
        
        JsonObject memberLimitValue = new JsonObject();
        memberLimitValue.addProperty("integerValue", memberLimit);
        fields.add("memberLimit", memberLimitValue);
        
        JsonObject workerLimitValue = new JsonObject();
        workerLimitValue.addProperty("integerValue", workerLimit);
        fields.add("workerLimit", workerLimitValue);
        
        JsonObject lastUpgradeAtValue = new JsonObject();
        lastUpgradeAtValue.addProperty("integerValue", lastUpgradeAt);
        fields.add("lastUpgradeAt", lastUpgradeAtValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static IslandUpgradeDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return createDefault();
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        int sizeLevel = fields.has("sizeLevel") && fields.getAsJsonObject("sizeLevel").has("integerValue")
                ? fields.getAsJsonObject("sizeLevel").get("integerValue").getAsInt()
                : 0;
                
        int memberLimit = fields.has("memberLimit") && fields.getAsJsonObject("memberLimit").has("integerValue")
                ? fields.getAsJsonObject("memberLimit").get("integerValue").getAsInt()
                : 3;
                
        int workerLimit = fields.has("workerLimit") && fields.getAsJsonObject("workerLimit").has("integerValue")
                ? fields.getAsJsonObject("workerLimit").get("integerValue").getAsInt()
                : 1;
                
        long lastUpgradeAt = fields.has("lastUpgradeAt") && fields.getAsJsonObject("lastUpgradeAt").has("integerValue")
                ? fields.getAsJsonObject("lastUpgradeAt").get("integerValue").getAsLong()
                : System.currentTimeMillis();
        
        return new IslandUpgradeDTO(sizeLevel, memberLimit, workerLimit, lastUpgradeAt);
    }
    
    /**
     * Map으로 변환 (Firebase 저장용)
     */
    @Deprecated
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("sizeLevel", sizeLevel);
        map.put("memberLimit", memberLimit);
        map.put("workerLimit", workerLimit);
        map.put("lastUpgradeAt", lastUpgradeAt);
        return map;
    }
    
    /**
     * Map에서 생성
     */
    @Deprecated
    public static IslandUpgradeDTO fromMap(Map<String, Object> map) {
        if (map == null) return createDefault();
        
        return new IslandUpgradeDTO(
                ((Number) map.get("sizeLevel")).intValue(),
                ((Number) map.get("memberLimit")).intValue(),
                ((Number) map.get("workerLimit")).intValue(),
                ((Number) map.get("lastUpgradeAt")).longValue()
        );
    }
}