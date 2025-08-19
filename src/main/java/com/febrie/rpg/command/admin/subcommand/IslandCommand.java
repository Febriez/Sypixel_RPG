package com.febrie.rpg.command.admin.subcommand;

import net.kyori.adventure.text.Component;
import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.base.BaseSubCommand;
import com.febrie.rpg.dto.island.IslandCoreDTO;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.island.Island;
import com.febrie.rpg.island.manager.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 섬 관리 명령어
 *
 * @author Febrie
 */
public class IslandCommand extends BaseSubCommand {

    private final IslandManager islandManager;

    private IslandCommand(@NotNull String name, @NotNull String permission, @NotNull String description, @NotNull IslandManager islandManager) {
        super(name, permission, description);
        this.islandManager = islandManager;
        this.setMinArgs(1);
        this.setUsage("/rpgadmin island <info|tp|delete|reset|setowner> [player]");
    }

    @Contract("_ -> new")
    public static @NotNull IslandCommand create(@NotNull RPGMain plugin) {
        return new IslandCommand("island", "rpg.admin.island", "Manage player islands", plugin.getIslandManager());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        String action = args[0].toLowerCase();

        switch (action) {
            case "info" -> {
                if (args.length < 2) {
                    if (sender instanceof Player player) {
                        Island island = islandManager.getIslandAt(player.getLocation());
                        if (island == null) {
                            sender.sendMessage(Component.translatable("commands.admin.island.not-found"));
                            return true;
                        }
                        showIslandInfo(sender, island);
                    } else {
                        sender.sendMessage(Component.translatable("commands.admin.island.info.usage"));
                    }
                } else {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(Component.translatable("commands.admin.player-not-found"));
                        return true;
                    }

                    islandManager.getPlayerIsland(target.getUniqueId().toString(), target.getName())
                            .thenAccept(island -> {
                                if (island == null) {
                                    sender.sendMessage(Component.translatable("commands.admin.island.player-no-island", Component.text(target.getName())));
                                } else {
                                    showIslandInfo(sender, island);
                                }
                            });
                }
                return true;
            }

            case "tp" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Component.translatable("commands.admin.player-only"));
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(Component.translatable("commands.admin.island.tp.usage"));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(Component.translatable("commands.admin.player-not-found"));
                    return true;
                }

                islandManager.getPlayerIsland(target.getUniqueId().toString(), target.getName()).thenAccept(island -> {
                    if (island == null) {
                        sender.sendMessage(Component.translatable("commands.admin.island.player-no-island", Component.text(target.getName())));
                        return;
                    }

                    Location spawnLoc = island.getSpawnLocation();
                    if (spawnLoc == null) {
                        sender.sendMessage(Component.translatable("commands.admin.island.tp.no-spawn"));
                        return;
                    }

                    player.teleport(spawnLoc);
                    sender.sendMessage(Component.translatable("commands.admin.island.tp.success", Component.text(target.getName())));
                });
                return true;
            }

            case "delete" -> {
                if (args.length < 2) {
                    sender.sendMessage(Component.translatable("commands.admin.island.delete.usage"));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(Component.translatable("commands.admin.player-not-found"));
                    return true;
                }

                islandManager.getPlayerIsland(target.getUniqueId().toString(), target.getName()).thenAccept(island -> {
                    if (island == null) {
                        sender.sendMessage(Component.translatable("commands.admin.island.player-no-island", Component.text(target.getName())));
                        return;
                    }

                    // 확인 메시지
                    if (args.length < 3 || !args[2].equalsIgnoreCase("confirm")) {
                        sender.sendMessage(Component.translatable("commands.admin.island.delete.confirm", Component.text(target.getName())));
                        return;
                    }

                    islandManager.deleteIsland(island.getId());
                    sender.sendMessage(Component.translatable("commands.admin.island.delete.success", Component.text(target.getName())));
                });
                return true;
            }

            case "reset" -> {
                if (args.length < 2) {
                    sender.sendMessage(Component.translatable("commands.admin.island.reset.usage"));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(Component.translatable("commands.admin.player-not-found"));
                    return true;
                }

                islandManager.getPlayerIsland(target.getUniqueId().toString(), target.getName()).thenAccept(island -> {
                    if (island == null) {
                        sender.sendMessage(Component.translatable("commands.admin.island.player-no-island", Component.text(target.getName())));
                        return;
                    }

                    // 확인 메시지
                    if (args.length < 3 || !args[2].equalsIgnoreCase("confirm")) {
                        sender.sendMessage(Component.translatable("commands.admin.island.reset.confirm", Component.text(target.getName())));
                        return;
                    }

                    islandManager.resetIsland(island.getId());
                    sender.sendMessage(Component.translatable("commands.admin.island.reset.success", Component.text(target.getName())));
                });
                return true;
            }

            case "setowner" -> {
                if (args.length < 3) {
                    sender.sendMessage(Component.translatable("commands.admin.island.setowner.usage"));
                    return true;
                }

                Player oldOwner = Bukkit.getPlayer(args[1]);
                if (oldOwner == null) {
                    sender.sendMessage(Component.translatable("commands.admin.player-not-found"));
                    return true;
                }

                final Player newOwner = Bukkit.getPlayer(args[2]);
                if (newOwner == null) {
                    sender.sendMessage(Component.translatable("commands.admin.player-not-found"));
                    return true;
                }

                islandManager.getPlayerIsland(oldOwner.getUniqueId().toString(), oldOwner.getName())
                        .thenAccept(island -> {
                            if (island == null) {
                                sender.sendMessage(Component.translatable("commands.admin.island.player-no-island", Component.text(oldOwner.getName())));
                                return;
                            }

                            // Island의 소유자 변경 (IslandDTO를 새로 생성)
                            IslandDTO currentData = island.getData();
                            IslandCoreDTO updatedCore = new IslandCoreDTO(currentData.core()
                                    .islandId(), newOwner.getUniqueId()
                                    .toString(), newOwner.getName(), currentData.core().islandName(), currentData.core()
                                    .size(), currentData.core().isPublic(), currentData.core()
                                    .createdAt(), System.currentTimeMillis(), currentData.core()
                                    .totalResets(), currentData.core().deletionScheduledAt(), currentData.core()
                                    .location());
                            IslandDTO updatedData = new IslandDTO(updatedCore, currentData.membership(), currentData.social(), currentData.configuration());
                            islandManager.updateIsland(updatedData);
                            sender.sendMessage(Component.translatable("commands.admin.island.setowner.success", Component.text(oldOwner.getName()), Component.text(newOwner.getName())));
                        });
                return true;
            }

            default -> {
                sender.sendMessage(Component.translatable("commands.admin.island.usage"));
                return false;
            }
        }
    }

    private void showIslandInfo(@NotNull CommandSender sender, @NotNull Island island) {
        IslandDTO dto = island.getData();

        sender.sendMessage(Component.translatable("commands.admin.island.info.title"));
        sender.sendMessage(Component.translatable("commands.admin.island.info.id", Component.text(dto.core()
                .islandId())));
        sender.sendMessage(Component.translatable("commands.admin.island.info.name", Component.text(dto.core()
                .islandName())));
        sender.sendMessage(Component.translatable("commands.admin.island.info.owner", Component.text(dto.core()
                .ownerName())));
        sender.sendMessage(Component.translatable("commands.admin.island.info.members", Component.text(String.valueOf(dto.membership()
                .members().size()))));
        sender.sendMessage(Component.translatable("commands.admin.island.info.size", Component.text(String.valueOf(dto.core()
                .size()))));
        sender.sendMessage(Component.translatable("commands.admin.island.info.public", Component.text(dto.core()
                .isPublic() ? "공개" : "비공개")));
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return Stream.of("info", "tp", "delete", "reset", "setowner")
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
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