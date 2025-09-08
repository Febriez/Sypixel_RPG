package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;

import com.febrie.rpg.util.LangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * 일일 채집 - 일일 퀘스트
 * 매일 수행할 수 있는 자원 수집 퀘스트
 *
 * @author Febrie
 */
public class DailyGatheringQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DailyGatheringQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder().id(QuestID.DAILY_GATHERING).objectives(List.of(
                        // 기본 자원 수집
                        new InteractNPCObjective("meet_foreman", "gathering_foreman", 1), // 채집 감독관
                        new CollectItemObjective("gather_wood", Material.OAK_LOG, 32), new CollectItemObjective("gather_stone", Material.COBBLESTONE, 64), new CollectItemObjective("gather_coal", Material.COAL, 16),

                        // 광물 채굴
                        new BreakBlockObjective("mine_iron", Material.IRON_ORE, 10), new BreakBlockObjective("mine_gold", Material.GOLD_ORE, 5), new CollectItemObjective("gather_iron", Material.IRON_INGOT, 10),

                        // 농업 활동
                        new HarvestObjective("harvest_crops", Material.WHEAT, 20), new CollectItemObjective("gather_wheat", Material.WHEAT, 20), new CollectItemObjective("gather_carrots", Material.CARROT, 15),

                        // 특수 자원
                        new CollectItemObjective("gather_flowers", Material.DANDELION, 5), new CollectItemObjective("gather_saplings", Material.OAK_SAPLING, 5),

                        // 납품
                        new DeliverItemObjective("deliver_resources", "gathering_supervisor", Material.CHEST, 1)))
                .reward(new BasicReward.Builder().addCurrency(CurrencyType.GOLD, 1500)
                        .addCurrency(CurrencyType.DIAMOND, 10).addItem(new ItemStack(Material.GOLDEN_APPLE, 2))
                        .addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 10)).addExperience(500).build())
                .sequential(false)  // 자유롭게 진행 가능
                .repeatable(true).daily(true)       // 일일 퀘스트
                .category(QuestCategory.DAILY).minLevel(5).maxLevel(0).addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_DAILY_GATHERING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_DAILY_GATHERING_INFO, who);
    }

        @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "meet_foreman" -> LangManager.list(LangKey.QUEST_DAILY_GATHERING_OBJECTIVES_MEET_FOREMAN, who);
            case "gather_wood" -> LangManager.list(LangKey.QUEST_DAILY_GATHERING_OBJECTIVES_GATHER_WOOD, who);
            case "gather_stone" -> LangManager.list(LangKey.QUEST_DAILY_GATHERING_OBJECTIVES_GATHER_STONE, who);
            case "gather_coal" -> LangManager.list(LangKey.QUEST_DAILY_GATHERING_OBJECTIVES_GATHER_COAL, who);
            case "mine_iron" -> LangManager.list(LangKey.QUEST_DAILY_GATHERING_OBJECTIVES_MINE_IRON, who);
            case "mine_gold" -> LangManager.list(LangKey.QUEST_DAILY_GATHERING_OBJECTIVES_MINE_GOLD, who);
            case "gather_iron" -> LangManager.list(LangKey.QUEST_DAILY_GATHERING_OBJECTIVES_GATHER_IRON, who);
            case "harvest_crops" -> LangManager.list(LangKey.QUEST_DAILY_GATHERING_OBJECTIVES_HARVEST_CROPS, who);
            case "gather_wheat" -> LangManager.list(LangKey.QUEST_DAILY_GATHERING_OBJECTIVES_GATHER_WHEAT, who);
            case "gather_carrots" -> LangManager.list(LangKey.QUEST_DAILY_GATHERING_OBJECTIVES_GATHER_CARROTS, who);
            case "gather_flowers" -> LangManager.list(LangKey.QUEST_DAILY_GATHERING_OBJECTIVES_GATHER_FLOWERS, who);
            case "gather_saplings" -> LangManager.list(LangKey.QUEST_DAILY_GATHERING_OBJECTIVES_GATHER_SAPLINGS, who);
            case "deliver_resources" -> LangManager.list(LangKey.QUEST_DAILY_GATHERING_OBJECTIVES_DELIVER_RESOURCES, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_DAILY_GATHERING_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_DAILY_GATHERING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_DAILY_GATHERING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_DAILY_GATHERING_DECLINE, who);
    }
}