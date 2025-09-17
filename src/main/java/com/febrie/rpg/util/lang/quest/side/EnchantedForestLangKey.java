package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for EnchantedForest quests
 */
public enum EnchantedForestLangKey implements ILangKey {
    QUEST_SIDE_ENCHANTED_FOREST_NAME("quest.side.enchanted_forest.name"),
    QUEST_SIDE_ENCHANTED_FOREST_DESC("quest.side.enchanted_forest.desc"),
    QUEST_SIDE_ENCHANTED_FOREST_ACCEPT("quest.side.enchanted.forest.accept"),
    QUEST_SIDE_ENCHANTED_FOREST_DECLINE("quest.side.enchanted.forest.decline"),
    QUEST_SIDE_ENCHANTED_FOREST_DIALOGS("quest.side.enchanted.forest.dialogs"),
    QUEST_SIDE_ENCHANTED_FOREST_INFO("quest.side.enchanted.forest.info"),
    QUEST_SIDE_ENCHANTED_FOREST_NPC_NAME("quest.side.enchanted.forest.npc.name"),
    QUEST_SIDE_ENCHANTED_FOREST_OBJECTIVES_ENCHANTED_SAPLINGS("quest.side.enchanted.forest.objectives.enchanted.saplings"),
    QUEST_SIDE_ENCHANTED_FOREST_OBJECTIVES_FAIRY_CIRCLE("quest.side.enchanted.forest.objectives.fairy.circle"),
    QUEST_SIDE_ENCHANTED_FOREST_OBJECTIVES_FAIRY_DUST("quest.side.enchanted.forest.objectives.fairy.dust"),
    QUEST_SIDE_ENCHANTED_FOREST_OBJECTIVES_KILL_WITCHES("quest.side.enchanted.forest.objectives.kill.witches"),
    QUEST_SIDE_ENCHANTED_FOREST_OBJECTIVES_MAGICAL_GROVE("quest.side.enchanted.forest.objectives.magical.grove"),
    QUEST_SIDE_ENCHANTED_FOREST_OBJECTIVES_TALK_FOREST_DRUID("quest.side.enchanted.forest.objectives.talk.forest.druid"),
    QUEST_SIDE_ENCHANTED_FOREST_OBJECTIVES_GLOWSTONE_DUST_COLLECT("quest.side.enchanted.forest.objectives.glowstone.dust.collect"),
    QUEST_SIDE_ENCHANTED_FOREST_OBJECTIVES_OAK_SAPLING_COLLECT("quest.side.enchanted.forest.objectives.oak.sapling.collect");

    private final String key;
    
    EnchantedForestLangKey(String key) {
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
