package com.febrie.rpg.quest.progress;

import com.febrie.rpg.quest.QuestID;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 퀘스트 진행도
 * 플레이어의 특정 퀘스트 진행 상태를 추적
 *
 * @author Febrie
 */
public class QuestProgress {

    /**
     * 퀘스트 상태
     */
    public enum QuestState {
        ACTIVE,     // 진행 중
        COMPLETED,  // 완료됨
        ABANDONED,  // 포기함
        FAILED      // 실패함
    }

    private final QuestID questId;
    private final UUID playerId;
    private final Map<String, ObjectiveProgress> objectives;
    private QuestState state;
    private int currentObjectiveIndex;
    private final Instant startedAt;
    private Instant completedAt;
    private Instant lastUpdatedAt;

    /**
     * 기본 생성자
     *
     * @param questId    퀘스트 ID
     * @param playerId   플레이어 UUID
     * @param objectives 목표 진행도 맵
     */
    public QuestProgress(@NotNull QuestID questId, @NotNull UUID playerId,
                         @NotNull Map<String, ObjectiveProgress> objectives) {
        this.questId = Objects.requireNonNull(questId);
        this.playerId = Objects.requireNonNull(playerId);
        this.objectives = new HashMap<>(objectives);
        this.state = QuestState.ACTIVE;
        this.currentObjectiveIndex = 0;
        this.startedAt = Instant.now();
        this.lastUpdatedAt = Instant.now();
    }

    /**
     * 전체 생성자 (데이터 로드용)
     */
    public QuestProgress(@NotNull QuestID questId, @NotNull UUID playerId,
                         @NotNull Map<String, ObjectiveProgress> objectives,
                         @NotNull QuestState state, int currentObjectiveIndex,
                         @NotNull Instant startedAt, @Nullable Instant completedAt,
                         @NotNull Instant lastUpdatedAt) {
        this.questId = Objects.requireNonNull(questId);
        this.playerId = Objects.requireNonNull(playerId);
        this.objectives = new HashMap<>(objectives);
        this.state = Objects.requireNonNull(state);
        this.currentObjectiveIndex = currentObjectiveIndex;
        this.startedAt = Objects.requireNonNull(startedAt);
        this.completedAt = completedAt;
        this.lastUpdatedAt = Objects.requireNonNull(lastUpdatedAt);
    }

    /**
     * 특정 목표의 진행도 가져오기
     *
     * @param objectiveId 목표 ID
     * @return 목표 진행도 (없으면 null)
     */
    @Nullable
    public ObjectiveProgress getObjective(@NotNull String objectiveId) {
        return objectives.get(objectiveId);
    }

    /**
     * 목표 진행도 가져오기 (null-safe)
     */
    @Nullable
    public ObjectiveProgress getObjectiveProgress(@NotNull String objectiveId) {
        return objectives.get(objectiveId);
    }

    /**
     * 모든 목표 진행도 가져오기
     *
     * @return 목표 진행도 맵의 복사본
     */
    @NotNull
    public Map<String, ObjectiveProgress> getObjectives() {
        return new HashMap<>(objectives);
    }

    /**
     * 특정 목표 완료 여부 확인
     *
     * @param objectiveId 목표 ID
     * @return 완료 여부
     */
    public boolean isObjectiveComplete(@NotNull String objectiveId) {
        ObjectiveProgress progress = objectives.get(objectiveId);
        return progress != null && progress.isCompleted();
    }

    /**
     * 전체 퀘스트 진행률 계산
     *
     * @return 0.0 ~ 1.0 사이의 진행률
     */
    public double getOverallProgress() {
        if (objectives.isEmpty()) return 0.0;

        int completed = 0;
        for (ObjectiveProgress progress : objectives.values()) {
            if (progress.isCompleted()) {
                completed++;
            }
        }

        return (double) completed / objectives.size();
    }

    /**
     * 완료 퍼센트 (0-100)
     */
    public int getCompletionPercentage() {
        return (int) (getOverallProgress() * 100);
    }

    /**
     * 모든 목표 완료 여부 확인
     *
     * @return 모든 목표 완료 여부
     */
    public boolean areAllObjectivesComplete() {
        return objectives.values().stream().allMatch(ObjectiveProgress::isCompleted);
    }

    /**
     * 진행도 업데이트 시간 갱신
     */
    public void updateTimestamp() {
        this.lastUpdatedAt = Instant.now();
    }

    // Getters
    @NotNull
    public QuestID getQuestId() {
        return questId;
    }

    @NotNull
    public UUID getPlayerId() {
        return playerId;
    }

    @NotNull
    public QuestState getState() {
        return state;
    }

    public int getCurrentObjectiveIndex() {
        return currentObjectiveIndex;
    }

    @NotNull
    public Instant getStartedAt() {
        return startedAt;
    }

    @Nullable
    public Instant getCompletedAt() {
        return completedAt;
    }

    @NotNull
    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    // Setters
    public void setState(@NotNull QuestState state) {
        this.state = Objects.requireNonNull(state);
        updateTimestamp();
    }

