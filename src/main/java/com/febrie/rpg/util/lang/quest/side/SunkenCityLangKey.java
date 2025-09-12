package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for SunkenCity quests
 */
public enum SunkenCityLangKey implements ILangKey {
    QUEST_SIDE_SUNKEN_CITY_NAME("quest.side.sunken_city.name"),
    QUEST_SIDE_SUNKEN_CITY_DESC("quest.side.sunken_city.desc"),
    QUEST_SIDE_SUNKEN_CITY_ACCEPT("quest.side.sunken.city.accept"),
    QUEST_SIDE_SUNKEN_CITY_DECLINE("quest.side.sunken.city.decline"),
    QUEST_SIDE_SUNKEN_CITY_DIALOGS("quest.side.sunken.city.dialogs"),
    QUEST_SIDE_SUNKEN_CITY_INFO("quest.side.sunken.city.info"),
    QUEST_SIDE_SUNKEN_CITY_NPC_NAME("quest.side.sunken.city.npc.name"),
    QUEST_SIDE_SUNKEN_CITY_OBJECTIVES_ATLANTEAN_ARTIFACT("quest.side.sunken.city.objectives.atlantean.artifact"),
    QUEST_SIDE_SUNKEN_CITY_OBJECTIVES_DEEP_SEA_DIVER("quest.side.sunken.city.objectives.deep.sea.diver"),
    QUEST_SIDE_SUNKEN_CITY_OBJECTIVES_KILL_GUARDIANS("quest.side.sunken.city.objectives.kill.guardians"),
    QUEST_SIDE_SUNKEN_CITY_OBJECTIVES_SEA_CRYSTALS("quest.side.sunken.city.objectives.sea.crystals"),
    QUEST_SIDE_SUNKEN_CITY_OBJECTIVES_SUNKEN_PALACE("quest.side.sunken.city.objectives.sunken.palace"),
    QUEST_SIDE_SUNKEN_CITY_OBJECTIVES_UNDERWATER_RUINS("quest.side.sunken.city.objectives.underwater.ruins"),
    QUEST_SIDE_SUNKEN_CITY_OBJECTIVES_HEART_OF_THE_SEA_COLLECT("quest.side.sunken.city.objectives.heart.of.the.sea.collect"),
    QUEST_SIDE_SUNKEN_CITY_OBJECTIVES_PRISMARINE_CRYSTALS_COLLECT("quest.side.sunken.city.objectives.prismarine.crystals.collect");

    private final String key;
    
    SunkenCityLangKey(String key) {
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
