package com.febrie.rpg.dto;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어 재화 정보 DTO
 * Firebase 저장용
 *
 * @author Febrie, CoffeeTory
 */
public class WalletDTO {

    private Map<String, Long> currencies = new HashMap<>();
    private long lastUpdated = System.currentTimeMillis();

    public WalletDTO() {
        // 기본 생성자
    }

    /**
     * 특정 통화 설정
     */
    public void setCurrency(@NotNull String currencyId, long amount) {
        currencies.put(currencyId, amount);
        lastUpdated = System.currentTimeMillis();
    }

    /**
     * 특정 통화 조회
     */
    public long getCurrency(@NotNull String currencyId) {
        return currencies.getOrDefault(currencyId, 0L);
    }

    /**
     * 모든 통화 정보 가져오기
     */
    @NotNull
    public Map<String, Long> getCurrencies() {
        return new HashMap<>(currencies);
    }

    /**
     * 모든 통화 정보 설정
     */
    public void setCurrencies(@NotNull Map<String, Long> currencies) {
        this.currencies = new HashMap<>(currencies);
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * 마지막 업데이트 시간
     */
    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}