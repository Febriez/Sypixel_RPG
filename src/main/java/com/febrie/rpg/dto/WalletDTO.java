package com.febrie.rpg.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어 재화 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record WalletDTO(
        Map<String, Long> currencies,
        long lastUpdated
) {
    /**
     * 기본 생성자 - 신규 플레이어용
     */
    public WalletDTO() {
        this(new HashMap<>(), System.currentTimeMillis());
    }

    /**
     * 방어적 복사를 위한 생성자
     */
    public WalletDTO(Map<String, Long> currencies, long lastUpdated) {
        this.currencies = new HashMap<>(currencies);
        this.lastUpdated = lastUpdated;
    }

    /**
     * 재화 맵의 불변 뷰 반환
     */
    @Override
    public Map<String, Long> currencies() {
        return new HashMap<>(currencies);
    }
}