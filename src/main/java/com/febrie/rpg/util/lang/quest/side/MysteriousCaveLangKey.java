package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for MysteriousCave quests
 */
public enum MysteriousCaveLangKey implements ILangKey {
    QUEST_SIDE_MYSTERIOUS_CAVE_NAME("quest.side.mysterious_cave.name"),
    QUEST_SIDE_MYSTERIOUS_CAVE_DESC("quest.side.mysterious_cave.desc"),
    QUEST_SIDE_MYSTERIOUS_CAVE_INFO("quest.side.mysterious.cave.info"),
    QUEST_SIDE_MYSTERIOUS_CAVE_ACCEPT("quest.side.mysterious.cave.accept"),
    QUEST_SIDE_MYSTERIOUS_CAVE_DECLINE("quest.side.mysterious.cave.decline"),
    QUEST_SIDE_MYSTERIOUS_CAVE_DIALOGS("quest.side.mysterious.cave.dialogs"),
    QUEST_SIDE_MYSTERIOUS_CAVE_NPC_NAME("quest.side.mysterious.cave.npc.name"),
    QUEST_SIDE_MYSTERIOUS_CAVE_OBJECTIVES_COLLECT_CAVE_PEARL("quest.side.mysterious.cave.objectives.collect.cave.pearl"),
    QUEST_SIDE_MYSTERIOUS_CAVE_OBJECTIVES_COLLECT_GLOWING_MOSS("quest.side.mysterious.cave.objectives.collect.glowing.moss"),
    QUEST_SIDE_MYSTERIOUS_CAVE_OBJECTIVES_KILL_BATS("quest.side.mysterious.cave.objectives.kill.bats"),
    QUEST_SIDE_MYSTERIOUS_CAVE_OBJECTIVES_TALK_CAVE_EXPLORER("quest.side.mysterious.cave.objectives.talk.cave.explorer"),
    QUEST_SIDE_MYSTERIOUS_CAVE_OBJECTIVES_VISIT_DARK_CAVE_ENTRANCE("quest.side.mysterious.cave.objectives.visit.dark.cave.entrance"),
    QUEST_SIDE_MYSTERIOUS_CAVE_OBJECTIVES_VISIT_UNDERGROUND_LAKE("quest.side.mysterious.cave.objectives.visit.underground.lake"),
    QUEST_SIDE_MYSTERIOUS_CAVE_OBJECTIVES_ENDER_PEARL_COLLECT("quest.side.mysterious.cave.objectives.ender.pearl.collect"),
    QUEST_SIDE_MYSTERIOUS_CAVE_OBJECTIVES_GLOW_LICHEN_COLLECT("quest.side.mysterious.cave.objectives.glow.lichen.collect");

    private final String key;
    
    MysteriousCaveLangKey(String key) {
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
