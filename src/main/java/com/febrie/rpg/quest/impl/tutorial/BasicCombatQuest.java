package com.febrie.rpg.quest.impl.tutorial;

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
 * 기초 전투 - 튜토리얼 퀘스트 2
 * 전투의 기본을 배우는 퀘스트
 *
 * @author Febrie
 */
public class BasicCombatQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class BasicCombatBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new BasicCombatQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public BasicCombatQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private BasicCombatQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static Builder createBuilder() {
        return new BasicCombatBuilder()
                .id(QuestID.TUTORIAL_BASIC_COMBAT)
                .objectives(Arrays.asList(
                        new KillMobObjective("kill_zombies", EntityType.ZOMBIE, 5),
                        new KillMobObjective("kill_skeletons", EntityType.SKELETON, 3)
                ))
                .reward(BasicReward.builder()
                        .addCurrency(CurrencyType.GOLD, 200)
                        .addItem(new ItemStack(Material.IRON_SWORD))
                        .addItem(new ItemStack(Material.IRON_CHESTPLATE))
                        .addItem(new ItemStack(Material.COOKED_BEEF, 20))
                        .addExperience(100)
                        .build())
                .sequential(false)  // 순서 상관없이 진행 가능
                .category(QuestCategory.TUTORIAL)
                .minLevel(1)
                .addPrerequisite(QuestID.TUTORIAL_FIRST_STEPS);  // 첫 걸음 퀘스트 완료 필요
    }
}