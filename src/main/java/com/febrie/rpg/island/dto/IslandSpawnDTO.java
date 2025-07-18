package com.febrie.rpg.island.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

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
}