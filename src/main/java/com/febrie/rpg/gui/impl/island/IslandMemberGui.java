package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.dto.island.IslandMemberDTO;
import com.febrie.rpg.dto.island.IslandRole;
import com.febrie.rpg.dto.island.IslandWorkerDTO;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.InteractiveGui;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import net.kyori.adventure.text.Component;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.island.permission.IslandPermissionHandler;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.StandardItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.SkullUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import com.febrie.rpg.util.DateFormatUtil;

/**
 * 섬 멤버 관리 GUI
 *
 * @author Febrie, CoffeeTory
 */
public class IslandMemberGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final IslandDTO island;
    private int currentPage = 0;
    private final int ITEMS_PER_PAGE = 28; // 7x4 grid
    
    private IslandMemberGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                          @NotNull IslandDTO island) {
        super(viewer, guiManager, 54, "gui.island.member.title", island.core().islandName());
        this.islandManager = guiManager.getPlugin().getIslandManager();
        this.island = island;
    }
    
    /**
     * Factory method to create the GUI
     */
    public static IslandMemberGui create(@NotNull GuiManager guiManager, @NotNull Player viewer,
                                       @NotNull IslandDTO island) {
        IslandMemberGui gui = new IslandMemberGui(viewer, guiManager, island);
        gui.initialize("gui.island.member.title", island.core().islandName());
        return gui;
    }
    
    @Override
    protected void setupLayout() {
        // 배경 설정
        for (int i = 0; i < 9; i++) {
            setItem(i, GuiItem.display(
                StandardItemBuilder.guiItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                    .displayName(Component.empty())
            ));
        }
        for (int i = size - 9; i < size; i++) {
            setItem(i, GuiItem.display(
                StandardItemBuilder.guiItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                    .displayName(Component.empty())
            ));
        }
        
        // 섬장 표시 (항상 첫번째)
        setItem(10, GuiItem.display(createOwnerItem()));
        
        // 멤버 목록 표시
        displayMembers();
        
        // 페이지 네비게이션
        if (currentPage > 0) {
            setItem(45, GuiItem.clickable(createPreviousPageButton(), player -> {
                currentPage--;
                refresh();
            }));
        }
        
        if (hasNextPage()) {
            setItem(53, GuiItem.clickable(createNextPageButton(), player -> {
                currentPage++;
                refresh();
            }));
        }
        
        // 멤버 초대 버튼 (권한이 있는 경우)
        if (IslandPermissionHandler.hasPermission(island, viewer, "INVITE_MEMBERS")) {
            setItem(49, GuiItem.clickable(createInviteButton(), player -> {
                player.closeInventory();
                player.sendMessage(UnifiedColorUtil.parse("&a초대할 플레이어의 이름을 입력하세요:"));
                player.sendMessage(UnifiedColorUtil.parse("&7예시: /섬 초대 <플레이어명>"));
            }));
        }
        
        // 뒤로 가기 버튼
        setItem(48, GuiItem.clickable(createBackButton(), player -> 
            IslandMainGui.create(guiManager, viewer).open(viewer)
        ));
        
        // 닫기 버튼
        setItem(50, GuiItem.clickable(createCloseButton(), Player::closeInventory));
    }
    
    /**
     * 멤버 목록 표시
     */
    private void displayMembers() {
        List<ItemBuilder> memberItems = new ArrayList<>();
        
        // 섬원들
        for (IslandMemberDTO member : island.membership().members()) {
            memberItems.add(createMemberItem(member));
        }
        
        // 알바들
        for (IslandWorkerDTO worker : island.membership().workers()) {
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
                int memberIndex = i;
                setItem(slots[slotIndex], GuiItem.clickable(
                    memberItems.get(i),
                    player -> handleMemberItemClick(player, memberIndex)
                ));
            }
        }
    }
    
    /**
     * 섬장 아이템 생성
     */
    private ItemBuilder createOwnerItem() {
        return ItemBuilder.from(SkullUtil.getPlayerHead(island.core().ownerUuid()))
                .displayName(UnifiedColorUtil.parseComponent("&6&l" + island.core().ownerName()))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7역할: &e섬장"))
                .addLore(UnifiedColorUtil.parseComponent("&7UUID: &f" + island.core().ownerUuid().substring(0, 8) + "..."))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&a✓ 모든 권한 보유"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&c섬장은 변경할 수 없습니다."));
    }
    
    /**
     * 멤버 아이템 생성
     */
    private ItemBuilder createMemberItem(@NotNull IslandMemberDTO member) {
        ItemBuilder builder = ItemBuilder.from(SkullUtil.getPlayerHead(member.uuid()))
                .displayName(UnifiedColorUtil.parseComponent("&b" + member.name()))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7역할: &f" + IslandPermissionHandler.getRoleDisplayName(viewer.locale().getLanguage(), member.isCoOwner() ? IslandRole.CO_OWNER : IslandRole.MEMBER)))
                .addLore(UnifiedColorUtil.parseComponent("&7가입일: &f" + formatDate(member.joinedAt())))
                .addLore(UnifiedColorUtil.parseComponent(""));
        
        // 권한 표시
        if (member.isCoOwner()) {
            builder.addLore(UnifiedColorUtil.parseComponent("&a✓ 대부분의 권한 보유"));
        } else {
            builder.addLore(UnifiedColorUtil.parseComponent("&7일반 섬원 권한"));
        }
        
        // 관리 옵션 (권한이 있는 경우)
        if (canManageMember(member)) {
            builder.addLore(UnifiedColorUtil.parseComponent(""))
                   .addLore(UnifiedColorUtil.parseComponent("&e▶ 좌클릭: 역할 변경"))
                   .addLore(UnifiedColorUtil.parseComponent("&c▶ 우클릭: 추방"));
        }
        
        return builder;
    }
    
    /**
     * 알바 아이템 생성
     */
    private ItemBuilder createWorkerItem(@NotNull IslandWorkerDTO worker) {
        ItemBuilder builder = ItemBuilder.from(SkullUtil.getPlayerHead(worker.uuid()))
                .displayName(UnifiedColorUtil.parseComponent("&7" + worker.name()))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7역할: &f알바"))
                .addLore(UnifiedColorUtil.parseComponent("&7고용일: &f" + formatDate(worker.hiredAt())))
                .addLore(UnifiedColorUtil.parseComponent("&7마지막 활동: &f" + formatDate(worker.lastActivity())))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7제한된 권한"));
        
        // 관리 옵션 (권한이 있는 경우)
        if (IslandPermissionHandler.hasPermission(island, viewer, "MANAGE_WORKERS")) {
            builder.addLore(UnifiedColorUtil.parseComponent(""))
                   .addLore(UnifiedColorUtil.parseComponent("&e▶ 좌클릭: 기간 연장"))
                   .addLore(UnifiedColorUtil.parseComponent("&c▶ 우클릭: 해고"));
        }
        
        return builder;
    }
    
    /**
     * 초대 버튼
     */
    private ItemBuilder createInviteButton() {
        return StandardItemBuilder.guiItem(Material.EMERALD)
                .displayName(UnifiedColorUtil.parseComponent("&a새 멤버 초대"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7새로운 섬원을 초대합니다."))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7현재 멤버: &e" + (1 + island.membership().members().size()) + "/" + 
                        (1 + island.configuration().upgradeData().memberLimit())))
                .addLore(UnifiedColorUtil.parseComponent("&7알바: &e" + island.membership().workers().size() + "/" + 
                        island.configuration().upgradeData().workerLimit()))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&e▶ 클릭하여 초대"));
    }
    
    /**
     * 뒤로 가기 버튼
     */
    private ItemBuilder createBackButton() {
        return StandardItemBuilder.guiItem(Material.ARROW)
                .displayName(UnifiedColorUtil.parseComponent("&f뒤로 가기"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7메인 메뉴로 돌아갑니다."));
    }
    
    /**
     * 닫기 버튼
     */
    private ItemBuilder createCloseButton() {
        return StandardItemBuilder.guiItem(Material.BARRIER)
                .displayName(UnifiedColorUtil.parseComponent("&c닫기"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7메뉴를 닫습니다."));
    }
    
    /**
     * 이전 페이지 버튼
     */
    private ItemBuilder createPreviousPageButton() {
        return StandardItemBuilder.guiItem(Material.ARROW)
                .displayName(UnifiedColorUtil.parseComponent("&f이전 페이지"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7현재 페이지: &e" + (currentPage + 1)));
    }
    
    /**
     * 다음 페이지 버튼
     */
    private ItemBuilder createNextPageButton() {
        return StandardItemBuilder.guiItem(Material.ARROW)
                .displayName(UnifiedColorUtil.parseComponent("&f다음 페이지"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7현재 페이지: &e" + (currentPage + 1)));
    }
    
    
    @Override
    protected BaseGui getBackTarget() {
        return IslandMainGui.create(guiManager, viewer);
    }
    
    @Override
    public Component getTitle() {
        return UnifiedColorUtil.parseComponent("&b섬원 관리 - " + island.core().islandName());
    }
    
    private void handleMemberItemClick(Player player, int memberIndex) {
        String memberUuid = null;
        
        if (memberIndex < island.membership().members().size()) {
            memberUuid = island.membership().members().get(memberIndex).uuid();
        } else if (memberIndex < island.membership().members().size() + island.membership().workers().size()) {
            int workerIndex = memberIndex - island.membership().members().size();
            memberUuid = island.membership().workers().get(workerIndex).uuid();
        }
        
        if (memberUuid != null && canManageMember(memberIndex)) {
            player.closeInventory();
            IslandMemberManageGui.create(plugin, viewer, island, memberUuid).open(viewer);
        }
    }
    
    
    
    /**
     * 다음 페이지가 있는지 확인
     */
    private boolean hasNextPage() {
        int totalItems = island.membership().members().size() + island.membership().workers().size();
        return (currentPage + 1) * ITEMS_PER_PAGE < totalItems;
    }
    
    /**
     * 멤버를 관리할 수 있는지 확인
     */
    private boolean canManageMember(int memberIndex) {
        if (memberIndex < island.membership().members().size()) {
            IslandMemberDTO member = island.membership().members().get(memberIndex);
            // 섬장만 부섬장을 관리 가능
            if (member.isCoOwner()) {
                return IslandPermissionHandler.isOwner(island, viewer);
            }
            // 섬장과 부섬장은 일반 멤버 관리 가능
            return IslandPermissionHandler.hasPermission(island, viewer, "KICK_MEMBERS");
        } else {
            // 알바 관리 권한
            return IslandPermissionHandler.hasPermission(island, viewer, "MANAGE_WORKERS");
        }
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
        return DateFormatUtil.formatDateOnlyFromMillis(timestamp);
    }
    
    @Override
    public void onClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        event.setCancelled(true);
        // GuiItem이 클릭 처리를 담당합니다
    }
}