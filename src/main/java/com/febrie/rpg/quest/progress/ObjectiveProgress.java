package com.febrie.rpg.quest.progress;

import com.febrie.rpg.dto.quest.ObjectiveProgressDTO;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 퀘스트 목표 진행도
 * 각 목표의 현재 진행 상태를 추적
 *
 * @author Febrie
 */
public class ObjectiveProgress {

    private final String objectiveId;
    private final UUID playerId;
    private int currentValue;
    private final int requiredValue;
    private boolean completed;
    private final long startedAt;
    private long completedAt;
    private long lastUpdated;


    /**
     * 새로운 진행도 생성
     *
     * @param objectiveId   목표 ID
     * @param playerId      플레이어 UUID
     * @param requiredValue 필요 수치
     */
    public ObjectiveProgress(@NotNull String objectiveId, @NotNull UUID playerId, int requiredValue) {
        this(objectiveId, playerId, 0, requiredValue, false, System.currentTimeMillis(), 0);
    }

    /**
     * 전체 데이터로 생성
     *
     * @param objectiveId   목표 ID
     * @param playerId      플레이어 UUID
     * @param currentValue  현재 수치
     * @param requiredValue 필요 수치
     * @param completed     완료 여부
     */
    public ObjectiveProgress(@NotNull String objectiveId, @NotNull UUID playerId, int currentValue, int requiredValue, boolean completed) {
        this(objectiveId, playerId, currentValue, requiredValue, completed, System.currentTimeMillis(), completed ? System.currentTimeMillis() : 0);
    }

    /**
     * 모든 필드 포함 생성자
     *
     * @param objectiveId   목표 ID
     * @param playerId      플레이어 UUID
     * @param currentValue  현재 수치
     * @param requiredValue 필요 수치
     * @param completed     완료 여부
     * @param startedAt     시작 시간
     * @param completedAt   완료 시간
     */
    public ObjectiveProgress(@NotNull String objectiveId, @NotNull UUID playerId, int currentValue, int requiredValue, boolean completed, long startedAt, long completedAt) {
        this.objectiveId = Objects.requireNonNull(objectiveId);
        this.playerId = Objects.requireNonNull(playerId);
        this.currentValue = currentValue;
        this.requiredValue = requiredValue;
        this.completed = completed;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.lastUpdated = System.currentTimeMillis();

        if (requiredValue <= 0) {
            throw new IllegalArgumentException("Required value must be positive");
        }
    }

    /**
     * 진행도 업데이트
     *
     * @param newValue 새로운 값
     */
    public void update(int newValue) {
        if (newValue < 0) {
            throw new IllegalArgumentException("Progress cannot be negative");
        }

        this.currentValue = Math.min(newValue, requiredValue);
        this.lastUpdated = System.currentTimeMillis();

        if (this.currentValue >= requiredValue && !this.completed) {
            this.completed = true;
            this.completedAt = System.currentTimeMillis();
        }
    }

    /**
     * 진행도 증가
     *
     * @param amount 증가량
     */
    public void increment(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Increment cannot be negative");
        }

