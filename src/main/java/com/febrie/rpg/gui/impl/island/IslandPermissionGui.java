package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandPermissionDTO;
import com.febrie.rpg.dto.island.IslandRole;
import com.febrie.rpg.gui.BaseGui;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.island.permission.IslandPermissionHandler;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 섬 권한 관리 GUI
 *
 * @author Febrie, CoffeeTory
 */
public class IslandPermissionGui extends BaseGui {
    
    private final IslandManager islandManager;
    private IslandDTO island;
    private IslandRole selectedRole = IslandRole.MEMBER;
    
    // 권한 목록
    private static final String[] PERMISSIONS = {
        "BUILD", "USE_ITEMS", "OPEN_CONTAINERS", "INVITE_MEMBERS",
        "KICK_MEMBERS", "MANAGE_WORKERS", "MODIFY_SPAWNS", "CHANGE_SETTINGS"
    };
    
    private IslandPermissionGui(@NotNull RPGMain plugin, @NotNull IslandManager islandManager,
                              @NotNull IslandDTO island, @NotNull Player viewer) {
        super(plugin, 54);
        this.islandManager = islandManager;
        this.island = island;
        this.viewer = viewer;
    }
    
    /**
     * Factory method to create the GUI
     */
    public static IslandPermissionGui create(@NotNull RPGMain plugin, @NotNull IslandManager islandManager,
                                           @NotNull IslandDTO island, @NotNull Player viewer) {
        IslandPermissionGui gui = new IslandPermissionGui(plugin, islandManager, island, viewer);
        return BaseGui.create(gui, ColorUtil.parseComponent("&c권한 관리 - " + island.islandName()));
    }
    
    @Override
    protected void setupItems() {
        // 배경 설정
        fillBorder(Material.RED_STAINED_GLASS_PANE);
        
        // 역할 선택 버튼들
        setItem(10, createRoleButton(IslandRole.CO_OWNER));
        setItem(11, createRoleButton(IslandRole.MEMBER));
        setItem(12, createRoleButton(IslandRole.WORKER));
        setItem(13, createRoleButton(IslandRole.VISITOR));
        
        // 선택된 역할 표시
        setItem(15, createSelectedRoleItem());
        
        // 권한 토글 버튼들
        displayPermissions();
        
        // 저장 버튼
        setItem(49, createSaveButton());
        
        // 뒤로 가기 버튼
        setItem(48, createBackButton());
        
        // 닫기 버튼
        setItem(50, createCloseButton());
    }
    
    /**
     * 역할 버튼 생성
     */
    private ItemStack createRoleButton(@NotNull IslandRole role) {
        Material material = switch (role) {
            case CO_OWNER -> Material.GOLD_BLOCK;
            case MEMBER -> Material.IRON_BLOCK;
            case WORKER -> Material.COAL_BLOCK;
            case VISITOR -> Material.STONE;
            default -> Material.BARRIER;
        };
        
        boolean selected = role == selectedRole;
        
        return new ItemBuilder(material)
                .displayName(ColorUtil.parseComponent((selected ? "&a&l" : "&7") + 
                        IslandPermissionHandler.getRoleDisplayName(plugin.getLangManager(), viewer.locale().getLanguage(), role)))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7이 역할의 권한을 설정합니다."))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent(selected ? "&a▶ 선택됨" : "&e▶ 클릭하여 선택"))
                .glint(selected)
                .build();
    }
    
    /**
     * 선택된 역할 표시
     */
    private ItemStack createSelectedRoleItem() {
        return new ItemBuilder(Material.PAPER)
                .displayName(ColorUtil.parseComponent("&e현재 편집 중: &f" + 
                        IslandPermissionHandler.getRoleDisplayName(plugin.getLangManager(), viewer.locale().getLanguage(), selectedRole)))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7좌측에서 다른 역할을 선택하여"))
                .addLore(ColorUtil.parseComponent("&7해당 역할의 권한을 편집할 수 있습니다."))
                .build();
    }
    
    /**
     * 권한 표시
     */
    private void displayPermissions() {
        IslandPermissionDTO.RolePermissions rolePerms = island.permissions()
                .rolePermissions().get(selectedRole);
        
        if (rolePerms == null) {
            // 기본값 생성 - 10개의 boolean 파라미터 필요
            rolePerms = new IslandPermissionDTO.RolePermissions(
                false, false, false, false, false, false, false, false, false, false
            );
        }
        
        int[] slots = {20, 21, 22, 23, 29, 30, 31, 32};
        
        for (int i = 0; i < PERMISSIONS.length && i < slots.length; i++) {
            String permission = PERMISSIONS[i];
            boolean hasPermission = getPermissionValue(rolePerms, permission);
            setItem(slots[i], createPermissionItem(permission, hasPermission));
        }
    }
    
