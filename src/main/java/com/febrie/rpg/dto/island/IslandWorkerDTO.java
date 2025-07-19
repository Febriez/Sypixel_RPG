package com.febrie.rpg.dto.island;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 섬 알바생 정보 DTO (Record)
 *
 * @author Febrie, CoffeeTory
 */
public record IslandWorkerDTO(
        @NotNull String uuid,
        @NotNull String name,
        long hiredAt,
        long lastActivity
) {
    /**
     * 새 알바생 생성
     */
    public static IslandWorkerDTO createNew(String uuid, String name) {
        return new IslandWorkerDTO(
                uuid,
                name,
                System.currentTimeMillis(),
                System.currentTimeMillis()
        );
    }
    
    /**
     * JsonObject로 변환 (Firebase 저장용)
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        JsonObject uuidValue = new JsonObject();
        uuidValue.addProperty("stringValue", uuid);
        fields.add("uuid", uuidValue);
        
        JsonObject nameValue = new JsonObject();
        nameValue.addProperty("stringValue", name);
        fields.add("name", nameValue);
        
        JsonObject hiredAtValue = new JsonObject();
        hiredAtValue.addProperty("integerValue", hiredAt);
        fields.add("hiredAt", hiredAtValue);
        
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
    public static IslandWorkerDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            throw new IllegalArgumentException("Invalid IslandWorkerDTO JSON: missing fields");
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        String uuid = fields.has("uuid") && fields.getAsJsonObject("uuid").has("stringValue")
                ? fields.getAsJsonObject("uuid").get("stringValue").getAsString()
                : "";
                
        String name = fields.has("name") && fields.getAsJsonObject("name").has("stringValue")
                ? fields.getAsJsonObject("name").get("stringValue").getAsString()
                : "";
                
        long hiredAt = fields.has("hiredAt") && fields.getAsJsonObject("hiredAt").has("integerValue")
                ? fields.getAsJsonObject("hiredAt").get("integerValue").getAsLong()
                : System.currentTimeMillis();
                
        long lastActivity = fields.has("lastActivity") && fields.getAsJsonObject("lastActivity").has("integerValue")
                ? fields.getAsJsonObject("lastActivity").get("integerValue").getAsLong()
                : System.currentTimeMillis();
        
        return new IslandWorkerDTO(uuid, name, hiredAt, lastActivity);
    }
    
}