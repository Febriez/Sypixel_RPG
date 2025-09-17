package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for LostTreasure quests
 */
public enum LostTreasureLangKey implements ILangKey {
    QUEST_SIDE_LOST_TREASURE_NAME("quest.side.lost_treasure.name"),
    QUEST_SIDE_LOST_TREASURE_DESC("quest.side.lost_treasure.desc"),
    QUEST_SIDE_LOST_TREASURE_ACCEPT("quest.side.lost.treasure.accept"),
    QUEST_SIDE_LOST_TREASURE_DECLINE("quest.side.lost.treasure.decline"),
    QUEST_SIDE_LOST_TREASURE_DIALOGS("quest.side.lost.treasure.dialogs"),
    QUEST_SIDE_LOST_TREASURE_INFO("quest.side.lost.treasure.info"),
    QUEST_SIDE_LOST_TREASURE_NPC_NAME("quest.side.lost.treasure.npc.name"),
    QUEST_SIDE_LOST_TREASURE_OBJECTIVES_COLLECT_ANCIENT_ARTIFACT("quest.side.lost.treasure.objectives.collect.ancient.artifact"),
    QUEST_SIDE_LOST_TREASURE_OBJECTIVES_COLLECT_GOLD_COINS("quest.side.lost.treasure.objectives.collect.gold.coins"),
    QUEST_SIDE_LOST_TREASURE_OBJECTIVES_COLLECT_TREASURE_MAP("quest.side.lost.treasure.objectives.collect.treasure.map"),
    QUEST_SIDE_LOST_TREASURE_OBJECTIVES_KILL_SKELETONS("quest.side.lost.treasure.objectives.kill.skeletons"),
    QUEST_SIDE_LOST_TREASURE_OBJECTIVES_RETURN_OLD_SAILOR("quest.side.lost.treasure.objectives.return.old.sailor"),
    QUEST_SIDE_LOST_TREASURE_OBJECTIVES_TALK_OLD_SAILOR("quest.side.lost.treasure.objectives.talk.old.sailor"),
    QUEST_SIDE_LOST_TREASURE_OBJECTIVES_VISIT_BURIED_TREASURE("quest.side.lost.treasure.objectives.visit.buried.treasure"),
    QUEST_SIDE_LOST_TREASURE_OBJECTIVES_VISIT_CURSED_COVE("quest.side.lost.treasure.objectives.visit.cursed.cove"),
    QUEST_SIDE_LOST_TREASURE_OBJECTIVES_GOLDEN_APPLE_COLLECT("quest.side.lost.treasure.objectives.golden.apple.collect"),
    QUEST_SIDE_LOST_TREASURE_OBJECTIVES_GOLD_NUGGET_COLLECT("quest.side.lost.treasure.objectives.gold.nugget.collect"),
    QUEST_SIDE_LOST_TREASURE_OBJECTIVES_MAP_COLLECT("quest.side.lost.treasure.objectives.map.collect");

    private final String key;
    
    LostTreasureLangKey(String key) {
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
