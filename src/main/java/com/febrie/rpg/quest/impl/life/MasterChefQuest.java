package com.febrie.rpg.quest.impl.life;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 요리 달인 - 생활 퀘스트
 * 최고의 요리사가 되기 위한 수련 퀘스트
 *
 * @author Febrie
 */
public class MasterChefQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class MasterChefBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new MasterChefQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public MasterChefQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private MasterChefQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new MasterChefBuilder()
                .id(QuestID.LIFE_MASTER_CHEF)
                .objectives(Arrays.asList(
                        // 시작
                        new InteractNPCObjective("master_chef", "master_chef"), // 요리 마스터
                        
                        // 기초 재료 수집
                        new HarvestObjective("harvest_wheat", Material.WHEAT, 20),
                        new CollectItemObjective("gather_wheat", Material.WHEAT, 20),
                        new CollectItemObjective("gather_eggs", Material.EGG, 12),
                        new CollectItemObjective("gather_sugar", Material.SUGAR, 10),
                        new CollectItemObjective("gather_milk", Material.MILK_BUCKET, 3),
                        
                        // 고기와 해산물
                        new KillMobObjective("hunt_cows", EntityType.COW, 10),
                        new CollectItemObjective("gather_beef", Material.BEEF, 15),
                        new KillMobObjective("hunt_pigs", EntityType.PIG, 10),
                        new CollectItemObjective("gather_pork", Material.PORKCHOP, 15),
                        new FishingObjective("catch_fish", 20),
                        new CollectItemObjective("gather_fish", Material.COD, 10),
                        new CollectItemObjective("gather_salmon", Material.SALMON, 10),
                        
                        // 채소와 과일
                        new HarvestObjective("harvest_vegetables", Material.CARROTS, 30),
                        new CollectItemObjective("gather_carrots", Material.CARROT, 20),
                        new CollectItemObjective("gather_potatoes", Material.POTATO, 20),
                        new CollectItemObjective("gather_beetroot", Material.BEETROOT, 15),
                        new CollectItemObjective("gather_apples", Material.APPLE, 10),
                        new CollectItemObjective("gather_melons", Material.MELON_SLICE, 16),
                        
                        // 주방 설치
                        new PlaceBlockObjective("setup_furnace", Material.FURNACE, 3),
                        new PlaceBlockObjective("setup_smoker", Material.SMOKER, 2),
                        new PlaceBlockObjective("setup_campfire", Material.CAMPFIRE, 1),
                        new PlaceBlockObjective("setup_cauldron", Material.CAULDRON, 2),
                        
                        // 기초 요리
                        new CraftItemObjective("bake_bread", Material.BREAD, 20),
                        new CraftItemObjective("cook_beef", Material.COOKED_BEEF, 15),
                        new CraftItemObjective("cook_pork", Material.COOKED_PORKCHOP, 15),
                        new CraftItemObjective("cook_fish", Material.COOKED_COD, 10),
                        new CraftItemObjective("bake_potato", Material.BAKED_POTATO, 20),
                        
                        // 고급 요리
                        new CraftItemObjective("make_cookies", Material.COOKIE, 32),
                        new CraftItemObjective("make_pie", Material.PUMPKIN_PIE, 5),
                        new CraftItemObjective("make_cake", Material.CAKE, 3),
                        new CraftItemObjective("make_stew", Material.RABBIT_STEW, 5),
                        new CraftItemObjective("make_soup", Material.MUSHROOM_STEW, 5),
                        
                        // 특별 요리 - 황금 사과
                        new CollectItemObjective("special_ingredient", Material.GOLD_INGOT, 8),
                        new CraftItemObjective("golden_apple", Material.GOLDEN_APPLE, 2),
                        
                        // 완성
                        new DeliverItemObjective("deliver_feast", "요리 마스터", Material.CAKE, 1),
                        new DeliverItemObjective("deliver_golden", "요리 마스터", Material.GOLDEN_APPLE, 1),
                        new InteractNPCObjective("graduation", "master_chef")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 3500)
                        .addCurrency(CurrencyType.DIAMOND, 30)
                        .addItem(new ItemStack(Material.GOLDEN_CARROT, 16))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE))
                        .addItem(new ItemStack(Material.WRITTEN_BOOK)) // 요리 레시피북
                        .addItem(new ItemStack(Material.CAMPFIRE))
                        .addExperience(2000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.LIFE)
                .minLevel(15)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "요리 마스터의 길" : "Path of the Master Chef";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "최고의 요리사가 되기 위한 수련을 시작하세요.",
                    "다양한 재료를 수집하고 맛있는 요리를 만들어보세요.",
                    "",
                    "🍳 수련 과정:",
                    "• 1단계: 기초 재료 수집",
                    "• 2단계: 고기와 해산물 확보",
                    "• 3단계: 농사와 채소 재배",
                    "• 4단계: 주방 설비 갖추기",
                    "• 5단계: 기초 요리 마스터",
                    "• 6단계: 고급 요리 제작",
                    "• 7단계: 특별 요리 완성",
                    "",
                    "요리 목록:",
                    "• 빵, 스테이크, 구운 감자",
                    "• 쿠키, 파이, 케이크",
                    "• 스튜, 수프",
                    "• 황금 사과",
                    "",
                    "목표:",
                    "• 요리 마스터와 대화",
                    "• 다양한 재료 수집",
                    "• 주방 설비 설치",
                    "• 기초 요리 20개 이상 제작",
                    "• 고급 요리 5가지 제작",
                    "• 황금 사과 제작",
                    "• 완성품 제출",
                    "",
                    "보상:",
                    "• 골드 3,500",
                    "• 다이아몬드 30개",
                    "• 황금 당근 16개",
                    "• 인챈트된 황금 사과",
                    "• 요리 레시피북",
                    "• 모닥불",
                    "• 경험치 2,000"
            );
        } else {
            return Arrays.asList(
                    "Begin your training to become a master chef.",
                    "Gather various ingredients and create delicious dishes.",
                    "",
                    "🍳 Training Process:",
                    "• Stage 1: Basic Ingredient Collection",
                    "• Stage 2: Meat and Seafood Procurement",
                    "• Stage 3: Farming and Vegetable Growing",
                    "• Stage 4: Kitchen Equipment Setup",
                    "• Stage 5: Basic Cooking Mastery",
                    "• Stage 6: Advanced Dish Creation",
                    "• Stage 7: Special Dish Completion",
                    "",
                    "Dish List:",
                    "• Bread, Steak, Baked Potato",
                    "• Cookies, Pie, Cake",
                    "• Stew, Soup",
                    "• Golden Apple",
                    "",
                    "Objectives:",
                    "• Talk to the Master Chef",
                    "• Gather various ingredients",
                    "• Set up kitchen equipment",
                    "• Create 20+ basic dishes",
                    "• Create 5 advanced dishes",
                    "• Craft Golden Apple",
                    "• Submit completed dishes",
                    "",
                    "Rewards:",
                    "• 3,500 Gold",
                    "• 30 Diamonds",
                    "• 16 Golden Carrots",
                    "• Enchanted Golden Apple",
                    "• Recipe Book",
                    "• Campfire",
                    "• 2,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "master_chef" -> isKorean ? "요리 마스터와 대화" : "Talk to the Master Chef";
            case "harvest_wheat" -> isKorean ? "밀 20개 수확" : "Harvest 20 Wheat";
            case "gather_wheat" -> isKorean ? "밀 20개 수집" : "Gather 20 Wheat";
            case "gather_eggs" -> isKorean ? "달걀 12개 수집" : "Gather 12 Eggs";
            case "gather_sugar" -> isKorean ? "설탕 10개 수집" : "Gather 10 Sugar";
            case "gather_milk" -> isKorean ? "우유 3통 수집" : "Gather 3 Milk Buckets";
            case "hunt_cows" -> isKorean ? "소 10마리 사냥" : "Hunt 10 Cows";
            case "gather_beef" -> isKorean ? "소고기 15개 수집" : "Gather 15 Raw Beef";
            case "hunt_pigs" -> isKorean ? "돼지 10마리 사냥" : "Hunt 10 Pigs";
            case "gather_pork" -> isKorean ? "돼지고기 15개 수집" : "Gather 15 Raw Porkchop";
            case "catch_fish" -> isKorean ? "물고기 20마리 낚기" : "Catch 20 Fish";
            case "gather_fish" -> isKorean ? "대구 10개 수집" : "Gather 10 Raw Cod";
            case "gather_salmon" -> isKorean ? "연어 10개 수집" : "Gather 10 Raw Salmon";
            case "harvest_vegetables" -> isKorean ? "채소 30개 수확" : "Harvest 30 Vegetables";
            case "gather_carrots" -> isKorean ? "당근 20개 수집" : "Gather 20 Carrots";
            case "gather_potatoes" -> isKorean ? "감자 20개 수집" : "Gather 20 Potatoes";
            case "gather_beetroot" -> isKorean ? "비트 15개 수집" : "Gather 15 Beetroot";
            case "gather_apples" -> isKorean ? "사과 10개 수집" : "Gather 10 Apples";
            case "gather_melons" -> isKorean ? "수박 조각 16개 수집" : "Gather 16 Melon Slices";
            case "setup_furnace" -> isKorean ? "화로 3개 설치" : "Place 3 Furnaces";
            case "setup_smoker" -> isKorean ? "훈연기 2개 설치" : "Place 2 Smokers";
            case "setup_campfire" -> isKorean ? "모닥불 설치" : "Place Campfire";
            case "setup_cauldron" -> isKorean ? "가마솥 2개 설치" : "Place 2 Cauldrons";
            case "bake_bread" -> isKorean ? "빵 20개 굽기" : "Bake 20 Bread";
            case "cook_beef" -> isKorean ? "익힌 소고기 15개 요리" : "Cook 15 Steaks";
            case "cook_pork" -> isKorean ? "익힌 돼지고기 15개 요리" : "Cook 15 Cooked Porkchops";
            case "cook_fish" -> isKorean ? "익힌 대구 10개 요리" : "Cook 10 Cooked Cod";
            case "bake_potato" -> isKorean ? "구운 감자 20개 요리" : "Bake 20 Potatoes";
            case "make_cookies" -> isKorean ? "쿠키 32개 제작" : "Make 32 Cookies";
            case "make_pie" -> isKorean ? "호박 파이 5개 제작" : "Make 5 Pumpkin Pies";
            case "make_cake" -> isKorean ? "케이크 3개 제작" : "Make 3 Cakes";
            case "make_stew" -> isKorean ? "토끼 스튜 5개 제작" : "Make 5 Rabbit Stews";
            case "make_soup" -> isKorean ? "버섯 스튜 5개 제작" : "Make 5 Mushroom Stews";
            case "special_ingredient" -> isKorean ? "금 주괴 8개 수집" : "Gather 8 Gold Ingots";
            case "golden_apple" -> isKorean ? "황금 사과 2개 제작" : "Craft 2 Golden Apples";
            case "deliver_feast" -> isKorean ? "케이크 납품" : "Deliver Cake";
            case "deliver_golden" -> isKorean ? "황금 사과 납품" : "Deliver Golden Apple";
            case "graduation" -> isKorean ? "졸업식" : "Graduation ceremony";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("master_chef_dialog");

        dialog.addLine("요리 마스터",
                "오, 새로운 제자 후보군인가? 요리의 세계에 오신 것을 환영하네!",
                "Oh, a new apprentice candidate? Welcome to the world of cooking!");

        dialog.addLine("요리 마스터",
                "진정한 요리사가 되려면 재료를 아끼지 않고 정성을 다해야 하지.",
                "To become a true chef, you must not spare ingredients and put your heart into it.");

        dialog.addLine("플레이어",
                "가르쳐 주시면 열심히 배우겠습니다!",
                "If you teach me, I'll learn diligently!");

        dialog.addLine("요리 마스터",
                "좋아! 먼저 기초부터 시작하자. 신선한 재료 수집이 첫 번째야.",
                "Good! Let's start with the basics. Gathering fresh ingredients is first.");

        // 중간 대화
        dialog.addLine("요리 마스터",
                "재료 준비가 잘 되었군! 이제 본격적으로 요리를 시작해보자.",
                "Ingredients are well prepared! Now let's start cooking in earnest.");

        dialog.addLine("요리 마스터",
                "기억해, 불 조절과 타이밍이 맛을 좌우한다네.",
                "Remember, heat control and timing determine the taste.");

        // 고급 요리
        dialog.addLine("요리 마스터",
                "기초는 충분히 익혔군. 이제 고급 요리에 도전할 때야.",
                "You've mastered the basics. Now it's time to challenge advanced dishes.");

        dialog.addLine("요리 마스터",
                "케이크, 파이, 스튜... 복잡하지만 그만큼 보람 있는 요리들이지.",
                "Cakes, pies, stews... Complex but equally rewarding dishes.");

        // 특별 요리
        dialog.addLine("요리 마스터",
                "마지막 시험이다. 황금 사과를 만들어보게.",
                "This is the final test. Try making a golden apple.");

        dialog.addLine("요리 마스터",
                "이건 단순한 요리가 아니야. 마법과 요리의 조화란다.",
                "This isn't just cooking. It's the harmony of magic and cuisine.");

        // 완료
        dialog.addLine("요리 마스터",
                "훌륭해! 자네는 이제 진정한 요리사야!",
                "Excellent! You are now a true chef!");

        dialog.addLine("요리 마스터",
                "이 레시피북은 내가 평생 모은 비법들이야. 잘 사용하게나.",
                "This recipe book contains secrets I've gathered my whole life. Use it well.");

        return dialog;
    }
}