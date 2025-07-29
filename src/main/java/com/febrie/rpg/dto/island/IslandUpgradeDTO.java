package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.JsonUtil;
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
        int memberLimitLevel, // 멤버 제한 업그레이드 레벨 (0 = 5명, max = 4 for 40명)
        int workerLimitLevel, // 알바 제한 업그레이드 레벨 (0 = 2명, max = 4 for 30명)
        int memberLimit, // 현재 섬원 최대치 (기본 5)
        int workerLimit, // 현재 알바생 최대치 (기본 2)
        long lastUpgradeAt
) {
    /**
     * 기본 업그레이드 정보 생성
     */
    public static IslandUpgradeDTO createDefault() {
        return new IslandUpgradeDTO(
                0, // 초기 크기 레벨
                0, // 초기 멤버 제한 레벨
                0, // 초기 알바 제한 레벨
                5, // 기본 섬원 5명
                2, // 기본 알바 2명
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
    
    // ===== 서비스 호환성을 위한 Alias 메서드들 =====
    
    public int currentSize() { return getCurrentSize(); }
    public int maxSize() { return getCurrentSize(); }
    public int sizeUpgrades() { return sizeLevel; }
    public int memberSlots() { return memberLimit; }
    public int workerSlots() { return workerLimit; }
    public int spawnSlots() { return 10; } // 기본값
    public long lastUpgraded() { return lastUpgradeAt; }
    
    /**
     * JsonObject로 변환 (Firebase 저장용)
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        fields.add("sizeLevel", JsonUtil.createIntegerValue(sizeLevel));
        fields.add("memberLimitLevel", JsonUtil.createIntegerValue(memberLimitLevel));
        fields.add("workerLimitLevel", JsonUtil.createIntegerValue(workerLimitLevel));
        fields.add("memberLimit", JsonUtil.createIntegerValue(memberLimit));
        fields.add("workerLimit", JsonUtil.createIntegerValue(workerLimit));
        fields.add("lastUpgradeAt", JsonUtil.createIntegerValue(lastUpgradeAt));
        
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
        
        int sizeLevel = (int) JsonUtil.getLongValue(fields, "sizeLevel", 0);
        int memberLimitLevel = (int) JsonUtil.getLongValue(fields, "memberLimitLevel", 0);
        int workerLimitLevel = (int) JsonUtil.getLongValue(fields, "workerLimitLevel", 0);
        int memberLimit = (int) JsonUtil.getLongValue(fields, "memberLimit", 5);
        int workerLimit = (int) JsonUtil.getLongValue(fields, "workerLimit", 2);
        long lastUpgradeAt = JsonUtil.getLongValue(fields, "lastUpgradeAt", System.currentTimeMillis());
        
        return new IslandUpgradeDTO(sizeLevel, memberLimitLevel, workerLimitLevel, memberLimit, workerLimit, lastUpgradeAt);
    }
    
}