package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for GoblinRaiders quests
 */
public enum GoblinRaidersLangKey implements ILangKey {
    QUEST_SIDE_GOBLIN_RAIDERS_NAME("quest.side.goblin_raiders.name"),
    QUEST_SIDE_GOBLIN_RAIDERS_DESC("quest.side.goblin_raiders.desc"),
    QUEST_SIDE_GOBLIN_RAIDERS_DIALOGS("quest.side.goblin.raiders.dialogs"),
    QUEST_SIDE_GOBLIN_RAIDERS_INFO("quest.side.goblin.raiders.info"),
    QUEST_SIDE_GOBLIN_RAIDERS_NPC_NAME("quest.side.goblin.raiders.npc.name"),
    QUEST_SIDE_GOBLIN_RAIDERS_ACCEPT("quest.side.goblin.raiders.accept"),
    QUEST_SIDE_GOBLIN_RAIDERS_DECLINE("quest.side.goblin.raiders.decline"),
    QUEST_SIDE_GOBLIN_RAIDERS_OBJECTIVES_GOBLIN_CAMPS("quest.side.goblin.raiders.objectives.goblin.camps"),
    QUEST_SIDE_GOBLIN_RAIDERS_OBJECTIVES_GOBLIN_CHIEFTAIN("quest.side.goblin.raiders.objectives.goblin.chieftain"),
    QUEST_SIDE_GOBLIN_RAIDERS_OBJECTIVES_GOBLIN_SCOUTS("quest.side.goblin.raiders.objectives.goblin.scouts"),
    QUEST_SIDE_GOBLIN_RAIDERS_OBJECTIVES_GOBLIN_STRONGHOLD("quest.side.goblin.raiders.objectives.goblin.stronghold"),
    QUEST_SIDE_GOBLIN_RAIDERS_OBJECTIVES_GOLD_INGOT_COLLECT("quest.side.goblin.raiders.objectives.gold.ingot.collect"),
    QUEST_SIDE_GOBLIN_RAIDERS_OBJECTIVES_REPORT_SUCCESS("quest.side.goblin.raiders.objectives.report.success"),
    QUEST_SIDE_GOBLIN_RAIDERS_OBJECTIVES_RESCUE_MERCHANTS("quest.side.goblin.raiders.objectives.rescue.merchants"),
    QUEST_SIDE_GOBLIN_RAIDERS_OBJECTIVES_TALK_MERCHANT_LEADER("quest.side.goblin.raiders.objectives.talk.merchant.leader");

    private final String key;
    
    GoblinRaidersLangKey(String key) {
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
