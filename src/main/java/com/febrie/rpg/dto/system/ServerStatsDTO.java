package com.febrie.rpg.dto.system;

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
        
        JsonObject onlinePlayersValue = new JsonObject();
        onlinePlayersValue.addProperty("integerValue", onlinePlayers);
        fields.add("onlinePlayers", onlinePlayersValue);
        
        JsonObject maxPlayersValue = new JsonObject();
        maxPlayersValue.addProperty("integerValue", maxPlayers);
        fields.add("maxPlayers", maxPlayersValue);
        
        JsonObject totalPlayersValue = new JsonObject();
        totalPlayersValue.addProperty("integerValue", totalPlayers);
        fields.add("totalPlayers", totalPlayersValue);
        
        JsonObject uptimeValue = new JsonObject();
        uptimeValue.addProperty("integerValue", uptime);
        fields.add("uptime", uptimeValue);
        
        JsonObject tpsValue = new JsonObject();
        tpsValue.addProperty("doubleValue", tps);
        fields.add("tps", tpsValue);
        
        JsonObject totalPlaytimeValue = new JsonObject();
        totalPlaytimeValue.addProperty("integerValue", totalPlaytime);
        fields.add("totalPlaytime", totalPlaytimeValue);
        
        JsonObject versionValue = new JsonObject();
        versionValue.addProperty("stringValue", version);
        fields.add("version", versionValue);
        
        JsonObject lastUpdatedValue = new JsonObject();
        lastUpdatedValue.addProperty("integerValue", lastUpdated);
        fields.add("lastUpdated", lastUpdatedValue);
        
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
        
        int onlinePlayers = fields.has("onlinePlayers") && fields.getAsJsonObject("onlinePlayers").has("integerValue")
                ? fields.getAsJsonObject("onlinePlayers").get("integerValue").getAsInt()
                : 0;
                
        int maxPlayers = fields.has("maxPlayers") && fields.getAsJsonObject("maxPlayers").has("integerValue")
                ? fields.getAsJsonObject("maxPlayers").get("integerValue").getAsInt()
                : 0;
                
        int totalPlayers = fields.has("totalPlayers") && fields.getAsJsonObject("totalPlayers").has("integerValue")
                ? fields.getAsJsonObject("totalPlayers").get("integerValue").getAsInt()
                : 0;
                
        long uptime = fields.has("uptime") && fields.getAsJsonObject("uptime").has("integerValue")
                ? fields.getAsJsonObject("uptime").get("integerValue").getAsLong()
                : 0L;
                
        double tps = fields.has("tps") && fields.getAsJsonObject("tps").has("doubleValue")
                ? fields.getAsJsonObject("tps").get("doubleValue").getAsDouble()
                : 20.0;
                
        long totalPlaytime = fields.has("totalPlaytime") && fields.getAsJsonObject("totalPlaytime").has("integerValue")
                ? fields.getAsJsonObject("totalPlaytime").get("integerValue").getAsLong()
                : 0L;
                
        String version = fields.has("version") && fields.getAsJsonObject("version").has("stringValue")
                ? fields.getAsJsonObject("version").get("stringValue").getAsString()
                : "1.21.7";
                
        long lastUpdated = fields.has("lastUpdated") && fields.getAsJsonObject("lastUpdated").has("integerValue")
                ? fields.getAsJsonObject("lastUpdated").get("integerValue").getAsLong()
                : System.currentTimeMillis();
        
        return new ServerStatsDTO(onlinePlayers, maxPlayers, totalPlayers, uptime, tps, 
                                 totalPlaytime, version, lastUpdated);
    }
}