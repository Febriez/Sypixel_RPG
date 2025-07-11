package com.febrie.rpg.gui.impl;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.InteractiveGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Main menu GUI implementation with full color placeholder support
 * Central hub for accessing all RPG features
 * <p>
 * Updated: All GuiFactory calls now use LangManager-based methods
 *
 * @author Febrie, CoffeeTory
 */
public class MainMenuGui implements InteractiveGui {

    private static final int GUI_SIZE = 45; // 5 rows

    private final GuiManager guiManager;
    private final LangManager langManager;
    private final Player player;
    private final Inventory inventory;
    private final Map<Integer, GuiItem> items;

    public MainMenuGui(@NotNull GuiManager guiManager,
                       @NotNull LangManager langManager, @NotNull Player player) {
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, GUI_SIZE,
                langManager.getComponent(player, "gui.mainmenu.title"));
        this.items = new HashMap<>();

        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return langManager.getComponent(player, "gui.mainmenu.title");
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
        // Main menu cleanup if needed
    }

    /**
     * Sets up the main menu layout
     */
    private void setupLayout() {
        setupDecorations();
        setupMenuButtons();
        setupNavigationButtons();
    }

    /**
     * Sets up decorative elements
     */
    private void setupDecorations() {
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
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(langManager.getComponent(player, "gui.mainmenu.subtitle"))
                        .build()
        ));
    }

    /**
     * Sets up main menu buttons
     */
    private void setupMenuButtons() {
        // Profile button (slot 20)
        GuiItem profileButton = GuiItem.clickable(
                ItemBuilder.of(Material.PLAYER_HEAD)
                        .displayName(langManager.getComponent(player, "items.mainmenu.profile-button.name"))
                        .lore(langManager.getComponentList(player, "items.mainmenu.profile-button.lore"))
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
                        .lore(langManager.getComponentList(player, "items.mainmenu.shop-button.lore"))
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
                        .lore(langManager.getComponentList(player, "items.mainmenu.dungeon-button.lore"))
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
                        .lore(langManager.getComponentList(player, "items.mainmenu.settings-button.lore"))
                        .build(),
                clickedPlayer -> {
                    langManager.sendMessage(clickedPlayer, "general.coming-soon");
                    // TODO: guiManager.openSettingsGui(clickedPlayer);
                }
        );
        setItem(31, settingsButton);

        // Stats Management button (slot 29)
        GuiItem statsButton = GuiItem.clickable(
                ItemBuilder.of(Material.IRON_SWORD)
                        .displayName(langManager.getComponent(player, "items.mainmenu.stats-button.name"))
                        .lore(langManager.getComponentList(player, "items.mainmenu.stats-button.lore"))
                        .flags(org.bukkit.inventory.ItemFlag.values())
                        .build(),
                clickedPlayer -> {
                    com.febrie.rpg.player.RPGPlayer rpgPlayer = com.febrie.rpg.RPGMain.getPlugin()
                            .getRPGPlayerManager().getOrCreatePlayer(clickedPlayer);

                    if (!rpgPlayer.hasJob()) {
                        langManager.sendMessage(clickedPlayer, "messages.no-job-for-stats");
                        return;
                    }

                    clickedPlayer.closeInventory();
                    com.febrie.rpg.gui.impl.StatsGui statsGui = new com.febrie.rpg.gui.impl.StatsGui(
                            guiManager, langManager, clickedPlayer, rpgPlayer);
                    statsGui.open(clickedPlayer);
                }
        );
        setItem(29, statsButton);

        // Talent Management button (slot 33)
        GuiItem talentsButton = GuiItem.clickable(
                ItemBuilder.of(Material.ENCHANTED_BOOK)
                        .displayName(langManager.getComponent(player, "items.mainmenu.talents-button.name"))
                        .lore(langManager.getComponentList(player, "items.mainmenu.talents-button.lore"))
                        .build(),
                clickedPlayer -> {
                    com.febrie.rpg.player.RPGPlayer rpgPlayer = com.febrie.rpg.RPGMain.getPlugin()
                            .getRPGPlayerManager().getOrCreatePlayer(clickedPlayer);

                    if (!rpgPlayer.hasJob()) {
                        langManager.sendMessage(clickedPlayer, "messages.no-job-for-talents");

                        // Ask if they want to choose a job
                        clickedPlayer.closeInventory();
                        com.febrie.rpg.gui.impl.JobSelectionGui jobGui = new com.febrie.rpg.gui.impl.JobSelectionGui(
                                guiManager, langManager, clickedPlayer, rpgPlayer);
                        jobGui.open(clickedPlayer);
                        return;
                    }

                    clickedPlayer.closeInventory();
                    java.util.List<com.febrie.rpg.talent.Talent> mainTalents = com.febrie.rpg.RPGMain.getPlugin()
                            .getTalentManager().getJobMainTalents(rpgPlayer.getJob());
                    com.febrie.rpg.gui.impl.TalentGui talentGui = new com.febrie.rpg.gui.impl.TalentGui(
                            guiManager, langManager, clickedPlayer, rpgPlayer, "main", mainTalents);
                    talentGui.open(clickedPlayer);
                }
        );
        setItem(33, talentsButton);
    }

    /**
     * Sets up navigation buttons
     */
    private void setupNavigationButtons() {
        // Close button (slot 40)
        setItem(40, GuiFactory.createCloseButton(langManager, player));

        // Back button (slot 38) - if there's history
        setItem(38, GuiFactory.createBackButton(guiManager, langManager, player));
    }

    /**
     * Sets an item at the specified slot
     */
    private void setItem(int slot, @NotNull GuiItem item) {
        items.put(slot, item);
        inventory.setItem(slot, item.getItemStack());
    }
}