package com.febrie.rpg.gui.impl.settings;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.PlayerSettings;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * 인게임 설정 GUI
 * 대화 속도, 길안내, 데미지 표시 등의 설정
 *
 * @author Febrie
 */
public class IngameSettingsGui extends BaseGui {

    private static final int GUI_SIZE = 45; // 5 rows

    // 설정 버튼 슬롯
    private static final int DIALOG_SPEED_DECREASE_SLOT = 19;
    private static final int DIALOG_SPEED_DISPLAY_SLOT = 20;
    private static final int DIALOG_SPEED_INCREASE_SLOT = 21;
    
    private static final int QUEST_GUIDE_SLOT = 23;
    private static final int DAMAGE_DISPLAY_SLOT = 25;

    // 타이틀 슬롯
    private static final int TITLE_SLOT = 4;

    private IngameSettingsGui(@NotNull GuiManager guiManager,
                            @NotNull Player player) {
        super(player, guiManager, GUI_SIZE, Component.translatable("gui.ingame-settings.title"));
    }

    /**
     * IngameSettingsGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param player 플레이어
     * @return 초기화된 IngameSettingsGui 인스턴스
     */
    public static IngameSettingsGui create(@NotNull GuiManager guiManager,
                                          @NotNull Player player) {
        return new IngameSettingsGui(guiManager, player);
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("gui.ingame-settings.title");
    }

    @Override
    protected GuiFramework getBackTarget() {
        return PlayerSettingsGui.create(guiManager, viewer);
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
        setupSettingControls();
        setupStandardNavigation(false, true);
    }

    /**
     * 장식 요소 설정
     */
    private void setupDecorations() {
        createBorder();
        setupTitleItem();
    }

    /**
     * 타이틀 아이템 설정
     */
    private void setupTitleItem() {
        GuiItem titleItem = GuiItem.display(
                ItemBuilder.of(Material.GRASS_BLOCK, viewer.locale())
                        .displayNameTranslated("items.settings.ingame-settings.title.name")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.ingame-settings.title.lore")
                        .hideAllFlags()
                        .build()
        );
        setItem(TITLE_SLOT, titleItem);
    }

    /**
     * 설정 컨트롤 설정
     */
    private void setupSettingControls() {
        RPGPlayer rpgPlayer = RPGMain.getPlugin().getRPGPlayerManager().getOrCreatePlayer(viewer);
        PlayerSettings settings = rpgPlayer.getPlayerSettings();

        setupDialogSpeedControls(settings);
        setupQuestGuideToggle(settings);
        setupDamageDisplayToggle(settings);

        // 빈 슬롯들을 투명한 유리판으로 채우기
        for (int i = 0; i < GUI_SIZE; i++) {
            if (getItem(i) == null) {
                setItem(i, GuiFactory.createDecoration());
            }
        }
    }

    /**
     * 대화 속도 조절 컨트롤 설정
     */
    private void setupDialogSpeedControls(PlayerSettings settings) {
        // 속도 감소 버튼
        GuiItem speedDecreaseButton = GuiItem.clickable(
                ItemBuilder.of(Material.RED_CONCRETE, viewer.locale())
                        .displayNameTranslated("items.settings.ingame-settings.speed-decrease.name")
                        .addLoreTranslated("items.settings.ingame-settings.speed-decrease.lore")
                        .hideAllFlags()
                        .build(),
                p -> {
                    int newSpeed = settings.adjustDialogSpeed(false);
                    updateDialogSpeedDisplay(settings);
                    playClickSound(p);
                    p.sendMessage(LangManager.get("gui.ingame-settings.dialog-speed-changed", p, Component.text(settings.getDialogSpeedDisplayName())));
                }
        );
        setItem(DIALOG_SPEED_DECREASE_SLOT, speedDecreaseButton);

        // 대화 속도 표시
        updateDialogSpeedDisplay(settings);

        // 속도 증가 버튼
        GuiItem speedIncreaseButton = GuiItem.clickable(
                ItemBuilder.of(Material.GREEN_CONCRETE, viewer.locale())
                        .displayNameTranslated("items.settings.ingame-settings.speed-increase.name")
                        .addLoreTranslated("items.settings.ingame-settings.speed-increase.lore")
                        .hideAllFlags()
                        .build(),
                p -> {
                    int newSpeed = settings.adjustDialogSpeed(true);
                    updateDialogSpeedDisplay(settings);
                    playClickSound(p);
                    p.sendMessage(LangManager.get("gui.ingame-settings.dialog-speed-changed", p, Component.text(settings.getDialogSpeedDisplayName())));
                }
        );
        setItem(DIALOG_SPEED_INCREASE_SLOT, speedIncreaseButton);
    }

    /**
     * 퀘스트 길안내 토글 설정
     */
    private void setupQuestGuideToggle(PlayerSettings settings) {
        updateQuestGuideToggle(settings);
    }

    /**
     * 데미지 표시 토글 설정
     */
    private void setupDamageDisplayToggle(PlayerSettings settings) {
        updateDamageDisplayToggle(settings);
    }

