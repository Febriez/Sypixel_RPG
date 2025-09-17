package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for MemoryThief quests
 */
public enum MemoryThiefLangKey implements ILangKey {
    QUEST_SIDE_MEMORY_THIEF_NAME("quest.side.memory_thief.name"),
    QUEST_SIDE_MEMORY_THIEF_DESC("quest.side.memory_thief.desc"),
    QUEST_SIDE_MEMORY_THIEF_ACCEPT("quest.side.memory.thief.accept"),
    QUEST_SIDE_MEMORY_THIEF_DECLINE("quest.side.memory.thief.decline"),
    QUEST_SIDE_MEMORY_THIEF_DIALOGS("quest.side.memory.thief.dialogs"),
    QUEST_SIDE_MEMORY_THIEF_INFO("quest.side.memory.thief.info"),
    QUEST_SIDE_MEMORY_THIEF_NPC_NAME("quest.side.memory.thief.npc.name"),
    QUEST_SIDE_MEMORY_THIEF_OBJECTIVES_EXPERIENCE_BOTTLE_COLLECT("quest.side.memory.thief.objectives.experience.bottle.collect"),
    QUEST_SIDE_MEMORY_THIEF_OBJECTIVES_INTERVIEW_VICTIMS("quest.side.memory.thief.objectives.interview.victims"),
    QUEST_SIDE_MEMORY_THIEF_OBJECTIVES_MEMORY_SHRINE("quest.side.memory.thief.objectives.memory.shrine"),
    QUEST_SIDE_MEMORY_THIEF_OBJECTIVES_MEMORY_THIEF("quest.side.memory.thief.objectives.memory.thief"),
    QUEST_SIDE_MEMORY_THIEF_OBJECTIVES_PHANTOM_MEMBRANE_COLLECT("quest.side.memory.thief.objectives.phantom.membrane.collect"),
    QUEST_SIDE_MEMORY_THIEF_OBJECTIVES_PRISMARINE_CRYSTALS_COLLECT("quest.side.memory.thief.objectives.prismarine.crystals.collect"),
    QUEST_SIDE_MEMORY_THIEF_OBJECTIVES_RESTORE_MEMORIES("quest.side.memory.thief.objectives.restore.memories"),
    QUEST_SIDE_MEMORY_THIEF_OBJECTIVES_TALK_CONCERNED_VILLAGER("quest.side.memory.thief.objectives.talk.concerned.villager"),
    QUEST_SIDE_MEMORY_THIEF_OBJECTIVES_THIEFS_LAIR("quest.side.memory.thief.objectives.thiefs.lair");

    private final String key;
    
    MemoryThiefLangKey(String key) {
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
