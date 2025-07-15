package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 일일 사냥 - 일일 퀘스트
 * 매일 리셋되는 사냥 퀘스트
 *
 * @author Febrie
 */
public class DailyHuntingQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class DailyHuntingBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new DailyHuntingQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public DailyHuntingQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private DailyHuntingQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static Builder createBuilder() {
        return new DailyHuntingBuilder()
                .id(QuestID.DAILY_HUNTING)
                .objectives(Arrays.asList(
                        new KillMobObjective("kill_zombies", EntityType.ZOMBIE, 20),
                        new KillMobObjective("kill_skeletons", EntityType.SKELETON, 15),
                        new KillMobObjective("kill_creepers", EntityType.CREEPER, 10)
                ))
                .reward(BasicReward.builder()
                        .addCurrency(CurrencyType.GOLD, 200)
                        .addItem(new ItemStack(Material.ARROW, 64))
                        .addItem(new ItemStack(Material.COOKED_BEEF, 32))
                        .addExperience(150)
                        .build())
                .sequential(false)
                .daily(true)  // daily 설정하면 자동으로 repeatable도 true가 됨
                .category(QuestCategory.DAILY)
                .minLevel(5);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "일일 사냥" : "Daily Hunting";
    }

    @Override
    public @NotNull List<String> getDescription(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "오늘의 사냥 목표를 완료하세요.",
                    "매일 자정에 리셋됩니다.",
                    "",
                    "목표:",
                    "• 좀비 20마리 처치",
                    "• 스켈레톤 15마리 처치",
                    "• 크리퍼 10마리 처치",
                    "",
                    "보상:",
                    "• 골드 200",
                    "• 화살 64개",
                    "• 익힌 소고기 32개",
                    "• 경험치 150"
            );
        } else {
            return Arrays.asList(
                    "Complete today's hunting objectives.",
                    "Resets daily at midnight.",
                    "",
                    "Objectives:",
                    "• Kill 20 zombies",
                    "• Kill 15 skeletons",
                    "• Kill 10 creepers",
                    "",
                    "Rewards:",
                    "• 200 Gold",
                    "• 64 Arrows",
                    "• 32 Cooked Beef",
                    "• 150 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "kill_zombies" -> isKorean ? "좀비 20마리 처치" : "Kill 20 zombies";
            case "kill_skeletons" -> isKorean ? "스켈레톤 15마리 처치" : "Kill 15 skeletons";
            case "kill_creepers" -> isKorean ? "크리퍼 10마리 처치" : "Kill 10 creepers";
            default -> objective.getStatusInfo(null);
        };
    }
}