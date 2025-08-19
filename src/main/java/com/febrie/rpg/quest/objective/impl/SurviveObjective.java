package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.kyori.adventure.text.Component;
/**
 * 생존 퀘스트 목표
 * 특정 시간 동안 또는 특정 조건에서 생존
 *
 * @author Febrie
 */
public class SurviveObjective extends BaseObjective {

    public enum SurvivalType {
        TIME("Time"),
        LOCATION("Location"),
        WAVE("Wave"),
        NO_DEATH("No Death");

        private final String displayName;

        SurvivalType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private final SurvivalType survivalType;
    private final int durationSeconds;
    private final @Nullable Location survivalLocation;
    private final double locationRadius;
    private final Map<UUID, Long> startTimes = new HashMap<>();

    /**
     * 시간 생존 생성자
     *
     * @param id              목표 ID
     * @param durationSeconds 생존 시간 (초)
     */
    public SurviveObjective(@NotNull String id, int durationSeconds) {
        this(id, SurvivalType.TIME, durationSeconds, null, 0);
    }

    /**
     * 지역 생존 생성자
     *
     * @param id              목표 ID
     * @param durationSeconds 생존 시간 (초)
     * @param location        생존 지역
     * @param radius          지역 반경
     */
    public SurviveObjective(@NotNull String id, int durationSeconds,
                            @NotNull Location location, double radius) {
        this(id, SurvivalType.LOCATION, durationSeconds, location, radius);
    }

    /**
     * 전체 옵션 생성자
     *
     * @param id               목표 ID
     * @param survivalType     생존 타입
     * @param durationSeconds  생존 시간 (초)
     * @param survivalLocation 생존 위치 (LOCATION 타입용)
     * @param locationRadius   위치 반경 (LOCATION 타입용)
     */
    protected SurviveObjective(@NotNull String id, @NotNull SurvivalType survivalType,
                               int durationSeconds, @Nullable Location survivalLocation,
                               double locationRadius) {
        super(id, survivalType == SurvivalType.WAVE ? durationSeconds : 1);
        this.survivalType = survivalType;
        this.durationSeconds = durationSeconds;
        this.survivalLocation = survivalLocation;
        this.locationRadius = locationRadius;
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.SURVIVE;
    }

    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        return switch (survivalType) {
            case TIME, NO_DEATH -> survivalType.getDisplayName() + " " + durationSeconds + "s " +
                    (progress.isCompleted() ? "✓" : "");
            case LOCATION -> (survivalLocation != null ?
                    survivalLocation.getWorld().getName() : "Unknown") +
                    " " + durationSeconds + "s " +
                    (progress.isCompleted() ? "✓" : "");
            case WAVE -> survivalType.getDisplayName() + " " + getProgressString(progress);
        };
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        UUID playerId = player.getUniqueId();

        switch (survivalType) {
            case TIME, NO_DEATH -> {
                // 시작 시간 기록
                if (!startTimes.containsKey(playerId)) {
                    startTimes.put(playerId, System.currentTimeMillis());
                }

                // 사망 시 리셋 (NO_DEATH 타입)
                if (survivalType == SurvivalType.NO_DEATH && event instanceof PlayerDeathEvent deathEvent) {
                    if (deathEvent.getEntity().equals(player)) {
                        startTimes.remove(playerId);
                        return false;
                    }
                }

                // 시간 체크
                long elapsed = System.currentTimeMillis() - startTimes.get(playerId);
                return elapsed >= durationSeconds * 1000L;
            }

            case LOCATION -> {
                if (survivalLocation == null) return false;

                if (event instanceof PlayerMoveEvent moveEvent) {
                    Location to = moveEvent.getTo();
                    if (!to.getWorld().equals(survivalLocation.getWorld())) {
                        startTimes.remove(playerId);
                        return false;
                    }

                    if (to.distance(survivalLocation) > locationRadius) {
                        startTimes.remove(playerId);
                        return false;
                    }

                    if (!startTimes.containsKey(playerId)) {
                        startTimes.put(playerId, System.currentTimeMillis());
                    }

                    long elapsed = System.currentTimeMillis() - startTimes.get(playerId);
                    return elapsed >= durationSeconds * 1000L;
                }
            }

            case WAVE -> {
                // 웨이브 생존은 별도 이벤트 처리 필요
                return false;
            }
        }

        return false;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        return canProgress(event, player) ? 1 : 0;
    }

    @Override
    protected @NotNull String serializeData() {
        return survivalType.name() + ";" + durationSeconds + ";" +
                (survivalLocation != null ?
                        survivalLocation.getWorld().getName() + "," +
                                survivalLocation.getX() + "," +
                                survivalLocation.getY() + "," +
                                survivalLocation.getZ() : "") + ";" +
                locationRadius;
    }

    public SurvivalType getSurvivalType() {
        return survivalType;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public @Nullable Location getSurvivalLocation() {
        return survivalLocation != null ? survivalLocation.clone() : null;
    }

    public double getLocationRadius() {
        return locationRadius;
    }
}