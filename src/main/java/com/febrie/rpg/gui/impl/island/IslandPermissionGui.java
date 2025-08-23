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
import com.febrie.rpg.util.StandardItemBuilder;
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
        super(viewer, guiManager, 54, Component.translatable("gui.island.permission.title"));
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
        return Component.translatable("gui.island.permission.title");
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
        
        return ItemBuilder.of(material, viewer.locale())
                .displayName(Component.text(IslandPermissionHandler.getRoleDisplayName(viewer.locale().getLanguage(), role),
                        selected ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.SECONDARY))
                .addLore(Component.empty())
                .addLore(Component.translatable("gui.island.permission.select-role-desc").color(UnifiedColorUtil.SECONDARY))
                .addLore(Component.empty())
                .addLore(selected ? 
                    Component.translatable("gui.island.permission.selected").color(UnifiedColorUtil.SUCCESS) :
                    Component.translatable("gui.island.permission.click-to-select").color(UnifiedColorUtil.WARNING))
                .glint(selected)
                .hideAllFlags()
                .build();
    }
    
    /**
     * 선택된 역할 표시
     */
    private ItemStack createSelectedRoleItem() {
        return ItemBuilder.of(Material.PAPER, viewer.locale())
                .displayName(Component.translatable("gui.island.permission.current-editing")
                    .color(UnifiedColorUtil.WARNING)
                    .append(Component.text(": "))
                    .append(Component.text(IslandPermissionHandler.getRoleDisplayName(viewer.locale().getLanguage(), selectedRole))
                        .color(UnifiedColorUtil.WHITE)))
                .addLore(Component.empty())
                .addLore(Component.translatable("gui.island.permission.select-other-role").color(UnifiedColorUtil.SECONDARY))
                .addLore(Component.translatable("gui.island.permission.edit-role-permissions").color(UnifiedColorUtil.SECONDARY))
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
        
        ItemBuilder builder = ItemBuilder.of(material, viewer.locale())
                .displayName(Component.text(displayName, enabled ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR))
                .addLore(Component.empty())
                .addLore(Component.translatable("gui.island.permission.status")
                    .append(Component.text(": "))
                    .append(Component.translatable(enabled ? "gui.island.permission.enabled" : "gui.island.permission.disabled")
                        .color(enabled ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR)))
                .addLore(Component.empty())
                .hideAllFlags();
        
        // 권한 설명 추가
        switch (permission) {
            case "BUILD" -> builder.addLore(Component.translatable("gui.island.permission.permissions.build-desc").color(UnifiedColorUtil.fromName("GRAY")));
            case "USE_ITEMS" -> builder.addLore(Component.translatable("gui.island.permission.permissions.interact-desc").color(UnifiedColorUtil.fromName("GRAY")));
            case "OPEN_CONTAINERS" -> builder.addLore(Component.translatable("gui.island.permission.permissions.containers-desc").color(UnifiedColorUtil.fromName("GRAY")));
            case "INVITE_MEMBERS" -> builder.addLore(Component.translatable("gui.island.permission.permissions.invite-desc").color(UnifiedColorUtil.fromName("GRAY")));
            case "KICK_MEMBERS" -> builder.addLore(Component.translatable("gui.island.permission.permissions.kick-desc").color(UnifiedColorUtil.fromName("GRAY")));
            case "MANAGE_WORKERS" -> builder.addLore(Component.translatable("gui.island.permission.permissions.workers-desc").color(UnifiedColorUtil.SECONDARY));
            case "MODIFY_SPAWNS" -> builder.addLore(Component.translatable("gui.island.permission.permissions.spawns-desc").color(UnifiedColorUtil.SECONDARY));
            case "CHANGE_SETTINGS" -> builder.addLore(Component.translatable("gui.island.permission.permissions.settings-desc").color(UnifiedColorUtil.fromName("GRAY")));
        }
        
        builder.addLore(Component.empty())
               .addLore(Component.translatable("gui.island.permission.click-to-toggle").color(UnifiedColorUtil.WARNING));
        
        return builder.build();
    }
    
    /**
     * 저장 버튼
     */
    private ItemStack createSaveButton() {
        return StandardItemBuilder.guiItem(Material.EMERALD_BLOCK)
                .displayName(UnifiedColorUtil.parseComponent("&a권한 설정 저장"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7변경한 권한 설정을 저장합니다."))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&e▶ 클릭하여 저장"))
                .build();
    }
    
    /**
     * 뒤로 가기 버튼
     */
    private ItemStack createBackButton() {
        return StandardItemBuilder.guiItem(Material.ARROW)
                .displayName(UnifiedColorUtil.parseComponent("&f뒤로 가기"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7메인 메뉴로 돌아갑니다."))
                .build();
    }
    
    /**
     * 닫기 버튼
     */
    private ItemStack createCloseButton() {
        return StandardItemBuilder.guiItem(Material.BARRIER)
                .displayName(UnifiedColorUtil.parseComponent("&c닫기"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7메뉴를 닫습니다."))
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
                player.sendMessage(UnifiedColorUtil.parse("&a권한 설정이 저장되었습니다!"));
                IslandMainGui.create(plugin.getGuiManager(), viewer).open(viewer);
            } else {
                player.sendMessage(UnifiedColorUtil.parse("&c권한 설정 저장에 실패했습니다."));
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