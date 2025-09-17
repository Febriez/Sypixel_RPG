package com.febrie.rpg.util.lang.quest.tutorial;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for TutorialQuest quests
 */
public enum TutorialQuestLangKey implements ILangKey {
    QUEST_TUTORIAL_FIRST_STEPS_NAME("quest.tutorial.first_steps.name"),
    QUEST_TUTORIAL_FIRST_STEPS_DESC("quest.tutorial.first_steps.desc"),
    QUEST_TUTORIAL_BASIC_COMBAT_NAME("quest.tutorial.basic_combat.name"),
    QUEST_TUTORIAL_BASIC_COMBAT_DESC("quest.tutorial.basic_combat.desc"),
    QUEST_TUTORIAL_BASIC_COMBAT_INFO("quest.tutorial.basic.combat.info"),
    QUEST_TUTORIAL_BASIC_COMBAT_OBJECTIVES_KILL_SKELETONS("quest.tutorial.basic.combat.objectives.kill.skeletons"),
    QUEST_TUTORIAL_BASIC_COMBAT_OBJECTIVES_KILL_ZOMBIES("quest.tutorial.basic.combat.objectives.kill.zombies"),
    QUEST_TUTORIAL_BASIC_COMBAT_DIALOGS("quest.tutorial.basic.combat.dialogs"),
    QUEST_TUTORIAL_BASIC_COMBAT_ACCEPT("quest.tutorial.basic.combat.accept"),
    QUEST_TUTORIAL_FIRST_STEPS_DECLINE("quest.tutorial.first.steps.decline"),
    QUEST_TUTORIAL_BASIC_COMBAT_DECLINE("quest.tutorial.basic.combat.decline"),
    QUEST_TUTORIAL_FIRST_STEPS_NPC_NAME("quest.tutorial.first.steps.npc.name"),
    QUEST_TUTORIAL_FIRST_STEPS_OBJECTIVES_VISIT_HUB("quest.tutorial.first.steps.objectives.visit.hub"),
    QUEST_TUTORIAL_BASIC_COMBAT_NPC_NAME("quest.tutorial.basic.combat.npc.name"),
    QUEST_TUTORIAL_FIRST_STEPS_DIALOGS("quest.tutorial.first.steps.dialogs"),
    QUEST_TUTORIAL_FIRST_STEPS_OBJECTIVES_VISIT_MERCHANT("quest.tutorial.first.steps.objectives.visit.merchant"),
    QUEST_TUTORIAL_FIRST_STEPS_INFO("quest.tutorial.first.steps.info"),
    QUEST_TUTORIAL_FIRST_STEPS_ACCEPT("quest.tutorial.first.steps.accept");

    private final String key;
    
    TutorialQuestLangKey(String key) {
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
