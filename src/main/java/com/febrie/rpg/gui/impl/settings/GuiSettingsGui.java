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
import com.febrie.rpg.util.LangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
        super(player, guiManager, GUI_SIZE, LangManager.text(LangKey.GUI_GUI_SETTINGS_TITLE, player));
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
        return new GuiSettingsGui(guiManager, player);
    }

    @Override
    public @NotNull Component getTitle() {
        return LangManager.text(LangKey.GUI_GUI_SETTINGS_TITLE, viewer);
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
                ItemBuilder.of(Material.IRON_TRAPDOOR)
                        .displayName(LangManager.text(LangKey.ITEMS_SETTINGS_GUI_SETTINGS_TITLE_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_GUI_SETTINGS_TITLE_LORE, viewer))
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

        // 볼륨 감소 버튼
        GuiItem volumeDecreaseButton = GuiItem.clickable(
                ItemBuilder.of(Material.RED_CONCRETE)
                        .displayName(LangManager.text(LangKey.ITEMS_SETTINGS_GUI_SETTINGS_VOLUME_DECREASE_NAME, viewer))
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_GUI_SETTINGS_VOLUME_DECREASE_LORE, viewer))
                        .hideAllFlags()
                        .build(),
                p -> {
                    int currentVolume = settings.getGuiSoundVolume();
                    int newVolume = Math.max(0, currentVolume - 5);
                    settings.setGuiSoundVolume(newVolume);
                    
                    updateVolumeDisplay();
                    playClickSound(p);
                    p.sendMessage(LangManager.text(LangKey.GUI_GUI_SETTINGS_VOLUME_CHANGED, p, String.valueOf(newVolume)));
                }
        );
        setItem(VOLUME_DECREASE_SLOT, volumeDecreaseButton);

        // 볼륨 표시
        updateVolumeDisplay();

        // 볼륨 증가 버튼
        GuiItem volumeIncreaseButton = GuiItem.clickable(
                ItemBuilder.of(Material.GREEN_CONCRETE)
                        .displayName(LangManager.text(LangKey.ITEMS_SETTINGS_GUI_SETTINGS_VOLUME_INCREASE_NAME, viewer))
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_GUI_SETTINGS_VOLUME_INCREASE_LORE, viewer))
                        .hideAllFlags()
                        .build(),
                p -> {
                    int currentVolume = settings.getGuiSoundVolume();
                    int newVolume = Math.min(100, currentVolume + 5);
                    settings.setGuiSoundVolume(newVolume);
                    
                    updateVolumeDisplay();
                    playClickSound(p);
                    p.sendMessage(LangManager.text(LangKey.GUI_GUI_SETTINGS_VOLUME_CHANGED, p, String.valueOf(newVolume)));
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
        
        GuiItem volumeDisplay = GuiItem.display(
                ItemBuilder.of(material)
                        .displayName(LangManager.text(LangKey.ITEMS_SETTINGS_GUI_SETTINGS_VOLUME_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_GUI_SETTINGS_CURRENT_VOLUME, viewer, Component.text(String.valueOf(volume))))
                        .addLore(Component.text(volumeBar).color(volume > 0 ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_GUI_SETTINGS_STATUS, viewer,
                                (isMuted ? LangManager.text(LangKey.STATUS_MUTED, viewer) : LangManager.text(LangKey.STATUS_ACTIVE, viewer))
                                .color(isMuted ? UnifiedColorUtil.ERROR : UnifiedColorUtil.SUCCESS)))
                        .hideAllFlags()
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
        
        GuiItem muteToggle = GuiItem.clickable(
                ItemBuilder.of(isMuted ? Material.REDSTONE_TORCH : Material.TORCH)
                        .displayName(LangManager.text(isMuted ? LangKey.ITEMS_SETTINGS_GUI_SETTINGS_UNMUTE_NAME : LangKey.ITEMS_SETTINGS_GUI_SETTINGS_MUTE_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_GUI_SETTINGS_CURRENT_STATUS, viewer,
                                (isMuted ? LangManager.text(LangKey.STATUS_MUTED, viewer) : LangManager.text(LangKey.STATUS_ACTIVE, viewer))
                                .color(isMuted ? UnifiedColorUtil.ERROR : UnifiedColorUtil.SUCCESS)))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(isMuted ? LangKey.ITEMS_SETTINGS_GUI_SETTINGS_UNMUTE_LORE : LangKey.ITEMS_SETTINGS_GUI_SETTINGS_MUTE_LORE, viewer))
                        .hideAllFlags()
                        .build(),
                p -> {
                    settings.setGuiSoundMuted(!isMuted);
                    
                    updateMuteToggle();
                    updateVolumeDisplay();
                    if (!settings.isGuiSoundMuted()) {
                        playClickSound(p);
                    }
                    
                    p.sendMessage(LangManager.text(settings.isGuiSoundMuted() ? LangKey.GUI_GUI_SETTINGS_SOUND_MUTED : LangKey.GUI_GUI_SETTINGS_SOUND_UNMUTED, p));
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