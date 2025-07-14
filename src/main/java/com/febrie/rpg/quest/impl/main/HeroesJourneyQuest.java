package com.febrie.rpg.quest.impl.main;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.CraftItemObjective;
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
 * 영웅의 여정 - 메인 퀘스트 1
 * 본격적인 모험의 시작
 *
 * @author Febrie
 */
public class HeroesJourneyQuest extends Quest {

    private static final String QUEST_ID = "main_heroes_journey";
    private static final String NAME_KEY = "quest.main.heroes_journey.name";
    private static final String DESC_KEY = "quest.main.heroes_journey.description";

    private final List<QuestObjective> objectives;

    public HeroesJourneyQuest() {
        super(QUEST_ID, NAME_KEY, DESC_KEY);
        this.objectives = createObjectives();
    }

    private List<QuestObjective> createObjectives() {
        List<QuestObjective> list = new ArrayList<>();

        // 1. 다양한 몬스터 처치
        list.add(new KillMobObjective("kill_zombies", EntityType.ZOMBIE, 10));
        list.add(new KillMobObjective("kill_skeletons", EntityType.SKELETON, 10));
        list.add(new KillMobObjective("kill_spiders", EntityType.SPIDER, 5));

        // 2. 자원 수집
        list.add(new CollectItemObjective("collect_iron", Material.IRON_INGOT, 20));
        list.add(new CollectItemObjective("collect_gold", Material.GOLD_INGOT, 10));

        // 3. 장비 제작
        list.add(new CraftItemObjective("craft_iron_sword", Material.IRON_SWORD, 1));
        list.add(new CraftItemObjective("craft_iron_armor", Material.IRON_CHESTPLATE, 1));

        return list;
    }

    @Override
    public @NotNull List<QuestObjective> getObjectives() {
        return new ArrayList<>(objectives);
    }

    @Override
    public boolean isSequential() {
        return false; // 자유롭게 진행 가능
    }

    @Override
    public @NotNull QuestReward getReward() {
        return BasicReward.builder()
                .addCurrency(CurrencyType.GOLD, 500)
                .addCurrency(CurrencyType.DIAMOND, 5)
                .addItem(new ItemStack(Material.DIAMOND))
                .addItem(new ItemStack(Material.ENCHANTING_TABLE))
                .addExperience(500)
                .build();
    }

    @Override
    public boolean canStart(@NotNull UUID playerId) {
        return true;
    }

    @Override
    public int getMinLevel() {
        return 5; // 최소 레벨 5
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }

    @Override
    public @NotNull List<String> getPrerequisiteQuests() {
        // 튜토리얼 퀘스트 완료 필요
        return List.of("tutorial_basic_combat");
    }

    @Override
    public @NotNull QuestCategory getCategory() {
        return QuestCategory.MAIN;
    }
}