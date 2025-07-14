package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.objective.impl.BreakBlockObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

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
                .reward(BasicReward.builder()
                        .addCurrency(CurrencyType.GOLD, 150)
                        .addItem(new ItemStack(Material.IRON_PICKAXE))
                        .addItem(new ItemStack(Material.TORCH, 32))
                        .addExperience(100)
                        .build())
                .sequential(false)
                .daily(true)  // 일일 퀘스트 설정
                .category(QuestCategory.DAILY)
                .minLevel(1);
    }
}