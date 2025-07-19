package com.febrie.rpg.dto.quest;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * 완료된 퀘스트 정보 DTO
 * Firebase 저장용 데이터 구조
 *
 * @author Febrie
 */
public class CompletedQuestDTO {
    private String questId;
    private long completedAt;    // 퀘스트 완료 시간
    private int completionCount; // 완료 횟수 (반복 퀘스트용)
    private boolean rewarded;    // 보상 수령 여부
    
    /**
     * 기본 생성자
     */
    public CompletedQuestDTO() {
        this.questId = "";
        this.completedAt = System.currentTimeMillis();
        this.completionCount = 1;
        this.rewarded = false;
    }
    
    /**
     * 전체 생성자
     */
    public CompletedQuestDTO(String questId, long completedAt, int completionCount, boolean rewarded) {
        this.questId = questId;
        this.completedAt = completedAt;
        this.completionCount = completionCount;
        this.rewarded = rewarded;
    }
    
    /**
     * 간편 생성자 (이전 버전 호환용)
     */
    public CompletedQuestDTO(String questId, long completedAt, int completionCount) {
        this(questId, completedAt, completionCount, false);
    }
    
    // Getter methods
    public String getQuestId() {
        return questId;
    }
    
    public long getCompletedAt() {
        return completedAt;
    }
    
    public int getCompletionCount() {
        return completionCount;
    }
    
    public boolean isRewarded() {
        return rewarded;
    }
    
    // Setter methods
    public void setQuestId(String questId) {
        this.questId = questId;
    }
    
    public void setCompletedAt(long completedAt) {
        this.completedAt = completedAt;
    }
    
    public void setCompletionCount(int completionCount) {
        this.completionCount = completionCount;
    }
    
    public void setRewarded(boolean rewarded) {
        this.rewarded = rewarded;
    }
    
    /**
     * JsonObject로 변환
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        JsonObject questIdValue = new JsonObject();
        questIdValue.addProperty("stringValue", questId);
        fields.add("questId", questIdValue);
        
        JsonObject completedAtValue = new JsonObject();
        completedAtValue.addProperty("integerValue", completedAt);
        fields.add("completedAt", completedAtValue);
        
        JsonObject completionCountValue = new JsonObject();
        completionCountValue.addProperty("integerValue", completionCount);
        fields.add("completionCount", completionCountValue);
        
        JsonObject rewardedValue = new JsonObject();
        rewardedValue.addProperty("booleanValue", rewarded);
        fields.add("rewarded", rewardedValue);
        
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
        
        String questId = fields.has("questId") && fields.getAsJsonObject("questId").has("stringValue")
                ? fields.getAsJsonObject("questId").get("stringValue").getAsString()
                : "";
                
        long completedAt = fields.has("completedAt") && fields.getAsJsonObject("completedAt").has("integerValue")
                ? fields.getAsJsonObject("completedAt").get("integerValue").getAsLong()
                : System.currentTimeMillis();
                
        int completionCount = fields.has("completionCount") && fields.getAsJsonObject("completionCount").has("integerValue")
                ? fields.getAsJsonObject("completionCount").get("integerValue").getAsInt()
                : 1;
                
        boolean rewarded = fields.has("rewarded") && fields.getAsJsonObject("rewarded").has("booleanValue")
                ? fields.getAsJsonObject("rewarded").get("booleanValue").getAsBoolean()
                : false;
        
        return new CompletedQuestDTO(questId, completedAt, completionCount, rewarded);
    }
}