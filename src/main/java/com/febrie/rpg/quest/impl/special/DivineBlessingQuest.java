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
                        new InteractNPCObjective("divine_messenger", "heavenly_angel", 1),
                        new VisitLocationObjective("celestial_temple", "divine_sanctuary"),
                        new CollectItemObjective("holy_scriptures", Material.ENCHANTED_BOOK, 5),
                        
                        // 신성한 시험들
                        new PerformPilgrimageObjective("sacred_pilgrimage", "holy_sites", 7),
                        new DonateToShrineObjective("temple_donations", CurrencyType.GOLD, 2000),
                        new PrayAtAltarObjective("daily_prayers", "divine_altar", 7),
                        new FastingObjective("spiritual_fasting", 3), // 3일 금식
                        
                        // 자비의 행위
                        new HealPlayersObjective("heal_wounded", 50),
                        new FeedHungryObjective("feed_poor", Material.BREAD, 100),
                        new ProtectInnocentObjective("guard_villagers", EntityType.VILLAGER, 20),
                        new DeliverItemObjective("charity_work", "villager", Material.EMERALD, 25),
                        
                        // 악의 정화
                        new KillMobObjective("vanquish_undead", EntityType.ZOMBIE, 100),
                        new KillMobObjective("destroy_demons", EntityType.WITHER_SKELETON, 50),
                        new ExorciseObjective("banish_spirits", EntityType.PHANTOM, 25),
                        new PurifyLocationObjective("cleanse_corruption", "tainted_lands"),
                        
                        // 신성한 유물 수집
                        new CollectItemObjective("angel_feathers", Material.FEATHER, 64),
                        new CollectItemObjective("holy_water", Material.WATER_BUCKET, 20),
                        new CollectItemObjective("blessed_gold", Material.GOLD_INGOT, 50),
                        new CraftItemObjective("divine_chalice", Material.GOLDEN_APPLE, 10),
                        
                        // 천상의 시험
                        new VisitLocationObjective("heaven_realm", "celestial_kingdom"),
                        new SurviveObjective("divine_trial", 900), // 15분 시험
                        new AnswerRiddlesObjective("heavenly_wisdom", "divine_questions", 10),
                        new ProveWorthinessObjective("final_judgment", "divine_court"),
                        
                        // 신의 축복 수여
                        new ReceiveBlessingObjective("divine_blessing", "god_blessing"),
                        new InteractNPCObjective("blessing_ceremony", "supreme_deity", 1)
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
        return LangManager.text(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "divine_messenger" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_DIVINE_MESSENGER, who);
            case "celestial_temple" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_CELESTIAL_TEMPLE, who);
            case "holy_scriptures" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_HOLY_SCRIPTURES, who);
            case "sacred_pilgrimage" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_SACRED_PILGRIMAGE, who);
            case "temple_donations" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_TEMPLE_DONATIONS, who);
            case "daily_prayers" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_DAILY_PRAYERS, who);
            case "spiritual_fasting" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_SPIRITUAL_FASTING, who);
            case "heal_wounded" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_HEAL_WOUNDED, who);
            case "feed_poor" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_FEED_POOR, who);
            case "guard_villagers" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_GUARD_VILLAGERS, who);
            case "charity_work" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_CHARITY_WORK, who);
            case "vanquish_undead" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_VANQUISH_UNDEAD, who);
            case "destroy_demons" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_DESTROY_DEMONS, who);
            case "banish_spirits" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_BANISH_SPIRITS, who);
            case "cleanse_corruption" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_CLEANSE_CORRUPTION, who);
            case "angel_feathers" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_ANGEL_FEATHERS, who);
            case "holy_water" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_HOLY_WATER, who);
            case "blessed_gold" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_BLESSED_GOLD, who);
            case "divine_chalice" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_DIVINE_CHALICE, who);
            case "heaven_realm" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_HEAVEN_REALM, who);
            case "divine_trial" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_DIVINE_TRIAL, who);
            case "heavenly_wisdom" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_HEAVENLY_WISDOM, who);
            case "final_judgment" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_FINAL_JUDGMENT, who);
            case "divine_blessing" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_DIVINE_BLESSING, who);
            case "blessing_ceremony" -> LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_OBJECTIVES_BLESSING_CEREMONY, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() {
        return 15;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_DIVINE_BLESSING_DECLINE, who);
    }
}