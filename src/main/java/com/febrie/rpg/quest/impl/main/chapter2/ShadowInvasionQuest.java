package com.febrie.rpg.quest.impl.main.chapter2;

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
 * 그림자의 침략 - 메인 스토리 퀘스트 (Chapter 2)
 * 어둠의 세력이 차원의 틈을 통해 침략하는 퀘스트
 *
 * @author Febrie
 */
public class ShadowInvasionQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class ShadowInvasionBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new ShadowInvasionQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public ShadowInvasionQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private ShadowInvasionQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new ShadowInvasionBuilder()
                .id(QuestID.MAIN_SHADOW_INVASION)
                .objectives(Arrays.asList(
                        // 침략 발견
                        new InteractNPCObjective("scout_report", "shadow_scout"), // 정찰병
                        new VisitLocationObjective("shadow_portal", "shadow_breach"),
                        new CollectItemObjective("portal_sample", Material.OBSIDIAN, 5),
                        
                        // 초반 전투
                        new KillMobObjective("shadow_scouts", EntityType.ENDERMAN, 15),
                        new KillMobObjective("shadow_warriors", EntityType.WITHER_SKELETON, 20),
                        new CollectItemObjective("shadow_essence", Material.ENDER_PEARL, 20),
                        new CollectItemObjective("dark_fragments", Material.COAL, 30),
                        
                        // 방어 준비
                        new InteractNPCObjective("commander_talk", "defense_commander"), // 방어 사령관
                        new CollectItemObjective("defense_materials", Material.IRON_INGOT, 50),
                        new PlaceBlockObjective("build_walls", Material.IRON_BARS, 30),
                        new PlaceBlockObjective("build_towers", Material.STONE_BRICKS, 20),
                        new CraftItemObjective("craft_arrows", Material.ARROW, 128),
                        
                        // 대규모 방어전
                        new VisitLocationObjective("defense_position", "castle_walls"),
                        new KillMobObjective("defend_wave1", EntityType.ZOMBIE, 40),
                        new KillMobObjective("defend_wave2", EntityType.SKELETON, 35),
                        new KillMobObjective("defend_wave3", EntityType.SPIDER, 30),
                        new SurviveObjective("hold_position", 600), // 10분간 방어
                        
                        // 엘리트 적 등장
                        new KillMobObjective("shadow_captains", EntityType.VINDICATOR, 5),
                        new KillMobObjective("shadow_mages", EntityType.EVOKER, 5),
                        new CollectItemObjective("captain_badges", Material.IRON_NUGGET, 5),
                        
                        // 그림자 장군과의 대결
                        new VisitLocationObjective("shadow_throne", "shadow_general_arena"),
                        new InteractNPCObjective("shadow_general_talk", "shadow_general"), // 그림자 장군
                        new KillMobObjective("shadow_general", EntityType.WITHER, 1),
                        new CollectItemObjective("general_crown", Material.WITHER_SKELETON_SKULL, 1),
                        
                        // 포탈 파괴
                        new CollectItemObjective("portal_keys", Material.ENDER_EYE, 3),
                        new VisitLocationObjective("return_portal", "shadow_breach"),
                        new PlaceBlockObjective("seal_portal", Material.END_STONE, 9),
                        new CollectItemObjective("sealed_core", Material.NETHER_STAR, 1),
                        
                        // 전리품 보고
                        new DeliverItemObjective("deliver_crown", "commander", Material.WITHER_SKELETON_SKULL, 1),
                        new DeliverItemObjective("deliver_core", "commander", Material.NETHER_STAR, 1),
                        new InteractNPCObjective("victory_ceremony", "defense_commander")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 12000)
                        .addCurrency(CurrencyType.DIAMOND, 80)
                        .addItem(new ItemStack(Material.NETHERITE_HELMET))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING))
                        .addItem(new ItemStack(Material.ENDER_CHEST))
                        .addItem(new ItemStack(Material.BEACON))
                        .addExperience(6000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_GUARDIAN_AWAKENING)
                .minLevel(35)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.main.shadow_invasion.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return List.of() /* TODO: Convert LangManager.getList("quest.main.shadow_invasion.description") manually */;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return Component.translatable("quest.main.shadow_invasion.objectives.");
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("shadow_invasion_dialog");

        // 시작
        dialog.addLine("quest.main.shadow_invasion.dialog.scout1",
                "quest.main.shadow_invasion.dialog.scout1");

        dialog.addLine("quest.main.shadow_invasion.dialog.scout2",
                "quest.main.shadow_invasion.dialog.scout2");

        dialog.addLine("quest.main.shadow_invasion.dialog.player1",
                "quest.main.shadow_invasion.dialog.player1");

        // 방어 사령관
        dialog.addLine("quest.main.shadow_invasion.dialog.commander1",
                "quest.main.shadow_invasion.dialog.commander1");

        dialog.addLine("quest.main.shadow_invasion.dialog.commander2",
                "quest.main.shadow_invasion.dialog.commander2");

        // 전투 중
        dialog.addLine("quest.main.shadow_invasion.dialog.commander3",
                "quest.main.shadow_invasion.dialog.commander3");

        // 그림자 장군
        dialog.addLine("quest.main.shadow_invasion.dialog.general1",
                "quest.main.shadow_invasion.dialog.general1");

        dialog.addLine("quest.main.shadow_invasion.dialog.general2",
                "quest.main.shadow_invasion.dialog.general2");

        dialog.addLine("quest.main.shadow_invasion.dialog.player2",
                "quest.main.shadow_invasion.dialog.player2");

        // 승리
        dialog.addLine("quest.main.shadow_invasion.dialog.commander4",
                "quest.main.shadow_invasion.dialog.commander4");

        dialog.addLine("quest.main.shadow_invasion.dialog.commander5",
                "quest.main.shadow_invasion.dialog.commander5");

        return dialog;
    }
}