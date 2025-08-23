package com.febrie.rpg.player;

import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * 플레이어 개인 설정 관리
 * 대화 속도, 사운드 설정 등을 관리
 *
 * @author Febrie
 */
public class PlayerSettings {

    // 설정 키들
    private static final String DIALOG_SPEED_KEY = "dialog_speed";
    private static final String SOUND_ENABLED_KEY = "sound_enabled";
    private static final String GUI_SOUND_VOLUME_KEY = "gui_sound_volume";
    private static final String GUI_SOUND_MUTED_KEY = "gui_sound_muted";
    private static final String CONFIRMATION_DIALOGS_KEY = "confirmation_dialogs";
    private static final String QUEST_AUTO_GUIDE_KEY = "quest_auto_guide";
    private static final String DAMAGE_DISPLAY_KEY = "damage_display";
    private static final String FRIEND_REQUESTS_KEY = "friend_requests";
    private static final String GUILD_INVITES_KEY = "guild_invites";
    private static final String WHISPER_MODE_KEY = "whisper_mode";
    private static final String WHISPER_NOTIFICATIONS_KEY = "whisper_notifications";
    private static final String INVITE_NOTIFICATIONS_KEY = "invite_notifications";
    private static final String SERVER_ANNOUNCEMENTS_KEY = "server_announcements";
    
    // 기본값들
    private static final int DEFAULT_DIALOG_SPEED = 2; // 2틱 (100ms)
    private static final boolean DEFAULT_SOUND_ENABLED = true;
    private static final int DEFAULT_GUI_SOUND_VOLUME = 50; // 0-100
    private static final boolean DEFAULT_GUI_SOUND_MUTED = false;
    private static final boolean DEFAULT_CONFIRMATION_DIALOGS = true;
    private static final boolean DEFAULT_QUEST_AUTO_GUIDE = true;
    private static final boolean DEFAULT_DAMAGE_DISPLAY = true;
    private static final boolean DEFAULT_FRIEND_REQUESTS = true;
    private static final boolean DEFAULT_GUILD_INVITES = true;
    private static final String DEFAULT_WHISPER_MODE = "ALL"; // ALL, FRIENDS_ONLY, BLOCKED
    private static final boolean DEFAULT_WHISPER_NOTIFICATIONS = true;
    private static final String DEFAULT_INVITE_NOTIFICATIONS = "ALL"; // OFF, FRIEND_ONLY, GUILD_ONLY, ALL
    private static final boolean DEFAULT_SERVER_ANNOUNCEMENTS = true;
    
    // 속도 범위 (최소 1틱, 최대 10틱)
    private static final int MIN_DIALOG_SPEED = 1;
    private static final int MAX_DIALOG_SPEED = 10;

    private final Player player;
    private final NamespacedKey dialogSpeedKey;
    private final NamespacedKey soundEnabledKey;
    private final NamespacedKey guiSoundVolumeKey;
    private final NamespacedKey guiSoundMutedKey;
    private final NamespacedKey confirmationDialogsKey;
    private final NamespacedKey questAutoGuideKey;
    private final NamespacedKey damageDisplayKey;
    private final NamespacedKey friendRequestsKey;
    private final NamespacedKey guildInvitesKey;
    private final NamespacedKey whisperModeKey;
    private final NamespacedKey whisperNotificationsKey;
    private final NamespacedKey inviteNotificationsKey;
    private final NamespacedKey serverAnnouncementsKey;

    public PlayerSettings(@NotNull Player player, @NotNull NamespacedKey dialogSpeedKey, 
                         @NotNull NamespacedKey soundEnabledKey) {
        this.player = player;
        this.dialogSpeedKey = dialogSpeedKey;
        this.soundEnabledKey = soundEnabledKey;
        
        // 추가 키들 초기화
        String pluginName = dialogSpeedKey.getNamespace();
        this.guiSoundVolumeKey = new NamespacedKey(pluginName, GUI_SOUND_VOLUME_KEY);
        this.guiSoundMutedKey = new NamespacedKey(pluginName, GUI_SOUND_MUTED_KEY);
        this.confirmationDialogsKey = new NamespacedKey(pluginName, CONFIRMATION_DIALOGS_KEY);
        this.questAutoGuideKey = new NamespacedKey(pluginName, QUEST_AUTO_GUIDE_KEY);
        this.damageDisplayKey = new NamespacedKey(pluginName, DAMAGE_DISPLAY_KEY);
        this.friendRequestsKey = new NamespacedKey(pluginName, FRIEND_REQUESTS_KEY);
        this.guildInvitesKey = new NamespacedKey(pluginName, GUILD_INVITES_KEY);
        this.whisperModeKey = new NamespacedKey(pluginName, WHISPER_MODE_KEY);
        this.whisperNotificationsKey = new NamespacedKey(pluginName, WHISPER_NOTIFICATIONS_KEY);
        this.inviteNotificationsKey = new NamespacedKey(pluginName, INVITE_NOTIFICATIONS_KEY);
        this.serverAnnouncementsKey = new NamespacedKey(pluginName, SERVER_ANNOUNCEMENTS_KEY);
    }

