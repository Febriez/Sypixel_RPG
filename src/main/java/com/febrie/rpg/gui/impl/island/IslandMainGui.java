package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.gui.BaseGui;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LegacyItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 섬 메인 GUI
 * 섬 관련 모든 기능에 접근할 수 있는 메인 인터페이스
 *
 * @author Febrie, CoffeeTory
 */
public class IslandMainGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final IslandDTO island;
    private final Player viewer;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public IslandMainGui(@NotNull RPGMain plugin, @NotNull IslandManager islandManager, 
                        @NotNull IslandDTO island, @NotNull Player viewer) {
        super(plugin, 45, ColorUtil.colorize("&b섬 관리 - " + island.islandName()));
        this.islandManager = islandManager;
        this.island = island;
        this.viewer = viewer;
    }
    
    @Override
    protected void setupItems() {
        // 배경 설정
        fillBorder(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        
        // 섬 정보
        setItem(13, createIslandInfoItem());
        
        // 멤버 관리 (섬장/부섬장만)
        if (isOwnerOrCoOwner()) {
            setItem(20, createMemberManagementItem());
        }
        
        // 권한 관리 (섬장만)
        if (isOwner()) {
            setItem(21, createPermissionManagementItem());
        }
        
        // 업그레이드
        setItem(22, createUpgradeItem());
        
        // 기여도
        setItem(23, createContributionItem());
        
        // 스폰 설정
        if (hasSpawnPermission()) {
            setItem(24, createSpawnSettingsItem());
        }
        
        // 섬 설정 (섬장만)
        if (isOwner()) {
            setItem(30, createIslandSettingsItem());
        }
        
        // 방문자 목록
        setItem(31, createVisitorListItem());
        
        // 섬 워프
        setItem(32, createWarpItem());
        
        // 닫기 버튼
        setItem(40, createCloseButton());
    }
    
    /**
     * 섬 정보 아이템 생성
     */
    private ItemStack createIslandInfoItem() {
        return new LegacyItemBuilder(Material.GRASS_BLOCK)
                .setDisplayName(ColorUtil.colorize("&b" + island.islandName()))
                .addLore("")
                .addLore(ColorUtil.colorize("&7섬장: &f" + island.ownerName()))
                .addLore(ColorUtil.colorize("&7크기: &e" + island.size() + " x " + island.size()))
                .addLore(ColorUtil.colorize("&7멤버: &a" + island.getMemberCount() + "/" + island.upgradeData().memberLimit()))
                .addLore(ColorUtil.colorize("&7알바: &a" + island.workers().size() + "/" + island.upgradeData().workerLimit()))
                .addLore("")
                .addLore(ColorUtil.colorize("&7생성일: &f" + dateFormat.format(new Date(island.createdAt()))))
                .addLore(ColorUtil.colorize("&7공개 여부: " + (island.isPublic() ? "&a공개" : "&c비공개")))
                .build();
    }
    
    /**
     * 멤버 관리 아이템
     */
    private ItemStack createMemberManagementItem() {
        return new LegacyItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName(ColorUtil.colorize("&a멤버 관리"))
                .addLore("")
                .addLore(ColorUtil.colorize("&7섬원을 초대하거나"))
                .addLore(ColorUtil.colorize("&7추방할 수 있습니다."))
                .addLore("")
                .addLore(ColorUtil.colorize("&e▶ 클릭하여 열기"))
                .build();
    }
    
    /**
     * 권한 관리 아이템
     */
    private ItemStack createPermissionManagementItem() {
        return new LegacyItemBuilder(Material.COMMAND_BLOCK)
                .setDisplayName(ColorUtil.colorize("&c권한 관리"))
                .addLore("")
                .addLore(ColorUtil.colorize("&7각 역할의 권한을"))
                .addLore(ColorUtil.colorize("&7설정할 수 있습니다."))
                .addLore("")
                .addLore(ColorUtil.colorize("&e▶ 클릭하여 열기"))
                .build();
    }
    
    /**
     * 업그레이드 아이템
     */
    private ItemStack createUpgradeItem() {
        return new LegacyItemBuilder(Material.ANVIL)
                .setDisplayName(ColorUtil.colorize("&6업그레이드"))
                .addLore("")
                .addLore(ColorUtil.colorize("&7현재 레벨:"))
                .addLore(ColorUtil.colorize("  &f크기: &e" + island.upgradeData().sizeLevel() + " 레벨"))
                .addLore(ColorUtil.colorize("  &f멤버: &e" + island.upgradeData().memberLimitLevel() + " 레벨"))
                .addLore(ColorUtil.colorize("  &f알바: &e" + island.upgradeData().workerLimitLevel() + " 레벨"))
                .addLore("")
                .addLore(ColorUtil.colorize("&e▶ 클릭하여 열기"))
                .build();
    }
    
    /**
     * 기여도 아이템
     */
    private ItemStack createContributionItem() {
        return new LegacyItemBuilder(Material.EMERALD)
                .setDisplayName(ColorUtil.colorize("&a기여도"))
                .addLore("")
                .addLore(ColorUtil.colorize("&7섬원들의 기여도를"))
                .addLore(ColorUtil.colorize("&7확인할 수 있습니다."))
                .addLore("")
                .addLore(ColorUtil.colorize("&7내 기여도: &e" + 
                        island.contributions().getOrDefault(viewer.getUniqueId().toString(), 0L)))
                .addLore("")
                .addLore(ColorUtil.colorize("&e▶ 클릭하여 열기"))
                .build();
    }
    
    /**
     * 스폰 설정 아이템
     */
    private ItemStack createSpawnSettingsItem() {
        return new LegacyItemBuilder(Material.ENDER_PEARL)
                .setDisplayName(ColorUtil.colorize("&d스폰 설정"))
                .addLore("")
                .addLore(ColorUtil.colorize("&7섬의 스폰 위치를"))
                .addLore(ColorUtil.colorize("&7관리할 수 있습니다."))
                .addLore("")
                .addLore(ColorUtil.colorize("&e▶ 클릭하여 열기"))
                .build();
    }
    
    /**
     * 섬 설정 아이템
     */
    private ItemStack createIslandSettingsItem() {
        return new LegacyItemBuilder(Material.COMPARATOR)
                .setDisplayName(ColorUtil.colorize("&9섬 설정"))
                .addLore("")
                .addLore(ColorUtil.colorize("&7섬 이름 변경"))
                .addLore(ColorUtil.colorize("&7공개/비공개 설정"))
                .addLore(ColorUtil.colorize("&7바이옴 변경"))
                .addLore("")
                .addLore(ColorUtil.colorize("&e▶ 클릭하여 열기"))
                .build();
    }
    
    /**
     * 방문자 목록 아이템
     */
    private ItemStack createVisitorListItem() {
        return new LegacyItemBuilder(Material.BOOK)
                .setDisplayName(ColorUtil.colorize("&f방문자 기록"))
                .addLore("")
                .addLore(ColorUtil.colorize("&7최근 방문자 목록을"))
                .addLore(ColorUtil.colorize("&7확인할 수 있습니다."))
                .addLore("")
                .addLore(ColorUtil.colorize("&7최근 방문자: &e" + island.recentVisits().size() + "명"))
                .addLore("")
                .addLore(ColorUtil.colorize("&e▶ 클릭하여 열기"))
                .build();
    }
    
    /**
     * 워프 아이템
     */
    private ItemStack createWarpItem() {
        return new LegacyItemBuilder(Material.COMPASS)
                .setDisplayName(ColorUtil.colorize("&b섬으로 이동"))
                .addLore("")
                .addLore(ColorUtil.colorize("&7섬으로 순간이동합니다."))
                .addLore("")
                .addLore(ColorUtil.colorize("&e▶ 클릭하여 이동"))
                .build();
    }
    
    /**
     * 닫기 버튼
     */
    private ItemStack createCloseButton() {
        return new LegacyItemBuilder(Material.BARRIER)
                .setDisplayName(ColorUtil.colorize("&c닫기"))
                .addLore("")
                .addLore(ColorUtil.colorize("&7메뉴를 닫습니다."))
                .build();
    }
    
    @Override
    protected void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        int slot = event.getSlot();
        
        switch (slot) {
            case 20 -> { // 멤버 관리
                if (isOwnerOrCoOwner()) {
                    new IslandMemberGui(plugin, islandManager, island, player).open();
                }
            }
            case 21 -> { // 권한 관리
                if (isOwner()) {
                    new IslandPermissionGui(plugin, islandManager, island, player).open();
                }
            }
            case 22 -> { // 업그레이드
                new IslandUpgradeGui(plugin, player, island).open(player);
            }
            case 23 -> { // 기여도
                new IslandContributionGui(plugin, player, island, 1).open();
            }
            case 24 -> { // 스폰 설정
                if (hasSpawnPermission()) {
                    // TODO: 스폰 설정 GUI 열기
                    player.sendMessage(ColorUtil.colorize("&c준비 중인 기능입니다."));
                }
            }
            case 30 -> { // 섬 설정
                if (isOwner()) {
                    // TODO: 섬 설정 GUI 열기
                    player.sendMessage(ColorUtil.colorize("&c준비 중인 기능입니다."));
                }
            }
            case 31 -> { // 방문자 목록
                new IslandVisitorGui(plugin, player, island, 1).open();
            }
            case 32 -> { // 섬으로 이동
                handleWarp(player);
            }
            case 40 -> { // 닫기
                player.closeInventory();
            }
        }
    }
    
    /**
     * 섬으로 워프
     */
    private void handleWarp(@NotNull Player player) {
        player.closeInventory();
        player.sendMessage(ColorUtil.colorize("&e섬으로 이동 중..."));
        
        // 스폰 위치 가져오기
        var spawn = island.spawnData().defaultSpawn()
                .toLocation(islandManager.getWorldManager().getIslandWorld());
        spawn.setY(spawn.getY() + 4);
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.teleport(spawn);
            player.sendMessage(ColorUtil.colorize("&a섬으로 이동했습니다!"));
        });
    }
    
    /**
     * 권한 확인 메서드들
     */
    private boolean isOwner() {
        return island.ownerUuid().equals(viewer.getUniqueId().toString());
    }
    
    private boolean isOwnerOrCoOwner() {
        String uuid = viewer.getUniqueId().toString();
        return island.ownerUuid().equals(uuid) || 
               island.members().stream()
                   .anyMatch(m -> m.uuid().equals(uuid) && m.isCoOwner());
    }
    
    private boolean hasSpawnPermission() {
        // TODO: 권한 시스템 구현 후 실제 권한 확인
        return isOwnerOrCoOwner();
    }
}