package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandSettingsDTO;
import com.febrie.rpg.dto.island.PlayerIslandDataDTO;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.island.permission.IslandPermissionHandler;
import com.febrie.rpg.util.DateFormatUtil;
import com.febrie.rpg.util.GuiHandlerUtil;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangHelper;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Island Main GUI
 * Main interface for accessing all island-related features
 *
 * @author Febrie, CoffeeTory
 */
public class IslandMainGui extends BaseGui {

    private final IslandManager islandManager;
    private final PlayerIslandDataDTO playerIslandData;
    private final IslandDTO island;
    private final boolean isOwner;
    private final boolean isCoOwner;
    private final boolean isMember;
    private final boolean isWorker;

    private IslandMainGui(@NotNull GuiManager guiManager, @NotNull Player player) {
        super(player, guiManager, 54, LangHelper.text(LangKey.GUI_ISLAND_MAIN_TITLE, player));
        this.islandManager = RPGMain.getInstance().getIslandManager();

        // Get player island data
        String uuid = player.getUniqueId().toString();
        this.playerIslandData = islandManager.getPlayerIslandDataFromCache(uuid);

        if (playerIslandData != null && playerIslandData.hasIsland()) {
            String currentIslandId = playerIslandData.currentIslandId();
            if (currentIslandId != null) {
                this.island = islandManager.getIslandFromCache(currentIslandId);
            } else {
                this.island = null;
            }
            // ??�� ?�인
            this.isOwner = island != null && island.core().ownerUuid().equals(uuid);
            this.isCoOwner = island != null && island.membership().members().stream()
                    .anyMatch(m -> m.uuid().equals(uuid) && m.isCoOwner());
            this.isMember = island != null && island.membership().members().stream()
                    .anyMatch(m -> m.uuid().equals(uuid) && !m.isCoOwner());
            this.isWorker = island != null && island.membership().workers().stream()
                    .anyMatch(w -> w.uuid().equals(uuid));
        } else {
            this.island = null;
            this.isOwner = false;
            this.isCoOwner = false;
            this.isMember = false;
            this.isWorker = false;
        }
    }

    /**
     * IslandMainGui ?�스?�스�??�성?�고 초기?�합?�다.
     *
     * @param guiManager  GUI 매니?�
     * @param langManager ?�어 매니?�
     * @param player      ?�레?�어
     * @return 초기?�된 IslandMainGui ?�스?�스
     */
    public static IslandMainGui create(@NotNull GuiManager guiManager, @NotNull Player player) {
        return new IslandMainGui(guiManager, player);
    }

    @Override
    public @NotNull Component getTitle() {
        if (island != null) {
            return LangHelper.text(LangKey.ISLAND_GUI_MAIN_TITLE_WITH_NAME, viewer, Component.text(island.core().islandName()));
        } else {
            return LangHelper.text(LangKey.ISLAND_GUI_MAIN_TITLE, viewer);
        }
    }

    @Override
    protected GuiFramework getBackTarget() {
        // Return to main menu
        return null;
    }

    @Override
    protected void setupLayout() {
        if (island == null) {
            setupNoIslandLayout();
        } else {
            setupIslandLayout();
        }
        setupStandardNavigation(true, true);
    }

    /**
     * ?�이 ?�을 ???�이?�웃
     */
    private void setupNoIslandLayout() {
        createBorder();

        // 중앙?????�성 ?�내
        GuiItem createIslandInfo = GuiItem.display(ItemBuilder.of(Material.GRASS_BLOCK, getViewerLocale())
                .displayNameTranslated("island.gui.main.create-island.title")
                .addLore(Component.empty())
                .addLoreTranslated("island.gui.main.create-island.no-island")
                .addLore(Component.empty())
                .addLoreTranslated("island.gui.main.create-island.description")
                .addLoreTranslated("island.gui.main.create-island.feature-1")
                .addLoreTranslated("island.gui.main.create-island.feature-2")
                .addLoreTranslated("island.gui.main.create-island.feature-3")
                .addLoreTranslated("island.gui.main.create-island.feature-4")
                .addLore(Component.empty())
                .addLoreTranslated("island.gui.main.create-island.contact-admin")
                .hideAllFlags()
                .build());
        setItem(22, createIslandInfo);
    }

