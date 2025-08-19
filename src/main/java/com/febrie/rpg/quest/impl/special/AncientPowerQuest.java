package com.febrie.rpg.quest.impl.special;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 고대의 힘 - 특수 히든 퀘스트
 * 잊혀진 고대 신들의 힘을 얻는 비밀 퀘스트
 *
 * @author Febrie
 */
public class AncientPowerQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class AncientPowerBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new AncientPowerQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public AncientPowerQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private AncientPowerQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new AncientPowerBuilder()
                .id(QuestID.SPECIAL_ANCIENT_POWER)
                .objectives(Arrays.asList(
                        // 숨겨진 시작
                        new CollectItemObjective("mysterious_rune", Material.CRYING_OBSIDIAN, 1),
                        new VisitLocationObjective("hidden_shrine", "forgotten_god_shrine"),
                        new InteractNPCObjective("ancient_spirit", "ancient_spirit"), // 고대 영혼
                        
                        // 첫 번째 신 - 전쟁의 신
                        new VisitLocationObjective("war_temple", "temple_of_war"),
                        new KillMobObjective("prove_strength", EntityType.IRON_GOLEM, 20),
                        new KillMobObjective("defeat_warriors", EntityType.VINDICATOR, 50),
                        new KillPlayerObjective("pvp_kills", 10), // PvP 승리
                        new CollectItemObjective("warrior_souls", Material.IRON_NUGGET, 100),
                        new SurviveObjective("endless_battle", 1200), // 20분간 전투
                        new InteractNPCObjective("war_god_altar", "war_god_altar"), // 전쟁신 제단
                        new CollectItemObjective("war_blessing", Material.IRON_SWORD, 1),
                        
                        // 두 번째 신 - 지혜의 신
                        new VisitLocationObjective("wisdom_temple", "temple_of_wisdom"),
                        new CollectItemObjective("ancient_books", Material.WRITTEN_BOOK, 20),
                        new BreakBlockObjective("uncover_secrets", Material.BOOKSHELF, 50),
                        new CraftItemObjective("wisdom_elixir", Material.POTION, 30),
                        new PlaceBlockObjective("arrange_puzzle", Material.REDSTONE_LAMP, 16),
                        new SurviveObjective("mental_trial", 600), // 10분간 정신 시험
                        new InteractNPCObjective("wisdom_god_altar", "wisdom_god_altar"), // 지혜신 제단
                        new CollectItemObjective("wisdom_blessing", Material.ENCHANTED_BOOK, 1),
                        
                        // 세 번째 신 - 자연의 신
                        new VisitLocationObjective("nature_temple", "temple_of_nature"),
                        new HarvestObjective("nature_offering", Material.WHEAT, 100),
                        new CollectItemObjective("sacred_seeds", Material.WHEAT_SEEDS, 50),
                        new PlaceBlockObjective("plant_trees", Material.OAK_SAPLING, 20),
                        new KillMobObjective("protect_nature", EntityType.PILLAGER, 30),
                        new CollectItemObjective("nature_essence", Material.EMERALD, 30),
                        new InteractNPCObjective("nature_god_altar", "nature_god_altar"), // 자연신 제단
                        new CollectItemObjective("nature_blessing", Material.GOLDEN_APPLE, 1),
                        
                        // 네 번째 신 - 죽음의 신
                        new VisitLocationObjective("death_temple", "temple_of_death"),
                        new KillMobObjective("undead_army", EntityType.ZOMBIE, 100),
                        new KillMobObjective("skeleton_legion", EntityType.SKELETON, 80),
                        new KillMobObjective("death_knights", EntityType.WITHER_SKELETON, 40),
                        new CollectItemObjective("soul_fragments", Material.SOUL_SAND, 50),
                        new CollectItemObjective("death_tokens", Material.WITHER_SKELETON_SKULL, 5),
                        new KillMobObjective("death_avatar", EntityType.WITHER, 2),
                        new InteractNPCObjective("death_god_altar", "death_god_altar"), // 죽음신 제단
                        new CollectItemObjective("death_blessing", Material.TOTEM_OF_UNDYING, 1),
                        
                        // 다섯 번째 신 - 시간의 신
                        new VisitLocationObjective("time_temple", "temple_of_time"),
                        new CollectItemObjective("temporal_shards", Material.CLOCK, 10),
                        new PlaceBlockObjective("time_mechanism", Material.REPEATER, 20),
                        new SurviveObjective("time_loop", 900), // 15분간 시간 고리
                        new CollectItemObjective("past_artifact", Material.ANCIENT_DEBRIS, 5),
                        new CollectItemObjective("future_artifact", Material.NETHERITE_SCRAP, 5),
                        new InteractNPCObjective("time_god_altar", "time_god_altar"), // 시간신 제단
                        new CollectItemObjective("time_blessing", Material.ENDER_PEARL, 1),
                        
                        // 최종 의식 - 모든 축복 통합
                        new VisitLocationObjective("convergence_altar", "altar_of_convergence"),
                        new DeliverItemObjective("place_war", "convergence_altar", Material.IRON_SWORD, 1),
                        new DeliverItemObjective("place_wisdom", "convergence_altar", Material.ENCHANTED_BOOK, 1),
                        new DeliverItemObjective("place_nature", "convergence_altar", Material.GOLDEN_APPLE, 1),
                        new DeliverItemObjective("place_death", "convergence_altar", Material.TOTEM_OF_UNDYING, 1),
                        new DeliverItemObjective("place_time", "convergence_altar", Material.ENDER_PEARL, 1),
                        
                        // 고대의 힘 각성
                        new PayCurrencyObjective("final_offering", CurrencyType.DIAMOND, 100),
                        new SurviveObjective("power_awakening", 1800), // 30분간 각성 의식
                        new KillMobObjective("ancient_guardian", EntityType.ELDER_GUARDIAN, 5),
                        new CollectItemObjective("ancient_power_core", Material.NETHER_STAR, 5),
                        
                        // 완료
                        new InteractNPCObjective("power_granted", "ancient_spirit")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 100000)
                        .addCurrency(CurrencyType.DIAMOND, 1000)
                        .addItem(new ItemStack(Material.NETHER_STAR, 10)) // 고대의 힘 핵심
                        .addItem(new ItemStack(Material.NETHERITE_BLOCK, 5))
                        .addItem(new ItemStack(Material.BEACON, 5))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 10))
                        .addItem(new ItemStack(Material.DRAGON_EGG)) // 특별한 증표
                        .addExperience(50000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SPECIAL)
                .minLevel(60)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("special.ancient_power.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return List.of() /* TODO: Convert LangManager.getList("special.ancient_power.description") manually */;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return Component.translatable("special.ancient_power.objectives.");
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("ancient_power_dialog");
        
        dialog.addLine("quest.special_ancient_power.npcs.ancient_spirit", "quest.special_ancient_power.dialogs.start_line1");
        dialog.addLine("quest.special_ancient_power.npcs.ancient_spirit", "quest.special_ancient_power.dialogs.start_line2");
        dialog.addLine("quest.dialog.player", "quest.special_ancient_power.dialogs.start_line3");
        dialog.addLine("quest.special_ancient_power.npcs.ancient_spirit", "quest.special_ancient_power.dialogs.start_line4");
        dialog.addLine("quest.special_ancient_power.npcs.war_god", "quest.special_ancient_power.dialogs.war_god");
        dialog.addLine("quest.special_ancient_power.npcs.wisdom_god", "quest.special_ancient_power.dialogs.wisdom_god");
        dialog.addLine("quest.special_ancient_power.npcs.nature_god", "quest.special_ancient_power.dialogs.nature_god");
        dialog.addLine("quest.special_ancient_power.npcs.death_god", "quest.special_ancient_power.dialogs.death_god");
        dialog.addLine("quest.special_ancient_power.npcs.time_god", "quest.special_ancient_power.dialogs.time_god");
        dialog.addLine("quest.special_ancient_power.npcs.ancient_spirit", "quest.special_ancient_power.dialogs.convergence");
        dialog.addLine("quest.special_ancient_power.npcs.ancient_spirit", "quest.special_ancient_power.dialogs.awakening");
        dialog.addLine("quest.special_ancient_power.npcs.ancient_spirit", "quest.special_ancient_power.dialogs.complete_line1");
        dialog.addLine("quest.special_ancient_power.npcs.ancient_spirit", "quest.special_ancient_power.dialogs.complete_line2");

        return dialog;
    }
}