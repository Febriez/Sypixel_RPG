package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.JsonUtil;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * 섬 스폰 관련 정보 DTO (Record)
 *
 * @author Febrie, CoffeeTory
 */
public record IslandSpawnDTO(@NotNull IslandSpawnPointDTO defaultSpawn, // 기본 스폰 위치
                             @NotNull List<IslandSpawnPointDTO> ownerSpawns, // 섬장 개인 스폰 위치들 (최대 3개)
                             @NotNull Map<String, IslandSpawnPointDTO> memberSpawns // 섬원별 개인 스폰 (UUID -> 스폰)
) {
    /**
     * 기본 스폰 데이터 생성
     */
    public static IslandSpawnDTO createDefault() {
        return new IslandSpawnDTO(IslandSpawnPointDTO.createDefault(), List.of(), Map.of());
    }

    /**
     * 특정 플레이어의 개인 스폰 위치 가져오기
     */
    @Nullable
    public IslandSpawnPointDTO getPersonalSpawn(String playerUuid, boolean isOwner) {
        if (isOwner && !ownerSpawns.isEmpty()) return ownerSpawns.getFirst(); // 첫 번째 스폰을 기본으로
        return memberSpawns.get(playerUuid);
    }

    /**
     * 섬장 스폰 추가 가능 여부 확인
     */
    public boolean canAddOwnerSpawn() {
        return ownerSpawns.size() < 3;
    }

    /**
     * JsonObject로 변환 (Firebase 저장용)
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject fields = new JsonObject();

        // defaultSpawn
        fields.add("defaultSpawn", JsonUtil.createMapValue(defaultSpawn.toJsonObject()));

        // ownerSpawns 배열
        fields.add("ownerSpawns", JsonUtil.createArrayValue(ownerSpawns, IslandSpawnPointDTO::toJsonObject));

        // memberSpawns 맵
        fields.add("memberSpawns", JsonUtil.createMapField(memberSpawns, spawn -> JsonUtil.createMapValue(spawn.toJsonObject())));

        return JsonUtil.wrapInDocument(fields);
    }

    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static IslandSpawnDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return createDefault();
        }

        JsonObject fields = JsonUtil.unwrapDocument(json);

        // defaultSpawn 파싱
        JsonObject defaultSpawnJson = JsonUtil.getMapValue(fields, "defaultSpawn");
        IslandSpawnPointDTO defaultSpawn = defaultSpawnJson.entrySet().isEmpty() ? IslandSpawnPointDTO.createDefault() : IslandSpawnPointDTO.fromJsonObject(defaultSpawnJson);

        // ownerSpawns 배열 파싱
        List<IslandSpawnPointDTO> ownerSpawns = JsonUtil.getArrayValue(fields, "ownerSpawns", IslandSpawnPointDTO::fromJsonObject);

        // memberSpawns 맵 파싱
        Map<String, IslandSpawnPointDTO> memberSpawns = JsonUtil.getMapField(fields, "memberSpawns", key -> key, obj -> {
            JsonObject mapValue = obj.getAsJsonObject("mapValue");
            return IslandSpawnPointDTO.fromJsonObject(mapValue);
        });

        return new IslandSpawnDTO(defaultSpawn, ownerSpawns, memberSpawns);
    }

}