    /**
     * 권한 아이템 생성
     */
    private ItemStack createPermissionItem(@NotNull String permission, boolean enabled) {
        Material material = enabled ? Material.LIME_DYE : Material.GRAY_DYE;
        String displayName = IslandPermissionHandler.getPermissionDisplayName(plugin.getLangManager(), viewer.locale().getLanguage(), permission);
        
        ItemBuilder builder = new ItemBuilder(material)
                .displayName(ColorUtil.parseComponent((enabled ? "&a" : "&c") + displayName))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7상태: " + (enabled ? "&a활성화" : "&c비활성화")))
                .addLore(ColorUtil.parseComponent(""));
        
        // 권한 설명 추가
        switch (permission) {
            case "BUILD" -> builder.addLore(ColorUtil.parseComponent("&7블록을 설치하고 파괴할 수 있습니다."));
            case "USE_ITEMS" -> builder.addLore(ColorUtil.parseComponent("&7문, 버튼 등을 사용할 수 있습니다."));
            case "OPEN_CONTAINERS" -> builder.addLore(ColorUtil.parseComponent("&7상자를 열 수 있습니다."));
            case "INVITE_MEMBERS" -> builder.addLore(ColorUtil.parseComponent("&7새 멤버를 초대할 수 있습니다."));
            case "KICK_MEMBERS" -> builder.addLore(ColorUtil.parseComponent("&7멤버를 추방할 수 있습니다."));
            case "MANAGE_WORKERS" -> builder.addLore(ColorUtil.parseComponent("&7알바를 관리할 수 있습니다."));
            case "MODIFY_SPAWNS" -> builder.addLore(ColorUtil.parseComponent("&7스폰 위치를 설정할 수 있습니다."));
            case "CHANGE_SETTINGS" -> builder.addLore(ColorUtil.parseComponent("&7섬 설정을 변경할 수 있습니다."));
        }
        
        builder.addLore(ColorUtil.parseComponent(""))
               .addLore(ColorUtil.parseComponent("&e▶ 클릭하여 " + (enabled ? "비활성화" : "활성화")));
        
        return builder.build();
    }
    
    /**
     * 저장 버튼
     */
    private ItemStack createSaveButton() {
        return new ItemBuilder(Material.EMERALD_BLOCK)
                .displayName(ColorUtil.parseComponent("&a권한 설정 저장"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7변경한 권한 설정을 저장합니다."))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&e▶ 클릭하여 저장"))
                .build();
    }
    
