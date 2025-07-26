package com.febrie.rpg.dto.player;

import com.febrie.rpg.job.JobType;
import com.febrie.rpg.util.JsonUtil;
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
        @Nullable JobType job,
        boolean isAdmin
) {
    /**
     * 기본 생성자 - 신규 플레이어용
     */
    public PlayerDTO(String uuid, String name) {
        this(uuid, name, System.currentTimeMillis(), 0L, null, false);
    }
    
    /**
     * JsonObject로 변환
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject fields = new JsonObject();
        
        fields.add("uuid", JsonUtil.createStringValue(uuid));
        fields.add("name", JsonUtil.createStringValue(name));
        fields.add("lastLogin", JsonUtil.createIntegerValue(lastLogin));
        fields.add("totalPlaytime", JsonUtil.createIntegerValue(totalPlaytime));
        
        if (job != null) {
            fields.add("job", JsonUtil.createStringValue(job.name()));
        }
        
        fields.add("isAdmin", JsonUtil.createBooleanValue(isAdmin));
        
        return JsonUtil.wrapInDocument(fields);
    }
    
    /**
     * JsonObject에서 PlayerDTO 생성
     */
    @NotNull
    public static PlayerDTO fromJsonObject(@NotNull JsonObject json) {
        JsonObject fields = JsonUtil.unwrapDocument(json);
        
        String uuid = JsonUtil.getStringValue(fields, "uuid", "");
        String name = JsonUtil.getStringValue(fields, "name", "");
        long lastLogin = JsonUtil.getLongValue(fields, "lastLogin", System.currentTimeMillis());
        long totalPlaytime = JsonUtil.getLongValue(fields, "totalPlaytime", 0L);
        
        JobType job = null;
        String jobName = JsonUtil.getStringValue(fields, "job");
        if (jobName != null && !jobName.isEmpty()) {
            try {
                job = JobType.valueOf(jobName);
            } catch (IllegalArgumentException ignored) {
                // Invalid job type, keep as null
            }
        }
        
        boolean isAdmin = JsonUtil.getBooleanValue(fields, "isAdmin", false);
        
        return new PlayerDTO(uuid, name, lastLogin, totalPlaytime, job, isAdmin);
    }
}