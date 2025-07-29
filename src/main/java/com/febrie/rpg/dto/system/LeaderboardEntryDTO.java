package com.febrie.rpg.dto.system;

import com.febrie.rpg.util.JsonUtil;
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
        
        fields.add("playerUuid", JsonUtil.createStringValue(playerUuid));
        fields.add("playerName", JsonUtil.createStringValue(playerName));
        fields.add("rank", JsonUtil.createIntegerValue(rank));
        fields.add("value", JsonUtil.createIntegerValue(value));
        fields.add("type", JsonUtil.createStringValue(type));
        fields.add("lastUpdated", JsonUtil.createIntegerValue(lastUpdated));
        
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
        
        String playerUuid = JsonUtil.getStringValue(fields, "playerUuid", "");
        String playerName = JsonUtil.getStringValue(fields, "playerName", "");
        int rank = (int) JsonUtil.getLongValue(fields, "rank", 0L);
        long value = JsonUtil.getLongValue(fields, "value", 0L);
        String type = JsonUtil.getStringValue(fields, "type", "");
        long lastUpdated = JsonUtil.getLongValue(fields, "lastUpdated", System.currentTimeMillis());
        
        return new LeaderboardEntryDTO(playerUuid, playerName, rank, value, type, lastUpdated);
    }
}