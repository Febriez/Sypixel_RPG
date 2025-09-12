package com.febrie.rpg.util.lang;

public enum MessageLangKey implements ILangKey {
    // General Messages
    MESSAGES_NO_JOB_FOR_STATS("messages.no_job_for_stats"),
    MESSAGES_STAT_INCREASED("messages.stat_increased"),
    MESSAGES_NOT_ENOUGH_STAT_POINTS("messages.not_enough_stat_points"),
    MESSAGES_TALENT_LEARNED("messages.talent_learned"),
    MESSAGES_TALENT_CANNOT_LEARN("messages.talent_cannot_learn"),
    MESSAGES_NOT_ENOUGH_TALENT_POINTS("messages.not_enough_talent_points"),
    
    // Error Messages
    ERROR_PLAYER_DATA_NOT_FOUND("error.player_data_not_found"),
    ERROR_FRIEND_REQUEST_ID_MISSING("general.error.friend_request_id_missing"),
    ERROR_FRIEND_REQUEST_NOT_FOUND("general.error.friend_request_not_found"),
    ERROR_FRIEND_ALREADY_EXISTS("general.error.friend_already_exists"),
    
    // Command Messages
    COMMAND_LEVELUP_SUCCESS("command.levelup.success"),
    COMMAND_LEVELUP_TARGET_SUCCESS("command.levelup.target_success"),
    COMMAND_LEVELUP_ERROR("command.levelup.error"),
    COMMAND_NO_PERMISSION("command.no_permission"),
    COMMAND_PLAYER_NOT_FOUND("command.player_not_found"),
    COMMAND_INVALID_ARGS("command.invalid_args"),
    COMMAND_SUCCESS("command.success"),
    COMMAND_FAILED("command.failed"),
    
    // Island Messages
    ISLAND_CREATED("island.created"),
    ISLAND_DELETED("island.deleted"),
    ISLAND_JOINED("island.joined"),
    ISLAND_LEFT("island.left"),
    ISLAND_KICKED("island.kicked"),
    ISLAND_BANNED("island.banned"),
    ISLAND_UNBANNED("island.unbanned"),
    ISLAND_PROMOTED("island.promoted"),
    ISLAND_DEMOTED("island.demoted"),
    ISLAND_TRANSFERRED("island.transferred"),
    ISLAND_NO_ISLAND("island.no_island"),
    ISLAND_ALREADY_HAS_ISLAND("island.already_has_island"),
    ISLAND_NOT_OWNER("island.not_owner"),
    ISLAND_NOT_MEMBER("island.not_member"),
    ISLAND_MEMBER_LIMIT_REACHED("island.member_limit_reached"),
    ISLAND_INVITE_SENT("island.invite_sent"),
    ISLAND_INVITE_RECEIVED("island.invite_received"),
    ISLAND_INVITE_ACCEPTED("island.invite_accepted"),
    ISLAND_INVITE_DENIED("island.invite_denied"),
    ISLAND_INVITE_EXPIRED("island.invite_expired"),
    ISLAND_TELEPORT_SUCCESS("island.teleport_success"),
    ISLAND_TELEPORT_FAILED("island.teleport_failed"),
    ISLAND_HOME_SET("island.home_set"),
    ISLAND_HOME_NOT_SET("island.home_not_set"),
    ISLAND_WARP_CREATED("island.warp_created"),
    ISLAND_WARP_DELETED("island.warp_deleted"),
    ISLAND_WARP_NOT_FOUND("island.warp_not_found"),
    ISLAND_BIOME_CHANGED("island.biome_changed"),
    ISLAND_SETTINGS_UPDATED("island.settings_updated"),
    ISLAND_PERMISSION_UPDATED("island.permission_updated"),
    ISLAND_UPGRADE_SUCCESS("island.upgrade_success"),
    ISLAND_UPGRADE_FAILED("island.upgrade_failed"),
    ISLAND_NOT_ENOUGH_MONEY("island.not_enough_money"),
    ISLAND_RESET_CONFIRM("island.reset_confirm"),
    ISLAND_RESET_SUCCESS("island.reset_success"),
    ISLAND_RESET_CANCELLED("island.reset_cancelled"),
    ISLAND_PUBLIC_ENABLED("island.public_enabled"),
    ISLAND_PUBLIC_DISABLED("island.public_disabled"),
    ISLAND_PVP_ENABLED("island.pvp_enabled"),
    ISLAND_PVP_DISABLED("island.pvp_disabled"),
    ISLAND_MOB_SPAWN_ENABLED("island.mob_spawn_enabled"),
    ISLAND_MOB_SPAWN_DISABLED("island.mob_spawn_disabled"),
    ISLAND_FIRE_SPREAD_ENABLED("island.fire_spread_enabled"),
    ISLAND_FIRE_SPREAD_DISABLED("island.fire_spread_disabled"),
    ISLAND_TNT_ENABLED("island.tnt_enabled"),
    ISLAND_TNT_DISABLED("island.tnt_disabled"),
    ISLAND_LOCKED("island.locked"),
    ISLAND_UNLOCKED("island.unlocked"),
    ISLAND_GUI_MAIN_TITLE("island.gui.main.title"),
    ISLAND_GUI_MAIN_TITLE_WITH_NAME("island.gui.main.title_with_name"),
    
