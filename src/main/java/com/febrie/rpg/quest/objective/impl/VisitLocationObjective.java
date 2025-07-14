package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 지역 방문 퀘스트 목표
 * 특정 좌표 반경 내 도달
 *
 * @author Febrie
 */
public class VisitLocationObjective extends BaseObjective {

    private final Location targetLocation;
    private final double radius;
    private final String locationName;

    /**
     * 기본 생성자
     *
     * @param id             목표 ID
     * @param targetLocation 목표 위치
     * @param radius         도달 판정 반경
     * @param locationName   위치 이름
     */
    public VisitLocationObjective(@NotNull String id, @NotNull Location targetLocation, double radius, @NotNull String locationName) {
        super(id, 1);
        this.targetLocation = Objects.requireNonNull(targetLocation);
        this.radius = radius;
        this.locationName = Objects.requireNonNull(locationName);

        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive: " + radius);
        }
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.VISIT_LOCATION;
    }

    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        if (progress.isCompleted()) {
            return locationName + " ✓";
        }
        return locationName + " " + getProgressString(progress);
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        if (!(event instanceof PlayerMoveEvent moveEvent)) {
            return false;
        }

        // 플레이어 확인
        if (!moveEvent.getPlayer().equals(player)) {
            return false;
        }

        // 이미 완료했으면 무시
        Location to = moveEvent.getTo();
        if (to == null) return false;

        // 같은 월드인지 확인
        if (!to.getWorld().equals(targetLocation.getWorld())) {
            return false;
        }

        // 거리 확인
        return to.distance(targetLocation) <= radius;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        return canProgress(event, player) ? 1 : 0;
    }

    @Override
    protected @NotNull String serializeData() {
        return String.format("%s;%f;%f;%f;%f;%s", targetLocation.getWorld().getName(), targetLocation.getX(), targetLocation.getY(), targetLocation.getZ(), radius, locationName);
    }

    /**
     * 대상 위치 반환
     */
    public @NotNull Location getTargetLocation() {
        return targetLocation.clone();
    }

    /**
     * 반경 반환
     */
    public double getRadius() {
        return radius;
    }

    /**
     * 위치 이름 반환
     */
    public @NotNull String getLocationName() {
        return locationName;
    }
}