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
    MESSAGES_NO_JOB_FOR_STATS("messages.no_job_for_stats"),

    // Biome names
    BIOME_PLAINS_NAME("biome.plains.name"),
    BIOME_FOREST_NAME("biome.forest.name"),
    BIOME_DESERT_NAME("biome.desert.name"),
    BIOME_JUNGLE_NAME("biome.jungle.name"),
    BIOME_TAIGA_NAME("biome.taiga.name"),
    BIOME_SNOWY_PLAINS_NAME("biome.snowy_plains.name"),
    BIOME_SAVANNA_NAME("biome.savanna.name"),
    BIOME_SWAMP_NAME("biome.swamp.name"),
    BIOME_MUSHROOM_FIELDS_NAME("biome.mushroom_fields.name"),
    BIOME_BEACH_NAME("biome.beach.name"),
    BIOME_FLOWER_FOREST_NAME("biome.flower_forest.name"),
    BIOME_BAMBOO_JUNGLE_NAME("biome.bamboo_jungle.name"),
    BIOME_DARK_FOREST_NAME("biome.dark_forest.name"),
    BIOME_BIRCH_FOREST_NAME("biome.birch_forest.name"),
    BIOME_BADLANDS_NAME("biome.badlands.name"),
    BIOME_CHERRY_GROVE_NAME("biome.cherry_grove.name"),
    BIOME_OCEAN_NAME("biome.ocean.name"),

    // Biome descriptions
    BIOME_PLAINS_DESCRIPTION("biome.plains.description"),
    BIOME_FOREST_DESCRIPTION("biome.forest.description"),
    BIOME_DESERT_DESCRIPTION("biome.desert.description"),
    BIOME_JUNGLE_DESCRIPTION("biome.jungle.description"),
    BIOME_TAIGA_DESCRIPTION("biome.taiga.description"),
    BIOME_SNOWY_PLAINS_DESCRIPTION("biome.snowy_plains.description"),
    BIOME_SAVANNA_DESCRIPTION("biome.savanna.description"),
    BIOME_SWAMP_DESCRIPTION("biome.swamp.description"),
    BIOME_MUSHROOM_FIELDS_DESCRIPTION("biome.mushroom_fields.description"),
    BIOME_BEACH_DESCRIPTION("biome.beach.description"),
    BIOME_FLOWER_FOREST_DESCRIPTION("biome.flower_forest.description"),
    BIOME_BAMBOO_JUNGLE_DESCRIPTION("biome.bamboo_jungle.description"),
    BIOME_DARK_FOREST_DESCRIPTION("biome.dark_forest.description"),
    BIOME_BIRCH_FOREST_DESCRIPTION("biome.birch_forest.description"),
    BIOME_BADLANDS_DESCRIPTION("biome.badlands.description"),
    BIOME_CHERRY_GROVE_DESCRIPTION("biome.cherry_grove.description"),
    BIOME_OCEAN_DESCRIPTION("biome.ocean.description"),

    // Items - Buttons
    ITEMS_BUTTONS_CANCEL_NAME("items.buttons.cancel.name"),
    ITEMS_BUTTONS_BACK_NAME("items.buttons.back.name"),
    ITEMS_BUTTONS_BACK_LORE("items.buttons.back.lore"),
    ITEMS_BUTTONS_NEXT_PAGE_NAME("items.buttons.next_page.name"),
    ITEMS_BUTTONS_NEXT_PAGE_LORE("items.buttons.next_page.lore"),
    ITEMS_BUTTONS_PREVIOUS_PAGE_NAME("items.buttons.previous_page.name"),
    ITEMS_BUTTONS_PREVIOUS_PAGE_LORE("items.buttons.previous_page.lore"),

    // Items - Island Main
    ITEMS_ISLAND_MAIN_BIOME_CHANGE_LORE("items.island.main.biome_change.lore"),
    ITEMS_ISLAND_MAIN_BIOME_CHANGE_NAME("items.island.main.biome_change.name"),
    ITEMS_ISLAND_MAIN_CONTRIBUTION_INFO_LORE("items.island.main.contribution_info.lore"),
    ITEMS_ISLAND_MAIN_CONTRIBUTION_INFO_NAME("items.island.main.contribution_info.name"),
    ITEMS_ISLAND_MAIN_INFO_LORE("items.island.main.info.lore"),
    ITEMS_ISLAND_MAIN_ISLAND_SETTINGS_LORE("items.island.main.island_settings.lore"),
    ITEMS_ISLAND_MAIN_ISLAND_SETTINGS_NAME("items.island.main.island_settings.name"),
    ITEMS_ISLAND_MAIN_UPGRADE_INFO_LORE("items.island.main.upgrade_info.lore"),
    ITEMS_ISLAND_MAIN_UPGRADE_INFO_NAME("items.island.main.upgrade_info.name"),
    ITEMS_ISLAND_MAIN_VISITOR_LORE("items.island.main.visitor.lore"),
    ITEMS_ISLAND_MAIN_VISITOR_NAME("items.island.main.visitor.name"),
    ITEMS_ISLAND_MAIN_WARP_LORE("items.island.main.warp.lore"),
    ITEMS_ISLAND_MAIN_WARP_NAME("items.island.main.warp.name"),

    // Items - Island Member
    ITEMS_ISLAND_MEMBER_INVITE_BUTTON_LORE("items.island.member.invite_button.lore"),
    ITEMS_ISLAND_MEMBER_INVITE_LORE("items.island.member.invite.lore"),
    ITEMS_ISLAND_MEMBER_INVITE_NAME("items.island.member.invite.name"),
    ITEMS_ISLAND_MEMBER_MANAGE_DEMOTE_LORE("items.island.member_manage.demote.lore"),
    ITEMS_ISLAND_MEMBER_MANAGE_DEMOTE_NAME("items.island.member_manage.demote.name"),
    ITEMS_ISLAND_MEMBER_MANAGE_KICK_LORE("items.island.member_manage.kick.lore"),
    ITEMS_ISLAND_MEMBER_MANAGE_KICK_NAME("items.island.member_manage.kick.name"),
    ITEMS_ISLAND_MEMBER_MANAGE_MEMBER_INFO_LORE("items.island.member_manage.member_info.lore"),
    ITEMS_ISLAND_MEMBER_MANAGE_MEMBER_INFO_NAME("items.island.member_manage.member_info.name"),
    ITEMS_ISLAND_MEMBER_MANAGE_NO_PERMISSION_LORE("items.island.member_manage.no_permission.lore"),
    ITEMS_ISLAND_MEMBER_MANAGE_NO_PERMISSION_NAME("items.island.member_manage.no_permission.name"),
    ITEMS_ISLAND_MEMBER_MANAGE_PERMISSION_LORE("items.island.member_manage.permission.lore"),
    ITEMS_ISLAND_MEMBER_MANAGE_PERMISSION_NAME("items.island.member_manage.permission.name"),
    ITEMS_ISLAND_MEMBER_MANAGE_PROMOTE_LORE("items.island.member_manage.promote.lore"),
    ITEMS_ISLAND_MEMBER_MANAGE_PROMOTE_NAME("items.island.member_manage.promote.name"),
    ITEMS_ISLAND_MEMBER_MANAGE_TO_MEMBER_LORE("items.island.member_manage.to_member.lore"),
    ITEMS_ISLAND_MEMBER_MANAGE_TO_MEMBER_NAME("items.island.member_manage.to_member.name"),
    ITEMS_ISLAND_MEMBER_MANAGE_TO_WORKER_LORE("items.island.member_manage.to_worker.lore"),
    ITEMS_ISLAND_MEMBER_MANAGE_TO_WORKER_NAME("items.island.member_manage.to_worker.name"),
    ITEMS_ISLAND_MEMBER_NEXT_PAGE_LORE("items.island.member.next_page.lore"),
    ITEMS_ISLAND_MEMBER_PERMISSION_LORE("items.island.member.permission.lore"),
    ITEMS_ISLAND_MEMBER_PERMISSION_NAME("items.island.member.permission.name"),
    ITEMS_ISLAND_MEMBER_PREVIOUS_PAGE_LORE("items.island.member.previous_page.lore"),

    // Items - Island Personal Spawn
    ITEMS_ISLAND_PERSONAL_SPAWN_BACK_LORE("items.island.personal_spawn.back.lore"),
    ITEMS_ISLAND_PERSONAL_SPAWN_INFO_LORE("items.island.personal_spawn.info.lore"),
    ITEMS_ISLAND_PERSONAL_SPAWN_INFO_NAME("items.island.personal_spawn.info.name"),
    ITEMS_ISLAND_PERSONAL_SPAWN_INFO_NOT_SET_LORE("items.island.personal_spawn.info_not_set.lore"),
    ITEMS_ISLAND_PERSONAL_SPAWN_MANAGE_CLICK("items.island.personal_spawn.manage.click"),
    ITEMS_ISLAND_PERSONAL_SPAWN_MANAGE_LORE("items.island.personal_spawn.manage.lore"),
    ITEMS_ISLAND_PERSONAL_SPAWN_MANAGE_NAME("items.island.personal_spawn.manage.name"),
    ITEMS_ISLAND_PERSONAL_SPAWN_MANAGE_STATUS("items.island.personal_spawn.manage.status"),
    ITEMS_ISLAND_PERSONAL_SPAWN_NO_PERMISSION_LORE("items.island.personal_spawn.no_permission.lore"),
    ITEMS_ISLAND_PERSONAL_SPAWN_NO_PERMISSION_NAME("items.island.personal_spawn.no_permission.name"),
    ITEMS_ISLAND_PERSONAL_SPAWN_REMOVE_CLICK("items.island.personal_spawn.remove.click"),
    ITEMS_ISLAND_PERSONAL_SPAWN_REMOVE_LORE("items.island.personal_spawn.remove.lore"),
    ITEMS_ISLAND_PERSONAL_SPAWN_REMOVE_NAME("items.island.personal_spawn.remove.name"),
    ITEMS_ISLAND_PERSONAL_SPAWN_SET_CLICK("items.island.personal_spawn.set.click"),
    ITEMS_ISLAND_PERSONAL_SPAWN_SET_LORE("items.island.personal_spawn.set.lore"),
    ITEMS_ISLAND_PERSONAL_SPAWN_SET_NAME("items.island.personal_spawn.set.name"),
    ITEMS_ISLAND_PERSONAL_SPAWN_TELEPORT_CLICK("items.island.personal_spawn.teleport.click"),
    ITEMS_ISLAND_PERSONAL_SPAWN_TELEPORT_LORE("items.island.personal_spawn.teleport.lore"),
    ITEMS_ISLAND_PERSONAL_SPAWN_TELEPORT_NAME("items.island.personal_spawn.teleport.name"),
    ITEMS_ISLAND_PERSONAL_SPAWN_TELEPORT_NO_SPAWN("items.island.personal_spawn.teleport_no_spawn"),

    // Items - Island Settings
    ITEMS_ISLAND_SETTINGS_BIOME_CHANGE_LORE("items.island.settings.biome_change.lore"),
    ITEMS_ISLAND_SETTINGS_BIOME_CHANGE_NAME("items.island.settings.biome_change.name"),
    ITEMS_ISLAND_SETTINGS_DELETE_LORE("items.island.settings.delete.lore"),
    ITEMS_ISLAND_SETTINGS_DELETE_NAME("items.island.settings.delete.name"),
    ITEMS_ISLAND_SETTINGS_INFO_NAME("items.island.settings.info.name"),
    ITEMS_ISLAND_SETTINGS_NAME_CHANGE_LORE("items.island.settings.name_change.lore"),
    ITEMS_ISLAND_SETTINGS_NAME_CHANGE_NAME("items.island.settings.name_change.name"),
    ITEMS_ISLAND_SETTINGS_PRIVATE_NAME("items.island.settings.private.name"),
    ITEMS_ISLAND_SETTINGS_PUBLIC_NAME("items.island.settings.public.name"),
    ITEMS_ISLAND_SETTINGS_PUBLIC_TOGGLE_LORE("items.island.settings.public_toggle.lore"),
    ITEMS_ISLAND_SETTINGS_SAVE_LORE("items.island.settings.save.lore"),
    ITEMS_ISLAND_SETTINGS_SAVE_NAME("items.island.settings.save.name"),
    ITEMS_ISLAND_SETTINGS_CANCEL_LORE("items.island.settings.cancel.lore"),

    // Items - Island Spawn
    ITEMS_ISLAND_SPAWN_CURRENT_INFO_LORE("items.island.spawn.current_info.lore"),
    ITEMS_ISLAND_SPAWN_CURRENT_INFO_NAME("items.island.spawn.current_info.name"),
    ITEMS_ISLAND_SPAWN_MESSAGE_NAME("items.island.spawn.message.name"),
    ITEMS_ISLAND_SPAWN_MESSAGE_LORE("items.island.spawn.message.lore"),
    ITEMS_ISLAND_SPAWN_NO_PERMISSION_NAME("items.island.spawn.no_permission.name"),
    ITEMS_ISLAND_SPAWN_NO_PERMISSION_LORE("items.island.spawn.no_permission.lore"),
    ITEMS_ISLAND_SPAWN_PERSONAL_NAME("items.island.spawn.personal.name"),
    ITEMS_ISLAND_SPAWN_PERSONAL_LORE("items.island.spawn.personal.lore"),
    ITEMS_ISLAND_SPAWN_PROTECTION_NAME("items.island.spawn.protection.name"),
    ITEMS_ISLAND_SPAWN_PROTECTION_LORE("items.island.spawn.protection.lore"),
    ITEMS_ISLAND_SPAWN_RESET_NAME("items.island.spawn.reset.name"),
    ITEMS_ISLAND_SPAWN_RESET_LORE("items.island.spawn.reset.lore"),
    ITEMS_ISLAND_SPAWN_SET_MAIN_NAME("items.island.spawn.set_main.name"),
    ITEMS_ISLAND_SPAWN_SET_MAIN_LORE("items.island.spawn.set_main.lore"),
    ITEMS_ISLAND_SPAWN_SET_VISITOR_NAME("items.island.spawn.set_visitor.name"),
    ITEMS_ISLAND_SPAWN_SET_VISITOR_LORE("items.island.spawn.set_visitor.lore"),

    // Items - Island Upgrade
    ITEMS_ISLAND_UPGRADE_INFO_NAME("items.island.upgrade_info.name"),
    ITEMS_ISLAND_UPGRADE_INFO_LORE("items.island.upgrade_info.lore"),
    ITEMS_ISLAND_UPGRADE_MEMBER_NAME("items.island.upgrade.member.name"),
    ITEMS_ISLAND_UPGRADE_SIZE_NAME("items.island.upgrade.size.name"),
    ITEMS_ISLAND_UPGRADE_WORKER_NAME("items.island.upgrade.worker.name"),

    // Items - Island Visitor
    ITEMS_ISLAND_VISITOR_INFO_NAME("items.island.visitor.info.name"),
    ITEMS_ISLAND_VISITOR_INFO_LORE("items.island.visitor.info.lore"),
    ITEMS_ISLAND_VISITOR_NO_VISITORS_NAME("items.island.visitor.no_visitors.name"),
    ITEMS_ISLAND_VISITOR_NO_VISITORS_LORE("items.island.visitor.no_visitors.lore"),
    ITEMS_ISLAND_VISITOR_STATISTICS_NAME("items.island.visitor.statistics.name"),

    // Items - Island Creation
    ITEMS_ISLAND_CREATION_TITLE_NAME("items.island.creation.title.name"),
    ITEMS_ISLAND_CREATION_TITLE_LORE("items.island.creation.title.lore"),
    ITEMS_ISLAND_CREATION_NAME_NAME("items.island.creation.name.name"),
    ITEMS_ISLAND_CREATION_NAME_LORE("items.island.creation.name.lore"),
    ITEMS_ISLAND_CREATION_COLOR_NAME("items.island.creation.color.name"),
    ITEMS_ISLAND_CREATION_COLOR_LORE("items.island.creation.color.lore"),
    ITEMS_ISLAND_CREATION_BIOME_NAME("items.island.creation.biome.name"),
    ITEMS_ISLAND_CREATION_BIOME_LORE("items.island.creation.biome.lore"),
    ITEMS_ISLAND_CREATION_TEMPLATE_BASIC_NAME("items.island.creation.template.basic.name"),
    ITEMS_ISLAND_CREATION_TEMPLATE_BASIC_LORE("items.island.creation.template.basic.lore"),
    ITEMS_ISLAND_CREATION_TEMPLATE_LARGE_NAME("items.island.creation.template.large.name"),
    ITEMS_ISLAND_CREATION_TEMPLATE_LARGE_LORE("items.island.creation.template.large.lore"),
    ITEMS_ISLAND_CREATION_TEMPLATE_SKYBLOCK_NAME("items.island.creation.template.skyblock.name"),
    ITEMS_ISLAND_CREATION_TEMPLATE_SKYBLOCK_LORE("items.island.creation.template.skyblock.lore"),
    ITEMS_ISLAND_CREATION_TEMPLATE_WATER_NAME("items.island.creation.template.water.name"),
    ITEMS_ISLAND_CREATION_TEMPLATE_WATER_LORE("items.island.creation.template.water.lore"),
    ITEMS_ISLAND_CREATION_CREATE_BUTTON_NAME("items.island.creation.create_button.name"),
    ITEMS_ISLAND_CREATION_CREATE_BUTTON_LORE("items.island.creation.create_button.lore"),

    // Items - Loading
    ITEMS_LOADING_NAME("items.loading.name"),

    // Items - Social Mailbox
    ITEMS_SOCIAL_MAILBOX_TITLE_NAME("items.social.mailbox.title.name"),
    ITEMS_SOCIAL_MAILBOX_TITLE_LORE("items.social.mailbox.title.lore"),
    ITEMS_SOCIAL_MAILBOX_REFRESH_NAME("items.social.mailbox.refresh.name"),
    ITEMS_SOCIAL_MAILBOX_REFRESH_LORE("items.social.mailbox.refresh.lore"),
    ITEMS_SOCIAL_MAILBOX_REFRESH_CLICK("items.social.mailbox.refresh.click"),
    ITEMS_SOCIAL_MAILBOX_SEND_NAME("items.social.mailbox.send.name"),
    ITEMS_SOCIAL_MAILBOX_SEND_LORE("items.social.mailbox.send.lore"),
    ITEMS_SOCIAL_MAILBOX_SEND_CLICK("items.social.mailbox.send.click"),
    ITEMS_SOCIAL_MAILBOX_DELETE_READ_NAME("items.social.mailbox.delete_read.name"),
    ITEMS_SOCIAL_MAILBOX_DELETE_READ_LORE("items.social.mailbox.delete_read.lore"),
    ITEMS_SOCIAL_MAILBOX_DELETE_READ_CLICK("items.social.mailbox.delete_read.click"),
    ITEMS_SOCIAL_MAILBOX_NO_MAILS_NAME("items.social.mailbox.no_mails.name"),
    ITEMS_SOCIAL_MAILBOX_NO_UNREAD_MAILS_NAME("items.social.mailbox.no_unread_mails.name"),
    ITEMS_SOCIAL_MAILBOX_NO_MAILS_LORE("items.social.mailbox.no_mails.lore"),
    ITEMS_SOCIAL_MAILBOX_MAIL_ITEM_CLICK("items.social.mailbox.mail_item.click"),

    // Items - Social Friends
    ITEMS_SOCIAL_FRIENDS_TITLE_NAME("items.social.friends.title.name"),
    ITEMS_SOCIAL_FRIENDS_TITLE_LORE("items.social.friends.title.lore"),
    ITEMS_SOCIAL_FRIENDS_REFRESH_NAME("items.social.friends.refresh.name"),
    ITEMS_SOCIAL_FRIENDS_REFRESH_LORE("items.social.friends.refresh.lore"),
    ITEMS_SOCIAL_FRIENDS_REFRESH_CLICK("items.social.friends.refresh.click"),
    ITEMS_SOCIAL_FRIENDS_NO_FRIENDS_NAME("items.social.friends.no_friends.name"),
    ITEMS_SOCIAL_FRIENDS_NO_FRIENDS_LORE("items.social.friends.no_friends.lore"),
    ITEMS_SOCIAL_FRIENDS_FRIEND_ITEM_LEFT_CLICK("items.social.friends.friend_item.left_click"),

    // Items - Settings
    ITEMS_SETTINGS_NOTIFICATION_SETTINGS_WHISPER_DESC("items.settings.notification_settings.whisper.desc"),
    ITEMS_SETTINGS_NOTIFICATION_SETTINGS_WHISPER_NOTE("items.settings.notification_settings.whisper.note"),
    ITEMS_SETTINGS_NOTIFICATION_SETTINGS_SERVER_DESC("items.settings.notification_settings.server.desc"),
    ITEMS_SETTINGS_NOTIFICATION_SETTINGS_SERVER_EXAMPLE_TITLE("items.settings.notification_settings.server.example_title"),
    ITEMS_SETTINGS_NOTIFICATION_SETTINGS_SERVER_EXAMPLES("items.settings.notification_settings.server.examples"),
    ITEMS_SETTINGS_SYSTEM_SETTINGS_CONFIRMATION_DESC("items.settings.system_settings.confirmation.desc"),
    ITEMS_SETTINGS_SYSTEM_SETTINGS_CONFIRMATION_EXAMPLE_TITLE("items.settings.system_settings.confirmation.example_title"),
    ITEMS_SETTINGS_SYSTEM_SETTINGS_CONFIRMATION_EXAMPLE("items.settings.system_settings.confirmation.example"),
    ITEMS_SETTINGS_INGAME_SETTINGS_QUEST_GUIDE_DESC("items.settings.ingame_settings.quest_guide.desc"),
    ITEMS_SETTINGS_INGAME_SETTINGS_DAMAGE_DISPLAY_DESC("items.settings.ingame_settings.damage_display.desc"),
    ITEMS_SETTINGS_GUI_SETTINGS_TITLE_LORE("items.settings.gui_settings.title.lore"),

    // Items - Quest Dialog
    ITEMS_QUEST_DIALOG_ACCEPT_NAME("items.quest_dialog.accept.name"),
    ITEMS_QUEST_DIALOG_ACCEPT_LORE("items.quest_dialog.accept.lore"),
    ITEMS_QUEST_DIALOG_DECLINE_NAME("items.quest_dialog.decline.name"),
    ITEMS_QUEST_DIALOG_DECLINE_LORE("items.quest_dialog.decline.lore"),
    ITEMS_QUEST_DIALOG_CLOSE_NAME("items.quest_dialog.close.name"),
    ITEMS_QUEST_DIALOG_CLOSE_LORE("items.quest_dialog.close.lore"),

    // Items - Quest List
    ITEMS_QUEST_LIST_ACTIVE_NAME("items.quest_list.active.name"),
    ITEMS_QUEST_LIST_ACTIVE_LORE("items.quest_list.active.lore"),
    ITEMS_QUEST_LIST_COMPLETED_NAME("items.quest_list.completed.name"),
    ITEMS_QUEST_LIST_COMPLETED_LORE("items.quest_list.completed.lore"),
    ITEMS_QUEST_LIST_VIEW_ALL("items.quest_list.view_all"),

    // Items - Quest Detail
    ITEMS_QUEST_DETAIL_OBJECTIVES_NAME("items.quest_detail.objectives.name"),
    ITEMS_QUEST_DETAIL_PROGRESS_NAME("items.quest_detail.progress.name"),
    ITEMS_QUEST_DETAIL_REWARDS_NAME("items.quest_detail.rewards.name"),

    // Items - Quest Reward
    ITEMS_QUEST_REWARD_ALREADY_CLAIMED_NAME("items.quest_reward.already_claimed.name"),
    ITEMS_QUEST_REWARD_ALREADY_CLAIMED_LORE("items.quest_reward.already_claimed.lore"),
    ITEMS_QUEST_REWARD_CLAIM_ALL_NAME("items.quest_reward.claim_all.name"),
    ITEMS_QUEST_REWARD_CLAIM_ALL_LORE("items.quest_reward.claim_all.lore"),
    ITEMS_QUEST_REWARD_CLAIMED_NAME("items.quest_reward.claimed.name"),
    ITEMS_QUEST_REWARD_CONFIRM_NO_NAME("items.quest_reward.confirm_no.name"),
    ITEMS_QUEST_REWARD_CONFIRM_NO_LORE("items.quest_reward.confirm_no.lore"),
    ITEMS_QUEST_REWARD_CONFIRM_WARNING_NAME("items.quest_reward.confirm_warning.name"),
    ITEMS_QUEST_REWARD_CONFIRM_WARNING_LORE("items.quest_reward.confirm_warning.lore"),
    ITEMS_QUEST_REWARD_CONFIRM_WARNING_QUESTION("items.quest_reward.confirm_warning.question"),
    ITEMS_QUEST_REWARD_CONFIRM_YES_NAME("items.quest_reward.confirm_yes.name"),
    ITEMS_QUEST_REWARD_CONFIRM_YES_LORE("items.quest_reward.confirm_yes.lore"),
    ITEMS_QUEST_REWARD_DESTROY_NAME("items.quest_reward.destroy.name"),
    ITEMS_QUEST_REWARD_DESTROY_LORE("items.quest_reward.destroy.lore"),
    ITEMS_QUEST_REWARD_NO_ITEMS_NAME("items.quest_reward.no_items.name"),
    ITEMS_QUEST_REWARD_NO_ITEMS_LORE("items.quest_reward.no_items.lore"),

    // Items - Quest Selection
    ITEMS_QUEST_SELECTION_CLICK_HINT("items.quest_selection.click_hint"),

    // Items - Main Menu
    ITEMS_MAINMENU_JOB_BUTTON_NAME("items.mainmenu.job_button.name"),
    ITEMS_MAINMENU_JOB_BUTTON_LORE("items.mainmenu.job_button.lore"),
    ITEMS_MAINMENU_STATS_BUTTON_NAME("items.mainmenu.stats_button.name"),
    ITEMS_MAINMENU_STATS_BUTTON_LORE("items.mainmenu.stats_button.lore"),
    ITEMS_MAINMENU_SHOP_BUTTON_NAME("items.mainmenu.shop_button.name"),
    ITEMS_MAINMENU_SHOP_BUTTON_LORE("items.mainmenu.shop_button.lore"),
    ITEMS_MAINMENU_DUNGEON_BUTTON_NAME("items.mainmenu.dungeon_button.name"),
    ITEMS_MAINMENU_DUNGEON_BUTTON_LORE("items.mainmenu.dungeon_button.lore"),
    ITEMS_MAINMENU_WILD_BUTTON_NAME("items.mainmenu.wild_button.name"),
    ITEMS_MAINMENU_WILD_BUTTON_LORE("items.mainmenu.wild_button.lore"),
    ITEMS_MAINMENU_ISLAND_BUTTON_NAME("items.mainmenu.island_button.name"),
    ITEMS_MAINMENU_ISLAND_BUTTON_LORE("items.mainmenu.island_button.lore"),
    ITEMS_MAINMENU_PROFILE_BUTTON_NAME("items.mainmenu.profile_button.name"),
    ITEMS_MAINMENU_PROFILE_BUTTON_LORE("items.mainmenu.profile_button.lore"),

    // Items - Profile
    ITEMS_PROFILE_COLLECTION_BOOK_NAME("items.profile.collection_book.name"),
    ITEMS_PROFILE_GAME_STATS_NAME("items.profile.game_stats.name"),
    ITEMS_PROFILE_LEVEL_INFO_NAME("items.profile.level_info.name"),
    ITEMS_PROFILE_PETS_NAME("items.profile.pets.name"),
    ITEMS_PROFILE_QUEST_INFO_NAME("items.profile.quest_info.name"),
    ITEMS_PROFILE_QUEST_INFO_CLICK_LORE("items.profile.quest_info.click_lore"),
    ITEMS_PROFILE_USER_SETTINGS_NAME("items.profile.user_settings.name"),
    ITEMS_PROFILE_USER_SETTINGS_LORE("items.profile.user_settings.lore"),

    // Items - Settings GUI Settings
    ITEMS_SETTINGS_GUI_SETTINGS_TITLE_NAME("items.settings.gui_settings.title.name"),
    ITEMS_SETTINGS_GUI_SETTINGS_MUTE_NAME("items.settings.gui_settings.mute.name"),
    ITEMS_SETTINGS_GUI_SETTINGS_MUTE_LORE("items.settings.gui_settings.mute.lore"),
    ITEMS_SETTINGS_GUI_SETTINGS_UNMUTE_NAME("items.settings.gui_settings.unmute.name"),
    ITEMS_SETTINGS_GUI_SETTINGS_UNMUTE_LORE("items.settings.gui_settings.unmute.lore"),
    ITEMS_SETTINGS_GUI_SETTINGS_VOLUME_DECREASE_NAME("items.settings.gui_settings.volume_decrease.name"),
    ITEMS_SETTINGS_GUI_SETTINGS_VOLUME_DECREASE_LORE("items.settings.gui_settings.volume_decrease.lore"),
    ITEMS_SETTINGS_GUI_SETTINGS_VOLUME_INCREASE_NAME("items.settings.gui_settings.volume_increase.name"),
    ITEMS_SETTINGS_GUI_SETTINGS_VOLUME_INCREASE_LORE("items.settings.gui_settings.volume_increase.lore"),
    ITEMS_SETTINGS_GUI_SETTINGS_VOLUME_NAME("items.settings.gui_settings.volume.name"),
    ITEMS_SETTINGS_GUI_SETTINGS_VOLUME_LORE("items.settings.gui_settings.volume.lore"),

    // Items - Settings Ingame
    ITEMS_SETTINGS_INGAME_SETTINGS_TITLE_NAME("items.settings.ingame_settings.title.name"),
    ITEMS_SETTINGS_INGAME_SETTINGS_TITLE_LORE("items.settings.ingame_settings.title.lore"),
    ITEMS_SETTINGS_INGAME_SETTINGS_QUEST_GUIDE_NAME("items.settings.ingame_settings.quest_guide.name"),
    ITEMS_SETTINGS_INGAME_SETTINGS_QUEST_GUIDE_LORE("items.settings.ingame_settings.quest_guide.lore"),
    ITEMS_SETTINGS_INGAME_SETTINGS_DAMAGE_DISPLAY_NAME("items.settings.ingame_settings.damage_display.name"),
    ITEMS_SETTINGS_INGAME_SETTINGS_DAMAGE_DISPLAY_LORE("items.settings.ingame_settings.damage_display.lore"),
    ITEMS_SETTINGS_INGAME_SETTINGS_AUTO_SELL_NAME("items.settings.ingame_settings.auto_sell.name"),
    ITEMS_SETTINGS_INGAME_SETTINGS_AUTO_SELL_DESC("items.settings.ingame_settings.auto_sell.desc"),
    ITEMS_SETTINGS_INGAME_SETTINGS_AUTO_SELL_LORE("items.settings.ingame_settings.auto_sell.lore"),
    ITEMS_SETTINGS_INGAME_SETTINGS_DIALOG_SPEED_NAME("items.settings.ingame_settings.dialog_speed.name"),
    ITEMS_SETTINGS_INGAME_SETTINGS_DIALOG_SPEED_LORE("items.settings.ingame_settings.dialog_speed.lore"),
    ITEMS_SETTINGS_INGAME_SETTINGS_DIALOG_SPEED_DESC("items.settings.ingame_settings.dialog_speed.desc"),
    ITEMS_SETTINGS_INGAME_SETTINGS_DIALOG_SPEED_NORMAL("items.settings.ingame_settings.dialog_speed.normal"),
    ITEMS_SETTINGS_INGAME_SETTINGS_DIALOG_SPEED_FAST("items.settings.ingame_settings.dialog_speed.fast"),
    ITEMS_SETTINGS_INGAME_SETTINGS_DIALOG_SPEED_INSTANT("items.settings.ingame_settings.dialog_speed.instant"),

    // Items - Settings Notification
    ITEMS_SETTINGS_NOTIFICATION_SETTINGS_TITLE_NAME("items.settings.notification_settings.title.name"),
    ITEMS_SETTINGS_NOTIFICATION_SETTINGS_TITLE_LORE("items.settings.notification_settings.title.lore"),
    ITEMS_SETTINGS_NOTIFICATION_SETTINGS_WHISPER_NAME("items.settings.notification_settings.whisper.name"),
    ITEMS_SETTINGS_NOTIFICATION_SETTINGS_INVITE_NAME("items.settings.notification_settings.invite.name"),
    ITEMS_SETTINGS_NOTIFICATION_SETTINGS_INVITE_DESC("items.settings.notification_settings.invite.desc"),
    ITEMS_SETTINGS_NOTIFICATION_SETTINGS_INVITE_CLICK_HINT("items.settings.notification_settings.invite.click_hint"),
    ITEMS_SETTINGS_NOTIFICATION_SETTINGS_INVITE_MODE_CYCLE("items.settings.notification_settings.invite.mode_cycle"),
    ITEMS_SETTINGS_NOTIFICATION_SETTINGS_INVITE_NOTE("items.settings.notification_settings.invite.note"),
    ITEMS_SETTINGS_NOTIFICATION_SETTINGS_SERVER_NAME("items.settings.notification_settings.server.name"),

    // Items - Settings System
    ITEMS_SETTINGS_SYSTEM_SETTINGS_TITLE_NAME("items.settings.system_settings.title.name"),
    ITEMS_SETTINGS_SYSTEM_SETTINGS_TITLE_LORE("items.settings.system_settings.title.lore"),
    ITEMS_SETTINGS_SYSTEM_SETTINGS_CONFIRMATION_NAME("items.settings.system_settings.confirmation.name"),
    ITEMS_SETTINGS_SYSTEM_SETTINGS_CONFIRMATION_LORE("items.settings.system_settings.confirmation.lore"),

    // Items - Settings Player
    ITEMS_SETTINGS_PLAYER_SETTINGS_TITLE_NAME("items.settings.player_settings.title.name"),
    ITEMS_SETTINGS_PLAYER_SETTINGS_TITLE_LORE("items.settings.player_settings.title.lore"),
    ITEMS_SETTINGS_PLAYER_SETTINGS_INGAME_NAME("items.settings.player_settings.ingame.name"),
    ITEMS_SETTINGS_PLAYER_SETTINGS_INGAME_LORE("items.settings.player_settings.ingame.lore"),
    ITEMS_SETTINGS_PLAYER_SETTINGS_NOTIFICATION_NAME("items.settings.player_settings.notification.name"),
    ITEMS_SETTINGS_PLAYER_SETTINGS_NOTIFICATION_LORE("items.settings.player_settings.notification.lore"),
    ITEMS_SETTINGS_PLAYER_SETTINGS_GUI_NAME("items.settings.player_settings.gui.name"),
    ITEMS_SETTINGS_PLAYER_SETTINGS_GUI_LORE("items.settings.player_settings.gui.lore"),
    ITEMS_SETTINGS_PLAYER_SETTINGS_SYSTEM_NAME("items.settings.player_settings.system.name"),
    ITEMS_SETTINGS_PLAYER_SETTINGS_SYSTEM_LORE("items.settings.player_settings.system.lore"),
    ITEMS_SETTINGS_PLAYER_SETTINGS_SOCIAL_NAME("items.settings.player_settings.social.name"),
    ITEMS_SETTINGS_PLAYER_SETTINGS_SOCIAL_LORE("items.settings.player_settings.social.lore"),

    // Items - Settings Ingame Speed controls
    ITEMS_SETTINGS_INGAME_SETTINGS_SPEED_DECREASE_NAME("items.settings.ingame_settings.speed_decrease.name"),
    ITEMS_SETTINGS_INGAME_SETTINGS_SPEED_DECREASE_LORE("items.settings.ingame_settings.speed_decrease.lore"),
    ITEMS_SETTINGS_INGAME_SETTINGS_SPEED_INCREASE_NAME("items.settings.ingame_settings.speed_increase.name"),
    ITEMS_SETTINGS_INGAME_SETTINGS_SPEED_INCREASE_LORE("items.settings.ingame_settings.speed_increase.lore"),
    ITEMS_SETTINGS_INGAME_SETTINGS_DIALOG_SPEED_NOTE("items.settings.ingame_settings.dialog_speed.note"),

    // Main settings items
    ITEMS_SETTINGS_MAIN_TITLE_NAME("items.settings.main.title.name"),
    ITEMS_SETTINGS_MAIN_TITLE_LORE("items.settings.main.title.lore"),
    ITEMS_SETTINGS_GUI_NAME("items.settings.gui.name"),
    ITEMS_SETTINGS_GUI_LORE("items.settings.gui.lore"),
    ITEMS_SETTINGS_INGAME_NAME("items.settings.ingame.name"),
    ITEMS_SETTINGS_INGAME_LORE("items.settings.ingame.lore"),
    ITEMS_SETTINGS_SOCIAL_NAME("items.settings.social.name"),
    ITEMS_SETTINGS_SOCIAL_LORE("items.settings.social.lore"),
    ITEMS_SETTINGS_SYSTEM_NAME("items.settings.system.name"),
    ITEMS_SETTINGS_SYSTEM_LORE("items.settings.system.lore"),
    ITEMS_SETTINGS_NOTIFICATION_NAME("items.settings.notification.name"),
    ITEMS_SETTINGS_NOTIFICATION_LORE("items.settings.notification.lore"),
    ITEMS_SETTINGS_CLICK("items.settings.click"),

    // Social Settings items
    ITEMS_SETTINGS_SOCIAL_SETTINGS_TITLE_NAME("items.settings.social_settings.title.name"),
    ITEMS_SETTINGS_SOCIAL_SETTINGS_TITLE_LORE("items.settings.social_settings.title.lore"),
    ITEMS_SETTINGS_SOCIAL_SETTINGS_FRIEND_REQUESTS_NAME("items.settings.social_settings.friend_requests.name"),
    ITEMS_SETTINGS_SOCIAL_SETTINGS_FRIEND_REQUESTS_DESC("items.settings.social_settings.friend_requests.desc"),
    ITEMS_SETTINGS_SOCIAL_SETTINGS_GUILD_INVITES_NAME("items.settings.social_settings.guild_invites.name"),
    ITEMS_SETTINGS_SOCIAL_SETTINGS_GUILD_INVITES_DESC("items.settings.social_settings.guild_invites.desc"),
    ITEMS_SETTINGS_SOCIAL_SETTINGS_GUILD_INVITES_NOTE("items.settings.social_settings.guild_invites.note"),
    ITEMS_SETTINGS_SOCIAL_SETTINGS_WHISPER_NAME("items.settings.social_settings.whisper.name"),
    ITEMS_SETTINGS_SOCIAL_SETTINGS_WHISPER_CLICK_HINT("items.settings.social_settings.whisper.click_hint"),
    ITEMS_SETTINGS_SOCIAL_SETTINGS_WHISPER_MODE_CYCLE("items.settings.social_settings.whisper.mode_cycle"),
    ITEMS_SETTINGS_SOCIAL_SETTINGS_WHISPER_NOTE("items.settings.social_settings.whisper.note"),

    // Main Menu items
    ITEMS_MAINMENU_TITLE_NAME("items.mainmenu.title.name"),
    ITEMS_MAINMENU_TITLE_LORE("items.mainmenu.title.lore"),
    ITEMS_MAINMENU_HUB_BUTTON_NAME("items.mainmenu.hub_button.name"),
    ITEMS_MAINMENU_HUB_BUTTON_LORE("items.mainmenu.hub_button.lore"),
    ITEMS_MAINMENU_LEADERBOARD_BUTTON_NAME("items.mainmenu.leaderboard_button.name"),
    ITEMS_MAINMENU_LEADERBOARD_BUTTON_LORE("items.mainmenu.leaderboard_button.lore"),

    // Leaderboard items
    ITEMS_LEADERBOARD_LOADING_NAME("items.leaderboard.loading.name"),
    ITEMS_LEADERBOARD_LOADING_LORE("items.leaderboard.loading.lore"),
    ITEMS_LEADERBOARD_MY_RANK_NAME("items.leaderboard.my_rank.name"),
    ITEMS_LEADERBOARD_NO_RANK_NAME("items.leaderboard.no_rank.name"),
    ITEMS_LEADERBOARD_NO_RANK_LORE("items.leaderboard.no_rank.lore"),

    // Shop items
    ITEMS_SHOP_GOLD_NAME("items.shop.gold.name"),

    // Social items
    ITEMS_SOCIAL_FRIENDS_ADD_NAME("items.social.friends.add.name"),
    ITEMS_SOCIAL_FRIENDS_ADD_LORE("items.social.friends.add.lore"),
    ITEMS_SOCIAL_FRIENDS_ADD_CLICK("items.social.friends.add.click"),
    ITEMS_SOCIAL_FRIENDS_REQUESTS_NAME("items.social.friends.requests.name"),
    ITEMS_SOCIAL_FRIENDS_REQUESTS_LORE("items.social.friends.requests.lore"),
    ITEMS_SOCIAL_FRIENDS_REQUESTS_CLICK("items.social.friends.requests.click"),
    ITEMS_SOCIAL_FRIENDS_FRIEND_ITEM_RIGHT_CLICK("items.social.friends.friend_item.right_click"),
    ITEMS_SOCIAL_FRIEND_REQUESTS_TITLE_NAME("items.social.friend_requests.title.name"),
    ITEMS_SOCIAL_FRIEND_REQUESTS_TITLE_LORE("items.social.friend_requests.title.lore"),
    ITEMS_SOCIAL_FRIEND_REQUESTS_NO_REQUESTS_NAME("items.social.friend_requests.no_requests.name"),
    ITEMS_SOCIAL_FRIEND_REQUESTS_NO_REQUESTS_LORE("items.social.friend_requests.no_requests.lore"),
    ITEMS_SOCIAL_FRIEND_REQUESTS_REQUEST_INFO_HINT("items.social.friend_requests.request_info.hint"),
    ITEMS_SOCIAL_FRIEND_REQUESTS_ACCEPT_NAME("items.social.friend_requests.accept.name"),
    ITEMS_SOCIAL_FRIEND_REQUESTS_ACCEPT_CLICK("items.social.friend_requests.accept.click"),
    ITEMS_SOCIAL_FRIEND_REQUESTS_REJECT_NAME("items.social.friend_requests.reject.name"),
    ITEMS_SOCIAL_FRIEND_REQUESTS_REJECT_CLICK("items.social.friend_requests.reject.click"),

    // Mail detail items
    ITEMS_SOCIAL_MAIL_DETAIL_MESSAGE_NAME("items.social.mail_detail.message.name"),
    ITEMS_SOCIAL_MAIL_DETAIL_NO_ATTACHMENTS_NAME("items.social.mail_detail.no_attachments.name"),
    ITEMS_SOCIAL_MAIL_DETAIL_NO_ATTACHMENTS_LORE("items.social.mail_detail.no_attachments.lore"),
    ITEMS_SOCIAL_MAIL_DETAIL_REPLY_NAME("items.social.mail_detail.reply.name"),
    ITEMS_SOCIAL_MAIL_DETAIL_REPLY_CLICK("items.social.mail_detail.reply.click"),
    ITEMS_SOCIAL_MAIL_DETAIL_DELETE_NAME("items.social.mail_detail.delete.name"),
    ITEMS_SOCIAL_MAIL_DETAIL_DELETE_LORE("items.social.mail_detail.delete.lore"),
    ITEMS_SOCIAL_MAIL_DETAIL_DELETE_CLICK("items.social.mail_detail.delete.click"),

    // Error messages
    ERROR_FRIEND_REQUEST_ID_MISSING("error.friend_request_id_missing");

    private final String key;
    
    GeneralLangKey(String key) {
        this.key = key;
    }
    
    @Override
    public String key() {
        return key;
    }
    
    @Override
    public String getDefaultValue() {
        return key;
    }

    /**
     * Create a dynamic language key from a string
     * @param key The language key string
     * @return An ILangKey instance
     */
    public static ILangKey fromString(String key) {
        return new DynamicLangKey(key);
    }

    /**
     * Dynamic language key implementation for runtime-generated keys
     */
    private static class DynamicLangKey implements ILangKey {
        private final String key;

        public DynamicLangKey(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public String getDefaultValue() {
            return "";
        }
    }
}