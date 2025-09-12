package com.febrie.rpg.quest.impl.special;

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
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import java.util.ArrayList;
import java.util.List;

/**
 * 저주 해제 - 특별 퀘스트
 * 고대의 저주를 해제하고 정화의 힘을 얻는 퀘스트
 *
 * @author Febrie
 */
public class CurseRemovalQuest extends Quest {

    public CurseRemovalQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SPECIAL_CURSE_REMOVAL)
                .objectives(List.of(
                        // 저주의 근원 조사
                        new InteractNPCObjective("curse_investigator", "dark_scholar"),
                        new VisitLocationObjective("cursed_ruins", "forbidden_ruins"),
                        new CollectItemObjective("written_book_collect", Material.WRITTEN_BOOK, 5),
                        
                        // 정화 재료 수집
                        new CollectItemObjective("water_bucket_collect", Material.WATER_BUCKET, 10),
                        new CollectItemObjective("sugar_collect", Material.SUGAR, 64),
                        new CollectItemObjective("candle_collect", Material.CANDLE, 20),
                        new CollectItemObjective("sweet_berries_collect", Material.SWEET_BERRIES, 50),
                        
                        // 저주받은 생물 정화
                        new KillMobObjective("purify_zombies", EntityType.ZOMBIE, 30),
                        new KillMobObjective("purify_skeletons", EntityType.SKELETON, 25),
                        new KillMobObjective("purify_phantoms", EntityType.PHANTOM, 15),
                        new CollectItemObjective("rotten_flesh_collect", Material.ROTTEN_FLESH, 40),
                        
                        // 저주받은 물품 정화
                        new CollectItemObjective("iron_sword_collect", Material.IRON_SWORD, 8),
                        new CraftItemObjective("diamond_sword_craft", Material.DIAMOND_SWORD, 3),
                        new CraftItemObjective("diamond_chestplate_craft", Material.DIAMOND_CHESTPLATE, 2),
                        new CraftItemObjective("gold_ingot_craft", Material.GOLD_INGOT, 15),
                        
                        // 저주의 제단 파괴
                        new VisitLocationObjective("dark_altar", "curse_altar"),
                        new BreakBlockObjective("destroy_altar", Material.OBSIDIAN, 20),
                        new KillMobObjective("altar_guardians", EntityType.WITHER_SKELETON, 10),
                        new CollectItemObjective("amethyst_shard_collect", Material.AMETHYST_SHARD, 25),
                        
                        // 정화 의식 진행
                        new PlaceBlockObjective("purification_circle", Material.WHITE_CONCRETE, 25),
                        new PlaceBlockObjective("ritual_candles", Material.CANDLE, 13),
                        new CollectItemObjective("splash_potion_collect", Material.SPLASH_POTION, 5),
                        new InteractNPCObjective("purification_ritual", "ritual_master"),
                        
                        // 저주의 근원 제거
                        new VisitLocationObjective("curse_source", "shadow_dimension"),
                        new KillMobObjective("curse_master", EntityType.EVOKER, 1),
                        new CollectItemObjective("nether_star_collect", Material.NETHER_STAR, 1),
                        new InteractNPCObjective("destroy_curse_core", "curse_source_core"),
                        
                        // 정화 완료
                        new InteractNPCObjective("purification_complete", "holy_priest"),
                        new InteractNPCObjective("blessing_received", "purification_blessing")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 3000)
                        .addCurrency(CurrencyType.DIAMOND, 50)
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 5)) // 정화의 황금사과
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 2)) // 생명의 토템
                        .addItem(new ItemStack(Material.BEACON, 1)) // 정화의 등대
                        .addExperience(5000)
                        .build())
                .sequential(true)
                .repeatable(false)  // 한 번만 수행 가능
                .category(QuestCategory.SPECIAL)
                .minLevel(35)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "curse_investigator" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_CURSE_INVESTIGATOR, who);
            case "cursed_ruins" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_CURSED_RUINS, who);
            case "written_book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_WRITTEN_BOOK_COLLECT, who);
            case "water_bucket_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_WATER_BUCKET_COLLECT, who);
            case "sugar_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_SUGAR_COLLECT, who);
            case "candle_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_CANDLE_COLLECT, who);
            case "sweet_berries_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_SWEET_BERRIES_COLLECT, who);
            case "purify_zombies" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_PURIFY_ZOMBIES, who);
            case "purify_skeletons" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_PURIFY_SKELETONS, who);
            case "purify_phantoms" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_PURIFY_PHANTOMS, who);
            case "rotten_flesh_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_ROTTEN_FLESH_COLLECT, who);
            case "iron_sword_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_IRON_SWORD_COLLECT, who);
            case "diamond_sword_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_DIAMOND_SWORD_CRAFT, who);
            case "diamond_chestplate_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_DIAMOND_CHESTPLATE_CRAFT, who);
            case "gold_ingot_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_GOLD_INGOT_CRAFT, who);
            case "dark_altar" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_DARK_ALTAR, who);
            case "destroy_altar" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_DESTROY_ALTAR, who);
            case "altar_guardians" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_ALTAR_GUARDIANS, who);
            case "amethyst_shard_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_AMETHYST_SHARD_COLLECT, who);
            case "purification_circle" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_PURIFICATION_CIRCLE, who);
            case "ritual_candles" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_RITUAL_CANDLES, who);
            case "splash_potion_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_SPLASH_POTION_COLLECT, who);
            case "purification_ritual" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_PURIFICATION_RITUAL, who);
            case "curse_source" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_CURSE_SOURCE, who);
            case "curse_master" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_CURSE_MASTER, who);
            case "nether_star_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_NETHER_STAR_COLLECT, who);
            case "destroy_curse_core" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_DESTROY_CURSE_CORE, who);
            case "purification_complete" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_PURIFICATION_COMPLETE, who);
            case "blessing_received" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_BLESSING_RECEIVED, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 10;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_CURSE_REMOVAL_DECLINE, who);
    }
}