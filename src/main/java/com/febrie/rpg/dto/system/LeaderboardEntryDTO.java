package com.febrie.rpg.dto.system;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 순위표 항목 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record LeaderboardEntryDTO(
        String playerUuid,
        String playerName,
        int rank,
        long value,
        String type,
        long lastUpdated
) {
    /**
     * 간편 생성자
     */
    public LeaderboardEntryDTO(String playerUuid, String playerName, int rank, long value, String type) {
        this(playerUuid, playerName, rank, value, type, System.currentTimeMillis());
    }
    
    /**
     * Map으로 변환
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("playerUuid", playerUuid);
        map.put("playerName", playerName);
        map.put("rank", rank);
        map.put("value", value);
        map.put("type", type);
        map.put("lastUpdated", lastUpdated);
        
        return map;
    }
    
    /**
     * Map에서 LeaderboardEntryDTO 생성
     */
    @NotNull
    public static LeaderboardEntryDTO fromMap(@NotNull Map<String, Object> map) {
        if (map.isEmpty()) {
            return new LeaderboardEntryDTO("", "", 0, 0L, "");
        }
        
        String playerUuid = FirestoreUtils.getString(map, "playerUuid");
        String playerName = FirestoreUtils.getString(map, "playerName");
        int rank = FirestoreUtils.getInt(map, "rank");
        long value = FirestoreUtils.getLong(map, "value");
        String type = FirestoreUtils.getString(map, "type");
        long lastUpdated = FirestoreUtils.getLong(map, "lastUpdated", System.currentTimeMillis());
        
        return new LeaderboardEntryDTO(playerUuid, playerName, rank, value, type, lastUpdated);
    }
}