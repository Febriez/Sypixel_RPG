package com.febrie.rpg.quest.impl.tutorial;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.QuestReward;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 기초 전투 - 튜토리얼 퀘스트 2
 * 전투의 기본을 배우는 퀘스트
 *
 * @author Febrie
 */
public class BasicCombatQuest extends Quest {

    private static final String QUEST_ID = "tutorial_basic_combat";
    private static final String NAME_KEY = "quest.tutorial.basic_combat.name";
    private static final String DESC_KEY = "quest.tutorial.basic_combat.description";

    private final List<QuestObjective> objectives;

    public BasicCombatQuest() {
        super(QUEST_ID, NAME_KEY, DESC_KEY);
        this.objectives = createObjectives();

        // 선행 퀘스트 설정 - FirstStepsQuest를 먼저 완료해야 함
        addPrerequisiteQuest("tutorial_first_steps");
    }

    private List<QuestObjective> createObjectives() {
        List<QuestObjective> list = new ArrayList<>();

        // 1. 좀비 5마리 처치
        list.add(new KillMobObjective(
                "kill_zombies",
                EntityType.ZOMBIE,
                5
        ));

        // 2. 스켈레톤 3마리 처치
        list.add(new KillMobObjective(
                "kill_skeletons",
                EntityType.SKELETON,
                3
        ));

        return list;
    }

    @Override
    public @NotNull List<QuestObjective> getObjectives() {
        return new ArrayList<>(objectives);
    }

    @Override
    public boolean isSequential() {
        return false; // 순서 상관없이 진행 가능
    }

    @Override
    public @NotNull QuestReward getReward() {
        // 보상: 200 골드, 철 장비
        return BasicReward.builder()
                .addCurrency(CurrencyType.GOLD, 200)
                .addItem(new ItemStack(Material.IRON_SWORD))
                .addItem(new ItemStack(Material.IRON_CHESTPLATE))
                .addItem(new ItemStack(Material.COOKED_BEEF, 20))
                .addExperience(100)
                .build();
    }

    @Override
    public boolean canStart(@NotNull UUID playerId) {
        // 추가 조건이 필요하면 여기서 확인
        return true;
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }

    @Override
    public @NotNull QuestCategory getCategory() {
        return QuestCategory.TUTORIAL;
    }
}