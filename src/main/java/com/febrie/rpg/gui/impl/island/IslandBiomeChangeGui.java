package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.SoundUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 섬 바이옴 변경 GUI
 * 권한 있는 사용자만 바이옴을 변경할 수 있음
 *
 * @author Febrie, CoffeeTory
 */
public class IslandBiomeChangeGui extends BaseGui {
    
    private final IslandManager islandManager;
    private final Player viewer;
    private final IslandDTO island;
    private final boolean canChangeBiome;
    
    private IslandBiomeChangeGui(@NotNull RPGMain plugin, @NotNull Player viewer, 
                                @NotNull IslandDTO island) {
        super(plugin, 54);
        this.islandManager = plugin.getIslandManager();
        this.viewer = viewer;
        this.island = island;
        
        // 바이옴 변경 권한 확인 (섬 주인 또는 공동 소유자)
        this.canChangeBiome = island.ownerUuid().equals(viewer.getUniqueId().toString()) ||
                island.members().stream()
                        .anyMatch(m -> m.uuid().equals(viewer.getUniqueId().toString()) && m.isCoOwner());
    }
    
    /**
     * Factory method to create and open the biome change GUI
     */
    public static IslandBiomeChangeGui create(@NotNull RPGMain plugin, @NotNull Player viewer, 
                                             @NotNull IslandDTO island) {
        // 권한 체크 - 섬 주인이거나 공동 소유자인 경우만 GUI 생성
        boolean canChangeBiome = island.ownerUuid().equals(viewer.getUniqueId().toString()) ||
                island.members().stream()
                        .anyMatch(m -> m.uuid().equals(viewer.getUniqueId().toString()) && m.isCoOwner());
        
        if (!canChangeBiome) {
            viewer.sendMessage(ColorUtil.colorize("&c바이옴을 변경하려면 섬 주인이거나 공동 소유자여야 합니다."));
            return null;
        }
        
        IslandBiomeChangeGui gui = new IslandBiomeChangeGui(plugin, viewer, island);
        return BaseGui.create(gui, LangManager.getMessage(viewer, "gui.island.biome-change.title"));
    }
    
    @Override
    protected void setupItems() {
        fillBorder(Material.CYAN_STAINED_GLASS_PANE);
        
        // 바이옴 선택 옵션들 - 권한 체크는 create()에서 이미 완료
        setupBiomeOptions();
        
        // 현재 바이옴 정보
        setItem(4, createCurrentBiomeInfo().getItemStack());
        
        // 뒤로가기
        setItem(49, createBackButton().getItemStack());
    }
    
    private void setupBiomeOptions() {
        // 평원
        setItem(10, createBiomeItem(Biome.PLAINS, Material.GRASS_BLOCK, 
                "평원", "넓고 평평한 초원 지형").getItemStack());
        
        // 숲
        setItem(11, createBiomeItem(Biome.FOREST, Material.OAK_LOG, 
                "숲", "다양한 나무가 자라는 숲").getItemStack());
        
        // 사막
        setItem(12, createBiomeItem(Biome.DESERT, Material.SAND, 
                "사막", "모래로 덮인 건조한 지형").getItemStack());
        
        // 정글
        setItem(13, createBiomeItem(Biome.JUNGLE, Material.JUNGLE_LOG, 
                "정글", "울창한 열대 정글").getItemStack());
        
        // 타이가
        setItem(14, createBiomeItem(Biome.TAIGA, Material.SPRUCE_LOG, 
                "타이가", "침엽수가 자라는 추운 숲"));
        
        // 눈 덮인 평원
        setItem(15, createBiomeItem(Biome.SNOWY_PLAINS, Material.SNOW_BLOCK, 
                "설원", "눈으로 덮인 차가운 평원"));
        
        // 사바나
        setItem(16, createBiomeItem(Biome.SAVANNA, Material.ACACIA_LOG, 
                "사바나", "아카시아 나무가 자라는 건조한 평원"));
        
        // 늪
        setItem(19, createBiomeItem(Biome.SWAMP, Material.LILY_PAD, 
                "늪", "습하고 어두운 늪지대"));
        
        // 버섯 들판
        setItem(20, createBiomeItem(Biome.MUSHROOM_FIELDS, Material.RED_MUSHROOM_BLOCK, 
                "버섯 들판", "거대한 버섯이 자라는 특별한 지형"));
        
        // 해변
        setItem(21, createBiomeItem(Biome.BEACH, Material.SAND, 
                "해변", "모래로 덮인 해안가"));
        
        // 꽃 숲
        setItem(22, createBiomeItem(Biome.FLOWER_FOREST, Material.ROSE_BUSH, 
                "꽃 숲", "다양한 꽃이 피는 아름다운 숲"));
        
        // 대나무 정글
        setItem(23, createBiomeItem(Biome.BAMBOO_JUNGLE, Material.BAMBOO, 
                "대나무 정글", "대나무가 무성한 정글"));
        
        // 어두운 숲
        setItem(24, createBiomeItem(Biome.DARK_FOREST, Material.DARK_OAK_LOG, 
                "어두운 숲", "빛이 잘 들지 않는 울창한 숲"));
        
        // 자작나무 숲
        setItem(25, createBiomeItem(Biome.BIRCH_FOREST, Material.BIRCH_LOG, 
                "자작나무 숲", "자작나무로 가득한 밝은 숲"));
        
        // 메사
        setItem(28, createBiomeItem(Biome.BADLANDS, Material.TERRACOTTA, 
                "악지", "붉은 점토로 이루어진 협곡 지형"));
    }
    
