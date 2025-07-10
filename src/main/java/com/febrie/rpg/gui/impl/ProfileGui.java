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
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Player profile GUI implementation with internationalization support
 * Shows player statistics, information, and provides access to various features
 *
 * @author Febrie, CoffeeTory
 */
public class ProfileGui implements InteractiveGui {

    private static final int GUI_SIZE = 54; // 6 rows

    private final Player targetPlayer;
    private final Inventory inventory;
    private final Map<Integer, GuiItem> items;
    private final GuiManager guiManager;
    private final LangManager langManager;

    /**
     * Creates a new ProfileGui for a specific player with language support
     */
    public ProfileGui(@NotNull Player targetPlayer,
                      @Nullable GuiManager guiManager, @NotNull LangManager langManager) {
        this.targetPlayer = targetPlayer;
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.inventory = Bukkit.createInventory(this, GUI_SIZE,
                langManager.getComponent(targetPlayer, "gui.profile.player-title", "player", targetPlayer.getName()));
        this.items = new HashMap<>();

        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return langManager.getComponent(targetPlayer, "gui.profile.player-title", "player", targetPlayer.getName());
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

    @Override
    public void onClosed(@NotNull Player player) {
        // Profile GUI cleanup logic if needed
    }

    /**
     * Gets the target player whose profile this GUI shows
     */
    public Player getTargetPlayer() {
        return targetPlayer;
    }

    /**
     * Sets up the complete GUI layout
     */
    private void setupLayout() {
        setupDecorations();
        setupPlayerInfo();
        setupStatsSection();
        setupActionButtons();
    }

    /**
     * Sets up decorative elements and borders
     */
    private void setupDecorations() {
        // Top and bottom borders
        for (int i = 0; i < 9; i++) {
            if (i != 4) { // Skip title slot
                setItem(i, GuiFactory.createDecoration());
                setItem(45 + i, GuiFactory.createDecoration());
            }
        }

        // Side borders
        int[] sideBorders = {9, 17, 18, 26, 27, 35, 36, 44};
        for (int slot : sideBorders) {
            setItem(slot, GuiFactory.createDecoration());
        }

        // Title decoration
        setItem(4, GuiItem.display(
                ItemBuilder.of(Material.NETHER_STAR)
                        .displayName(Component.text("★ " + targetPlayer.getName() + " ★", ColorUtil.LEGENDARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore("", NamedTextColor.GRAY)
                        .addLore(langManager.getMessage(targetPlayer, "gui.profile.title"), NamedTextColor.YELLOW)
                        .build()
        ));
    }

    /**
     * Sets up player head and basic info
     */
    private void setupPlayerInfo() {
        GuiItem playerHead = GuiItem.clickable(
                ItemBuilder.of(Material.PLAYER_HEAD)
                        .displayName(langManager.getComponent(targetPlayer, "items.profile.player-head.name",
                                "player", targetPlayer.getName()))
                        .lore(langManager.getComponentList(targetPlayer, "items.profile.player-head.lore",
                                "player", targetPlayer.getName(),
                                "uuid", targetPlayer.getUniqueId().toString().substring(0, 8) + "...",
                                "playtime", formatPlayTime()))
                        .build(),
                player -> {
                    langManager.sendMessage(player, "general.coming-soon");
                }
        );

        setItem(13, playerHead);
    }

    /**
     * Sets up the statistics section
     */
    private void setupStatsSection() {
        // Level info (slot 19)
        GuiItem levelItem = GuiItem.display(
                ItemBuilder.of(Material.EXPERIENCE_BOTTLE)
                        .displayName(langManager.getComponent(targetPlayer, "items.profile.level-info.name"))
                        .lore(langManager.getComponentList(targetPlayer, "items.profile.level-info.lore",
                                "level", String.valueOf(targetPlayer.getLevel()),
                                "exp", String.valueOf(Math.round(targetPlayer.getExp() * 100)),
                                "total_exp", String.valueOf(targetPlayer.getTotalExperience())))
                        .build()
        );
        setItem(19, levelItem);

        // Health info (slot 21)
        AttributeInstance maxHealthAttr = targetPlayer.getAttribute(Attribute.MAX_HEALTH);
        double maxHealth = maxHealthAttr != null ? maxHealthAttr.getValue() : 20.0;
        double currentHealth = targetPlayer.getHealth();
        double healthPercentage = (currentHealth / maxHealth) * 100;

        GuiItem healthItem = GuiItem.display(
                ItemBuilder.of(Material.RED_DYE)
                        .displayName(langManager.getComponent(targetPlayer, "items.profile.health-info.name"))
                        .lore(langManager.getComponentList(targetPlayer, "items.profile.health-info.lore",
                                "current", String.format("%.1f", currentHealth),
                                "max", String.format("%.1f", maxHealth),
                                "percentage", String.format("%.1f", healthPercentage),
                                "health_bar", createHealthBar(healthPercentage)))
                        .build()
        );
        setItem(21, healthItem);

        // Food info (slot 23)
        GuiItem foodItem = GuiItem.display(
                ItemBuilder.of(Material.BREAD)
                        .displayName(langManager.getComponent(targetPlayer, "items.profile.food-info.name"))
                        .lore(langManager.getComponentList(targetPlayer, "items.profile.food-info.lore",
                                "food", String.valueOf(targetPlayer.getFoodLevel()),
                                "saturation", String.format("%.1f", targetPlayer.getSaturation()),
                                "hunger_bar", createHungerBar(targetPlayer.getFoodLevel())))
                        .build()
        );
        setItem(23, foodItem);

        // Game mode info (slot 25)
        String gameModeName = langManager.getMessage(targetPlayer, "gamemode." + targetPlayer.getGameMode().name());
        String canFly = langManager.getMessage(targetPlayer, targetPlayer.getAllowFlight() ? "status.yes" : "status.no");

        GuiItem gameModeItem = GuiItem.display(
                ItemBuilder.of(Material.COMPASS)
                        .displayName(langManager.getComponent(targetPlayer, "items.profile.game-info.name"))
                        .lore(langManager.getComponentList(targetPlayer, "items.profile.game-info.lore",
                                "gamemode", gameModeName,
                                "can_fly", canFly,
                                "world", targetPlayer.getWorld().getName()))
                        .build()
        );
        setItem(25, gameModeItem);
    }

    /**
     * Sets up action buttons
     */
    private void setupActionButtons() {
        // Settings button (slot 47)
        GuiItem settingsButton = GuiItem.clickable(
                ItemBuilder.of(Material.COMPARATOR)
                        .displayName(langManager.getComponent(targetPlayer, "items.profile.settings-button.name"))
                        .lore(langManager.getComponentList(targetPlayer, "items.profile.settings-button.lore"))
                        .build(),
                player -> {
                    langManager.sendMessage(player, "general.coming-soon");
                }
        );
        setItem(47, settingsButton);

        // Stats button (slot 48)
        GuiItem statsButton = GuiItem.clickable(
                ItemBuilder.of(Material.BOOK)
                        .displayName(langManager.getComponent(targetPlayer, "items.profile.stats-button.name"))
                        .lore(langManager.getComponentList(targetPlayer, "items.profile.stats-button.lore"))
                        .build(),
                player -> {
                    langManager.sendMessage(player, "general.coming-soon");
                }
        );
        setItem(48, statsButton);

        // Close button (slot 49)
        setItem(49, GuiFactory.createCloseButton(langManager, targetPlayer));

        // Back button (slot 50) - if GuiManager is available
        if (guiManager != null) {
            setItem(50, GuiFactory.createBackButton(guiManager, langManager, targetPlayer));
        }

        // Refresh button (slot 51)
        if (guiManager != null) {
            setItem(51, GuiFactory.createRefreshButton(guiManager, langManager, targetPlayer));
        } else {
            GuiItem refreshButton = GuiFactory.createRefreshButton(player -> {
                refresh();
                langManager.sendMessage(player, "messages.profile-opened");
            }, langManager, targetPlayer);
            setItem(51, refreshButton);
        }
    }

    /**
     * Sets an item at the specified slot
     */
    private void setItem(int slot, @NotNull GuiItem item) {
        items.put(slot, item);
        inventory.setItem(slot, item.getItemStack());
    }

    // Helper methods for display
    private String formatPlayTime() {
        return langManager.getMessage(targetPlayer, "status.unknown");
    }

    private String createHealthBar(double percentage) {
        int bars = 20;
        int filled = (int) (percentage / 100.0 * bars);
        return "█".repeat(Math.max(0, filled)) + "░".repeat(Math.max(0, bars - filled));
    }

    private String createHungerBar(int foodLevel) {
        int bars = 20;
        int filled = foodLevel;
        return "🍖".repeat(Math.max(0, filled)) + "░".repeat(Math.max(0, bars - filled));
    }
}