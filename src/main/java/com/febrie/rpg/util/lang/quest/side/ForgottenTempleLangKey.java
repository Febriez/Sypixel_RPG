package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for ForgottenTemple quests
 */
public enum ForgottenTempleLangKey implements ILangKey {
    QUEST_SIDE_FORGOTTEN_TEMPLE_NAME("quest.side.forgotten_temple.name"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_DESC("quest.side.forgotten_temple.desc"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_ACCEPT("quest.side.forgotten.temple.accept"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_DECLINE("quest.side.forgotten.temple.decline"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_DIALOGS("quest.side.forgotten.temple.dialogs"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_INFO("quest.side.forgotten.temple.info"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_NPC_NAME("quest.side.forgotten.temple.npc.name"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_OBJECTIVES_COLLECT_SACRED_RELIC("quest.side.forgotten.temple.objectives.collect.sacred.relic"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_OBJECTIVES_COLLECT_TEMPLE_KEY("quest.side.forgotten.temple.objectives.collect.temple.key"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_OBJECTIVES_KILL_ZOMBIES("quest.side.forgotten.temple.objectives.kill.zombies"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_OBJECTIVES_TALK_TEMPLE_SCHOLAR("quest.side.forgotten.temple.objectives.talk.temple.scholar"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_OBJECTIVES_VISIT_INNER_SANCTUM("quest.side.forgotten.temple.objectives.visit.inner.sanctum"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_OBJECTIVES_VISIT_TEMPLE_RUINS("quest.side.forgotten.temple.objectives.visit.temple.ruins"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_OBJECTIVES_GOLDEN_APPLE_COLLECT("quest.side.forgotten.temple.objectives.golden.apple.collect"),
    QUEST_SIDE_FORGOTTEN_TEMPLE_OBJECTIVES_GOLDEN_SWORD_COLLECT("quest.side.forgotten.temple.objectives.golden.sword.collect");

    private final String key;
    
    ForgottenTempleLangKey(String key) {
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
