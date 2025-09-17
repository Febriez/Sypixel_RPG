package com.febrie.rpg.gui.impl.system;
import com.febrie.rpg.util.lang.GeneralLangKey;

import com.febrie.rpg.util.lang.GuiLangKey;
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
import com.febrie.rpg.util.LangManager;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Main Menu GUI - Refactored Version
 * Central hub for accessing RPG features
 *
 * @author Febrie, CoffeeTory
 */
public class MainMenuGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6 rows

    // Menu button positions (new layout)
    private static final int PROFILE_SLOT = 4; // Top profile
    private static final int HUB_SLOT = 20;
    private static final int SHOP_SLOT = 21;
    private static final int DUNGEON_SLOT = 22;
    private static final int WILD_SLOT = 23;
    private static final int ISLAND_SLOT = 24;
    private static final int LEADERBOARD_SLOT = 31;

    // Title slot
    private static final int TITLE_SLOT = 4;

    private MainMenuGui(@NotNull GuiManager guiManager, @NotNull Player player) {
        super(player, guiManager, GUI_SIZE, LangManager.text(GuiLangKey.GUI_MAINMENU_TITLE, player));
    }
    
    /**
     * Factory method to create the GUI
     */
    public static MainMenuGui create(@NotNull GuiManager guiManager, @NotNull Player player) {
        return new MainMenuGui(guiManager, player);
    }

    
    @Override
    protected GuiFramework getBackTarget() {
        // 메인 메뉴??최상??메뉴?��?�??�로가�??�음
        return null;
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
        setupMenuButtons();
        // 메인 메뉴???�기 버튼�??�시
        setupStandardNavigation(false, true);
    }

    @Override
    public void updateNavigationButtons() {
        // 메인 메뉴???�로가�?버튼 ?�음 - ??�� ?�코?�이???�시
        setItem(getBackButtonSlot(), GuiFactory.createDecoration());
    }

    /**
     * ?�식 ?�소 ?�정
     */
    private void setupDecorations() {
        createBorder();
        setupTitleItem();
    }

    /**
     * ?�?��? ?�이???�정
     */
    private void setupTitleItem() {
        GuiItem titleItem = GuiItem.display(
                ItemBuilder.of(Material.NETHER_STAR)
                        .displayName(LangManager.text(GeneralLangKey.ITEMS_MAINMENU_TITLE_NAME, viewer))
                        .addLore(LangManager.list(GeneralLangKey.ITEMS_MAINMENU_TITLE_LORE, viewer))
                        .hideAllFlags()
                        .build()
        );
        setItem(TITLE_SLOT, titleItem);
    }

    /**
     * 메뉴 버튼???�정
     */
    private void setupMenuButtons() {
        GuiBuilder builder = new GuiBuilder(this, viewer, guiManager);
        
        // ?�로??버튼 (?�단)
        builder.menuButton(PROFILE_SLOT, 
            ItemBuilder.of(Material.PLAYER_HEAD)
                .displayName(LangManager.text(GeneralLangKey.ITEMS_MAINMENU_PROFILE_BUTTON_NAME, viewer))
                .addLore(LangManager.list(GeneralLangKey.ITEMS_MAINMENU_PROFILE_BUTTON_LORE, viewer))
                .hideAllFlags()
                .build(),
            player -> {
                ProfileGui profileGui = ProfileGui.create(guiManager, player);
                guiManager.openGui(player, profileGui);
            });
            
        // ?�브 버튼 (?�더??�?
        builder.menuButton(HUB_SLOT, Material.NETHER_STAR,
            GeneralLangKey.ITEMS_MAINMENU_HUB_BUTTON_NAME,
            GeneralLangKey.ITEMS_MAINMENU_HUB_BUTTON_LORE,
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
            
        // ?�점 버튼 (준비중)
        builder.menuButton(SHOP_SLOT, Material.EMERALD,
            GeneralLangKey.ITEMS_MAINMENU_SHOP_BUTTON_NAME,
            GeneralLangKey.ITEMS_MAINMENU_SHOP_BUTTON_LORE,
            player -> sendMessage(player, "general.coming-soon"));
            
        // ?�전 버튼 (준비중)
        builder.menuButton(DUNGEON_SLOT, Material.END_PORTAL_FRAME,
            GeneralLangKey.ITEMS_MAINMENU_DUNGEON_BUTTON_NAME,
            GeneralLangKey.ITEMS_MAINMENU_DUNGEON_BUTTON_LORE,
            player -> sendMessage(player, "general.coming-soon"));
            
        // ?�생 버튼 (준비중)
        builder.menuButton(WILD_SLOT, Material.IRON_SWORD,
            GeneralLangKey.ITEMS_MAINMENU_WILD_BUTTON_NAME,
            GeneralLangKey.ITEMS_MAINMENU_WILD_BUTTON_LORE,
            player -> sendMessage(player, "general.coming-soon"));
            
        // 리더보드 버튼
        setupLeaderboardButton();
            
        // ??버튼 (?�디 블럭)
        builder.menuButton(ISLAND_SLOT, Material.GRASS_BLOCK,
            GeneralLangKey.ITEMS_MAINMENU_ISLAND_BUTTON_NAME,
            GeneralLangKey.ITEMS_MAINMENU_ISLAND_BUTTON_LORE,
            player -> {
                IslandManager islandManager = RPGMain.getPlugin().getIslandManager();
                PlayerIslandDataDTO playerIslandData = islandManager.getPlayerIslandDataFromCache(player.getUniqueId().toString());
                
                if (playerIslandData != null && playerIslandData.hasIsland()) {
                    // ?�이 ?�으�???메뉴 ?�기
                    IslandMainGui islandGui = IslandMainGui.create(guiManager, player);
                    guiManager.openGui(player, islandGui);
                    playClickSound(player);
                } else {
                    // ?�이 ?�으�????�성 GUI ?�기
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
                ItemBuilder.of(Material.GOLDEN_APPLE)
                        .displayName(LangManager.text(GeneralLangKey.ITEMS_MAINMENU_LEADERBOARD_BUTTON_NAME, viewer))
                        .addLore(LangManager.list(GeneralLangKey.ITEMS_MAINMENU_LEADERBOARD_BUTTON_LORE, viewer))
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
