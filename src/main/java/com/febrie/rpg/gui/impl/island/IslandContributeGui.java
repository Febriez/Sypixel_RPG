package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
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
import org.jetbrains.annotations.Contract;
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
        super(viewer, guiManager, 36, "gui.island.contribute.title"); // 4줄 GUI
        this.islandManager = plugin.getIslandManager();
        this.playerManager = plugin.getRPGPlayerManager();
        this.island = island;
        this.rpgPlayer = playerManager.getPlayer(viewer);
    }
    /**
     * Factory method to create and open the contribution GUI
     */
    @Contract("_, _, _ -> new")
    public static @NotNull IslandContributeGui create(@NotNull RPGMain plugin, @NotNull Player viewer,
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
        return trans("gui.island.contribute.title");
    }
    
    private ItemStack createGoldInfoItem() {
        long currentGold = rpgPlayer != null ? rpgPlayer.getWallet().getBalance(CurrencyType.GOLD) : 0;
        long myContribution = island.membership().contributions().getOrDefault(viewer.getUniqueId().toString(), 0L);
        return StandardItemBuilder.guiItem(Material.GOLD_INGOT)
                .displayName(trans("gui.island.contribute.gold-info.title").color(UnifiedColorUtil.GOLD))
                .addLore(Component.empty())
                .addLore(trans("gui.island.contribute.gold-info.balance", "amount", String.format("%,d", currentGold)))
                .addLore(trans("gui.island.contribute.gold-info.contribution", "amount", String.format("%,d", myContribution)))
                .addLore(trans("gui.island.contribute.gold-info.description1"))
                .addLore(trans("gui.island.contribute.gold-info.description2"))
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
                .displayName(
                        trans("gui.island.contribute.quick-amount", "amount", String.format("%,d", amount)).color(canAfford ? UnifiedColorUtil.YELLOW : UnifiedColorUtil.ERROR)
                );
        builder.addLore(UnifiedColorUtil.parseComponent(""));
        if (canAfford) {
            builder.addLore(trans("gui.island.contribute.click-to-contribute"));
            builder.addLore(Component.empty());
            builder.addLore(trans("gui.island.contribute.click-prompt").color(UnifiedColorUtil.YELLOW));
        } else {
            builder.addLore(trans("gui.island.contribute.insufficient-gold").color(UnifiedColorUtil.ERROR));
            builder.addLore(trans("gui.island.contribute.gold-needed", "amount", String.format("%,d", amount - currentGold)).color(UnifiedColorUtil.ERROR));
        }
        return builder.build();
    }
    private ItemStack createCustomAmountItem() {
        return StandardItemBuilder.guiItem(Material.ANVIL)
                .displayName(trans("gui.island.contribute.custom-amount.title").color(UnifiedColorUtil.INFO))
                .addLore(trans("gui.island.contribute.custom-amount.description1"))
                .addLore(trans("gui.island.contribute.custom-amount.description2"))
                .addLore(trans("gui.island.contribute.custom-amount.minimum"))
                .addLore(trans("gui.island.contribute.custom-amount.maximum"))
                .addLore(trans("gui.island.contribute.custom-amount.click-prompt").color(UnifiedColorUtil.YELLOW))
                .build();
    }
    
    private ItemStack createBackButton() {
        return StandardItemBuilder.guiItem(Material.ARROW)
                .displayName(trans("gui.common.back").color(UnifiedColorUtil.ERROR))
                .addLore(trans("gui.island.contribute.back-description"))
                .build();
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
                            sendMessage(player, "gui.island.contribute.minimum-amount-error");
                            return Arrays.asList(AnvilGUI.ResponseAction.close());
                        }
                        // GUI 닫고 기여 처리
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            contributeGold(player, amount);
                        });
                    } catch (NumberFormatException e) {
                        sendMessage(player, "gui.island.contribute.invalid-number");
                    }
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                })
                .text(transString("gui.island.contribute.anvil.placeholder"))
                .title(transString("gui.island.contribute.anvil.title"))
                .plugin(plugin)
                .open(player);
    }
    
    private void contributeGold(Player player, int amount) {
        if (rpgPlayer == null) {
            sendMessage(player, "error.player-data-not-found");
            return;
        }
        
        long currentGold = rpgPlayer.getWallet().getBalance(CurrencyType.GOLD);
        if (currentGold < amount) {
            sendMessage(player, "gui.island.contribute.insufficient-gold-message", "amount", String.format("%,d", currentGold));
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
        IslandCoreDTO updatedCore = new IslandCoreDTO(
                island.core().islandId(), island.core().ownerUuid(), island.core().ownerName(),
                island.core().islandName(), island.core().size(), island.core().isPublic(),
                island.core().createdAt(), System.currentTimeMillis(),
                island.core().totalResets(), island.core().deletionScheduledAt(),
                island.core().location()
        );
        
        IslandMembershipDTO updatedMembership = new IslandMembershipDTO(
                island.core().islandId(),
                island.membership().members(),
                island.membership().workers(),
                newContributions
        );
        
        IslandDTO updated = new IslandDTO(updatedCore, updatedMembership, island.social(), island.configuration());
        islandManager.updateIsland(updated);
        sendMessage(player, "gui.island.contribute.success", "amount", String.format("%,d", amount));
        sendMessage(player, "gui.island.contribute.total-contribution", "amount", String.format("%,d", currentContribution + amount));
        // GUI 새로고침
        player.closeInventory();
        IslandContributionGui.create(plugin.getGuiManager(), viewer, updated, 1).open(viewer);
    }
}
