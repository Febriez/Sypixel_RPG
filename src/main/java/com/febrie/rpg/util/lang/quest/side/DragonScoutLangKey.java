package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for DragonScout quests
 */
public enum DragonScoutLangKey implements ILangKey {
    QUEST_SIDE_DRAGON_SCOUT_NAME("quest.side.dragon_scout.name"),
    QUEST_SIDE_DRAGON_SCOUT_DESC("quest.side.dragon_scout.desc"),
    QUEST_SIDE_DRAGON_SCOUT_ACCEPT("quest.side.dragon.scout.accept"),
    QUEST_SIDE_DRAGON_SCOUT_DECLINE("quest.side.dragon.scout.decline"),
    QUEST_SIDE_DRAGON_SCOUT_DIALOGS("quest.side.dragon.scout.dialogs"),
    QUEST_SIDE_DRAGON_SCOUT_INFO("quest.side.dragon.scout.info"),
    QUEST_SIDE_DRAGON_SCOUT_NPC_NAME("quest.side.dragon.scout.npc.name"),
    QUEST_SIDE_DRAGON_SCOUT_OBJECTIVES_DRAGON_BREATH_COLLECT("quest.side.dragon.scout.objectives.dragon.breath.collect"),
    QUEST_SIDE_DRAGON_SCOUT_OBJECTIVES_DRAGON_LAIR("quest.side.dragon.scout.objectives.dragon.lair"),
    QUEST_SIDE_DRAGON_SCOUT_OBJECTIVES_MOUNTAIN_PEAKS("quest.side.dragon.scout.objectives.mountain.peaks"),
    QUEST_SIDE_DRAGON_SCOUT_OBJECTIVES_OBSERVATION_POST("quest.side.dragon.scout.objectives.observation.post"),
    QUEST_SIDE_DRAGON_SCOUT_OBJECTIVES_REPORT_FINDINGS("quest.side.dragon.scout.objectives.report.findings"),
    QUEST_SIDE_DRAGON_SCOUT_OBJECTIVES_SCUTE_COLLECT("quest.side.dragon.scout.objectives.scute.collect"),
    QUEST_SIDE_DRAGON_SCOUT_OBJECTIVES_SPYGLASS_COLLECT("quest.side.dragon.scout.objectives.spyglass.collect"),
    QUEST_SIDE_DRAGON_SCOUT_OBJECTIVES_TALK_TOWN_GUARD("quest.side.dragon.scout.objectives.talk.town.guard");

    private final String key;
    
    DragonScoutLangKey(String key) {
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
