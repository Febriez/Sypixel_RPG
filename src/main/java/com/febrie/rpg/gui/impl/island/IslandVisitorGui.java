package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.dto.island.IslandVisitDTO;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.UnifiedTimeUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.StandardItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import com.febrie.rpg.util.DateFormatUtil;
/**
 * 섬 방문자 목록 GUI
 * 최근 방문자 기록을 확인
 *
 * @author Febrie, CoffeeTory
 */
public class IslandVisitorGui extends BaseGui {
    
    private final IslandDTO island;
    private final List<IslandVisitDTO> visitors;
    private final int page;
    private final int maxPage;
    private static final int ITEMS_PER_PAGE = 28; // 7x4 grid
    private IslandVisitorGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                           @NotNull RPGMain plugin, @NotNull IslandDTO island, int page) {
        super(viewer, guiManager, 54, Component.translatable("gui.island.visitor.title"));
        this.island = island;
        this.visitors = island.social().recentVisits();
        
        this.maxPage = (int) Math.ceil((double) visitors.size() / ITEMS_PER_PAGE);
        this.page = Math.max(1, Math.min(page, Math.max(maxPage, 1)));
    }
    /**
     * Factory method to create the GUI
     */
    public static IslandVisitorGui create(@NotNull RPGMain plugin, @NotNull Player viewer, 
                                        @NotNull IslandDTO island, int page) {
        return new IslandVisitorGui(viewer, plugin.getGuiManager(), plugin, island, page);
    }
    
    @Override
    protected void setupLayout() {
        // 배경 설정
        fillBorder(Material.WHITE_STAINED_GLASS_PANE);
        // 정보 아이템
        setItem(4, new GuiItem(createInfoItem()));
        // 방문자 목록 표시
        displayVisitors();
        // 페이지 네비게이션
        if (page > 1) {
            setItem(45, new GuiItem(createPreviousPageItem()).onAnyClick(player -> {
                IslandVisitorGui.create(plugin, viewer, island, page - 1).open(viewer);
            }));
        }
        if (page < maxPage) {
            setItem(53, new GuiItem(createNextPageItem()).onAnyClick(player -> {
                IslandVisitorGui.create(plugin, viewer, island, page + 1).open(viewer);
            }));
        }
        // 통계 아이템
        setItem(49, new GuiItem(createStatisticsItem()));
        // 뒤로가기 버튼
        setItem(48, new GuiItem(createBackButton()));
    }
    
    private ItemStack createInfoItem() {
        return ItemBuilder.of(Material.BOOK, viewer.locale())
                .displayNameTranslated("items.island.visitor.info.name")
                .addLore(Component.empty())
                .addLore(LangManager.get("gui.island.visitor.island-name", viewer, Component.text(island.core().islandName())))
                .addLore(LangManager.get("gui.island.visitor.total-visitors", viewer, Component.text(visitors.size())))
                .addLore(LangManager.get("gui.island.visitor.public-status", viewer, 
                        Component.translatable(island.core().isPublic() ? "status.public" : "status.private")))
                .addLoreTranslated("items.island.visitor.info.lore")
                .hideAllFlags()
                .build();
    }
    
    private void displayVisitors() {
        if (visitors.isEmpty()) {
            // 방문자가 없는 경우
            setItem(22, new GuiItem(ItemBuilder.of(Material.BARRIER, viewer.locale())
                    .displayNameTranslated("items.island.visitor.no-visitors.name")
                    .loreTranslated("items.island.visitor.no-visitors.lore")
                    .hideAllFlags()
                    .build()));
            return;
        }
        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, visitors.size());
        int slot = 10; // 시작 슬롯
        for (int i = startIndex; i < endIndex; i++) {
            IslandVisitDTO visit = visitors.get(i);
            setItem(slot, new GuiItem(createVisitorItem(visit, i + 1)));
            
            slot++;
            // 다음 줄로 이동
            if ((slot - 10) % 7 == 0) {
                slot += 2;
            }
            // 최대 슬롯 확인
            if (slot >= 44) break;
        }
    }
    
    private ItemStack createVisitorItem(IslandVisitDTO visit, int index) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(visit.visitorUuid()));
        String playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown";
        // 방문 시간 포맷
        String visitTime = DateFormatUtil.formatSlashDateTimeFromMillis(visit.visitedAt());
        String duration = UnifiedTimeUtil.formatDuration(visit.duration());
        
        ItemStack item = ItemBuilder.of(Material.PLAYER_HEAD, viewer.locale())
                .displayName(Component.text(playerName, UnifiedColorUtil.WHITE))
                .addLore(LangManager.get("gui.island.visitor.visit-time", viewer, Component.text(visitTime)))
                .addLore(LangManager.get("gui.island.visitor.stay-duration", viewer, Component.text(duration)))
                .addLore(LangManager.get("gui.island.visitor.visitor-number", viewer, Component.text(index)))
                .hideAllFlags()
                .build();
        // 플레이어 머리 설정
        if (item.getItemMeta() instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(offlinePlayer);
            item.setItemMeta(skullMeta);
        }
        return item;
    }
    
    
    private ItemStack createStatisticsItem() {
        // 통계 계산
        Map<String, Integer> visitorCount = new HashMap<>();
        Map<String, Long> visitorDuration = new HashMap<>();
        for (IslandVisitDTO visit : visitors) {
            visitorCount.merge(visit.visitorUuid(), 1, Integer::sum);
            visitorDuration.merge(visit.visitorUuid(), visit.duration(), Long::sum);
        }
        // 가장 자주 방문한 플레이어 찾기
        String mostFrequent = null;
        int maxVisits = 0;
        for (Map.Entry<String, Integer> entry : visitorCount.entrySet()) {
            if (entry.getValue() > maxVisits) {
                maxVisits = entry.getValue();
                mostFrequent = entry.getKey();
            }
        }
        // 가장 오래 머문 플레이어 찾기
        String longestStay = null;
        long maxDuration = 0;
        for (Map.Entry<String, Long> entry : visitorDuration.entrySet()) {
            if (entry.getValue() > maxDuration) {
                maxDuration = entry.getValue();
                longestStay = entry.getKey();
            }
        }
        ItemBuilder builder = ItemBuilder.of(Material.WRITABLE_BOOK, viewer.locale())
                .displayNameTranslated("items.island.visitor.statistics.name")
                .addLore(Component.empty())
                .addLore(LangManager.get("gui.island.visitor.total-visits", viewer, Component.text(visitors.size())))
                .addLore(LangManager.get("gui.island.visitor.unique-visitors", viewer, Component.text(visitorCount.size())));
        
        if (mostFrequent != null) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(mostFrequent));
            String name = player.getName() != null ? player.getName() : "Unknown";
            builder.addLore(LangManager.get("gui.island.visitor.most-frequent", viewer, 
                    Component.text(name), Component.text(maxVisits)));
        }
        if (longestStay != null) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(longestStay));
            String name = player.getName() != null ? player.getName() : "Unknown";
            builder.addLore(LangManager.get("gui.island.visitor.longest-stay", viewer,
                    Component.text(name), Component.text(UnifiedTimeUtil.formatDuration(maxDuration))));
        }
        
        return builder.hideAllFlags().build();
    }
    
    private ItemStack createPreviousPageItem() {
        return StandardItemBuilder.previousPageButton(viewer.locale(), page - 1)
                .addLore(LangManager.get("gui.page-info", viewer, 
                        Component.text(page - 1), Component.text(maxPage)))
                .build();
    }
    
    private ItemStack createNextPageItem() {
        return StandardItemBuilder.nextPageButton(viewer.locale(), page + 1)
                .addLore(LangManager.get("gui.page-info", viewer,
                        Component.text(page + 1), Component.text(maxPage)))
                .build();
    }
    
    private ItemStack createBackButton() {
        return StandardItemBuilder.backButton(viewer.locale())
                .build();
    }
    
    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("gui.island.visitor.title");
    }
    
    @Override
    protected com.febrie.rpg.gui.framework.GuiFramework getBackTarget() {
        return IslandMainGui.create(plugin.getGuiManager(), viewer);
    }
    
}
