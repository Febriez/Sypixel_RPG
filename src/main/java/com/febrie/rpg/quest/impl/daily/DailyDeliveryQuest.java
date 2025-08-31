package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangHelper;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

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
                .objectives(Arrays.asList(
                        new InteractNPCObjective("delivery_master", "delivery_master"), // 배달부 마스터

                        // 첫 번째 배달 - 빵집으로 빵 배달
                        new CollectItemObjective("collect_bread", Material.BREAD, 10),
                        new VisitLocationObjective("visit_bakery", "village_bakery"),
                        new DeliverItemObjective("deliver_bread", "baker", Material.BREAD, 10),
                        new InteractNPCObjective("baker_thanks", "village_baker"),

                        // 두 번째 배달 - 약국으로 포션 재료 배달
                        new CollectItemObjective("collect_spider_eyes", Material.SPIDER_EYE, 5),
                        new CollectItemObjective("collect_sugar", Material.SUGAR, 10),
                        new VisitLocationObjective("visit_pharmacy", "village_pharmacy"),
                        new DeliverItemObjective("deliver_potion_materials", "pharmacist", Material.SPIDER_EYE, 5),
                        new DeliverItemObjective("deliver_sugar", "pharmacist", Material.SUGAR, 10),
                        new InteractNPCObjective("pharmacist_thanks", "village_pharmacist"),

                        // 세 번째 배달 - 대장간으로 광물 배달
                        new CollectItemObjective("collect_iron", Material.IRON_INGOT, 20),
                        new CollectItemObjective("collect_coal", Material.COAL, 30),
                        new VisitLocationObjective("visit_blacksmith", "village_blacksmith"),
                        new DeliverItemObjective("deliver_iron", "blacksmith", Material.IRON_INGOT, 20),
                        new DeliverItemObjective("deliver_coal", "blacksmith", Material.COAL, 30),
                        new InteractNPCObjective("blacksmith_thanks", "village_blacksmith"),

                        // 네 번째 배달 - 도서관으로 책 배달
                        new CollectItemObjective("collect_books", Material.BOOK, 5),
                        new CollectItemObjective("collect_paper", Material.PAPER, 20),
                        new VisitLocationObjective("visit_library", "village_library"),
                        new DeliverItemObjective("deliver_books", "librarian", Material.BOOK, 5),
                        new DeliverItemObjective("deliver_paper", "librarian", Material.PAPER, 20),
                        new InteractNPCObjective("librarian_thanks", "village_librarian"),

                        // 다섯 번째 배달 - 농장으로 씨앗 배달
                        new CollectItemObjective("collect_wheat_seeds", Material.WHEAT_SEEDS, 32),
                        new CollectItemObjective("collect_bone_meal", Material.BONE_MEAL, 16),
                        new VisitLocationObjective("visit_farm", "village_farm"),
                        new DeliverItemObjective("deliver_seeds", "farmer", Material.WHEAT_SEEDS, 32),
                        new DeliverItemObjective("deliver_fertilizer", "farmer", Material.BONE_MEAL, 16),
                        new InteractNPCObjective("farmer_thanks", "village_farmer"),

                        // 긴급 배달 - 경비대로 무기 배달
                        new CollectItemObjective("collect_swords", Material.IRON_SWORD, 3),
                        new CollectItemObjective("collect_shields", Material.SHIELD, 3),
                        new VisitLocationObjective("visit_guard_post", "village_guard_post"),
                        new SurviveObjective("urgent_delivery", 300), // 5분 제한
                        new DeliverItemObjective("deliver_weapons", "guard_captain", Material.IRON_SWORD, 3),
                        new DeliverItemObjective("deliver_shields", "guard_captain", Material.SHIELD, 3),
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
        return LangHelper.text(LangKey.QUEST_DAILY_DELIVERY_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_DAILY_DELIVERY_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.daily.delivery.objectives." + objective.getId();
        return LangManager.get(key, who);
    }

    @Override
    public int getDialogCount() {
        return 8;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangHelper.text(LangKey.QUEST_DAILY_DELIVERY_DIALOGS_0, who);
            case 1 -> LangHelper.text(LangKey.QUEST_DAILY_DELIVERY_DIALOGS_1, who);
            case 2 -> LangHelper.text(LangKey.QUEST_DAILY_DELIVERY_DIALOGS_2, who);
            case 3 -> LangHelper.text(LangKey.QUEST_DAILY_DELIVERY_DIALOGS_3, who);
            case 4 -> LangHelper.text(LangKey.QUEST_DAILY_DELIVERY_DIALOGS_4, who);
            case 5 -> LangHelper.text(LangKey.QUEST_DAILY_DELIVERY_DIALOGS_5, who);
            case 6 -> LangHelper.text(LangKey.QUEST_DAILY_DELIVERY_DIALOGS_6, who);
            case 7 -> LangHelper.text(LangKey.QUEST_DAILY_DELIVERY_DIALOGS_7, who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_DAILY_DELIVERY_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_DAILY_DELIVERY_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_DAILY_DELIVERY_DECLINE, who);
    }
}