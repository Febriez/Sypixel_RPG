package com.febrie.rpg.util;

/**
 * 모든 언어 키를 중앙에서 관리하는 enum
 * 컴파일 타임에 키 검증 및 IDE 자동완성 지원
 */
public enum LangKey {
    // =================================
    // BIOME 관련 키
    // =================================
    BIOME_PLAINS("biome.plains"),
    BIOME_FOREST("biome.forest"),
    BIOME_DESERT("biome.desert"),
    BIOME_JUNGLE("biome.jungle"),
    BIOME_TAIGA("biome.taiga"),
    BIOME_SNOWY_PLAINS("biome.snowy_plains"),
    BIOME_SAVANNA("biome.savanna"),
    BIOME_SWAMP("biome.swamp"),
    BIOME_MUSHROOM_FIELDS("biome.mushroom_fields"),
    BIOME_BEACH("biome.beach"),
    BIOME_FLOWER_FOREST("biome.flower_forest"),
    BIOME_BAMBOO_JUNGLE("biome.bamboo_jungle"),
    BIOME_DARK_FOREST("biome.dark_forest"),
    BIOME_BIRCH_FOREST("biome.birch_forest"),
    BIOME_BADLANDS("biome.badlands"),
    BIOME_OCEAN("biome.ocean"),
    BIOME_CHERRY_GROVE("biome.cherry_grove"),

    // =================================
    // GUI 관련 키
    // =================================
    GUI_COMMON_BACK("gui.common.back"),
    GUI_COMMON_CLOSE("gui.common.close"),
    GUI_COMMON_NEXT_PAGE("gui.common.next-page"),
    GUI_COMMON_PREVIOUS_PAGE("gui.common.previous-page"),
    GUI_COMMON_PREV_PAGE("gui.common.prev-page"),
    GUI_COMMON_CONFIRM("gui.common.confirm"),
    GUI_COMMON_CANCEL("gui.common.cancel"),
    GUI_COMMON_SETTINGS("gui.common.settings"),
    GUI_COMMON_UNKNOWN("gui.common.unknown"),
    GUI_COMMON_PAGE("gui.common.page"),
    
    GUI_MAINMENU_TITLE("gui.mainmenu.title"),
    
    // Profile GUI
    GUI_PROFILE_TITLE("gui.profile.title"),
    GUI_PROFILE_PLAYER_TITLE("gui.profile.player-title"),
    GUI_PROFILE_VIEWING("gui.profile.viewing"),
    GUI_PROFILE_LEVEL("gui.profile.level"),
    GUI_PROFILE_LEVEL_INFO("gui.profile.level-info"),
    GUI_PROFILE_EXPERIENCE("gui.profile.experience"),
    GUI_PROFILE_EXP("gui.profile.exp"),
    GUI_PROFILE_EXP_PERCENT("gui.profile.exp-percent"),
    GUI_PROFILE_JOB("gui.profile.job"),
    GUI_PROFILE_NO_JOB("gui.profile.no-job"),
    GUI_PROFILE_CURRENT_JOB("gui.profile.current-job"),
    GUI_PROFILE_JOB_LEVEL("gui.profile.job-level"),
    GUI_PROFILE_COMBAT_POWER("gui.profile.combat-power"),
    GUI_PROFILE_CLICK_TO_TALENTS("gui.profile.click-to-talents"),
    GUI_PROFILE_GOLD("gui.profile.gold"),
    GUI_PROFILE_PLAYTIME("gui.profile.playtime"),
    GUI_PROFILE_JOIN_DATE("gui.profile.join-date"),
    GUI_PROFILE_LAST_SEEN("gui.profile.last-seen"),
    GUI_PROFILE_STATS("gui.profile.stats"),
    GUI_PROFILE_TALENTS("gui.profile.talents"),
    GUI_PROFILE_CLICK_FOR_TALENTS("gui.profile.click-for-talents"),
    GUI_PROFILE_CLICK_FOR_STATS("gui.profile.click-for-stats"),
    GUI_PROFILE_ONLINE_STATUS("gui.profile.online-status"),
    GUI_PROFILE_GAMEMODE("gui.profile.gamemode"),
    GUI_PROFILE_LOCATION("gui.profile.location"),
    GUI_PROFILE_HEALTH("gui.profile.health"),
    GUI_PROFILE_HEALTH_INFO("gui.profile.health-info"),
    GUI_PROFILE_FOOD_INFO("gui.profile.food-info"),
    GUI_PROFILE_FOOD_LEVEL("gui.profile.food-level"),
    GUI_PROFILE_SATURATION("gui.profile.saturation"),
    GUI_PROFILE_GAME_INFO("gui.profile.game-info"),
    GUI_PROFILE_WORLD("gui.profile.world"),
    GUI_PROFILE_HUNGER("gui.profile.hunger"),
    GUI_PROFILE_ACTIVE_QUESTS("gui.profile.active-quests"),
    GUI_PROFILE_COMPLETED_QUESTS("gui.profile.completed-quests"),
    GUI_PROFILE_MOB_KILLS("gui.profile.mob-kills"),
    GUI_PROFILE_PLAYER_INFO_NAME("gui.profile.player-info.name"),
    
    // Buttons
    GUI_BUTTONS_CLOSE_NAME("gui.buttons.close.name"),
    GUI_BUTTONS_BACK_NAME("gui.buttons.back.name"),
    GUI_BUTTONS_BACK_LORE("gui.buttons.back.lore"),
    GUI_BUTTONS_REFRESH_NAME("gui.buttons.refresh.name"),
    GUI_BUTTONS_REFRESH_LORE("gui.buttons.refresh.lore"),
    GUI_BUTTONS_NEXT_PAGE_NAME("gui.buttons.next-page.name"),
    GUI_BUTTONS_PREVIOUS_PAGE_NAME("gui.buttons.previous-page.name"),
    GUI_BUTTONS_PAGE_INFO_LORE("gui.buttons.page-info.lore"),
    GUI_BUTTONS_CLOSE_LORE("gui.buttons.close.lore"),
    GUI_BUTTONS_NEXT_PAGE_LORE("gui.buttons.next-page.lore"),
    GUI_BUTTONS_PREVIOUS_PAGE_LORE("gui.buttons.previous-page.lore"),
    
    // Leaderboard GUI
    GUI_LEADERBOARD_TITLE("gui.leaderboard.title"),
    GUI_LEADERBOARD_TAB_SELECTED("gui.leaderboard.tab_selected"),
    GUI_LEADERBOARD_TAB_CLICK("gui.leaderboard.tab_click"),
    GUI_LEADERBOARD_CURRENT_TYPE("gui.leaderboard.current-type"),
    GUI_LEADERBOARD_TOTAL_ENTRIES("gui.leaderboard.total-entries"),
    GUI_LEADERBOARD_TYPE_LEVEL("gui.leaderboard.type.level"),
    GUI_LEADERBOARD_TYPE_COMBAT_POWER("gui.leaderboard.type.combat_power"),
    GUI_LEADERBOARD_TYPE_GOLD("gui.leaderboard.type.gold"),
    GUI_LEADERBOARD_TYPE_PLAYTIME("gui.leaderboard.type.playtime"),
    GUI_LEADERBOARD_MY_RANK("gui.leaderboard.my-rank"),
    GUI_LEADERBOARD_MY_VALUE("gui.leaderboard.my-value"),
    GUI_LEADERBOARD_LAST_UPDATED("gui.leaderboard.last-updated"),
    
    // Island GUI
    GUI_ISLAND_MAIN_TITLE("gui.island.main.title"),
    ISLAND_GUI_MAIN_TITLE_WITH_NAME("island.gui.main.title_with_name"),
    ISLAND_GUI_MAIN_TITLE("island.gui.main.title"),
    GUI_ISLAND_MAIN_COLOR_CHANGE_HINT("gui.island.main.color-change-hint"),
    GUI_ISLAND_MAIN_HEX_FORMAT_ERROR("gui.island.main.hex_format_error"),
    GUI_ISLAND_MAIN_HEX_FORMAT_EXAMPLE("gui.island.main.hex_format_example"),
    GUI_ISLAND_MAIN_COLOR_CHANGED("gui.island.main.color_changed"),
    GUI_ISLAND_MAIN_HEX_INPUT_TITLE("gui.island.main.hex_input_title"),
    GUI_ISLAND_MAIN_WARP_MOVING("gui.island.main.warp_moving"),
    GUI_ISLAND_MAIN_WARP_SUCCESS("gui.island.main.warp_success"),
    GUI_ISLAND_MAIN_INFO_LORE("gui.island.main.info.lore"),
    GUI_ISLAND_MAIN_INFO_MEMBERS("gui.island.main.info.members"),
    GUI_ISLAND_MAIN_CREATE_LORE1("gui.island.main.create.lore1"),
    GUI_ISLAND_MAIN_CREATE_LORE2("gui.island.main.create.lore2"),
    GUI_ISLAND_MAIN_SPAWN_NAME("gui.island.main.spawn.name"),
    GUI_ISLAND_MAIN_SPAWN_LORE("gui.island.main.spawn.lore"),
    GUI_ISLAND_MAIN_VISIT_NAME("gui.island.main.visit.name"),
    GUI_ISLAND_MAIN_MEMBERS_LORE("gui.island.main.members.lore"),
    GUI_ISLAND_MAIN_SETTINGS_LORE("gui.island.main.settings.lore"),
    GUI_ISLAND_MAIN_UPGRADES_LORE("gui.island.main.upgrades.lore"),
    GUI_ISLAND_MAIN_PERMISSIONS_LORE("gui.island.main.permissions.lore"),
    GUI_ISLAND_MAIN_BIOME_LORE("gui.island.main.biome.lore"),
    GUI_ISLAND_MAIN_CONTRIBUTIONS_LORE("gui.island.main.contributions.lore"),
    GUI_ISLAND_MAIN_VISITORS_LORE("gui.island.main.visitors.lore"),
    GUI_ISLAND_MAIN_RESET_LORE("gui.island.main.reset.lore"),
    GUI_ISLAND_MAIN_LEAVE_LORE("gui.island.main.leave.lore"),
    GUI_ISLAND_MAIN_BACK_NAME("gui.island.main.back.name"),
    GUI_ISLAND_MAIN_CLOSE_NAME("gui.island.main.close.name"),
    GUI_ISLAND_SETTINGS_TITLE("gui.island.settings.title"),
    GUI_ISLAND_CREATE_TITLE("gui.island.create.title"),
    
    // Combat Power GUI
    GUI_COMBAT_POWER_TITLE("gui.combat_power.title"),
    GUI_COMBAT_POWER_TOTAL("gui.combat_power.total"),
    GUI_COMBAT_POWER_BREAKDOWN("gui.combat_power.breakdown"),
    GUI_COMBAT_POWER_FROM_LEVEL("gui.combat_power.from_level"),
    GUI_COMBAT_POWER_FROM_STATS("gui.combat_power.from_stats"),
    GUI_COMBAT_POWER_LEVEL_CONTRIBUTION("gui.combat_power.level_contribution"),
    GUI_COMBAT_POWER_LEVEL_DETAIL("gui.combat_power.level_detail"),
    GUI_COMBAT_POWER_STAT_CONTRIBUTION("gui.combat_power.stat_contribution"),
    GUI_COMBAT_POWER_STAT_DETAIL("gui.combat_power.stat_detail"),
    
