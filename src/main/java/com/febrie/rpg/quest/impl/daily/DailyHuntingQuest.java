package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

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
}