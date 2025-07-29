package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.JsonUtil;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        JsonObject fields = new JsonObject();

        fields.add("uuid", JsonUtil.createStringValue(uuid));
        fields.add("name", JsonUtil.createStringValue(name));
        fields.add("isCoOwner", JsonUtil.createBooleanValue(isCoOwner));
        fields.add("joinedAt", JsonUtil.createIntegerValue(joinedAt));
        fields.add("lastActivity", JsonUtil.createIntegerValue(lastActivity));

        if (personalSpawn != null) {
            fields.add("personalSpawn", JsonUtil.createMapValue(personalSpawn.toJsonObject()));
        }

        return JsonUtil.wrapInDocument(fields);
    }

    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static IslandMemberDTO fromJsonObject(@NotNull JsonObject json) {
        JsonObject fields = JsonUtil.unwrapDocument(json);

        String uuid = JsonUtil.getStringValue(fields, "uuid", "");
        String name = JsonUtil.getStringValue(fields, "name", "");
        boolean isCoOwner = JsonUtil.getBooleanValue(fields, "isCoOwner", false);
        long joinedAt = JsonUtil.getLongValue(fields, "joinedAt", System.currentTimeMillis());
        long lastActivity = JsonUtil.getLongValue(fields, "lastActivity", System.currentTimeMillis());

        IslandSpawnPointDTO personalSpawn = null;
        JsonObject personalSpawnJson = JsonUtil.getMapValue(fields, "personalSpawn");
        if (!personalSpawnJson.entrySet().isEmpty()) {
            personalSpawn = IslandSpawnPointDTO.fromJsonObject(personalSpawnJson);
        }

        return new IslandMemberDTO(uuid, name, isCoOwner, joinedAt, lastActivity, personalSpawn);
    }

}