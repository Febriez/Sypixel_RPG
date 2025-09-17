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
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
                        new InteractNPCObjective("meet_foreman", "gathering_foreman"), // 채집 감독관
                        new CollectItemObjective("oak_log_collect", Material.OAK_LOG, 32), new CollectItemObjective("cobblestone_collect", Material.COBBLESTONE, 64), new CollectItemObjective("coal_collect", Material.COAL, 16),

                        // 광물 채굴
                        new BreakBlockObjective("mine_iron", Material.IRON_ORE, 10), new BreakBlockObjective("mine_gold", Material.GOLD_ORE, 5), new CollectItemObjective("iron_ingot_collect", Material.IRON_INGOT, 10),

                        // 농업 활동
                        new HarvestObjective("harvest_crops", Material.WHEAT, 20), new CollectItemObjective("wheat_collect", Material.WHEAT, 20), new CollectItemObjective("carrot_collect", Material.CARROT, 15),

                        // 특수 자원
                        new CollectItemObjective("dandelion_collect", Material.DANDELION, 5), new CollectItemObjective("oak_sapling_collect", Material.OAK_SAPLING, 5),

                        // 납품
                        new DeliverItemObjective("chest_deliver", Material.CHEST, 1, "gathering_supervisor")))
                .reward(new BasicReward.Builder().addCurrency(CurrencyType.GOLD, 1500)
                        .addCurrency(CurrencyType.DIAMOND, 10).addItem(new ItemStack(Material.GOLDEN_APPLE, 2))
                        .addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 10)).addExperience(500).build())
                .sequential(false)  // 자유롭게 진행 가능
                .repeatable(true).daily(true)       // 일일 퀘스트
                .category(QuestCategory.DAILY).minLevel(5).maxLevel(0).addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_GATHERING_INFO, who);
    }

        @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "meet_foreman" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_OBJECTIVES_MEET_FOREMAN, who);
            case "oak_log_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_OBJECTIVES_OAK_LOG_COLLECT, who);
            case "cobblestone_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_OBJECTIVES_COBBLESTONE_COLLECT, who);
            case "coal_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_OBJECTIVES_COAL_COLLECT, who);
            case "mine_iron" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_OBJECTIVES_MINE_IRON, who);
            case "mine_gold" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_OBJECTIVES_MINE_GOLD, who);
            case "iron_ingot_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_OBJECTIVES_IRON_INGOT_COLLECT, who);
            case "harvest_crops" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_OBJECTIVES_HARVEST_CROPS, who);
            case "wheat_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_OBJECTIVES_WHEAT_COLLECT, who);
            case "carrot_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_OBJECTIVES_CARROT_COLLECT, who);
            case "dandelion_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_OBJECTIVES_DANDELION_COLLECT, who);
            case "oak_sapling_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_OBJECTIVES_OAK_SAPLING_COLLECT, who);
            case "chest_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_OBJECTIVES_CHEST_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_GATHERING_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_GATHERING_DECLINE, who);
    }
}