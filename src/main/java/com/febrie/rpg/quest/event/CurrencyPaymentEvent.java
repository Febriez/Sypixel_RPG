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

