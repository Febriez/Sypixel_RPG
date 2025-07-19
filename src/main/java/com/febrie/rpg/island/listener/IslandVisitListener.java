package com.febrie.rpg.island.listener;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandVisitDTO;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // 블록 단위 이동이 아니면 무시
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        
        handleLocationChange(event.getPlayer(), event.getFrom(), event.getTo());
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        handleLocationChange(event.getPlayer(), event.getFrom(), event.getTo());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 플레이어 나갈 때 방문 종료 처리
        String playerUuid = event.getPlayer().getUniqueId().toString();
        VisitInfo visit = currentVisits.remove(playerUuid);
        
        if (visit != null) {
            endVisit(playerUuid, visit);
        }
    }
    
    private void handleLocationChange(@NotNull Player player, @NotNull Location from, @NotNull Location to) {
        // 섬 월드가 아니면 무시
        if (!islandManager.getWorldManager().isIslandWorld(to.getWorld())) {
            // 섬 월드에서 나간 경우 방문 종료
            String playerUuid = player.getUniqueId().toString();
            VisitInfo visit = currentVisits.remove(playerUuid);
            if (visit != null) {
                endVisit(playerUuid, visit);
            }
            return;
        }
        
        // 현재 위치의 섬 찾기
        IslandDTO fromIsland = islandManager.getIslandAt(from);
        IslandDTO toIsland = islandManager.getIslandAt(to);
        
        // 같은 섬이면 무시
        if ((fromIsland == null && toIsland == null) ||
            (fromIsland != null && toIsland != null && fromIsland.islandId().equals(toIsland.islandId()))) {
            return;
        }
        
        String playerUuid = player.getUniqueId().toString();
        
        // 이전 섬에서 나간 경우
        if (fromIsland != null) {
            VisitInfo visit = currentVisits.remove(playerUuid);
            if (visit != null) {
                endVisit(playerUuid, visit);
            }
        }
        
        // 새 섬에 들어간 경우
        if (toIsland != null) {
            // 본인의 섬이면 방문 기록하지 않음
            if (toIsland.ownerUuid().equals(playerUuid) ||
                toIsland.members().stream().anyMatch(m -> m.uuid().equals(playerUuid))) {
                return;
            }
            
            // 방문 시작
            currentVisits.put(playerUuid, new VisitInfo(toIsland.islandId(), System.currentTimeMillis()));
            
            // 환영 메시지 (공개 섬인 경우)
            if (toIsland.isPublic()) {
                player.sendMessage(ColorUtil.colorize("&a" + toIsland.islandName() + " 섬에 방문하셨습니다!"));
            }
        }
    }
    
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
            List<IslandVisitDTO> recentVisits = new ArrayList<>(island.recentVisits());
            recentVisits.add(0, visitRecord); // 최신 방문을 앞에 추가
            
            if (recentVisits.size() > 100) {
                recentVisits = recentVisits.subList(0, 100);
            }
            
            // 섬 데이터 업데이트
            IslandDTO updatedIsland = new IslandDTO(
                    island.islandId(),
                    island.ownerUuid(),
                    island.ownerName(),
                    island.islandName(),
                    island.size(),
                    island.isPublic(),
                    island.createdAt(),
                    island.lastActivity(),
                    island.members(),
                    island.workers(),
                    island.contributions(),
                    island.spawnData(),
                    island.upgradeData(),
                    island.permissions(),
                    island.pendingInvites(),
                    recentVisits,
                    island.totalResets(),
                    island.deletionScheduledAt()
            );
            
            islandManager.updateIsland(updatedIsland).thenAccept(success -> {
                if (success) {
                    LogUtil.info("방문 기록 저장: " + playerUuid + " -> " + island.islandName() + 
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