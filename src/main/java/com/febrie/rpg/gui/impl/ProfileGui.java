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
 * <p>
 * 개선사항:
 * - 동적 슬롯 계산
 * - 상수 사용으로 매직 넘버 제거
 * - 더 체계적인 레이아웃
 *
 * @author Febrie, CoffeeTory
 */
public class ProfileGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6 rows
    private final Player targetPlayer;

    // 레이아웃 상수
    private static final int TITLE_SLOT = 4;
    private static final int PLAYER_HEAD_SLOT = 13;

    // 스탯 표시 슬롯
    private static final int LEVEL_INFO_SLOT = 19;
    private static final int HEALTH_INFO_SLOT = 21;
    private static final int FOOD_INFO_SLOT = 23;
    private static final int GAME_INFO_SLOT = 25;

    // 액션 버튼 슬롯
    private static final int JOB_SLOT = 31;
    private static final int SETTINGS_SLOT = 47;
    private static final int STATS_SLOT = 48;
    private static final int TALENTS_SLOT = 50;

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
        // 상단 테두리 (타이틀 슬롯 제외)
        for (int i = 0; i < 9; i++) {
            if (i != TITLE_SLOT) {
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 하단 테두리 - 네비게이션 버튼 위치 제외
        int lastRowStart = getLastRowStart();
        for (int i = lastRowStart; i < GUI_SIZE; i++) {
            // 네비게이션 버튼 위치 건너뛰기
            if (i != lastRowStart && i != lastRowStart + 4 && i != GUI_SIZE - 1) {
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 좌우 테두리
        for (int row = 1; row < getRows() - 1; row++) {
            setItem(row * 9, GuiFactory.createDecoration());
            setItem(row * 9 + 8, GuiFactory.createDecoration());
        }

        // Title decoration
        setItem(TITLE_SLOT, GuiItem.display(
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
                                "uuid", formatUUID(targetPlayer.getUniqueId().toString()),
                                "playtime", formatPlayTime()))
                        .build(),
                player -> {
                    sendMessage(player, "general.coming-soon");
                    playClickSound(player);
                }
        );

        setItem(PLAYER_HEAD_SLOT, playerHead);
    }

    /**
     * Sets up the statistics section
     */
    private void setupStatsSection() {
        // Level info
        setupLevelInfo();

        // Health info
        setupHealthInfo();

        // Food info
        setupFoodInfo();

        // Game mode info
        setupGameModeInfo();
    }

    /**
     * Level info item setup
     */
    private void setupLevelInfo() {
        GuiItem levelItem = GuiItem.display(
                ItemBuilder.of(Material.EXPERIENCE_BOTTLE)
                        .displayName(trans("items.profile.level-info.name"))
                        .lore(langManager.getComponentList(viewer, "items.profile.level-info.lore",
                                "level", String.valueOf(targetPlayer.getLevel()),
                                "exp", String.valueOf(Math.round(targetPlayer.getExp() * 100)),
                                "total_exp", String.valueOf(targetPlayer.getTotalExperience())))
                        .build()
        );
        setItem(LEVEL_INFO_SLOT, levelItem);
    }

    /**
     * Health info item setup
     */
    private void setupHealthInfo() {
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
        setItem(HEALTH_INFO_SLOT, healthItem);
    }

    /**
     * Food info item setup
     */
    private void setupFoodInfo() {
        GuiItem foodItem = GuiItem.display(
                ItemBuilder.of(Material.BREAD)
                        .displayName(trans("items.profile.food-info.name"))
                        .lore(langManager.getComponentList(viewer, "items.profile.food-info.lore",
                                "food", String.valueOf(targetPlayer.getFoodLevel()),
                                "saturation", String.format("%.1f", targetPlayer.getSaturation()),
                                "hunger_bar", createHungerBar(targetPlayer.getFoodLevel())))
                        .build()
        );
        setItem(FOOD_INFO_SLOT, foodItem);
    }

    /**
     * Game mode info item setup
     */
    private void setupGameModeInfo() {
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
        setItem(GAME_INFO_SLOT, gameModeItem);
    }

    /**
     * Sets up action buttons
     */
    private void setupActionButtons() {
        com.febrie.rpg.player.RPGPlayer rpgPlayer = RPGMain.getPlugin()
                .getRPGPlayerManager().getOrCreatePlayer(targetPlayer);

        // Job selection or display
        setupJobButton(rpgPlayer);

        // Settings button
        setupSettingsButton();

        // Stats and Talents buttons (only if has job)
        if (rpgPlayer.hasJob()) {
            setupStatsButton(rpgPlayer);
            setupTalentsButton(rpgPlayer);
        }

        // Navigation buttons with dynamic positioning
        setupDynamicNavigation();

        // Custom refresh action
        if (guiManager == null) {
            int refreshSlot = getLastRowStart() + 4; // 중앙
            setItem(refreshSlot, GuiFactory.createRefreshButton(player -> {
                refresh();
                sendMessage(player, "messages.profile-opened");
                playSuccessSound(player);
            }, langManager, viewer));
        }
    }

    /**
     * Job button setup
     */
    private void setupJobButton(com.febrie.rpg.player.RPGPlayer rpgPlayer) {
        if (!rpgPlayer.hasJob()) {
            GuiItem jobButton = GuiItem.clickable(
                    ItemBuilder.of(Material.ENCHANTING_TABLE)
                            .displayName(trans("items.mainmenu.job-button.name"))
                            .lore(langManager.getComponentList(viewer, "items.mainmenu.job-button.lore"))
                            .build(),
                    player -> {
                        if (player.equals(targetPlayer)) {
                            new JobSelectionGui(guiManager, langManager, player, rpgPlayer).open(player);
                            playSuccessSound(player);
                        } else {
                            sendMessage(player, "general.coming-soon");
                            playErrorSound(player);
                        }
                    }
            );
            setItem(JOB_SLOT, jobButton);
        } else {
            // Show current job info
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
            setItem(JOB_SLOT, jobInfo);
        }
    }

    /**
     * Settings button setup
     */
    private void setupSettingsButton() {
        GuiItem settingsButton = GuiItem.clickable(
                ItemBuilder.of(Material.COMPARATOR)
                        .displayName(trans("items.profile.settings-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.profile.settings-button.lore"))
                        .build(),
                player -> {
                    sendMessage(player, "general.coming-soon");
                    playClickSound(player);
                }
        );
        setItem(SETTINGS_SLOT, settingsButton);
    }

    /**
     * Stats button setup
     */
    private void setupStatsButton(com.febrie.rpg.player.RPGPlayer rpgPlayer) {
        GuiItem statsButton = GuiItem.clickable(
                ItemBuilder.of(Material.BOOK)
                        .displayName(trans("items.profile.stats-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.profile.stats-button.lore"))
                        .build(),
                player -> {
                    new StatsGui(guiManager, langManager, player,
                            Objects.requireNonNull(RPGMain.getPlugin().getRPGPlayerManager().getPlayer(player)))
                            .open(player);
                    playSuccessSound(player);
                }
        );
        setItem(STATS_SLOT, statsButton);
    }

    /**
     * Talents button setup
     */
    private void setupTalentsButton(com.febrie.rpg.player.RPGPlayer rpgPlayer) {
        GuiItem talentsButton = GuiItem.clickable(
                ItemBuilder.of(Material.ENCHANTED_BOOK)
                        .displayName(trans("items.profile.talents-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.profile.talents-button.lore"))
                        .build(),
                player -> {
                    java.util.List<com.febrie.rpg.talent.Talent> mainTalents = RPGMain.getPlugin()
                            .getTalentManager().getJobMainTalents(rpgPlayer.getJob());
                    new TalentGui(guiManager, langManager, player, rpgPlayer, "main", mainTalents).open(player);
                    playSuccessSound(player);
                }
        );
        setItem(TALENTS_SLOT, talentsButton);
    }

    // Helper methods for display
    private String formatUUID(String uuid) {
        return uuid.length() > 8 ? uuid.substring(0, 8) + "..." : uuid;
    }

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