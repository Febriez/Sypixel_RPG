package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ArrayList;

/**
 * 일일 배달 - 일일 퀘스트
 * 매일 리셋되는 배달 퀘스트
 *
 * @author Febrie
 */
public class DailyDeliveryQuest extends Quest {

    /**
     * 기본 생성자
     */
    public DailyDeliveryQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.DAILY_DELIVERY)
                .objectives(List.of(
                        new InteractNPCObjective("delivery_master", "delivery_master"), // 배달부 마스터

                        // 첫 번째 배달 - 빵집으로 빵 배달
                        new CollectItemObjective("bread_collect", Material.BREAD, 10),
                        new VisitLocationObjective("visit_bakery", "village_bakery"),
                        new DeliverItemObjective("bread_deliver", Material.BREAD, 10, "baker"),
                        new InteractNPCObjective("baker_thanks", "village_baker"),

                        // 두 번째 배달 - 약국으로 포션 재료 배달
                        new CollectItemObjective("spider_eye_collect", Material.SPIDER_EYE, 5),
                        new CollectItemObjective("sugar_collect", Material.SUGAR, 10),
                        new VisitLocationObjective("visit_pharmacy", "village_pharmacy"),
                        new DeliverItemObjective("spider_eye_deliver", Material.SPIDER_EYE, 5, "pharmacist"),
                        new DeliverItemObjective("sugar_deliver", Material.SUGAR, 10, "pharmacist"),
                        new InteractNPCObjective("pharmacist_thanks", "village_pharmacist"),

                        // 세 번째 배달 - 대장간으로 광물 배달
                        new CollectItemObjective("iron_ingot_collect", Material.IRON_INGOT, 20),
                        new CollectItemObjective("coal_collect", Material.COAL, 30),
                        new VisitLocationObjective("visit_blacksmith", "village_blacksmith"),
                        new DeliverItemObjective("iron_ingot_deliver", Material.IRON_INGOT, 20, "blacksmith"),
                        new DeliverItemObjective("coal_deliver", Material.COAL, 30, "blacksmith"),
                        new InteractNPCObjective("blacksmith_thanks", "village_blacksmith"),

                        // 네 번째 배달 - 도서관으로 책 배달
                        new CollectItemObjective("book_collect", Material.BOOK, 5),
                        new CollectItemObjective("paper_collect", Material.PAPER, 20),
                        new VisitLocationObjective("visit_library", "village_library"),
                        new DeliverItemObjective("book_deliver", Material.BOOK, 5, "librarian"),
                        new DeliverItemObjective("paper_deliver", Material.PAPER, 20, "librarian"),
                        new InteractNPCObjective("librarian_thanks", "village_librarian"),

                        // 다섯 번째 배달 - 농장으로 씨앗 배달
                        new CollectItemObjective("wheat_seeds_collect", Material.WHEAT_SEEDS, 32),
                        new CollectItemObjective("bone_meal_collect", Material.BONE_MEAL, 16),
                        new VisitLocationObjective("visit_farm", "village_farm"),
                        new DeliverItemObjective("wheat_seeds_deliver", Material.WHEAT_SEEDS, 32, "farmer"),
                        new DeliverItemObjective("bone_meal_deliver", Material.BONE_MEAL, 16, "farmer"),
                        new InteractNPCObjective("farmer_thanks", "village_farmer"),

                        // 긴급 배달 - 경비대로 무기 배달
                        new CollectItemObjective("iron_sword_collect", Material.IRON_SWORD, 3),
                        new CollectItemObjective("shield_collect", Material.SHIELD, 3),
                        new VisitLocationObjective("visit_guard_post", "village_guard_post"),
                        new SurviveObjective("urgent_delivery", 300), // 5분 제한
                        new DeliverItemObjective("iron_sword_deliver", Material.IRON_SWORD, 3, "guard_captain"),
                        new DeliverItemObjective("shield_deliver", Material.SHIELD, 3, "guard_captain"),
                        new InteractNPCObjective("guard_thanks", "village_guard"),

                        // 배달 완료 보고
                        new InteractNPCObjective("report_complete", "delivery_master")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 800)
                        .addCurrency(CurrencyType.DIAMOND, 10)
                        .addItem(new ItemStack(Material.LEATHER_BOOTS)) // 배달부 부츠 (속도 향상)
                        .addItem(new ItemStack(Material.MINECART, 3))
                        .addItem(new ItemStack(Material.RAIL, 32))
                        .addItem(new ItemStack(Material.ENDER_PEARL, 5))
                        .addItem(new ItemStack(Material.MAP, 3))
                        .addExperience(500)
                        .build())
                .sequential(true) // 순차적 배달
                .daily(true)  // 일일 퀘스트 설정
                .repeatable(true)
                .category(QuestCategory.DAILY)
                .minLevel(12)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_DELIVERY_INFO, who);
    }

        @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "delivery_master" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_DELIVERY_MASTER, who);
            case "bread_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_BREAD_COLLECT, who);
            case "visit_bakery" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_VISIT_BAKERY, who);
            case "bread_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_BREAD_DELIVER, who);
            case "baker_thanks" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_BAKER_THANKS, who);
            case "spider_eye_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_SPIDER_EYE_COLLECT, who);
            case "sugar_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_SUGAR_COLLECT, who);
            case "visit_pharmacy" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_VISIT_PHARMACY, who);
            case "spider_eye_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_SPIDER_EYE_DELIVER, who);
            case "sugar_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_SUGAR_DELIVER, who);
            case "pharmacist_thanks" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_PHARMACIST_THANKS, who);
            case "iron_ingot_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_IRON_INGOT_COLLECT, who);
            case "coal_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_COAL_COLLECT, who);
            case "visit_blacksmith" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_VISIT_BLACKSMITH, who);
            case "iron_ingot_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_IRON_INGOT_DELIVER, who);
            case "coal_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_COAL_DELIVER, who);
            case "blacksmith_thanks" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_BLACKSMITH_THANKS, who);
            case "book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_BOOK_COLLECT, who);
            case "paper_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_PAPER_COLLECT, who);
            case "visit_library" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_VISIT_LIBRARY, who);
            case "book_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_BOOK_DELIVER, who);
            case "paper_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_PAPER_DELIVER, who);
            case "librarian_thanks" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_LIBRARIAN_THANKS, who);
            case "wheat_seeds_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_WHEAT_SEEDS_COLLECT, who);
            case "bone_meal_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_BONE_MEAL_COLLECT, who);
            case "visit_farm" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_VISIT_FARM, who);
            case "wheat_seeds_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_WHEAT_SEEDS_DELIVER, who);
            case "bone_meal_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_BONE_MEAL_DELIVER, who);
            case "farmer_thanks" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_FARMER_THANKS, who);
            case "iron_sword_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_IRON_SWORD_COLLECT, who);
            case "shield_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_SHIELD_COLLECT, who);
            case "visit_guard_post" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_VISIT_GUARD_POST, who);
            case "urgent_delivery" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_URGENT_DELIVERY, who);
            case "iron_sword_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_IRON_SWORD_DELIVER, who);
            case "shield_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_SHIELD_DELIVER, who);
            case "guard_thanks" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_GUARD_THANKS, who);
            case "report_complete" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_REPORT_COMPLETE, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 8;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_DELIVERY_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_DECLINE, who);
    }
}