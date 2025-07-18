package com.febrie.rpg.island.dto;

/**
 * 섬 내 역할 정의
 *
 * @author Febrie, CoffeeTory
 */
public enum IslandRole {
    OWNER("섬장", 4),
    CO_OWNER("부섬장", 3),
    MEMBER("섬원", 2),
    WORKER("알바생", 1),
    VISITOR("방문자", 0);
    
    private final String displayName;
    private final int priority;
    
    IslandRole(String displayName, int priority) {
        this.displayName = displayName;
        this.priority = priority;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getPriority() {
        return priority;
    }
    
    /**
     * 특정 역할보다 높은 권한인지 확인
     */
    public boolean isHigherThan(IslandRole other) {
        return this.priority > other.priority;
    }
    
    /**
     * 특정 역할 이상의 권한인지 확인
     */
    public boolean isAtLeast(IslandRole other) {
        return this.priority >= other.priority;
    }
}