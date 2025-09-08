package com.febrie.rpg.quest.impl.main.chapter1;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
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

/**
 * 원소의 돌 - 메인 퀘스트 Chapter 1
 * 네 가지 원소의 힘을 모으는 대서사시
 *
 * @author Febrie
 */
public class ElementalStonesQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public ElementalStonesQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder().id(QuestID.MAIN_ELEMENTAL_STONES)
                .objectives(List.of(
                        // 시작
                        new InteractNPCObjective("meet_sage", "elemental_sage", 1), // 원소의 현자

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
        return LangManager.text(LangKey.QUEST_MAIN_ELEMENTAL_STONES_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "meet_sage" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_MEET_SAGE, who);
            case "fire_temple" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_FIRE_TEMPLE, who);
            case "fire_elementals" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_FIRE_ELEMENTALS, who);
            case "magma_cubes" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_MAGMA_CUBES, who);
            case "fire_essence" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_FIRE_ESSENCE, who);
            case "fire_guardian" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_FIRE_GUARDIAN, who);
            case "fire_stone" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_FIRE_STONE, who);
            case "water_temple" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_WATER_TEMPLE, who);
            case "water_elementals" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_WATER_ELEMENTALS, who);
            case "drowned" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_DROWNED, who);
            case "water_essence" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_WATER_ESSENCE, who);
            case "water_guardian" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_WATER_GUARDIAN, who);
            case "water_stone" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_WATER_STONE, who);
            case "earth_temple" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_EARTH_TEMPLE, who);
            case "mine_ores" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_MINE_ORES, who);
            case "earth_elementals" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_EARTH_ELEMENTALS, who);
            case "earth_essence" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_EARTH_ESSENCE, who);
            case "earth_guardian" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_EARTH_GUARDIAN, who);
            case "earth_stone" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_EARTH_STONE, who);
            case "air_temple" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_AIR_TEMPLE, who);
            case "air_elementals" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_AIR_ELEMENTALS, who);
            case "vexes" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_VEXES, who);
            case "air_essence" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_AIR_ESSENCE, who);
            case "air_guardian" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_AIR_GUARDIAN, who);
            case "air_stone" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_AIR_STONE, who);
            case "elemental_core" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_ELEMENTAL_CORE, who);
            case "return_sage" -> LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_RETURN_SAGE, who);
            default -> List.of(Component.text("Objective: " + objective.getId()));
        };
    }

    @Override
    public int getDialogCount() {
        return 8;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_ELEMENTAL_STONES_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_ELEMENTAL_STONES_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_ELEMENTAL_STONES_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_ELEMENTAL_STONES_DECLINE, who);
    }
}