package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for ElementalChaos quests
 */
public enum ElementalChaosLangKey implements ILangKey {
    QUEST_SIDE_ELEMENTAL_CHAOS_NAME("quest.side.elemental_chaos.name"),
    QUEST_SIDE_ELEMENTAL_CHAOS_DESC("quest.side.elemental_chaos.desc"),
    QUEST_SIDE_ELEMENTAL_CHAOS_ACCEPT("quest.side.elemental.chaos.accept"),
    QUEST_SIDE_ELEMENTAL_CHAOS_DECLINE("quest.side.elemental.chaos.decline"),
    QUEST_SIDE_ELEMENTAL_CHAOS_DIALOGS("quest.side.elemental.chaos.dialogs"),
    QUEST_SIDE_ELEMENTAL_CHAOS_INFO("quest.side.elemental.chaos.info"),
    QUEST_SIDE_ELEMENTAL_CHAOS_NPC_NAME("quest.side.elemental.chaos.npc.name"),
    QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_AIR_RIFT("quest.side.elemental.chaos.objectives.air.rift"),
    QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_BLAZE_ROD_COLLECT("quest.side.elemental.chaos.objectives.blaze.rod.collect"),
    QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_EARTH_RIFT("quest.side.elemental.chaos.objectives.earth.rift"),
    QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_ELEMENTAL_GUARDIANS("quest.side.elemental.chaos.objectives.elemental.guardians"),
    QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_EMERALD_COLLECT("quest.side.elemental.chaos.objectives.emerald.collect"),
    QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_FEATHER_COLLECT("quest.side.elemental.chaos.objectives.feather.collect"),
    QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_FIRE_RIFT("quest.side.elemental.chaos.objectives.fire.rift"),
    QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_PRISMARINE_SHARD_COLLECT("quest.side.elemental.chaos.objectives.prismarine.shard.collect"),
    QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_RESTORE_BALANCE("quest.side.elemental.chaos.objectives.restore.balance"),
    QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_TALK_ELEMENTAL_SAGE("quest.side.elemental.chaos.objectives.talk.elemental.sage"),
    QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_WATER_RIFT("quest.side.elemental.chaos.objectives.water.rift");

    private final String key;
    
    ElementalChaosLangKey(String key) {
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
