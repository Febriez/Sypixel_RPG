package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.JsonUtil;
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
        JsonObject fields = new JsonObject();
        
        fields.add("uuid", JsonUtil.createStringValue(uuid));
        fields.add("name", JsonUtil.createStringValue(name));
        fields.add("hiredAt", JsonUtil.createIntegerValue(hiredAt));
        fields.add("lastActivity", JsonUtil.createIntegerValue(lastActivity));
        
        return JsonUtil.wrapInDocument(fields);
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static IslandWorkerDTO fromJsonObject(@NotNull JsonObject json) {
        JsonObject fields = JsonUtil.unwrapDocument(json);
        
        String uuid = JsonUtil.getStringValue(fields, "uuid", "");
        String name = JsonUtil.getStringValue(fields, "name", "");
        long hiredAt = JsonUtil.getLongValue(fields, "hiredAt", System.currentTimeMillis());
        long lastActivity = JsonUtil.getLongValue(fields, "lastActivity", System.currentTimeMillis());
        
        return new IslandWorkerDTO(uuid, name, hiredAt, lastActivity);
    }
    
}