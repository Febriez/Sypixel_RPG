package com.febrie.rpg.quest.impl.main;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CraftItemObjective;
import com.febrie.rpg.quest.objective.impl.DeliverItemObjective;
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
 * 빛의 길 - 선택 퀘스트 (선)
 * 어둠의 길과 양자택일
 *
 * @author Febrie
 */
public class PathOfLightQuest extends Quest {

    private static final String QUEST_ID = "main_path_of_light";
    private static final String NAME_KEY = "quest.main.path_of_light.name";
    private static final String DESC_KEY = "quest.main.path_of_light.description";

    private final List<QuestObjective> objectives;

    public PathOfLightQuest() {
        super(QUEST_ID, NAME_KEY, DESC_KEY);
        this.objectives = createObjectives();

        // 어둠의 길과 양자택일
        addExclusiveQuest("main_path_of_darkness");

        // 영웅의 여정을 먼저 완료해야 함
        addPrerequisiteQuest("main_heroes_journey");
    }

    private List<QuestObjective> createObjectives() {
        List<QuestObjective> list = new ArrayList<>();

        // 1. 언데드 몬스터 정화
        list.add(new KillMobObjective("purify_undead_zombie", EntityType.ZOMBIE, 50));
        list.add(new KillMobObjective("purify_undead_skeleton", EntityType.SKELETON, 30));
        list.add(new KillMobObjective("purify_undead_phantom", EntityType.PHANTOM, 10));

        // 2. 성스러운 아이템 제작
        list.add(new CraftItemObjective("craft_golden_apple", Material.GOLDEN_APPLE, 5));

        // 3. 마을 사람들 도와주기 (빵 전달)
        list.add(new DeliverItemObjective(
                "help_villagers",
                "굶주린 주민",
                Material.BREAD,
                30
        ));

        return list;
    }

    @Override
    public @NotNull List<QuestObjective> getObjectives() {
        return new ArrayList<>(objectives);
    }

    @Override
    public boolean isSequential() {
        return false;
    }

    @Override
    public @NotNull QuestReward getReward() {
        // 빛의 보상 - 신성한 아이템들
        return BasicReward.builder()
                .addCurrency(CurrencyType.GOLD, 1000)
                .addCurrency(CurrencyType.DIAMOND, 20)
                .addItem(new ItemStack(Material.ELYTRA)) // 엘리트라 (천사의 날개)
                .addItem(new ItemStack(Material.TOTEM_OF_UNDYING)) // 불사의 토템
                .addItem(new ItemStack(Material.BEACON)) // 신호기
                .addExperience(2000)
                .setDescriptionKey("quest.main.path_of_light.reward.description")
                .build();
    }

    @Override
    public boolean canStart(@NotNull UUID playerId) {
        return true;
    }

    @Override
    public int getMinLevel() {
        return 20;
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }

    @Override
    public @NotNull QuestCategory getCategory() {
        return QuestCategory.MAIN;
    }
}