    // Quest Messages
    QUEST_START_SUCCESS("quest.start_success"),
    QUEST_COMPLETE_SUCCESS("quest.complete_success"),
    QUEST_ABANDON_SUCCESS("quest.abandon_success"),
    QUEST_ALREADY_ACTIVE("quest.already_active"),
    QUEST_NOT_ACTIVE("quest.not_active"),
    QUEST_REQUIREMENTS_NOT_MET("quest.requirements_not_met"),
    QUEST_COOLDOWN_ACTIVE("quest.cooldown_active"),
    QUEST_OBJECTIVE_PROGRESS("quest.objective_progress"),
    QUEST_REWARD_CLAIMED("quest.reward_claimed"),
    QUEST_REWARD_INVENTORY_FULL("quest.reward_inventory_full"),
    QUEST_DAILY_LIMIT_REACHED("quest.daily_limit_reached"),
    QUEST_WEEKLY_LIMIT_REACHED("quest.weekly_limit_reached"),
    
    // Job Messages
    JOB_SELECTED("job.selected"),
    JOB_ALREADY_HAS("job.already_has"),
    JOB_LEVEL_UP("job.level_up"),
    JOB_MAX_LEVEL("job.max_level"),
    JOB_REQUIREMENT_NOT_MET("job.requirement_not_met"),
    JOB_SKILL_LEARNED("job.skill_learned"),
    JOB_SKILL_UPGRADED("job.skill_upgraded"),
    JOB_SKILL_MAX_LEVEL("job.skill_max_level"),
    JOB_NOT_ENOUGH_SKILL_POINTS("job.not_enough_skill_points"),
    
    // Combat Messages
    COMBAT_DAMAGE_DEALT("combat.damage_dealt"),
    COMBAT_DAMAGE_TAKEN("combat.damage_taken"),
    COMBAT_CRITICAL_HIT("combat.critical_hit"),
    COMBAT_DODGE("combat.dodge"),
    COMBAT_BLOCK("combat.block"),
    COMBAT_DEATH("combat.death"),
    COMBAT_KILL("combat.kill"),
    COMBAT_ASSIST("combat.assist"),
    COMBAT_COMBO("combat.combo"),
    COMBAT_SKILL_USED("combat.skill_used"),
    COMBAT_SKILL_COOLDOWN("combat.skill_cooldown"),
    COMBAT_SKILL_NO_MANA("combat.skill_no_mana"),
    COMBAT_BUFF_APPLIED("combat.buff_applied"),
    COMBAT_DEBUFF_APPLIED("combat.debuff_applied"),
    COMBAT_BUFF_EXPIRED("combat.buff_expired"),
    COMBAT_DEBUFF_EXPIRED("combat.debuff_expired"),
    
