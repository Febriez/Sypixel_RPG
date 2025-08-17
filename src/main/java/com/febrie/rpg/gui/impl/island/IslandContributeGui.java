package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.StandardItemBuilder;
import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Collections;
import java.util.Arrays;
/**
 * 섬 기여 GUI
 * 플레이어가 섬에 골드를 기여할 수 있는 인터페이스
 *
 * @author Febrie, CoffeeTory
 */
public class IslandContributeGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final RPGPlayerManager playerManager;
    private final IslandDTO island;
    private final RPGPlayer rpgPlayer;
    // 빠른 기여 금액 옵션
    private static final int[] QUICK_AMOUNTS = {1000, 5000, 10000, 50000, 100000, 500000};
    private IslandContributeGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                               @NotNull RPGMain plugin, @NotNull IslandDTO island) {
        super(viewer, guiManager, 36, "&6&l섬 기여하기"); // 4줄 GUI
        this.islandManager = plugin.getIslandManager();
        this.playerManager = plugin.getRPGPlayerManager();
        this.island = island;
        this.rpgPlayer = playerManager.getPlayer(viewer);
    }
    /**
     * Factory method to create and open the contribution GUI
     */
    public static IslandContributeGui create(@NotNull RPGMain plugin, @NotNull Player viewer,
                                            @NotNull IslandDTO island) {
        return new IslandContributeGui(viewer, plugin.getGuiManager(), plugin, island);
    }
    @Override
    protected void setupLayout() {
        fillBorder(Material.YELLOW_STAINED_GLASS_PANE);
        
        // 현재 보유 골드 정보
        setItem(4, new GuiItem(createGoldInfoItem()));
        // 빠른 기여 옵션들
        int[] slots = {11, 12, 13, 14, 15, 16};
        for (int i = 0; i < QUICK_AMOUNTS.length && i < slots.length; i++) {
            final int amount = QUICK_AMOUNTS[i];
            setItem(slots[i], new GuiItem(createQuickContributeItem(amount))
                    .onAnyClick(player -> contributeGold(player, amount)));
        }
        // 사용자 지정 금액
        setItem(22, new GuiItem(createCustomAmountItem())
                .onAnyClick(this::openCustomAmountInput));
        // 뒤로가기
        setItem(31, new GuiItem(createBackButton())
                .onAnyClick(player -> {
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
        return Component.text("섬 기여하기", UnifiedColorUtil.PRIMARY);
    }
    
    private ItemStack createGoldInfoItem() {
        long currentGold = rpgPlayer != null ? rpgPlayer.getWallet().getBalance(CurrencyType.GOLD) : 0;
        long myContribution = island.membership().contributions().getOrDefault(viewer.getUniqueId().toString(), 0L);
        return StandardItemBuilder.guiItem(Material.GOLD_INGOT)
                .displayName(UnifiedColorUtil.parseComponent("&6&l내 골드 정보"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7보유 골드: &f" + String.format("%,d", currentGold) + " G"))
                .addLore(UnifiedColorUtil.parseComponent("&7내 총 기여도: &f" + String.format("%,d", myContribution) + " G"))
                .addLore(UnifiedColorUtil.parseComponent("&7섬에 골드를 기여하면"))
                .addLore(UnifiedColorUtil.parseComponent("&7섬 업그레이드에 사용됩니다"))
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
        ItemBuilder builder = StandardItemBuilder.guiItem(material)
                .displayName(UnifiedColorUtil.parseComponent(
                        (canAfford ? "&e&l" : "&c&l") + String.format("%,d", amount) + " G 기여"
                ));
        builder.addLore(UnifiedColorUtil.parseComponent(""));
        if (canAfford) {
            builder.addLore(UnifiedColorUtil.parseComponent("&7클릭하여 기여합니다"));
            builder.addLore(UnifiedColorUtil.parseComponent(""));
            builder.addLore(UnifiedColorUtil.parseComponent("&e▶ 클릭하여 기여"));
        } else {
            builder.addLore(UnifiedColorUtil.parseComponent("&c골드가 부족합니다"));
            builder.addLore(UnifiedColorUtil.parseComponent("&c필요: " + String.format("%,d", amount - currentGold) + " G 더 필요"));
        }
        return builder.build();
    }
    private ItemStack createCustomAmountItem() {
        return StandardItemBuilder.guiItem(Material.ANVIL)
                .displayName(UnifiedColorUtil.parseComponent("&b&l사용자 지정 금액"))
                .addLore(UnifiedColorUtil.parseComponent("&7원하는 금액을 직접"))
                .addLore(UnifiedColorUtil.parseComponent("&7입력할 수 있습니다"))
                .addLore(UnifiedColorUtil.parseComponent("&7최소: &f100 G"))
                .addLore(UnifiedColorUtil.parseComponent("&7최대: &f보유 골드"))
                .addLore(UnifiedColorUtil.parseComponent("&e▶ 클릭하여 입력"))
                .build();
    }
    
    private ItemStack createBackButton() {
        return StandardItemBuilder.guiItem(Material.ARROW)
                .displayName(UnifiedColorUtil.parseComponent("&c뒤로가기"))
                .addLore(UnifiedColorUtil.parseComponent("&7기여도 목록으로 돌아갑니다"))
                .build();
    }
    
    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
    
    private void openCustomAmountInput(Player player) {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    
                    String input = stateSnapshot.getText();
                    try {
                        int amount = Integer.parseInt(input);
                        
                        if (amount < 100) {
                            player.sendMessage(UnifiedColorUtil.parse("&c최소 100 골드 이상 기여해야 합니다."));
                            return Arrays.asList(AnvilGUI.ResponseAction.close());
                        }
                        // GUI 닫고 기여 처리
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            contributeGold(player, amount);
                        });
                    } catch (NumberFormatException e) {
                        player.sendMessage(UnifiedColorUtil.parse("&c올바른 숫자를 입력해주세요."));
                    }
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                })
                .text("기여할 금액")
                .title("기여할 골드 입력")
                .plugin(plugin)
                .open(player);
    }
    
    private void contributeGold(Player player, int amount) {
        if (rpgPlayer == null) {
            player.sendMessage(UnifiedColorUtil.parse("&c플레이어 데이터를 찾을 수 없습니다."));
            return;
        }
        
        long currentGold = rpgPlayer.getWallet().getBalance(CurrencyType.GOLD);
        if (currentGold < amount) {
            player.sendMessage(UnifiedColorUtil.parse("&c골드가 부족합니다! (보유: " + 
                    String.format("%,d", currentGold) + " G)"));
            return;
        }
        
        // 골드 차감
        rpgPlayer.getWallet().subtract(CurrencyType.GOLD, amount);
        // 기여도 업데이트
        Map<String, Long> newContributions = new HashMap<>(island.membership().contributions());
        String playerUuid = player.getUniqueId().toString();
        long currentContribution = newContributions.getOrDefault(playerUuid, 0L);
        newContributions.put(playerUuid, currentContribution + amount);
        // 섬 업데이트
        IslandDTO updated = IslandDTO.fromFields(
                island.core().islandId(), island.core().ownerUuid(), island.core().ownerName(),
                island.core().islandName(), island.core().size(), island.core().isPublic(),
                island.core().createdAt(), System.currentTimeMillis(),
                island.membership().members(), island.membership().workers(), newContributions,
                island.configuration().spawnData(), island.configuration().upgradeData(), island.configuration().permissions(),
                island.social().pendingInvites(), island.social().recentVisits(),
                island.core().totalResets(), island.core().deletionScheduledAt(),
                island.configuration().settings()
        );
        islandManager.updateIsland(updated);
        player.sendMessage(UnifiedColorUtil.parse("&a" + String.format("%,d", amount) + 
                " 골드를 섬에 기여했습니다!"));
        player.sendMessage(UnifiedColorUtil.parse("&7총 기여도: &f" + 
                String.format("%,d", currentContribution + amount) + " G"));
        // GUI 새로고침
        player.closeInventory();
        IslandContributionGui.create(plugin.getGuiManager(), viewer, updated, 1).open(viewer);
    }
}
