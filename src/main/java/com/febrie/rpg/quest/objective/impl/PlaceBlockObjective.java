package com.febrie.rpg.quest.objective.impl;

import com.febrie.rpg.quest.objective.BaseObjective;
import com.febrie.rpg.quest.objective.ObjectiveType;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 블럭 설치 퀘스트 목표
 * 특정 블럭을 특정 위치/영역에 설치
 *
 * @author Febrie
 */
public class PlaceBlockObjective extends BaseObjective {

    public enum PlacementType {
        ANYWHERE,      // 아무 곳에나
        SPECIFIC_LOCATION,  // 특정 위치
        WITHIN_RADIUS,      // 특정 위치 반경 내
        WORLDGUARD_REGION   // WorldGuard 영역 내
    }

    private final Material blockType;
    private final PlacementType placementType;
    private final @Nullable Location targetLocation;
    private final double radius;
    private final @Nullable String regionName;

    /**
     * 기본 생성자 - 아무 곳에나 설치
     *
     * @param id        목표 ID
     * @param blockType 설치할 블럭 타입
     * @param amount    설치 수량
     */
    public PlaceBlockObjective(@NotNull String id, @NotNull Material blockType, int amount) {
        super(id, amount);
        this.blockType = Objects.requireNonNull(blockType);
        this.placementType = PlacementType.ANYWHERE;
        this.targetLocation = null;
        this.radius = 0;
        this.regionName = null;

        validateBlock();
    }

    /**
     * 특정 위치 반경 내 설치 생성자
     *
     * @param id             목표 ID
     * @param blockType      설치할 블럭 타입
     * @param amount         설치 수량
     * @param targetLocation 중심 위치
     * @param radius         반경
     */
    public PlaceBlockObjective(@NotNull String id, @NotNull Material blockType, int amount,
                               @NotNull Location targetLocation, double radius) {
        super(id, amount);
        this.blockType = Objects.requireNonNull(blockType);
        this.placementType = radius <= 0 ? PlacementType.SPECIFIC_LOCATION : PlacementType.WITHIN_RADIUS;
        this.targetLocation = Objects.requireNonNull(targetLocation);
        this.radius = Math.max(0, radius);
        this.regionName = null;

        validateBlock();
    }

    /**
     * WorldGuard 영역 내 설치 생성자
     *
     * @param id         목표 ID
     * @param blockType  설치할 블럭 타입
     * @param amount     설치 수량
     * @param regionName WorldGuard 영역 이름
     */
    public PlaceBlockObjective(@NotNull String id, @NotNull Material blockType, int amount,
                               @NotNull String regionName) {
        super(id, amount);
        this.blockType = Objects.requireNonNull(blockType);
        this.placementType = PlacementType.WORLDGUARD_REGION;
        this.targetLocation = null;
        this.radius = 0;
        this.regionName = Objects.requireNonNull(regionName);

        validateBlock();

        // WorldGuard 플러그인 확인
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            throw new IllegalStateException("WorldGuard plugin is not loaded");
        }
    }

    private void validateBlock() {
        if (!blockType.isBlock()) {
            throw new IllegalArgumentException("Material must be a block: " + blockType);
        }
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.PLACE_BLOCK;
    }

    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        String status = blockType.name() + " " + getProgressString(progress);

        switch (placementType) {
            case SPECIFIC_LOCATION -> {
                if (targetLocation != null) {
                    status += " @(" + targetLocation.getBlockX() + "," +
                            targetLocation.getBlockY() + "," +
                            targetLocation.getBlockZ() + ")";
                }
            }
            case WITHIN_RADIUS -> {
                if (targetLocation != null) {
                    status += " @(" + targetLocation.getBlockX() + "," +
                            targetLocation.getBlockY() + "," +
                            targetLocation.getBlockZ() + " r:" + (int) radius + ")";
                }
            }
            case WORLDGUARD_REGION -> {
                if (regionName != null) {
                    status += " @" + regionName;
                }
            }
        }

        return status;
    }

    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        if (!(event instanceof BlockPlaceEvent placeEvent)) {
            return false;
        }

        // 플레이어 확인
        if (!placeEvent.getPlayer().equals(player)) {
            return false;
        }

        // 블럭 타입 확인
        if (placeEvent.getBlock().getType() != blockType) {
            return false;
        }

        // 위치 제한 확인
        Location blockLocation = placeEvent.getBlock().getLocation();

        return switch (placementType) {
            case ANYWHERE -> true;

            case SPECIFIC_LOCATION -> targetLocation != null &&
                    blockLocation.getBlockX() == targetLocation.getBlockX() &&
                    blockLocation.getBlockY() == targetLocation.getBlockY() &&
                    blockLocation.getBlockZ() == targetLocation.getBlockZ();

            case WITHIN_RADIUS -> targetLocation != null &&
                    blockLocation.getWorld().equals(targetLocation.getWorld()) &&
                    blockLocation.distance(targetLocation) <= radius;

            case WORLDGUARD_REGION -> isInRegion(blockLocation, regionName);
        };
    }

    /**
     * WorldGuard 영역 내인지 확인
     */
    private boolean isInRegion(@NotNull Location location, @Nullable String regionName) {
        if (regionName == null) return false;

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(location.getWorld()));

        if (regionManager != null) {
            ProtectedRegion region = regionManager.getRegion(regionName);
            if (region != null) {
                return region.contains(BukkitAdapter.asBlockVector(location));
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
        StringBuilder sb = new StringBuilder();
        sb.append(blockType.name()).append(";");
        sb.append(placementType.name()).append(";");

        if (targetLocation != null) {
            sb.append(targetLocation.getWorld().getName()).append(",");
            sb.append(targetLocation.getX()).append(",");
            sb.append(targetLocation.getY()).append(",");
            sb.append(targetLocation.getZ());
        }
        sb.append(";");
        sb.append(radius).append(";");
        sb.append(regionName != null ? regionName : "");

        return sb.toString();
    }

    // Getters
    public @NotNull Material getBlockType() {
        return blockType;
    }

    public @NotNull PlacementType getPlacementType() {
        return placementType;
    }

    public @Nullable Location getTargetLocation() {
        return targetLocation != null ? targetLocation.clone() : null;
    }

    public double getRadius() {
        return radius;
    }

    public @Nullable String getRegionName() {
        return regionName;
    }
}