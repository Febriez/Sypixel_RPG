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
 * 시스템 설정 GUI
 * 확인 대화상자 등의 시스템 관련 설정
 *
 * @author Febrie
 */
public class SystemSettingsGui extends BaseGui {

    private static final int GUI_SIZE = 27; // 3 rows

    // 설정 버튼 슬롯
    private static final int CONFIRMATION_DIALOGS_SLOT = 13;

    // 타이틀 슬롯
    private static final int TITLE_SLOT = 4;

    private SystemSettingsGui(@NotNull GuiManager guiManager,
                            @NotNull Player player) {
        super(player, guiManager, GUI_SIZE, LangManager.text(LangKey.GUI_SYSTEM_SETTINGS_TITLE, player));
    }

    /**
     * SystemSettingsGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param player 플레이어
     * @return 초기화된 SystemSettingsGui 인스턴스
     */
    public static SystemSettingsGui create(@NotNull GuiManager guiManager,
                                          @NotNull Player player) {
        return new SystemSettingsGui(guiManager, player);
    }

    @Override
    public @NotNull Component getTitle() {
        return LangManager.text(LangKey.GUI_SYSTEM_SETTINGS_TITLE, viewer);
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
                ItemBuilder.of(Material.REDSTONE_BLOCK)
                        .displayName(LangManager.text(LangKey.ITEMS_SETTINGS_SYSTEM_SETTINGS_TITLE_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_SYSTEM_SETTINGS_TITLE_LORE, viewer))
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

        setupConfirmationDialogsToggle(settings);

        // 빈 슬롯들을 투명한 유리판으로 채우기
        for (int i = 0; i < GUI_SIZE; i++) {
            if (getItem(i) == null) {
                setItem(i, GuiFactory.createDecoration());
            }
        }
    }

    /**
     * 확인 대화상자 토글 설정
     */
    private void setupConfirmationDialogsToggle(PlayerSettings settings) {
        updateConfirmationDialogsToggle(settings);
    }

    /**
     * 확인 대화상자 토글 업데이트
     */
    private void updateConfirmationDialogsToggle(PlayerSettings settings) {
        boolean enabled = settings.isConfirmationDialogsEnabled();
        
        GuiItem confirmationDialogsToggle = GuiItem.clickable(
                ItemBuilder.of(enabled ? Material.WRITABLE_BOOK : Material.BOOK)
                        .displayName(LangManager.text(LangKey.ITEMS_SETTINGS_SYSTEM_SETTINGS_CONFIRMATION_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_SYSTEM_SETTINGS_STATUS, viewer, 
                                LangManager.text(enabled ? LangKey.STATUS_ENABLED : LangKey.STATUS_DISABLED, viewer)
                                .color(enabled ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR)))
                        .addLore(Component.empty())
                        .addLore(LangManager.list(LangKey.ITEMS_SETTINGS_SYSTEM_SETTINGS_CONFIRMATION_DESC, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.ITEMS_SETTINGS_SYSTEM_SETTINGS_CONFIRMATION_EXAMPLE_TITLE, viewer))
                        .addLore(LangManager.list(LangKey.ITEMS_SETTINGS_SYSTEM_SETTINGS_CONFIRMATION_EXAMPLE, getViewerLocale()))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_SYSTEM_SETTINGS_CLICK_TO_TOGGLE, viewer,
                                LangManager.text(enabled ? LangKey.ACTION_DISABLE : LangKey.ACTION_ENABLE, viewer)))
                        .hideAllFlags()
                        .build(),
                p -> {
                    settings.setConfirmationDialogsEnabled(!enabled);
                    updateConfirmationDialogsToggle(settings);
                    playClickSound(p);
                    p.sendMessage(LangManager.text(LangKey.GUI_SYSTEM_SETTINGS_CONFIRMATION_TOGGLED, p, 
                            LangManager.text(enabled ? LangKey.STATUS_DISABLED : LangKey.STATUS_ENABLED, p)));
                }
        );
        setItem(CONFIRMATION_DIALOGS_SLOT, confirmationDialogsToggle);
    }
    
}