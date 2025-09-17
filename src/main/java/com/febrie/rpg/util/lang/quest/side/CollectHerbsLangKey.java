package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for CollectHerbs quests
 */
public enum CollectHerbsLangKey implements ILangKey {
    QUEST_SIDE_COLLECT_HERBS_NAME("quest.side.collect_herbs.name"),
    QUEST_SIDE_COLLECT_HERBS_DESC("quest.side.collect_herbs.desc"),
    QUEST_SIDE_COLLECT_HERBS_ACCEPT("quest.side.collect.herbs.accept"),
    QUEST_SIDE_COLLECT_HERBS_DECLINE("quest.side.collect.herbs.decline"),
    QUEST_SIDE_COLLECT_HERBS_DIALOGS("quest.side.collect.herbs.dialogs"),
    QUEST_SIDE_COLLECT_HERBS_INFO("quest.side.collect.herbs.info"),
    QUEST_SIDE_COLLECT_HERBS_NPC_NAME("quest.side.collect.herbs.npc.name"),
    QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_HEALING_HERBS("quest.side.collect.herbs.objectives.healing.herbs"),
    QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_HERB_MEADOW("quest.side.collect.herbs.objectives.herb.meadow"),
    QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_MOUNTAIN_HERBS("quest.side.collect.herbs.objectives.mountain.herbs"),
    QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_MOUNTAIN_SAGE("quest.side.collect.herbs.objectives.mountain.sage"),
    QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_RARE_FLOWERS("quest.side.collect.herbs.objectives.rare.flowers"),
    QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_RETURN_VILLAGE_HEALER("quest.side.collect.herbs.objectives.return.village.healer"),
    QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_TALK_VILLAGE_HEALER("quest.side.collect.herbs.objectives.talk.village.healer"),
    QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_FERN_COLLECT("quest.side.collect.herbs.objectives.fern.collect"),
    QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_POPPY_COLLECT("quest.side.collect.herbs.objectives.poppy.collect"),
    QUEST_SIDE_COLLECT_HERBS_OBJECTIVES_SWEET_BERRIES_COLLECT("quest.side.collect.herbs.objectives.sweet.berries.collect");

    private final String key;
    
    CollectHerbsLangKey(String key) {
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
