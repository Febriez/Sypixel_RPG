package com.febrie.rpg.command.social;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.impl.social.FriendListGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.social.FriendManager;
import com.febrie.rpg.util.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private final LangManager langManager;
    private final FriendManager friendManager;

    public FriendCommand(@NotNull RPGMain plugin, @NotNull GuiManager guiManager, 
                        @NotNull LangManager langManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.friendManager = FriendManager.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                           @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        String commandName = command.getName().toLowerCase();
        
        switch (commandName) {
            case "친구", "friend" -> handleFriendCommand(player, args);
            case "친구추가", "friendadd" -> handleFriendAddCommand(player, args);
            case "친구삭제", "friendremove" -> handleFriendRemoveCommand(player, args);
            default -> {
                player.sendMessage("§c알 수 없는 명령어입니다.");
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
            FriendListGui friendListGui = FriendListGui.create(guiManager, langManager, player);
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
            player.sendMessage("§c사용법: /친구추가 <플레이어명> [메시지]");
            player.sendMessage("§7예시: /친구추가 Steve 안녕하세요!");
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
            player.sendMessage("§c사용법: /친구삭제 <플레이어명>");
            return;
        }

        String targetName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetName);
        
        if (targetPlayer == null) {
            player.sendMessage("§c해당 플레이어를 찾을 수 없습니다: " + targetName);
            return;
        }

        // 친구인지 확인 후 삭제
        friendManager.areFriends(player.getUniqueId(), targetPlayer.getUniqueId()).thenAccept(areFriends -> {
            if (!areFriends) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.sendMessage("§c" + targetName + "님과는 친구가 아닙니다.");
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
        player.sendMessage("§6=== 친구 명령어 도움말 ===");
        player.sendMessage("§e/친구 목록 §7- 친구 목록 GUI를 엽니다");
        player.sendMessage("§e/친구추가 <플레이어명> [메시지] §7- 친구 요청을 보냅니다");
        player.sendMessage("§e/친구삭제 <플레이어명> §7- 친구를 삭제합니다");
        player.sendMessage("§7친구 요청 수락/거절은 GUI에서 할 수 있습니다.");
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
                    // TODO: 실제 친구 목록에서 가져오기 (현재는 온라인 플레이어로 대체)
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> !name.equals(sender.getName()))
                            .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                            .collect(Collectors.toList());
                }
            }
        }

        return new ArrayList<>();
    }
}