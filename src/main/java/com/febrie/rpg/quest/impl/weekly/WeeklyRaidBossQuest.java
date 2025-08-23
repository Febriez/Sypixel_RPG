package com.febrie.rpg.quest.impl.weekly;

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
 * 주간 레이드 보스 - 주간 퀘스트
 * 매주 초기화되는 대규모 레이드 도전
 *
 * @author Febrie
 */
public class WeeklyRaidBossQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class WeeklyRaidBossBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new WeeklyRaidBossQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public WeeklyRaidBossQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private WeeklyRaidBossQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new WeeklyRaidBossBuilder()
                .id(QuestID.WEEKLY_RAID_BOSS)
                .objectives(Arrays.asList(
                        // 준비 단계
                        new InteractNPCObjective("raid_commander", "raid_commander"), // 레이드 사령관
                        new ReachLevelObjective("level_requirement", 40),
                        new CollectItemObjective("raid_key", Material.TRIPWIRE_HOOK, 3),
                        
                        // 레이드 던전 입장
                        new VisitLocationObjective("raid_entrance", "chaos_fortress_entrance"),
                        
                        // 첫 번째 구역 - 혼돈의 전당
                        new KillMobObjective("chaos_minions", EntityType.WITHER_SKELETON, 30),
                        new KillMobObjective("chaos_knights", EntityType.PIGLIN_BRUTE, 20),
                        new KillMobObjective("mini_boss_1", EntityType.ELDER_GUARDIAN, 1),
                        new CollectItemObjective("chaos_fragment", Material.NETHERITE_SCRAP, 5),
                        
                        // 두 번째 구역 - 어둠의 성소
                        new VisitLocationObjective("dark_sanctuary", "chaos_fortress_sanctuary"),
                        new SurviveObjective("darkness_trial", 300), // 5분
                        new KillMobObjective("shadow_assassins", EntityType.EVOKER, 10),
                        new KillMobObjective("void_walkers", EntityType.ENDERMAN, 25),
                        new KillMobObjective("mini_boss_2", EntityType.RAVAGER, 1),
                        new CollectItemObjective("void_essence", Material.ENDER_EYE, 10),
                        
                        // 최종 보스 구역
                        new VisitLocationObjective("throne_room", "chaos_fortress_throne"),
                        new KillPlayerObjective("pvp_zone", 3), // PvP 구역에서 3명 처치
                        new PayCurrencyObjective("boss_summon", CurrencyType.GOLD, 5000),
                        new KillMobObjective("chaos_lord", EntityType.WITHER, 1),
                        
                        // 보상 획득
                        new CollectItemObjective("legendary_loot", Material.NETHER_STAR, 1),
                        new DeliverItemObjective("raid_complete", "raid_commander", Material.NETHER_STAR, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 15000)
                        .addCurrency(CurrencyType.DIAMOND, 100)
                        .addItem(new ItemStack(Material.NETHERITE_INGOT, 3))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 5))
                        .addItem(new ItemStack(Material.BEACON))
                        .addExperience(10000)
                        .build())
                .sequential(false)  // 자유로운 진행 가능
                .repeatable(true)
                .weekly(true)      // 주간 퀘스트
                .category(QuestCategory.WEEKLY)
                .minLevel(40)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.weekly.raid_boss.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return List.of() /* TODO: Convert LangManager.getList("quest.weekly.raid_boss.description") manually */;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "raid_commander" -> Component.translatable("quest.weekly.raid_boss.objectives.raid_commander");
            case "level_requirement" -> Component.translatable("quest.weekly.raid_boss.objectives.level_requirement");
            case "raid_key" -> Component.translatable("quest.weekly.raid_boss.objectives.raid_key");
            case "raid_entrance" -> Component.translatable("quest.weekly.raid_boss.objectives.raid_entrance");
            case "chaos_minions" -> Component.translatable("quest.weekly.raid_boss.objectives.chaos_minions");
            case "chaos_knights" -> Component.translatable("quest.weekly.raid_boss.objectives.chaos_knights");
            case "mini_boss_1" -> Component.translatable("quest.weekly.raid_boss.objectives.mini_boss_1");
            case "chaos_fragment" -> Component.translatable("quest.weekly.raid_boss.objectives.chaos_fragment");
            case "dark_sanctuary" -> Component.translatable("quest.weekly.raid_boss.objectives.dark_sanctuary");
            case "darkness_trial" -> Component.translatable("quest.weekly.raid_boss.objectives.darkness_trial");
            case "shadow_assassins" -> Component.translatable("quest.weekly.raid_boss.objectives.shadow_assassins");
            case "void_walkers" -> Component.translatable("quest.weekly.raid_boss.objectives.void_walkers");
            case "mini_boss_2" -> Component.translatable("quest.weekly.raid_boss.objectives.mini_boss_2");
            case "void_essence" -> Component.translatable("quest.weekly.raid_boss.objectives.void_essence");
            case "throne_room" -> Component.translatable("quest.weekly.raid_boss.objectives.throne_room");
            case "pvp_zone" -> Component.translatable("quest.weekly.raid_boss.objectives.pvp_zone");
            case "boss_summon" -> Component.translatable("quest.weekly.raid_boss.objectives.boss_summon");
            case "chaos_lord" -> Component.translatable("quest.weekly.raid_boss.objectives.chaos_lord");
            case "legendary_loot" -> Component.translatable("quest.weekly.raid_boss.objectives.legendary_loot");
            case "raid_complete" -> Component.translatable("quest.weekly.raid_boss.objectives.raid_complete");
            default -> Component.translatable("quest.weekly.raid_boss.objectives." + id);
        };
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("weekly_raid_boss_dialog");

        dialog.addLine("quest.weekly_raid_boss.npcs.raid_commander", "quest.weekly_raid_boss.dialogs.line1");

        dialog.addLine("quest.weekly_raid_boss.npcs.raid_commander", "quest.weekly_raid_boss.dialogs.line2");

        dialog.addLine("quest.dialog.player", "quest.weekly_raid_boss.dialogs.line3");

        dialog.addLine("quest.weekly_raid_boss.npcs.raid_commander", "quest.weekly_raid_boss.dialogs.line4");

        dialog.addLine("quest.weekly_raid_boss.npcs.raid_commander", "quest.weekly_raid_boss.dialogs.line5");

        dialog.addLine("quest.dialog.player", "quest.weekly_raid_boss.dialogs.line6");

        dialog.addLine("quest.weekly_raid_boss.npcs.raid_commander", "quest.weekly_raid_boss.dialogs.line7");

        dialog.addLine("quest.weekly_raid_boss.npcs.raid_commander", "quest.weekly_raid_boss.dialogs.line8");

        return dialog;
    }
}