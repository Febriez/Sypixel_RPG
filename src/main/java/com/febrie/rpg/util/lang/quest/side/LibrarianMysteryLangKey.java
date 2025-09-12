package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for LibrarianMystery quests
 */
public enum LibrarianMysteryLangKey implements ILangKey {
    QUEST_SIDE_LIBRARIAN_MYSTERY_NAME("quest.side.librarian_mystery.name"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_DESC("quest.side.librarian_mystery.desc"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_ACCEPT("quest.side.librarian.mystery.accept"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_DECLINE("quest.side.librarian.mystery.decline"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_DIALOGS("quest.side.librarian.mystery.dialogs"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_INFO("quest.side.librarian.mystery.info"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_NPC_NAME("quest.side.librarian.mystery.npc.name"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_OBJECTIVES_COLLECT_CIPHER_KEY("quest.side.librarian.mystery.objectives.collect.cipher.key"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_OBJECTIVES_COLLECT_FORBIDDEN_KNOWLEDGE("quest.side.librarian.mystery.objectives.collect.forbidden.knowledge"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_OBJECTIVES_COLLECT_MISSING_TOME("quest.side.librarian.mystery.objectives.collect.missing.tome"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_OBJECTIVES_RETURN_HEAD_LIBRARIAN("quest.side.librarian.mystery.objectives.return.head.librarian"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_OBJECTIVES_TALK_HEAD_LIBRARIAN("quest.side.librarian.mystery.objectives.talk.head.librarian"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_OBJECTIVES_VISIT_ANCIENT_ARCHIVES("quest.side.librarian.mystery.objectives.visit.ancient.archives"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_OBJECTIVES_VISIT_SECRET_CHAMBER("quest.side.librarian.mystery.objectives.visit.secret.chamber"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_OBJECTIVES_COMPASS_COLLECT("quest.side.librarian.mystery.objectives.compass.collect"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_OBJECTIVES_ENCHANTED_BOOK_COLLECT("quest.side.librarian.mystery.objectives.enchanted.book.collect"),
    QUEST_SIDE_LIBRARIAN_MYSTERY_OBJECTIVES_WRITTEN_BOOK_COLLECT("quest.side.librarian.mystery.objectives.written.book.collect");

    private final String key;
    
    LibrarianMysteryLangKey(String key) {
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
