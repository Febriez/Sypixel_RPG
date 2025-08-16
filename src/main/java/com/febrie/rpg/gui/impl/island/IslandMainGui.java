package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.PlayerIslandDataDTO;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.island.permission.IslandPermissionHandler;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
            this.isOwner = island != null && island.ownerUuid().equals(uuid);
            this.isCoOwner = island != null && island.members().stream()
                    .anyMatch(m -> m.uuid().equals(uuid) && m.isCoOwner());
            this.isMember = island != null && island.members().stream()
                    .anyMatch(m -> m.uuid().equals(uuid) && !m.isCoOwner());
            this.isWorker = island != null && island.workers().stream()
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
            return trans("island.gui.main.title-with-name", "name", island.islandName());
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
            new ItemBuilder(Material.GRASS_BLOCK)
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
    }
    
    /**
     * 멤버 메뉴 설정
     */
    private void setupMemberMenu() {
        setItem(13, createIslandInfoItem());
        setItem(22, createUpgradeItem());
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
        net.kyori.adventure.text.format.TextColor nameColor = ColorUtil.parseHexColor(island.settings().nameColorHex());
        
        return GuiItem.display(
            new ItemBuilder(Material.GRASS_BLOCK)
                .displayName(Component.text(island.islandName(), nameColor))
                .lore(List.of(
                    Component.empty(),
                    Component.text("섬장: ", ColorUtil.GRAY).append(Component.text(island.ownerName(), ColorUtil.WHITE)),
                    Component.text("크기: ", ColorUtil.GRAY).append(Component.text(island.size() + " x " + island.size(), ColorUtil.YELLOW)),
                    Component.text("멤버: ", ColorUtil.GRAY).append(Component.text(island.getMemberCount() + "/" + island.upgradeData().memberLimit(), ColorUtil.GREEN)),
                    Component.text("알바: ", ColorUtil.GRAY).append(Component.text(island.workers().size() + "/" + island.upgradeData().workerLimit(), ColorUtil.GREEN)),
                    Component.empty(),
                    Component.text("생성일: ", ColorUtil.GRAY).append(Component.text(DateFormatUtil.formatFullDateTimeFromMillis(island.createdAt()), ColorUtil.WHITE)),
                    Component.text("공개 여부: ", ColorUtil.GRAY).append(
                        island.isPublic() ? 
                        Component.text("공개", ColorUtil.GREEN) : 
                        Component.text("비공개", ColorUtil.RED)
                    ),
                    Component.empty(),
                    Component.text("내 역할: ", ColorUtil.GRAY).append(
                        isOwner ? Component.text("섬장", ColorUtil.GOLD) :
                        isCoOwner ? Component.text("부섬장", ColorUtil.YELLOW) :
                        isMember ? Component.text("멤버", ColorUtil.GREEN) :
                        isWorker ? Component.text("알바", ColorUtil.AQUA) :
                        Component.text("방문자", ColorUtil.GRAY)
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
            new ItemBuilder(Material.PLAYER_HEAD)
                .displayName(Component.text("멤버 관리", ColorUtil.GREEN))
                .lore(List.of(
                    Component.empty(),
                    Component.text("섬원을 초대하거나", ColorUtil.GRAY),
                    Component.text("추방할 수 있습니다.", ColorUtil.GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 열기", ColorUtil.YELLOW)
                ))
                .build(),
            player -> {
                player.closeInventory();
                IslandMemberGui.create(RPGMain.getInstance(), player, island).open();
                playClickSound(player);
            }
        );
    }
    
    /**
     * 권한 관리 아이템
     */
    private GuiItem createPermissionManagementItem() {
        return GuiItem.clickable(
            new ItemBuilder(Material.COMMAND_BLOCK)
                .displayName(Component.text("권한 관리", ColorUtil.RED))
                .lore(List.of(
                    Component.empty(),
                    Component.text("각 역할의 권한을", ColorUtil.GRAY),
                    Component.text("설정할 수 있습니다.", ColorUtil.GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 열기", ColorUtil.YELLOW)
                ))
                .build(),
            player -> {
                player.closeInventory();
                IslandPermissionGui.create(RPGMain.getInstance(), RPGMain.getInstance().getIslandManager(), island, player).open();
                playClickSound(player);
            }
        );
    }
    
    /**
     * 업그레이드 아이템
     */
    private GuiItem createUpgradeItem() {
        return GuiItem.clickable(
            new ItemBuilder(Material.ANVIL)
                .displayName(Component.text("업그레이드", ColorUtil.GOLD))
                .lore(List.of(
                    Component.empty(),
                    Component.text("현재 레벨:", ColorUtil.GRAY),
                    Component.text("  크기: ", ColorUtil.WHITE).append(Component.text(island.upgradeData().sizeLevel() + " 레벨", ColorUtil.YELLOW)),
                    Component.text("  멤버: ", ColorUtil.WHITE).append(Component.text(island.upgradeData().memberLimitLevel() + " 레벨", ColorUtil.YELLOW)),
                    Component.text("  알바: ", ColorUtil.WHITE).append(Component.text(island.upgradeData().workerLimitLevel() + " 레벨", ColorUtil.YELLOW)),
                    Component.empty(),
                    Component.text("▶ 클릭하여 열기", ColorUtil.YELLOW)
                ))
                .build(),
            player -> {
                player.closeInventory();
                IslandUpgradeGui.create(RPGMain.getInstance(), player, island).open();
                playClickSound(player);
            }
        );
    }
    
    /**
     * 기여도 아이템
     */
    private GuiItem createContributionItem() {
        long myContribution = island.contributions().getOrDefault(viewer.getUniqueId().toString(), 0L);
        return GuiItem.clickable(
            new ItemBuilder(Material.EMERALD)
                .displayName(Component.text("기여도", ColorUtil.GREEN))
                .lore(List.of(
                    Component.empty(),
                    Component.text("섬원들의 기여도를", ColorUtil.GRAY),
                    Component.text("확인할 수 있습니다.", ColorUtil.GRAY),
                    Component.empty(),
                    Component.text("내 기여도: ", ColorUtil.GRAY).append(Component.text(myContribution, ColorUtil.YELLOW)),
                    Component.empty(),
                    Component.text("▶ 클릭하여 열기", ColorUtil.YELLOW)
                ))
                .build(),
            player -> {
                IslandContributionGui.create(RPGMain.getInstance(), player, island, 1).open(player);
                playClickSound(player);
            }
        );
    }
    
    /**
     * 스폰 설정 아이템
     */
    private GuiItem createSpawnSettingsItem() {
        return GuiItem.clickable(
            new ItemBuilder(Material.ENDER_PEARL)
                .displayName(Component.text("스폰 설정", ColorUtil.LIGHT_PURPLE))
                .lore(List.of(
                    Component.empty(),
                    Component.text("섬의 스폰 위치를", ColorUtil.GRAY),
                    Component.text("관리할 수 있습니다.", ColorUtil.GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 열기", ColorUtil.YELLOW)
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
            new ItemBuilder(Material.COMPARATOR)
                .displayName(Component.text("섬 설정", ColorUtil.BLUE))
                .lore(List.of(
                    Component.empty(),
                    Component.text("섬 이름 변경", ColorUtil.GRAY),
                    Component.text("공개/비공개 설정", ColorUtil.GRAY),
                    Component.text("바이옴 변경", ColorUtil.GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 열기", ColorUtil.YELLOW)
                ))
                .build(),
            player -> {
                player.closeInventory();
                IslandSettingsGui.create(RPGMain.getInstance(), player, island).open();
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
            visitListener.getCurrentVisitors(island.islandId()).size() : 0;
        
        return GuiItem.clickable(
            new ItemBuilder(Material.BOOK)
                .displayName(Component.text("방문자 관리", ColorUtil.WHITE))
                .lore(List.of(
                    Component.empty(),
                    Component.text("방문자 관련 정보를", ColorUtil.GRAY),
                    Component.text("확인할 수 있습니다.", ColorUtil.GRAY),
                    Component.empty(),
                    Component.text("최근 방문자: ", ColorUtil.GRAY).append(Component.text(island.recentVisits().size() + "명", ColorUtil.YELLOW)),
                    Component.text("현재 방문자: ", ColorUtil.GRAY).append(Component.text(currentVisitorCount + "명", ColorUtil.AQUA)),
                    Component.empty(),
                    Component.text("• 방문 히스토리", ColorUtil.DARK_GRAY),
                    Component.text("• 현재 방문자 (실시간)", ColorUtil.DARK_GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 메뉴 열기", ColorUtil.GREEN)
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
     * 워프 아이템
     */
    private GuiItem createWarpItem() {
        return GuiItem.clickable(
            new ItemBuilder(Material.COMPASS)
                .displayName(Component.text("섬으로 이동", ColorUtil.AQUA))
                .lore(List.of(
                    Component.empty(),
                    Component.text("섬으로 순간이동합니다.", ColorUtil.GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 이동", ColorUtil.YELLOW)
                ))
                .build(),
            player -> handleWarp(player)
        );
    }
    
    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
    
    /**
     * 섬으로 워프
     */
    private void handleWarp(@NotNull Player player) {
        player.closeInventory();
        player.sendMessage(Component.text("섬으로 이동 중...", ColorUtil.YELLOW));
        
        // 스폰 위치 가져오기
        var spawn = island.spawnData().defaultSpawn()
                .toLocation(islandManager.getWorldManager().getIslandWorld());
        spawn.setY(spawn.getY() + 4);
        
        Bukkit.getScheduler().runTask(RPGMain.getInstance(), () -> {
            player.teleport(spawn);
            player.sendMessage(Component.text("섬으로 이동했습니다!", ColorUtil.SUCCESS));
        });
    }
}