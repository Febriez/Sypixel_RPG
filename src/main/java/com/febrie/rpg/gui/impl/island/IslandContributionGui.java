package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.dto.island.IslandMemberDTO;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.StandardItemBuilder;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
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
        super(viewer, guiManager, 54, Component.translatable("gui.island.contribution.title"));
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
        return Component.translatable("gui.island.contribution.title");
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
        return ItemBuilder.of(Material.EMERALD_BLOCK, getViewerLocale())
                .displayNameTranslated("gui.island.contribution.info.title")
                .addLore(Component.empty())
                .addLore(LangManager.get("gui.island.contribution.info.island-name", viewer, Component.text(island.core().islandName())))
                .addLore(LangManager.get("gui.island.contribution.info.total", viewer, Component.text(String.format("%,d", totalContribution))))
                .addLore(LangManager.get("gui.island.contribution.info.contributors", viewer, Component.text(String.valueOf(sortedContributions.size()))))
                .addLoreTranslated("gui.island.contribution.info.description1")
                .addLoreTranslated("gui.island.contribution.info.description2")
                .hideAllFlags()
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
        String playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown";
        // 순위에 따른 메달 색상
        String rankColor = switch (rank) {
            case 1 -> "&6"; // 금
            case 2 -> "&7"; // 은
            case 3 -> "&c"; // 동
            default -> "&f"; // 기본
        };
        // 역할 확인
        Component role = getPlayerRoleComponent(playerUuid);
        ItemStack item = ItemBuilder.of(Material.PLAYER_HEAD, getViewerLocale())
                .displayName(LangManager.get("gui.island.contribution.contributor.name", viewer, 
                        Component.text("#" + rank), Component.text(playerName)))
                .addLore(LangManager.get("gui.island.contribution.contributor.contribution", viewer, Component.text(String.format("%,d", contribution))))
                .addLore(LangManager.get("gui.island.contribution.contributor.role", viewer, role))
                .addLore(LangManager.get("gui.island.contribution.contributor.percentage", viewer, Component.text(String.format("%.1f", getContributionPercentage(contribution)))))
                .hideAllFlags()
                .build();
        
        // 플레이어 머리 설정
        if (item.getItemMeta() instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(offlinePlayer);
            item.setItemMeta(skullMeta);
        }
        return item;
    }
    
    private Component getPlayerRoleComponent(String playerUuid) {
        if (island.core().ownerUuid().equals(playerUuid)) {
            return Component.translatable("gui.island.role.owner");
        }
        for (IslandMemberDTO member : island.membership().members()) {
            if (member.uuid().equals(playerUuid)) {
                return member.isCoOwner() ? Component.translatable("gui.island.role.co-owner") : Component.translatable("gui.island.role.member");
            }
        }
        // 알바생 확인
        if (island.membership().workers().stream().anyMatch(w -> w.uuid().equals(playerUuid))) {
            return Component.translatable("gui.island.role.worker");
        }
        return Component.translatable("gui.island.role.contributor");
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
        return ItemBuilder.of(Material.EMERALD, getViewerLocale())
                .displayNameTranslated("gui.island.contribution.add.title")
                .addLore(LangManager.get("gui.island.contribution.add.current", viewer, Component.text(String.format("%,d", currentContribution))))
                .addLoreTranslated("gui.island.contribution.add.description1")
                .addLoreTranslated("gui.island.contribution.add.description2")
                .addLoreTranslated("gui.island.contribution.add.click")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createPreviousPageItem() {
        return ItemBuilder.of(Material.ARROW, getViewerLocale())
                .displayNameTranslated("gui.common.previous-page")
                .addLore(LangManager.get("gui.common.page", viewer, Component.text(String.valueOf(page - 1)), Component.text(String.valueOf(maxPage))))
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createNextPageItem() {
        return ItemBuilder.of(Material.ARROW, getViewerLocale())
                .displayNameTranslated("gui.common.next-page")
                .addLore(LangManager.get("gui.common.page", viewer, Component.text(String.valueOf(page + 1)), Component.text(String.valueOf(maxPage))))
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createBackButton() {
        return ItemBuilder.of(Material.ARROW, getViewerLocale())
                .displayNameTranslated("gui.buttons.back.name")
                .addLoreTranslated("gui.island.contribution.back-description")
                .hideAllFlags()
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
