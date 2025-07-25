package com.febrie.rpg.island.listener;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandVisitDTO;
import com.febrie.rpg.island.Island;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.ColorUtil;
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
            island.getData().members().stream().anyMatch(m -> m.uuid().equals(playerUuid))) {
            return;
        }
        
        // 방문 시작
        currentVisits.put(playerUuid, new VisitInfo(island.getId(), System.currentTimeMillis()));
        
        // 환영 메시지 (공개 섬인 경우)
        if (island.isPublic()) {
            player.sendMessage(ColorUtil.colorize("&a" + island.getName() + " 섬에 방문하셨습니다!"));
        }
    }
    
    /**
     * 섬 방문 종료 처리
     */
    private void endVisit(@NotNull String playerUuid, @NotNull VisitInfo visit) {
        long duration = System.currentTimeMillis() - visit.startTime;
        
        // 5초 미만 방문은 기록하지 않음
        if (duration < 5000) {
            return;
        }
        
        // 방문 기록 저장
        islandManager.loadIsland(visit.islandId).thenAccept(island -> {
            if (island == null) return;
            
            // 방문 기록 생성
            Player player = Bukkit.getPlayer(java.util.UUID.fromString(playerUuid));
            String playerName = player != null ? player.getName() : "Unknown";
            IslandVisitDTO visitRecord = new IslandVisitDTO(
                    playerUuid,
                    playerName,
                    visit.startTime,
                    duration
            );
            
            // 최근 방문 목록 업데이트 (최대 100개 유지)
            IslandDTO islandData = island.getData();
            List<IslandVisitDTO> recentVisits = new ArrayList<>(islandData.recentVisits());
            recentVisits.add(0, visitRecord); // 최신 방문을 앞에 추가
            
            if (recentVisits.size() > 100) {
                recentVisits = recentVisits.subList(0, 100);
            }
            
            // 섬 데이터 업데이트
            IslandDTO updatedIsland = new IslandDTO(
                    islandData.islandId(),
                    islandData.ownerUuid(),
                    islandData.ownerName(),
                    islandData.islandName(),
                    islandData.size(),
                    islandData.isPublic(),
                    islandData.createdAt(),
                    islandData.lastActivity(),
                    islandData.members(),
                    islandData.workers(),
                    islandData.contributions(),
                    islandData.spawnData(),
                    islandData.upgradeData(),
                    islandData.permissions(),
                    islandData.pendingInvites(),
                    recentVisits,
                    islandData.totalResets(),
                    islandData.deletionScheduledAt(),
                    islandData.settings()
            );
            
            islandManager.updateIsland(updatedIsland).thenAccept(success -> {
                if (success) {
                    LogUtil.info("방문 기록 저장: " + playerUuid + " -> " + island.getName() + 
                            " (" + (duration / 1000) + "초)");
                }
            });
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
     * 모든 현재 방문 종료 (서버 종료 시)
     */
    public void endAllVisits() {
        currentVisits.forEach(this::endVisit);
        currentVisits.clear();
    }
}