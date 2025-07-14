package com.febrie.rpg.quest;

import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.quest.reward.QuestReward;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 퀘스트 기본 추상 클래스
 * 모든 퀘스트가 상속받아야 하는 기본 클래스
 *
 * @author Febrie
 */
public abstract class Quest {

    protected final QuestID id;
    protected final List<QuestObjective> objectives;
    protected final QuestReward reward;

    // 퀘스트 속성
    protected final boolean sequential;
    protected final boolean repeatable;
    protected final boolean daily;
    protected final boolean weekly;
    protected final int minLevel;
    protected final int maxLevel;
    protected final QuestCategory category;

    // 선행 퀘스트 시스템
    private final Set<QuestID> prerequisiteQuests = new HashSet<>();

    // 양자택일 퀘스트 시스템
    private final Set<QuestID> exclusiveQuests = new HashSet<>();

    /**
     * 빌더를 통한 생성자
     */
    protected Quest(@NotNull Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "Quest ID cannot be null");
        this.objectives = new ArrayList<>(builder.objectives);
        this.reward = Objects.requireNonNull(builder.reward, "Quest reward cannot be null");

        this.sequential = builder.sequential;
        this.repeatable = builder.repeatable;
        this.daily = builder.daily;
        this.weekly = builder.weekly;
        this.minLevel = builder.minLevel;
        this.maxLevel = builder.maxLevel;
        this.category = builder.category;

        this.prerequisiteQuests.addAll(builder.prerequisiteQuests);
        this.exclusiveQuests.addAll(builder.exclusiveQuests);

