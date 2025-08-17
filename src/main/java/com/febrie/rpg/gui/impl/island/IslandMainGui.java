package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandMemberDTO;
import com.febrie.rpg.dto.island.PlayerIslandDataDTO;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.island.permission.IslandPermissionHandler;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.StandardItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import com.febrie.rpg.util.DateFormatUtil;

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
    
    private IslandMainGui(@NotNull GuiManager guiManager, 
                        @NotNull Player player) {
        super(player, guiManager, 54, "gui.island.main.title");
        this.islandManager = RPGMain.getInstance().getIslandManager();
        
        // 플레이어의 섬 데이터 가져오기
        String uuid = player.getUniqueId().toString();
        this.playerIslandData = islandManager.getPlayerIslandDataFromCache(uuid);
        
        if (playerIslandData != null && playerIslandData.hasIsland()) {
            this.island = islandManager.getIslandFromCache(playerIslandData.currentIslandId());
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
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param player 플레이어
     * @return 초기화된 IslandMainGui 인스턴스
     */
    public static IslandMainGui create(@NotNull GuiManager guiManager,
                                      @NotNull Player player) {
        IslandMainGui gui = new IslandMainGui(guiManager, player);
        gui.initialize("gui.island.main.title");
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
        GuiItem createIslandInfo = GuiItem.display(
            StandardItemBuilder.guiItem(Material.GRASS_BLOCK)
                .displayName(trans("island.gui.main.create-island.title"))
                .lore(List.of(
                    Component.empty(),
                    trans("island.gui.main.create-island.no-island"),
                    Component.empty(),
                    trans("island.gui.main.create-island.description"),
                    trans("island.gui.main.create-island.feature-1"),
                    trans("island.gui.main.create-island.feature-2"),
                    trans("island.gui.main.create-island.feature-3"),
                    trans("island.gui.main.create-island.feature-4"),
                    Component.empty(),
                    trans("island.gui.main.create-island.contact-admin")
                ))
                .build()
        );
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
        net.kyori.adventure.text.format.TextColor nameColor = UnifiedColorUtil.parseHexColor(island.configuration().settings().nameColorHex());
        
        return GuiItem.display(
            StandardItemBuilder.guiItem(Material.GRASS_BLOCK)
                .displayName(Component.text(island.core().islandName(), nameColor))
                .lore(List.of(
                    Component.empty(),
                    Component.text("섬장: ", UnifiedColorUtil.GRAY).append(Component.text(island.core().ownerName(), UnifiedColorUtil.WHITE)),
                    Component.text("크기: ", UnifiedColorUtil.GRAY).append(Component.text(island.core().size() + " x " + island.core().size(), UnifiedColorUtil.YELLOW)),
                    Component.text("멤버: ", UnifiedColorUtil.GRAY).append(Component.text(island.getMemberCount() + "/" + island.configuration().upgradeData().memberLimit(), UnifiedColorUtil.GREEN)),
                    Component.text("알바: ", UnifiedColorUtil.GRAY).append(Component.text(island.membership().workers().size() + "/" + island.configuration().upgradeData().workerLimit(), UnifiedColorUtil.GREEN)),
                    Component.empty(),
                    Component.text("생성일: ", UnifiedColorUtil.GRAY).append(Component.text(DateFormatUtil.formatFullDateTimeFromMillis(island.core().createdAt()), UnifiedColorUtil.WHITE)),
                    Component.text("공개 여부: ", UnifiedColorUtil.GRAY).append(
                        island.core().isPublic() ? 
                        Component.text("공개", UnifiedColorUtil.GREEN) : 
                        Component.text("비공개", UnifiedColorUtil.RED)
                    ),
                    Component.empty(),
                    Component.text("내 역할: ", UnifiedColorUtil.GRAY).append(
                        isOwner ? Component.text("섬장", UnifiedColorUtil.GOLD) :
                        isCoOwner ? Component.text("부섬장", UnifiedColorUtil.YELLOW) :
                        isMember ? Component.text("멤버", UnifiedColorUtil.GREEN) :
                        isWorker ? Component.text("알바", UnifiedColorUtil.AQUA) :
                        Component.text("방문자", UnifiedColorUtil.GRAY)
                    )
                ))
                .build()
        );
    }
    
    /**
     * 멤버 관리 아이템
     */
    private GuiItem createMemberManagementItem() {
        return GuiItem.clickable(
            StandardItemBuilder.guiItem(Material.PLAYER_HEAD)
                .displayName(Component.text("멤버 관리", UnifiedColorUtil.GREEN))
                .lore(List.of(
                    Component.empty(),
                    Component.text("섬원을 초대하거나", UnifiedColorUtil.GRAY),
                    Component.text("추방할 수 있습니다.", UnifiedColorUtil.GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 열기", UnifiedColorUtil.YELLOW)
                ))
                .build(),
            player -> {
                player.closeInventory();
                IslandMemberGui.create(guiManager, player, island).open(player);
                playClickSound(player);
            }
        );
    }
    
    /**
     * 권한 관리 아이템
     */
    private GuiItem createPermissionManagementItem() {
        return GuiItem.clickable(
            StandardItemBuilder.guiItem(Material.COMMAND_BLOCK)
                .displayName(Component.text("권한 관리", UnifiedColorUtil.RED))
                .lore(List.of(
                    Component.empty(),
                    Component.text("각 역할의 권한을", UnifiedColorUtil.GRAY),
                    Component.text("설정할 수 있습니다.", UnifiedColorUtil.GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 열기", UnifiedColorUtil.YELLOW)
                ))
                .build(),
            player -> {
                player.closeInventory();
                IslandPermissionGui.create(RPGMain.getInstance(), RPGMain.getInstance().getIslandManager(), island, player).open(player);
                playClickSound(player);
            }
        );
    }
    
    /**
     * 업그레이드 아이템
     */
    private GuiItem createUpgradeItem() {
        return GuiItem.clickable(
            StandardItemBuilder.guiItem(Material.ANVIL)
                .displayName(Component.text("업그레이드", UnifiedColorUtil.GOLD))
                .lore(List.of(
                    Component.empty(),
                    Component.text("현재 레벨:", UnifiedColorUtil.GRAY),
                    Component.text("  크기: ", UnifiedColorUtil.WHITE).append(Component.text(island.configuration().upgradeData().sizeLevel() + " 레벨", UnifiedColorUtil.YELLOW)),
                    Component.text("  멤버: ", UnifiedColorUtil.WHITE).append(Component.text(island.configuration().upgradeData().memberLimitLevel() + " 레벨", UnifiedColorUtil.YELLOW)),
                    Component.text("  알바: ", UnifiedColorUtil.WHITE).append(Component.text(island.configuration().upgradeData().workerLimitLevel() + " 레벨", UnifiedColorUtil.YELLOW)),
                    Component.empty(),
                    Component.text("▶ 클릭하여 열기", UnifiedColorUtil.YELLOW)
                ))
                .build(),
            player -> {
                player.closeInventory();
                IslandUpgradeGui.create(RPGMain.getInstance(), player, island).open(player);
                playClickSound(player);
            }
        );
    }
    
    /**
     * 기여도 아이템
     */
    private GuiItem createContributionItem() {
        long myContribution = island.membership().contributions().getOrDefault(viewer.getUniqueId().toString(), 0L);
        return GuiItem.clickable(
            StandardItemBuilder.guiItem(Material.EMERALD)
                .displayName(Component.text("기여도", UnifiedColorUtil.GREEN))
                .lore(List.of(
                    Component.empty(),
                    Component.text("섬원들의 기여도를", UnifiedColorUtil.GRAY),
                    Component.text("확인할 수 있습니다.", UnifiedColorUtil.GRAY),
                    Component.empty(),
                    Component.text("내 기여도: ", UnifiedColorUtil.GRAY).append(Component.text(myContribution, UnifiedColorUtil.YELLOW)),
                    Component.empty(),
                    Component.text("▶ 클릭하여 열기", UnifiedColorUtil.YELLOW)
                ))
                .build(),
            player -> {
                IslandContributionGui.create(guiManager, player, island, 1).open(player);
                playClickSound(player);
            }
        );
    }
    
    /**
     * 스폰 설정 아이템
     */
    private GuiItem createSpawnSettingsItem() {
        return GuiItem.clickable(
            StandardItemBuilder.guiItem(Material.ENDER_PEARL)
                .displayName(Component.text("스폰 설정", UnifiedColorUtil.LIGHT_PURPLE))
                .lore(List.of(
                    Component.empty(),
                    Component.text("섬의 스폰 위치를", UnifiedColorUtil.GRAY),
                    Component.text("관리할 수 있습니다.", UnifiedColorUtil.GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 열기", UnifiedColorUtil.YELLOW)
                ))
                .build(),
            player -> {
                player.closeInventory();
                IslandSpawnSettingsGui.create(RPGMain.getInstance(), player, island).open(player);
                playClickSound(player);
            }
        );
    }
    
    /**
     * 섬 설정 아이템
     */
    private GuiItem createIslandSettingsItem() {
        return GuiItem.clickable(
            StandardItemBuilder.guiItem(Material.COMPARATOR)
                .displayName(Component.text("섬 설정", UnifiedColorUtil.BLUE))
                .lore(List.of(
                    Component.empty(),
                    Component.text("섬 이름 변경", UnifiedColorUtil.GRAY),
                    Component.text("공개/비공개 설정", UnifiedColorUtil.GRAY),
                    Component.text("바이옴 변경", UnifiedColorUtil.GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 열기", UnifiedColorUtil.YELLOW)
                ))
                .build(),
            player -> {
                player.closeInventory();
                IslandSettingsGui.create(RPGMain.getInstance(), player, island).open(player);
                playClickSound(player);
            }
        );
    }
    
    /**
     * 방문자 목록 아이템
     */
    private GuiItem createVisitorListItem() {
        // 현재 방문자 수 계산
        var visitListener = RPGMain.getInstance().getIslandVisitListener();
        int currentVisitorCount = visitListener != null ? 
            visitListener.getCurrentVisitors(island.core().islandId()).size() : 0;
        
        return GuiItem.clickable(
            StandardItemBuilder.guiItem(Material.BOOK)
                .displayName(Component.text("방문자 관리", UnifiedColorUtil.WHITE))
                .lore(List.of(
                    Component.empty(),
                    Component.text("방문자 관련 정보를", UnifiedColorUtil.GRAY),
                    Component.text("확인할 수 있습니다.", UnifiedColorUtil.GRAY),
                    Component.empty(),
                    Component.text("최근 방문자: ", UnifiedColorUtil.GRAY).append(Component.text(island.social().recentVisits().size() + "명", UnifiedColorUtil.YELLOW)),
                    Component.text("현재 방문자: ", UnifiedColorUtil.GRAY).append(Component.text(currentVisitorCount + "명", UnifiedColorUtil.AQUA)),
                    Component.empty(),
                    Component.text("• 방문 히스토리", UnifiedColorUtil.DARK_GRAY),
                    Component.text("• 현재 방문자 (실시간)", UnifiedColorUtil.DARK_GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 메뉴 열기", UnifiedColorUtil.GREEN)
                ))
                .build(),
            player -> {
                player.closeInventory();
                IslandVisitorMenuGui.create(guiManager, player, island).open(player);
                playClickSound(player);
            }
        );
    }
    
    /**
     * 바이옴 변경 아이템
     */
    private GuiItem createBiomeChangeItem() {
        return GuiItem.clickable(
            StandardItemBuilder.guiItem(Material.GRASS_BLOCK)
                .displayName(Component.text("바이옴 변경", UnifiedColorUtil.GREEN))
                .lore(Arrays.asList(
                    Component.empty(),
                    Component.text("섬의 바이옴을", UnifiedColorUtil.GRAY),
                    Component.text("변경할 수 있습니다.", UnifiedColorUtil.GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 열기", UnifiedColorUtil.YELLOW)
                ))
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
     * 워프 아이템
     */
    private GuiItem createWarpItem() {
        return GuiItem.clickable(
            StandardItemBuilder.guiItem(Material.COMPASS)
                .displayName(Component.text("섬으로 이동", UnifiedColorUtil.AQUA))
                .lore(List.of(
                    Component.empty(),
                    Component.text("섬으로 순간이동합니다.", UnifiedColorUtil.GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 이동", UnifiedColorUtil.YELLOW)
                ))
                .build(),
            player -> handleWarp(player)
        );
    }
    
    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
    
    @Override
    public void onClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        event.setCancelled(true);
        // GuiItem이 클릭 처리를 담당합니다
    }
    
    /**
     * 섬으로 워프
     */
    private void handleWarp(@NotNull Player player) {
        player.closeInventory();
        player.sendMessage(Component.text("섬으로 이동 중...", UnifiedColorUtil.YELLOW));
        
        // 스폰 위치 가져오기
        var spawn = island.configuration().spawnData().defaultSpawn()
                .toLocation(islandManager.getWorldManager().getIslandWorld());
        spawn.setY(spawn.getY() + 4);
        
        Bukkit.getScheduler().runTask(RPGMain.getInstance(), () -> {
            player.teleport(spawn);
            player.sendMessage(Component.text("섬으로 이동했습니다!", UnifiedColorUtil.SUCCESS));
        });
    }
}