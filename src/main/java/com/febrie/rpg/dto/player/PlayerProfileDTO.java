package com.febrie.rpg.dto.player;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * 플레이어 프로필 DTO
 * 기본 플레이어 정보
 */
public record PlayerProfileDTO(
        @NotNull UUID uuid,
        @NotNull String name,
        int level,
        long exp,
        long totalExp,
        long lastPlayed
) {
    /**
     * JsonObject로 변환
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        JsonObject uuidValue = new JsonObject();
        uuidValue.addProperty("stringValue", uuid.toString());
        fields.add("uuid", uuidValue);
        
        JsonObject nameValue = new JsonObject();
        nameValue.addProperty("stringValue", name);
        fields.add("name", nameValue);
        
        JsonObject levelValue = new JsonObject();
        levelValue.addProperty("integerValue", level);
        fields.add("level", levelValue);
        
        JsonObject expValue = new JsonObject();
        expValue.addProperty("integerValue", exp);
        fields.add("exp", expValue);
        
        JsonObject totalExpValue = new JsonObject();
        totalExpValue.addProperty("integerValue", totalExp);
        fields.add("totalExp", totalExpValue);
        
        JsonObject lastPlayedValue = new JsonObject();
        lastPlayedValue.addProperty("integerValue", lastPlayed);
        fields.add("lastPlayed", lastPlayedValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static PlayerProfileDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            throw new IllegalArgumentException("Invalid PlayerProfileDTO JSON: missing fields");
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        String uuidStr = fields.has("uuid") && fields.getAsJsonObject("uuid").has("stringValue")
                ? fields.getAsJsonObject("uuid").get("stringValue").getAsString()
                : UUID.randomUUID().toString();
        UUID uuid = UUID.fromString(uuidStr);
        
        String name = fields.has("name") && fields.getAsJsonObject("name").has("stringValue")
                ? fields.getAsJsonObject("name").get("stringValue").getAsString()
                : "";
                
        int level = fields.has("level") && fields.getAsJsonObject("level").has("integerValue")
                ? fields.getAsJsonObject("level").get("integerValue").getAsInt()
                : 1;
                
        long exp = fields.has("exp") && fields.getAsJsonObject("exp").has("integerValue")
                ? fields.getAsJsonObject("exp").get("integerValue").getAsLong()
                : 0;
                
        long totalExp = fields.has("totalExp") && fields.getAsJsonObject("totalExp").has("integerValue")
                ? fields.getAsJsonObject("totalExp").get("integerValue").getAsLong()
                : 0;
                
        long lastPlayed = fields.has("lastPlayed") && fields.getAsJsonObject("lastPlayed").has("integerValue")
                ? fields.getAsJsonObject("lastPlayed").get("integerValue").getAsLong()
                : System.currentTimeMillis();
        
        return new PlayerProfileDTO(uuid, name, level, exp, totalExp, lastPlayed);
    }
}