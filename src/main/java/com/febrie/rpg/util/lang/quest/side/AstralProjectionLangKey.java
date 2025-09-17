package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for AstralProjection quests
 */
public enum AstralProjectionLangKey implements ILangKey {
    QUEST_SIDE_ASTRAL_PROJECTION_NAME("quest.side.astral_projection.name"),
    QUEST_SIDE_ASTRAL_PROJECTION_DESC("quest.side.astral_projection.desc"),
    QUEST_SIDE_ASTRAL_PROJECTION_ACCEPT("quest.side.astral.projection.accept"),
    QUEST_SIDE_ASTRAL_PROJECTION_DECLINE("quest.side.astral.projection.decline"),
    QUEST_SIDE_ASTRAL_PROJECTION_DIALOGS("quest.side.astral.projection.dialogs"),
    QUEST_SIDE_ASTRAL_PROJECTION_INFO("quest.side.astral.projection.info"),
    QUEST_SIDE_ASTRAL_PROJECTION_NPC_NAME("quest.side.astral.projection.npc.name"),
    QUEST_SIDE_ASTRAL_PROJECTION_OBJECTIVES_AMETHYST_SHARD_COLLECT("quest.side.astral.projection.objectives.amethyst.shard.collect"),
    QUEST_SIDE_ASTRAL_PROJECTION_OBJECTIVES_ASTRAL_GATEWAY("quest.side.astral.projection.objectives.astral.gateway"),
    QUEST_SIDE_ASTRAL_PROJECTION_OBJECTIVES_ETHEREAL_MAZE("quest.side.astral.projection.objectives.ethereal.maze"),
    QUEST_SIDE_ASTRAL_PROJECTION_OBJECTIVES_EXPERIENCE_BOTTLE_COLLECT("quest.side.astral.projection.objectives.experience.bottle.collect"),
    QUEST_SIDE_ASTRAL_PROJECTION_OBJECTIVES_RESCUE_BROTHER_TRANSCENDENCE("quest.side.astral.projection.objectives.rescue.brother.transcendence"),
    QUEST_SIDE_ASTRAL_PROJECTION_OBJECTIVES_SOUL_LANTERN_COLLECT("quest.side.astral.projection.objectives.soul.lantern.collect"),
    QUEST_SIDE_ASTRAL_PROJECTION_OBJECTIVES_TALK_BROTHER_SEEKER("quest.side.astral.projection.objectives.talk.brother.seeker");

    private final String key;
    
    AstralProjectionLangKey(String key) {
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
