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
import com.febrie.rpg.util.StandardItemBuilder;
import com.febrie.rpg.util.UnifiedColorUtil;
import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 섬 메인 GUI
 * 섬 관련 모든 기능에 접근할 수 있는 메인 인터페이스
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
        super(player, guiManager, 54, "gui.island.main.title");
        this.islandManager = RPGMain.getInstance().getIslandManager();

        // 플레이어의 섬 데이터 가져오기
        String uuid = player.getUniqueId().toString();
        this.playerIslandData = islandManager.getPlayerIslandDataFromCache(uuid);

        if (playerIslandData != null && playerIslandData.hasIsland()) {
            String currentIslandId = playerIslandData.currentIslandId();
            if (currentIslandId != null) {
                this.island = islandManager.getIslandFromCache(currentIslandId);
            } else {
                this.island = null;
            }
            // 역할 확인
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
     * IslandMainGui 인스턴스를 생성하고 초기화합니다.
     *
     * @param guiManager  GUI 매니저
     * @param langManager 언어 매니저
     * @param player      플레이어
     * @return 초기화된 IslandMainGui 인스턴스
     */
    public static IslandMainGui create(@NotNull GuiManager guiManager, @NotNull Player player) {
        IslandMainGui gui = new IslandMainGui(guiManager, player);
        gui.initialize();
        return gui;
    }

    @Override
    public @NotNull Component getTitle() {
        if (island != null) {
            return trans("island.gui.main.title-with-name", "name", island.core().islandName());
        } else {
            return trans("island.gui.main.title");
        }
    }

    @Override
    protected GuiFramework getBackTarget() {
        // 메인 메뉴로 돌아가기
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
     * 섬이 없을 때 레이아웃
     */
    private void setupNoIslandLayout() {
        createBorder();

        // 중앙에 섬 생성 안내
        GuiItem createIslandInfo = GuiItem.display(StandardItemBuilder.guiItem(Material.GRASS_BLOCK)
                .displayName(trans("island.gui.main.create-island.title"))
                .lore(List.of(Component.empty(), trans("island.gui.main.create-island.no-island"), Component.empty(), trans("island.gui.main.create-island.description"), trans("island.gui.main.create-island.feature-1"), trans("island.gui.main.create-island.feature-2"), trans("island.gui.main.create-island.feature-3"), trans("island.gui.main.create-island.feature-4"), Component.empty(), trans("island.gui.main.create-island.contact-admin")))
                .build());
        setItem(22, createIslandInfo);
    }

    /**
     * 섬이 있을 때 레이아웃
     */
    private void setupIslandLayout() {
        createBorder();

        // 섬 정보
        setItem(13, createIslandInfoItem());

        // 섬 역할에 따른 메뉴 구성
        if (isOwner) {
            // 섬장 메뉴
            setupOwnerMenu();
        } else if (isCoOwner) {
            // 부섬장 메뉴
            setupCoOwnerMenu();
        } else if (isMember) {
            // 멤버 메뉴
            setupMemberMenu();
        } else if (isWorker) {
            // 알바 메뉴
            setupWorkerMenu();
        } else {
            // 방문자 메뉴
            setupVisitorMenu();
        }
    }

    /**
     * 섬장 메뉴 설정
     */
    private void setupOwnerMenu() {
        setItem(13, createIslandInfoItem());
        setItem(20, createMemberManagementItem());
        setItem(21, createPermissionManagementItem());
        setItem(22, createUpgradeItem());
        setItem(23, createContributionItem());
        setItem(24, createSpawnSettingsItem());
        setItem(30, createIslandSettingsItem());
        // 방문자 목록은 권한 체크 후 표시
        if (IslandPermissionHandler.hasPermission(island, viewer, "VIEW_VISITORS")) {
            setItem(31, createVisitorListItem());
        }
        setItem(32, createWarpItem());
        setItem(33, createBiomeChangeItem());
    }

    /**
     * 부섬장 메뉴 설정
     */
    private void setupCoOwnerMenu() {
        setItem(13, createIslandInfoItem());
        setItem(20, createMemberManagementItem());
        setItem(22, createUpgradeItem());
        setItem(23, createContributionItem());
        setItem(24, createSpawnSettingsItem());
        // 방문자 목록은 권한 체크 후 표시
        if (IslandPermissionHandler.hasPermission(island, viewer, "VIEW_VISITORS")) {
            setItem(31, createVisitorListItem());
        }
        setItem(32, createWarpItem());
        // 바이옴 변경은 권한 있는 경우만 표시
        if (island.membership().members().stream()
                .anyMatch(m -> m.uuid().equals(viewer.getUniqueId().toString()) && m.isCoOwner())) {
            setItem(33, createBiomeChangeItem());
        }
    }

    /**
     * 멤버 메뉴 설정
     */
    private void setupMemberMenu() {
        setItem(13, createIslandInfoItem());
        // 업그레이드는 권한 체크 후 표시
        if (IslandPermissionHandler.hasPermission(island, viewer, "UPGRADE_ISLAND")) {
            setItem(22, createUpgradeItem());
        }
        setItem(23, createContributionItem());
        // 방문자 목록은 권한 체크 후 표시
        if (IslandPermissionHandler.hasPermission(island, viewer, "VIEW_VISITORS")) {
            setItem(31, createVisitorListItem());
        }
        setItem(32, createWarpItem());
    }

    /**
     * 알바 메뉴 설정
     */
    private void setupWorkerMenu() {
        setItem(13, createIslandInfoItem());
        setItem(23, createContributionItem());
        // 방문자 목록은 권한 체크 후 표시
        if (IslandPermissionHandler.hasPermission(island, viewer, "VIEW_VISITORS")) {
            setItem(31, createVisitorListItem());
        }
        setItem(32, createWarpItem());
    }

    /**
     * 방문자 메뉴 설정
     */
    private void setupVisitorMenu() {
        setItem(13, createIslandInfoItem());
        setItem(23, createContributionItem()); // 방문자도 기여도 순위를 볼 수 있도록 추가
        // 방문자 목록은 권한 체크 후 표시 (방문자는 기본적으로 권한 없음)
        if (IslandPermissionHandler.hasPermission(island, viewer, "VIEW_VISITORS")) {
            setItem(31, createVisitorListItem());
        }
        setItem(32, createWarpItem());
    }

    /**
     * 섬 정보 아이템 생성
     */
    private GuiItem createIslandInfoItem() {
        // 섬 이름에 설정된 색상 적용
        net.kyori.adventure.text.format.TextColor nameColor = UnifiedColorUtil.parseHexColor(island.configuration()
                .settings().nameColorHex());

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(trans("gui.island-main.owner").color(UnifiedColorUtil.GRAY)
                .append(Component.text(island.core().ownerName(), UnifiedColorUtil.WHITE)));
        lore.add(trans("gui.island-main.size").color(UnifiedColorUtil.GRAY)
                .append(Component.text(island.core().size() + " x " + island.core().size(), UnifiedColorUtil.YELLOW)));
        lore.add(trans("gui.island-main.members").color(UnifiedColorUtil.GRAY)
                .append(Component.text(island.getMemberCount() + "/" + island.configuration().upgradeData()
                        .memberLimit(), UnifiedColorUtil.GREEN)));
        lore.add(trans("gui.island-main.workers").color(UnifiedColorUtil.GRAY)
                .append(Component.text(island.membership().workers().size() + "/" + island.configuration().upgradeData()
                        .workerLimit(), UnifiedColorUtil.GREEN)));
        lore.add(Component.empty());
        lore.add(trans("gui.island-main.created-date").color(UnifiedColorUtil.GRAY)
                .append(Component.text(DateFormatUtil.formatFullDateTimeFromMillis(island.core()
                        .createdAt()), UnifiedColorUtil.WHITE)));
        lore.add(trans("gui.island-main.public-status").color(UnifiedColorUtil.GRAY).append(island.core()
                .isPublic() ? trans("gui.island-main.public").color(UnifiedColorUtil.GREEN) : trans("gui.island-main.private").color(UnifiedColorUtil.RED)));
        lore.add(Component.empty());
        lore.add(trans("gui.island-main.my-role").color(UnifiedColorUtil.GRAY)
                .append(isOwner ? trans("gui.island-main.role-owner").color(UnifiedColorUtil.GOLD) : isCoOwner ? trans("gui.island-main.role-co-owner").color(UnifiedColorUtil.YELLOW) : isMember ? trans("gui.island-main.role-member").color(UnifiedColorUtil.GREEN) : isWorker ? trans("gui.island-main.role-worker").color(UnifiedColorUtil.AQUA) : trans("gui.island-main.role-visitor").color(UnifiedColorUtil.GRAY)));

        // 섬장이나 부섬장인 경우 색상 변경 안내 추가
        if (isOwner || isCoOwner) {
            lore.add(Component.empty());
            lore.add(trans("gui.island-main.color-change-hint").color(UnifiedColorUtil.AQUA));
        }

        GuiItem.Builder itemBuilder = GuiItem.builder(StandardItemBuilder.guiItem(Material.GRASS_BLOCK)
                .displayName(Component.text(island.core().islandName(), nameColor)).lore(lore).build());

        // 섬장이나 부섬장인 경우에만 클릭 이벤트 추가
        if (isOwner || isCoOwner) {
            itemBuilder.onAnyClick((player, clickType) -> {
                if (clickType == org.bukkit.event.inventory.ClickType.MIDDLE) {
                    handleColorChange(player);
                }
            });
        }

        return itemBuilder.build();
    }

    /**
     * 섬 색상 변경 처리
     */
    private void handleColorChange(Player player) {
        new AnvilGUI.Builder().onClose((completedPlayer, text) -> {
                    String hexColor = text;
                    // HEX 코드 유효성 검사
                    if (!hexColor.matches("^#[0-9A-Fa-f]{6}$")) {
                        player.sendMessage(trans("gui.island-main.hex-format-error").color(UnifiedColorUtil.ERROR));
                        return AnvilGUI.Response.text("#RRGGBB");
                    }


                    // 섬 설정 업데이트
                    IslandSettingsDTO newSettings = new IslandSettingsDTO(hexColor, island.configuration().settings()
                            .biome(), island.configuration().settings().template());

                    IslandDTO updated = GuiHandlerUtil.updateIslandSettings(island, newSettings);
                    islandManager.updateIsland(updated);

                    player.sendMessage(trans("gui.island-main.color-changed").color(UnifiedColorUtil.SUCCESS));

                    return AnvilGUI.Response.close();
                }).onClose(closePlayer -> {
                    // GUI 다시 열기
                    Bukkit.getScheduler().runTaskLater(RPGMain.getInstance(), () -> IslandMainGui.create(guiManager, viewer)
                            .open(viewer), 1L);
                }).text(island.configuration().settings().nameColorHex()).title(transString("gui.island-main.hex-input-title")).plugin(RPGMain.getInstance())
                .open(player);
    }

    /**
     * 멤버 관리 아이템
     */
    private GuiItem createMemberManagementItem() {
        return GuiItem.clickable(StandardItemBuilder.guiItem(Material.PLAYER_HEAD)
                .displayName(trans("gui.island-main.member-management-title").color(UnifiedColorUtil.GREEN))
                .lore(List.of(Component.empty(), trans("gui.island-main.member-management-desc1").color(UnifiedColorUtil.GRAY), trans("gui.island-main.member-management-desc2").color(UnifiedColorUtil.GRAY), Component.empty(), trans("gui.island-main.click-to-open").color(UnifiedColorUtil.YELLOW)))
                .build(), player -> {
            player.closeInventory();
            IslandMemberGui.create(guiManager, player, island).open(player);
            playClickSound(player);
        });
    }

    /**
     * 권한 관리 아이템
     */
    private GuiItem createPermissionManagementItem() {
        return GuiItem.clickable(StandardItemBuilder.guiItem(Material.COMMAND_BLOCK)
                .displayName(trans("gui.island-main.permission-management-title").color(UnifiedColorUtil.RED))
                .lore(List.of(Component.empty(), trans("gui.island-main.permission-management-desc1").color(UnifiedColorUtil.GRAY), trans("gui.island-main.permission-management-desc2").color(UnifiedColorUtil.GRAY), Component.empty(), trans("gui.island-main.click-to-open").color(UnifiedColorUtil.YELLOW)))
                .build(), player -> {
            player.closeInventory();
            IslandPermissionGui.create(RPGMain.getInstance(), RPGMain.getInstance().getIslandManager(), island, player)
                    .open(player);
            playClickSound(player);
        });
    }

    /**
     * 업그레이드 아이템
     */
    private GuiItem createUpgradeItem() {
        return GuiItem.clickable(StandardItemBuilder.guiItem(Material.ANVIL)
                .displayName(trans("gui.island-main.upgrade-title").color(UnifiedColorUtil.GOLD))
                .lore(List.of(Component.empty(), trans("gui.island-main.upgrade-current-level").color(UnifiedColorUtil.GRAY), trans("gui.island-main.upgrade-size").color(UnifiedColorUtil.WHITE)
                        .append(Component.text(island.configuration().upgradeData()
                                .sizeLevel() + transString("gui.island-main.upgrade-level"), UnifiedColorUtil.YELLOW)), trans("gui.island-main.upgrade-member").color(UnifiedColorUtil.WHITE)
                        .append(Component.text(island.configuration().upgradeData()
                                .memberLimitLevel() + transString("gui.island-main.upgrade-level"), UnifiedColorUtil.YELLOW)), trans("gui.island-main.upgrade-worker").color(UnifiedColorUtil.WHITE)
                        .append(Component.text(island.configuration().upgradeData()
                                .workerLimitLevel() + transString("gui.island-main.upgrade-level"), UnifiedColorUtil.YELLOW)), Component.empty(), trans("gui.island-main.click-to-open").color(UnifiedColorUtil.YELLOW)))
                .build(), player -> {
            player.closeInventory();
            IslandUpgradeGui.create(RPGMain.getInstance(), player, island).open(player);
            playClickSound(player);
        });
    }

    /**
     * 기여도 아이템
     */
    private GuiItem createContributionItem() {
        long myContribution = island.membership().contributions().getOrDefault(viewer.getUniqueId().toString(), 0L);
        return GuiItem.clickable(StandardItemBuilder.guiItem(Material.EMERALD)
                .displayName(trans("gui.island-main.contribution-title").color(UnifiedColorUtil.GREEN))
                .lore(List.of(Component.empty(), trans("gui.island-main.contribution-desc1").color(UnifiedColorUtil.GRAY), trans("gui.island-main.contribution-desc2").color(UnifiedColorUtil.GRAY), Component.empty(), trans("gui.island-main.contribution-my").color(UnifiedColorUtil.GRAY)
                        .append(Component.text(myContribution, UnifiedColorUtil.YELLOW)), Component.empty(), trans("gui.island-main.click-to-open").color(UnifiedColorUtil.YELLOW)))
                .build(), player -> {
            IslandContributionGui.create(guiManager, player, island, 1).open(player);
            playClickSound(player);
        });
    }

    /**
     * 스폰 설정 아이템
     */
    private GuiItem createSpawnSettingsItem() {
        return GuiItem.clickable(StandardItemBuilder.guiItem(Material.ENDER_PEARL)
                .displayName(trans("gui.island-main.spawn-settings-title").color(UnifiedColorUtil.LIGHT_PURPLE))
                .lore(List.of(Component.empty(), trans("gui.island-main.spawn-settings-desc1").color(UnifiedColorUtil.GRAY), trans("gui.island-main.spawn-settings-desc2").color(UnifiedColorUtil.GRAY), Component.empty(), trans("gui.island-main.click-to-open").color(UnifiedColorUtil.YELLOW)))
                .build(), player -> {
            player.closeInventory();
            IslandSpawnSettingsGui.create(RPGMain.getInstance(), player, island).open(player);
            playClickSound(player);
        });
    }

    /**
     * 섬 설정 아이템
     */
    private GuiItem createIslandSettingsItem() {
        return GuiItem.clickable(StandardItemBuilder.guiItem(Material.COMPARATOR)
                .displayName(trans("gui.island-main.island-settings-title").color(UnifiedColorUtil.BLUE))
                .lore(List.of(Component.empty(), trans("gui.island-main.island-settings-desc1").color(UnifiedColorUtil.GRAY), trans("gui.island-main.island-settings-desc2").color(UnifiedColorUtil.GRAY), trans("gui.island-main.island-settings-desc3").color(UnifiedColorUtil.GRAY), Component.empty(), trans("gui.island-main.click-to-open").color(UnifiedColorUtil.YELLOW)))
                .build(), player -> {
            player.closeInventory();
            IslandSettingsGui.create(RPGMain.getInstance(), player, island).open(player);
            playClickSound(player);
        });
    }

    /**
     * 방문자 목록 아이템
     */
    private GuiItem createVisitorListItem() {
        // 현재 방문자 수 계산
        var visitListener = RPGMain.getInstance().getIslandVisitListener();
        int currentVisitorCount = visitListener != null ? visitListener.getCurrentVisitors(island.core().islandId())
                .size() : 0;

        return GuiItem.clickable(StandardItemBuilder.guiItem(Material.BOOK)
                .displayName(trans("gui.island-main.visitor-management-title").color(UnifiedColorUtil.WHITE))
                .lore(List.of(Component.empty(), trans("gui.island-main.visitor-management-desc1").color(UnifiedColorUtil.GRAY), trans("gui.island-main.visitor-management-desc2").color(UnifiedColorUtil.GRAY), Component.empty(), trans("gui.island-main.visitor-management-recent").color(UnifiedColorUtil.GRAY)
                        .append(Component.text(island.social().recentVisits()
                                .size() + transString("gui.island-main.people-count"), UnifiedColorUtil.YELLOW)), trans("gui.island-main.visitor-management-current").color(UnifiedColorUtil.GRAY)
                        .append(Component.text(currentVisitorCount + transString("gui.island-main.people-count"), UnifiedColorUtil.AQUA)), Component.empty(), trans("gui.island-main.visitor-management-history").color(UnifiedColorUtil.DARK_GRAY), trans("gui.island-main.visitor-management-realtime").color(UnifiedColorUtil.DARK_GRAY), Component.empty(), trans("gui.island-main.visitor-management-open-menu").color(UnifiedColorUtil.GREEN)))
                .build(), player -> {
            player.closeInventory();
            IslandVisitorMenuGui.create(guiManager, player, island).open(player);
            playClickSound(player);
        });
    }

    /**
     * 바이옴 변경 아이템
     */
    private GuiItem createBiomeChangeItem() {
        return GuiItem.clickable(StandardItemBuilder.guiItem(Material.GRASS_BLOCK)
                .displayName(trans("gui.island-main.biome-change-title").color(UnifiedColorUtil.GREEN))
                .lore(Arrays.asList(Component.empty(), trans("gui.island-main.biome-change-desc1").color(UnifiedColorUtil.GRAY), trans("gui.island-main.biome-change-desc2").color(UnifiedColorUtil.GRAY), Component.empty(), trans("gui.island-main.click-to-open").color(UnifiedColorUtil.YELLOW)))
                .build(), player -> {
            player.closeInventory();
            var gui = IslandBiomeChangeGui.create(RPGMain.getInstance(), player, island);
            if (gui != null) {
                gui.open(player);
                playClickSound(player);
            }
        });
    }

    /**
     * 워프 아이템
     */
    private GuiItem createWarpItem() {
        return GuiItem.clickable(StandardItemBuilder.guiItem(Material.COMPASS)
                .displayName(trans("gui.island-main.warp-title").color(UnifiedColorUtil.AQUA))
                .lore(List.of(Component.empty(), trans("gui.island-main.warp-desc").color(UnifiedColorUtil.GRAY), Component.empty(), trans("gui.island-main.click-to-warp").color(UnifiedColorUtil.YELLOW)))
                .build(), this::handleWarp);
    }


    /**
     * 섬으로 워프
     */
    private void handleWarp(@NotNull Player player) {
        player.closeInventory();
        player.sendMessage(trans("gui.island-main.warp-moving").color(UnifiedColorUtil.YELLOW));

        // 스폰 위치 가져오기
        var spawn = island.configuration().spawnData().defaultSpawn()
                .toLocation(islandManager.getWorldManager().getIslandWorld());
        spawn.setY(spawn.getY() + 4);

        Bukkit.getScheduler().runTask(RPGMain.getInstance(), () -> {
            player.teleport(spawn);
            player.sendMessage(trans("gui.island-main.warp-success").color(UnifiedColorUtil.SUCCESS));
        });
    }
}