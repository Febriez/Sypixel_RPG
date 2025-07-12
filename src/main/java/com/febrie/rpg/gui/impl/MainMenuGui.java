package com.febrie.rpg.gui.impl;

import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Main menu GUI implementation with full color placeholder support
 * Central hub for accessing all RPG features
 *
 * @author Febrie, CoffeeTory
 */
public class MainMenuGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6 rows (통일성을 위해 변경)

    public MainMenuGui(@NotNull GuiManager guiManager,
                       @NotNull LangManager langManager, @NotNull Player player) {
        super(player, guiManager, langManager, GUI_SIZE, "gui.mainmenu.title");
        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return trans("gui.mainmenu.title");
    }

    @Override
    public int getSize() {
        return GUI_SIZE;
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
        setupMenuButtons();
        setupNavigationButtons();
    }

    /**
     * Sets up decorative elements
     */
    private void setupDecorations() {
        // Create border with default material
        createBorder();

        // Title item
        setItem(4, GuiItem.display(
                ItemBuilder.of(Material.NETHER_STAR)
                        .displayName(trans("gui.mainmenu.title")
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(trans("gui.mainmenu.subtitle"))
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
                        .displayName(trans("items.mainmenu.profile-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.profile-button.lore"))
                        .build(),
                guiManager::openProfileGui
        );
        setItem(20, profileButton);

        // Shop button (slot 22)
        GuiItem shopButton = GuiItem.clickable(
                ItemBuilder.of(Material.EMERALD)
                        .displayName(trans("items.mainmenu.shop-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.shop-button.lore"))
                        .build(),
                clickedPlayer -> {
                    sendMessage(clickedPlayer, "general.coming-soon");
                    // TODO: guiManager.openShopGui(clickedPlayer);
                }
        );
        setItem(22, shopButton);

        // Dungeon button (slot 24)
        GuiItem dungeonButton = GuiItem.clickable(
                ItemBuilder.of(Material.IRON_SWORD)
                        .displayName(trans("items.mainmenu.dungeon-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.dungeon-button.lore"))
                        .build(),
                clickedPlayer -> {
                    sendMessage(clickedPlayer, "general.coming-soon");
                    // TODO: guiManager.openDungeonGui(clickedPlayer);
                }
        );
        setItem(24, dungeonButton);

        // Settings button (slot 31)
        GuiItem settingsButton = GuiItem.clickable(
                ItemBuilder.of(Material.COMPARATOR)
                        .displayName(trans("items.mainmenu.settings-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.settings-button.lore"))
                        .build(),
                clickedPlayer -> {
                    sendMessage(clickedPlayer, "general.coming-soon");
                    // TODO: guiManager.openSettingsGui(clickedPlayer);
                }
        );
        setItem(31, settingsButton);

        // Stats Management button (slot 29)
        GuiItem statsButton = GuiItem.clickable(
                ItemBuilder.of(Material.IRON_SWORD)
                        .displayName(trans("items.mainmenu.stats-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.stats-button.lore"))
                        .flags(org.bukkit.inventory.ItemFlag.values())
                        .build(),
                clickedPlayer -> {
                    com.febrie.rpg.player.RPGPlayer rpgPlayer = com.febrie.rpg.RPGMain.getPlugin()
                            .getRPGPlayerManager().getOrCreatePlayer(clickedPlayer);

                    if (!rpgPlayer.hasJob()) {
                        sendMessage(clickedPlayer, "messages.no-job-for-stats");
                        return;
                    }

                    // closeInventory() 제거
                    com.febrie.rpg.gui.impl.StatsGui statsGui = new com.febrie.rpg.gui.impl.StatsGui(
                            guiManager, langManager, clickedPlayer, rpgPlayer);
                    statsGui.open(clickedPlayer);
                }
        );
        setItem(29, statsButton);

        // Talent Management button (slot 33)
        GuiItem talentsButton = GuiItem.clickable(
                ItemBuilder.of(Material.ENCHANTED_BOOK)
                        .displayName(trans("items.mainmenu.talents-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.talents-button.lore"))
                        .build(),
                clickedPlayer -> {
                    com.febrie.rpg.player.RPGPlayer rpgPlayer = com.febrie.rpg.RPGMain.getPlugin()
                            .getRPGPlayerManager().getOrCreatePlayer(clickedPlayer);

                    if (!rpgPlayer.hasJob()) {
                        sendMessage(clickedPlayer, "messages.no-job-for-talents");

                        // Ask if they want to choose a job - closeInventory() 제거
                        com.febrie.rpg.gui.impl.JobSelectionGui jobGui = new com.febrie.rpg.gui.impl.JobSelectionGui(
                                guiManager, langManager, clickedPlayer, rpgPlayer);
                        jobGui.open(clickedPlayer);
                        return;
                    }

                    // closeInventory() 제거
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
     * Sets up navigation buttons - 위치 통일
     */
    private void setupNavigationButtons() {
        // Back button (45번), Refresh button (-1 없음), Close button (53번)
        setupNavigationButtons(45, -1, 53);
    }
}