    /**
     * 대화 속도 가져오기 (틱 단위)
     * 
     * @return 대화 속도 (1-10틱)
     */
    public int getDialogSpeed() {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        int speed = pdc.getOrDefault(dialogSpeedKey, PersistentDataType.INTEGER, DEFAULT_DIALOG_SPEED);
        
        // 범위 검증
        if (speed < MIN_DIALOG_SPEED || speed > MAX_DIALOG_SPEED) {
            speed = DEFAULT_DIALOG_SPEED;
            setDialogSpeed(speed); // 잘못된 값이면 기본값으로 복구
        }
        
        return speed;
    }

    /**
     * 대화 속도 설정 (틱 단위)
     * 
     * @param speed 대화 속도 (1-10틱)
     */
    public void setDialogSpeed(int speed) {
        // 범위 제한
        speed = Math.max(MIN_DIALOG_SPEED, Math.min(MAX_DIALOG_SPEED, speed));
        
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(dialogSpeedKey, PersistentDataType.INTEGER, speed);
    }

    /**
     * 사운드 활성화 여부 가져오기
     * 
     * @return 사운드 활성화 여부
     */
    public boolean isSoundEnabled() {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(soundEnabledKey, PersistentDataType.BYTE, (byte) (DEFAULT_SOUND_ENABLED ? 1 : 0)) == 1;
    }

