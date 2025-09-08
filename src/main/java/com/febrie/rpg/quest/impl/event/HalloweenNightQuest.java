package com.febrie.rpg.quest.impl.event;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * 할로윈의 밤 - 계절 이벤트 퀘스트
 * 매년 할로윈 시즌에만 진행 가능한 특별 퀘스트
 *
 * @author Febrie
 */
public class HalloweenNightQuest extends Quest {
    

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public HalloweenNightQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SEASON_HALLOWEEN_NIGHT)
                .objectives(List.of(
                        // 할로윈 시작
                        new InteractNPCObjective("pumpkin_king", "pumpkin_king", 1), // 호박 왕
                        new VisitLocationObjective("haunted_village", "spooky_village"),
                        
                        // 호박 수집
                        new HarvestObjective("harvest_pumpkins", Material.PUMPKIN, 50),
                        new CollectItemObjective("collect_pumpkins", Material.PUMPKIN, 30),
                        new CraftItemObjective("carve_lanterns", Material.JACK_O_LANTERN, 20),
                        new PlaceBlockObjective("decorate_village", Material.JACK_O_LANTERN, 20),
                        
                        // 사탕 만들기
                        new CollectItemObjective("sugar_collect", Material.SUGAR, 50),
                        new CollectItemObjective("cocoa_beans", Material.COCOA_BEANS, 30),
                        new CollectItemObjective("honey_collect", Material.HONEY_BOTTLE, 10),
                        new CraftItemObjective("make_cookies", Material.COOKIE, 64),
                        new CraftItemObjective("make_pies", Material.PUMPKIN_PIE, 20),
                        
                        // 유령의 숲 탐험
                        new VisitLocationObjective("ghost_forest", "haunted_forest"),
                        new KillMobObjective("spooky_zombies", EntityType.ZOMBIE, 50),
                        new KillMobObjective("skeleton_army", EntityType.SKELETON, 40),
                        new KillMobObjective("phantom_spirits", EntityType.PHANTOM, 30),
                        new CollectItemObjective("ghost_essence", Material.GHAST_TEAR, 10),
                        
                        // 마녀의 저택
                        new VisitLocationObjective("witch_mansion", "witchs_manor"),
                        new InteractNPCObjective("witch_greeting", "halloween_witch", 1), // 마녀
                        new KillMobObjective("witch_cats", EntityType.CAT, 15),
                        new KillMobObjective("evil_witches", EntityType.WITCH, 20),
                        new CollectItemObjective("witch_brew", Material.POTION, 20),
                        new CollectItemObjective("spider_eyes", Material.SPIDER_EYE, 30),
                        
                        // 유령의 시험
                        new VisitLocationObjective("ghost_realm", "spectral_dimension"),
                        new SurviveObjective("ghost_maze", 600), // 10분간 유령 미로
                        new CollectItemObjective("soul_fragments", Material.SOUL_SAND, 20),
                        new KillMobObjective("vengeful_spirits", EntityType.VEX, 40),
                        
                        // 불길한 의식
                        new VisitLocationObjective("ritual_site", "dark_altar"),
                        new PlaceBlockObjective("place_candles", Material.CANDLE, 13),
                        new PlaceBlockObjective("place_skulls", Material.SKELETON_SKULL, 6),
                        new PayCurrencyObjective("ritual_offering", CurrencyType.GOLD, 6666),
                        new KillMobObjective("summoned_demon", EntityType.WITHER_SKELETON, 66),
                        
                        // 호박 왕과의 대결
                        new InteractNPCObjective("challenge_king", "pumpkin_king", 1),
                        new KillMobObjective("pumpkin_minions", EntityType.SNOW_GOLEM, 30),
                        new KillMobObjective("headless_horseman", EntityType.SKELETON_HORSE, 10),
                        new KillMobObjective("pumpkin_king_boss", EntityType.IRON_GOLEM, 3),
                        
                        // 할로윈 파티
                        new CollectItemObjective("party_treats", Material.CAKE, 5),
                        new DeliverItemObjective("deliver_treats", "villager", Material.COOKIE, 32),
                        new DeliverItemObjective("deliver_pies", "villager", Material.PUMPKIN_PIE, 10),
                        new InteractNPCObjective("halloween_party", "party_host", 1), // 파티 주최자
                        
                        // 보상 수령
                        new CollectItemObjective("halloween_mask", Material.CARVED_PUMPKIN, 1),
                        new InteractNPCObjective("event_complete", "pumpkin_king", 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 13000)
                        .addCurrency(CurrencyType.DIAMOND, 66)
                        .addItem(new ItemStack(Material.CARVED_PUMPKIN)) // 특별 할로윈 마스크
                        .addItem(new ItemStack(Material.SKELETON_SKULL, 3))
                        .addItem(new ItemStack(Material.ZOMBIE_HEAD, 3))
                        .addItem(new ItemStack(Material.CREEPER_HEAD, 3))
                        .addItem(new ItemStack(Material.BAT_SPAWN_EGG, 5))
                        .addItem(new ItemStack(Material.JACK_O_LANTERN, 20))
                        .addExperience(6666)
                        .build())
                .sequential(true)
                .repeatable(true)  // 매년 반복 가능
                .category(QuestCategory.EVENT)
                .minLevel(15)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_INFO, who);
    }

        @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "pumpkin_king" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_PUMPKIN_KING, who);
            case "haunted_village" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_HAUNTED_VILLAGE, who);
            case "harvest_pumpkins" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_HARVEST_PUMPKINS, who);
            case "collect_pumpkins" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_COLLECT_PUMPKINS, who);
            case "carve_lanterns" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_CARVE_LANTERNS, who);
            case "decorate_village" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_DECORATE_VILLAGE, who);
            case "sugar_collect" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_SUGAR_COLLECT, who);
            case "cocoa_beans" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_COCOA_BEANS, who);
            case "honey_collect" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_HONEY_COLLECT, who);
            case "make_cookies" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_MAKE_COOKIES, who);
            case "make_pies" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_MAKE_PIES, who);
            case "ghost_forest" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_GHOST_FOREST, who);
            case "spooky_zombies" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_SPOOKY_ZOMBIES, who);
            case "skeleton_army" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_SKELETON_ARMY, who);
            case "phantom_spirits" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_PHANTOM_SPIRITS, who);
            case "ghost_essence" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_GHOST_ESSENCE, who);
            case "witch_mansion" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_WITCH_MANSION, who);
            case "witch_greeting" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_WITCH_GREETING, who);
            case "witch_cats" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_WITCH_CATS, who);
            case "evil_witches" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_EVIL_WITCHES, who);
            case "witch_brew" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_WITCH_BREW, who);
            case "spider_eyes" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_SPIDER_EYES, who);
            case "ghost_realm" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_GHOST_REALM, who);
            case "ghost_maze" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_GHOST_MAZE, who);
            case "soul_fragments" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_SOUL_FRAGMENTS, who);
            case "vengeful_spirits" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_VENGEFUL_SPIRITS, who);
            case "ritual_site" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_RITUAL_SITE, who);
            case "place_candles" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_PLACE_CANDLES, who);
            case "place_skulls" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_PLACE_SKULLS, who);
            case "ritual_offering" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_RITUAL_OFFERING, who);
            case "summoned_demon" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_SUMMONED_DEMON, who);
            case "challenge_king" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_CHALLENGE_KING, who);
            case "pumpkin_minions" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_PUMPKIN_MINIONS, who);
            case "headless_horseman" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_HEADLESS_HORSEMAN, who);
            case "pumpkin_king_boss" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_PUMPKIN_KING_BOSS, who);
            case "party_treats" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_PARTY_TREATS, who);
            case "deliver_treats" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_DELIVER_TREATS, who);
            case "deliver_pies" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_DELIVER_PIES, who);
            case "halloween_party" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_HALLOWEEN_PARTY, who);
            case "halloween_mask" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_HALLOWEEN_MASK, who);
            case "event_complete" -> LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_OBJECTIVES_EVENT_COMPLETE, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() {
        return 12;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_DECLINE, who);
    }
}