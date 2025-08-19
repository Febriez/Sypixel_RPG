package com.febrie.rpg.economy;

import com.febrie.rpg.dto.player.WalletDTO;
import com.febrie.rpg.util.LogUtil;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import net.kyori.adventure.text.Component;
/**
 * 플레이어의 재화 관리 시스템
 * 다양한 통화를 통합 관리
 * 모든 데이터는 Firebase를 통해서만 저장됨
 *
 * @author Febrie, CoffeeTory
 */
public class Wallet {

    // 각 통화별 잔액
    private final Map<CurrencyType, Long> balances = new EnumMap<>(CurrencyType.class);

    public Wallet() {
        // 모든 통화를 0으로 초기화
        for (CurrencyType type : CurrencyType.values()) {
            balances.put(type, 0L);
        }
    }

    /**
     * DTO로 변환
     */
    @NotNull
    public WalletDTO toDTO() {
        // 재화 정보를 Map<String, Long>으로 변환
        Map<String, Long> currencies = new HashMap<>();
        for (Map.Entry<CurrencyType, Long> entry : balances.entrySet()) {
            currencies.put(entry.getKey().getId(), entry.getValue());
        }

        // WalletDTO는 record이므로 생성자로 생성
        return new WalletDTO(currencies, System.currentTimeMillis());
    }

    /**
     * DTO에서 데이터 적용
     */
    public void applyFromDTO(@NotNull WalletDTO dto) {
        // record의 accessor 메소드 사용
        Map<String, Long> currencies = dto.currencies();

        for (Map.Entry<String, Long> entry : currencies.entrySet()) {
            try {
                CurrencyType type = CurrencyType.getById(entry.getKey());
                setBalance(type, entry.getValue());
            } catch (IllegalArgumentException e) {
                LogUtil.warning("알 수 없는 통화 타입: " + entry.getKey());
            }
        }
    }

    /**
     * 특정 통화의 잔액 조회
     */
    public long getBalance(@NotNull CurrencyType type) {
        return balances.getOrDefault(type, 0L);
    }

    /**
     * 특정 통화의 잔액 설정
     */
    public void setBalance(@NotNull CurrencyType type, long amount) {
        if (amount < 0) {
            LogUtil.warning("음수 잔액 설정 시도: " + type + " = " + amount);
            amount = 0;
        }

        balances.put(type, Math.min(amount, type.getMaxAmount()));
    }

    /**
     * 통화 추가
     */
    public boolean add(@NotNull CurrencyType type, long amount) {
        if (amount <= 0) {
            return false;
        }

        long current = getBalance(type);
        long newAmount = Math.min(current + amount, type.getMaxAmount());

        if (newAmount == current) {
            return false; // 최대치 도달
        }

        balances.put(type, newAmount);
        return true;
    }

    /**
     * 통화 차감 (충분한 잔액이 있을 경우에만)
     */
    public boolean subtract(@NotNull CurrencyType type, long amount) {
        if (amount <= 0) {
            return false;
        }

        long current = getBalance(type);
        if (current < amount) {
            return false; // 잔액 부족
        }

        balances.put(type, current - amount);
        return true;
    }

    /**
     * 통화 사용 가능 여부 확인
     */
    public boolean has(@NotNull CurrencyType type, long amount) {
        return amount > 0 && getBalance(type) >= amount;
    }

    /**
     * 모든 통화 정보 가져오기
     */
    @NotNull
    public Map<CurrencyType, Long> getAllBalances() {
        return new EnumMap<>(balances);
    }

    /**
     * 총 재산 가치 계산 (나중에 환율 시스템 추가 가능)
     */
    public long getTotalValue() {
        // 기본적으로 골드 기준으로 계산
        long total = getBalance(CurrencyType.GOLD);

        // 다른 통화들의 가치를 골드로 환산 (임시 환율)
        total += getBalance(CurrencyType.DIAMOND) * 100; // 1 다이아몬드 = 100 골드
        total += getBalance(CurrencyType.EMERALD) * 50; // 1 에메랄드 = 50 골드
        total += getBalance(CurrencyType.GHAST_TEAR) * 1000; // 1 별가루 = 1000 골드
        total += getBalance(CurrencyType.NETHER_STAR) * 10000; // 1 별 = 10000 골드

        return total;
    }
}
