package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for HiddenValley quests
 */
public enum HiddenValleyLangKey implements ILangKey {
    QUEST_SIDE_HIDDEN_VALLEY_NAME("quest.side.hidden_valley.name"),
    QUEST_SIDE_HIDDEN_VALLEY_DESC("quest.side.hidden_valley.desc"),
    QUEST_SIDE_HIDDEN_VALLEY_ACCEPT("quest.side.hidden.valley.accept"),
    QUEST_SIDE_HIDDEN_VALLEY_DECLINE("quest.side.hidden.valley.decline"),
    QUEST_SIDE_HIDDEN_VALLEY_DIALOGS("quest.side.hidden.valley.dialogs"),
    QUEST_SIDE_HIDDEN_VALLEY_INFO("quest.side.hidden.valley.info"),
    QUEST_SIDE_HIDDEN_VALLEY_NPC_NAME("quest.side.hidden.valley.npc.name"),
    QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_COLLECT_MOUNTAIN_FLOWER("quest.side.hidden.valley.objectives.collect.mountain.flower"),
    QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_COLLECT_VALLEY_CRYSTAL("quest.side.hidden.valley.objectives.collect.valley.crystal"),
    QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_KILL_WOLVES("quest.side.hidden.valley.objectives.kill.wolves"),
    QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_TALK_VALLEY_GUARDIAN("quest.side.hidden.valley.objectives.talk.valley.guardian"),
    QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_TALK_VALLEY_SCOUT("quest.side.hidden.valley.objectives.talk.valley.scout"),
    QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_VISIT_HIDDEN_ENTRANCE("quest.side.hidden.valley.objectives.visit.hidden.entrance"),
    QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_VISIT_MOUNTAIN_PASS("quest.side.hidden.valley.objectives.visit.mountain.pass"),
    QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_VISIT_VALLEY_HEART("quest.side.hidden.valley.objectives.visit.valley.heart"),
    QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_AZURE_BLUET_COLLECT("quest.side.hidden.valley.objectives.azure.bluet.collect"),
    QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_EMERALD_COLLECT("quest.side.hidden.valley.objectives.emerald.collect");

    private final String key;
    
    HiddenValleyLangKey(String key) {
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
