package com.febrie.rpg.quest.impl.main.chapter3;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
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
                        new InteractNPCObjective("dragon_sage", "dragon_sage", 1), // 용의 현자
                        new CollectItemObjective("ancient_scrolls", Material.WRITTEN_BOOK, 5),
                        new VisitLocationObjective("ancient_library", "dragon_library"),
                        new CollectItemObjective("dragon_lore", Material.ENCHANTED_BOOK, 3),
                        
                        // 용의 신전 찾기
                        new VisitLocationObjective("mountain_peak", "dragon_mountain_peak"),
                        new BreakBlockObjective("clear_path", Material.STONE, 50),
                        new KillMobObjective("mountain_guardians", EntityType.IRON_GOLEM, 5),
                        new VisitLocationObjective("dragon_shrine", "ancient_dragon_shrine"),
                        
                        // 각성 의식 준비
                        new CollectItemObjective("dragon_tears", Material.GHAST_TEAR, 3),
                        new CollectItemObjective("ancient_gold", Material.GOLD_BLOCK, 10),
                        new CollectItemObjective("emerald_offering", Material.EMERALD_BLOCK, 5),
                        new CollectItemObjective("dragon_breath", Material.DRAGON_BREATH, 3),
                        
                        // 각성 의식
                        new PlaceBlockObjective("place_gold", Material.GOLD_BLOCK, 10),
                        new PlaceBlockObjective("place_emerald", Material.EMERALD_BLOCK, 5),
                        new CollectItemObjective("ritual_catalyst", Material.END_CRYSTAL, 4),
                        // END_CRYSTAL은 엔티티이므로 PlaceBlockObjective를 사용할 수 없음
                        // 대신 obsidian을 놓는 것으로 변경
                        new PlaceBlockObjective("place_obsidian", Material.OBSIDIAN, 4),
                        new SurviveObjective("ritual_duration", 600), // 10분
                        
                        // 용의 시험
                        new KillMobObjective("flame_dragons", EntityType.BLAZE, 20),
                        new KillMobObjective("dragon_priests", EntityType.EVOKER, 10),
                        new CollectItemObjective("dragon_scales", Material.PRISMARINE_SHARD, 20),
                        new CollectItemObjective("dragon_bones", Material.BONE, 50),
                        
                        // 용의 둥지 진입
                        new VisitLocationObjective("dragon_lair_entrance", "dragon_lair_entrance"),
                        new KillMobObjective("lair_guardians", EntityType.ELDER_GUARDIAN, 3),
                        new CollectItemObjective("lair_key", Material.HEART_OF_THE_SEA, 1),
                        new VisitLocationObjective("inner_lair", "dragon_inner_lair"),
                        
                        // 용과의 만남
                        new InteractNPCObjective("sleeping_dragon", "ancient_dragon", 1), // 잠든 고대 용
                        new CollectItemObjective("dragon_egg", Material.DRAGON_EGG, 1),
                        new DeliverItemObjective("offer_treasures", "sleeping_dragon", Material.DIAMOND_BLOCK, 10),
                        
                        // 용과의 결투
                        new KillMobObjective("dragon_test", EntityType.ENDER_DRAGON, 1),
                        
                        // 동맹 체결
                        new InteractNPCObjective("dragon_pact", "ancient_dragon", 1),
                        new CollectItemObjective("dragon_heart", Material.NETHER_STAR, 1),
                        new CollectItemObjective("pact_scroll", Material.WRITTEN_BOOK, 1),
                        new DeliverItemObjective("complete_pact", "dragon_sage", Material.WRITTEN_BOOK, 1)
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
        return LangManager.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "dragon_sage" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_SAGE, who);
            case "ancient_scrolls" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_ANCIENT_SCROLLS, who);
            case "ancient_library" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_ANCIENT_LIBRARY, who);
            case "dragon_lore" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_LORE, who);
            case "mountain_peak" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_MOUNTAIN_PEAK, who);
            case "clear_path" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_CLEAR_PATH, who);
            case "mountain_guardians" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_MOUNTAIN_GUARDIANS, who);
            case "dragon_shrine" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_SHRINE, who);
            case "dragon_tears" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_TEARS, who);
            case "ancient_gold" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_ANCIENT_GOLD, who);
            case "emerald_offering" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_EMERALD_OFFERING, who);
            case "dragon_breath" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_BREATH, who);
            case "place_gold" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_PLACE_GOLD, who);
            case "place_emerald" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_PLACE_EMERALD, who);
            case "ritual_catalyst" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_RITUAL_CATALYST, who);
            case "place_obsidian" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_PLACE_OBSIDIAN, who);
            case "ritual_duration" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_RITUAL_DURATION, who);
            case "flame_dragons" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_FLAME_DRAGONS, who);
            case "dragon_priests" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_PRIESTS, who);
            case "dragon_scales" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_SCALES, who);
            case "dragon_bones" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_BONES, who);
            case "dragon_lair_entrance" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_LAIR_ENTRANCE, who);
            case "lair_guardians" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_LAIR_GUARDIANS, who);
            case "lair_key" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_LAIR_KEY, who);
            case "inner_lair" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_INNER_LAIR, who);
            case "sleeping_dragon" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_SLEEPING_DRAGON, who);
            case "dragon_egg" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_EGG, who);
            case "offer_treasures" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_OFFER_TREASURES, who);
            case "dragon_test" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_TEST, who);
            case "dragon_pact" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_PACT, who);
            case "dragon_heart" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_DRAGON_HEART, who);
            case "pact_scroll" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_PACT_SCROLL, who);
            case "complete_pact" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_OBJECTIVES_COMPLETE_PACT, who);
            default -> List.of(Component.text("Objective: " + objective.getId()));
        };
    }

    @Override
    public int getDialogCount() {
        return 12;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_DECLINE, who);
    }
}