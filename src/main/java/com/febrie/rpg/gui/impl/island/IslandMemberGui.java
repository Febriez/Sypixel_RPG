package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandMemberDTO;
import com.febrie.rpg.dto.island.IslandRole;
import com.febrie.rpg.dto.island.IslandWorkerDTO;
import com.febrie.rpg.gui.BaseGui;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.island.permission.IslandPermissionHandler;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LegacyItemBuilder;
import com.febrie.rpg.util.SkullUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 섬 멤버 관리 GUI
 *
 * @author Febrie, CoffeeTory
 */
public class IslandMemberGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final IslandDTO island;
    private final Player viewer;
    private int currentPage = 0;
    private final int ITEMS_PER_PAGE = 28; // 7x4 grid
    
    public IslandMemberGui(@NotNull RPGMain plugin, @NotNull IslandManager islandManager,
                          @NotNull IslandDTO island, @NotNull Player viewer) {
        super(plugin, 54, ColorUtil.colorize("&b섬원 관리 - " + island.islandName()));
        this.islandManager = islandManager;
        this.island = island;
        this.viewer = viewer;
    }
    
    @Override
    protected void setupItems() {
        // 배경 설정
        fillBorder(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        
        // 섬장 표시 (항상 첫번째)
        setItem(10, createOwnerItem());
        
        // 멤버 목록 표시
        displayMembers();
        
        // 페이지 네비게이션
        if (currentPage > 0) {
            setItem(45, createPreviousPageButton());
        }
        
        if (hasNextPage()) {
            setItem(53, createNextPageButton());
        }
        
        // 멤버 초대 버튼 (권한이 있는 경우)
        if (IslandPermissionHandler.hasPermission(island, viewer, "INVITE_MEMBERS")) {
            setItem(49, createInviteButton());
        }
        
        // 뒤로 가기 버튼
        setItem(48, createBackButton());
        
        // 닫기 버튼
        setItem(50, createCloseButton());
    }
    
    /**
     * 멤버 목록 표시
     */
    private void displayMembers() {
        List<ItemStack> memberItems = new ArrayList<>();
        
        // 섬원들
        for (IslandMemberDTO member : island.members()) {
            memberItems.add(createMemberItem(member));
        }
        
        // 알바들
        for (IslandWorkerDTO worker : island.workers()) {
            memberItems.add(createWorkerItem(worker));
        }
        
        // 페이지에 맞게 표시
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, memberItems.size());
        
        int[] slots = {
            11, 12, 13, 14, 15, 16, 17,
            20, 21, 22, 23, 24, 25, 26,
            29, 30, 31, 32, 33, 34, 35,
            38, 39, 40, 41, 42, 43, 44
        };
        
        for (int i = startIndex; i < endIndex; i++) {
            int slotIndex = i - startIndex;
            if (slotIndex < slots.length) {
                setItem(slots[slotIndex], memberItems.get(i));
            }
        }
    }
    
    /**
     * 섬장 아이템 생성
     */
    private ItemStack createOwnerItem() {
        return new LegacyItemBuilder(SkullUtil.getPlayerHead(island.ownerUuid()))
                .setDisplayName(ColorUtil.colorize("&6&l" + island.ownerName()))
                .addLore("")
                .addLore(ColorUtil.colorize("&7역할: &e섬장"))
                .addLore(ColorUtil.colorize("&7UUID: &f" + island.ownerUuid().substring(0, 8) + "..."))
                .addLore("")
                .addLore(ColorUtil.colorize("&a✓ 모든 권한 보유"))
                .addLore("")
                .addLore(ColorUtil.colorize("&c섬장은 변경할 수 없습니다."))
                .build();
    }
    
    /**
     * 멤버 아이템 생성
     */
    private ItemStack createMemberItem(@NotNull IslandMemberDTO member) {
        LegacyItemBuilder builder = new LegacyItemBuilder(SkullUtil.getPlayerHead(member.uuid()))
                .setDisplayName(ColorUtil.colorize("&b" + member.name()))
                .addLore("")
                .addLore(ColorUtil.colorize("&7역할: &f" + IslandPermissionHandler.getRoleDisplayName(member.isCoOwner() ? IslandRole.CO_OWNER : IslandRole.MEMBER)))
                .addLore(ColorUtil.colorize("&7가입일: &f" + formatDate(member.joinedAt())))
                .addLore("");
        
        // 권한 표시
        if (member.isCoOwner()) {
            builder.addLore(ColorUtil.colorize("&a✓ 대부분의 권한 보유"));
        } else {
            builder.addLore(ColorUtil.colorize("&7일반 섬원 권한"));
        }
        
        // 관리 옵션 (권한이 있는 경우)
        if (canManageMember(member)) {
            builder.addLore("")
                   .addLore(ColorUtil.colorize("&e▶ 좌클릭: 역할 변경"))
                   .addLore(ColorUtil.colorize("&c▶ 우클릭: 추방"));
        }
        
        return builder.build();
    }
    
    /**
     * 알바 아이템 생성
     */
    private ItemStack createWorkerItem(@NotNull IslandWorkerDTO worker) {
        LegacyItemBuilder builder = new LegacyItemBuilder(SkullUtil.getPlayerHead(worker.uuid()))
                .setDisplayName(ColorUtil.colorize("&7" + worker.name()))
                .addLore("")
                .addLore(ColorUtil.colorize("&7역할: &f알바"))
                .addLore(ColorUtil.colorize("&7고용일: &f" + formatDate(worker.hiredAt())))
                .addLore(ColorUtil.colorize("&7마지막 활동: &f" + formatDate(worker.lastActivity())))
                .addLore("")
                .addLore(ColorUtil.colorize("&7제한된 권한"));
        
        // 관리 옵션 (권한이 있는 경우)
        if (IslandPermissionHandler.hasPermission(island, viewer, "MANAGE_WORKERS")) {
            builder.addLore("")
                   .addLore(ColorUtil.colorize("&e▶ 좌클릭: 기간 연장"))
                   .addLore(ColorUtil.colorize("&c▶ 우클릭: 해고"));
        }
        
        return builder.build();
    }
    
    /**
     * 초대 버튼
     */
    private ItemStack createInviteButton() {
        return new LegacyItemBuilder(Material.EMERALD)
                .setDisplayName(ColorUtil.colorize("&a새 멤버 초대"))
                .addLore("")
                .addLore(ColorUtil.colorize("&7새로운 섬원을 초대합니다."))
                .addLore("")
                .addLore(ColorUtil.colorize("&7현재 멤버: &e" + (1 + island.members().size()) + "/" + 
                        (1 + island.upgradeData().memberLimit())))
                .addLore(ColorUtil.colorize("&7알바: &e" + island.workers().size() + "/" + 
                        island.upgradeData().workerLimit()))
                .addLore("")
                .addLore(ColorUtil.colorize("&e▶ 클릭하여 초대"))
                .build();
    }
    
    /**
     * 뒤로 가기 버튼
     */
    private ItemStack createBackButton() {
        return new LegacyItemBuilder(Material.ARROW)
                .setDisplayName(ColorUtil.colorize("&f뒤로 가기"))
                .addLore("")
                .addLore(ColorUtil.colorize("&7메인 메뉴로 돌아갑니다."))
                .build();
    }
    
    /**
     * 닫기 버튼
     */
    private ItemStack createCloseButton() {
        return new LegacyItemBuilder(Material.BARRIER)
                .setDisplayName(ColorUtil.colorize("&c닫기"))
                .addLore("")
                .addLore(ColorUtil.colorize("&7메뉴를 닫습니다."))
                .build();
    }
    
    /**
     * 이전 페이지 버튼
     */
    private ItemStack createPreviousPageButton() {
        return new LegacyItemBuilder(Material.ARROW)
                .setDisplayName(ColorUtil.colorize("&f이전 페이지"))
                .addLore("")
                .addLore(ColorUtil.colorize("&7현재 페이지: &e" + (currentPage + 1)))
                .build();
    }
    
    /**
     * 다음 페이지 버튼
     */
    private ItemStack createNextPageButton() {
        return new LegacyItemBuilder(Material.ARROW)
                .setDisplayName(ColorUtil.colorize("&f다음 페이지"))
                .addLore("")
                .addLore(ColorUtil.colorize("&7현재 페이지: &e" + (currentPage + 1)))
                .build();
    }
    
    @Override
    protected void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        int slot = event.getSlot();
        
        switch (slot) {
            case 45 -> { // 이전 페이지
                if (currentPage > 0) {
                    currentPage--;
                    refresh();
                }
            }
            case 48 -> { // 뒤로 가기
                new IslandMainGui(plugin.getGuiManager(), plugin.getLangManager(), viewer).open(viewer);
            }
            case 49 -> { // 초대
                if (IslandPermissionHandler.hasPermission(island, viewer, "INVITE_MEMBERS")) {
                    player.closeInventory();
                    player.sendMessage(ColorUtil.colorize("&a초대할 플레이어의 이름을 입력하세요:"));
                    player.sendMessage(ColorUtil.colorize("&7예시: /섬 초대 <플레이어명>"));
                }
            }
            case 50 -> { // 닫기
                player.closeInventory();
            }
            case 53 -> { // 다음 페이지
                if (hasNextPage()) {
                    currentPage++;
                    refresh();
                }
            }
            default -> {
                // 멤버 아이템 클릭 처리
                handleMemberClick(event);
            }
        }
    }
    
    /**
     * 멤버 클릭 처리
     */
    private void handleMemberClick(@NotNull InventoryClickEvent event) {
        // TODO: 멤버 역할 변경 및 추방 처리
    }
    
    /**
     * 다음 페이지가 있는지 확인
     */
    private boolean hasNextPage() {
        int totalItems = island.members().size() + island.workers().size();
        return (currentPage + 1) * ITEMS_PER_PAGE < totalItems;
    }
    
    /**
     * 멤버를 관리할 수 있는지 확인
     */
    private boolean canManageMember(@NotNull IslandMemberDTO member) {
        // 섬장만 부섬장을 관리 가능
        if (member.isCoOwner()) {
            return IslandPermissionHandler.isOwner(island, viewer);
        }
        
        // 섬장과 부섬장은 일반 멤버 관리 가능
        return IslandPermissionHandler.hasPermission(island, viewer, "KICK_MEMBERS");
    }
    
    /**
     * 날짜 포맷
     */
    private String formatDate(long timestamp) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(timestamp));
    }
}