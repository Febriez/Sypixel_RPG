package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.listener.IslandVisitListener;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 섬 현재 방문자 실시간 GUI
 * 현재 방문 중인 플레이어들의 실시간 정보를 표시
 *
 * @author Febrie, CoffeeTory
 */
public class IslandVisitorLiveGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final IslandDTO island;
    private final List<IslandVisitListener.CurrentVisitorInfo> currentVisitors;
    private final int page;
    private final int maxPage;
    
    private static final int ITEMS_PER_PAGE = 28; // 7x4 grid
    
    private IslandVisitorLiveGui(@NotNull Player viewer, @NotNull GuiManager guiManager, 
                                @NotNull IslandDTO island, int page) {
        super(viewer, guiManager, 54, "gui.island.visitor.live.title");
        this.islandManager = RPGMain.getInstance().getIslandManager();
        this.island = island;
        
        // 현재 방문자 목록 가져오기
        var visitListener = RPGMain.getInstance().getIslandVisitListener();
        this.currentVisitors = visitListener != null ? 
            visitListener.getCurrentVisitors(island.core().islandId()) : List.of();
        
        this.maxPage = (int) Math.ceil((double) currentVisitors.size() / ITEMS_PER_PAGE);
        this.page = Math.max(1, Math.min(page, Math.max(maxPage, 1)));
    }
    
    /**
     * IslandVisitorLiveGui 인스턴스를 생성하고 초기화합니다.
     */
    public static IslandVisitorLiveGui create(@NotNull GuiManager guiManager, @NotNull Player viewer, 
                                            @NotNull IslandDTO island, int page) {
        IslandVisitorLiveGui gui = new IslandVisitorLiveGui(viewer, guiManager, island, page);
        return gui;
    }
    
    @Override
    public @NotNull Component getTitle() {
        return trans("island.gui.visitor.live.title", "name", island.core().islandName());
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        return IslandVisitorMenuGui.create(guiManager, viewer, island);
    }
    
    @Override
    protected void setupLayout() {
        createBorder();
        
        // 정보 아이템
        setItem(4, createInfoItem());
        
        // 현재 방문자 목록 표시
        displayCurrentVisitors();
        
        // 페이지 네비게이션
        if (page > 1) {
            setItem(45, createPreviousPageItem());
        }
        
        if (page < maxPage) {
            setItem(53, createNextPageItem());
        }
        // 새로고침 버튼
        setItem(49, createRefreshItem());
        
        // 표준 네비게이션
        setupStandardNavigation(true, true);
    }
    
    /**
     * 정보 아이템 생성
     */
    private GuiItem createInfoItem() {
        return GuiItem.display(
            new ItemBuilder(Material.COMPASS)
                .displayName(Component.text("현재 방문자", UnifiedColorUtil.AQUA))
                .lore(List.of(
                    Component.empty(),
                    Component.text("섬 이름: ", UnifiedColorUtil.GRAY).append(Component.text(island.core().islandName(), UnifiedColorUtil.WHITE)),
                    Component.text("현재 방문자: ", UnifiedColorUtil.GRAY).append(Component.text(currentVisitors.size() + "명", UnifiedColorUtil.AQUA)),
                    Component.empty(),
                    Component.text("실시간으로 업데이트되는", UnifiedColorUtil.GRAY),
                    Component.text("방문자 목록입니다.", UnifiedColorUtil.GRAY),
                    Component.empty(),
                    Component.text("※ 방문자를 클릭하면", UnifiedColorUtil.DARK_GRAY),
                    Component.text("   액션 메뉴가 열립니다", UnifiedColorUtil.DARK_GRAY),
                    Component.empty(),
                    Component.text("※ 하루마다 초기화됩니다", UnifiedColorUtil.DARK_GRAY)
                ))
                .build()
        );
    }
    
    /**
     * 현재 방문자 목록 표시
     */
    private void displayCurrentVisitors() {
        if (currentVisitors.isEmpty()) {
            // 방문자가 없는 경우
            setItem(22, GuiItem.display(
                new ItemBuilder(Material.BARRIER)
                    .displayName(Component.text("현재 방문자가 없습니다", UnifiedColorUtil.RED))
                    .lore(List.of(
                        Component.empty(),
                        Component.text("현재 섬을 방문 중인", UnifiedColorUtil.GRAY),
                        Component.text("플레이어가 없습니다.", UnifiedColorUtil.GRAY),
                        Component.empty(),
                        Component.text("새로고침 버튼을 눌러", UnifiedColorUtil.GRAY),
                        Component.text("최신 상태로 업데이트하세요.", UnifiedColorUtil.GRAY)
                    ))
                    .build()
            ));
            return;
        }
        
        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, currentVisitors.size());
        
        int slot = 10; // 시작 슬롯
        
        for (int i = startIndex; i < endIndex; i++) {
            IslandVisitListener.CurrentVisitorInfo visitor = currentVisitors.get(i);
            setItem(slot, createVisitorItem(visitor, i + 1));
            
            slot++;
            // 다음 줄로 이동
            if ((slot - 10) % 7 == 0) {
                slot += 2;
            }
            
            // 최대 슬롯 확인
            if (slot >= 44) break;
        }
    }
    
    /**
     * 방문자 아이템 생성
     */
    private GuiItem createVisitorItem(IslandVisitListener.CurrentVisitorInfo visitor, int index) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(visitor.getPlayerUuid()));
        String playerName = visitor.getPlayerName();
        
        // 경과 시간 포맷
        String duration = formatDuration(visitor.getCurrentDuration());
        
        // 방문 시간에 따른 색상 결정
        long minutes = visitor.getCurrentDuration() / (60 * 1000);
        net.kyori.adventure.text.format.TextColor nameColor;
        if (minutes < 5) {
            nameColor = UnifiedColorUtil.GREEN; // 5분 미만 - 새로운 방문자
        } else if (minutes < 30) {
            nameColor = UnifiedColorUtil.YELLOW; // 30분 미만 - 일반 방문자
        } else {
            nameColor = UnifiedColorUtil.GOLD; // 30분 이상 - 장기 방문자
        }
        
        ItemStack item = new ItemBuilder(Material.PLAYER_HEAD)
                .displayName(Component.text(playerName, nameColor))
                .lore(List.of(
                    Component.empty(),
                    Component.text("방문 시작: ", UnifiedColorUtil.GRAY).append(
                        Component.text(formatTimestamp(visitor.getVisitStartTime()), UnifiedColorUtil.WHITE)
                    ),
                    Component.text("경과 시간: ", UnifiedColorUtil.GRAY).append(Component.text(duration, UnifiedColorUtil.YELLOW)),
                    Component.empty(),
                    Component.text("상태: ", UnifiedColorUtil.GRAY).append(
                        Component.text("온라인", UnifiedColorUtil.GREEN)
                    ),
                    Component.empty(),
                    Component.text("#" + index + " 현재 방문자", UnifiedColorUtil.DARK_GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 액션 메뉴 열기", UnifiedColorUtil.GREEN)
                ))
                .build();
        
        // 플레이어 머리 설정
        if (item.getItemMeta() instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(offlinePlayer);
            item.setItemMeta(skullMeta);
        }
        
        return GuiItem.clickable(item, player -> {
            player.closeInventory();
            IslandVisitorActionGui.create(guiManager, player, island, visitor).open(player);
            playClickSound(player);
        });
    }
    
    /**
     * 경과 시간 포맷 (밀리초 -> 시:분:초)
     */
    private String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        if (seconds < 60) {
            return seconds + "초";
        }
        
        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + "분 " + (seconds % 60) + "초";
        }
        
        long hours = minutes / 60;
        return hours + "시간 " + (minutes % 60) + "분";
    }
    
    /**
     * 타임스탬프 포맷 (밀리초 -> HH:mm:ss)
     */
    private String formatTimestamp(long timestamp) {
        java.time.LocalDateTime dateTime = 
            java.time.LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(timestamp), 
                java.time.ZoneId.systemDefault()
            );
        java.time.format.DateTimeFormatter formatter = 
            java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");
        return dateTime.format(formatter);
    }
    
    /**
     * 새로고침 아이템 생성
     */
    private GuiItem createRefreshItem() {
        return GuiItem.clickable(
            new ItemBuilder(Material.RECOVERY_COMPASS)
                .displayName(Component.text("새로고침", UnifiedColorUtil.GREEN))
                .lore(List.of(
                    Component.empty(),
                    Component.text("현재 방문자 목록을", UnifiedColorUtil.GRAY),
                    Component.text("최신 상태로 업데이트합니다.", UnifiedColorUtil.GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 새로고침", UnifiedColorUtil.YELLOW)
                ))
                .build(),
            player -> {
                // 새로고침: 현재 페이지로 다시 열기
                IslandVisitorLiveGui.create(guiManager, player, island, page).open(player);
                playClickSound(player);
            }
        );
    }
    
    /**
     * 이전 페이지 아이템 생성
     */
    private GuiItem createPreviousPageItem() {
        return GuiItem.clickable(
            new ItemBuilder(Material.ARROW)
                .displayName(Component.text("이전 페이지", UnifiedColorUtil.GREEN))
                .lore(List.of(
                    Component.empty(),
                    Component.text("페이지 " + (page - 1) + "/" + maxPage, UnifiedColorUtil.GRAY)
                ))
                .build(),
            player -> {
                if (page > 1) {
                    IslandVisitorLiveGui.create(guiManager, player, island, page - 1).open(player);
                    playClickSound(player);
                }
            }
        );
    }
    
    /**
     * 다음 페이지 아이템 생성
     */
    private GuiItem createNextPageItem() {
        return GuiItem.clickable(
            new ItemBuilder(Material.ARROW)
                .displayName(Component.text("다음 페이지", UnifiedColorUtil.GREEN))
                .lore(List.of(
                    Component.empty(),
                    Component.text("페이지 " + (page + 1) + "/" + maxPage, UnifiedColorUtil.GRAY)
                ))
                .build(),
            player -> {
                if (page < maxPage) {
                    IslandVisitorLiveGui.create(guiManager, player, island, page + 1).open(player);
                    playClickSound(player);
                }
            }
        );
    }
}