package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for EternalFlame quests
 */
public enum EternalFlameLangKey implements ILangKey {
    QUEST_SIDE_ETERNAL_FLAME_NAME("quest.side.eternal_flame.name"),
    QUEST_SIDE_ETERNAL_FLAME_DESC("quest.side.eternal_flame.desc"),
    QUEST_SIDE_ETERNAL_FLAME_ACCEPT("quest.side.eternal.flame.accept"),
    QUEST_SIDE_ETERNAL_FLAME_DECLINE("quest.side.eternal.flame.decline"),
    QUEST_SIDE_ETERNAL_FLAME_DIALOGS("quest.side.eternal.flame.dialogs"),
    QUEST_SIDE_ETERNAL_FLAME_INFO("quest.side.eternal.flame.info"),
    QUEST_SIDE_ETERNAL_FLAME_NPC_NAME("quest.side.eternal.flame.npc.name"),
    QUEST_SIDE_ETERNAL_FLAME_OBJECTIVES_ANCIENT_BRAZIER("quest.side.eternal.flame.objectives.ancient.brazier"),
    QUEST_SIDE_ETERNAL_FLAME_OBJECTIVES_FIRE_CHARGE_COLLECT("quest.side.eternal.flame.objectives.fire.charge.collect"),
    QUEST_SIDE_ETERNAL_FLAME_OBJECTIVES_FLAME_ALTAR("quest.side.eternal.flame.objectives.flame.altar"),
    QUEST_SIDE_ETERNAL_FLAME_OBJECTIVES_GLOWSTONE_DUST_COLLECT("quest.side.eternal.flame.objectives.glowstone.dust.collect"),
    QUEST_SIDE_ETERNAL_FLAME_OBJECTIVES_HONEY_BOTTLE_COLLECT("quest.side.eternal.flame.objectives.honey.bottle.collect"),
    QUEST_SIDE_ETERNAL_FLAME_OBJECTIVES_REKINDLE_FLAME("quest.side.eternal.flame.objectives.rekindle.flame"),
    QUEST_SIDE_ETERNAL_FLAME_OBJECTIVES_STARLIGHT_PEAK("quest.side.eternal.flame.objectives.starlight.peak"),
    QUEST_SIDE_ETERNAL_FLAME_OBJECTIVES_TALK_FLAME_KEEPER("quest.side.eternal.flame.objectives.talk.flame.keeper");

    private final String key;
    
    EternalFlameLangKey(String key) {
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
