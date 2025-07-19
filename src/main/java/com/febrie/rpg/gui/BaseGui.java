package com.febrie.rpg.gui;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
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
    protected Inventory inventory;
    protected final int size;
    protected Player viewer;
    private boolean initialized = false;

    /**
     * Constructor - protected to prevent direct instantiation
     * Use the create() factory method instead
     *
     * @param plugin The main plugin instance
     * @param size   The size of the inventory (must be multiple of 9)
     */
    protected BaseGui(@NotNull RPGMain plugin, int size) {
        this.plugin = plugin;
        this.size = size;
        // Inventory will be created during initialization
    }

    /**
     * Initialize the GUI - must be called after construction
     * This method creates the inventory and sets up the items
     *
     * @param title The title of the GUI
     */
    protected void initialize(@NotNull Component title) {
        if (initialized) {
            throw new IllegalStateException("GUI already initialized");
        }
        this.inventory = Bukkit.createInventory(this, size, title);
        setupItems();
        this.initialized = true;
    }

    /**
     * Factory method to create and initialize a GUI
     * This ensures the GUI is fully constructed before 'this' escapes
     *
     * @param gui The GUI instance to initialize
     * @param title The title of the GUI
     * @return The initialized GUI instance
     */
    protected static <T extends BaseGui> T create(@NotNull T gui, @NotNull Component title) {
        gui.initialize(title);
        return gui;
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
        ensureInitialized();
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
        ensureInitialized();
        player.openInventory(inventory);
    }

    /**
     * Ensure the GUI has been initialized
     */
    private void ensureInitialized() {
        if (!initialized) {
            throw new IllegalStateException("GUI not initialized. Call initialize() first.");
        }
    }

    /**
     * Fill the border of the GUI with a specific material
     */
    protected void fillBorder(@NotNull Material material) {
        ItemStack borderItem = new ItemBuilder(material)
                .displayName(Component.empty())
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                .hideTooltip(true)
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
        ensureInitialized();
        inventory.clear();
        setupItems();
    }

    /**
     * Check if the GUI has been initialized
     */
    public boolean isInitialized() {
        return initialized;
    }
}