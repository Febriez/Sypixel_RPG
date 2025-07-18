package com.febrie.rpg.island.command;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.island.gui.IslandMainGui;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * 섬 명령어 처리
 * /섬 또는 /island
 *
 * @author Febrie, CoffeeTory
 */
public class IslandCommand implements CommandExecutor {
    
    private final RPGMain plugin;
    private final IslandManager islandManager;
    
    public IslandCommand(@NotNull RPGMain plugin, @NotNull IslandManager islandManager) {
        this.plugin = plugin;
        this.islandManager = islandManager;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                           @NotNull String label, @NotNull String[] args) {
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtil.colorize("&c이 명령어는 플레이어만 사용할 수 있습니다."));
            return true;
        }
        
        // 인수가 없으면 GUI 열기
        if (args.length == 0) {
            openIslandGui(player);
            return true;
        }
        
        // 명령어 처리
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "생성", "create" -> handleCreateCommand(player, args);
            case "삭제", "delete" -> handleDeleteCommand(player);
            case "초기화", "reset" -> handleResetCommand(player);
            case "도움말", "help" -> showHelp(player);
            default -> {
                player.sendMessage(ColorUtil.colorize("&c알 수 없는 명령어입니다. /섬 도움말"));
            }
        }
        
        return true;
    }
    
    /**
     * 섬 GUI 열기
     */
    private void openIslandGui(@NotNull Player player) {
        String playerUuid = player.getUniqueId().toString();
        
        islandManager.getPlayerIsland(playerUuid).thenAccept(island -> {
            if (island == null) {
                // 섬이 없는 경우 생성 안내
                showNoIslandMessage(player);
            } else {
                // 섬 메인 GUI 열기
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    IslandMainGui gui = new IslandMainGui(plugin, islandManager, island, player);
                    gui.open();
                });
            }
        });
    }
    
    /**
     * 섬 생성 명령어 처리
     */
    private void handleCreateCommand(@NotNull Player player, @NotNull String[] args) {
        if (args.length < 2) {
            player.sendMessage(ColorUtil.colorize("&c사용법: /섬 생성 <섬이름>"));
            return;
        }
        
        // 섬 이름 조합
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) nameBuilder.append(" ");
            nameBuilder.append(args[i]);
        }
        String islandName = nameBuilder.toString();
        
        // 섬 이름 유효성 검사
        if (islandName.length() < 2 || islandName.length() > 16) {
            player.sendMessage(ColorUtil.colorize("&c섬 이름은 2-16자여야 합니다."));
            return;
        }
        
        player.sendMessage(ColorUtil.colorize("&e섬을 생성하는 중..."));
        
        islandManager.createIsland(player, islandName).thenAccept(island -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (island != null) {
                    sendIslandCreatedMessage(player, island);
                } else {
                    player.sendMessage(ColorUtil.colorize("&c섬 생성에 실패했습니다. 이미 섬을 소유하고 있을 수 있습니다."));
                }
            });
        });
    }
    
    /**
     * 섬 삭제 명령어 처리
     */
    private void handleDeleteCommand(@NotNull Player player) {
        String playerUuid = player.getUniqueId().toString();
        
        islandManager.getPlayerIsland(playerUuid).thenAccept(island -> {
            if (island == null) {
                player.sendMessage(ColorUtil.colorize("&c소유한 섬이 없습니다."));
                return;
            }
            
            if (!island.ownerUuid().equals(playerUuid)) {
                player.sendMessage(ColorUtil.colorize("&c섬장만 섬을 삭제할 수 있습니다."));
                return;
            }
            
            if (!island.canDelete()) {
                player.sendMessage(ColorUtil.colorize("&c섬 생성 후 1주일이 지나야 삭제할 수 있습니다."));
                return;
            }
            
            // 확인 메시지
            sendDeleteConfirmation(player, island);
        });
    }
    
    /**
     * 섬 초기화 명령어 처리
     */
    private void handleResetCommand(@NotNull Player player) {
        String playerUuid = player.getUniqueId().toString();
        
        islandManager.getPlayerIsland(playerUuid).thenAccept(island -> {
            if (island == null) {
                player.sendMessage(ColorUtil.colorize("&c소유한 섬이 없습니다."));
                return;
            }
            
            if (!island.ownerUuid().equals(playerUuid)) {
                player.sendMessage(ColorUtil.colorize("&c섬장만 섬을 초기화할 수 있습니다."));
                return;
            }
            
            // 초기화 가능 여부 확인
            islandManager.loadPlayerIslandData(playerUuid).thenAccept(playerData -> {
                if (playerData == null || !playerData.canResetIsland()) {
                    player.sendMessage(ColorUtil.colorize("&c섬 초기화는 평생 1번만 가능합니다. 이미 사용하셨습니다."));
                    return;
                }
                
                // 확인 메시지
                sendResetConfirmation(player, island);
            });
        });
    }
    
    /**
     * 섬이 없을 때 메시지
     */
    private void showNoIslandMessage(@NotNull Player player) {
        Component message = Component.text()
                .append(Component.text("====== ", NamedTextColor.GRAY))
                .append(Component.text("섬 시스템", NamedTextColor.AQUA, TextDecoration.BOLD))
                .append(Component.text(" ======\n\n", NamedTextColor.GRAY))
                .append(Component.text("아직 섬을 소유하고 있지 않습니다.\n", NamedTextColor.YELLOW))
                .append(Component.text("섬을 생성하려면 ", NamedTextColor.WHITE))
                .append(Component.text("/섬 생성 <섬이름>", NamedTextColor.AQUA)
                        .clickEvent(ClickEvent.suggestCommand("/섬 생성 ")))
                .append(Component.text(" 명령어를 사용하세요.\n\n", NamedTextColor.WHITE))
                .append(Component.text("예시: /섬 생성 나의섬\n", NamedTextColor.GRAY))
                .append(Component.text("=========================", NamedTextColor.GRAY))
                .build();
        
        player.sendMessage(message);
    }
    
    /**
     * 섬 생성 완료 메시지
     */
    private void sendIslandCreatedMessage(@NotNull Player player, @NotNull IslandDTO island) {
        Component message = Component.text()
                .append(Component.text("====== ", NamedTextColor.GRAY))
                .append(Component.text("섬 생성 완료", NamedTextColor.GREEN, TextDecoration.BOLD))
                .append(Component.text(" ======\n\n", NamedTextColor.GRAY))
                .append(Component.text("✨ 섬 이름: ", NamedTextColor.WHITE))
                .append(Component.text(island.islandName() + "\n", NamedTextColor.AQUA))
                .append(Component.text("📏 섬 크기: ", NamedTextColor.WHITE))
                .append(Component.text(island.size() + " x " + island.size() + "\n", NamedTextColor.YELLOW))
                .append(Component.text("👥 최대 인원: ", NamedTextColor.WHITE))
                .append(Component.text(island.upgradeData().memberLimit() + "명\n\n", NamedTextColor.GREEN))
                .append(Component.text("/섬", NamedTextColor.AQUA)
                        .clickEvent(ClickEvent.runCommand("/섬")))
                .append(Component.text(" 명령어로 섬을 관리하세요!\n", NamedTextColor.WHITE))
                .append(Component.text("=========================", NamedTextColor.GRAY))
                .build();
        
        player.sendMessage(message);
    }
    
    /**
     * 섬 삭제 확인 메시지
     */
    private void sendDeleteConfirmation(@NotNull Player player, @NotNull IslandDTO island) {
        Component message = Component.text()
                .append(Component.text("====== ", NamedTextColor.GRAY))
                .append(Component.text("섬 삭제 확인", NamedTextColor.RED, TextDecoration.BOLD))
                .append(Component.text(" ======\n\n", NamedTextColor.GRAY))
                .append(Component.text("⚠️ 경고: ", NamedTextColor.RED, TextDecoration.BOLD))
                .append(Component.text("이 작업은 되돌릴 수 없습니다!\n\n", NamedTextColor.WHITE))
                .append(Component.text("섬 이름: ", NamedTextColor.WHITE))
                .append(Component.text(island.islandName() + "\n", NamedTextColor.YELLOW))
                .append(Component.text("섬원 수: ", NamedTextColor.WHITE))
                .append(Component.text(island.getMemberCount() + "명\n\n", NamedTextColor.YELLOW))
                .append(Component.text("정말로 삭제하시겠습니까?\n\n", NamedTextColor.WHITE))
                .append(Component.text("[삭제 확인]", NamedTextColor.RED, TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/섬삭제확인 " + island.islandId())))
                .append(Component.text("   "))
                .append(Component.text("[취소]", NamedTextColor.GREEN, TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/섬")))
                .append(Component.text("\n=========================", NamedTextColor.GRAY))
                .build();
        
        player.sendMessage(message);
    }
    
    /**
     * 섬 초기화 확인 메시지
     */
    private void sendResetConfirmation(@NotNull Player player, @NotNull IslandDTO island) {
        Component message = Component.text()
                .append(Component.text("====== ", NamedTextColor.GRAY))
                .append(Component.text("섬 초기화 확인", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text(" ======\n\n", NamedTextColor.GRAY))
                .append(Component.text("⚠️ 주의: ", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text("섬 초기화는 평생 1번만 가능합니다!\n\n", NamedTextColor.WHITE))
                .append(Component.text("초기화 시:\n", NamedTextColor.YELLOW))
                .append(Component.text("• 모든 건축물이 사라집니다\n", NamedTextColor.GRAY))
                .append(Component.text("• 모든 섬원이 추방됩니다\n", NamedTextColor.GRAY))
                .append(Component.text("• 모든 업그레이드가 초기화됩니다\n", NamedTextColor.GRAY))
                .append(Component.text("• 섬 크기가 85x85로 돌아갑니다\n\n", NamedTextColor.GRAY))
                .append(Component.text("정말로 초기화하시겠습니까?\n\n", NamedTextColor.WHITE))
                .append(Component.text("[초기화 확인]", NamedTextColor.GOLD, TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/섬초기화확인 " + island.islandId())))
                .append(Component.text("   "))
                .append(Component.text("[취소]", NamedTextColor.GREEN, TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/섬")))
                .append(Component.text("\n=========================", NamedTextColor.GRAY))
                .build();
        
        player.sendMessage(message);
    }
    
    /**
     * 도움말 표시
     */
    private void showHelp(@NotNull Player player) {
        Component message = Component.text()
                .append(Component.text("====== ", NamedTextColor.GRAY))
                .append(Component.text("섬 도움말", NamedTextColor.AQUA, TextDecoration.BOLD))
                .append(Component.text(" ======\n\n", NamedTextColor.GRAY))
                .append(Component.text("/섬", NamedTextColor.AQUA))
                .append(Component.text(" - 섬 관리 GUI 열기\n", NamedTextColor.WHITE))
                .append(Component.text("/섬 생성 <이름>", NamedTextColor.AQUA))
                .append(Component.text(" - 새 섬 생성\n", NamedTextColor.WHITE))
                .append(Component.text("/섬 삭제", NamedTextColor.AQUA))
                .append(Component.text(" - 섬 삭제 (1주일 후 가능)\n", NamedTextColor.WHITE))
                .append(Component.text("/섬 초기화", NamedTextColor.AQUA))
                .append(Component.text(" - 섬 초기화 (평생 1회)\n", NamedTextColor.WHITE))
                .append(Component.text("/섬 도움말", NamedTextColor.AQUA))
                .append(Component.text(" - 이 도움말 표시\n", NamedTextColor.WHITE))
                .append(Component.text("\n=========================", NamedTextColor.GRAY))
                .build();
        
        player.sendMessage(message);
    }
}