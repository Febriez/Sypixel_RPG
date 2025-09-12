package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for SoulFragments quests
 */
public enum SoulFragmentsLangKey implements ILangKey {
    QUEST_SIDE_SOUL_FRAGMENTS_NAME("quest.side.soul_fragments.name"),
    QUEST_SIDE_SOUL_FRAGMENTS_DESC("quest.side.soul_fragments.desc"),
    QUEST_SIDE_SOUL_FRAGMENTS_ACCEPT("quest.side.soul.fragments.accept"),
    QUEST_SIDE_SOUL_FRAGMENTS_DECLINE("quest.side.soul.fragments.decline"),
    QUEST_SIDE_SOUL_FRAGMENTS_DIALOGS("quest.side.soul.fragments.dialogs"),
    QUEST_SIDE_SOUL_FRAGMENTS_INFO("quest.side.soul.fragments.info"),
    QUEST_SIDE_SOUL_FRAGMENTS_NPC_NAME("quest.side.soul.fragments.npc.name"),
    QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_COMPLETE_SOULS("quest.side.soul.fragments.objectives.complete.souls"),
    QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_PURIFICATION_ESSENCE("quest.side.soul.fragments.objectives.purification.essence"),
    QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_RESTORE_SOULS("quest.side.soul.fragments.objectives.restore.souls"),
    QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_SOUL_FRAGMENTS("quest.side.soul.fragments.objectives.soul.fragments"),
    QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_SOUL_NEXUS("quest.side.soul.fragments.objectives.soul.nexus"),
    QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_SPIRIT_SHRINE("quest.side.soul.fragments.objectives.spirit.shrine"),
    QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_SPIRITUAL_BARRIER("quest.side.soul.fragments.objectives.spiritual.barrier"),
    QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_TALK_SPIRIT_GUIDE("quest.side.soul.fragments.objectives.talk.spirit.guide"),
    QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_GHAST_TEAR_COLLECT("quest.side.soul.fragments.objectives.ghast.tear.collect"),
    QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_GLOWSTONE_DUST_COLLECT("quest.side.soul.fragments.objectives.glowstone.dust.collect"),
    QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_SOUL_TORCH_COLLECT("quest.side.soul.fragments.objectives.soul.torch.collect");

    private final String key;
    
    SoulFragmentsLangKey(String key) {
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
