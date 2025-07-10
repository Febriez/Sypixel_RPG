package com.febrie.rpg.gui.impl;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.InteractiveGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Main menu GUI implementation
 * Central hub for accessing all RPG features
 *
 * @author Febrie, CoffeeTory
 */
public class MainMenuGui implements InteractiveGui {

    private static final int GUI_SIZE = 45; // 5 rows

    private final Plugin plugin;
    private final GuiManager guiManager;
    private final LangManager langManager;
    private final Inventory inventory;
    private final Map<Integer, GuiItem> items;

    public MainMenuGui(@NotNull Plugin plugin, @NotNull GuiManager guiManager,
                       @NotNull LangManager langManager, @NotNull Player player) {
        this.plugin = plugin;
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.inventory = Bukkit.createInventory(this, GUI_SIZE,
                langManager.getComponent(player, "gui.mainmenu.title"));
        this.items = new HashMap<>();

        setupLayout(player);
    }

    @Override
    public @NotNull Component getTitle() {
        // This will be overridden by the inventory title
        return Component.text("Main Menu");
    }

    @Override
    public int getSize() {
        return GUI_SIZE;
    }

    @Override
    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    @Override
    public void refresh() {
        // Main menu doesn't need refresh typically, but can be implemented if needed
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onSlotClick(@NotNull InventoryClickEvent event, @NotNull Player player, int slot, @NotNull ClickType click) {
        GuiItem item = items.get(slot);
        if (item != null && item.hasActions()) {
            item.executeAction(player, click);
        }
    }

    @Override
    public boolean isSlotClickable(int slot, @NotNull Player player) {
        GuiItem item = items.get(slot);
        return item != null && item.hasActions() && item.isEnabled();
    }

    @Override
    public void onClosed(@NotNull Player player) {
        // Main menu cleanup if needed
    }

    /**
     * Sets up the main menu layout
     */
    private void setupLayout(@NotNull Player player) {
        setupDecorations(player);
        setupMenuButtons(player);
        setupNavigationButtons(player);
    }

    /**
     * Sets up decorative elements
     */
    private void setupDecorations(@NotNull Player player) {
        // Create border decoration
        for (int i = 0; i < 9; i++) {
            setItem(i, GuiFactory.createDecoration());
            setItem(36 + i, GuiFactory.createDecoration());
        }

        // Side decorations
        setItem(9, GuiFactory.createDecoration());
        setItem(17, GuiFactory.createDecoration());
        setItem(18, GuiFactory.createDecoration());
        setItem(26, GuiFactory.createDecoration());
        setItem(27, GuiFactory.createDecoration());
        setItem(35, GuiFactory.createDecoration());

        // Title item
        setItem(4, GuiItem.display(
                ItemBuilder.of(Material.NETHER_STAR)
                        .displayName(langManager.getComponent(player, "gui.mainmenu.title")
                                .color(ColorUtil.LEGENDARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(langManager.getMessage(player, "gui.mainmenu.subtitle"), NamedTextColor.YELLOW)
                        .build()
        ));
    }

    /**
     * Sets up main menu buttons
     */
    private void setupMenuButtons(@NotNull Player player) {
        // Profile button (slot 20)
        GuiItem profileButton = GuiItem.clickable(
                ItemBuilder.of(Material.PLAYER_HEAD)
                        .displayName(langManager.getComponent(player, "items.mainmenu.profile-button.name"))
                        .addLore(langManager.getMessage(player, "items.mainmenu.profile-button.lore"), NamedTextColor.GRAY)
                        .build(),
                clickedPlayer -> {
                    guiManager.openProfileGui(clickedPlayer);
                }
        );
        setItem(20, profileButton);

        // Shop button (slot 22)
        GuiItem shopButton = GuiItem.clickable(
                ItemBuilder.of(Material.EMERALD)
                        .displayName(langManager.getComponent(player, "items.mainmenu.shop-button.name"))
                        .addLore(langManager.getMessage(player, "items.mainmenu.shop-button.lore"), NamedTextColor.GRAY)
                        .build(),
                clickedPlayer -> {
                    langManager.sendMessage(clickedPlayer, "general.coming-soon");
                    // TODO: guiManager.openShopGui(clickedPlayer);
                }
        );
        setItem(22, shopButton);

        // Dungeon button (slot 24)
        GuiItem dungeonButton = GuiItem.clickable(
                ItemBuilder.of(Material.IRON_SWORD)
                        .displayName(langManager.getComponent(player, "items.mainmenu.dungeon-button.name"))
                        .addLore(langManager.getMessage(player, "items.mainmenu.dungeon-button.lore"), NamedTextColor.GRAY)
                        .build(),
                clickedPlayer -> {
                    langManager.sendMessage(clickedPlayer, "general.coming-soon");
                    // TODO: guiManager.openDungeonGui(clickedPlayer);
                }
        );
        setItem(24, dungeonButton);

        // Settings button (slot 31)
        GuiItem settingsButton = GuiItem.clickable(
                ItemBuilder.of(Material.COMPARATOR)
                        .displayName(langManager.getComponent(player, "items.mainmenu.settings-button.name"))
                        .addLore(langManager.getMessage(player, "items.mainmenu.settings-button.lore"), NamedTextColor.GRAY)
                        .build(),
                clickedPlayer -> {
                    langManager.sendMessage(clickedPlayer, "general.coming-soon");
                    // TODO: guiManager.openSettingsGui(clickedPlayer);
                }
        );
        setItem(31, settingsButton);
    }

    /**
     * Sets up navigation buttons
     */
    private void setupNavigationButtons(@NotNull Player player) {
        // Close button (slot 40)
        setItem(40, GuiFactory.createCloseButton());

        // Back button (slot 38) - if there's history
        setItem(38, GuiFactory.createBackButton(guiManager));
    }

    /**
     * Sets an item at the specified slot
     */
    private void setItem(int slot, @NotNull GuiItem item) {
        items.put(slot, item);
        inventory.setItem(slot, item.getItemStack());
    }
}