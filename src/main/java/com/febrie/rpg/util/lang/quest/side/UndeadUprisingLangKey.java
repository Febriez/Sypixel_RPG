package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for UndeadUprising quests
 */
public enum UndeadUprisingLangKey implements ILangKey {
    QUEST_SIDE_UNDEAD_UPRISING_NAME("quest.side.undead_uprising.name"),
    QUEST_SIDE_UNDEAD_UPRISING_DESC("quest.side.undead_uprising.desc"),
    QUEST_SIDE_UNDEAD_UPRISING_INFO("quest.side.undead.uprising.info"),
    QUEST_SIDE_UNDEAD_UPRISING_OBJECTIVES_TALK_TOWN_GUARD("quest.side.undead.uprising.objectives.talk.town.guard"),
    QUEST_SIDE_UNDEAD_UPRISING_ACCEPT("quest.side.undead.uprising.accept"),
    QUEST_SIDE_UNDEAD_UPRISING_DECLINE("quest.side.undead.uprising.decline"),
    QUEST_SIDE_UNDEAD_UPRISING_DIALOGS("quest.side.undead.uprising.dialogs"),
    QUEST_SIDE_UNDEAD_UPRISING_NPC_NAME("quest.side.undead.uprising.npc.name"),
    QUEST_SIDE_UNDEAD_UPRISING_OBJECTIVES_CURSED_GRAVEYARD("quest.side.undead.uprising.objectives.cursed.graveyard"),
    QUEST_SIDE_UNDEAD_UPRISING_OBJECTIVES_DARK_NECROMANCER("quest.side.undead.uprising.objectives.dark.necromancer"),
    QUEST_SIDE_UNDEAD_UPRISING_OBJECTIVES_HOLY_WATER("quest.side.undead.uprising.objectives.holy.water"),
    QUEST_SIDE_UNDEAD_UPRISING_OBJECTIVES_NECROMANCER_TOWER("quest.side.undead.uprising.objectives.necromancer.tower"),
    QUEST_SIDE_UNDEAD_UPRISING_OBJECTIVES_NECROMANTIC_TOME("quest.side.undead.uprising.objectives.necromantic.tome"),
    QUEST_SIDE_UNDEAD_UPRISING_OBJECTIVES_PURIFY_GRAVEYARD("quest.side.undead.uprising.objectives.purify.graveyard"),
    QUEST_SIDE_UNDEAD_UPRISING_OBJECTIVES_SKELETON_WARRIORS("quest.side.undead.uprising.objectives.skeleton.warriors"),
    QUEST_SIDE_UNDEAD_UPRISING_OBJECTIVES_UNDEAD_CREATURES("quest.side.undead.uprising.objectives.undead.creatures"),
    QUEST_SIDE_UNDEAD_UPRISING_OBJECTIVES_ENCHANTED_BOOK_COLLECT("quest.side.undead.uprising.objectives.enchanted.book.collect"),
    QUEST_SIDE_UNDEAD_UPRISING_OBJECTIVES_POTION_COLLECT("quest.side.undead.uprising.objectives.potion.collect");

    private final String key;
    
    UndeadUprisingLangKey(String key) {
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
