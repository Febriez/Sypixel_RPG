package com.febrie.rpg.dto.island;

import com.febrie.rpg.database.constants.DatabaseConstants;
import com.febrie.rpg.util.FirestoreUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 섬 핵심 정보 DTO (Record)
 * 가장 기본적인 섬 정보와 위치 정보 포함
 *
 * @author Febrie
 */
public record IslandCoreDTO(
        @NotNull String islandId,
        @NotNull String ownerUuid,
        @NotNull String ownerName,
        @NotNull String islandName,
        int size,
        boolean isPublic,
        long createdAt,
        long lastActivity,
        int totalResets,
        @Nullable Long deletionScheduledAt,
        @Nullable IslandLocationDTO location  // 위치 정보 (옵션)
) {
    /**
     * 신규 섬 생성용 기본 생성자
     */
    public static IslandCoreDTO createNew(String islandId, String ownerUuid, String ownerName, String islandName) {
        return new IslandCoreDTO(
                islandId,
                ownerUuid,
                ownerName,
                islandName,
                DatabaseConstants.ISLAND_INITIAL_SIZE, // 초기 크기
                false, // 기본 비공개
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                0,
                null,
                null  // 위치는 나중에 설정
        );
    }
    
    /**
     * 위치 정보 포함 생성자
     */
    public static IslandCoreDTO createWithLocation(String islandId, String ownerUuid, String ownerName, 
                                                   String islandName, IslandLocationDTO location) {
        return new IslandCoreDTO(
                islandId,
                ownerUuid,
                ownerName,
                islandName,
                DatabaseConstants.ISLAND_INITIAL_SIZE,
                false,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                0,
                null,
                location
        );
    }

    /**
     * 섬 삭제 가능 여부 확인 (생성 후 1주일 경과)
     */
    public boolean canDelete() {
        return System.currentTimeMillis() - createdAt >= DatabaseConstants.ISLAND_DELETE_COOLDOWN_MS;
    }
    
    /**
     * 위치 정보가 있는지 확인
     */
    public boolean hasLocation() {
        return location != null;
    }
    
    /**
     * 특정 위치가 섬 범위 내에 있는지 확인
     */
    public boolean contains(Location loc) {
        return location != null && location.contains(loc);
    }
    
    /**
     * 섬 중앙 위치 가져오기
     */
    @Nullable
    public Location getCenter(World world) {
        return location != null ? location.getCenter(world) : null;
    }

    /**
     * Map으로 변환 (Firestore SDK용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("islandId", islandId);
        map.put("ownerUuid", ownerUuid);
        map.put("ownerName", ownerName);
        map.put("islandName", islandName);
        map.put("size", size);
        map.put("isPublic", isPublic);
        map.put("createdAt", createdAt);
        map.put("lastActivity", lastActivity);
        map.put("totalResets", totalResets);
        
        if (deletionScheduledAt != null) {
            map.put("deletionScheduledAt", deletionScheduledAt);
        }
        
        if (location != null) {
            map.put("location", location.toMap());
        }
        
        return map;
    }

    /**
     * Map에서 생성 (Firestore SDK용)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static IslandCoreDTO fromMap(@NotNull Map<String, Object> map) {
        @NotNull String islandId = Objects.requireNonNull(FirestoreUtils.getString(map, "islandId", ""));
        @NotNull String ownerUuid = Objects.requireNonNull(FirestoreUtils.getString(map, "ownerUuid", ""));
        @NotNull String ownerName = Objects.requireNonNull(FirestoreUtils.getString(map, "ownerName", ""));
        @NotNull String islandName = Objects.requireNonNull(FirestoreUtils.getString(map, "islandName", ""));
        
        // 필수 필드 검증
        if (islandId.isEmpty()) {
            throw new IllegalArgumentException("IslandCoreDTO: islandId cannot be empty");
        }
        if (ownerUuid.isEmpty()) {
            throw new IllegalArgumentException("IslandCoreDTO: ownerUuid cannot be empty");
        }
        if (ownerName.isEmpty()) {
            throw new IllegalArgumentException("IslandCoreDTO: ownerName cannot be empty");
        }
        if (islandName.isEmpty()) {
            throw new IllegalArgumentException("IslandCoreDTO: islandName cannot be empty");
        }
        
        // 위치 정보 파싱 (옵션)
        IslandLocationDTO location = null;
        Object locationObj = map.get("location");
        if (locationObj instanceof Map) {
            location = IslandLocationDTO.fromMap((Map<String, Object>) locationObj);
        }
        
        return new IslandCoreDTO(
                islandId,
                ownerUuid,
                ownerName,
                islandName,
                FirestoreUtils.getInt(map, "size", DatabaseConstants.ISLAND_INITIAL_SIZE),
                FirestoreUtils.getBoolean(map, "isPublic", false),
                FirestoreUtils.getLong(map, "createdAt", System.currentTimeMillis()),
                FirestoreUtils.getLong(map, "lastActivity", System.currentTimeMillis()),
                FirestoreUtils.getInt(map, "totalResets", 0),
                FirestoreUtils.getLongOrNull(map, "deletionScheduledAt"),
                location
        );
    }
}