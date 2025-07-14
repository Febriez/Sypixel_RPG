package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
    public VisitLocationObjective(@NotNull String id, @NotNull Location targetLocation,
                                  double radius, @NotNull String locationName) {
        super(id, 1, createDescription(locationName));
        this.targetLocation = Objects.requireNonNull(targetLocation);
        this.radius = radius;
        this.locationName = Objects.requireNonNull(locationName);

        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive: " + radius);
        }
    }

    private static Component createDescription(String locationName) {
        return Component.translatable("quest.objective.visit_location",
                        Component.text(locationName))
                .color(NamedTextColor.YELLOW);
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.VISIT_LOCATION;
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

        // 월드 확인
        if (!moveEvent.getTo().getWorld().equals(targetLocation.getWorld())) {
            return false;
        }

        // 거리 계산
        double distance = moveEvent.getTo().distance(targetLocation);
        return distance <= radius;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        return canProgress(event, player) ? 1 : 0;
    }

    @Override
    protected @NotNull String serializeData() {
        return String.format("%s:%f:%f:%f:%f:%s",
                targetLocation.getWorld().getName(),
                targetLocation.getX(),
                targetLocation.getY(),
                targetLocation.getZ(),
                radius,
                locationName);
    }

    public Location getTargetLocation() {
        return targetLocation.clone();
    }

    public double getRadius() {
        return radius;
    }

    public String getLocationName() {
        return locationName;
    }
}