    // Party Messages
    PARTY_CREATED("party.created"),
    PARTY_DISBANDED("party.disbanded"),
    PARTY_JOINED("party.joined"),
    PARTY_LEFT("party.left"),
    PARTY_KICKED("party.kicked"),
    PARTY_PROMOTED("party.promoted"),
    PARTY_INVITE_SENT("party.invite_sent"),
    PARTY_INVITE_RECEIVED("party.invite_received"),
    PARTY_INVITE_ACCEPTED("party.invite_accepted"),
    PARTY_INVITE_DENIED("party.invite_denied"),
    PARTY_INVITE_EXPIRED("party.invite_expired"),
    PARTY_FULL("party.full"),
    PARTY_NOT_LEADER("party.not_leader"),
    PARTY_NOT_MEMBER("party.not_member"),
    PARTY_ALREADY_IN_PARTY("party.already_in_party"),
    PARTY_NO_PARTY("party.no_party"),
    
    // Guild Messages
    GUILD_CREATED("guild.created"),
    GUILD_DISBANDED("guild.disbanded"),
    GUILD_JOINED("guild.joined"),
    GUILD_LEFT("guild.left"),
    GUILD_KICKED("guild.kicked"),
    GUILD_PROMOTED("guild.promoted"),
    GUILD_DEMOTED("guild.demoted"),
    GUILD_INVITE_SENT("guild.invite_sent"),
    GUILD_INVITE_RECEIVED("guild.invite_received"),
    GUILD_INVITE_ACCEPTED("guild.invite_accepted"),
    GUILD_INVITE_DENIED("guild.invite_denied"),
    GUILD_INVITE_EXPIRED("guild.invite_expired"),
    GUILD_FULL("guild.full"),
    GUILD_NOT_LEADER("guild.not_leader"),
    GUILD_NOT_OFFICER("guild.not_officer"),
    GUILD_NOT_MEMBER("guild.not_member"),
    GUILD_ALREADY_IN_GUILD("guild.already_in_guild"),
    GUILD_NO_GUILD("guild.no_guild"),
    GUILD_LEVEL_UP("guild.level_up"),
    GUILD_CONTRIBUTION_ADDED("guild.contribution_added"),
    GUILD_BANK_DEPOSIT("guild.bank_deposit"),
    GUILD_BANK_WITHDRAW("guild.bank_withdraw"),
    GUILD_BANK_INSUFFICIENT_FUNDS("guild.bank_insufficient_funds"),
    GUILD_WAR_STARTED("guild.war_started"),
    GUILD_WAR_ENDED("guild.war_ended"),
    GUILD_WAR_WON("guild.war_won"),
    GUILD_WAR_LOST("guild.war_lost"),
    
    // Trade Messages
    TRADE_REQUEST_SENT("trade.request_sent"),
    TRADE_REQUEST_RECEIVED("trade.request_received"),
    TRADE_ACCEPTED("trade.accepted"),
    TRADE_CANCELLED("trade.cancelled"),
    TRADE_COMPLETED("trade.completed"),
    TRADE_ITEM_ADDED("trade.item_added"),
    TRADE_ITEM_REMOVED("trade.item_removed"),
    TRADE_MONEY_ADDED("trade.money_added"),
    TRADE_CONFIRMED("trade.confirmed"),
    TRADE_UNCONFIRMED("trade.unconfirmed"),
    TRADE_NOT_ENOUGH_SPACE("trade.not_enough_space"),
    TRADE_NOT_ENOUGH_MONEY("trade.not_enough_money"),
    
    // Friend Messages
    FRIEND_REQUEST_SENT("friend.request_sent"),
    FRIEND_REQUEST_RECEIVED("friend.request_received"),
    FRIEND_REQUEST_ACCEPTED("friend.request_accepted"),
    FRIEND_REQUEST_DENIED("friend.request_denied"),
    FRIEND_REQUEST_CANCELLED("friend.request_cancelled"),
    FRIEND_ADDED("friend.added"),
    FRIEND_REMOVED("friend.removed"),
    FRIEND_ONLINE("friend.online"),
    FRIEND_OFFLINE("friend.offline"),
    FRIEND_ALREADY_FRIENDS("friend.already_friends"),
    FRIEND_NOT_FRIENDS("friend.not_friends"),
    FRIEND_LIST_FULL("friend.list_full"),
    FRIEND_TARGET_LIST_FULL("friend.target_list_full"),
    
