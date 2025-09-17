package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for TimeParadox quests
 */
public enum TimeParadoxLangKey implements ILangKey {
    QUEST_SIDE_TIME_PARADOX_NAME("quest.side.time_paradox.name"),
    QUEST_SIDE_TIME_PARADOX_DESC("quest.side.time_paradox.desc"),
    QUEST_SIDE_TIME_PARADOX_ACCEPT("quest.side.time.paradox.accept"),
    QUEST_SIDE_TIME_PARADOX_DECLINE("quest.side.time.paradox.decline"),
    QUEST_SIDE_TIME_PARADOX_DIALOGS("quest.side.time.paradox.dialogs"),
    QUEST_SIDE_TIME_PARADOX_INFO("quest.side.time.paradox.info"),
    QUEST_SIDE_TIME_PARADOX_NPC_NAME("quest.side.time.paradox.npc.name"),
    QUEST_SIDE_TIME_PARADOX_OBJECTIVES_CHRONOS_ESSENCE("quest.side.time.paradox.objectives.chronos.essence"),
    QUEST_SIDE_TIME_PARADOX_OBJECTIVES_STABILIZE_TIMELINE("quest.side.time.paradox.objectives.stabilize.timeline"),
    QUEST_SIDE_TIME_PARADOX_OBJECTIVES_TALK_CHRONO_MAGE("quest.side.time.paradox.objectives.talk.chrono.mage"),
    QUEST_SIDE_TIME_PARADOX_OBJECTIVES_TEMPORAL_FRAGMENTS("quest.side.time.paradox.objectives.temporal.fragments"),
    QUEST_SIDE_TIME_PARADOX_OBJECTIVES_TIME_CRYSTALS("quest.side.time.paradox.objectives.time.crystals"),
    QUEST_SIDE_TIME_PARADOX_OBJECTIVES_TIME_NEXUS("quest.side.time.paradox.objectives.time.nexus"),
    QUEST_SIDE_TIME_PARADOX_OBJECTIVES_AMETHYST_SHARD_COLLECT("quest.side.time.paradox.objectives.amethyst.shard.collect"),
    QUEST_SIDE_TIME_PARADOX_OBJECTIVES_CLOCK_COLLECT("quest.side.time.paradox.objectives.clock.collect"),
    QUEST_SIDE_TIME_PARADOX_OBJECTIVES_EXPERIENCE_BOTTLE_COLLECT("quest.side.time.paradox.objectives.experience.bottle.collect");

    private final String key;
    
    TimeParadoxLangKey(String key) {
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
