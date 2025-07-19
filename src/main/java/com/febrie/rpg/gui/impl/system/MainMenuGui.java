package com.febrie.rpg.gui.impl.system;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.builder.GuiBuilder;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.player.ProfileGui;
import com.febrie.rpg.gui.impl.island.IslandMainGui;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.dto.island.PlayerIslandDataDTO;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import com.febrie.rpg.gui.manager.GuiManager;
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
        
        // 프로필 버튼 (상단)
        builder.menuButton(PROFILE_SLOT, 
            new ItemBuilder(viewer)
                .displayName(trans("items.mainmenu.profile-button.name"))
                .lore(langManager.getComponentList(viewer, "items.mainmenu.profile-button.lore"))
                .build(),
            player -> {
                ProfileGui profileGui = new ProfileGui(guiManager, langManager, player);
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
                    player.sendMessage(Component.text("허브로 이동했습니다!", ColorUtil.SUCCESS));
                    playClickSound(player);
                } else {
                    player.sendMessage(Component.text("허브 월드를 찾을 수 없습니다.", ColorUtil.ERROR));
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
                IslandManager islandManager = RPGMain.getInstance().getIslandManager();
                PlayerIslandDataDTO playerIslandData = islandManager.getPlayerIslandDataFromCache(player.getUniqueId().toString());
                
                if (playerIslandData != null && playerIslandData.hasIsland()) {
                    // 섬이 있으면 섬 메뉴 열기
                    IslandMainGui islandGui = new IslandMainGui(guiManager, langManager, player);
                    guiManager.openGui(player, islandGui);
                    playClickSound(player);
                } else {
                    // 섬이 없으면 섬 생성 안내
                    player.sendMessage(Component.text("아직 섬이 없습니다!", ColorUtil.ERROR));
                    player.sendMessage(Component.text("/섬 명령어로 섬을 생성할 수 있습니다.", ColorUtil.YELLOW));
                }
            });
    }

    /**
     * 리더보드 버튼
     */
    private void setupLeaderboardButton() {
        GuiItem leaderboardButton = GuiItem.clickable(
                new ItemBuilder(Material.GOLDEN_APPLE)
                        .displayName(trans("items.mainmenu.leaderboard-button.name"))
                        .lore(langManager.getComponentList(viewer, "items.mainmenu.leaderboard-button.lore"))
                        .build(),
                player -> {
                    LeaderboardGui leaderboardGui = new LeaderboardGui(guiManager, langManager, player);
                    guiManager.openGui(player, leaderboardGui);
                    playClickSound(player);
                }
        );
        setItem(LEADERBOARD_SLOT, leaderboardButton);
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
}