    /**
     * 사운드 활성화 여부 설정
     * 
     * @param enabled 사운드 활성화 여부
     */
    public void setSoundEnabled(boolean enabled) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(soundEnabledKey, PersistentDataType.BYTE, (byte) (enabled ? 1 : 0));
    }

    /**
     * 대화 속도를 사용자 친화적인 문자열로 변환
     * 
     * @return 속도 설명 ("매우 빠름", "빠름", "보통", "느림", "매우 느림")
     */
    @NotNull
    public String getDialogSpeedDisplayName() {
        int speed = getDialogSpeed();
        return switch (speed) {
            case 1 -> "매우 빠름";
            case 2 -> "빠름";
            case 3, 4 -> "보통";
            case 5, 6 -> "느림";
            default -> "매우 느림";
        };
    }

    /**
     * 대화 속도를 단계별로 조정
     * 
     * @param faster true면 빨라지게, false면 느려지게
     * @return 변경 후 속도
     */
    public int adjustDialogSpeed(boolean faster) {
        int currentSpeed = getDialogSpeed();
        int newSpeed = faster ? Math.max(MIN_DIALOG_SPEED, currentSpeed - 1) 
                             : Math.min(MAX_DIALOG_SPEED, currentSpeed + 1);
        setDialogSpeed(newSpeed);
        return newSpeed;
    }

    /**
     * 모든 설정을 기본값으로 리셋
     */
    public void resetToDefaults() {
        setDialogSpeed(DEFAULT_DIALOG_SPEED);
        setSoundEnabled(DEFAULT_SOUND_ENABLED);
    }

    /**
     * 최소 대화 속도 반환
     */
    public static int getMinDialogSpeed() {
        return MIN_DIALOG_SPEED;
    }

    /**
     * 최대 대화 속도 반환
     */
    public static int getMaxDialogSpeed() {
        return MAX_DIALOG_SPEED;
    }

    /**
     * 기본 대화 속도 반환
     */
    public static int getDefaultDialogSpeed() {
        return DEFAULT_DIALOG_SPEED;
    }
    
    // === GUI 설정 ===
    
    /**
     * GUI 사운드 볼륨 가져오기 (0-100)
     */
    public int getGuiSoundVolume() {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        int volume = pdc.getOrDefault(guiSoundVolumeKey, PersistentDataType.INTEGER, DEFAULT_GUI_SOUND_VOLUME);
        return Math.max(0, Math.min(100, volume));
    }
    
    /**
     * GUI 사운드 볼륨 설정 (0-100)
     */
    public void setGuiSoundVolume(int volume) {
        volume = Math.max(0, Math.min(100, volume));
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(guiSoundVolumeKey, PersistentDataType.INTEGER, volume);
    }
    
    /**
     * GUI 사운드 음소거 여부 가져오기
     */
    public boolean isGuiSoundMuted() {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(guiSoundMutedKey, PersistentDataType.BYTE, (byte) (DEFAULT_GUI_SOUND_MUTED ? 1 : 0)) == 1;
    }
    
    /**
     * GUI 사운드 음소거 여부 설정
     */
    public void setGuiSoundMuted(boolean muted) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(guiSoundMutedKey, PersistentDataType.BYTE, (byte) (muted ? 1 : 0));
    }
    
    // === 인게임 설정 ===
    
    /**
     * 확인 대화상자 표시 여부 가져오기
     */
    public boolean isConfirmationDialogsEnabled() {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(confirmationDialogsKey, PersistentDataType.BYTE, (byte) (DEFAULT_CONFIRMATION_DIALOGS ? 1 : 0)) == 1;
    }
    
    /**
     * 확인 대화상자 표시 여부 설정
     */
    public void setConfirmationDialogsEnabled(boolean enabled) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(confirmationDialogsKey, PersistentDataType.BYTE, (byte) (enabled ? 1 : 0));
    }
    
    /**
     * 퀘스트 자동 길안내 여부 가져오기
     */
    public boolean isQuestAutoGuideEnabled() {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(questAutoGuideKey, PersistentDataType.BYTE, (byte) (DEFAULT_QUEST_AUTO_GUIDE ? 1 : 0)) == 1;
    }
    
    /**
     * 퀘스트 자동 길안내 여부 설정
     */
    public void setQuestAutoGuideEnabled(boolean enabled) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(questAutoGuideKey, PersistentDataType.BYTE, (byte) (enabled ? 1 : 0));
    }
    
    /**
     * 데미지 표시 여부 가져오기
     */
    public boolean isDamageDisplayEnabled() {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(damageDisplayKey, PersistentDataType.BYTE, (byte) (DEFAULT_DAMAGE_DISPLAY ? 1 : 0)) == 1;
    }
    
    /**
     * 데미지 표시 여부 설정
     */
    public void setDamageDisplayEnabled(boolean enabled) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(damageDisplayKey, PersistentDataType.BYTE, (byte) (enabled ? 1 : 0));
    }
    
    // === 소셜 설정 ===
    
    /**
     * 친구 요청 받기 여부 가져오기
     */
    public boolean isFriendRequestsEnabled() {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(friendRequestsKey, PersistentDataType.BYTE, (byte) (DEFAULT_FRIEND_REQUESTS ? 1 : 0)) == 1;
    }
    
    /**
     * 친구 요청 받기 여부 설정
     */
    public void setFriendRequestsEnabled(boolean enabled) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(friendRequestsKey, PersistentDataType.BYTE, (byte) (enabled ? 1 : 0));
    }
    
    /**
     * 길드 초대 받기 여부 가져오기
     */
    public boolean isGuildInvitesEnabled() {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(guildInvitesKey, PersistentDataType.BYTE, (byte) (DEFAULT_GUILD_INVITES ? 1 : 0)) == 1;
    }
    
    /**
     * 길드 초대 받기 여부 설정
     */
    public void setGuildInvitesEnabled(boolean enabled) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(guildInvitesKey, PersistentDataType.BYTE, (byte) (enabled ? 1 : 0));
    }
    
    /**
     * 귓말 모드 가져오기 (ALL, FRIENDS_ONLY, BLOCKED)
     */
    @NotNull
    public String getWhisperMode() {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        String mode = pdc.getOrDefault(whisperModeKey, PersistentDataType.STRING, DEFAULT_WHISPER_MODE);
        return mode != null ? mode : DEFAULT_WHISPER_MODE;
    }
    
    /**
     * 귓말 모드 설정 (ALL, FRIENDS_ONLY, BLOCKED)
     */
    public void setWhisperMode(@NotNull String mode) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(whisperModeKey, PersistentDataType.STRING, mode);
    }
    
    // === 알림 설정 ===
    
    /**
     * 귓말 알림 여부 가져오기
     */
    public boolean isWhisperNotificationsEnabled() {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(whisperNotificationsKey, PersistentDataType.BYTE, (byte) (DEFAULT_WHISPER_NOTIFICATIONS ? 1 : 0)) == 1;
    }
    
    /**
     * 귓말 알림 여부 설정
     */
    public void setWhisperNotificationsEnabled(boolean enabled) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(whisperNotificationsKey, PersistentDataType.BYTE, (byte) (enabled ? 1 : 0));
    }
    
    /**
     * 초대 알림 모드 가져오기 (OFF, FRIEND_ONLY, GUILD_ONLY, ALL)
     */
    @NotNull
    public String getInviteNotificationsMode() {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        String mode = pdc.getOrDefault(inviteNotificationsKey, PersistentDataType.STRING, DEFAULT_INVITE_NOTIFICATIONS);
        return mode != null ? mode : DEFAULT_INVITE_NOTIFICATIONS;
    }
    
    /**
     * 초대 알림 모드 설정 (OFF, FRIEND_ONLY, GUILD_ONLY, ALL)
     */
    public void setInviteNotificationsMode(@NotNull String mode) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(inviteNotificationsKey, PersistentDataType.STRING, mode);
    }
    
    /**
     * 서버 공지 알림 여부 가져오기
     */
    public boolean isServerAnnouncementsEnabled() {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(serverAnnouncementsKey, PersistentDataType.BYTE, (byte) (DEFAULT_SERVER_ANNOUNCEMENTS ? 1 : 0)) == 1;
    }
    
    /**
     * 서버 공지 알림 여부 설정
     */
    public void setServerAnnouncementsEnabled(boolean enabled) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(serverAnnouncementsKey, PersistentDataType.BYTE, (byte) (enabled ? 1 : 0));
    }
}