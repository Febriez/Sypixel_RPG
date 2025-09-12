package com.febrie.rpg.util.lang.quest.exploration;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for ExplorationQuest quests
 */
public enum ExplorationQuestLangKey implements ILangKey {
    QUEST_EXPLORATION_ANCIENT_RUINS_NAME("quest.exploration.ancient_ruins.name"),
    QUEST_EXPLORATION_ANCIENT_RUINS_DESC("quest.exploration.ancient_ruins.desc"),
    QUEST_EXPLORATION_LOST_CONTINENT_NAME("quest.exploration.lost_continent.name"),
    QUEST_EXPLORATION_LOST_CONTINENT_DESC("quest.exploration.lost_continent.desc"),
    QUEST_EXPLORATION_SKY_ISLANDS_NAME("quest.exploration.sky_islands.name"),
    QUEST_EXPLORATION_SKY_ISLANDS_DESC("quest.exploration.sky_islands.desc"),
    QUEST_EXPLORATION_ANCIENT_RUINS_ACCEPT("quest.exploration.ancient.ruins.accept"),
    QUEST_EXPLORATION_ANCIENT_RUINS_DECLINE("quest.exploration.ancient.ruins.decline"),
    QUEST_EXPLORATION_ANCIENT_RUINS_DIALOGS("quest.exploration.ancient.ruins.dialogs"),
    QUEST_EXPLORATION_ANCIENT_RUINS_INFO("quest.exploration.ancient.ruins.info"),
    QUEST_EXPLORATION_ANCIENT_RUINS_NPC_NAME("quest.exploration.ancient.ruins.npc.name"),
    QUEST_EXPLORATION_ANCIENT_RUINS_OBJECTIVES_BREAK_ANCIENT_BLOCKS("quest.exploration.ancient.ruins.objectives.break.ancient.blocks"),
    QUEST_EXPLORATION_ANCIENT_RUINS_OBJECTIVES_COLLECT_ARTIFACTS("quest.exploration.ancient.ruins.objectives.collect.artifacts"),
    QUEST_EXPLORATION_ANCIENT_RUINS_OBJECTIVES_EXPLORE_DESERT_RUINS("quest.exploration.ancient.ruins.objectives.explore.desert.ruins"),
    QUEST_EXPLORATION_ANCIENT_RUINS_OBJECTIVES_EXPLORE_UNDERGROUND_RUINS("quest.exploration.ancient.ruins.objectives.explore.underground.ruins"),
    QUEST_EXPLORATION_ANCIENT_RUINS_OBJECTIVES_TALK_TO_ARCHAEOLOGIST("quest.exploration.ancient.ruins.objectives.talk.to.archaeologist"),
    QUEST_EXPLORATION_LOST_CONTINENT_ACCEPT("quest.exploration.lost.continent.accept"),
    QUEST_EXPLORATION_LOST_CONTINENT_DECLINE("quest.exploration.lost.continent.decline"),
    QUEST_EXPLORATION_LOST_CONTINENT_DIALOGS("quest.exploration.lost.continent.dialogs"),
    QUEST_EXPLORATION_LOST_CONTINENT_INFO("quest.exploration.lost.continent.info"),
    QUEST_EXPLORATION_LOST_CONTINENT_NPC_NAME("quest.exploration.lost.continent.npc.name"),
    QUEST_EXPLORATION_LOST_CONTINENT_OBJECTIVES_BREAK_OCEAN_BLOCKS("quest.exploration.lost.continent.objectives.break.ocean.blocks"),
    QUEST_EXPLORATION_LOST_CONTINENT_OBJECTIVES_BUILD_EXPEDITION_CAMP("quest.exploration.lost.continent.objectives.build.expedition.camp"),
    QUEST_EXPLORATION_LOST_CONTINENT_OBJECTIVES_COLLECT_EXOTIC_MATERIALS("quest.exploration.lost.continent.objectives.collect.exotic.materials"),
    QUEST_EXPLORATION_LOST_CONTINENT_OBJECTIVES_DEFEAT_CONTINENT_GUARDIANS("quest.exploration.lost.continent.objectives.defeat.continent.guardians"),
    QUEST_EXPLORATION_LOST_CONTINENT_OBJECTIVES_EXPLORE_MYSTERIOUS_LANDS("quest.exploration.lost.continent.objectives.explore.mysterious.lands"),
    QUEST_EXPLORATION_SKY_ISLANDS_ACCEPT("quest.exploration.sky.islands.accept"),
    QUEST_EXPLORATION_SKY_ISLANDS_DECLINE("quest.exploration.sky.islands.decline"),
    QUEST_EXPLORATION_SKY_ISLANDS_DIALOGS("quest.exploration.sky.islands.dialogs"),
    QUEST_EXPLORATION_SKY_ISLANDS_INFO("quest.exploration.sky.islands.info"),
    QUEST_EXPLORATION_SKY_ISLANDS_NPC_NAME("quest.exploration.sky.islands.npc.name"),
    QUEST_EXPLORATION_SKY_ISLANDS_OBJECTIVES_BUILD_SKY_BRIDGE("quest.exploration.sky.islands.objectives.build.sky.bridge"),
    QUEST_EXPLORATION_SKY_ISLANDS_OBJECTIVES_COLLECT_SKY_CRYSTALS("quest.exploration.sky.islands.objectives.collect.sky.crystals"),
    QUEST_EXPLORATION_SKY_ISLANDS_OBJECTIVES_DEFEAT_SKY_GUARDIANS("quest.exploration.sky.islands.objectives.defeat.sky.guardians"),
    QUEST_EXPLORATION_SKY_ISLANDS_OBJECTIVES_EXPLORE_FLOATING_ISLANDS("quest.exploration.sky.islands.objectives.explore.floating.islands"),
    QUEST_EXPLORATION_SKY_ISLANDS_OBJECTIVES_REACH_SKY_HEIGHT("quest.exploration.sky.islands.objectives.reach.sky.height");

    private final String key;
    
    ExplorationQuestLangKey(String key) {
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
