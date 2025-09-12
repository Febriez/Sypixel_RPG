package com.febrie.rpg.util.lang.quest.repeatable;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for RepeatableQuest quests
 */
public enum RepeatableQuestLangKey implements ILangKey {
    QUEST_REPEATABLE_MONSTER_EXTERMINATION_NAME("quest.repeatable.monster_extermination.name"),
    QUEST_REPEATABLE_MONSTER_EXTERMINATION_DESC("quest.repeatable.monster_extermination.desc"),
    QUEST_REPEATABLE_RESOURCE_COLLECTION_NAME("quest.repeatable.resource_collection.name"),
    QUEST_REPEATABLE_RESOURCE_COLLECTION_DESC("quest.repeatable.resource_collection.desc"),
    QUEST_REPEATABLE_EQUIPMENT_UPGRADE_NAME("quest.repeatable.equipment_upgrade.name"),
    QUEST_REPEATABLE_EQUIPMENT_UPGRADE_DESC("quest.repeatable.equipment_upgrade.desc"),
    QUEST_REPEATABLE_EQUIPMENT_UPGRADE_ACCEPT("quest.repeatable.equipment.upgrade.accept"),
    QUEST_REPEATABLE_EQUIPMENT_UPGRADE_DECLINE("quest.repeatable.equipment.upgrade.decline"),
    QUEST_REPEATABLE_EQUIPMENT_UPGRADE_DIALOGS("quest.repeatable.equipment.upgrade.dialogs"),
    QUEST_REPEATABLE_EQUIPMENT_UPGRADE_INFO("quest.repeatable.equipment.upgrade.info"),
    QUEST_REPEATABLE_EQUIPMENT_UPGRADE_NPC_NAME("quest.repeatable.equipment.upgrade.npc.name"),
    QUEST_REPEATABLE_EQUIPMENT_UPGRADE_OBJECTIVES_COLLECT_IRON("quest.repeatable.equipment.upgrade.objectives.collect.iron"),
    QUEST_REPEATABLE_EQUIPMENT_UPGRADE_OBJECTIVES_CRAFT_TOOLS("quest.repeatable.equipment.upgrade.objectives.craft.tools"),
    QUEST_REPEATABLE_EQUIPMENT_UPGRADE_OBJECTIVES_ENCHANT_ITEMS("quest.repeatable.equipment.upgrade.objectives.enchant.items"),
    QUEST_REPEATABLE_EQUIPMENT_UPGRADE_OBJECTIVES_REPAIR_ITEMS("quest.repeatable.equipment.upgrade.objectives.repair.items"),
    QUEST_REPEATABLE_MONSTER_EXTERMINATION_ACCEPT("quest.repeatable.monster.extermination.accept"),
    QUEST_REPEATABLE_MONSTER_EXTERMINATION_DECLINE("quest.repeatable.monster.extermination.decline"),
    QUEST_REPEATABLE_MONSTER_EXTERMINATION_DIALOGS("quest.repeatable.monster.extermination.dialogs"),
    QUEST_REPEATABLE_MONSTER_EXTERMINATION_INFO("quest.repeatable.monster.extermination.info"),
    QUEST_REPEATABLE_MONSTER_EXTERMINATION_NPC_NAME("quest.repeatable.monster.extermination.npc.name"),
    QUEST_REPEATABLE_MONSTER_EXTERMINATION_OBJECTIVES_KILL_BOSS_MONSTERS("quest.repeatable.monster.extermination.objectives.kill.boss.monsters"),
    QUEST_REPEATABLE_MONSTER_EXTERMINATION_OBJECTIVES_KILL_ENDERMEN("quest.repeatable.monster.extermination.objectives.kill.endermen"),
    QUEST_REPEATABLE_MONSTER_EXTERMINATION_OBJECTIVES_KILL_HOSTILE_MOBS("quest.repeatable.monster.extermination.objectives.kill.hostile.mobs"),
    QUEST_REPEATABLE_MONSTER_EXTERMINATION_OBJECTIVES_KILL_SPIDERS("quest.repeatable.monster.extermination.objectives.kill.spiders"),
    QUEST_REPEATABLE_MONSTER_EXTERMINATION_OBJECTIVES_KILL_WITCHES("quest.repeatable.monster.extermination.objectives.kill.witches"),
    QUEST_REPEATABLE_RESOURCE_COLLECTION_ACCEPT("quest.repeatable.resource.collection.accept"),
    QUEST_REPEATABLE_RESOURCE_COLLECTION_DECLINE("quest.repeatable.resource.collection.decline"),
    QUEST_REPEATABLE_RESOURCE_COLLECTION_DIALOGS("quest.repeatable.resource.collection.dialogs"),
    QUEST_REPEATABLE_RESOURCE_COLLECTION_INFO("quest.repeatable.resource.collection.info"),
    QUEST_REPEATABLE_RESOURCE_COLLECTION_NPC_NAME("quest.repeatable.resource.collection.npc.name"),
    QUEST_REPEATABLE_RESOURCE_COLLECTION_OBJECTIVES_CATCH_FISH("quest.repeatable.resource.collection.objectives.catch.fish"),
    QUEST_REPEATABLE_RESOURCE_COLLECTION_OBJECTIVES_COLLECT_GEMS("quest.repeatable.resource.collection.objectives.collect.gems"),
    QUEST_REPEATABLE_RESOURCE_COLLECTION_OBJECTIVES_COLLECT_STONE("quest.repeatable.resource.collection.objectives.collect.stone"),
    QUEST_REPEATABLE_RESOURCE_COLLECTION_OBJECTIVES_COLLECT_WOOD("quest.repeatable.resource.collection.objectives.collect.wood"),
    QUEST_REPEATABLE_RESOURCE_COLLECTION_OBJECTIVES_MINE_COAL("quest.repeatable.resource.collection.objectives.mine.coal"),
    QUEST_REPEATABLE_RESOURCE_COLLECTION_OBJECTIVES_MINE_IRON("quest.repeatable.resource.collection.objectives.mine.iron"),
    QUEST_REPEATABLE_EQUIPMENT_UPGRADE_OBJECTIVES_IRON_INGOT_COLLECT("quest.repeatable.equipment.upgrade.objectives.iron.ingot.collect"),
    QUEST_REPEATABLE_EQUIPMENT_UPGRADE_OBJECTIVES_IRON_PICKAXE_CRAFT("quest.repeatable.equipment.upgrade.objectives.iron.pickaxe.craft"),
    QUEST_REPEATABLE_RESOURCE_COLLECTION_OBJECTIVES_COBBLESTONE_COLLECT("quest.repeatable.resource.collection.objectives.cobblestone.collect"),
    QUEST_REPEATABLE_RESOURCE_COLLECTION_OBJECTIVES_DIAMOND_COLLECT("quest.repeatable.resource.collection.objectives.diamond.collect"),
    QUEST_REPEATABLE_RESOURCE_COLLECTION_OBJECTIVES_OAK_LOG_COLLECT("quest.repeatable.resource.collection.objectives.oak.log.collect"),
    QUEST_REPEATABLE_RESOURCE_COLLECTION_OBJECTIVES_SALMON_COLLECT("quest.repeatable.resource.collection.objectives.salmon.collect");

    private final String key;
    
    RepeatableQuestLangKey(String key) {
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
