package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.FishingObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 일일 낚시 - 일일 퀘스트
 * 매일 일정량의 물고기를 낚는 퀘스트
 *
 * @author Febrie
 */
public class DailyFishingQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class DailyFishingBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new DailyFishingQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public DailyFishingQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private DailyFishingQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        List<QuestObjective> objectives = new ArrayList<>();
        
        // 낚시 목표
        objectives.add(new FishingObjective("catch_any_fish", FishingObjective.FishType.ANY, 10)); // 아무 물고기나 10마리
        objectives.add(new FishingObjective("catch_salmon", FishingObjective.FishType.SPECIFIC, 5, Material.SALMON)); // 연어 5마리
        objectives.add(new FishingObjective("catch_pufferfish", FishingObjective.FishType.SPECIFIC, 2, Material.PUFFERFISH)); // 복어 2마리

        return new DailyFishingBuilder()
                .id(QuestID.DAILY_FISHING)
                .objectives(objectives)
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 200)
                        .addCurrency(CurrencyType.EMERALD, 5)
                        .addItem(new ItemStack(Material.FISHING_ROD)) // 낚싯대
                        .addItem(new ItemStack(Material.COOKED_SALMON, 16))
                        .addExperience(100)
                        .build())
                .sequential(false) // 순서 상관없이 진행 가능
                .category(QuestCategory.DAILY)
                .minLevel(3)
                .repeatable(true) // 24시간 쿨다운은 별도 관리
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "일일 낚시" : "Daily Fishing";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "오늘의 낚시 할당량을 채워주세요!",
                    "매일 리셋되는 일일 퀘스트입니다.",
                    "",
                    "목표:",
                    "• 물고기 10마리 낚기",
                    "• 연어 5마리 낚기",
                    "• 복어 2마리 낚기",
                    "",
                    "보상:",
                    "• 골드 200",
                    "• 에메랄드 5",
                    "• 낚싯대",
                    "• 구운 연어 16개",
                    "• 경험치 100",
                    "",
                    "일일 퀘스트 - 24시간마다 반복 가능"
            );
        } else {
            return Arrays.asList(
                    "Complete today's fishing quota!",
                    "This is a daily quest that resets every day.",
                    "",
                    "Objectives:",
                    "• Catch 10 fish",
                    "• Catch 5 salmon",
                    "• Catch 2 pufferfish",
                    "",
                    "Rewards:",
                    "• 200 Gold",
                    "• 5 Emeralds",
                    "• Fishing Rod",
                    "• 16 Cooked Salmon",
                    "• 100 Experience",
                    "",
                    "Daily Quest - Repeatable every 24 hours"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "catch_any_fish" -> isKorean ? "물고기 10마리 낚기" : "Catch 10 fish";
            case "catch_salmon" -> isKorean ? "연어 5마리 낚기" : "Catch 5 salmon";
            case "catch_pufferfish" -> isKorean ? "복어 2마리 낚기" : "Catch 2 pufferfish";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("daily_fishing_dialog");

        dialog.addLine("어부 김씨",
                "안녕하세요! 오늘도 낚시하러 오셨군요?",
                "Hello! Are you here to fish again today?");

        dialog.addLine("어부 김씨",
                "매일 물고기를 잡아오면 보상을 드리고 있어요.",
                "I give rewards for catching fish every day.");

        dialog.addLine("어부 김씨",
                "아무 물고기나 10마리, 연어 5마리, 그리고 복어 2마리를 잡아주세요!",
                "Please catch any 10 fish, 5 salmon, and 2 pufferfish!");

        dialog.addLine("어부 김씨",
                "내일 다시 와도 같은 보상을 드릴게요!",
                "Come back tomorrow for the same rewards!");

        return dialog;
    }
}