package com.febrie.rpg.dto.system;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 서버 통계 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record ServerStatsDTO(
        int onlinePlayers,
        int maxPlayers,
        int totalPlayers,
        long uptime,
        double tps,
        long totalPlaytime,
        String version,
        long lastUpdated
) {
    /**
     * 기본 생성자
     */
    public ServerStatsDTO() {
        this(0, 0, 0, 0L, 20.0, 0L, "1.21.7", System.currentTimeMillis());
    }

    /**
     * 업데이트 시간 제외 생성자
     */
    public ServerStatsDTO(int onlinePlayers, int maxPlayers, int totalPlayers,
                          long uptime, double tps, long totalPlaytime, String version) {
        this(onlinePlayers, maxPlayers, totalPlayers, uptime, tps, totalPlaytime, version, System.currentTimeMillis());
    }
    
    /**
     * Map으로 변환
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("onlinePlayers", onlinePlayers);
        map.put("maxPlayers", maxPlayers);
        map.put("totalPlayers", totalPlayers);
        map.put("uptime", uptime);
        map.put("tps", tps);
        map.put("totalPlaytime", totalPlaytime);
        map.put("version", version);
        map.put("lastUpdated", lastUpdated);
        
        return map;
    }
    
    /**
     * Map에서 ServerStatsDTO 생성
     */
    @NotNull
    public static ServerStatsDTO fromMap(@NotNull Map<String, Object> map) {
        if (map.isEmpty()) {
            return new ServerStatsDTO();
        }
        
        int onlinePlayers = FirestoreUtils.getInt(map, "onlinePlayers");
        int maxPlayers = FirestoreUtils.getInt(map, "maxPlayers");
        int totalPlayers = FirestoreUtils.getInt(map, "totalPlayers");
        long uptime = FirestoreUtils.getLong(map, "uptime");
        double tps = FirestoreUtils.getDouble(map, "tps", 20.0);
        long totalPlaytime = FirestoreUtils.getLong(map, "totalPlaytime");
        String version = FirestoreUtils.getString(map, "version", "1.21.7");
        long lastUpdated = FirestoreUtils.getLong(map, "lastUpdated", System.currentTimeMillis());
        
        return new ServerStatsDTO(onlinePlayers, maxPlayers, totalPlayers, uptime, tps, 
                                 totalPlaytime, version, lastUpdated);
    }
}