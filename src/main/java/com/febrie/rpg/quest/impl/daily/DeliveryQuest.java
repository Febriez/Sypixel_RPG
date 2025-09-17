package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.ArrayList;

/**
 * 일일 배송 - 일일 퀘스트
 * 매일 물품을 배송하고 전달하는 퀘스트
 *
 * @author Febrie
 */
public class DeliveryQuest extends Quest {

    /**
     * 기본 생성자
     */
    public DeliveryQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        List<QuestObjective> objectives = new ArrayList<>();
        
        // 배송 목표
        objectives.add(new InteractNPCObjective("postmaster", "delivery_chief"));
        objectives.add(new CollectItemObjective("chest_collect", Material.CHEST, 4));
        objectives.add(new VisitLocationObjective("visit_locations", "delivery_point_1"));
        objectives.add(new DeliverItemObjective("chest_deliver", Material.CHEST, 3, "recipient"));

        return new QuestBuilder()
                .id(QuestID.DAILY_DELIVERY)
                .objectives(objectives)
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 120)
                        .addCurrency(CurrencyType.EMERALD, 3)
                        .addItem(new ItemStack(Material.MINECART))
                        .addItem(new ItemStack(Material.BREAD, 8))
                        .addExperience(70)
                        .build())
                .sequential(true)
                .category(QuestCategory.DAILY)
                .minLevel(2)
                .repeatable(true)
                .daily(true)
                .addPrerequisite(QuestID.TUTORIAL_FIRST_STEPS);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_DELIVERY_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "postmaster" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_POSTMASTER, who);
            case "chest_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_CHEST_COLLECT, who);
            case "visit_locations" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_VISIT_LOCATIONS, who);
            case "chest_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_OBJECTIVES_CHEST_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_DELIVERY_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_DELIVERY_DECLINE, who);
    }
}