package com.febrie.rpg.util.lang.quest.event;

import com.febrie.rpg.util.lang.ILangKey;

/**
 * Language keys for EventQuest quests
 */
public enum EventQuestLangKey implements ILangKey {
    QUEST_EVENT_HALLOWEEN_NIGHT_NAME("quest.event.halloween_night.name"),
    QUEST_EVENT_HALLOWEEN_NIGHT_DESC("quest.event.halloween_night.desc"),
    QUEST_EVENT_HALLOWEEN_NIGHT_ACCEPT("quest.event.halloween.night.accept"),
    QUEST_EVENT_HALLOWEEN_NIGHT_DECLINE("quest.event.halloween.night.decline"),
    QUEST_EVENT_HALLOWEEN_NIGHT_DIALOGS("quest.event.halloween.night.dialogs"),
    QUEST_EVENT_HALLOWEEN_NIGHT_INFO("quest.event.halloween.night.info"),
    QUEST_EVENT_HALLOWEEN_NIGHT_NPC_NAME("quest.event.halloween.night.npc.name"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_CARVE_LANTERNS("quest.event.halloween.night.objectives.carve.lanterns"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_CHALLENGE_KING("quest.event.halloween.night.objectives.challenge.king"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_COCOA_BEANS("quest.event.halloween.night.objectives.cocoa.beans"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_COLLECT_PUMPKINS("quest.event.halloween.night.objectives.collect.pumpkins"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_DECORATE_VILLAGE("quest.event.halloween.night.objectives.decorate.village"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_DELIVER_PIES("quest.event.halloween.night.objectives.deliver.pies"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_DELIVER_TREATS("quest.event.halloween.night.objectives.deliver.treats"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_EVENT_COMPLETE("quest.event.halloween.night.objectives.event.complete"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_EVIL_WITCHES("quest.event.halloween.night.objectives.evil.witches"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_GHOST_ESSENCE("quest.event.halloween.night.objectives.ghost.essence"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_GHOST_FOREST("quest.event.halloween.night.objectives.ghost.forest"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_GHOST_MAZE("quest.event.halloween.night.objectives.ghost.maze"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_GHOST_REALM("quest.event.halloween.night.objectives.ghost.realm"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_HALLOWEEN_MASK("quest.event.halloween.night.objectives.halloween.mask"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_HALLOWEEN_PARTY("quest.event.halloween.night.objectives.halloween.party"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_HARVEST_PUMPKINS("quest.event.halloween.night.objectives.harvest.pumpkins"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_HAUNTED_VILLAGE("quest.event.halloween.night.objectives.haunted.village"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_HEADLESS_HORSEMAN("quest.event.halloween.night.objectives.headless.horseman"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_HONEY_COLLECT("quest.event.halloween.night.objectives.honey.collect"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_MAKE_COOKIES("quest.event.halloween.night.objectives.make.cookies"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_MAKE_PIES("quest.event.halloween.night.objectives.make.pies"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_PARTY_TREATS("quest.event.halloween.night.objectives.party.treats"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_PHANTOM_SPIRITS("quest.event.halloween.night.objectives.phantom.spirits"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_PLACE_CANDLES("quest.event.halloween.night.objectives.place.candles"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_PLACE_SKULLS("quest.event.halloween.night.objectives.place.skulls"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_PUMPKIN_KING("quest.event.halloween.night.objectives.pumpkin.king"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_PUMPKIN_KING_BOSS("quest.event.halloween.night.objectives.pumpkin.king.boss"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_PUMPKIN_MINIONS("quest.event.halloween.night.objectives.pumpkin.minions"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_RITUAL_OFFERING("quest.event.halloween.night.objectives.ritual.offering"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_RITUAL_SITE("quest.event.halloween.night.objectives.ritual.site"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_SKELETON_ARMY("quest.event.halloween.night.objectives.skeleton.army"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_SOUL_FRAGMENTS("quest.event.halloween.night.objectives.soul.fragments"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_SPIDER_EYES("quest.event.halloween.night.objectives.spider.eyes"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_SPOOKY_ZOMBIES("quest.event.halloween.night.objectives.spooky.zombies"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_SUGAR_COLLECT("quest.event.halloween.night.objectives.sugar.collect"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_SUMMONED_DEMON("quest.event.halloween.night.objectives.summoned.demon"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_VENGEFUL_SPIRITS("quest.event.halloween.night.objectives.vengeful.spirits"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_WITCH_BREW("quest.event.halloween.night.objectives.witch.brew"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_WITCH_CATS("quest.event.halloween.night.objectives.witch.cats"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_WITCH_GREETING("quest.event.halloween.night.objectives.witch.greeting"),
    QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_WITCH_MANSION("quest.event.halloween.night.objectives.witch.mansion");

    private final String key;
    
    EventQuestLangKey(String key) {
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
