package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.dto.island.IslandSettingsDTO;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.GuiHandlerUtil;
import com.febrie.rpg.util.ItemBuilder;
import net.kyori.adventure.text.Component;
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
    private final IslandDTO island;
    private final boolean isOwner;
    
    // 변경된 설정 임시 저장
    private String tempIslandName;
    private boolean tempIsPublic;
    private String tempBiome;
    
    private IslandSettingsGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                             @NotNull RPGMain plugin, @NotNull IslandDTO island) {
        super(viewer, guiManager, 54, "&9&l섬 설정");
        this.islandManager = plugin.getIslandManager();
        this.island = island;
        this.isOwner = island.core().ownerUuid().equals(viewer.getUniqueId().toString());
        
        // 현재 설정 로드
        this.tempIslandName = island.core().islandName();
        this.tempIsPublic = island.core().isPublic();
        this.tempBiome = island.configuration().settings().biome();
    }
    
    /**
     * Factory method to create and open the settings GUI
     */
    public static IslandSettingsGui create(@NotNull RPGMain plugin, @NotNull Player viewer, 
                                          @NotNull IslandDTO island) {
        return new IslandSettingsGui(viewer, plugin.getGuiManager(), plugin, island);
    }
    
    @Override
    protected void setupLayout() {
        fillBorder(Material.BLUE_STAINED_GLASS_PANE);
        
        // 섬 정보
        setItem(4, new GuiItem(createIslandInfoItem()));
        
        // 설정 옵션들
        setItem(20, new GuiItem(createNameChangeItem()).onAnyClick(this::handleNameChange));
        setItem(22, new GuiItem(createPublicToggleItem()).onAnyClick(this::handlePublicToggle));
        setItem(24, new GuiItem(createBiomeChangeItem()).onAnyClick(this::handleBiomeChange));
        
        // 섬 삭제 (섬장만)
        if (isOwner) {
            setItem(40, new GuiItem(createDeleteIslandItem()).onAnyClick(this::handleIslandDelete));
        }
        
        // 저장 및 취소
        setItem(48, new GuiItem(createCancelButton()).onAnyClick(player -> {
            player.closeInventory();
            IslandMainGui.create(plugin.getGuiManager(), viewer).open(viewer);
        }));
        setItem(50, new GuiItem(createSaveButton()).onAnyClick(this::handleSave));
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        return null; // Use back button with direct navigation
    }
    
    @Override
    public @NotNull Component getTitle() {
        return Component.text("섬 설정", UnifiedColorUtil.PRIMARY);
    }
    
    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
    
    private ItemStack createIslandInfoItem() {
        return new ItemBuilder(Material.GRASS_BLOCK)
                .displayName(UnifiedColorUtil.parseComponent("&6&l" + island.core().islandName()))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7섬 ID: &f" + island.core().islandId()))
                .addLore(UnifiedColorUtil.parseComponent("&7섬장: &f" + island.core().ownerName()))
                .addLore(UnifiedColorUtil.parseComponent("&7멤버 수: &f" + (1 + island.membership().members().size()) + "명"))
                .addLore(UnifiedColorUtil.parseComponent("&7상태: " + (island.core().isPublic() ? "&a공개" : "&c비공개")))
                .build();
    }
    
    private ItemStack createNameChangeItem() {
        return new ItemBuilder(Material.NAME_TAG)
                .displayName(UnifiedColorUtil.parseComponent("&e&l섬 이름 변경"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7현재 이름: &f" + tempIslandName))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7섬 이름을 변경합니다"))
                .addLore(UnifiedColorUtil.parseComponent("&7최대 20자까지 가능합니다"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&e▶ 클릭하여 변경"))
                .build();
    }
    
    private ItemStack createPublicToggleItem() {
        return new ItemBuilder(tempIsPublic ? Material.LIME_DYE : Material.GRAY_DYE)
                .displayName(UnifiedColorUtil.parseComponent(tempIsPublic ? "&a&l공개 섬" : "&c&l비공개 섬"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7현재 상태: " + (tempIsPublic ? "&a공개" : "&c비공개")))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7공개 섬: 모든 플레이어가 방문 가능"))
                .addLore(UnifiedColorUtil.parseComponent("&7비공개 섬: 초대받은 플레이어만 방문 가능"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&e▶ 클릭하여 전환"))
                .build();
    }
    
    private ItemStack createBiomeChangeItem() {
        return new ItemBuilder(Material.OAK_SAPLING)
                .displayName(UnifiedColorUtil.parseComponent("&b&l바이옴 변경"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7현재 바이옴: &f" + getBiomeDisplayName(tempBiome)))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7섬의 바이옴을 변경합니다"))
                .addLore(UnifiedColorUtil.parseComponent("&7날씨와 환경이 변경됩니다"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&e▶ 클릭하여 변경"))
                .build();
    }
    
    private ItemStack createDeleteIslandItem() {
        return new ItemBuilder(Material.BARRIER)
                .displayName(UnifiedColorUtil.parseComponent("&c&l섬 삭제"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&c⚠ 주의: 이 작업은 되돌릴 수 없습니다!"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7섬과 모든 데이터가 영구적으로"))
                .addLore(UnifiedColorUtil.parseComponent("&7삭제됩니다"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&c▶ 클릭하여 삭제"))
                .build();
    }
    
    private ItemStack createSaveButton() {
        boolean hasChanges = !tempIslandName.equals(island.core().islandName()) ||
                           tempIsPublic != island.core().isPublic() ||
                           !tempBiome.equals(island.configuration().settings().biome());
        
        return new ItemBuilder(Material.EMERALD_BLOCK)
                .displayName(UnifiedColorUtil.parseComponent("&a&l설정 저장"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent(hasChanges ? "&7변경사항이 있습니다" : "&7변경사항이 없습니다"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&e▶ 클릭하여 저장"))
                .build();
    }
    
    private ItemStack createCancelButton() {
        return new ItemBuilder(Material.REDSTONE_BLOCK)
                .displayName(UnifiedColorUtil.parseComponent("&c&l취소"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7변경사항을 저장하지 않고"))
                .addLore(UnifiedColorUtil.parseComponent("&7돌아갑니다"))
                .build();
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
                        player.sendMessage(UnifiedColorUtil.parse("&c섬 이름은 1~20자여야 합니다."));
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
        player.sendMessage(UnifiedColorUtil.parse("&a섬을 " + (tempIsPublic ? "공개" : "비공개") + "로 설정했습니다."));
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
        if (!tempIslandName.equals(island.core().islandName())) {
            IslandCoreDTO updatedCore = new IslandCoreDTO(
                    island.core().islandId(),
                    island.core().ownerUuid(),
                    island.core().ownerName(),
                    tempIslandName,
                    island.core().size(),
                    island.core().isPublic(),
                    island.core().createdAt(),
                    System.currentTimeMillis(),
                    island.core().totalResets(),
                    island.core().deletionScheduledAt(),
                    island.core().location()
            );
            IslandDTO updated = new IslandDTO(updatedCore, island.membership(), island.social(), island.configuration());
            islandManager.updateIsland(updated);
            hasChanges = true;
        }
        
        // 공개/비공개 변경
        if (tempIsPublic != island.core().isPublic()) {
            IslandCoreDTO updatedCore = new IslandCoreDTO(
                    island.core().islandId(),
                    island.core().ownerUuid(),
                    island.core().ownerName(),
                    island.core().islandName(),
                    island.core().size(),
                    tempIsPublic,
                    island.core().createdAt(),
                    System.currentTimeMillis(),
                    island.core().totalResets(),
                    island.core().deletionScheduledAt(),
                    island.core().location()
            );
            IslandDTO updated = new IslandDTO(updatedCore, island.membership(), island.social(), island.configuration());
            islandManager.updateIsland(updated);
            hasChanges = true;
        }
        
        // 바이옴 변경
        if (!tempBiome.equals(island.configuration().settings().biome())) {
            IslandSettingsDTO newSettings = new IslandSettingsDTO(
                    island.configuration().settings().nameColorHex(),
                    tempBiome,
                    island.configuration().settings().template()
            );
            
            IslandDTO updated = GuiHandlerUtil.updateIslandSettings(island, newSettings);
            islandManager.updateIsland(updated);
            hasChanges = true;
        }
        
        if (hasChanges) {
            player.sendMessage(UnifiedColorUtil.parse("&a섬 설정이 저장되었습니다!"));
        } else {
            player.sendMessage(UnifiedColorUtil.parse("&e변경사항이 없습니다."));
        }
        
        player.closeInventory();
        IslandMainGui.create(plugin.getGuiManager(), viewer).open(viewer);
    }
    
    private String getBiomeDisplayName(String biome) {
        return GuiHandlerUtil.getBiomeDisplayName(biome);
    }
}