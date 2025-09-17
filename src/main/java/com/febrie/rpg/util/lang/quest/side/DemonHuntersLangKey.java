package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for DemonHunters quests
 */
public enum DemonHuntersLangKey implements ILangKey {
    QUEST_SIDE_DEMON_HUNTERS_NAME("quest.side.demon_hunters.name"),
    QUEST_SIDE_DEMON_HUNTERS_DESC("quest.side.demon_hunters.desc"),
    QUEST_SIDE_DEMON_HUNTERS_DIALOGS("quest.side.demon.hunters.dialogs"),
    QUEST_SIDE_DEMON_HUNTERS_INFO("quest.side.demon.hunters.info"),
    QUEST_SIDE_DEMON_HUNTERS_NPC_NAME("quest.side.demon.hunters.npc.name"),
    QUEST_SIDE_DEMON_HUNTERS_ACCEPT("quest.side.demon.hunters.accept"),
    QUEST_SIDE_DEMON_HUNTERS_DECLINE("quest.side.demon.hunters.decline"),
    QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_BLAZE_POWDER_COLLECT("quest.side.demon.hunters.objectives.blaze.powder.collect"),
    QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_DEMON_PORTAL("quest.side.demon.hunters.objectives.demon.portal"),
    QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_DEMON_REALM("quest.side.demon.hunters.objectives.demon.realm"),
    QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_DIAMOND_SWORD_COLLECT("quest.side.demon.hunters.objectives.diamond.sword.collect"),
    QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_IRON_INGOT_COLLECT("quest.side.demon.hunters.objectives.iron.ingot.collect"),
    QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_LESSER_DEMONS("quest.side.demon.hunters.objectives.lesser.demons"),
    QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_PORTAL_GUARDIAN("quest.side.demon.hunters.objectives.portal.guardian"),
    QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_SEAL_PORTAL("quest.side.demon.hunters.objectives.seal.portal"),
    QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_TALK_DEMON_HUNTER_CAPTAIN("quest.side.demon.hunters.objectives.talk.demon.hunter.captain");

    private final String key;
    
    DemonHuntersLangKey(String key) {
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
