package com.febrie.rpg.gui.impl.shop;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.npc.trait.RPGShopTrait;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;

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
        super(viewer, guiManager, 54, "gui.shop.title", shopName);
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
        NPCShopGui gui = new NPCShopGui(viewer, guiManager, shopTrait, shopName);
        return createAndInitialize(gui, "gui.shop.title", shopName);
    }
    
    @Override
    public @NotNull Component getTitle() {
        return trans("gui.shop.title", shopName);
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
        
        return GuiItem.display(new ItemBuilder(Material.GOLD_INGOT)
                .displayName(ColorUtil.parseComponent("&6&l보유 골드"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&f" + String.format("%,d", gold) + " G"))
                .build());
    }
    
    private GuiItem createShopItem(RPGShopTrait.ShopItem shopItem) {
        ItemStack baseItem = shopItem.getItem().clone();
        ItemBuilder builder = new ItemBuilder(baseItem);
        
        // 기존 lore 유지 (Paper API)
        List<net.kyori.adventure.text.Component> originalLore = baseItem.getItemMeta().lore();
        if (originalLore != null) {
            for (net.kyori.adventure.text.Component line : originalLore) {
                builder.addLore(line);
            }
        }
        
        builder.addLore(ColorUtil.parseComponent(""));
        builder.addLore(ColorUtil.parseComponent("&7구매 가격: &f" + 
                String.format("%,d", shopItem.getBuyPrice()) + " G"));
        
        if (shopItem.isSellable()) {
            builder.addLore(ColorUtil.parseComponent("&7판매 가격: &f" + 
                    String.format("%,d", shopItem.getSellPrice()) + " G"));
        }
        
        builder.addLore(ColorUtil.parseComponent(""));
        
        long playerGold = rpgPlayer != null ? rpgPlayer.getWallet().getBalance(CurrencyType.GOLD) : 0;
        if (playerGold >= shopItem.getBuyPrice()) {
            builder.addLore(ColorUtil.parseComponent("&a▶ 좌클릭으로 구매"));
        } else {
            builder.addLore(ColorUtil.parseComponent("&c✖ 골드가 부족합니다"));
        }
        
        if (shopItem.isSellable() && hasItem(baseItem)) {
            builder.addLore(ColorUtil.parseComponent("&e▶ 우클릭으로 판매"));
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
            player.sendMessage(ColorUtil.colorize("&c골드가 부족합니다!"));
            return;
        }
        
        // 인벤토리 공간 확인
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ColorUtil.colorize("&c인벤토리가 가득 찼습니다!"));
            return;
        }
        
        // 구매 처리
        rpgPlayer.getWallet().subtract(CurrencyType.GOLD, shopItem.getBuyPrice());
        player.getInventory().addItem(shopItem.getItem().clone());
        
        player.sendMessage(ColorUtil.colorize("&a" + shopItem.getItem().getType().name() + 
                "을(를) 구매했습니다! (-" + String.format("%,d", shopItem.getBuyPrice()) + " G)"));
        
        // GUI 새로고침
        refresh();
    }
    
    private void handleSell(Player player, RPGShopTrait.ShopItem shopItem) {
        if (rpgPlayer == null) return;
        
        if (!hasItem(shopItem.getItem())) {
            player.sendMessage(ColorUtil.colorize("&c판매할 아이템이 없습니다!"));
            return;
        }
        
        // 판매 처리
        removeItem(player, shopItem.getItem());
        rpgPlayer.getWallet().add(CurrencyType.GOLD, shopItem.getSellPrice());
        
        player.sendMessage(ColorUtil.colorize("&a" + shopItem.getItem().getType().name() + 
                "을(를) 판매했습니다! (+" + String.format("%,d", shopItem.getSellPrice()) + " G)"));
        
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