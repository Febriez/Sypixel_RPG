package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for DreamWalker quests
 */
public enum DreamWalkerLangKey implements ILangKey {
    QUEST_SIDE_DREAM_WALKER_NAME("quest.side.dream_walker.name"),
    QUEST_SIDE_DREAM_WALKER_DESC("quest.side.dream_walker.desc"),
    QUEST_SIDE_DREAM_WALKER_ACCEPT("quest.side.dream.walker.accept"),
    QUEST_SIDE_DREAM_WALKER_DECLINE("quest.side.dream.walker.decline"),
    QUEST_SIDE_DREAM_WALKER_DIALOGS("quest.side.dream.walker.dialogs"),
    QUEST_SIDE_DREAM_WALKER_INFO("quest.side.dream.walker.info"),
    QUEST_SIDE_DREAM_WALKER_NPC_NAME("quest.side.dream.walker.npc.name"),
    QUEST_SIDE_DREAM_WALKER_OBJECTIVES_AWAKEN_VILLAGERS("quest.side.dream.walker.objectives.awaken.villagers"),
    QUEST_SIDE_DREAM_WALKER_OBJECTIVES_DREAM_REALM("quest.side.dream.walker.objectives.dream.realm"),
    QUEST_SIDE_DREAM_WALKER_OBJECTIVES_NIGHTMARE_LORD("quest.side.dream.walker.objectives.nightmare.lord"),
    QUEST_SIDE_DREAM_WALKER_OBJECTIVES_PHANTOM_MEMBRANE_COLLECT("quest.side.dream.walker.objectives.phantom.membrane.collect"),
    QUEST_SIDE_DREAM_WALKER_OBJECTIVES_SLEEPING_VILLAGE("quest.side.dream.walker.objectives.sleeping.village"),
    QUEST_SIDE_DREAM_WALKER_OBJECTIVES_SOUL_TORCH_COLLECT("quest.side.dream.walker.objectives.soul.torch.collect"),
    QUEST_SIDE_DREAM_WALKER_OBJECTIVES_STRING_COLLECT("quest.side.dream.walker.objectives.string.collect"),
    QUEST_SIDE_DREAM_WALKER_OBJECTIVES_SWEET_BERRIES_COLLECT("quest.side.dream.walker.objectives.sweet.berries.collect"),
    QUEST_SIDE_DREAM_WALKER_OBJECTIVES_TALK_DREAM_KEEPER("quest.side.dream.walker.objectives.talk.dream.keeper");

    private final String key;
    
    DreamWalkerLangKey(String key) {
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