    private GuiItem createBiomeItem(Biome biome, Material material, String name, String description) {
        World islandWorld = plugin.getServer().getWorld("island_" + island.islandId());
        Biome currentBiome = islandWorld != null ? 
                islandWorld.getBiome(0, 100, 0) : Biome.PLAINS;
        
        boolean isCurrentBiome = currentBiome == biome;
        
        return new GuiItem(
            new ItemBuilder(material)
                .displayName(Component.text(name, isCurrentBiome ? ColorUtil.LEGENDARY : ColorUtil.YELLOW))
                .lore(Arrays.asList(
                    Component.empty(),
                    Component.text(description, ColorUtil.GRAY),
                    Component.empty(),
                    isCurrentBiome ? 
                        Component.text("✓ 현재 바이옴", ColorUtil.GREEN) :
                        Component.text("▶ 클릭하여 변경", ColorUtil.YELLOW)
                ))
                .build()
        ).onAnyClick(player -> {
            if (!isCurrentBiome) {
                changeBiome(player, biome, name);
            } else {
                player.sendMessage(ColorUtil.colorize("&e이미 선택된 바이옴입니다."));
            }
            SoundUtil.playClickSound(player);
        });
    }
    
    private void changeBiome(Player player, Biome biome, String biomeName) {
        World islandWorld = plugin.getServer().getWorld("island_" + island.islandId());
        if (islandWorld == null) {
            player.sendMessage(ColorUtil.colorize("&c섬 월드를 찾을 수 없습니다."));
            return;
        }
        
        player.sendMessage(ColorUtil.colorize("&e바이옴을 변경하는 중..."));
        player.closeInventory();
        
        // 비동기로 바이옴 변경 처리
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            // 섬 전체 영역의 바이옴 변경
            int size = island.size();
            int radius = 100 + (size * 50); // 기본 100블록 + 업그레이드당 50블록
            
            for (int x = -radius; x <= radius; x += 4) {
                for (int z = -radius; z <= radius; z += 4) {
                    for (int y = islandWorld.getMinHeight(); y < islandWorld.getMaxHeight(); y += 4) {
                        islandWorld.setBiome(x, y, z, biome);
                    }
                }
            }
            
            // 메인 스레드에서 완료 메시지
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                player.sendMessage(ColorUtil.colorize("&a바이옴을 &e" + biomeName + "&a(으)로 변경했습니다!"));
                player.sendMessage(ColorUtil.colorize("&7변경사항을 적용하려면 청크를 다시 로드해야 할 수 있습니다."));
                
                // 플레이어가 섬에 있다면 청크 리로드
                if (player.getWorld().equals(islandWorld)) {
                    player.teleport(player.getLocation().add(0, 1, 0));
                    player.teleport(player.getLocation().subtract(0, 1, 0));
                }
            });
        });
    }
    
    private GuiItem createCurrentBiomeInfo() {
        World islandWorld = plugin.getServer().getWorld("island_" + island.islandId());
        String currentBiomeName = "알 수 없음";
        
        if (islandWorld != null) {
            Biome biome = islandWorld.getBiome(0, 100, 0);
            currentBiomeName = translateBiomeName(biome);
        }
        
        return new GuiItem(
            new ItemBuilder(Material.COMPASS)
                .displayName(Component.text("현재 바이옴: " + currentBiomeName, ColorUtil.AQUA))
                .lore(Arrays.asList(
                    Component.empty(),
                    Component.text("섬의 현재 바이옴 설정입니다", ColorUtil.GRAY)
                ))
                .build()
        );
    }
    
    private String translateBiomeName(Biome biome) {
        if (biome == Biome.PLAINS) return "평원";
        if (biome == Biome.FOREST) return "숲";
        if (biome == Biome.DESERT) return "사막";
        if (biome == Biome.JUNGLE) return "정글";
        if (biome == Biome.TAIGA) return "타이가";
        if (biome == Biome.SNOWY_PLAINS) return "설원";
        if (biome == Biome.SAVANNA) return "사바나";
        if (biome == Biome.SWAMP) return "늪";
        if (biome == Biome.MUSHROOM_FIELDS) return "버섯 들판";
        if (biome == Biome.BEACH) return "해변";
        if (biome == Biome.FLOWER_FOREST) return "꽃 숲";
        if (biome == Biome.BAMBOO_JUNGLE) return "대나무 정글";
        if (biome == Biome.DARK_FOREST) return "어두운 숲";
        if (biome == Biome.BIRCH_FOREST) return "자작나무 숲";
        if (biome == Biome.BADLANDS) return "악지";
        return biome.toString().toLowerCase().replace("_", " ");
    }
    
    
    private GuiItem createBackButton() {
        return new GuiItem(
            new ItemBuilder(Material.ARROW)
                .displayName(Component.text("뒤로가기", ColorUtil.YELLOW))
                .lore(List.of(Component.text("섬 관리 메뉴로 돌아갑니다", ColorUtil.GRAY)))
                .build()
        ).onAnyClick(
            player -> {
                player.closeInventory();
                IslandMainGui.create(plugin.getGuiManager(), viewer).open(viewer);
                SoundUtil.playClickSound(player);
            }
        );
    }
    
    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}