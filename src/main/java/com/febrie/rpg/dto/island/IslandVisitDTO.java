package com.febrie.rpg.dto.island;

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
        
        JsonObject visitorUuidValue = new JsonObject();
        visitorUuidValue.addProperty("stringValue", visitorUuid);
        fields.add("visitorUuid", visitorUuidValue);
        
        JsonObject visitorNameValue = new JsonObject();
        visitorNameValue.addProperty("stringValue", visitorName);
        fields.add("visitorName", visitorNameValue);
        
        JsonObject visitedAtValue = new JsonObject();
        visitedAtValue.addProperty("integerValue", visitedAt);
        fields.add("visitedAt", visitedAtValue);
        
        JsonObject durationValue = new JsonObject();
        durationValue.addProperty("integerValue", duration);
        fields.add("duration", durationValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static IslandVisitDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            throw new IllegalArgumentException("Invalid IslandVisitDTO JSON: missing fields");
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        String visitorUuid = fields.has("visitorUuid") && fields.getAsJsonObject("visitorUuid").has("stringValue")
                ? fields.getAsJsonObject("visitorUuid").get("stringValue").getAsString()
                : "";
                
        String visitorName = fields.has("visitorName") && fields.getAsJsonObject("visitorName").has("stringValue")
                ? fields.getAsJsonObject("visitorName").get("stringValue").getAsString()
                : "";
                
        long visitedAt = fields.has("visitedAt") && fields.getAsJsonObject("visitedAt").has("integerValue")
                ? fields.getAsJsonObject("visitedAt").get("integerValue").getAsLong()
                : System.currentTimeMillis();
                
        long duration = fields.has("duration") && fields.getAsJsonObject("duration").has("integerValue")
                ? fields.getAsJsonObject("duration").get("integerValue").getAsLong()
                : 0;
        
        return new IslandVisitDTO(visitorUuid, visitorName, visitedAt, duration);
    }
    
    /**
     * Map으로 변환 (Firebase 저장용)
     */
    @Deprecated
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
    @Deprecated
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