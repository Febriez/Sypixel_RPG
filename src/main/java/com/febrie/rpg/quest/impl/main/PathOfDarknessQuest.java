package com.febrie.rpg.quest.impl.main;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CraftItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.KillPlayerObjective;
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
 * 어둠의 길 - 선택 퀘스트 (악)
 * 빛의 길과 양자택일
 *
 * @author Febrie
 */
public class PathOfDarknessQuest extends Quest {

    private static final String QUEST_ID = "main_path_of_darkness";
    private static final String NAME_KEY = "quest.main.path_of_darkness.name";
    private static final String DESC_KEY = "quest.main.path_of_darkness.description";

    private final List<QuestObjective> objectives;

    public PathOfDarknessQuest() {
        super(QUEST_ID, NAME_KEY, DESC_KEY);
        this.objectives = createObjectives();

        // 빛의 길과 양자택일
        addExclusiveQuest("main_path_of_light");

        // 영웅의 여정을 먼저 완료해야 함
        addPrerequisiteQuest("main_heroes_journey");
    }

    private List<QuestObjective> createObjectives() {
        List<QuestObjective> list = new ArrayList<>();

        // 1. 마을 주민 처치 (어둠의 길...)
        list.add(new KillMobObjective("eliminate_villagers", EntityType.VILLAGER, 10));

        // 2. 플레이어 처치 (PvP)
        list.add(new KillPlayerObjective("dominate_players", 5));

        // 3. 위더 스켈레톤 처치하여 재료 수집
        list.add(new KillMobObjective("hunt_wither_skeletons", EntityType.WITHER_SKELETON, 20));

        // 4. 어둠의 아이템 제작
        list.add(new CraftItemObjective("craft_tnt", Material.TNT, 10));

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
        // 어둠의 보상 - 강력하지만 사악한 아이템들
        return BasicReward.builder()
                .addCurrency(CurrencyType.GOLD, 1500) // 더 많은 골드
                .addCurrency(CurrencyType.GHAST_TEAR, 50) // 특별 재화
                .addItem(new ItemStack(Material.WITHER_SKELETON_SKULL, 3)) // 위더 소환 재료
                .addItem(new ItemStack(Material.NETHERITE_SWORD)) // 네더라이트 검
                .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 5)) // 인챈트된 황금사과
                .addExperience(2500)
                .setDescriptionKey("quest.main.path_of_darkness.reward.description")
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