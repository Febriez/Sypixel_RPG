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
        setupStandardNavigation(true, true); // 새로고침, 닫기 버튼 포함
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
                ItemBuilder.of(Material.IRON_SWORD)
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
     * Stats button setup
     */
    private void setupStatsButton() {
        GuiItem statsButton = GuiItem.clickable(
                ItemBuilder.of(Material.BOOK)
                        .displayName(trans("items.mainmenu.stats-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.stats-button.lore"))
                        .build(),
                clickedPlayer -> {
                    langManager.sendMessage(clickedPlayer, "general.coming-soon");
                    playClickSound(clickedPlayer);
                }
        );
        setItem(STATS_SLOT, statsButton);
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
                clickedPlayer -> {
                    langManager.sendMessage(clickedPlayer, "general.coming-soon");
                    playClickSound(clickedPlayer);
                }
        );
        setItem(SETTINGS_SLOT, settingsButton);
    }

    /**
     * Talents button setup
     */
    private void setupTalentsButton() {
        GuiItem talentsButton = GuiItem.clickable(
                ItemBuilder.of(Material.ENCHANTING_TABLE)
                        .displayName(trans("items.mainmenu.talents-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.talents-button.lore"))
                        .build(),
                clickedPlayer -> {
                    langManager.sendMessage(clickedPlayer, "general.coming-soon");
                    playClickSound(clickedPlayer);
                }
        );
        setItem(TALENTS_SLOT, talentsButton);
    }

    /**
     * Additional decorative elements
     */
    private void addMenuDecorations() {
        // 메뉴 버튼 주변 장식 (선택적)
        // 필요시 여기에 추가 장식 아이템들을 배치할 수 있음
    }
}