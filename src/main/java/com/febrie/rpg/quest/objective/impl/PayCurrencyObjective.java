package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.event.CurrencyPaymentEvent;
import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 재화 지불 퀘스트 목표
 * 특정 NPC에게 재화 지불
 *
 * @author Febrie
 */
public class PayCurrencyObjective extends BaseObjective {

    public enum CurrencyType {
        GOLD("currency.gold", "G"),
        GEM("currency.gem", "💎"),
        TOKEN("currency.token", "T");

        private final String translationKey;
        private final String symbol;

        CurrencyType(String translationKey, String symbol) {
            this.translationKey = translationKey;
            this.symbol = symbol;
        }

        public String getTranslationKey() {
            return translationKey;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    private final CurrencyType currencyType;
    private final @Nullable String targetNpc;

    /**
     * 기본 생성자 - 단순 재화 소비
     *
     * @param id           목표 ID
     * @param currencyType 재화 타입
     * @param amount       지불 금액
     */
    public PayCurrencyObjective(@NotNull String id, @NotNull CurrencyType currencyType, int amount) {
        this(id, currencyType, amount, null);
    }

    /**
     * NPC 지정 생성자
     *
     * @param id           목표 ID
     * @param currencyType 재화 타입
     * @param amount       지불 금액
     * @param targetNpc    대상 NPC (선택사항)
     */
    public PayCurrencyObjective(@NotNull String id, @NotNull CurrencyType currencyType,
                                int amount, @Nullable String targetNpc) {
        super(id, amount, createDescription(currencyType, amount, targetNpc));
        this.currencyType = Objects.requireNonNull(currencyType);
        this.targetNpc = targetNpc;
    }

    private static Component createDescription(CurrencyType type, int amount, @Nullable String npc) {
        Component currencyAmount = Component.translatable(type.getTranslationKey())
                .append(Component.text(" " + amount + type.getSymbol()));

        if (npc != null) {
            return Component.translatable("quest.objective.pay_currency.to_npc",
                            currencyAmount, Component.text(npc))
                    .color(NamedTextColor.YELLOW);
        } else {
            return Component.translatable("quest.objective.pay_currency",
                            currencyAmount)
                    .color(NamedTextColor.YELLOW);
        }
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.PAY_CURRENCY;
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        if (!(event instanceof CurrencyPaymentEvent paymentEvent)) {
            return false;
        }

        // 플레이어 확인
        if (!paymentEvent.getPlayer().equals(player)) {
            return false;
        }

        // 재화 타입 확인
        if (!paymentEvent.getCurrencyType().equals(currencyType.name())) {
            return false;
        }

        // NPC 확인 (지정된 경우)
        if (targetNpc != null && !targetNpc.equals(paymentEvent.getTargetNpc())) {
            return false;
        }

        return true;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        if (!canProgress(event, player)) return 0;

        CurrencyPaymentEvent paymentEvent = (CurrencyPaymentEvent) event;
        return paymentEvent.getAmount();
    }

    @Override
    protected @NotNull String serializeData() {
        return currencyType.name() + (targetNpc != null ? ":" + targetNpc : "");
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public @Nullable String getTargetNpc() {
        return targetNpc;
    }
}