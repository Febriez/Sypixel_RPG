package com.febrie.rpg.gui.impl.settings;
import com.febrie.rpg.util.lang.SystemLangKey;
import com.febrie.rpg.util.lang.GeneralLangKey;
import com.febrie.rpg.util.lang.MessageLangKey;

import com.febrie.rpg.util.lang.GuiLangKey;
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
 * 알림 설정 GUI
 * 귓말, 초대, 서버 공지 알림 설정
 *
 * @author Febrie
 */
public class NotificationSettingsGui extends BaseGui {

    private static final int GUI_SIZE = 45; // 5 rows

    // 설정 버튼 슬롯
    private static final int WHISPER_NOTIFICATIONS_SLOT = 20;
    private static final int INVITE_NOTIFICATIONS_SLOT = 22;
    private static final int SERVER_ANNOUNCEMENTS_SLOT = 24;

    // 타이틀 슬롯
    private static final int TITLE_SLOT = 4;

    private NotificationSettingsGui(@NotNull GuiManager guiManager,
                                  @NotNull Player player) {
        super(player, guiManager, GUI_SIZE, LangManager.text(GuiLangKey.GUI_NOTIFICATION_SETTINGS_TITLE, player));
    }

    /**
     * NotificationSettingsGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param player 플레이어
     * @return 초기화된 NotificationSettingsGui 인스턴스
     */
    public static NotificationSettingsGui create(@NotNull GuiManager guiManager,
                                                @NotNull Player player) {
        return new NotificationSettingsGui(guiManager, player);
    }

