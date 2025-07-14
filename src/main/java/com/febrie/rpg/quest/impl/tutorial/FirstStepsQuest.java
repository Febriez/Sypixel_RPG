package com.febrie.rpg.quest.impl.tutorial;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * 첫 걸음 - 튜토리얼 퀘스트 1
 * 기본적인 이동과 상호작용을 배우는 퀘스트
 *
 * @author Febrie
 */
public class FirstStepsQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class FirstStepsBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new FirstStepsQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public FirstStepsQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private FirstStepsQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static Builder createBuilder() {
        // 스폰 지점
        Location spawnLocation = Bukkit.getWorlds().getFirst().getSpawnLocation();

        return new FirstStepsBuilder()
                .id(QuestID.TUTORIAL_FIRST_STEPS)
                .objectives(Arrays.asList(
                        // 1. 스폰 지점 방문
                        new VisitLocationObjective("visit_spawn", spawnLocation, 10.0, "스폰 지점"),
                        // 2. 마을 상인 NPC 방문
                        new InteractNPCObjective("visit_merchant", "마을 상인")
                ))
                .reward(BasicReward.builder()
                        .addCurrency(CurrencyType.GOLD, 100)
                        .addItem(new ItemStack(Material.WOODEN_SWORD))
                        .addItem(new ItemStack(Material.WOODEN_PICKAXE))
                        .addItem(new ItemStack(Material.WOODEN_AXE))
                        .addItem(new ItemStack(Material.BREAD, 10))
                        .addExperience(50)
                        .build())
                .sequential(true)  // 순차적으로 진행
                .category(QuestCategory.TUTORIAL)
                .minLevel(1);
    }
}