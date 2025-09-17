package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for GiantSlayer quests
 */
public enum GiantSlayerLangKey implements ILangKey {
    QUEST_SIDE_GIANT_SLAYER_NAME("quest.side.giant_slayer.name"),
    QUEST_SIDE_GIANT_SLAYER_DESC("quest.side.giant_slayer.desc"),
    QUEST_SIDE_GIANT_SLAYER_ACCEPT("quest.side.giant.slayer.accept"),
    QUEST_SIDE_GIANT_SLAYER_DECLINE("quest.side.giant.slayer.decline"),
    QUEST_SIDE_GIANT_SLAYER_DIALOGS("quest.side.giant.slayer.dialogs"),
    QUEST_SIDE_GIANT_SLAYER_INFO("quest.side.giant.slayer.info"),
    QUEST_SIDE_GIANT_SLAYER_NPC_NAME("quest.side.giant.slayer.npc.name"),
    QUEST_SIDE_GIANT_SLAYER_OBJECTIVES_ANCIENT_BATTLEFIELD("quest.side.giant.slayer.objectives.ancient.battlefield"),
    QUEST_SIDE_GIANT_SLAYER_OBJECTIVES_CLAIM_VICTORY("quest.side.giant.slayer.objectives.claim.victory"),
    QUEST_SIDE_GIANT_SLAYER_OBJECTIVES_CLAY_BALL_COLLECT("quest.side.giant.slayer.objectives.clay.ball.collect"),
    QUEST_SIDE_GIANT_SLAYER_OBJECTIVES_FROST_GIANTS("quest.side.giant.slayer.objectives.frost.giants"),
    QUEST_SIDE_GIANT_SLAYER_OBJECTIVES_GIANT_TRACKS("quest.side.giant.slayer.objectives.giant.tracks"),
    QUEST_SIDE_GIANT_SLAYER_OBJECTIVES_STONE_GIANTS("quest.side.giant.slayer.objectives.stone.giants"),
    QUEST_SIDE_GIANT_SLAYER_OBJECTIVES_TALK_GIANT_RESEARCHER("quest.side.giant.slayer.objectives.talk.giant.researcher"),
    QUEST_SIDE_GIANT_SLAYER_OBJECTIVES_TOTEM_OF_UNDYING_COLLECT("quest.side.giant.slayer.objectives.totem.of.undying.collect"),
    QUEST_SIDE_GIANT_SLAYER_OBJECTIVES_WRITTEN_BOOK_COLLECT("quest.side.giant.slayer.objectives.written.book.collect");

    private final String key;
    
    GiantSlayerLangKey(String key) {
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
