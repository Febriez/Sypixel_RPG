package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.DeliverItemObjective;
import com.febrie.rpg.quest.objective.impl.HarvestObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 농부의 부탁 - 사이드 퀘스트
 * 농부를 도와 농작물을 수확하고 전달
 *
 * @author Febrie
 */
public class FarmersRequestQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class FarmersRequestBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new FarmersRequestQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public FarmersRequestQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private FarmersRequestQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static Builder createBuilder() {
        // 농작물 수확 목표
        List<QuestObjective> objectives = new ArrayList<>();
        objectives.add(new HarvestObjective("harvest_wheat", Material.WHEAT, 30));
        objectives.add(new HarvestObjective("harvest_carrots", Material.CARROTS, 20));
        objectives.add(new HarvestObjective("harvest_potatoes", Material.POTATOES, 20));

        // 농부에게 전달 목표
        Map<Material, Integer> deliveryItems = new HashMap<>();
        deliveryItems.put(Material.WHEAT, 30);
        deliveryItems.put(Material.CARROT, 20);
        deliveryItems.put(Material.POTATO, 20);

        objectives.add(new DeliverItemObjective(
                "deliver_to_farmer",
                "농부 김씨",
                deliveryItems
        ));

        return new FarmersRequestBuilder()
                .id(QuestID.SIDE_FARMERS_REQUEST)
                .objectives(objectives)
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 300)
                        .addCurrency(CurrencyType.EMERALD, 10)
                        .addItem(new ItemStack(Material.GOLDEN_HOE))
                        .addItem(new ItemStack(Material.BONE_MEAL, 64))
                        .addExperience(200)
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(3)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "농부의 부탁" : "Farmer's Request";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "농부 김씨가 도움을 요청했습니다.",
                    "농작물을 수확하고 전달해주세요.",
                    "",
                    "목표:",
                    "• 밀 30개 수확",
                    "• 당근 20개 수확",
                    "• 감자 20개 수확",
                    "• 농부에게 전달",
                    "",
                    "보상:",
                    "• 골드 300",
                    "• 에메랄드 10",
                    "• 황금 괭이",
                    "• 뼛가루 64개",
                    "• 경험치 200"
            );
        } else {
            return Arrays.asList(
                    "Farmer Kim has requested help.",
                    "Please harvest crops and deliver them.",
                    "",
                    "Objectives:",
                    "• Harvest 30 wheat",
                    "• Harvest 20 carrots",
                    "• Harvest 20 potatoes",
                    "• Deliver to farmer",
                    "",
                    "Rewards:",
                    "• 300 Gold",
                    "• 10 Emeralds",
                    "• Golden Hoe",
                    "• 64 Bone Meal",
                    "• 200 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "harvest_wheat" -> isKorean ? "밀 30개 수확" : "Harvest 30 wheat";
            case "harvest_carrots" -> isKorean ? "당근 20개 수확" : "Harvest 20 carrots";
            case "harvest_potatoes" -> isKorean ? "감자 20개 수확" : "Harvest 20 potatoes";
            case "deliver_to_farmer" -> isKorean ? "농부에게 전달" : "Deliver to farmer";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("farmers_request_dialog");

        dialog.addLine("농부 김씨",
                "안녕하세요! 모험가님, 도와주실 수 있나요?",
                "Hello! Adventurer, can you help me?");

        dialog.addLine("농부 김씨",
                "수확할 농작물이 너무 많아서 혼자서는 힘들어요.",
                "I have too many crops to harvest alone.");

        dialog.addLine("농부 김씨",
                "밀 30개, 당근 20개, 감자 20개를 수확해서 가져다주시면 보상을 드리겠습니다!",
                "If you harvest 30 wheat, 20 carrots, and 20 potatoes for me, I'll give you a reward!");

        return dialog;
    }
}