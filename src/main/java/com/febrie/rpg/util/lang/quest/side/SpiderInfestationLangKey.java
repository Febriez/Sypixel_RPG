package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for SpiderInfestation quests
 */
public enum SpiderInfestationLangKey implements ILangKey {
    QUEST_SIDE_SPIDER_INFESTATION_NAME("quest.side.spider_infestation.name"),
    QUEST_SIDE_SPIDER_INFESTATION_DESC("quest.side.spider_infestation.desc"),
    QUEST_SIDE_SPIDER_INFESTATION_ACCEPT("quest.side.spider.infestation.accept"),
    QUEST_SIDE_SPIDER_INFESTATION_DECLINE("quest.side.spider.infestation.decline"),
    QUEST_SIDE_SPIDER_INFESTATION_DIALOGS("quest.side.spider.infestation.dialogs"),
    QUEST_SIDE_SPIDER_INFESTATION_INFO("quest.side.spider.infestation.info"),
    QUEST_SIDE_SPIDER_INFESTATION_NPC_NAME("quest.side.spider.infestation.npc.name"),
    QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_DESTROY_NESTS("quest.side.spider.infestation.objectives.destroy.nests"),
    QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_GIANT_SPIDERS("quest.side.spider.infestation.objectives.giant.spiders"),
    QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_INFESTED_WAREHOUSE("quest.side.spider.infestation.objectives.infested.warehouse"),
    QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_QUEENS_CHAMBER("quest.side.spider.infestation.objectives.queens.chamber"),
    QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_REPORT_COMPLETION("quest.side.spider.infestation.objectives.report.completion"),
    QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_SPIDER_NESTS("quest.side.spider.infestation.objectives.spider.nests"),
    QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_SPIDER_QUEEN("quest.side.spider.infestation.objectives.spider.queen"),
    QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_SPIDER_SILK("quest.side.spider.infestation.objectives.spider.silk"),
    QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_TALK_WAREHOUSE_FOREMAN("quest.side.spider.infestation.objectives.talk.warehouse.foreman"),
    QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_COBWEB_COLLECT("quest.side.spider.infestation.objectives.cobweb.collect"),
    QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_STRING_COLLECT("quest.side.spider.infestation.objectives.string.collect");

    private final String key;
    
    SpiderInfestationLangKey(String key) {
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
