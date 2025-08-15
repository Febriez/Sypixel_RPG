package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandMemberDTO;
import com.febrie.rpg.gui.GuiHolder;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * 섬 기여도 GUI
 * 섬원들의 기여도를 확인하고 관리
 *
 * @author Febrie, CoffeeTory
 */
public class IslandContributionGui extends GuiHolder {
    
    private final IslandManager islandManager;
    private final Player viewer;
    private final IslandDTO island;
    private final List<Map.Entry<String, Long>> sortedContributions;
    private final int page;
    private final int maxPage;
    
    private static final int ITEMS_PER_PAGE = 28; // 7x4 grid
    
    private IslandContributionGui(@NotNull RPGMain plugin, @NotNull Player viewer, 
                                  @NotNull IslandDTO island, int page) {
        super(plugin, 54);
        this.islandManager = plugin.getIslandManager();
        this.viewer = viewer;
        this.island = island;
        
        // 기여도를 내림차순으로 정렬
        this.sortedContributions = island.contributions().entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toList());
        
        this.maxPage = (int) Math.ceil((double) sortedContributions.size() / ITEMS_PER_PAGE);
        this.page = Math.max(1, Math.min(page, maxPage));
    }
    
    /**
     * Factory method to create and open the contribution GUI
     * @param plugin The plugin instance
     * @param viewer The player viewing the GUI
     * @param island The island DTO
     * @param page The page number to display
     * @return The initialized GUI instance
     */
    public static IslandContributionGui create(@NotNull RPGMain plugin, @NotNull Player viewer, 
                                               @NotNull IslandDTO island, int page) {
        IslandContributionGui gui = new IslandContributionGui(plugin, viewer, island, page);
        return GuiHolder.create(gui, "&6&l기여도 순위");
    }
    
    @Override
    protected void setupItems() {
        // 배경 설정
        fillBorder(Material.ORANGE_STAINED_GLASS_PANE);
        
        // 정보 아이템
        setItem(4, createInfoItem());
        
        // 기여도 목록 표시
        displayContributions();
        
        // 페이지 네비게이션
        if (page > 1) {
            setItem(45, createPreviousPageItem());
        }
        
        if (page < maxPage) {
            setItem(53, createNextPageItem());
        }
        
        // 기여도 추가 버튼 (섬원만)
        if (isIslandMember()) {
            setItem(49, createContributeItem());
        }
        
        // 뒤로가기 버튼
        setItem(48, createBackButton());
    }
    
    private ItemStack createInfoItem() {
        long totalContribution = island.contributions().values().stream()
                .mapToLong(Long::longValue)
                .sum();
        
        return new ItemBuilder(Material.EMERALD_BLOCK)
                .displayName(ColorUtil.parseComponent("&6&l섬 기여도 정보"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7섬 이름: &f" + island.islandName()))
                .addLore(ColorUtil.parseComponent("&7총 기여도: &a" + String.format("%,d", totalContribution)))
                .addLore(ColorUtil.parseComponent("&7기여자 수: &e" + sortedContributions.size() + "명"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7기여도는 섬 업그레이드에"))
                .addLore(ColorUtil.parseComponent("&7사용됩니다"))
                .build();
    }
    
    private void displayContributions() {
        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, sortedContributions.size());
        
        int slot = 10; // 시작 슬롯
        
        for (int i = startIndex; i < endIndex; i++) {
            Map.Entry<String, Long> entry = sortedContributions.get(i);
            String playerUuid = entry.getKey();
            long contribution = entry.getValue();
            
            setItem(slot, createContributorItem(playerUuid, contribution, i + 1));
            
            slot++;
            // 다음 줄로 이동
            if ((slot - 10) % 7 == 0) {
                slot += 2;
            }
            
            // 최대 슬롯 확인
            if (slot >= 44) break;
        }
    }
    
    private ItemStack createContributorItem(String playerUuid, long contribution, int rank) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerUuid));
        String playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : "알 수 없음";
        
        // 순위에 따른 메달 색상
        String rankColor = switch (rank) {
            case 1 -> "&6"; // 금
            case 2 -> "&7"; // 은
            case 3 -> "&c"; // 동
            default -> "&f"; // 기본
        };
        
        // 역할 확인
        String role = getPlayerRole(playerUuid);
        
        ItemStack item = new ItemBuilder(Material.PLAYER_HEAD)
                .displayName(ColorUtil.parseComponent(rankColor + "#" + rank + " &f" + playerName))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7기여도: &a" + String.format("%,d", contribution)))
                .addLore(ColorUtil.parseComponent("&7역할: " + role))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7전체 기여도의 &e" + 
                        String.format("%.1f%%", getContributionPercentage(contribution))))
                .build();
        
        // 플레이어 머리 설정
        if (item.getItemMeta() instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(offlinePlayer);
            item.setItemMeta(skullMeta);
        }
        
        return item;
    }
    
    private String getPlayerRole(String playerUuid) {
        if (island.ownerUuid().equals(playerUuid)) {
            return "&c섬장";
        }
        
        for (IslandMemberDTO member : island.members()) {
            if (member.uuid().equals(playerUuid)) {
                return member.isCoOwner() ? "&6부섬장" : "&a멤버";
            }
        }
        
        // 알바생 확인
        if (island.workers().stream().anyMatch(w -> w.uuid().equals(playerUuid))) {
            return "&e알바";
        }
        
        return "&7기여자";
    }
    
    private double getContributionPercentage(long contribution) {
        long total = island.contributions().values().stream()
                .mapToLong(Long::longValue)
                .sum();
        
        if (total == 0) return 0.0;
        return (contribution * 100.0) / total;
    }
    
    private ItemStack createContributeItem() {
        String playerUuid = viewer.getUniqueId().toString();
        long currentContribution = island.contributions().getOrDefault(playerUuid, 0L);
        
        return new ItemBuilder(Material.EMERALD)
                .displayName(ColorUtil.parseComponent("&a&l기여도 추가"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7현재 내 기여도: &a" + String.format("%,d", currentContribution)))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7아이템이나 재화를 기여하여"))
                .addLore(ColorUtil.parseComponent("&7섬 발전에 도움을 줄 수 있습니다"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&e▶ 클릭하여 기여하기"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&c※ 현재 준비 중입니다"))
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
                .addLore(ColorUtil.parseComponent("&7메인 메뉴로 돌아갑니다"))
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
                    IslandContributionGui.create(plugin, viewer, island, page - 1).open();
                }
            }
            case 48 -> { // 뒤로가기
                player.closeInventory();
                IslandMainGui.create(plugin.getGuiManager(), viewer).open(viewer);
            }
            case 49 -> { // 기여하기
                if (isIslandMember()) {
                    player.closeInventory();
                    IslandContributeGui.create(plugin, viewer, island).open(viewer);
                }
            }
            case 53 -> { // 다음 페이지
                if (page < maxPage) {
                    IslandContributionGui.create(plugin, viewer, island, page + 1).open();
                }
            }
        }
    }
    
    private boolean isIslandMember() {
        String playerUuid = viewer.getUniqueId().toString();
        
        // 섬장
        if (island.ownerUuid().equals(playerUuid)) {
            return true;
        }
        
        // 멤버
        if (island.members().stream().anyMatch(m -> m.uuid().equals(playerUuid))) {
            return true;
        }
        
        // 알바
        return island.workers().stream().anyMatch(w -> w.uuid().equals(playerUuid));
    }
}