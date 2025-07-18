package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.BreakBlockObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 일일 채광 - 일일 퀘스트
 * 매일 리셋되는 채광 퀘스트
 *
 * @author Febrie
 */
public class DailyMiningQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class DailyMiningBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new DailyMiningQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public DailyMiningQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private DailyMiningQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static Builder createBuilder() {
        return new DailyMiningBuilder()
                .id(QuestID.DAILY_MINING)
                .objectives(Arrays.asList(
                        new BreakBlockObjective("mine_stone", Material.STONE, 50),
                        new BreakBlockObjective("mine_coal", Material.COAL_ORE, 20),
                        new BreakBlockObjective("mine_iron", Material.IRON_ORE, 10)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 150)
                        .addItem(new ItemStack(Material.IRON_PICKAXE))
                        .addItem(new ItemStack(Material.TORCH, 32))
                        .addExperience(100)
                        .build())
                .sequential(false)
                .daily(true)  // 일일 퀘스트 설정
                .category(QuestCategory.DAILY)
                .minLevel(1)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "일일 채광" : "Daily Mining";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "오늘의 채광 목표를 완료하세요.",
                    "매일 자정에 리셋됩니다.",
                    "",
                    "목표:",
                    "• 돌 50개 채광",
                    "• 석탄 광석 20개 채광",
                    "• 철 광석 10개 채광",
                    "",
                    "보상:",
                    "• 골드 150",
                    "• 철 곡괭이",
                    "• 횃불 32개",
                    "• 경험치 100"
            );
        } else {
            return Arrays.asList(
                    "Complete today's mining objectives.",
                    "Resets daily at midnight.",
                    "",
                    "Objectives:",
                    "• Mine 50 stone",
                    "• Mine 20 coal ore",
                    "• Mine 10 iron ore",
                    "",
                    "Rewards:",
                    "• 150 Gold",
                    "• Iron Pickaxe",
                    "• 32 Torches",
                    "• 100 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "mine_stone" -> isKorean ? "돌 50개 채광" : "Mine 50 stone";
            case "mine_coal" -> isKorean ? "석탄 광석 20개 채광" : "Mine 20 coal ore";
            case "mine_iron" -> isKorean ? "철 광석 10개 채광" : "Mine 10 iron ore";
            default -> objective.getStatusInfo(null);
        };
    }
}