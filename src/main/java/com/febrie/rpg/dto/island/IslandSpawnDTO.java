package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.FirestoreUtils;
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
     * Map으로 변환 (Firebase 저장용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        // defaultSpawn
        map.put("defaultSpawn", defaultSpawn.toMap());

        // ownerSpawns 배열
        List<Map<String, Object>> ownerSpawnsList = ownerSpawns.stream()
                .map(IslandSpawnPointDTO::toMap)
                .collect(Collectors.toList());
        map.put("ownerSpawns", ownerSpawnsList);

        // memberSpawns 맵
        Map<String, Map<String, Object>> memberSpawnsMap = memberSpawns.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().toMap()
                ));
        map.put("memberSpawns", memberSpawnsMap);

        return map;
    }

    /**
     * Map에서 생성
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static IslandSpawnDTO fromMap(@NotNull Map<String, Object> map) {
        // defaultSpawn 파싱
        Map<String, Object> defaultSpawnMap = FirestoreUtils.getMap(map, "defaultSpawn", null);
        IslandSpawnPointDTO defaultSpawn = defaultSpawnMap != null ? 
            IslandSpawnPointDTO.fromMap(defaultSpawnMap) : IslandSpawnPointDTO.createDefault();

        // ownerSpawns 배열 파싱
        List<IslandSpawnPointDTO> ownerSpawns = new ArrayList<>();
        List<Map<String, Object>> ownerSpawnsList = FirestoreUtils.getList(map, "ownerSpawns", new ArrayList<>());
        for (Map<String, Object> item : ownerSpawnsList) {
            ownerSpawns.add(IslandSpawnPointDTO.fromMap(item));
        }

        // memberSpawns 맵 파싱
        Map<String, IslandSpawnPointDTO> memberSpawns = new HashMap<>();
        Map<String, Object> memberSpawnsMap = FirestoreUtils.getMap(map, "memberSpawns", new HashMap<>());
        for (Map.Entry<String, Object> entry : memberSpawnsMap.entrySet()) {
            if (entry.getValue() instanceof Map) {
                memberSpawns.put(entry.getKey(), IslandSpawnPointDTO.fromMap((Map<String, Object>) entry.getValue()));
            }
        }

        return new IslandSpawnDTO(defaultSpawn, ownerSpawns, memberSpawns);
    }

}