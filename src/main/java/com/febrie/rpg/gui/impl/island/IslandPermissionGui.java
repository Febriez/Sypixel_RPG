package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.island.permission.IslandPermissionHandler;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
    
    private IslandPermissionGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                              @NotNull RPGMain plugin, @NotNull IslandDTO island) {
        super(viewer, guiManager, 54, LangManager.text(LangKey.GUI_ISLAND_PERMISSION_TITLE, viewer.locale()));
        this.islandManager = plugin.getIslandManager();
        this.island = island;
    }
    
    /**
     * Factory method to create the GUI
     */
    public static IslandPermissionGui create(@NotNull RPGMain plugin, @NotNull IslandManager islandManager,
                                           @NotNull IslandDTO island, @NotNull Player viewer) {
        return new IslandPermissionGui(viewer, plugin.getGuiManager(), plugin, island);
    }
    
    @Override
    public @NotNull Component getTitle() {
        return LangManager.text(LangKey.GUI_ISLAND_PERMISSION_TITLE, viewer.locale());
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        return IslandMainGui.create(guiManager, viewer);
    }
    
    @Override
    protected void setupLayout() {
        // 배경 설정
        fillBorder(Material.RED_STAINED_GLASS_PANE);
        
        // 역할 선택 버튼들
        setItem(10, new GuiItem(createRoleButton(IslandRole.CO_OWNER))
                .onAnyClick(player -> selectRole(IslandRole.CO_OWNER)));
        setItem(11, new GuiItem(createRoleButton(IslandRole.MEMBER))
                .onAnyClick(player -> selectRole(IslandRole.MEMBER)));
        setItem(12, new GuiItem(createRoleButton(IslandRole.WORKER))
                .onAnyClick(player -> selectRole(IslandRole.WORKER)));
        setItem(13, new GuiItem(createRoleButton(IslandRole.VISITOR))
                .onAnyClick(player -> selectRole(IslandRole.VISITOR)));
        
        // 선택된 역할 표시
        setItem(15, new GuiItem(createSelectedRoleItem()));
        
        // 권한 토글 버튼들
        displayPermissions();
        
        // 저장 버튼
        setItem(49, new GuiItem(createSaveButton())
                .onAnyClick(player -> savePermissions(player)));
        
        // 뒤로 가기 버튼
        setItem(48, new GuiItem(createBackButton())
                .onAnyClick(player -> IslandMainGui.create(plugin.getGuiManager(), viewer).open(viewer)));
        
        // 닫기 버튼
        setItem(50, new GuiItem(createCloseButton())
                .onAnyClick(player -> player.closeInventory()));
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
        
        return ItemBuilder.of(material)
                .displayName(Component.text(IslandPermissionHandler.getRoleDisplayName(viewer.locale().getLanguage(), role),
                        selected ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.SECONDARY))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_SELECT_ROLE_DESC, viewer.locale()).color(UnifiedColorUtil.SECONDARY))
                .addLore(Component.empty())
                .addLore(selected ? 
                    LangManager.text(LangKey.GUI_ISLAND_PERMISSION_SELECTED, viewer.locale()).color(UnifiedColorUtil.SUCCESS) :
                    LangManager.text(LangKey.GUI_ISLAND_PERMISSION_CLICK_TO_SELECT, viewer.locale()).color(UnifiedColorUtil.WARNING))
                .glint(selected)
                .hideAllFlags()
                .build();
    }
    
    /**
     * 선택된 역할 표시
     */
    private ItemStack createSelectedRoleItem() {
        return ItemBuilder.of(Material.PAPER)
                .displayName(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_CURRENT_EDITING, viewer.locale())
                    .color(UnifiedColorUtil.WARNING)
                    .append(Component.text(": "))
                    .append(Component.text(IslandPermissionHandler.getRoleDisplayName(viewer.locale().getLanguage(), selectedRole))
                        .color(UnifiedColorUtil.WHITE)))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_SELECT_OTHER_ROLE, viewer.locale()).color(UnifiedColorUtil.SECONDARY))
                .addLore(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_EDIT_ROLE_PERMISSIONS, viewer.locale()).color(UnifiedColorUtil.SECONDARY))
                .hideAllFlags()
                .build();
    }
    
    /**
     * 권한 표시
     */
    private void displayPermissions() {
        IslandPermissionDTO.RolePermissions rolePerms = island.configuration().permissions()
                .rolePermissions().get(selectedRole);
        
        if (rolePerms == null) {
            // 기본값 생성 - 11개의 boolean 파라미터 필요 (canViewVisitors 추가)
            rolePerms = new IslandPermissionDTO.RolePermissions(
                false, false, false, false, false, false, false, false, false, false, false
            );
        }
        
        int[] slots = {20, 21, 22, 23, 29, 30, 31, 32};
        
        for (int i = 0; i < PERMISSIONS.length && i < slots.length; i++) {
            String permission = PERMISSIONS[i];
            boolean hasPermission = getPermissionValue(rolePerms, permission);
            final String finalPermission = permission;
            setItem(slots[i], new GuiItem(createPermissionItem(permission, hasPermission))
                    .onAnyClick(player -> togglePermission(finalPermission)));
        }
    }
    
    /**
     * 권한 아이템 생성
     */
    private ItemStack createPermissionItem(@NotNull String permission, boolean enabled) {
        Material material = enabled ? Material.LIME_DYE : Material.GRAY_DYE;
        String displayName = IslandPermissionHandler.getPermissionDisplayName(viewer.locale().getLanguage(), permission);
        
        ItemBuilder builder = ItemBuilder.of(material)
                .displayName(Component.text(displayName, enabled ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_STATUS, viewer.locale())
                    .append(Component.text(": "))
                    .append(LangManager.text(enabled ? LangKey.GUI_ISLAND_PERMISSION_ENABLED : LangKey.GUI_ISLAND_PERMISSION_DISABLED, viewer.locale())
                        .color(enabled ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR)))
                .addLore(Component.empty())
                .hideAllFlags();
        
        // 권한 설명 추가
        switch (permission) {
            case "BUILD" -> builder.addLore(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_PERMISSIONS_BUILD_DESC, viewer.locale()).color(UnifiedColorUtil.fromName("GRAY")));
            case "USE_ITEMS" -> builder.addLore(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_PERMISSIONS_INTERACT_DESC, viewer.locale()).color(UnifiedColorUtil.fromName("GRAY")));
            case "OPEN_CONTAINERS" -> builder.addLore(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_PERMISSIONS_CONTAINERS_DESC, viewer.locale()).color(UnifiedColorUtil.fromName("GRAY")));
            case "INVITE_MEMBERS" -> builder.addLore(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_PERMISSIONS_INVITE_DESC, viewer.locale()).color(UnifiedColorUtil.fromName("GRAY")));
            case "KICK_MEMBERS" -> builder.addLore(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_PERMISSIONS_KICK_DESC, viewer.locale()).color(UnifiedColorUtil.fromName("GRAY")));
            case "MANAGE_WORKERS" -> builder.addLore(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_PERMISSIONS_WORKERS_DESC, viewer.locale()).color(UnifiedColorUtil.SECONDARY));
            case "MODIFY_SPAWNS" -> builder.addLore(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_PERMISSIONS_SPAWNS_DESC, viewer.locale()).color(UnifiedColorUtil.SECONDARY));
            case "CHANGE_SETTINGS" -> builder.addLore(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_PERMISSIONS_SETTINGS_DESC, viewer.locale()).color(UnifiedColorUtil.fromName("GRAY")));
        }
        
        builder.addLore(Component.empty())
               .addLore(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_CLICK_TO_TOGGLE, viewer.locale()).color(UnifiedColorUtil.WARNING));
        
        return builder.build();
    }
    
    /**
     * 저장 버튼
     */
    private ItemStack createSaveButton() {
        return ItemBuilder.of(Material.EMERALD_BLOCK)
                .displayName(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_SAVE_TITLE, viewer.locale()))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_SAVE_DESCRIPTION, viewer.locale()))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_SAVE_CLICK, viewer.locale()))
                .hideAllFlags()
                .build();
    }
    
    /**
     * 뒤로 가기 버튼
     */
    private ItemStack createBackButton() {
        return ItemBuilder.of(Material.ARROW)
                .displayName(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_BACK_TITLE, viewer.locale()))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_BACK_DESCRIPTION, viewer.locale()))
                .hideAllFlags()
                .build();
    }
    
    /**
     * 닫기 버튼
     */
    private ItemStack createCloseButton() {
        return ItemBuilder.of(Material.BARRIER)
                .displayName(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_CLOSE_TITLE, viewer.locale()))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_CLOSE_DESCRIPTION, viewer.locale()))
                .hideAllFlags()
                .build();
    }
    
    /**
     * 역할 선택
     */
    private void selectRole(@NotNull IslandRole role) {
        if (role != IslandRole.OWNER) { // 섬장 권한은 변경 불가
            selectedRole = role;
            setupLayout();
        }
    }
    
    /**
     * 권한 토글
     */
    private void togglePermission(@NotNull String permission) {
        // 현재 권한 설정 가져오기
        Map<IslandRole, IslandPermissionDTO.RolePermissions> permissions = 
                new HashMap<>(island.configuration().permissions().rolePermissions());
        
        IslandPermissionDTO.RolePermissions rolePerms = permissions.get(selectedRole);
        if (rolePerms == null) {
            rolePerms = new IslandPermissionDTO.RolePermissions(
                false, false, false, false, false, false, false, false, false, false, false
            );
        }
        
        // 권한 토글
        boolean newValue = !getPermissionValue(rolePerms, permission);
        rolePerms = setPermissionValue(rolePerms, permission, newValue);
        permissions.put(selectedRole, rolePerms);
        
        // 업데이트된 섬 데이터 생성
        IslandConfigurationDTO updatedConfiguration = new IslandConfigurationDTO(
                island.core().islandId(),
                island.configuration().spawnData(),
                island.configuration().upgradeData(),
                new IslandPermissionDTO(permissions),
                island.configuration().settings()
        );
        
        island = new IslandDTO(island.core(), island.membership(), island.social(), updatedConfiguration);
        
        setupLayout();
    }
    
    /**
     * 권한 저장
     */
    private void savePermissions(@NotNull Player player) {
        islandManager.updateIsland(island).thenAccept(success -> {
            if (success) {
                player.sendMessage(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_SAVE_SUCCESS, player.locale()).color(UnifiedColorUtil.SUCCESS));
                IslandMainGui.create(plugin.getGuiManager(), viewer).open(viewer);
            } else {
                player.sendMessage(LangManager.text(LangKey.GUI_ISLAND_PERMISSION_SAVE_FAILED, player.locale()).color(UnifiedColorUtil.ERROR));
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
                perms.canUpgrade(), perms.canReset(), perms.canViewVisitors()
            );
            case "USE_ITEMS", "OPEN_CONTAINERS" -> new IslandPermissionDTO.RolePermissions(
                perms.canBuild(), perms.canBreak(), value, perms.canInvite(),
                perms.canKick(), perms.canSetSpawn(), perms.canManageWorkers(), perms.canManagePermissions(),
                perms.canUpgrade(), perms.canReset(), perms.canViewVisitors()
            );
            case "INVITE_MEMBERS" -> new IslandPermissionDTO.RolePermissions(
                perms.canBuild(), perms.canBreak(), perms.canInteract(), value,
                perms.canKick(), perms.canSetSpawn(), perms.canManageWorkers(), perms.canManagePermissions(),
                perms.canUpgrade(), perms.canReset(), perms.canViewVisitors()
            );
            case "KICK_MEMBERS" -> new IslandPermissionDTO.RolePermissions(
                perms.canBuild(), perms.canBreak(), perms.canInteract(), perms.canInvite(),
                value, perms.canSetSpawn(), perms.canManageWorkers(), perms.canManagePermissions(),
                perms.canUpgrade(), perms.canReset(), perms.canViewVisitors()
            );
            case "MANAGE_WORKERS" -> new IslandPermissionDTO.RolePermissions(
                perms.canBuild(), perms.canBreak(), perms.canInteract(), perms.canInvite(),
                perms.canKick(), perms.canSetSpawn(), value, perms.canManagePermissions(),
                perms.canUpgrade(), perms.canReset(), perms.canViewVisitors()
            );
            case "MODIFY_SPAWNS" -> new IslandPermissionDTO.RolePermissions(
                perms.canBuild(), perms.canBreak(), perms.canInteract(), perms.canInvite(),
                perms.canKick(), value, perms.canManageWorkers(), perms.canManagePermissions(),
                perms.canUpgrade(), perms.canReset(), perms.canViewVisitors()
            );
            case "CHANGE_SETTINGS" -> new IslandPermissionDTO.RolePermissions(
                perms.canBuild(), perms.canBreak(), perms.canInteract(), perms.canInvite(),
                perms.canKick(), perms.canSetSpawn(), perms.canManageWorkers(), value,
                perms.canUpgrade(), perms.canReset(), perms.canViewVisitors()
            );
            default -> perms;
        };
    }
}