package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for MinersPlight quests
 */
public enum MinersPlightLangKey implements ILangKey {
    QUEST_SIDE_MINERS_PLIGHT_NAME("quest.side.miners_plight.name"),
    QUEST_SIDE_MINERS_PLIGHT_DESC("quest.side.miners_plight.desc"),
    QUEST_SIDE_MINERS_PLIGHT_ACCEPT("quest.side.miners.plight.accept"),
    QUEST_SIDE_MINERS_PLIGHT_DECLINE("quest.side.miners.plight.decline"),
    QUEST_SIDE_MINERS_PLIGHT_DIALOGS("quest.side.miners.plight.dialogs"),
    QUEST_SIDE_MINERS_PLIGHT_INFO("quest.side.miners.plight.info"),
    QUEST_SIDE_MINERS_PLIGHT_NPC_NAME("quest.side.miners.plight.npc.name"),
    QUEST_SIDE_MINERS_PLIGHT_OBJECTIVES_COLLECT_MINING_EQUIPMENT("quest.side.miners.plight.objectives.collect.mining.equipment"),
    QUEST_SIDE_MINERS_PLIGHT_OBJECTIVES_COLLECT_SUPPORT_BEAMS("quest.side.miners.plight.objectives.collect.support.beams"),
    QUEST_SIDE_MINERS_PLIGHT_OBJECTIVES_KILL_CAVE_SPIDERS("quest.side.miners.plight.objectives.kill.cave.spiders"),
    QUEST_SIDE_MINERS_PLIGHT_OBJECTIVES_RETURN_MINE_FOREMAN("quest.side.miners.plight.objectives.return.mine.foreman"),
    QUEST_SIDE_MINERS_PLIGHT_OBJECTIVES_TALK_MINE_FOREMAN("quest.side.miners.plight.objectives.talk.mine.foreman"),
    QUEST_SIDE_MINERS_PLIGHT_OBJECTIVES_VISIT_COLLAPSED_MINE("quest.side.miners.plight.objectives.visit.collapsed.mine"),
    QUEST_SIDE_MINERS_PLIGHT_OBJECTIVES_VISIT_TRAPPED_MINERS("quest.side.miners.plight.objectives.visit.trapped.miners"),
    QUEST_SIDE_MINERS_PLIGHT_OBJECTIVES_IRON_PICKAXE_COLLECT("quest.side.miners.plight.objectives.iron.pickaxe.collect"),
    QUEST_SIDE_MINERS_PLIGHT_OBJECTIVES_OAK_LOG_COLLECT("quest.side.miners.plight.objectives.oak.log.collect");

    private final String key;
    
    MinersPlightLangKey(String key) {
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
