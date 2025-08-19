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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        super(player, guiManager, GUI_SIZE, "gui.ingame-settings.title");
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
        IngameSettingsGui gui = new IngameSettingsGui(guiManager, player);
        return gui;
    }

    @Override
    public @NotNull Component getTitle() {
        return trans("settings.ingame-settings.title");
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
                new ItemBuilder(Material.GRASS_BLOCK)
                        .displayName(trans("settings.ingame")
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(trans("settings.gameplay"))
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
                new ItemBuilder(Material.RED_CONCRETE)
                        .displayName(trans("gui-settings.volume-decrease").color(UnifiedColorUtil.ERROR))
                        .addLore(trans("settings.ingame-settings.quest-dialog-speed"))
                        .build(),
                p -> {
                    int newSpeed = settings.adjustDialogSpeed(false);
                    updateDialogSpeedDisplay(settings);
                    playClickSound(p);
                    p.sendMessage(Component.translatable("dialog-speed-changed", Component.text(settings.getDialogSpeedDisplayName())));
                }
        );
        setItem(DIALOG_SPEED_DECREASE_SLOT, speedDecreaseButton);

        // 대화 속도 표시
        updateDialogSpeedDisplay(settings);

        // 속도 증가 버튼
        GuiItem speedIncreaseButton = GuiItem.clickable(
                new ItemBuilder(Material.GREEN_CONCRETE)
                        .displayName(trans("gui-settings.volume-increase").color(UnifiedColorUtil.SUCCESS))
                        .addLore(trans("settings.ingame-settings.quest-dialog-speed"))
                        .build(),
                p -> {
                    int newSpeed = settings.adjustDialogSpeed(true);
                    updateDialogSpeedDisplay(settings);
                    playClickSound(p);
                    p.sendMessage(Component.translatable("dialog-speed-changed", Component.text(settings.getDialogSpeedDisplayName())));
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
                new ItemBuilder(material)
                        .displayName(trans("settings.ingame-settings.quest-dialog-speed")
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(trans("gui-settings.current-volume", "volume", displayName))
                        .addLore(Component.text(speedBar, UnifiedColorUtil.GOLD))
                        .addLore(Component.empty())
                        .addLore(Component.text("속도 값: " + speed + "틱", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("(낮을수록 빠름)", UnifiedColorUtil.GRAY))
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
                new ItemBuilder(enabled ? Material.COMPASS : Material.CLOCK)
                        .displayName(Component.text("🧭 퀘스트 자동 길안내", UnifiedColorUtil.PRIMARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("상태: " + (enabled ? "활성화" : "비활성화"), 
                                enabled ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR))
                        .addLore(Component.empty())
                        .addLore(Component.text("퀘스트 시작 시 자동으로", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("목표 지점까지의 길을", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("파티클로 표시합니다", UnifiedColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 " + (enabled ? "비활성화" : "활성화"), UnifiedColorUtil.YELLOW))
                        .build(),
                p -> {
                    settings.setQuestAutoGuideEnabled(!enabled);
                    updateQuestGuideToggle(settings);
                    playClickSound(p);
                    p.sendMessage(Component.translatable("settings.quest-guide.toggled", Component.text(enabled ? "비활성화" : "활성화")));
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
                new ItemBuilder(enabled ? Material.DIAMOND_SWORD : Material.WOODEN_SWORD)
                        .displayName(Component.text("⚔ 공격 데미지 표시", UnifiedColorUtil.PRIMARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("상태: " + (enabled ? "활성화" : "비활성화"), 
                                enabled ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR))
                        .addLore(Component.empty())
                        .addLore(Component.text("공격 시 데미지 수치를", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("홀로그램으로 표시합니다", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("(3초간 표시됨)", UnifiedColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 " + (enabled ? "비활성화" : "활성화"), UnifiedColorUtil.YELLOW))
                        .build(),
                p -> {
                    settings.setDamageDisplayEnabled(!enabled);
                    updateDamageDisplayToggle(settings);
                    playClickSound(p);
                    p.sendMessage(Component.translatable("settings.damage-display.toggled", Component.text(enabled ? "비활성화" : "활성화")));
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