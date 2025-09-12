package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for ThievesGuild quests
 */
public enum ThievesGuildLangKey implements ILangKey {
    QUEST_SIDE_THIEVES_GUILD_NAME("quest.side.thieves_guild.name"),
    QUEST_SIDE_THIEVES_GUILD_DESC("quest.side.thieves_guild.desc"),
    QUEST_SIDE_THIEVES_GUILD_ACCEPT("quest.side.thieves.guild.accept"),
    QUEST_SIDE_THIEVES_GUILD_DECLINE("quest.side.thieves.guild.decline"),
    QUEST_SIDE_THIEVES_GUILD_DIALOGS("quest.side.thieves.guild.dialogs"),
    QUEST_SIDE_THIEVES_GUILD_INFO("quest.side.thieves.guild.info"),
    QUEST_SIDE_THIEVES_GUILD_NPC_NAME("quest.side.thieves.guild.npc.name"),
    QUEST_SIDE_THIEVES_GUILD_OBJECTIVES_GUILD_CONTACT("quest.side.thieves.guild.objectives.guild.contact"),
    QUEST_SIDE_THIEVES_GUILD_OBJECTIVES_GUILD_MASTER("quest.side.thieves.guild.objectives.guild.master"),
    QUEST_SIDE_THIEVES_GUILD_OBJECTIVES_KILL_VINDICATORS("quest.side.thieves.guild.objectives.kill.vindicators"),
    QUEST_SIDE_THIEVES_GUILD_OBJECTIVES_LOCKPICKS("quest.side.thieves.guild.objectives.lockpicks"),
    QUEST_SIDE_THIEVES_GUILD_OBJECTIVES_RIVAL_HIDEOUT("quest.side.thieves.guild.objectives.rival.hideout"),
    QUEST_SIDE_THIEVES_GUILD_OBJECTIVES_SECRET_ENTRANCE("quest.side.thieves.guild.objectives.secret.entrance"),
    QUEST_SIDE_THIEVES_GUILD_OBJECTIVES_STOLEN_LEDGER("quest.side.thieves.guild.objectives.stolen.ledger"),
    QUEST_SIDE_THIEVES_GUILD_OBJECTIVES_BOOK_COLLECT("quest.side.thieves.guild.objectives.book.collect"),
    QUEST_SIDE_THIEVES_GUILD_OBJECTIVES_TRIPWIRE_HOOK_COLLECT("quest.side.thieves.guild.objectives.tripwire.hook.collect");

    private final String key;
    
    ThievesGuildLangKey(String key) {
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
