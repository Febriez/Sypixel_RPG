package com.febrie.rpg.gui.impl.settings;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.PlayerSettings;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.ColorUtil;
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

    public GuiSettingsGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                         @NotNull Player player) {
        super(player, guiManager, langManager, GUI_SIZE, "gui.gui-settings.title");
        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.text("GUI 설정", ColorUtil.UNCOMMON);
    }

    @Override
    protected GuiFramework getBackTarget() {
        return new PlayerSettingsGui(guiManager, langManager, viewer);
    }

    @Override
    protected void setupLayout() {
        setupDecorations();
        setupSettingControls();
        setupStandardNavigation(true, true);
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
                        .displayName(Component.text("🖥 GUI 설정", ColorUtil.UNCOMMON)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("GUI 관련 설정을 변경합니다", ColorUtil.GRAY))
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
                        .displayName(Component.text("- 볼륨 감소", ColorUtil.ERROR))
                        .addLore(Component.text("클릭하여 볼륨을 5 감소시킵니다", ColorUtil.GRAY))
                        .build(),
                p -> {
                    int currentVolume = settings.getGuiSoundVolume();
                    int newVolume = Math.max(0, currentVolume - 5);
                    settings.setGuiSoundVolume(newVolume);
                    
                    updateVolumeDisplay();
                    playClickSound(p);
                    langManager.sendMessage(p, "설정이 변경되었습니다: 볼륨 " + newVolume + "%");
                }
        );
        setItem(VOLUME_DECREASE_SLOT, volumeDecreaseButton);

        // 볼륨 표시
        updateVolumeDisplay();

        // 볼륨 증가 버튼
        GuiItem volumeIncreaseButton = GuiItem.clickable(
                new ItemBuilder(Material.GREEN_CONCRETE)
                        .displayName(Component.text("+ 볼륨 증가", ColorUtil.SUCCESS))
                        .addLore(Component.text("클릭하여 볼륨을 5 증가시킵니다", ColorUtil.GRAY))
                        .build(),
                p -> {
                    int currentVolume = settings.getGuiSoundVolume();
                    int newVolume = Math.min(100, currentVolume + 5);
                    settings.setGuiSoundVolume(newVolume);
                    
                    updateVolumeDisplay();
                    playClickSound(p);
                    langManager.sendMessage(p, "설정이 변경되었습니다: 볼륨 " + newVolume + "%");
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
                new ItemBuilder(material)
                        .displayName(Component.text("🔊 GUI 사운드 볼륨", ColorUtil.PRIMARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("현재 볼륨: " + volume + "%", ColorUtil.WHITE))
                        .addLore(Component.text(volumeBar, volume > 0 ? ColorUtil.SUCCESS : ColorUtil.ERROR))
                        .addLore(Component.empty())
                        .addLore(Component.text("상태: " + (isMuted ? "음소거됨" : "활성화"), 
                                isMuted ? ColorUtil.ERROR : ColorUtil.SUCCESS))
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
                new ItemBuilder(isMuted ? Material.REDSTONE_TORCH : Material.TORCH)
                        .displayName(Component.text(isMuted ? "🔇 음소거 해제" : "🔇 음소거", 
                                isMuted ? ColorUtil.SUCCESS : ColorUtil.ERROR)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("현재 상태: " + (isMuted ? "음소거됨" : "활성화"), 
                                isMuted ? ColorUtil.ERROR : ColorUtil.SUCCESS))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 " + (isMuted ? "음소거 해제" : "음소거"), ColorUtil.YELLOW))
                        .build(),
                p -> {
                    settings.setGuiSoundMuted(!isMuted);
                    
                    updateMuteToggle();
                    updateVolumeDisplay();
                    if (!settings.isGuiSoundMuted()) {
                        playClickSound(p);
                    }
                    
                    langManager.sendMessage(p, "GUI 사운드가 " + (settings.isGuiSoundMuted() ? "음소거" : "활성화") + "되었습니다");
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

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
}