package com.febrie.rpg.dto.system;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

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
     * JsonObject로 변환
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        JsonObject playerUuidValue = new JsonObject();
        playerUuidValue.addProperty("stringValue", playerUuid);
        fields.add("playerUuid", playerUuidValue);
        
        JsonObject playerNameValue = new JsonObject();
        playerNameValue.addProperty("stringValue", playerName);
        fields.add("playerName", playerNameValue);
        
        JsonObject rankValue = new JsonObject();
        rankValue.addProperty("integerValue", rank);
        fields.add("rank", rankValue);
        
        JsonObject valueValue = new JsonObject();
        valueValue.addProperty("integerValue", value);
        fields.add("value", valueValue);
        
        JsonObject typeValue = new JsonObject();
        typeValue.addProperty("stringValue", type);
        fields.add("type", typeValue);
        
        JsonObject lastUpdatedValue = new JsonObject();
        lastUpdatedValue.addProperty("integerValue", lastUpdated);
        fields.add("lastUpdated", lastUpdatedValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 LeaderboardEntryDTO 생성
     */
    @NotNull
    public static LeaderboardEntryDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return new LeaderboardEntryDTO("", "", 0, 0L, "");
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        String playerUuid = fields.has("playerUuid") && fields.getAsJsonObject("playerUuid").has("stringValue")
                ? fields.getAsJsonObject("playerUuid").get("stringValue").getAsString()
                : "";
                
        String playerName = fields.has("playerName") && fields.getAsJsonObject("playerName").has("stringValue")
                ? fields.getAsJsonObject("playerName").get("stringValue").getAsString()
                : "";
                
        int rank = fields.has("rank") && fields.getAsJsonObject("rank").has("integerValue")
                ? fields.getAsJsonObject("rank").get("integerValue").getAsInt()
                : 0;
                
        long value = fields.has("value") && fields.getAsJsonObject("value").has("integerValue")
                ? fields.getAsJsonObject("value").get("integerValue").getAsLong()
                : 0L;
                
        String type = fields.has("type") && fields.getAsJsonObject("type").has("stringValue")
                ? fields.getAsJsonObject("type").get("stringValue").getAsString()
                : "";
                
        long lastUpdated = fields.has("lastUpdated") && fields.getAsJsonObject("lastUpdated").has("integerValue")
                ? fields.getAsJsonObject("lastUpdated").get("integerValue").getAsLong()
                : System.currentTimeMillis();
        
        return new LeaderboardEntryDTO(playerUuid, playerName, rank, value, type, lastUpdated);
    }
}