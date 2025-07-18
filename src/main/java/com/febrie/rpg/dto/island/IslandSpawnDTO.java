package com.febrie.rpg.dto.island;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 섬 스폰 관련 정보 DTO (Record)
 *
 * @author Febrie, CoffeeTory
 */
public record IslandSpawnDTO(
        @NotNull IslandSpawnPointDTO defaultSpawn, // 기본 스폰 위치
        @NotNull List<IslandSpawnPointDTO> ownerSpawns, // 섬장 개인 스폰 위치들 (최대 3개)
        @NotNull Map<String, IslandSpawnPointDTO> memberSpawns // 섬원별 개인 스폰 (UUID -> 스폰)
) {
    /**
     * 기본 스폰 데이터 생성
     */
    public static IslandSpawnDTO createDefault() {
        return new IslandSpawnDTO(
                IslandSpawnPointDTO.createDefault(),
                List.of(),
                Map.of()
        );
    }
    
    /**
     * 특정 플레이어의 개인 스폰 위치 가져오기
     */
    @Nullable
    public IslandSpawnPointDTO getPersonalSpawn(String playerUuid, boolean isOwner) {
        if (isOwner && !ownerSpawns.isEmpty()) {
            return ownerSpawns.get(0); // 첫 번째 스폰을 기본으로
        }
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
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        // defaultSpawn
        JsonObject defaultSpawnValue = new JsonObject();
        defaultSpawnValue.add("mapValue", defaultSpawn.toJsonObject());
        fields.add("defaultSpawn", defaultSpawnValue);
        
        // ownerSpawns 배열
        JsonObject ownerSpawnsValue = new JsonObject();
        JsonObject ownerSpawnsArray = new JsonObject();
        JsonArray ownerSpawnsValues = new JsonArray();
        for (IslandSpawnPointDTO spawn : ownerSpawns) {
            JsonObject spawnValue = new JsonObject();
            spawnValue.add("mapValue", spawn.toJsonObject());
            ownerSpawnsValues.add(spawnValue);
        }
        ownerSpawnsArray.add("values", ownerSpawnsValues);
        ownerSpawnsValue.add("arrayValue", ownerSpawnsArray);
        fields.add("ownerSpawns", ownerSpawnsValue);
        
        // memberSpawns 맵
        JsonObject memberSpawnsValue = new JsonObject();
        JsonObject memberSpawnsMap = new JsonObject();
        JsonObject memberSpawnsFields = new JsonObject();
        memberSpawns.forEach((uuid, spawn) -> {
            JsonObject spawnValue = new JsonObject();
            spawnValue.add("mapValue", spawn.toJsonObject());
            memberSpawnsFields.add(uuid, spawnValue);
        });
        memberSpawnsMap.add("fields", memberSpawnsFields);
        memberSpawnsValue.add("mapValue", memberSpawnsMap);
        fields.add("memberSpawns", memberSpawnsValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static IslandSpawnDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return createDefault();
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        // defaultSpawn 파싱
        IslandSpawnPointDTO defaultSpawn = fields.has("defaultSpawn") && fields.getAsJsonObject("defaultSpawn").has("mapValue")
                ? IslandSpawnPointDTO.fromJsonObject(fields.getAsJsonObject("defaultSpawn").getAsJsonObject("mapValue"))
                : IslandSpawnPointDTO.createDefault();
        
        // ownerSpawns 배열 파싱
        List<IslandSpawnPointDTO> ownerSpawns = new ArrayList<>();
        if (fields.has("ownerSpawns") && fields.getAsJsonObject("ownerSpawns").has("arrayValue")) {
            JsonObject ownerSpawnsArray = fields.getAsJsonObject("ownerSpawns").getAsJsonObject("arrayValue");
            if (ownerSpawnsArray.has("values")) {
                JsonArray ownerSpawnsValues = ownerSpawnsArray.getAsJsonArray("values");
                for (JsonElement element : ownerSpawnsValues) {
                    if (element.isJsonObject() && element.getAsJsonObject().has("mapValue")) {
                        ownerSpawns.add(IslandSpawnPointDTO.fromJsonObject(element.getAsJsonObject().getAsJsonObject("mapValue")));
                    }
                }
            }
        }
        
        // memberSpawns 맵 파싱
        Map<String, IslandSpawnPointDTO> memberSpawns = new HashMap<>();
        if (fields.has("memberSpawns") && fields.getAsJsonObject("memberSpawns").has("mapValue")) {
            JsonObject memberSpawnsMap = fields.getAsJsonObject("memberSpawns").getAsJsonObject("mapValue");
            if (memberSpawnsMap.has("fields")) {
                JsonObject memberSpawnsFields = memberSpawnsMap.getAsJsonObject("fields");
                for (Map.Entry<String, JsonElement> entry : memberSpawnsFields.entrySet()) {
                    if (entry.getValue().isJsonObject() && entry.getValue().getAsJsonObject().has("mapValue")) {
                        memberSpawns.put(entry.getKey(), IslandSpawnPointDTO.fromJsonObject(entry.getValue().getAsJsonObject().getAsJsonObject("mapValue")));
                    }
                }
            }
        }
        
        return new IslandSpawnDTO(defaultSpawn, ownerSpawns, memberSpawns);
    }
    
    /**
     * Map으로 변환 (Firebase 저장용)
     */
    @Deprecated
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("defaultSpawn", defaultSpawn.toMap());
        map.put("ownerSpawns", ownerSpawns.stream()
                .map(IslandSpawnPointDTO::toMap)
                .collect(Collectors.toList()));
        
        Map<String, Map<String, Object>> memberSpawnMaps = new HashMap<>();
        memberSpawns.forEach((uuid, spawn) -> memberSpawnMaps.put(uuid, spawn.toMap()));
        map.put("memberSpawns", memberSpawnMaps);
        
        return map;
    }
    
    /**
     * Map에서 생성
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static IslandSpawnDTO fromMap(Map<String, Object> map) {
        if (map == null) return createDefault();
        
        IslandSpawnPointDTO defaultSpawn = IslandSpawnPointDTO.fromMap(
                (Map<String, Object>) map.get("defaultSpawn")
        );
        if (defaultSpawn == null) {
            defaultSpawn = IslandSpawnPointDTO.createDefault();
        }
        
        List<IslandSpawnPointDTO> ownerSpawns = new ArrayList<>();
        if (map.containsKey("ownerSpawns")) {
            List<Map<String, Object>> ownerSpawnsList = (List<Map<String, Object>>) map.get("ownerSpawns");
            for (Map<String, Object> spawnMap : ownerSpawnsList) {
                IslandSpawnPointDTO spawn = IslandSpawnPointDTO.fromMap(spawnMap);
                if (spawn != null) {
                    ownerSpawns.add(spawn);
                }
            }
        }
        
        Map<String, IslandSpawnPointDTO> memberSpawns = new HashMap<>();
        if (map.containsKey("memberSpawns")) {
            Map<String, Map<String, Object>> memberSpawnMaps = 
                    (Map<String, Map<String, Object>>) map.get("memberSpawns");
            memberSpawnMaps.forEach((uuid, spawnMap) -> {
                IslandSpawnPointDTO spawn = IslandSpawnPointDTO.fromMap(spawnMap);
                if (spawn != null) {
                    memberSpawns.put(uuid, spawn);
                }
            });
        }
        
        return new IslandSpawnDTO(defaultSpawn, ownerSpawns, memberSpawns);
    }
}