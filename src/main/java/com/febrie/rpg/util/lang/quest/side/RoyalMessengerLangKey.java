package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for RoyalMessenger quests
 */
public enum RoyalMessengerLangKey implements ILangKey {
    QUEST_SIDE_ROYAL_MESSENGER_NAME("quest.side.royal_messenger.name"),
    QUEST_SIDE_ROYAL_MESSENGER_DESC("quest.side.royal_messenger.desc"),
    QUEST_SIDE_ROYAL_MESSENGER_ACCEPT("quest.side.royal.messenger.accept"),
    QUEST_SIDE_ROYAL_MESSENGER_DECLINE("quest.side.royal.messenger.decline"),
    QUEST_SIDE_ROYAL_MESSENGER_DIALOGS("quest.side.royal.messenger.dialogs"),
    QUEST_SIDE_ROYAL_MESSENGER_INFO("quest.side.royal.messenger.info"),
    QUEST_SIDE_ROYAL_MESSENGER_NPC_NAME("quest.side.royal.messenger.npc.name"),
    QUEST_SIDE_ROYAL_MESSENGER_OBJECTIVES_COLLECT_ROYAL_SEAL("quest.side.royal.messenger.objectives.collect.royal.seal"),
    QUEST_SIDE_ROYAL_MESSENGER_OBJECTIVES_COLLECT_URGENT_MESSAGE("quest.side.royal.messenger.objectives.collect.urgent.message"),
    QUEST_SIDE_ROYAL_MESSENGER_OBJECTIVES_KILL_BANDITS("quest.side.royal.messenger.objectives.kill.bandits"),
    QUEST_SIDE_ROYAL_MESSENGER_OBJECTIVES_TALK_CASTLE_GUARD("quest.side.royal.messenger.objectives.talk.castle.guard"),
    QUEST_SIDE_ROYAL_MESSENGER_OBJECTIVES_TALK_ROYAL_COURIER("quest.side.royal.messenger.objectives.talk.royal.courier"),
    QUEST_SIDE_ROYAL_MESSENGER_OBJECTIVES_VISIT_NORTHERN_OUTPOST("quest.side.royal.messenger.objectives.visit.northern.outpost"),
    QUEST_SIDE_ROYAL_MESSENGER_OBJECTIVES_VISIT_ROYAL_CASTLE("quest.side.royal.messenger.objectives.visit.royal.castle"),
    QUEST_SIDE_ROYAL_MESSENGER_OBJECTIVES_EMERALD_COLLECT("quest.side.royal.messenger.objectives.emerald.collect"),
    QUEST_SIDE_ROYAL_MESSENGER_OBJECTIVES_PAPER_COLLECT("quest.side.royal.messenger.objectives.paper.collect");

    private final String key;
    
    RoyalMessengerLangKey(String key) {
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
}
