package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.event.CurrencyPaymentEvent;
import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
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
        super(id, amount);
        this.currencyType = Objects.requireNonNull(currencyType);
        this.targetNpc = targetNpc;
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.PAY_CURRENCY;
    }

    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        // 통화 아이콘과 진행도 표시
        String status = currencyType.getIcon() + " " + getProgressString(progress);
        if (targetNpc != null) {
            status += " → " + targetNpc;
        }
        return status;
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

        // 재화 타입 확인 - ID로 비교
        if (!paymentEvent.getCurrencyType().equals(currencyType)) {
            return false;
        }

        // NPC 확인 (지정된 경우)
        return targetNpc == null || targetNpc.equals(paymentEvent.getTargetNpc());
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        if (!canProgress(event, player)) return 0;

        CurrencyPaymentEvent paymentEvent = (CurrencyPaymentEvent) event;
        return paymentEvent.getAmount();
    }

    @Override
    protected @NotNull String serializeData() {
        return currencyType.getId() + (targetNpc != null ? ":" + targetNpc : "");
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public @Nullable String getTargetNpc() {
        return targetNpc;
    }

    /**
     * 직렬화된 데이터에서 목표 생성
     */
    public static PayCurrencyObjective deserialize(@NotNull String id, int amount, @NotNull String data) {
        String[] parts = data.split(":");
        CurrencyType type = CurrencyType.getById(parts[0]);
        String npc = parts.length > 1 ? parts[1] : null;
        return new PayCurrencyObjective(id, type, amount, npc);
    }
}