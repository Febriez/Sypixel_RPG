package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandSettingsDTO;
import com.febrie.rpg.gui.BaseGui;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.GuiHandlerUtil;
import com.febrie.rpg.util.ItemBuilder;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;

/**
 * 섬 설정 GUI
 * 섬 이름 변경, 공개/비공개 설정, 바이옴 변경 등의 기능 제공
 *
 * @author Febrie, CoffeeTory
 */
public class IslandSettingsGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final Player viewer;
    private final IslandDTO island;
    private final boolean isOwner;
    
    // 변경된 설정 임시 저장
    private String tempIslandName;
    private boolean tempIsPublic;
    private String tempBiome;
    
    private IslandSettingsGui(@NotNull RPGMain plugin, @NotNull Player viewer, 
                             @NotNull IslandDTO island) {
        super(plugin, 54);
        this.islandManager = plugin.getIslandManager();
        this.viewer = viewer;
        this.island = island;
        this.isOwner = island.ownerUuid().equals(viewer.getUniqueId().toString());
        
        // 현재 설정 로드
        this.tempIslandName = island.islandName();
        this.tempIsPublic = island.isPublic();
        this.tempBiome = island.settings().biome();
    }
    
    /**
     * Factory method to create and open the settings GUI
     */
    public static IslandSettingsGui create(@NotNull RPGMain plugin, @NotNull Player viewer, 
                                          @NotNull IslandDTO island) {
        IslandSettingsGui gui = new IslandSettingsGui(plugin, viewer, island);
        return BaseGui.create(gui, ColorUtil.parseComponent("&9&l섬 설정"));
    }
    
    @Override
    protected void setupItems() {
        fillBorder(Material.BLUE_STAINED_GLASS_PANE);
        
        // 섬 정보
        setItem(4, createIslandInfoItem());
        
        // 설정 옵션들
        setItem(20, createNameChangeItem());
        setItem(22, createPublicToggleItem());
        setItem(24, createBiomeChangeItem());
        
        // 섬 삭제 (섬장만)
        if (isOwner) {
            setItem(40, createDeleteIslandItem());
        }
        
        // 저장 및 취소
        setItem(48, createCancelButton());
        setItem(50, createSaveButton());
    }
    
    private ItemStack createIslandInfoItem() {
        return new ItemBuilder(Material.GRASS_BLOCK)
                .displayName(ColorUtil.parseComponent("&6&l" + island.islandName()))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7섬 ID: &f" + island.islandId()))
                .addLore(ColorUtil.parseComponent("&7섬장: &f" + island.ownerName()))
                .addLore(ColorUtil.parseComponent("&7멤버 수: &f" + (1 + island.members().size()) + "명"))
                .addLore(ColorUtil.parseComponent("&7상태: " + (island.isPublic() ? "&a공개" : "&c비공개")))
                .build();
    }
    
    private ItemStack createNameChangeItem() {
        return new ItemBuilder(Material.NAME_TAG)
                .displayName(ColorUtil.parseComponent("&e&l섬 이름 변경"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7현재 이름: &f" + tempIslandName))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7섬 이름을 변경합니다"))
                .addLore(ColorUtil.parseComponent("&7최대 20자까지 가능합니다"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&e▶ 클릭하여 변경"))
                .build();
    }
    
    private ItemStack createPublicToggleItem() {
        return new ItemBuilder(tempIsPublic ? Material.LIME_DYE : Material.GRAY_DYE)
                .displayName(ColorUtil.parseComponent(tempIsPublic ? "&a&l공개 섬" : "&c&l비공개 섬"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7현재 상태: " + (tempIsPublic ? "&a공개" : "&c비공개")))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7공개 섬: 모든 플레이어가 방문 가능"))
                .addLore(ColorUtil.parseComponent("&7비공개 섬: 초대받은 플레이어만 방문 가능"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&e▶ 클릭하여 전환"))
                .build();
    }
    
    private ItemStack createBiomeChangeItem() {
        return new ItemBuilder(Material.OAK_SAPLING)
                .displayName(ColorUtil.parseComponent("&b&l바이옴 변경"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7현재 바이옴: &f" + getBiomeDisplayName(tempBiome)))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7섬의 바이옴을 변경합니다"))
                .addLore(ColorUtil.parseComponent("&7날씨와 환경이 변경됩니다"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&e▶ 클릭하여 변경"))
                .build();
    }
    
    private ItemStack createDeleteIslandItem() {
        return new ItemBuilder(Material.BARRIER)
                .displayName(ColorUtil.parseComponent("&c&l섬 삭제"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&c⚠ 주의: 이 작업은 되돌릴 수 없습니다!"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7섬과 모든 데이터가 영구적으로"))
                .addLore(ColorUtil.parseComponent("&7삭제됩니다"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&c▶ 클릭하여 삭제"))
                .build();
    }
    
    private ItemStack createSaveButton() {
        boolean hasChanges = !tempIslandName.equals(island.islandName()) ||
                           tempIsPublic != island.isPublic() ||
                           !tempBiome.equals(island.settings().biome());
        
        return new ItemBuilder(Material.EMERALD_BLOCK)
                .displayName(ColorUtil.parseComponent("&a&l설정 저장"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent(hasChanges ? "&7변경사항이 있습니다" : "&7변경사항이 없습니다"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&e▶ 클릭하여 저장"))
                .build();
    }
    
    private ItemStack createCancelButton() {
        return new ItemBuilder(Material.REDSTONE_BLOCK)
                .displayName(ColorUtil.parseComponent("&c&l취소"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7변경사항을 저장하지 않고"))
                .addLore(ColorUtil.parseComponent("&7돌아갑니다"))
                .build();
    }
    
    @Override
    protected void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        int slot = event.getSlot();
        
        switch (slot) {
            case 20 -> handleNameChange(player);
            case 22 -> handlePublicToggle(player);
            case 24 -> handleBiomeChange(player);
            case 40 -> {
                if (isOwner) {
                    handleIslandDelete(player);
                }
            }
            case 48 -> {
                player.closeInventory();
                IslandMainGui.create(plugin.getGuiManager(), viewer).open(viewer);
            }
            case 50 -> handleSave(player);
        }
    }
    
    private void handleNameChange(Player player) {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    
                    String newName = stateSnapshot.getText();
                    
                    // 유효성 검사
                    if (newName.isEmpty() || newName.length() > 20) {
                        player.sendMessage(ColorUtil.colorize("&c섬 이름은 1~20자여야 합니다."));
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }
                    
                    // 색상 코드 제거 (Paper API)
                    newName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                            .serialize(net.kyori.adventure.text.Component.text(newName));
                    
                    tempIslandName = newName;
                    
                    // GUI 다시 열기
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        IslandSettingsGui.create(plugin, viewer, island).open(viewer);
                    });
                    
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                })
                .text(tempIslandName)
                .title("새로운 섬 이름 입력")
                .plugin(plugin)
                .open(player);
    }
    
    private void handlePublicToggle(Player player) {
        tempIsPublic = !tempIsPublic;
        refresh();
        player.sendMessage(ColorUtil.colorize("&a섬을 " + (tempIsPublic ? "공개" : "비공개") + "로 설정했습니다."));
    }
    
    private void handleBiomeChange(Player player) {
        player.closeInventory();
        // 간단한 바이옴 선택 GUI 구현
        IslandBiomeSimpleGui.create(plugin, viewer, island, tempBiome).open(viewer);
    }
    
    private void handleIslandDelete(Player player) {
        // 섬 삭제 확인 GUI
        player.closeInventory();
        IslandDeleteConfirmGui.create(plugin, viewer, island).open(viewer);
    }
    
    private void handleSave(Player player) {
        boolean hasChanges = false;
        
        // 이름 변경
        if (!tempIslandName.equals(island.islandName())) {
            IslandDTO updated = new IslandDTO(
                    island.islandId(),
                    island.ownerUuid(),
                    island.ownerName(),
                    tempIslandName,
                    island.size(),
                    island.isPublic(),
                    island.createdAt(),
                    System.currentTimeMillis(),
                    island.members(),
                    island.workers(),
                    island.contributions(),
                    island.spawnData(),
                    island.upgradeData(),
                    island.permissions(),
                    island.pendingInvites(),
                    island.recentVisits(),
                    island.totalResets(),
                    island.deletionScheduledAt(),
                    island.settings()
            );
            islandManager.updateIsland(updated);
            hasChanges = true;
        }
        
        // 공개/비공개 변경
        if (tempIsPublic != island.isPublic()) {
            IslandDTO updated = new IslandDTO(
                    island.islandId(),
                    island.ownerUuid(),
                    island.ownerName(),
                    island.islandName(),
                    island.size(),
                    tempIsPublic,
                    island.createdAt(),
                    System.currentTimeMillis(),
                    island.members(),
                    island.workers(),
                    island.contributions(),
                    island.spawnData(),
                    island.upgradeData(),
                    island.permissions(),
                    island.pendingInvites(),
                    island.recentVisits(),
                    island.totalResets(),
                    island.deletionScheduledAt(),
                    island.settings()
            );
            islandManager.updateIsland(updated);
            hasChanges = true;
        }
        
        // 바이옴 변경
        if (!tempBiome.equals(island.settings().biome())) {
            IslandSettingsDTO newSettings = new IslandSettingsDTO(
                    island.settings().nameColorHex(),
                    tempBiome,
                    island.settings().template()
            );
            
            IslandDTO updated = GuiHandlerUtil.updateIslandSettings(island, newSettings);
            islandManager.updateIsland(updated);
            hasChanges = true;
        }
        
        if (hasChanges) {
            player.sendMessage(ColorUtil.colorize("&a섬 설정이 저장되었습니다!"));
        } else {
            player.sendMessage(ColorUtil.colorize("&e변경사항이 없습니다."));
        }
        
        player.closeInventory();
        IslandMainGui.create(plugin.getGuiManager(), viewer).open(viewer);
    }
    
    private String getBiomeDisplayName(String biome) {
        return GuiHandlerUtil.getBiomeDisplayName(biome);
    }
}