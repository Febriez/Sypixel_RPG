package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.objective.impl.BreakBlockObjective;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.reward.QuestReward;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 일일 채광 - 일일 퀘스트
 * 매일 리셋되는 채광 퀘스트
 *
 * @author Febrie
 */
public class DailyMiningQuest extends Quest {

    private static final String QUEST_ID = "daily_mining";
    private static final String NAME_KEY = "quest.daily.mining.name";
    private static final String DESC_KEY = "quest.daily.mining.description";

    private final List<QuestObjective> objectives;

    public DailyMiningQuest() {
        super(QUEST_ID, NAME_KEY, DESC_KEY);
        this.objectives = createObjectives();
    }

    private List<QuestObjective> createObjectives() {
        List<QuestObjective> list = new ArrayList<>();

        // 광석 채굴
        list.add(new BreakBlockObjective("mine_stone", Material.STONE, 50));
        list.add(new BreakBlockObjective("mine_coal", Material.COAL_ORE, 20));
        list.add(new BreakBlockObjective("mine_iron", Material.IRON_ORE, 10));

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
        return BasicReward.builder()
                .addCurrency(CurrencyType.GOLD, 150)
                .addItem(new ItemStack(Material.IRON_PICKAXE))
                .addItem(new ItemStack(Material.TORCH, 32))
                .addExperience(100)
                .build();
    }

    @Override
    public boolean canStart(@NotNull UUID playerId) {
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
        return QuestCategory.DAILY;
    }

    @Override
    public boolean isDaily() {
        return true;
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }
}