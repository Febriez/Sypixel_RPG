package com.febrie.rpg.quest.builder;

import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.reward.QuestReward;
import com.febrie.rpg.quest.reward.RewardDeliveryType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 퀘스트 빌더 - Quest.java에서 분리
 * 퀘스트 생성을 위한 빌더 패턴 구현
 *
 * @author Febrie
 */
public abstract class QuestBuilder {
    public QuestID id;
    public List<QuestObjective> objectives = new ArrayList<>();
    public QuestReward reward;
    public boolean sequential = false;
    public boolean repeatable = false;
    public boolean daily = false;
    public boolean weekly = false;
    public int minLevel = 1;
    public int maxLevel = 0;
    public QuestCategory category = QuestCategory.SIDE;
    public Set<QuestID> prerequisiteQuests = new HashSet<>();
    public Set<QuestID> exclusiveQuests = new HashSet<>();
    public RewardDeliveryType rewardDeliveryType = RewardDeliveryType.NPC_VISIT;
    public int completionLimit = 1; // 기본값: 1회만 완료 가능

    public QuestBuilder id(@NotNull QuestID id) {
        this.id = id;
        return this;
    }

    public QuestBuilder objectives(@NotNull List<QuestObjective> objectives) {
        this.objectives = new ArrayList<>(objectives);
        return this;
    }

    public QuestBuilder addObjective(@NotNull QuestObjective objective) {
        this.objectives.add(objective);
        return this;
    }

    public QuestBuilder reward(@NotNull QuestReward reward) {
        this.reward = reward;
        return this;
    }

    public QuestBuilder sequential(boolean sequential) {
        this.sequential = sequential;
        return this;
    }

    public QuestBuilder repeatable(boolean repeatable) {
        this.repeatable = repeatable;
        return this;
    }

    public QuestBuilder daily(boolean daily) {
        this.daily = daily;
        if (daily) {
            this.repeatable = true; // 일일 퀘스트는 자동으로 반복 가능
        }
        return this;
    }

    public QuestBuilder weekly(boolean weekly) {
        this.weekly = weekly;
        if (weekly) {
            this.repeatable = true; // 주간 퀘스트는 자동으로 반복 가능
        }
        return this;
    }

    public QuestBuilder minLevel(int minLevel) {
        this.minLevel = minLevel;
        return this;
    }

    public QuestBuilder maxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    public QuestBuilder category(@NotNull QuestCategory category) {
        this.category = category;
        return this;
    }

    public QuestBuilder addPrerequisite(@NotNull QuestID questId) {
        this.prerequisiteQuests.add(questId);
        return this;
    }

    public QuestBuilder addExclusive(@NotNull QuestID questId) {
        this.exclusiveQuests.add(questId);
        return this;
    }
    
    public QuestBuilder rewardDeliveryType(@NotNull RewardDeliveryType type) {
        this.rewardDeliveryType = type;
        return this;
    }

    public QuestBuilder completionLimit(int limit) {
        this.completionLimit = limit;
        return this;
    }

    public abstract Quest build();
}