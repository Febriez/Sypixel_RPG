package com.febrie.rpg.quest.impl.main.chapter3;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
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
 * 용의 심장 - 메인 스토리 퀘스트 (Chapter 3 Finale)
 * 전설의 용의 심장을 획득하는 최종 퀘스트
 *
 * @author Febrie
 */
public class DragonHeartQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DragonHeartQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_DRAGON_HEART)
                .objectives(List.of(
                        // 전설의 시작
                        new InteractNPCObjective("ancient_dragon", "ancient_dragon"),
                        new CollectItemObjective("written_book_collect", Material.WRITTEN_BOOK, 1),
                        new InteractNPCObjective("elder_sage", "elder_sage"),
                        new VisitLocationObjective("heart_shrine", "dragon_heart_shrine"),
                        
                        // 세 가지 열쇠 수집
                        // 첫 번째 열쇠 - 용암의 열쇠
                        new VisitLocationObjective("lava_depths", "volcanic_depths"),
                        new KillMobObjective("lava_dragons", EntityType.MAGMA_CUBE, 50),
                        new CollectItemObjective("magma_block_collect", Material.MAGMA_BLOCK, 30),
                        new KillMobObjective("fire_lord", EntityType.BLAZE, 25),
                        new CollectItemObjective("blaze_rod_collect", Material.BLAZE_ROD, 1),
                        
                        // 두 번째 열쇠 - 얼음의 열쇠
                        new VisitLocationObjective("frozen_peaks", "ice_dragon_peaks"),
                        new KillMobObjective("ice_dragons", EntityType.POLAR_BEAR, 30),
                        new CollectItemObjective("packed_ice_collect", Material.PACKED_ICE, 50),
                        new KillMobObjective("frost_guardian", EntityType.STRAY, 40),
                        new CollectItemObjective("blue_ice_collect", Material.BLUE_ICE, 1),
                        
                        // 세 번째 열쇠 - 폭풍의 열쇠
                        new VisitLocationObjective("storm_nexus", "storm_dragon_nexus"),
                        new KillMobObjective("storm_dragons", EntityType.PHANTOM, 60),
                        new CollectItemObjective("end_rod_collect", Material.END_ROD, 20),
                        new KillMobObjective("thunder_lord", EntityType.WITCH, 30),
                        new CollectItemObjective("lightning_rod_collect", Material.LIGHTNING_ROD, 1),
                        
                        // 심장의 방 진입
                        new VisitLocationObjective("heart_chamber_entrance", "heart_chamber_entrance"),
                        new PlaceBlockObjective("place_fire_key", Material.REDSTONE_BLOCK, 1),
                        new PlaceBlockObjective("place_ice_key", Material.DIAMOND_BLOCK, 1),
                        new PlaceBlockObjective("place_storm_key", Material.EMERALD_BLOCK, 1),
                        new SurviveObjective("key_ritual", 300), // 5분 의식
                        
                        // 수호자들과의 전투
                        new VisitLocationObjective("guardian_arena", "heart_guardian_arena"),
                        new KillMobObjective("fire_guardian", EntityType.ELDER_GUARDIAN, 3),
                        new KillMobObjective("ice_guardian", EntityType.ELDER_GUARDIAN, 3),
                        new KillMobObjective("storm_guardian", EntityType.ELDER_GUARDIAN, 3),
                        new CollectItemObjective("nether_star_collect", Material.NETHER_STAR, 3),
                        
                        // 고대 용왕과의 대결
                        new InteractNPCObjective("dragon_emperor", "ancient_dragon_emperor"),
                        new KillMobObjective("dragon_emperor_battle", EntityType.ENDER_DRAGON, 3),
                        new SurviveObjective("emperor_wrath", 900), // 15분 생존
                        new CollectItemObjective("dragon_head_collect", Material.DRAGON_HEAD, 1),
                        
                        // 용의 심장 획득
                        new VisitLocationObjective("heart_core", "dragon_heart_core"),
                        new InteractNPCObjective("heart_guardian", "heart_guardian_spirit"),
                        new CollectItemObjective("nether_star_collect", Material.NETHER_STAR, 5),
                        new PlaceBlockObjective("assemble_heart", Material.BEACON, 1),
                        new CollectItemObjective("dragon_egg_collect", Material.DRAGON_EGG, 1),
                        
                        // 챕터 완결
                        new InteractNPCObjective("ancient_dragon_finale", "ancient_dragon"),
                        new CollectItemObjective("enchanted_golden_apple_collect", Material.ENCHANTED_GOLDEN_APPLE, 1),
                        new CollectItemObjective("written_book_collect", Material.WRITTEN_BOOK, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 50000)
                        .addCurrency(CurrencyType.DIAMOND, 500)
                        .addItem(new ItemStack(Material.DRAGON_EGG))
                        .addItem(new ItemStack(Material.NETHER_STAR, 5))
                        .addItem(new ItemStack(Material.NETHERITE_INGOT, 10))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 10))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 5))
                        .addItem(new ItemStack(Material.BEACON, 3))
                        .addExperience(25000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_SKY_FORTRESS)
                .minLevel(65)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "ancient_dragon" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_ANCIENT_DRAGON, who);
            case "written_book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_WRITTEN_BOOK_COLLECT, who);
            case "elder_sage" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_ELDER_SAGE, who);
            case "heart_shrine" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_HEART_SHRINE, who);
            case "lava_depths" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_LAVA_DEPTHS, who);
            case "lava_dragons" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_LAVA_DRAGONS, who);
            case "magma_block_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_MAGMA_BLOCK_COLLECT, who);
            case "fire_lord" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_FIRE_LORD, who);
            case "blaze_rod_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_BLAZE_ROD_COLLECT, who);
            case "frozen_peaks" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_FROZEN_PEAKS, who);
            case "ice_dragons" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_ICE_DRAGONS, who);
            case "packed_ice_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_PACKED_ICE_COLLECT, who);
            case "frost_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_FROST_GUARDIAN, who);
            case "blue_ice_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_BLUE_ICE_COLLECT, who);
            case "storm_nexus" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_STORM_NEXUS, who);
            case "storm_dragons" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_STORM_DRAGONS, who);
            case "end_rod_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_END_ROD_COLLECT, who);
            case "thunder_lord" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_THUNDER_LORD, who);
            case "lightning_rod_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_LIGHTNING_ROD_COLLECT, who);
            case "heart_chamber_entrance" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_HEART_CHAMBER_ENTRANCE, who);
            case "place_fire_key" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_PLACE_FIRE_KEY, who);
            case "place_ice_key" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_PLACE_ICE_KEY, who);
            case "place_storm_key" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_PLACE_STORM_KEY, who);
            case "key_ritual" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_KEY_RITUAL, who);
            case "guardian_arena" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_GUARDIAN_ARENA, who);
            case "fire_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_FIRE_GUARDIAN, who);
            case "ice_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_ICE_GUARDIAN, who);
            case "storm_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_STORM_GUARDIAN, who);
            case "nether_star_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_NETHER_STAR_COLLECT, who);
            case "dragon_emperor" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_DRAGON_EMPEROR, who);
            case "dragon_emperor_battle" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_DRAGON_EMPEROR_BATTLE, who);
            case "emperor_wrath" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_EMPEROR_WRATH, who);
            case "dragon_head_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_DRAGON_HEAD_COLLECT, who);
            case "heart_core" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_HEART_CORE, who);
            case "heart_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_HEART_GUARDIAN, who);
            case "assemble_heart" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_ASSEMBLE_HEART, who);
            case "dragon_egg_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_DRAGON_EGG_COLLECT, who);
            case "ancient_dragon_finale" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_ANCIENT_DRAGON_FINALE, who);
            case "enchanted_golden_apple_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_ENCHANTED_GOLDEN_APPLE_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_HEART_DECLINE, who);
    }
}