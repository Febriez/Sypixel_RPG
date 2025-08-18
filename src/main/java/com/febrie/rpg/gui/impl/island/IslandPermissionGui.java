package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.dto.island.IslandPermissionDTO;
import com.febrie.rpg.dto.island.IslandRole;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.island.permission.IslandPermissionHandler;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.StandardItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
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
    
    private IslandPermissionGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                              @NotNull RPGMain plugin, @NotNull IslandDTO island) {
        super(viewer, guiManager, 54, "gui.island.permission.title");
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
        return Component.text("섬 권한 설정", UnifiedColorUtil.PRIMARY);
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
        
        return StandardItemBuilder.guiItem(material)
                .displayName(UnifiedColorUtil.parseComponent((selected ? "&a&l" : "&7") + 
                        IslandPermissionHandler.getRoleDisplayName(viewer.locale().getLanguage(), role)))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7이 역할의 권한을 설정합니다."))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent(selected ? "&a▶ 선택됨" : "&e▶ 클릭하여 선택"))
                .glint(selected)
                .build();
    }
    
    /**
     * 선택된 역할 표시
     */
    private ItemStack createSelectedRoleItem() {
        return StandardItemBuilder.guiItem(Material.PAPER)
                .displayName(UnifiedColorUtil.parseComponent("&e현재 편집 중: &f" + 
                        IslandPermissionHandler.getRoleDisplayName(viewer.locale().getLanguage(), selectedRole)))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7좌측에서 다른 역할을 선택하여"))
                .addLore(UnifiedColorUtil.parseComponent("&7해당 역할의 권한을 편집할 수 있습니다."))
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
        
        ItemBuilder builder = StandardItemBuilder.guiItem(material)
                .displayName(UnifiedColorUtil.parseComponent((enabled ? "&a" : "&c") + displayName))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7상태: " + (enabled ? "&a활성화" : "&c비활성화")))
                .addLore(UnifiedColorUtil.parseComponent(""));
        
        // 권한 설명 추가
        switch (permission) {
            case "BUILD" -> builder.addLore(UnifiedColorUtil.parseComponent("&7블록을 설치하고 파괴할 수 있습니다."));
            case "USE_ITEMS" -> builder.addLore(UnifiedColorUtil.parseComponent("&7문, 버튼 등을 사용할 수 있습니다."));
            case "OPEN_CONTAINERS" -> builder.addLore(UnifiedColorUtil.parseComponent("&7상자를 열 수 있습니다."));
            case "INVITE_MEMBERS" -> builder.addLore(UnifiedColorUtil.parseComponent("&7새 멤버를 초대할 수 있습니다."));
            case "KICK_MEMBERS" -> builder.addLore(UnifiedColorUtil.parseComponent("&7멤버를 추방할 수 있습니다."));
            case "MANAGE_WORKERS" -> builder.addLore(UnifiedColorUtil.parseComponent("&7알바를 관리할 수 있습니다."));
            case "MODIFY_SPAWNS" -> builder.addLore(UnifiedColorUtil.parseComponent("&7스폰 위치를 설정할 수 있습니다."));
            case "CHANGE_SETTINGS" -> builder.addLore(UnifiedColorUtil.parseComponent("&7섬 설정을 변경할 수 있습니다."));
        }
        
        builder.addLore(UnifiedColorUtil.parseComponent(""))
               .addLore(UnifiedColorUtil.parseComponent("&e▶ 클릭하여 " + (enabled ? "비활성화" : "활성화")));
        
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