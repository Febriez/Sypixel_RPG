package com.febrie.rpg.island.listener;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandVisitDTO;
import com.febrie.rpg.island.Island;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import net.kyori.adventure.text.Component;
/**
 * 섬 방문 추적 리스너
 * 플레이어가 섬에 입장/퇴장할 때를 감지하고 기록
 *
 * @author Febrie, CoffeeTory
 */
public class IslandVisitListener implements Listener {
    
    private final RPGMain plugin;
    private final IslandManager islandManager;
    
    // 현재 방문 중인 정보 추적 (플레이어 UUID -> 섬 ID + 시작 시간)
    private final Map<String, VisitInfo> currentVisits = new ConcurrentHashMap<>();
    
    // 플레이어의 마지막 위치 정보 (플레이어 UUID -> 섬 ID)
    private final Map<String, String> lastKnownIslands = new ConcurrentHashMap<>();
    
    // 방문 정보 임시 저장
    private static class VisitInfo {
        final String islandId;
        final long startTime;
        
        VisitInfo(String islandId, long startTime) {
            this.islandId = islandId;
            this.startTime = startTime;
        }
    }
    
    public IslandVisitListener(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        this.islandManager = plugin.getIslandManager();
        
        // 2초마다 섬 월드의 플레이어들 위치 확인
        startLocationChecker();
    }
    
    /**
     * 2초마다 섬 월드의 플레이어들 위치를 확인하는 스케줄러 시작
     */
    private void startLocationChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // null 체크
                if (islandManager == null || islandManager.getWorldManager() == null) {
                    return;
                }
                
                // 섬 월드 가져오기
                var islandWorld = islandManager.getWorldManager().getIslandWorld();
                if (islandWorld == null) {
                    return;
                }
                