    public void setCurrentObjectiveIndex(int currentObjectiveIndex) {
        this.currentObjectiveIndex = currentObjectiveIndex;
        updateTimestamp();
    }

    public void setCompletedAt(@NotNull Instant completedAt) {
        this.completedAt = Objects.requireNonNull(completedAt);
        updateTimestamp();
    }

    /**
     * 퀘스트 완료 처리
     */
    public void complete() {
        this.state = QuestState.COMPLETED;
        this.completedAt = Instant.now();
        updateTimestamp();
    }

    /**
     * 퀘스트 포기 처리
     */
    public void abandon() {
        this.state = QuestState.ABANDONED;
        updateTimestamp();
    }

    /**
     * 퀘스트 실패 처리
     */
    public void fail() {
        this.state = QuestState.FAILED;
        updateTimestamp();
    }

    /**
     * 활성 상태인지 확인
     *
     * @return 활성 상태 여부
     */
    public boolean isActive() {
        return state == QuestState.ACTIVE;
    }

    /**
     * 완료 상태인지 확인
     *
     * @return 완료 상태 여부
     */
    public boolean isCompleted() {
        return state == QuestState.COMPLETED;
    }

    /**
     * 진행 시간 계산 (밀리초)
     *
     * @return 진행 시간
     */
    public long getDuration() {
        if (completedAt != null) {
            return completedAt.toEpochMilli() - startedAt.toEpochMilli();
        }
        return Instant.now().toEpochMilli() - startedAt.toEpochMilli();
    }

