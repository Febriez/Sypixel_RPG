package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.event.PlayerLevelUpEvent;
import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * 레벨 달성 퀘스트 목표
 * 특정 레벨에 도달
 *
 * @author Febrie
 */
public class ReachLevelObjective extends BaseObjective {

    public enum LevelType {
        PLAYER("Player"),
        RPG("RPG"),
        SKILL("Skill"),
        JOB("Job");

        private final String displayName;

        LevelType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private final LevelType levelType;
    private final int targetLevel;

    /**
     * 기본 생성자 - RPG 레벨
     *
     * @param id          목표 ID
     * @param targetLevel 목표 레벨
     */
    public ReachLevelObjective(@NotNull String id, int targetLevel) {
        this(id, LevelType.RPG, targetLevel);
    }

    /**
     * 레벨 타입 지정 생성자
     *
     * @param id          목표 ID
     * @param levelType   레벨 타입
     * @param targetLevel 목표 레벨
     */
    public ReachLevelObjective(@NotNull String id, @NotNull LevelType levelType, int targetLevel) {
        super(id, 1);
        this.levelType = levelType;
        this.targetLevel = targetLevel;

        if (targetLevel <= 0) {
            throw new IllegalArgumentException("Target level must be positive: " + targetLevel);
        }
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.REACH_LEVEL;
    }

    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        String status = levelType.getDisplayName() + " Lv." + targetLevel;
        if (progress.isCompleted()) {
            return status + " ✓";
        }
        return status;
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        if (!(event instanceof PlayerLevelUpEvent levelUpEvent)) {
            return false;
        }

        // 플레이어 확인
        if (!levelUpEvent.getPlayer().equals(player)) {
            return false;
        }

        // 레벨 타입 확인
        if (!levelUpEvent.getLevelType().equals(levelType.name())) {
            return false;
        }

        // 목표 레벨 도달 확인
        return levelUpEvent.getNewLevel() >= targetLevel;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        return canProgress(event, player) ? 1 : 0;
    }

    @Override
    protected @NotNull String serializeData() {
        return levelType.name() + ";" + targetLevel;
    }

    public LevelType getLevelType() {
        return levelType;
    }

    public int getTargetLevel() {
        return targetLevel;
    }
}