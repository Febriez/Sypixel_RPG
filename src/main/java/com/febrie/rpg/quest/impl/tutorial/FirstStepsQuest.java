package com.febrie.rpg.quest.impl.tutorial;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.reward.QuestReward;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 첫 걸음 - 튜토리얼 퀘스트 1
 * 기본적인 이동과 상호작용을 배우는 퀘스트
 *
 * @author Febrie
 */
public class FirstStepsQuest extends Quest {

    private static final String QUEST_ID = "tutorial_first_steps";
    private static final String NAME_KEY = "quest.tutorial.first_steps.name";
    private static final String DESC_KEY = "quest.tutorial.first_steps.description";

    private final List<QuestObjective> objectives;

    public FirstStepsQuest() {
        super(QUEST_ID, NAME_KEY, DESC_KEY);
        this.objectives = createObjectives();
    }

    private List<QuestObjective> createObjectives() {
        List<QuestObjective> list = new ArrayList<>();

        // 1. 스폰 지점 방문
        Location spawnLocation = Bukkit.getWorlds().getFirst().getSpawnLocation();
        list.add(new VisitLocationObjective(
                "visit_spawn",
                spawnLocation,
                10.0,
                "스폰 지점"
        ));

        // 2. 마을 상인 NPC 방문
        list.add(new InteractNPCObjective(
                "visit_merchant",
                "마을 상인"
        ));

        return list;
    }

    @Override
    public @NotNull List<QuestObjective> getObjectives() {
        return new ArrayList<>(objectives);
    }

    @Override
    public boolean isSequential() {
        return true; // 순차적으로 진행
    }

    @Override
    public @NotNull QuestReward getReward() {
        // 보상: 100 골드, 나무 도구 세트
        return BasicReward.builder()
                .addCurrency(CurrencyType.GOLD, 100)
                .addItem(new ItemStack(Material.WOODEN_SWORD))
                .addItem(new ItemStack(Material.WOODEN_PICKAXE))
                .addItem(new ItemStack(Material.WOODEN_AXE))
                .addItem(new ItemStack(Material.BREAD, 10))
                .addExperience(50)
                .build();
    }

    @Override
    public boolean canStart(@NotNull UUID playerId) {
        // 모든 플레이어가 시작 가능
        return true;
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 0; // 레벨 제한 없음
    }

    @Override
    public @NotNull QuestCategory getCategory() {
        return QuestCategory.TUTORIAL;
    }
}