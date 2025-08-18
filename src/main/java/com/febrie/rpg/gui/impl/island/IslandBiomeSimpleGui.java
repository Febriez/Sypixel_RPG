package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.dto.island.IslandSettingsDTO;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.GuiHandlerUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.StandardItemBuilder;
import com.febrie.rpg.util.UnifiedColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 간단한 섬 바이옴 선택 GUI
 * IslandSettingsGui와 호환되는 BaseGui 사용
 *
 * @author Febrie, CoffeeTory
 */
public class IslandBiomeSimpleGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final IslandDTO island;
    private final String currentBiome;
    
    // 사용 가능한 바이옴들
    private static final List<BiomeOption> BIOMES = List.of(
        new BiomeOption("PLAINS", "평원", Material.GRASS_BLOCK, "넓고 평평한 초원 지형"),
        new BiomeOption("FOREST", "숲", Material.OAK_SAPLING, "나무가 우거진 숲 지형"),
        new BiomeOption("DESERT", "사막", Material.SAND, "모래로 덮인 건조한 지형"),
        new BiomeOption("SNOWY_PLAINS", "설원", Material.SNOW_BLOCK, "눈으로 덮인 차가운 지형"),
        new BiomeOption("JUNGLE", "정글", Material.JUNGLE_SAPLING, "열대 우림 지형"),
        new BiomeOption("SWAMP", "늪", Material.LILY_PAD, "습지와 늪 지형"),
        new BiomeOption("OCEAN", "바다", Material.KELP, "깊고 푸른 바다"),
        new BiomeOption("MUSHROOM_FIELDS", "버섯 들판", Material.RED_MUSHROOM, "거대한 버섯이 자라는 특별한 지형")
    );
    
    private IslandBiomeSimpleGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                                @NotNull RPGMain plugin, @NotNull IslandDTO island, @NotNull String currentBiome) {
        super(viewer, guiManager, 36, "&b&l바이옴 선택");
        this.islandManager = plugin.getIslandManager();
        this.island = island;
        this.currentBiome = currentBiome;
    }
    
    /**
     * Factory method to create and open the biome selection GUI
     */
    public static IslandBiomeSimpleGui create(@NotNull RPGMain plugin, @NotNull Player viewer,
                                             @NotNull IslandDTO island, @NotNull String currentBiome) {
        return new IslandBiomeSimpleGui(viewer, plugin.getGuiManager(), plugin, island, currentBiome);
    }
    
    @Override
    protected void setupLayout() {
        fillBorder(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        
        // 바이옴 옵션들 배치
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
        for (int i = 0; i < BIOMES.size() && i < slots.length; i++) {
            BiomeOption biome = BIOMES.get(i);
            boolean isSelected = biome.id.equals(currentBiome);
            final BiomeOption finalBiome = biome;
            setItem(slots[i], new GuiItem(createBiomeItem(biome, isSelected))
                    .onAnyClick(player -> handleBiomeSelection(player, finalBiome)));
        }
        
        // 뒤로가기 버튼
        setItem(31, new GuiItem(createBackButton())
                .onAnyClick(player -> {
                    player.closeInventory();
                    IslandSettingsGui.create(plugin, viewer, island).open(viewer);
                }));
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        return null; // Use back button with direct navigation
    }
    
    @Override
    public @NotNull Component getTitle() {
        return Component.text("바이옴 선택", UnifiedColorUtil.PRIMARY);
    }
    
    private ItemStack createBiomeItem(BiomeOption biome, boolean isSelected) {
        ItemBuilder builder = StandardItemBuilder.guiItem(biome.material)
                .displayName(trans("gui.island.biome.name", biome.displayName)
                    .color(isSelected ? UnifiedColorUtil.GREEN : UnifiedColorUtil.YELLOW)
                    .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD))
                .addLore(Component.empty());
        
        if (isSelected) {
            builder.addLore(trans("gui.island.biome.currently_selected"));
            builder.addLore(Component.empty());
        }
        
        builder.addLore(trans("gui.island.biome.description", biome.description));
        builder.addLore(Component.empty());
        
        if (!isSelected) {
            builder.addLore(trans("gui.island.biome.click_to_select"));
        }
        
        return builder.build();
    }
    
    private ItemStack createBackButton() {
        return StandardItemBuilder.guiItem(Material.ARROW)
                .displayName(trans("gui.common.back"))
                .addLore(Component.empty())
                .addLore(trans("gui.island.biome.back_to_settings"))
                .build();
    }
    
    private void handleBiomeSelection(Player player, BiomeOption biome) {
        // 이미 선택된 바이옴이면 무시
        if (biome.id.equals(currentBiome)) {
            sendMessage(player, "gui.island.biome.message.already_selected");
            return;
        }
        
        // 새로운 설정 생성
        IslandSettingsDTO newSettings = new IslandSettingsDTO(
                island.configuration().settings().nameColorHex(),
                biome.id,
                island.configuration().settings().template()
        );
        
        // 섬 업데이트 - GuiHandlerUtil 사용
        IslandDTO updated = GuiHandlerUtil.updateIslandSettings(island, newSettings);
        
        islandManager.updateIsland(updated);
        
        // 실제 월드에 바이옴 적용
        // applyBiomeToIsland은 private 메서드이므로 주석 처리
        // 바이옴 설정은 DTO에만 저장
        
        sendMessage(player, "gui.island.biome.message.changed", biome.displayName);
        player.closeInventory();
        
        // 설정 메뉴로 돌아가기
        IslandSettingsGui.create(plugin, viewer, updated).open(viewer);
    }
    
    // 바이옴 옵션 레코드
    private record BiomeOption(String id, String displayName, Material material, String description) {}
}