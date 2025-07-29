package com.febrie.rpg.dto.player;

import com.febrie.rpg.util.JsonUtil;
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
        
        fields.add("uuid", JsonUtil.createStringValue(uuid.toString()));
        fields.add("name", JsonUtil.createStringValue(name));
        fields.add("level", JsonUtil.createIntegerValue(level));
        fields.add("exp", JsonUtil.createIntegerValue(exp));
        fields.add("totalExp", JsonUtil.createIntegerValue(totalExp));
        fields.add("lastPlayed", JsonUtil.createIntegerValue(lastPlayed));
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static PlayerProfileDTO fromJsonObject(@NotNull JsonObject json) {
        JsonUtil.validateDTOJson(json, "PlayerProfileDTO");
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        String uuidStr = JsonUtil.getStringValue(fields, "uuid", UUID.randomUUID().toString());
        UUID uuid = UUID.fromString(uuidStr);
        
        String name = JsonUtil.getStringValue(fields, "name", "");
        int level = (int) JsonUtil.getLongValue(fields, "level", 1L);
        long exp = JsonUtil.getLongValue(fields, "exp", 0L);
        long totalExp = JsonUtil.getLongValue(fields, "totalExp", 0L);
        long lastPlayed = JsonUtil.getLongValue(fields, "lastPlayed", System.currentTimeMillis());
        
        return new PlayerProfileDTO(uuid, name, level, exp, totalExp, lastPlayed);
    }
}