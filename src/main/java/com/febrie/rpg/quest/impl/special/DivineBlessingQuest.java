package com.febrie.rpg.quest.impl.special;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
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
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import java.util.ArrayList;
import java.util.List;

/**
 * 신의 축복 - 특별 퀘스트
 * 신들의 축복을 받아 신성한 힘을 얻는 퀘스트
 *
 * @author Febrie
 */
public class DivineBlessingQuest extends Quest {

    public DivineBlessingQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SPECIAL_DIVINE_BLESSING)
                .objectives(List.of(
                        // 신의 사도와 만남
                        new InteractNPCObjective("divine_messenger", "heavenly_angel"),
                        new VisitLocationObjective("celestial_temple", "divine_sanctuary"),
                        new CollectItemObjective("enchanted_book_collect", Material.ENCHANTED_BOOK, 5),
                        
                        // 신성한 시험들
                        new VisitLocationObjective("sacred_pilgrimage", "holy_sites"),
                        new PayCurrencyObjective("temple_donations", CurrencyType.GOLD, 2000),
                        new InteractNPCObjective("daily_prayers", "divine_altar"),
                        new SurviveObjective("spiritual_fasting", 259200), // 3 days in seconds
                        
                        // 자비의 행위
                        new InteractNPCObjective("heal_wounded", "wounded_villager"),
                        new DeliverItemObjective("bread_deliver", Material.BREAD, 100, "hungry_villager"),
                        new KillMobObjective("guard_villagers", EntityType.ZOMBIE, 20), // Protect by killing threats
                        new DeliverItemObjective("emerald_deliver", Material.EMERALD, 25, "villager"),
                        
                        // 악의 정화
                        new KillMobObjective("vanquish_undead", EntityType.ZOMBIE, 100),
                        new KillMobObjective("destroy_demons", EntityType.WITHER_SKELETON, 50),
                        new KillMobObjective("banish_spirits", EntityType.PHANTOM, 25),
                        new VisitLocationObjective("cleanse_corruption", "tainted_lands"),
                        
                        // 신성한 유물 수집
                        new CollectItemObjective("feather_collect", Material.FEATHER, 64),
                        new CollectItemObjective("water_bucket_collect", Material.WATER_BUCKET, 20),
                        new CollectItemObjective("gold_ingot_collect", Material.GOLD_INGOT, 50),
                        new CraftItemObjective("golden_apple_craft", Material.GOLDEN_APPLE, 10),
                        
                        // 천상의 시험
                        new VisitLocationObjective("heaven_realm", "celestial_kingdom"),
                        new SurviveObjective("divine_trial", 900), // 15분 시험
                        new InteractNPCObjective("heavenly_wisdom", "divine_oracle"),
                        new SurviveObjective("final_judgment", 600), // 10-minute trial
                        
                        // 신의 축복 수여
                        new InteractNPCObjective("divine_blessing", "god_blessing"),
                        new InteractNPCObjective("blessing_ceremony", "supreme_deity")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 10000)
                        .addCurrency(CurrencyType.DIAMOND, 100)
                        .addItem(new ItemStack(Material.BEACON, 1)) // 신의 등대
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 10))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 5))
                        .addItem(new ItemStack(Material.ELYTRA, 1)) // 천사의 날개
                        .addExperience(15000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SPECIAL)
                .minLevel(50)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "divine_messenger" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_DIVINE_MESSENGER, who);
            case "celestial_temple" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_CELESTIAL_TEMPLE, who);
            case "enchanted_book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_ENCHANTED_BOOK_COLLECT, who);
            case "sacred_pilgrimage" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_SACRED_PILGRIMAGE, who);
            case "temple_donations" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_TEMPLE_DONATIONS, who);
            case "daily_prayers" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_DAILY_PRAYERS, who);
            case "spiritual_fasting" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_SPIRITUAL_FASTING, who);
            case "heal_wounded" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_HEAL_WOUNDED, who);
            case "bread_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_BREAD_DELIVER, who);
            case "guard_villagers" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_GUARD_VILLAGERS, who);
            case "emerald_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_EMERALD_DELIVER, who);
            case "vanquish_undead" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_VANQUISH_UNDEAD, who);
            case "destroy_demons" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_DESTROY_DEMONS, who);
            case "banish_spirits" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_BANISH_SPIRITS, who);
            case "cleanse_corruption" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_CLEANSE_CORRUPTION, who);
            case "feather_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_FEATHER_COLLECT, who);
            case "water_bucket_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_WATER_BUCKET_COLLECT, who);
            case "gold_ingot_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_GOLD_INGOT_COLLECT, who);
            case "golden_apple_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_GOLDEN_APPLE_CRAFT, who);
            case "heaven_realm" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_HEAVEN_REALM, who);
            case "divine_trial" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_DIVINE_TRIAL, who);
            case "heavenly_wisdom" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_HEAVENLY_WISDOM, who);
            case "final_judgment" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_FINAL_JUDGMENT, who);
            case "divine_blessing" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_DIVINE_BLESSING, who);
            case "blessing_ceremony" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_BLESSING_CEREMONY, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 15;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_DIVINE_BLESSING_DECLINE, who);
    }
}