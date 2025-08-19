package com.febrie.rpg.dto.island;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
/**
 * 섬 내 역할 정의
 *
 * @author Febrie, CoffeeTory
 */
public enum IslandRole {
    OWNER(4),
    CO_OWNER(3),
    MEMBER(2),
    WORKER(1),
    VISITOR(0);

    private final int priority;

    IslandRole(int priority) {
        this.priority = priority;
    }

    /**
     * 언어 파일에서 표시 이름을 가져오기 위한 키 반환
     */
    public String getLangKey() {
        return switch (this) {
            case OWNER -> "island.roles.owner";
            case CO_OWNER -> "island.roles.sub-owner";
            case MEMBER -> "island.roles.member";
            case WORKER -> "island.roles.worker";
            case VISITOR -> "island.roles.visitor";
        };
    }

    public int getPriority() {
        return priority;
    }

    /**
     * 특정 역할보다 높은 권한인지 확인
     */
    @Contract(pure = true)
    public boolean isHigherThan(@NotNull IslandRole other) {
        return this.priority > other.priority;
    }

    /**
     * 특정 역할 이상의 권한인지 확인
     */
    @Contract(pure = true)
    public boolean isAtLeast(@NotNull IslandRole other) {
        return this.priority >= other.priority;
    }
}