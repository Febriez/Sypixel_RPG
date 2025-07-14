package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

/**
 * 생존 퀘스트 목표
 * 특정 시간 동안 또는 특정 조건에서 생존
 *
 * @author Febrie
 */
public class SurviveObjective extends BaseObjective {

    public enum SurvivalType {
        TIME("quest.objective.survive.type.time"),
        LOCATION("quest.objective.survive.type.location"),
        WAVE("quest.objective.survive.type.wave"),
        NO_DEATH("quest.objective.survive.type.no_death");

        private final String translationKey;

        SurvivalType(String translationKey) {
            this.translationKey = translationKey;
        }

        public String getTranslationKey() {
            return translationKey;
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
     * @param survivalLocation 생존 지역 (LOCATION 타입일 때만)
     * @param locationRadius   지역 반경
     */
    public SurviveObjective(@NotNull String id, @NotNull SurvivalType survivalType,
                            int durationSeconds, @Nullable Location survivalLocation,
                            double locationRadius) {
        super(id, durationSeconds, createDescription(survivalType, durationSeconds, survivalLocation));
        this.survivalType = survivalType;
        this.durationSeconds = durationSeconds;
        this.survivalLocation = survivalLocation;
        this.locationRadius = locationRadius;

        if (survivalType == SurvivalType.LOCATION && survivalLocation == null) {
            throw new IllegalArgumentException("Location required for LOCATION survival type");
        }
    }

    private static Component createDescription(SurvivalType type, int seconds,
                                               @Nullable Location location) {
        Component timeComponent = Component.text(seconds);

        return (switch (type) {
            case TIME -> Component.translatable("quest.objective.survive.time", timeComponent);
            case LOCATION -> location != null ?
                    Component.translatable("quest.objective.survive.location", timeComponent) :
                    Component.translatable("quest.objective.survive.time", timeComponent);
            case WAVE -> Component.translatable("quest.objective.survive.wave", timeComponent);
            case NO_DEATH -> Component.translatable("quest.objective.survive.no_death", timeComponent);
        }).color(NamedTextColor.GOLD);
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.SURVIVE;
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        // 죽음 이벤트 처리
        if (event instanceof PlayerDeathEvent deathEvent) {
            if (deathEvent.getEntity().equals(player)) {
                startTimes.remove(player.getUniqueId()); // 리셋
                return false;
            }
        }

        // 이동 이벤트로 시간 체크
        if (event instanceof PlayerMoveEvent moveEvent) {
            if (!moveEvent.getPlayer().equals(player)) {
                return false;
            }

            UUID playerId = player.getUniqueId();

            // 시작 시간 기록
            if (!startTimes.containsKey(playerId)) {
                startTimes.put(playerId, System.currentTimeMillis());
            }

            // 지역 생존인 경우 위치 확인
            if (survivalType == SurvivalType.LOCATION && survivalLocation != null) {
                Location playerLoc = moveEvent.getTo();
                if (!playerLoc.getWorld().equals(survivalLocation.getWorld()) ||
                        playerLoc.distance(survivalLocation) > locationRadius) {
                    startTimes.remove(playerId); // 구역 이탈시 리셋
                    return false;
                }
            }

            // 시간 확인
            long elapsedSeconds = (System.currentTimeMillis() - startTimes.get(playerId)) / 1000;
            return elapsedSeconds >= durationSeconds;
        }

        return false;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        if (!canProgress(event, player)) return 0;

        // 목표 달성
        startTimes.remove(player.getUniqueId());
        return durationSeconds;
    }

    @Override
    public int getCurrentProgress(@NotNull ObjectiveProgress progress) {
        // 현재 생존 시간 반환
        Long startTime = startTimes.get(progress.getPlayerId());
        if (startTime == null) return 0;

        long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
        return (int) Math.min(elapsedSeconds, durationSeconds);
    }

    @Override
    protected @NotNull String serializeData() {
        StringBuilder data = new StringBuilder();
        data.append(survivalType.name()).append(":").append(durationSeconds);

        if (survivalLocation != null) {
            data.append(":").append(survivalLocation.getWorld().getName())
                    .append(":").append(survivalLocation.getX())
                    .append(":").append(survivalLocation.getY())
                    .append(":").append(survivalLocation.getZ())
                    .append(":").append(locationRadius);
        }

        return data.toString();
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