package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for DesertOasis quests
 */
public enum DesertOasisLangKey implements ILangKey {
    QUEST_SIDE_DESERT_OASIS_NAME("quest.side.desert_oasis.name"),
    QUEST_SIDE_DESERT_OASIS_DESC("quest.side.desert_oasis.desc"),
    QUEST_SIDE_DESERT_OASIS_ACCEPT("quest.side.desert.oasis.accept"),
    QUEST_SIDE_DESERT_OASIS_DECLINE("quest.side.desert.oasis.decline"),
    QUEST_SIDE_DESERT_OASIS_DIALOGS("quest.side.desert.oasis.dialogs"),
    QUEST_SIDE_DESERT_OASIS_INFO("quest.side.desert.oasis.info"),
    QUEST_SIDE_DESERT_OASIS_NPC_NAME("quest.side.desert.oasis.npc.name"),
    QUEST_SIDE_DESERT_OASIS_OBJECTIVES_DESERT_BLOOMS("quest.side.desert.oasis.objectives.desert.blooms"),
    QUEST_SIDE_DESERT_OASIS_OBJECTIVES_HIDDEN_OASIS("quest.side.desert.oasis.objectives.hidden.oasis"),
    QUEST_SIDE_DESERT_OASIS_OBJECTIVES_KILL_HUSKS("quest.side.desert.oasis.objectives.kill.husks"),
    QUEST_SIDE_DESERT_OASIS_OBJECTIVES_MIRAGES_EDGE("quest.side.desert.oasis.objectives.mirages.edge"),
    QUEST_SIDE_DESERT_OASIS_OBJECTIVES_OASIS_WATER("quest.side.desert.oasis.objectives.oasis.water"),
    QUEST_SIDE_DESERT_OASIS_OBJECTIVES_TALK_DESERT_NOMAD("quest.side.desert.oasis.objectives.talk.desert.nomad"),
    QUEST_SIDE_DESERT_OASIS_OBJECTIVES_CACTUS_COLLECT("quest.side.desert.oasis.objectives.cactus.collect"),
    QUEST_SIDE_DESERT_OASIS_OBJECTIVES_WATER_BUCKET_COLLECT("quest.side.desert.oasis.objectives.water.bucket.collect");

    private final String key;
    
    DesertOasisLangKey(String key) {
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
