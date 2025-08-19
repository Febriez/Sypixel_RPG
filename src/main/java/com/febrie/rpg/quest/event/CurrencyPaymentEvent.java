package com.febrie.rpg.quest.event;

import com.febrie.rpg.economy.CurrencyType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;
/**
 * 재화 지불 이벤트
 * 플레이어가 재화를 사용할 때 발생
 *
 * @author Febrie
 */
public class CurrencyPaymentEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final CurrencyType currencyType;
    private final int amount;
    private final @Nullable String targetNpc;
    private final String reason;

    /**
     * 기본 생성자
     *
     * @param player       플레이어
     * @param currencyType 재화 타입
     * @param amount       지불 금액
     * @param targetNpc    대상 NPC (선택사항)
     * @param reason       지불 사유
     */
    public CurrencyPaymentEvent(@NotNull Player player, @NotNull CurrencyType currencyType,
                                int amount, @Nullable String targetNpc, @NotNull String reason) {
        this.player = player;
        this.currencyType = currencyType;
        this.amount = amount;
        this.targetNpc = targetNpc;
        this.reason = reason;
    }

    /**
     * 간편 생성자 (NPC 없이)
     *
     * @param player       플레이어
     * @param currencyType 재화 타입
     * @param amount       지불 금액
     * @param reason       지불 사유
     */
    public CurrencyPaymentEvent(@NotNull Player player, @NotNull CurrencyType currencyType,
                                int amount, @NotNull String reason) {
        this(player, currencyType, amount, null, reason);
    }

    public @NotNull Player getPlayer() {
        return player;
    }


    /**
     * 재화 타입 enum 반환
     */
    public @NotNull CurrencyType getCurrencyType() {
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