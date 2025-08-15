package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.BackableGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

/**
 * 섬 바이옴 선택 GUI
 * 
 * @author CoffeeTory
 */
public class IslandBiomeSelectionGui extends BaseGui implements BackableGui {
    
    private static final int GUI_SIZE = 54; // 6 rows
    
    // 바이옴 아이템 슬롯들
    private static final int[] BIOME_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34
    };
    
    // 사용 가능한 바이옴들
    private static final List<BiomeOption> AVAILABLE_BIOMES = List.of(
        new BiomeOption("PLAINS", "평원", Material.GRASS_BLOCK, "넓고 평평한 초원 지형"),
        new BiomeOption("FOREST", "숲", Material.OAK_SAPLING, "나무가 우거진 숲 지형"),
        new BiomeOption("DESERT", "사막", Material.SAND, "모래로 덮인 건조한 지형"),
        new BiomeOption("SNOWY_PLAINS", "설원", Material.SNOW_BLOCK, "눈으로 덮인 차가운 지형"),
        new BiomeOption("JUNGLE", "정글", Material.JUNGLE_SAPLING, "열대 우림 지형"),
        new BiomeOption("SWAMP", "늪", Material.LILY_PAD, "습지와 늪 지형"),
        new BiomeOption("SAVANNA", "사바나", Material.ACACIA_SAPLING, "드문드문 나무가 있는 초원"),
        new BiomeOption("MUSHROOM_FIELDS", "버섯 들판", Material.RED_MUSHROOM, "거대한 버섯이 자라는 특별한 지형"),
        new BiomeOption("TAIGA", "타이가", Material.SPRUCE_SAPLING, "침엽수림 지형"),
        new BiomeOption("BEACH", "해변", Material.SAND, "모래 해변 지형"),
        new BiomeOption("CHERRY_GROVE", "벚꽃 숲", Material.CHERRY_SAPLING, "아름다운 벚꽃나무 숲"),
        new BiomeOption("BAMBOO_JUNGLE", "대나무 정글", Material.BAMBOO, "대나무가 빽빽한 정글")
    );
    
    private final Consumer<String> onBiomeSelected;
    private final String currentBiome;
    private final GuiFramework backDestination;
    
    private IslandBiomeSelectionGui(@NotNull GuiManager guiManager,
                                   @NotNull Player player,
                                   @NotNull String currentBiome,
                                   @NotNull Consumer<String> onBiomeSelected,
                                   @NotNull GuiFramework backDestination) {
        super(player, guiManager, GUI_SIZE, "gui.island.biome-selection.title");
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
        IslandBiomeSelectionGui gui = new IslandBiomeSelectionGui(guiManager, player, currentBiome, onBiomeSelected, backDestination);
        return createAndInitialize(gui, "gui.island.biome-selection.title");
    }
    
    @Override
    public @NotNull Component getTitle() {
        return Component.text("바이옴 선택", ColorUtil.PRIMARY);
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        return backDestination;
    }
    
    @Override
    public GuiFramework getBackDestination() {
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
                .displayName(Component.text("🌍 바이옴 선택", NamedTextColor.GREEN)
                    .decoration(TextDecoration.BOLD, true))
                .lore(List.of(
                    Component.text(""),
                    Component.text("섬의 바이옴을 선택하세요", NamedTextColor.GRAY),
                    Component.text("바이옴에 따라 지형과 몹이 달라집니다", NamedTextColor.GRAY),
                    Component.text("")
                ))
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
            
            List<Component> lore = List.of(
                Component.text(""),
                Component.text(biome.description, NamedTextColor.GRAY),
                Component.text(""),
                isSelected ? 
                    Component.text("✔ 현재 선택됨", NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true) :
                    Component.text("▶ 클릭하여 선택", NamedTextColor.YELLOW),
                Component.text("")
            );
            
            GuiItem biomeItem = GuiItem.clickable(
                new ItemBuilder(biome.icon)
                    .displayName(Component.text(biome.name, 
                        isSelected ? NamedTextColor.GREEN : NamedTextColor.WHITE)
                        .decoration(TextDecoration.BOLD, isSelected))
                    .lore(lore)
                    .glint(isSelected)
                    .build(),
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
                .displayName(Component.text("◀ 돌아가기", NamedTextColor.GRAY))
                .lore(List.of(
                    Component.text("섬 생성 메뉴로 돌아갑니다", NamedTextColor.GRAY)
                ))
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
    private record BiomeOption(String id, String name, Material icon, String description) {}
}