    // Job Confirmation GUI
    GUI_JOB_CONFIRMATION_TITLE("gui.job_confirmation.title"),
    GUI_JOB_CONFIRMATION_TITLE_SUCCESS("gui.job_confirmation.title_success"),
    GUI_JOB_CONFIRMATION_SUBTITLE_SUCCESS("gui.job_confirmation.subtitle_success"),
    GUI_JOB_CONFIRMATION_SELECTED_JOB("gui.job_confirmation.selected_job"),
    GUI_JOB_CONFIRMATION_MAX_LEVEL("gui.job_confirmation.max_level"),
    GUI_JOB_CONFIRMATION_WARNING("gui.job_confirmation.warning"),
    GUI_JOB_CONFIRMATION_WARNING_TITLE("gui.job_confirmation.warning_title"),
    GUI_JOB_CONFIRMATION_WARNING_DESCRIPTION("gui.job_confirmation.warning_description"),
    GUI_JOB_CONFIRMATION_INFO_TITLE("gui.job_confirmation.info_title"),
    GUI_JOB_CONFIRMATION_INFO_LINE1("gui.job_confirmation.info_line1"),
    GUI_JOB_CONFIRMATION_INFO_LINE2("gui.job_confirmation.info_line2"),
    GUI_JOB_CONFIRMATION_INFO_LINE3("gui.job_confirmation.info_line3"),
    GUI_JOB_CONFIRMATION_CONFIRM("gui.job_confirmation.confirm"),
    GUI_JOB_CONFIRMATION_CONFIRM_DESCRIPTION("gui.job_confirmation.confirm_description"),
    GUI_JOB_CONFIRMATION_CLICK_TO_CONFIRM("gui.job_confirmation.click_to_confirm"),
    GUI_JOB_CONFIRMATION_CANCEL("gui.job_confirmation.cancel"),
    GUI_JOB_CONFIRMATION_CANCEL_DESCRIPTION("gui.job_confirmation.cancel_description"),
    GUI_JOB_CONFIRMATION_CLICK_TO_CANCEL("gui.job_confirmation.click_to_cancel"),
    GUI_JOB_CONFIRMATION_SUCCESS("gui.job_confirmation.success"),
    GUI_JOB_CONFIRMATION_ALREADY_HAS_JOB("gui.job_confirmation.already_has_job"),
    GUI_JOB_CONFIRMATION_CANNOT_CHANGE("gui.job_confirmation.cannot_change"),
    
    // Job Selection GUI
    GUI_JOB_SELECTION_TITLE("gui.job_selection.title"),
    GUI_JOB_SELECTION_TAB_SELECTED("gui.job_selection.tab_selected"),
    GUI_JOB_SELECTION_TAB_CLICK("gui.job_selection.tab_click"),
    GUI_JOB_SELECTION_MAX_LEVEL("gui.job_selection.max_level"),
    GUI_JOB_SELECTION_CLICK_TO_CHOOSE("gui.job_selection.click_to_choose"),
    GUI_JOB_SELECTION_WARNING("gui.job_selection.warning"),
    
    // Stats GUI
    GUI_STATS_TITLE("gui.stats.title"),
    GUI_STATS_POINTS_AVAILABLE("gui.stats.points_available"),
    GUI_STATS_POINTS_COUNT("gui.stats.points_count"),
    GUI_STATS_POINTS_INFO("gui.stats.points_info"),
    GUI_STATS_CURRENT_VALUE("gui.stats.current_value"),
    GUI_STATS_BASE_BONUS("gui.stats.base_bonus"),
    GUI_STATS_CLICK_TO_ADD("gui.stats.click_to_add"),
    
    // Talent GUI
    GUI_TALENT_TITLE("gui.talent.title"),
    GUI_TALENT_NO_JOB("gui.talent.no_job"),
    GUI_TALENT_JOB("gui.talent.job"),
    GUI_TALENT_PAGE_INFO("gui.talent.page_info"),
    GUI_TALENT_CURRENT_PAGE("gui.talent.current_page"),
    GUI_TALENT_AVAILABLE_POINTS("gui.talent.available_points"),
    GUI_TALENT_LEVEL_INFO("gui.talent.level_info"),
    GUI_TALENT_STAT_BONUSES("gui.talent.stat_bonuses"),
    GUI_TALENT_STAT_BONUS_LINE("gui.talent.stat_bonus_line"),
    GUI_TALENT_EFFECTS("gui.talent.effects"),
    GUI_TALENT_PREREQUISITES("gui.talent.prerequisites"),
    GUI_TALENT_PREREQ_MET("gui.talent.prereq_met"),
    GUI_TALENT_PREREQ_NOT_MET("gui.talent.prereq_not_met"),
    GUI_TALENT_CAN_LEARN("gui.talent.can_learn"),
    GUI_TALENT_CANNOT_LEARN("gui.talent.cannot_learn"),
    GUI_TALENT_NOT_ENOUGH_POINTS("gui.talent.not_enough_points"),
    GUI_TALENT_MAXED("gui.talent.maxed"),
    GUI_TALENT_HAS_SUB_PAGE("gui.talent.has_sub_page"),
    
    // System Settings GUI
    GUI_SYSTEM_SETTINGS_TITLE("gui.system_settings.title"),
    GUI_SYSTEM_SETTINGS_STATUS("gui.system_settings.status"),
    GUI_SYSTEM_SETTINGS_CLICK_TO_TOGGLE("gui.system_settings.click_to_toggle"),
    GUI_SYSTEM_SETTINGS_CONFIRMATION_TOGGLED("gui.system_settings.confirmation_toggled"),
    
    // =================================
    // GENERAL 키
    // =================================
    GENERAL_SEPARATOR("general.separator"),
    GENERAL_COMING_SOON("general.coming_soon"),
    GENERAL_CANNOT_VIEW_OTHERS_QUESTS("general.cannot_view_others_quests"),
    GENERAL_CANNOT_VIEW_OTHERS_STATS("general.cannot_view_others_stats"),
    GENERAL_CANNOT_VIEW_OTHERS_TALENTS("general.cannot_view_others_talents"),
    GENERAL_CANNOT_SELECT_OTHERS_JOB("general.cannot_select_others_job"),
    
    // Status
    STATUS_ENABLED("status.enabled"),
    STATUS_DISABLED("status.disabled"),
    ACTION_ENABLE("action.enable"),
    ACTION_DISABLE("action.disable"),
    
    // Messages
    MESSAGES_STAT_INCREASED("messages.stat_increased"),
    MESSAGES_NOT_ENOUGH_STAT_POINTS("messages.not_enough_stat_points"),
    MESSAGES_TALENT_LEARNED("messages.talent_learned"),
    MESSAGES_TALENT_CANNOT_LEARN("messages.talent_cannot_learn"),
    MESSAGES_NOT_ENOUGH_TALENT_POINTS("messages.not_enough_talent_points"),
    
    // =================================
    // STATUS 키
    // =================================
    STATUS_ONLINE("status.online"),
    STATUS_OFFLINE("status.offline"),
    
    // =================================
    // MESSAGES 키
    // =================================
    MESSAGES_NO_JOB_FOR_STATS("messages.no_job_for_stats"),
    
    // =================================
    // ITEMS GUI 관련 키
    // =================================
    ITEMS_GUI_BUTTONS_CLOSE_NAME("items.gui.buttons.close.name"),
    ITEMS_GUI_BUTTONS_CLOSE_LORE("items.gui.buttons.close.lore"),
    ITEMS_GUI_BUTTONS_BACK_NAME("items.gui.buttons.back.name"),
    ITEMS_GUI_BUTTONS_BACK_LORE("items.gui.buttons.back.lore"),
    ITEMS_GUI_BUTTONS_REFRESH_NAME("items.gui.buttons.refresh.name"),
    ITEMS_GUI_BUTTONS_REFRESH_LORE("items.gui.buttons.refresh.lore"),
    ITEMS_GUI_BUTTONS_NEXT_PAGE_NAME("items.gui.buttons.next-page.name"),
    ITEMS_GUI_BUTTONS_NEXT_PAGE_LORE("items.gui.buttons.next-page.lore"),
    ITEMS_GUI_BUTTONS_PREVIOUS_PAGE_NAME("items.gui.buttons.previous-page.name"),
    ITEMS_GUI_BUTTONS_PREVIOUS_PAGE_LORE("items.gui.buttons.previous-page.lore"),
    ITEMS_GUI_BUTTONS_CONFIRM_NAME("items.gui.buttons.confirm.name"),
    ITEMS_GUI_BUTTONS_CONFIRM_LORE("items.gui.buttons.confirm.lore"),
    ITEMS_GUI_BUTTONS_CANCEL_NAME("items.gui.buttons.cancel.name"),
    ITEMS_GUI_BUTTONS_CANCEL_LORE("items.gui.buttons.cancel.lore"),
    ITEMS_GUI_BUTTONS_PAGE_INFO_NAME("items.gui.buttons.page-info.name"),
    ITEMS_GUI_BUTTONS_TOGGLE_ENABLED("items.gui.buttons.toggle.enabled"),
    ITEMS_GUI_BUTTONS_TOGGLE_DISABLED("items.gui.buttons.toggle.disabled"),
    ITEMS_GUI_BUTTONS_STATUS_ONLINE("items.gui.buttons.status.online"),
    ITEMS_GUI_BUTTONS_STATUS_OFFLINE("items.gui.buttons.status.offline"),
    ITEMS_GUI_ERROR_TITLE("items.gui.error.title"),
    
    // Main Menu Items
    ITEMS_MAINMENU_TITLE_NAME("items.mainmenu.title.name"),
    ITEMS_MAINMENU_TITLE_LORE("items.mainmenu.title.lore"),
    ITEMS_MAINMENU_PROFILE_BUTTON_NAME("items.mainmenu.profile-button.name"),
    ITEMS_MAINMENU_PROFILE_BUTTON_LORE("items.mainmenu.profile-button.lore"),
    ITEMS_MAINMENU_LEADERBOARD_BUTTON_NAME("items.mainmenu.leaderboard-button.name"),
    ITEMS_MAINMENU_LEADERBOARD_BUTTON_LORE("items.mainmenu.leaderboard-button.lore"),
    ITEMS_MAINMENU_JOB_BUTTON_NAME("items.mainmenu.job-button.name"),
    ITEMS_MAINMENU_JOB_BUTTON_LORE("items.mainmenu.job-button.lore"),
    ITEMS_MAINMENU_STATS_BUTTON_NAME("items.mainmenu.stats-button.name"),
    ITEMS_MAINMENU_STATS_BUTTON_LORE("items.mainmenu.stats-button.lore"),
    ITEMS_MAINMENU_HUB_BUTTON_NAME("items.mainmenu.hub-button.name"),
    ITEMS_MAINMENU_HUB_BUTTON_LORE("items.mainmenu.hub-button.lore"),
    ITEMS_MAINMENU_SHOP_BUTTON_NAME("items.mainmenu.shop-button.name"),
    ITEMS_MAINMENU_SHOP_BUTTON_LORE("items.mainmenu.shop-button.lore"),
    ITEMS_MAINMENU_DUNGEON_BUTTON_NAME("items.mainmenu.dungeon-button.name"),
    ITEMS_MAINMENU_DUNGEON_BUTTON_LORE("items.mainmenu.dungeon-button.lore"),
    ITEMS_MAINMENU_WILD_BUTTON_NAME("items.mainmenu.wild-button.name"),
    ITEMS_MAINMENU_WILD_BUTTON_LORE("items.mainmenu.wild-button.lore"),
    ITEMS_MAINMENU_ISLAND_BUTTON_NAME("items.mainmenu.island-button.name"),
    ITEMS_MAINMENU_ISLAND_BUTTON_LORE("items.mainmenu.island-button.lore"),
    
