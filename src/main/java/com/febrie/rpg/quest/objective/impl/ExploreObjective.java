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
 * @author Febrie
 */
public class ExploreObjective extends BaseObjective {

    private final List<String> regionNames;
    private final Set<String> visitedRegions = new HashSet<>();
    private final Map<UUID, Set<String>> playerRegionCache = new HashMap<>();
    private final boolean useWorldGuard;

    /**
     * WorldGuard 영역 기반 생성자
     *
     * @param id          목표 ID
     * @param regionNames WorldGuard 영역 이름 목록
     */
    public ExploreObjective(@NotNull String id, @NotNull List<String> regionNames) {
        super(id, regionNames.size());
        this.regionNames = new ArrayList<>(regionNames);
        this.useWorldGuard = true;

        if (regionNames.isEmpty()) {
            throw new IllegalArgumentException("At least one region required");
        }

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
        if (regionNames.size() == 1) {
            return regionNames.get(0) + " " + getProgressString(progress);
        }
        return "Regions " + getProgressString(progress);
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

        // 새로 진입한 영역 확인
        for (String regionName : regionNames) {
            if (currentRegions.contains(regionName) &&
                    !previousRegions.contains(regionName) &&
                    !visitedRegions.contains(regionName)) {

                visitedRegions.add(regionName);
                playerRegionCache.put(playerId, new HashSet<>(currentRegions));
                return true;
            }
        }

        // 캐시 업데이트
        playerRegionCache.put(playerId, new HashSet<>(currentRegions));
        return false;
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
        return String.join(",", regionNames);
    }

    public List<String> getRegionNames() {
        return new ArrayList<>(regionNames);
    }

    public Set<String> getVisitedRegions() {
        return new HashSet<>(visitedRegions);
    }

    /**
     * 플레이어 캐시 정리 (메모리 관리용)
     */
    public void clearPlayerCache(@NotNull UUID playerId) {
        playerRegionCache.remove(playerId);
    }
}