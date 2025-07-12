package com.febrie.rpg.dto;

import com.febrie.rpg.job.JobType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 플레이어 기본 정보 DTO
 * Firebase 저장용
 *
 * @author Febrie, CoffeeTory
 */
public class PlayerDTO {

    private String uuid;
    private String name;
    private JobType job;
    private long lastLogin = System.currentTimeMillis();
    private long totalPlaytime = 0; // 밀리초
    private long firstJoin = System.currentTimeMillis();

    public PlayerDTO() {
        // 기본 생성자 (Firebase 역직렬화용)
    }

    public PlayerDTO(@NotNull String uuid, @NotNull String name) {
        this.uuid = uuid;
        this.name = name;
    }

    // Getters and Setters
    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @Nullable
    public JobType getJob() {
        return job;
    }

    public void setJob(@Nullable JobType job) {
        this.job = job;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public long getTotalPlaytime() {
        return totalPlaytime;
    }

    public void setTotalPlaytime(long totalPlaytime) {
        this.totalPlaytime = Math.max(0, totalPlaytime);
    }

    public long getFirstJoin() {
        return firstJoin;
    }

    public void setFirstJoin(long firstJoin) {
        this.firstJoin = firstJoin;
    }

    /**
     * 플레이 시간 추가
     */
    public void addPlaytime(long milliseconds) {
        this.totalPlaytime += Math.max(0, milliseconds);
    }

    /**
     * 직업 보유 여부
     */
    public boolean hasJob() {
        return job != null;
    }
}