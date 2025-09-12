package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for VolcanicDepths quests
 */
public enum VolcanicDepthsLangKey implements ILangKey {
    QUEST_SIDE_VOLCANIC_DEPTHS_NAME("quest.side.volcanic_depths.name"),
    QUEST_SIDE_VOLCANIC_DEPTHS_DESC("quest.side.volcanic_depths.desc"),
    QUEST_SIDE_VOLCANIC_DEPTHS_ACCEPT("quest.side.volcanic.depths.accept"),
    QUEST_SIDE_VOLCANIC_DEPTHS_DECLINE("quest.side.volcanic.depths.decline"),
    QUEST_SIDE_VOLCANIC_DEPTHS_DIALOGS("quest.side.volcanic.depths.dialogs"),
    QUEST_SIDE_VOLCANIC_DEPTHS_INFO("quest.side.volcanic.depths.info"),
    QUEST_SIDE_VOLCANIC_DEPTHS_NPC_NAME("quest.side.volcanic.depths.npc.name"),
    QUEST_SIDE_VOLCANIC_DEPTHS_OBJECTIVES_FIRE_ESSENCE("quest.side.volcanic.depths.objectives.fire.essence"),
    QUEST_SIDE_VOLCANIC_DEPTHS_OBJECTIVES_KILL_MAGMA_CUBES("quest.side.volcanic.depths.objectives.kill.magma.cubes"),
    QUEST_SIDE_VOLCANIC_DEPTHS_OBJECTIVES_LAVA_CHAMBER("quest.side.volcanic.depths.objectives.lava.chamber"),
    QUEST_SIDE_VOLCANIC_DEPTHS_OBJECTIVES_VOLCANIC_GLASS("quest.side.volcanic.depths.objectives.volcanic.glass"),
    QUEST_SIDE_VOLCANIC_DEPTHS_OBJECTIVES_VOLCANO_RESEARCHER("quest.side.volcanic.depths.objectives.volcano.researcher"),
    QUEST_SIDE_VOLCANIC_DEPTHS_OBJECTIVES_VOLCANO_RIM("quest.side.volcanic.depths.objectives.volcano.rim"),
    QUEST_SIDE_VOLCANIC_DEPTHS_OBJECTIVES_MAGMA_CREAM_COLLECT("quest.side.volcanic.depths.objectives.magma.cream.collect"),
    QUEST_SIDE_VOLCANIC_DEPTHS_OBJECTIVES_OBSIDIAN_COLLECT("quest.side.volcanic.depths.objectives.obsidian.collect");

    private final String key;
    
    VolcanicDepthsLangKey(String key) {
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
