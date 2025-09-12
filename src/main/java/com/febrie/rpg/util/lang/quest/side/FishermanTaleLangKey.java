package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for FishermanTale quests
 */
public enum FishermanTaleLangKey implements ILangKey {
    QUEST_SIDE_FISHERMAN_TALE_NAME("quest.side.fisherman_tale.name"),
    QUEST_SIDE_FISHERMAN_TALE_DESC("quest.side.fisherman_tale.desc"),
    QUEST_SIDE_FISHERMAN_TALE_ACCEPT("quest.side.fisherman.tale.accept"),
    QUEST_SIDE_FISHERMAN_TALE_DECLINE("quest.side.fisherman.tale.decline"),
    QUEST_SIDE_FISHERMAN_TALE_DIALOGS("quest.side.fisherman.tale.dialogs"),
    QUEST_SIDE_FISHERMAN_TALE_INFO("quest.side.fisherman.tale.info"),
    QUEST_SIDE_FISHERMAN_TALE_NPC_NAME("quest.side.fisherman.tale.npc.name"),
    QUEST_SIDE_FISHERMAN_TALE_OBJECTIVES_COLLECT_RARE_FISH("quest.side.fisherman.tale.objectives.collect.rare.fish"),
    QUEST_SIDE_FISHERMAN_TALE_OBJECTIVES_COLLECT_SEA_TREASURE("quest.side.fisherman.tale.objectives.collect.sea.treasure"),
    QUEST_SIDE_FISHERMAN_TALE_OBJECTIVES_KILL_DROWNED("quest.side.fisherman.tale.objectives.kill.drowned"),
    QUEST_SIDE_FISHERMAN_TALE_OBJECTIVES_RETURN_OLD_FISHERMAN("quest.side.fisherman.tale.objectives.return.old.fisherman"),
    QUEST_SIDE_FISHERMAN_TALE_OBJECTIVES_TALK_OLD_FISHERMAN("quest.side.fisherman.tale.objectives.talk.old.fisherman"),
    QUEST_SIDE_FISHERMAN_TALE_OBJECTIVES_VISIT_DEEP_WATERS("quest.side.fisherman.tale.objectives.visit.deep.waters"),
    QUEST_SIDE_FISHERMAN_TALE_OBJECTIVES_VISIT_FISHING_DOCK("quest.side.fisherman.tale.objectives.visit.fishing.dock"),
    QUEST_SIDE_FISHERMAN_TALE_OBJECTIVES_PRISMARINE_SHARD_COLLECT("quest.side.fisherman.tale.objectives.prismarine.shard.collect"),
    QUEST_SIDE_FISHERMAN_TALE_OBJECTIVES_SALMON_COLLECT("quest.side.fisherman.tale.objectives.salmon.collect");

    private final String key;
    
    FishermanTaleLangKey(String key) {
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
