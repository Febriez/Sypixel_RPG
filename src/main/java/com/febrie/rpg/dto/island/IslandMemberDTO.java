package com.febrie.rpg.dto.island;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 섬원 정보 DTO (Record)
 *
 * @author Febrie, CoffeeTory
 */
public record IslandMemberDTO(
        @NotNull String uuid,
        @NotNull String name,
        boolean isCoOwner, // 부섬장 여부
        long joinedAt,
        long lastActivity,
        @Nullable IslandSpawnPointDTO personalSpawn // 개인 스폰 위치
) {
    /**
     * 새 섬원 생성
     */
    public static IslandMemberDTO createNew(String uuid, String name, boolean isCoOwner) {
        return new IslandMemberDTO(
                uuid,
                name,
                isCoOwner,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                null
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
        
        JsonObject isCoOwnerValue = new JsonObject();
        isCoOwnerValue.addProperty("booleanValue", isCoOwner);
        fields.add("isCoOwner", isCoOwnerValue);
        
        JsonObject joinedAtValue = new JsonObject();
        joinedAtValue.addProperty("integerValue", joinedAt);
        fields.add("joinedAt", joinedAtValue);
        
        JsonObject lastActivityValue = new JsonObject();
        lastActivityValue.addProperty("integerValue", lastActivity);
        fields.add("lastActivity", lastActivityValue);
        
        if (personalSpawn != null) {
            JsonObject personalSpawnValue = new JsonObject();
            personalSpawnValue.add("mapValue", personalSpawn.toJsonObject());
            fields.add("personalSpawn", personalSpawnValue);
        }
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static IslandMemberDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            throw new IllegalArgumentException("Invalid IslandMemberDTO JSON: missing fields");
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        String uuid = fields.has("uuid") && fields.getAsJsonObject("uuid").has("stringValue")
                ? fields.getAsJsonObject("uuid").get("stringValue").getAsString()
                : "";
                
        String name = fields.has("name") && fields.getAsJsonObject("name").has("stringValue")
                ? fields.getAsJsonObject("name").get("stringValue").getAsString()
                : "";
                
        boolean isCoOwner = fields.has("isCoOwner") && fields.getAsJsonObject("isCoOwner").has("booleanValue")
                ? fields.getAsJsonObject("isCoOwner").get("booleanValue").getAsBoolean()
                : false;
                
        long joinedAt = fields.has("joinedAt") && fields.getAsJsonObject("joinedAt").has("integerValue")
                ? fields.getAsJsonObject("joinedAt").get("integerValue").getAsLong()
                : System.currentTimeMillis();
                
        long lastActivity = fields.has("lastActivity") && fields.getAsJsonObject("lastActivity").has("integerValue")
                ? fields.getAsJsonObject("lastActivity").get("integerValue").getAsLong()
                : System.currentTimeMillis();
                
        IslandSpawnPointDTO personalSpawn = null;
        if (fields.has("personalSpawn") && fields.getAsJsonObject("personalSpawn").has("mapValue")) {
            personalSpawn = IslandSpawnPointDTO.fromJsonObject(fields.getAsJsonObject("personalSpawn").getAsJsonObject("mapValue"));
        }
        
        return new IslandMemberDTO(uuid, name, isCoOwner, joinedAt, lastActivity, personalSpawn);
    }
    
}