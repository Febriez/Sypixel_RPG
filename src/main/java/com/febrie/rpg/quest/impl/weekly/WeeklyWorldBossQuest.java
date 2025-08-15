package com.febrie.rpg.quest.impl.weekly;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
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

import java.util.Arrays;
import java.util.List;

/**
 * 주간 월드 보스 - 주간 퀘스트
 * 서버 전체가 협력하여 거대한 월드 보스를 처치하는 대규모 이벤트
 *
 * @author Febrie
 */
public class WeeklyWorldBossQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class WeeklyWorldBossBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new WeeklyWorldBossQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public WeeklyWorldBossQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private WeeklyWorldBossQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new WeeklyWorldBossBuilder().id(QuestID.WEEKLY_WORLD_BOSS)
                .objectives(Arrays.asList(
                        // 월드 보스 출현 준비
                        new InteractNPCObjective("world_herald", "world_boss_herald"), // 월드 전령
                        new VisitLocationObjective("boss_spawn", "titan_summoning_grounds"),

                        // 소환 의식 준비
                        new CollectItemObjective("summoning_crystals", Material.END_CRYSTAL, 8), new CollectItemObjective("titan_essence", Material.ECHO_SHARD, 20), new CollectItemObjective("ancient_runes", Material.ENCHANTED_BOOK, 5), new PayCurrencyObjective("ritual_cost", CurrencyType.GOLD, 10000),

                        // 제단 활성화
                        new PlaceBlockObjective("place_crystals", Material.END_CRYSTAL, 8), new PlaceBlockObjective("place_beacons", Material.BEACON, 4), new InteractNPCObjective("start_ritual", "world_boss_herald"),

                        // 첫 번째 단계 - 타이탄의 하수인들
                        new KillMobObjective("titan_minions", EntityType.GIANT, 10), new KillMobObjective("elemental_guards", EntityType.BLAZE, 50), new KillMobObjective("shadow_priests", EntityType.EVOKER, 20), new SurviveObjective("first_wave", 600), // 10분간 생존

                        // 두 번째 단계 - 타이탄의 장군들
                        new KillMobObjective("fire_general", EntityType.MAGMA_CUBE, 5), new KillMobObjective("ice_general", EntityType.STRAY, 30), new KillMobObjective("earth_general", EntityType.IRON_GOLEM, 10), new KillMobObjective("wind_general", EntityType.PHANTOM, 40), new CollectItemObjective("general_cores", Material.NETHER_STAR, 4),

                        // 세 번째 단계 - 타이탄 각성
                        new VisitLocationObjective("titan_arena", "world_boss_arena"), new PlaceBlockObjective("activate_cores", Material.NETHER_STAR, 4), new SurviveObjective("titan_roar", 300), // 5분간 타이탄의 포효 견디기

                        // 최종 전투 - 세계의 타이탄
                        new KillMobObjective("world_titan_phase1", EntityType.WITHER, 1), new CollectItemObjective("titan_heart", Material.BEACON, 1), new KillMobObjective("world_titan_phase2", EntityType.ENDER_DRAGON, 1), new CollectItemObjective("titan_soul", Material.DRAGON_EGG, 1), new KillMobObjective("world_titan_final", EntityType.WARDEN, 3),

                        // 전리품 수집
                        new CollectItemObjective("titan_scales", Material.NETHERITE_SCRAP, 10), new CollectItemObjective("titan_blood", Material.REDSTONE_BLOCK, 20), new CollectItemObjective("titan_bones", Material.BONE_BLOCK, 30),

                        // 보상 수령
                        new DeliverItemObjective("deliver_heart", "world_herald", Material.BEACON, 1), new DeliverItemObjective("deliver_soul", "world_herald", Material.DRAGON_EGG, 1), new InteractNPCObjective("claim_rewards", "world_boss_herald")))
                .reward(new BasicReward.Builder().addCurrency(CurrencyType.GOLD, 30000)
                        .addCurrency(CurrencyType.DIAMOND, 200)
                        .addItem(new ItemStack(Material.NETHERITE_CHESTPLATE))
                        .addItem(new ItemStack(Material.ELYTRA))
                        .addItem(new ItemStack(Material.NETHER_STAR, 5))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 10))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 3))
                        .addExperience(15000)
                        .build())
                .sequential(true)
                .repeatable(true)
                .weekly(true)      // 주간 퀘스트
                .category(QuestCategory.WEEKLY)
                .minLevel(45)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.getMessage(who, "quest.weekly.world_boss.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList(who, "quest.weekly.world_boss.description");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "world_herald" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.world_herald");
            case "boss_spawn" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.boss_spawn");
            case "summoning_crystals" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.summoning_crystals");
            case "titan_essence" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.titan_essence");
            case "ancient_runes" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.ancient_runes");
            case "ritual_cost" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.ritual_cost");
            case "place_crystals" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.place_crystals");
            case "place_beacons" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.place_beacons");
            case "start_ritual" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.start_ritual");
            case "titan_minions" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.titan_minions");
            case "elemental_guards" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.elemental_guards");
            case "shadow_priests" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.shadow_priests");
            case "first_wave" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.first_wave");
            case "fire_general" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.fire_general");
            case "ice_general" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.ice_general");
            case "earth_general" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.earth_general");
            case "wind_general" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.wind_general");
            case "general_cores" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.general_cores");
            case "titan_arena" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.titan_arena");
            case "activate_cores" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.activate_cores");
            case "titan_roar" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.titan_roar");
            case "world_titan_phase1" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.world_titan_phase1");
            case "titan_heart" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.titan_heart");
            case "world_titan_phase2" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.world_titan_phase2");
            case "titan_soul" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.titan_soul");
            case "world_titan_final" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.world_titan_final");
            case "titan_scales" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.titan_scales");
            case "titan_blood" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.titan_blood");
            case "titan_bones" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.titan_bones");
            case "deliver_heart" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.deliver_heart");
            case "deliver_soul" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.deliver_soul");
            case "claim_rewards" ->
                    LangManager.getMessage(who, "quest.weekly.world_boss.objectives.claim_rewards");
            default -> Component.text(objective.getStatusInfo(null));
        };
    }

    @Override
    public QuestDialog getDialog(@NotNull Player player) {
        QuestDialog dialog = new QuestDialog("weekly_world_boss_dialog");

        // 시작
        dialog.addLine("quest.weekly_world_boss.npcs.world_herald", "quest.weekly_world_boss.dialogs.line1");

        dialog.addLine("quest.weekly_world_boss.npcs.world_herald", "quest.weekly_world_boss.dialogs.line2");

        dialog.addLine("quest.dialog.player", "quest.weekly_world_boss.dialogs.line3");

        dialog.addLine("quest.weekly_world_boss.npcs.world_herald", "quest.weekly_world_boss.dialogs.line4");

        // 소환 의식
        dialog.addLine("quest.weekly_world_boss.npcs.world_herald", "quest.weekly_world_boss.dialogs.line5");

        // 전투 중
        dialog.addLine("quest.weekly_world_boss.npcs.world_herald", "quest.weekly_world_boss.dialogs.line6");

        dialog.addLine("quest.weekly_world_boss.npcs.world_herald", "quest.weekly_world_boss.dialogs.line7");

        // 타이탄 등장
        dialog.addLine("quest.weekly_world_boss.npcs.world_herald", "quest.weekly_world_boss.dialogs.line8");

        dialog.addLine("quest.weekly_world_boss.npcs.world_herald", "quest.weekly_world_boss.dialogs.line9");

        // 승리
        dialog.addLine("quest.weekly_world_boss.npcs.world_herald", "quest.weekly_world_boss.dialogs.line10");

        dialog.addLine("quest.weekly_world_boss.npcs.world_herald", "quest.weekly_world_boss.dialogs.line11");

        return dialog;
    }
}