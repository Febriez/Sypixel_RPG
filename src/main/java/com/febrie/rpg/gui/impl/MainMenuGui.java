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
 * <p>
 * 개선사항:
 * - 동적 슬롯 계산
 * - 상수 사용으로 매직 넘버 제거
 * - 메뉴 버튼 배치 개선
 *
 * @author Febrie, CoffeeTory
 */
public class MainMenuGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6 rows

    // 메뉴 버튼 위치 상수 (3x3 그리드 중앙 배치)
    private static final int PROFILE_SLOT = 20;
    private static final int SHOP_SLOT = 22;
    private static final int DUNGEON_SLOT = 24;
    private static final int STATS_SLOT = 29;
    private static final int SETTINGS_SLOT = 31;
    private static final int TALENTS_SLOT = 33;

    // 타이틀 슬롯
    private static final int TITLE_SLOT = 4;

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
    protected void setupLayout() {
        setupDecorations();
        setupMenuButtons();
        setupDynamicNavigation();
    }

    /**
     * Sets up decorative elements
     */
    private void setupDecorations() {
        // Create border with default material
        createBorder();

        // Title item in top center
        setItem(TITLE_SLOT, GuiItem.display(
                ItemBuilder.of(Material.NETHER_STAR)
                        .displayName(trans("gui.mainmenu.title")
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(trans("gui.mainmenu.subtitle"))
                        .build()
        ));
    }

    /**
     * Sets up main menu buttons in organized layout
     */
    private void setupMenuButtons() {
        // Row 1: Profile, Shop, Dungeon
        setupProfileButton();
        setupShopButton();
        setupDungeonButton();

        // Row 2: Stats, Settings, Talents
        setupStatsButton();
        setupSettingsButton();
        setupTalentsButton();

        // 추가 장식 아이템 (선택적)
        addMenuDecorations();
    }

    /**
     * Profile button setup
     */
    private void setupProfileButton() {
        GuiItem profileButton = GuiItem.clickable(
                ItemBuilder.of(Material.PLAYER_HEAD)
                        .displayName(trans("items.mainmenu.profile-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.profile-button.lore"))
                        .build(),
                guiManager::openProfileGui
        );
        setItem(PROFILE_SLOT, profileButton);
    }

    /**
     * Shop button setup
     */
    private void setupShopButton() {
        GuiItem shopButton = GuiItem.clickable(
                ItemBuilder.of(Material.EMERALD)
                        .displayName(trans("items.mainmenu.shop-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.shop-button.lore"))
                        .build(),
                clickedPlayer -> {
                    sendMessage(clickedPlayer, "general.coming-soon");
                    playClickSound(clickedPlayer);
                }
        );
        setItem(SHOP_SLOT, shopButton);
    }

    /**
     * Dungeon button setup
     */
    private void setupDungeonButton() {
        GuiItem dungeonButton = GuiItem.clickable(
                ItemBuilder.of(Material.IRON_SWORD)
                        .displayName(trans("items.mainmenu.dungeon-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.dungeon-button.lore"))
                        .build(),
                clickedPlayer -> {
                    sendMessage(clickedPlayer, "general.coming-soon");
                    playClickSound(clickedPlayer);
                }
        );
        setItem(DUNGEON_SLOT, dungeonButton);
    }

    /**
     * Stats Management button setup
     */
    private void setupStatsButton() {
        GuiItem statsButton = GuiItem.clickable(
                ItemBuilder.of(Material.IRON_SWORD)
                        .displayName(trans("items.mainmenu.stats-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.stats-button.lore"))
                        .flags(org.bukkit.inventory.ItemFlag.values())
                        .build(),
                this::handleStatsButtonClick
        );
        setItem(STATS_SLOT, statsButton);
    }

    /**
     * Settings button setup
     */
    private void setupSettingsButton() {
        GuiItem settingsButton = GuiItem.clickable(
                ItemBuilder.of(Material.COMPARATOR)
                        .displayName(trans("items.mainmenu.settings-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.settings-button.lore"))
                        .build(),
                clickedPlayer -> {
                    sendMessage(clickedPlayer, "general.coming-soon");
                    playClickSound(clickedPlayer);
                }
        );
        setItem(SETTINGS_SLOT, settingsButton);
    }

    /**
     * Talent Management button setup
     */
    private void setupTalentsButton() {
        GuiItem talentsButton = GuiItem.clickable(
                ItemBuilder.of(Material.ENCHANTED_BOOK)
                        .displayName(trans("items.mainmenu.talents-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.talents-button.lore"))
                        .build(),
                this::handleTalentsButtonClick
        );
        setItem(TALENTS_SLOT, talentsButton);
    }

    /**
     * 메뉴 장식 추가
     */
    private void addMenuDecorations() {
        // 메뉴 버튼 사이 구분선 (선택적)
        Material separatorMaterial = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
        GuiItem separator = com.febrie.rpg.gui.component.GuiFactory.createDecoration(separatorMaterial);

        // 상단 행 구분선
        setItem(21, separator);
        setItem(23, separator);

        // 하단 행 구분선
        setItem(30, separator);
        setItem(32, separator);
    }

    /**
     * Stats 버튼 클릭 처리
     */
    private void handleStatsButtonClick(@NotNull Player clickedPlayer) {
        com.febrie.rpg.player.RPGPlayer rpgPlayer = com.febrie.rpg.RPGMain.getPlugin()
                .getRPGPlayerManager().getOrCreatePlayer(clickedPlayer);

        if (!rpgPlayer.hasJob()) {
            sendMessage(clickedPlayer, "messages.no-job-for-stats");
            playErrorSound(clickedPlayer);
            return;
        }

        com.febrie.rpg.gui.impl.StatsGui statsGui = new com.febrie.rpg.gui.impl.StatsGui(
                guiManager, langManager, clickedPlayer, rpgPlayer);
        statsGui.open(clickedPlayer);
        playSuccessSound(clickedPlayer);
    }

    /**
     * Talents 버튼 클릭 처리
     */
    private void handleTalentsButtonClick(@NotNull Player clickedPlayer) {
        com.febrie.rpg.player.RPGPlayer rpgPlayer = com.febrie.rpg.RPGMain.getPlugin()
                .getRPGPlayerManager().getOrCreatePlayer(clickedPlayer);

        if (!rpgPlayer.hasJob()) {
            sendMessage(clickedPlayer, "messages.no-job-for-talents");
            playErrorSound(clickedPlayer);

            // 직업 선택 GUI로 이동 제안
            com.febrie.rpg.gui.impl.JobSelectionGui jobGui = new com.febrie.rpg.gui.impl.JobSelectionGui(
                    guiManager, langManager, clickedPlayer, rpgPlayer);
            jobGui.open(clickedPlayer);
            return;
        }

        java.util.List<com.febrie.rpg.talent.Talent> mainTalents = com.febrie.rpg.RPGMain.getPlugin()
                .getTalentManager().getJobMainTalents(rpgPlayer.getJob());
        com.febrie.rpg.gui.impl.TalentGui talentGui = new com.febrie.rpg.gui.impl.TalentGui(
                guiManager, langManager, clickedPlayer, rpgPlayer, "main", mainTalents);
        talentGui.open(clickedPlayer);
        playSuccessSound(clickedPlayer);
    }
}