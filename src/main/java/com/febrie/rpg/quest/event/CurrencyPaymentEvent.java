package com.febrie.rpg.quest.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 재화 지불 이벤트
 * 플레이어가 재화를 사용할 때 발생
 *
 * @author Febrie
 */
public class CurrencyPaymentEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String currencyType;
    private final int amount;
    private final @Nullable String targetNpc;
    private final String reason;

    public CurrencyPaymentEvent(@NotNull Player player, @NotNull String currencyType,
                                int amount, @Nullable String targetNpc, @NotNull String reason) {
        this.player = player;
        this.currencyType = currencyType;
        this.amount = amount;
        this.targetNpc = targetNpc;
        this.reason = reason;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull String getCurrencyType() {
        return currencyType;
    }

    public int getAmount() {
        return amount;
    }

    public @Nullable String getTargetNpc() {
        return targetNpc;
    }

    public @NotNull String getReason() {
        return reason;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

/**
 * 플레이어 레벨업 이벤트
 * RPG 시스템에서 레벨이 오를 때 발생
 *
 * @author Febrie
 */
public class PlayerLevelUpEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String levelType;
    private final int oldLevel;
    private final int newLevel;

    public PlayerLevelUpEvent(@NotNull Player player, @NotNull String levelType,
                              int oldLevel, int newLevel) {
        this.player = player;
        this.levelType = levelType;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull String getLevelType() {
        return levelType;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public int getLevelGained() {
        return newLevel - oldLevel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

/**
 * 퀘스트 시작 이벤트
 *
 * @author Febrie
 */
public class QuestStartEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String questId;
    private final String questName;

    public QuestStartEvent(@NotNull Player player, @NotNull String questId, @NotNull String questName) {
        this.player = player;
        this.questId = questId;
        this.questName = questName;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull String getQuestId() {
        return questId;
    }

    public @NotNull String getQuestName() {
        return questName;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

/**
 * 퀘스트 완료 이벤트
 *
 * @author Febrie
 */
public class QuestCompleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String questId;
    private final String questName;

    public QuestCompleteEvent(@NotNull Player player, @NotNull String questId, @NotNull String questName) {
        this.player = player;
        this.questId = questId;
        this.questName = questName;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull String getQuestId() {
        return questId;
    }

    public @NotNull String getQuestName() {
        return questName;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

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