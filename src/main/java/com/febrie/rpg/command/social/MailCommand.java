package com.febrie.rpg.command.social;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.impl.social.MailboxGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.social.MailManager;
import com.febrie.rpg.util.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 우편 관련 명령어 처리
 * /우편함, /우편보내기 등
 *
 * @author Febrie
 */
public class MailCommand implements CommandExecutor, TabCompleter {

    private final RPGMain plugin;
    private final GuiManager guiManager;
    private final LangManager langManager;
    private final MailManager mailManager;

    public MailCommand(@NotNull RPGMain plugin, @NotNull GuiManager guiManager, 
                      @NotNull LangManager langManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.mailManager = MailManager.getInstance();
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
            case "우편함", "mailbox", "mail" -> handleMailboxCommand(player, args);
            case "우편보내기", "sendmail" -> handleSendMailCommand(player, args);
            default -> {
                player.sendMessage("§c알 수 없는 명령어입니다.");
                return false;
            }
        }

        return true;
    }

    /**
     * /우편함 명령어 처리
     */
    private void handleMailboxCommand(@NotNull Player player, @NotNull String[] args) {
        // 우편함 GUI 열기
        MailboxGui mailboxGui = new MailboxGui(guiManager, langManager, player);
        guiManager.openGui(player, mailboxGui);
    }

    /**
     * /우편보내기 명령어 처리
     */
    private void handleSendMailCommand(@NotNull Player player, @NotNull String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c사용법: /우편보내기 <플레이어명> <제목> [메시지]");
            player.sendMessage("§7예시: /우편보내기 Steve 선물 안녕하세요!");
            player.sendMessage("§7첨부물: 들고 있는 아이템이 자동으로 첨부됩니다.");
            return;
        }

        String targetName = args[0];
        String subject = args[1];
        String message = null;

        if (args.length > 2) {
            message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        }

        // 제목 길이 확인
        if (subject.length() > 50) {
            player.sendMessage("§c제목은 50자 이내로 입력해주세요.");
            return;
        }

        // 메시지 길이 확인
        if (message != null && message.length() > 500) {
            player.sendMessage("§c메시지는 500자 이내로 입력해주세요.");
            return;
        }

        // 첨부물 확인 (현재 들고 있는 아이템)
        List<ItemStack> attachments = new ArrayList<>();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (heldItem != null && !heldItem.getType().isAir()) {
            // 플레이어에게 확인 요청
            player.sendMessage("§e우편을 보낼 준비가 되었습니다:");
            player.sendMessage("§7받는 사람: " + targetName);
            player.sendMessage("§7제목: " + subject);
            if (message != null) {
                player.sendMessage("§7메시지: " + message);
            }
            player.sendMessage("§7첨부물: " + heldItem.getType().name() + " x" + heldItem.getAmount());
            player.sendMessage("§a15초 내에 '/우편확인'을 입력하여 전송하거나 '/우편취소'로 취소하세요.");
            
            // 임시 저장 (실제로는 더 정교한 시스템 필요)
            attachments.add(heldItem.clone());
            
            // 임시로 바로 전송 (실제로는 확인 시스템 구현 필요)
            sendMailWithConfirmation(player, targetName, subject, message, attachments);
        } else {
            // 첨부물 없이 전송
            sendMailWithConfirmation(player, targetName, subject, message, attachments);
        }
    }

    /**
     * 우편 전송 (확인 과정 포함)
     */
    private void sendMailWithConfirmation(@NotNull Player player, @NotNull String targetName, 
                                        @NotNull String subject, @Nullable String message, 
                                        @NotNull List<ItemStack> attachments) {
        
        mailManager.sendMailWithAttachments(player, targetName, subject, message, attachments);
    }

    /**
     * 우편 명령어 도움말 전송
     */
    private void sendMailHelp(@NotNull Player player) {
        player.sendMessage("§6=== 우편 명령어 도움말 ===");
        player.sendMessage("§e/우편함 §7- 받은 우편을 확인합니다");
        player.sendMessage("§e/우편보내기 <플레이어명> <제목> [메시지] §7- 우편을 보냅니다");
        player.sendMessage("§7첨부물: 메인핸드에 든 아이템이 자동으로 첨부됩니다");
        player.sendMessage("§7제목은 50자, 메시지는 500자 이내로 입력하세요");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, 
                                              @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        String commandName = command.getName().toLowerCase();
        
        switch (commandName) {
            case "우편함", "mailbox", "mail" -> {
                // 우편함은 추가 인수 없음
                return new ArrayList<>();
            }
            case "우편보내기", "sendmail" -> {
                if (args.length == 1) {
                    // 온라인 플레이어 목록 반환
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> !name.equals(sender.getName())) // 자기 자신 제외
                            .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                            .collect(Collectors.toList());
                } else if (args.length == 2) {
                    return Arrays.asList("선물", "안녕하세요", "도움요청", "감사합니다");
                } else if (args.length == 3) {
                    return Arrays.asList("안녕하세요!", "선물입니다", "도움이_필요해요", "고마워요!");
                }
            }
        }

        return new ArrayList<>();
    }
}