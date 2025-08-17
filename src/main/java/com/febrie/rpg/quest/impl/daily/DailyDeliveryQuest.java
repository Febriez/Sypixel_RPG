package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
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
     * 퀘스트 빌더
     */
    private static class DailyDeliveryBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new DailyDeliveryQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public DailyDeliveryQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private DailyDeliveryQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new DailyDeliveryBuilder()
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
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.daily.delivery.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getList(who, "quest.daily.delivery.description");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.daily.delivery.objectives." + objective.getId();
        return com.febrie.rpg.util.LangManager.getMessage(who, key);
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("daily_delivery_dialog");

        // 시작 대화
        dialog.addLine("quest.daily.delivery.npcs.delivery_master", "quest.daily.delivery.dialogs.start1");
        dialog.addLine("quest.dialog.player", "quest.daily.delivery.dialogs.player_accept");
        dialog.addLine("quest.daily.delivery.npcs.delivery_master", "quest.daily.delivery.dialogs.start2");

        // 배달 중 대화
        dialog.addLine("quest.daily.delivery.npcs.baker", "quest.daily.delivery.dialogs.baker_thanks");
        dialog.addLine("quest.daily.delivery.npcs.pharmacist", "quest.daily.delivery.dialogs.pharmacist_thanks");
        dialog.addLine("quest.daily.delivery.npcs.guard_captain", "quest.daily.delivery.dialogs.guard_thanks");

        // 완료 대화
        dialog.addLine("quest.daily.delivery.npcs.delivery_master", "quest.daily.delivery.dialogs.complete1");
        dialog.addLine("quest.daily.delivery.npcs.delivery_master", "quest.daily.delivery.dialogs.complete2");

        return dialog;
    }
}