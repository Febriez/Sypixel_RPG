package com.febrie.rpg.dto.quest;

import com.febrie.rpg.quest.progress.QuestProgress;
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
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        // playerId
        JsonObject playerIdValue = new JsonObject();
        playerIdValue.addProperty("stringValue", playerId);
        fields.add("playerId", playerIdValue);
        
        // activeQuests
        JsonObject activeQuestsValue = new JsonObject();
        JsonObject activeQuestsMap = new JsonObject();
        JsonObject activeQuestsFields = new JsonObject();
        
        activeQuests.forEach((questId, progress) -> {
            JsonObject progressValue = new JsonObject();
            progressValue.add("mapValue", progress.toJsonObject());
            activeQuestsFields.add(questId, progressValue);
        });
        
        activeQuestsMap.add("fields", activeQuestsFields);
        activeQuestsValue.add("mapValue", activeQuestsMap);
        fields.add("activeQuests", activeQuestsValue);
        
        // completedQuests
        JsonObject completedQuestsValue = new JsonObject();
        JsonObject completedQuestsMap = new JsonObject();
        JsonObject completedQuestsFields = new JsonObject();
        
        completedQuests.forEach((questId, completed) -> {
            JsonObject completedValue = new JsonObject();
            completedValue.add("mapValue", completed.toJsonObject());
            completedQuestsFields.add(questId, completedValue);
        });
        
        completedQuestsMap.add("fields", completedQuestsFields);
        completedQuestsValue.add("mapValue", completedQuestsMap);
        fields.add("completedQuests", completedQuestsValue);
        
        // lastUpdated
        JsonObject lastUpdatedValue = new JsonObject();
        lastUpdatedValue.addProperty("integerValue", lastUpdated);
        fields.add("lastUpdated", lastUpdatedValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 PlayerQuestDTO 생성
     */
    @NotNull
    public static PlayerQuestDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return new PlayerQuestDTO("");
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        String playerId = fields.has("playerId") && fields.getAsJsonObject("playerId").has("stringValue")
                ? fields.getAsJsonObject("playerId").get("stringValue").getAsString()
                : "";
                
        Map<String, QuestProgress> activeQuests = new HashMap<>();
        if (fields.has("activeQuests") && fields.getAsJsonObject("activeQuests").has("mapValue")) {
            JsonObject activeQuestsMap = fields.getAsJsonObject("activeQuests").getAsJsonObject("mapValue");
            if (activeQuestsMap.has("fields")) {
                JsonObject activeQuestsFields = activeQuestsMap.getAsJsonObject("fields");
                for (Map.Entry<String, JsonElement> entry : activeQuestsFields.entrySet()) {
                    if (entry.getValue().isJsonObject() && entry.getValue().getAsJsonObject().has("mapValue")) {
                        QuestProgress progress = QuestProgress.fromJsonObject(entry.getValue().getAsJsonObject().getAsJsonObject("mapValue"));
                        activeQuests.put(entry.getKey(), progress);
                    }
                }
            }
        }
        
        Map<String, CompletedQuestDTO> completedQuests = new HashMap<>();
        if (fields.has("completedQuests") && fields.getAsJsonObject("completedQuests").has("mapValue")) {
            JsonObject completedQuestsMap = fields.getAsJsonObject("completedQuests").getAsJsonObject("mapValue");
            if (completedQuestsMap.has("fields")) {
                JsonObject completedQuestsFields = completedQuestsMap.getAsJsonObject("fields");
                for (Map.Entry<String, JsonElement> entry : completedQuestsFields.entrySet()) {
                    if (entry.getValue().isJsonObject() && entry.getValue().getAsJsonObject().has("mapValue")) {
                        CompletedQuestDTO completed = CompletedQuestDTO.fromJsonObject(entry.getValue().getAsJsonObject().getAsJsonObject("mapValue"));
                        completedQuests.put(entry.getKey(), completed);
                    }
                }
            }
        }
        
        long lastUpdated = fields.has("lastUpdated") && fields.getAsJsonObject("lastUpdated").has("integerValue")
                ? fields.getAsJsonObject("lastUpdated").get("integerValue").getAsLong()
                : System.currentTimeMillis();
        
        return new PlayerQuestDTO(playerId, activeQuests, completedQuests, lastUpdated);
    }
}