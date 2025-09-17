package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for HealersRequest quests
 */
public enum HealersRequestLangKey implements ILangKey {
    QUEST_SIDE_HEALERS_REQUEST_NAME("quest.side.healers_request.name"),
    QUEST_SIDE_HEALERS_REQUEST_DESC("quest.side.healers_request.desc"),
    QUEST_SIDE_HEALERS_REQUEST_ACCEPT("quest.side.healers.request.accept"),
    QUEST_SIDE_HEALERS_REQUEST_DECLINE("quest.side.healers.request.decline"),
    QUEST_SIDE_HEALERS_REQUEST_DIALOGS("quest.side.healers.request.dialogs"),
    QUEST_SIDE_HEALERS_REQUEST_INFO("quest.side.healers.request.info"),
    QUEST_SIDE_HEALERS_REQUEST_NPC_NAME("quest.side.healers.request.npc.name"),
    QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_COLLECT_GHAST_TEARS("quest.side.healers.request.objectives.collect.ghast.tears"),
    QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_COLLECT_HOLY_WATER("quest.side.healers.request.objectives.collect.holy.water"),
    QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_COLLECT_MEDICINAL_HERBS("quest.side.healers.request.objectives.collect.medicinal.herbs"),
    QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_COLLECT_SPIDER_EYES("quest.side.healers.request.objectives.collect.spider.eyes"),
    QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_TALK_VILLAGE_HEALER("quest.side.healers.request.objectives.talk.village.healer"),
    QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_VISIT_HERB_GARDEN("quest.side.healers.request.objectives.visit.herb.garden"),
    QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_VISIT_SACRED_SPRING("quest.side.healers.request.objectives.visit.sacred.spring"),
    QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_GHAST_TEAR_COLLECT("quest.side.healers.request.objectives.ghast.tear.collect"),
    QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_POTION_COLLECT("quest.side.healers.request.objectives.potion.collect"),
    QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_SPIDER_EYE_COLLECT("quest.side.healers.request.objectives.spider.eye.collect"),
    QUEST_SIDE_HEALERS_REQUEST_OBJECTIVES_SWEET_BERRIES_COLLECT("quest.side.healers.request.objectives.sweet.berries.collect");

    private final String key;
    
    HealersRequestLangKey(String key) {
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
