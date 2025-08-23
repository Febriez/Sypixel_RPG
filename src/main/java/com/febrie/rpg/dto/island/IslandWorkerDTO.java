package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 섬 알바생 정보 DTO (Record)
 *
 * @author Febrie, CoffeeTory
 */
public record IslandWorkerDTO(@NotNull String uuid, @NotNull String name, long hiredAt, long lastActivity) {
    /**
     * 새 알바생 생성
     */
    public static IslandWorkerDTO createNew(String uuid, String name) {
        return new IslandWorkerDTO(uuid, name, System.currentTimeMillis(), System.currentTimeMillis());
    }

    /**
     * Map으로 변환 (Firebase 저장용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("uuid", uuid);
        map.put("name", name);
        map.put("hiredAt", hiredAt);
        map.put("lastActivity", lastActivity);

        return map;
    }

    /**
     * Map에서 생성
     */
    @NotNull
    public static IslandWorkerDTO fromMap(@NotNull Map<String, Object> map) {
        String uuid = FirestoreUtils.getString(map, "uuid", "");
        String name = FirestoreUtils.getString(map, "name", "");
        long hiredAt = FirestoreUtils.getLong(map, "hiredAt", System.currentTimeMillis());
        long lastActivity = FirestoreUtils.getLong(map, "lastActivity", System.currentTimeMillis());

        // Ensure non-null values
        String safeUuid = uuid != null ? uuid : "";
        String safeName = name != null ? name : "";
        
        return new IslandWorkerDTO(safeUuid, safeName, hiredAt, lastActivity);
    }

}