package com.febrie.rpg.dto;

import com.febrie.rpg.job.JobType;
import org.jetbrains.annotations.Nullable;

/**
 * 플레이어 기본 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record PlayerDTO(
        String uuid,
        String name,
        long lastLogin,
        long totalPlaytime,
        @Nullable JobType job
) {
    /**
     * 기본 생성자 - 신규 플레이어용
     */
    public PlayerDTO(String uuid, String name) {
        this(uuid, name, System.currentTimeMillis(), 0L, null);
    }
}