    /**
     * ?�이 ?�을 ???�이?�웃
     */
    private void setupIslandLayout() {
        createBorder();

        // ???�보
        setItem(13, createIslandInfoItem());

        // ????��???�른 메뉴 구성
        if (isOwner) {
            // ?�장 메뉴
            setupOwnerMenu();
        } else if (isCoOwner) {
            // 부?�장 메뉴
            setupCoOwnerMenu();
        } else if (isMember) {
            // 멤버 메뉴
            setupMemberMenu();
        } else if (isWorker) {
            // ?�바 메뉴
            setupWorkerMenu();
        } else {
            // 방문??메뉴
            setupVisitorMenu();
        }
    }

    /**
     * ?�장 메뉴 ?�정
     */
    private void setupOwnerMenu() {
        setItem(13, createIslandInfoItem());
        setItem(20, createMemberManagementItem());
        setItem(21, createPermissionManagementItem());
        setItem(22, createUpgradeItem());
        setItem(23, createContributionItem());
        setItem(24, createSpawnSettingsItem());
        setItem(30, createIslandSettingsItem());
        // 방문??목록?� 권한 체크 ???�시
        if (IslandPermissionHandler.hasPermission(island, viewer, "VIEW_VISITORS")) {
            setItem(31, createVisitorListItem());
        }
        setItem(32, createWarpItem());
        setItem(33, createBiomeChangeItem());
    }

    /**
     * 부?�장 메뉴 ?�정
     */
    private void setupCoOwnerMenu() {
        setItem(13, createIslandInfoItem());
        setItem(20, createMemberManagementItem());
        setItem(22, createUpgradeItem());
        setItem(23, createContributionItem());
        setItem(24, createSpawnSettingsItem());
        // 방문??목록?� 권한 체크 ???�시
        if (IslandPermissionHandler.hasPermission(island, viewer, "VIEW_VISITORS")) {
            setItem(31, createVisitorListItem());
        }
        setItem(32, createWarpItem());
        // 바이??변경�? 권한 ?�는 경우�??�시
        if (island.membership().members().stream()
                .anyMatch(m -> m.uuid().equals(viewer.getUniqueId().toString()) && m.isCoOwner())) {
            setItem(33, createBiomeChangeItem());
        }
    }

    /**
     * 멤버 메뉴 ?�정
     */
    private void setupMemberMenu() {
        setItem(13, createIslandInfoItem());
        // ?�그?�이?�는 권한 체크 ???�시
        if (IslandPermissionHandler.hasPermission(island, viewer, "UPGRADE_ISLAND")) {
            setItem(22, createUpgradeItem());
        }
        setItem(23, createContributionItem());
        // 방문??목록?� 권한 체크 ???�시
        if (IslandPermissionHandler.hasPermission(island, viewer, "VIEW_VISITORS")) {
            setItem(31, createVisitorListItem());
        }
        setItem(32, createWarpItem());
    }

    /**
     * ?�바 메뉴 ?�정
     */
    private void setupWorkerMenu() {
        setItem(13, createIslandInfoItem());
        setItem(23, createContributionItem());
        // 방문??목록?� 권한 체크 ???�시
        if (IslandPermissionHandler.hasPermission(island, viewer, "VIEW_VISITORS")) {
            setItem(31, createVisitorListItem());
        }
        setItem(32, createWarpItem());
    }

    /**
     * 방문??메뉴 ?�정
     */
    private void setupVisitorMenu() {
        setItem(13, createIslandInfoItem());
        setItem(23, createContributionItem()); // 방문?�도 기여???�위�?�????�도�?추�?
        // 방문??목록?� 권한 체크 ???�시 (방문?�는 기본?�으�?권한 ?�음)
        if (IslandPermissionHandler.hasPermission(island, viewer, "VIEW_VISITORS")) {
            setItem(31, createVisitorListItem());
        }
        setItem(32, createWarpItem());
    }

    /**
     * ???�보 ?�이???�성
     */
    private GuiItem createIslandInfoItem() {
        // ???�름???�정???�상 ?�용
        net.kyori.adventure.text.format.TextColor nameColor = UnifiedColorUtil.parseHexColor(island.configuration()
                .settings().nameColorHex());
        
        ItemBuilder builder = ItemBuilder.of(Material.GRASS_BLOCK)
                .displayName(Component.text(island.core().islandName(), nameColor))
                .addLore(LangHelper.list(LangKey.ITEMS_ISLAND_MAIN_INFO_LORE, viewer))
                .hideAllFlags();

        // ?�장?�나 부?�장??경우 ?�상 변�??�내 추�?
        if (isOwner || isCoOwner) {
            builder.addLore(Component.empty());
            builder.addLore(LangHelper.text(LangKey.GUI_ISLAND_MAIN_COLOR_CHANGE_HINT, viewer).color(UnifiedColorUtil.AQUA));
        }

        ItemStack itemStack = builder.build();

        // ?�장?�나 부?�장??경우?�만 ?�릭 ?�벤??추�?
        if (isOwner || isCoOwner) {
            return GuiItem.clickable(itemStack, this::handleColorChange);
        }

        return GuiItem.of(itemStack);
    }

