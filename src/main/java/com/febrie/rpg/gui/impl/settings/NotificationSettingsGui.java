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
        super(player, guiManager, GUI_SIZE, Component.translatable("gui.notification-settings.title"));
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
        return Component.translatable("gui.notification-settings.title");
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
                        .displayNameTranslated("items.settings.notification-settings.title.name")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.notification-settings.title.lore")
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
                        .displayNameTranslated("items.settings.notification-settings.whisper.name")
                        .addLore(Component.empty())
                        .addLore(LangManager.get("gui.notification-settings.status", viewer,
                                Component.translatable(enabled ? "status.enabled" : "status.disabled")
                                .color(enabled ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR)))
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.notification-settings.whisper.desc1")
                        .addLoreTranslated("items.settings.notification-settings.whisper.desc2")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.notification-settings.whisper.note")
                        .addLore(Component.empty())
                        .addLore(LangManager.get("gui.notification-settings.click-to-toggle", viewer,
                                Component.translatable(enabled ? "action.disable" : "action.enable")))
                        .hideAllFlags()
                        .build(),
                p -> {
                    settings.setWhisperNotificationsEnabled(!enabled);
                    updateWhisperNotificationsToggle(settings);
                    playClickSound(p);
                    p.sendMessage(LangManager.get("gui.notification-settings.whisper-toggled", p,
                            Component.translatable(enabled ? "status.disabled" : "status.enabled")));
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
            case "ALL" -> Component.translatable("notification.mode.all");
            case "FRIEND_ONLY" -> Component.translatable("notification.mode.friend-only");
            case "GUILD_ONLY" -> Component.translatable("notification.mode.guild-only");
            case "OFF" -> Component.translatable("notification.mode.off");
            default -> Component.translatable("notification.mode.unknown");
        };

        Component modeDescription = switch (mode) {
            case "ALL" -> Component.translatable("notification.mode.all.desc");
            case "FRIEND_ONLY" -> Component.translatable("notification.mode.friend-only.desc");
            case "GUILD_ONLY" -> Component.translatable("notification.mode.guild-only.desc");
            case "OFF" -> Component.translatable("notification.mode.off.desc");
            default -> Component.translatable("notification.mode.unknown.desc");
        };
        
        GuiItem inviteNotificationsToggle = GuiItem.clickable(
                ItemBuilder.of(material)
                        .displayNameTranslated("items.settings.notification-settings.invite.name")
                        .addLore(Component.empty())
                        .addLore(LangManager.get("gui.notification-settings.current-mode", viewer, modeDisplay))
                        .addLore(modeDescription.color(UnifiedColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.notification-settings.invite.desc1")
                        .addLoreTranslated("items.settings.notification-settings.invite.desc2")
                        .addLoreTranslated("items.settings.notification-settings.invite.desc3")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.notification-settings.invite.click-hint")
                        .addLoreTranslated("items.settings.notification-settings.invite.mode-cycle")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.notification-settings.invite.note")
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
                        case "ALL" -> Component.translatable("notification.mode.all");
                        case "FRIEND_ONLY" -> Component.translatable("notification.mode.friend-only");
                        case "GUILD_ONLY" -> Component.translatable("notification.mode.guild-only");
                        case "OFF" -> Component.translatable("notification.mode.off");
                        default -> Component.translatable("notification.mode.unknown");
                    };
                    
                    p.sendMessage(LangManager.get("gui.notification-settings.invite-changed", p, newModeDisplay));
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
                        .displayNameTranslated("items.settings.notification-settings.server.name")
                        .addLore(Component.empty())
                        .addLore(LangManager.get("gui.notification-settings.status", viewer,
                                Component.translatable(enabled ? "status.enabled" : "status.disabled")
                                .color(enabled ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR)))
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.notification-settings.server.desc1")
                        .addLoreTranslated("items.settings.notification-settings.server.desc2")
                        .addLore(Component.empty())
                        .addLoreTranslated("items.settings.notification-settings.server.example-title")
                        .addLoreTranslated("items.settings.notification-settings.server.example1")
                        .addLoreTranslated("items.settings.notification-settings.server.example2")
                        .addLoreTranslated("items.settings.notification-settings.server.example3")
                        .addLore(Component.empty())
                        .addLore(LangManager.get("gui.notification-settings.click-to-toggle", viewer,
                                Component.translatable(enabled ? "action.disable" : "action.enable")))
                        .hideAllFlags()
                        .build(),
                p -> {
                    settings.setServerAnnouncementsEnabled(!enabled);
                    updateServerAnnouncementsToggle(settings);
                    playClickSound(p);
                    p.sendMessage(LangManager.get("gui.notification-settings.server-toggled", p,
                            Component.translatable(enabled ? "status.disabled" : "status.enabled")));
                }
        );
        setItem(SERVER_ANNOUNCEMENTS_SLOT, serverAnnouncementsToggle);
    }
    
}