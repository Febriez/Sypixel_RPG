package com.febrie.rpg.util.lang;

public enum GeneralLangKey implements ILangKey {
    // General
    GENERAL_TIME_DAYS("general.time.days"),
    GENERAL_TIME_HOURS("general.time.hours"),
    GENERAL_TIME_MINUTES("general.time.minutes"),
    
    // Status
    STATUS_ENABLED("status.enabled"),
    STATUS_DISABLED("status.disabled"),
    STATUS_TRUE("status.true"),
    STATUS_FALSE("status.false"),
    STATUS_NEW_MAIL("status.new_mail"),
    STATUS_READ("status.read"),
    STATUS_MUTED("status.muted"),
    STATUS_ACTIVE("status.active"),
    STATUS_PUBLIC("status.public"),
    STATUS_PRIVATE("status.private"),
    
    // Unit
    UNIT_BLOCKS("unit.blocks"),
    UNIT_PLAYERS("unit.players"),
    
    // Error
    ERROR_PLAYER_DATA_NOT_FOUND("error.player_data_not_found"),
    GENERAL_ERROR("general.error"),
    GENERAL_NO_PERMISSION("general.no-permission"),
    
    // Settings
    SETTINGS_PERSONAL("settings.personal"),
    
    // Notification
    NOTIFICATION_MODE_ALL("notification.mode.all"),
    NOTIFICATION_MODE_FRIEND_ONLY("notification.mode.friend-only"),
    NOTIFICATION_MODE_GUILD_ONLY("notification.mode.guild-only"),
    NOTIFICATION_MODE_OFF("notification.mode.off"),
    NOTIFICATION_MODE_UNKNOWN("notification.mode.unknown"),
    NOTIFICATION_MODE_ALL_DESC("notification.mode.all.desc"),
    NOTIFICATION_MODE_FRIEND_ONLY_DESC("notification.mode.friend-only.desc"),
    NOTIFICATION_MODE_GUILD_ONLY_DESC("notification.mode.guild-only.desc"),
    NOTIFICATION_MODE_OFF_DESC("notification.mode.off.desc"),
    NOTIFICATION_MODE_UNKNOWN_DESC("notification.mode.unknown.desc"),
    
    // Mailbox
    MAILBOX_DELETE_CONFIRM_WORD("mailbox.delete_confirm_word"),

    

    // Final missing keys batch,
    GENERAL_UNKNOWN("general.unknown"),

    ECTOPLASM("ectoplasm"),
    SCUTE("scute"),
    GENERAL_SEPARATOR("general.separator"),
    GENERAL_COMING_SOON("general.coming_soon"),
    GENERAL_CANNOT_VIEW_OTHERS_QUESTS("general.cannot_view_others_quests"),
    GENERAL_CANNOT_VIEW_OTHERS_STATS("general.cannot_view_others_stats"),
    GENERAL_CANNOT_VIEW_OTHERS_TALENTS("general.cannot_view_others_talents"),
    GENERAL_CANNOT_SELECT_OTHERS_JOB("general.cannot_select_others_job"),
    ACTION_ENABLE("action.enable"),
    ACTION_DISABLE("action.disable"),
    MESSAGES_STAT_INCREASED("messages.stat_increased"),
    MESSAGES_NOT_ENOUGH_STAT_POINTS("messages.not_enough_stat_points"),
    MESSAGES_TALENT_LEARNED("messages.talent_learned"),
    MESSAGES_TALENT_CANNOT_LEARN("messages.talent_cannot_learn"),
    MESSAGES_NOT_ENOUGH_TALENT_POINTS("messages.not_enough_talent_points"),
    STATUS_ONLINE("status.online"),
    STATUS_OFFLINE("status.offline"),
    MESSAGES_NO_JOB_FOR_STATS("messages.no_job_for_stats");

    private final String key;
    
    GeneralLangKey(String key) {
        this.key = key;
    }
    
    @Override
    public String getKey() {
        return key;
    }
    
    @Override
    public String getDefaultValue() {
        return key;
    }
}