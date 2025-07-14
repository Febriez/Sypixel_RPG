package com.febrie.rpg.quest.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

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
