package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.dto.island.IslandUpgradeDTO;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LogUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;

/**
 * 섬 업그레이드 GUI
 * 섬 크기, 멤버 제한, 알바 제한 업그레이드
 *
 * @author Febrie, CoffeeTory
 */
public class IslandUpgradeGui extends BaseGui {
    
    private final IslandManager islandManager;
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
    
    private IslandUpgradeGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                            @NotNull RPGMain plugin, @NotNull IslandDTO island) {
        super(viewer, guiManager, 45, "&2&l섬 업그레이드");
        this.islandManager = plugin.getIslandManager();
        this.island = island;
    }
    
    /**
     * Factory method to create the GUI
     */
    public static IslandUpgradeGui create(@NotNull RPGMain plugin, @NotNull Player player, @NotNull IslandDTO island) {
        return new IslandUpgradeGui(player, plugin.getGuiManager(), plugin, island);
    }
    
    @Override
    protected void setupLayout() {
        // 배경 설정
        fillBorder(Material.GREEN_STAINED_GLASS_PANE);
        
        // 섬 크기 업그레이드
        ItemStack sizeItem = createUpgradeItem(
                Material.GRASS_BLOCK,
                "&a섬 크기 업그레이드",
                island.configuration().upgradeData().sizeLevel(),
                SIZE_VALUES,
                SIZE_UPGRADE_COSTS,
                "블록"
        );
        setItem(11, new GuiItem(sizeItem).onAnyClick(player -> handleSizeUpgrade()));
        
        // 멤버 제한 업그레이드
        ItemStack memberItem = createUpgradeItem(
                Material.PLAYER_HEAD,
                "&b멤버 제한 업그레이드",
                island.configuration().upgradeData().memberLimitLevel(),
                MEMBER_VALUES,
                MEMBER_UPGRADE_COSTS,
                "명"
        );
        setItem(13, new GuiItem(memberItem).onAnyClick(player -> handleMemberUpgrade()));
        
        // 알바 제한 업그레이드
        ItemStack workerItem = createUpgradeItem(
                Material.IRON_HELMET,
                "&e알바 제한 업그레이드",
                island.configuration().upgradeData().workerLimitLevel(),
                WORKER_VALUES,
                WORKER_UPGRADE_COSTS,
                "명"
        );
        setItem(15, new GuiItem(workerItem).onAnyClick(player -> handleWorkerUpgrade()));
        
        // 정보 아이템
        ItemStack infoItem = new ItemBuilder(Material.BOOK)
                .displayName(UnifiedColorUtil.parseComponent("&d&l업그레이드 안내"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7섬 업그레이드는 기여도를 소모합니다"))
                .addLore(UnifiedColorUtil.parseComponent("&7업그레이드 후에는 다운그레이드할 수 없습니다"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&e섬 크기가 500 이상이면"))
                .addLore(UnifiedColorUtil.parseComponent("&e바이옴 설정이 가능합니다"))
                .build();
        setItem(31, new GuiItem(infoItem));
        
        // 뒤로가기 버튼
        setItem(40, new GuiItem(createBackButton()).onAnyClick(player -> {
            player.closeInventory();
            IslandMainGui.create(plugin.getGuiManager(), viewer).open(viewer);
        }));
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        return null; // Use back button with direct navigation
    }
    
    @Override
    public @NotNull Component getTitle() {
        return Component.text("섬 업그레이드", UnifiedColorUtil.PRIMARY);
    }
    
    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
    
    private ItemStack createUpgradeItem(Material material, String name, 
                                       int currentLevel, int[] values, long[] costs, String unit) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        
        // 현재 레벨
        lore.add("&7현재 레벨: &fLv." + currentLevel + " &7(" + values[currentLevel] + unit + ")");
        
        if (currentLevel < values.length - 1) {
            // 다음 레벨 정보
            lore.add("&7다음 레벨: &aLv." + (currentLevel + 1) + " &7(" + values[currentLevel + 1] + unit + ")");
            
            // 업그레이드 비용
            long cost = costs[currentLevel + 1];
            long currentContribution = island.membership().contributions().getOrDefault(viewer.getUniqueId().toString(), 0L);
            
            lore.add("");
            lore.add("&7업그레이드 비용: &6" + String.format("%,d", cost) + " 기여도");
            
            lore.add("&7보유 기여도: " + 
                    (currentContribution >= cost ? "&a" : "&c") + String.format("%,d", currentContribution));
            
            lore.add("");
            if (currentContribution >= cost) {
                lore.add("&a&l클릭하여 업그레이드!");
            } else {
                lore.add("&c기여도가 부족합니다!");
            }
        } else {
            // 최대 레벨
            lore.add("");
            lore.add("&6&l최대 레벨 달성!");
        }
        
        // 레벨 진행도 표시
        lore.add("");
        lore.add("&7업그레이드 진행도:");
        lore.add(createProgressBar(currentLevel, values.length - 1));
        
        List<Component> componentLore = new ArrayList<>();
        for (String line : lore) {
            componentLore.add(UnifiedColorUtil.parseComponent(line));
        }
        
        return new ItemBuilder(material)
                .displayName(UnifiedColorUtil.parseComponent(name))
                .lore(componentLore)
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
        
        return builder.toString();
    }
    
    private ItemStack createBackButton() {
        return new ItemBuilder(Material.ARROW)
                .displayName(UnifiedColorUtil.parseComponent("&c뒤로가기"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7메인 메뉴로 돌아갑니다"))
                .build();
    }
    
    
    private void handleSizeUpgrade() {
        int currentLevel = island.configuration().upgradeData().sizeLevel();
        if (currentLevel >= SIZE_VALUES.length - 1) {
            viewer.sendMessage(UnifiedColorUtil.parse("&c이미 최대 레벨입니다!"));
            viewer.playSound(viewer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        long cost = SIZE_UPGRADE_COSTS[currentLevel + 1];
        performUpgrade("size", cost, currentLevel + 1);
    }
    
    private void handleMemberUpgrade() {
        int currentLevel = island.configuration().upgradeData().memberLimitLevel();
        if (currentLevel >= MEMBER_VALUES.length - 1) {
            viewer.sendMessage(UnifiedColorUtil.parse("&c이미 최대 레벨입니다!"));
            viewer.playSound(viewer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        long cost = MEMBER_UPGRADE_COSTS[currentLevel + 1];
        performUpgrade("member", cost, currentLevel + 1);
    }
    
    private void handleWorkerUpgrade() {
        int currentLevel = island.configuration().upgradeData().workerLimitLevel();
        if (currentLevel >= WORKER_VALUES.length - 1) {
            viewer.sendMessage(UnifiedColorUtil.parse("&c이미 최대 레벨입니다!"));
            viewer.playSound(viewer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        long cost = WORKER_UPGRADE_COSTS[currentLevel + 1];
        performUpgrade("worker", cost, currentLevel + 1);
    }
    
    private void performUpgrade(String type, long cost, int newLevel) {
        String playerUuid = viewer.getUniqueId().toString();
        long currentContribution = island.membership().contributions().getOrDefault(playerUuid, 0L);
        
        if (currentContribution < cost) {
            viewer.sendMessage(UnifiedColorUtil.parse("&c기여도가 부족합니다!"));
            viewer.playSound(viewer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        // 기여도 차감
        island.membership().contributions().put(playerUuid, currentContribution - cost);
        
        // 업그레이드 적용
        IslandUpgradeDTO currentUpgrade = island.configuration().upgradeData();
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
        IslandCoreDTO updatedCore = new IslandCoreDTO(
                island.core().islandId(),
                island.core().ownerUuid(),
                island.core().ownerName(),
                island.core().islandName(),
                type.equals("size") ? SIZE_VALUES[newLevel] : island.core().size(),
                island.core().isPublic(),
                island.core().createdAt(),
                System.currentTimeMillis(),
                island.core().totalResets(),
                island.core().deletionScheduledAt(),
                island.core().location()
        );
        
        IslandConfigurationDTO updatedConfiguration = new IslandConfigurationDTO(
                island.core().islandId(),
                island.configuration().spawnData(),
                newUpgrade,
                island.configuration().permissions(),
                island.configuration().settings()
        );
        
        IslandDTO updatedIsland = new IslandDTO(updatedCore, island.membership(), island.social(), updatedConfiguration);
        
        // 저장
        islandManager.updateIsland(updatedIsland).thenAccept(success -> {
            if (success) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    viewer.sendMessage(UnifiedColorUtil.parse("&a" + 
                            (type.equals("size") ? "섬 크기" : 
                             type.equals("member") ? "멤버 제한" : "알바 제한") + 
                            " 업그레이드 완료!"));
                    viewer.playSound(viewer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    
                    // 크기 업그레이드의 경우 물리적 확장 필요
                    if (type.equals("size")) {
                        expandIslandTerrain(updatedIsland);
                    }
                    
                    // GUI 새로고침
                    IslandUpgradeGui.create(plugin, viewer, updatedIsland).open(viewer);
                });
            } else {
                viewer.sendMessage(UnifiedColorUtil.parse("&c업그레이드 처리 중 오류가 발생했습니다."));
            }
        });
    }
    
    private void expandIslandTerrain(IslandDTO updatedIsland) {
        // 섬 물리적 확장 처리
        islandManager.getWorldManager().expandIsland(
                (int) updatedIsland.configuration().spawnData().defaultSpawn().x(),
                (int) updatedIsland.configuration().spawnData().defaultSpawn().z(),
                island.core().size(), // 이전 크기
                updatedIsland.core().size() // 새 크기
        ).thenRun(() -> {
            LogUtil.info(String.format("섬 크기 확장 완료: %s (%dx%d -> %dx%d)",
                    updatedIsland.core().islandName(),
                    island.core().size(), island.core().size(),
                    updatedIsland.core().size(), updatedIsland.core().size()));
        });
    }
}