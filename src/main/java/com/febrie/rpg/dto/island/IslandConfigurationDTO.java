package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 섬 설정 정보 DTO (Record)
 * 스폰, 업그레이드, 권한, 설정 관련 정보
 *
 * @author Febrie
 */
public record IslandConfigurationDTO(
        @NotNull String islandId,
        @NotNull IslandSpawnDTO spawnData,
        @NotNull IslandUpgradeDTO upgradeData,
        @NotNull IslandPermissionDTO permissions,
        @NotNull IslandSettingsDTO settings
) {
    /**
     * 기본 생성자 - 기본 설정
     */
    public static IslandConfigurationDTO createDefault(String islandId) {
        return new IslandConfigurationDTO(
                islandId,
                IslandSpawnDTO.createDefault(),
                IslandUpgradeDTO.createDefault(),
                IslandPermissionDTO.createDefault(),
                IslandSettingsDTO.createDefault()
        );
    }

    /**
     * 섬 크기를 16의 배수로 반올림 (바이옴 설정용)
     */
    public int getBiomeSize(int islandSize) {
        // 500을 넘는 가장 가까운 16의 배수 찾기
        int minSize = Math.max(islandSize, 500);
        return ((minSize + 15) / 16) * 16;
    }

    /**
     * Map으로 변환 (Firestore SDK용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("islandId", islandId);
        map.put("spawnData", spawnData.toMap());
        map.put("upgradeData", upgradeData.toMap());
        map.put("permissions", permissions.toMap());
        map.put("settings", settings.toMap());
        return map;
    }

    /**
     * Map에서 생성 (Firestore SDK용)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static IslandConfigurationDTO fromMap(@NotNull Map<String, Object> map) {
        @NotNull String islandId = Objects.requireNonNull(FirestoreUtils.getString(map, "islandId", ""));
        
        // 필수 필드 검증
        if (islandId.isEmpty()) {
            throw new IllegalArgumentException("IslandConfigurationDTO: islandId cannot be empty");
        }
        
        IslandSpawnDTO spawnData = IslandSpawnDTO.createDefault();
        Object spawnObj = map.get("spawnData");
        if (spawnObj instanceof Map) {
            spawnData = IslandSpawnDTO.fromMap((Map<String, Object>) spawnObj);
        }
        
        IslandUpgradeDTO upgradeData = IslandUpgradeDTO.createDefault();
        Object upgradeObj = map.get("upgradeData");
        if (upgradeObj instanceof Map) {
            upgradeData = IslandUpgradeDTO.fromMap((Map<String, Object>) upgradeObj);
        }
        
        IslandPermissionDTO permissions = IslandPermissionDTO.createDefault();
        Object permissionsObj = map.get("permissions");
        if (permissionsObj instanceof Map) {
            permissions = IslandPermissionDTO.fromMap((Map<String, Object>) permissionsObj);
        }
        
        IslandSettingsDTO settings = IslandSettingsDTO.createDefault();
        Object settingsObj = map.get("settings");
        if (settingsObj instanceof Map) {
            settings = IslandSettingsDTO.fromMap((Map<String, Object>) settingsObj);
        }
        
        return new IslandConfigurationDTO(islandId, spawnData, upgradeData, permissions, settings);
    }
}