package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 약초 수집 - 사이드 퀘스트
 * 연금술사를 위해 특정 약초들을 수집
 *
 * @author Febrie
 */
public class CollectHerbsQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class CollectHerbsBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new CollectHerbsQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public CollectHerbsQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private CollectHerbsQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static Builder createBuilder() {
        List<QuestObjective> objectives = new ArrayList<>();
        
        // 약초 수집 목표
        objectives.add(new CollectItemObjective("collect_dandelions", Material.DANDELION, 15));
        objectives.add(new CollectItemObjective("collect_poppies", Material.POPPY, 10));
        objectives.add(new CollectItemObjective("collect_azure_bluets", Material.AZURE_BLUET, 10));
        objectives.add(new CollectItemObjective("collect_spider_eyes", Material.SPIDER_EYE, 5));

        return new CollectHerbsBuilder()
                .id(QuestID.SIDE_COLLECT_HERBS)
                .objectives(objectives)
                .reward(BasicReward.builder()
                        .addCurrency(CurrencyType.GOLD, 250)
                        .addCurrency(CurrencyType.EMERALD, 8)
                        .addItem(new ItemStack(Material.POTION, 5)) // 치유 물약
                        .addItem(new ItemStack(Material.GLISTERING_MELON_SLICE, 3))
                        .addExperience(150)
                        .build())
                .sequential(false) // 순서 상관없이 수집 가능
                .category(QuestCategory.SIDE)
                .minLevel(5);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "약초 수집" : "Collect Herbs";
    }

    @Override
    public @NotNull List<String> getDescription(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "연금술사가 약초를 구하고 있습니다.",
                    "필요한 재료들을 모아주세요.",
                    "",
                    "목표:",
                    "• 민들레 15개",
                    "• 양귀비 10개",
                    "• 파란 난초 10개",
                    "• 거미 눈 5개",
                    "",
                    "보상:",
                    "• 골드 250",
                    "• 에메랄드 8",
                    "• 치유 물약 5개",
                    "• 반짝이는 수박 조각 3개",
                    "• 경험치 150"
            );
        } else {
            return Arrays.asList(
                    "The alchemist is looking for herbs.",
                    "Please collect the required materials.",
                    "",
                    "Objectives:",
                    "• 15 Dandelions",
                    "• 10 Poppies",
                    "• 10 Azure Bluets",
                    "• 5 Spider Eyes",
                    "",
                    "Rewards:",
                    "• 250 Gold",
                    "• 8 Emeralds",
                    "• 5 Healing Potions",
                    "• 3 Glistering Melon Slices",
                    "• 150 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "collect_dandelions" -> isKorean ? "민들레 15개 수집" : "Collect 15 Dandelions";
            case "collect_poppies" -> isKorean ? "양귀비 10개 수집" : "Collect 10 Poppies";
            case "collect_azure_bluets" -> isKorean ? "파란 난초 10개 수집" : "Collect 10 Azure Bluets";
            case "collect_spider_eyes" -> isKorean ? "거미 눈 5개 수집" : "Collect 5 Spider Eyes";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("collect_herbs_dialog");

        dialog.addLine("연금술사 미나",
                "안녕하세요, 모험가님. 혹시 약초 수집에 능숙하신가요?",
                "Hello, adventurer. Are you skilled in herb gathering?");

        dialog.addLine("연금술사 미나",
                "새로운 물약을 만들기 위해 특별한 재료들이 필요해요.",
                "I need special ingredients to create new potions.");

        dialog.addLine("연금술사 미나",
                "민들레 15개, 양귀비 10개, 파란 난초 10개, 그리고 거미 눈 5개를 구해주시면 보답하겠습니다.",
                "If you bring me 15 dandelions, 10 poppies, 10 azure bluets, and 5 spider eyes, I'll reward you.");

        return dialog;
    }
}