package com.febrie.rpg.dto.quest;

import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.util.JsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어 퀘스트 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie
 */
public record PlayerQuestDTO(
        String playerId,
        Map<String, QuestProgress> activeQuests,
        Map<String, CompletedQuestDTO> completedQuests,
        long lastUpdated
) {
    /**
     * 기본 생성자 - 신규 플레이어용
     */
    public PlayerQuestDTO(String playerId) {
        this(playerId, new HashMap<>(), new HashMap<>(), System.currentTimeMillis());
    }

    /**
     * 방어적 복사를 위한 생성자
     */
    public PlayerQuestDTO(String playerId, Map<String, QuestProgress> activeQuests,
                          Map<String, CompletedQuestDTO> completedQuests, long lastUpdated) {
        this.playerId = playerId;
        this.activeQuests = new HashMap<>(activeQuests);
        this.completedQuests = new HashMap<>(completedQuests);
        this.lastUpdated = lastUpdated;
    }

    /**
     * 활성 퀘스트 맵의 불변 뷰 반환
     */
    @Override
    public Map<String, QuestProgress> activeQuests() {
        return new HashMap<>(activeQuests);
    }

    /**
     * 완료된 퀘스트 맵의 불변 뷰 반환
     */
    @Override
    public Map<String, CompletedQuestDTO> completedQuests() {
        return new HashMap<>(completedQuests);
    }
    
    /**
     * JsonObject로 변환 (Firebase 저장용)
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject fields = new JsonObject();
        
        fields.add("playerId", JsonUtil.createStringValue(playerId));
        
        // activeQuests
        fields.add("activeQuests", JsonUtil.createMapField(activeQuests, 
                progress -> JsonUtil.createMapValue(progress.toJsonObject())));
        
        // completedQuests
        fields.add("completedQuests", JsonUtil.createMapField(completedQuests,
                completed -> JsonUtil.createMapValue(completed.toJsonObject())));
        
        fields.add("lastUpdated", JsonUtil.createIntegerValue(lastUpdated));
        
        return JsonUtil.wrapInDocument(fields);
    }
    
    /**
     * JsonObject에서 PlayerQuestDTO 생성
     */
    @NotNull
    public static PlayerQuestDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return new PlayerQuestDTO("");
        }
        
        JsonObject fields = JsonUtil.unwrapDocument(json);
        
        String playerId = JsonUtil.getStringValue(fields, "playerId", "");
        
        // activeQuests
        Map<String, QuestProgress> activeQuests = JsonUtil.getMapField(fields, "activeQuests",
                key -> key,
                obj -> {
                    JsonObject mapValue = obj.getAsJsonObject("mapValue");
                    return QuestProgress.fromJsonObject(mapValue);
                });
        
        // completedQuests
        Map<String, CompletedQuestDTO> completedQuests = JsonUtil.getMapField(fields, "completedQuests",
                key -> key,
                obj -> {
                    JsonObject mapValue = obj.getAsJsonObject("mapValue");
                    return CompletedQuestDTO.fromJsonObject(mapValue);
                });
        
        long lastUpdated = JsonUtil.getLongValue(fields, "lastUpdated", System.currentTimeMillis());
        
        return new PlayerQuestDTO(playerId, activeQuests, completedQuests, lastUpdated);
    }
}