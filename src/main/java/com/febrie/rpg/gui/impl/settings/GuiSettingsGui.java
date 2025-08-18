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
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * GUI 설정 GUI
 * GUI 사운드 볼륨 및 음소거 설정
 *
 * @author Febrie
 */
public class GuiSettingsGui extends BaseGui {

    private static final int GUI_SIZE = 27; // 3 rows

    // 설정 버튼 슬롯
    private static final int VOLUME_DECREASE_SLOT = 10;
    private static final int VOLUME_DISPLAY_SLOT = 11;
    private static final int VOLUME_INCREASE_SLOT = 12;
    private static final int MUTE_TOGGLE_SLOT = 14;

    // 타이틀 슬롯
    private static final int TITLE_SLOT = 4;

    private GuiSettingsGui(@NotNull GuiManager guiManager,
                         @NotNull Player player) {
        super(player, guiManager, GUI_SIZE, "gui-settings.title");
    }

    /**
     * GuiSettingsGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param player 플레이어
     * @return 초기화된 GuiSettingsGui 인스턴스
     */
    public static GuiSettingsGui create(@NotNull GuiManager guiManager,
                                       @NotNull Player player) {
        GuiSettingsGui gui = new GuiSettingsGui(guiManager, player);
        return gui;
    }

    @Override
    public @NotNull Component getTitle() {
        return com.febrie.rpg.util.LangManager.getComponent(viewer, "gui-settings.title").color(UnifiedColorUtil.UNCOMMON);
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
                new ItemBuilder(Material.IRON_TRAPDOOR)
                        .displayName(com.febrie.rpg.util.LangManager.getComponent(viewer, "gui-settings.gui-settings")
                                .color(UnifiedColorUtil.UNCOMMON)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(com.febrie.rpg.util.LangManager.getComponent(viewer, "gui-settings.gui-settings-desc").color(UnifiedColorUtil.GRAY))
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

        // 볼륨 감소 버튼
        GuiItem volumeDecreaseButton = GuiItem.clickable(
                new ItemBuilder(Material.RED_CONCRETE)
                        .displayName(com.febrie.rpg.util.LangManager.getComponent(viewer, "gui-settings.volume-decrease").color(UnifiedColorUtil.ERROR))
                        .addLore(com.febrie.rpg.util.LangManager.getComponent(viewer, "gui-settings.click-decrease").color(UnifiedColorUtil.GRAY))
                        .build(),
                p -> {
                    int currentVolume = settings.getGuiSoundVolume();
                    int newVolume = Math.max(0, currentVolume - 5);
                    settings.setGuiSoundVolume(newVolume);
                    
                    updateVolumeDisplay();
                    playClickSound(p);
                    com.febrie.rpg.util.LangManager.sendMessage(p, "gui-settings.volume-changed", "{volume}", String.valueOf(newVolume));
                }
        );
        setItem(VOLUME_DECREASE_SLOT, volumeDecreaseButton);

        // 볼륨 표시
        updateVolumeDisplay();

        // 볼륨 증가 버튼
        GuiItem volumeIncreaseButton = GuiItem.clickable(
                new ItemBuilder(Material.GREEN_CONCRETE)
                        .displayName(com.febrie.rpg.util.LangManager.getComponent(viewer, "gui-settings.volume-increase").color(UnifiedColorUtil.SUCCESS))
                        .addLore(com.febrie.rpg.util.LangManager.getComponent(viewer, "gui-settings.click-increase").color(UnifiedColorUtil.GRAY))
                        .build(),
                p -> {
                    int currentVolume = settings.getGuiSoundVolume();
                    int newVolume = Math.min(100, currentVolume + 5);
                    settings.setGuiSoundVolume(newVolume);
                    
                    updateVolumeDisplay();
                    playClickSound(p);
                    com.febrie.rpg.util.LangManager.sendMessage(p, "gui-settings.volume-changed", "{volume}", String.valueOf(newVolume));
                }
        );
        setItem(VOLUME_INCREASE_SLOT, volumeIncreaseButton);

        // 음소거 토글 버튼
        updateMuteToggle();

        // 빈 슬롯들을 투명한 유리판으로 채우기
        for (int i = 0; i < GUI_SIZE; i++) {
            if (getItem(i) == null) {
                setItem(i, GuiFactory.createDecoration());
            }
        }
    }

    /**
     * 볼륨 표시 아이템 업데이트
     */
    private void updateVolumeDisplay() {
        RPGPlayer rpgPlayer = RPGMain.getPlugin().getRPGPlayerManager().getOrCreatePlayer(viewer);
        PlayerSettings settings = rpgPlayer.getPlayerSettings();
        
        int volume = settings.getGuiSoundVolume();
        boolean isMuted = settings.isGuiSoundMuted();
        
        Material material = volume > 66 ? Material.EMERALD_BLOCK :
                           volume > 33 ? Material.GOLD_BLOCK :
                           volume > 0 ? Material.IRON_BLOCK : Material.COAL_BLOCK;

        String volumeBar = createVolumeBar(volume);
        
        String status = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(com.febrie.rpg.util.LangManager.getComponent(viewer, isMuted ? "gui-settings.status-muted" : "gui-settings.status-active"));
        
        GuiItem volumeDisplay = GuiItem.display(
                new ItemBuilder(material)
                        .displayName(com.febrie.rpg.util.LangManager.getComponent(viewer, "gui-settings.sound-volume")
                                .color(UnifiedColorUtil.PRIMARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(com.febrie.rpg.util.LangManager.getComponent(viewer, "gui-settings.current-volume", "{volume}", String.valueOf(volume)).color(UnifiedColorUtil.WHITE))
                        .addLore(Component.text(volumeBar).color(volume > 0 ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR))
                        .addLore(Component.empty())
                        .addLore(com.febrie.rpg.util.LangManager.getComponent(viewer, "gui-settings.status", "{status}", status)
                                .color(isMuted ? UnifiedColorUtil.ERROR : UnifiedColorUtil.SUCCESS))
                        .build()
        );
        setItem(VOLUME_DISPLAY_SLOT, volumeDisplay);
    }

    /**
     * 음소거 토글 버튼 업데이트
     */
    private void updateMuteToggle() {
        RPGPlayer rpgPlayer = RPGMain.getPlugin().getRPGPlayerManager().getOrCreatePlayer(viewer);
        PlayerSettings settings = rpgPlayer.getPlayerSettings();
        
        boolean isMuted = settings.isGuiSoundMuted();
        
        String currentStatus = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(com.febrie.rpg.util.LangManager.getComponent(viewer, isMuted ? "gui-settings.status-muted" : "gui-settings.status-active"));
        
        GuiItem muteToggle = GuiItem.clickable(
                new ItemBuilder(isMuted ? Material.REDSTONE_TORCH : Material.TORCH)
                        .displayName(com.febrie.rpg.util.LangManager.getComponent(viewer, isMuted ? "gui-settings.unmute" : "gui-settings.mute")
                                .color(isMuted ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(com.febrie.rpg.util.LangManager.getComponent(viewer, "gui-settings.current-status", "{status}", currentStatus)
                                .color(isMuted ? UnifiedColorUtil.ERROR : UnifiedColorUtil.SUCCESS))
                        .addLore(Component.empty())
                        .addLore(com.febrie.rpg.util.LangManager.getComponent(viewer, isMuted ? "gui-settings.click-to-unmute" : "gui-settings.click-to-mute").color(UnifiedColorUtil.YELLOW))
                        .build(),
                p -> {
                    settings.setGuiSoundMuted(!isMuted);
                    
                    updateMuteToggle();
                    updateVolumeDisplay();
                    if (!settings.isGuiSoundMuted()) {
                        playClickSound(p);
                    }
                    
                    com.febrie.rpg.util.LangManager.sendMessage(p, settings.isGuiSoundMuted() ? "gui-settings.sound-muted" : "gui-settings.sound-unmuted");
                }
        );
        setItem(MUTE_TOGGLE_SLOT, muteToggle);
    }

    /**
     * 볼륨 바 생성
     */
    private String createVolumeBar(int volume) {
        StringBuilder bar = new StringBuilder();
        int filledBars = volume / 10;
        
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