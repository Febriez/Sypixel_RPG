package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 섬 방문자 메뉴 선택 GUI
 * 방문 히스토리와 현재 방문자를 선택할 수 있는 메뉴
 *
 * @author Febrie, CoffeeTory
 */
public class IslandVisitorMenuGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final IslandDTO island;
    
    private IslandVisitorMenuGui(@NotNull Player viewer, @NotNull GuiManager guiManager, @NotNull IslandDTO island) {
        super(viewer, guiManager, 27, Component.translatable("gui.island.visitor.menu.title"));
        this.islandManager = RPGMain.getInstance().getIslandManager();
        this.island = island;
    }
    
    /**
     * IslandVisitorMenuGui 인스턴스를 생성하고 초기화합니다.
     */
    public static IslandVisitorMenuGui create(@NotNull GuiManager guiManager, @NotNull Player viewer, @NotNull IslandDTO island) {
        return new IslandVisitorMenuGui(viewer, guiManager, island);
    }
    
    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("island.gui.visitor.menu.title", Component.text(island.core().islandName()));
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        return IslandMainGui.create(guiManager, viewer);
    }
    
    @Override
    protected void setupLayout() {
        createBorder();
        
        // 섬 정보 아이템
        setItem(4, createIslandInfoItem());
        
        // 방문 히스토리 버튼
        setItem(11, createVisitHistoryItem());
        
        // 현재 방문자 버튼
        setItem(15, createCurrentVisitorsItem());
        
        // 표준 네비게이션 버튼
        setupStandardNavigation(true, true);
    }
    
    /**
     * 섬 정보 아이템 생성
     */
    private GuiItem createIslandInfoItem() {
        // 섬 이름에 설정된 색상 적용
        net.kyori.adventure.text.format.TextColor nameColor = UnifiedColorUtil.parseHexColor(island.configuration().settings().nameColorHex());
        
        return GuiItem.display(
            new ItemBuilder(Material.BOOK)
                .displayName(Component.text(island.core().islandName(), nameColor))
                .lore(List.of(
                    Component.empty(),
                    Component.text("섬장: ", UnifiedColorUtil.GRAY).append(Component.text(island.core().ownerName(), UnifiedColorUtil.WHITE)),
                    Component.text("공개 상태: ", UnifiedColorUtil.GRAY).append(
                        island.core().isPublic() ? 
                        Component.text("공개", UnifiedColorUtil.GREEN) : 
                        Component.text("비공개", UnifiedColorUtil.RED)
                    ),
                    Component.empty(),
                    Component.text("방문자 관련 정보를", UnifiedColorUtil.GRAY),
                    Component.text("확인할 수 있습니다.", UnifiedColorUtil.GRAY)
                ))
                .build()
        );
    }
    
    /**
     * 방문 히스토리 아이템 생성
     */
    private GuiItem createVisitHistoryItem() {
        int totalVisits = island.social().recentVisits().size();
        
        return GuiItem.clickable(
            new ItemBuilder(Material.WRITTEN_BOOK)
                .displayName(Component.text("방문 히스토리", UnifiedColorUtil.YELLOW))
                .lore(List.of(
                    Component.empty(),
                    Component.text("최근 방문자들의", UnifiedColorUtil.GRAY),
                    Component.text("기록을 확인합니다.", UnifiedColorUtil.GRAY),
                    Component.empty(),
                    Component.text("총 방문 기록: ", UnifiedColorUtil.GRAY).append(Component.text(totalVisits + "회", UnifiedColorUtil.YELLOW)),
                    Component.empty(),
                    Component.text("▶ 클릭하여 열기", UnifiedColorUtil.GREEN)
                ))
                .build(),
            player -> {
                player.closeInventory();
                IslandVisitorGui.create(RPGMain.getInstance(), player, island, 1).open(player);
                playClickSound(player);
            }
        );
    }
    
    /**
     * 현재 방문자 아이템 생성
     */
    private GuiItem createCurrentVisitorsItem() {
        // 현재 방문자 수 계산
        var visitListener = RPGMain.getInstance().getIslandVisitListener();
        int currentVisitorCount = visitListener != null ? 
            visitListener.getCurrentVisitors(island.core().islandId()).size() : 0;
        
        return GuiItem.clickable(
            new ItemBuilder(Material.PLAYER_HEAD)
                .displayName(Component.text("현재 방문자", UnifiedColorUtil.AQUA))
                .lore(List.of(
                    Component.empty(),
                    Component.text("현재 섬을 방문 중인", UnifiedColorUtil.GRAY),
                    Component.text("플레이어들을 확인합니다.", UnifiedColorUtil.GRAY),
                    Component.empty(),
                    Component.text("현재 방문자: ", UnifiedColorUtil.GRAY).append(Component.text(currentVisitorCount + "명", UnifiedColorUtil.AQUA)),
                    Component.empty(),
                    Component.text("※ 실시간으로 업데이트됩니다", UnifiedColorUtil.DARK_GRAY),
                    Component.text("※ 하루마다 초기화됩니다", UnifiedColorUtil.DARK_GRAY),
                    Component.empty(),
                    Component.text("▶ 클릭하여 열기", UnifiedColorUtil.GREEN)
                ))
                .build(),
            player -> {
                player.closeInventory();
                IslandVisitorLiveGui.create(guiManager, player, island, 1).open(player);
                playClickSound(player);
            }
        );
    }
}