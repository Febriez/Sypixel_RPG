package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.dto.island.IslandSpawnDTO;
import com.febrie.rpg.dto.island.IslandSpawnPointDTO;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.GuiHandlerUtil;
import com.febrie.rpg.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 섬 스폰 설정 GUI
 * 섬 스폰 포인트 및 방문자 스폰 설정
 *
 * @author Febrie, CoffeeTory
 */
public class IslandSpawnSettingsGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final IslandDTO island;
    private final boolean isOwner;
    private final boolean canManageSpawns;
    
    private IslandSpawnSettingsGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                                  @NotNull RPGMain plugin, @NotNull IslandDTO island) {
        super(viewer, guiManager, 54, "&3&l스폰 설정");
        this.islandManager = plugin.getIslandManager();
        this.island = island;
        this.isOwner = island.core().ownerUuid().equals(viewer.getUniqueId().toString());
        
        // 스폰 관리 권한 확인
        this.canManageSpawns = isOwner || island.membership().members().stream()
                .anyMatch(m -> m.uuid().equals(viewer.getUniqueId().toString()) && m.isCoOwner());
    }
    
    /**
     * Factory method to create and open the spawn settings GUI
     */
    public static IslandSpawnSettingsGui create(@NotNull RPGMain plugin, @NotNull Player viewer, 
                                               @NotNull IslandDTO island) {
        return new IslandSpawnSettingsGui(viewer, plugin.getGuiManager(), plugin, island);
    }
    
    @Override
    protected void setupLayout() {
        fillBorder(Material.CYAN_STAINED_GLASS_PANE);
        
        // 현재 스폰 정보
        setItem(13, new GuiItem(createCurrentSpawnInfo()));
        
        if (canManageSpawns) {
            // 스폰 설정 옵션들
            setItem(20, new GuiItem(createSetMainSpawnItem()).onAnyClick(this::handleSetMainSpawn));
            setItem(22, new GuiItem(createSetVisitorSpawnItem()).onAnyClick(this::handleSetVisitorSpawn));
            setItem(24, new GuiItem(createResetSpawnItem()).onAnyClick(this::handleResetSpawn));
            
            // 고급 설정
            setItem(30, new GuiItem(createSpawnProtectionItem()).onAnyClick(this::handleSpawnProtection));
            setItem(32, new GuiItem(createSpawnMessageItem()).onAnyClick(this::handleSpawnMessage));
            
            // 개인 스폰 관리
            setItem(40, new GuiItem(createPersonalSpawnItem()).onAnyClick(player -> {
                player.closeInventory();
                IslandPersonalSpawnGui.create(plugin, viewer, island).open(viewer);
            }));
        } else {
            // 권한 없음 안내
            setItem(22, new GuiItem(createNoPermissionItem()));
        }
        
        // 뒤로가기
        setItem(49, new GuiItem(createBackButton()).onAnyClick(player -> {
            player.closeInventory();
            IslandMainGui.create(plugin.getGuiManager(), viewer).open(viewer);
        }));
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        return null; // Use back button with direct navigation
    }
    
    @Override
    public @NotNull Component getTitle() {
        return Component.text("스폰 설정", UnifiedColorUtil.PRIMARY);
    }
    
    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
    
    private ItemStack createCurrentSpawnInfo() {
        IslandSpawnDTO spawn = island.configuration().spawnData();
        com.febrie.rpg.dto.island.IslandSpawnPointDTO defaultSpawn = spawn.defaultSpawn();
        com.febrie.rpg.dto.island.IslandSpawnPointDTO visitorSpawn = spawn.visitorSpawn();
        String mainSpawn = String.format("%.1f, %.1f, %.1f", defaultSpawn.x(), defaultSpawn.y(), defaultSpawn.z());
        
        ItemBuilder builder = new ItemBuilder(Material.BEACON)
                .displayName(UnifiedColorUtil.parseComponent("&b&l현재 스폰 설정"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7기본 스폰: &f" + mainSpawn))
                .addLore(UnifiedColorUtil.parseComponent("&7위치 이름: &f" + defaultSpawn.alias()))
                .addLore(UnifiedColorUtil.parseComponent(""));
        
        if (visitorSpawn != null) {
            String visitorLoc = String.format("%.1f, %.1f, %.1f", visitorSpawn.x(), visitorSpawn.y(), visitorSpawn.z());
            builder.addLore(UnifiedColorUtil.parseComponent("&7방문자 스폰: &f" + visitorLoc));
        } else {
            builder.addLore(UnifiedColorUtil.parseComponent("&7방문자 스폰: &c설정되지 않음"));
        }
        
        builder.addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7기본 스폰: 섬원이 이동하는 위치"))
                .addLore(UnifiedColorUtil.parseComponent("&7방문자 스폰: 방문자가 이동하는 위치"));
        
        return builder.build();
    }
    
    private ItemStack createSetMainSpawnItem() {
        return new ItemBuilder(Material.ENDER_PEARL)
                .displayName(UnifiedColorUtil.parseComponent("&a&l메인 스폰 설정"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7현재 위치를 메인 스폰으로"))
                .addLore(UnifiedColorUtil.parseComponent("&7설정합니다"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7섬원들이 /섬 워프 명령어를"))
                .addLore(UnifiedColorUtil.parseComponent("&7사용할 때 이동하는 위치입니다"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&e▶ 클릭하여 설정"))
                .build();
    }
    
    private ItemStack createSetVisitorSpawnItem() {
        return new ItemBuilder(Material.ENDER_EYE)
                .displayName(UnifiedColorUtil.parseComponent("&d&l방문자 스폰 설정"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7현재 위치를 방문자 스폰으로"))
                .addLore(UnifiedColorUtil.parseComponent("&7설정합니다"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7방문자가 섬에 들어올 때"))
                .addLore(UnifiedColorUtil.parseComponent("&7이동하는 위치입니다"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&e▶ 클릭하여 설정"))
                .build();
    }
    
    private ItemStack createResetSpawnItem() {
        return new ItemBuilder(Material.BARRIER)
                .displayName(UnifiedColorUtil.parseComponent("&c&l스폰 초기화"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7모든 스폰 설정을"))
                .addLore(UnifiedColorUtil.parseComponent("&7초기화합니다"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&c주의: 이 작업은 되돌릴 수 없습니다"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&e▶ 클릭하여 초기화"))
                .build();
    }
    
    private ItemStack createSpawnProtectionItem() {
        return new ItemBuilder(Material.SHIELD)
                .displayName(UnifiedColorUtil.parseComponent("&6&l스폰 보호"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&c※ 준비 중인 기능입니다"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7스폰 주변 반경을 설정하여"))
                .addLore(UnifiedColorUtil.parseComponent("&7블록 파괴/설치를 방지합니다"))
                .build();
    }
    
    private ItemStack createPersonalSpawnItem() {
        return new ItemBuilder(Material.ENDER_CHEST)
                .displayName(UnifiedColorUtil.parseComponent("&d&l개인 스폰 관리"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7자신만의 개인 스폰을"))
                .addLore(UnifiedColorUtil.parseComponent("&7설정하고 관리합니다"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&e▶ 클릭하여 관리"))
                .build();
    }
    
    private ItemStack createSpawnMessageItem() {
        return new ItemBuilder(Material.WRITABLE_BOOK)
                .displayName(UnifiedColorUtil.parseComponent("&e&l환영 메시지"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&c※ 준비 중인 기능입니다"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7방문자가 섬에 들어올 때"))
                .addLore(UnifiedColorUtil.parseComponent("&7표시되는 메시지를 설정합니다"))
                .build();
    }
    
    private ItemStack createNoPermissionItem() {
        return new ItemBuilder(Material.REDSTONE_BLOCK)
                .displayName(UnifiedColorUtil.parseComponent("&c&l권한 없음"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7스폰 설정은 섬장과"))
                .addLore(UnifiedColorUtil.parseComponent("&7부섬장만 할 수 있습니다"))
                .build();
    }
    
    private ItemStack createBackButton() {
        return new ItemBuilder(Material.ARROW)
                .displayName(UnifiedColorUtil.parseComponent("&c뒤로가기"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7메인 메뉴로 돌아갑니다"))
                .build();
    }
    
    
    private void handleSetMainSpawn(Player player) {
        Location loc = player.getLocation();
        
        // 섬 영역 내인지 확인
        if (!islandManager.getWorldManager().isIslandWorld(loc.getWorld())) {
            player.sendMessage(UnifiedColorUtil.parse("&c섬 월드에서만 스폰을 설정할 수 있습니다."));
            return;
        }
        
        if (islandManager.getIslandAt(loc) == null || !islandManager.getIslandAt(loc).getId().equals(island.core().islandId())) {
            player.sendMessage(UnifiedColorUtil.parse("&c자신의 섬 영역 내에서만 스폰을 설정할 수 있습니다."));
            return;
        }
        
        // 새로운 기본 스폰 포인트 생성
        com.febrie.rpg.dto.island.IslandSpawnPointDTO newDefaultSpawn = com.febrie.rpg.dto.island.IslandSpawnPointDTO.fromLocation(loc, "메인 스폰");
        
        // 새로운 스폰 DTO 생성
        IslandSpawnDTO newSpawn = new IslandSpawnDTO(
                newDefaultSpawn,
                island.configuration().spawnData().visitorSpawn(),
                island.configuration().spawnData().ownerSpawns(),
                island.configuration().spawnData().memberSpawns()
        );
        
        // 섬 업데이트 - GuiHandlerUtil 사용
        IslandDTO updated = GuiHandlerUtil.updateIslandSpawn(island, newSpawn);
        
        islandManager.updateIsland(updated);
        player.sendMessage(UnifiedColorUtil.parse("&a메인 스폰이 설정되었습니다!"));
        refresh();
    }
    
    private void handleSetVisitorSpawn(Player player) {
        Location loc = player.getLocation();
        
        // 섬 영역 내인지 확인
        if (!islandManager.getWorldManager().isIslandWorld(loc.getWorld())) {
            player.sendMessage(UnifiedColorUtil.parse("&c섬 월드에서만 스폰을 설정할 수 있습니다."));
            return;
        }
        
        // 방문자 스폰 위치 생성
        IslandSpawnPointDTO visitorSpawn = new IslandSpawnPointDTO(
            loc.getX(), loc.getY(), loc.getZ(),
            loc.getYaw(), loc.getPitch(),
            loc.getWorld().getName()
        );
        
        // 섬 스폰 데이터 업데이트
        IslandSpawnDTO newSpawn = new IslandSpawnDTO(
            island.configuration().spawnData().defaultSpawn(),
            visitorSpawn,
            island.configuration().spawnData().ownerSpawns(),
            island.configuration().spawnData().memberSpawns()
        );
        
        // 섬 업데이트
        IslandDTO updated = GuiHandlerUtil.updateIslandSpawn(island, newSpawn);
        
        islandManager.updateIsland(updated);
        player.sendMessage(UnifiedColorUtil.parse("&a방문자 스폰이 설정되었습니다!"));
        refresh();
    }
    
    private void handleResetSpawn(Player player) {
        // 기본 스폰으로 초기화
        IslandSpawnDTO newSpawn = IslandSpawnDTO.createDefault();
        
        // 섬 업데이트 - GuiHandlerUtil 사용
        IslandDTO updated = GuiHandlerUtil.updateIslandSpawn(island, newSpawn);
        
        islandManager.updateIsland(updated);
        player.sendMessage(UnifiedColorUtil.parse("&a스폰 설정이 초기화되었습니다!"));
        refresh();
    }
    
    private void handleSpawnProtection(Player player) {
        // 스폰 보호 기능 - 아직 구현되지 않음
        player.sendMessage(UnifiedColorUtil.parse("&c이 기능은 아직 구현되지 않았습니다."));
    }
    
    private void handleSpawnMessage(Player player) {
        // 환영 메시지 기능 - 아직 구현되지 않음
        player.sendMessage(UnifiedColorUtil.parse("&c이 기능은 아직 구현되지 않았습니다."));
    }
}