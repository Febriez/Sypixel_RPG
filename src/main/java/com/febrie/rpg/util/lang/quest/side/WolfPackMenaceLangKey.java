package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for WolfPackMenace quests
 */
public enum WolfPackMenaceLangKey implements ILangKey {
    QUEST_SIDE_WOLF_PACK_MENACE_NAME("quest.side.wolf_pack_menace.name"),
    QUEST_SIDE_WOLF_PACK_MENACE_DESC("quest.side.wolf_pack_menace.desc"),
    QUEST_SIDE_WOLF_PACK_MENACE_ACCEPT("quest.side.wolf.pack.menace.accept"),
    QUEST_SIDE_WOLF_PACK_MENACE_DECLINE("quest.side.wolf.pack.menace.decline"),
    QUEST_SIDE_WOLF_PACK_MENACE_DIALOGS("quest.side.wolf.pack.menace.dialogs"),
    QUEST_SIDE_WOLF_PACK_MENACE_INFO("quest.side.wolf.pack.menace.info"),
    QUEST_SIDE_WOLF_PACK_MENACE_NPC_NAME("quest.side.wolf.pack.menace.npc.name"),
    QUEST_SIDE_WOLF_PACK_MENACE_OBJECTIVES_ALPHA_WOLF("quest.side.wolf.pack.menace.objectives.alpha.wolf"),
    QUEST_SIDE_WOLF_PACK_MENACE_OBJECTIVES_COMPASS_COLLECT("quest.side.wolf.pack.menace.objectives.compass.collect"),
    QUEST_SIDE_WOLF_PACK_MENACE_OBJECTIVES_LEATHER_COLLECT("quest.side.wolf.pack.menace.objectives.leather.collect"),
    QUEST_SIDE_WOLF_PACK_MENACE_OBJECTIVES_PACK_DEN("quest.side.wolf.pack.menace.objectives.pack.den"),
    QUEST_SIDE_WOLF_PACK_MENACE_OBJECTIVES_PACK_MEMBERS("quest.side.wolf.pack.menace.objectives.pack.members"),
    QUEST_SIDE_WOLF_PACK_MENACE_OBJECTIVES_REPORT_SUCCESS("quest.side.wolf.pack.menace.objectives.report.success"),
    QUEST_SIDE_WOLF_PACK_MENACE_OBJECTIVES_TALK_CARAVAN_LEADER("quest.side.wolf.pack.menace.objectives.talk.caravan.leader"),
    QUEST_SIDE_WOLF_PACK_MENACE_OBJECTIVES_WOLF_SCOUTS("quest.side.wolf.pack.menace.objectives.wolf.scouts"),
    QUEST_SIDE_WOLF_PACK_MENACE_OBJECTIVES_WOLF_TERRITORY("quest.side.wolf.pack.menace.objectives.wolf.territory");

    private final String key;
    
    WolfPackMenaceLangKey(String key) {
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
