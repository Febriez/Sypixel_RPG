package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for BeastTaming quests
 */
public enum BeastTamingLangKey implements ILangKey {
    QUEST_SIDE_BEAST_TAMING_NAME("quest.side.beast_taming.name"),
    QUEST_SIDE_BEAST_TAMING_DESC("quest.side.beast_taming.desc"),
    QUEST_SIDE_BEAST_TAMING_ACCEPT("quest.side.beast.taming.accept"),
    QUEST_SIDE_BEAST_TAMING_DECLINE("quest.side.beast.taming.decline"),
    QUEST_SIDE_BEAST_TAMING_DIALOGS("quest.side.beast.taming.dialogs"),
    QUEST_SIDE_BEAST_TAMING_INFO("quest.side.beast.taming.info"),
    QUEST_SIDE_BEAST_TAMING_NPC_NAME("quest.side.beast.taming.npc.name"),
    QUEST_SIDE_BEAST_TAMING_OBJECTIVES_BONE_COLLECT("quest.side.beast.taming.objectives.bone.collect"),
    QUEST_SIDE_BEAST_TAMING_OBJECTIVES_COMPASS_COLLECT("quest.side.beast.taming.objectives.compass.collect"),
    QUEST_SIDE_BEAST_TAMING_OBJECTIVES_OBSERVE_WOLVES("quest.side.beast.taming.objectives.observe.wolves"),
    QUEST_SIDE_BEAST_TAMING_OBJECTIVES_PACK_TERRITORY("quest.side.beast.taming.objectives.pack.territory"),
    QUEST_SIDE_BEAST_TAMING_OBJECTIVES_RETURN_BEAST_MASTER("quest.side.beast.taming.objectives.return.beast.master"),
    QUEST_SIDE_BEAST_TAMING_OBJECTIVES_TALK_BEAST_MASTER("quest.side.beast.taming.objectives.talk.beast.master"),
    QUEST_SIDE_BEAST_TAMING_OBJECTIVES_WHEAT_COLLECT("quest.side.beast.taming.objectives.wheat.collect"),
    QUEST_SIDE_BEAST_TAMING_OBJECTIVES_WILD_PLAINS("quest.side.beast.taming.objectives.wild.plains");

    private final String key;
    
    BeastTamingLangKey(String key) {
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
