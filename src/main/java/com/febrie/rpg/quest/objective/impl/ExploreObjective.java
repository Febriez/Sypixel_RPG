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
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 탐험 퀘스트 목표
 * WorldGuard 영역을 방문하여 탐험
 * 
 * 사용법:
 * // 단일 영역 방문
 * new ExploreObjective("visit_hub", "Hub")
 * 
 * // 여러 영역 방문하려면 각각 ExploreObjective 추가
 * objectives.add(new ExploreObjective("visit_dungeon1", "dungeon1"));
 * objectives.add(new ExploreObjective("visit_dungeon2", "dungeon2"));
 * objectives.add(new ExploreObjective("visit_dungeon3", "dungeon3"));
 * 
 * // 빌더 패턴 사용
 * ExploreObjective.builder()
 *     .id("visit_hub")
 *     .region("Hub")
 *     .build()
 * 
 * @author Febrie
 */
public class ExploreObjective extends BaseObjective {

    private final String regionName;
    private final Map<UUID, Set<String>> playerRegionCache = new HashMap<>();
    private final boolean useWorldGuard;

    /**
     * WorldGuard 영역 기반 생성자
     *
     * @param id         목표 ID
     * @param regionName WorldGuard 영역 이름
     */
    public ExploreObjective(@NotNull String id, @NotNull String regionName) {
        super(id, 1);
        this.regionName = Objects.requireNonNull(regionName);
        this.useWorldGuard = true;

        // WorldGuard 플러그인 확인
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            throw new IllegalStateException("WorldGuard plugin is not loaded");
        }
    }

    @Override
    public @NotNull ObjectiveType getType() {
        return ObjectiveType.EXPLORE;
    }

    @Override
    public @NotNull String getStatusInfo(@NotNull ObjectiveProgress progress) {
        return regionName + " " + getProgressString(progress);
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

        Location to = moveEvent.getTo();
        if (to == null) return false;

        // 작은 움직임은 무시 (블록 단위 이동만 체크)
        Location from = moveEvent.getFrom();
        if (from.getBlockX() == to.getBlockX() &&
                from.getBlockZ() == to.getBlockZ()) {
            return false;
        }

        // WorldGuard 영역 확인
        Set<String> currentRegions = getRegionsAt(to);
        UUID playerId = player.getUniqueId();

        // 플레이어의 이전 영역 캐시 가져오기
        Set<String> previousRegions = playerRegionCache.computeIfAbsent(playerId, k -> new HashSet<>());

        // 대상 영역에 새로 진입했는지 확인
        boolean enteringRegion = currentRegions.contains(regionName) && !previousRegions.contains(regionName);

        // 캐시 업데이트
        playerRegionCache.put(playerId, new HashSet<>(currentRegions));
        
        return enteringRegion;
    }

    @Override
    public int calculateIncrement(@NotNull Event event, @NotNull Player player) {
        return canProgress(event, player) ? 1 : 0;
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
        return regionName;
    }

    public String getRegionName() {
        return regionName;
    }

    /**
     * 플레이어 캐시 정리 (메모리 관리용)
     */
    public void clearPlayerCache(@NotNull UUID playerId) {
        playerRegionCache.remove(playerId);
    }
    
    /**
     * 빌더 클래스
     */
    public static class Builder {
        private String id;
        private String region;
        
        public Builder id(@NotNull String id) {
            this.id = id;
            return this;
        }
        
        public Builder region(@NotNull String regionName) {
            this.region = regionName;
            return this;
        }
        
        public ExploreObjective build() {
            if (id == null) {
                throw new IllegalStateException("ID is required");
            }
            if (region == null) {
                throw new IllegalStateException("Region name is required");
            }
            return new ExploreObjective(id, region);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    @Override
    public @Nullable String validate() {
        if (regionName == null || regionName.trim().isEmpty()) {
            return "ExploreObjective '" + id + "': WorldGuard 영역 이름이 설정되지 않았습니다.";
        }
        
        return null; // 유효함
    }
}