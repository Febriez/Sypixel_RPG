package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.DeliverItemObjective;
import com.febrie.rpg.quest.objective.impl.HarvestObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

    /**
     * 퀘스트 빌더
     */
    private static class FarmersRequestBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new FarmersRequestQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public FarmersRequestQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private FarmersRequestQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        // 농작물 수확 목표
        List<QuestObjective> objectives = new ArrayList<>();
        objectives.add(new HarvestObjective("harvest_wheat", Material.WHEAT, 30));
        objectives.add(new HarvestObjective("harvest_carrots", Material.CARROTS, 20));
        objectives.add(new HarvestObjective("harvest_potatoes", Material.POTATOES, 20));

        // 농부에게 전달 목표
        Map<Material, Integer> deliveryItems = new HashMap<>();
        deliveryItems.put(Material.WHEAT, 30);
        deliveryItems.put(Material.CARROT, 20);
        deliveryItems.put(Material.POTATO, 20);

        objectives.add(new DeliverItemObjective(
                "deliver_to_farmer",
                "농부 김씨",
                deliveryItems
        ));

        return new FarmersRequestBuilder()
                .id(QuestID.SIDE_FARMERS_REQUEST)
                .objectives(objectives)
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 300)
                        .addCurrency(CurrencyType.EMERALD, 10)
                        .addItem(new ItemStack(Material.GOLDEN_HOE))
                        .addItem(new ItemStack(Material.BONE_MEAL, 64))
                        .addExperience(200)
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(3)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.side.farmers_request.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getComponentList(who, "quest.side.farmers_request.description");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.side.farmers_request.objectives." + id);
    }

    @Override
    public QuestDialog getDialog(@NotNull Player player) {
        QuestDialog dialog = new QuestDialog("farmers_request_dialog");

        dialog.addLine("quest.farmers_request.npcs.farmer", "quest.farmers_request.dialogs.line1");
        dialog.addLine("quest.farmers_request.npcs.farmer", "quest.farmers_request.dialogs.line2");
        dialog.addLine("quest.farmers_request.npcs.farmer", "quest.farmers_request.dialogs.line3");

        return dialog;
    }
}