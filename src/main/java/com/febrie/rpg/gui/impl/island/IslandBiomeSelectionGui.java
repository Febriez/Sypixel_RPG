package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
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
        new BiomeOption("PLAINS", LangKey.BIOME_PLAINS_NAME, LangKey.BIOME_PLAINS_DESCRIPTION, Material.GRASS_BLOCK),
        new BiomeOption("FOREST", LangKey.BIOME_FOREST_NAME, LangKey.BIOME_FOREST_DESCRIPTION, Material.OAK_SAPLING),
        new BiomeOption("DESERT", LangKey.BIOME_DESERT_NAME, LangKey.BIOME_DESERT_DESCRIPTION, Material.SAND),
        new BiomeOption("SNOWY_PLAINS", LangKey.BIOME_SNOWY_PLAINS_NAME, LangKey.BIOME_SNOWY_PLAINS_DESCRIPTION, Material.SNOW_BLOCK),
        new BiomeOption("JUNGLE", LangKey.BIOME_JUNGLE_NAME, LangKey.BIOME_JUNGLE_DESCRIPTION, Material.JUNGLE_SAPLING),
        new BiomeOption("SWAMP", LangKey.BIOME_SWAMP_NAME, LangKey.BIOME_SWAMP_DESCRIPTION, Material.LILY_PAD),
        new BiomeOption("SAVANNA", LangKey.BIOME_SAVANNA_NAME, LangKey.BIOME_SAVANNA_DESCRIPTION, Material.ACACIA_SAPLING),
        new BiomeOption("MUSHROOM_FIELDS", LangKey.BIOME_MUSHROOM_FIELDS_NAME, LangKey.BIOME_MUSHROOM_FIELDS_DESCRIPTION, Material.RED_MUSHROOM),
        new BiomeOption("TAIGA", LangKey.BIOME_TAIGA_NAME, LangKey.BIOME_TAIGA_DESCRIPTION, Material.SPRUCE_SAPLING),
        new BiomeOption("BEACH", LangKey.BIOME_BEACH_NAME, LangKey.BIOME_BEACH_DESCRIPTION, Material.SAND),
        new BiomeOption("CHERRY_GROVE", LangKey.BIOME_CHERRY_GROVE_NAME, LangKey.BIOME_CHERRY_GROVE_DESCRIPTION, Material.CHERRY_SAPLING),
        new BiomeOption("BAMBOO_JUNGLE", LangKey.BIOME_BAMBOO_JUNGLE_NAME, LangKey.BIOME_BAMBOO_JUNGLE_DESCRIPTION, Material.BAMBOO)
    );
    
    private final Consumer<String> onBiomeSelected;
    private final String currentBiome;
    private final GuiFramework backDestination;
    
    private IslandBiomeSelectionGui(@NotNull GuiManager guiManager,
                                   @NotNull Player player,
                                   @NotNull String currentBiome,
                                   @NotNull Consumer<String> onBiomeSelected,
                                   @NotNull GuiFramework backDestination) {
        super(player, guiManager, GUI_SIZE, LangManager.text(LangKey.GUI_ISLAND_BIOME_SELECTION_TITLE, player.locale()));
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
        return LangManager.text(LangKey.GUI_ISLAND_BIOME_SELECTION_TITLE, viewer.locale());
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
            ItemBuilder.of(Material.FILLED_MAP)
                .displayName(LangManager.text(LangKey.GUI_ISLAND_BIOME_SELECTION_INFO_TITLE, getViewerLocale()))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_ISLAND_BIOME_SELECTION_INFO_LORE1, getViewerLocale()))
                .addLore(LangManager.text(LangKey.GUI_ISLAND_BIOME_SELECTION_INFO_LORE2, getViewerLocale()))
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
            
            ItemBuilder builder = ItemBuilder.of(biome.icon)
                .displayName(LangManager.text(biome.nameKey, getViewerLocale()))
                .addLore(Component.empty());
            
            // Add description as list
            for (Component line : LangManager.list(biome.descriptionKey, getViewerLocale())) {
                builder.addLore(line);
            }
            builder.addLore(Component.empty());
            
            if (isSelected) {
                builder.addLore(LangManager.text(LangKey.GUI_ISLAND_BIOME_SELECTION_CURRENT_SELECTED, getViewerLocale()));
                builder.glint(true);
            } else {
                builder.addLore(LangManager.text(LangKey.GUI_ISLAND_BIOME_SELECTION_CLICK_TO_SELECT, getViewerLocale()));
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
            ItemBuilder.of(Material.ARROW)
                .displayName(LangManager.text(LangKey.GUI_BUTTONS_BACK_NAME, getViewerLocale()))
                .addLore(LangManager.text(LangKey.GUI_ISLAND_BIOME_SELECTION_BACK_LORE, getViewerLocale()))
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
    private record BiomeOption(String id, LangKey nameKey, LangKey descriptionKey, Material icon) {}
    
}