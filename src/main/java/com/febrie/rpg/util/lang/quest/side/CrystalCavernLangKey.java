package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for CrystalCavern quests
 */
public enum CrystalCavernLangKey implements ILangKey {
    QUEST_SIDE_CRYSTAL_CAVERN_NAME("quest.side.crystal_cavern.name"),
    QUEST_SIDE_CRYSTAL_CAVERN_DESC("quest.side.crystal_cavern.desc"),
    QUEST_SIDE_CRYSTAL_CAVERN_ACCEPT("quest.side.crystal.cavern.accept"),
    QUEST_SIDE_CRYSTAL_CAVERN_DECLINE("quest.side.crystal.cavern.decline"),
    QUEST_SIDE_CRYSTAL_CAVERN_DIALOGS("quest.side.crystal.cavern.dialogs"),
    QUEST_SIDE_CRYSTAL_CAVERN_INFO("quest.side.crystal.cavern.info"),
    QUEST_SIDE_CRYSTAL_CAVERN_NPC_NAME("quest.side.crystal.cavern.npc.name"),
    QUEST_SIDE_CRYSTAL_CAVERN_OBJECTIVES_CAVERN_ENTRANCE("quest.side.crystal.cavern.objectives.cavern.entrance"),
    QUEST_SIDE_CRYSTAL_CAVERN_OBJECTIVES_CRYSTAL_CHAMBER("quest.side.crystal.cavern.objectives.crystal.chamber"),
    QUEST_SIDE_CRYSTAL_CAVERN_OBJECTIVES_KILL_SPIDERS("quest.side.crystal.cavern.objectives.kill.spiders"),
    QUEST_SIDE_CRYSTAL_CAVERN_OBJECTIVES_PURE_CRYSTAL("quest.side.crystal.cavern.objectives.pure.crystal"),
    QUEST_SIDE_CRYSTAL_CAVERN_OBJECTIVES_RAW_CRYSTALS("quest.side.crystal.cavern.objectives.raw.crystals"),
    QUEST_SIDE_CRYSTAL_CAVERN_OBJECTIVES_TALK_CRYSTAL_MINER("quest.side.crystal.cavern.objectives.talk.crystal.miner"),
    QUEST_SIDE_CRYSTAL_CAVERN_OBJECTIVES_AMETHYST_CLUSTER_COLLECT("quest.side.crystal.cavern.objectives.amethyst.cluster.collect"),
    QUEST_SIDE_CRYSTAL_CAVERN_OBJECTIVES_AMETHYST_SHARD_COLLECT("quest.side.crystal.cavern.objectives.amethyst.shard.collect");

    private final String key;
    
    CrystalCavernLangKey(String key) {
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
