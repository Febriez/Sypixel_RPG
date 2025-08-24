package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.dto.island.IslandMemberDTO;
import com.febrie.rpg.dto.island.IslandRole;
import com.febrie.rpg.dto.island.IslandWorkerDTO;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.island.permission.IslandPermissionHandler;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.SkullUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
        super(viewer, guiManager, 54, Component.translatable("gui.island.member.title", Component.text(island.core().islandName())));
        this.islandManager = guiManager.getPlugin().getIslandManager();
        this.island = island;
    }
    
    /**
     * Factory method to create the GUI
     */
    public static IslandMemberGui create(@NotNull GuiManager guiManager, @NotNull Player viewer,
                                       @NotNull IslandDTO island) {
        return new IslandMemberGui(viewer, guiManager, island);
    }
    
    @Override
    protected void setupLayout() {
        // 배경 설정
        for (int i = 0; i < 9; i++) {
            setItem(i, GuiItem.display(
                ItemBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                    .displayName(Component.empty())
                    .hideAllFlags()
                    .build()
            ));
        }
        for (int i = size - 9; i < size; i++) {
            setItem(i, GuiItem.display(
                ItemBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                    .displayName(Component.empty())
                    .hideAllFlags()
                    .build()
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
                player.sendMessage(Component.translatable("gui.island.member.invite-prompt").color(UnifiedColorUtil.GREEN));
                player.sendMessage(Component.translatable("gui.island.member.invite-example").color(UnifiedColorUtil.GRAY));
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
                .displayName(Component.text(island.core().ownerName()).color(UnifiedColorUtil.GOLD).decorate(TextDecoration.BOLD))
                .addLore(Component.empty())
                .addLore(Component.translatable("gui.island.member.role").color(UnifiedColorUtil.GRAY)
                        .append(Component.translatable("gui.island.member.role-owner").color(UnifiedColorUtil.YELLOW)))
                .addLore(Component.text("UUID: ").color(UnifiedColorUtil.GRAY)
                        .append(Component.text(island.core().ownerUuid().substring(0, 8) + "...", UnifiedColorUtil.WHITE)))
                .addLore(Component.empty())
                .addLore(Component.translatable("gui.island.member.all-permissions").color(UnifiedColorUtil.GREEN))
                .addLore(Component.empty())
                .addLore(Component.translatable("gui.island.member.owner-cannot-change").color(UnifiedColorUtil.RED));
    }
    
    /**
     * 멤버 아이템 생성
     */
    private ItemBuilder createMemberItem(@NotNull IslandMemberDTO member) {
        ItemBuilder builder = ItemBuilder.from(SkullUtil.getPlayerHead(member.uuid()))
                .displayName(Component.text(member.name(), UnifiedColorUtil.AQUA))
                .addLore(Component.empty())
                .addLore(Component.translatable("gui.island.member.role").color(UnifiedColorUtil.GRAY)
                        .append(Component.text(IslandPermissionHandler.getRoleDisplayName(viewer.locale().getLanguage(), member.isCoOwner() ? IslandRole.CO_OWNER : IslandRole.MEMBER), UnifiedColorUtil.WHITE)))
                .addLore(Component.translatable("gui.island.member.join-date").color(UnifiedColorUtil.GRAY)
                        .append(Component.text(formatDate(member.joinedAt()), UnifiedColorUtil.WHITE)))
                .addLore(Component.empty());
        
        // 권한 표시
        if (member.isCoOwner()) {
            builder.addLore(Component.translatable("gui.island.member.most-permissions").color(UnifiedColorUtil.GREEN));
        } else {
            builder.addLore(Component.translatable("gui.island.member.normal-permissions").color(UnifiedColorUtil.GRAY));
        }
        
        // 관리 옵션 (권한이 있는 경우)
        if (canManageMember(member)) {
            builder.addLore(Component.empty())
                   .addLore(Component.translatable("gui.island.member.left-click-role").color(UnifiedColorUtil.YELLOW))
                   .addLore(Component.translatable("gui.island.member.right-click-kick").color(UnifiedColorUtil.RED));
        }
        
        return builder;
    }
    
    /**
     * 알바 아이템 생성
     */
    private ItemBuilder createWorkerItem(@NotNull IslandWorkerDTO worker) {
        ItemBuilder builder = ItemBuilder.from(SkullUtil.getPlayerHead(worker.uuid()))
                .displayName(Component.text(worker.name(), UnifiedColorUtil.GRAY))
                .addLore(Component.empty())
                .addLore(Component.translatable("gui.island.member.role").color(UnifiedColorUtil.GRAY)
                        .append(Component.translatable("gui.island.member.role-worker").color(UnifiedColorUtil.WHITE)))
                .addLore(Component.translatable("gui.island.member.hire-date").color(UnifiedColorUtil.GRAY)
                        .append(Component.text(formatDate(worker.hiredAt()), UnifiedColorUtil.WHITE)))
                .addLore(Component.translatable("gui.island.member.last-activity").color(UnifiedColorUtil.GRAY)
                        .append(Component.text(formatDate(worker.lastActivity()), UnifiedColorUtil.WHITE)))
                .addLore(Component.empty())
                .addLore(Component.translatable("gui.island.member.limited-permissions").color(UnifiedColorUtil.GRAY));
        
        // 관리 옵션 (권한이 있는 경우)
        if (IslandPermissionHandler.hasPermission(island, viewer, "MANAGE_WORKERS")) {
            builder.addLore(Component.empty())
                   .addLore(Component.translatable("gui.island.member.left-click-extend").color(UnifiedColorUtil.YELLOW))
                   .addLore(Component.translatable("gui.island.member.right-click-fire").color(UnifiedColorUtil.RED));
        }
        
        return builder;
    }
    
    /**
     * 초대 버튼
     */
    private ItemBuilder createInviteButton() {
        String currentMembers = (1 + island.membership().members().size()) + "/" + 
                (1 + island.configuration().upgradeData().memberLimit());
        String currentWorkers = island.membership().workers().size() + "/" + 
                island.configuration().upgradeData().workerLimit();
        
        return ItemBuilder.of(Material.EMERALD)
                .displayNameTranslated("items.island.member.invite.name")
                .loreTranslated("items.island.member.invite-button.lore", currentMembers, currentWorkers)
                .hideAllFlags();
    }
    
    /**
     * 뒤로 가기 버튼
     */
    private ItemBuilder createBackButton() {
        return ItemBuilder.of(Material.ARROW)
                .displayNameTranslated("items.gui.buttons.back.name")
                .addLoreTranslated("items.gui.buttons.back.lore")
                .hideAllFlags();
    }
    
    /**
     * 닫기 버튼
     */
    private ItemBuilder createCloseButton() {
        return ItemBuilder.of(Material.BARRIER)
                .displayNameTranslated("items.gui.buttons.close.name")
                .addLoreTranslated("items.gui.buttons.close.lore")
                .hideAllFlags();
    }
    
    /**
     * 이전 페이지 버튼
     */
    private ItemBuilder createPreviousPageButton() {
        return ItemBuilder.of(Material.ARROW)
                .displayNameTranslated("items.gui.buttons.previous-page.name")
                .loreTranslated("items.island.member.previous-page.lore", String.valueOf(currentPage + 1))
                .hideAllFlags();
    }
    
    /**
     * 다음 페이지 버튼
     */
    private ItemBuilder createNextPageButton() {
        return ItemBuilder.of(Material.ARROW)
                .displayNameTranslated("items.gui.buttons.next-page.name")
                .loreTranslated("items.island.member.next-page.lore", String.valueOf(currentPage + 1))
                .hideAllFlags();
    }
    
    
    @Override
    protected BaseGui getBackTarget() {
        return IslandMainGui.create(guiManager, viewer);
    }
    
    @Override
    public Component getTitle() {
        return Component.translatable("gui.island.member.title", Component.text(island.core().islandName()));
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
    
}