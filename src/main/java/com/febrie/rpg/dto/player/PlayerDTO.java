package com.febrie.rpg.dto.player;

import com.febrie.rpg.job.JobType;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 플레이어 기본 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record PlayerDTO(
        String uuid,
        String name,
        long lastLogin,
        long totalPlaytime,
        @Nullable JobType job
) {
    /**
     * 기본 생성자 - 신규 플레이어용
     */
    public PlayerDTO(String uuid, String name) {
        this(uuid, name, System.currentTimeMillis(), 0L, null);
    }
    
    /**
     * JsonObject로 변환
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
        
        JsonObject lastLoginValue = new JsonObject();
        lastLoginValue.addProperty("integerValue", lastLogin);
        fields.add("lastLogin", lastLoginValue);
        
        JsonObject totalPlaytimeValue = new JsonObject();
        totalPlaytimeValue.addProperty("integerValue", totalPlaytime);
        fields.add("totalPlaytime", totalPlaytimeValue);
        
        if (job != null) {
            JsonObject jobValue = new JsonObject();
            jobValue.addProperty("stringValue", job.name());
            fields.add("job", jobValue);
        }
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 PlayerDTO 생성
     */
    @NotNull
    public static PlayerDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            throw new IllegalArgumentException("Invalid PlayerDTO JSON: missing fields");
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        String uuid = fields.has("uuid") && fields.getAsJsonObject("uuid").has("stringValue")
                ? fields.getAsJsonObject("uuid").get("stringValue").getAsString()
                : "";
                
        String name = fields.has("name") && fields.getAsJsonObject("name").has("stringValue")
                ? fields.getAsJsonObject("name").get("stringValue").getAsString()
                : "";
                
        long lastLogin = fields.has("lastLogin") && fields.getAsJsonObject("lastLogin").has("integerValue")
                ? fields.getAsJsonObject("lastLogin").get("integerValue").getAsLong()
                : System.currentTimeMillis();
                
        long totalPlaytime = fields.has("totalPlaytime") && fields.getAsJsonObject("totalPlaytime").has("integerValue")
                ? fields.getAsJsonObject("totalPlaytime").get("integerValue").getAsLong()
                : 0L;
                
        JobType job = null;
        if (fields.has("job") && fields.getAsJsonObject("job").has("stringValue")) {
            String jobName = fields.getAsJsonObject("job").get("stringValue").getAsString();
            try {
                job = JobType.valueOf(jobName);
            } catch (IllegalArgumentException ignored) {
                // Invalid job type, keep as null
            }
        }
        
        return new PlayerDTO(uuid, name, lastLogin, totalPlaytime, job);
    }
}