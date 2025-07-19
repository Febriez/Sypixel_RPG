package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandVisitDTO;
import com.febrie.rpg.gui.BaseGui;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;

/**
 * 섬 방문자 목록 GUI
 * 최근 방문자 기록을 확인
 *
 * @author Febrie, CoffeeTory
 */
public class IslandVisitorGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final Player viewer;
    private final IslandDTO island;
    private final List<IslandVisitDTO> visitors;
    private final int page;
    private final int maxPage;
    
    private static final int ITEMS_PER_PAGE = 28; // 7x4 grid
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd HH:mm");
    
    private IslandVisitorGui(@NotNull RPGMain plugin, @NotNull Player viewer, 
                           @NotNull IslandDTO island, int page) {
        super(plugin, 54);
        this.islandManager = plugin.getIslandManager();
        this.viewer = viewer;
        this.island = island;
        this.visitors = island.recentVisits();
        
        this.maxPage = (int) Math.ceil((double) visitors.size() / ITEMS_PER_PAGE);
        this.page = Math.max(1, Math.min(page, Math.max(maxPage, 1)));
    }
    
    /**
     * Factory method to create the GUI
     */
    public static IslandVisitorGui create(@NotNull RPGMain plugin, @NotNull Player viewer, 
                                        @NotNull IslandDTO island, int page) {
        IslandVisitorGui gui = new IslandVisitorGui(plugin, viewer, island, page);
        return BaseGui.create(gui, ColorUtil.parseComponent("&f&l방문자 기록"));
    }
    
    @Override
    protected void setupItems() {
        // 배경 설정
        fillBorder(Material.WHITE_STAINED_GLASS_PANE);
        
        // 정보 아이템
        setItem(4, createInfoItem());
        
        // 방문자 목록 표시
        displayVisitors();
        
        // 페이지 네비게이션
        if (page > 1) {
            setItem(45, createPreviousPageItem());
        }
        
        if (page < maxPage) {
            setItem(53, createNextPageItem());
        }
        
        // 통계 아이템
        setItem(49, createStatisticsItem());
        
        // 뒤로가기 버튼
        setItem(48, createBackButton());
    }
    
    private ItemStack createInfoItem() {
        return new ItemBuilder(Material.BOOK)
                .displayName(ColorUtil.parseComponent("&f&l방문자 기록"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7섬 이름: &f" + island.islandName()))
                .addLore(ColorUtil.parseComponent("&7총 방문자: &e" + visitors.size() + "명"))
                .addLore(ColorUtil.parseComponent("&7공개 상태: " + (island.isPublic() ? "&a공개" : "&c비공개")))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7최근 방문자 목록을"))
                .addLore(ColorUtil.parseComponent("&7확인할 수 있습니다"))
                .build();
    }
    
    private void displayVisitors() {
        if (visitors.isEmpty()) {
            // 방문자가 없는 경우
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName(ColorUtil.parseComponent("&c방문 기록이 없습니다"))
                    .addLore(ColorUtil.parseComponent(""))
                    .addLore(ColorUtil.parseComponent("&7아직 섬을 방문한"))
                    .addLore(ColorUtil.parseComponent("&7플레이어가 없습니다"))
                    .build());
            return;
        }
        
        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, visitors.size());
        
        int slot = 10; // 시작 슬롯
        
        for (int i = startIndex; i < endIndex; i++) {
            IslandVisitDTO visit = visitors.get(i);
            setItem(slot, createVisitorItem(visit, i + 1));
            
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
        String playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : "알 수 없음";
        
        // 방문 시간 포맷
        String visitTime = DATE_FORMAT.format(new Date(visit.visitedAt()));
        String duration = formatDuration(visit.duration());
        
        // 최근 방문 강조
        boolean isRecent = System.currentTimeMillis() - visit.visitedAt() < TimeUnit.HOURS.toMillis(24);
        String nameColor = isRecent ? "&a" : "&f";
        
        ItemStack item = new ItemBuilder(Material.PLAYER_HEAD)
                .displayName(ColorUtil.parseComponent(nameColor + playerName))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7방문 시간: &f" + visitTime))
                .addLore(ColorUtil.parseComponent("&7체류 시간: &e" + duration))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7#" + index + " 방문자"))
                .build();
        
        // 플레이어 머리 설정
        if (item.getItemMeta() instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(offlinePlayer);
            item.setItemMeta(skullMeta);
        }
        
        return item;
    }
    
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
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ColorUtil.colorize("&7총 방문 횟수: &e" + visitors.size() + "회"));
        lore.add(ColorUtil.colorize("&7고유 방문자: &e" + visitorCount.size() + "명"));
        lore.add("");
        
        if (mostFrequent != null) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(mostFrequent));
            String name = player.getName() != null ? player.getName() : "알 수 없음";
            lore.add(ColorUtil.colorize("&7최다 방문: &a" + name + " &7(" + maxVisits + "회)"));
        }
        
        if (longestStay != null) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(longestStay));
            String name = player.getName() != null ? player.getName() : "알 수 없음";
            lore.add(ColorUtil.colorize("&7최장 체류: &a" + name + " &7(" + formatDuration(maxDuration) + ")"));
        }
        
        List<Component> componentLore = new ArrayList<>();
        for (String line : lore) {
            componentLore.add(ColorUtil.parseComponent(line));
        }
        
        return new ItemBuilder(Material.WRITABLE_BOOK)
                .displayName(ColorUtil.parseComponent("&6&l방문 통계"))
                .lore(componentLore)
                .build();
    }
    
    private ItemStack createPreviousPageItem() {
        return new ItemBuilder(Material.ARROW)
                .displayName(ColorUtil.parseComponent("&a이전 페이지"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7페이지 " + (page - 1) + "/" + maxPage))
                .build();
    }
    
    private ItemStack createNextPageItem() {
        return new ItemBuilder(Material.ARROW)
                .displayName(ColorUtil.parseComponent("&a다음 페이지"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7페이지 " + (page + 1) + "/" + maxPage))
                .build();
    }
    
    private ItemStack createBackButton() {
        return new ItemBuilder(Material.ARROW)
                .displayName(ColorUtil.parseComponent("&c뒤로가기"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7메인 메뉴로 돌아갉니다"))
                .build();
    }
    
    @Override
    protected void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        int slot = event.getSlot();
        
        switch (slot) {
            case 45 -> { // 이전 페이지
                if (page > 1) {
                    IslandVisitorGui.create(plugin, viewer, island, page - 1).open();
                }
            }
            case 48 -> { // 뒤로가기
                player.closeInventory();
                IslandMainGui.create(plugin.getGuiManager(), plugin.getLangManager(), viewer).open(viewer);
            }
            case 53 -> { // 다음 페이지
                if (page < maxPage) {
                    IslandVisitorGui.create(plugin, viewer, island, page + 1).open();
                }
            }
        }
    }
}