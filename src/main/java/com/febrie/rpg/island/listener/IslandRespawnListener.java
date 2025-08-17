package com.febrie.rpg.island.listener;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandSpawnDTO;
import com.febrie.rpg.dto.island.IslandSpawnPointDTO;
import com.febrie.rpg.dto.island.PlayerIslandDataDTO;
import com.febrie.rpg.island.manager.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 섬 월드 리스폰 이벤트 리스너
 * 플레이어가 섬 월드에서 죽었을 때 적절한 스폰 위치로 리스폰시킴
 *
 * @author Febrie, CoffeeTory
 */
public class IslandRespawnListener implements Listener {

    private final RPGMain plugin;
    private final IslandManager islandManager;

    public IslandRespawnListener(@NotNull RPGMain plugin, @NotNull IslandManager islandManager) {
        this.plugin = plugin;
        this.islandManager = islandManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        // 섬 월드에서 죽은 경우만 처리
        if (!player.getWorld().getName().equals("island_world")) {
            return;
        }

        // 플레이어 섬 데이터 가져오기
        PlayerIslandDataDTO playerData = islandManager.getPlayerIslandDataFromCache(player.getUniqueId().toString());

        if (playerData == null || playerData.currentIslandId() == null) {
            // 섬이 없는 경우 Hub 월드로
            World hub = Bukkit.getWorld("world");
            if (hub != null) {
                event.setRespawnLocation(hub.getSpawnLocation());
            }
            return;
        }

        // 섬 데이터 가져오기
        IslandDTO island = islandManager.getIslandFromCache(playerData.currentIslandId());
        if (island == null) {
            // 섬 데이터가 없으면 Hub로
            World hub = Bukkit.getWorld("world");
            if (hub != null) {
                event.setRespawnLocation(hub.getSpawnLocation());
            }
            return;
        }

        // 섬 스폰 위치 계산
        IslandSpawnDTO spawnData = island.configuration().spawnData();
        IslandSpawnPointDTO spawnPoint = null;

        // 1. 개인 스폰 확인
        boolean isOwner = island.core().ownerUuid().equals(player.getUniqueId().toString());
        spawnPoint = spawnData.getPersonalSpawn(player.getUniqueId().toString(), isOwner);

        // 2. 개인 스폰이 없으면 기본 스폰 사용
        if (spawnPoint == null) {
            spawnPoint = spawnData.defaultSpawn();
        }

        // 섬 월드 가져오기
        World islandWorld = Bukkit.getWorld("island_world");
        if (islandWorld != null) {
            // 스폰 위치로 텔레포트 (절대 좌표로 변환)
            Location respawnLoc = new Location(
                islandWorld,
                spawnPoint.x(),
                spawnPoint.y() + 0.5, // 블록 중앙에 스폰
                spawnPoint.z(),
                spawnPoint.yaw(),
                spawnPoint.pitch()
            );
            event.setRespawnLocation(respawnLoc);
        }
    }
}