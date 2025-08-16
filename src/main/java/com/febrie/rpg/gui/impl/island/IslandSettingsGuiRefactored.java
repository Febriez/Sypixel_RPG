package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandSettingsDTO;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;

/**
 * 섬 설정 GUI (Framework 버전)
 * 섬 이름 변경, 공개/비공개 설정, 바이옴 변경 등의 기능 제공
 *
 * @author Febrie, CoffeeTory
 */
public class IslandSettingsGuiRefactored extends BaseGui {
    
    private final IslandManager islandManager;
    private final IslandDTO island;
    private final boolean isOwner;
    
    // 변경된 설정 임시 저장
    private String tempIslandName;
    private boolean tempIsPublic;
    private String tempBiome;
    
    private IslandSettingsGuiRefactored(@NotNull Player viewer, @NotNull GuiManager guiManager,
                                         @NotNull IslandDTO island) {
        super(viewer, guiManager, 54, "gui.island.settings.title");
        this.islandManager = plugin.getIslandManager();
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
    public static IslandSettingsGuiRefactored create(@NotNull GuiManager guiManager, @NotNull Player viewer, 
                                                      @NotNull IslandDTO island) {
        IslandSettingsGuiRefactored gui = new IslandSettingsGuiRefactored(viewer, guiManager, island);
        gui.initialize("gui.island.settings.title");
        return gui;
    }
    
    @Override
    protected void setupLayout() {
        // 배경 설정
        fillBorder(Material.BLACK_STAINED_GLASS_PANE);
        
        // 섬 정보 표시
        setItem(4, createIslandInfoItem());
        
        // 섬 이름 변경
        if (isOwner) {
            setItem(20, createNameChangeItem());
        }
        
        // 공개/비공개 설정
        setItem(22, createPublicToggleItem());
        
        // 바이옴 변경
        if (isOwner) {
            setItem(24, createBiomeChangeItem());
        }
        
        // 권한 설정
        if (isOwner) {
            setItem(30, createPermissionItem());
        }
        
        // 스폰 위치 설정
        setItem(32, createSpawnSettingItem());
        
        // 섬 삭제
        if (isOwner) {
            setItem(40, createDeleteItem());
        }
        
        // 네비게이션
        setupStandardNavigation(false, true);
    }
    
    private GuiItem createIslandInfoItem() {
        return GuiItem.builder()
            .item(ItemBuilder.of(Material.GRASS_BLOCK)
                .displayName(ColorUtil.parseComponent("&b" + tempIslandName))
                .addLore(ColorUtil.parseComponent("&7크기: &e" + island.size() + "x" + island.size()))
                .addLore(ColorUtil.parseComponent("&7레벨: &e" + island.level()))
                .addLore(ColorUtil.parseComponent("&7바이옴: &e" + tempBiome))
                .addLore(ColorUtil.parseComponent("&7공개 여부: " + (tempIsPublic ? "&a공개" : "&c비공개")))
                .build())
            .build();
    }
    
    private GuiItem createNameChangeItem() {
        return GuiItem.builder()
            .item(ItemBuilder.of(Material.NAME_TAG)
                .displayName(ColorUtil.parseComponent("&e섬 이름 변경"))
                .addLore(ColorUtil.parseComponent("&7현재 이름: &f" + tempIslandName))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&a클릭하여 변경"))
                .build())
            .onClick((player, click) -> {
                new AnvilGUI.Builder()
                    .onComplete((p, text) -> {
                        if (text.length() > 20) {
                            p.sendMessage(ColorUtil.colorize("&c섬 이름은 20자 이하로 설정해주세요."));
                            return AnvilGUI.Response.text("너무 깁니다");
                        }
                        
                        tempIslandName = text;
                        p.sendMessage(ColorUtil.colorize("&a섬 이름을 '" + text + "'로 변경했습니다."));
                        
                        // 설정 저장
                        saveSettings();
                        
                        // GUI 새로고침
                        create(guiManager, viewer, island).open(viewer);
                        return AnvilGUI.Response.close();
                    })
                    .text(tempIslandName)
                    .title("새 섬 이름 입력")
                    .plugin(plugin)
                    .open(player);
            })
            .build();
    }
    
    private GuiItem createPublicToggleItem() {
        Material material = tempIsPublic ? Material.LIME_DYE : Material.GRAY_DYE;
        String status = tempIsPublic ? "&a공개" : "&c비공개";
        
        return GuiItem.builder()
            .item(ItemBuilder.of(material)
                .displayName(ColorUtil.parseComponent("&e공개 설정"))
                .addLore(ColorUtil.parseComponent("&7현재 상태: " + status))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7공개 섬은 다른 플레이어가"))
                .addLore(ColorUtil.parseComponent("&7방문할 수 있습니다."))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&a클릭하여 전환"))
                .build())
            .onClick((player, click) -> {
                tempIsPublic = !tempIsPublic;
                player.sendMessage(ColorUtil.colorize("&a섬을 " + (tempIsPublic ? "공개" : "비공개") + "로 설정했습니다."));
                
                // 설정 저장
                saveSettings();
                
                // GUI 새로고침
                refresh();
            })
            .build();
    }
    
    private GuiItem createBiomeChangeItem() {
        return GuiItem.builder()
            .item(ItemBuilder.of(Material.GRASS_BLOCK)
                .displayName(ColorUtil.parseComponent("&e바이옴 변경"))
                .addLore(ColorUtil.parseComponent("&7현재 바이옴: &f" + tempBiome))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&a클릭하여 변경"))
                .build())
            .onClick((player, click) -> {
                // 바이옴 선택 GUI 열기
                IslandBiomeChangeGui.create(plugin, viewer, island).open(viewer);
            })
            .build();
    }
    
    private GuiItem createPermissionItem() {
        return GuiItem.builder()
            .item(ItemBuilder.of(Material.IRON_DOOR)
                .displayName(ColorUtil.parseComponent("&e권한 설정"))
                .addLore(ColorUtil.parseComponent("&7멤버별 권한을 설정합니다"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&a클릭하여 열기"))
                .build())
            .onClick((player, click) -> {
                IslandPermissionGui.create(plugin, viewer, island).open(viewer);
            })
            .build();
    }
    
    private GuiItem createSpawnSettingItem() {
        return GuiItem.builder()
            .item(ItemBuilder.of(Material.ENDER_PEARL)
                .displayName(ColorUtil.parseComponent("&e스폰 위치 설정"))
                .addLore(ColorUtil.parseComponent("&7섬 스폰 위치를 설정합니다"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&a클릭하여 열기"))
                .build())
            .onClick((player, click) -> {
                IslandSpawnSettingsGui.create(plugin, viewer, island).open(viewer);
            })
            .build();
    }
    
    private GuiItem createDeleteItem() {
        return GuiItem.builder()
            .item(ItemBuilder.of(Material.TNT)
                .displayName(ColorUtil.parseComponent("&c섬 삭제"))
                .addLore(ColorUtil.parseComponent("&c⚠ 주의: 되돌릴 수 없습니다!"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&c클릭하여 삭제"))
                .build())
            .onClick((player, click) -> {
                IslandDeleteConfirmGui.create(plugin, viewer, island).open(viewer);
            })
            .build();
    }
    
    private void fillBorder(Material material) {
        ItemStack borderItem = ItemBuilder.of(material)
            .displayName(ColorUtil.parseComponent(" "))
            .build();
        
        // 상단과 하단
        for (int i = 0; i < 9; i++) {
            setItem(i, GuiItem.builder().item(borderItem).build());
            setItem(45 + i, GuiItem.builder().item(borderItem).build());
        }
        
        // 좌우
        for (int i = 1; i < 5; i++) {
            setItem(i * 9, GuiItem.builder().item(borderItem).build());
            setItem(i * 9 + 8, GuiItem.builder().item(borderItem).build());
        }
    }
    
    private void saveSettings() {
        // 변경된 설정으로 새 DTO 생성
        IslandSettingsDTO newSettings = new IslandSettingsDTO(
            tempBiome,
            island.settings().difficulty(),
            island.settings().spawnPoint(),
            island.settings().permissions(),
            island.settings().personalSpawns()
        );
        
        IslandDTO updatedIsland = new IslandDTO(
            island.islandId(),
            island.ownerUuid(),
            tempIslandName,
            island.members(),
            island.workers(),
            island.bannedPlayers(),
            island.level(),
            island.size(),
            island.centerLocation(),
            tempIsPublic,
            newSettings,
            island.createdAt(),
            island.lastVisited()
        );
        
        // 비동기로 저장
        islandManager.updateIsland(updatedIsland);
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        return IslandMainGui.create(guiManager, viewer);
    }
    
    @Override
    public Component getTitle() {
        return ColorUtil.parseComponent("&b섬 설정");
    }
}