        if (objectives.isEmpty()) {
            throw new IllegalArgumentException("Quest must have at least one objective");
        }
    }

    /**
     * 퀘스트 ID 반환
     */
    public @NotNull QuestID getId() {
        return id;
    }

    /**
     * 퀘스트 표시 이름 (하드코딩)
     *
     * @param isKorean 한국어 여부
     * @return 퀘스트 이름
     */
    public abstract @NotNull String getDisplayName(boolean isKorean);

    /**
     * 퀘스트 설명 (하드코딩)
     *
     * @param isKorean 한국어 여부
     * @return 퀘스트 설명 (여러 줄)
     */
    public abstract @NotNull List<String> getDescription(boolean isKorean);

    /**
     * 퀘스트 대화 (없으면 null)
     *
     * @return 퀘스트 대화
     */
    @Nullable
    public QuestDialog getDialog() {
        return null; // 기본적으로 대화 없음, 필요한 퀘스트만 오버라이드
    }

    /**
     * 퀘스트 목표 목록 반환
     */
    public @NotNull List<QuestObjective> getObjectives() {
        return new ArrayList<>(objectives);
    }

    /**
     * 순차적 진행 여부
     */
    public boolean isSequential() {
        return sequential;
    }

    /**
     * 퀘스트 보상
     */
    public @NotNull QuestReward getReward() {
        return reward;
    }

    /**
     * 퀘스트 시작 가능 여부 확인 (하위 클래스에서 추가 조건 구현 가능)
     */
    public boolean canStart(@NotNull UUID playerId) {
        return true;
    }

    /**
     * 최소 레벨 요구사항
     */
    public int getMinLevel() {
        return minLevel;
    }

    /**
     * 최대 레벨 요구사항
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * 선행 퀘스트 목록
     */
    public @NotNull Set<QuestID> getPrerequisiteQuests() {
        return new HashSet<>(prerequisiteQuests);
    }

    /**
     * 양자택일 퀘스트 목록
     */
    public @NotNull Set<QuestID> getExclusiveQuests() {
        return new HashSet<>(exclusiveQuests);
    }

    /**
     * 선행 퀘스트 확인
     */
    public boolean hasPrerequisiteQuests() {
        return !prerequisiteQuests.isEmpty();
    }

    /**
     * 양자택일 퀘스트 확인
     */
    public boolean hasExclusiveQuests() {
        return !exclusiveQuests.isEmpty();
    }

    /**
     * 선행 퀘스트 완료 확인
     */
    public boolean arePrerequisitesComplete(@NotNull Collection<QuestID> completedQuests) {
        return completedQuests.containsAll(prerequisiteQuests);
    }

    /**
     * 양자택일 퀘스트 완료 확인
     */
    public boolean hasCompletedExclusiveQuests(@NotNull Collection<QuestID> completedQuests) {
        for (QuestID exclusiveQuest : exclusiveQuests) {
            if (completedQuests.contains(exclusiveQuest)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 퀘스트 카테고리
     */
    public @NotNull QuestCategory getCategory() {
        return category;
    }

    /**
     * 반복 가능 여부
     */
    public boolean isRepeatable() {
        return repeatable;
    }

    /**
     * 일일 퀘스트 여부
     */
    public boolean isDaily() {
        return daily;
    }

    /**
     * 주간 퀘스트 여부
     */
    public boolean isWeekly() {
        return weekly;
    }

    /**
     * 퀘스트 진행도 생성
     */
    public @NotNull QuestProgress createProgress(@NotNull UUID playerId) {
        Map<String, ObjectiveProgress> objectives = new HashMap<>();

        for (QuestObjective objective : this.objectives) {
            objectives.put(objective.getId(),
                    new ObjectiveProgress(objective.getId(), playerId, objective.getRequiredAmount()));
        }

        return new QuestProgress(id, playerId, objectives);
    }

    /**
     * 퀘스트 카테고리
     */
    public enum QuestCategory {
        MAIN("quest.category.main"),
        SIDE("quest.category.side"),
        DAILY("quest.category.daily"),
        WEEKLY("quest.category.weekly"),
        EVENT("quest.category.event"),
        TUTORIAL("quest.category.tutorial"),
        NORMAL("quest.category.normal");

        private final String translationKey;

        QuestCategory(String translationKey) {
            this.translationKey = translationKey;
        }

        public String getTranslationKey() {
            return translationKey;
        }
    }

    /**
     * 퀘스트 빌더
     */
    public static abstract class Builder {
        protected QuestID id;
        protected List<QuestObjective> objectives = new ArrayList<>();
        protected QuestReward reward;
        protected boolean sequential = false;
        protected boolean repeatable = false;
        protected boolean daily = false;
        protected boolean weekly = false;
        protected int minLevel = 1;
        protected int maxLevel = 0;
        protected QuestCategory category = QuestCategory.NORMAL;
        protected Set<QuestID> prerequisiteQuests = new HashSet<>();
        protected Set<QuestID> exclusiveQuests = new HashSet<>();

        public Builder id(@NotNull QuestID id) {
            this.id = id;
            return this;
        }

        public Builder objectives(@NotNull List<QuestObjective> objectives) {
            this.objectives = new ArrayList<>(objectives);
            return this;
        }

        public Builder addObjective(@NotNull QuestObjective objective) {
            this.objectives.add(objective);
            return this;
        }

        public Builder reward(@NotNull QuestReward reward) {
            this.reward = reward;
            return this;
        }

        public Builder sequential(boolean sequential) {
            this.sequential = sequential;
            return this;
        }

        public Builder repeatable(boolean repeatable) {
            this.repeatable = repeatable;
            return this;
        }

        public Builder daily(boolean daily) {
            this.daily = daily;
            if (daily) {
                this.repeatable = true; // 일일 퀘스트는 자동으로 반복 가능
            }
            return this;
        }

        public Builder weekly(boolean weekly) {
            this.weekly = weekly;
            if (weekly) {
                this.repeatable = true; // 주간 퀘스트는 자동으로 반복 가능
            }
            return this;
        }

        public Builder minLevel(int minLevel) {
            this.minLevel = minLevel;
            return this;
        }

        public Builder maxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
            return this;
        }

        public Builder category(@NotNull QuestCategory category) {
            this.category = category;
            return this;
        }

        public Builder addPrerequisite(@NotNull QuestID questId) {
            this.prerequisiteQuests.add(questId);
            return this;
        }

        public Builder addExclusive(@NotNull QuestID questId) {
            this.exclusiveQuests.add(questId);
            return this;
        }

        public abstract Quest build();
    }
}