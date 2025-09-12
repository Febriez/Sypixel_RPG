package com.febrie.rpg.util.lang.quest.guild;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for GuildQuest quests
 */
public enum GuildQuestLangKey implements ILangKey {
    QUEST_GUILD_ESTABLISHMENT_NAME("quest.guild.establishment.name"),
    QUEST_GUILD_ESTABLISHMENT_DESC("quest.guild.establishment.desc"),
    QUEST_GUILD_FORTRESS_SIEGE_NAME("quest.guild.fortress_siege.name"),
    QUEST_GUILD_FORTRESS_SIEGE_DESC("quest.guild.fortress_siege.desc"),
    QUEST_GUILD_ALLIANCE_FORMATION_NAME("quest.guild.alliance_formation.name"),
    QUEST_GUILD_ALLIANCE_FORMATION_DESC("quest.guild.alliance_formation.desc"),
    QUEST_GUILD_RESOURCE_WAR_NAME("quest.guild.resource_war.name"),
    QUEST_GUILD_RESOURCE_WAR_DESC("quest.guild.resource_war.desc"),
    QUEST_GUILD_ALLIANCE_FORMATION_ACCEPT("quest.guild.alliance.formation.accept"),
    QUEST_GUILD_ALLIANCE_FORMATION_DECLINE("quest.guild.alliance.formation.decline"),
    QUEST_GUILD_ALLIANCE_FORMATION_DIALOGS("quest.guild.alliance.formation.dialogs"),
    QUEST_GUILD_ALLIANCE_FORMATION_INFO("quest.guild.alliance.formation.info"),
    QUEST_GUILD_ALLIANCE_FORMATION_NPC_NAME("quest.guild.alliance.formation.npc.name"),
    QUEST_GUILD_ALLIANCE_FORMATION_OBJECTIVES_COLLECT_TREATY_MATERIALS("quest.guild.alliance.formation.objectives.collect.treaty.materials"),
    QUEST_GUILD_ALLIANCE_FORMATION_OBJECTIVES_NEGOTIATE_WITH_GUILDS("quest.guild.alliance.formation.objectives.negotiate.with.guilds"),
    QUEST_GUILD_ALLIANCE_FORMATION_OBJECTIVES_FORM_ALLIANCES("quest.guild.alliance.formation.objectives.form.alliances"),
    QUEST_GUILD_ALLIANCE_FORMATION_OBJECTIVES_COMPLETE_JOINT_QUESTS("quest.guild.alliance.formation.objectives.complete.joint.quests"),
    QUEST_GUILD_ESTABLISHMENT_ACCEPT("quest.guild.establishment.accept"),
    QUEST_GUILD_ESTABLISHMENT_DECLINE("quest.guild.establishment.decline"),
    QUEST_GUILD_ESTABLISHMENT_DIALOGS("quest.guild.establishment.dialogs"),
    QUEST_GUILD_ESTABLISHMENT_INFO("quest.guild.establishment.info"),
    QUEST_GUILD_ESTABLISHMENT_NPC_NAME("quest.guild.establishment.npc.name"),
    QUEST_GUILD_ESTABLISHMENT_OBJECTIVES_BUILD_GUILD_HALL("quest.guild.establishment.objectives.build.guild.hall"),
    QUEST_GUILD_ESTABLISHMENT_OBJECTIVES_COLLECT_FOUNDATION_MATERIALS("quest.guild.establishment.objectives.collect.foundation.materials"),
    QUEST_GUILD_ESTABLISHMENT_OBJECTIVES_COLLECT_GOLD_FOR_CHARTER("quest.guild.establishment.objectives.collect.gold.for.charter"),
    QUEST_GUILD_ESTABLISHMENT_OBJECTIVES_CREATE_GUILD("quest.guild.establishment.objectives.create.guild"),
    QUEST_GUILD_ESTABLISHMENT_OBJECTIVES_RECRUIT_INITIAL_MEMBERS("quest.guild.establishment.objectives.recruit.initial.members"),
    QUEST_GUILD_FORTRESS_SIEGE_ACCEPT("quest.guild.fortress.siege.accept"),
    QUEST_GUILD_FORTRESS_SIEGE_DECLINE("quest.guild.fortress.siege.decline"),
    QUEST_GUILD_FORTRESS_SIEGE_DIALOGS("quest.guild.fortress.siege.dialogs"),
    QUEST_GUILD_FORTRESS_SIEGE_INFO("quest.guild.fortress.siege.info"),
    QUEST_GUILD_FORTRESS_SIEGE_NPC_NAME("quest.guild.fortress.siege.npc.name"),
    QUEST_GUILD_FORTRESS_SIEGE_OBJECTIVES_CAPTURE_CONTROL_POINTS("quest.guild.fortress.siege.objectives.capture.control.points"),
    QUEST_GUILD_FORTRESS_SIEGE_OBJECTIVES_DEFEAT_ENEMY_PLAYERS("quest.guild.fortress.siege.objectives.defeat.enemy.players"),
    QUEST_GUILD_FORTRESS_SIEGE_OBJECTIVES_DEFEND_FORTRESS("quest.guild.fortress.siege.objectives.defend.fortress"),
    QUEST_GUILD_FORTRESS_SIEGE_OBJECTIVES_DESTROY_ENEMY_WALLS("quest.guild.fortress.siege.objectives.destroy.enemy.walls"),
    QUEST_GUILD_FORTRESS_SIEGE_OBJECTIVES_PARTICIPATE_IN_SIEGE("quest.guild.fortress.siege.objectives.participate.in.siege"),
    QUEST_GUILD_RESOURCE_WAR_ACCEPT("quest.guild.resource.war.accept"),
    QUEST_GUILD_RESOURCE_WAR_DECLINE("quest.guild.resource.war.decline"),
    QUEST_GUILD_RESOURCE_WAR_DIALOGS("quest.guild.resource.war.dialogs"),
    QUEST_GUILD_RESOURCE_WAR_INFO("quest.guild.resource.war.info"),
    QUEST_GUILD_RESOURCE_WAR_NPC_NAME("quest.guild.resource.war.npc.name"),
    QUEST_GUILD_RESOURCE_WAR_OBJECTIVES_COLLECT_RARE_ORES("quest.guild.resource.war.objectives.collect.rare.ores"),
    QUEST_GUILD_RESOURCE_WAR_OBJECTIVES_CONTRIBUTE_TO_GUILD("quest.guild.resource.war.objectives.contribute.to.guild"),
    QUEST_GUILD_RESOURCE_WAR_OBJECTIVES_CONTROL_MINING_NODES("quest.guild.resource.war.objectives.control.mining.nodes"),
    QUEST_GUILD_RESOURCE_WAR_OBJECTIVES_CONTROL_STRATEGIC_LOCATIONS("quest.guild.resource.war.objectives.control.strategic.locations"),
    QUEST_GUILD_RESOURCE_WAR_OBJECTIVES_DEFEND_RESOURCE_NODES("quest.guild.resource.war.objectives.defend.resource.nodes"),
    QUEST_GUILD_ALLIANCE_FORMATION_OBJECTIVES_PAPER_COLLECT("quest.guild.alliance.formation.objectives.paper.collect"),
    QUEST_GUILD_ALLIANCE_FORMATION_OBJECTIVES_PAPER_DELIVER("quest.guild.alliance.formation.objectives.paper.deliver"),
    QUEST_GUILD_ESTABLISHMENT_OBJECTIVES_GOLD_INGOT_COLLECT("quest.guild.establishment.objectives.gold.ingot.collect"),
    QUEST_GUILD_ESTABLISHMENT_OBJECTIVES_STONE_BRICKS_COLLECT("quest.guild.establishment.objectives.stone.bricks.collect"),
    QUEST_GUILD_RESOURCE_WAR_OBJECTIVES_GOLD_INGOT_DELIVER("quest.guild.resource.war.objectives.gold.ingot.deliver"),
    QUEST_GUILD_RESOURCE_WAR_OBJECTIVES_NETHERITE_INGOT_COLLECT("quest.guild.resource.war.objectives.netherite.ingot.collect");

    private final String key;
    
    GuildQuestLangKey(String key) {
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
