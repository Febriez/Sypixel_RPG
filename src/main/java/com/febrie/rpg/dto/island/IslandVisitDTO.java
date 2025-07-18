package com.febrie.rpg.dto.island;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 섬 방문 기록 DTO (Record)
 *
 * @author Febrie, CoffeeTory
 */
public record IslandVisitDTO(
        @NotNull String visitorUuid,
        @NotNull String visitorName,
        long visitedAt,
        long duration // 방문 시간 (밀리초)
) {
    /**
     * 새 방문 기록 생성 (방문 시작)
     */
    public static IslandVisitDTO startVisit(String visitorUuid, String visitorName) {
        return new IslandVisitDTO(
                visitorUuid,
                visitorName,
                System.currentTimeMillis(),
                0
        );
    }
    
    /**
     * 방문 종료 시 duration 업데이트
     */
    public IslandVisitDTO endVisit() {
        return new IslandVisitDTO(
                visitorUuid,
                visitorName,
                visitedAt,
                System.currentTimeMillis() - visitedAt
        );
    }
    
    /**
     * 방문 시간을 읽기 쉬운 형식으로 변환
     */
    public String getFormattedDuration() {
        if (duration == 0) {
            return "방문 중";
        }
        
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%d시간 %d분", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d분 %d초", minutes, seconds % 60);
        } else {
            return String.format("%d초", seconds);
        }
    }
    
    /**
     * Map으로 변환 (Firebase 저장용)
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("visitorUuid", visitorUuid);
        map.put("visitorName", visitorName);
        map.put("visitedAt", visitedAt);
        map.put("duration", duration);
        return map;
    }
    
    /**
     * Map에서 생성
     */
    public static IslandVisitDTO fromMap(Map<String, Object> map) {
        if (map == null) return null;
        
        return new IslandVisitDTO(
                (String) map.get("visitorUuid"),
                (String) map.get("visitorName"),
                ((Number) map.get("visitedAt")).longValue(),
                ((Number) map.get("duration")).longValue()
        );
    }
}