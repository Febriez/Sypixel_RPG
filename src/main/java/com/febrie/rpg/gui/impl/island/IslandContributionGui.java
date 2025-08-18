package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.dto.island.IslandMemberDTO;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.StandardItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;
/**
 * 섬 기여도 GUI
 * 섬원들의 기여도를 확인하고 관리
 *
 * @author Febrie, CoffeeTory
 */
public class IslandContributionGui extends BaseGui {
    
    private final IslandDTO island;
    private final List<Map.Entry<String, Long>> sortedContributions;
    private final int page;
    private final int maxPage;
    private static final int ITEMS_PER_PAGE = 28; // 7x4 grid
    private IslandContributionGui(@NotNull GuiManager guiManager, @NotNull Player viewer, 
                                  @NotNull IslandDTO island, int page) {
        super(viewer, guiManager, 54, "gui.island.contribution.title");
        this.island = island;
        
        // 기여도를 내림차순으로 정렬
        this.sortedContributions = island.membership().contributions().entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toList());
        this.maxPage = (int) Math.ceil((double) sortedContributions.size() / ITEMS_PER_PAGE);
        this.page = Math.max(1, Math.min(page, maxPage));
    }
    /**
     * Factory method to create and open the contribution GUI
     * @param guiManager The GUI manager
     * @param viewer The player viewing the GUI
     * @param island The island DTO
     * @param page The page number to display
     * @return The initialized GUI instance
     */
    @Contract("_, _, _, _ -> new")
    public static @NotNull IslandContributionGui create(@NotNull GuiManager guiManager, @NotNull Player viewer,
                                                        @NotNull IslandDTO island, int page) {
        return new IslandContributionGui(guiManager, viewer, island, page);
    }
    
    @Override
    public @NotNull Component getTitle() {
        return trans("gui.island.contribution.title").color(UnifiedColorUtil.PRIMARY);
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        return IslandMainGui.create(guiManager, viewer);
    }
    
    @Override
    protected void setupLayout() {
        // 배경 설정
        fillBorder(Material.ORANGE_STAINED_GLASS_PANE);
        // 정보 아이템
        setItem(4, new GuiItem(createInfoItem()));
        // 기여도 목록 표시
        displayContributions();
        // 페이지 네비게이션
        if (page > 1) {
            setItem(45, new GuiItem(createPreviousPageItem()));
        }
        if (page < maxPage) {
            setItem(53, new GuiItem(createNextPageItem()));
        }
        // 기여도 추가 버튼 (섬원만)
        if (isIslandMember()) {
            setItem(49, new GuiItem(createContributeItem()));
        }
        // 뒤로가기 버튼
        setItem(48, new GuiItem(createBackButton()));
    }
    
    private ItemStack createInfoItem() {
        long totalContribution = island.membership().contributions().values().stream()
                .mapToLong(Long::longValue)
                .sum();
        return StandardItemBuilder.guiItem(Material.EMERALD_BLOCK)
                .displayName(trans("gui.island.contribution.info.title").color(UnifiedColorUtil.GOLD))
                .addLore(Component.empty())
                .addLore(trans("gui.island.contribution.info.island-name", "name", island.core().islandName()))
                .addLore(trans("gui.island.contribution.info.total", "amount", String.format("%,d", totalContribution)))
                .addLore(trans("gui.island.contribution.info.contributors", "count", String.valueOf(sortedContributions.size())))
                .addLore(trans("gui.island.contribution.info.description1"))
                .addLore(trans("gui.island.contribution.info.description2"))
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
            
            setItem(slot, new GuiItem(createContributorItem(playerUuid, contribution, i + 1)));
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
        String playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : transString("gui.common.unknown");
        // 순위에 따른 메달 색상
        String rankColor = switch (rank) {
            case 1 -> "&6"; // 금
            case 2 -> "&7"; // 은
            case 3 -> "&c"; // 동
            default -> "&f"; // 기본
        };
        // 역할 확인
        String role = getPlayerRole(playerUuid);
        ItemStack item = StandardItemBuilder.guiItem(Material.PLAYER_HEAD)
                .displayName(UnifiedColorUtil.parseComponent(rankColor + "#" + rank + " &f" + playerName))
                .addLore(trans("gui.island.contribution.contributor.contribution", "amount", String.format("%,d", contribution)))
                .addLore(trans("gui.island.contribution.contributor.role", "role", role))
                .addLore(trans("gui.island.contribution.contributor.percentage", "percent", String.format("%.1f", getContributionPercentage(contribution))))
                .build();
        
        // 플레이어 머리 설정
        if (item.getItemMeta() instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(offlinePlayer);
            item.setItemMeta(skullMeta);
        }
        return item;
    }
    
    private String getPlayerRole(String playerUuid) {
        if (island.core().ownerUuid().equals(playerUuid)) {
            return transString("gui.island.role.owner");
        }
        for (IslandMemberDTO member : island.membership().members()) {
            if (member.uuid().equals(playerUuid)) {
                return member.isCoOwner() ? transString("gui.island.role.co-owner") : transString("gui.island.role.member");
            }
        }
        // 알바생 확인
        if (island.membership().workers().stream().anyMatch(w -> w.uuid().equals(playerUuid))) {
            return transString("gui.island.role.worker");
        }
        return transString("gui.island.role.contributor");
    }
    
    private double getContributionPercentage(long contribution) {
        long total = island.membership().contributions().values().stream()
                .mapToLong(Long::longValue)
                .sum();
        if (total == 0) return 0.0;
        return (contribution * 100.0) / total;
    }
    
    private ItemStack createContributeItem() {
        String playerUuid = viewer.getUniqueId().toString();
        long currentContribution = island.membership().contributions().getOrDefault(playerUuid, 0L);
        return StandardItemBuilder.guiItem(Material.EMERALD)
                .displayName(trans("gui.island.contribution.add.title").color(UnifiedColorUtil.SUCCESS))
                .addLore(trans("gui.island.contribution.add.current", "amount", String.format("%,d", currentContribution)))
                .addLore(trans("gui.island.contribution.add.description1"))
                .addLore(trans("gui.island.contribution.add.description2"))
                .addLore(trans("gui.island.contribution.add.click").color(UnifiedColorUtil.YELLOW))
                .build();
    }
    
    private ItemStack createPreviousPageItem() {
        return StandardItemBuilder.guiItem(Material.ARROW)
                .displayName(trans("gui.common.previous-page").color(UnifiedColorUtil.SUCCESS))
                .addLore(trans("gui.common.page", "current", String.valueOf(page - 1), "max", String.valueOf(maxPage)))
                .build();
    }
    
    private ItemStack createNextPageItem() {
        return StandardItemBuilder.guiItem(Material.ARROW)
                .displayName(trans("gui.common.next-page").color(UnifiedColorUtil.SUCCESS))
                .addLore(trans("gui.common.page", "current", String.valueOf(page + 1), "max", String.valueOf(maxPage)))
                .build();
    }
    
    private ItemStack createBackButton() {
        return StandardItemBuilder.guiItem(Material.ARROW)
                .displayName(trans("gui.common.back").color(UnifiedColorUtil.ERROR))
                .addLore(trans("gui.island.contribution.back-description"))
                .build();
    }
    
    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getSlot();
        switch (slot) {
            case 45 -> { // 이전 페이지
                if (page > 1) {
                    IslandContributionGui.create(guiManager, viewer, island, page - 1).open(viewer);
                }
            }
            case 48 -> { // 뒤로가기
                player.closeInventory();
                IslandMainGui.create(guiManager, viewer).open(viewer);
            }
            case 49 -> { // 기여하기
                if (isIslandMember()) {
                    player.closeInventory();
                    IslandContributeGui.create(RPGMain.getPlugin(), viewer, island).open(viewer);
                }
            }
            case 53 -> { // 다음 페이지
                if (page < maxPage) {
                    IslandContributionGui.create(guiManager, viewer, island, page + 1).open(viewer);
                }
            }
        }
    }
    
    private boolean isIslandMember() {
        String playerUuid = viewer.getUniqueId().toString();
        // 섬장
        if (island.core().ownerUuid().equals(playerUuid)) {
            return true;
        }
        // 멤버
        if (island.membership().members().stream().anyMatch(m -> m.uuid().equals(playerUuid))) {
            return true;
        }
        // 알바
        return island.membership().workers().stream().anyMatch(w -> w.uuid().equals(playerUuid));
    }
}
