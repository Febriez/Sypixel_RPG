package com.febrie.rpg.island.manager;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.island.Island;
import com.febrie.rpg.island.listener.IslandProtectionListener;
import com.febrie.rpg.island.permission.IslandPermissionHandler;
import com.febrie.rpg.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 섬 방문 추적기
 * PlayerMoveEvent 대신 스케줄러를 사용하여 플레이어의 섬 방문을 추적
 *
 * @author Febrie, CoffeeTory
 */
public class IslandVisitTracker {
    
    private final RPGMain plugin;
    private final IslandManager islandManager;
    private final IslandProtectionListener protectionListener;
    private final Map<UUID, String> playerCurrentIsland = new ConcurrentHashMap<>();
    private BukkitTask trackingTask;
    
    // 추적 간격 (틱 단위, 20틱 = 1초)
    private static final long TRACKING_INTERVAL = 60L; // 3초
    
    public IslandVisitTracker(@NotNull RPGMain plugin, @NotNull IslandManager islandManager) {
        this.plugin = plugin;
        this.islandManager = islandManager;
        this.protectionListener = new IslandProtectionListener(plugin, islandManager);
    }
    
    /**
     * 추적 시작
     */
    public void startTracking() {
        if (trackingTask != null && !trackingTask.isCancelled()) {
            return;
        }
        
        trackingTask = Bukkit.getScheduler().runTaskTimer(plugin, this::trackPlayers, 20L, TRACKING_INTERVAL);
    }
    
    /**
     * 추적 중지
     */
    public void stopTracking() {
        if (trackingTask != null && !trackingTask.isCancelled()) {
            trackingTask.cancel();
            trackingTask = null;
        }
        playerCurrentIsland.clear();
    }
    
    /**
     * 모든 온라인 플레이어의 위치 추적
     */
    private void trackPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            trackPlayerLocation(player);
        }
    }
    
    /**
     * 개별 플레이어의 위치 추적
     */
    private void trackPlayerLocation(@NotNull Player player) {
        Location location = player.getLocation();
        
        // 섬 월드가 아니면 처리하지 않음
        if (!islandManager.getWorldManager().isIslandWorld(location.getWorld())) {
            // 섬 월드를 벗어났다면 캐시에서 제거
            String previousIslandId = playerCurrentIsland.remove(player.getUniqueId());
            if (previousIslandId != null) {
                IslandDTO previousIsland = islandManager.getIslandFromCache(previousIslandId);
                if (previousIsland != null) {
                    protectionListener.handleIslandExit(player, previousIsland);
                }
            }
            return;
        }
        
        // 현재 위치의 섬 찾기
        Island currentIsland = islandManager.getIslandAt(location);
        String currentIslandId = currentIsland != null ? currentIsland.getId() : null;
        
        // 이전에 있던 섬 ID
        String previousIslandId = playerCurrentIsland.get(player.getUniqueId());
        
        // 섬이 변경되었는지 확인
        if (!isSameIsland(previousIslandId, currentIslandId)) {
            // 이전 섬에서 나감
            if (previousIslandId != null) {
                IslandDTO previousIsland = islandManager.getIslandFromCache(previousIslandId);
                if (previousIsland != null) {
                    protectionListener.handleIslandExit(player, previousIsland);
                }
            }
            
            // 새로운 섬에 입장
            if (currentIslandId != null) {
                playerCurrentIsland.put(player.getUniqueId(), currentIslandId);
                protectionListener.handleIslandEntry(player, currentIsland.getData());
            } else {
                playerCurrentIsland.remove(player.getUniqueId());
            }
        }
    }
    
    /**
     * 두 섬 ID가 동일한지 확인
     */
    private boolean isSameIsland(@Nullable String island1, @Nullable String island2) {
        if (island1 == null && island2 == null) {
            return true;
        }
        if (island1 == null || island2 == null) {
            return false;
        }
        return island1.equals(island2);
    }
    
    
    /**
     * 플레이어가 서버를 떠날 때 캐시 정리
     */
    public void handlePlayerQuit(@NotNull Player player) {
        String islandId = playerCurrentIsland.remove(player.getUniqueId());
        if (islandId != null) {
            IslandDTO island = islandManager.getIslandFromCache(islandId);
            if (island != null) {
                protectionListener.handleIslandExit(player, island);
            }
        }
    }
    
    /**
     * 플레이어가 현재 있는 섬 ID 조회
     */
    @Nullable
    public String getCurrentIslandId(@NotNull UUID playerUuid) {
        return playerCurrentIsland.get(playerUuid);
    }
    
    /**
     * 플레이어가 특정 섬에 있는지 확인
     */
    public boolean isPlayerOnIsland(@NotNull UUID playerUuid, @NotNull String islandId) {
        String currentIslandId = playerCurrentIsland.get(playerUuid);
        return currentIslandId != null && currentIslandId.equals(islandId);
    }
}