    // Mail Messages
    MAIL_SENT("mail.sent"),
    MAIL_RECEIVED("mail.received"),
    MAIL_READ("mail.read"),
    MAIL_DELETED("mail.deleted"),
    MAIL_ATTACHMENT_CLAIMED("mail.attachment_claimed"),
    MAIL_BOX_FULL("mail.box_full"),
    MAIL_NOT_FOUND("mail.not_found"),
    MAIL_NO_ATTACHMENTS("mail.no_attachments"),
    MAIL_INVENTORY_FULL("mail.inventory_full"),
    
    // Teleport Messages
    TELEPORT_REQUEST_SENT("teleport.request_sent"),
    TELEPORT_REQUEST_RECEIVED("teleport.request_received"),
    TELEPORT_ACCEPTED("teleport.accepted"),
    TELEPORT_DENIED("teleport.denied"),
    TELEPORT_CANCELLED("teleport.cancelled"),
    TELEPORT_EXPIRED("teleport.expired"),
    TELEPORT_IN_COMBAT("teleport.in_combat"),
    TELEPORT_COOLDOWN("teleport.cooldown"),
    TELEPORT_SUCCESS("teleport.success"),
    TELEPORT_FAILED("teleport.failed"),
    
    // Economy Messages
    ECONOMY_MONEY_ADDED("economy.money_added"),
    ECONOMY_MONEY_REMOVED("economy.money_removed"),
    ECONOMY_NOT_ENOUGH_MONEY("economy.not_enough_money"),
    ECONOMY_PAYMENT_SENT("economy.payment_sent"),
    ECONOMY_PAYMENT_RECEIVED("economy.payment_received"),
    ECONOMY_BALANCE("economy.balance"),
    
    // Shop Messages
    SHOP_ITEM_PURCHASED("shop.item_purchased"),
    SHOP_ITEM_SOLD("shop.item_sold"),
    SHOP_NOT_ENOUGH_MONEY("shop.not_enough_money"),
    SHOP_NOT_ENOUGH_ITEMS("shop.not_enough_items"),
    SHOP_INVENTORY_FULL("shop.inventory_full"),
    SHOP_ITEM_NOT_FOUND("shop.item_not_found"),
    SHOP_CLOSED("shop.closed"),
    SHOP_DISCOUNT_APPLIED("shop.discount_applied"),
    
    // Dungeon Messages
    DUNGEON_ENTERED("dungeon.entered"),
    DUNGEON_COMPLETED("dungeon.completed"),
    DUNGEON_FAILED("dungeon.failed"),
    DUNGEON_BOSS_SPAWNED("dungeon.boss_spawned"),
    DUNGEON_BOSS_DEFEATED("dungeon.boss_defeated"),
    DUNGEON_WAVE_COMPLETED("dungeon.wave_completed"),
    DUNGEON_TREASURE_FOUND("dungeon.treasure_found"),
    DUNGEON_KEY_REQUIRED("dungeon.key_required"),
    DUNGEON_COOLDOWN_ACTIVE("dungeon.cooldown_active"),
    DUNGEON_PARTY_REQUIRED("dungeon.party_required"),
    DUNGEON_LEVEL_REQUIREMENT("dungeon.level_requirement"),
    
    // Achievement Messages
    ACHIEVEMENT_UNLOCKED("achievement.unlocked"),
    ACHIEVEMENT_PROGRESS("achievement.progress"),
    ACHIEVEMENT_COMPLETED("achievement.completed"),
    ACHIEVEMENT_REWARD_CLAIMED("achievement.reward_claimed"),
    ACHIEVEMENT_ALREADY_CLAIMED("achievement.already_claimed"),
    
    // Notification Messages
    NOTIFICATION_FRIEND_ONLINE("notification.friend_online"),
    NOTIFICATION_FRIEND_OFFLINE("notification.friend_offline"),
    NOTIFICATION_GUILD_MESSAGE("notification.guild_message"),
    NOTIFICATION_PARTY_MESSAGE("notification.party_message"),
    NOTIFICATION_SYSTEM_MESSAGE("notification.system_message"),
    NOTIFICATION_ACHIEVEMENT_UNLOCKED("notification.achievement_unlocked"),
    NOTIFICATION_QUEST_COMPLETED("notification.quest_completed"),
    NOTIFICATION_LEVEL_UP("notification.level_up"),
    NOTIFICATION_MAIL_RECEIVED("notification.mail_received"),
    
