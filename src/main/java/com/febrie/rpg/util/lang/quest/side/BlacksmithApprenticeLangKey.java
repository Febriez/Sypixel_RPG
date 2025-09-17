package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for BlacksmithApprentice quests
 */
public enum BlacksmithApprenticeLangKey implements ILangKey {
    QUEST_SIDE_BLACKSMITH_APPRENTICE_NAME("quest.side.blacksmith_apprentice.name"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_DESC("quest.side.blacksmith_apprentice.desc"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_ACCEPT("quest.side.blacksmith.apprentice.accept"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_DECLINE("quest.side.blacksmith.apprentice.decline"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_DIALOGS("quest.side.blacksmith.apprentice.dialogs"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_INFO("quest.side.blacksmith.apprentice.info"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_NPC_NAME("quest.side.blacksmith.apprentice.npc.name"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_OBJECTIVES_COAL("quest.side.blacksmith.apprentice.objectives.coal"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_OBJECTIVES_IRON_ORE("quest.side.blacksmith.apprentice.objectives.iron.ore"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_OBJECTIVES_KILL_SKELETONS("quest.side.blacksmith.apprentice.objectives.kill.skeletons"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_OBJECTIVES_MINING_SITE("quest.side.blacksmith.apprentice.objectives.mining.site"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_OBJECTIVES_REFINED_IRON("quest.side.blacksmith.apprentice.objectives.refined.iron"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_OBJECTIVES_RETURN_MASTER_BLACKSMITH("quest.side.blacksmith.apprentice.objectives.return.master.blacksmith"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_OBJECTIVES_TALK_MASTER_BLACKSMITH("quest.side.blacksmith.apprentice.objectives.talk.master.blacksmith"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_OBJECTIVES_COAL_COLLECT("quest.side.blacksmith.apprentice.objectives.coal.collect"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_OBJECTIVES_IRON_INGOT_COLLECT("quest.side.blacksmith.apprentice.objectives.iron.ingot.collect"),
    QUEST_SIDE_BLACKSMITH_APPRENTICE_OBJECTIVES_IRON_ORE_COLLECT("quest.side.blacksmith.apprentice.objectives.iron.ore.collect");

    private final String key;
    
    BlacksmithApprenticeLangKey(String key) {
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
