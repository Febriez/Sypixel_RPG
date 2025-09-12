package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for FarmersRequest quests
 */
public enum FarmersRequestLangKey implements ILangKey {
    QUEST_SIDE_FARMERS_REQUEST_NAME("quest.side.farmers_request.name"),
    QUEST_SIDE_FARMERS_REQUEST_DESC("quest.side.farmers_request.desc"),
    QUEST_SIDE_FARMERS_REQUEST_ACCEPT("quest.side.farmers.request.accept"),
    QUEST_SIDE_FARMERS_REQUEST_DECLINE("quest.side.farmers.request.decline"),
    QUEST_SIDE_FARMERS_REQUEST_DIALOGS("quest.side.farmers.request.dialogs"),
    QUEST_SIDE_FARMERS_REQUEST_INFO("quest.side.farmers.request.info"),
    QUEST_SIDE_FARMERS_REQUEST_NPC_NAME("quest.side.farmers.request.npc.name"),
    QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_COLLECT_BONE_MEAL("quest.side.farmers.request.objectives.collect.bone.meal"),
    QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_COLLECT_FRESH_SEEDS("quest.side.farmers.request.objectives.collect.fresh.seeds"),
    QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_KILL_RABBITS("quest.side.farmers.request.objectives.kill.rabbits"),
    QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_RETURN_WORRIED_FARMER("quest.side.farmers.request.objectives.return.worried.farmer"),
    QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_TALK_WORRIED_FARMER("quest.side.farmers.request.objectives.talk.worried.farmer"),
    QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_VISIT_DAMAGED_FARMLAND("quest.side.farmers.request.objectives.visit.damaged.farmland"),
    QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_VISIT_IRRIGATION_CANAL("quest.side.farmers.request.objectives.visit.irrigation.canal"),
    QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_BONE_MEAL_COLLECT("quest.side.farmers.request.objectives.bone.meal.collect"),
    QUEST_SIDE_FARMERS_REQUEST_OBJECTIVES_WHEAT_SEEDS_COLLECT("quest.side.farmers.request.objectives.wheat.seeds.collect");

    private final String key;
    
    FarmersRequestLangKey(String key) {
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
