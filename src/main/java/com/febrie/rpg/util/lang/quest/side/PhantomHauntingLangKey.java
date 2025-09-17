package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for PhantomHaunting quests
 */
public enum PhantomHauntingLangKey implements ILangKey {
    QUEST_SIDE_PHANTOM_HAUNTING_NAME("quest.side.phantom_haunting.name"),
    QUEST_SIDE_PHANTOM_HAUNTING_DESC("quest.side.phantom_haunting.desc"),
    QUEST_SIDE_PHANTOM_HAUNTING_ACCEPT("quest.side.phantom.haunting.accept"),
    QUEST_SIDE_PHANTOM_HAUNTING_DECLINE("quest.side.phantom.haunting.decline"),
    QUEST_SIDE_PHANTOM_HAUNTING_DIALOGS("quest.side.phantom.haunting.dialogs"),
    QUEST_SIDE_PHANTOM_HAUNTING_INFO("quest.side.phantom.haunting.info"),
    QUEST_SIDE_PHANTOM_HAUNTING_NPC_NAME("quest.side.phantom.haunting.npc.name"),
    QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_CONFRONT_LORD_BLACKWOOD("quest.side.phantom.haunting.objectives.confront.lord.blackwood"),
    QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_ECTOPLASM_COLLECT("quest.side.phantom.haunting.objectives.ectoplasm.collect"),
    QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_FAMILY_CRYPT("quest.side.phantom.haunting.objectives.family.crypt"),
    QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_HAUNTED_MANSION("quest.side.phantom.haunting.objectives.haunted.mansion"),
    QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_PAPER_COLLECT("quest.side.phantom.haunting.objectives.paper.collect"),
    QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_PERFORM_EXORCISM("quest.side.phantom.haunting.objectives.perform.exorcism"),
    QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_RESTLESS_SPIRITS("quest.side.phantom.haunting.objectives.restless.spirits"),
    QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_SOUL_TORCH_COLLECT("quest.side.phantom.haunting.objectives.soul.torch.collect"),
    QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_TALK_VILLAGE_PRIEST("quest.side.phantom.haunting.objectives.talk.village.priest");

    private final String key;
    
    PhantomHauntingLangKey(String key) {
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
