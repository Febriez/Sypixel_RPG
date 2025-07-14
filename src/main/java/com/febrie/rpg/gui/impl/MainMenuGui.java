package com.febrie.rpg.gui.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    private static final int LEADERBOARD_SLOT = 40; // 리더보드 버튼 추가

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
        // 메인 메뉴에는 새로고침 버튼 없이, 닫기 버튼만 표시
        // BaseGui가 자동으로 빈 위치에 장식을 배치함
        setupStandardNavigation(false, true);
    }

    /**
     * Decorative elements setup
     */
    private void setupDecorations() {
        createBorder();
        setupTitleItem();
        addMenuDecorations();
    }

    /**
     * Title item setup
     */
    private void setupTitleItem() {
        GuiItem titleItem = GuiItem.display(
                new ItemBuilder(Material.NETHER_STAR)
                        .displayName(trans("items.mainmenu.title.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.title.lore"))
                        .build()
        );
        setItem(TITLE_SLOT, titleItem);
    }

    /**
     * Menu buttons setup
     */
    private void setupMenuButtons() {
        setupProfileButton();
        setupShopButton();
        setupDungeonButton();
        setupStatsButton();
        setupSettingsButton();
        setupTalentsButton();
        setupLeaderboardButton(); // 리더보드 버튼 추가
    }

    /**
     * Profile button setup
     */
    private void setupProfileButton() {
        GuiItem profileButton = GuiItem.clickable(
                new ItemBuilder(viewer)
                        .displayName(trans("items.mainmenu.profile-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.profile-button.lore"))
                        .build(),
                clickedPlayer -> {
                    guiManager.openProfileGui(clickedPlayer);
                    playClickSound(clickedPlayer);
                }
        );
        setItem(PROFILE_SLOT, profileButton);
    }

    /**
     * Shop button setup
     */
    private void setupShopButton() {
        GuiItem shopButton = GuiItem.clickable(
                new ItemBuilder(Material.EMERALD)
                        .displayName(trans("items.mainmenu.shop-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.shop-button.lore"))
                        .build(),
                clickedPlayer -> {
                    langManager.sendMessage(clickedPlayer, "general.coming-soon");
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
                new ItemBuilder(Material.END_PORTAL_FRAME)
                        .displayName(trans("items.mainmenu.dungeon-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.dungeon-button.lore"))
                        .build(),
                clickedPlayer -> {
                    langManager.sendMessage(clickedPlayer, "general.coming-soon");
                    playClickSound(clickedPlayer);
                }
        );
        setItem(DUNGEON_SLOT, dungeonButton);
    }

    /**
     * Stats button setup - 실제 StatsGui로 연결
     */
    private void setupStatsButton() {
        GuiItem statsButton = GuiItem.clickable(
                new ItemBuilder(Material.DIAMOND_SWORD)
                        .displayName(trans("items.mainmenu.stats-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.stats-button.lore"))
                        .build(),
                clickedPlayer -> {
                    // RPGPlayer 가져오기
                    RPGPlayer rpgPlayer = RPGMain.getPlugin()
                            .getRPGPlayerManager().getOrCreatePlayer(clickedPlayer);

                    // 직업이 있는지 확인
                    if (!rpgPlayer.hasJob()) {
                        langManager.sendMessage(clickedPlayer, "messages.no-job-for-stats");
                        playErrorSound(clickedPlayer);
                        return;
                    }

                    // StatsGui 열기
                    StatsGui statsGui = new StatsGui(guiManager, langManager, clickedPlayer, rpgPlayer);
                    guiManager.openGui(clickedPlayer, statsGui);
                    playSuccessSound(clickedPlayer);
                }
        );
        setItem(STATS_SLOT, statsButton);
    }

    /**
     * Settings button setup - 전체 설정으로 이름 변경
     */
    private void setupSettingsButton() {
        // 언어에 따라 "전체 설정" 또는 "All Settings"로 표시
        Component settingsName = viewer.locale().getLanguage().equals("ko")
                ? Component.text("전체 설정", com.febrie.rpg.util.ColorUtil.GRAY)
                : Component.text("All Settings", com.febrie.rpg.util.ColorUtil.GRAY);

        GuiItem settingsButton = GuiItem.clickable(
                new ItemBuilder(Material.REDSTONE)
                        .displayName(settingsName)
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.settings-button.lore"))
                        .build(),
                clickedPlayer -> {
                    langManager.sendMessage(clickedPlayer, "general.coming-soon");
                    playClickSound(clickedPlayer);
                }
        );
        setItem(SETTINGS_SLOT, settingsButton);
    }

    /**
     * Talents button setup - 실제 TalentGui로 연결
     */
    private void setupTalentsButton() {
        GuiItem talentsButton = GuiItem.clickable(
                new ItemBuilder(Material.ENCHANTING_TABLE)
                        .displayName(trans("items.mainmenu.talents-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.talents-button.lore"))
                        .build(),
                clickedPlayer -> {
                    // RPGPlayer 가져오기
                    RPGPlayer rpgPlayer = RPGMain.getPlugin()
                            .getRPGPlayerManager().getOrCreatePlayer(clickedPlayer);

                    // 직업이 있는지 확인
                    if (!rpgPlayer.hasJob()) {
                        langManager.sendMessage(clickedPlayer, "messages.no-job-for-talents");
                        playErrorSound(clickedPlayer);
                        return;
                    }

                    // TalentGui 열기
                    java.util.List<com.febrie.rpg.talent.Talent> talents = RPGMain.getPlugin()
                            .getTalentManager().getJobMainTalents(rpgPlayer.getJob());
                    TalentGui talentGui = new TalentGui(guiManager, langManager, clickedPlayer, rpgPlayer, "main", talents);
                    guiManager.openGui(clickedPlayer, talentGui);
                    playSuccessSound(clickedPlayer);
                }
        );
        setItem(TALENTS_SLOT, talentsButton);
    }

    /**
     * Leaderboard button setup
     */
    private void setupLeaderboardButton() {
        GuiItem leaderboardButton = GuiItem.clickable(
                new ItemBuilder(Material.GOLDEN_APPLE)
                        .displayName(trans("items.mainmenu.leaderboard-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.leaderboard-button.lore"))
                        .build(),
                clickedPlayer -> {
                    guiManager.openLeaderboardGui(clickedPlayer);
                    playClickSound(clickedPlayer);
                }
        );
        setItem(LEADERBOARD_SLOT, leaderboardButton);
    }

    /**
     * Add menu decorations
     */
    private void addMenuDecorations() {
        // Additional decorative elements can be added here
        // For example, fill empty slots with decorative glass panes
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
}