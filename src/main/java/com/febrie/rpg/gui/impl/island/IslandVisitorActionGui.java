package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.listener.IslandVisitListener;
import com.febrie.rpg.island.permission.IslandPermissionHandler;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.UnifiedTimeUtil;
import com.febrie.rpg.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * 섬 방문자 액션 GUI
 * 특정 방문자에 대한 액션(추방, 차단, 귓말, 초대) 메뉴
 *
 * @author Febrie, CoffeeTory
 */
public class IslandVisitorActionGui extends BaseGui {
    
    private final IslandDTO island;
    private final IslandVisitListener.CurrentVisitorInfo visitor;
    private final OfflinePlayer targetPlayer;
    
    private IslandVisitorActionGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                                  @NotNull IslandDTO island, @NotNull IslandVisitListener.CurrentVisitorInfo visitor) {
        super(viewer, guiManager, 27, "gui.island.visitor.action.title");
        this.island = island;
        this.visitor = visitor;
        this.targetPlayer = Bukkit.getOfflinePlayer(UUID.fromString(visitor.getPlayerUuid()));
    }
    
    /**
     * IslandVisitorActionGui 인스턴스를 생성하고 초기화합니다.
     */
    public static IslandVisitorActionGui create(@NotNull GuiManager guiManager, @NotNull Player viewer,
                                              @NotNull IslandDTO island, @NotNull IslandVisitListener.CurrentVisitorInfo visitor) {
        IslandVisitorActionGui gui = new IslandVisitorActionGui(viewer, guiManager, island, visitor);
        return gui;
    }
    
    @Override
    public @NotNull Component getTitle() {
        return trans("island.gui.visitor.action.title", "player", visitor.getPlayerName());
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        return IslandVisitorLiveGui.create(guiManager, viewer, island, 1);
    }
    
    @Override
    protected void setupLayout() {
        createBorder();
        
        // 플레이어 정보 아이템
        setItem(4, createPlayerInfoItem());
        
        // 액션 버튼들
        setItem(10, createKickItem()); // 추방
        setItem(12, createBanItem());  // 차단
        setItem(14, createWhisperItem()); // 귓말
        setItem(16, createInviteItem()); // 초대
        
        // 표준 네비게이션
        setupStandardNavigation(true, true);
    }
    
    /**
     * 플레이어 정보 아이템 생성
     */
    private GuiItem createPlayerInfoItem() {
        String playerName = visitor.getPlayerName();
        String duration = UnifiedTimeUtil.formatDuration(visitor.getCurrentDuration());
        
        ItemStack item = new ItemBuilder(Material.PLAYER_HEAD)
                .displayName(Component.text(playerName, UnifiedColorUtil.YELLOW))
                .lore(List.of(
                    Component.empty(),
                    Component.text("방문 시작: ", UnifiedColorUtil.GRAY).append(
                        Component.text(formatTimestamp(visitor.getVisitStartTime()), UnifiedColorUtil.WHITE)
                    ),
                    Component.text("경과 시간: ", UnifiedColorUtil.GRAY).append(Component.text(duration, UnifiedColorUtil.YELLOW)),
                    Component.empty(),
                    Component.text("상태: ", UnifiedColorUtil.GRAY).append(
                        Component.text("온라인", UnifiedColorUtil.GREEN)
                    ),
                    Component.empty(),
                    Component.text("아래 버튼을 클릭하여", UnifiedColorUtil.GRAY),
                    Component.text("액션을 선택하세요.", UnifiedColorUtil.GRAY)
                ))
                .build();
        
        // 플레이어 머리 설정
        if (item.getItemMeta() instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(targetPlayer);
            item.setItemMeta(skullMeta);
        }
        
        return GuiItem.display(item);
    }
    
    /**
     * 추방 아이템 생성
     */
    private GuiItem createKickItem() {
        boolean hasPermission = IslandPermissionHandler.hasPermission(island, viewer, "KICK_MEMBERS");
        
        Component displayName;
        List<Component> lore;
        Material material;
        
        if (hasPermission) {
            displayName = Component.text("추방", UnifiedColorUtil.RED);
            material = Material.IRON_SWORD;
            lore = List.of(
                Component.empty(),
                Component.text("이 플레이어를 섬에서", UnifiedColorUtil.GRAY),
                Component.text("즉시 추방합니다.", UnifiedColorUtil.GRAY),
                Component.empty(),
                Component.text("※ 추방된 플레이어는", UnifiedColorUtil.YELLOW),
                Component.text("   즉시 Hub로 이동됩니다", UnifiedColorUtil.YELLOW),
                Component.empty(),
                Component.text("▶ 클릭하여 추방", UnifiedColorUtil.RED)
            );
        } else {
            displayName = Component.text("추방", UnifiedColorUtil.DARK_GRAY);
            material = Material.BARRIER;
            lore = List.of(
                Component.empty(),
                Component.text("이 플레이어를 섬에서", UnifiedColorUtil.GRAY),
                Component.text("추방하는 기능입니다.", UnifiedColorUtil.GRAY),
                Component.empty(),
                Component.text("❌ 권한이 없습니다", UnifiedColorUtil.RED),
                Component.text("   추방 권한이 필요합니다", UnifiedColorUtil.GRAY)
            );
        }
        
        return GuiItem.clickable(
            new ItemBuilder(material)
                .displayName(displayName)
                .lore(lore)
                .build(),
            player -> handleKick(player, hasPermission)
        );
    }
    
    /**
     * 차단 아이템 생성
     */
    private GuiItem createBanItem() {
        // 차단 기능은 현재 구현되지 않았으므로 canKick 권한을 재사용
        boolean hasPermission = IslandPermissionHandler.hasPermission(island, viewer, "KICK_MEMBERS");
        
        Component displayName;
        List<Component> lore;
        Material material;
        
        if (hasPermission) {
            displayName = Component.text("차단", UnifiedColorUtil.DARK_RED);
            material = Material.IRON_BARS;
            lore = List.of(
                Component.empty(),
                Component.text("이 플레이어의 섬", UnifiedColorUtil.GRAY),
                Component.text("접근을 차단합니다.", UnifiedColorUtil.GRAY),
                Component.empty(),
                Component.text("※ 현재 미구현 기능입니다", UnifiedColorUtil.YELLOW),
                Component.empty(),
                Component.text("▶ 클릭 (미구현)", UnifiedColorUtil.DARK_GRAY)
            );
        } else {
            displayName = Component.text("차단", UnifiedColorUtil.DARK_GRAY);
            material = Material.BARRIER;
            lore = List.of(
                Component.empty(),
                Component.text("이 플레이어의 섬", UnifiedColorUtil.GRAY),
                Component.text("접근을 차단하는 기능입니다.", UnifiedColorUtil.GRAY),
                Component.empty(),
                Component.text("❌ 권한이 없습니다", UnifiedColorUtil.RED),
                Component.text("   차단 권한이 필요합니다", UnifiedColorUtil.GRAY)
            );
        }
        
        return GuiItem.clickable(
            new ItemBuilder(material)
                .displayName(displayName)
                .lore(lore)
                .build(),
            player -> handleBan(player, hasPermission)
        );
    }
    
    /**
     * 귓말 아이템 생성
     */
    private GuiItem createWhisperItem() {
        // 귓말은 별도 권한 체크 없음
        Player targetOnlinePlayer = Bukkit.getPlayer(UUID.fromString(visitor.getPlayerUuid()));
        boolean isOnline = targetOnlinePlayer != null && targetOnlinePlayer.isOnline();
        
        Component displayName;
        List<Component> lore;
        Material material;
        
        if (isOnline) {
            displayName = Component.text("귓말", UnifiedColorUtil.GREEN);
            material = Material.WRITABLE_BOOK;
            lore = List.of(
                Component.empty(),
                Component.text("이 플레이어에게", UnifiedColorUtil.GRAY),
                Component.text("귓말을 보냅니다.", UnifiedColorUtil.GRAY),
                Component.empty(),
                Component.text("※ 채팅창에 메시지를", UnifiedColorUtil.YELLOW),
                Component.text("   입력하여 전송하세요", UnifiedColorUtil.YELLOW),
                Component.empty(),
                Component.text("▶ 클릭하여 귓말", UnifiedColorUtil.GREEN)
            );
        } else {
            displayName = Component.text("귓말", UnifiedColorUtil.DARK_GRAY);
            material = Material.BARRIER;
            lore = List.of(
                Component.empty(),
                Component.text("이 플레이어에게", UnifiedColorUtil.GRAY),
                Component.text("귓말을 보내는 기능입니다.", UnifiedColorUtil.GRAY),
                Component.empty(),
                Component.text("❌ 플레이어가 오프라인입니다", UnifiedColorUtil.RED)
            );
        }
        
        return GuiItem.clickable(
            new ItemBuilder(material)
                .displayName(displayName)
                .lore(lore)
                .build(),
            player -> handleWhisper(player, isOnline)
        );
    }
    
    /**
     * 초대 아이템 생성
     */
    private GuiItem createInviteItem() {
        boolean hasPermission = IslandPermissionHandler.hasPermission(island, viewer, "INVITE_MEMBERS");
        
        Component displayName;
        List<Component> lore;
        Material material;
        
        if (hasPermission) {
            displayName = Component.text("초대", UnifiedColorUtil.BLUE);
            material = Material.PAPER;
            lore = List.of(
                Component.empty(),
                Component.text("이 플레이어를 섬", UnifiedColorUtil.GRAY),
                Component.text("멤버로 초대합니다.", UnifiedColorUtil.GRAY),
                Component.empty(),
                Component.text("※ 초대장이 전송됩니다", UnifiedColorUtil.YELLOW),
                Component.empty(),
                Component.text("▶ 클릭하여 초대", UnifiedColorUtil.BLUE)
            );
        } else {
            displayName = Component.text("초대", UnifiedColorUtil.DARK_GRAY);
            material = Material.BARRIER;
            lore = List.of(
                Component.empty(),
                Component.text("이 플레이어를 섬", UnifiedColorUtil.GRAY),
                Component.text("멤버로 초대하는 기능입니다.", UnifiedColorUtil.GRAY),
                Component.empty(),
                Component.text("❌ 권한이 없습니다", UnifiedColorUtil.RED),
                Component.text("   초대 권한이 필요합니다", UnifiedColorUtil.GRAY)
            );
        }
        
        return GuiItem.clickable(
            new ItemBuilder(material)
                .displayName(displayName)
                .lore(lore)
                .build(),
            player -> handleInvite(player, hasPermission)
        );
    }
    
    /**
     * 추방 처리
     */
    private void handleKick(@NotNull Player player, boolean hasPermission) {
        if (!hasPermission) {
            player.sendMessage(Component.text("❌ 권한이 없습니다. 추방 권한이 필요합니다.", UnifiedColorUtil.RED));
            playErrorSound(player);
            return;
        }
        
        Player targetPlayer = Bukkit.getPlayer(UUID.fromString(visitor.getPlayerUuid()));
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage(Component.text("❌ 대상 플레이어가 오프라인입니다.", UnifiedColorUtil.RED));
            playErrorSound(player);
            return;
        }
        
        player.closeInventory();
        
        // Hub 월드로 텔레포트
        var hubWorld = Bukkit.getWorld("world");
        if (hubWorld != null) {
            targetPlayer.teleport(hubWorld.getSpawnLocation());
            targetPlayer.sendMessage(Component.text("섬에서 추방되었습니다.", UnifiedColorUtil.RED));
            player.sendMessage(Component.text(visitor.getPlayerName() + " 플레이어를 추방했습니다.", UnifiedColorUtil.GREEN));
            playClickSound(player);
        } else {
            player.sendMessage(Component.text("❌ Hub 월드를 찾을 수 없습니다.", UnifiedColorUtil.RED));
            playErrorSound(player);
        }
    }
    
    /**
     * 차단 처리
     */
    private void handleBan(@NotNull Player player, boolean hasPermission) {
        if (!hasPermission) {
            player.sendMessage(Component.text("❌ 권한이 없습니다. 차단 권한이 필요합니다.", UnifiedColorUtil.RED));
            playErrorSound(player);
            return;
        }
        
        player.sendMessage(Component.text("❌ 차단 기능은 현재 구현되지 않았습니다.", UnifiedColorUtil.YELLOW));
        playErrorSound(player);
    }
    
    /**
     * 귓말 처리
     */
    private void handleWhisper(@NotNull Player player, boolean isOnline) {
        if (!isOnline) {
            player.sendMessage(Component.text("❌ 대상 플레이어가 오프라인입니다.", UnifiedColorUtil.RED));
            playErrorSound(player);
            return;
        }
        
        player.closeInventory();
        player.sendMessage(Component.text("채팅창에 귓말을 입력하세요: /tell " + visitor.getPlayerName() + " <메시지>", UnifiedColorUtil.GREEN));
        playClickSound(player);
    }
    
    /**
     * 초대 처리
     */
    private void handleInvite(@NotNull Player player, boolean hasPermission) {
        if (!hasPermission) {
            player.sendMessage(Component.text("❌ 권한이 없습니다. 초대 권한이 필요합니다.", UnifiedColorUtil.RED));
            playErrorSound(player);
            return;
        }
        
        Player targetPlayer = Bukkit.getPlayer(UUID.fromString(visitor.getPlayerUuid()));
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage(Component.text("❌ 대상 플레이어가 오프라인입니다.", UnifiedColorUtil.RED));
            playErrorSound(player);
            return;
        }
        
        player.closeInventory();
        
        // 섬 초대 명령어 실행 (실제 구현에 따라 조정 필요)
        player.performCommand("island invite " + visitor.getPlayerName());
        playClickSound(player);
    }
    
    
    /**
     * 타임스탬프 포맷 (밀리초 -> HH:mm:ss)
     */
    private String formatTimestamp(long timestamp) {
        java.time.LocalDateTime dateTime = 
            java.time.LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(timestamp), 
                java.time.ZoneId.systemDefault()
            );
        java.time.format.DateTimeFormatter formatter = 
            java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");
        return dateTime.format(formatter);
    }
}