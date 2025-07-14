package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
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
    private final String npcName;

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
        this(id, targetLocation, radius, locationName, "모험가 길드");
    }

    /**
     * NPC 지정 생성자
     *
     * @param id             목표 ID
     * @param targetLocation 목표 위치
     * @param radius         도달 판정 반경
     * @param locationName   위치 이름
     * @param npcName        퀘스트 제공 NPC
     */
    public VisitLocationObjective(@NotNull String id, @NotNull Location targetLocation,
                                  double radius, @NotNull String locationName, @NotNull String npcName) {
        super(id, 1, "quest.objective.visit_location",
                "location", locationName);
        this.targetLocation = Objects.requireNonNull(targetLocation);
        this.radius = radius;
        this.locationName = Objects.requireNonNull(locationName);
        this.npcName = Objects.requireNonNull(npcName);

        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive: " + radius);
        }
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.VISIT_LOCATION;
    }

    @Override
    public @NotNull String getDescription(boolean isKorean) {
        return isKorean ?
                "퀘스트를 준 사람: " + npcName + "\n\n" +
                        "모험가여, " + locationName + " 지역을 방문해주게. " +
                        "그곳에서 중요한 단서를 찾을 수 있을 걸세. " +
                        "지도에 표시해둔 위치로 가서 주변을 잘 살펴보게나." :

                "Quest Giver: " + npcName + "\n\n" +
                        "Adventurer, please visit the " + locationName + " area. " +
                        "You'll find important clues there. " +
                        "Go to the marked location on your map and look around carefully.";
    }

    @Override
    public @NotNull String getGiverName(boolean isKorean) {
        return npcName;
    }

    @Override
    public @NotNull String getLocationInfo(boolean isKorean) {
        if (targetLocation.getWorld() == null) {
            return isKorean ? "알 수 없는 위치" : "Unknown Location";
        }

        return String.format("%s (%d, %d, %d)",
                locationName,
                targetLocation.getBlockX(),
                targetLocation.getBlockY(),
                targetLocation.getBlockZ()
        );
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
        return String.format("%s;%f;%f;%f;%f;%s;%s",
                targetLocation.getWorld() != null ? targetLocation.getWorld().getName() : "world",
                targetLocation.getX(),
                targetLocation.getY(),
                targetLocation.getZ(),
                radius,
                locationName,
                npcName
        );
    }

    /**
     * 목표 위치 반환
     */
    public @NotNull Location getTargetLocation() {
        return targetLocation.clone();
    }

    /**
     * 도달 반경 반환
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