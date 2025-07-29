package com.febrie.rpg.dto.system;

import com.febrie.rpg.util.JsonUtil;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

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
     * JsonObject로 변환
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        fields.add("onlinePlayers", JsonUtil.createIntegerValue(onlinePlayers));
        fields.add("maxPlayers", JsonUtil.createIntegerValue(maxPlayers));
        fields.add("totalPlayers", JsonUtil.createIntegerValue(totalPlayers));
        fields.add("uptime", JsonUtil.createIntegerValue(uptime));
        fields.add("tps", JsonUtil.createDoubleValue(tps));
        fields.add("totalPlaytime", JsonUtil.createIntegerValue(totalPlaytime));
        fields.add("version", JsonUtil.createStringValue(version));
        fields.add("lastUpdated", JsonUtil.createIntegerValue(lastUpdated));
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 ServerStatsDTO 생성
     */
    @NotNull
    public static ServerStatsDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return new ServerStatsDTO();
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        int onlinePlayers = (int) JsonUtil.getLongValue(fields, "onlinePlayers", 0L);
        int maxPlayers = (int) JsonUtil.getLongValue(fields, "maxPlayers", 0L);
        int totalPlayers = (int) JsonUtil.getLongValue(fields, "totalPlayers", 0L);
        long uptime = JsonUtil.getLongValue(fields, "uptime", 0L);
        double tps = JsonUtil.getDoubleValue(fields, "tps", 20.0);
        long totalPlaytime = JsonUtil.getLongValue(fields, "totalPlaytime", 0L);
        String version = JsonUtil.getStringValue(fields, "version", "1.21.7");
        long lastUpdated = JsonUtil.getLongValue(fields, "lastUpdated", System.currentTimeMillis());
        
        return new ServerStatsDTO(onlinePlayers, maxPlayers, totalPlayers, uptime, tps, 
                                 totalPlaytime, version, lastUpdated);
    }
}