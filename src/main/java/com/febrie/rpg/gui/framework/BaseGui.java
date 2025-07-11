package com.febrie.rpg.gui.framework;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Base abstract class for all GUI implementations
 * Provides common functionality to reduce code duplication
 *
 * @author Febrie, CoffeeTory
 */
public abstract class BaseGui implements InteractiveGui {

    protected final GuiManager guiManager;
    protected final LangManager langManager;
    protected final Player viewer;
    protected final Inventory inventory;
    protected final Map<Integer, GuiItem> items = new HashMap<>();

    /**
     * Creates a new BaseGui
     *
     * @param viewer      The player viewing the GUI
     * @param guiManager  The GUI manager (nullable)
     * @param langManager The language manager
     * @param size        The inventory size (must be multiple of 9)
     * @param titleKey    The language key for the title
     * @param titleArgs   Arguments for the title translation
     */
    protected BaseGui(@NotNull Player viewer, @Nullable GuiManager guiManager,
                      @NotNull LangManager langManager, int size,
                      @NotNull String titleKey, @NotNull String... titleArgs) {
        this.viewer = viewer;
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.inventory = Bukkit.createInventory(this, size,
                langManager.getComponent(viewer, titleKey, titleArgs));
    }

    @Override
    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    @Override
    public void refresh() {
        inventory.clear();
        items.clear();
        setupLayout();
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
     * Sets up the GUI layout. Must be implemented by subclasses.
     */
    protected abstract void setupLayout();

    /**
     * Sets an item at the specified slot
     *
     * @param slot The slot index
     * @param item The GUI item to set
     */
    protected void setItem(int slot, @NotNull GuiItem item) {
        items.put(slot, item);
        inventory.setItem(slot, item.getItemStack());
    }

    /**
     * Fills a range of slots with the same item
     *
     * @param startSlot The starting slot (inclusive)
     * @param endSlot   The ending slot (inclusive)
     * @param item      The item to fill with
     */
    protected void fillSlots(int startSlot, int endSlot, @NotNull GuiItem item) {
        for (int i = startSlot; i <= endSlot; i++) {
            setItem(i, item);
        }
    }

    /**
     * Creates a standard border around the GUI
     *
     * @param material The material for the border (default: GRAY_STAINED_GLASS_PANE)
     */
    protected void createBorder(@NotNull Material material) {
        GuiItem borderItem = GuiFactory.createDecoration(material);

        // Top row
        fillSlots(0, 8, borderItem);

        // Bottom row
        fillSlots(getSize() - 9, getSize() - 1, borderItem);

        // Left and right columns
        for (int row = 1; row < (getSize() / 9) - 1; row++) {
            setItem(row * 9, borderItem);
            setItem(row * 9 + 8, borderItem);
        }
    }

    /**
     * Creates a standard border with default material
     */
    protected void createBorder() {
        createBorder(Material.GRAY_STAINED_GLASS_PANE);
    }

    /**
     * Sets up standard navigation buttons
     *
     * @param backSlot    The slot for back button (-1 to skip)
     * @param refreshSlot The slot for refresh button (-1 to skip)
     * @param closeSlot   The slot for close button (-1 to skip)
     */
    protected void setupNavigationButtons(int backSlot, int refreshSlot, int closeSlot) {
        // Back button
        if (backSlot >= 0 && guiManager != null) {
            setItem(backSlot, GuiFactory.createBackButton(guiManager, langManager, viewer));
        }

        // Refresh button
        if (refreshSlot >= 0) {
            if (guiManager != null) {
                setItem(refreshSlot, GuiFactory.createRefreshButton(guiManager, langManager, viewer));
            } else {
                setItem(refreshSlot, GuiFactory.createRefreshButton(player -> refresh(), langManager, viewer));
            }
        }

        // Close button
        if (closeSlot >= 0) {
            setItem(closeSlot, GuiFactory.createCloseButton(langManager, viewer));
        }
    }

    /**
     * Gets a translatable component for the viewer
     *
     * @param key  The translation key
     * @param args The arguments
     * @return The translated component
     */
    protected Component trans(@NotNull String key, @NotNull String... args) {
        return langManager.getComponent(viewer, key, args);
    }

    /**
     * Gets a translated message for the viewer
     *
     * @param key  The translation key
     * @param args The arguments
     * @return The translated string
     */
    protected String transString(@NotNull String key, @NotNull String... args) {
        return langManager.getMessage(viewer, key, args);
    }

    /**
     * Sends a translated message to a player
     *
     * @param player The player to send to
     * @param key    The translation key
     * @param args   The arguments
     */
    protected void sendMessage(@NotNull Player player, @NotNull String key, @NotNull String... args) {
        langManager.sendMessage(player, key, args);
    }

    /**
     * Plays a success sound to the player
     *
     * @param player The player
     */
    protected void playSuccessSound(@NotNull Player player) {
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    /**
     * Plays an error sound to the player
     *
     * @param player The player
     */
    protected void playErrorSound(@NotNull Player player) {
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
    }

    /**
     * Plays a click sound to the player
     *
     * @param player The player
     */
    protected void playClickSound(@NotNull Player player) {
        player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
    }
}