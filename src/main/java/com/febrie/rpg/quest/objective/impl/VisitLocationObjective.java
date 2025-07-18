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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 지역 방문 퀘스트 목표
 * WorldGuard 영역 또는 특정 좌표 반경 내 도달
 * 
 * 사용법:
 * // WorldGuard 영역 방문
 * new VisitLocationObjective("visit_hub", "Hub")
 * 
 * // 좌표 기반 방문
 * Location spawnLocation = world.getSpawnLocation();
 * new VisitLocationObjective("visit_spawn", spawnLocation, 10.0, "스폰 지점")
 * 
 * 참고: LocationCheckTask에서 3초마다 자동으로 위치를 체크함
 *
 * @author Febrie
 */
public class VisitLocationObjective extends BaseObjective {

    // WorldGuard 영역 방문
    private final String regionName;
    
    // 좌표 기반 방문
    private final Location targetLocation;
    private final double radius;
    private final String locationName;
    
    // 방문 타입
    private final LocationType locationType;
    
    // 플레이어가 이미 영역에 있었는지 추적 (첫 진입만 카운트)
    private final Set<UUID> playersInLocation = new HashSet<>();

    /**
     * 방문 타입
     */
    public enum LocationType {
        WORLDGUARD_REGION,
        COORDINATE
    }

    /**
     * WorldGuard 영역 기반 생성자
     *
     * @param id         목표 ID
     * @param regionName WorldGuard 영역 이름
     */
    public VisitLocationObjective(@NotNull String id, @NotNull String regionName) {
        super(id, 1);
        this.regionName = Objects.requireNonNull(regionName);
        this.targetLocation = null;
        this.radius = 0;
        this.locationName = regionName;
        this.locationType = LocationType.WORLDGUARD_REGION;

        // WorldGuard 플러그인 확인
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            com.febrie.rpg.util.LogUtil.error("[WorldGuard] Plugin is not loaded when creating VisitLocationObjective for region: " + regionName);
            throw new IllegalStateException("WorldGuard plugin is not loaded");
        }
    }

    /**
     * 좌표 기반 생성자
     *
     * @param id             목표 ID
     * @param targetLocation 목표 위치
     * @param radius         도달 판정 반경
     * @param locationName   위치 이름
     */
    public VisitLocationObjective(@NotNull String id, @NotNull Location targetLocation, double radius, @NotNull String locationName) {
        super(id, 1);
        this.regionName = null;
        this.targetLocation = Objects.requireNonNull(targetLocation);
        this.radius = radius;
        this.locationName = Objects.requireNonNull(locationName);
        this.locationType = LocationType.COORDINATE;

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
        return locationName + " " + getProgressString(progress);
    }

    /**
     * 플레이어가 현재 목표 위치에 있는지 확인
     * LocationCheckTask에서 호출됨
     */
    public boolean checkLocation(@NotNull Player player, @NotNull Location location) {
        UUID playerId = player.getUniqueId();
        boolean inTargetLocation = false;

        // 타입에 따른 처리
        if (locationType == LocationType.WORLDGUARD_REGION) {
            // WorldGuard 영역 확인
            Set<String> currentRegions = getRegionsAt(location);
            inTargetLocation = currentRegions.stream()
                    .anyMatch(region -> region.equalsIgnoreCase(regionName));
        } else {
            // 좌표 기반 확인
            if (!location.getWorld().equals(targetLocation.getWorld())) {
                return false;
            }
            inTargetLocation = location.distance(targetLocation) <= radius;
        }

        // 이전에 위치에 있었는지 확인
        boolean wasInLocation = playersInLocation.contains(playerId);
        
        if (inTargetLocation && !wasInLocation) {
            // 처음으로 위치에 진입
            playersInLocation.add(playerId);
            return true;
        } else if (!inTargetLocation && wasInLocation) {
            // 위치를 떠남
            playersInLocation.remove(playerId);
        }
        
        return false;
    }
    
    @Override
    public boolean canProgress(@NotNull Event event, @NotNull Player player) {
        // LocationCheckTask에서만 호출되므로 직접 체크하지 않음
        return false;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        // LocationCheckTask에서만 호출되므로 0 반환
        return 0;
    }

    /**
     * 위치의 WorldGuard 영역 목록 가져오기
     */
    private Set<String> getRegionsAt(@NotNull Location location) {
        Set<String> regionSet = new HashSet<>();

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(location.getWorld()));

        if (regionManager != null) {
            var regions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
            for (ProtectedRegion region : regions) {
                regionSet.add(region.getId());
            }
        }

        return regionSet;
    }

    @Override
    protected @NotNull String serializeData() {
        if (locationType == LocationType.WORLDGUARD_REGION) {
            return "REGION:" + regionName;
        } else {
            return String.format("COORD:%s;%f;%f;%f;%f;%s", 
                targetLocation.getWorld().getName(), 
                targetLocation.getX(), 
                targetLocation.getY(), 
                targetLocation.getZ(), 
                radius, 
                locationName);
        }
    }

    public String getRegionName() {
        return regionName;
    }

    public Location getTargetLocation() {
        return targetLocation != null ? targetLocation.clone() : null;
    }

    public double getRadius() {
        return radius;
    }

    public String getLocationName() {
        return locationName;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    /**
     * 플레이어 캐시 정리 (메모리 관리용)
     */
    public void clearPlayerCache(@NotNull UUID playerId) {
        playersInLocation.remove(playerId);
    }
    
    /**
     * 빌더 클래스
     */
    public static class Builder {
        private String id;
        private String region;
        private Location location;
        private double radius = 10.0; // 기본값
        private String locationName;
        
        public Builder id(@NotNull String id) {
            this.id = id;
            return this;
        }
        
        public Builder region(@NotNull String regionName) {
            this.region = regionName;
            return this;
        }
        
        public Builder location(@NotNull Location location) {
            this.location = location;
            return this;
        }
        
        public Builder radius(double radius) {
            if (radius <= 0) {
                throw new IllegalArgumentException("Radius must be positive");
            }
            this.radius = radius;
            return this;
        }
        
        public Builder locationName(@NotNull String name) {
            this.locationName = name;
            return this;
        }
        
        public VisitLocationObjective build() {
            if (id == null) {
                throw new IllegalStateException("ID is required");
            }
            
            // WorldGuard 영역 기반
            if (region != null) {
                if (location != null) {
                    throw new IllegalStateException("Cannot specify both region and location");
                }
                return new VisitLocationObjective(id, region);
            }
            
            // 좌표 기반
            if (location != null) {
                if (locationName == null) {
                    throw new IllegalStateException("Location name is required for coordinate-based objectives");
                }
                return new VisitLocationObjective(id, location, radius, locationName);
            }
            
            throw new IllegalStateException("Either region or location must be specified");
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public @Nullable String validate() {
        if (locationType == LocationType.WORLDGUARD_REGION) {
            if (regionName == null || regionName.trim().isEmpty()) {
                return "VisitLocationObjective '" + id + "': WorldGuard 영역 이름이 설정되지 않았습니다.";
            }
        } else {
            if (targetLocation == null) {
                return "VisitLocationObjective '" + id + "': 목표 위치가 설정되지 않았습니다.";
            }
            if (locationName == null || locationName.trim().isEmpty()) {
                return "VisitLocationObjective '" + id + "': 위치 이름이 설정되지 않았습니다.";
            }
        }
        
        return null; // 유효함
    }
}