package com.febrie.rpg.quest.impl.main.chapter1;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
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
                        new InteractNPCObjective("meet_sage", "elemental_sage"), // 원소의 현자

                        // 불의 돌 - 용암 지대
                        new VisitLocationObjective("fire_temple", "fire_elemental_temple"), new KillMobObjective("fire_elementals", EntityType.BLAZE, 15), new KillMobObjective("magma_cubes", EntityType.MAGMA_CUBE, 10), new CollectItemObjective("blaze_powder_collect", Material.BLAZE_POWDER, 20), new KillMobObjective("fire_guardian", EntityType.WITHER_SKELETON, 1), new CollectItemObjective("blaze_rod_collect", Material.BLAZE_ROD, 1),

                        // 물의 돌 - 해저 신전
                        new VisitLocationObjective("water_temple", "water_elemental_temple"), new KillMobObjective("water_elementals", EntityType.GUARDIAN, 15), new KillMobObjective("drowned", EntityType.DROWNED, 20), new CollectItemObjective("prismarine_shard_collect", Material.PRISMARINE_SHARD, 20), new KillMobObjective("water_guardian", EntityType.ELDER_GUARDIAN, 1), new CollectItemObjective("heart_of_the_sea_collect", Material.HEART_OF_THE_SEA, 1),

                        // 대지의 돌 - 깊은 동굴
                        new VisitLocationObjective("earth_temple", "earth_elemental_temple"), new BreakBlockObjective("mine_ores", Material.IRON_ORE, 30), new KillMobObjective("earth_elementals", EntityType.IRON_GOLEM, 10), new CollectItemObjective("emerald_collect", Material.EMERALD, 15), new KillMobObjective("earth_guardian", EntityType.RAVAGER, 1), new CollectItemObjective("emerald_block_collect", Material.EMERALD_BLOCK, 1),

                        // 바람의 돌 - 하늘 섬
                        new VisitLocationObjective("air_temple", "air_elemental_temple"), new KillMobObjective("air_elementals", EntityType.PHANTOM, 20), new KillMobObjective("vexes", EntityType.VEX, 15), new CollectItemObjective("feather_collect", Material.FEATHER, 30), new KillMobObjective("air_guardian", EntityType.EVOKER, 1), new CollectItemObjective("elytra_collect", Material.ELYTRA, 1),

                        // 최종 - 원소의 융합
                        new CraftItemObjective("beacon_craft", Material.BEACON, 1), new DeliverItemObjective("beacon_deliver", Material.BEACON, 1, "현자 아카테")))
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
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "meet_sage" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_MEET_SAGE, who);
            case "fire_temple" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_FIRE_TEMPLE, who);
            case "fire_elementals" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_FIRE_ELEMENTALS, who);
            case "magma_cubes" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_MAGMA_CUBES, who);
            case "blaze_powder_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_BLAZE_POWDER_COLLECT, who);
            case "fire_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_FIRE_GUARDIAN, who);
            case "blaze_rod_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_BLAZE_ROD_COLLECT, who);
            case "water_temple" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_WATER_TEMPLE, who);
            case "water_elementals" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_WATER_ELEMENTALS, who);
            case "drowned" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_DROWNED, who);
            case "prismarine_shard_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_PRISMARINE_SHARD_COLLECT, who);
            case "water_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_WATER_GUARDIAN, who);
            case "heart_of_the_sea_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_HEART_OF_THE_SEA_COLLECT, who);
            case "earth_temple" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_EARTH_TEMPLE, who);
            case "mine_ores" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_MINE_ORES, who);
            case "earth_elementals" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_EARTH_ELEMENTALS, who);
            case "emerald_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_EMERALD_COLLECT, who);
            case "earth_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_EARTH_GUARDIAN, who);
            case "emerald_block_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_EMERALD_BLOCK_COLLECT, who);
            case "air_temple" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_AIR_TEMPLE, who);
            case "air_elementals" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_AIR_ELEMENTALS, who);
            case "vexes" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_VEXES, who);
            case "feather_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_FEATHER_COLLECT, who);
            case "air_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_AIR_GUARDIAN, who);
            case "elytra_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_ELYTRA_COLLECT, who);
            case "beacon_craft" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_BEACON_CRAFT, who);
            case "beacon_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_OBJECTIVES_BEACON_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 8;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_ELEMENTAL_STONES_DECLINE, who);
    }
}