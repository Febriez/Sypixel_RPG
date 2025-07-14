package com.febrie.rpg.gui.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.economy.Wallet;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.stat.Stat;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        // 상단 테두리 (4번 슬롯 제외 - 플레이어 머리가 들어갈 자리)
        for (int i = 0; i < 9; i++) {
            if (i != TITLE_SLOT) {
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 중간 구분선
        for (int i = 27; i < 36; i++) {
            if (i != JOB_INFO_SLOT) {
                setItem(i, GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE));
            }
        }

        // 좌우 테두리
        for (int row = 1; row < 5; row++) {
            setItem(row * 9, GuiFactory.createDecoration());
            setItem(row * 9 + 8, GuiFactory.createDecoration());
        }

        // 13번 슬롯(원래 플레이어 머리 위치)에 장식 추가
        setItem(PLAYER_HEAD_SLOT, GuiFactory.createDecoration());
    }

    /**
     * 플레이어 재화 정보를 ItemBuilder의 lore에 추가
     *
     * @param builder ItemBuilder 인스턴스
     * @param rpgPlayer 대상 플레이어
     */
    private void addWalletInfoToLore(@NotNull ItemBuilder builder, @NotNull com.febrie.rpg.player.RPGPlayer rpgPlayer) {
        builder.addLore(trans("gui.profile.currency-info"));

        Wallet wallet = rpgPlayer.getWallet();

    }

    /**
     * 개별 스탯 라인 생성
     *
     * @param stat 스탯 타입
     * @param stats 스탯 홀더
     * @return 포맷된 스탯 라인 컴포넌트
     */
    private Component createStatLine(@NotNull Stat stat, @NotNull Stat.StatHolder stats) {
        int baseStat = stats.getBaseStat(stat);
        int bonusStat = stats.getBonusStat(stat);
        int totalStat = stats.getTotalStat(stat);

        // 스탯 이름과 값 표시
        Component statLine = trans("stat." + stat.getId().toLowerCase() + ".name")
                .append(Component.text(": ", ColorUtil.WHITE))
                .append(Component.text(totalStat, ColorUtil.YELLOW));

        // 보너스가 있으면 표시
        if (bonusStat > 0) {
            statLine = statLine
                    .append(Component.text(" (", ColorUtil.GRAY))
                    .append(Component.text(baseStat, ColorUtil.WHITE))
                    .append(Component.text(" +", ColorUtil.SUCCESS))
                    .append(Component.text(bonusStat, ColorUtil.SUCCESS))
                    .append(Component.text(")", ColorUtil.GRAY));
        } else if (bonusStat < 0) {
            // 음수 보너스 처리
            statLine = statLine
                    .append(Component.text(" (", ColorUtil.GRAY))
                    .append(Component.text(baseStat, ColorUtil.WHITE))
                    .append(Component.text(" ", ColorUtil.ERROR))
                    .append(Component.text(bonusStat, ColorUtil.ERROR))
                    .append(Component.text(")", ColorUtil.GRAY));
        }

        return statLine;
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
                                "status", targetPlayer.isOnline() ?
                                        transString("status.online") :
                                        transString("status.offline")))
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
                        .addLore(trans("gui.profile.food-level",
                                "level", String.valueOf(targetPlayer.getFoodLevel())))
                        .addLore(trans("gui.profile.saturation",
                                "saturation", String.format("%.1f", targetPlayer.getSaturation())))
                        .build()
        ));

        // Game info
        setItem(GAME_INFO_SLOT, GuiItem.display(
                ItemBuilder.of(Material.COMPASS)
                        .displayName(trans("gui.profile.game-info"))
                        .addLore(trans("gui.profile.gamemode",
                                "mode", targetPlayer.getGameMode().toString()))
                        .addLore(trans("gui.profile.world",
                                "world", targetPlayer.getWorld().getName()))
                        .build()
        ));
    }

    /**
     * Sets up player statistics section
     */
    private void setupStatsSection() {
        // This section is handled in setupActionButtons with the job button
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

        // Stats and Talents buttons (available for all, but only functional for job holders)
        setupStatsButton(rpgPlayer);
        setupTalentsButton(rpgPlayer);

        // Navigation buttons - true, true = include refresh button and close button
        setupStandardNavigation(true);
    }

    /**
     * Job button setup
     */
    private void setupJobButton(com.febrie.rpg.player.@NotNull RPGPlayer rpgPlayer) {
        if (!rpgPlayer.hasJob()) {
            // No job - show job selection button
            GuiItem jobButton = GuiItem.clickable(
                    ItemBuilder.of(Material.ENCHANTING_TABLE)
                            .displayName(trans("items.mainmenu.job-button.name"))
                            .addLore(langManager.getComponentList(viewer, "items.mainmenu.job-button.lore"))
                            .glint(true)
                            .build(),
                    p -> {
                        if (p.equals(targetPlayer)) {
                            JobSelectionGui jobGui = new JobSelectionGui(guiManager, langManager, p, rpgPlayer);
                            guiManager.openGui(p, jobGui);
                            playSuccessSound(p);
                        } else {
                            langManager.sendMessage(p, "general.cannot-select-others-job");
                            playErrorSound(p);
                        }
                    }
            );
            setItem(JOB_SLOT, jobButton);
        } else {
            // Has job - show current job info
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
                            .addLore(trans("gui.profile.combat-power", "power", String.valueOf(rpgPlayer.getCombatPower())))
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
                        .displayName(trans("gui.buttons.settings.name"))
                        .addLore(trans("gui.buttons.settings.lore"))
                        .build(),
                p -> {
                    langManager.sendMessage(p, "general.coming-soon");
                    playClickSound(p);
                }
        );
        setItem(SETTINGS_SLOT, settingsButton);
    }

    /**
     * Stats button setup
     */
    private void setupStatsButton(com.febrie.rpg.player.RPGPlayer rpgPlayer) {
        GuiItem statsButton = GuiItem.clickable(
                ItemBuilder.of(Material.IRON_CHESTPLATE)
                        .displayName(trans("items.mainmenu.stats-button.name"))
                        .addLore(langManager.getComponentList(viewer, "items.mainmenu.stats-button.lore"))
                        .flags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES)
                        .build(),
                p -> {
                    if (!rpgPlayer.hasJob()) {
                        langManager.sendMessage(p, "messages.no-job-for-stats");
                        playErrorSound(p);
                        return;
                    }

                    if (p.equals(targetPlayer)) {
                        StatsGui statsGui = new StatsGui(guiManager, langManager, p, rpgPlayer);
                        guiManager.openGui(p, statsGui);
                        playSuccessSound(p);
                    } else {
                        langManager.sendMessage(p, "general.cannot-view-others-stats");
                        playErrorSound(p);
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
                ItemBuilder.of(Material.BLAZE_POWDER)
                        .displayName(trans("items.mainmenu.talents-button.name"))
                        .addLore(langManager.getComponentList(viewer, "items.mainmenu.talents-button.lore"))
                        .build(),
                p -> {
                    if (!rpgPlayer.hasJob()) {
                        langManager.sendMessage(p, "messages.no-job-for-talents");
                        playErrorSound(p);
                        return;
                    }

                    if (p.equals(targetPlayer)) {
                        // TalentGui 열기
                        java.util.List<com.febrie.rpg.talent.Talent> talents = RPGMain.getPlugin()
                                .getTalentManager().getJobMainTalents(rpgPlayer.getJob());
                        TalentGui talentGui = new TalentGui(guiManager, langManager, p, rpgPlayer, "main", talents);
                        guiManager.openGui(p, talentGui);
                        playSuccessSound(p);
                    } else {
                        langManager.sendMessage(p, "general.cannot-view-others-talents");
                        playErrorSound(p);
                    }
                }
        );
        setItem(TALENTS_SLOT, talentsButton);
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
}