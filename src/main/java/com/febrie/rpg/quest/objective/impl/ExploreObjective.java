package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 탐험 퀘스트 목표
 * 여러 지역을 방문하여 탐험
 *
 * @author Febrie
 */
public class ExploreObjective extends BaseObjective {

    private final List<ExploreLocation> locations;
    private final Set<String> visitedLocations = new HashSet<>();

    /**
     * 탐험 위치 정보
     */
    public static class ExploreLocation {
        private final String name;
        private final Location center;
        private final double radius;

        public ExploreLocation(@NotNull String name, @NotNull Location center, double radius) {
            this.name = Objects.requireNonNull(name);
            this.center = Objects.requireNonNull(center);
            this.radius = radius;

            if (radius <= 0) {
                throw new IllegalArgumentException("Radius must be positive");
            }
        }

        public boolean isInRange(@NotNull Location location) {
            if (!location.getWorld().equals(center.getWorld())) {
                return false;
            }
            return location.distance(center) <= radius;
        }

        public String getName() {
            return name;
        }

        public Location getCenter() {
            return center.clone();
        }

        public double getRadius() {
            return radius;
        }
    }

    /**
     * 기본 생성자
     *
     * @param id        목표 ID
     * @param locations 탐험할 위치 목록
     */
    public ExploreObjective(@NotNull String id, @NotNull List<ExploreLocation> locations) {
        super(id, locations.size(), createDescription(locations));
        this.locations = new ArrayList<>(locations);

        if (locations.isEmpty()) {
            throw new IllegalArgumentException("At least one location required");
        }
    }

    private static Component createDescription(List<ExploreLocation> locations) {
        if (locations.size() == 1) {
            return Component.translatable("quest.objective.explore.single",
                            Component.text(locations.get(0).getName()))
                    .color(NamedTextColor.LIGHT_PURPLE);
        } else {
            return Component.translatable("quest.objective.explore.multiple",
                            Component.text(locations.size()))
                    .color(NamedTextColor.LIGHT_PURPLE);
        }
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.EXPLORE;
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

        // 아직 방문하지 않은 위치 중 현재 위치가 포함되는지 확인
        Location playerLoc = moveEvent.getTo();

        for (ExploreLocation location : locations) {
            if (!visitedLocations.contains(location.getName()) &&
                    location.isInRange(playerLoc)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        if (!canProgress(event, player)) return 0;

        PlayerMoveEvent moveEvent = (PlayerMoveEvent) event;
        Location playerLoc = moveEvent.getTo();

        int newVisits = 0;
        for (ExploreLocation location : locations) {
            if (!visitedLocations.contains(location.getName()) &&
                    location.isInRange(playerLoc)) {
                visitedLocations.add(location.getName());
                newVisits++;
            }
        }

        return newVisits;
    }

    @Override
    public int getCurrentProgress(@NotNull ObjectiveProgress progress) {
        // 방문한 위치 수 반환
        return visitedLocations.size();
    }

    @Override
    public @NotNull Component getDescription() {
        Component base = super.getDescription();

        // 각 위치별 방문 상태 추가
        List<Component> details = new ArrayList<>();
        for (ExploreLocation location : locations) {
            boolean visited = visitedLocations.contains(location.getName());
            Component status = Component.text(location.getName())
                    .color(visited ? NamedTextColor.GREEN : NamedTextColor.GRAY)
                    .append(Component.text(visited ? " ✓" : " ✗"));
            details.add(status);
        }

        return base.append(Component.newline())
                .append(Component.join(Component.newline(), details));
    }

    @Override
    protected @NotNull String serializeData() {
        StringBuilder data = new StringBuilder();
        for (int i = 0; i < locations.size(); i++) {
            if (i > 0) data.append("|");
            ExploreLocation loc = locations.get(i);
            data.append(loc.getName()).append(":")
                    .append(loc.getCenter().getWorld().getName()).append(":")
                    .append(loc.getCenter().getX()).append(":")
                    .append(loc.getCenter().getY()).append(":")
                    .append(loc.getCenter().getZ()).append(":")
                    .append(loc.getRadius());
        }
        return data.toString();
    }

    public List<ExploreLocation> getLocations() {
        return new ArrayList<>(locations);
    }

    public Set<String> getVisitedLocations() {
        return new HashSet<>(visitedLocations);
    }
}