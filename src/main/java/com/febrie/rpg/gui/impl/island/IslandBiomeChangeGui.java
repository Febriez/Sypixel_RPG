package com.febrie.rpg.gui.impl.island;
import com.febrie.rpg.util.lang.GeneralLangKey;

import com.febrie.rpg.util.lang.GuiLangKey;
import com.febrie.rpg.util.lang.SystemLangKey;
import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.SoundUtil;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.lang.ILangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
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
        super(viewer, guiManager, 54, LangManager.text(GuiLangKey.GUI_ISLAND_BIOME_CHANGE_TITLE, viewer.locale()));
        this.island = island;
        // plugin is already available from BaseGui
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
            viewer.sendMessage(LangManager.text(GuiLangKey.GUI_ISLAND_BIOME_MESSAGE_NO_PERMISSION, viewer.locale()));
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
        return LangManager.text(GuiLangKey.GUI_ISLAND_BIOME_CHANGE_TITLE, viewer.locale());
    }

    private void setupBiomeOptions() {
        // 평원
        setItem(10, createBiomeItem(Biome.PLAINS, Material.GRASS_BLOCK, "biome.plains"));

        // 숲
        setItem(11, createBiomeItem(Biome.FOREST, Material.OAK_LOG, "biome.forest"));

        // 사막
        setItem(12, createBiomeItem(Biome.DESERT, Material.SAND, "biome.desert"));

        // 정글
        setItem(13, createBiomeItem(Biome.JUNGLE, Material.JUNGLE_LOG, "biome.jungle"));

        // 타이가
        setItem(14, createBiomeItem(Biome.TAIGA, Material.SPRUCE_LOG, "biome.taiga"));

        // 눈 덮인 평원
        setItem(15, createBiomeItem(Biome.SNOWY_PLAINS, Material.SNOW_BLOCK, "biome.snowy_plains"));

        // 사바나
        setItem(16, createBiomeItem(Biome.SAVANNA, Material.ACACIA_LOG, "biome.savanna"));

        // 늪
        setItem(19, createBiomeItem(Biome.SWAMP, Material.LILY_PAD, "biome.swamp"));

        // 버섯 들판
        setItem(20, createBiomeItem(Biome.MUSHROOM_FIELDS, Material.RED_MUSHROOM_BLOCK, "biome.mushroom_fields"));

        // 해변
        setItem(21, createBiomeItem(Biome.BEACH, Material.SAND, "biome.beach"));

        // 꽃 숲
        setItem(22, createBiomeItem(Biome.FLOWER_FOREST, Material.ROSE_BUSH, "biome.flower_forest"));

        // 대나무 정글
        setItem(23, createBiomeItem(Biome.BAMBOO_JUNGLE, Material.BAMBOO, "biome.bamboo_jungle"));

        // 어두운 숲
        setItem(24, createBiomeItem(Biome.DARK_FOREST, Material.DARK_OAK_LOG, "biome.dark_forest"));

        // 자작나무 숲
        setItem(25, createBiomeItem(Biome.BIRCH_FOREST, Material.BIRCH_LOG, "biome.birch_forest"));

        // 메사
        setItem(28, createBiomeItem(Biome.BADLANDS, Material.TERRACOTTA, "biome.badlands"));
    }

    private GuiItem createBiomeItem(Biome biome, Material material, String biomeKey) {
        World islandWorld = plugin.getServer()
                .getWorld("Island");
        // 섬의 중앙 좌표 가져오기
        int centerX = (int) island.configuration().spawnData().defaultSpawn().x();
        int centerZ = (int) island.configuration().spawnData().defaultSpawn().z();
        Biome currentBiome = islandWorld != null ? islandWorld.getBiome(centerX, 100, centerZ) : Biome.PLAINS;

        boolean isCurrentBiome = currentBiome == biome;
        
        // Get appropriate LangKey for biome name and description
        ILangKey nameKey = getBiomeNameKey(biome);
        ILangKey descKey = getBiomeDescriptionKey(biome);

        ItemBuilder builder = ItemBuilder.of(material)
                .displayName(LangManager.text(nameKey, getViewerLocale()))
                .addLore(Component.empty());
        
        // Add description as list
        for (Component line : LangManager.list(descKey, getViewerLocale())) {
            builder.addLore(line);
        }
        builder.addLore(Component.empty());
        
        if (isCurrentBiome) {
            builder.addLore(LangManager.text(GuiLangKey.GUI_ISLAND_BIOME_CURRENT, getViewerLocale()));
        } else {
            builder.addLore(LangManager.text(GuiLangKey.GUI_ISLAND_BIOME_CLICK_TO_CHANGE, getViewerLocale()));
        }
        
        return new GuiItem(builder.hideAllFlags().build()).onAnyClick(player -> {
            if (!isCurrentBiome) {
                changeBiome(player, biome, biomeKey);
            } else {
                player.sendMessage(LangManager.text(GuiLangKey.GUI_ISLAND_BIOME_MESSAGE_ALREADY_SELECTED, viewer.locale()));
            }
            SoundUtil.playClickSound(player);
        });
    }

    private void changeBiome(Player player, Biome biome, String biomeKey) {
        World islandWorld = plugin.getServer()
                .getWorld("Island");
        if (islandWorld == null) {
            player.sendMessage(LangManager.text(GuiLangKey.GUI_ISLAND_BIOME_MESSAGE_WORLD_NOT_FOUND, viewer.locale()));
            return;
        }

        player.sendMessage(LangManager.text(GuiLangKey.GUI_ISLAND_BIOME_MESSAGE_CHANGING, viewer.locale()));

        // 비동기로 바이옴 변경 처리
        plugin.getServer()
                .getScheduler()
                .runTaskAsynchronously(plugin, () -> {
                    // 섬의 중앙 좌표 가져오기
                    int centerX = (int) island.configuration().spawnData().defaultSpawn().x();
                    int centerZ = (int) island.configuration().spawnData().defaultSpawn().z();
                    
                    // 섬 전체 영역의 바이옴 변경
                    int size = island.core().size();
                    int radius = 100 + (size * 50); // 기본 100블록 + 업그레이드당 50블록

                    for (int x = centerX - radius; x <= centerX + radius; x += 4) {
                        for (int z = centerZ - radius; z <= centerZ + radius; z += 4) {
                            for (int y = islandWorld.getMinHeight(); y < islandWorld.getMaxHeight(); y += 4) {
                                islandWorld.setBiome(x, y, z, biome);
                            }
                        }
                    }

                    // 메인 스레드에서 완료 메시지 및 GUI 재오픈
                    plugin.getServer()
                            .getScheduler()
                            .runTask(plugin, () -> {
                                ILangKey nameKey = getBiomeNameKey(biome);
                                player.sendMessage(LangManager.text(GuiLangKey.GUI_ISLAND_BIOME_MESSAGE_CHANGED, viewer.locale(), LangManager.text(nameKey, viewer.locale())));
                                player.sendMessage(LangManager.text(GuiLangKey.GUI_ISLAND_BIOME_MESSAGE_CHUNK_RELOAD_NOTICE, viewer.locale()));

                                // 플레이어가 섬에 있다면 청크 리로드
                                if (player.getWorld()
                                        .equals(islandWorld)) {
                                    player.teleport(player.getLocation()
                                            .add(0, 1, 0));
                                    player.teleport(player.getLocation()
                                            .subtract(0, 1, 0));
                                }
                                
                                // 섬 메인 GUI로 돌아가기
                                IslandMainGui.create(plugin.getGuiManager(), player).open(player);
                            });
                });
    }

    @Contract(" -> new")
    private @NotNull GuiItem createCurrentBiomeInfo() {
        World islandWorld = plugin.getServer()
                .getWorld("Island");
        Component currentBiomeName = LangManager.text(GuiLangKey.GUI_ISLAND_BIOME_UNKNOWN, viewer.locale());

        if (islandWorld != null) {
            // 섬의 중앙 좌표에서 바이옴 가져오기
            int centerX = (int) island.configuration().spawnData().defaultSpawn().x();
            int centerZ = (int) island.configuration().spawnData().defaultSpawn().z();
            Biome biome = islandWorld.getBiome(centerX, 100, centerZ);
            ILangKey nameKey = getBiomeNameKey(biome);
            currentBiomeName = LangManager.text(nameKey, viewer.locale());
        }

        return new GuiItem(ItemBuilder.of(Material.COMPASS)
                .displayName(LangManager.text(GuiLangKey.GUI_ISLAND_BIOME_CURRENT_INFO, getViewerLocale()))
                .addLore(Component.empty())
                .addLore(currentBiomeName)
                .addLore(Component.empty())
                .addLore(LangManager.text(GuiLangKey.GUI_ISLAND_BIOME_CURRENT_INFO_LORE, getViewerLocale()))
                .hideAllFlags()
                .build());
    }

    private @NotNull String getBiomeKey(Biome biome) {
        if (biome == Biome.PLAINS) return "biome.plains";
        if (biome == Biome.FOREST) return "biome.forest";
        if (biome == Biome.DESERT) return "biome.desert";
        if (biome == Biome.JUNGLE) return "biome.jungle";
        if (biome == Biome.TAIGA) return "biome.taiga";
        if (biome == Biome.SNOWY_PLAINS) return "biome.snowy_plains";
        if (biome == Biome.SAVANNA) return "biome.savanna";
        if (biome == Biome.SWAMP) return "biome.swamp";
        if (biome == Biome.MUSHROOM_FIELDS) return "biome.mushroom_fields";
        if (biome == Biome.BEACH) return "biome.beach";
        if (biome == Biome.FLOWER_FOREST) return "biome.flower_forest";
        if (biome == Biome.BAMBOO_JUNGLE) return "biome.bamboo_jungle";
        if (biome == Biome.DARK_FOREST) return "biome.dark_forest";
        if (biome == Biome.BIRCH_FOREST) return "biome.birch_forest";
        if (biome == Biome.BADLANDS) return "biome.badlands";
        return "biome." + biome.toString().toLowerCase();
    }
    
    private @NotNull ILangKey getBiomeNameKey(Biome biome) {
        if (biome == Biome.PLAINS) return GeneralLangKey.BIOME_PLAINS_NAME;
        if (biome == Biome.FOREST) return GeneralLangKey.BIOME_FOREST_NAME;
        if (biome == Biome.DESERT) return GeneralLangKey.BIOME_DESERT_NAME;
        if (biome == Biome.JUNGLE) return GeneralLangKey.BIOME_JUNGLE_NAME;
        if (biome == Biome.TAIGA) return GeneralLangKey.BIOME_TAIGA_NAME;
        if (biome == Biome.SNOWY_PLAINS) return GeneralLangKey.BIOME_SNOWY_PLAINS_NAME;
        if (biome == Biome.SAVANNA) return GeneralLangKey.BIOME_SAVANNA_NAME;
        if (biome == Biome.SWAMP) return GeneralLangKey.BIOME_SWAMP_NAME;
        if (biome == Biome.MUSHROOM_FIELDS) return GeneralLangKey.BIOME_MUSHROOM_FIELDS_NAME;
        if (biome == Biome.BEACH) return GeneralLangKey.BIOME_BEACH_NAME;
        if (biome == Biome.FLOWER_FOREST) return GeneralLangKey.BIOME_FLOWER_FOREST_NAME;
        if (biome == Biome.BAMBOO_JUNGLE) return GeneralLangKey.BIOME_BAMBOO_JUNGLE_NAME;
        if (biome == Biome.DARK_FOREST) return GeneralLangKey.BIOME_DARK_FOREST_NAME;
        if (biome == Biome.BIRCH_FOREST) return GeneralLangKey.BIOME_BIRCH_FOREST_NAME;
        if (biome == Biome.BADLANDS) return GeneralLangKey.BIOME_BADLANDS_NAME;
        return GuiLangKey.GUI_ISLAND_BIOME_UNKNOWN;
    }
    
    private @NotNull ILangKey getBiomeDescriptionKey(Biome biome) {
        if (biome == Biome.PLAINS) return GeneralLangKey.BIOME_PLAINS_DESCRIPTION;
        if (biome == Biome.FOREST) return GeneralLangKey.BIOME_FOREST_DESCRIPTION;
        if (biome == Biome.DESERT) return GeneralLangKey.BIOME_DESERT_DESCRIPTION;
        if (biome == Biome.JUNGLE) return GeneralLangKey.BIOME_JUNGLE_DESCRIPTION;
        if (biome == Biome.TAIGA) return GeneralLangKey.BIOME_TAIGA_DESCRIPTION;
        if (biome == Biome.SNOWY_PLAINS) return GeneralLangKey.BIOME_SNOWY_PLAINS_DESCRIPTION;
        if (biome == Biome.SAVANNA) return GeneralLangKey.BIOME_SAVANNA_DESCRIPTION;
        if (biome == Biome.SWAMP) return GeneralLangKey.BIOME_SWAMP_DESCRIPTION;
        if (biome == Biome.MUSHROOM_FIELDS) return GeneralLangKey.BIOME_MUSHROOM_FIELDS_DESCRIPTION;
        if (biome == Biome.BEACH) return GeneralLangKey.BIOME_BEACH_DESCRIPTION;
        if (biome == Biome.FLOWER_FOREST) return GeneralLangKey.BIOME_FLOWER_FOREST_DESCRIPTION;
        if (biome == Biome.BAMBOO_JUNGLE) return GeneralLangKey.BIOME_BAMBOO_JUNGLE_DESCRIPTION;
        if (biome == Biome.DARK_FOREST) return GeneralLangKey.BIOME_DARK_FOREST_DESCRIPTION;
        if (biome == Biome.BIRCH_FOREST) return GeneralLangKey.BIOME_BIRCH_FOREST_DESCRIPTION;
        if (biome == Biome.BADLANDS) return GeneralLangKey.BIOME_BADLANDS_DESCRIPTION;
        return GeneralLangKey.BIOME_PLAINS_DESCRIPTION; // Default fallback
    }


    private GuiItem createBackButton() {
        return new GuiItem(ItemBuilder.of(Material.ARROW)
                .displayName(LangManager.text(GuiLangKey.GUI_BUTTONS_BACK_NAME, getViewerLocale()))
                .addLore(LangManager.text(GuiLangKey.GUI_ISLAND_BIOME_BACK_LORE, getViewerLocale()))
                .hideAllFlags()
                .build()).onAnyClick(player -> {
            player.closeInventory();
            IslandMainGui.create(plugin.getGuiManager(), viewer)
                    .open(viewer);
            SoundUtil.playClickSound(player);
        });
    }
}