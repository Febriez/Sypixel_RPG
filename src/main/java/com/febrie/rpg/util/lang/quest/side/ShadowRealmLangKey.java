package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for ShadowRealm quests
 */
public enum ShadowRealmLangKey implements ILangKey {
    QUEST_SIDE_SHADOW_REALM_NAME("quest.side.shadow_realm.name"),
    QUEST_SIDE_SHADOW_REALM_DESC("quest.side.shadow_realm.desc"),
    QUEST_SIDE_SHADOW_REALM_ACCEPT("quest.side.shadow.realm.accept"),
    QUEST_SIDE_SHADOW_REALM_DECLINE("quest.side.shadow.realm.decline"),
    QUEST_SIDE_SHADOW_REALM_DIALOGS("quest.side.shadow.realm.dialogs"),
    QUEST_SIDE_SHADOW_REALM_INFO("quest.side.shadow.realm.info"),
    QUEST_SIDE_SHADOW_REALM_NPC_NAME("quest.side.shadow.realm.npc.name"),
    QUEST_SIDE_SHADOW_REALM_OBJECTIVES_END_CRYSTAL_COLLECT("quest.side.shadow.realm.objectives.end.crystal.collect"),
    QUEST_SIDE_SHADOW_REALM_OBJECTIVES_PORTAL_GUARDIANS("quest.side.shadow.realm.objectives.portal.guardians"),
    QUEST_SIDE_SHADOW_REALM_OBJECTIVES_PORTAL_NEXUS("quest.side.shadow.realm.objectives.portal.nexus"),
    QUEST_SIDE_SHADOW_REALM_OBJECTIVES_SEAL_PORTALS("quest.side.shadow.realm.objectives.seal.portals"),
    QUEST_SIDE_SHADOW_REALM_OBJECTIVES_SHADOW_CREATURES("quest.side.shadow.realm.objectives.shadow.creatures"),
    QUEST_SIDE_SHADOW_REALM_OBJECTIVES_SHADOW_LORD("quest.side.shadow.realm.objectives.shadow.lord"),
    QUEST_SIDE_SHADOW_REALM_OBJECTIVES_TALK_LIGHT_MAGE("quest.side.shadow.realm.objectives.talk.light.mage");

    private final String key;
    
    ShadowRealmLangKey(String key) {
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
