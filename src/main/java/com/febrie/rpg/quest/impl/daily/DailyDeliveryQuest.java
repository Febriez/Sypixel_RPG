package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
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
    private static class DailyDeliveryBuilder extends Quest.Builder {
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
    private DailyDeliveryQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static Builder createBuilder() {
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
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "일일 긴급 배달" : "Daily Express Delivery";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "마을 곳곳에 긴급한 물품을 배달하세요!",
                    "6곳의 서로 다른 장소에 물품을 배달해야 합니다.",
                    "매일 자정에 리셋됩니다.",
                    "",
                    "배달 목록:",
                    "• 빵집 - 빵 10개",
                    "• 약국 - 거미 눈 5개, 설탕 10개",
                    "• 대장간 - 철 주괴 20개, 석탄 30개",
                    "• 도서관 - 책 5건, 종이 20장",
                    "• 농장 - 밀 씨앗 32개, 뽓가루 16개",
                    "• 경비대 - 철 검 3개, 방패 3개 (긴급!)",
                    "",
                    "특별 도전:",
                    "• 경비대 배달은 5분 안에 완료해야 함",
                    "• 모든 배달은 순서대로 진행",
                    "• 각 장소를 방문해야 함",
                    "",
                    "팁:",
                    "• 물품을 미리 준비하세요",
                    "• 빠른 이동 수단을 활용하세요",
                    "• 지도를 참고하세요",
                    "",
                    "보상:",
                    "• 골드 800",
                    "• 다이아몬드 10개",
                    "• 배달부 부츠 (속도 향상)",
                    "• 마인카트 3개",
                    "• 레일 32개",
                    "• 엔더 진주 5개",
                    "• 지도 3장",
                    "• 경험치 500"
            );
        } else {
            return Arrays.asList(
                    "Deliver urgent supplies throughout the village!",
                    "You must deliver items to 6 different locations.",
                    "Resets daily at midnight.",
                    "",
                    "Delivery List:",
                    "• Bakery - 10 Bread",
                    "• Pharmacy - 5 Spider Eyes, 10 Sugar",
                    "• Blacksmith - 20 Iron Ingots, 30 Coal",
                    "• Library - 5 Books, 20 Paper",
                    "• Farm - 32 Wheat Seeds, 16 Bone Meal",
                    "• Guard Post - 3 Iron Swords, 3 Shields (URGENT!)",
                    "",
                    "Special Challenge:",
                    "• Guard Post delivery must be completed within 5 minutes",
                    "• All deliveries must be done in order",
                    "• Must visit each location",
                    "",
                    "Tips:",
                    "• Prepare items in advance",
                    "• Use fast travel methods",
                    "• Reference the map",
                    "",
                    "Rewards:",
                    "• 800 Gold",
                    "• 10 Diamonds",
                    "• Delivery Boots (Speed boost)",
                    "• 3 Minecarts",
                    "• 32 Rails",
                    "• 5 Ender Pearls",
                    "• 3 Maps",
                    "• 500 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "delivery_master" -> isKorean ? "배달부 마스터와 대화" : "Talk to Delivery Master";
            case "collect_bread" -> isKorean ? "빵 10개 준비" : "Prepare 10 bread";
            case "visit_bakery" -> isKorean ? "빵집 방문" : "Visit bakery";
            case "deliver_bread" -> isKorean ? "빵 배달" : "Deliver bread";
            case "baker_thanks" -> isKorean ? "빵집 주인과 대화" : "Talk to baker";
            case "collect_spider_eyes" -> isKorean ? "거미 눈 5개 준비" : "Prepare 5 spider eyes";
            case "collect_sugar" -> isKorean ? "설탕 10개 준비" : "Prepare 10 sugar";
            case "visit_pharmacy" -> isKorean ? "약국 방문" : "Visit pharmacy";
            case "deliver_potion_materials" -> isKorean ? "포션 재료 배달" : "Deliver potion materials";
            case "deliver_sugar" -> isKorean ? "설탕 배달" : "Deliver sugar";
            case "pharmacist_thanks" -> isKorean ? "약사와 대화" : "Talk to pharmacist";
            case "collect_iron" -> isKorean ? "철 주괴 20개 준비" : "Prepare 20 iron ingots";
            case "collect_coal" -> isKorean ? "석탄 30개 준비" : "Prepare 30 coal";
            case "visit_blacksmith" -> isKorean ? "대장간 방문" : "Visit blacksmith";
            case "deliver_iron" -> isKorean ? "철 주괴 배달" : "Deliver iron ingots";
            case "deliver_coal" -> isKorean ? "석탄 배달" : "Deliver coal";
            case "blacksmith_thanks" -> isKorean ? "대장장이와 대화" : "Talk to blacksmith";
            case "collect_books" -> isKorean ? "책 5권 준비" : "Prepare 5 books";
            case "collect_paper" -> isKorean ? "종이 20장 준비" : "Prepare 20 paper";
            case "visit_library" -> isKorean ? "도서관 방문" : "Visit library";
            case "deliver_books" -> isKorean ? "책 배달" : "Deliver books";
            case "deliver_paper" -> isKorean ? "종이 배달" : "Deliver paper";
            case "librarian_thanks" -> isKorean ? "사서와 대화" : "Talk to librarian";
            case "collect_wheat_seeds" -> isKorean ? "밀 씨앗 32개 준비" : "Prepare 32 wheat seeds";
            case "collect_bone_meal" -> isKorean ? "뽓가루 16개 준비" : "Prepare 16 bone meal";
            case "visit_farm" -> isKorean ? "농장 방문" : "Visit farm";
            case "deliver_seeds" -> isKorean ? "씨앗 배달" : "Deliver seeds";
            case "deliver_fertilizer" -> isKorean ? "비료 배달" : "Deliver fertilizer";
            case "farmer_thanks" -> isKorean ? "농부와 대화" : "Talk to farmer";
            case "collect_swords" -> isKorean ? "철 검 3개 준비" : "Prepare 3 iron swords";
            case "collect_shields" -> isKorean ? "방패 3개 준비" : "Prepare 3 shields";
            case "visit_guard_post" -> isKorean ? "경비대 방문" : "Visit guard post";
            case "urgent_delivery" -> isKorean ? "5분 안에 긴급 배달" : "Urgent delivery within 5 minutes";
            case "deliver_weapons" -> isKorean ? "무기 배달" : "Deliver weapons";
            case "deliver_shields" -> isKorean ? "방패 배달" : "Deliver shields";
            case "guard_thanks" -> isKorean ? "경비대장과 대화" : "Talk to guard captain";
            case "report_complete" -> isKorean ? "배달 완료 보고" : "Report delivery complete";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("daily_delivery_dialog");

        // 시작
        dialog.addLine("배달부 마스터",
                "빠르고 정확한 배달부가 필요해! 오늘도 긴급한 물품들이 많아.",
                "I need a fast and accurate delivery person! Many urgent items today.");

        dialog.addLine("플레이어",
                "제가 도와드리겠습니다!",
                "I'll help!");

        dialog.addLine("배달부 마스터",
                "6곳의 서로 다른 장소에 물품을 배달해야 해. 특히 경비대는 긴급이야!",
                "You need to deliver items to 6 different locations. The guard post is especially urgent!");

        // 배달 중
        dialog.addLine("빵집 주인",
                "빵이 정확히 10개네요! 감사합니다!",
                "Exactly 10 loaves of bread! Thank you!");

        dialog.addLine("약사",
                "이 재료로 중요한 포션을 만들 수 있어요. 고마워요!",
                "I can make important potions with these materials. Thanks!");

        dialog.addLine("경비대장",
                "시간 내에 도착했군! 이 무기로 마을을 지킬 수 있겠어!",
                "You arrived on time! We can defend the village with these weapons!");

        // 완료
        dialog.addLine("배달부 마스터",
                "훌륭해! 모든 배달을 완벽하게 완료했군. 내일도 부탁해!",
                "Excellent! All deliveries completed perfectly. Please help again tomorrow!");

        dialog.addLine("배달부 마스터",
                "이 특제 부츠를 받아. 더 빠르게 이동할 수 있을 거야!",
                "Take these special boots. You'll be able to move faster!");

        return dialog;
    }
}