package com.febrie.rpg.island.command;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.ColorUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * 섬 삭제/초기화 확인 명령어
 * /섬삭제확인, /섬초기화확인
 *
 * @author Febrie, CoffeeTory
 */
public class IslandConfirmCommand implements CommandExecutor {
    
    private final RPGMain plugin;
    private final IslandManager islandManager;
    private final boolean isDelete; // true: 삭제, false: 초기화
    
    public IslandConfirmCommand(@NotNull RPGMain plugin, @NotNull IslandManager islandManager, boolean isDelete) {
        this.plugin = plugin;
        this.islandManager = islandManager;
        this.isDelete = isDelete;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, @NotNull String[] args) {
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtil.colorize("&c이 명령어는 플레이어만 사용할 수 있습니다."));
            return true;
        }
        
        if (args.length < 1) {
            player.sendMessage(ColorUtil.colorize("&c잘못된 명령어 사용입니다."));
            return true;
        }
        
        String islandId = args[0];
        
        if (isDelete) {
            handleDeleteConfirm(player, islandId);
        } else {
            handleResetConfirm(player, islandId);
        }
        
        return true;
    }
    
    /**
     * 섬 삭제 확인 처리
     */
    private void handleDeleteConfirm(@NotNull Player player, @NotNull String islandId) {
        String playerUuid = player.getUniqueId().toString();
        
        islandManager.loadIsland(islandId).thenAccept(island -> {
            if (island == null) {
                player.sendMessage(ColorUtil.colorize("&c섬을 찾을 수 없습니다."));
                return;
            }
            
            if (!island.ownerUuid().equals(playerUuid)) {
                player.sendMessage(ColorUtil.colorize("&c자신의 섬만 삭제할 수 있습니다."));
                return;
            }
            
            player.sendMessage(ColorUtil.colorize("&c섬을 삭제하는 중..."));
            
            islandManager.deleteIsland(islandId).thenAccept(deleted -> {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (deleted) {
                        player.sendMessage(ColorUtil.colorize("&a섬이 성공적으로 삭제되었습니다."));
                        
                        // 플레이어를 스폰으로 이동
                        if (plugin.getServer().getWorlds().size() > 0) {
                            player.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
                        }
                    } else {
                        player.sendMessage(ColorUtil.colorize("&c섬 삭제에 실패했습니다."));
                    }
                });
            });
        });
    }
    
    /**
     * 섬 초기화 확인 처리
     */
    private void handleResetConfirm(@NotNull Player player, @NotNull String islandId) {
        String playerUuid = player.getUniqueId().toString();
        
        islandManager.loadIsland(islandId).thenAccept(island -> {
            if (island == null) {
                player.sendMessage(ColorUtil.colorize("&c섬을 찾을 수 없습니다."));
                return;
            }
            
            if (!island.ownerUuid().equals(playerUuid)) {
                player.sendMessage(ColorUtil.colorize("&c자신의 섬만 초기화할 수 있습니다."));
                return;
            }
            
            player.sendMessage(ColorUtil.colorize("&e섬을 초기화하는 중..."));
            
            islandManager.resetIsland(islandId).thenAccept(reset -> {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (reset) {
                        player.sendMessage(ColorUtil.colorize("&a섬이 성공적으로 초기화되었습니다."));
                        player.sendMessage(ColorUtil.colorize("&e섬의 중앙으로 이동합니다..."));
                        
                        // 플레이어를 섬 중앙으로 이동
                        Location spawn = island.spawnData().defaultSpawn()
                                .toLocation(islandManager.getWorldManager().getIslandWorld());
                        spawn.setY(spawn.getY() + 4);
                        player.teleport(spawn);
                    } else {
                        player.sendMessage(ColorUtil.colorize("&c섬 초기화에 실패했습니다."));
                    }
                });
            });
        });
    }
}