    // Settings Items
    ITEMS_SETTINGS_SYSTEM_SETTINGS_TITLE_NAME("items.settings.system_settings.title.name"),
    ITEMS_SETTINGS_SYSTEM_SETTINGS_TITLE_LORE("items.settings.system_settings.title.lore"),
    ITEMS_SETTINGS_SYSTEM_SETTINGS_CONFIRMATION_NAME("items.settings.system_settings.confirmation.name"),
    ITEMS_SETTINGS_SYSTEM_SETTINGS_CONFIRMATION_DESC1("items.settings.system_settings.confirmation.desc1"),
    ITEMS_SETTINGS_SYSTEM_SETTINGS_CONFIRMATION_DESC2("items.settings.system_settings.confirmation.desc2"),
    ITEMS_SETTINGS_SYSTEM_SETTINGS_CONFIRMATION_DESC3("items.settings.system_settings.confirmation.desc3"),
    ITEMS_SETTINGS_SYSTEM_SETTINGS_CONFIRMATION_EXAMPLE_TITLE("items.settings.system_settings.confirmation.example_title"),
    ITEMS_SETTINGS_SYSTEM_SETTINGS_CONFIRMATION_EXAMPLE1("items.settings.system_settings.confirmation.example1"),
    ITEMS_SETTINGS_SYSTEM_SETTINGS_CONFIRMATION_EXAMPLE2("items.settings.system_settings.confirmation.example2"),
    
    // Profile Items
    ITEMS_PROFILE_LEVEL_INFO_NAME("items.profile.level-info.name"),
    ITEMS_PROFILE_GAME_STATS_NAME("items.profile.game-stats.name"),
    ITEMS_PROFILE_QUEST_INFO_NAME("items.profile.quest-info.name"),
    ITEMS_PROFILE_QUEST_INFO_CLICK_LORE("items.profile.quest-info.click-lore"),
    ITEMS_PROFILE_COLLECTION_BOOK_NAME("items.profile.collection-book.name"),
    ITEMS_PROFILE_PETS_NAME("items.profile.pets.name"),
    ITEMS_PROFILE_USER_SETTINGS_NAME("items.profile.user-settings.name"),
    ITEMS_PROFILE_USER_SETTINGS_LORE("items.profile.user-settings.lore"),
    
    // Leaderboard Items
    ITEMS_LEADERBOARD_LOADING_NAME("items.leaderboard.loading.name"),
    ITEMS_LEADERBOARD_LOADING_LORE("items.leaderboard.loading.lore"),
    ITEMS_LEADERBOARD_MY_RANK_NAME("items.leaderboard.my-rank.name"),
    ITEMS_LEADERBOARD_NO_RANK_NAME("items.leaderboard.no-rank.name"),
    ITEMS_LEADERBOARD_NO_RANK_LORE1("items.leaderboard.no-rank.lore1"),
    ITEMS_LEADERBOARD_NO_RANK_LORE2("items.leaderboard.no-rank.lore2"),
    
    // Island Items
    ITEMS_ISLAND_MAIN_INFO_LORE("items.island.main.info.lore"),
    ITEMS_ISLAND_MEMBER_INVITE_LORE("items.island.member.invite.lore"),
    ITEMS_ISLAND_MEMBER_PERMISSION_LORE("items.island.member.permission.lore"),
    ITEMS_ISLAND_MAIN_UPGRADE_INFO_LORE("items.island.main.upgrade-info.lore"),
    ITEMS_ISLAND_MAIN_CONTRIBUTION_INFO_LORE("items.island.main.contribution-info.lore"),
    ITEMS_ISLAND_SPAWN_CURRENT_INFO_LORE("items.island.spawn.current-info.lore"),
    ITEMS_ISLAND_MAIN_ISLAND_SETTINGS_LORE("items.island.main.island-settings.lore"),
    ITEMS_ISLAND_MAIN_VISITOR_LORE("items.island.main.visitor.lore"),
    ITEMS_ISLAND_MAIN_BIOME_CHANGE_LORE("items.island.main.biome-change.lore"),
    ITEMS_ISLAND_MAIN_WARP_LORE("items.island.main.warp.lore"),
    
    // =================================
    // QUEST 관련 키
    // =================================
    
    // Tutorial Quests

    QUEST_TUTORIAL_FIRST_STEPS_NAME("quest.tutorial.first_steps.name"),
    QUEST_TUTORIAL_FIRST_STEPS_DIALOGS("quest.tutorial.first.steps.dialogs"),
    QUEST_TUTORIAL_FIRST_STEPS_NPC_NAME("quest.tutorial.first_steps.npc_name"),
    QUEST_TUTORIAL_FIRST_STEPS_ACCEPT("quest.tutorial.first_steps.accept"),
    QUEST_TUTORIAL_FIRST_STEPS_DECLINE("quest.tutorial.first_steps.decline"),
    
    QUEST_TUTORIAL_BASIC_COMBAT_NAME("quest.tutorial.basic_combat.name"),
    QUEST_TUTORIAL_BASIC_COMBAT_DIALOGS("quest.tutorial.basic.combat.dialogs"),
    QUEST_TUTORIAL_BASIC_COMBAT_NPC_NAME("quest.tutorial.basic_combat.npc_name"),
    QUEST_TUTORIAL_BASIC_COMBAT_ACCEPT("quest.tutorial.basic_combat.accept"),
    QUEST_TUTORIAL_BASIC_COMBAT_DECLINE("quest.tutorial.basic_combat.decline"),
    
    // Daily Quests
    QUEST_DAILY_MINING_NAME("quest.daily.mining.name"),
    QUEST_DAILY_MINING_DIALOGS("quest.daily.mining.dialogs"),
    QUEST_DAILY_MINING_NPC_NAME("quest.daily.mining.npc_name"),
    QUEST_DAILY_MINING_ACCEPT("quest.daily.mining.accept"),
    QUEST_DAILY_MINING_DECLINE("quest.daily.mining.decline"),
    
    QUEST_DAILY_HUNTING_NAME("quest.daily.hunting.name"),
    QUEST_DAILY_HUNTING_OBJECTIVES_KILL_ZOMBIES("quest.daily.hunting.objectives.kill_zombies"),
    QUEST_DAILY_HUNTING_OBJECTIVES_KILL_SKELETONS("quest.daily.hunting.objectives.kill_skeletons"),
    QUEST_DAILY_HUNTING_OBJECTIVES_KILL_CREEPERS("quest.daily.hunting.objectives.kill_creepers"),
    QUEST_DAILY_HUNTING_DIALOGS("quest.daily.hunting.dialogs"),
    QUEST_DAILY_HUNTING_NPC_NAME("quest.daily.hunting.npc_name"),
    QUEST_DAILY_HUNTING_ACCEPT("quest.daily.hunting.accept"),
    QUEST_DAILY_HUNTING_DECLINE("quest.daily.hunting.decline"),
    
    QUEST_DAILY_GATHERING_NAME("quest.daily.gathering.name"),
    QUEST_DAILY_GATHERING_DIALOGS("quest.daily.gathering.dialogs"),
    QUEST_DAILY_GATHERING_NPC_NAME("quest.daily.gathering.npc_name"),
    QUEST_DAILY_GATHERING_ACCEPT("quest.daily.gathering.accept"),
    QUEST_DAILY_GATHERING_DECLINE("quest.daily.gathering.decline"),
    
    QUEST_DAILY_FISHING_NAME("quest.daily.fishing.name"),
    QUEST_DAILY_FISHING_DIALOGS("quest.daily.fishing.dialogs"),
    QUEST_DAILY_FISHING_NPC_NAME("quest.daily.fishing.npc_name"),
    QUEST_DAILY_FISHING_ACCEPT("quest.daily.fishing.accept"),
    QUEST_DAILY_FISHING_DECLINE("quest.daily.fishing.decline"),
    
    QUEST_DAILY_EXPLORATION_NAME("quest.daily.exploration.name"),
    QUEST_DAILY_EXPLORATION_DIALOGS("quest.daily.exploration.dialogs"),
    QUEST_DAILY_EXPLORATION_NPC_NAME("quest.daily.exploration.npc_name"),
    QUEST_DAILY_EXPLORATION_ACCEPT("quest.daily.exploration.accept"),
    QUEST_DAILY_EXPLORATION_DECLINE("quest.daily.exploration.decline"),
    
    QUEST_DAILY_DELIVERY_NAME("quest.daily.delivery.name"),
    QUEST_DAILY_DELIVERY_DIALOGS("quest.daily.delivery.dialogs"),
    QUEST_DAILY_DELIVERY_NPC_NAME("quest.daily.delivery.npc_name"),
    QUEST_DAILY_DELIVERY_ACCEPT("quest.daily.delivery.accept"),
    QUEST_DAILY_DELIVERY_DECLINE("quest.daily.delivery.decline"),
    
    QUEST_DAILY_CRAFTING_NAME("quest.daily.crafting.name"),
    QUEST_DAILY_CRAFTING_DIALOGS("quest.daily.crafting.dialogs"),
    QUEST_DAILY_CRAFTING_NPC_NAME("quest.daily.crafting.npc_name"),
    QUEST_DAILY_CRAFTING_ACCEPT("quest.daily.crafting.accept"),
    QUEST_DAILY_CRAFTING_DECLINE("quest.daily.crafting.decline"),
    
    QUEST_DAILY_BOUNTY_HUNTER_NAME("quest.daily.bounty_hunter.name"),
    QUEST_DAILY_BOUNTY_HUNTER_DIALOGS("quest.daily.bounty.hunter.dialogs"),
    QUEST_DAILY_BOUNTY_HUNTER_NPC_NAME("quest.daily.bounty_hunter.npc_name"),
    QUEST_DAILY_BOUNTY_HUNTER_ACCEPT("quest.daily.bounty_hunter.accept"),
    QUEST_DAILY_BOUNTY_HUNTER_DECLINE("quest.daily.bounty_hunter.decline"),
    
    // Side Quests
    QUEST_SIDE_THIEVES_GUILD_NAME("quest.side.thieves_guild.name"),
    QUEST_SIDE_THIEVES_GUILD_INFO("quest.side.thieves_guild.info"),
    QUEST_SIDE_THIEVES_GUILD_DIALOGS("quest.side.thieves.guild.dialogs"),
    QUEST_SIDE_THIEVES_GUILD_NPC_NAME("quest.side.thieves_guild.npc_name"),
    QUEST_SIDE_THIEVES_GUILD_ACCEPT("quest.side.thieves_guild.accept"),
    QUEST_SIDE_THIEVES_GUILD_DECLINE("quest.side.thieves_guild.decline"),
    
    QUEST_SIDE_VOLCANIC_DEPTHS_NAME("quest.side.volcanic_depths.name"),
    QUEST_SIDE_VOLCANIC_DEPTHS_INFO("quest.side.volcanic_depths.info"),
    QUEST_SIDE_VOLCANIC_DEPTHS_DIALOGS("quest.side.volcanic.depths.dialogs"),
    QUEST_SIDE_VOLCANIC_DEPTHS_NPC_NAME("quest.side.volcanic_depths.npc_name"),
    QUEST_SIDE_VOLCANIC_DEPTHS_ACCEPT("quest.side.volcanic_depths.accept"),
    QUEST_SIDE_VOLCANIC_DEPTHS_DECLINE("quest.side.volcanic_depths.decline"),
    
