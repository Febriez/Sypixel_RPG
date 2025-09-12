package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for MerchantsDilemma quests
 */
public enum MerchantsDilemmaLangKey implements ILangKey {
    QUEST_SIDE_MERCHANTS_DILEMMA_NAME("quest.side.merchants_dilemma.name"),
    QUEST_SIDE_MERCHANTS_DILEMMA_DESC("quest.side.merchants_dilemma.desc"),
    QUEST_SIDE_MERCHANTS_DILEMMA_ACCEPT("quest.side.merchants.dilemma.accept"),
    QUEST_SIDE_MERCHANTS_DILEMMA_DECLINE("quest.side.merchants.dilemma.decline"),
    QUEST_SIDE_MERCHANTS_DILEMMA_DIALOGS("quest.side.merchants.dilemma.dialogs"),
    QUEST_SIDE_MERCHANTS_DILEMMA_INFO("quest.side.merchants.dilemma.info"),
    QUEST_SIDE_MERCHANTS_DILEMMA_NPC_NAME("quest.side.merchants.dilemma.npc.name"),
    QUEST_SIDE_MERCHANTS_DILEMMA_OBJECTIVES_COLLECT_STOLEN_GOODS("quest.side.merchants.dilemma.objectives.collect.stolen.goods"),
    QUEST_SIDE_MERCHANTS_DILEMMA_OBJECTIVES_COLLECT_TRADE_CONTRACT("quest.side.merchants.dilemma.objectives.collect.trade.contract"),
    QUEST_SIDE_MERCHANTS_DILEMMA_OBJECTIVES_KILL_PILLAGERS("quest.side.merchants.dilemma.objectives.kill.pillagers"),
    QUEST_SIDE_MERCHANTS_DILEMMA_OBJECTIVES_RETURN_TROUBLED_MERCHANT("quest.side.merchants.dilemma.objectives.return.troubled.merchant"),
    QUEST_SIDE_MERCHANTS_DILEMMA_OBJECTIVES_TALK_TROUBLED_MERCHANT("quest.side.merchants.dilemma.objectives.talk.troubled.merchant"),
    QUEST_SIDE_MERCHANTS_DILEMMA_OBJECTIVES_VISIT_BANDITS_HIDEOUT("quest.side.merchants.dilemma.objectives.visit.bandits.hideout"),
    QUEST_SIDE_MERCHANTS_DILEMMA_OBJECTIVES_VISIT_CARAVAN_ROUTE("quest.side.merchants.dilemma.objectives.visit.caravan.route"),
    QUEST_SIDE_MERCHANTS_DILEMMA_OBJECTIVES_CHEST_COLLECT("quest.side.merchants.dilemma.objectives.chest.collect"),
    QUEST_SIDE_MERCHANTS_DILEMMA_OBJECTIVES_PAPER_COLLECT("quest.side.merchants.dilemma.objectives.paper.collect");

    private final String key;
    
    MerchantsDilemmaLangKey(String key) {
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
