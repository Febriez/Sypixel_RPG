package com.febrie.rpg.command.social;

import net.kyori.adventure.text.Component;
import com.febrie.rpg.RPGMain;
import com.febrie.rpg.social.WhisperManager;
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
 * 귓말 관련 명령어 처리
 * /귓말, /w, /whisper, /r, /reply 등
 *
 * @author Febrie
 */
public class WhisperCommand implements CommandExecutor, TabCompleter {

    private final RPGMain plugin;
    private final WhisperManager whisperManager;

    public WhisperCommand(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        this.whisperManager = WhisperManager.getInstance();
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
            case "귓말", "w", "whisper", "tell", "msg" -> handleWhisperCommand(player, args);
            case "r", "reply", "답장" -> handleReplyCommand(player, args);
            default -> {
                player.sendMessage("§c알 수 없는 명령어입니다.");
                return false;
            }
        }

        return true;
    }

    /**
     * /귓말 명령어 처리
     */
    private void handleWhisperCommand(@NotNull Player player, @NotNull String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c사용법: /귓말 <플레이어명> <메시지>");
            player.sendMessage("§7예시: /귓말 Steve 안녕하세요!");
            return;
        }

        String targetName = args[0];
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        // 메시지가 너무 길면 제한
        if (message.length() > 256) {
            player.sendMessage("§c메시지가 너무 깁니다. 256자 이내로 입력해주세요.");
            return;
        }

        // 빈 메시지 방지
        if (message.trim().isEmpty()) {
            player.sendMessage("§c빈 메시지는 보낼 수 없습니다.");
            return;
        }

        // 귓말 전송
        whisperManager.sendWhisper(player, targetName, message);
    }

    /**
     * /r 명령어 처리 (답장)
     */
    private void handleReplyCommand(@NotNull Player player, @NotNull String[] args) {
        if (args.length < 1) {
            String lastTarget = whisperManager.getLastWhisperTarget(player.getUniqueId());
            if (lastTarget != null) {
                player.sendMessage("§e마지막 귓말 대상: " + lastTarget);
                player.sendMessage("§c사용법: /r <메시지>");
            } else {
                player.sendMessage("§c답장할 대상이 없습니다.");
                player.sendMessage("§7먼저 누군가와 귓말을 주고받아야 합니다.");
            }
            return;
        }

        String message = String.join(" ", args);

        // 메시지가 너무 길면 제한
        if (message.length() > 256) {
            player.sendMessage("§c메시지가 너무 깁니다. 256자 이내로 입력해주세요.");
            return;
        }

        // 빈 메시지 방지
        if (message.trim().isEmpty()) {
            player.sendMessage("§c빈 메시지는 보낼 수 없습니다.");
            return;
        }

        // 답장 전송
        whisperManager.replyToLastWhisper(player, message);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, 
                                              @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        String commandName = command.getName().toLowerCase();
        
        switch (commandName) {
            case "귓말", "w", "whisper", "tell", "msg" -> {
                if (args.length == 1) {
                    // 온라인 플레이어 목록 반환
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> !name.equals(sender.getName())) // 자기 자신 제외
                            .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                            .collect(Collectors.toList());
                } else if (args.length == 2) {
                    return Arrays.asList("안녕하세요!", "ㅎㅇ", "뭐해?", "같이할래?");
                }
            }
            case "r", "reply", "답장" -> {
                if (args.length == 1) {
                    return Arrays.asList("넵", "ㅇㅋ", "알겠어요", "고마워요");
                }
            }
        }

        return new ArrayList<>();
    }
}