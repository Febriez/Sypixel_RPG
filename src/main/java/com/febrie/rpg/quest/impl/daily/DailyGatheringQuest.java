package com.febrie.rpg.quest.impl.daily;

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
 * 일일 채집 - 일일 퀘스트
 * 매일 수행할 수 있는 자원 수집 퀘스트
 *
 * @author Febrie
 */
public class DailyGatheringQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class DailyGatheringBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new DailyGatheringQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DailyGatheringQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private DailyGatheringQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new DailyGatheringBuilder()
                .id(QuestID.DAILY_GATHERING)
                .objectives(Arrays.asList(
                        // 기본 자원 수집
                        new InteractNPCObjective("meet_foreman", "gathering_foreman"), // 채집 감독관
                        new CollectItemObjective("gather_wood", Material.OAK_LOG, 32),
                        new CollectItemObjective("gather_stone", Material.COBBLESTONE, 64),
                        new CollectItemObjective("gather_coal", Material.COAL, 16),
                        
                        // 광물 채굴
                        new BreakBlockObjective("mine_iron", Material.IRON_ORE, 10),
                        new BreakBlockObjective("mine_gold", Material.GOLD_ORE, 5),
                        new CollectItemObjective("gather_iron", Material.IRON_INGOT, 10),
                        
                        // 농업 활동
                        new HarvestObjective("harvest_crops", Material.WHEAT, 20),
                        new CollectItemObjective("gather_wheat", Material.WHEAT, 20),
                        new CollectItemObjective("gather_carrots", Material.CARROT, 15),
                        
                        // 특수 자원
                        new CollectItemObjective("gather_flowers", Material.DANDELION, 5),
                        new CollectItemObjective("gather_saplings", Material.OAK_SAPLING, 5),
                        
                        // 납품
                        new DeliverItemObjective("deliver_resources", "gathering_supervisor", Material.CHEST, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1500)
                        .addCurrency(CurrencyType.DIAMOND, 10)
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 2))
                        .addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 10))
                        .addExperience(500)
                        .build())
                .sequential(false)  // 자유롭게 진행 가능
                .repeatable(true)
                .daily(true)       // 일일 퀘스트
                .category(QuestCategory.DAILY)
                .minLevel(5)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "일일 자원 수집" : "Daily Resource Gathering";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "마을에 필요한 다양한 자원을 수집해주세요.",
                    "매일 리셋되며 반복 수행이 가능합니다.",
                    "",
                    "다양한 활동을 통해 자원을 모으세요:",
                    "• 나무 베기와 돌 캐기",
                    "• 광물 채굴과 제련",
                    "• 농작물 재배와 수확",
                    "• 특수 자원 수집",
                    "",
                    "모든 목표는 동시에 진행할 수 있습니다.",
                    "",
                    "목표:",
                    "• 채집 감독관과 대화",
                    "• 나무 32개 수집",
                    "• 조약돌 64개 수집",
                    "• 석탄 16개 수집",
                    "• 철광석 10개 채굴",
                    "• 금광석 5개 채굴",
                    "• 철 주괴 10개 수집",
                    "• 작물 20개 수확",
                    "• 밀 20개, 당근 15개 수집",
                    "• 꽃 5개, 묘목 5개 수집",
                    "• 수집한 자원 납품",
                    "",
                    "보상:",
                    "• 골드 1500",
                    "• 다이아몬드 10개",
                    "• 황금 사과 2개",
                    "• 경험치 병 10개",
                    "• 경험치 500"
            );
        } else {
            return Arrays.asList(
                    "Gather various resources needed for the village.",
                    "Resets daily and can be repeated.",
                    "",
                    "Collect resources through various activities:",
                    "• Wood cutting and stone mining",
                    "• Ore mining and smelting",
                    "• Crop farming and harvesting",
                    "• Special resource collection",
                    "",
                    "All objectives can progress simultaneously.",
                    "",
                    "Objectives:",
                    "• Talk to the Gathering Foreman",
                    "• Gather 32 wood logs",
                    "• Gather 64 cobblestone",
                    "• Gather 16 coal",
                    "• Mine 10 iron ore",
                    "• Mine 5 gold ore",
                    "• Gather 10 iron ingots",
                    "• Harvest 20 crops",
                    "• Gather 20 wheat, 15 carrots",
                    "• Gather 5 flowers, 5 saplings",
                    "• Deliver collected resources",
                    "",
                    "Rewards:",
                    "• 1500 Gold",
                    "• 10 Diamonds",
                    "• 2 Golden Apples",
                    "• 10 Experience Bottles",
                    "• 500 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "meet_foreman" -> isKorean ? "채집 감독관과 대화" : "Talk to the Gathering Foreman";
            case "gather_wood" -> isKorean ? "참나무 원목 32개 수집" : "Gather 32 Oak Logs";
            case "gather_stone" -> isKorean ? "조약돌 64개 수집" : "Gather 64 Cobblestone";
            case "gather_coal" -> isKorean ? "석탄 16개 수집" : "Gather 16 Coal";
            case "mine_iron" -> isKorean ? "철광석 10개 채굴" : "Mine 10 Iron Ore";
            case "mine_gold" -> isKorean ? "금광석 5개 채굴" : "Mine 5 Gold Ore";
            case "gather_iron" -> isKorean ? "철 주괴 10개 수집" : "Gather 10 Iron Ingots";
            case "harvest_crops" -> isKorean ? "작물 20개 수확" : "Harvest 20 Crops";
            case "gather_wheat" -> isKorean ? "밀 20개 수집" : "Gather 20 Wheat";
            case "gather_carrots" -> isKorean ? "당근 15개 수집" : "Gather 15 Carrots";
            case "gather_flowers" -> isKorean ? "꽃 5개 수집" : "Gather 5 Flowers";
            case "gather_saplings" -> isKorean ? "묘목 5개 수집" : "Gather 5 Saplings";
            case "deliver_resources" -> isKorean ? "수집한 자원 납품" : "Deliver collected resources";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("daily_gathering_dialog");

        dialog.addLine("채집 감독관",
                "아, 좋은 타이밍에 왔네! 마을에 자원이 부족해서 도움이 필요해.",
                "Ah, perfect timing! The village is low on resources and needs help.");

        dialog.addLine("채집 감독관",
                "나무, 돌, 광물, 그리고 식량... 모든 것이 필요하다네.",
                "Wood, stone, minerals, and food... we need everything.");

        dialog.addLine("플레이어",
                "어떤 것들을 모아야 하나요?",
                "What should I gather?");

        dialog.addLine("채집 감독관",
                "목록을 줄 테니 가능한 모든 것을 모아와 주게. 특히 철이 많이 필요해.",
                "I'll give you a list. Gather everything you can. We especially need iron.");

        dialog.addLine("채집 감독관",
                "작물도 수확해주면 좋겠어. 마을 사람들이 배가 고프거든.",
                "It would be great if you could harvest crops too. The villagers are hungry.");

        dialog.addLine("플레이어",
                "알겠습니다. 바로 시작하겠습니다!",
                "Got it. I'll start right away!");

        dialog.addLine("채집 감독관",
                "고맙네! 모든 자원을 모으면 상자에 담아서 가져와 주게.",
                "Thanks! When you've gathered everything, bring it back in a chest.");

        return dialog;
    }
}