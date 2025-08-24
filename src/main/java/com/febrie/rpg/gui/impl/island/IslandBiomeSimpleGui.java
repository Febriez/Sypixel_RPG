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
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
        new BiomeOption("PLAINS", "biome.plains", Material.GRASS_BLOCK),
        new BiomeOption("FOREST", "biome.forest", Material.OAK_SAPLING),
        new BiomeOption("DESERT", "biome.desert", Material.SAND),
        new BiomeOption("SNOWY_PLAINS", "biome.snowy_plains", Material.SNOW_BLOCK),
        new BiomeOption("JUNGLE", "biome.jungle", Material.JUNGLE_SAPLING),
        new BiomeOption("SWAMP", "biome.swamp", Material.LILY_PAD),
        new BiomeOption("OCEAN", "biome.ocean", Material.KELP),
        new BiomeOption("MUSHROOM_FIELDS", "biome.mushroom_fields", Material.RED_MUSHROOM)
    );
    
    private IslandBiomeSimpleGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                                @NotNull RPGMain plugin, @NotNull IslandDTO island, @NotNull String currentBiome) {
        super(viewer, guiManager, 36, Component.translatable("gui.island.biome-simple.title"));
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
        return Component.translatable("gui.island.biome-simple.title");
    }
    
    private ItemStack createBiomeItem(BiomeOption biome, boolean isSelected) {
        ItemBuilder builder = ItemBuilder.of(biome.material, getViewerLocale())
                .displayNameTranslated(biome.biomeKey + ".name")
                .addLore(Component.empty());
        
        if (isSelected) {
            builder.addLoreTranslated("gui.island.biome.currently_selected");
            builder.addLore(Component.empty());
            builder.glint(true);
        }
        
        builder.addLoreTranslated(biome.biomeKey + ".description");
        builder.addLore(Component.empty());
        
        if (!isSelected) {
            builder.addLoreTranslated("gui.island.biome.click_to_select");
        }
        
        return builder.hideAllFlags().build();
    }
    
    private ItemStack createBackButton() {
        return ItemBuilder.of(Material.ARROW, getViewerLocale())
                .displayNameTranslated("gui.buttons.back.name")
                .addLore(Component.empty())
                .addLoreTranslated("gui.island.biome.back_to_settings")
                .hideAllFlags()
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
        
        player.sendMessage(LangManager.get("gui.island.biome.message.changed", player, Component.translatable(biome.biomeKey + ".name")));
        player.closeInventory();
        
        // 설정 메뉴로 돌아가기
        IslandSettingsGui.create(plugin, viewer, updated).open(viewer);
    }
    
    // 바이옴 옵션 레코드
    private record BiomeOption(String id, String biomeKey, Material material) {}
}