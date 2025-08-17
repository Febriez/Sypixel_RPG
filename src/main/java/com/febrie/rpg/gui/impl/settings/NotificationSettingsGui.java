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
        super(player, guiManager, GUI_SIZE, "gui.notification-settings.title");
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
        NotificationSettingsGui gui = new NotificationSettingsGui(guiManager, player);
        gui.initialize("gui.notification-settings.title");
        return gui;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.text("알림 설정", UnifiedColorUtil.MYTHIC);
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
                new ItemBuilder(Material.BELL)
                        .displayName(Component.text("🔔 알림 설정", UnifiedColorUtil.MYTHIC)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("알림 관련 설정을 변경합니다", UnifiedColorUtil.GRAY))
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
                new ItemBuilder(enabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE)
                        .displayName(Component.text("💬 귓말 알림", UnifiedColorUtil.PRIMARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("상태: " + (enabled ? "활성화" : "비활성화"), 
                                enabled ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR))
                        .addLore(Component.empty())
                        .addLore(Component.text("귓말을 받았을 때", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("채팅에 알림을 표시할지 설정합니다", UnifiedColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("※ 귓말 시스템은 준비중입니다", UnifiedColorUtil.YELLOW))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 " + (enabled ? "비활성화" : "활성화"), UnifiedColorUtil.YELLOW))
                        .build(),
                p -> {
                    settings.setWhisperNotificationsEnabled(!enabled);
                    updateWhisperNotificationsToggle(settings);
                    playClickSound(p);
                    com.febrie.rpg.util.LangManager.sendMessage(p, "귓말 알림이 " + (settings.isWhisperNotificationsEnabled() ? "활성화" : "비활성화") + "되었습니다");
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

        String modeDisplay = switch (mode) {
            case "ALL" -> "전체";
            case "FRIEND_ONLY" -> "친구 요청만";
            case "GUILD_ONLY" -> "길드 초대만";
            case "OFF" -> "끄기";
            default -> "알 수 없음";
        };

        String modeDescription = switch (mode) {
            case "ALL" -> "모든 초대 알림을 표시합니다";
            case "FRIEND_ONLY" -> "친구 요청 알림만 표시합니다";
            case "GUILD_ONLY" -> "길드 초대 알림만 표시합니다";
            case "OFF" -> "모든 초대 알림을 숨깁니다";
            default -> "알 수 없는 모드입니다";
        };
        
        GuiItem inviteNotificationsToggle = GuiItem.clickable(
                new ItemBuilder(material)
                        .displayName(Component.text("📨 초대 알림", UnifiedColorUtil.PRIMARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("현재 모드: " + modeDisplay, UnifiedColorUtil.WHITE))
                        .addLore(Component.text(modeDescription, UnifiedColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("친구 요청이나 길드 초대를", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("받았을 때 채팅에 알림을", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("표시할지 설정합니다", UnifiedColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 다음 모드로 변경:", UnifiedColorUtil.YELLOW))
                        .addLore(Component.text("전체 → 친구만 → 길드만 → 끄기", UnifiedColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("※ 관련 시스템은 준비중입니다", UnifiedColorUtil.YELLOW))
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
                    
                    String newModeDisplay = switch (nextMode) {
                        case "ALL" -> "전체";
                        case "FRIEND_ONLY" -> "친구 요청만";
                        case "GUILD_ONLY" -> "길드 초대만";
                        case "OFF" -> "끄기";
                        default -> "알 수 없음";
                    };
                    
                    com.febrie.rpg.util.LangManager.sendMessage(p, "초대 알림 모드가 '" + newModeDisplay + "'로 변경되었습니다");
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
                new ItemBuilder(enabled ? Material.BEACON : Material.GLASS)
                        .displayName(Component.text("📢 서버 공지 알림", UnifiedColorUtil.PRIMARY)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("상태: " + (enabled ? "활성화" : "비활성화"), 
                                enabled ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR))
                        .addLore(Component.empty())
                        .addLore(Component.text("서버 공지사항을", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("채팅에 표시할지 설정합니다", UnifiedColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("예시:", UnifiedColorUtil.YELLOW))
                        .addLore(Component.text("• 이벤트 알림", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("• 업데이트 공지", UnifiedColorUtil.GRAY))
                        .addLore(Component.text("• 중요 알림", UnifiedColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("클릭하여 " + (enabled ? "비활성화" : "활성화"), UnifiedColorUtil.YELLOW))
                        .build(),
                p -> {
                    settings.setServerAnnouncementsEnabled(!enabled);
                    updateServerAnnouncementsToggle(settings);
                    playClickSound(p);
                    com.febrie.rpg.util.LangManager.sendMessage(p, "서버 공지 알림이 " + (settings.isServerAnnouncementsEnabled() ? "활성화" : "비활성화") + "되었습니다");
                }
        );
        setItem(SERVER_ANNOUNCEMENTS_SLOT, serverAnnouncementsToggle);
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }
    
    @Override
    public void onClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        event.setCancelled(true);
        // GuiItem이 클릭 처리를 담당합니다
    }
}