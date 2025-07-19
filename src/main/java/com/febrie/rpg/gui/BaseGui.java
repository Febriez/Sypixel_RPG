package com.febrie.rpg.gui;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LegacyItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Legacy-style base GUI class for island GUIs
 * This is a simpler GUI system used by island-related GUIs
 *
 * @author Febrie, CoffeeTory
 */
public abstract class BaseGui implements InventoryHolder {
    
    protected final RPGMain plugin;
    protected final Inventory inventory;
    protected final int size;
    protected Player viewer;
    
    /**
     * Constructor
     * @param plugin The main plugin instance
     * @param size The size of the inventory (must be multiple of 9)
     * @param title The title of the GUI
     */
    public BaseGui(@NotNull RPGMain plugin, int size, @NotNull String title) {
        this.plugin = plugin;
        this.size = size;
        this.inventory = Bukkit.createInventory(this, size, title);
        // setupItems() should be called after construction by subclasses
    }
    
    /**
     * Set up the items in the GUI
     * Must be implemented by subclasses
     */
    protected abstract void setupItems();
    
    /**
     * Handle clicks in the GUI
     * Must be implemented by subclasses
     */
    protected abstract void handleClick(InventoryClickEvent event);
    
    /**
     * Open the GUI for a player
     * This is called when the GUI constructor has the viewer information
     */
    public void open() {
        // Default implementation - tries to use the stored viewer if available
        if (viewer != null) {
            viewer.openInventory(inventory);
        } else {
            throw new UnsupportedOperationException("No viewer set for this GUI");
        }
    }
    
    /**
     * Open the GUI for a specific player
     */
    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }
    
    /**
     * Fill the border of the GUI with a specific material
     */
    protected void fillBorder(@NotNull Material material) {
        ItemStack borderItem = new LegacyItemBuilder(material)
                .setDisplayName(" ")
                .build();
        
        // Top row
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, borderItem);
        }
        
        // Bottom row
        for (int i = size - 9; i < size; i++) {
            inventory.setItem(i, borderItem);
        }
        
        // Left and right columns
        for (int i = 9; i < size - 9; i += 9) {
            inventory.setItem(i, borderItem);
            inventory.setItem(i + 8, borderItem);
        }
    }
    
    /**
     * Set an item in the inventory
     */
    protected void setItem(int slot, @NotNull ItemStack item) {
        if (slot >= 0 && slot < size) {
            inventory.setItem(slot, item);
        }
    }
    
    /**
     * Get the inventory
     */
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
    
    /**
     * Handle inventory click events
     * This method is called by the GUI listener
     */
    public void onClick(InventoryClickEvent event) {
        // Default behavior - cancel the event and delegate to handleClick
        event.setCancelled(true);
        handleClick(event);
    }
    
    /**
     * Refresh the GUI
     * Clears and rebuilds the GUI
     */
    public void refresh() {
        inventory.clear();
        setupItems();
    }
}