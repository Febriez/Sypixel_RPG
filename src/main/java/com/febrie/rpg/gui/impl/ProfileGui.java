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

/**
 * Player profile GUI implementation with internationalization support
 * Shows player statistics, information, and provides access to various features
 * <p>
 * 개선된 네비게이션 시스템 적용
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
     * Creates a new ProfileGui for viewing another player's profile
     * 프로젝트의 실제 생성자 시그니처와 일치
     */
    public ProfileGui(@NotNull Player targetPlayer, @NotNull Player viewer,
                      @NotNull GuiManager guiManager, @NotNull LangManager langManager) {
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

        // 타이틀 아이템
        setItem(TITLE_SLOT, GuiItem.display(
                ItemBuilder.of(Material.NAME_TAG)
                        .displayName(trans("gui.profile.title")
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(trans("gui.profile.viewing", "player", targetPlayer.getName()))
                        .build()
        ));

        // 중간 구분선
        for (int i = 27; i < 36; i++) {
            if (i != JOB_SLOT) {
                setItem(i, GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE));
            }
        }

        // 좌우 테두리
        for (int row = 1; row < 5; row++) {
            setItem(row * 9, GuiFactory.createDecoration());
            setItem(row * 9 + 8, GuiFactory.createDecoration());
        }
    }

    /**
     * Sets up player information display
     */
    private void setupPlayerInfo() {
        // Player head in center
        setItem(PLAYER_HEAD_SLOT, GuiItem.display(
                new ItemBuilder(targetPlayer)
                        .displayName(Component.text(targetPlayer.getName())
                                .color(ColorUtil.LEGENDARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(trans("gui.profile.online-status",
                                "status", targetPlayer.isOnline() ? "온라인" : "오프라인"))
                        .build()
        ));

        // Level/XP info
        setItem(LEVEL_INFO_SLOT, GuiItem.display(
                ItemBuilder.of(Material.EXPERIENCE_BOTTLE)
                        .displayName(trans("gui.profile.level-info"))
                        .addLore(trans("gui.profile.level", "level", "1"))
                        .addLore(trans("gui.profile.experience", "exp", "0"))
                        .build()
        ));

        // Health info
        AttributeInstance healthAttr = targetPlayer.getAttribute(Attribute.MAX_HEALTH);
        double maxHealth = healthAttr != null ? healthAttr.getValue() : 20.0;
        setItem(HEALTH_INFO_SLOT, GuiItem.display(
                ItemBuilder.of(Material.RED_DYE)
                        .displayName(trans("gui.profile.health-info"))
                        .addLore(trans("gui.profile.health",
                                "current", String.format("%.1f", targetPlayer.getHealth()),
                                "max", String.format("%.1f", maxHealth)))
                        .build()
        ));

        // Food info
        setItem(FOOD_INFO_SLOT, GuiItem.display(
                ItemBuilder.of(Material.BREAD)
                        .displayName(trans("gui.profile.food-info"))
                        .addLore(trans("gui.profile.food-level", "level", String.valueOf(targetPlayer.getFoodLevel())))
                        .addLore(trans("gui.profile.saturation", "saturation", String.format("%.1f", targetPlayer.getSaturation())))
                        .build()
        ));

        // Game mode and world info
        setItem(GAME_INFO_SLOT, GuiItem.display(
                ItemBuilder.of(Material.COMPASS)
                        .displayName(trans("gui.profile.game-info"))
                        .addLore(trans("gui.profile.gamemode", "mode", targetPlayer.getGameMode().name()))
                        .addLore(trans("gui.profile.world", "world", targetPlayer.getWorld().getName()))
                        .build()
        ));
    }

    /**
     * Sets up stats section
     */
    private void setupStatsSection() {
        // Implement stats display section if needed
    }

    /**
     * Sets up action buttons
     */
    private void setupActionButtons() {
        com.febrie.rpg.player.RPGPlayer rpgPlayer = RPGMain.getPlugin()
                .getRPGPlayerManager().getOrCreatePlayer(targetPlayer);

        // Job button/display
        setupJobButton(rpgPlayer);

        // Settings button (only for own profile)
        if (viewer.equals(targetPlayer)) {
            setupSettingsButton();
        }

        // Stats and Talents buttons (only if has job)
        if (rpgPlayer.hasJob()) {
            setupStatsButton(rpgPlayer);
            setupTalentsButton(rpgPlayer);
        }

        // Navigation buttons using BaseGui's standard navigation
        setupStandardNavigation(true, true); // 새로고침, 닫기 버튼 포함
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
                            JobSelectionGui jobGui = new JobSelectionGui(guiManager, langManager, player, rpgPlayer);
                            guiManager.openGui(player, jobGui);
                            playSuccessSound(player);
                        } else {
                            langManager.sendMessage(player, "general.coming-soon");
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
                            .addLore(trans("gui.profile.current-job"))
                            .addLore(trans("gui.profile.job-level", "level", String.valueOf(rpgPlayer.getLevel())))
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
                ItemBuilder.of(Material.REDSTONE)
                        .displayName(trans("items.mainmenu.settings-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.settings-button.lore"))
                        .build(),
                player -> {
                    langManager.sendMessage(player, "general.coming-soon");
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
                        .displayName(trans("items.mainmenu.stats-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.stats-button.lore"))
                        .build(),
                player -> {
                    if (player.equals(targetPlayer)) {
                        StatsGui statsGui = new StatsGui(guiManager, langManager, player, rpgPlayer);
                        guiManager.openGui(player, statsGui);
                        playSuccessSound(player);
                    } else {
                        langManager.sendMessage(player, "general.coming-soon");
                        playErrorSound(player);
                    }
                }
        );
        setItem(STATS_SLOT, statsButton);
    }

    /**
     * Talents button setup
     */
    private void setupTalentsButton(com.febrie.rpg.player.RPGPlayer rpgPlayer) {
        GuiItem talentsButton = GuiItem.clickable(
                ItemBuilder.of(Material.ENCHANTING_TABLE)
                        .displayName(trans("items.mainmenu.talents-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.talents-button.lore"))
                        .build(),
                player -> {
                    if (player.equals(targetPlayer)) {
                        // TalentGui talentsGui = new TalentGui(guiManager, langManager, player, rpgPlayer, "main", talents);
                        // guiManager.openGui(player, talentsGui);
                        langManager.sendMessage(player, "general.coming-soon");
                        playSuccessSound(player);
                    } else {
                        langManager.sendMessage(player, "general.coming-soon");
                        playErrorSound(player);
                    }
                }
        );
        setItem(TALENTS_SLOT, talentsButton);
    }
}