    QUEST_SIDE_ANCIENT_RUINS_NAME("quest.side.ancient_ruins.name"),
    QUEST_SIDE_ANCIENT_RUINS_INFO("quest.side.ancient_ruins.info"),
    QUEST_SIDE_ANCIENT_RUINS_DIALOGS("quest.side.ancient.ruins.dialogs"),
    QUEST_SIDE_ANCIENT_RUINS_NPC_NAME("quest.side.ancient_ruins.npc_name"),
    QUEST_SIDE_ANCIENT_RUINS_ACCEPT("quest.side.ancient_ruins.accept"),
    QUEST_SIDE_ANCIENT_RUINS_DECLINE("quest.side.ancient_ruins.decline"),
    
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_NAME("quest.side.alchemist_experiment.name"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_INFO("quest.side.alchemist_experiment.info"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_DIALOGS("quest.side.alchemist.experiment.dialogs"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_NPC_NAME("quest.side.alchemist_experiment.npc_name"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_ACCEPT("quest.side.alchemist_experiment.accept"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_DECLINE("quest.side.alchemist_experiment.decline"),
    
    // Additional Side Quests that exist in the codebase but are missing from LangManager
    QUEST_SIDE_ROYAL_MESSENGER_NAME("quest.side.royal_messenger.name"),
    QUEST_SIDE_ROYAL_MESSENGER_INFO("quest.side.royal_messenger.info"),
    QUEST_SIDE_ROYAL_MESSENGER_DIALOGS("quest.side.royal.messenger.dialogs"),
    QUEST_SIDE_ROYAL_MESSENGER_NPC_NAME("quest.side.royal_messenger.npc_name"),
    QUEST_SIDE_ROYAL_MESSENGER_ACCEPT("quest.side.royal_messenger.accept"),
    QUEST_SIDE_ROYAL_MESSENGER_DECLINE("quest.side.royal_messenger.decline"),
    
    QUEST_SIDE_SUNKEN_CITY_NAME("quest.side.sunken_city.name"),
    QUEST_SIDE_SUNKEN_CITY_INFO("quest.side.sunken_city.info"),
    QUEST_SIDE_SUNKEN_CITY_DIALOGS("quest.side.sunken.city.dialogs"),
    QUEST_SIDE_SUNKEN_CITY_NPC_NAME("quest.side.sunken_city.npc_name"),
    QUEST_SIDE_SUNKEN_CITY_ACCEPT("quest.side.sunken_city.accept"),
    QUEST_SIDE_SUNKEN_CITY_DECLINE("quest.side.sunken_city.decline"),
    
    QUEST_SIDE_BLACKSMITH_APPRENTICE_NAME("quest.side.blacksmith_apprentice.name"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_INFO("quest.side.blacksmith_apprentice.info"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_DIALOGS("quest.side.blacksmith.apprentice.dialogs"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_NPC_NAME("quest.side.blacksmith_apprentice.npc_name"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_ACCEPT("quest.side.blacksmith_apprentice.accept"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_DECLINE("quest.side.blacksmith_apprentice.decline"),
    
    QUEST_SIDE_COLLECT_HERBS_NAME("quest.side.collect_herbs.name"),
    QUEST_SIDE_COLLECT_HERBS_INFO("quest.side.collect_herbs.info"),
    QUEST_SIDE_COLLECT_HERBS_DIALOGS("quest.side.collect.herbs.dialogs"),
    QUEST_SIDE_COLLECT_HERBS_NPC_NAME("quest.side.collect_herbs.npc_name"),
    QUEST_SIDE_COLLECT_HERBS_ACCEPT("quest.side.collect_herbs.accept"),
    QUEST_SIDE_COLLECT_HERBS_DECLINE("quest.side.collect_herbs.decline"),
    
    QUEST_SIDE_CRYSTAL_CAVERN_NAME("quest.side.crystal_cavern.name"),
    QUEST_SIDE_CRYSTAL_CAVERN_INFO("quest.side.crystal_cavern.info"),
    QUEST_SIDE_CRYSTAL_CAVERN_DIALOGS("quest.side.crystal.cavern.dialogs"),
    QUEST_SIDE_CRYSTAL_CAVERN_NPC_NAME("quest.side.crystal_cavern.npc_name"),
    QUEST_SIDE_CRYSTAL_CAVERN_ACCEPT("quest.side.crystal_cavern.accept"),
    QUEST_SIDE_CRYSTAL_CAVERN_DECLINE("quest.side.crystal_cavern.decline"),
    
    QUEST_SIDE_DESERT_OASIS_NAME("quest.side.desert_oasis.name"),
    QUEST_SIDE_DESERT_OASIS_INFO("quest.side.desert_oasis.info"),
    QUEST_SIDE_DESERT_OASIS_DIALOGS("quest.side.desert.oasis.dialogs"),
    QUEST_SIDE_DESERT_OASIS_NPC_NAME("quest.side.desert_oasis.npc_name"),
    QUEST_SIDE_DESERT_OASIS_ACCEPT("quest.side.desert_oasis.accept"),
    QUEST_SIDE_DESERT_OASIS_DECLINE("quest.side.desert_oasis.decline"),
    
    QUEST_SIDE_ENCHANTED_FOREST_NAME("quest.side.enchanted_forest.name"),
    QUEST_SIDE_ENCHANTED_FOREST_INFO("quest.side.enchanted_forest.info"),
    QUEST_SIDE_ENCHANTED_FOREST_DIALOGS("quest.side.enchanted.forest.dialogs"),
    QUEST_SIDE_ENCHANTED_FOREST_NPC_NAME("quest.side.enchanted_forest.npc_name"),
    QUEST_SIDE_ENCHANTED_FOREST_ACCEPT("quest.side.enchanted_forest.accept"),
    QUEST_SIDE_ENCHANTED_FOREST_DECLINE("quest.side.enchanted_forest.decline"),
    
    QUEST_SIDE_FARMERS_REQUEST_NAME("quest.side.farmers_request.name"),
    QUEST_SIDE_FARMERS_REQUEST_INFO("quest.side.farmers_request.info"),
    QUEST_SIDE_FARMERS_REQUEST_DIALOGS("quest.side.farmers.request.dialogs"),
    QUEST_SIDE_FARMERS_REQUEST_NPC_NAME("quest.side.farmers_request.npc_name"),
    QUEST_SIDE_FARMERS_REQUEST_ACCEPT("quest.side.farmers_request.accept"),
    QUEST_SIDE_FARMERS_REQUEST_DECLINE("quest.side.farmers_request.decline"),
    
    QUEST_SIDE_FISHERMAN_TALE_NAME("quest.side.fisherman_tale.name"),
    QUEST_SIDE_FISHERMAN_TALE_INFO("quest.side.fisherman_tale.info"),
    QUEST_SIDE_FISHERMAN_TALE_DIALOGS("quest.side.fisherman.tale.dialogs"),
    QUEST_SIDE_FISHERMAN_TALE_NPC_NAME("quest.side.fisherman_tale.npc_name"),
    QUEST_SIDE_FISHERMAN_TALE_ACCEPT("quest.side.fisherman_tale.accept"),
    QUEST_SIDE_FISHERMAN_TALE_DECLINE("quest.side.fisherman_tale.decline"),
    
    QUEST_SIDE_FORGOTTEN_TEMPLE_NAME("quest.side.forgotten_temple.name"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_INFO("quest.side.forgotten_temple.info"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_DIALOGS("quest.side.forgotten.temple.dialogs"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_NPC_NAME("quest.side.forgotten_temple.npc_name"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_ACCEPT("quest.side.forgotten_temple.accept"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_DECLINE("quest.side.forgotten_temple.decline"),
    
    QUEST_SIDE_FROZEN_PEAKS_NAME("quest.side.frozen_peaks.name"),
    QUEST_SIDE_FROZEN_PEAKS_INFO("quest.side.frozen_peaks.info"),
    QUEST_SIDE_FROZEN_PEAKS_DIALOGS("quest.side.frozen.peaks.dialogs"),
    QUEST_SIDE_FROZEN_PEAKS_NPC_NAME("quest.side.frozen_peaks.npc_name"),
    QUEST_SIDE_FROZEN_PEAKS_ACCEPT("quest.side.frozen_peaks.accept"),
    QUEST_SIDE_FROZEN_PEAKS_DECLINE("quest.side.frozen_peaks.decline"),
    
    QUEST_SIDE_HEALERS_REQUEST_NAME("quest.side.healers_request.name"),
    QUEST_SIDE_HEALERS_REQUEST_INFO("quest.side.healers_request.info"),
    QUEST_SIDE_HEALERS_REQUEST_DIALOGS("quest.side.healers.request.dialogs"),
    QUEST_SIDE_HEALERS_REQUEST_NPC_NAME("quest.side.healers_request.npc_name"),
    QUEST_SIDE_HEALERS_REQUEST_ACCEPT("quest.side.healers_request.accept"),
    QUEST_SIDE_HEALERS_REQUEST_DECLINE("quest.side.healers_request.decline"),
    
    QUEST_SIDE_HIDDEN_VALLEY_NAME("quest.side.hidden_valley.name"),
    QUEST_SIDE_HIDDEN_VALLEY_INFO("quest.side.hidden_valley.info"),
    QUEST_SIDE_HIDDEN_VALLEY_DIALOGS("quest.side.hidden.valley.dialogs"),
    QUEST_SIDE_HIDDEN_VALLEY_NPC_NAME("quest.side.hidden_valley.npc_name"),
    QUEST_SIDE_HIDDEN_VALLEY_ACCEPT("quest.side.hidden_valley.accept"),
    QUEST_SIDE_HIDDEN_VALLEY_DECLINE("quest.side.hidden_valley.decline"),
    
    QUEST_SIDE_INNKEEPER_TROUBLE_NAME("quest.side.innkeeper_trouble.name"),
    QUEST_SIDE_INNKEEPER_TROUBLE_INFO("quest.side.innkeeper_trouble.info"),
    QUEST_SIDE_INNKEEPER_TROUBLE_DIALOGS("quest.side.innkeeper.trouble.dialogs"),
    QUEST_SIDE_INNKEEPER_TROUBLE_NPC_NAME("quest.side.innkeeper_trouble.npc_name"),
    QUEST_SIDE_INNKEEPER_TROUBLE_ACCEPT("quest.side.innkeeper_trouble.accept"),
    QUEST_SIDE_INNKEEPER_TROUBLE_DECLINE("quest.side.innkeeper_trouble.decline"),
    
    QUEST_SIDE_LIBRARIAN_MYSTERY_NAME("quest.side.librarian_mystery.name"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_INFO("quest.side.librarian_mystery.info"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_DIALOGS("quest.side.librarian.mystery.dialogs"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_NPC_NAME("quest.side.librarian_mystery.npc_name"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_ACCEPT("quest.side.librarian_mystery.accept"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_DECLINE("quest.side.librarian_mystery.decline"),
    
    QUEST_SIDE_LOST_TREASURE_NAME("quest.side.lost_treasure.name"),
    QUEST_SIDE_LOST_TREASURE_INFO("quest.side.lost_treasure.info"),
    QUEST_SIDE_LOST_TREASURE_DIALOGS("quest.side.lost.treasure.dialogs"),
    QUEST_SIDE_LOST_TREASURE_NPC_NAME("quest.side.lost_treasure.npc_name"),
    QUEST_SIDE_LOST_TREASURE_ACCEPT("quest.side.lost_treasure.accept"),
    QUEST_SIDE_LOST_TREASURE_DECLINE("quest.side.lost_treasure.decline"),
    
    QUEST_SIDE_MERCHANTS_DILEMMA_NAME("quest.side.merchants_dilemma.name"),
    QUEST_SIDE_MERCHANTS_DILEMMA_INFO("quest.side.merchants_dilemma.info"),
    QUEST_SIDE_MERCHANTS_DILEMMA_DIALOGS("quest.side.merchants.dilemma.dialogs"),
    QUEST_SIDE_MERCHANTS_DILEMMA_NPC_NAME("quest.side.merchants_dilemma.npc_name"),
    QUEST_SIDE_MERCHANTS_DILEMMA_ACCEPT("quest.side.merchants_dilemma.accept"),
    QUEST_SIDE_MERCHANTS_DILEMMA_DECLINE("quest.side.merchants_dilemma.decline"),
    
    QUEST_SIDE_MINERS_PLIGHT_NAME("quest.side.miners_plight.name"),
    QUEST_SIDE_MINERS_PLIGHT_INFO("quest.side.miners_plight.info"),
    QUEST_SIDE_MINERS_PLIGHT_DIALOGS("quest.side.miners.plight.dialogs"),
    QUEST_SIDE_MINERS_PLIGHT_NPC_NAME("quest.side.miners_plight.npc_name"),
    QUEST_SIDE_MINERS_PLIGHT_ACCEPT("quest.side.miners_plight.accept"),
    QUEST_SIDE_MINERS_PLIGHT_DECLINE("quest.side.miners_plight.decline"),
    
    QUEST_SIDE_MYSTERIOUS_CAVE_NAME("quest.side.mysterious_cave.name"),
    QUEST_SIDE_MYSTERIOUS_CAVE_INFO("quest.side.mysterious_cave.info"),
    QUEST_SIDE_MYSTERIOUS_CAVE_DIALOGS("quest.side.mysterious.cave.dialogs"),
    QUEST_SIDE_MYSTERIOUS_CAVE_NPC_NAME("quest.side.mysterious_cave.npc_name"),
    QUEST_SIDE_MYSTERIOUS_CAVE_ACCEPT("quest.side.mysterious_cave.accept"),
    QUEST_SIDE_MYSTERIOUS_CAVE_DECLINE("quest.side.mysterious_cave.decline"),
    
    // Branch Quest Keys
    QUEST_BRANCH_LIGHT_PALADIN_NAME("quest.branch.light_paladin.name"),
    QUEST_BRANCH_LIGHT_PALADIN_INFO("quest.branch.light_paladin.info"),
    QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_PALADIN_MASTER("quest.branch.light_paladin.objectives.paladin_master"),
    QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_HOLY_WATER("quest.branch.light_paladin.objectives.holy_water"),
    QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_PURGE_UNDEAD("quest.branch.light_paladin.objectives.purge_undead"),
    QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_PURGE_SKELETONS("quest.branch.light_paladin.objectives.purge_skeletons"),
    QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_HOLY_SHRINE("quest.branch.light_paladin.objectives.holy_shrine"),
    QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_MEDITATION("quest.branch.light_paladin.objectives.meditation"),
    QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_HOLY_SWORD("quest.branch.light_paladin.objectives.holy_sword"),
    QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_BUILD_ALTAR("quest.branch.light_paladin.objectives.build_altar"),
    QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_DEFEAT_DARKNESS("quest.branch.light_paladin.objectives.defeat_darkness"),
    QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_LIGHT_ESSENCE("quest.branch.light_paladin.objectives.light_essence"),
    QUEST_BRANCH_LIGHT_PALADIN_OBJECTIVES_OATH_COMPLETION("quest.branch.light_paladin.objectives.oath_completion"),
    QUEST_BRANCH_LIGHT_PALADIN_DIALOGS("quest.branch.light.paladin.dialogs"),
    QUEST_BRANCH_LIGHT_PALADIN_NPC_NAME("quest.branch.light_paladin.npc_name"),
    QUEST_BRANCH_LIGHT_PALADIN_ACCEPT("quest.branch.light_paladin.accept"),
    QUEST_BRANCH_LIGHT_PALADIN_DECLINE("quest.branch.light_paladin.decline"),
    
    // Class Quest Keys
    QUEST_CLAZZ_WARRIOR_ADVANCEMENT_NAME("quest.clazz.warrior_advancement.name"),
    QUEST_CLAZZ_WARRIOR_ADVANCEMENT_INFO("quest.clazz.warrior_advancement.info"),
    QUEST_CLAZZ_WARRIOR_ADVANCEMENT_DIALOGS("quest.clazz.warrior.advancement.dialogs"),
    QUEST_CLAZZ_WARRIOR_ADVANCEMENT_NPC_NAME("quest.clazz.warrior_advancement.npc_name"),
    QUEST_CLAZZ_WARRIOR_ADVANCEMENT_ACCEPT("quest.clazz.warrior_advancement.accept"),
    QUEST_CLAZZ_WARRIOR_ADVANCEMENT_DECLINE("quest.clazz.warrior_advancement.decline"),
    
    // Crafting Quest Keys
    QUEST_CRAFTING_MASTER_BLACKSMITH_NAME("quest.crafting.master_blacksmith.name"),
    QUEST_CRAFTING_MASTER_BLACKSMITH_INFO("quest.crafting.master_blacksmith.info"),
    QUEST_CRAFTING_MASTER_BLACKSMITH_DIALOGS("quest.crafting.master.blacksmith.dialogs"),
    QUEST_CRAFTING_MASTER_BLACKSMITH_NPC_NAME("quest.crafting.master_blacksmith.npc_name"),
    QUEST_CRAFTING_MASTER_BLACKSMITH_ACCEPT("quest.crafting.master_blacksmith.accept"),
    QUEST_CRAFTING_MASTER_BLACKSMITH_DECLINE("quest.crafting.master_blacksmith.decline"),
    
    // Event Quest Keys
    QUEST_EVENT_HALLOWEEN_NIGHT_NAME("quest.event.halloween_night.name"),
    QUEST_EVENT_HALLOWEEN_NIGHT_INFO("quest.event.halloween_night.info"),
    QUEST_EVENT_HALLOWEEN_NIGHT_DIALOGS("quest.event.halloween.night.dialogs"),
    QUEST_EVENT_HALLOWEEN_NIGHT_NPC_NAME("quest.event.halloween_night.npc_name"),
    QUEST_EVENT_HALLOWEEN_NIGHT_ACCEPT("quest.event.halloween_night.accept"),
    QUEST_EVENT_HALLOWEEN_NIGHT_DECLINE("quest.event.halloween_night.decline"),
    
    // Main Quest Keys
    QUEST_MAIN_ANCIENT_PROPHECY_NAME("quest.main.ancient_prophecy.name"),
    QUEST_MAIN_ANCIENT_PROPHECY_INFO("quest.main.ancient_prophecy.info"),
    QUEST_MAIN_ANCIENT_PROPHECY_DIALOGS("quest.main.ancient.prophecy.dialogs"),
    QUEST_MAIN_ANCIENT_PROPHECY_NPC_NAME("quest.main.ancient_prophecy.npc_name"),
    QUEST_MAIN_ANCIENT_PROPHECY_ACCEPT("quest.main.ancient_prophecy.accept"),
    QUEST_MAIN_ANCIENT_PROPHECY_DECLINE("quest.main.ancient_prophecy.decline"),
    
    // Chapter 1 Main Quests
    QUEST_MAIN_CHOSEN_ONE_NAME("quest.main.chosen_one.name"),
    QUEST_MAIN_CHOSEN_ONE_INFO("quest.main.chosen_one.info"),
    QUEST_MAIN_CHOSEN_ONE_DIALOGS("quest.main.chosen.one.dialogs"),
    QUEST_MAIN_CHOSEN_ONE_NPC_NAME("quest.main.chosen_one.npc_name"),
    QUEST_MAIN_CHOSEN_ONE_ACCEPT("quest.main.chosen_one.accept"),
    QUEST_MAIN_CHOSEN_ONE_DECLINE("quest.main.chosen_one.decline"),
    
    QUEST_MAIN_ELEMENTAL_STONES_NAME("quest.main.elemental_stones.name"),
    QUEST_MAIN_ELEMENTAL_STONES_INFO("quest.main.elemental_stones.info"),
    QUEST_MAIN_ELEMENTAL_STONES_DIALOGS("quest.main.elemental.stones.dialogs"),
    QUEST_MAIN_ELEMENTAL_STONES_NPC_NAME("quest.main.elemental_stones.npc_name"),
    QUEST_MAIN_ELEMENTAL_STONES_ACCEPT("quest.main.elemental_stones.accept"),
    QUEST_MAIN_ELEMENTAL_STONES_DECLINE("quest.main.elemental_stones.decline"),
    
    QUEST_MAIN_FIRST_TRIAL_NAME("quest.main.first_trial.name"),
    QUEST_MAIN_FIRST_TRIAL_INFO("quest.main.first_trial.info"),
    QUEST_MAIN_FIRST_TRIAL_DIALOGS("quest.main.first.trial.dialogs"),
    QUEST_MAIN_FIRST_TRIAL_NPC_NAME("quest.main.first_trial.npc_name"),
    QUEST_MAIN_FIRST_TRIAL_ACCEPT("quest.main.first_trial.accept"),
    QUEST_MAIN_FIRST_TRIAL_DECLINE("quest.main.first_trial.decline"),
    
    // Chapter 2 Main Quests
    QUEST_MAIN_LOST_KINGDOM_NAME("quest.main.lost_kingdom.name"),
    QUEST_MAIN_LOST_KINGDOM_INFO("quest.main.lost_kingdom.info"),
    QUEST_MAIN_LOST_KINGDOM_DIALOGS("quest.main.lost.kingdom.dialogs"),
    QUEST_MAIN_LOST_KINGDOM_NPC_NAME("quest.main.lost_kingdom.npc_name"),
    QUEST_MAIN_LOST_KINGDOM_ACCEPT("quest.main.lost_kingdom.accept"),
    QUEST_MAIN_LOST_KINGDOM_DECLINE("quest.main.lost_kingdom.decline"),
    
    QUEST_MAIN_SHADOW_INVASION_NAME("quest.main.shadow_invasion.name"),
    QUEST_MAIN_SHADOW_INVASION_INFO("quest.main.shadow_invasion.info"),
    QUEST_MAIN_SHADOW_INVASION_DIALOGS("quest.main.shadow.invasion.dialogs"),
    QUEST_MAIN_SHADOW_INVASION_NPC_NAME("quest.main.shadow_invasion.npc_name"),
    QUEST_MAIN_SHADOW_INVASION_ACCEPT("quest.main.shadow_invasion.accept"),
    QUEST_MAIN_SHADOW_INVASION_DECLINE("quest.main.shadow_invasion.decline"),
    
    QUEST_MAIN_CORRUPTED_LANDS_NAME("quest.main.corrupted_lands.name"),
    QUEST_MAIN_CORRUPTED_LANDS_INFO("quest.main.corrupted_lands.info"),
    QUEST_MAIN_CORRUPTED_LANDS_DIALOGS("quest.main.corrupted.lands.dialogs"),
    QUEST_MAIN_CORRUPTED_LANDS_NPC_NAME("quest.main.corrupted_lands.npc_name"),
    QUEST_MAIN_CORRUPTED_LANDS_ACCEPT("quest.main.corrupted_lands.accept"),
    QUEST_MAIN_CORRUPTED_LANDS_DECLINE("quest.main.corrupted_lands.decline"),
    
    QUEST_MAIN_ANCIENT_EVIL_NAME("quest.main.ancient_evil.name"),
    QUEST_MAIN_ANCIENT_EVIL_INFO("quest.main.ancient_evil.info"),
    QUEST_MAIN_ANCIENT_EVIL_DIALOGS("quest.main.ancient.evil.dialogs"),
    QUEST_MAIN_ANCIENT_EVIL_NPC_NAME("quest.main.ancient_evil.npc_name"),
    QUEST_MAIN_ANCIENT_EVIL_ACCEPT("quest.main.ancient_evil.accept"),
    QUEST_MAIN_ANCIENT_EVIL_DECLINE("quest.main.ancient_evil.decline"),
    
    QUEST_MAIN_HEROES_ALLIANCE_NAME("quest.main.heroes_alliance.name"),
    QUEST_MAIN_HEROES_ALLIANCE_INFO("quest.main.heroes_alliance.info"),
    QUEST_MAIN_HEROES_ALLIANCE_DIALOGS("quest.main.heroes.alliance.dialogs"),
    QUEST_MAIN_HEROES_ALLIANCE_NPC_NAME("quest.main.heroes_alliance.npc_name"),
    QUEST_MAIN_HEROES_ALLIANCE_ACCEPT("quest.main.heroes_alliance.accept"),
    QUEST_MAIN_HEROES_ALLIANCE_DECLINE("quest.main.heroes_alliance.decline"),
    
    // Chapter 3 Main Quests
    QUEST_MAIN_DRAGON_AWAKENING_NAME("quest.main.dragon_awakening.name"),
    QUEST_MAIN_DRAGON_AWAKENING_INFO("quest.main.dragon_awakening.info"),
    QUEST_MAIN_DRAGON_AWAKENING_DIALOGS("quest.main.dragon.awakening.dialogs"),
    QUEST_MAIN_DRAGON_AWAKENING_NPC_NAME("quest.main.dragon_awakening.npc_name"),
    QUEST_MAIN_DRAGON_AWAKENING_ACCEPT("quest.main.dragon_awakening.accept"),
    QUEST_MAIN_DRAGON_AWAKENING_DECLINE("quest.main.dragon_awakening.decline"),
    
    QUEST_MAIN_DRAGON_TRIALS_NAME("quest.main.dragon_trials.name"),
    QUEST_MAIN_DRAGON_TRIALS_INFO("quest.main.dragon_trials.info"),
    QUEST_MAIN_DRAGON_TRIALS_DIALOGS("quest.main.dragon.trials.dialogs"),
    QUEST_MAIN_DRAGON_TRIALS_NPC_NAME("quest.main.dragon_trials.npc_name"),
    QUEST_MAIN_DRAGON_TRIALS_ACCEPT("quest.main.dragon_trials.accept"),
    QUEST_MAIN_DRAGON_TRIALS_DECLINE("quest.main.dragon_trials.decline"),
    
    QUEST_MAIN_DRAGON_HEART_NAME("quest.main.dragon_heart.name"),
    QUEST_MAIN_DRAGON_HEART_INFO("quest.main.dragon_heart.info"),
    QUEST_MAIN_DRAGON_HEART_DIALOGS("quest.main.dragon.heart.dialogs"),
    QUEST_MAIN_DRAGON_HEART_NPC_NAME("quest.main.dragon_heart.npc_name"),
    QUEST_MAIN_DRAGON_HEART_ACCEPT("quest.main.dragon_heart.accept"),
    QUEST_MAIN_DRAGON_HEART_DECLINE("quest.main.dragon_heart.decline"),
    
    QUEST_MAIN_DRAGON_PACT_NAME("quest.main.dragon_pact.name"),
    QUEST_MAIN_DRAGON_PACT_INFO("quest.main.dragon_pact.info"),
    QUEST_MAIN_DRAGON_PACT_DIALOGS("quest.main.dragon.pact.dialogs"),
    QUEST_MAIN_DRAGON_PACT_NPC_NAME("quest.main.dragon_pact.npc_name"),
    QUEST_MAIN_DRAGON_PACT_ACCEPT("quest.main.dragon_pact.accept"),
    QUEST_MAIN_DRAGON_PACT_DECLINE("quest.main.dragon_pact.decline"),
    
    QUEST_MAIN_SKY_FORTRESS_NAME("quest.main.sky_fortress.name"),
    QUEST_MAIN_SKY_FORTRESS_INFO("quest.main.sky_fortress.info"),
    QUEST_MAIN_SKY_FORTRESS_DIALOGS("quest.main.sky.fortress.dialogs"),
    QUEST_MAIN_SKY_FORTRESS_NPC_NAME("quest.main.sky_fortress.npc_name"),
    QUEST_MAIN_SKY_FORTRESS_ACCEPT("quest.main.sky_fortress.accept"),
    QUEST_MAIN_SKY_FORTRESS_DECLINE("quest.main.sky_fortress.decline"),
    
    QUEST_MAIN_SHADOW_CULT_NAME("quest.main.shadow_cult.name"),
    QUEST_MAIN_SHADOW_CULT_INFO("quest.main.shadow_cult.info"),
    QUEST_MAIN_SHADOW_CULT_DIALOGS("quest.main.shadow.cult.dialogs"),
    QUEST_MAIN_SHADOW_CULT_NPC_NAME("quest.main.shadow_cult.npc_name"),
    QUEST_MAIN_SHADOW_CULT_ACCEPT("quest.main.shadow_cult.accept"),
    QUEST_MAIN_SHADOW_CULT_DECLINE("quest.main.shadow_cult.decline"),
    
    QUEST_MAIN_FORBIDDEN_RITUAL_NAME("quest.main.forbidden_ritual.name"),
    QUEST_MAIN_FORBIDDEN_RITUAL_INFO("quest.main.forbidden_ritual.info"),
    QUEST_MAIN_FORBIDDEN_RITUAL_DIALOGS("quest.main.forbidden.ritual.dialogs"),
    QUEST_MAIN_FORBIDDEN_RITUAL_NPC_NAME("quest.main.forbidden_ritual.npc_name"),
    QUEST_MAIN_FORBIDDEN_RITUAL_ACCEPT("quest.main.forbidden_ritual.accept"),
    QUEST_MAIN_FORBIDDEN_RITUAL_DECLINE("quest.main.forbidden_ritual.decline"),
    
    QUEST_MAIN_CHAOS_STORM_NAME("quest.main.chaos_storm.name"),
    QUEST_MAIN_CHAOS_STORM_INFO("quest.main.chaos_storm.info"),
    QUEST_MAIN_CHAOS_STORM_DIALOGS("quest.main.chaos.storm.dialogs"),
    QUEST_MAIN_CHAOS_STORM_NPC_NAME("quest.main.chaos_storm.npc_name"),
    QUEST_MAIN_CHAOS_STORM_ACCEPT("quest.main.chaos_storm.accept"),
    QUEST_MAIN_CHAOS_STORM_DECLINE("quest.main.chaos_storm.decline"),
    
    QUEST_MAIN_DARK_FORTRESS_NAME("quest.main.dark_fortress.name"),
    QUEST_MAIN_DARK_FORTRESS_INFO("quest.main.dark_fortress.info"),
    QUEST_MAIN_DARK_FORTRESS_DIALOGS("quest.main.dark.fortress.dialogs"),
    QUEST_MAIN_DARK_FORTRESS_NPC_NAME("quest.main.dark_fortress.npc_name"),
    QUEST_MAIN_DARK_FORTRESS_ACCEPT("quest.main.dark_fortress.accept"),
    QUEST_MAIN_DARK_FORTRESS_DECLINE("quest.main.dark_fortress.decline"),
    
    // Chapter 4 Main Quest
    QUEST_MAIN_DIMENSIONAL_RIFT_NAME("quest.main.dimensional_rift.name"),
    QUEST_MAIN_DIMENSIONAL_RIFT_INFO("quest.main.dimensional_rift.info"),
    QUEST_MAIN_DIMENSIONAL_RIFT_DIALOGS("quest.main.dimensional.rift.dialogs"),
    QUEST_MAIN_DIMENSIONAL_RIFT_NPC_NAME("quest.main.dimensional_rift.npc_name"),
    QUEST_MAIN_DIMENSIONAL_RIFT_ACCEPT("quest.main.dimensional_rift.accept"),
    QUEST_MAIN_DIMENSIONAL_RIFT_DECLINE("quest.main.dimensional_rift.decline"),
    
    // Missing Daily Quest Info keys
    QUEST_DAILY_BOUNTY_HUNTER_INFO("quest.daily.bounty_hunter.info"),
    QUEST_DAILY_CRAFTING_INFO("quest.daily.crafting.info"),
    QUEST_DAILY_DELIVERY_INFO("quest.daily.delivery.info"),
    QUEST_DAILY_EXPLORATION_INFO("quest.daily.exploration.info"),
    QUEST_DAILY_FISHING_INFO("quest.daily.fishing.info"),
    QUEST_DAILY_GATHERING_INFO("quest.daily.gathering.info"),
    QUEST_DAILY_HUNTING_INFO("quest.daily.hunting.info"),
    QUEST_DAILY_MINING_INFO("quest.daily.mining.info"),
    
    // Missing Tutorial Quest Info keys
    QUEST_TUTORIAL_FIRST_STEPS_INFO("quest.tutorial.first_steps.info"),
    QUEST_TUTORIAL_FIRST_STEPS_OBJECTIVES_VISIT_HUB("quest.tutorial.first_steps.objectives.visit_hub"),
    QUEST_TUTORIAL_FIRST_STEPS_OBJECTIVES_VISIT_MERCHANT("quest.tutorial.first_steps.objectives.visit_merchant"),
    QUEST_TUTORIAL_BASIC_COMBAT_INFO("quest.tutorial.basic_combat.info"),
    
    // Missing Main Quest keys
    QUEST_MAIN_GUARDIAN_AWAKENING_NAME("quest.main.guardian_awakening.name"),
    QUEST_MAIN_GUARDIAN_AWAKENING_INFO("quest.main.guardian_awakening.info"),
    QUEST_MAIN_GUARDIAN_AWAKENING_DIALOGS("quest.main.guardian.awakening.dialogs"),
    QUEST_MAIN_GUARDIAN_AWAKENING_NPC_NAME("quest.main.guardian_awakening.npc_name"),
    QUEST_MAIN_GUARDIAN_AWAKENING_ACCEPT("quest.main.guardian_awakening.accept"),
    QUEST_MAIN_GUARDIAN_AWAKENING_DECLINE("quest.main.guardian_awakening.decline"),
    
    QUEST_MAIN_HEROES_JOURNEY_NAME("quest.main.heroes_journey.name"),
    QUEST_MAIN_HEROES_JOURNEY_INFO("quest.main.heroes_journey.info"),
    QUEST_MAIN_HEROES_JOURNEY_DIALOGS("quest.main.heroes.journey.dialogs"),
    QUEST_MAIN_HEROES_JOURNEY_NPC_NAME("quest.main.heroes_journey.npc_name"),
    QUEST_MAIN_HEROES_JOURNEY_ACCEPT("quest.main.heroes_journey.accept"),
    QUEST_MAIN_HEROES_JOURNEY_DECLINE("quest.main.heroes_journey.decline"),
    
    QUEST_MAIN_PATH_OF_DARKNESS_NAME("quest.main.path_of_darkness.name"),
    QUEST_MAIN_PATH_OF_DARKNESS_INFO("quest.main.path_of_darkness.info"),
    QUEST_MAIN_PATH_OF_DARKNESS_DIALOGS("quest.main.path.of.darkness.dialogs"),
    QUEST_MAIN_PATH_OF_DARKNESS_NPC_NAME("quest.main.path_of_darkness.npc_name"),
    QUEST_MAIN_PATH_OF_DARKNESS_ACCEPT("quest.main.path_of_darkness.accept"),
    QUEST_MAIN_PATH_OF_DARKNESS_DECLINE("quest.main.path_of_darkness.decline"),
    
    QUEST_MAIN_PATH_OF_LIGHT_NAME("quest.main.path_of_light.name"),
    QUEST_MAIN_PATH_OF_LIGHT_INFO("quest.main.path_of_light.info"),
    QUEST_MAIN_PATH_OF_LIGHT_DIALOGS("quest.main.path.of.light.dialogs"),
    QUEST_MAIN_PATH_OF_LIGHT_NPC_NAME("quest.main.path_of_light.npc_name"),
    QUEST_MAIN_PATH_OF_LIGHT_ACCEPT("quest.main.path_of_light.accept"),
    QUEST_MAIN_PATH_OF_LIGHT_DECLINE("quest.main.path_of_light.decline"),
    
    // Chapter 4 Missing Keys
    QUEST_MAIN_REALM_DEFENDERS_NAME("quest.main.realm_defenders.name"),
    QUEST_MAIN_REALM_DEFENDERS_INFO("quest.main.realm_defenders.info"),
    QUEST_MAIN_REALM_DEFENDERS_DIALOGS("quest.main.realm.defenders.dialogs"),
    QUEST_MAIN_REALM_DEFENDERS_NPC_NAME("quest.main.realm_defenders.npc_name"),
    QUEST_MAIN_REALM_DEFENDERS_ACCEPT("quest.main.realm_defenders.accept"),
    QUEST_MAIN_REALM_DEFENDERS_DECLINE("quest.main.realm_defenders.decline"),
    
    QUEST_MAIN_REALM_PORTAL_NAME("quest.main.realm_portal.name"),
    QUEST_MAIN_REALM_PORTAL_INFO("quest.main.realm_portal.info"),
    QUEST_MAIN_REALM_PORTAL_DIALOGS("quest.main.realm.portal.dialogs"),
    QUEST_MAIN_REALM_PORTAL_NPC_NAME("quest.main.realm_portal.npc_name"),
    QUEST_MAIN_REALM_PORTAL_ACCEPT("quest.main.realm_portal.accept"),
    QUEST_MAIN_REALM_PORTAL_DECLINE("quest.main.realm_portal.decline"),
    
    QUEST_MAIN_VOID_INVASION_NAME("quest.main.void_invasion.name"),
    QUEST_MAIN_VOID_INVASION_INFO("quest.main.void_invasion.info"),
    QUEST_MAIN_VOID_INVASION_DIALOGS("quest.main.void.invasion.dialogs"),
    QUEST_MAIN_VOID_INVASION_NPC_NAME("quest.main.void_invasion.npc_name"),
    QUEST_MAIN_VOID_INVASION_ACCEPT("quest.main.void_invasion.accept"),
    QUEST_MAIN_VOID_INVASION_DECLINE("quest.main.void_invasion.decline"),
    
    // Chapter 5 Missing Keys
    QUEST_MAIN_FINAL_BATTLE_NAME("quest.main.final_battle.name"),
    QUEST_MAIN_FINAL_BATTLE_INFO("quest.main.final_battle.info"),
    QUEST_MAIN_FINAL_BATTLE_DIALOGS("quest.main.final.battle.dialogs"),
    QUEST_MAIN_FINAL_BATTLE_NPC_NAME("quest.main.final_battle.npc_name"),
    QUEST_MAIN_FINAL_BATTLE_ACCEPT("quest.main.final_battle.accept"),
    QUEST_MAIN_FINAL_BATTLE_DECLINE("quest.main.final_battle.decline"),
    
    QUEST_MAIN_GATHERING_STORM_NAME("quest.main.gathering_storm.name"),
    QUEST_MAIN_GATHERING_STORM_INFO("quest.main.gathering_storm.info"),
    QUEST_MAIN_GATHERING_STORM_DIALOGS("quest.main.gathering.storm.dialogs"),
    QUEST_MAIN_GATHERING_STORM_NPC_NAME("quest.main.gathering_storm.npc_name"),
    QUEST_MAIN_GATHERING_STORM_ACCEPT("quest.main.gathering_storm.accept"),
    QUEST_MAIN_GATHERING_STORM_DECLINE("quest.main.gathering_storm.decline"),
    
    QUEST_MAIN_LAST_STAND_NAME("quest.main.last_stand.name"),
    QUEST_MAIN_LAST_STAND_INFO("quest.main.last_stand.info"),
    QUEST_MAIN_LAST_STAND_DIALOGS("quest.main.last.stand.dialogs"),
    QUEST_MAIN_LAST_STAND_NPC_NAME("quest.main.last_stand.npc_name"),
    QUEST_MAIN_LAST_STAND_ACCEPT("quest.main.last_stand.accept"),
    QUEST_MAIN_LAST_STAND_DECLINE("quest.main.last_stand.decline"),
    
    QUEST_MAIN_NEW_ERA_NAME("quest.main.new_era.name"),
    QUEST_MAIN_NEW_ERA_INFO("quest.main.new_era.info"),
    QUEST_MAIN_NEW_ERA_DIALOGS("quest.main.new.era.dialogs"),
    QUEST_MAIN_NEW_ERA_NPC_NAME("quest.main.new_era.npc_name"),
    QUEST_MAIN_NEW_ERA_ACCEPT("quest.main.new_era.accept"),
    QUEST_MAIN_NEW_ERA_DECLINE("quest.main.new_era.decline"),
    
    QUEST_MAIN_SACRIFICE_OF_HEROES_NAME("quest.main.sacrifice_of_heroes.name"),
    QUEST_MAIN_SACRIFICE_OF_HEROES_INFO("quest.main.sacrifice_of_heroes.info"),
    QUEST_MAIN_SACRIFICE_OF_HEROES_DIALOGS("quest.main.sacrifice.of.heroes.dialogs"),
    QUEST_MAIN_SACRIFICE_OF_HEROES_NPC_NAME("quest.main.sacrifice_of_heroes.npc_name"),
    QUEST_MAIN_SACRIFICE_OF_HEROES_ACCEPT("quest.main.sacrifice_of_heroes.accept"),
    QUEST_MAIN_SACRIFICE_OF_HEROES_DECLINE("quest.main.sacrifice_of_heroes.decline"),
    
    // Chapter 6 Missing Keys
    QUEST_MAIN_ETERNAL_GUARDIAN_NAME("quest.main.eternal_guardian.name"),
    QUEST_MAIN_ETERNAL_GUARDIAN_INFO("quest.main.eternal_guardian.info"),
    QUEST_MAIN_ETERNAL_GUARDIAN_DIALOGS("quest.main.eternal.guardian.dialogs"),
    QUEST_MAIN_ETERNAL_GUARDIAN_NPC_NAME("quest.main.eternal_guardian.npc_name"),
    QUEST_MAIN_ETERNAL_GUARDIAN_ACCEPT("quest.main.eternal_guardian.accept"),
    QUEST_MAIN_ETERNAL_GUARDIAN_DECLINE("quest.main.eternal_guardian.decline"),
    
    QUEST_MAIN_LEGACY_OF_HEROES_NAME("quest.main.legacy_of_heroes.name"),
    QUEST_MAIN_LEGACY_OF_HEROES_INFO("quest.main.legacy_of_heroes.info"),
    QUEST_MAIN_LEGACY_OF_HEROES_DIALOGS("quest.main.legacy.of.heroes.dialogs"),
    QUEST_MAIN_LEGACY_OF_HEROES_NPC_NAME("quest.main.legacy_of_heroes.npc_name"),
    QUEST_MAIN_LEGACY_OF_HEROES_ACCEPT("quest.main.legacy_of_heroes.accept"),
    QUEST_MAIN_LEGACY_OF_HEROES_DECLINE("quest.main.legacy_of_heroes.decline"),
    
    QUEST_MAIN_RESTORATION_NAME("quest.main.restoration.name"),
    QUEST_MAIN_RESTORATION_INFO("quest.main.restoration.info"),
    QUEST_MAIN_RESTORATION_DIALOGS("quest.main.restoration.dialogs"),
    QUEST_MAIN_RESTORATION_NPC_NAME("quest.main.restoration.npc_name"),
    QUEST_MAIN_RESTORATION_ACCEPT("quest.main.restoration.accept"),
    QUEST_MAIN_RESTORATION_DECLINE("quest.main.restoration.decline"),
    
    // =================================
    // GUI DIALOG CHOICE KEYS
    // =================================
    GUI_DIALOG_CHOICE_TITLE("gui.dialog-choice.title"),
    GUI_DIALOG_CHOICE_ICON("gui.dialog-choice.icon"),
    GUI_DIALOG_CHOICE_DESCRIPTION("gui.dialog-choice.description"),
    GUI_DIALOG_CHOICE_NPC("gui.dialog-choice.npc"),
    GUI_DIALOG_CHOICE_NPC_CONTENT("gui.dialog-choice.npc-content"),
    GUI_DIALOG_CHOICE_OPTION("gui.dialog-choice.option"),
    GUI_DIALOG_CHOICE_CLICK_TO_SELECT("gui.dialog-choice.click-to-select"),
    
    // =================================
    // GUI LEADERBOARD ADDITIONAL KEYS
    // =================================
    GUI_LEADERBOARD_VALUE("gui.leaderboard.value"),
    GUI_LEADERBOARD_THIS_IS_YOU("gui.leaderboard.this-is-you"),
    
    // =================================
    // GUI MAIL DETAIL KEYS
    // =================================
    GUI_MAIL_DETAIL_TITLE("gui.mail-detail.title"),
    GUI_MAIL_DETAIL_SENDER("gui.mail-detail.sender"),
    GUI_MAIL_DETAIL_RECEIVER("gui.mail-detail.receiver"),
    GUI_MAIL_DETAIL_SENT_TIME("gui.mail-detail.sent-time"),
    GUI_MAIL_DETAIL_STATUS("gui.mail-detail.status"),
    GUI_MAIL_DETAIL_NO_MESSAGE("gui.mail-detail.no-message"),
    GUI_MAIL_DETAIL_DELETE_SUCCESS("gui.mail-detail.delete-success"),
    GUI_MAIL_DETAIL_DELETE_FAILED("gui.mail-detail.delete-failed"),
    GUI_MAIL_DETAIL_REPLY_DESC("gui.mail-detail.reply-desc"),
    GUI_MAIL_DETAIL_REPLY_GUIDE("gui.mail-detail.reply-guide"),
    GUI_MAIL_DETAIL_REPLY_COMMAND("gui.mail-detail.reply-command"),
    
    // =================================
    // GUI MAILBOX KEYS
    // =================================
    GUI_MAILBOX_REFRESH_SUCCESS("gui.mailbox.refresh-success"),
    GUI_MAILBOX_SEND_MAIL_GUIDE("gui.mailbox.send-mail-guide"),
    GUI_MAILBOX_SEND_MAIL_COMMAND("gui.mailbox.send-mail-command"),
    GUI_MAILBOX_SEND_MAIL_EXAMPLE("gui.mailbox.send-mail-example"),
    GUI_MAILBOX_SENDER("gui.mailbox.sender"),
    GUI_MAILBOX_STATUS("gui.mailbox.status"),
    GUI_MAILBOX_TIME("gui.mailbox.time"),
    GUI_MAILBOX_DELETE_CONFIRM_WORD("mailbox.delete-confirm-word"),
    GUI_MAILBOX_DELETE_CONFIRM_TEXT("gui.mailbox.delete-confirm-text"),
    GUI_MAILBOX_DELETE_CONFIRM_TITLE("gui.mailbox.delete-confirm-title"),
    
    // =================================
    // GUI FRIEND REQUEST KEYS
    // =================================
    GUI_FRIEND_REQUESTS_REQUEST_TIME("gui.friend-requests.request-time"),
    GUI_FRIEND_REQUESTS_MESSAGE("gui.friend-requests.message"),
    GUI_FRIEND_REQUESTS_ACCEPT_DESC1("gui.friend-requests.accept-desc1"),
    GUI_FRIEND_REQUESTS_REJECT_DESC1("gui.friend-requests.reject-desc1"),
    
    // =================================
    // GUI FRIENDS KEYS
    // =================================
    GUI_FRIENDS_TITLE("gui.friends.title"),
    GUI_FRIENDS_ADD_COMMAND_HINT("gui.friends.add-command-hint"),
    GUI_FRIENDS_ADD_COMMAND_EXAMPLE("gui.friends.add-command-example"),
    GUI_FRIENDS_REFRESHED("gui.friends.refreshed"),
    GUI_FRIENDS_STATUS("gui.friends.status"),
    GUI_FRIENDS_SINCE("gui.friends.since"),
    GUI_FRIENDS_WHISPER_HINT("gui.friends.whisper-hint"),
    
    // =================================
    // GUI ALL QUESTS KEYS
    // =================================
    GUI_ALL_QUESTS_TITLE("gui.all_quests.title"),
    
    // =================================
    // QUEST FILTER KEYS
    // =================================
    QUEST_FILTER("quest.filter"),
    QUEST_TOTAL_COUNT("quest.total-count"),
    QUEST_PROGRESS("quest.progress"),
    
    // =================================
    // ERROR KEYS
    // =================================
    ERROR_FRIEND_REQUEST_ID_MISSING("error.friend-request-id-missing"),
    
    // =================================
    // GUI ISLAND KEYS (Additional missing ones)
    // =================================
    GUI_ISLAND_BIOME_CHANGE_TITLE("gui.island.biome-change.title"),
    GUI_ISLAND_BIOME_MESSAGE_NO_PERMISSION("gui.island.biome.message.no_permission"),
    GUI_ISLAND_BIOME_CURRENT("gui.island.biome.current"),
    GUI_ISLAND_BIOME_CLICK_TO_CHANGE("gui.island.biome.click-to-change"),
    GUI_ISLAND_BIOME_MESSAGE_ALREADY_SELECTED("gui.island.biome.message.already_selected"),
    GUI_ISLAND_BIOME_MESSAGE_WORLD_NOT_FOUND("gui.island.biome.message.world_not_found"),
    GUI_ISLAND_BIOME_MESSAGE_CHANGING("gui.island.biome.message.changing"),
    GUI_ISLAND_BIOME_MESSAGE_CHUNK_RELOAD_NOTICE("gui.island.biome.message.chunk_reload_notice"),
    GUI_ISLAND_BIOME_UNKNOWN("gui.island.biome.unknown"),
    GUI_ISLAND_BIOME_CURRENT_INFO("gui.island.biome.current-info"),
    GUI_ISLAND_BIOME_CURRENT_INFO_LORE("gui.island.biome.current-info.lore"),
    GUI_ISLAND_BIOME_BACK_LORE("gui.island.biome.back.lore"),
    
    GUI_ISLAND_BIOME_SELECTION_TITLE("gui.island.biome-selection.title"),
    GUI_ISLAND_BIOME_SELECTION_INFO_TITLE("gui.island.biome-selection.info.title"),
    GUI_ISLAND_BIOME_SELECTION_INFO_LORE1("gui.island.biome-selection.info.lore1"),
    GUI_ISLAND_BIOME_SELECTION_INFO_LORE2("gui.island.biome-selection.info.lore2"),
    GUI_ISLAND_BIOME_SELECTION_CURRENT_SELECTED("gui.island.biome-selection.current-selected"),
    GUI_ISLAND_BIOME_SELECTION_CLICK_TO_SELECT("gui.island.biome-selection.click-to-select"),
    GUI_ISLAND_BIOME_SELECTION_BACK_LORE("gui.island.biome-selection.back.lore"),
    
    GUI_ISLAND_BIOME_SIMPLE_TITLE("gui.island.biome-simple.title"),
    GUI_ISLAND_BIOME_MESSAGE_CHANGED("gui.island.biome.message.changed"),
    
    // Island contribute/contribution keys
    GUI_ISLAND_CONTRIBUTE_TITLE("gui.island.contribute.title"),
    GUI_ISLAND_CONTRIBUTE_GOLD_INFO_BALANCE("gui.island.contribute.gold-info.balance"),
    GUI_ISLAND_CONTRIBUTE_GOLD_INFO_CONTRIBUTION("gui.island.contribute.gold-info.contribution"),
    GUI_ISLAND_CONTRIBUTE_QUICK_AMOUNT("gui.island.contribute.quick-amount"),
    GUI_ISLAND_CONTRIBUTE_GOLD_NEEDED("gui.island.contribute.gold-needed"),
    
    GUI_ISLAND_CONTRIBUTION_TITLE("gui.island.contribution.title"),
    GUI_ISLAND_CONTRIBUTION_INFO_ISLAND_NAME("gui.island.contribution.info.island-name"),
    GUI_ISLAND_CONTRIBUTION_INFO_TOTAL("gui.island.contribution.info.total"),
    GUI_ISLAND_CONTRIBUTION_INFO_CONTRIBUTORS("gui.island.contribution.info.contributors"),
    GUI_ISLAND_CONTRIBUTION_CONTRIBUTOR_NAME("gui.island.contribution.contributor.name"),
    GUI_ISLAND_CONTRIBUTION_CONTRIBUTOR_CONTRIBUTION("gui.island.contribution.contributor.contribution"),
    GUI_ISLAND_CONTRIBUTION_CONTRIBUTOR_ROLE("gui.island.contribution.contributor.role"),
    GUI_ISLAND_CONTRIBUTION_CONTRIBUTOR_PERCENTAGE("gui.island.contribution.contributor.percentage"),
    GUI_ISLAND_CONTRIBUTION_ADD_CURRENT("gui.island.contribution.add.current"),
    
    // Island role keys
    GUI_ISLAND_ROLE_OWNER("gui.island.role.owner"),
    GUI_ISLAND_ROLE_CO_OWNER("gui.island.role.co-owner"),
    GUI_ISLAND_ROLE_MEMBER("gui.island.role.member"),
    GUI_ISLAND_ROLE_WORKER("gui.island.role.worker"),
    GUI_ISLAND_ROLE_CONTRIBUTOR("gui.island.role.contributor"),
    
    // Island creation keys
    GUI_ISLAND_CREATION_TITLE("gui.island.creation.title"),
    ISLAND_DEFAULT_NAME("island.default-name"),
    ISLAND_GUI_CREATION_SELECTED("island.gui.creation.selected"),
    ISLAND_GUI_CREATION_CLICK_TO_SELECT("island.gui.creation.click-to-select"),
    
    // Island delete confirm keys
    GUI_ISLAND_DELETE_CONFIRM_TITLE("gui.island.delete-confirm.title"),
    GUI_ISLAND_DELETE_CONFIRM_ISLAND_INFO_TITLE("gui.island.delete-confirm.island-info.title"),
    GUI_ISLAND_DELETE_CONFIRM_ISLAND_INFO_ID("gui.island.delete-confirm.island-info.id"),
    GUI_ISLAND_DELETE_CONFIRM_ISLAND_INFO_MEMBERS("gui.island.delete-confirm.island-info.members"),
    GUI_ISLAND_DELETE_CONFIRM_ISLAND_INFO_CREATED("gui.island.delete-confirm.island-info.created"),
    
    // Island member keys
    GUI_ISLAND_MEMBER_INVITE_PROMPT("gui.island.member.invite-prompt"),
    GUI_ISLAND_MEMBER_INVITE_EXAMPLE("gui.island.member.invite-example"),
    GUI_ISLAND_MEMBER_ROLE("gui.island.member.role"),
    GUI_ISLAND_MEMBER_ROLE_OWNER("gui.island.member.role-owner"),
    GUI_ISLAND_MEMBER_ALL_PERMISSIONS("gui.island.member.all-permissions"),
    GUI_ISLAND_MEMBER_OWNER_CANNOT_CHANGE("gui.island.member.owner-cannot-change"),
    GUI_ISLAND_MEMBER_JOIN_DATE("gui.island.member.join-date"),
    GUI_ISLAND_MEMBER_MOST_PERMISSIONS("gui.island.member.most-permissions"),
    GUI_ISLAND_MEMBER_NORMAL_PERMISSIONS("gui.island.member.normal-permissions"),
    GUI_ISLAND_MEMBER_LEFT_CLICK_ROLE("gui.island.member.left-click-role"),
    GUI_ISLAND_MEMBER_RIGHT_CLICK_KICK("gui.island.member.right-click-kick"),
    GUI_ISLAND_MEMBER_ROLE_WORKER("gui.island.member.role-worker"),
    GUI_ISLAND_MEMBER_HIRE_DATE("gui.island.member.hire-date"),
    GUI_ISLAND_MEMBER_LAST_ACTIVITY("gui.island.member.last-activity"),
    GUI_ISLAND_MEMBER_LIMITED_PERMISSIONS("gui.island.member.limited-permissions"),
    GUI_ISLAND_MEMBER_LEFT_CLICK_EXTEND("gui.island.member.left-click-extend"),
    GUI_ISLAND_MEMBER_RIGHT_CLICK_FIRE("gui.island.member.right-click-fire"),
    
    // Island member manage keys
    GUI_ISLAND_MEMBER_MANAGE_TITLE("gui.island.member-manage.title"),
    GUI_ISLAND_MEMBER_MANAGE_PERMISSION_NOT_IMPLEMENTED("gui.island.member-manage.permission-not-implemented"),
    GUI_ISLAND_MEMBER_MANAGE_CURRENT_ROLE("gui.island.member-manage.current-role"),
    
    // Island permission keys
    GUI_ISLAND_PERMISSION_TITLE("gui.island.permission.title"),
    
    // Island roles
    ISLAND_ROLES_SUB_OWNER("island.roles.sub-owner"),
    ISLAND_ROLES_MEMBER("island.roles.member"),
    ISLAND_ROLES_WORKER("island.roles.worker"),
    
    // Island contribute/delete/member actions
    ISLAND_CONTRIBUTE_AMOUNT_TOO_LOW("island.contribute.amount-too-low"),
    ISLAND_CONTRIBUTE_INVALID_AMOUNT("island.contribute.invalid-amount"),
    ISLAND_GUI_CONTRIBUTE_CONTRIBUTION_INPUT_TEXT("island.gui.contribute.contribution-input-text"),
    ISLAND_GUI_CONTRIBUTE_CONTRIBUTION_INPUT_TITLE("island.gui.contribute.contribution-input-title"),
    
    ISLAND_DELETE_CONFIRM_WORD("island.delete.confirm-word"),
    ISLAND_DELETE_INPUT_ERROR("island.delete.input-error"),
    ISLAND_DELETE_INPUT_TEXT("island.delete.input-text"),
    ISLAND_DELETE_INPUT_TITLE("island.delete.input-title"),
    
    ISLAND_MEMBER_KICK_CONFIRM_WORD("island.member.kick-confirm-word"),
    ISLAND_MEMBER_KICK_INPUT_ERROR("island.member.kick-input-error"),
    ISLAND_MEMBER_KICK_INPUT_TEXT("island.member.kick-input-text"),
    ISLAND_MEMBER_KICK_INPUT_TITLE("island.member.kick-input-title"),
    
    // Items buttons additional keys
    ITEMS_BUTTONS_BACK_NAME("items.buttons.back.name"),
    ITEMS_BUTTONS_BACK_LORE("items.buttons.back.lore"),
    
    // =================================
    // Missing keys for deprecated method migration
    // =================================
    GUI_PAGE_INFO("gui.page-info"),
    GUI_SHOP_TITLE("gui.shop.title"),
    GUI_SHOP_GOLD_AMOUNT("gui.shop.gold.amount"),
    GUI_SHOP_ITEM_BUY_PRICE("gui.shop.item.buy_price"),
    GUI_SHOP_ITEM_SELL_PRICE("gui.shop.item.sell_price"),
    GUI_SHOP_ITEM_CLICK_TO_BUY("gui.shop.item.click_to_buy"),
    GUI_SHOP_ITEM_CLICK_TO_SELL("gui.shop.item.click_to_sell"),
    GUI_SHOP_ITEM_INSUFFICIENT_GOLD("gui.shop.item.insufficient_gold");
    
    private final String key;
    
    LangKey(String key) {
        this.key = key;
    }
    
    public String getKey() {
        return key;
    }
}