                // 섬 월드에 있는 모든 플레이어 확인
                for (Player player : islandWorld.getPlayers()) {
                    checkPlayerLocation(player);
                }
            }
        }.runTaskTimer(plugin, 40L, 40L); // 2초(40 ticks)마다 실행
    }
    
    /**
     * 플레이어의 현재 위치 확인 및 섬 방문 처리
     */
    private void checkPlayerLocation(@NotNull Player player) {
        String playerUuid = player.getUniqueId().toString();
        Location location = player.getLocation();
        
        // 현재 플레이어가 있는 섬 찾기
        Island currentIsland = islandManager.getIslandAt(location);
        String currentIslandId = currentIsland != null ? currentIsland.getId() : null;
        
        // 이전에 있던 섬 ID
        String previousIslandId = lastKnownIslands.get(playerUuid);
        
        // 섬이 변경되었는지 확인
        if (!Objects.equals(currentIslandId, previousIslandId)) {
            // 이전 섬에서 나가기
            if (previousIslandId != null) {
                VisitInfo visit = currentVisits.remove(playerUuid);
                if (visit != null) {
                    endVisit(playerUuid, visit);
                }
            }
            
            // 새 섬에 들어가기
            if (currentIslandId != null) {
                startVisit(player, currentIsland);
                lastKnownIslands.put(playerUuid, currentIslandId);
            } else {
                lastKnownIslands.remove(playerUuid);
            }
        }
    }
    
    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String playerUuid = player.getUniqueId().toString();
        
        // 섬 월드로 이동한 경우
        if (islandManager != null && islandManager.getWorldManager() != null &&
            islandManager.getWorldManager().isIslandWorld(player.getWorld())) {
            // 즉시 위치 확인
            checkPlayerLocation(player);
        } else {
            // 섬 월드에서 나간 경우
            VisitInfo visit = currentVisits.remove(playerUuid);
            if (visit != null) {
                endVisit(playerUuid, visit);
            }
            lastKnownIslands.remove(playerUuid);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 플레이어 나갈 때 방문 종료 처리
        String playerUuid = event.getPlayer().getUniqueId().toString();
        VisitInfo visit = currentVisits.remove(playerUuid);
        
        if (visit != null) {
            endVisit(playerUuid, visit);
        }
        
        // 마지막 위치 정보도 제거
        lastKnownIslands.remove(playerUuid);
        
        // Visit tracker에서도 제거
        islandManager.getVisitTracker().handlePlayerQuit(event.getPlayer());
    }
    
    /**
     * 섬 방문 시작 처리
     */
    private void startVisit(@NotNull Player player, @NotNull Island island) {
        String playerUuid = player.getUniqueId().toString();
        
        // 본인의 섬이면 방문 기록하지 않음
        if (island.getOwnerUuid().equals(playerUuid) ||
            island.getData().membership().members().stream().anyMatch(m -> m.uuid().equals(playerUuid))) {
            return;
        }
        
        // 방문 시작
        currentVisits.put(playerUuid, new VisitInfo(island.getId(), System.currentTimeMillis()));
        
        // 환영 메시지 (공개 섬인 경우)
        if (island.isPublic()) {
            player.sendMessage(UnifiedColorUtil.parse("&a" + island.getName() + " 섬에 방문하셨습니다!"));
        }
    }
    
    /**
     * 섬 방문 종료 처리
     */
    private void endVisit(@NotNull String playerUuid, @NotNull VisitInfo visit) {
        long duration = System.currentTimeMillis() - visit.startTime;
        
        // 5초 미만 방문은 로그 출력하지 않음
        if (duration < 5000) {
            return;
        }
        
        // 방문 종료 로그만 출력 (저장하지 않음)
        islandManager.loadIsland(visit.islandId).thenAccept(island -> {
            if (island == null) return;
            
            Player player = Bukkit.getPlayer(java.util.UUID.fromString(playerUuid));
            String playerName = player != null ? player.getName() : "Unknown";
            
            // 방문 시간을 분:초 형식으로 표시
            long totalSeconds = duration / 1000;
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            String timeStr = minutes > 0 ? minutes + "분 " + seconds + "초" : seconds + "초";
            
            LogUtil.info("방문 종료: " + playerName + " -> " + island.getName() + 
                    " (" + timeStr + ")");
        });
    }
    
    /**
     * 현재 방문 중인 섬 정보 가져오기
     */
    public Optional<String> getCurrentVisitingIsland(@NotNull String playerUuid) {
        VisitInfo visit = currentVisits.get(playerUuid);
        return visit != null ? Optional.of(visit.islandId) : Optional.empty();
    }
    
    /**
     * 특정 섬의 현재 방문자 목록 가져오기
     */
    public List<CurrentVisitorInfo> getCurrentVisitors(@NotNull String islandId) {
        List<CurrentVisitorInfo> visitors = new ArrayList<>();
        currentVisits.forEach((playerUuid, visit) -> {
            if (visit.islandId.equals(islandId)) {
                Player player = Bukkit.getPlayer(java.util.UUID.fromString(playerUuid));
                if (player != null && player.isOnline()) {
                    visitors.add(new CurrentVisitorInfo(
                        playerUuid,
                        player.getName(),
                        visit.startTime,
                        System.currentTimeMillis() - visit.startTime
                    ));
                }
            }
        });
        return visitors;
    }
    
    /**
     * 특정 방문자의 방문 정보 가져오기
     */
    public Optional<CurrentVisitorInfo> getVisitInfo(@NotNull String islandId, @NotNull String playerUuid) {
        VisitInfo visit = currentVisits.get(playerUuid);
        if (visit != null && visit.islandId.equals(islandId)) {
            Player player = Bukkit.getPlayer(java.util.UUID.fromString(playerUuid));
            if (player != null && player.isOnline()) {
                return Optional.of(new CurrentVisitorInfo(
                    playerUuid,
                    player.getName(),
                    visit.startTime,
                    System.currentTimeMillis() - visit.startTime
                ));
            }
        }
        return Optional.empty();
    }
    
    /**
     * 모든 현재 방문자 목록 가져오기
     */
    public Map<String, List<CurrentVisitorInfo>> getAllCurrentVisitors() {
        Map<String, List<CurrentVisitorInfo>> allVisitors = new HashMap<>();
        currentVisits.forEach((playerUuid, visit) -> {
            Player player = Bukkit.getPlayer(java.util.UUID.fromString(playerUuid));
            if (player != null && player.isOnline()) {
                allVisitors.computeIfAbsent(visit.islandId, k -> new ArrayList<>())
                    .add(new CurrentVisitorInfo(
                        playerUuid,
                        player.getName(),
                        visit.startTime,
                        System.currentTimeMillis() - visit.startTime
                    ));
            }
        });
        return allVisitors;
    }
    
    /**
     * 현재 방문 정보 클래스
     */
    public static class CurrentVisitorInfo {
        private final String playerUuid;
        private final String playerName;
        private final long visitStartTime;
        private final long currentDuration;
        
        public CurrentVisitorInfo(String playerUuid, String playerName, long visitStartTime, long currentDuration) {
            this.playerUuid = playerUuid;
            this.playerName = playerName;
            this.visitStartTime = visitStartTime;
            this.currentDuration = currentDuration;
        }
        
        public String getPlayerUuid() { return playerUuid; }
        public String getPlayerName() { return playerName; }
        public long getVisitStartTime() { return visitStartTime; }
        public long getCurrentDuration() { return currentDuration; }
    }
    
    /**
     * 모든 현재 방문 종료 (서버 종료 시)
     */
    public void endAllVisits() {
        currentVisits.forEach(this::endVisit);
        currentVisits.clear();
    }
}