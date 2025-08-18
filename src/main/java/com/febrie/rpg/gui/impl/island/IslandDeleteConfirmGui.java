package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.StandardItemBuilder;
import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
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
        super(viewer, guiManager, 27, "&4&l⚠ 섬 삭제 확인 ⚠");
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
        ItemStack redPane = StandardItemBuilder.guiItem(Material.RED_STAINED_GLASS_PANE)
                .displayName(UnifiedColorUtil.parseComponent("&c&l⚠ 주의 ⚠"))
                .hideTooltip(true)
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
                        player.sendMessage(UnifiedColorUtil.parse("&c정말로 삭제하시려면 다시 한 번 클릭하세요!"));
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
        return Component.text("섬 삭제 확인", UnifiedColorUtil.ERROR);
    }
    
    private ItemStack createIslandInfoItem() {
        return StandardItemBuilder.guiItem(Material.GRASS_BLOCK)
                .displayName(UnifiedColorUtil.parseComponent("&c&l삭제할 섬: " + island.core().islandName()))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7섬 ID: &f" + island.core().islandId()))
                .addLore(UnifiedColorUtil.parseComponent("&7멤버 수: &f" + (1 + island.membership().members().size()) + "명"))
                .addLore(UnifiedColorUtil.parseComponent("&7생성일: &f" + DateFormatUtil.formatDateOnlyFromMillis(island.core().createdAt())))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&c이 섬의 모든 데이터가 삭제됩니다!"))
                .build();
    }
    
    private ItemStack createCancelButton() {
        return StandardItemBuilder.guiItem(Material.EMERALD_BLOCK)
                .displayName(UnifiedColorUtil.parseComponent("&a&l취소"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&7섬 삭제를 취소하고"))
                .addLore(UnifiedColorUtil.parseComponent("&7설정 메뉴로 돌아갑니다"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&e▶ 클릭하여 취소"))
                .build();
    }
    
    private ItemStack createConfirmButton() {
        if (!confirmClicked) {
            return StandardItemBuilder.guiItem(Material.YELLOW_CONCRETE)
                    .displayName(UnifiedColorUtil.parseComponent("&e&l삭제 확인"))
                    .addLore(UnifiedColorUtil.parseComponent(""))
                    .addLore(UnifiedColorUtil.parseComponent("&7섬을 정말로 삭제하시겠습니까?"))
                    .addLore(UnifiedColorUtil.parseComponent(""))
                    .addLore(UnifiedColorUtil.parseComponent("&c⚠ 이 작업은 되돌릴 수 없습니다!"))
                    .addLore(UnifiedColorUtil.parseComponent(""))
                    .addLore(UnifiedColorUtil.parseComponent("&e▶ 클릭하여 계속"))
                    .build();
        } else {
            return StandardItemBuilder.guiItem(Material.RED_CONCRETE)
                    .displayName(UnifiedColorUtil.parseComponent("&c&l최종 삭제 확인"))
                    .addLore(UnifiedColorUtil.parseComponent(""))
                    .addLore(UnifiedColorUtil.parseComponent("&c⚠ 마지막 경고!"))
                    .addLore(UnifiedColorUtil.parseComponent("&c모든 섬 데이터가 영구 삭제됩니다!"))
                    .addLore(UnifiedColorUtil.parseComponent(""))
                    .addLore(UnifiedColorUtil.parseComponent("&4▶ 클릭하여 영구 삭제"))
                    .build();
        }
    }
    
    private ItemStack createWarningItem() {
        return StandardItemBuilder.guiItem(Material.BARRIER)
                .displayName(UnifiedColorUtil.parseComponent("&4&l삭제 시 사라지는 것들"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&c• 섬의 모든 블록과 건축물"))
                .addLore(UnifiedColorUtil.parseComponent("&c• 섬 업그레이드 및 설정"))
                .addLore(UnifiedColorUtil.parseComponent("&c• 모든 멤버의 섬 권한"))
                .addLore(UnifiedColorUtil.parseComponent("&c• 섬 기여도 및 통계"))
                .addLore(UnifiedColorUtil.parseComponent("&c• 섬 스폰 위치"))
                .addLore(UnifiedColorUtil.parseComponent(""))
                .addLore(UnifiedColorUtil.parseComponent("&4이 모든 것이 영구적으로 삭제됩니다!"))
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
                    
                    // "삭제"를 입력해야 삭제 진행
                    if (!"삭제".equals(input)) {
                        player.sendMessage(UnifiedColorUtil.parse("&c'삭제'를 정확히 입력해야 합니다."));
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }
                    
                    // 섬 삭제 진행
                    performIslandDeletion(player);
                    
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                })
                .text("'삭제' 입력")
                .title("섬을 삭제하려면 '삭제' 입력")
                .plugin(plugin)
                .open(player);
    }
    
    private void performIslandDeletion(Player player) {
        player.sendMessage(UnifiedColorUtil.parse("&c섬을 삭제하는 중..."));
        
        // 모든 멤버를 섬에서 제거
        for (var member : island.membership().members()) {
            Player memberPlayer = Bukkit.getPlayer(java.util.UUID.fromString(member.uuid()));
            if (memberPlayer != null && memberPlayer.isOnline()) {
                // 스폰으로 이동
                memberPlayer.teleport(memberPlayer.getWorld().getSpawnLocation());
                memberPlayer.sendMessage(UnifiedColorUtil.parse("&c섬이 삭제되어 스폰으로 이동되었습니다."));
            }
        }
        
        // 섬장도 스폰으로 이동
        player.teleport(player.getWorld().getSpawnLocation());
        
        // 섬 삭제
        islandManager.deleteIsland(island.core().islandId()).whenComplete((success, ex) -> {
            if (ex != null) {
                player.sendMessage(UnifiedColorUtil.parse("&c섬 삭제 중 오류가 발생했습니다: " + ex.getMessage()));
                return;
            }
            
            if (success) {
                player.sendMessage(UnifiedColorUtil.parse("&a섬이 성공적으로 삭제되었습니다."));
                
                // 플레이어의 섬 정보 제거
                plugin.getRPGPlayerManager().getPlayer(player).setIslandId(null);
            } else {
                player.sendMessage(UnifiedColorUtil.parse("&c섬 삭제에 실패했습니다."));
            }
        });
    }
}