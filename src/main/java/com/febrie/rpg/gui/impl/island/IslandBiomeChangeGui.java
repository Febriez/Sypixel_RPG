package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.SoundUtil;
import com.febrie.rpg.util.StandardItemBuilder;
import com.febrie.rpg.util.UnifiedColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * 섬 바이옴 변경 GUI
 * 권한 있는 사용자만 바이옴을 변경할 수 있음
 *
 * @author Febrie, CoffeeTory
 */
public class IslandBiomeChangeGui extends BaseGui {

    private final IslandDTO island;

    private IslandBiomeChangeGui(@NotNull Player viewer, @NotNull GuiManager guiManager, @NotNull RPGMain plugin, @NotNull IslandDTO island) {
        super(viewer, guiManager, 54, "&b&l바이옴 변경");
        this.island = island;
    }

    /**
     * Factory method to create and open the biome change GUI
     */
    public static @Nullable IslandBiomeChangeGui create(@NotNull RPGMain plugin, @NotNull Player viewer, @NotNull IslandDTO island) {
        // 권한 체크 - 섬 주인이거나 공동 소유자인 경우만 GUI 생성
        boolean canChangeBiome = island.core().ownerUuid()
                .equals(viewer.getUniqueId()
                        .toString()) || island.membership().members()
                .stream()
                .anyMatch(m -> m.uuid()
                        .equals(viewer.getUniqueId()
                                .toString()) && m.isCoOwner());

        if (!canChangeBiome) {
            viewer.sendMessage(LangManager.getMessage(viewer, "gui.island.biome.message.no_permission"));
            return null;
        }

        return new IslandBiomeChangeGui(viewer, plugin.getGuiManager(), plugin, island);
    }

    @Override
    protected void setupLayout() {
        fillBorder(Material.CYAN_STAINED_GLASS_PANE);

        // 바이옴 선택 옵션들 - 권한 체크는 create()에서 이미 완료
        setupBiomeOptions();

        // 현재 바이옴 정보
        setItem(4, createCurrentBiomeInfo());

        // 뒤로가기
        setItem(49, createBackButton());
    }

    @Override
    protected GuiFramework getBackTarget() {
        return null; // No specific back target for this GUI
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.text("바이옴 변경", UnifiedColorUtil.PRIMARY);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
    }

    private void setupBiomeOptions() {
        // 평원
        setItem(10, createBiomeItem(Biome.PLAINS, Material.GRASS_BLOCK, "평원", "넓고 평평한 초원 지형"));

        // 숲
        setItem(11, createBiomeItem(Biome.FOREST, Material.OAK_LOG, "숲", "다양한 나무가 자라는 숲"));

        // 사막
        setItem(12, createBiomeItem(Biome.DESERT, Material.SAND, "사막", "모래로 덮인 건조한 지형"));

        // 정글
        setItem(13, createBiomeItem(Biome.JUNGLE, Material.JUNGLE_LOG, "정글", "울창한 열대 정글"));

        // 타이가
        setItem(14, createBiomeItem(Biome.TAIGA, Material.SPRUCE_LOG, "타이가", "침엽수가 자라는 추운 숲"));

        // 눈 덮인 평원
        setItem(15, createBiomeItem(Biome.SNOWY_PLAINS, Material.SNOW_BLOCK, "설원", "눈으로 덮인 차가운 평원"));

        // 사바나
        setItem(16, createBiomeItem(Biome.SAVANNA, Material.ACACIA_LOG, "사바나", "아카시아 나무가 자라는 건조한 평원"));

        // 늪
        setItem(19, createBiomeItem(Biome.SWAMP, Material.LILY_PAD, "늪", "습하고 어두운 늪지대"));

        // 버섯 들판
        setItem(20, createBiomeItem(Biome.MUSHROOM_FIELDS, Material.RED_MUSHROOM_BLOCK, "버섯 들판", "거대한 버섯이 자라는 특별한 지형"));

        // 해변
        setItem(21, createBiomeItem(Biome.BEACH, Material.SAND, "해변", "모래로 덮인 해안가"));

        // 꽃 숲
        setItem(22, createBiomeItem(Biome.FLOWER_FOREST, Material.ROSE_BUSH, "꽃 숲", "다양한 꽃이 피는 아름다운 숲"));

        // 대나무 정글
        setItem(23, createBiomeItem(Biome.BAMBOO_JUNGLE, Material.BAMBOO, "대나무 정글", "대나무가 무성한 정글"));

        // 어두운 숲
        setItem(24, createBiomeItem(Biome.DARK_FOREST, Material.DARK_OAK_LOG, "어두운 숲", "빛이 잘 들지 않는 울창한 숲"));

        // 자작나무 숲
        setItem(25, createBiomeItem(Biome.BIRCH_FOREST, Material.BIRCH_LOG, "자작나무 숲", "자작나무로 가득한 밝은 숲"));

        // 메사
        setItem(28, createBiomeItem(Biome.BADLANDS, Material.TERRACOTTA, "악지", "붉은 점토로 이루어진 협곡 지형"));
    }

