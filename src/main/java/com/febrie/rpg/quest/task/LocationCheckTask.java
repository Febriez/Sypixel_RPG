package com.febrie.rpg.quest.task;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import com.febrie.rpg.util.LogUtil;

/**
 * 지역 방문 퀘스트 목표를 비동기로 체크하는 태스크
 * 3초마다 온라인 플레이어들의 위치를 확인하여 퀘스트 진행
 *
 * @author Febrie
 */
public class LocationCheckTask implements Runnable {
    
    private final RPGMain plugin;
    
    // 플레이어별 마지막 위치 캐시 (이동 감지용)
    private final Map<UUID, Location> lastLocations = new ConcurrentHashMap<>();
    
    // 플레이어별 현재 영역 캐시
    private final Map<UUID, Set<String>> playerRegionsCache = new ConcurrentHashMap<>();
    
    public LocationCheckTask(@NotNull RPGMain plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        // 온라인 플레이어 목록 가져오기
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        if (onlinePlayers.isEmpty()) return;
        
        // 비동기 처리
        CompletableFuture.runAsync(() -> {
            Map<UUID, PlayerLocationData> playerDataMap = new HashMap<>();
            
            // 메인 스레드에서 플레이어 위치 정보 수집
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (Player player : onlinePlayers) {
                    if (!player.isOnline()) continue;
                    
                    UUID playerId = player.getUniqueId();
                    Location currentLocation = player.getLocation();
                    
                    // 지역 방문 목표가 있는 활성 퀘스트가 있는지 확인
                    boolean hasLocationObjective = false;
                    QuestManager questManager = QuestManager.getInstance();
                    List<QuestProgress> activeQuests = questManager.getActiveQuests(playerId);
                    
                    for (QuestProgress progress : activeQuests) {
                        Quest quest = QuestManager.getInstance().getQuest(progress.getQuestId());
                        if (quest == null) continue;
                        
                        for (QuestObjective objective : quest.getObjectives()) {
                            if (objective instanceof VisitLocationObjective) {
                                hasLocationObjective = true;
                                break;
                            }
                        }
                        if (hasLocationObjective) break;
                    }
                    
                    // 지역 방문 목표가 있는 경우만 처리
                    if (hasLocationObjective) {
                        playerDataMap.put(playerId, new PlayerLocationData(
                            player,
                            currentLocation.clone(),
                            lastLocations.get(playerId),
                            activeQuests
                        ));
                        lastLocations.put(playerId, currentLocation.clone());
                    }
                }
                
                // 비동기로 처리 계속
                CompletableFuture.runAsync(() -> processLocationChecks(playerDataMap));
            });
        });
    }
    
    /**
     * 비동기로 위치 체크 처리
     */
    private void processLocationChecks(@NotNull Map<UUID, PlayerLocationData> playerDataMap) {
        for (Map.Entry<UUID, PlayerLocationData> entry : playerDataMap.entrySet()) {
            UUID playerId = entry.getKey();
            PlayerLocationData data = entry.getValue();
            
            // 이동하지 않았으면 스킵
            if (data.lastLocation != null) {
                if (data.currentLocation.getBlockX() == data.lastLocation.getBlockX() &&
                    data.currentLocation.getBlockZ() == data.lastLocation.getBlockZ()) {
                    continue;
                }
            }
            
            // 현재 위치의 영역 확인
            Set<String> currentRegions = getRegionsAt(data.currentLocation);
            Set<String> previousRegions = playerRegionsCache.getOrDefault(playerId, new HashSet<>());
            
            // 새로 진입한 영역 확인
            boolean regionChanged = !currentRegions.equals(previousRegions);
            if (regionChanged) {
                playerRegionsCache.put(playerId, new HashSet<>(currentRegions));
                if (!currentRegions.isEmpty()) {
                    LogUtil.info("Player " + data.player.getName() + " is in regions: " + currentRegions);
                }
            }
            
            // 퀘스트 목표 처리
            for (QuestProgress progress : data.activeQuests) {
                if (!progress.isActive()) continue;
                
                Quest quest = QuestManager.getInstance().getQuest(progress.getQuestId());
                if (quest == null) continue;
                
                // 각 목표 확인
                for (QuestObjective objective : quest.getObjectives()) {
                    if (!(objective instanceof VisitLocationObjective visitObj)) continue;
                    
                    // 이미 완료된 목표는 스킵
                    if (progress.isObjectiveComplete(objective.getId())) continue;
                    
                    boolean shouldProgress = false;
                    
                    if (visitObj.getLocationType() == VisitLocationObjective.LocationType.WORLDGUARD_REGION) {
                        // WorldGuard 영역 체크 (대소문자 구분 없이)
                        String targetRegion = visitObj.getRegionName();
                        boolean inTargetRegion = currentRegions.stream()
                            .anyMatch(region -> region.equalsIgnoreCase(targetRegion));
                        boolean wasInTargetRegion = previousRegions.stream()
                            .anyMatch(region -> region.equalsIgnoreCase(targetRegion));
                        
                        if (inTargetRegion && !wasInTargetRegion) {
                            shouldProgress = true;
                            LogUtil.info("Player " + data.player.getName() + " entered region " + targetRegion + 
                                " for quest " + quest.getId().name());
                        }
                    } else {
                        // 좌표 기반 체크
                        Location targetLocation = visitObj.getTargetLocation();
                        if (targetLocation != null && 
                            data.currentLocation.getWorld().equals(targetLocation.getWorld())) {
                            double distance = data.currentLocation.distance(targetLocation);
                            if (distance <= visitObj.getRadius()) {
                                // 이전에 범위 밖에 있었는지 확인
                                if (data.lastLocation == null || 
                                    data.lastLocation.distance(targetLocation) > visitObj.getRadius()) {
                                    shouldProgress = true;
                                }
                            }
                        }
                    }
                    
                    // 메인 스레드에서 퀘스트 진행 처리
                    if (shouldProgress) {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            // PlayerMoveEvent를 시뮬레이션
                            PlayerMoveEvent fakeEvent = new PlayerMoveEvent(
                                data.player, 
                                data.lastLocation != null ? data.lastLocation : data.currentLocation,
                                data.currentLocation
                            );
                            QuestManager.getInstance().progressObjective(fakeEvent, data.player);
                        });
                    }
                }
            }
        }
    }
    
    /**
     * 위치의 WorldGuard 영역 목록 가져오기
     */
    private Set<String> getRegionsAt(@NotNull Location location) {
        Set<String> regionSet = new HashSet<>();
        
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            return regionSet;
        }

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
    
    /**
     * 플레이어 캐시 정리
     */
    public void clearPlayerCache(@NotNull UUID playerId) {
        lastLocations.remove(playerId);
        playerRegionsCache.remove(playerId);
    }
    
    /**
     * 모든 캐시 정리
     */
    public void clearAllCaches() {
        lastLocations.clear();
        playerRegionsCache.clear();
    }
    
    /**
     * 플레이어 위치 데이터
     */
    private static class PlayerLocationData {
        final Player player;
        final Location currentLocation;
        final Location lastLocation;
        final List<QuestProgress> activeQuests;
        
        PlayerLocationData(Player player, Location currentLocation, Location lastLocation, List<QuestProgress> activeQuests) {
            this.player = player;
            this.currentLocation = currentLocation;
            this.lastLocation = lastLocation;
            this.activeQuests = activeQuests;
        }
    }
}