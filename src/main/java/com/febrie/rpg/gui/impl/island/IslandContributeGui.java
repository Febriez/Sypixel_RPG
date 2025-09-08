package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.GuiHandlerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
/**
 * 섬 기여 GUI
 * 플레이어가 섬에 골드를 기여할 수 있는 인터페이스
 *
 * @author Febrie, CoffeeTory
 */
public class IslandContributeGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final IslandDTO island;
    private final RPGPlayer rpgPlayer;
    // 빠른 기여 금액 옵션
    private static final int[] QUICK_AMOUNTS = {1000, 5000, 10000, 50000, 100000, 500000};
    private IslandContributeGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                               @NotNull RPGMain plugin, @NotNull IslandDTO island) {
        super(viewer, guiManager, 36, LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_TITLE, viewer.locale())); // 4줄 GUI
        this.islandManager = plugin.getIslandManager();
        this.island = island;
        this.rpgPlayer = plugin.getRPGPlayerManager().getPlayer(viewer);
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
        return LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_TITLE, viewer.locale());
    }
    
    private ItemStack createGoldInfoItem() {
        long currentGold = rpgPlayer != null ? rpgPlayer.getWallet().getBalance(CurrencyType.GOLD) : 0;
        long myContribution = island.membership().contributions().getOrDefault(viewer.getUniqueId().toString(), 0L);
        return ItemBuilder.of(Material.GOLD_INGOT)
                .displayName(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_GOLD_INFO_TITLE, getViewerLocale()))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_GOLD_INFO_BALANCE, getViewerLocale(), Component.text(String.format("%,d", currentGold))))
                .addLore(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_GOLD_INFO_CONTRIBUTION, getViewerLocale(), Component.text(String.format("%,d", myContribution))))
                .addLore(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_GOLD_INFO_DESCRIPTION1, getViewerLocale()))
                .addLore(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_GOLD_INFO_DESCRIPTION2, getViewerLocale()))
                .hideAllFlags()
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
        ItemBuilder builder = ItemBuilder.of(material)
                .displayName(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_QUICK_AMOUNT, getViewerLocale(), Component.text(String.format("%,d", amount))));
        builder.addLore(Component.empty());
        if (canAfford) {
            builder.addLore(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_CLICK_TO_CONTRIBUTE, getViewerLocale()));
            builder.addLore(Component.empty());
            builder.addLore(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_CLICK_PROMPT, getViewerLocale()));
        } else {
            builder.addLore(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_INSUFFICIENT_GOLD, getViewerLocale()));
            builder.addLore(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_GOLD_NEEDED, getViewerLocale(), Component.text(String.format("%,d", amount - currentGold))));
        }
        return builder.hideAllFlags().build();
    }
    private ItemStack createCustomAmountItem() {
        return ItemBuilder.of(Material.ANVIL)
                .displayName(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_CUSTOM_AMOUNT_TITLE, getViewerLocale()))
                .addLore(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_CUSTOM_AMOUNT_DESCRIPTION1, getViewerLocale()))
                .addLore(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_CUSTOM_AMOUNT_DESCRIPTION2, getViewerLocale()))
                .addLore(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_CUSTOM_AMOUNT_MINIMUM, getViewerLocale()))
                .addLore(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_CUSTOM_AMOUNT_MAXIMUM, getViewerLocale()))
                .addLore(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_CUSTOM_AMOUNT_CLICK_PROMPT, getViewerLocale()))
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createBackButton() {
        return ItemBuilder.of(Material.ARROW)
                .displayName(LangManager.text(LangKey.GUI_BUTTONS_BACK_NAME, getViewerLocale()))
                .addLore(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_BACK_DESCRIPTION, getViewerLocale()))
                .hideAllFlags()
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
                            player.sendMessage(LangManager.text(LangKey.ISLAND_CONTRIBUTE_AMOUNT_TOO_LOW, player.locale()).color(NamedTextColor.RED));
                            return List.of(AnvilGUI.ResponseAction.close());
                        }
                        // GUI 닫고 기여 처리
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            contributeGold(player, amount);
                        });
                    } catch (NumberFormatException e) {
                        player.sendMessage(LangManager.text(LangKey.ISLAND_CONTRIBUTE_INVALID_AMOUNT, player.locale()).color(NamedTextColor.RED));
                    }
                    return List.of(AnvilGUI.ResponseAction.close());
                })
                .text(net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(
                        LangManager.text(LangKey.ISLAND_GUI_CONTRIBUTE_CONTRIBUTION_INPUT_TEXT, player.locale())))
                .itemLeft(new ItemStack(Material.GOLD_INGOT))
                .title(net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(
                        LangManager.text(LangKey.ISLAND_GUI_CONTRIBUTE_CONTRIBUTION_INPUT_TITLE, player.locale())))
                .plugin(plugin)
                .open(player);
    }
    
    private void contributeGold(Player player, int amount) {
        if (rpgPlayer == null) {
            player.sendMessage(LangManager.text(LangKey.ERROR_PLAYER_DATA_NOT_FOUND, player.locale()));
            return;
        }
        
        long currentGold = rpgPlayer.getWallet().getBalance(CurrencyType.GOLD);
        if (currentGold < amount) {
            player.sendMessage(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_INSUFFICIENT_GOLD_MESSAGE, player.locale(), Component.text(String.format("%,d", currentGold))));
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
        IslandCoreDTO updatedCore = GuiHandlerUtil.createUpdatedCore(island.core());
        
        IslandMembershipDTO updatedMembership = new IslandMembershipDTO(
                island.core().islandId(),
                island.membership().members(),
                island.membership().workers(),
                newContributions
        );
        
        IslandDTO updated = new IslandDTO(updatedCore, updatedMembership, island.social(), island.configuration());
        islandManager.updateIsland(updated);
        player.sendMessage(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_SUCCESS, player.locale(), Component.text(String.format("%,d", amount))));
        player.sendMessage(LangManager.text(LangKey.GUI_ISLAND_CONTRIBUTE_TOTAL_CONTRIBUTION, player.locale(), Component.text(String.format("%,d", currentContribution + amount))));
        // GUI 새로고침
        player.closeInventory();
        IslandContributionGui.create(plugin.getGuiManager(), viewer, updated, 1).open(viewer);
    }
}
