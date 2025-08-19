package com.febrie.rpg.gui.framework;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
/**
 * Interface for GUI implementations that handle player interactions
 * Extends GuiFramework with click handling capabilities
 *
 * @author Febrie, CoffeeTory
 */
public interface InteractiveGui extends GuiFramework {

    /**
     * Handles click events for this GUI
     *
     * @param event  The click event
     * @param player The player who clicked
     * @param slot   The slot that was clicked
     * @param click  The type of click performed
     */
    void onSlotClick(@NotNull InventoryClickEvent event, @NotNull Player player, int slot, @NotNull ClickType click);

    /**
     * Checks if the given slot can be clicked
     *
     * @param slot   The slot to check
     * @param player The player attempting to click
     * @return true if the slot is clickable
     */
    default boolean isSlotClickable(int slot, @NotNull Player player) {
        return true;
    }

    /**
     * Called when a player attempts to close this GUI
     *
     * @param player The player attempting to close
     * @return true to allow closing, false to prevent
     */
    default boolean onCloseAttempt(@NotNull Player player) {
        return true;
    }

    /**
     * Called when the GUI is actually closed
     *
     * @param player The player who closed the GUI
     */
    default void onClosed(@NotNull Player player) {
        // Override for cleanup logic
    }
}