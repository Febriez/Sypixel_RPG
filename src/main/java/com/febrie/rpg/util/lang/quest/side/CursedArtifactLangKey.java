package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for CursedArtifact quests
 */
public enum CursedArtifactLangKey implements ILangKey {
    QUEST_SIDE_CURSED_ARTIFACT_NAME("quest.side.cursed_artifact.name"),
    QUEST_SIDE_CURSED_ARTIFACT_DESC("quest.side.cursed_artifact.desc"),
    QUEST_SIDE_CURSED_ARTIFACT_ACCEPT("quest.side.cursed.artifact.accept"),
    QUEST_SIDE_CURSED_ARTIFACT_DECLINE("quest.side.cursed.artifact.decline"),
    QUEST_SIDE_CURSED_ARTIFACT_DIALOGS("quest.side.cursed.artifact.dialogs"),
    QUEST_SIDE_CURSED_ARTIFACT_INFO("quest.side.cursed.artifact.info"),
    QUEST_SIDE_CURSED_ARTIFACT_NPC_NAME("quest.side.cursed.artifact.npc.name"),
    QUEST_SIDE_CURSED_ARTIFACT_OBJECTIVES_ARTIFACT_CHAMBER("quest.side.cursed.artifact.objectives.artifact.chamber"),
    QUEST_SIDE_CURSED_ARTIFACT_OBJECTIVES_CORRUPTED_CREATURES("quest.side.cursed.artifact.objectives.corrupted.creatures"),
    QUEST_SIDE_CURSED_ARTIFACT_OBJECTIVES_CORRUPTED_GROVE("quest.side.cursed.artifact.objectives.corrupted.grove"),
    QUEST_SIDE_CURSED_ARTIFACT_OBJECTIVES_GOLDEN_APPLE_COLLECT("quest.side.cursed.artifact.objectives.golden.apple.collect"),
    QUEST_SIDE_CURSED_ARTIFACT_OBJECTIVES_NETHER_STAR_COLLECT("quest.side.cursed.artifact.objectives.nether.star.collect"),
    QUEST_SIDE_CURSED_ARTIFACT_OBJECTIVES_SEAL_ARTIFACT("quest.side.cursed.artifact.objectives.seal.artifact"),
    QUEST_SIDE_CURSED_ARTIFACT_OBJECTIVES_SUGAR_COLLECT("quest.side.cursed.artifact.objectives.sugar.collect"),
    QUEST_SIDE_CURSED_ARTIFACT_OBJECTIVES_TALK_CONCERNED_SCHOLAR("quest.side.cursed.artifact.objectives.talk.concerned.scholar");

    private final String key;
    
    CursedArtifactLangKey(String key) {
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
