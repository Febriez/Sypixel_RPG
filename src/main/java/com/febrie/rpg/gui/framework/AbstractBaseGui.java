package com.febrie.rpg.gui.framework;

import com.febrie.rpg.gui.component.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for GUI implementations
 * Provides common functionality for inventory management
 *
 * @author Febrie, CoffeeTory
 */
public abstract class AbstractBaseGui implements InteractiveGui {

    protected final Plugin plugin;
    protected final Inventory inventory;
    protected final Map<Integer, GuiItem> items;

    protected AbstractBaseGui(@NotNull Plugin plugin, int size, @NotNull Component title) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, size, title);
        this.items = new HashMap<>();
    }

    @Override
    public void open(@NotNull Player player) {
        player.openInventory(inventory);
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

    /**
     * Sets an item at the specified slot
     */
    protected void setItem(int slot, @NotNull GuiItem item) {
        items.put(slot, item);
        inventory.setItem(slot, item.getItemStack());
    }

    /**
     * Removes an item from the specified slot
     */
    protected void removeItem(int slot) {
        items.remove(slot);
        inventory.clear(slot);
    }

    /**
     * Gets the item at the specified slot
     */
    protected GuiItem getItem(int slot) {
        return items.get(slot);
    }

    /**
     * Clears all items from the GUI
     */
    protected void clearItems() {
        items.clear();
        inventory.clear();
    }

    /**
     * Fills empty slots with a filler item
     */
    protected void fillEmpty(@NotNull GuiItem filler) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (!items.containsKey(i)) {
                setItem(i, filler);
            }
        }
    }

    /**
     * Sets border items around the GUI
     */
    protected void setBorder(@NotNull GuiItem borderItem) {
        int size = inventory.getSize();
        int rows = size / 9;

        // Top and bottom rows
        for (int i = 0; i < 9; i++) {
            setItem(i, borderItem); // Top row
            if (rows > 1) {
                setItem(size - 9 + i, borderItem); // Bottom row
            }
        }

        // Side columns
        for (int row = 1; row < rows - 1; row++) {
            setItem(row * 9, borderItem); // Left column
            setItem(row * 9 + 8, borderItem); // Right column
        }
    }
}