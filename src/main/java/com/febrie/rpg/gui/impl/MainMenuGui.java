package com.febrie.rpg.gui.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.builder.GuiBuilder;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.talent.Talent;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 메인 메뉴 GUI - 리팩토링된 버전
 * RPG 기능들에 접근할 수 있는 중앙 허브
 *
 * @author Febrie, CoffeeTory
 */
public class MainMenuGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6 rows

    // 메뉴 버튼 위치 (3x3 그리드 중앙 배치)
    private static final int PROFILE_SLOT = 20;
    private static final int SHOP_SLOT = 22;
    private static final int DUNGEON_SLOT = 24;
    private static final int STATS_SLOT = 29;
    private static final int SETTINGS_SLOT = 31;
    private static final int TALENTS_SLOT = 33;
    private static final int LEADERBOARD_SLOT = 40;

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
    protected GuiFramework getBackTarget() {
        // 메인 메뉴는 최상위 메뉴이므로 뒤로가기 없음
        return null;
    }


    @Override
    protected void setupLayout() {
        setupDecorations();
        setupMenuButtons();
        // 메인 메뉴는 닫기 버튼만 표시
        setupStandardNavigation(false, true);
    }

    @Override
    public void updateNavigationButtons() {
        // 메인 메뉴는 뒤로가기 버튼 없음 - 항상 데코레이션 표시
        setItem(BACK_BUTTON_SLOT, GuiFactory.createDecoration());
    }

    /**
     * 장식 요소 설정
     */
    private void setupDecorations() {
        createBorder();
        setupTitleItem();
    }

    /**
     * 타이틀 아이템 설정
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
     * 메뉴 버튼들 설정
     */
    private void setupMenuButtons() {
        GuiBuilder builder = new GuiBuilder(this, viewer, langManager, guiManager);
        
        // 프로필 버튼
        builder.menuButton(PROFILE_SLOT, 
            new ItemBuilder(viewer)
                .displayName(trans("items.mainmenu.profile-button.name"))
                .lore(langManager.getComponentList(viewer, "items.mainmenu.profile-button.lore"))
                .build(),
            player -> {
                ProfileGui profileGui = new ProfileGui(player, player, guiManager, langManager);
                guiManager.openGui(player, profileGui);
            });
            
        // 상점 버튼 (준비중)
        builder.menuButton(SHOP_SLOT, Material.EMERALD,
            "items.mainmenu.shop-button.name",
            "items.mainmenu.shop-button.lore",
            player -> sendMessage(player, "general.coming-soon"));
            
        // 던전 버튼 (준비중)
        builder.menuButton(DUNGEON_SLOT, Material.END_PORTAL_FRAME,
            "items.mainmenu.dungeon-button.name",
            "items.mainmenu.dungeon-button.lore",
            player -> sendMessage(player, "general.coming-soon"));
            
        // 스탯 버튼
        builder.menuButton(STATS_SLOT, Material.DIAMOND_SWORD,
            "items.mainmenu.stats-button.name",
            "items.mainmenu.stats-button.lore",
            this::openStatsGui);
            
        // 설정 버튼
        setupSettingsButton();
        
        // 특성 버튼
        builder.menuButton(TALENTS_SLOT, Material.ENCHANTING_TABLE,
            "items.mainmenu.talents-button.name",
            "items.mainmenu.talents-button.lore",
            this::openTalentsGui);
            
        // 리더보드 버튼
        builder.menuButton(LEADERBOARD_SLOT, Material.GOLDEN_APPLE,
            "items.mainmenu.leaderboard-button.name",
            "items.mainmenu.leaderboard-button.lore",
            player -> {
                LeaderboardGui leaderboardGui = new LeaderboardGui(guiManager, langManager, player);
                guiManager.openGui(player, leaderboardGui);
            });
    }

    /**
     * 스탯 GUI 열기
     */
    private void openStatsGui(Player player) {
        RPGPlayer rpgPlayer = RPGMain.getPlugin()
                .getRPGPlayerManager().getOrCreatePlayer(player);

        if (!rpgPlayer.hasJob()) {
            sendMessage(player, "messages.no-job-for-stats");
            playErrorSound(player);
            return;
        }

        StatsGui statsGui = new StatsGui(guiManager, langManager, player, rpgPlayer);
        guiManager.openGui(player, statsGui);
        playSuccessSound(player);
    }

    /**
     * 설정 버튼 (준비중)
     */
    private void setupSettingsButton() {
        Component settingsName = viewer.locale().getLanguage().equals("ko")
                ? Component.text("전체 설정", ColorUtil.GRAY)
                : Component.text("All Settings", ColorUtil.GRAY);

        GuiItem settingsButton = GuiItem.clickable(
                new ItemBuilder(Material.REDSTONE)
                        .displayName(settingsName)
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.settings-button.lore"))
                        .build(),
                player -> {
                    sendMessage(player, "general.coming-soon");
                    playClickSound(player);
                }
        );
        setItem(SETTINGS_SLOT, settingsButton);
    }

    /**
     * 특성 GUI 열기
     */
    private void openTalentsGui(Player player) {
        RPGPlayer rpgPlayer = RPGMain.getPlugin()
                .getRPGPlayerManager().getOrCreatePlayer(player);

        if (!rpgPlayer.hasJob()) {
            sendMessage(player, "messages.no-job-for-talents");
            playErrorSound(player);
            return;
        }

        List<Talent> talents = RPGMain.getPlugin()
                .getTalentManager().getJobMainTalents(rpgPlayer.getJob());
        TalentGui talentGui = new TalentGui(guiManager, langManager, player, rpgPlayer, "main", talents);
        guiManager.openGui(player, talentGui);
        playSuccessSound(player);
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
}