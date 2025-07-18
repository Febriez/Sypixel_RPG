package com.febrie.rpg.island.dto;

import org.jetbrains.annotations.NotNull;

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
}