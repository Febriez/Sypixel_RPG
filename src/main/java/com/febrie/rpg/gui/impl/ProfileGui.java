package com.febrie.rpg.gui.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Player profile GUI implementation with internationalization support
 * Shows player statistics, information, and provides access to various features
 *
 * @author Febrie, CoffeeTory
 */
public class ProfileGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6 rows
    private final Player targetPlayer;

    /**
     * Creates a new ProfileGui for a specific player with language support
     */
    public ProfileGui(@NotNull Player targetPlayer,
                      @Nullable GuiManager guiManager, @NotNull LangManager langManager) {
        this(targetPlayer, targetPlayer, guiManager, langManager);
    }

    /**
     * Creates a new ProfileGui for viewing another player's profile
     */
    public ProfileGui(@NotNull Player targetPlayer, @NotNull Player viewer,
                      @Nullable GuiManager guiManager, @NotNull LangManager langManager) {
        super(viewer, guiManager, langManager, GUI_SIZE, "gui.profile.player-title",
                "player", targetPlayer.getName());
        this.targetPlayer = targetPlayer;
        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return trans("gui.profile.player-title", "player", targetPlayer.getName());
    }

    @Override
    public int getSize() {
        return GUI_SIZE;
    }

    /**
     * Gets the target player whose profile this GUI shows
     */
    public Player getTargetPlayer() {
        return targetPlayer;
    }

    @Override
    protected void setupLayout() {
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
            }
        }

        // Bottom border - excluding navigation buttons
        for (int i = 45; i < 54; i++) {
            if (i != 45 && i != 49 && i != 53) {
                setItem(i, GuiFactory.createDecoration());
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
                        .addLore(Component.empty())
                        .addLore(trans("gui.profile.title"))
                        .build()
        ));
    }

    /**
     * Sets up player head and basic info
     */
    private void setupPlayerInfo() {
        GuiItem playerHead = GuiItem.clickable(
                ItemBuilder.of(Material.PLAYER_HEAD)
                        .displayName(trans("items.profile.player-head.name",
                                "player", targetPlayer.getName()))
                        .lore(langManager.getComponentList(viewer, "items.profile.player-head.lore",
                                "player", targetPlayer.getName(),
                                "uuid", targetPlayer.getUniqueId().toString().substring(0, 8) + "...",
                                "playtime", formatPlayTime()))
                        .build(),
                player -> sendMessage(player, "general.coming-soon")
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
                        .displayName(trans("items.profile.level-info.name"))
                        .lore(langManager.getComponentList(viewer, "items.profile.level-info.lore",
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
                        .displayName(trans("items.profile.health-info.name"))
                        .lore(langManager.getComponentList(viewer, "items.profile.health-info.lore",
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
                        .displayName(trans("items.profile.food-info.name"))
                        .lore(langManager.getComponentList(viewer, "items.profile.food-info.lore",
                                "food", String.valueOf(targetPlayer.getFoodLevel()),
                                "saturation", String.format("%.1f", targetPlayer.getSaturation()),
                                "hunger_bar", createHungerBar(targetPlayer.getFoodLevel())))
                        .build()
        );
        setItem(23, foodItem);

        // Game mode info (slot 25)
        String gameModeName = transString("gamemode." + targetPlayer.getGameMode().name());
        String canFly = transString(targetPlayer.getAllowFlight() ? "status.flight.yes" : "status.flight.no");

        GuiItem gameModeItem = GuiItem.display(
                ItemBuilder.of(Material.COMPASS)
                        .displayName(trans("items.profile.game-info.name"))
                        .lore(langManager.getComponentList(viewer, "items.profile.game-info.lore",
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
        com.febrie.rpg.player.RPGPlayer rpgPlayer = RPGMain.getPlugin()
                .getRPGPlayerManager().getOrCreatePlayer(targetPlayer);

        // Job selection or display (slot 31)
        if (!rpgPlayer.hasJob()) {
            GuiItem jobButton = GuiItem.clickable(
                    ItemBuilder.of(Material.ENCHANTING_TABLE)
                            .displayName(trans("items.mainmenu.job-button.name"))
                            .lore(langManager.getComponentList(viewer, "items.mainmenu.job-button.lore"))
                            .build(),
                    player -> {
                        if (player.equals(targetPlayer)) {
                            new JobSelectionGui(guiManager, langManager, player, rpgPlayer).open(player);
                        } else {
                            sendMessage(player, "general.coming-soon");
                        }
                    }
            );
            setItem(31, jobButton);
        } else {
            // Show current job info - JobType.getMaterial() 사용
            String jobKey = rpgPlayer.getJob().name().toLowerCase();
            GuiItem jobInfo = GuiItem.display(
                    ItemBuilder.of(rpgPlayer.getJob().getMaterial())
                            .displayName(Component.text(rpgPlayer.getJob().getIcon() + " ")
                                    .append(trans("job." + jobKey + ".name"))
                                    .color(rpgPlayer.getJob().getColor())
                                    .decoration(TextDecoration.BOLD, true))
                            .addLore(Component.empty())
                            .addLore(trans("gui.talent.level", "level",
                                    String.valueOf(rpgPlayer.getLevel()) + " / " + rpgPlayer.getJob().getMaxLevel()))
                            .addLore(trans("gui.stats.combat-power", "power", String.valueOf(rpgPlayer.getCombatPower())))
                            .addLore(Component.empty())
                            .lore(langManager.getComponentList(viewer, "job." + jobKey + ".description"))
                            .build()
            );
            setItem(31, jobInfo);
        }

        // Settings button (slot 47)
        GuiItem settingsButton = GuiItem.clickable(
                ItemBuilder.of(Material.COMPARATOR)
                        .displayName(trans("items.profile.settings-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.profile.settings-button.lore"))
                        .build(),
                player -> sendMessage(player, "general.coming-soon")
        );
        setItem(47, settingsButton);

        // Stats button (slot 48) - only show if has job
        if (rpgPlayer.hasJob()) {
            GuiItem statsButton = GuiItem.clickable(
                    ItemBuilder.of(Material.BOOK)
                            .displayName(trans("items.profile.stats-button.name"))
                            .lore(langManager.getComponentList(viewer, "items.profile.stats-button.lore"))
                            .build(),
                    player -> new StatsGui(guiManager, langManager, player,
                            Objects.requireNonNull(RPGMain.getPlugin().getRPGPlayerManager().getPlayer(player))).open(player)
            );
            setItem(48, statsButton);
        }

        // Talents button (slot 50) - only show if has job
        if (rpgPlayer.hasJob()) {
            GuiItem talentsButton = GuiItem.clickable(
                    ItemBuilder.of(Material.ENCHANTED_BOOK)
                            .displayName(trans("items.profile.talents-button.name"))
                            .lore(langManager.getComponentList(viewer, "items.profile.talents-button.lore"))
                            .build(),
                    player -> {
                        java.util.List<com.febrie.rpg.talent.Talent> mainTalents = RPGMain.getPlugin()
                                .getTalentManager().getJobMainTalents(rpgPlayer.getJob());
                        new TalentGui(guiManager, langManager, player, rpgPlayer, "main", mainTalents).open(player);
                    }
            );
            setItem(50, talentsButton);
        }

        // Navigation buttons: back (45번), refresh (49번), close (53번) - 위치 통일
        setupNavigationButtons(45, 49, 53);

        // Additional refresh action
        if (guiManager == null) {
            setItem(49, GuiFactory.createRefreshButton(player -> {
                refresh();
                sendMessage(player, "messages.profile-opened");
            }, langManager, viewer));
        }
    }

    // Helper methods for display
    private String formatPlayTime() {
        return transString("status.unknown");
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