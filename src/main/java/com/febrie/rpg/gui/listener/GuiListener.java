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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global event listener for all GUI interactions
 * Handles routing events to appropriate GUI implementations
 *
 * @author Febrie, CoffeeTory
 */
public class GuiListener implements Listener {

    // 클릭 쿨타임 (밀리초)
    private static final long CLICK_COOLDOWN_MS = 400;

    // 플레이어별 마지막 클릭 시간 저장
    private final Map<UUID, Long> lastClickTimes = new ConcurrentHashMap<>();

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

            // 클릭 쿨타임 체크
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            Long lastClickTime = lastClickTimes.get(playerId);

            if (lastClickTime != null && (currentTime - lastClickTime) < CLICK_COOLDOWN_MS) {
                return; // 쿨타임 중이면 클릭 무시
            }

            // 클릭 시간 업데이트
            lastClickTimes.put(playerId, currentTime);

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

        // 플레이어가 GUI를 닫을 때 쿨타임 정보 제거 (메모리 최적화)
        lastClickTimes.remove(player.getUniqueId());

        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder(false);

        // Handle InteractiveGui close event
        if (holder instanceof InteractiveGui interactiveGui) {
            interactiveGui.onClosed(player);
        }
    }
}