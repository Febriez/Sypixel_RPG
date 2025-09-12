package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for AncientCipher quests
 */
public enum AncientCipherLangKey implements ILangKey {
    QUEST_SIDE_ANCIENT_CIPHER_NAME("quest.side.ancient_cipher.name"),
    QUEST_SIDE_ANCIENT_CIPHER_DESC("quest.side.ancient_cipher.desc"),
    QUEST_SIDE_ANCIENT_CIPHER_ACCEPT("quest.side.ancient.cipher.accept"),
    QUEST_SIDE_ANCIENT_CIPHER_DECLINE("quest.side.ancient.cipher.decline"),
    QUEST_SIDE_ANCIENT_CIPHER_DIALOGS("quest.side.ancient.cipher.dialogs"),
    QUEST_SIDE_ANCIENT_CIPHER_INFO("quest.side.ancient.cipher.info"),
    QUEST_SIDE_ANCIENT_CIPHER_NPC_NAME("quest.side.ancient.cipher.npc.name"),
    QUEST_SIDE_ANCIENT_CIPHER_OBJECTIVES_CIPHER_PIECES("quest.side.ancient.cipher.objectives.cipher.pieces"),
    QUEST_SIDE_ANCIENT_CIPHER_OBJECTIVES_DECODED_CIPHER("quest.side.ancient.cipher.objectives.decoded.cipher"),
    QUEST_SIDE_ANCIENT_CIPHER_OBJECTIVES_SCHOLAR("quest.side.ancient.cipher.objectives.scholar"),
    QUEST_SIDE_ANCIENT_CIPHER_OBJECTIVES_STUDY_RUINS("quest.side.ancient.cipher.objectives.study.ruins"),
    QUEST_SIDE_ANCIENT_CIPHER_OBJECTIVES_BOOK_DELIVER("quest.side.ancient.cipher.objectives.book.deliver"),
    QUEST_SIDE_ANCIENT_CIPHER_OBJECTIVES_PAPER_COLLECT("quest.side.ancient.cipher.objectives.paper.collect");

    private final String key;
    
    AncientCipherLangKey(String key) {
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