    @Override
    public @NotNull Component getTitle() {
        return LangManager.text(GuiLangKey.GUI_NOTIFICATION_SETTINGS_TITLE, viewer);
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
                ItemBuilder.of(Material.BELL)
                        .displayName(LangManager.text(GeneralLangKey.ITEMS_SETTINGS_NOTIFICATION_SETTINGS_TITLE_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(GeneralLangKey.ITEMS_SETTINGS_NOTIFICATION_SETTINGS_TITLE_LORE, viewer))
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

        setupWhisperNotificationsToggle(settings);
        setupInviteNotificationsToggle(settings);
        setupServerAnnouncementsToggle(settings);

        // 빈 슬롯들을 투명한 유리판으로 채우기
        for (int i = 0; i < GUI_SIZE; i++) {
            if (getItem(i) == null) {
                setItem(i, GuiFactory.createDecoration());
            }
        }
    }

    /**
     * 귓말 알림 토글 설정
     */
    private void setupWhisperNotificationsToggle(PlayerSettings settings) {
        updateWhisperNotificationsToggle(settings);
    }

    /**
     * 초대 알림 토글 설정
     */
    private void setupInviteNotificationsToggle(PlayerSettings settings) {
        updateInviteNotificationsToggle(settings);
    }

    /**
     * 서버 공지 알림 토글 설정
     */
    private void setupServerAnnouncementsToggle(PlayerSettings settings) {
        updateServerAnnouncementsToggle(settings);
    }

    /**
     * 귓말 알림 토글 업데이트
     */
    private void updateWhisperNotificationsToggle(PlayerSettings settings) {
        boolean enabled = settings.isWhisperNotificationsEnabled();
        
        GuiItem whisperNotificationsToggle = GuiItem.clickable(
                ItemBuilder.of(enabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE)
                        .displayName(LangManager.text(GeneralLangKey.ITEMS_SETTINGS_NOTIFICATION_SETTINGS_WHISPER_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(GuiLangKey.GUI_NOTIFICATION_SETTINGS_STATUS, viewer,
                                (enabled ? LangManager.text(GeneralLangKey.STATUS_ENABLED, viewer) : LangManager.text(GeneralLangKey.STATUS_DISABLED, viewer))
                                .color(enabled ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR)))
                        .addLore(Component.empty())
                        .addLore(LangManager.list(GeneralLangKey.ITEMS_SETTINGS_NOTIFICATION_SETTINGS_WHISPER_DESC, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(GeneralLangKey.ITEMS_SETTINGS_NOTIFICATION_SETTINGS_WHISPER_NOTE, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(GuiLangKey.GUI_NOTIFICATION_SETTINGS_CLICK_TO_TOGGLE, viewer,
                                (enabled ? LangManager.text(GeneralLangKey.ACTION_DISABLE, viewer) : LangManager.text(GeneralLangKey.ACTION_ENABLE, viewer))))
                        .hideAllFlags()
                        .build(),
                p -> {
                    settings.setWhisperNotificationsEnabled(!enabled);
                    updateWhisperNotificationsToggle(settings);
                    playClickSound(p);
                    p.sendMessage(LangManager.text(GuiLangKey.GUI_NOTIFICATION_SETTINGS_WHISPER_TOGGLED, p,
                            (enabled ? LangManager.text(GeneralLangKey.STATUS_DISABLED, p) : LangManager.text(GeneralLangKey.STATUS_ENABLED, p))));
                }
        );
        setItem(WHISPER_NOTIFICATIONS_SLOT, whisperNotificationsToggle);
    }

    /**
     * 초대 알림 토글 업데이트
     */
    private void updateInviteNotificationsToggle(PlayerSettings settings) {
        String mode = settings.getInviteNotificationsMode();
        
        Material material = switch (mode) {
            case "ALL" -> Material.LIME_DYE;
            case "FRIEND_ONLY" -> Material.YELLOW_DYE;
            case "GUILD_ONLY" -> Material.ORANGE_DYE;
            case "OFF" -> Material.RED_DYE;
            default -> Material.WHITE_DYE;
        };

        Component modeDisplay = switch (mode) {
            case "ALL" -> LangManager.text(GeneralLangKey.NOTIFICATION_MODE_ALL, viewer);
            case "FRIEND_ONLY" -> LangManager.text(GeneralLangKey.NOTIFICATION_MODE_FRIEND_ONLY, viewer);
            case "GUILD_ONLY" -> LangManager.text(GeneralLangKey.NOTIFICATION_MODE_GUILD_ONLY, viewer);
            case "OFF" -> LangManager.text(GeneralLangKey.NOTIFICATION_MODE_OFF, viewer);
            default -> LangManager.text(GeneralLangKey.NOTIFICATION_MODE_UNKNOWN, viewer);
        };

        Component modeDescription = switch (mode) {
            case "ALL" -> LangManager.text(GeneralLangKey.NOTIFICATION_MODE_ALL_DESC, viewer);
            case "FRIEND_ONLY" -> LangManager.text(GeneralLangKey.NOTIFICATION_MODE_FRIEND_ONLY_DESC, viewer);
            case "GUILD_ONLY" -> LangManager.text(GeneralLangKey.NOTIFICATION_MODE_GUILD_ONLY_DESC, viewer);
            case "OFF" -> LangManager.text(GeneralLangKey.NOTIFICATION_MODE_OFF_DESC, viewer);
            default -> LangManager.text(GeneralLangKey.NOTIFICATION_MODE_UNKNOWN_DESC, viewer);
        };
        
        GuiItem inviteNotificationsToggle = GuiItem.clickable(
                ItemBuilder.of(material)
                        .displayName(LangManager.text(GeneralLangKey.ITEMS_SETTINGS_NOTIFICATION_SETTINGS_INVITE_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(GuiLangKey.GUI_NOTIFICATION_SETTINGS_CURRENT_MODE, viewer, modeDisplay))
                        .addLore(modeDescription.color(UnifiedColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(LangManager.list(GeneralLangKey.ITEMS_SETTINGS_NOTIFICATION_SETTINGS_INVITE_DESC, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(GeneralLangKey.ITEMS_SETTINGS_NOTIFICATION_SETTINGS_INVITE_CLICK_HINT, viewer))
                        .addLore(LangManager.text(GeneralLangKey.ITEMS_SETTINGS_NOTIFICATION_SETTINGS_INVITE_MODE_CYCLE, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(GeneralLangKey.ITEMS_SETTINGS_NOTIFICATION_SETTINGS_INVITE_NOTE, viewer))
                        .hideAllFlags()
                        .build(),
                p -> {
                    String nextMode = switch (mode) {
                        case "ALL" -> "FRIEND_ONLY";
                        case "FRIEND_ONLY" -> "GUILD_ONLY";
                        case "GUILD_ONLY" -> "OFF";
                        case "OFF" -> "ALL";
                        default -> "ALL";
                    };
                    
                    settings.setInviteNotificationsMode(nextMode);
                    updateInviteNotificationsToggle(settings);
                    playClickSound(p);
                    
                    Component newModeDisplay = switch (nextMode) {
                        case "ALL" -> LangManager.text(GeneralLangKey.NOTIFICATION_MODE_ALL, p);
                        case "FRIEND_ONLY" -> LangManager.text(GeneralLangKey.NOTIFICATION_MODE_FRIEND_ONLY, p);
                        case "GUILD_ONLY" -> LangManager.text(GeneralLangKey.NOTIFICATION_MODE_GUILD_ONLY, p);
                        case "OFF" -> LangManager.text(GeneralLangKey.NOTIFICATION_MODE_OFF, p);
                        default -> LangManager.text(GeneralLangKey.NOTIFICATION_MODE_UNKNOWN, p);
                    };
                    
                    p.sendMessage(LangManager.text(GuiLangKey.GUI_NOTIFICATION_SETTINGS_INVITE_CHANGED, p, newModeDisplay));
                }
        );
        setItem(INVITE_NOTIFICATIONS_SLOT, inviteNotificationsToggle);
    }

    /**
     * 서버 공지 알림 토글 업데이트
     */
    private void updateServerAnnouncementsToggle(PlayerSettings settings) {
        boolean enabled = settings.isServerAnnouncementsEnabled();
        
        GuiItem serverAnnouncementsToggle = GuiItem.clickable(
                ItemBuilder.of(enabled ? Material.BEACON : Material.GLASS)
                        .displayName(LangManager.text(GeneralLangKey.ITEMS_SETTINGS_NOTIFICATION_SETTINGS_SERVER_NAME, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(GuiLangKey.GUI_NOTIFICATION_SETTINGS_STATUS, viewer,
                                (enabled ? LangManager.text(GeneralLangKey.STATUS_ENABLED, viewer) : LangManager.text(GeneralLangKey.STATUS_DISABLED, viewer))
                                .color(enabled ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR)))
                        .addLore(Component.empty())
                        .addLore(LangManager.list(GeneralLangKey.ITEMS_SETTINGS_NOTIFICATION_SETTINGS_SERVER_DESC, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(GeneralLangKey.ITEMS_SETTINGS_NOTIFICATION_SETTINGS_SERVER_EXAMPLE_TITLE, viewer))
                        .addLore(LangManager.list(GeneralLangKey.ITEMS_SETTINGS_NOTIFICATION_SETTINGS_SERVER_EXAMPLES, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(GuiLangKey.GUI_NOTIFICATION_SETTINGS_CLICK_TO_TOGGLE, viewer,
                                (enabled ? LangManager.text(GeneralLangKey.ACTION_DISABLE, viewer) : LangManager.text(GeneralLangKey.ACTION_ENABLE, viewer))))
                        .hideAllFlags()
                        .build(),
                p -> {
                    settings.setServerAnnouncementsEnabled(!enabled);
                    updateServerAnnouncementsToggle(settings);
                    playClickSound(p);
                    p.sendMessage(LangManager.text(GuiLangKey.GUI_NOTIFICATION_SETTINGS_SERVER_TOGGLED, p,
                            (enabled ? LangManager.text(GeneralLangKey.STATUS_DISABLED, p) : LangManager.text(GeneralLangKey.STATUS_ENABLED, p))));
                }
        );
        setItem(SERVER_ANNOUNCEMENTS_SLOT, serverAnnouncementsToggle);
    }
    
}