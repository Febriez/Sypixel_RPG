package com.febrie.rpg.island.command;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandInviteDTO;
import com.febrie.rpg.dto.island.IslandLocationDTO;
import com.febrie.rpg.dto.island.IslandMemberDTO;
import com.febrie.rpg.dto.island.IslandRole;
import com.febrie.rpg.dto.island.IslandSpawnDTO;
import com.febrie.rpg.dto.island.IslandSpawnPointDTO;
import com.febrie.rpg.dto.island.PlayerIslandDataDTO;
import com.febrie.rpg.island.gui.IslandMainGui;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.island.permission.IslandPermissionHandler;
import com.febrie.rpg.util.ColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
            case "초대", "invite" -> handleInviteCommand(player, args);
            case "수락", "accept" -> handleAcceptCommand(player, args);
            case "거절", "reject" -> handleRejectCommand(player, args);
            case "추방", "kick" -> handleKickCommand(player, args);
            case "탈퇴", "leave" -> handleLeaveCommand(player);
            case "홈", "home", "이동", "tp" -> handleHomeCommand(player);
            case "스폰설정", "setspawn" -> handleSetSpawnCommand(player, args);
            case "기여", "contribute", "donation" -> handleContributeCommand(player, args);
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
     * 초대 명령어 처리
     */
    private void handleInviteCommand(@NotNull Player player, @NotNull String[] args) {
        if (args.length < 2) {
            player.sendMessage(ColorUtil.colorize("&c사용법: /섬 초대 <플레이어명>"));
            return;
        }
        
        String targetName = args[1];
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            player.sendMessage(ColorUtil.colorize("&c해당 플레이어를 찾을 수 없습니다."));
            return;
        }
        
        if (target.equals(player)) {
            player.sendMessage(ColorUtil.colorize("&c자기 자신을 초대할 수 없습니다."));
            return;
        }
        
        String playerUuid = player.getUniqueId().toString();
        
        islandManager.getPlayerIsland(playerUuid).thenAccept(island -> {
            if (island == null) {
                player.sendMessage(ColorUtil.colorize("&c섬을 소유하고 있지 않습니다."));
                return;
            }
            
            // 권한 확인
            if (!IslandPermissionHandler.hasPermission(island, player, "INVITE_MEMBERS")) {
                player.sendMessage(ColorUtil.colorize("&c멤버를 초대할 권한이 없습니다."));
                return;
            }
            
            // 이미 멤버인지 확인
            if (IslandPermissionHandler.isMember(island, target.getUniqueId().toString())) {
                player.sendMessage(ColorUtil.colorize("&c이미 섬의 멤버입니다."));
                return;
            }
            
            // 멤버 제한 확인
            if (island.members().size() >= island.upgradeData().memberLimit()) {
                player.sendMessage(ColorUtil.colorize("&c섬원 수가 최대치에 도달했습니다. (" + 
                        (island.members().size() + 1) + "/" + (island.upgradeData().memberLimit() + 1) + ")"));
                return;
            }
            
            // 이미 초대장이 있는지 확인
            boolean hasInvite = island.pendingInvites().stream()
                    .anyMatch(invite -> invite.targetUuid().equals(target.getUniqueId().toString()) && 
                                      !invite.isExpired());
            
            if (hasInvite) {
                player.sendMessage(ColorUtil.colorize("&c이미 초대장을 보냈습니다."));
                return;
            }
            
            // 초대장 생성
            String inviteId = UUID.randomUUID().toString();
            IslandInviteDTO invite = IslandInviteDTO.createNew(
                    inviteId,
                    target.getUniqueId().toString(),
                    target.getName(),
                    player.getUniqueId().toString(),
                    player.getName()
            );
            
            // 섬 데이터 업데이트
            List<IslandInviteDTO> invites = new ArrayList<>(island.pendingInvites());
            invites.add(invite);
            
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
                    invites,
                    island.recentVisits(),
                    island.totalResets(),
                    island.deletionScheduledAt()
            );
            
            islandManager.updateIsland(updatedIsland).thenAccept(success -> {
                if (success) {
                    // 초대 메시지 전송
                    player.sendMessage(ColorUtil.colorize("&a" + target.getName() + "님에게 초대장을 보냈습니다!"));
                    
                    // 대상 플레이어에게 초대 메시지
                    sendInviteMessage(target, player.getName(), island.islandName(), island.islandId());
                } else {
                    player.sendMessage(ColorUtil.colorize("&c초대장 전송에 실패했습니다."));
                }
            });
        });
    }
    
    /**
     * 초대 수락 명령어 처리
     */
    private void handleAcceptCommand(@NotNull Player player, @NotNull String[] args) {
        if (args.length < 2) {
            player.sendMessage(ColorUtil.colorize("&c사용법: /섬 수락 <섬ID>"));
            return;
        }
        
        String islandId = args[1];
        String playerUuid = player.getUniqueId().toString();
        
        islandManager.loadIsland(islandId).thenAccept(island -> {
            if (island == null) {
                player.sendMessage(ColorUtil.colorize("&c섬을 찾을 수 없습니다."));
                return;
            }
            
            // 초대장 확인
            IslandInviteDTO invite = island.pendingInvites().stream()
                    .filter(inv -> inv.targetUuid().equals(playerUuid) && !inv.isExpired())
                    .findFirst()
                    .orElse(null);
            
            if (invite == null) {
                player.sendMessage(ColorUtil.colorize("&c유효한 초대장이 없습니다."));
                return;
            }
            
            // 이미 다른 섬에 속해있는지 확인
            islandManager.getPlayerIsland(playerUuid).thenAccept(currentIsland -> {
                if (currentIsland != null) {
                    player.sendMessage(ColorUtil.colorize("&c이미 다른 섬에 속해 있습니다. 먼저 탈퇴해주세요."));
                    return;
                }
                
                // 멤버 추가
                List<IslandMemberDTO> members = new ArrayList<>(island.members());
                members.add(IslandMemberDTO.createNew(
                        playerUuid,
                        player.getName(),
                        false // 일반 멤버는 부섬장이 아님
                ));
                
                // 초대장 제거
                List<IslandInviteDTO> invites = new ArrayList<>(island.pendingInvites());
                invites.removeIf(inv -> inv.targetUuid().equals(playerUuid));
                
                // 섬 데이터 업데이트
                IslandDTO updatedIsland = new IslandDTO(
                        island.islandId(),
                        island.ownerUuid(),
                        island.ownerName(),
                        island.islandName(),
                        island.size(),
                        island.isPublic(),
                        island.createdAt(),
                        System.currentTimeMillis(),
                        members,
                        island.workers(),
                        island.contributions(),
                        island.spawnData(),
                        island.upgradeData(),
                        island.permissions(),
                        invites,
                        island.recentVisits(),
                        island.totalResets(),
                        island.deletionScheduledAt()
                );
                
                // 플레이어의 섬 데이터 업데이트
                islandManager.loadPlayerIslandData(playerUuid).thenCompose(playerData -> {
                    PlayerIslandDataDTO updatedPlayerData;
                    if (playerData == null) {
                        updatedPlayerData = PlayerIslandDataDTO.createNew(playerUuid)
                                .joinIsland(islandId, IslandRole.MEMBER);
                    } else {
                        updatedPlayerData = playerData.joinIsland(islandId, IslandRole.MEMBER);
                    }
                    
                    // 섬 데이터와 플레이어 데이터 모두 업데이트
                    return CompletableFuture.allOf(
                            islandManager.updateIsland(updatedIsland),
                            islandManager.getFirestoreService().savePlayerIslandData(updatedPlayerData)
                    ).thenApply(v -> {
                        // 캐시 업데이트
                        islandManager.updatePlayerCache(playerUuid, updatedPlayerData);
                        return true;
                    });
                }).thenAccept(success -> {
                    if (success) {
                        player.sendMessage(ColorUtil.colorize("&a" + island.islandName() + " 섬에 가입했습니다!"));
                        
                        // 섬원들에게 알림
                        notifyIslandMembers(island, player.getName() + "님이 섬에 가입했습니다!");
                    } else {
                        player.sendMessage(ColorUtil.colorize("&c섬 가입에 실패했습니다."));
                    }
                });
            });
        });
    }
    
    /**
     * 초대 거절 명령어 처리
     */
    private void handleRejectCommand(@NotNull Player player, @NotNull String[] args) {
        if (args.length < 2) {
            player.sendMessage(ColorUtil.colorize("&c사용법: /섬 거절 <섬ID>"));
            return;
        }
        
        String islandId = args[1];
        String playerUuid = player.getUniqueId().toString();
        
        islandManager.loadIsland(islandId).thenAccept(island -> {
            if (island == null) {
                player.sendMessage(ColorUtil.colorize("&c섬을 찾을 수 없습니다."));
                return;
            }
            
            // 초대장 제거
            List<IslandInviteDTO> invites = new ArrayList<>(island.pendingInvites());
            boolean removed = invites.removeIf(inv -> inv.targetUuid().equals(playerUuid));
            
            if (!removed) {
                player.sendMessage(ColorUtil.colorize("&c초대장을 찾을 수 없습니다."));
                return;
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
                    invites,
                    island.recentVisits(),
                    island.totalResets(),
                    island.deletionScheduledAt()
            );
            
            islandManager.updateIsland(updatedIsland).thenAccept(success -> {
                if (success) {
                    player.sendMessage(ColorUtil.colorize("&c섬 초대를 거절했습니다."));
                }
            });
        });
    }
    
    /**
     * 초대 메시지 전송
     */
    private void sendInviteMessage(@NotNull Player target, @NotNull String inviterName, 
                                  @NotNull String islandName, @NotNull String islandId) {
        Component message = Component.text()
                .append(Component.text("\n====== ", NamedTextColor.GRAY))
                .append(Component.text("섬 초대", NamedTextColor.AQUA, TextDecoration.BOLD))
                .append(Component.text(" ======\n\n", NamedTextColor.GRAY))
                .append(Component.text(inviterName, NamedTextColor.YELLOW))
                .append(Component.text("님이 ", NamedTextColor.WHITE))
                .append(Component.text(islandName, NamedTextColor.AQUA))
                .append(Component.text(" 섬으로 초대했습니다!\n\n", NamedTextColor.WHITE))
                .append(Component.text("초대는 5분 후 만료됩니다.\n\n", NamedTextColor.GRAY))
                .append(Component.text("[수락]", NamedTextColor.GREEN, TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/섬 수락 " + islandId)))
                .append(Component.text("   "))
                .append(Component.text("[거절]", NamedTextColor.RED, TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/섬 거절 " + islandId)))
                .append(Component.text("\n=========================\n", NamedTextColor.GRAY))
                .build();
        
        target.sendMessage(message);
    }
    
    /**
     * 섬원들에게 알림
     */
    private void notifyIslandMembers(@NotNull IslandDTO island, @NotNull String message) {
        // 섬장에게 알림
        Player owner = Bukkit.getPlayer(island.ownerUuid());
        if (owner != null) {
            owner.sendMessage(ColorUtil.colorize("&b[섬] &f" + message));
        }
        
        // 섬원들에게 알림
        for (IslandMemberDTO member : island.members()) {
            Player memberPlayer = Bukkit.getPlayer(member.uuid());
            if (memberPlayer != null) {
                memberPlayer.sendMessage(ColorUtil.colorize("&b[섬] &f" + message));
            }
        }
    }
    
    /**
     * 추방 명령어 처리
     */
    private void handleKickCommand(@NotNull Player player, @NotNull String[] args) {
        if (args.length < 2) {
            player.sendMessage(ColorUtil.colorize("&c사용법: /섬 추방 <플레이어명>"));
            return;
        }
        
        String targetName = args[1];
        String playerUuid = player.getUniqueId().toString();
        
        islandManager.getPlayerIsland(playerUuid).thenAccept(island -> {
            if (island == null) {
                player.sendMessage(ColorUtil.colorize("&c섬을 소유하고 있지 않습니다."));
                return;
            }
            
            // 권한 확인
            if (!IslandPermissionHandler.hasPermission(island, player, "KICK_MEMBERS")) {
                player.sendMessage(ColorUtil.colorize("&c멤버를 추방할 권한이 없습니다."));
                return;
            }
            
            // 추방할 멤버 찾기
            IslandMemberDTO targetMember = island.members().stream()
                    .filter(member -> member.name().equalsIgnoreCase(targetName))
                    .findFirst()
                    .orElse(null);
            
            if (targetMember == null) {
                player.sendMessage(ColorUtil.colorize("&c해당 플레이어는 섬원이 아닙니다."));
                return;
            }
            
            // 부섬장 추방은 섬장만 가능
            if (targetMember.isCoOwner() && !island.ownerUuid().equals(playerUuid)) {
                player.sendMessage(ColorUtil.colorize("&c부섬장은 섬장만 추방할 수 있습니다."));
                return;
            }
            
            // 멤버 제거
            List<IslandMemberDTO> members = new ArrayList<>(island.members());
            members.removeIf(member -> member.uuid().equals(targetMember.uuid()));
            
            // 섬 데이터 업데이트
            IslandDTO updatedIsland = new IslandDTO(
                    island.islandId(),
                    island.ownerUuid(),
                    island.ownerName(),
                    island.islandName(),
                    island.size(),
                    island.isPublic(),
                    island.createdAt(),
                    System.currentTimeMillis(),
                    members,
                    island.workers(),
                    island.contributions(),
                    island.spawnData(),
                    island.upgradeData(),
                    island.permissions(),
                    island.pendingInvites(),
                    island.recentVisits(),
                    island.totalResets(),
                    island.deletionScheduledAt()
            );
            
            // 추방당한 플레이어의 섬 데이터 업데이트
            islandManager.loadPlayerIslandData(targetMember.uuid()).thenCompose(targetData -> {
                if (targetData != null && targetData.hasIsland()) {
                    PlayerIslandDataDTO updatedTargetData = targetData.leaveIsland();
                    return islandManager.getFirestoreService().savePlayerIslandData(updatedTargetData)
                            .thenApply(saved -> {
                                if (saved) {
                                    islandManager.updatePlayerCache(targetMember.uuid(), updatedTargetData);
                                }
                                return saved;
                            });
                }
                return CompletableFuture.completedFuture(true);
            }).thenCompose(playerDataUpdated -> {
                // 섬 데이터 업데이트
                return islandManager.updateIsland(updatedIsland);
            }).thenAccept(success -> {
                if (success) {
                    player.sendMessage(ColorUtil.colorize("&a" + targetName + "님을 섬에서 추방했습니다."));
                    
                    // 추방당한 플레이어에게 알림
                    Player targetPlayer = Bukkit.getPlayer(targetName);
                    if (targetPlayer != null) {
                        targetPlayer.sendMessage(ColorUtil.colorize("&c" + island.islandName() + " 섬에서 추방당했습니다."));
                    }
                    
                    // 섬원들에게 알림
                    notifyIslandMembers(island, targetName + "님이 섬에서 추방되었습니다.");
                } else {
                    player.sendMessage(ColorUtil.colorize("&c멤버 추방에 실패했습니다."));
                }
            });
        });
    }
    
    /**
     * 탈퇴 명령어 처리
     */
    private void handleLeaveCommand(@NotNull Player player) {
        String playerUuid = player.getUniqueId().toString();
        
        islandManager.getPlayerIsland(playerUuid).thenAccept(island -> {
            if (island == null) {
                player.sendMessage(ColorUtil.colorize("&c소속된 섬이 없습니다."));
                return;
            }
            
            // 섬장은 탈퇴 불가
            if (island.ownerUuid().equals(playerUuid)) {
                player.sendMessage(ColorUtil.colorize("&c섬장은 섬을 탈퇴할 수 없습니다. 섬을 삭제하거나 다른 사람에게 양도하세요."));
                return;
            }
            
            // 멤버인지 알바인지 확인
            boolean isMember = island.members().stream()
                    .anyMatch(member -> member.uuid().equals(playerUuid));
            
            if (isMember) {
                // 멤버 제거
                List<IslandMemberDTO> members = new ArrayList<>(island.members());
                members.removeIf(member -> member.uuid().equals(playerUuid));
                
                // 섬 데이터 업데이트
                IslandDTO updatedIsland = new IslandDTO(
                        island.islandId(),
                        island.ownerUuid(),
                        island.ownerName(),
                        island.islandName(),
                        island.size(),
                        island.isPublic(),
                        island.createdAt(),
                        System.currentTimeMillis(),
                        members,
                        island.workers(),
                        island.contributions(),
                        island.spawnData(),
                        island.upgradeData(),
                        island.permissions(),
                        island.pendingInvites(),
                        island.recentVisits(),
                        island.totalResets(),
                        island.deletionScheduledAt()
                );
                
                // 플레이어의 섬 데이터 업데이트
                islandManager.loadPlayerIslandData(playerUuid).thenCompose(playerData -> {
                    PlayerIslandDataDTO updatedPlayerData;
                    if (playerData == null) {
                        updatedPlayerData = PlayerIslandDataDTO.createNew(playerUuid);
                    } else {
                        updatedPlayerData = playerData.leaveIsland();
                    }
                    
                    // 플레이어 데이터와 섬 데이터 모두 업데이트
                    return CompletableFuture.allOf(
                            islandManager.updateIsland(updatedIsland),
                            islandManager.getFirestoreService().savePlayerIslandData(updatedPlayerData)
                    ).thenApply(v -> {
                        // 캐시 업데이트
                        islandManager.updatePlayerCache(playerUuid, updatedPlayerData);
                        return true;
                    });
                }).thenAccept(success -> {
                    if (success) {
                        player.sendMessage(ColorUtil.colorize("&a" + island.islandName() + " 섬에서 탈퇴했습니다."));
                        
                        // 섬원들에게 알림
                        notifyIslandMembers(island, player.getName() + "님이 섬을 떠났습니다.");
                    } else {
                        player.sendMessage(ColorUtil.colorize("&c섬 탈퇴에 실패했습니다."));
                    }
                });
            } else {
                // 알바인 경우 처리 (추후 구현)
                player.sendMessage(ColorUtil.colorize("&c알바 탈퇴는 아직 구현되지 않았습니다."));
            }
        });
    }
    
    /**
     * 홈 명령어 처리 (섬으로 이동)
     */
    private void handleHomeCommand(@NotNull Player player) {
        String playerUuid = player.getUniqueId().toString();
        
        islandManager.getPlayerIsland(playerUuid).thenAccept(island -> {
            if (island == null) {
                player.sendMessage(ColorUtil.colorize("&c소속된 섬이 없습니다."));
                return;
            }
            
            // 플레이어의 개인 스폰 위치 확인
            IslandSpawnPointDTO spawnPoint = island.spawnData().getPersonalSpawn(
                    playerUuid, 
                    island.ownerUuid().equals(playerUuid)
            );
            
            // 개인 스폰이 없으면 기본 스폰 사용
            if (spawnPoint == null) {
                spawnPoint = island.spawnData().defaultSpawn();
            }
            
            // 섬 중심 좌표 계산 (기본 스폰의 x, z 좌표가 섬의 중심)
            final int centerX = (int) island.spawnData().defaultSpawn().x();
            final int centerZ = (int) island.spawnData().defaultSpawn().z();
            final IslandSpawnPointDTO finalSpawnPoint = spawnPoint;
            
            // 절대 위치로 변환하여 텔레포트
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                World islandWorld = islandManager.getWorldManager().getIslandWorld();
                if (islandWorld == null) {
                    player.sendMessage(ColorUtil.colorize("&c섬 월드를 찾을 수 없습니다."));
                    return;
                }
                
                Location tpLocation = finalSpawnPoint.toAbsoluteLocation(islandWorld, centerX, centerZ);
                player.teleport(tpLocation);
                player.sendMessage(ColorUtil.colorize("&a섬으로 이동했습니다!"));
            });
        });
    }
    
    /**
     * 스폰 설정 명령어 처리
     */
    private void handleSetSpawnCommand(@NotNull Player player, @NotNull String[] args) {
        String playerUuid = player.getUniqueId().toString();
        
        islandManager.getPlayerIsland(playerUuid).thenAccept(island -> {
            if (island == null) {
                player.sendMessage(ColorUtil.colorize("&c소속된 섬이 없습니다."));
                return;
            }
            
            // 권한 확인 - 기본 스폰 설정은 섬장/부섬장만, 개인 스폰은 본인만
            IslandRole playerRole = island.getPlayerRole(playerUuid);
            
            // 스폰 타입 결정
            String spawnType = args.length > 1 ? args[1].toLowerCase() : "personal";
            
            switch (spawnType) {
                case "기본", "default" -> {
                    // 기본 스폰 설정은 섬장/부섬장만 가능
                    if (playerRole != IslandRole.OWNER && playerRole != IslandRole.CO_OWNER) {
                        player.sendMessage(ColorUtil.colorize("&c기본 스폰은 섬장/부섬장만 설정할 수 있습니다."));
                        return;
                    }
                    
                    setDefaultSpawn(player, island);
                }
                case "개인", "personal" -> {
                    // 개인 스폰 설정
                    setPersonalSpawn(player, island);
                }
                case "섬장", "owner" -> {
                    // 섬장 전용 스폰 설정 (섬장만 가능)
                    if (!island.ownerUuid().equals(playerUuid)) {
                        player.sendMessage(ColorUtil.colorize("&c섬장 스폰은 섬장만 설정할 수 있습니다."));
                        return;
                    }
                    
                    setOwnerSpawn(player, island, args);
                }
                default -> {
                    player.sendMessage(ColorUtil.colorize("&c사용법: /섬 스폰설정 [기본|개인|섬장] [별칭]"));
                    player.sendMessage(ColorUtil.colorize("&7예시: /섬 스폰설정 개인"));
                    player.sendMessage(ColorUtil.colorize("&7예시: /섬 스폰설정 섬장 창고앞"));
                }
            }
        });
    }
    
    /**
     * 기본 스폰 설정
     */
    private void setDefaultSpawn(@NotNull Player player, @NotNull IslandDTO island) {
        Location currentLoc = player.getLocation();
        World islandWorld = islandManager.getWorldManager().getIslandWorld();
        
        // 섬 월드에 있는지 확인
        if (!currentLoc.getWorld().equals(islandWorld)) {
            player.sendMessage(ColorUtil.colorize("&c섬에서만 스폰을 설정할 수 있습니다."));
            return;
        }
        
        // 섬 범위 내에 있는지 확인
        int centerX = (int) island.spawnData().defaultSpawn().x();
        int centerZ = (int) island.spawnData().defaultSpawn().z();
        IslandLocationDTO islandLoc = new IslandLocationDTO(centerX, centerZ, island.size());
        
        if (!islandLoc.contains(currentLoc)) {
            player.sendMessage(ColorUtil.colorize("&c본인의 섬 범위 내에서만 스폰을 설정할 수 있습니다."));
            return;
        }
        
        // 상대 좌표로 변환
        double relativeX = currentLoc.getX() - centerX;
        double relativeZ = currentLoc.getZ() - centerZ;
        
        // 새 스폰 포인트 생성
        IslandSpawnPointDTO newDefaultSpawn = new IslandSpawnPointDTO(
                relativeX,
                currentLoc.getY(),
                relativeZ,
                currentLoc.getYaw(),
                currentLoc.getPitch(),
                "섬 기본 스폰"
        );
        
        // 섬 데이터 업데이트
        IslandSpawnDTO updatedSpawnData = new IslandSpawnDTO(
                newDefaultSpawn,
                island.spawnData().ownerSpawns(),
                island.spawnData().memberSpawns()
        );
        
        updateIslandSpawnData(player, island, updatedSpawnData, "기본 스폰이 설정되었습니다!");
    }
    
    /**
     * 개인 스폰 설정
     */
    private void setPersonalSpawn(@NotNull Player player, @NotNull IslandDTO island) {
        Location currentLoc = player.getLocation();
        World islandWorld = islandManager.getWorldManager().getIslandWorld();
        String playerUuid = player.getUniqueId().toString();
        
        // 섬 월드에 있는지 확인
        if (!currentLoc.getWorld().equals(islandWorld)) {
            player.sendMessage(ColorUtil.colorize("&c섬에서만 스폰을 설정할 수 있습니다."));
            return;
        }
        
        // 섬 범위 내에 있는지 확인
        int centerX = (int) island.spawnData().defaultSpawn().x();
        int centerZ = (int) island.spawnData().defaultSpawn().z();
        IslandLocationDTO islandLoc = new IslandLocationDTO(centerX, centerZ, island.size());
        
        if (!islandLoc.contains(currentLoc)) {
            player.sendMessage(ColorUtil.colorize("&c본인의 섬 범위 내에서만 스폰을 설정할 수 있습니다."));
            return;
        }
        
        // 상대 좌표로 변환
        double relativeX = currentLoc.getX() - centerX;
        double relativeZ = currentLoc.getZ() - centerZ;
        
        // 새 스폰 포인트 생성
        IslandSpawnPointDTO newPersonalSpawn = new IslandSpawnPointDTO(
                relativeX,
                currentLoc.getY(),
                relativeZ,
                currentLoc.getYaw(),
                currentLoc.getPitch(),
                player.getName() + "의 개인 스폰"
        );
        
        // 멤버 스폰 맵 업데이트
        Map<String, IslandSpawnPointDTO> updatedMemberSpawns = new HashMap<>(island.spawnData().memberSpawns());
        updatedMemberSpawns.put(playerUuid, newPersonalSpawn);
        
        // 섬 데이터 업데이트
        IslandSpawnDTO updatedSpawnData = new IslandSpawnDTO(
                island.spawnData().defaultSpawn(),
                island.spawnData().ownerSpawns(),
                updatedMemberSpawns
        );
        
        updateIslandSpawnData(player, island, updatedSpawnData, "개인 스폰이 설정되었습니다!");
    }
    
    /**
     * 섬장 전용 스폰 설정
     */
    private void setOwnerSpawn(@NotNull Player player, @NotNull IslandDTO island, @NotNull String[] args) {
        // 섬장 스폰 개수 확인
        if (!island.spawnData().canAddOwnerSpawn()) {
            player.sendMessage(ColorUtil.colorize("&c섬장 스폰은 최대 3개까지만 설정할 수 있습니다."));
            return;
        }
        
        Location currentLoc = player.getLocation();
        World islandWorld = islandManager.getWorldManager().getIslandWorld();
        
        // 섬 월드에 있는지 확인
        if (!currentLoc.getWorld().equals(islandWorld)) {
            player.sendMessage(ColorUtil.colorize("&c섬에서만 스폰을 설정할 수 있습니다."));
            return;
        }
        
        // 섬 범위 내에 있는지 확인
        int centerX = (int) island.spawnData().defaultSpawn().x();
        int centerZ = (int) island.spawnData().defaultSpawn().z();
        IslandLocationDTO islandLoc = new IslandLocationDTO(centerX, centerZ, island.size());
        
        if (!islandLoc.contains(currentLoc)) {
            player.sendMessage(ColorUtil.colorize("&c본인의 섬 범위 내에서만 스폰을 설정할 수 있습니다."));
            return;
        }
        
        // 별칭 설정
        String alias = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) 
                      : "섬장 스폰 " + (island.spawnData().ownerSpawns().size() + 1);
        
        // 상대 좌표로 변환
        double relativeX = currentLoc.getX() - centerX;
        double relativeZ = currentLoc.getZ() - centerZ;
        
        // 새 스폰 포인트 생성
        IslandSpawnPointDTO newOwnerSpawn = new IslandSpawnPointDTO(
                relativeX,
                currentLoc.getY(),
                relativeZ,
                currentLoc.getYaw(),
                currentLoc.getPitch(),
                alias
        );
        
        // 섬장 스폰 목록 업데이트
        List<IslandSpawnPointDTO> updatedOwnerSpawns = new ArrayList<>(island.spawnData().ownerSpawns());
        updatedOwnerSpawns.add(newOwnerSpawn);
        
        // 섬 데이터 업데이트
        IslandSpawnDTO updatedSpawnData = new IslandSpawnDTO(
                island.spawnData().defaultSpawn(),
                updatedOwnerSpawns,
                island.spawnData().memberSpawns()
        );
        
        updateIslandSpawnData(player, island, updatedSpawnData, 
                "섬장 스폰 '" + alias + "'이(가) 설정되었습니다!");
    }
    
    /**
     * 섬 스폰 데이터 업데이트 헬퍼 메서드
     */
    private void updateIslandSpawnData(@NotNull Player player, @NotNull IslandDTO island, 
                                     @NotNull IslandSpawnDTO newSpawnData, @NotNull String successMessage) {
        // 새 섬 DTO 생성
        IslandDTO updatedIsland = new IslandDTO(
                island.islandId(),
                island.ownerUuid(),
                island.ownerName(),
                island.islandName(),
                island.size(),
                island.isPublic(),
                island.createdAt(),
                System.currentTimeMillis(),
                island.members(),
                island.workers(),
                island.contributions(),
                newSpawnData,
                island.upgradeData(),
                island.permissions(),
                island.pendingInvites(),
                island.recentVisits(),
                island.totalResets(),
                island.deletionScheduledAt()
        );
        
        // Firebase에 저장
        islandManager.updateIsland(updatedIsland).thenAccept(success -> {
            if (success) {
                player.sendMessage(ColorUtil.colorize("&a" + successMessage));
            } else {
                player.sendMessage(ColorUtil.colorize("&c스폰 설정에 실패했습니다."));
            }
        });
    }
    
    /**
     * 기여 명령어 처리
     */
    private void handleContributeCommand(@NotNull Player player, @NotNull String[] args) {
        if (args.length < 2) {
            player.sendMessage(ColorUtil.colorize("&c사용법: /섬 기여 <금액>"));
            player.sendMessage(ColorUtil.colorize("&7예시: /섬 기여 10000"));
            return;
        }
        
        String playerUuid = player.getUniqueId().toString();
        
        // 금액 파싱
        long amount;
        try {
            amount = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ColorUtil.colorize("&c올바른 금액을 입력해주세요."));
            return;
        }
        
        if (amount <= 0) {
            player.sendMessage(ColorUtil.colorize("&c0보다 큰 금액을 입력해주세요."));
            return;
        }
        
        if (amount > 1000000000) { // 10억 제한
            player.sendMessage(ColorUtil.colorize("&c한 번에 최대 10억까지만 기여할 수 있습니다."));
            return;
        }
        
        islandManager.getPlayerIsland(playerUuid).thenAccept(island -> {
            if (island == null) {
                player.sendMessage(ColorUtil.colorize("&c섬에 소속되어 있지 않습니다."));
                return;
            }
            
            // TODO: 플레이어 돈 확인 및 차감
            // 임시로 기여도만 추가 (실제로는 돈 확인 필요)
            
            // 현재 기여도
            long currentContribution = island.contributions().getOrDefault(playerUuid, 0L);
            island.contributions().put(playerUuid, currentContribution + amount);
            
            // 섬 데이터 업데이트
            IslandDTO updatedIsland = new IslandDTO(
                    island.islandId(),
                    island.ownerUuid(),
                    island.ownerName(),
                    island.islandName(),
                    island.size(),
                    island.isPublic(),
                    island.createdAt(),
                    System.currentTimeMillis(),
                    island.members(),
                    island.workers(),
                    island.contributions(),
                    island.spawnData(),
                    island.upgradeData(),
                    island.permissions(),
                    island.pendingInvites(),
                    island.recentVisits(),
                    island.totalResets(),
                    island.deletionScheduledAt()
            );
            
            islandManager.updateIsland(updatedIsland).thenAccept(success -> {
                if (success) {
                    player.sendMessage(ColorUtil.colorize("&a섬에 &e" + String.format("%,d", amount) + 
                            "원&a을 기여했습니다!"));
                    player.sendMessage(ColorUtil.colorize("&7현재 총 기여도: &e" + 
                            String.format("%,d", currentContribution + amount)));
                    
                    // 섬 내 알림 (선택적)
                    Bukkit.getOnlinePlayers().stream()
                            .filter(p -> IslandPermissionHandler.isMember(island, p.getUniqueId().toString()))
                            .filter(p -> !p.equals(player))
                            .forEach(p -> p.sendMessage(ColorUtil.colorize("&a" + player.getName() + 
                                    "님이 섬에 &e" + String.format("%,d", amount) + "원&a을 기여했습니다!")));
                } else {
                    player.sendMessage(ColorUtil.colorize("&c기여 처리 중 오류가 발생했습니다."));
                }
            });
        });
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
                .append(Component.text("/섬 초대 <플레이어>", NamedTextColor.AQUA))
                .append(Component.text(" - 플레이어를 섬으로 초대\n", NamedTextColor.WHITE))
                .append(Component.text("/섬 수락 <섬ID>", NamedTextColor.AQUA))
                .append(Component.text(" - 섬 초대 수락\n", NamedTextColor.WHITE))
                .append(Component.text("/섬 거절 <섬ID>", NamedTextColor.AQUA))
                .append(Component.text(" - 섬 초대 거절\n", NamedTextColor.WHITE))
                .append(Component.text("/섬 추방 <플레이어>", NamedTextColor.AQUA))
                .append(Component.text(" - 섬원 추방\n", NamedTextColor.WHITE))
                .append(Component.text("/섬 탈퇴", NamedTextColor.AQUA))
                .append(Component.text(" - 섬에서 탈퇴\n", NamedTextColor.WHITE))
                .append(Component.text("/섬 홈", NamedTextColor.AQUA))
                .append(Component.text(" - 섬으로 이동\n", NamedTextColor.WHITE))
                .append(Component.text("/섬 스폰설정 [타입]", NamedTextColor.AQUA))
                .append(Component.text(" - 스폰 위치 설정\n", NamedTextColor.WHITE))
                .append(Component.text("  - 타입: 기본, 개인, 섬장\n", NamedTextColor.GRAY))
                .append(Component.text("/섬 기여 <금액>", NamedTextColor.AQUA))
                .append(Component.text(" - 섬에 기여도 추가\n", NamedTextColor.WHITE))
                .append(Component.text("/섬 도움말", NamedTextColor.AQUA))
                .append(Component.text(" - 이 도움말 표시\n", NamedTextColor.WHITE))
                .append(Component.text("\n=========================", NamedTextColor.GRAY))
                .build();
        
        player.sendMessage(message);
    }
}