    @Override
    public String toString() {
        return "QuestProgress{" +
                "questId=" + questId +
                ", playerId=" + playerId +
                ", state=" + state +
                ", progress=" + String.format("%.1f%%", getOverallProgress() * 100) +
                ", objectives=" + objectives.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestProgress that)) return false;
        return Objects.equals(questId, that.questId) &&
                Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questId, playerId);
    }
    
    /**
     * JsonObject로 변환 (Firebase 저장용)
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        // questId
        JsonObject questIdValue = new JsonObject();
        questIdValue.addProperty("stringValue", questId.name());
        fields.add("questId", questIdValue);
        
        // playerId
        JsonObject playerIdValue = new JsonObject();
        playerIdValue.addProperty("stringValue", playerId.toString());
        fields.add("playerId", playerIdValue);
        
        // objectives
        JsonObject objectivesValue = new JsonObject();
        JsonObject objectivesMap = new JsonObject();
        JsonObject objectivesFields = new JsonObject();
        
        objectives.forEach((id, progress) -> {
            JsonObject progressValue = new JsonObject();
            progressValue.add("mapValue", progress.toJsonObject());
            objectivesFields.add(id, progressValue);
        });
        
        objectivesMap.add("fields", objectivesFields);
        objectivesValue.add("mapValue", objectivesMap);
        fields.add("objectives", objectivesValue);
        
        // state
        JsonObject stateValue = new JsonObject();
        stateValue.addProperty("stringValue", state.name());
        fields.add("state", stateValue);
        
        // currentObjectiveIndex
        JsonObject currentObjectiveIndexValue = new JsonObject();
        currentObjectiveIndexValue.addProperty("integerValue", currentObjectiveIndex);
        fields.add("currentObjectiveIndex", currentObjectiveIndexValue);
        
        // startedAt
        JsonObject startedAtValue = new JsonObject();
        startedAtValue.addProperty("integerValue", startedAt.toEpochMilli());
        fields.add("startedAt", startedAtValue);
        
        // completedAt
        if (completedAt != null) {
            JsonObject completedAtValue = new JsonObject();
            completedAtValue.addProperty("integerValue", completedAt.toEpochMilli());
            fields.add("completedAt", completedAtValue);
        }
        
        // lastUpdatedAt
        JsonObject lastUpdatedAtValue = new JsonObject();
        lastUpdatedAtValue.addProperty("integerValue", lastUpdatedAt.toEpochMilli());
        fields.add("lastUpdatedAt", lastUpdatedAtValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 QuestProgress 생성
     */
    @NotNull
    public static QuestProgress fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            throw new IllegalArgumentException("Invalid QuestProgress JSON structure");
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        String questIdStr = fields.has("questId") && fields.getAsJsonObject("questId").has("stringValue")
                ? fields.getAsJsonObject("questId").get("stringValue").getAsString()
                : "";
        QuestID questId = QuestID.valueOf(questIdStr);
        
        String playerIdStr = fields.has("playerId") && fields.getAsJsonObject("playerId").has("stringValue")
                ? fields.getAsJsonObject("playerId").get("stringValue").getAsString()
                : "";
        UUID playerId = UUID.fromString(playerIdStr);
        
        Map<String, ObjectiveProgress> objectives = new HashMap<>();
        if (fields.has("objectives") && fields.getAsJsonObject("objectives").has("mapValue")) {
            JsonObject objectivesMap = fields.getAsJsonObject("objectives").getAsJsonObject("mapValue");
            if (objectivesMap.has("fields")) {
                JsonObject objectivesFields = objectivesMap.getAsJsonObject("fields");
                for (Map.Entry<String, JsonElement> entry : objectivesFields.entrySet()) {
                    if (entry.getValue().isJsonObject() && entry.getValue().getAsJsonObject().has("mapValue")) {
                        ObjectiveProgress progress = ObjectiveProgress.fromJsonObject(entry.getValue().getAsJsonObject().getAsJsonObject("mapValue"));
                        objectives.put(entry.getKey(), progress);
                    }
                }
            }
        }
        
        String stateStr = fields.has("state") && fields.getAsJsonObject("state").has("stringValue")
                ? fields.getAsJsonObject("state").get("stringValue").getAsString()
                : "ACTIVE";
        QuestState state = QuestState.valueOf(stateStr);
        
        int currentObjectiveIndex = fields.has("currentObjectiveIndex") && fields.getAsJsonObject("currentObjectiveIndex").has("integerValue")
                ? fields.getAsJsonObject("currentObjectiveIndex").get("integerValue").getAsInt()
                : 0;
                
        long startedAtMs = fields.has("startedAt") && fields.getAsJsonObject("startedAt").has("integerValue")
                ? fields.getAsJsonObject("startedAt").get("integerValue").getAsLong()
                : System.currentTimeMillis();
        Instant startedAt = Instant.ofEpochMilli(startedAtMs);
        
        Instant completedAt = null;
        if (fields.has("completedAt") && fields.getAsJsonObject("completedAt").has("integerValue")) {
            long completedAtMs = fields.getAsJsonObject("completedAt").get("integerValue").getAsLong();
            completedAt = Instant.ofEpochMilli(completedAtMs);
        }
        
        long lastUpdatedAtMs = fields.has("lastUpdatedAt") && fields.getAsJsonObject("lastUpdatedAt").has("integerValue")
                ? fields.getAsJsonObject("lastUpdatedAt").get("integerValue").getAsLong()
                : System.currentTimeMillis();
        Instant lastUpdatedAt = Instant.ofEpochMilli(lastUpdatedAtMs);
        
        return new QuestProgress(questId, playerId, objectives, state, currentObjectiveIndex,
                startedAt, completedAt, lastUpdatedAt);
    }
    
    /**
     * Map으로 변환 (Firestore SDK용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("questId", questId.name());
        map.put("playerId", playerId.toString());
        
        // objectives
        Map<String, Object> objectivesMap = new HashMap<>();
        objectives.forEach((id, progress) -> objectivesMap.put(id, progress.toMap()));
        map.put("objectives", objectivesMap);
        
        map.put("state", state.name());
        map.put("currentObjectiveIndex", currentObjectiveIndex);
        map.put("startedAt", startedAt.toEpochMilli());
        
        if (completedAt != null) {
            map.put("completedAt", completedAt.toEpochMilli());
        }
        
        map.put("lastUpdatedAt", lastUpdatedAt.toEpochMilli());
        
        return map;
    }
    
    /**
     * Map에서 QuestProgress 생성 (Firestore SDK용)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static QuestProgress fromMap(@NotNull Map<String, Object> map) {
        String questIdStr = (String) map.getOrDefault("questId", "");
        QuestID questId = QuestID.valueOf(questIdStr);
        
        String playerIdStr = (String) map.getOrDefault("playerId", "");
        UUID playerId = UUID.fromString(playerIdStr);
        
        Map<String, ObjectiveProgress> objectives = new HashMap<>();
        Object objectivesObj = map.get("objectives");
        if (objectivesObj instanceof Map) {
            Map<String, Object> objectivesMap = (Map<String, Object>) objectivesObj;
            objectivesMap.forEach((id, value) -> {
                if (value instanceof Map) {
                    objectives.put(id, ObjectiveProgress.fromMap((Map<String, Object>) value));
                }
            });
        }
        
        String stateStr = (String) map.getOrDefault("state", "ACTIVE");
        QuestState state = QuestState.valueOf(stateStr);
        
        int currentObjectiveIndex = ((Number) map.getOrDefault("currentObjectiveIndex", 0)).intValue();
        
        long startedAtMs = ((Number) map.getOrDefault("startedAt", System.currentTimeMillis())).longValue();
        Instant startedAt = Instant.ofEpochMilli(startedAtMs);
        
        Instant completedAt = null;
        if (map.containsKey("completedAt")) {
            long completedAtMs = ((Number) map.get("completedAt")).longValue();
            completedAt = Instant.ofEpochMilli(completedAtMs);
        }
        
        long lastUpdatedAtMs = ((Number) map.getOrDefault("lastUpdatedAt", System.currentTimeMillis())).longValue();
        Instant lastUpdatedAt = Instant.ofEpochMilli(lastUpdatedAtMs);
        
        return new QuestProgress(questId, playerId, objectives, state, currentObjectiveIndex,
                startedAt, completedAt, lastUpdatedAt);
    }
}