    // Whisper Messages
    WHISPER_SENT("whisper.sent"),
    WHISPER_RECEIVED("whisper.received"),
    WHISPER_PLAYER_NOT_FOUND("whisper.player_not_found"),
    WHISPER_PLAYER_OFFLINE("whisper.player_offline"),
    WHISPER_DISABLED("whisper.disabled"),
    WHISPER_IGNORED("whisper.ignored"),
    WHISPER_MODE_ALL("whisper.mode.all"),
    WHISPER_MODE_FRIENDS("whisper.mode.friends"),
    WHISPER_MODE_GUILD("whisper.mode.guild"),
    WHISPER_MODE_OFF("whisper.mode.off"),
    
    // Island Messages
    ISLAND_CONTRIBUTE_AMOUNT_TOO_LOW("island.contribute.amount_too_low"),
    ISLAND_CONTRIBUTE_INVALID_AMOUNT("island.contribute.invalid_amount"),
    ISLAND_GUI_CONTRIBUTE_CONTRIBUTION_INPUT_TEXT("island.gui.contribute.contribution_input_text"),
    ISLAND_GUI_CONTRIBUTE_CONTRIBUTION_INPUT_TITLE("island.gui.contribute.contribution_input_title"),
    ISLAND_DEFAULT_NAME("island.default_name"),
    ISLAND_GUI_CREATION_SELECTED("island.gui.creation.selected"),
    ISLAND_GUI_CREATION_CLICK_TO_SELECT("island.gui.creation.click_to_select"),
    ISLAND_DELETE_CONFIRM_WORD("island.delete_confirm_word"),
    
    // Island Delete Messages
    ISLAND_DELETE_INPUT_ERROR("island.delete.input.error"),
    ISLAND_DELETE_SUCCESS_TITLE("island.delete.success.title"),
    ISLAND_DELETE_SUCCESS_MESSAGE("island.delete.success.message"),


    // Auto-added missing keys
    ISLAND_DELETE_INPUT_TEXT("island.delete.input.text"),
    ISLAND_DELETE_INPUT_TITLE("island.delete.input.title"),
    ISLAND_GUI_MAIN_CREATE_ISLAND_CONTACT_ADMIN("island.gui.main.create.island.contact.admin"),
    ISLAND_GUI_MAIN_CREATE_ISLAND_DESCRIPTION("island.gui.main.create.island.description"),
    ISLAND_GUI_MAIN_CREATE_ISLAND_FEATURE_1("island.gui.main.create.island.feature.1"),
    ISLAND_GUI_MAIN_CREATE_ISLAND_FEATURE_2("island.gui.main.create.island.feature.2"),
    ISLAND_GUI_MAIN_CREATE_ISLAND_FEATURE_3("island.gui.main.create.island.feature.3"),
    ISLAND_GUI_MAIN_CREATE_ISLAND_FEATURE_4("island.gui.main.create.island.feature.4"),
    ISLAND_GUI_MAIN_CREATE_ISLAND_NO_ISLAND("island.gui.main.create.island.no.island"),
    ISLAND_GUI_MAIN_CREATE_ISLAND_TITLE("island.gui.main.create.island.title"),
    ISLAND_MEMBER_KICK_CONFIRM_WORD("island.member.kick.confirm.word"),
    ISLAND_MEMBER_KICK_INPUT_ERROR("island.member.kick.input.error"),
    ISLAND_MEMBER_KICK_INPUT_TEXT("island.member.kick.input.text"),
    ISLAND_MEMBER_KICK_INPUT_TITLE("island.member.kick.input.title"),
    ISLAND_ROLES_MEMBER("island.roles.member"),
    ISLAND_ROLES_SUB_OWNER("island.roles.sub.owner"),
    ISLAND_ROLES_WORKER("island.roles.worker"),

    

    // Additional missing keys,
    ISLAND_GUI_CREATION_ISLAND_NAME_INPUT_TITLE("island.gui.creation.island.name.input.title"),
    ISLAND_SETTINGS_NAME_ERROR("island.settings.name.error"),
    ISLAND_SETTINGS_NAME_INPUT_ERROR("island.settings.name.input.error");
    private final String key;
    
    MessageLangKey(String key) {
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