package com.febrie.rpg.quest.impl.event;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
                        new InteractNPCObjective("pumpkin_king", "pumpkin_king"), // 호박 왕
                        new VisitLocationObjective("haunted_village", "spooky_village"),
                        
                        // 호박 수집
                        new HarvestObjective("harvest_pumpkins", Material.PUMPKIN, 50),
                        new CollectItemObjective("pumpkin_collect", Material.PUMPKIN, 30),
                        new CraftItemObjective("jack_o_lantern_craft", Material.JACK_O_LANTERN, 20),
                        new PlaceBlockObjective("decorate_village", Material.JACK_O_LANTERN, 20),
                        
                        // 사탕 만들기
                        new CollectItemObjective("sugar_collect", Material.SUGAR, 50),
                        new CollectItemObjective("cocoa_beans_collect", Material.COCOA_BEANS, 30),
                        new CollectItemObjective("honey_bottle_collect", Material.HONEY_BOTTLE, 10),
                        new CraftItemObjective("cookie_craft", Material.COOKIE, 64),
                        new CraftItemObjective("pumpkin_pie_craft", Material.PUMPKIN_PIE, 20),
                        
                        // 유령의 숲 탐험
                        new VisitLocationObjective("ghost_forest", "haunted_forest"),
                        new KillMobObjective("spooky_zombies", EntityType.ZOMBIE, 50),
                        new KillMobObjective("skeleton_army", EntityType.SKELETON, 40),
                        new KillMobObjective("phantom_spirits", EntityType.PHANTOM, 30),
                        new CollectItemObjective("ghast_tear_collect", Material.GHAST_TEAR, 10),
                        
                        // 마녀의 저택
                        new VisitLocationObjective("witch_mansion", "witchs_manor"),
                        new InteractNPCObjective("witch_greeting", "halloween_witch"), // 마녀
                        new KillMobObjective("witch_cats", EntityType.CAT, 15),
                        new KillMobObjective("evil_witches", EntityType.WITCH, 20),
                        new CollectItemObjective("potion_collect", Material.POTION, 20),
                        new CollectItemObjective("spider_eye_collect", Material.SPIDER_EYE, 30),
                        
                        // 유령의 시험
                        new VisitLocationObjective("ghost_realm", "spectral_dimension"),
                        new SurviveObjective("ghost_maze", 600), // 10분간 유령 미로
                        new CollectItemObjective("soul_sand_collect", Material.SOUL_SAND, 20),
                        new KillMobObjective("vengeful_spirits", EntityType.VEX, 40),
                        
                        // 불길한 의식
                        new VisitLocationObjective("ritual_site", "dark_altar"),
                        new PlaceBlockObjective("place_candles", Material.CANDLE, 13),
                        new PlaceBlockObjective("place_skulls", Material.SKELETON_SKULL, 6),
                        new PayCurrencyObjective("ritual_offering", CurrencyType.GOLD, 6666),
                        new KillMobObjective("summoned_demon", EntityType.WITHER_SKELETON, 66),
                        
                        // 호박 왕과의 대결
                        new InteractNPCObjective("challenge_king", "pumpkin_king"),
                        new KillMobObjective("pumpkin_minions", EntityType.SNOW_GOLEM, 30),
                        new KillMobObjective("headless_horseman", EntityType.SKELETON_HORSE, 10),
                        new KillMobObjective("pumpkin_king_boss", EntityType.IRON_GOLEM, 3),
                        
                        // 할로윈 파티
                        new CollectItemObjective("cake_collect", Material.CAKE, 5),
                        new DeliverItemObjective("cookie_deliver", Material.COOKIE, 32, "villager"),
                        new DeliverItemObjective("pumpkin_pie_deliver", Material.PUMPKIN_PIE, 10, "villager"),
                        new InteractNPCObjective("halloween_party", "party_host"), // 파티 주최자
                        
                        // 보상 수령
                        new CollectItemObjective("carved_pumpkin_collect", Material.CARVED_PUMPKIN, 1),
                        new InteractNPCObjective("event_complete", "pumpkin_king")
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
        return LangManager.text(QuestCommonLangKey.QUEST_EVENT_HALLOWEEN_NIGHT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_EVENT_HALLOWEEN_NIGHT_INFO, who);
    }

        @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "pumpkin_king" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_PUMPKIN_KING, who);
            case "haunted_village" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_HAUNTED_VILLAGE, who);
            case "harvest_pumpkins" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_HARVEST_PUMPKINS, who);
            case "pumpkin_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_PUMPKIN_COLLECT, who);
            case "jack_o_lantern_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_JACK_O_LANTERN_CRAFT, who);
            case "decorate_village" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_DECORATE_VILLAGE, who);
            case "sugar_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_SUGAR_COLLECT, who);
            case "cocoa_beans_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_COCOA_BEANS_COLLECT, who);
            case "honey_bottle_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_HONEY_BOTTLE_COLLECT, who);
            case "cookie_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_COOKIE_CRAFT, who);
            case "pumpkin_pie_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_PUMPKIN_PIE_CRAFT, who);
            case "ghost_forest" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_GHOST_FOREST, who);
            case "spooky_zombies" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_SPOOKY_ZOMBIES, who);
            case "skeleton_army" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_SKELETON_ARMY, who);
            case "phantom_spirits" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_PHANTOM_SPIRITS, who);
            case "ghast_tear_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_GHAST_TEAR_COLLECT, who);
            case "witch_mansion" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_WITCH_MANSION, who);
            case "witch_greeting" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_WITCH_GREETING, who);
            case "witch_cats" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_WITCH_CATS, who);
            case "evil_witches" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_EVIL_WITCHES, who);
            case "potion_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_POTION_COLLECT, who);
            case "spider_eye_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_SPIDER_EYE_COLLECT, who);
            case "ghost_realm" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_GHOST_REALM, who);
            case "ghost_maze" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_GHOST_MAZE, who);
            case "soul_sand_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_SOUL_SAND_COLLECT, who);
            case "vengeful_spirits" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_VENGEFUL_SPIRITS, who);
            case "ritual_site" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_RITUAL_SITE, who);
            case "place_candles" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_PLACE_CANDLES, who);
            case "place_skulls" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_PLACE_SKULLS, who);
            case "ritual_offering" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_RITUAL_OFFERING, who);
            case "summoned_demon" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_SUMMONED_DEMON, who);
            case "challenge_king" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_CHALLENGE_KING, who);
            case "pumpkin_minions" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_PUMPKIN_MINIONS, who);
            case "headless_horseman" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_HEADLESS_HORSEMAN, who);
            case "pumpkin_king_boss" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_PUMPKIN_KING_BOSS, who);
            case "cake_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_CAKE_COLLECT, who);
            case "cookie_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_COOKIE_DELIVER, who);
            case "pumpkin_pie_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_PUMPKIN_PIE_DELIVER, who);
            case "halloween_party" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_HALLOWEEN_PARTY, who);
            case "carved_pumpkin_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_CARVED_PUMPKIN_COLLECT, who);
            case "event_complete" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_HALLOWEEN_NIGHT_OBJECTIVES_EVENT_COMPLETE, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 12;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_EVENT_HALLOWEEN_NIGHT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_EVENT_HALLOWEEN_NIGHT_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_EVENT_HALLOWEEN_NIGHT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_EVENT_HALLOWEEN_NIGHT_DECLINE, who);
    }
}