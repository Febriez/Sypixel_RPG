package com.febrie.rpg.gui.impl.system;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.builder.GuiBuilder;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.player.ProfileGui;
import com.febrie.rpg.gui.impl.island.IslandMainGui;
import com.febrie.rpg.gui.impl.island.IslandCreationGui;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.dto.island.PlayerIslandDataDTO;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.StandardItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

    // 메뉴 버튼 위치 (새로운 레이아웃)
    private static final int PROFILE_SLOT = 4; // 상단 프로필
    private static final int HUB_SLOT = 20;
    private static final int SHOP_SLOT = 21;
    private static final int DUNGEON_SLOT = 22;
    private static final int WILD_SLOT = 23;
    private static final int ISLAND_SLOT = 24;
    private static final int LEADERBOARD_SLOT = 31;

    // 타이틀 슬롯
    private static final int TITLE_SLOT = 4;

    private MainMenuGui(@NotNull GuiManager guiManager, @NotNull Player player) {
        super(player, guiManager, GUI_SIZE, Component.translatable("gui.mainmenu.title"));
    }
    
    /**
     * Factory method to create the GUI
     */
    public static MainMenuGui create(@NotNull GuiManager guiManager, @NotNull Player player) {
        return new MainMenuGui(guiManager, player);
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
        setItem(getBackButtonSlot(), GuiFactory.createDecoration());
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
                ItemBuilder.of(Material.NETHER_STAR, getViewerLocale())
                        .displayNameTranslated("items.mainmenu.title.name")
                        .addLoreTranslated("items.mainmenu.title.lore")
                        .hideAllFlags()
                        .build()
        );
        setItem(TITLE_SLOT, titleItem);
    }

    /**
     * 메뉴 버튼들 설정
     */
    private void setupMenuButtons() {
        GuiBuilder builder = new GuiBuilder(this, viewer, guiManager);
        
        // 프로필 버튼 (상단)
        builder.menuButton(PROFILE_SLOT, 
            ItemBuilder.of(Material.PLAYER_HEAD, getViewerLocale())
                .displayNameTranslated("items.mainmenu.profile-button.name")
                .addLoreTranslated("items.mainmenu.profile-button.lore")
                .hideAllFlags()
                .build(),
            player -> {
                ProfileGui profileGui = ProfileGui.create(guiManager, player);
                guiManager.openGui(player, profileGui);
            });
            
        // 허브 버튼 (네더의 별)
        builder.menuButton(HUB_SLOT, Material.NETHER_STAR,
            "items.mainmenu.hub-button.name",
            "items.mainmenu.hub-button.lore",
            player -> {
                World hubWorld = Bukkit.getWorld("Hub");
                if (hubWorld != null) {
                    Location hubLocation = new Location(hubWorld, 0, 65, 0);
                    player.teleport(hubLocation);
                    sendMessage(player, "general.teleport.hub-success");
                    playClickSound(player);
                } else {
                    sendMessage(player, "general.error.hub-world-not-found");
                }
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
            
        // 야생 버튼 (준비중)
        builder.menuButton(WILD_SLOT, Material.IRON_SWORD,
            "items.mainmenu.wild-button.name",
            "items.mainmenu.wild-button.lore",
            player -> sendMessage(player, "general.coming-soon"));
            
        // 리더보드 버튼
        setupLeaderboardButton();
            
        // 섬 버튼 (잔디 블럭)
        builder.menuButton(ISLAND_SLOT, Material.GRASS_BLOCK,
            "items.mainmenu.island-button.name",
            "items.mainmenu.island-button.lore",
            player -> {
                IslandManager islandManager = RPGMain.getPlugin().getIslandManager();
                PlayerIslandDataDTO playerIslandData = islandManager.getPlayerIslandDataFromCache(player.getUniqueId().toString());
                
                if (playerIslandData != null && playerIslandData.hasIsland()) {
                    // 섬이 있으면 섬 메뉴 열기
                    IslandMainGui islandGui = IslandMainGui.create(guiManager, player);
                    guiManager.openGui(player, islandGui);
                    playClickSound(player);
                } else {
                    // 섬이 없으면 섬 생성 GUI 열기
                    IslandCreationGui creationGui = IslandCreationGui.create(guiManager, player);
                    guiManager.openGui(player, creationGui);
                    playClickSound(player);
                }
            });
    }

    /**
     * 리더보드 버튼
     */
    private void setupLeaderboardButton() {
        GuiItem leaderboardButton = GuiItem.clickable(
                ItemBuilder.of(Material.GOLDEN_APPLE, getViewerLocale())
                        .displayNameTranslated("items.mainmenu.leaderboard-button.name")
                        .addLoreTranslated("items.mainmenu.leaderboard-button.lore")
                        .hideAllFlags()
                        .build(),
                player -> {
                    LeaderboardGui leaderboardGui = LeaderboardGui.create(guiManager, player);
                    guiManager.openGui(player, leaderboardGui);
                    playClickSound(player);
                }
        );
        setItem(LEADERBOARD_SLOT, leaderboardButton);
    }
}