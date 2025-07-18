package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
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
 * 대장장이의 제자 - 사이드 퀘스트
 * 마을 대장장이의 제자가 되어 기술을 배우는 퀘스트
 *
 * @author Febrie
 */
public class BlacksmithApprenticeQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class BlacksmithApprenticeBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new BlacksmithApprenticeQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public BlacksmithApprenticeQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private BlacksmithApprenticeQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new BlacksmithApprenticeBuilder()
                .id(QuestID.SIDE_BLACKSMITH_APPRENTICE)
                .objectives(Arrays.asList(
                        // 대장장이 만나기
                        new InteractNPCObjective("meet_blacksmith", 112), // 마을 대장장이
                        
                        // 기초 재료 수집
                        new CollectItemObjective("gather_coal", Material.COAL, 32),
                        new BreakBlockObjective("mine_iron", Material.IRON_ORE, 15),
                        new CollectItemObjective("gather_iron", Material.IRON_INGOT, 15),
                        
                        // 작업장 준비
                        new PlaceBlockObjective("setup_anvil", Material.ANVIL, 1),
                        new PlaceBlockObjective("setup_furnace", Material.FURNACE, 2),
                        new CollectItemObjective("gather_water", Material.WATER_BUCKET, 2),
                        
                        // 첫 번째 작품 - 도구
                        new CraftItemObjective("craft_pickaxe", Material.IRON_PICKAXE, 1),
                        new CraftItemObjective("craft_shovel", Material.IRON_SHOVEL, 1),
                        new CraftItemObjective("craft_axe", Material.IRON_AXE, 1),
                        new DeliverItemObjective("deliver_tools", "blacksmith", Material.IRON_PICKAXE, 1),
                        
                        // 고급 재료 수집
                        new KillMobObjective("hunt_skeletons", EntityType.SKELETON, 10),
                        new CollectItemObjective("gather_bones", Material.BONE, 20),
                        new CollectItemObjective("gather_string", Material.STRING, 10),
                        
                        // 두 번째 작품 - 무기
                        new CraftItemObjective("craft_sword", Material.IRON_SWORD, 2),
                        new CraftItemObjective("craft_bow", Material.BOW, 1),
                        new CraftItemObjective("craft_arrows", Material.ARROW, 64),
                        
                        // 품질 테스트
                        new KillMobObjective("test_weapons", EntityType.ZOMBIE, 15),
                        new InteractNPCObjective("report_test", 112),
                        
                        // 최종 시험 - 특별 주문
                        new CollectItemObjective("special_material", Material.DIAMOND, 3),
                        new CraftItemObjective("craft_special", Material.DIAMOND_SWORD, 1),
                        new DeliverItemObjective("deliver_special", "knight_captain", Material.DIAMOND_SWORD, 1), // 기사단장
                        
                        // 졸업
                        new InteractNPCObjective("graduation", 112)
                ))
                .reward(BasicReward.builder()
                        .addCurrency(CurrencyType.GOLD, 2500)
                        .addCurrency(CurrencyType.DIAMOND, 20)
                        .addItem(new ItemStack(Material.SMITHING_TABLE))
                        .addItem(new ItemStack(Material.IRON_CHESTPLATE))
                        .addItem(new ItemStack(Material.WRITTEN_BOOK)) // 대장장이 기술서
                        .addExperience(1500)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SIDE)
                .minLevel(10)
                .maxLevel(0);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "대장장이의 제자" : "The Blacksmith's Apprentice";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "마을의 대장장이가 새로운 제자를 찾고 있습니다.",
                    "대장장이의 기술을 배우고 장인의 길을 시작하세요.",
                    "",
                    "🔨 이 퀘스트를 통해 기초 대장간 기술을 배울 수 있습니다.",
                    "",
                    "수련 과정:",
                    "• 1단계: 기초 재료 수집과 작업장 준비",
                    "• 2단계: 도구 제작 실습",
                    "• 3단계: 무기 제작과 품질 테스트",
                    "• 4단계: 특별 주문 완성",
                    "",
                    "목표:",
                    "• 마을 대장장이와 대화",
                    "• 석탄과 철 수집",
                    "• 작업장 설치",
                    "• 철 도구 제작",
                    "• 몬스터 사냥으로 재료 수집",
                    "• 무기 제작 및 테스트",
                    "• 다이아몬드 검 제작",
                    "• 기사단장에게 납품",
                    "• 졸업식",
                    "",
                    "보상:",
                    "• 골드 2,500",
                    "• 다이아몬드 20개",
                    "• 대장장이 작업대",
                    "• 철 흉갑",
                    "• 대장장이 기술서",
                    "• 경험치 1,500"
            );
        } else {
            return Arrays.asList(
                    "The village blacksmith is looking for a new apprentice.",
                    "Learn the blacksmith's craft and begin the path of a craftsman.",
                    "",
                    "🔨 Through this quest, you can learn basic smithing skills.",
                    "",
                    "Training Process:",
                    "• Stage 1: Gather basic materials and prepare workshop",
                    "• Stage 2: Tool crafting practice",
                    "• Stage 3: Weapon crafting and quality testing",
                    "• Stage 4: Complete special order",
                    "",
                    "Objectives:",
                    "• Talk to the Village Blacksmith",
                    "• Gather coal and iron",
                    "• Set up workshop",
                    "• Craft iron tools",
                    "• Hunt monsters for materials",
                    "• Craft and test weapons",
                    "• Craft diamond sword",
                    "• Deliver to Knight Captain",
                    "• Graduation ceremony",
                    "",
                    "Rewards:",
                    "• 2,500 Gold",
                    "• 20 Diamonds",
                    "• Smithing Table",
                    "• Iron Chestplate",
                    "• Blacksmith's Manual",
                    "• 1,500 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "meet_blacksmith" -> isKorean ? "마을 대장장이와 대화" : "Talk to the Village Blacksmith";
            case "gather_coal" -> isKorean ? "석탄 32개 수집" : "Gather 32 Coal";
            case "mine_iron" -> isKorean ? "철광석 15개 채굴" : "Mine 15 Iron Ore";
            case "gather_iron" -> isKorean ? "철 주괴 15개 수집" : "Gather 15 Iron Ingots";
            case "setup_anvil" -> isKorean ? "모루 설치" : "Set up an Anvil";
            case "setup_furnace" -> isKorean ? "화로 2개 설치" : "Set up 2 Furnaces";
            case "gather_water" -> isKorean ? "물 양동이 2개 준비" : "Prepare 2 Water Buckets";
            case "craft_pickaxe" -> isKorean ? "철 곡괭이 제작" : "Craft Iron Pickaxe";
            case "craft_shovel" -> isKorean ? "철 삽 제작" : "Craft Iron Shovel";
            case "craft_axe" -> isKorean ? "철 도끼 제작" : "Craft Iron Axe";
            case "deliver_tools" -> isKorean ? "대장장이에게 도구 전달" : "Deliver tools to Blacksmith";
            case "hunt_skeletons" -> isKorean ? "스켈레톤 10마리 사냥" : "Hunt 10 Skeletons";
            case "gather_bones" -> isKorean ? "뼈 20개 수집" : "Gather 20 Bones";
            case "gather_string" -> isKorean ? "실 10개 수집" : "Gather 10 String";
            case "craft_sword" -> isKorean ? "철 검 2개 제작" : "Craft 2 Iron Swords";
            case "craft_bow" -> isKorean ? "활 제작" : "Craft a Bow";
            case "craft_arrows" -> isKorean ? "화살 64개 제작" : "Craft 64 Arrows";
            case "test_weapons" -> isKorean ? "좀비 15마리로 무기 테스트" : "Test weapons on 15 Zombies";
            case "report_test" -> isKorean ? "테스트 결과 보고" : "Report test results";
            case "special_material" -> isKorean ? "다이아몬드 3개 수집" : "Gather 3 Diamonds";
            case "craft_special" -> isKorean ? "다이아몬드 검 제작" : "Craft Diamond Sword";
            case "deliver_special" -> isKorean ? "기사단장에게 특별 주문 전달" : "Deliver special order to Knight Captain";
            case "graduation" -> isKorean ? "대장장이와 졸업식" : "Graduation ceremony with Blacksmith";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("blacksmith_apprentice_dialog");

        dialog.addLine("마을 대장장이",
                "아, 젊은이! 혹시 대장간 일에 관심이 있나?",
                "Ah, young one! Are you interested in smithing work?");

        dialog.addLine("마을 대장장이",
                "나이가 들어서 그런지 혼자 일하기가 힘들어졌네. 제자를 구하고 있었는데...",
                "I'm getting old and working alone has become difficult. I've been looking for an apprentice...");

        dialog.addLine("플레이어",
                "제가 도와드릴 수 있을까요?",
                "Can I help you?");

        dialog.addLine("마을 대장장이",
                "좋아! 먼저 기초부터 가르쳐주지. 석탄과 철을 모아오면 시작하도록 하자.",
                "Good! I'll teach you from the basics. Gather coal and iron, then we'll begin.");

        // 중간 대화
        dialog.addLine("마을 대장장이",
                "훌륭해! 도구 제작을 잘 해냈군. 이제 무기를 만들어볼 차례야.",
                "Excellent! You've done well with the tools. Now it's time to make weapons.");

        dialog.addLine("마을 대장장이",
                "무기는 도구보다 더 정교한 기술이 필요하지. 균형과 날카로움이 중요해.",
                "Weapons require more refined techniques than tools. Balance and sharpness are crucial.");

        // 특별 주문
        dialog.addLine("마을 대장장이",
                "기사단장님이 특별한 검을 주문했네. 이건 네 최종 시험이 될 거야.",
                "The Knight Captain has ordered a special sword. This will be your final test.");

        dialog.addLine("마을 대장장이",
                "다이아몬드로 만든 검이야. 최고의 기술을 발휘해야 할 거야.",
                "A sword made of diamond. You'll need to use your best techniques.");

        // 졸업
        dialog.addLine("마을 대장장이",
                "정말 자랑스럽구나! 이제 넌 진정한 대장장이야.",
                "I'm so proud! You're now a true blacksmith.");

        dialog.addLine("마을 대장장이",
                "이 기술서를 가져가거라. 내가 평생 모은 지식이 담겨있다.",
                "Take this manual. It contains all the knowledge I've gathered in my lifetime.");

        return dialog;
    }
}