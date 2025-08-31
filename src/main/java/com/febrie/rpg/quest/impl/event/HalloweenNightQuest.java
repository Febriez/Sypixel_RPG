package com.febrie.rpg.quest.impl.event;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangHelper;
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
                .objectives(Arrays.asList(
                        // 할로윈 시작
                        new InteractNPCObjective("pumpkin_king", "pumpkin_king"), // 호박 왕
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
                        new InteractNPCObjective("witch_greeting", "halloween_witch"), // 마녀
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
                        new InteractNPCObjective("challenge_king", "pumpkin_king"),
                        new KillMobObjective("pumpkin_minions", EntityType.SNOW_GOLEM, 30),
                        new KillMobObjective("headless_horseman", EntityType.SKELETON_HORSE, 10),
                        new KillMobObjective("pumpkin_king_boss", EntityType.IRON_GOLEM, 3),
                        
                        // 할로윈 파티
                        new CollectItemObjective("party_treats", Material.CAKE, 5),
                        new DeliverItemObjective("deliver_treats", "villager", Material.COOKIE, 32),
                        new DeliverItemObjective("deliver_pies", "villager", Material.PUMPKIN_PIE, 10),
                        new InteractNPCObjective("halloween_party", "party_host"), // 파티 주최자
                        
                        // 보상 수령
                        new CollectItemObjective("halloween_mask", Material.CARVED_PUMPKIN, 1),
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
        return LangHelper.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return LangManager.get("quest.event.halloween_night.objectives." + id, who);
    }

    @Override
    public int getDialogCount() {
        return 12;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangHelper.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_DIALOGS_0, who);
            case 1 -> LangHelper.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_DIALOGS_1, who);
            case 2 -> LangHelper.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_DIALOGS_2, who);
            case 3 -> LangHelper.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_DIALOGS_3, who);
            case 4 -> LangHelper.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_DIALOGS_4, who);
            case 5 -> LangHelper.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_DIALOGS_5, who);
            case 6 -> LangHelper.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_DIALOGS_6, who);
            case 7 -> LangHelper.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_DIALOGS_7, who);
            case 8 -> LangHelper.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_DIALOGS_8, who);
            case 9 -> LangHelper.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_DIALOGS_9, who);
            case 10 -> LangHelper.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_DIALOGS_10, who);
            case 11 -> LangHelper.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_DIALOGS_11, who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_EVENT_HALLOWEEN_NIGHT_DECLINE, who);
    }
}