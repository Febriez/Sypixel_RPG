package com.febrie.rpg.command.admin.subcommand;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.base.BaseSubCommand;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.island.Island;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 섬 관리 명령어
 * 
 * @author Febrie
 */
public class IslandCommand extends BaseSubCommand {
    
    private final IslandManager islandManager;
    
    public IslandCommand(@NotNull RPGMain plugin) {
        super("island", "rpg.admin.island", "Manage player islands");
        this.islandManager = plugin.getIslandManager();
        this.setMinArgs(1);
        this.setUsage("/rpgadmin island <info|tp|delete|reset|setowner> [player]");
    }
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "info" -> {
                if (args.length < 2) {
                    if (sender instanceof Player player) {
                        Island island = islandManager.getIslandAt(player.getLocation());
                        if (island == null) {
                            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.not-found"));
                            return true;
                        }
                        showIslandInfo(sender, island);
                    } else {
                        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.info.usage"));
                    }
                } else {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
                        return true;
                    }
                    
                    islandManager.getPlayerIsland(target.getUniqueId().toString(), target.getName())
                        .thenAccept(island -> {
                            if (island == null) {
                                sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.player-no-island",
                                    "player", target.getName()));
                            } else {
                                showIslandInfo(sender, island);
                            }
                        });
                }
                return true;
            }
            
            case "tp" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-only"));
                    return true;
                }
                
                if (args.length < 2) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.tp.usage"));
                    return true;
                }
                
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
                    return true;
                }
                
                islandManager.getPlayerIsland(target.getUniqueId().toString(), target.getName())
                    .thenAccept(island -> {
                        if (island == null) {
                            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.player-no-island",
                                "player", target.getName()));
                            return;
                        }
                        
                        Location spawnLoc = island.getSpawnLocation();
                        if (spawnLoc == null) {
                            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.tp.no-spawn"));
                            return;
                        }
                        
                        player.teleport(spawnLoc);
                        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.tp.success",
                            "player", target.getName()));
                    });
                return true;
            }
            
            case "delete" -> {
                if (args.length < 2) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.delete.usage"));
                    return true;
                }
                
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
                    return true;
                }
                
                islandManager.getPlayerIsland(target.getUniqueId().toString(), target.getName())
                    .thenAccept(island -> {
                        if (island == null) {
                            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.player-no-island",
                                "player", target.getName()));
                            return;
                        }
                        
                        // 확인 메시지
                        if (args.length < 3 || !args[2].equalsIgnoreCase("confirm")) {
                            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.delete.confirm",
                                "player", target.getName()));
                            return;
                        }
                        
                        islandManager.deleteIsland(island.getId());
                        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.delete.success",
                            "player", target.getName()));
                    });
                return true;
            }
            
            case "reset" -> {
                if (args.length < 2) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.reset.usage"));
                    return true;
                }
                
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
                    return true;
                }
                
                islandManager.getPlayerIsland(target.getUniqueId().toString(), target.getName())
                    .thenAccept(island -> {
                        if (island == null) {
                            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.player-no-island",
                                "player", target.getName()));
                            return;
                        }
                        
                        // 확인 메시지
                        if (args.length < 3 || !args[2].equalsIgnoreCase("confirm")) {
                            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.reset.confirm",
                                "player", target.getName()));
                            return;
                        }
                        
                        islandManager.resetIsland(island.getId());
                        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.reset.success",
                            "player", target.getName()));
                    });
                return true;
            }
            
            case "setowner" -> {
                if (args.length < 3) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.setowner.usage"));
                    return true;
                }
                
                Player oldOwner = Bukkit.getPlayer(args[1]);
                if (oldOwner == null) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
                    return true;
                }
                
                final Player newOwner = Bukkit.getPlayer(args[2]);
                if (newOwner == null) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
                    return true;
                }
                
                islandManager.getPlayerIsland(oldOwner.getUniqueId().toString(), oldOwner.getName())
                    .thenAccept(island -> {
                        if (island == null) {
                            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.player-no-island",
                                "player", oldOwner.getName()));
                            return;
                        }
                        
                        // Island의 소유자 변경 (IslandDTO를 새로 생성)
                        IslandDTO currentData = island.getData();
                        IslandDTO updatedData = IslandDTO.fromFields(
                            currentData.core().islandId(),
                            newOwner.getUniqueId().toString(),
                            newOwner.getName(),
                            currentData.core().islandName(),
                            currentData.core().size(),
                            currentData.core().isPublic(),
                            currentData.core().createdAt(),
                            System.currentTimeMillis(),
                            currentData.membership().members(),
                            currentData.membership().workers(),
                            currentData.membership().contributions(),
                            currentData.configuration().spawnData(),
                            currentData.configuration().upgradeData(),
                            currentData.configuration().permissions(),
                            currentData.social().pendingInvites(),
                            currentData.social().recentVisits(),
                            currentData.core().totalResets(),
                            currentData.core().deletionScheduledAt(),
                            currentData.configuration().settings()
                        );
                        islandManager.updateIsland(updatedData);
                        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.setowner.success",
                            "oldowner", oldOwner.getName(),
                            "newowner", newOwner.getName()));
                    });
                return true;
            }
            
            default -> {
                sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.usage"));
                return false;
            }
        }
    }
    
    private void showIslandInfo(@NotNull CommandSender sender, @NotNull Island island) {
        IslandDTO dto = island.getData();
        
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.info.title"));
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.info.id", 
            "id", dto.core().islandId()));
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.info.name", 
            "name", dto.core().islandName()));
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.info.owner", 
            "owner", dto.core().ownerName()));
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.info.members", 
            "count", String.valueOf(dto.membership().members().size())));
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.info.size", 
            "size", String.valueOf(dto.core().size())));
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.info.public", 
            "status", dto.core().isPublic() ? "공개" : "비공개"));
    }
    
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("info", "tp", "delete", "reset", "setowner").stream()
                .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            if (!args[0].equalsIgnoreCase("setowner")) {
                return getOnlinePlayerNames().stream()
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            } else {
                return getOnlinePlayerNames().stream()
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("reset")) {
                return List.of("confirm");
            } else if (args[0].equalsIgnoreCase("setowner")) {
                return getOnlinePlayerNames().stream()
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
            }
        }
        
        return List.of();
    }
}