package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.system.MainMenuGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.bukkit.Bukkit;
import java.util.List;

/**
 * 섬 생성 GUI
 * 섬 이름, 색상, 바이옴, 템플릿을 선택할 수 있는 인터페이스
 *
 * @author CoffeeTory
 */
public class IslandCreationGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6 rows

    // 선택 영역 슬롯
    private static final int NAME_SLOT = 11;
    private static final int COLOR_SLOT = 13;
    private static final int BIOME_SLOT = 15;
    private static final int TEMPLATE_START_SLOT = 28;

    // 생성 버튼
    private static final int CREATE_BUTTON_SLOT = 49;

    // 선택된 값들
    private String islandName;
    private String islandColorHex = "#FFFF00"; // 기본 노란색
    private String selectedBiome = "PLAINS";
    private String selectedTemplate = "BASIC";

    // 사용 가능한 색상들 (Hex)
    private static final List<String> AVAILABLE_COLORS = Arrays.asList(
            "#FFFF00", // 노란색
            "#00FF00", // 초록색
            "#00FFFF", // 하늘색
            "#FF00FF", // 보라색
            "#FF0000", // 빨간색
            "#FFA500", // 주황색
            "#0000FF", // 파란색
            "#FFFFFF"  // 흰색
    );

    // 사용 가능한 바이옴들
    private static final List<String> AVAILABLE_BIOMES = Arrays.asList(
            "PLAINS",
            "FOREST",
            "DESERT",
            "SNOWY_PLAINS",
            "JUNGLE",
            "SWAMP",
            "SAVANNA",
            "MUSHROOM_FIELDS"
    );

    // 템플릿 종류
    private static final List<String> AVAILABLE_TEMPLATES = Arrays.asList(
            "BASIC",
            "SKYBLOCK",
            "LARGE",
            "WATER"
    );

    private final IslandManager islandManager;

    private IslandCreationGui(@NotNull GuiManager guiManager, @NotNull Player player) {
        super(player, guiManager, GUI_SIZE, LangManager.getComponent("gui.island.creation.title".replace("-", "_"), viewer));
        this.islandManager = RPGMain.getInstance().getIslandManager();
        this.islandName = LangManager.getString("island.default-name", player.locale());
    }

    /**
     * Factory method to create the GUI
     */
    public static IslandCreationGui create(@NotNull GuiManager guiManager, @NotNull Player player) {
        return new IslandCreationGui(guiManager, player);
    }

    @Override
    public @NotNull Component getTitle() {
        return LangManager.getComponent("gui.island.creation.title".replace("-", "_"), viewer);
    }

    @Override
    protected GuiFramework getBackTarget() {
        return MainMenuGui.create(guiManager, viewer);
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
        setupSelectionItems();
        setupCreateButton();
        setupStandardNavigation(false, false);  // 새로고침과 닫기 버튼 둘 다 제거
    }

    /**
     * 장식 요소 설정
     */
    private void setupDecorations() {
        createBorder();

        // 제목 아이템
        GuiItem titleItem = GuiItem.display(
                ItemBuilder.of(Material.GRASS_BLOCK, getViewerLocale())
                        .displayNameTranslated("items.island.creation.title.name")
                        .addLoreTranslated("items.island.creation.title.lore")
                        .hideAllFlags()
                        .build()
        );
        setItem(4, titleItem);
    }

    /**
     * 선택 아이템들 설정
     */
    private void setupSelectionItems() {
        // 섬 이름 설정
        updateNameItem();

        // 섬 색상 설정
        updateColorItem();

        // 바이옴 설정
        updateBiomeItem();

        // 템플릿 선택
        updateTemplateItems();
    }

    /**
     * 섬 이름 아이템 업데이트
     */
    private void updateNameItem() {
        GuiItem nameItem = GuiItem.clickable(
                ItemBuilder.of(Material.NAME_TAG, getViewerLocale())
                        .displayNameTranslated("items.island.creation.name.name")
                        .loreTranslated("items.island.creation.name.lore", 
                                UnifiedColorUtil.parseHexColor(islandColorHex) + islandName)
                        .hideAllFlags()
                        .build(),
                player -> {
                    // Anvil GUI로 이름 입력
                    new AnvilGUI.Builder()
                            .onClick((slot, stateSnapshot) -> {
                                if (slot != AnvilGUI.Slot.OUTPUT) {
                                    return Collections.emptyList();
                                }
                                String text = stateSnapshot.getText();
                                if (!text.isEmpty() && text.length() <= 20) {
                                    // 메인 스레드에서 GUI 업데이트 실행
                                    Bukkit.getScheduler().runTask(RPGMain.getInstance(), () -> {
                                        islandName = text;
                                        updateNameItem();
                                        updateCreateButton();
                                        // GUI 다시 열기
                                        guiManager.openGui(stateSnapshot.getPlayer(), this);
                                    });
                                    return List.of(AnvilGUI.ResponseAction.close());
                                } else {
                                    return List.of(AnvilGUI.ResponseAction.replaceInputText(
                                            PlainTextComponentSerializer.plainText()
                                                    .serialize(Component.translatable("island.gui.creation.island-name-input-error"))));
                                }
                            })
                            .text(islandName)
                            .itemLeft(new org.bukkit.inventory.ItemStack(Material.NAME_TAG))
                            .title(net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                                    .serialize(Component.translatable("island.gui.creation.island-name-input-title")))
                            .plugin(RPGMain.getInstance())
                            .open(player);
                }
        );
        setItem(NAME_SLOT, nameItem);
    }

    /**
     * 색상 선택 아이템 업데이트
     */
    private void updateColorItem() {
        // 현재 색상으로 양털 색상 결정
        Material woolType = getWoolByHex(islandColorHex);

        // 다음/이전 색상 찾기
        int currentIndex = AVAILABLE_COLORS.indexOf(islandColorHex);
        int nextIndex = (currentIndex + 1) % AVAILABLE_COLORS.size();
        int prevIndex = (currentIndex - 1 + AVAILABLE_COLORS.size()) % AVAILABLE_COLORS.size();
        String nextColorName = getColorName(AVAILABLE_COLORS.get(nextIndex));
        String prevColorName = getColorName(AVAILABLE_COLORS.get(prevIndex));

        GuiItem colorItem = new GuiItem(
                ItemBuilder.of(woolType, getViewerLocale())
                        .displayNameTranslated("items.island.creation.color.name")
                        .loreTranslated("items.island.creation.color.lore",
                                UnifiedColorUtil.parseHexColor(islandColorHex) + "███",
                                getColorName(islandColorHex),
                                islandColorHex,
                                nextColorName,
                                prevColorName)
                        .hideAllFlags()
                        .build()
        ).onAnyClick((player, clickType) -> {
            int currentColorIndex = AVAILABLE_COLORS.indexOf(islandColorHex);
            if (clickType == ClickType.LEFT) {
                int newIndex = (currentColorIndex + 1) % AVAILABLE_COLORS.size();
                islandColorHex = AVAILABLE_COLORS.get(newIndex);
                updateColorItem();
                updateNameItem();
                updateCreateButton();
                playClickSound(player);
            } else if (clickType == ClickType.RIGHT) {
                int newIndex = (currentColorIndex - 1 + AVAILABLE_COLORS.size()) % AVAILABLE_COLORS.size();
                islandColorHex = AVAILABLE_COLORS.get(newIndex);
                updateColorItem();
                updateNameItem();
                updateCreateButton();
                playClickSound(player);
            } else if (clickType == ClickType.MIDDLE) {
                // AnvilGUI로 HEX 코드 입력
                new AnvilGUI.Builder()
                        .onClick((slot, stateSnapshot) -> {
                            if (slot != AnvilGUI.Slot.OUTPUT) {
                                return Collections.emptyList();
                            }
                            String trimmedText = stateSnapshot.getText().trim();
                            if (trimmedText.matches("^#[0-9A-Fa-f]{6}$")) {
                                // 메인 스레드에서 GUI 업데이트 실행
                                Bukkit.getScheduler().runTask(RPGMain.getInstance(), () -> {
                                    islandColorHex = trimmedText.toUpperCase();
                                    updateColorItem();
                                    updateNameItem();
                                    updateCreateButton();
                                    // GUI 다시 열기
                                    guiManager.openGui(stateSnapshot.getPlayer(), this);
                                });
                                return List.of(AnvilGUI.ResponseAction.close());
                            } else {
                                return List.of(AnvilGUI.ResponseAction.replaceInputText(
                                        PlainTextComponentSerializer.plainText()
                                                .serialize(Component.translatable("island.gui.creation.hex-input-error"))));
                            }
                        })
                        .text(islandColorHex)
                        .itemLeft(new org.bukkit.inventory.ItemStack(Material.PAPER))
                        .title(net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                                .serialize(Component.translatable("island.gui.creation.hex-input-title")))
                        .plugin(RPGMain.getInstance())
                        .open(player);
            }
        });
        setItem(COLOR_SLOT, colorItem);
    }

    /**
     * 바이옴 선택 아이템 업데이트
     */
    private void updateBiomeItem() {
        Material biomeIcon = getBiomeIcon(selectedBiome);
        
        GuiItem biomeItem = GuiItem.clickable(
                ItemBuilder.of(biomeIcon, getViewerLocale())
                        .displayNameTranslated("items.island.creation.biome.name")
                        .loreTranslated("items.island.creation.biome.lore", 
                                getBiomeName(selectedBiome))
                        .hideAllFlags()
                        .build(),
                player -> {
                    // 바이옴 선택 GUI 열기
                    IslandBiomeSelectionGui biomeGui = IslandBiomeSelectionGui.create(
                            guiManager, player, selectedBiome,
                            biome -> {
                                selectedBiome = biome;
                                updateBiomeItem();
                                updateCreateButton();
                            },
                            this  // 뒤로가기 시 이 GUI로 돌아옴
                    );
                    guiManager.openGui(player, biomeGui);
                }
        );
        setItem(BIOME_SLOT, biomeItem);
    }

    /**
     * 템플릿 아이템들 업데이트
     */
    private void updateTemplateItems() {
        for (int i = 0; i < AVAILABLE_TEMPLATES.size(); i++) {
            String template = AVAILABLE_TEMPLATES.get(i);
            boolean selected = template.equals(selectedTemplate);

            Component statusText = selected ? 
                    LangManager.getComponent("island.gui.creation.selected", getViewerLocale()).color(UnifiedColorUtil.SUCCESS) :
                    LangManager.getComponent("island.gui.creation.click-to-select", getViewerLocale()).color(UnifiedColorUtil.YELLOW);
            
            GuiItem templateItem = GuiItem.clickable(
                    ItemBuilder.of(getTemplateIcon(template), getViewerLocale())
                            .displayNameTranslated("items.island.creation.template." + template.toLowerCase() + ".name")
                            .addLoreTranslated("items.island.creation.template." + template.toLowerCase() + ".lore")
                            .addLore(statusText)
                            .glint(selected)
                            .hideAllFlags()
                            .build(),
                    player -> {
                        selectedTemplate = template;
                        updateTemplateItems();
                        playClickSound(player);
                    }
            );
            setItem(TEMPLATE_START_SLOT + i, templateItem);
        }
    }

    /**
     * 생성 버튼 설정
     */
    private void setupCreateButton() {
        updateCreateButton();
    }

    /**
     * 생성 버튼 업데이트
     */
    private void updateCreateButton() {
        // 섬 생성 처리
        GuiItem createButton = GuiItem.clickable(
                ItemBuilder.of(Material.LIME_CONCRETE, getViewerLocale())
                        .displayNameTranslated("items.island.creation.create-button.name")
                        .loreTranslated("items.island.creation.create-button.lore",
                                UnifiedColorUtil.parseHexColor(islandColorHex) + islandName,
                                getBiomeName(selectedBiome),
                                getTemplateName(selectedTemplate))
                        .glint(true)
                        .hideAllFlags()
                        .build(),
                this::createIsland
        );
        setItem(CREATE_BUTTON_SLOT, createButton);
    }

    /**
     * 섬 생성 처리
     */
    private void createIsland(@NotNull Player player) {
        player.closeInventory();
        player.sendMessage(Component.translatable("island.gui.creation.creating-island").color(NamedTextColor.YELLOW));

        // 비동기로 섬 생성
        islandManager.createIsland(player, islandName, islandColorHex, selectedBiome, selectedTemplate)
                .thenAccept(success -> {
                    if (success) {
                        player.sendMessage(Component.text(""));
                        player.sendMessage(Component.translatable("island.gui.creation.creation-complete-header").color(NamedTextColor.GREEN));
                        player.sendMessage(Component.text(""));
                        player.sendMessage(Component.text("✦ ", NamedTextColor.GREEN)
                                .append(Component.text(islandName, UnifiedColorUtil.parseHexColor(islandColorHex))
                                        .decoration(TextDecoration.BOLD, true))
                                .append(Component.translatable("island.gui.creation.creation-complete-message").color(NamedTextColor.GREEN)));
                        player.sendMessage(Component.text(""));
                        player.sendMessage(Component.translatable("island.gui.creation.teleporting-soon").color(NamedTextColor.YELLOW));
                        player.sendMessage(Component.translatable("island.gui.creation.creation-complete-footer").color(NamedTextColor.GREEN));

                        // 섬으로 텔레포트 - 메인 스레드에서 실행
                        org.bukkit.Bukkit.getScheduler().runTaskLater(RPGMain.getInstance(), () -> {
                            // 생성된 섬 로드 후 텔레포트
                            islandManager.loadIsland(player.getUniqueId().toString()).thenAccept(island -> {
                                if (island != null) {
                                    org.bukkit.Bukkit.getScheduler().runTask(RPGMain.getInstance(), () -> {
                                        player.teleport(island.getSpawnLocation());
                                    });
                                }
                            });
                        }, 20L); // 1초 후 실행
                    } else {
                        player.sendMessage(Component.translatable("island.gui.creation.creation-failed").color(NamedTextColor.RED));
                    }
                })
                .exceptionally(ex -> {
                    player.sendMessage(Component.translatable("island.gui.creation.creation-error").color(NamedTextColor.RED)
                            .append(Component.text(ex.getMessage(), NamedTextColor.RED)));
                    return null;
                });
    }

    /**
     * Hex 색상에 따른 양털 아이템 반환
     */
    private Material getWoolByHex(String hex) {
        return switch (hex) {
            case "#FFFF00" -> Material.YELLOW_WOOL;
            case "#00FF00" -> Material.LIME_WOOL;
            case "#00FFFF" -> Material.LIGHT_BLUE_WOOL;
            case "#FF00FF" -> Material.MAGENTA_WOOL;
            case "#FF0000" -> Material.RED_WOOL;
            case "#FFA500" -> Material.ORANGE_WOOL;
            case "#0000FF" -> Material.BLUE_WOOL;
            default -> Material.WHITE_WOOL;
        };
    }

    /**
     * 바이옴 아이콘 반환
     */
    private Material getBiomeIcon(String biome) {
        return switch (biome) {
            case "FOREST" -> Material.OAK_SAPLING;
            case "DESERT" -> Material.SAND;
            case "SNOWY_PLAINS" -> Material.SNOW_BLOCK;
            case "JUNGLE" -> Material.JUNGLE_SAPLING;
            case "SWAMP" -> Material.LILY_PAD;
            case "SAVANNA" -> Material.ACACIA_SAPLING;
            case "MUSHROOM_FIELDS" -> Material.RED_MUSHROOM;
            default -> Material.GRASS_BLOCK;
        };
    }

    /**
     * 바이옴 표시 이름
     */
    private String getBiomeName(String biome) {
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(
                Component.translatable("island.gui.creation.biomes." + biome.toLowerCase()));
    }

    /**
     * 템플릿 아이콘
     */
    private Material getTemplateIcon(String template) {
        return switch (template) {
            case "BASIC" -> Material.GRASS_BLOCK;
            case "SKYBLOCK" -> Material.COBBLESTONE;
            case "LARGE" -> Material.DIAMOND_BLOCK;
            case "WATER" -> Material.WATER_BUCKET;
            default -> Material.GRASS_BLOCK;
        };
    }

    /**
     * 템플릿 이름
     */
    private String getTemplateName(String template) {
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(
                Component.translatable("island.gui.creation.templates." + template.toLowerCase() + ".name"));
    }

    /**
     * 템플릿 설명
     */
    private String getTemplateDescription(String template) {
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(
                Component.translatable("island.gui.creation.templates." + template.toLowerCase() + ".desc"));
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT, ClickType.RIGHT, ClickType.MIDDLE);
    }

    /**
     * 색상 이름 반환
     */
    private String getColorName(String hex) {
        String colorKey = switch (hex) {
            case "#FFFF00" -> "yellow";
            case "#00FF00" -> "green";
            case "#00FFFF" -> "cyan";
            case "#FF00FF" -> "magenta";
            case "#FF0000" -> "red";
            case "#FFA500" -> "orange";
            case "#0000FF" -> "blue";
            case "#FFFFFF" -> "white";
            default -> "custom";
        };
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(
                Component.translatable("island.gui.creation.colors." + colorKey));
    }
    
}