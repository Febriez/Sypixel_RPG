package com.febrie.rpg.dto.player;

import com.febrie.rpg.job.JobType;
import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import net.kyori.adventure.text.Component;
/**
 * 플레이어 기본 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record PlayerDTO(String uuid, String name, long lastLogin, long totalPlaytime, @Nullable JobType job,
                        boolean isAdmin) {
    /**
     * 기본 생성자 - 신규 플레이어용
     */
    public PlayerDTO(String uuid, String name) {
        this(uuid, name, System.currentTimeMillis(), 0L, null, false);
    }

    /**
     * Map으로 변환
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("uuid", uuid);
        map.put("name", name);
        map.put("lastLogin", lastLogin);
        map.put("totalPlaytime", totalPlaytime);

        if (job != null) {
            map.put("job", job.name());
        }

        map.put("isAdmin", isAdmin);

        return map;
    }

    /**
     * Map에서 PlayerDTO 생성
     */
    @NotNull
    public static PlayerDTO fromMap(@NotNull Map<String, Object> map) {
        String uuid = FirestoreUtils.getString(map, "uuid", "");
        String name = FirestoreUtils.getString(map, "name", "");
        long lastLogin = FirestoreUtils.getLong(map, "lastLogin", System.currentTimeMillis());
        long totalPlaytime = FirestoreUtils.getLong(map, "totalPlaytime", 0L);

        JobType job = null;
        String jobName = FirestoreUtils.getString(map, "job", null);
        if (jobName != null && !jobName.isEmpty()) {
            try {
                job = JobType.valueOf(jobName);
            } catch (IllegalArgumentException ignored) {
                // Invalid job type, keep as null
            }
        }

        boolean isAdmin = FirestoreUtils.getBoolean(map, "isAdmin", false);

        return new PlayerDTO(uuid, name, lastLogin, totalPlaytime, job, isAdmin);
    }
}