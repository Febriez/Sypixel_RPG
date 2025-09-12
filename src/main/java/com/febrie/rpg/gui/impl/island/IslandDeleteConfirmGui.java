package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.febrie.rpg.util.DateFormatUtil;

/**
 * 섬 삭제 확인 GUI
 * 섬을 삭제하기 전 최종 확인
 *
 * @author Febrie, CoffeeTory
 */
public class IslandDeleteConfirmGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final IslandDTO island;
    private boolean confirmClicked = false;
    
    private IslandDeleteConfirmGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                                  @NotNull RPGMain plugin, @NotNull IslandDTO island) {
        super(viewer, guiManager, 27, LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_TITLE, viewer.locale()));
        this.islandManager = plugin.getIslandManager();
        this.island = island;
    }
    
    /**
     * Factory method to create and open the delete confirmation GUI
     */
    public static IslandDeleteConfirmGui create(@NotNull RPGMain plugin, @NotNull Player viewer,
                                               @NotNull IslandDTO island) {
        return new IslandDeleteConfirmGui(viewer, plugin.getGuiManager(), plugin, island);
    }
    
    @Override
    protected void setupLayout() {
        // 배경을 빨간색 유리판으로 채우기
        ItemStack redPane = ItemBuilder.of(Material.RED_STAINED_GLASS_PANE)
                .displayName(LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_WARNING, getViewerLocale()))
                .hideTooltip(true)
                .hideAllFlags()
                .build();
        
        for (int i = 0; i < getSize(); i++) {
            setItem(i, new GuiItem(redPane));
        }
        
        // 섬 정보
        setItem(4, new GuiItem(createIslandInfoItem()));
        
        // 취소 버튼
        setItem(11, new GuiItem(createCancelButton())
                .onAnyClick(player -> {
                    player.closeInventory();
                    IslandSettingsGui.create(plugin, viewer, island).open(viewer);
                }));
        
        // 확인 버튼 (첫 번째 단계)
        setItem(15, new GuiItem(createConfirmButton())
                .onAnyClick(player -> {
                    if (!confirmClicked) {
                        confirmClicked = true;
                        setupLayout();
                        player.sendMessage(LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_CLICK_AGAIN, player.locale()));
                    } else {
                        handleFinalDeleteConfirmation(player);
                    }
                }));
        
        // 경고 메시지
        setItem(22, new GuiItem(createWarningItem()));
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        return null; // Use back button with direct navigation
    }
    
    @Override
    public @NotNull Component getTitle() {
        return LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_TITLE, viewer.locale());
    }
    
    private ItemStack createIslandInfoItem() {
        return ItemBuilder.of(Material.GRASS_BLOCK)
                .displayName(LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_ISLAND_INFO_TITLE, getViewerLocale(), Component.text(island.core().islandName())))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_ISLAND_INFO_ID, getViewerLocale(), Component.text(island.core().islandId())))
                .addLore(LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_ISLAND_INFO_MEMBERS, getViewerLocale(), Component.text(String.valueOf(1 + island.membership().members().size()))))
                .addLore(LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_ISLAND_INFO_CREATED, getViewerLocale(), Component.text(DateFormatUtil.formatDateOnlyFromMillis(island.core().createdAt()))))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_ISLAND_INFO_WARNING, getViewerLocale()))
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createCancelButton() {
        return ItemBuilder.of(Material.EMERALD_BLOCK)
                .displayName(LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_CANCEL_TITLE, getViewerLocale()))
                .addLore(Component.empty())
                .addLore(LangManager.list(LangKey.GUI_ISLAND_DELETE_CONFIRM_CANCEL_LORE, getViewerLocale()))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_CANCEL_CLICK, getViewerLocale()))
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createConfirmButton() {
        if (!confirmClicked) {
            return ItemBuilder.of(Material.YELLOW_CONCRETE)
                    .displayName(LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_CONFIRM_TITLE, getViewerLocale()))
                    .addLore(Component.empty())
                    .addLore(LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_CONFIRM_QUESTION, getViewerLocale()))
                    .addLore(Component.empty())
                    .addLore(LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_CONFIRM_WARNING, getViewerLocale()))
                    .addLore(Component.empty())
                    .addLore(LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_CONFIRM_CLICK, getViewerLocale()))
                    .hideAllFlags()
                    .build();
        } else {
            return ItemBuilder.of(Material.RED_CONCRETE)
                    .displayName(LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_FINAL_CONFIRM_TITLE, getViewerLocale()))
                    .addLore(Component.empty())
                    .addLore(LangManager.list(LangKey.GUI_ISLAND_DELETE_CONFIRM_FINAL_CONFIRM_WARNING, getViewerLocale()))
                    .addLore(Component.empty())
                    .addLore(LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_FINAL_CONFIRM_CLICK, getViewerLocale()))
                    .hideAllFlags()
                    .build();
        }
    }
    
    private ItemStack createWarningItem() {
        return ItemBuilder.of(Material.BARRIER)
                .displayName(LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_WARNING_LIST_TITLE, getViewerLocale()))
                .addLore(Component.empty())
                .addLore(LangManager.list(LangKey.GUI_ISLAND_DELETE_CONFIRM_WARNING_LIST_ITEMS, getViewerLocale()))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_ISLAND_DELETE_CONFIRM_WARNING_LIST_FINAL, getViewerLocale()))
                .hideAllFlags()
                .build();
    }
    
    private void handleFinalDeleteConfirmation(Player player) {
        // AnvilGUI로 최종 확인
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    
                    String input = stateSnapshot.getText();
                    
                    // 삭제 확인 단어 체크
                    Component confirmComponent = LangManager.text(LangKey.ISLAND_DELETE_CONFIRM_WORD, player);
                    String confirmWord = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(confirmComponent);
                    if (!confirmWord.equals(input)) {
                        player.sendMessage(LangManager.text(LangKey.ISLAND_DELETE_INPUT_ERROR, player).color(NamedTextColor.RED));
                        return List.of(AnvilGUI.ResponseAction.close());
                    }
                    
                    // 섬 삭제 진행
                    performIslandDeletion(player);
                    
                    return List.of(AnvilGUI.ResponseAction.close());
                })
                .text(net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(
                        LangManager.text(LangKey.ISLAND_DELETE_INPUT_TEXT, player)))
                .title(net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(
                        LangManager.text(LangKey.ISLAND_DELETE_INPUT_TITLE, player)))
                .plugin(plugin)
                .open(player);
    }
    
    private void performIslandDeletion(Player player) {
        sendMessage(player, "gui.island.delete-confirm.deleting");
        
        // 모든 멤버를 섬에서 제거
        for (var member : island.membership().members()) {
            Player memberPlayer = Bukkit.getPlayer(java.util.UUID.fromString(member.uuid()));
            if (memberPlayer != null && memberPlayer.isOnline()) {
                // 스폰으로 이동
                memberPlayer.teleport(memberPlayer.getWorld().getSpawnLocation());
                sendMessage(memberPlayer, "gui.island.delete-confirm.member-teleported");
            }
        }
        
        // 섬장도 스폰으로 이동
        player.teleport(player.getWorld().getSpawnLocation());
        
        // 섬 삭제
        islandManager.deleteIsland(island.core().islandId()).whenComplete((success, ex) -> {
            if (ex != null) {
                sendMessage(player, "gui.island.delete-confirm.error", "error", ex.getMessage());
                return;
            }
            
            if (success) {
                sendMessage(player, "gui.island.delete-confirm.success");
                
                // 플레이어의 섬 정보 제거
                var rpgPlayer = plugin.getRPGPlayerManager().getPlayer(player);
                if (rpgPlayer != null) {
                    rpgPlayer.setIslandId(null);
                }
            } else {
                sendMessage(player, "gui.island.delete-confirm.failed");
            }
        });
    }
}