    /**
     * 대화 속도 표시 아이템 업데이트
     */
    private void updateDialogSpeedDisplay(PlayerSettings settings) {
        int speed = settings.getDialogSpeed();
        String displayName = settings.getDialogSpeedDisplayName();
        
        Material material = switch (speed) {
            case 1 -> Material.DIAMOND;
            case 2 -> Material.EMERALD;
            case 3, 4 -> Material.GOLD_INGOT;
            case 5, 6 -> Material.IRON_INGOT;
            default -> Material.COAL;
        };

        String speedBar = createSpeedBar(speed);
        
        GuiItem dialogSpeedDisplay = GuiItem.display(
                ItemBuilder.of(material, viewer.locale())
                        .displayNameTranslated("items.settings.ingame-settings.dialog-speed.name")
                        .addLore(Component.empty())
                        .addLore(LangManager.get("gui.ingame-settings.current-speed", viewer, Component.text(displayName)))
                        .addLore(Component.text(speedBar, UnifiedColorUtil.GOLD))
                        .addLore(Component.empty())
                        .addLore(LangManager.get("gui.ingame-settings.speed-value", viewer, Component.text(String.valueOf(speed))))
                        .addLoreTranslated("items.settings.ingame-settings.dialog-speed.note")
                        .hideAllFlags()
                        .build()
        );
        setItem(DIALOG_SPEED_DISPLAY_SLOT, dialogSpeedDisplay);
    }

    /**
     * 퀘스트 길안내 토글 업데이트
     */
    private void updateQuestGuideToggle(PlayerSettings settings) {
        boolean enabled = settings.isQuestAutoGuideEnabled();
        
        GuiItem questGuideToggle = GuiItem.clickable(
                ItemBuilder.of(enabled ? Material.COMPASS : Material.CLOCK, viewer.locale())
                        .displayNameTranslated("items.settings.ingame-settings.quest-guide.name")
                        .addLore(Component.empty())
                        .addLore(LangManager.get("gui.ingame-settings.status", viewer,
                                Component.translatable(enabled ? "status.enabled" : "status.disabled")
                                .color(enabled ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR)))
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.ingame-settings.quest-guide.desc1")
                        .addLoreTranslated("items.settings.ingame-settings.quest-guide.desc2")
                        .addLoreTranslated("items.settings.ingame-settings.quest-guide.desc3")
                        .addLore(Component.empty())
                        .addLore(LangManager.get("gui.ingame-settings.click-to-toggle", viewer,
                                Component.translatable(enabled ? "action.disable" : "action.enable")))
                        .hideAllFlags()
                        .build(),
                p -> {
                    settings.setQuestAutoGuideEnabled(!enabled);
                    updateQuestGuideToggle(settings);
                    playClickSound(p);
                    p.sendMessage(LangManager.get("gui.ingame-settings.quest-guide-toggled", p,
                            Component.translatable(settings.isQuestAutoGuideEnabled() ? "status.enabled" : "status.disabled")));
                }
        );
        setItem(QUEST_GUIDE_SLOT, questGuideToggle);
    }

    /**
     * 데미지 표시 토글 업데이트
     */
    private void updateDamageDisplayToggle(PlayerSettings settings) {
        boolean enabled = settings.isDamageDisplayEnabled();
        
        GuiItem damageDisplayToggle = GuiItem.clickable(
                ItemBuilder.of(enabled ? Material.DIAMOND_SWORD : Material.WOODEN_SWORD, viewer.locale())
                        .displayNameTranslated("items.settings.ingame-settings.damage-display.name")
                        .addLore(Component.empty())
                        .addLore(LangManager.get("gui.ingame-settings.status", viewer,
                                Component.translatable(enabled ? "status.enabled" : "status.disabled")
                                .color(enabled ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR)))
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.ingame-settings.damage-display.desc1")
                        .addLoreTranslated("items.settings.ingame-settings.damage-display.desc2")
                        .addLoreTranslated("items.settings.ingame-settings.damage-display.desc3")
                        .addLore(Component.empty())
                        .addLore(LangManager.get("gui.ingame-settings.click-to-toggle", viewer,
                                Component.translatable(enabled ? "action.disable" : "action.enable")))
                        .hideAllFlags()
                        .build(),
                p -> {
                    settings.setDamageDisplayEnabled(!enabled);
                    updateDamageDisplayToggle(settings);
                    playClickSound(p);
                    p.sendMessage(LangManager.get("gui.ingame-settings.damage-display-toggled", p,
                            Component.translatable(settings.isDamageDisplayEnabled() ? "status.enabled" : "status.disabled")));
                }
        );
        setItem(DAMAGE_DISPLAY_SLOT, damageDisplayToggle);
    }

    /**
     * 속도 바 생성
     */
    private String createSpeedBar(int speed) {
        StringBuilder bar = new StringBuilder();
        int filledBars = 11 - speed; // 속도가 낮을수록 바가 많이 채워짐 (빠름)
        
        for (int i = 0; i < 10; i++) {
            if (i < filledBars) {
                bar.append("■");
            } else {
                bar.append("□");
            }
        }
        
        return bar.toString();
    }
    
}