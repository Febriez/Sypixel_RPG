package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.gui.BaseGui;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 섬 기여 GUI
 * 플레이어가 섬에 골드를 기여할 수 있는 인터페이스
 *
 * @author Febrie, CoffeeTory
 */
public class IslandContributeGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final RPGPlayerManager playerManager;
    private final Player viewer;
    private final IslandDTO island;
    private final RPGPlayer rpgPlayer;
    
    // 빠른 기여 금액 옵션
    private static final int[] QUICK_AMOUNTS = {1000, 5000, 10000, 50000, 100000, 500000};
    
    private IslandContributeGui(@NotNull RPGMain plugin, @NotNull Player viewer,
                               @NotNull IslandDTO island) {
        super(plugin, 36); // 4줄 GUI
        this.islandManager = plugin.getIslandManager();
        this.playerManager = plugin.getRPGPlayerManager();
        this.viewer = viewer;
        this.island = island;
        this.rpgPlayer = playerManager.getPlayer(viewer);
    }
    
    /**
     * Factory method to create and open the contribution GUI
     */
    public static IslandContributeGui create(@NotNull RPGMain plugin, @NotNull Player viewer,
                                            @NotNull IslandDTO island) {
        IslandContributeGui gui = new IslandContributeGui(plugin, viewer, island);
        return BaseGui.create(gui, ColorUtil.parseComponent("&6&l섬 기여하기"));
    }
    
    @Override
    protected void setupItems() {
        fillBorder(Material.YELLOW_STAINED_GLASS_PANE);
        
        // 현재 보유 골드 정보
        setItem(4, createGoldInfoItem());
        
        // 빠른 기여 옵션들
        int[] slots = {11, 12, 13, 14, 15, 16};
        for (int i = 0; i < QUICK_AMOUNTS.length && i < slots.length; i++) {
            setItem(slots[i], createQuickContributeItem(QUICK_AMOUNTS[i]));
        }
        
        // 사용자 지정 금액
        setItem(22, createCustomAmountItem());
        
        // 뒤로가기
        setItem(31, createBackButton());
    }
    
    private ItemStack createGoldInfoItem() {
        long currentGold = rpgPlayer != null ? rpgPlayer.getWallet().getBalance(CurrencyType.GOLD) : 0;
        long myContribution = island.contributions().getOrDefault(viewer.getUniqueId().toString(), 0L);
        
        return new ItemBuilder(Material.GOLD_INGOT)
                .displayName(ColorUtil.parseComponent("&6&l내 골드 정보"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7보유 골드: &f" + String.format("%,d", currentGold) + " G"))
                .addLore(ColorUtil.parseComponent("&7내 총 기여도: &f" + String.format("%,d", myContribution) + " G"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7섬에 골드를 기여하면"))
                .addLore(ColorUtil.parseComponent("&7섬 업그레이드에 사용됩니다"))
                .build();
    }
    
    private ItemStack createQuickContributeItem(int amount) {
        long currentGold = rpgPlayer != null ? rpgPlayer.getWallet().getBalance(CurrencyType.GOLD) : 0;
        boolean canAfford = currentGold >= amount;
        
        Material material = switch (amount) {
            case 1000 -> Material.COPPER_INGOT;
            case 5000 -> Material.IRON_INGOT;
            case 10000 -> Material.GOLD_INGOT;
            case 50000 -> Material.DIAMOND;
            case 100000 -> Material.EMERALD;
            case 500000 -> Material.NETHERITE_INGOT;
            default -> Material.GOLD_NUGGET;
        };
        
        ItemBuilder builder = new ItemBuilder(material)
                .displayName(ColorUtil.parseComponent(
                        (canAfford ? "&e&l" : "&c&l") + String.format("%,d", amount) + " G 기여"
                ));
        
        builder.addLore(ColorUtil.parseComponent(""));
        
        if (canAfford) {
            builder.addLore(ColorUtil.parseComponent("&7클릭하여 기여합니다"));
            builder.addLore(ColorUtil.parseComponent(""));
            builder.addLore(ColorUtil.parseComponent("&e▶ 클릭하여 기여"));
        } else {
            builder.addLore(ColorUtil.parseComponent("&c골드가 부족합니다"));
            builder.addLore(ColorUtil.parseComponent("&c필요: " + String.format("%,d", amount - currentGold) + " G 더 필요"));
        }
        
        return builder.build();
    }
    
    private ItemStack createCustomAmountItem() {
        return new ItemBuilder(Material.ANVIL)
                .displayName(ColorUtil.parseComponent("&b&l사용자 지정 금액"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7원하는 금액을 직접"))
                .addLore(ColorUtil.parseComponent("&7입력할 수 있습니다"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7최소: &f100 G"))
                .addLore(ColorUtil.parseComponent("&7최대: &f보유 골드"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&e▶ 클릭하여 입력"))
                .build();
    }
    
    private ItemStack createBackButton() {
        return new ItemBuilder(Material.ARROW)
                .displayName(ColorUtil.parseComponent("&c뒤로가기"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7기여도 목록으로 돌아갑니다"))
                .build();
    }
    
    @Override
    protected void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        int slot = event.getSlot();
        
        // 뒤로가기
        if (slot == 31) {
            player.closeInventory();
            IslandContributionGui.create(plugin, viewer, island, 1).open(viewer);
            return;
        }
        
        // 사용자 지정 금액
        if (slot == 22) {
            handleCustomAmount(player);
            return;
        }
        
        // 빠른 기여 옵션
        int[] slots = {11, 12, 13, 14, 15, 16};
        for (int i = 0; i < slots.length && i < QUICK_AMOUNTS.length; i++) {
            if (slot == slots[i]) {
                handleContribution(player, QUICK_AMOUNTS[i]);
                break;
            }
        }
    }
    
    private void handleCustomAmount(Player player) {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    
                    String input = stateSnapshot.getText();
                    
                    try {
                        int amount = Integer.parseInt(input);
                        
                        if (amount < 100) {
                            player.sendMessage(ColorUtil.colorize("&c최소 100 골드 이상 기여해야 합니다."));
                            return Arrays.asList(AnvilGUI.ResponseAction.close());
                        }
                        
                        // GUI 닫고 기여 처리
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            handleContribution(player, amount);
                        });
                        
                    } catch (NumberFormatException e) {
                        player.sendMessage(ColorUtil.colorize("&c올바른 숫자를 입력해주세요."));
                    }
                    
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                })
                .text("기여할 금액")
                .title("기여할 골드 입력")
                .plugin(plugin)
                .open(player);
    }
    
    private void handleContribution(Player player, int amount) {
        if (rpgPlayer == null) {
            player.sendMessage(ColorUtil.colorize("&c플레이어 데이터를 찾을 수 없습니다."));
            return;
        }
        
        long currentGold = rpgPlayer.getWallet().getBalance(CurrencyType.GOLD);
        
        if (currentGold < amount) {
            player.sendMessage(ColorUtil.colorize("&c골드가 부족합니다! (보유: " + 
                    String.format("%,d", currentGold) + " G)"));
            return;
        }
        
        // 골드 차감
        rpgPlayer.getWallet().subtract(CurrencyType.GOLD, amount);
        
        // 기여도 업데이트
        Map<String, Long> newContributions = new HashMap<>(island.contributions());
        String playerUuid = player.getUniqueId().toString();
        long currentContribution = newContributions.getOrDefault(playerUuid, 0L);
        newContributions.put(playerUuid, currentContribution + amount);
        
        // 섬 업데이트
        IslandDTO updated = new IslandDTO(
                island.islandId(), island.ownerUuid(), island.ownerName(),
                island.islandName(), island.size(), island.isPublic(),
                island.createdAt(), System.currentTimeMillis(),
                island.members(), island.workers(), newContributions,
                island.spawnData(), island.upgradeData(), island.permissions(),
                island.pendingInvites(), island.recentVisits(),
                island.totalResets(), island.deletionScheduledAt(),
                island.settings()
        );
        
        islandManager.updateIsland(updated);
        
        player.sendMessage(ColorUtil.colorize("&a" + String.format("%,d", amount) + 
                " 골드를 섬에 기여했습니다!"));
        player.sendMessage(ColorUtil.colorize("&7총 기여도: &f" + 
                String.format("%,d", currentContribution + amount) + " G"));
        
        // GUI 새로고침
        player.closeInventory();
        IslandContributionGui.create(plugin, viewer, updated, 1).open(viewer);
    }
}