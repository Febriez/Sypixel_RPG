package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.GuiHandlerUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
        super(viewer, guiManager, 54, LangManager.getComponent("gui.island.spawn.title".replace("-", "_"), viewer));
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
        return LangManager.getComponent("gui.island.spawn.title".replace("-", "_"), viewer);
    }
    
    private ItemStack createCurrentSpawnInfo() {
        IslandSpawnDTO spawn = island.configuration().spawnData();
        com.febrie.rpg.dto.island.IslandSpawnPointDTO defaultSpawn = spawn.defaultSpawn();
        com.febrie.rpg.dto.island.IslandSpawnPointDTO visitorSpawn = spawn.visitorSpawn();
        String mainSpawn = String.format("%.1f, %.1f, %.1f", defaultSpawn.x(), defaultSpawn.y(), defaultSpawn.z());
        
        ItemBuilder builder = ItemBuilder.of(Material.BEACON)
                .displayNameTranslated("items.island.spawn.current-info.name")
                .addLore(Component.empty())
                .addLore(LangManager.getComponent("gui.island.spawn.default-spawn", getViewerLocale(), Component.text(mainSpawn)))
                .addLore(LangManager.getComponent("gui.island.spawn.location-name", getViewerLocale(), Component.text(defaultSpawn.alias())))
                .addLore(Component.empty());
        
        if (visitorSpawn != null) {
            String visitorLoc = String.format("%.1f, %.1f, %.1f", visitorSpawn.x(), visitorSpawn.y(), visitorSpawn.z());
            builder.addLore(LangManager.getComponent("gui.island.spawn.visitor-spawn", getViewerLocale(), Component.text(visitorLoc)));
        } else {
            builder.addLore(LangManager.getComponent("gui.island.spawn.visitor-spawn-not-set", getViewerLocale()));
        }
        
        builder.addLore(Component.empty())
                .addLoreTranslated("items.island.spawn.current-info.lore");
        
        return builder.hideAllFlags().build();
    }
    
    private ItemStack createSetMainSpawnItem() {
        return ItemBuilder.of(Material.ENDER_PEARL)
                .displayNameTranslated("items.island.spawn.set-main.name")
                .loreTranslated("items.island.spawn.set-main.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createSetVisitorSpawnItem() {
        return ItemBuilder.of(Material.ENDER_EYE)
                .displayNameTranslated("items.island.spawn.set-visitor.name")
                .loreTranslated("items.island.spawn.set-visitor.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createResetSpawnItem() {
        return ItemBuilder.of(Material.BARRIER)
                .displayNameTranslated("items.island.spawn.reset.name")
                .loreTranslated("items.island.spawn.reset.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createSpawnProtectionItem() {
        return ItemBuilder.of(Material.SHIELD)
                .displayNameTranslated("items.island.spawn.protection.name")
                .loreTranslated("items.island.spawn.protection.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createPersonalSpawnItem() {
        return ItemBuilder.of(Material.ENDER_CHEST)
                .displayNameTranslated("items.island.spawn.personal.name")
                .loreTranslated("items.island.spawn.personal.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createSpawnMessageItem() {
        return ItemBuilder.of(Material.WRITABLE_BOOK)
                .displayNameTranslated("items.island.spawn.message.name")
                .loreTranslated("items.island.spawn.message.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createNoPermissionItem() {
        return ItemBuilder.of(Material.REDSTONE_BLOCK)
                .displayNameTranslated("items.island.spawn.no-permission.name")
                .loreTranslated("items.island.spawn.no-permission.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createBackButton() {
        return ItemBuilder.of(Material.ARROW)
                .displayNameTranslated("items.buttons.back.name")
                .loreTranslated("items.buttons.back.lore")
                .hideAllFlags()
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