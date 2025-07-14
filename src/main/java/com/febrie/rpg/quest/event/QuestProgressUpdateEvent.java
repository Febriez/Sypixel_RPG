package com.febrie.rpg.quest.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * 퀘스트 진행도 업데이트 이벤트
 *
 * @author Febrie
 */
public class QuestProgressUpdateEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String questId;
    private final String objectiveId;
    private final int oldProgress;
    private final int newProgress;
    private final int maxProgress;

    public QuestProgressUpdateEvent(@NotNull Player player, @NotNull String questId,
                                    @NotNull String objectiveId, int oldProgress,
                                    int newProgress, int maxProgress) {
        this.player = player;
        this.questId = questId;
        this.objectiveId = objectiveId;
        this.oldProgress = oldProgress;
        this.newProgress = newProgress;
        this.maxProgress = maxProgress;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull String getQuestId() {
        return questId;
    }

    public @NotNull String getObjectiveId() {
        return objectiveId;
    }

    public int getOldProgress() {
        return oldProgress;
    }

    public int getNewProgress() {
        return newProgress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public boolean isCompleted() {
        return newProgress >= maxProgress;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
