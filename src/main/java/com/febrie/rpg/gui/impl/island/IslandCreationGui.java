package com.febrie.rpg.gui.impl.island;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.system.MainMenuGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private String islandName = "나의 섬";
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

    private IslandCreationGui(@NotNull GuiManager guiManager,
                              @NotNull LangManager langManager,
                              @NotNull Player player) {
        super(player, guiManager, langManager, GUI_SIZE, "gui.island.creation.title");
        this.islandManager = RPGMain.getInstance().getIslandManager();
    }

    /**
     * Factory method to create the GUI
     */
    public static IslandCreationGui create(@NotNull GuiManager guiManager,
                                           @NotNull LangManager langManager,
                                           @NotNull Player player) {
        IslandCreationGui gui = new IslandCreationGui(guiManager, langManager, player);
        return createAndInitialize(gui, "gui.island.creation.title");
    }

    @Override
    public @NotNull Component getTitle() {
        return trans("gui.island.creation.title");
    }

    @Override
    protected GuiFramework getBackTarget() {
        return MainMenuGui.create(guiManager, langManager, viewer);
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
                new ItemBuilder(Material.GRASS_BLOCK)
                        .displayName(trans("items.island.creation.title.name"))
                        .lore(langManager.getComponentList(viewer, "items.island.creation.title.lore"))
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
                new ItemBuilder(Material.NAME_TAG)
                        .displayName(trans("items.island.creation.name.name"))
                        .lore(List.of(
                                Component.text(""),
                                Component.text("현재 이름: ", NamedTextColor.GRAY)
                                        .append(Component.text(islandName, ColorUtil.parseHexColor(islandColorHex))),
                                Component.text(""),
                                Component.text("클릭하여 이름 변경", NamedTextColor.YELLOW),
                                Component.text("")
                        ))
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
                                    islandName = text;
                                    updateNameItem();
                                    updateCreateButton();
                                    return List.of(AnvilGUI.ResponseAction.close());
                                } else {
                                    return List.of(AnvilGUI.ResponseAction.replaceInputText("1-20자 사이로 입력하세요"));
                                }
                            })
                            .text(islandName)
                            .title("섬 이름 입력")
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
                new ItemBuilder(woolType)
                        .displayName(trans("items.island.creation.color.name"))
                        .lore(List.of(
                                Component.text(""),
                                Component.text("현재 색상: ", NamedTextColor.GRAY)
                                        .append(Component.text("███", ColorUtil.parseHexColor(islandColorHex)))
                                        .append(Component.text(" " + getColorName(islandColorHex), NamedTextColor.WHITE)),
                                Component.text("Hex: " + islandColorHex, NamedTextColor.GRAY),
                                Component.text(""),
                                Component.text("좌클릭: ", NamedTextColor.YELLOW)
                                        .append(Component.text("다음 (" + nextColorName + ")", NamedTextColor.GRAY)),
                                Component.text("우클릭: ", NamedTextColor.YELLOW)
                                        .append(Component.text("이전 (" + prevColorName + ")", NamedTextColor.GRAY)),
                                Component.text("가운데 클릭: ", NamedTextColor.AQUA)
                                        .append(Component.text("직접 HEX 코드 입력", NamedTextColor.GRAY)),
                                Component.text(""),
                                Component.text("예시: #FFFFFF (흰색)", NamedTextColor.DARK_GRAY)
                        ))
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
                            String text = stateSnapshot.getText().trim();
                            if (text.matches("^#[0-9A-Fa-f]{6}$")) {
                                islandColorHex = text.toUpperCase();
                                updateColorItem();
                                updateNameItem();
                                updateCreateButton();
                                return List.of(AnvilGUI.ResponseAction.close());
                            } else {
                                return List.of(AnvilGUI.ResponseAction.replaceInputText("#RRGGBB 형식으로 입력"));
                            }
                        })
                        .text(islandColorHex)
                        .title("HEX 색상 코드 입력")
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
                new ItemBuilder(biomeIcon)
                        .displayName(trans("items.island.creation.biome.name"))
                        .lore(List.of(
                                Component.text(""),
                                Component.text("현재 바이옴: ", NamedTextColor.GRAY)
                                        .append(Component.text(getBiomeName(selectedBiome), NamedTextColor.GREEN)),
                                Component.text(""),
                                Component.text("▶ 클릭하여 변경", NamedTextColor.YELLOW),
                                Component.text("")
                        ))
                        .build(),
                player -> {
                    // 바이옴 선택 GUI 열기
                    IslandBiomeSelectionGui biomeGui = IslandBiomeSelectionGui.create(
                            guiManager, langManager, player, selectedBiome,
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

            GuiItem templateItem = GuiItem.clickable(
                    new ItemBuilder(getTemplateIcon(template))
                            .displayName(Component.text(getTemplateName(template),
                                            selected ? NamedTextColor.GREEN : NamedTextColor.GRAY)
                                    .decoration(TextDecoration.BOLD, selected))
                            .lore(List.of(
                                    Component.text(""),
                                    Component.text(getTemplateDescription(template), NamedTextColor.GRAY),
                                    Component.text(""),
                                    selected ?
                                            Component.text("✔ 선택됨", NamedTextColor.GREEN) :
                                            Component.text("클릭하여 선택", NamedTextColor.YELLOW),
                                    Component.text("")
                            ))
                            .glint(selected)
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
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(""));
        lore.add(Component.text("선택된 설정:", NamedTextColor.YELLOW));
        lore.add(Component.text("• 이름: ", NamedTextColor.GRAY)
                .append(Component.text(islandName, ColorUtil.parseHexColor(islandColorHex))));
        lore.add(Component.text("• 바이옴: ", NamedTextColor.GRAY)
                .append(Component.text(getBiomeName(selectedBiome), NamedTextColor.GREEN)));
        lore.add(Component.text("• 템플릿: ", NamedTextColor.GRAY)
                .append(Component.text(getTemplateName(selectedTemplate), NamedTextColor.AQUA)));
        lore.add(Component.text(""));
        lore.add(Component.text("클릭하여 섬 생성!", NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true));
        lore.add(Component.text(""));

        // 섬 생성 처리
        GuiItem createButton = GuiItem.clickable(
                new ItemBuilder(Material.LIME_CONCRETE)
                        .displayName(Component.text("✦ 섬 생성하기 ✦", NamedTextColor.GREEN)
                                .decoration(TextDecoration.BOLD, true))
                        .lore(lore)
                        .glint(true)
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
        player.sendMessage(Component.text("섬을 생성하는 중...", NamedTextColor.YELLOW));

        // 비동기로 섬 생성
        islandManager.createIsland(player, islandName, islandColorHex, selectedBiome, selectedTemplate)
                .thenAccept(success -> {
                    if (success) {
                        player.sendMessage(Component.text(""));
                        player.sendMessage(Component.text("==== 섬 생성 완료! ====", NamedTextColor.GREEN));
                        player.sendMessage(Component.text(""));
                        player.sendMessage(Component.text("✦ ", NamedTextColor.GREEN)
                                .append(Component.text(islandName, ColorUtil.parseHexColor(islandColorHex))
                                        .decoration(TextDecoration.BOLD, true))
                                .append(Component.text(" 섬이 생성되었습니다!", NamedTextColor.GREEN)));
                        player.sendMessage(Component.text(""));
                        player.sendMessage(Component.text("잠시 후 섬으로 이동합니다...", NamedTextColor.YELLOW));
                        player.sendMessage(Component.text("====================", NamedTextColor.GREEN));

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
                        player.sendMessage(Component.text("섬 생성에 실패했습니다.", NamedTextColor.RED));
                    }
                })
                .exceptionally(ex -> {
                    player.sendMessage(Component.text("섬 생성 중 오류가 발생했습니다: " + ex.getMessage(), NamedTextColor.RED));
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
        return switch (biome) {
            case "PLAINS" -> "평원";
            case "FOREST" -> "숲";
            case "DESERT" -> "사막";
            case "SNOWY_PLAINS" -> "설원";
            case "JUNGLE" -> "정글";
            case "SWAMP" -> "늪";
            case "SAVANNA" -> "사바나";
            case "MUSHROOM_FIELDS" -> "버섯 들판";
            default -> biome;
        };
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
        return switch (template) {
            case "BASIC" -> "기본 섬";
            case "SKYBLOCK" -> "스카이블럭";
            case "LARGE" -> "대형 섬";
            case "WATER" -> "수상 섬";
            default -> template;
        };
    }

    /**
     * 템플릿 설명
     */
    private String getTemplateDescription(String template) {
        return switch (template) {
            case "BASIC" -> "표준 크기의 기본 섬";
            case "SKYBLOCK" -> "하늘에 떠있는 작은 섬";
            case "LARGE" -> "넓은 공간의 대형 섬";
            case "WATER" -> "바다 위의 수상 섬";
            default -> "";
        };
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT, ClickType.RIGHT, ClickType.MIDDLE);
    }

    /**
     * 색상 이름 반환
     */
    private String getColorName(String hex) {
        return switch (hex) {
            case "#FFFF00" -> "노란색";
            case "#00FF00" -> "초록색";
            case "#00FFFF" -> "하늘색";
            case "#FF00FF" -> "보라색";
            case "#FF0000" -> "빨간색";
            case "#FFA500" -> "주황색";
            case "#0000FF" -> "파란색";
            case "#FFFFFF" -> "흰색";
            default -> "사용자 정의";
        };
    }
}