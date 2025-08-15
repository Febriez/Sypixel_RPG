package com.febrie.rpg.quest.impl.main.chapter1;

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
 * 원소의 돌 - 메인 퀘스트 Chapter 1
 * 네 가지 원소의 힘을 모으는 대서사시
 *
 * @author Febrie
 */
public class ElementalStonesQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class ElementalStonesBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new ElementalStonesQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public ElementalStonesQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private ElementalStonesQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new ElementalStonesBuilder().id(QuestID.MAIN_ELEMENTAL_STONES)
                .objectives(Arrays.asList(
                        // 시작
                        new InteractNPCObjective("meet_sage", "elemental_sage"), // 원소의 현자

                        // 불의 돌 - 용암 지대
                        new VisitLocationObjective("fire_temple", "fire_elemental_temple"), new KillMobObjective("fire_elementals", EntityType.BLAZE, 15), new KillMobObjective("magma_cubes", EntityType.MAGMA_CUBE, 10), new CollectItemObjective("fire_essence", Material.BLAZE_POWDER, 20), new KillMobObjective("fire_guardian", EntityType.WITHER_SKELETON, 1), new CollectItemObjective("fire_stone", Material.BLAZE_ROD, 1),

                        // 물의 돌 - 해저 신전
                        new VisitLocationObjective("water_temple", "water_elemental_temple"), new KillMobObjective("water_elementals", EntityType.GUARDIAN, 15), new KillMobObjective("drowned", EntityType.DROWNED, 20), new CollectItemObjective("water_essence", Material.PRISMARINE_SHARD, 20), new KillMobObjective("water_guardian", EntityType.ELDER_GUARDIAN, 1), new CollectItemObjective("water_stone", Material.HEART_OF_THE_SEA, 1),

                        // 대지의 돌 - 깊은 동굴
                        new VisitLocationObjective("earth_temple", "earth_elemental_temple"), new BreakBlockObjective("mine_ores", Material.IRON_ORE, 30), new KillMobObjective("earth_elementals", EntityType.IRON_GOLEM, 10), new CollectItemObjective("earth_essence", Material.EMERALD, 15), new KillMobObjective("earth_guardian", EntityType.RAVAGER, 1), new CollectItemObjective("earth_stone", Material.EMERALD_BLOCK, 1),

                        // 바람의 돌 - 하늘 섬
                        new VisitLocationObjective("air_temple", "air_elemental_temple"), new KillMobObjective("air_elementals", EntityType.PHANTOM, 20), new KillMobObjective("vexes", EntityType.VEX, 15), new CollectItemObjective("air_essence", Material.FEATHER, 30), new KillMobObjective("air_guardian", EntityType.EVOKER, 1), new CollectItemObjective("air_stone", Material.ELYTRA, 1),

                        // 최종 - 원소의 융합
                        new CraftItemObjective("elemental_core", Material.BEACON, 1), new DeliverItemObjective("return_sage", "현자 아카테", Material.BEACON, 1)))
                .reward(new BasicReward.Builder().addCurrency(CurrencyType.GOLD, 5000)
                        .addCurrency(CurrencyType.DIAMOND, 50)
                        .addItem(new ItemStack(Material.BEACON))
                        .addItem(new ItemStack(Material.NETHER_STAR, 4))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 3))
                        .addExperience(5000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(25)
                .maxLevel(0)
                .addPrerequisite(QuestID.MAIN_FIRST_TRIAL);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.main.elemental_stones.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getList(who, "quest.main.elemental_stones.description");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.main.elemental_stones.objectives." + id);
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("elemental_stones_dialog");

        dialog.addLine("quest.main_elemental_stones.npcs.elemental_sage", "quest.main_elemental_stones.dialogs.line1");

        dialog.addLine("quest.main_elemental_stones.npcs.elemental_sage", "quest.main_elemental_stones.dialogs.line2");

        dialog.addLine("quest.main_elemental_stones.npcs.elemental_sage", "quest.main_elemental_stones.dialogs.line3");

        dialog.addLine("quest.dialog.player", "quest.main_elemental_stones.dialogs.line4");

        dialog.addLine("quest.main_elemental_stones.npcs.elemental_sage", "quest.main_elemental_stones.dialogs.line5");

        dialog.addLine("quest.main_elemental_stones.npcs.elemental_sage", "quest.main_elemental_stones.dialogs.line6");

        dialog.addLine("quest.dialog.player", "quest.main_elemental_stones.dialogs.line7");

        dialog.addLine("quest.main_elemental_stones.npcs.elemental_sage", "quest.main_elemental_stones.dialogs.line8");

        return dialog;
    }
}