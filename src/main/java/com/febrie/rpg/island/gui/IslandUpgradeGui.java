package com.febrie.rpg.island.gui;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandUpgradeDTO;
import com.febrie.rpg.gui.BaseGui;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LegacyItemBuilder;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 섬 업그레이드 GUI
 * 섬 크기, 멤버 제한, 알바 제한 업그레이드
 *
 * @author Febrie, CoffeeTory
 */
public class IslandUpgradeGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final Player player;
    private final IslandDTO island;
    
    // 업그레이드 비용 설정
    private static final long[] SIZE_UPGRADE_COSTS = {
            0,        // 레벨 0 (85x85) - 기본
            50000,    // 레벨 1 (125x125)
            150000,   // 레벨 2 (185x185)
            500000,   // 레벨 3 (265x265)
            1500000,  // 레벨 4 (365x365)
            5000000   // 레벨 5 (500x500)
    };
    
    private static final int[] SIZE_VALUES = {85, 125, 185, 265, 365, 500};
    
    private static final long[] MEMBER_UPGRADE_COSTS = {
            0,      // 레벨 0 (5명) - 기본
            25000,  // 레벨 1 (10명)
            75000,  // 레벨 2 (15명)
            200000, // 레벨 3 (25명)
            500000  // 레벨 4 (40명)
    };
    
    private static final int[] MEMBER_VALUES = {5, 10, 15, 25, 40};
    
    private static final long[] WORKER_UPGRADE_COSTS = {
            0,      // 레벨 0 (2명) - 기본
            15000,  // 레벨 1 (5명)
            50000,  // 레벨 2 (10명)
            150000, // 레벨 3 (20명)
            400000  // 레벨 4 (30명)
    };
    
    private static final int[] WORKER_VALUES = {2, 5, 10, 20, 30};
    
    public IslandUpgradeGui(@NotNull RPGMain plugin, @NotNull Player player, @NotNull IslandDTO island) {
        super(plugin, 45, ColorUtil.colorize("&2&l섬 업그레이드"));
        this.islandManager = plugin.getIslandManager();
        this.player = player;
        this.island = island;
    }
    
    @Override
    protected void setupItems() {
        // 배경 설정
        fillBorder(Material.GREEN_STAINED_GLASS_PANE);
        
        // 섬 크기 업그레이드
        ItemStack sizeItem = createUpgradeItem(
                Material.GRASS_BLOCK,
                "&a섬 크기 업그레이드",
                island.upgradeData().sizeLevel(),
                SIZE_VALUES,
                SIZE_UPGRADE_COSTS,
                "블록"
        );
        setItem(11, sizeItem);
        
        // 멤버 제한 업그레이드
        ItemStack memberItem = createUpgradeItem(
                Material.PLAYER_HEAD,
                "&b멤버 제한 업그레이드",
                island.upgradeData().memberLimitLevel(),
                MEMBER_VALUES,
                MEMBER_UPGRADE_COSTS,
                "명"
        );
        setItem(13, memberItem);
        
        // 알바 제한 업그레이드
        ItemStack workerItem = createUpgradeItem(
                Material.IRON_HELMET,
                "&e알바 제한 업그레이드",
                island.upgradeData().workerLimitLevel(),
                WORKER_VALUES,
                WORKER_UPGRADE_COSTS,
                "명"
        );
        setItem(15, workerItem);
        
        // 정보 아이템
        ItemStack infoItem = new LegacyItemBuilder(Material.BOOK)
                .setDisplayName(ColorUtil.colorize("&d&l업그레이드 안내"))
                .addLore("")
                .addLore(ColorUtil.colorize("&7섬 업그레이드는 기여도를 소모합니다"))
                .addLore(ColorUtil.colorize("&7업그레이드 후에는 다운그레이드할 수 없습니다"))
                .addLore("")
                .addLore(ColorUtil.colorize("&e섬 크기가 500 이상이면"))
                .addLore(ColorUtil.colorize("&e바이옴 설정이 가능합니다"))
                .build();
        setItem(31, infoItem);
        
        // 뒤로가기 버튼
        setItem(40, createBackButton());
    }
    
    private ItemStack createUpgradeItem(Material material, String name, 
                                       int currentLevel, int[] values, long[] costs, String unit) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        
        // 현재 레벨
        lore.add(ColorUtil.colorize("&7현재 레벨: &fLv." + currentLevel + " &7(" + values[currentLevel] + unit + ")"));
        
        if (currentLevel < values.length - 1) {
            // 다음 레벨 정보
            lore.add(ColorUtil.colorize("&7다음 레벨: &aLv." + (currentLevel + 1) + " &7(" + values[currentLevel + 1] + unit + ")"));
            
            // 업그레이드 비용
            long cost = costs[currentLevel + 1];
            long currentContribution = island.contributions().getOrDefault(player.getUniqueId().toString(), 0L);
            
            lore.add("");
            lore.add(ColorUtil.colorize("&7업그레이드 비용: &6" + String.format("%,d", cost) + " 기여도"));
            
            lore.add(ColorUtil.colorize("&7보유 기여도: " + 
                    (currentContribution >= cost ? "&a" : "&c") + String.format("%,d", currentContribution)));
            
            lore.add("");
            if (currentContribution >= cost) {
                lore.add(ColorUtil.colorize("&a&l클릭하여 업그레이드!"));
            } else {
                lore.add(ColorUtil.colorize("&c기여도가 부족합니다!"));
            }
        } else {
            // 최대 레벨
            lore.add("");
            lore.add(ColorUtil.colorize("&6&l최대 레벨 달성!"));
        }
        
        // 레벨 진행도 표시
        lore.add("");
        lore.add(ColorUtil.colorize("&7업그레이드 진행도:"));
        lore.add(createProgressBar(currentLevel, values.length - 1));
        
        return new LegacyItemBuilder(material)
                .setDisplayName(ColorUtil.colorize(name))
                .setLore(lore)
                .build();
    }
    
    private String createProgressBar(int current, int max) {
        StringBuilder builder = new StringBuilder();
        
        for (int i = 0; i <= max; i++) {
            if (i <= current) {
                builder.append("&a■");
            } else {
                builder.append("&7□");
            }
            
            if (i < max) {
                builder.append(" ");
            }
        }
        
        return ColorUtil.colorize(builder.toString());
    }
    
    private ItemStack createBackButton() {
        return new LegacyItemBuilder(Material.ARROW)
                .setDisplayName(ColorUtil.colorize("&c뒤로가기"))
                .addLore("")
                .addLore(ColorUtil.colorize("&7메인 메뉴로 돌아갑니다"))
                .build();
    }
    
    @Override
    protected void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player clicker)) return;
        if (!clicker.equals(player)) return;
        
        int slot = event.getSlot();
        
        switch (slot) {
            case 11 -> handleSizeUpgrade();
            case 13 -> handleMemberUpgrade();
            case 15 -> handleWorkerUpgrade();
            case 40 -> {
                player.closeInventory();
                new IslandMainGui(plugin, islandManager, island, player).open();
            }
        }
    }
    
    private void handleSizeUpgrade() {
        int currentLevel = island.upgradeData().sizeLevel();
        if (currentLevel >= SIZE_VALUES.length - 1) {
            player.sendMessage(ColorUtil.colorize("&c이미 최대 레벨입니다!"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        long cost = SIZE_UPGRADE_COSTS[currentLevel + 1];
        performUpgrade("size", cost, currentLevel + 1);
    }
    
    private void handleMemberUpgrade() {
        int currentLevel = island.upgradeData().memberLimitLevel();
        if (currentLevel >= MEMBER_VALUES.length - 1) {
            player.sendMessage(ColorUtil.colorize("&c이미 최대 레벨입니다!"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        long cost = MEMBER_UPGRADE_COSTS[currentLevel + 1];
        performUpgrade("member", cost, currentLevel + 1);
    }
    
    private void handleWorkerUpgrade() {
        int currentLevel = island.upgradeData().workerLimitLevel();
        if (currentLevel >= WORKER_VALUES.length - 1) {
            player.sendMessage(ColorUtil.colorize("&c이미 최대 레벨입니다!"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        long cost = WORKER_UPGRADE_COSTS[currentLevel + 1];
        performUpgrade("worker", cost, currentLevel + 1);
    }
    
    private void performUpgrade(String type, long cost, int newLevel) {
        String playerUuid = player.getUniqueId().toString();
        long currentContribution = island.contributions().getOrDefault(playerUuid, 0L);
        
        if (currentContribution < cost) {
            player.sendMessage(ColorUtil.colorize("&c기여도가 부족합니다!"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        // 기여도 차감
        island.contributions().put(playerUuid, currentContribution - cost);
        
        // 업그레이드 적용
        IslandUpgradeDTO currentUpgrade = island.upgradeData();
        IslandUpgradeDTO newUpgrade = switch (type) {
            case "size" -> new IslandUpgradeDTO(
                    newLevel,
                    currentUpgrade.memberLimitLevel(),
                    currentUpgrade.workerLimitLevel(),
                    currentUpgrade.memberLimit(),
                    currentUpgrade.workerLimit(),
                    System.currentTimeMillis()
            );
            case "member" -> new IslandUpgradeDTO(
                    currentUpgrade.sizeLevel(),
                    newLevel,
                    currentUpgrade.workerLimitLevel(),
                    MEMBER_VALUES[newLevel],
                    currentUpgrade.workerLimit(),
                    System.currentTimeMillis()
            );
            case "worker" -> new IslandUpgradeDTO(
                    currentUpgrade.sizeLevel(),
                    currentUpgrade.memberLimitLevel(),
                    newLevel,
                    currentUpgrade.memberLimit(),
                    WORKER_VALUES[newLevel],
                    System.currentTimeMillis()
            );
            default -> currentUpgrade;
        };
        
        // 섬 데이터 업데이트
        IslandDTO updatedIsland = new IslandDTO(
                island.islandId(),
                island.ownerUuid(),
                island.ownerName(),
                island.islandName(),
                type.equals("size") ? SIZE_VALUES[newLevel] : island.size(),
                island.isPublic(),
                island.createdAt(),
                System.currentTimeMillis(),
                island.members(),
                island.workers(),
                island.contributions(),
                island.spawnData(),
                newUpgrade,
                island.permissions(),
                island.pendingInvites(),
                island.recentVisits(),
                island.totalResets(),
                island.deletionScheduledAt()
        );
        
        // 저장
        islandManager.updateIsland(updatedIsland).thenAccept(success -> {
            if (success) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.sendMessage(ColorUtil.colorize("&a" + 
                            (type.equals("size") ? "섬 크기" : 
                             type.equals("member") ? "멤버 제한" : "알바 제한") + 
                            " 업그레이드 완료!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    
                    // 크기 업그레이드의 경우 물리적 확장 필요
                    if (type.equals("size")) {
                        expandIslandTerrain(updatedIsland);
                    }
                    
                    // GUI 새로고침
                    new IslandUpgradeGui(plugin, player, updatedIsland).open();
                });
            } else {
                player.sendMessage(ColorUtil.colorize("&c업그레이드 처리 중 오류가 발생했습니다."));
            }
        });
    }
    
    private void expandIslandTerrain(IslandDTO updatedIsland) {
        // 섬 물리적 확장 처리
        islandManager.getWorldManager().expandIsland(
                (int) updatedIsland.spawnData().defaultSpawn().x(),
                (int) updatedIsland.spawnData().defaultSpawn().z(),
                island.size(), // 이전 크기
                updatedIsland.size() // 새 크기
        ).thenRun(() -> {
            LogUtil.info(String.format("섬 크기 확장 완료: %s (%dx%d -> %dx%d)",
                    updatedIsland.islandName(),
                    island.size(), island.size(),
                    updatedIsland.size(), updatedIsland.size()));
        });
    }
}