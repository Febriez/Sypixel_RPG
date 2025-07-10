package com.febrie.rpg.gui.listener;

import com.febrie.rpg.gui.framework.DisplayGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.framework.InteractiveGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Global event listener for all GUI interactions
 * Handles routing events to appropriate GUI implementations
 *
 * @author Febrie, CoffeeTory
 */
public class GuiListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        Inventory inventory = event.getClickedInventory();
        if (inventory == null) {
            return;
        }

        InventoryHolder holder = inventory.getHolder(false);

        // Handle InteractiveGui
        if (holder instanceof InteractiveGui interactiveGui) {
            event.setCancelled(true); // Cancel by default for interactive GUIs

            int slot = event.getSlot();

            // Check if slot is clickable
            if (!interactiveGui.isSlotClickable(slot, player)) {
                return;
            }

            // Handle the click
            interactiveGui.onSlotClick(event, player, slot, event.getClick());
            return;
        }

        // Handle DisplayGui
        if (holder instanceof DisplayGui displayGui) {
            if (displayGui.preventItemMovement()) {
                event.setCancelled(true);
            }

            if (displayGui.closeOnClick()) {
                player.closeInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder(false);

        // Handle DisplayGui
        if (holder instanceof DisplayGui displayGui) {
            if (displayGui.preventDragging()) {
                event.setCancelled(true);
            }
            return;
        }

        // Cancel dragging for all other custom GUIs
        if (holder instanceof GuiFramework) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder(false);

        // Handle InteractiveGui close event
        if (holder instanceof InteractiveGui interactiveGui) {
            interactiveGui.onClosed(player);
        }
    }
}