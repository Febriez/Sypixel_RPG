package com.febrie.rpg.dto.island;

import com.febrie.rpg.database.constants.DatabaseConstants;
import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 섬 핵심 정보 DTO (Record)
 * 가장 기본적인 섬 정보만 포함
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
        @Nullable Long deletionScheduledAt
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
                null
        );
    }

    /**
     * 섬 삭제 가능 여부 확인 (생성 후 1주일 경과)
     */
    public boolean canDelete() {
        return System.currentTimeMillis() - createdAt >= DatabaseConstants.ISLAND_DELETE_COOLDOWN_MS;
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
        
        return map;
    }

    /**
     * Map에서 생성 (Firestore SDK용)
     */
    @NotNull
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
                FirestoreUtils.getLongOrNull(map, "deletionScheduledAt")
        );
    }
}