    /**
     * ???�상 변�?처리
     */
    private void handleColorChange(Player player) {
        new AnvilGUI.Builder().onClick((slot, stateSnapshot) -> {
            if (slot != AnvilGUI.Slot.OUTPUT) {
                return java.util.Collections.emptyList();
            }
            String hexColor = stateSnapshot.getText();
            // HEX code validation
            if (!hexColor.matches("^#[0-9A-Fa-f]{6}$")) {
                player.sendMessage(LangHelper.text(LangKey.GUI_ISLAND_MAIN_HEX_FORMAT_ERROR, player).color(UnifiedColorUtil.ERROR));
                String hexFormatExample = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                        .serialize(LangHelper.text(LangKey.GUI_ISLAND_MAIN_HEX_FORMAT_EXAMPLE, player));
                return java.util.Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(hexFormatExample));
            }

            // Update settings
            IslandSettingsDTO newSettings = new IslandSettingsDTO(hexColor, island.configuration().settings()
                    .biome(), island.configuration().settings().template());

            IslandDTO updated = GuiHandlerUtil.updateIslandSettings(island, newSettings);
            islandManager.updateIsland(updated);

            player.sendMessage(LangHelper.text(LangKey.GUI_ISLAND_MAIN_COLOR_CHANGED, player).color(UnifiedColorUtil.SUCCESS));

            return java.util.Collections.singletonList(AnvilGUI.ResponseAction.close());
                }).onClose(closePlayer -> {
                    // GUI ?�시 ?�기
                    Bukkit.getScheduler().runTaskLater(RPGMain.getInstance(), () -> IslandMainGui.create(guiManager, viewer)
                            .open(viewer), 1L);
                }).text(island.configuration().settings().nameColorHex())
                        .title(net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                                .serialize(LangHelper.text(LangKey.GUI_ISLAND_MAIN_HEX_INPUT_TITLE, player)))
                        .plugin(RPGMain.getInstance())
                .open(player);
    }

    /**
     * 멤버 관�??�이??     */
    private GuiItem createMemberManagementItem() {
        return GuiItem.clickable(
                ItemBuilder.of(Material.PLAYER_HEAD, getViewerLocale())
                        .displayNameTranslated("items.island.member.invite.name")
                        .addLore(LangHelper.list(LangKey.ITEMS_ISLAND_MEMBER_INVITE_LORE, viewer))
                        .hideAllFlags()
                        .build(),
                player -> {
                    player.closeInventory();
                    IslandMemberGui.create(guiManager, player, island).open(player);
                    playClickSound(player);
                }
        );
    }

    /**
     * 권한 관�??�이??     */
    private GuiItem createPermissionManagementItem() {
        return GuiItem.clickable(
                ItemBuilder.of(Material.COMMAND_BLOCK, getViewerLocale())
                        .displayNameTranslated("items.island.member.permission.name")
                        .addLore(LangHelper.list(LangKey.ITEMS_ISLAND_MEMBER_PERMISSION_LORE, viewer))
                        .hideAllFlags()
                        .build(),
                player -> {
                    player.closeInventory();
                    IslandPermissionGui.create(RPGMain.getInstance(), RPGMain.getInstance().getIslandManager(), island, player)
                            .open(player);
                    playClickSound(player);
                }
        );
    }

    /**
     * ?�그?�이???�이??     */
    private GuiItem createUpgradeItem() {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ANVIL, getViewerLocale())
                        .displayNameTranslated("items.island.main.upgrade-info.name")
                        .addLore(LangHelper.list(LangKey.ITEMS_ISLAND_MAIN_UPGRADE_INFO_LORE, viewer))
                        .hideAllFlags()
                        .build(),
                player -> {
                    player.closeInventory();
                    IslandUpgradeGui.create(RPGMain.getInstance(), player, island).open(player);
                    playClickSound(player);
                }
        );
    }

    /**
     * 기여???�이??     */
    private GuiItem createContributionItem() {
        long myContribution = island.membership().contributions().getOrDefault(viewer.getUniqueId().toString(), 0L);
        
        return GuiItem.clickable(
                ItemBuilder.of(Material.EMERALD, getViewerLocale())
                        .displayNameTranslated("items.island.main.contribution-info.name")
                        .addLore(LangHelper.list(LangKey.ITEMS_ISLAND_MAIN_CONTRIBUTION_INFO_LORE, viewer))
                        .hideAllFlags()
                        .build(),
                player -> {
                    IslandContributionGui.create(guiManager, player, island, 1).open(player);
                    playClickSound(player);
                }
        );
    }

    /**
     * ?�폰 ?�정 ?�이??     */
    private GuiItem createSpawnSettingsItem() {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ENDER_PEARL, getViewerLocale())
                        .displayNameTranslated("items.island.spawn.current-info.name")
                        .addLore(LangHelper.list(LangKey.ITEMS_ISLAND_SPAWN_CURRENT_INFO_LORE, viewer))
                        .hideAllFlags()
                        .build(),
                player -> {
                    player.closeInventory();
                    IslandSpawnSettingsGui.create(RPGMain.getInstance(), player, island).open(player);
                    playClickSound(player);
                }
        );
    }

    /**
     * ???�정 ?�이??     */
    private GuiItem createIslandSettingsItem() {
        return GuiItem.clickable(
                ItemBuilder.of(Material.COMPARATOR, getViewerLocale())
                        .displayNameTranslated("items.island.main.island-settings.name")
                        .addLore(LangHelper.list(LangKey.ITEMS_ISLAND_MAIN_ISLAND_SETTINGS_LORE, viewer))
                        .hideAllFlags()
                        .build(),
                player -> {
                    player.closeInventory();
                    IslandSettingsGui.create(RPGMain.getInstance(), player, island).open(player);
                    playClickSound(player);
                }
        );
    }

    /**
     * 방문??목록 ?�이??     */
    private GuiItem createVisitorListItem() {
        // ?�재 방문????계산
        var visitListener = RPGMain.getInstance().getIslandVisitListener();
        int currentVisitorCount = visitListener != null ? visitListener.getCurrentVisitors(island.core().islandId())
                .size() : 0;
        
        return GuiItem.clickable(
                ItemBuilder.of(Material.BOOK, getViewerLocale())
                        .displayNameTranslated("items.island.main.visitor.name")
                        .addLore(LangHelper.list(LangKey.ITEMS_ISLAND_MAIN_VISITOR_LORE, viewer))
                        .hideAllFlags()
                        .build(),
                player -> {
                    player.closeInventory();
                    IslandVisitorMenuGui.create(guiManager, player, island).open(player);
                    playClickSound(player);
                }
        );
    }

    /**
     * 바이??변�??�이??     */
    private GuiItem createBiomeChangeItem() {
        return GuiItem.clickable(
                ItemBuilder.of(Material.GRASS_BLOCK, getViewerLocale())
                        .displayNameTranslated("items.island.main.biome-change.name")
                        .addLore(LangHelper.list(LangKey.ITEMS_ISLAND_MAIN_BIOME_CHANGE_LORE, viewer))
                        .hideAllFlags()
                        .build(),
                player -> {
                    player.closeInventory();
                    var gui = IslandBiomeChangeGui.create(RPGMain.getInstance(), player, island);
                    if (gui != null) {
                        gui.open(player);
                        playClickSound(player);
                    }
                }
        );
    }

    /**
     * ?�프 ?�이??     */
    private GuiItem createWarpItem() {
        return GuiItem.clickable(
                ItemBuilder.of(Material.COMPASS, getViewerLocale())
                        .displayNameTranslated("items.island.main.warp.name")
                        .addLore(LangHelper.list(LangKey.ITEMS_ISLAND_MAIN_WARP_LORE, viewer))
                        .hideAllFlags()
                        .build(),
                this::handleWarp
        );
    }


    /**
     * ?�으�??�프
     */
    private void handleWarp(@NotNull Player player) {
        player.closeInventory();
        player.sendMessage(LangHelper.text(LangKey.GUI_ISLAND_MAIN_WARP_MOVING, player).color(UnifiedColorUtil.YELLOW));

        // Get spawn location
        var spawn = island.configuration().spawnData().defaultSpawn()
                .toLocation(islandManager.getWorldManager().getIslandWorld());
        spawn.setY(spawn.getY() + 4);

        Bukkit.getScheduler().runTask(RPGMain.getInstance(), () -> {
            player.teleport(spawn);
            player.sendMessage(LangHelper.text(LangKey.GUI_ISLAND_MAIN_WARP_SUCCESS, player).color(UnifiedColorUtil.SUCCESS));
        });
    }
}
