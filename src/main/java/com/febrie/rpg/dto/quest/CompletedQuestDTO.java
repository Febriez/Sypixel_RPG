package com.febrie.rpg.dto.quest;

import com.febrie.rpg.util.JsonUtil;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * 완료된 퀘스트 정보 DTO
 * Firebase 저장용 데이터 구조
 *
 * @author Febrie
 */
public record CompletedQuestDTO(
        @NotNull String questId,
        long completedAt,      // 퀘스트 완료 시간
        int completionCount,   // 완료 횟수 (반복 퀘스트용)
        boolean rewarded       // 보상 수령 여부
) {
    /**
     * 기본 생성자 - 신규 완료 퀘스트용
     */
    public CompletedQuestDTO() {
        this("", System.currentTimeMillis(), 1, false);
    }
    
    /**
     * 간편 생성자 (이전 버전 호환용)
     */
    public CompletedQuestDTO(@NotNull String questId, long completedAt, int completionCount) {
        this(questId, completedAt, completionCount, false);
    }
    
    /**
     * 퀘스트 완료 생성 팩토리 메소드
     */
    @NotNull
    public static CompletedQuestDTO createCompleted(@NotNull String questId) {
        return new CompletedQuestDTO(questId, System.currentTimeMillis(), 1, false);
    }
    
    /**
     * JsonObject로 변환
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        fields.add("questId", JsonUtil.createStringValue(questId));
        fields.add("completedAt", JsonUtil.createIntegerValue(completedAt));
        fields.add("completionCount", JsonUtil.createIntegerValue(completionCount));
        fields.add("rewarded", JsonUtil.createBooleanValue(rewarded));
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 CompletedQuestDTO 생성
     */
    @NotNull
    public static CompletedQuestDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return new CompletedQuestDTO();
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        String questId = JsonUtil.getStringValue(fields, "questId", "");
        long completedAt = JsonUtil.getLongValue(fields, "completedAt", System.currentTimeMillis());
        int completionCount = (int) JsonUtil.getLongValue(fields, "completionCount", 1L);
        boolean rewarded = JsonUtil.getBooleanValue(fields, "rewarded", false);
        
        return new CompletedQuestDTO(questId, completedAt, completionCount, rewarded);
    }
}