    /**
     * 뒤로 가기 버튼
     */
    private ItemStack createBackButton() {
        return new ItemBuilder(Material.ARROW)
                .displayName(ColorUtil.parseComponent("&f뒤로 가기"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7메인 메뉴로 돌아갑니다."))
                .build();
    }
    
    /**
     * 닫기 버튼
     */
    private ItemStack createCloseButton() {
        return new ItemBuilder(Material.BARRIER)
                .displayName(ColorUtil.parseComponent("&c닫기"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7메뉴를 닫습니다."))
                .build();
    }
    
    @Override
    protected void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        int slot = event.getSlot();
        
        switch (slot) {
            case 10 -> selectRole(IslandRole.CO_OWNER);
            case 11 -> selectRole(IslandRole.MEMBER);
            case 12 -> selectRole(IslandRole.WORKER);
            case 13 -> selectRole(IslandRole.VISITOR);
            case 48 -> IslandMainGui.create(plugin.getGuiManager(), viewer).open(viewer);
            case 49 -> savePermissions(player);
            case 50 -> player.closeInventory();
            default -> {
                // 권한 토글 처리
                int[] permSlots = {20, 21, 22, 23, 29, 30, 31, 32};
                for (int i = 0; i < permSlots.length && i < PERMISSIONS.length; i++) {
                    if (slot == permSlots[i]) {
                        togglePermission(PERMISSIONS[i]);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * 역할 선택
     */
    private void selectRole(@NotNull IslandRole role) {
        if (role != IslandRole.OWNER) { // 섬장 권한은 변경 불가
            selectedRole = role;
            refresh();
        }
    }
    
    /**
     * 권한 토글
     */
    private void togglePermission(@NotNull String permission) {
        // 현재 권한 설정 가져오기
        Map<IslandRole, IslandPermissionDTO.RolePermissions> permissions = 
                new HashMap<>(island.permissions().rolePermissions());
        
        IslandPermissionDTO.RolePermissions rolePerms = permissions.get(selectedRole);
        if (rolePerms == null) {
            rolePerms = new IslandPermissionDTO.RolePermissions(
                false, false, false, false, false, false, false, false, false, false
            );
        }
        
        // 권한 토글
        boolean newValue = !getPermissionValue(rolePerms, permission);
        rolePerms = setPermissionValue(rolePerms, permission, newValue);
        permissions.put(selectedRole, rolePerms);
        
        // 업데이트된 섬 데이터 생성
        island = new IslandDTO(
                island.islandId(),
                island.ownerUuid(),
                island.ownerName(),
                island.islandName(),
                island.size(),
                island.isPublic(),
                island.createdAt(),
                island.lastActivity(),
                island.members(),
                island.workers(),
                island.contributions(),
                island.spawnData(),
                island.upgradeData(),
                new IslandPermissionDTO(permissions),
                island.pendingInvites(),
                island.recentVisits(),
                island.totalResets(),
                island.deletionScheduledAt(),
                island.settings()
        );
        
        refresh();
    }
    
    /**
     * 권한 저장
     */
    private void savePermissions(@NotNull Player player) {
        islandManager.updateIsland(island).thenAccept(success -> {
            if (success) {
                player.sendMessage(ColorUtil.colorize("&a권한 설정이 저장되었습니다!"));
                IslandMainGui.create(plugin.getGuiManager(), viewer).open(viewer);
            } else {
                player.sendMessage(ColorUtil.colorize("&c권한 설정 저장에 실패했습니다."));
            }
        });
    }
    
    /**
     * 권한 값 가져오기
     */
    private boolean getPermissionValue(@NotNull IslandPermissionDTO.RolePermissions perms, @NotNull String permission) {
        return switch (permission) {
            case "BUILD" -> perms.canBuild();
            case "USE_ITEMS" -> perms.canInteract();
            case "OPEN_CONTAINERS" -> perms.canInteract();
            case "INVITE_MEMBERS" -> perms.canInvite();
            case "KICK_MEMBERS" -> perms.canKick();
            case "MANAGE_WORKERS" -> perms.canManageWorkers();
            case "MODIFY_SPAWNS" -> perms.canSetSpawn();
            case "CHANGE_SETTINGS" -> perms.canManagePermissions();
            default -> false;
        };
    }
    
    /**
     * 권한 값 설정
     */
    private IslandPermissionDTO.RolePermissions setPermissionValue(
            @NotNull IslandPermissionDTO.RolePermissions perms,
            @NotNull String permission,
            boolean value) {
        
        // RolePermissions has 10 parameters: canBuild, canBreak, canInteract, canInvite, canKick,
        // canSetSpawn, canManageWorkers, canManagePermissions, canUpgrade, canReset
        return switch (permission) {
            case "BUILD" -> new IslandPermissionDTO.RolePermissions(
                value, perms.canBreak(), perms.canInteract(), perms.canInvite(),
                perms.canKick(), perms.canSetSpawn(), perms.canManageWorkers(), perms.canManagePermissions(),
                perms.canUpgrade(), perms.canReset()
            );
            case "USE_ITEMS", "OPEN_CONTAINERS" -> new IslandPermissionDTO.RolePermissions(
                perms.canBuild(), perms.canBreak(), value, perms.canInvite(),
                perms.canKick(), perms.canSetSpawn(), perms.canManageWorkers(), perms.canManagePermissions(),
                perms.canUpgrade(), perms.canReset()
            );
            case "INVITE_MEMBERS" -> new IslandPermissionDTO.RolePermissions(
                perms.canBuild(), perms.canBreak(), perms.canInteract(), value,
                perms.canKick(), perms.canSetSpawn(), perms.canManageWorkers(), perms.canManagePermissions(),
                perms.canUpgrade(), perms.canReset()
            );
            case "KICK_MEMBERS" -> new IslandPermissionDTO.RolePermissions(
                perms.canBuild(), perms.canBreak(), perms.canInteract(), perms.canInvite(),
                value, perms.canSetSpawn(), perms.canManageWorkers(), perms.canManagePermissions(),
                perms.canUpgrade(), perms.canReset()
            );
            case "MANAGE_WORKERS" -> new IslandPermissionDTO.RolePermissions(
                perms.canBuild(), perms.canBreak(), perms.canInteract(), perms.canInvite(),
                perms.canKick(), perms.canSetSpawn(), value, perms.canManagePermissions(),
                perms.canUpgrade(), perms.canReset()
            );
            case "MODIFY_SPAWNS" -> new IslandPermissionDTO.RolePermissions(
                perms.canBuild(), perms.canBreak(), perms.canInteract(), perms.canInvite(),
                perms.canKick(), value, perms.canManageWorkers(), perms.canManagePermissions(),
                perms.canUpgrade(), perms.canReset()
            );
            case "CHANGE_SETTINGS" -> new IslandPermissionDTO.RolePermissions(
                perms.canBuild(), perms.canBreak(), perms.canInteract(), perms.canInvite(),
                perms.canKick(), perms.canSetSpawn(), perms.canManageWorkers(), value,
                perms.canUpgrade(), perms.canReset()
            );
            default -> perms;
        };
    }
}