        update(currentValue + amount);
    }


    /**
     * 진행도 리셋
     */
    public void reset() {
        this.currentValue = 0;
        this.completed = false;
        this.completedAt = 0;
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * 진행률 계산 (0.0 ~ 1.0)
     *
     * @return 진행률
     */
    public double getProgress() {
        return (double) currentValue / requiredValue; // double (primitive)
    }

    /**
     * 진행률 퍼센트 (0 ~ 100)
     *
     * @return 퍼센트
     */
    public int getProgressPercentage() {
        return (int) (getProgress() * 100);
    }

    /**
     * 남은 수치
     *
     * @return 완료까지 필요한 수치
     */
    public int getRemaining() {
        return Math.max(0, requiredValue - currentValue);
    }

    // Getters
    public @NotNull String getObjectiveId() {
        return objectiveId;
    }

    public @NotNull UUID getPlayerId() {
        return playerId;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public int getRequiredValue() {
        return requiredValue;
    }

    /**
     * RequiredAmount와 동일 (인터페이스 호환성)
     */
    public int getRequiredAmount() {
        return requiredValue;
    }

    public boolean isCompleted() {
        return completed;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public long getCompletedAt() {
        return completedAt;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectiveProgress that)) return false;
        return Objects.equals(objectiveId, that.objectiveId) && Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectiveId, playerId);
    }

    @Override
    public String toString() {
        return String.format("ObjectiveProgress{id=%s, player=%s, progress=%d/%d, completed=%s}", objectiveId, playerId, currentValue, requiredValue, completed);
    }

    /**
     * JsonObject로 변환 (Firebase 저장용)
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();

        // objectiveId
        JsonObject objectiveIdValue = new JsonObject();
        objectiveIdValue.addProperty("stringValue", objectiveId);
        fields.add("objectiveId", objectiveIdValue);

        // playerId
        JsonObject playerIdValue = new JsonObject();
        playerIdValue.addProperty("stringValue", playerId.toString());
        fields.add("playerId", playerIdValue);

        // currentValue
        JsonObject currentValueValue = new JsonObject();
        currentValueValue.addProperty("integerValue", currentValue);
        fields.add("currentValue", currentValueValue);

        // requiredValue
        JsonObject requiredValueValue = new JsonObject();
        requiredValueValue.addProperty("integerValue", requiredValue);
        fields.add("requiredValue", requiredValueValue);

        // completed
        JsonObject completedValue = new JsonObject();
        completedValue.addProperty("booleanValue", completed);
        fields.add("completed", completedValue);

        // startedAt
        JsonObject startedAtValue = new JsonObject();
        startedAtValue.addProperty("integerValue", startedAt);
        fields.add("startedAt", startedAtValue);

        // completedAt
        JsonObject completedAtValue = new JsonObject();
        completedAtValue.addProperty("integerValue", completedAt);
        fields.add("completedAt", completedAtValue);

        // lastUpdated
        JsonObject lastUpdatedValue = new JsonObject();
        lastUpdatedValue.addProperty("integerValue", lastUpdated);
        fields.add("lastUpdated", lastUpdatedValue);

        json.add("fields", fields);
        return json;
    }

    /**
     * JsonObject에서 ObjectiveProgress 생성
     */
    @NotNull
    public static ObjectiveProgress fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            throw new IllegalArgumentException("Invalid ObjectiveProgress JSON structure");
        }

        JsonObject fields = json.getAsJsonObject("fields");

        String objectiveId = fields.has("objectiveId") && fields.getAsJsonObject("objectiveId").has("stringValue") ? fields.getAsJsonObject("objectiveId").get("stringValue").getAsString() : "";

        String playerIdStr = fields.has("playerId") && fields.getAsJsonObject("playerId").has("stringValue") ? fields.getAsJsonObject("playerId").get("stringValue").getAsString() : "";
        UUID playerId = UUID.fromString(playerIdStr);

        int currentValue = fields.has("currentValue") && fields.getAsJsonObject("currentValue").has("integerValue") ? fields.getAsJsonObject("currentValue").get("integerValue").getAsInt() : 0;

        int requiredValue = fields.has("requiredValue") && fields.getAsJsonObject("requiredValue").has("integerValue") ? fields.getAsJsonObject("requiredValue").get("integerValue").getAsInt() : 1;

        boolean completed = fields.has("completed") && fields.getAsJsonObject("completed").has("booleanValue") && fields.getAsJsonObject("completed").get("booleanValue").getAsBoolean();

        long startedAt = fields.has("startedAt") && fields.getAsJsonObject("startedAt").has("integerValue") ? fields.getAsJsonObject("startedAt").get("integerValue").getAsLong() : System.currentTimeMillis();

        long completedAt = fields.has("completedAt") && fields.getAsJsonObject("completedAt").has("integerValue") ? fields.getAsJsonObject("completedAt").get("integerValue").getAsLong() : 0;

        return new ObjectiveProgress(objectiveId, playerId, currentValue, requiredValue, completed, startedAt, completedAt);
    }

    /**
     * Map으로 변환 (Firestore SDK용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("objectiveId", objectiveId);
        map.put("playerId", playerId.toString());
        map.put("currentValue", currentValue); // int (primitive)
        map.put("requiredValue", requiredValue); // int (primitive)
        map.put("completed", completed); // boolean (primitive)
        map.put("startedAt", startedAt); // long (primitive)
        map.put("completedAt", completedAt); // long (primitive)
        map.put("lastUpdated", lastUpdated); // long (primitive)

        return map;
    }

    /**
     * Map에서 ObjectiveProgress 생성 (Firestore SDK용)
     */
    @NotNull
    public static ObjectiveProgress fromMap(@NotNull Map<String, Object> map) {
        String objectiveId = (String) map.getOrDefault("objectiveId", "");
        String playerIdStr = (String) map.getOrDefault("playerId", "");
        UUID playerId = UUID.fromString(playerIdStr);

        // Number로 안전하게 캐스팅 후 primitive로 변환
        Object currentObj = map.getOrDefault("currentValue", 0);
        int currentValue = currentObj instanceof Number ? ((Number) currentObj).intValue() : 0;
        
        Object requiredObj = map.getOrDefault("requiredValue", 1);
        int requiredValue = requiredObj instanceof Number ? ((Number) requiredObj).intValue() : 1;
        
        Object completedObj = map.getOrDefault("completed", false);
        boolean completed = completedObj instanceof Boolean ? (boolean) completedObj : false;
        
        Object startedObj = map.getOrDefault("startedAt", System.currentTimeMillis());
        long startedAt = startedObj instanceof Number ? ((Number) startedObj).longValue() : System.currentTimeMillis();
        
        Object completedAtObj = map.getOrDefault("completedAt", 0L);
        long completedAt = completedAtObj instanceof Number ? ((Number) completedAtObj).longValue() : 0L;

        return new ObjectiveProgress(objectiveId, playerId, currentValue, requiredValue, completed, startedAt, completedAt);
    }

    /**
     * ObjectiveProgressDTO에서 생성
     */
    @NotNull
    public static ObjectiveProgress from(@NotNull ObjectiveProgressDTO dto, @NotNull UUID playerId) {
        return new ObjectiveProgress(dto.objectiveId(), playerId, dto.progress(), dto.target(), dto.completed(), System.currentTimeMillis(), dto.completed() ? dto.lastUpdated() : 0L);
    }
}