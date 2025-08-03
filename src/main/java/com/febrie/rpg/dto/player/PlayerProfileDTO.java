package com.febrie.rpg.dto.player;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 플레이어 프로필 DTO
 * 기본 플레이어 정보
 */
public record PlayerProfileDTO(
        @NotNull UUID uuid,
        @NotNull String name,
        int level,
        long exp,
        long totalExp,
        long lastPlayed
) {
    
    /**
     * Map으로 변환 (Firestore SDK용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", uuid.toString());
        map.put("name", name);
        map.put("level", level);
        map.put("exp", exp);
        map.put("totalExp", totalExp);
        map.put("lastPlayed", lastPlayed);
        return map;
    }
    
    /**
     * Map에서 생성 (Firestore SDK용)
     */
    @NotNull
    public static PlayerProfileDTO fromMap(@NotNull Map<String, Object> map) {
        String uuidStr = FirestoreUtils.getString(map, "uuid", UUID.randomUUID().toString());
        UUID uuid = UUID.fromString(uuidStr);
        String name = FirestoreUtils.getString(map, "name", "");
        int level = FirestoreUtils.getInt(map, "level", 1);
        long exp = FirestoreUtils.getLong(map, "exp", 0L);
        long totalExp = FirestoreUtils.getLong(map, "totalExp", 0L);
        long lastPlayed = FirestoreUtils.getLong(map, "lastPlayed", System.currentTimeMillis());
        
        return new PlayerProfileDTO(uuid, name, level, exp, totalExp, lastPlayed);
    }
    
}