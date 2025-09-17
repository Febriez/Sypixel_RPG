package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for MirrorWorld quests
 */
public enum MirrorWorldLangKey implements ILangKey {
    QUEST_SIDE_MIRROR_WORLD_NAME("quest.side.mirror_world.name"),
    QUEST_SIDE_MIRROR_WORLD_DESC("quest.side.mirror_world.desc"),
    QUEST_SIDE_MIRROR_WORLD_ACCEPT("quest.side.mirror.world.accept"),
    QUEST_SIDE_MIRROR_WORLD_DECLINE("quest.side.mirror.world.decline"),
    QUEST_SIDE_MIRROR_WORLD_DIALOGS("quest.side.mirror.world.dialogs"),
    QUEST_SIDE_MIRROR_WORLD_INFO("quest.side.mirror.world.info"),
    QUEST_SIDE_MIRROR_WORLD_NPC_NAME("quest.side.mirror.world.npc.name"),
    QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_DARK_REFLECTION("quest.side.mirror.world.objectives.dark.reflection"),
    QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_DESTROY_MIRROR("quest.side.mirror.world.objectives.destroy.mirror"),
    QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_EMERALD_BLOCK_COLLECT("quest.side.mirror.world.objectives.emerald.block.collect"),
    QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_GLASS_COLLECT("quest.side.mirror.world.objectives.glass.collect"),
    QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_MIRROR_MASTER("quest.side.mirror.world.objectives.mirror.master"),
    QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_MIRROR_PORTAL("quest.side.mirror.world.objectives.mirror.portal"),
    QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_SHADOW_DOUBLES("quest.side.mirror.world.objectives.shadow.doubles"),
    QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_SHATTERED_MIRROR("quest.side.mirror.world.objectives.shattered.mirror"),
    QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_TALK_MIRROR_GUARDIAN("quest.side.mirror.world.objectives.talk.mirror.guardian");

    private final String key;
    
    MirrorWorldLangKey(String key) {
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
