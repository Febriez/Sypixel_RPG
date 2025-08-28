package com.febrie.rpg.command.social;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.impl.social.FriendListGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.social.FriendManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.kyori.adventure.text.Component;
import com.febrie.rpg.util.UnifiedColorUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 친구 관련 명령어 처리
 * /친구, /친구추가, /친구삭제 등
 *
 * @author Febrie
 */
public class FriendCommand implements CommandExecutor, TabCompleter {

    private final RPGMain plugin;
    private final GuiManager guiManager;
    private final FriendManager friendManager;

    public FriendCommand(@NotNull RPGMain plugin, @NotNull GuiManager guiManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
        this.friendManager = FriendManager.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                           @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.translatable("commands.player-only").color(UnifiedColorUtil.ERROR));
            return true;
        }

        String commandName = command.getName().toLowerCase();
        
        switch (commandName) {
            case "친구", "friend" -> handleFriendCommand(player, args);
            case "친구추가", "friendadd" -> handleFriendAddCommand(player, args);
            case "친구삭제", "friendremove" -> handleFriendRemoveCommand(player, args);
            default -> {
                player.sendMessage(Component.translatable("commands.unknown").color(UnifiedColorUtil.ERROR));
                return false;
            }
        }

        return true;
    }

    /**
     * /친구 명령어 처리
     */
    private void handleFriendCommand(@NotNull Player player, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("목록") || args[0].equalsIgnoreCase("list")) {
            // 친구 목록 GUI 열기
            FriendListGui friendListGui = FriendListGui.create(guiManager, player);
            guiManager.openGui(player, friendListGui);
        } else {
            sendFriendHelp(player);
        }
    }

    /**
     * /친구추가 명령어 처리
     */
    private void handleFriendAddCommand(@NotNull Player player, @NotNull String[] args) {
        if (args.length < 1) {
            player.sendMessage(Component.translatable("friend.commands.add.usage").color(UnifiedColorUtil.ERROR));
            player.sendMessage(Component.translatable("friend.commands.add.example").color(UnifiedColorUtil.GRAY));
            return;
        }

        String targetName = args[0];
        String message = null;

        if (args.length > 1) {
            message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        }

        // 친구 요청 전송
        friendManager.sendFriendRequest(player, targetName, message);
    }

    /**
     * /친구삭제 명령어 처리
     */
    private void handleFriendRemoveCommand(@NotNull Player player, @NotNull String[] args) {
        if (args.length < 1) {
            player.sendMessage(Component.translatable("friend.commands.remove.usage").color(UnifiedColorUtil.ERROR));
            return;
        }

        String targetName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetName);
        
        if (targetPlayer == null) {
            player.sendMessage(Component.translatable("friend.player-not-found", Component.text(targetName)).color(UnifiedColorUtil.ERROR));
            return;
        }

        // 친구인지 확인 후 삭제
        friendManager.areFriends(player.getUniqueId(), targetPlayer.getUniqueId()).thenAccept(areFriends -> {
            if (!areFriends) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.sendMessage(Component.translatable("friend.not-friends", Component.text(targetName)).color(UnifiedColorUtil.ERROR));
                });
                return;
            }

            friendManager.removeFriend(player, targetPlayer.getUniqueId());
        });
    }

    /**
     * 친구 명령어 도움말 전송
     */
    private void sendFriendHelp(@NotNull Player player) {
        player.sendMessage(Component.translatable("friend.commands.help.title").color(UnifiedColorUtil.GOLD));
        player.sendMessage(Component.translatable("friend.commands.help.list").color(UnifiedColorUtil.YELLOW));
        player.sendMessage(Component.translatable("friend.commands.help.add").color(UnifiedColorUtil.YELLOW));
        player.sendMessage(Component.translatable("friend.commands.help.remove").color(UnifiedColorUtil.YELLOW));
        player.sendMessage(Component.translatable("friend.commands.help.gui-note").color(UnifiedColorUtil.GRAY));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, 
                                              @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        String commandName = command.getName().toLowerCase();
        
        switch (commandName) {
            case "친구", "friend" -> {
                if (args.length == 1) {
                    return Arrays.asList("목록", "list").stream()
                            .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                            .collect(Collectors.toList());
                }
            }
            case "친구추가", "friendadd" -> {
                if (args.length == 1) {
                    // 온라인 플레이어 목록 반환
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> !name.equals(sender.getName())) // 자기 자신 제외
                            .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                            .collect(Collectors.toList());
                } else if (args.length == 2) {
                    return Arrays.asList("안녕하세요!", "친구해요!", "같이 게임해요!");
                }
            }
            case "친구삭제", "friendremove" -> {
                if (args.length == 1) {
                    // 플레이어의 친구 목록에서 가져오기
                    Player player = (Player) sender;
                    List<String> friendNames = new ArrayList<>();
                    
                    // 비동기로 친구 목록을 가져오되, 탭 완성을 위해 캐시된 데이터 사용
                    friendManager.getFriends(player.getUniqueId()).thenAccept(friends -> {
                        for (com.febrie.rpg.dto.social.FriendshipDTO friend : friends) {
                            // 친구의 이름 가져오기
                            UUID friendUuid = friend.getFriendUuid(player.getUniqueId());
                            if (friendUuid != null) {
                                Player friendPlayer = Bukkit.getPlayer(friendUuid);
                                if (friendPlayer != null) {
                                    friendNames.add(friendPlayer.getName());
                                } else {
                                    // 오프라인 플레이어의 경우 FriendshipDTO에서 이름 가져오기
                                    String friendName = friend.getFriendName(player.getUniqueId());
                                    if (friendName != null && !friendName.isEmpty()) {
                                        friendNames.add(friendName);
                                    }
                                }
                            }
                        }
                    });
                    
                    // 일단 온라인 친구들만 즉시 반환 (비동기 처리 때문)
                    return Bukkit.getOnlinePlayers().stream()
                            .filter(p -> {
                                // 친구인지 확인 - 동기적으로 확인 불가하므로 일단 false
                                return false;
                            })
                            .map(Player::getName)
                            .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                            .collect(Collectors.toList());
                }
            }
        }

        return new ArrayList<>();
    }
}