    private GuiItem createBiomeItem(Biome biome, Material material, String name, String description) {
        World islandWorld = plugin.getServer()
                .getWorld("island_" + island.core().islandId());
        Biome currentBiome = islandWorld != null ? islandWorld.getBiome(0, 100, 0) : Biome.PLAINS;

        boolean isCurrentBiome = currentBiome == biome;

        return new GuiItem(StandardItemBuilder.guiItem(material)
                .displayName(Component.text(name, isCurrentBiome ? UnifiedColorUtil.LEGENDARY : UnifiedColorUtil.YELLOW))
                .lore(Arrays.asList(Component.empty(), Component.text(description, UnifiedColorUtil.GRAY), Component.empty(), isCurrentBiome ? Component.text("✓ 현재 바이옴", UnifiedColorUtil.GREEN) : Component.text("▶ 클릭하여 변경", UnifiedColorUtil.YELLOW)))
                .build()).onAnyClick(player -> {
            if (!isCurrentBiome) {
                changeBiome(player, biome, name);
            } else {
                player.sendMessage(LangManager.getMessage(player, "gui.island.biome.message.already_selected"));
            }
            SoundUtil.playClickSound(player);
        });
    }

    private void changeBiome(Player player, Biome biome, String biomeName) {
        World islandWorld = plugin.getServer()
                .getWorld("island_" + island.core().islandId());
        if (islandWorld == null) {
            player.sendMessage(LangManager.getMessage(player, "gui.island.biome.message.world_not_found"));
            return;
        }

        player.sendMessage(LangManager.getMessage(player, "gui.island.biome.message.changing"));
        player.closeInventory();

        // 비동기로 바이옴 변경 처리
        plugin.getServer()
                .getScheduler()
                .runTaskAsynchronously(plugin, () -> {
                    // 섬 전체 영역의 바이옴 변경
                    int size = island.core().size();
                    int radius = 100 + (size * 50); // 기본 100블록 + 업그레이드당 50블록

                    for (int x = -radius; x <= radius; x += 4) {
                        for (int z = -radius; z <= radius; z += 4) {
                            for (int y = islandWorld.getMinHeight(); y < islandWorld.getMaxHeight(); y += 4) {
                                islandWorld.setBiome(x, y, z, biome);
                            }
                        }
                    }

                    // 메인 스레드에서 완료 메시지
                    plugin.getServer()
                            .getScheduler()
                            .runTask(plugin, () -> {
                                player.sendMessage(LangManager.getMessage(player, "gui.island.biome.message.changed", "biome", biomeName));
                                player.sendMessage(LangManager.getMessage(player, "gui.island.biome.message.chunk_reload_notice"));

                                // 플레이어가 섬에 있다면 청크 리로드
                                if (player.getWorld()
                                        .equals(islandWorld)) {
                                    player.teleport(player.getLocation()
                                            .add(0, 1, 0));
                                    player.teleport(player.getLocation()
                                            .subtract(0, 1, 0));
                                }
                            });
                });
    }

    @Contract(" -> new")
    private @NotNull GuiItem createCurrentBiomeInfo() {
        World islandWorld = plugin.getServer()
                .getWorld("island_" + island.core().islandId());
        String currentBiomeName = "알 수 없음";

        if (islandWorld != null) {
            Biome biome = islandWorld.getBiome(0, 100, 0);
            currentBiomeName = translateBiomeName(biome);
        }

        return new GuiItem(StandardItemBuilder.guiItem(Material.COMPASS)
                .displayName(Component.text("현재 바이옴: " + currentBiomeName, UnifiedColorUtil.AQUA))
                .lore(Arrays.asList(Component.empty(), Component.text("섬의 현재 바이옴 설정입니다", UnifiedColorUtil.GRAY)))
                .build());
    }

    private @NotNull String translateBiomeName(Biome biome) {
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
        return biome.toString()
                .toLowerCase()
                .replace("_", " ");
    }


    private GuiItem createBackButton() {
        return new GuiItem(StandardItemBuilder.guiItem(Material.ARROW)
                .displayName(Component.text("뒤로가기", UnifiedColorUtil.YELLOW))
                .lore(List.of(Component.text("섬 관리 메뉴로 돌아갑니다", UnifiedColorUtil.GRAY)))
                .build()).onAnyClick(player -> {
            player.closeInventory();
            IslandMainGui.create(plugin.getGuiManager(), viewer)
                    .open(viewer);
            SoundUtil.playClickSound(player);
        });
    }
}