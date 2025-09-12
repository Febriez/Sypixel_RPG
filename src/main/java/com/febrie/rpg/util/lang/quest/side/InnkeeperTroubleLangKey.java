package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for InnkeeperTrouble quests
 */
public enum InnkeeperTroubleLangKey implements ILangKey {
    QUEST_SIDE_INNKEEPER_TROUBLE_NAME("quest.side.innkeeper_trouble.name"),
    QUEST_SIDE_INNKEEPER_TROUBLE_DESC("quest.side.innkeeper_trouble.desc"),
    QUEST_SIDE_INNKEEPER_TROUBLE_ACCEPT("quest.side.innkeeper.trouble.accept"),
    QUEST_SIDE_INNKEEPER_TROUBLE_DECLINE("quest.side.innkeeper.trouble.decline"),
    QUEST_SIDE_INNKEEPER_TROUBLE_DIALOGS("quest.side.innkeeper.trouble.dialogs"),
    QUEST_SIDE_INNKEEPER_TROUBLE_INFO("quest.side.innkeeper.trouble.info"),
    QUEST_SIDE_INNKEEPER_TROUBLE_NPC_NAME("quest.side.innkeeper.trouble.npc.name"),
    QUEST_SIDE_INNKEEPER_TROUBLE_OBJECTIVES_COLLECT_ALE_BARRELS("quest.side.innkeeper.trouble.objectives.collect.ale.barrels"),
    QUEST_SIDE_INNKEEPER_TROUBLE_OBJECTIVES_COLLECT_INN_SUPPLIES("quest.side.innkeeper.trouble.objectives.collect.inn.supplies"),
    QUEST_SIDE_INNKEEPER_TROUBLE_OBJECTIVES_KILL_SPIDERS("quest.side.innkeeper.trouble.objectives.kill.spiders"),
    QUEST_SIDE_INNKEEPER_TROUBLE_OBJECTIVES_RETURN_WORRIED_INNKEEPER("quest.side.innkeeper.trouble.objectives.return.worried.innkeeper"),
    QUEST_SIDE_INNKEEPER_TROUBLE_OBJECTIVES_TALK_WORRIED_INNKEEPER("quest.side.innkeeper.trouble.objectives.talk.worried.innkeeper"),
    QUEST_SIDE_INNKEEPER_TROUBLE_OBJECTIVES_VISIT_INN_BASEMENT("quest.side.innkeeper.trouble.objectives.visit.inn.basement"),
    QUEST_SIDE_INNKEEPER_TROUBLE_OBJECTIVES_VISIT_STORAGE_ROOM("quest.side.innkeeper.trouble.objectives.visit.storage.room"),
    QUEST_SIDE_INNKEEPER_TROUBLE_OBJECTIVES_BARREL_COLLECT("quest.side.innkeeper.trouble.objectives.barrel.collect"),
    QUEST_SIDE_INNKEEPER_TROUBLE_OBJECTIVES_BREAD_COLLECT("quest.side.innkeeper.trouble.objectives.bread.collect");

    private final String key;
    
    InnkeeperTroubleLangKey(String key) {
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
