package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

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
                85, // 초기 크기
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
        long oneWeekInMillis = 7L * 24 * 60 * 60 * 1000;
        return System.currentTimeMillis() - createdAt >= oneWeekInMillis;
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
        return new IslandCoreDTO(
                FirestoreUtils.getString(map, "islandId"),
                FirestoreUtils.getString(map, "ownerUuid"),
                FirestoreUtils.getString(map, "ownerName"),
                FirestoreUtils.getString(map, "islandName"),
                FirestoreUtils.getInt(map, "size", 85),
                FirestoreUtils.getBoolean(map, "isPublic", false),
                FirestoreUtils.getLong(map, "createdAt", System.currentTimeMillis()),
                FirestoreUtils.getLong(map, "lastActivity", System.currentTimeMillis()),
                FirestoreUtils.getInt(map, "totalResets", 0),
                FirestoreUtils.getLongOrNull(map, "deletionScheduledAt")
        );
    }
}