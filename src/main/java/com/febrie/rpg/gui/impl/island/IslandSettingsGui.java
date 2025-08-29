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
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
        super(viewer, guiManager, 54, LangManager.getComponent("gui.island.settings.title".replace("-", "_"), viewer.locale()));
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
    
    private ItemStack createIslandInfoItem() {
        return ItemBuilder.of(Material.GRASS_BLOCK)
                .displayNameTranslated("items.island.settings.info.name")
                .addLore(Component.empty())
                .addLore(LangManager.getComponent("gui.island.settings.id", getViewerLocale(), Component.text(island.core().islandId())))
                .addLore(LangManager.getComponent("gui.island.settings.owner", getViewerLocale(), Component.text(island.core().ownerName())))
                .addLore(LangManager.getComponent("gui.island.settings.members", getViewerLocale(), Component.text((1 + island.membership().members().size()))))
                .addLore(LangManager.getComponent("gui.island.settings.status", getViewerLocale(), 
                        Component.translatable(island.core().isPublic() ? "status.public" : "status.private")))
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createNameChangeItem() {
        return ItemBuilder.of(Material.NAME_TAG)
                .displayNameTranslated("items.island.settings.name-change.name")
                .addLore(Component.empty())
                .addLore(LangManager.getComponent("gui.island.settings.current-name", getViewerLocale(), Component.text(tempIslandName)))
                .addLore(Component.empty())
                .addLoreTranslated("items.island.settings.name-change.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createPublicToggleItem() {
        return ItemBuilder.of(tempIsPublic ? Material.LIME_DYE : Material.GRAY_DYE)
                .displayNameTranslated(tempIsPublic ? "items.island.settings.public.name" : "items.island.settings.private.name")
                .addLore(Component.empty())
                .addLore(LangManager.getComponent("gui.island.settings.current-status", getViewerLocale(), 
                        Component.translatable(tempIsPublic ? "status.public" : "status.private")))
                .addLore(Component.empty())
                .addLoreTranslated("items.island.settings.public-toggle.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createBiomeChangeItem() {
        return ItemBuilder.of(Material.OAK_SAPLING)
                .displayNameTranslated("items.island.settings.biome-change.name")
                .addLore(Component.empty())
                .addLore(LangManager.getComponent("gui.island.settings.current-biome", getViewerLocale(), 
                        Component.text(getBiomeDisplayName(tempBiome))))
                .addLore(Component.empty())
                .addLoreTranslated("items.island.settings.biome-change.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createDeleteIslandItem() {
        return ItemBuilder.of(Material.BARRIER)
                .displayNameTranslated("items.island.settings.delete.name")
                .loreTranslated("items.island.settings.delete.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createSaveButton() {
        boolean hasChanges = !tempIslandName.equals(island.core().islandName()) ||
                           tempIsPublic != island.core().isPublic() ||
                           !tempBiome.equals(island.configuration().settings().biome());
        
        return ItemBuilder.of(Material.EMERALD_BLOCK)
                .displayNameTranslated("items.island.settings.save.name")
                .addLore(Component.empty())
                .addLore(Component.translatable(hasChanges ? "gui.island.settings.has-changes" : "gui.island.settings.no-changes"))
                .addLore(Component.empty())
                .addLoreTranslated("items.island.settings.save.lore")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createCancelButton() {
        return ItemBuilder.of(Material.REDSTONE_BLOCK)
                .displayNameTranslated("items.buttons.cancel.name")
                .loreTranslated("items.island.settings.cancel.lore")
                .hideAllFlags()
                .build();
    }
    
    
    private void handleNameChange(Player player) {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return java.util.Collections.emptyList();
                    }
                    
                    String text = stateSnapshot.getText();
                    // 유효성 검사
                    if (text.isEmpty() || text.length() > 20) {
                        player.sendMessage(LangManager.getComponent("island.settings.name-error", player.locale()).color(NamedTextColor.RED));
                        String errorText = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(
                                LangManager.getComponent("island.settings.name-input-error", player.locale()));
                        return java.util.Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(errorText));
                    }
                    
                    // 색상 코드 제거 (Paper API)
                    tempIslandName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                            .serialize(net.kyori.adventure.text.Component.text(text));
                    
                    return java.util.Collections.singletonList(AnvilGUI.ResponseAction.close());
                })
                .onClose(closePlayer -> {
                    // AnvilGUI가 닫힌 후 다시 열기
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        IslandSettingsGui.create(plugin, viewer, island).open(viewer);
                    }, 1L);
                })
                .text(tempIslandName)
                .title(net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(
                        LangManager.getComponent("island.gui.creation.island-name-input-title", player.locale())))
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
            IslandCoreDTO updatedCore = GuiHandlerUtil.createUpdatedCore(island.core(),
                    tempIslandName, // new name
                    null, // isPublic unchanged
                    null  // size unchanged
            );
            IslandDTO updated = new IslandDTO(updatedCore, island.membership(), island.social(), island.configuration());
            islandManager.updateIsland(updated);
            hasChanges = true;
        }
        
        // 공개/비공개 변경
        if (tempIsPublic != island.core().isPublic()) {
            IslandCoreDTO updatedCore = GuiHandlerUtil.createUpdatedCore(island.core(),
                    null, // name unchanged
                    tempIsPublic, // new isPublic
                    null  // size unchanged
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