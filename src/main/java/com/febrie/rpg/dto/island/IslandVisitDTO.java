package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.JsonUtil;
import com.google.gson.JsonObject;
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
     * JsonObject로 변환 (Firebase 저장용)
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        fields.add("visitorUuid", JsonUtil.createStringValue(visitorUuid));
        fields.add("visitorName", JsonUtil.createStringValue(visitorName));
        fields.add("visitedAt", JsonUtil.createIntegerValue(visitedAt));
        fields.add("duration", JsonUtil.createIntegerValue(duration));
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static IslandVisitDTO fromJsonObject(@NotNull JsonObject json) {
        JsonUtil.validateDTOJson(json, "IslandVisitDTO");
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        try {
            // 필수 필드 검증
            JsonUtil.validateRequiredField(fields, "visitorUuid", "IslandVisitDTO");
            JsonUtil.validateRequiredField(fields, "visitorName", "IslandVisitDTO");
            
            String visitorUuid = JsonUtil.getStringValue(fields, "visitorUuid");
            String visitorName = JsonUtil.getStringValue(fields, "visitorName");
            long visitedAt = JsonUtil.getLongValue(fields, "visitedAt", System.currentTimeMillis());
            long duration = JsonUtil.getLongValue(fields, "duration", 0);
            
            // 유효성 검증
            if (visitorUuid.isEmpty()) {
                throw new IllegalArgumentException(
                    "Invalid IslandVisitDTO: visitorUuid cannot be empty"
                );
            }
            
            if (visitorName.isEmpty()) {
                throw new IllegalArgumentException(
                    "Invalid IslandVisitDTO: visitorName cannot be empty"
                );
            }
            
            if (visitedAt < 0) {
                throw new IllegalArgumentException(
                    String.format("Invalid IslandVisitDTO: visitedAt cannot be negative, but found: %d", visitedAt)
                );
            }
            
            if (duration < 0) {
                throw new IllegalArgumentException(
                    String.format("Invalid IslandVisitDTO: duration cannot be negative, but found: %d", duration)
                );
            }
            
            if (duration > 0 && visitedAt + duration > System.currentTimeMillis()) {
                throw new IllegalArgumentException(
                    String.format("Invalid IslandVisitDTO: visit end time (%d) cannot be in the future (current time: %d)",
                        visitedAt + duration, System.currentTimeMillis())
                );
            }
            
            return new IslandVisitDTO(visitorUuid, visitorName, visitedAt, duration);
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new IllegalArgumentException(
                String.format("Failed to parse IslandVisitDTO: %s. JSON structure: %s", 
                    e.getMessage(), 
                    json.toString().length() > 200 ? json.toString().substring(0, 200) + "..." : json.toString())
            );
        }
    }
    
}