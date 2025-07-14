package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.DeliverItemObjective;
import com.febrie.rpg.quest.objective.impl.HarvestObjective;
import com.febrie.rpg.quest.reward.QuestReward;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 농부의 부탁 - 사이드 퀘스트
 * 농부를 도와 농작물을 수확하고 전달
 *
 * @author Febrie
 */
public class FarmersRequestQuest extends Quest {

    private static final String QUEST_ID = "side_farmers_request";
    private static final String NAME_KEY = "quest.side.farmers_request.name";
    private static final String DESC_KEY = "quest.side.farmers_request.description";

    private final List<QuestObjective> objectives;

    public FarmersRequestQuest() {
        super(QUEST_ID, NAME_KEY, DESC_KEY);
        this.objectives = createObjectives();
    }

    private List<QuestObjective> createObjectives() {
        List<QuestObjective> list = new ArrayList<>();

        // 1. 농작물 수확
        list.add(new HarvestObjective("harvest_wheat", Material.WHEAT, 30));
        list.add(new HarvestObjective("harvest_carrots", Material.CARROTS, 20));
        list.add(new HarvestObjective("harvest_potatoes", Material.POTATOES, 20));

        // 2. 농부에게 전달
        Map<Material, Integer> deliveryItems = new HashMap<>();
        deliveryItems.put(Material.WHEAT, 30);
        deliveryItems.put(Material.CARROT, 20);
        deliveryItems.put(Material.POTATO, 20);

        list.add(new DeliverItemObjective(
                "deliver_to_farmer",
                "농부 김씨",
                deliveryItems
        ));

        return list;
    }

    @Override
    public @NotNull List<QuestObjective> getObjectives() {
        return new ArrayList<>(objectives);
    }

    @Override
    public boolean isSequential() {
        return true; // 수확 후 전달
    }

    @Override
    public @NotNull QuestReward getReward() {
        return BasicReward.builder()
                .addCurrency(CurrencyType.GOLD, 300)
                .addCurrency(CurrencyType.EMERALD, 10)
                .addItem(new ItemStack(Material.GOLDEN_HOE))
                .addItem(new ItemStack(Material.BONE_MEAL, 64))
                .addExperience(200)
                .build();
    }

    @Override
    public boolean canStart(@NotNull UUID playerId) {
        return true;
    }

    @Override
    public int getMinLevel() {
        return 3;
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }

    @Override
    public @NotNull QuestCategory getCategory() {
        return QuestCategory.SIDE;
    }
}