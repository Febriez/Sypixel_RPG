package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

/**
 * 섬 바이옴 선택 GUI
 * 
 * @author CoffeeTory
 */
public class IslandBiomeSelectionGui extends BaseGui {
    
    private static final int GUI_SIZE = 54; // 6 rows
    
    // 바이옴 아이템 슬롯들
    private static final int[] BIOME_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34
    };
    
    // 사용 가능한 바이옴들
    private static final List<BiomeOption> AVAILABLE_BIOMES = List.of(
        new BiomeOption("PLAINS", "biome.plains", Material.GRASS_BLOCK),
        new BiomeOption("FOREST", "biome.forest", Material.OAK_SAPLING),
        new BiomeOption("DESERT", "biome.desert", Material.SAND),
        new BiomeOption("SNOWY_PLAINS", "biome.snowy_plains", Material.SNOW_BLOCK),
        new BiomeOption("JUNGLE", "biome.jungle", Material.JUNGLE_SAPLING),
        new BiomeOption("SWAMP", "biome.swamp", Material.LILY_PAD),
        new BiomeOption("SAVANNA", "biome.savanna", Material.ACACIA_SAPLING),
        new BiomeOption("MUSHROOM_FIELDS", "biome.mushroom_fields", Material.RED_MUSHROOM),
        new BiomeOption("TAIGA", "biome.taiga", Material.SPRUCE_SAPLING),
        new BiomeOption("BEACH", "biome.beach", Material.SAND),
        new BiomeOption("CHERRY_GROVE", "biome.cherry_grove", Material.CHERRY_SAPLING),
        new BiomeOption("BAMBOO_JUNGLE", "biome.bamboo_jungle", Material.BAMBOO)
    );
    
    private final Consumer<String> onBiomeSelected;
    private final String currentBiome;
    private final GuiFramework backDestination;
    
    private IslandBiomeSelectionGui(@NotNull GuiManager guiManager,
                                   @NotNull Player player,
                                   @NotNull String currentBiome,
                                   @NotNull Consumer<String> onBiomeSelected,
                                   @NotNull GuiFramework backDestination) {
        super(player, guiManager, GUI_SIZE, Component.translatable("gui.island.biome-selection.title"));
        this.currentBiome = currentBiome;
        this.onBiomeSelected = onBiomeSelected;
        this.backDestination = backDestination;
    }
    
    /**
     * Factory method to create the GUI
     */
    public static IslandBiomeSelectionGui create(@NotNull GuiManager guiManager,
                                                @NotNull Player player,
                                                @NotNull String currentBiome,
                                                @NotNull Consumer<String> onBiomeSelected,
                                                @NotNull GuiFramework backDestination) {
        return new IslandBiomeSelectionGui(guiManager, player, currentBiome, onBiomeSelected, backDestination);
    }
    
    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("gui.island.biome-selection.title");
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        return backDestination;
    }
    
    @Override
    protected void setupLayout() {
        setupDecorations();
        setupBiomeItems();
        setupBackButton();
    }
    
    /**
     * 장식 요소 설정
     */
    private void setupDecorations() {
        createBorder();
        
        // 제목 아이템
        GuiItem titleItem = GuiItem.display(
            new ItemBuilder(Material.FILLED_MAP)
                .displayName(LangManager.getComponent("gui.island.biome-selection.info.title", getViewerLocale()))
                .addLore(Component.empty())
                .addLore(LangManager.getComponent("gui.island.biome-selection.info.lore1", getViewerLocale()))
                .addLore(LangManager.getComponent("gui.island.biome-selection.info.lore2", getViewerLocale()))
                .addLore(Component.empty())
                .hideAllFlags()
                .build()
        );
        setItem(4, titleItem);
    }
    
    /**
     * 바이옴 아이템들 설정
     */
    private void setupBiomeItems() {
        for (int i = 0; i < Math.min(AVAILABLE_BIOMES.size(), BIOME_SLOTS.length); i++) {
            BiomeOption biome = AVAILABLE_BIOMES.get(i);
            boolean isSelected = biome.id.equals(currentBiome);
            
            ItemBuilder builder = new ItemBuilder(biome.icon)
                .displayName(LangManager.getComponent(biome.biomeKey + ".name", getViewerLocale()))
                .addLore(Component.empty())
                .addLore(LangManager.getComponent(biome.biomeKey + ".description", getViewerLocale()))
                .addLore(Component.empty());
            
            if (isSelected) {
                builder.addLore(LangManager.getComponent("gui.island.biome-selection.current-selected", getViewerLocale()));
                builder.glint(true);
            } else {
                builder.addLore(LangManager.getComponent("gui.island.biome-selection.click-to-select", getViewerLocale()));
            }
            
            builder.addLore(Component.empty());
            builder.hideAllFlags();
            
            GuiItem biomeItem = GuiItem.clickable(
                builder.build(),
                player -> {
                    onBiomeSelected.accept(biome.id);
                    player.closeInventory();
                    playSuccessSound(player);
                }
            );
            
            setItem(BIOME_SLOTS[i], biomeItem);
        }
    }
    
    private void setupBackButton() {
        GuiItem backButton = GuiItem.clickable(
            new ItemBuilder(Material.ARROW)
                .displayName(LangManager.getComponent("gui.buttons.back.name", getViewerLocale()))
                .addLore(LangManager.getComponent("gui.island.biome-selection.back.lore", getViewerLocale()))
                .hideAllFlags()
                .build(),
            player -> {
                guiManager.openGui(player, backDestination);
                playClickSound(player);
            }
        );
        setItem(getBackButtonSlot(), backButton);
    }
    
    /**
     * 바이옴 옵션 레코드
     */
    private record BiomeOption(String id, String biomeKey, Material icon) {}
    
}