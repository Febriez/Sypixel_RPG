package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for AncientRuins quests
 */
public enum AncientRuinsLangKey implements ILangKey {
    QUEST_SIDE_ANCIENT_RUINS_NAME("quest.side.ancient_ruins.name"),
    QUEST_SIDE_ANCIENT_RUINS_DESC("quest.side.ancient_ruins.desc"),
    QUEST_SIDE_ANCIENT_RUINS_ACCEPT("quest.side.ancient.ruins.accept"),
    QUEST_SIDE_ANCIENT_RUINS_DECLINE("quest.side.ancient.ruins.decline"),
    QUEST_SIDE_ANCIENT_RUINS_DIALOGS("quest.side.ancient.ruins.dialogs"),
    QUEST_SIDE_ANCIENT_RUINS_INFO("quest.side.ancient.ruins.info"),
    QUEST_SIDE_ANCIENT_RUINS_NPC_NAME("quest.side.ancient.ruins.npc.name"),
    QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_ANCIENT_STONE("quest.side.ancient.ruins.objectives.ancient.stone"),
    QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_ARCHAEOLOGIST("quest.side.ancient.ruins.objectives.archaeologist"),
    QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_ARCHAEOLOGIST_COMPLETE("quest.side.ancient.ruins.objectives.archaeologist.complete"),
    QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_INNER_CHAMBER("quest.side.ancient.ruins.objectives.inner.chamber"),
    QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_KILL_SILVERFISH("quest.side.ancient.ruins.objectives.kill.silverfish"),
    QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_KILL_SPIDERS("quest.side.ancient.ruins.objectives.kill.spiders"),
    QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_RUINED_ENTRANCE("quest.side.ancient.ruins.objectives.ruined.entrance"),
    QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_RUNIC_TABLET("quest.side.ancient.ruins.objectives.runic.tablet"),
    QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_STONE_BRICKS_COLLECT("quest.side.ancient.ruins.objectives.stone.bricks.collect"),
    QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_STONE_COLLECT("quest.side.ancient.ruins.objectives.stone.collect");

    private final String key;
    
    AncientRuinsLangKey(String key) {
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
