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
        super(viewer, guiManager, 27, Component.translatable("gui.island.delete-confirm.title"));
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
        ItemStack redPane = ItemBuilder.of(Material.RED_STAINED_GLASS_PANE, getViewerLocale())
                .displayNameTranslated("gui.island.delete-confirm.warning")
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
                        sendMessage(player, "gui.island.delete-confirm.click-again");
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
        return Component.translatable("gui.island.delete-confirm.title");
    }
    
    private ItemStack createIslandInfoItem() {
        return ItemBuilder.of(Material.GRASS_BLOCK, getViewerLocale())
                .displayName(LangManager.get("gui.island.delete-confirm.island-info.title", viewer, Component.text(island.core().islandName())))
                .addLore(Component.empty())
                .addLore(LangManager.get("gui.island.delete-confirm.island-info.id", viewer, Component.text(island.core().islandId())))
                .addLore(LangManager.get("gui.island.delete-confirm.island-info.members", viewer, Component.text(String.valueOf(1 + island.membership().members().size()))))
                .addLore(LangManager.get("gui.island.delete-confirm.island-info.created", viewer, Component.text(DateFormatUtil.formatDateOnlyFromMillis(island.core().createdAt()))))
                .addLore(Component.empty())
                .addLoreTranslated("gui.island.delete-confirm.island-info.warning")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createCancelButton() {
        return ItemBuilder.of(Material.EMERALD_BLOCK, getViewerLocale())
                .displayNameTranslated("gui.island.delete-confirm.cancel.title")
                .addLore(Component.empty())
                .addLoreTranslated("gui.island.delete-confirm.cancel.lore1")
                .addLoreTranslated("gui.island.delete-confirm.cancel.lore2")
                .addLore(Component.empty())
                .addLoreTranslated("gui.island.delete-confirm.cancel.click")
                .hideAllFlags()
                .build();
    }
    
    private ItemStack createConfirmButton() {
        if (!confirmClicked) {
            return ItemBuilder.of(Material.YELLOW_CONCRETE, getViewerLocale())
                    .displayNameTranslated("gui.island.delete-confirm.confirm.title")
                    .addLore(Component.empty())
                    .addLoreTranslated("gui.island.delete-confirm.confirm.question")
                    .addLore(Component.empty())
                    .addLoreTranslated("gui.island.delete-confirm.confirm.warning")
                    .addLore(Component.empty())
                    .addLoreTranslated("gui.island.delete-confirm.confirm.click")
                    .hideAllFlags()
                    .build();
        } else {
            return ItemBuilder.of(Material.RED_CONCRETE, getViewerLocale())
                    .displayNameTranslated("gui.island.delete-confirm.final-confirm.title")
                    .addLore(Component.empty())
                    .addLoreTranslated("gui.island.delete-confirm.final-confirm.warning1")
                    .addLoreTranslated("gui.island.delete-confirm.final-confirm.warning2")
                    .addLore(Component.empty())
                    .addLoreTranslated("gui.island.delete-confirm.final-confirm.click")
                    .hideAllFlags()
                    .build();
        }
    }
    
    private ItemStack createWarningItem() {
        return ItemBuilder.of(Material.BARRIER, getViewerLocale())
                .displayNameTranslated("gui.island.delete-confirm.warning-list.title")
                .addLore(Component.empty())
                .addLoreTranslated("gui.island.delete-confirm.warning-list.item1")
                .addLoreTranslated("gui.island.delete-confirm.warning-list.item2")
                .addLoreTranslated("gui.island.delete-confirm.warning-list.item3")
                .addLoreTranslated("gui.island.delete-confirm.warning-list.item4")
                .addLoreTranslated("gui.island.delete-confirm.warning-list.item5")
                .addLore(Component.empty())
                .addLoreTranslated("gui.island.delete-confirm.warning-list.final")
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
                    String confirmWord = LangManager.getString("island.delete.confirm-word", player);
                    if (!confirmWord.equals(input)) {
                        player.sendMessage(LangManager.get("island.delete.input-error", player).color(NamedTextColor.RED));
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }
                    
                    // 섬 삭제 진행
                    performIslandDeletion(player);
                    
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                })
                .text(LangManager.getString("island.delete.input-text", player))
                .title(LangManager.getString("island.delete.input-title", player))
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