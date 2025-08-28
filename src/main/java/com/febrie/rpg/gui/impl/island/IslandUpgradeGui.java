package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LogUtil;
import com.febrie.rpg.util.GuiHandlerUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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
        super(viewer, guiManager, 45, Component.translatable("gui.island.upgrade.title"));
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
                "items.island.upgrade.size.name",
                island.configuration().upgradeData().sizeLevel(),
                SIZE_VALUES,
                SIZE_UPGRADE_COSTS,
                "unit.blocks"
        );
        setItem(11, new GuiItem(sizeItem).onAnyClick(player -> handleSizeUpgrade()));
        
        // 멤버 제한 업그레이드
        ItemStack memberItem = createUpgradeItem(
                Material.PLAYER_HEAD,
                "items.island.upgrade.member.name",
                island.configuration().upgradeData().memberLimitLevel(),
                MEMBER_VALUES,
                MEMBER_UPGRADE_COSTS,
                "unit.players"
        );
        setItem(13, new GuiItem(memberItem).onAnyClick(player -> handleMemberUpgrade()));
        
        // 알바 제한 업그레이드
        ItemStack workerItem = createUpgradeItem(
                Material.IRON_HELMET,
                "items.island.upgrade.worker.name",
                island.configuration().upgradeData().workerLimitLevel(),
                WORKER_VALUES,
                WORKER_UPGRADE_COSTS,
                "unit.players"
        );
        setItem(15, new GuiItem(workerItem).onAnyClick(player -> handleWorkerUpgrade()));
        
        // 정보 아이템
        ItemStack infoItem = ItemBuilder.of(Material.BOOK)
                .displayNameTranslated("items.island.upgrade.info.name")
                .loreTranslated("items.island.upgrade.info.lore")
                .hideAllFlags()
                .build();
        setItem(31, new GuiItem(infoItem));
        
        // 뒤로가기 버튼
        setItem(40, new GuiItem(ItemBuilder.of(Material.ARROW)
                .displayName(LangManager.getComponent("items.buttons.back.name", getViewerLocale()))
                .addLore(LangManager.getComponent("items.buttons.back.lore", getViewerLocale()))
                .hideAllFlags()
                .build()).onAnyClick(player -> {
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
        return Component.translatable("gui.island.upgrade.title");
    }
    
    private ItemStack createUpgradeItem(Material material, String nameKey, 
                                       int currentLevel, int[] values, long[] costs, String unitKey) {
        ItemBuilder builder = ItemBuilder.of(material)
                .displayNameTranslated(nameKey)
                .addLore(Component.empty());
        
        // 현재 레벨
        Component unit = Component.translatable(unitKey);
        builder.addLore(LangManager.getComponent("gui.island.upgrade.current-level", getViewerLocale(), 
                Component.text(currentLevel), Component.text(values[currentLevel]), unit));
        
        if (currentLevel < values.length - 1) {
            // 다음 레벨 정보
            builder.addLore(LangManager.getComponent("gui.island.upgrade.next-level", getViewerLocale(),
                    Component.text(currentLevel + 1), Component.text(values[currentLevel + 1]), unit));
            
            // 업그레이드 비용
            long cost = costs[currentLevel + 1];
            long currentContribution = island.membership().contributions().getOrDefault(viewer.getUniqueId().toString(), 0L);
            
            builder.addLore(Component.empty())
                   .addLore(LangManager.getComponent("gui.island.upgrade.cost", getViewerLocale(), Component.text(String.format("%,d", cost))));
            
            builder.addLore(LangManager.getComponent("gui.island.upgrade.current-contribution", getViewerLocale(),
                    Component.text(String.format("%,d", currentContribution))
                            .color(currentContribution >= cost ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR)));
            
            builder.addLore(Component.empty());
            if (currentContribution >= cost) {
                builder.addLore(Component.translatable("gui.island.upgrade.click-to-upgrade").color(UnifiedColorUtil.SUCCESS));
            } else {
                builder.addLore(Component.translatable("gui.island.upgrade.insufficient-contribution").color(UnifiedColorUtil.ERROR));
            }
        } else {
            // 최대 레벨
            builder.addLore(Component.empty())
                   .addLore(Component.translatable("gui.island.upgrade.max-level").color(UnifiedColorUtil.GOLD));
        }
        
        // 레벨 진행도 표시
        builder.addLore(Component.empty())
               .addLore(Component.translatable("gui.island.upgrade.progress").color(UnifiedColorUtil.SECONDARY))
               .addLore(UnifiedColorUtil.parseComponent(createProgressBar(currentLevel, values.length - 1)));
        
        return builder.hideAllFlags().build();
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
        IslandCoreDTO updatedCore = GuiHandlerUtil.createUpdatedCore(island.core(),
                null, // name unchanged
                null, // isPublic unchanged
                type.equals("size") ? SIZE_VALUES[newLevel] : null // size only if upgrading size
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