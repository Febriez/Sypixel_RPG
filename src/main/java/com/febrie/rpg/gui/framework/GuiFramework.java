package com.febrie.rpg.gui.framework;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Base interface for all GUI implementations in the RPG system
 * Provides core functionality for inventory-based user interfaces
 *
 * @author Febrie, CoffeeTory
 */
public interface GuiFramework extends InventoryHolder {

    /**
     * Gets the title of this GUI
     *
     * @return The GUI title component
     */
    @NotNull Component getTitle();

    /**
     * Gets the size of this GUI (number of slots)
     *
     * @return The GUI size (must be multiple of 9, max 54)
     */
    int getSize();

    /**
     * Opens this GUI for a player
     *
     * @param player The player to open the GUI for
     */
    void open(@NotNull Player player);

    /**
     * Closes this GUI for a player
     *
     * @param player The player to close the GUI for
     */
    default void close(@NotNull Player player) {
        player.closeInventory();
    }

    /**
     * Updates the GUI content
     * Called when the GUI needs to refresh its items
     */
    void refresh();

    /**
     * Checks if this GUI supports the given player
     * Used for permission or condition checks
     *
     * @param player The player to check
     * @return true if the player can use this GUI
     */
    default boolean canUse(@NotNull Player player) {
        return true;
    }

    /**
     * Gets the GUI type identifier
     * Used for tracking and management
     *
     * @return The GUI type name
     */
    default String getType() {
        return this.getClass().getSimpleName();
    }
}