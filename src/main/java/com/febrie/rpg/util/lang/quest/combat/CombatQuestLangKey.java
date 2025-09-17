package com.febrie.rpg.util.lang.quest.combat;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for CombatQuest quests
 */
public enum CombatQuestLangKey implements ILangKey {
    QUEST_COMBAT_ARENA_GLADIATOR_NAME("quest.combat.arena_gladiator.name"),
    QUEST_COMBAT_ARENA_GLADIATOR_DESC("quest.combat.arena_gladiator.desc"),
    QUEST_COMBAT_BOSS_SLAYER_NAME("quest.combat.boss_slayer.name"),
    QUEST_COMBAT_BOSS_SLAYER_DESC("quest.combat.boss_slayer.desc"),
    QUEST_COMBAT_SURVIVAL_EXPERT_NAME("quest.combat.survival_expert.name"),
    QUEST_COMBAT_SURVIVAL_EXPERT_DESC("quest.combat.survival_expert.desc"),
    QUEST_COMBAT_ARENA_GLADIATOR_ACCEPT("quest.combat.arena.gladiator.accept"),
    QUEST_COMBAT_ARENA_GLADIATOR_DECLINE("quest.combat.arena.gladiator.decline"),
    QUEST_COMBAT_ARENA_GLADIATOR_DIALOGS("quest.combat.arena.gladiator.dialogs"),
    QUEST_COMBAT_ARENA_GLADIATOR_INFO("quest.combat.arena.gladiator.info"),
    QUEST_COMBAT_ARENA_GLADIATOR_NPC_NAME("quest.combat.arena.gladiator.npc.name"),
    QUEST_COMBAT_ARENA_GLADIATOR_OBJECTIVES_DEFEAT_PLAYERS("quest.combat.arena.gladiator.objectives.defeat.players"),
    QUEST_COMBAT_ARENA_GLADIATOR_OBJECTIVES_PVP_KILLS("quest.combat.arena.gladiator.objectives.pvp.kills"),
    QUEST_COMBAT_ARENA_GLADIATOR_OBJECTIVES_SURVIVE_COMBAT("quest.combat.arena.gladiator.objectives.survive.combat"),
    QUEST_COMBAT_BOSS_SLAYER_ACCEPT("quest.combat.boss.slayer.accept"),
    QUEST_COMBAT_BOSS_SLAYER_DECLINE("quest.combat.boss.slayer.decline"),
    QUEST_COMBAT_BOSS_SLAYER_DIALOGS("quest.combat.boss.slayer.dialogs"),
    QUEST_COMBAT_BOSS_SLAYER_INFO("quest.combat.boss.slayer.info"),
    QUEST_COMBAT_BOSS_SLAYER_NPC_NAME("quest.combat.boss.slayer.npc.name"),
    QUEST_COMBAT_BOSS_SLAYER_OBJECTIVES_COLLECT_BOSS_DROPS("quest.combat.boss.slayer.objectives.collect.boss.drops"),
    QUEST_COMBAT_BOSS_SLAYER_OBJECTIVES_KILL_DUNGEON_BOSSES("quest.combat.boss.slayer.objectives.kill.dungeon.bosses"),
    QUEST_COMBAT_BOSS_SLAYER_OBJECTIVES_KILL_MINI_BOSSES("quest.combat.boss.slayer.objectives.kill.mini.bosses"),
    QUEST_COMBAT_BOSS_SLAYER_OBJECTIVES_KILL_WITHER_SKELETONS("quest.combat.boss.slayer.objectives.kill.wither.skeletons"),
    QUEST_COMBAT_BOSS_SLAYER_OBJECTIVES_KILL_WORLD_BOSS("quest.combat.boss.slayer.objectives.kill.world.boss"),
    QUEST_COMBAT_SURVIVAL_EXPERT_ACCEPT("quest.combat.survival.expert.accept"),
    QUEST_COMBAT_SURVIVAL_EXPERT_DECLINE("quest.combat.survival.expert.decline"),
    QUEST_COMBAT_SURVIVAL_EXPERT_DIALOGS("quest.combat.survival.expert.dialogs"),
    QUEST_COMBAT_SURVIVAL_EXPERT_INFO("quest.combat.survival.expert.info"),
    QUEST_COMBAT_SURVIVAL_EXPERT_NPC_NAME("quest.combat.survival.expert.npc.name"),
    QUEST_COMBAT_SURVIVAL_EXPERT_OBJECTIVES_COLLECT_SURVIVAL_SUPPLIES("quest.combat.survival.expert.objectives.collect.survival.supplies"),
    QUEST_COMBAT_SURVIVAL_EXPERT_OBJECTIVES_KILL_WHILE_SURVIVING("quest.combat.survival.expert.objectives.kill.while.surviving"),
    QUEST_COMBAT_SURVIVAL_EXPERT_OBJECTIVES_REACH_SURVIVAL_LEVEL("quest.combat.survival.expert.objectives.reach.survival.level"),
    QUEST_COMBAT_SURVIVAL_EXPERT_OBJECTIVES_SURVIVE_ENEMY_WAVES("quest.combat.survival.expert.objectives.survive.enemy.waves"),
    QUEST_COMBAT_BOSS_SLAYER_OBJECTIVES_NETHER_STAR_COLLECT("quest.combat.boss.slayer.objectives.nether.star.collect"),
    QUEST_COMBAT_SURVIVAL_EXPERT_OBJECTIVES_BREAD_COLLECT("quest.combat.survival.expert.objectives.bread.collect");

    private final String key;
    
    CombatQuestLangKey(String key) {
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
