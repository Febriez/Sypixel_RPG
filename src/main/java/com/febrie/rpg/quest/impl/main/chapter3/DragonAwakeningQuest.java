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
 * 용의 각성 - 메인 스토리 퀘스트 (Chapter 3)
 * 고대 용을 깨우고 동맹을 맺는 퀘스트
 *
 * @author Febrie
 */
public class DragonAwakeningQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DragonAwakeningQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_DRAGON_AWAKENING)
                .objectives(List.of(
                        // 전설 조사
                        new InteractNPCObjective("dragon_sage", "dragon_sage"), // 용의 현자
                        new CollectItemObjective("written_book_collect", Material.WRITTEN_BOOK, 5),
                        new VisitLocationObjective("ancient_library", "dragon_library"),
                        new CollectItemObjective("enchanted_book_collect", Material.ENCHANTED_BOOK, 3),
                        
                        // 용의 신전 찾기
                        new VisitLocationObjective("mountain_peak", "dragon_mountain_peak"),
                        new BreakBlockObjective("clear_path", Material.STONE, 50),
                        new KillMobObjective("mountain_guardians", EntityType.IRON_GOLEM, 5),
                        new VisitLocationObjective("dragon_shrine", "ancient_dragon_shrine"),
                        
                        // 각성 의식 준비
                        new CollectItemObjective("ghast_tear_collect", Material.GHAST_TEAR, 3),
                        new CollectItemObjective("gold_block_collect", Material.GOLD_BLOCK, 10),
                        new CollectItemObjective("emerald_block_collect", Material.EMERALD_BLOCK, 5),
                        new CollectItemObjective("dragon_breath_collect", Material.DRAGON_BREATH, 3),
                        
                        // 각성 의식
                        new PlaceBlockObjective("place_gold", Material.GOLD_BLOCK, 10),
                        new PlaceBlockObjective("place_emerald", Material.EMERALD_BLOCK, 5),
                        new CollectItemObjective("end_crystal_collect", Material.END_CRYSTAL, 4),
                        // END_CRYSTAL은 엔티티이므로 PlaceBlockObjective를 사용할 수 없음
                        // 대신 obsidian을 놓는 것으로 변경
                        new PlaceBlockObjective("place_obsidian", Material.OBSIDIAN, 4),
                        new SurviveObjective("ritual_duration", 600), // 10분
                        
                        // 용의 시험
                        new KillMobObjective("flame_dragons", EntityType.BLAZE, 20),
                        new KillMobObjective("dragon_priests", EntityType.EVOKER, 10),
                        new CollectItemObjective("prismarine_shard_collect", Material.PRISMARINE_SHARD, 20),
                        new CollectItemObjective("bone_collect", Material.BONE, 50),
                        
                        // 용의 둥지 진입
                        new VisitLocationObjective("dragon_lair_entrance", "dragon_lair_entrance"),
                        new KillMobObjective("lair_guardians", EntityType.ELDER_GUARDIAN, 3),
                        new CollectItemObjective("heart_of_the_sea_collect", Material.HEART_OF_THE_SEA, 1),
                        new VisitLocationObjective("inner_lair", "dragon_inner_lair"),
                        
                        // 용과의 만남
                        new InteractNPCObjective("sleeping_dragon", "ancient_dragon"), // 잠든 고대 용
                        new CollectItemObjective("dragon_egg_collect", Material.DRAGON_EGG, 1),
                        new DeliverItemObjective("diamond_block_deliver", Material.DIAMOND_BLOCK, 10, "sleeping_dragon"),
                        
                        // 용과의 결투
                        new KillMobObjective("dragon_test", EntityType.ENDER_DRAGON, 1),
                        
                        // 동맹 체결
                        new InteractNPCObjective("dragon_pact", "ancient_dragon"),
                        new CollectItemObjective("nether_star_collect", Material.NETHER_STAR, 1),
                        new CollectItemObjective("written_book_collect", Material.WRITTEN_BOOK, 1),
                        new DeliverItemObjective("written_book_deliver", Material.WRITTEN_BOOK, 1, "dragon_sage")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 20000)
                        .addCurrency(CurrencyType.DIAMOND, 150)
                        .addItem(new ItemStack(Material.ELYTRA)) // 용의 날개
                        .addItem(new ItemStack(Material.DRAGON_HEAD))
                        .addItem(new ItemStack(Material.DRAGON_BREATH, 10))
                        .addItem(new ItemStack(Material.END_CRYSTAL, 4))
                        .addItem(new ItemStack(Material.SHULKER_BOX, 3))
                        .addExperience(10000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_CORRUPTED_LANDS)
                .minLevel(45)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "dragon_sage" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_SAGE, who);
            case "written_book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_WRITTEN_BOOK_COLLECT, who);
            case "ancient_library" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_ANCIENT_LIBRARY, who);
            case "enchanted_book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_ENCHANTED_BOOK_COLLECT, who);
            case "mountain_peak" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_MOUNTAIN_PEAK, who);
            case "clear_path" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_CLEAR_PATH, who);
            case "mountain_guardians" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_MOUNTAIN_GUARDIANS, who);
            case "dragon_shrine" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_SHRINE, who);
            case "ghast_tear_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_GHAST_TEAR_COLLECT, who);
            case "gold_block_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_GOLD_BLOCK_COLLECT, who);
            case "emerald_block_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_EMERALD_BLOCK_COLLECT, who);
            case "dragon_breath_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_BREATH_COLLECT, who);
            case "place_gold" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_PLACE_GOLD, who);
            case "place_emerald" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_PLACE_EMERALD, who);
            case "end_crystal_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_END_CRYSTAL_COLLECT, who);
            case "place_obsidian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_PLACE_OBSIDIAN, who);
            case "ritual_duration" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_RITUAL_DURATION, who);
            case "flame_dragons" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_FLAME_DRAGONS, who);
            case "dragon_priests" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_PRIESTS, who);
            case "prismarine_shard_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_PRISMARINE_SHARD_COLLECT, who);
            case "bone_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_BONE_COLLECT, who);
            case "dragon_lair_entrance" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_LAIR_ENTRANCE, who);
            case "lair_guardians" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_LAIR_GUARDIANS, who);
            case "heart_of_the_sea_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_HEART_OF_THE_SEA_COLLECT, who);
            case "inner_lair" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_INNER_LAIR, who);
            case "sleeping_dragon" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_SLEEPING_DRAGON, who);
            case "dragon_egg_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_EGG_COLLECT, who);
            case "diamond_block_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DIAMOND_BLOCK_DELIVER, who);
            case "dragon_test" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_TEST, who);
            case "dragon_pact" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_PACT, who);
            case "nether_star_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_NETHER_STAR_COLLECT, who);
            case "written_book_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_WRITTEN_BOOK_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 12;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_AWAKENING_DECLINE, who);
    }
}