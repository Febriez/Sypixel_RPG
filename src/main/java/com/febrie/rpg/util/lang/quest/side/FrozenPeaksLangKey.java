package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for FrozenPeaks quests
 */
public enum FrozenPeaksLangKey implements ILangKey {
    QUEST_SIDE_FROZEN_PEAKS_NAME("quest.side.frozen_peaks.name"),
    QUEST_SIDE_FROZEN_PEAKS_DESC("quest.side.frozen_peaks.desc"),
    QUEST_SIDE_FROZEN_PEAKS_ACCEPT("quest.side.frozen.peaks.accept"),
    QUEST_SIDE_FROZEN_PEAKS_DECLINE("quest.side.frozen.peaks.decline"),
    QUEST_SIDE_FROZEN_PEAKS_DIALOGS("quest.side.frozen.peaks.dialogs"),
    QUEST_SIDE_FROZEN_PEAKS_INFO("quest.side.frozen.peaks.info"),
    QUEST_SIDE_FROZEN_PEAKS_NPC_NAME("quest.side.frozen.peaks.npc.name"),
    QUEST_SIDE_FROZEN_PEAKS_OBJECTIVES_COLLECT_ETERNAL_ICE("quest.side.frozen.peaks.objectives.collect.eternal.ice"),
    QUEST_SIDE_FROZEN_PEAKS_OBJECTIVES_COLLECT_ICE_SHARDS("quest.side.frozen.peaks.objectives.collect.ice.shards"),
    QUEST_SIDE_FROZEN_PEAKS_OBJECTIVES_KILL_POLAR_BEARS("quest.side.frozen.peaks.objectives.kill.polar.bears"),
    QUEST_SIDE_FROZEN_PEAKS_OBJECTIVES_TALK_MOUNTAIN_CLIMBER("quest.side.frozen.peaks.objectives.talk.mountain.climber"),
    QUEST_SIDE_FROZEN_PEAKS_OBJECTIVES_VISIT_FROZEN_SUMMIT("quest.side.frozen.peaks.objectives.visit.frozen.summit"),
    QUEST_SIDE_FROZEN_PEAKS_OBJECTIVES_VISIT_ICE_CLIFFS("quest.side.frozen.peaks.objectives.visit.ice.cliffs"),
    QUEST_SIDE_FROZEN_PEAKS_OBJECTIVES_ICE_COLLECT("quest.side.frozen.peaks.objectives.ice.collect"),
    QUEST_SIDE_FROZEN_PEAKS_OBJECTIVES_PACKED_ICE_COLLECT("quest.side.frozen.peaks.objectives.packed.ice.collect");

    private final String key;
    
    FrozenPeaksLangKey(String key) {
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
