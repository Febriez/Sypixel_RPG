package com.febrie.rpg.gui.impl.shop;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.npc.trait.RPGShopTrait;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;

import java.util.List;

/**
 * NPC 상점 GUI
 * RPGShopTrait를 가진 NPC와 상호작용할 때 표시
 *
 * @author Febrie, CoffeeTory
 */
public class NPCShopGui extends BaseGui {
    
    private final RPGPlayerManager playerManager;
    private final RPGPlayer rpgPlayer;
    private final RPGShopTrait shopTrait;
    private final String shopName;
    
    private NPCShopGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                      @NotNull RPGShopTrait shopTrait, @NotNull String shopName) {
        super(viewer, guiManager, 54, LangManager.getComponent("gui.shop.title", viewer, Component.text(shopName)));
        this.playerManager = guiManager.getPlugin().getRPGPlayerManager();
        this.rpgPlayer = playerManager.getPlayer(viewer);
        this.shopTrait = shopTrait;
        this.shopName = shopName;
    }
    
    /**
     * Factory method to create and open the shop GUI
     */
    public static NPCShopGui create(@NotNull GuiManager guiManager,
                                   @NotNull Player viewer, @NotNull RPGShopTrait shopTrait, @NotNull String shopName) {
        return new NPCShopGui(viewer, guiManager, shopTrait, shopName);
    }
    
    @Override
    public @NotNull Component getTitle() {
        return LangManager.getComponent("gui.shop.title", viewer, Component.text(shopName));
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        // NPC 상점은 NPC 대화에서 시작되므로 뒤로가기 없음
        return null;
    }
    
    @Override
    protected void setupLayout() {
        createBorder();
        setupStandardNavigation(false, true);
        
        // 현재 골드 표시
        setItem(4, createGoldDisplay());
        
        // 상점 아이템들 표시
        List<RPGShopTrait.ShopItem> items = shopTrait.getShopItems();
        int[] slots = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
        };
        
        for (int i = 0; i < items.size() && i < slots.length; i++) {
            setItem(slots[i], createShopItem(items.get(i)));
        }
    }
    
    private GuiItem createGoldDisplay() {
        long gold = rpgPlayer != null ? rpgPlayer.getWallet().getBalance(CurrencyType.GOLD) : 0;
        
        return GuiItem.display(ItemBuilder.of(Material.GOLD_INGOT)
                .displayNameTranslated("items.shop.gold.name")
                .addLore(Component.empty())
                .addLore(LangManager.get("gui.shop.gold.amount", viewer, Component.text(String.format("%,d", gold))))
                .hideAllFlags()
                .build());
    }
    
    private GuiItem createShopItem(RPGShopTrait.ShopItem shopItem) {
        ItemStack baseItem = shopItem.getItem().clone();
        ItemBuilder builder = ItemBuilder.from(baseItem);
        
        List<net.kyori.adventure.text.Component> originalLore = baseItem.getItemMeta().lore();
        if (originalLore != null) {
            for (net.kyori.adventure.text.Component line : originalLore) {
                builder.addLore(line);
            }
        }
        
        builder.addLore(Component.empty());
        builder.addLore(LangManager.getComponent("gui.shop.item.buy_price", viewer, Component.text(String.format("%,d", shopItem.getBuyPrice()))));
        
        if (shopItem.isSellable()) {
            builder.addLore(LangManager.getComponent("gui.shop.item.sell_price", viewer, Component.text(String.format("%,d", shopItem.getSellPrice()))));
        }
        
        builder.addLore(Component.empty());
        
        long playerGold = rpgPlayer != null ? rpgPlayer.getWallet().getBalance(CurrencyType.GOLD) : 0;
        if (playerGold >= shopItem.getBuyPrice()) {
            builder.addLore(LangManager.getComponent("gui.shop.item.click_to_buy", viewer));
        } else {
            builder.addLore(LangManager.getComponent("gui.shop.item.insufficient_gold", viewer));
        }
        
        if (shopItem.isSellable() && hasItem(baseItem)) {
            builder.addLore(LangManager.getComponent("gui.shop.item.click_to_sell", viewer));
        }
        
        return GuiItem.clickable(builder.build(), player -> {
            // 좌클릭은 구매, 우클릭은 판매 (아이템에서 직접 처리하지 않고 핸들러로 분리)
        }).onClick(org.bukkit.event.inventory.ClickType.LEFT, (p, click) -> {
            handleBuy(p, shopItem);
            playClickSound(p);
        }).onClick(org.bukkit.event.inventory.ClickType.RIGHT, (p, click) -> {
            if (shopItem.isSellable()) {
                handleSell(p, shopItem);
                playClickSound(p);
            }
        });
    }
    
    
    
    private void handleBuy(Player player, RPGShopTrait.ShopItem shopItem) {
        if (rpgPlayer == null) return;
        
        long playerGold = rpgPlayer.getWallet().getBalance(CurrencyType.GOLD);
        if (playerGold < shopItem.getBuyPrice()) {
            sendMessage(player, "gui.shop.message.insufficient_gold");
            return;
        }
        
        // 인벤토리 공간 확인
        if (player.getInventory().firstEmpty() == -1) {
            sendMessage(player, "gui.shop.message.inventory_full");
            return;
        }
        
        // 구매 처리
        rpgPlayer.getWallet().subtract(CurrencyType.GOLD, shopItem.getBuyPrice());
        player.getInventory().addItem(shopItem.getItem().clone());
        
        sendMessage(player, "gui.shop.message.item_bought", 
                shopItem.getItem().getType().name(), String.format("%,d", shopItem.getBuyPrice()));
        
        // GUI 새로고침
        refresh();
    }
    
    private void handleSell(Player player, RPGShopTrait.ShopItem shopItem) {
        if (rpgPlayer == null) return;
        
        if (!hasItem(shopItem.getItem())) {
            sendMessage(player, "gui.shop.message.no_item_to_sell");
            return;
        }
        
        // 판매 처리
        removeItem(player, shopItem.getItem());
        rpgPlayer.getWallet().add(CurrencyType.GOLD, shopItem.getSellPrice());
        
        sendMessage(player, "gui.shop.message.item_sold", 
                shopItem.getItem().getType().name(), String.format("%,d", shopItem.getSellPrice()));
        
        // GUI 새로고침
        refresh();
    }
    
    private boolean hasItem(ItemStack item) {
        return viewer.getInventory().containsAtLeast(item, 1);
    }
    
    private void removeItem(Player player, ItemStack item) {
        ItemStack toRemove = item.clone();
        toRemove.setAmount(1);
        player.getInventory().removeItem(toRemove);
    }
    
}