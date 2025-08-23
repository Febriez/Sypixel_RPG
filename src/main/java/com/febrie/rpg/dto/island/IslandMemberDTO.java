package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
     * Map으로 변환 (Firebase 저장용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("uuid", uuid);
        map.put("name", name);
        map.put("isCoOwner", isCoOwner);
        map.put("joinedAt", joinedAt);
        map.put("lastActivity", lastActivity);

        if (personalSpawn != null) {
            map.put("personalSpawn", personalSpawn.toMap());
        }

        return map;
    }

    /**
     * Map에서 생성
     */
    @NotNull
    public static IslandMemberDTO fromMap(@NotNull Map<String, Object> map) {
        String uuid = Objects.requireNonNull(FirestoreUtils.getString(map, "uuid", ""), "UUID cannot be null");
        String name = Objects.requireNonNull(FirestoreUtils.getString(map, "name", ""), "Name cannot be null");
        boolean isCoOwner = FirestoreUtils.getBoolean(map, "isCoOwner", false);
        long joinedAt = FirestoreUtils.getLong(map, "joinedAt", System.currentTimeMillis());
        long lastActivity = FirestoreUtils.getLong(map, "lastActivity", System.currentTimeMillis());

        Map<String, Object> personalSpawnMap = FirestoreUtils.getMapOrNull(map, "personalSpawn", null);
        IslandSpawnPointDTO personalSpawn = personalSpawnMap != null ? IslandSpawnPointDTO.fromMap(personalSpawnMap) : null;

        return new IslandMemberDTO(uuid, name, isCoOwner, joinedAt, lastActivity, personalSpawn);
    }

}