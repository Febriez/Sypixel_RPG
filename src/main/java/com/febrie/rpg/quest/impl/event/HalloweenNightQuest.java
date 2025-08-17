package com.febrie.rpg.quest.impl.event;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
     * 퀘스트 빌더
     */
    private static class HalloweenNightBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new HalloweenNightQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public HalloweenNightQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private HalloweenNightQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new HalloweenNightBuilder()
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
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.halloween_night.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getList(who, "quest.halloween_night.info");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.halloween_night.objective." + id);
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("halloween_night_dialog");

        // 시작
        dialog.addLine("quest.halloween_night.dialog.npc1",
                "quest.halloween_night.dialog.npc1");

        dialog.addLine("quest.halloween_night.dialog.npc2",
                "quest.halloween_night.dialog.npc2");

        dialog.addLine("quest.halloween_night.dialog.player1",
                "quest.halloween_night.dialog.player1");

        dialog.addLine("quest.halloween_night.dialog.npc3",
                "quest.halloween_night.dialog.npc3");

        // 마녀와의 만남
        dialog.addLine("quest.halloween_night.dialog.witch1",
                "quest.halloween_night.dialog.witch1");

        dialog.addLine("quest.halloween_night.dialog.witch2",
                "quest.halloween_night.dialog.witch2");

        // 유령의 숲
        dialog.addLine("quest.halloween_night.dialog.ghost1",
                "quest.halloween_night.dialog.ghost1");

        // 최종 대결
        dialog.addLine("quest.halloween_night.dialog.npc4",
                "quest.halloween_night.dialog.npc4");

        dialog.addLine("quest.halloween_night.dialog.npc5",
                "quest.halloween_night.dialog.npc5");

        // 파티
        dialog.addLine("quest.halloween_night.dialog.party1",
                "quest.halloween_night.dialog.party1");

        // 완료
        dialog.addLine("quest.halloween_night.dialog.npc6",
                "quest.halloween_night.dialog.npc6");

        dialog.addLine("quest.halloween_night.dialog.npc7",
                "quest.halloween_night.dialog.npc7");

        return dialog;
    }
}