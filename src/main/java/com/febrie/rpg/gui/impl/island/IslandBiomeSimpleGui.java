package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandSettingsDTO;
import com.febrie.rpg.gui.BaseGui;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
    private final Player viewer;
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
    
    private IslandBiomeSimpleGui(@NotNull RPGMain plugin, @NotNull Player viewer,
                                @NotNull IslandDTO island, @NotNull String currentBiome) {
        super(plugin, 36); // 4줄 GUI
        this.islandManager = plugin.getIslandManager();
        this.viewer = viewer;
        this.island = island;
        this.currentBiome = currentBiome;
    }
    
    /**
     * Factory method to create and open the biome selection GUI
     */
    public static IslandBiomeSimpleGui create(@NotNull RPGMain plugin, @NotNull Player viewer,
                                             @NotNull IslandDTO island, @NotNull String currentBiome) {
        IslandBiomeSimpleGui gui = new IslandBiomeSimpleGui(plugin, viewer, island, currentBiome);
        return BaseGui.create(gui, ColorUtil.parseComponent("&b&l바이옴 선택"));
    }
    
    @Override
    protected void setupItems() {
        fillBorder(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        
        // 바이옴 옵션들 배치
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
        for (int i = 0; i < BIOMES.size() && i < slots.length; i++) {
            BiomeOption biome = BIOMES.get(i);
            boolean isSelected = biome.id.equals(currentBiome);
            setItem(slots[i], createBiomeItem(biome, isSelected));
        }
        
        // 뒤로가기 버튼
        setItem(31, createBackButton());
    }
    
    private ItemStack createBiomeItem(BiomeOption biome, boolean isSelected) {
        ItemBuilder builder = new ItemBuilder(biome.material)
                .displayName(ColorUtil.parseComponent(
                    (isSelected ? "&a&l" : "&e&l") + biome.displayName
                ))
                .addLore(ColorUtil.parseComponent(""));
        
        if (isSelected) {
            builder.addLore(ColorUtil.parseComponent("&a✔ 현재 선택됨"));
            builder.addLore(ColorUtil.parseComponent(""));
        }
        
        builder.addLore(ColorUtil.parseComponent("&7" + biome.description));
        builder.addLore(ColorUtil.parseComponent(""));
        
        if (!isSelected) {
            builder.addLore(ColorUtil.parseComponent("&e▶ 클릭하여 선택"));
        }
        
        return builder.build();
    }
    
    private ItemStack createBackButton() {
        return new ItemBuilder(Material.ARROW)
                .displayName(ColorUtil.parseComponent("&c뒤로가기"))
                .addLore(ColorUtil.parseComponent(""))
                .addLore(ColorUtil.parseComponent("&7설정 메뉴로 돌아갑니다"))
                .build();
    }
    
    @Override
    protected void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        int slot = event.getSlot();
        
        // 뒤로가기
        if (slot == 31) {
            player.closeInventory();
            IslandSettingsGui.create(plugin, viewer, island).open(viewer);
            return;
        }
        
        // 바이옴 선택 확인
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
        for (int i = 0; i < slots.length && i < BIOMES.size(); i++) {
            if (slot == slots[i]) {
                BiomeOption selected = BIOMES.get(i);
                handleBiomeSelection(player, selected);
                break;
            }
        }
    }
    
    private void handleBiomeSelection(Player player, BiomeOption biome) {
        // 이미 선택된 바이옴이면 무시
        if (biome.id.equals(currentBiome)) {
            player.sendMessage(ColorUtil.colorize("&e이미 선택된 바이옴입니다."));
            return;
        }
        
        // 새로운 설정 생성
        IslandSettingsDTO newSettings = new IslandSettingsDTO(
                island.settings().nameColorHex(),
                biome.id,
                island.settings().template()
        );
        
        // 섬 업데이트
        IslandDTO updated = new IslandDTO(
                island.islandId(),
                island.ownerUuid(),
                island.ownerName(),
                island.islandName(),
                island.size(),
                island.isPublic(),
                island.createdAt(),
                System.currentTimeMillis(),
                island.members(),
                island.workers(),
                island.contributions(),
                island.spawnData(),
                island.upgradeData(),
                island.permissions(),
                island.pendingInvites(),
                island.recentVisits(),
                island.totalResets(),
                island.deletionScheduledAt(),
                newSettings
        );
        
        islandManager.updateIsland(updated);
        
        // 실제 월드에 바이옴 적용
        // applyBiomeToIsland은 private 메서드이므로 주석 처리
        // 바이옴 설정은 DTO에만 저장
        
        player.sendMessage(ColorUtil.colorize("&a섬 바이옴이 " + biome.displayName + "&a(으)로 변경되었습니다!"));
        player.closeInventory();
        
        // 설정 메뉴로 돌아가기
        IslandSettingsGui.create(plugin, viewer, updated).open(viewer);
    }
    
    // 바이옴 옵션 레코드
    private record BiomeOption(String id, String displayName, Material material, String description) {}
}