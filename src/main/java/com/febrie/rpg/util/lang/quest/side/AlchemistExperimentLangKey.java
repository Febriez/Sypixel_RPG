package com.febrie.rpg.util.lang.quest.side;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for AlchemistExperiment quests
 */
public enum AlchemistExperimentLangKey implements ILangKey {
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_NAME("quest.side.alchemist_experiment.name"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_DESC("quest.side.alchemist_experiment.desc"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_ACCEPT("quest.side.alchemist.experiment.accept"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_DECLINE("quest.side.alchemist.experiment.decline"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_DIALOGS("quest.side.alchemist.experiment.dialogs"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_INFO("quest.side.alchemist.experiment.info"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_NPC_NAME("quest.side.alchemist.experiment.npc.name"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_OBJECTIVES_ALCHEMY_LAB("quest.side.alchemist.experiment.objectives.alchemy.lab"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_OBJECTIVES_DRAGON_SCALES("quest.side.alchemist.experiment.objectives.dragon.scales"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_OBJECTIVES_KILL_WITCHES("quest.side.alchemist.experiment.objectives.kill.witches"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_OBJECTIVES_MAD_ALCHEMIST("quest.side.alchemist.experiment.objectives.mad.alchemist"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_OBJECTIVES_MAD_ALCHEMIST_COMPLETE("quest.side.alchemist.experiment.objectives.mad.alchemist.complete"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_OBJECTIVES_PHILOSOPHER_STONE("quest.side.alchemist.experiment.objectives.philosopher.stone"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_OBJECTIVES_RARE_REAGENTS("quest.side.alchemist.experiment.objectives.rare.reagents"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_OBJECTIVES_COLLECT_BLAZE_POWDER("quest.side.alchemist.experiment.objectives.collect.blaze.powder"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_OBJECTIVES_COLLECT_NETHER_STAR("quest.side.alchemist.experiment.objectives.collect.nether.star"),
    QUEST_SIDE_ALCHEMIST_EXPERIMENT_OBJECTIVES_COLLECT_SHULKER_SHELL("quest.side.alchemist.experiment.objectives.collect.shulker.shell");

    private final String key;
    
    AlchemistExperimentLangKey(String key) {
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
