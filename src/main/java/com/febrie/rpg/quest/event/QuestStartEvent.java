package com.febrie.rpg.quest.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
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
