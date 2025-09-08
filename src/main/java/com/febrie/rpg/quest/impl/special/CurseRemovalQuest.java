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
                        new InteractNPCObjective("curse_investigator", "dark_scholar", 1),
                        new VisitLocationObjective("cursed_ruins", "forbidden_ruins"),
                        new CollectItemObjective("curse_evidence", Material.WRITTEN_BOOK, 5),
                        
                        // 정화 재료 수집
                        new CollectItemObjective("holy_water", Material.WATER_BUCKET, 10),
                        new CollectItemObjective("purification_salt", Material.SUGAR, 64),
                        new CollectItemObjective("blessed_candles", Material.CANDLE, 20),
                        new CollectItemObjective("sacred_herbs", Material.SWEET_BERRIES, 50),
                        
                        // 저주받은 생물 정화
                        new KillMobObjective("purify_zombies", EntityType.ZOMBIE, 30),
                        new KillMobObjective("purify_skeletons", EntityType.SKELETON, 25),
                        new KillMobObjective("purify_phantoms", EntityType.PHANTOM, 15),
                        new CollectItemObjective("corrupted_essence", Material.ROTTEN_FLESH, 40),
                        
                        // 저주받은 물품 정화
                        new CollectItemObjective("cursed_items", Material.IRON_SWORD, 8),
                        new PurifyItemObjective("cleanse_weapons", Material.DIAMOND_SWORD, 3),
                        new PurifyItemObjective("cleanse_armor", Material.DIAMOND_CHESTPLATE, 2),
                        new CraftItemObjective("holy_symbols", Material.GOLD_INGOT, 15),
                        
                        // 저주의 제단 파괴
                        new VisitLocationObjective("dark_altar", "curse_altar"),
                        new BreakBlockObjective("destroy_altar", Material.OBSIDIAN, 20),
                        new KillMobObjective("altar_guardians", EntityType.WITHER_SKELETON, 10),
                        new CollectItemObjective("dark_crystals", Material.AMETHYST_SHARD, 25),
                        
                        // 정화 의식 진행
                        new PlaceBlockObjective("purification_circle", Material.WHITE_CONCRETE, 25),
                        new PlaceBlockObjective("ritual_candles", Material.CANDLE, 13),
                        new CollectItemObjective("holy_blessing", Material.SPLASH_POTION, 5),
                        new InteractNPCObjective("purification_ritual", "ritual_master", 1),
                        
                        // 저주의 근원 제거
                        new VisitLocationObjective("curse_source", "shadow_dimension"),
                        new KillMobObjective("curse_master", EntityType.EVOKER, 1),
                        new CollectItemObjective("curse_core", Material.NETHER_STAR, 1),
                        new InteractNPCObjective("destroy_curse_core", "curse_source_core", 1),
                        
                        // 정화 완료
                        new InteractNPCObjective("purification_complete", "holy_priest", 1),
                        new InteractNPCObjective("blessing_received", "purification_blessing", 1)
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
        return LangManager.text(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "curse_investigator" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_CURSE_INVESTIGATOR, who);
            case "cursed_ruins" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_CURSED_RUINS, who);
            case "curse_evidence" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_CURSE_EVIDENCE, who);
            case "holy_water" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_HOLY_WATER, who);
            case "purification_salt" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_PURIFICATION_SALT, who);
            case "blessed_candles" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_BLESSED_CANDLES, who);
            case "sacred_herbs" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_SACRED_HERBS, who);
            case "purify_zombies" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_PURIFY_ZOMBIES, who);
            case "purify_skeletons" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_PURIFY_SKELETONS, who);
            case "purify_phantoms" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_PURIFY_PHANTOMS, who);
            case "corrupted_essence" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_CORRUPTED_ESSENCE, who);
            case "cursed_items" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_CURSED_ITEMS, who);
            case "cleanse_weapons" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_CLEANSE_WEAPONS, who);
            case "cleanse_armor" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_CLEANSE_ARMOR, who);
            case "holy_symbols" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_HOLY_SYMBOLS, who);
            case "dark_altar" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_DARK_ALTAR, who);
            case "destroy_altar" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_DESTROY_ALTAR, who);
            case "altar_guardians" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_ALTAR_GUARDIANS, who);
            case "dark_crystals" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_DARK_CRYSTALS, who);
            case "purification_circle" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_PURIFICATION_CIRCLE, who);
            case "ritual_candles" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_RITUAL_CANDLES, who);
            case "holy_blessing" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_HOLY_BLESSING, who);
            case "purification_ritual" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_PURIFICATION_RITUAL, who);
            case "curse_source" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_CURSE_SOURCE, who);
            case "curse_master" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_CURSE_MASTER, who);
            case "curse_core" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_CURSE_CORE, who);
            case "destroy_curse_core" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_DESTROY_CURSE_CORE, who);
            case "purification_complete" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_PURIFICATION_COMPLETE, who);
            case "blessing_received" -> LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_OBJECTIVES_BLESSING_RECEIVED, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() {
        return 10;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_CURSE_REMOVAL_DECLINE, who);
    }
}