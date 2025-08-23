package com.febrie.rpg.quest.impl.special;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
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
 * 세계수 - 특수 퀘스트
 * 죽어가는 세계수를 되살리는 자연의 대서사시
 *
 * @author Febrie
 */
public class WorldTreeQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class WorldTreeBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new WorldTreeQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public WorldTreeQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private WorldTreeQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new WorldTreeBuilder().id(QuestID.SPECIAL_WORLD_TREE)
                .objectives(Arrays.asList(
                        // 세계수의 위기
                        new InteractNPCObjective("tree_guardian", "world_tree_guardian"), // 세계수 수호자
                        new VisitLocationObjective("world_tree", "yggdrasil_base"), new CollectItemObjective("dead_leaves", Material.DEAD_BUSH, 20), new InteractNPCObjective("dying_tree", "dying_world_tree"), // 죽어가는 세계수

                        // 첫 번째 뿌리 - 생명의 샘
                        new VisitLocationObjective("life_spring", "root_of_life"), new BreakBlockObjective("clear_corruption", Material.NETHERRACK, 100), new KillMobObjective("corruption_spirits", EntityType.WITHER_SKELETON, 50), new CollectItemObjective("pure_water", Material.WATER_BUCKET, 10), new PlaceBlockObjective("purify_spring", Material.WATER, 20), new CollectItemObjective("life_essence", Material.GLISTERING_MELON_SLICE, 30),

                        // 두 번째 뿌리 - 지혜의 우물
                        new VisitLocationObjective("wisdom_well", "root_of_wisdom"), new InteractNPCObjective("well_keeper", "life_well_keeper"), // 우물 지기
                        new CollectItemObjective("ancient_runes", Material.ENCHANTED_BOOK, 15), new PayCurrencyObjective("wisdom_price", CurrencyType.DIAMOND, 50), new CraftItemObjective("wisdom_potion", Material.POTION, 20), new SurviveObjective("meditation", 600), // 10분 명상
                        new CollectItemObjective("wisdom_water", Material.EXPERIENCE_BOTTLE, 64),

                        // 세 번째 뿌리 - 운명의 가닥
                        new VisitLocationObjective("fate_threads", "root_of_fate"), new CollectItemObjective("fate_strings", Material.STRING, 100), new KillMobObjective("fate_weavers", EntityType.SPIDER, 60), new KillMobObjective("cave_spiders", EntityType.CAVE_SPIDER, 40), new PlaceBlockObjective("weave_fate", Material.COBWEB, 30), new CollectItemObjective("destiny_thread", Material.LEAD, 10),

                        // 네 번째 뿌리 - 시간의 강
                        new VisitLocationObjective("time_river", "root_of_time"), new CollectItemObjective("time_crystals", Material.PRISMARINE_CRYSTALS, 50), new BreakBlockObjective("time_stones", Material.END_STONE, 50), new SurviveObjective("time_flux", 900), // 15분 시간 왜곡
                        new CollectItemObjective("temporal_sand", Material.SOUL_SAND, 30), new CollectItemObjective("chronos_dust", Material.GLOWSTONE_DUST, 50),

                        // 다섯 번째 뿌리 - 꿈의 차원
                        new VisitLocationObjective("dream_dimension", "root_of_dreams"), new KillMobObjective("nightmares", EntityType.PHANTOM, 100), new CollectItemObjective("dream_shards", Material.AMETHYST_SHARD, 30), new PlaceBlockObjective("dream_catchers", Material.AMETHYST_BLOCK, 10), new SurviveObjective("lucid_dream", 600), // 10분 자각몽
                        new CollectItemObjective("dream_essence", Material.ECHO_SHARD, 20),

                        // 세계수 정화 의식
                        new VisitLocationObjective("tree_crown", "yggdrasil_crown"), new DeliverItemObjective("place_life", "tree_altar", Material.GLISTERING_MELON_SLICE, 30), new DeliverItemObjective("place_wisdom", "tree_altar", Material.EXPERIENCE_BOTTLE, 64), new DeliverItemObjective("place_fate", "tree_altar", Material.LEAD, 10), new DeliverItemObjective("place_time", "tree_altar", Material.GLOWSTONE_DUST, 50), new DeliverItemObjective("place_dream", "tree_altar", Material.ECHO_SHARD, 20),

                        // 세계수 부활
                        new PlaceBlockObjective("plant_seeds", Material.OAK_SAPLING, 50), new HarvestObjective("grow_forest", Material.OAK_LOG, 100), new CollectItemObjective("golden_apples", Material.GOLDEN_APPLE, 10), new PayCurrencyObjective("revival_cost", CurrencyType.GOLD, 50000), new SurviveObjective("tree_awakening", 1800), // 30분 각성

                        // 세계수의 선물
                        new InteractNPCObjective("revived_tree", "dying_world_tree"), new CollectItemObjective("world_fruit", Material.ENCHANTED_GOLDEN_APPLE, 3), new CollectItemObjective("eternal_leaves", Material.OAK_LEAVES, 100), new InteractNPCObjective("tree_blessing", "world_tree_guardian")))
                .reward(new BasicReward.Builder().addCurrency(CurrencyType.GOLD, 100000)
                        .addCurrency(CurrencyType.DIAMOND, 1000)
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 5)) // 세계수의 열매
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 3)) // 불멸의 토템
                        .addItem(new ItemStack(Material.ELYTRA)) // 세계수의 날개
                        .addItem(new ItemStack(Material.OAK_SAPLING, 64)) // 세계수 묘목
                        .addItem(new ItemStack(Material.BONE_MEAL, 128)) // 성장 촉진제
                        .addExperience(50000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SPECIAL)
                .minLevel(50)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.special.world_tree.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return List.of() /* TODO: Convert LangManager.getList("quest.special.world_tree.description") manually */;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "tree_guardian" -> Component.translatable("quest.special.world_tree.objectives.tree_guardian");
            case "world_tree_guardian" ->
                    Component.translatable("quest.special.world_tree.objectives.world_tree_guardian");
            case "world_tree" -> Component.translatable("quest.special.world_tree.objectives.world_tree");
            case "dead_leaves" -> Component.translatable("quest.special.world_tree.objectives.dead_leaves");
            case "dying_tree" -> Component.translatable("quest.special.world_tree.objectives.dying_tree");
            case "life_spring" -> Component.translatable("quest.special.world_tree.objectives.life_spring");
            case "clear_corruption" ->
                    Component.translatable("quest.special.world_tree.objectives.clear_corruption");
            case "corruption_spirits" ->
                    Component.translatable("quest.special.world_tree.objectives.corruption_spirits");
            case "pure_water" -> Component.translatable("quest.special.world_tree.objectives.pure_water");
            case "purify_spring" -> Component.translatable("quest.special.world_tree.objectives.purify_spring");
            case "life_essence" -> Component.translatable("quest.special.world_tree.objectives.life_essence");
            case "wisdom_well" -> Component.translatable("quest.special.world_tree.objectives.wisdom_well");
            case "well_keeper" -> Component.translatable("quest.special.world_tree.objectives.well_keeper");
            case "ancient_runes" -> Component.translatable("quest.special.world_tree.objectives.ancient_runes");
            case "wisdom_price" -> Component.translatable("quest.special.world_tree.objectives.wisdom_price");
            case "wisdom_potion" -> Component.translatable("quest.special.world_tree.objectives.wisdom_potion");
            case "meditation" -> Component.translatable("quest.special.world_tree.objectives.meditation");
            case "wisdom_water" -> Component.translatable("quest.special.world_tree.objectives.wisdom_water");
            case "fate_threads" -> Component.translatable("quest.special.world_tree.objectives.fate_threads");
            case "fate_strings" -> Component.translatable("quest.special.world_tree.objectives.fate_strings");
            case "fate_weavers" -> Component.translatable("quest.special.world_tree.objectives.fate_weavers");
            case "cave_spiders" -> Component.translatable("quest.special.world_tree.objectives.cave_spiders");
            case "weave_fate" -> Component.translatable("quest.special.world_tree.objectives.weave_fate");
            case "destiny_thread" -> Component.translatable("quest.special.world_tree.objectives.destiny_thread");
            case "time_river" -> Component.translatable("quest.special.world_tree.objectives.time_river");
            case "time_crystals" -> Component.translatable("quest.special.world_tree.objectives.time_crystals");
            case "time_stones" -> Component.translatable("quest.special.world_tree.objectives.time_stones");
            case "time_flux" -> Component.translatable("quest.special.world_tree.objectives.time_flux");
            case "temporal_sand" -> Component.translatable("quest.special.world_tree.objectives.temporal_sand");
            case "chronos_dust" -> Component.translatable("quest.special.world_tree.objectives.chronos_dust");
            case "dream_dimension" ->
                    Component.translatable("quest.special.world_tree.objectives.dream_dimension");
            case "nightmares" -> Component.translatable("quest.special.world_tree.objectives.nightmares");
            case "dream_shards" -> Component.translatable("quest.special.world_tree.objectives.dream_shards");
            case "dream_catchers" -> Component.translatable("quest.special.world_tree.objectives.dream_catchers");
            case "lucid_dream" -> Component.translatable("quest.special.world_tree.objectives.lucid_dream");
            case "dream_essence" -> Component.translatable("quest.special.world_tree.objectives.dream_essence");
            case "tree_crown" -> Component.translatable("quest.special.world_tree.objectives.tree_crown");
            case "place_life" -> Component.translatable("quest.special.world_tree.objectives.place_life");
            case "place_wisdom" -> Component.translatable("quest.special.world_tree.objectives.place_wisdom");
            case "place_fate" -> Component.translatable("quest.special.world_tree.objectives.place_fate");
            case "place_time" -> Component.translatable("quest.special.world_tree.objectives.place_time");
            case "place_dream" -> Component.translatable("quest.special.world_tree.objectives.place_dream");
            case "plant_seeds" -> Component.translatable("quest.special.world_tree.objectives.plant_seeds");
            case "grow_forest" -> Component.translatable("quest.special.world_tree.objectives.grow_forest");
            case "golden_apples" -> Component.translatable("quest.special.world_tree.objectives.golden_apples");
            case "revival_cost" -> Component.translatable("quest.special.world_tree.objectives.revival_cost");
            case "tree_awakening" -> Component.translatable("quest.special.world_tree.objectives.tree_awakening");
            case "revived_tree" -> Component.translatable("quest.special.world_tree.objectives.revived_tree");
            case "world_fruit" -> Component.translatable("quest.special.world_tree.objectives.world_fruit");
            case "eternal_leaves" -> Component.translatable("quest.special.world_tree.objectives.eternal_leaves");
            case "tree_blessing" -> Component.translatable("quest.special.world_tree.objectives.tree_blessing");
            default -> Component.translatable("quest.special.world_tree.objectives." + id);
        };
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("world_tree_dialog");

        // 시작
        dialog.addLine("quest.special.world_tree.npc.tree_guardian", "quest.special.world_tree.dialog.guardian.line1");
        dialog.addLine("quest.special.world_tree.npc.tree_guardian", "quest.special.world_tree.dialog.guardian.line2");
        dialog.addLine("quest.special.world_tree.npc.player", "quest.special.world_tree.dialog.player.line1");
        dialog.addLine("quest.special.world_tree.npc.tree_guardian", "quest.special.world_tree.dialog.guardian.line3");

        // 세계수와의 대화
        dialog.addLine("quest.special.world_tree.npc.dying_tree", "quest.special.world_tree.dialog.tree.line1");
        dialog.addLine("quest.special.world_tree.npc.dying_tree", "quest.special.world_tree.dialog.tree.line2");

        // 생명의 샘
        dialog.addLine("quest.special.world_tree.npc.tree_guardian", "quest.special.world_tree.dialog.guardian.line4");

        // 지혜의 우물
        dialog.addLine("quest.special.world_tree.npc.well_keeper", "quest.special.world_tree.dialog.keeper.line1");
        dialog.addLine("quest.special.world_tree.npc.well_keeper", "quest.special.world_tree.dialog.keeper.line2");

        // 부활 의식
        dialog.addLine("quest.special.world_tree.npc.tree_guardian", "quest.special.world_tree.dialog.guardian.line5");
        dialog.addLine("quest.special.world_tree.npc.tree_guardian", "quest.special.world_tree.dialog.guardian.line6");

        // 부활한 세계수
        dialog.addLine("quest.special.world_tree.npc.revived_tree", "quest.special.world_tree.dialog.tree.line3");
        dialog.addLine("quest.special.world_tree.npc.revived_tree", "quest.special.world_tree.dialog.tree.line4");

        // 완료
        dialog.addLine("quest.special.world_tree.npc.tree_guardian", "quest.special.world_tree.dialog.guardian.line7");
        dialog.addLine("quest.special.world_tree.npc.tree_guardian", "quest.special.world_tree.dialog.guardian.line8");

        return dialog;
    }
}
