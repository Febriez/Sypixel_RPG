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
import com.febrie.rpg.util.LangHelper;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

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
        return new QuestBuilder().id(QuestID.DAILY_GATHERING).objectives(Arrays.asList(
                        // 기본 자원 수집
                        new InteractNPCObjective("meet_foreman", "gathering_foreman"), // 채집 감독관
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
        return LangHelper.text(LangKey.QUEST_DAILY_GATHERING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangHelper.list(LangKey.QUEST_DAILY_GATHERING_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.daily.gathering.objectives." + objective.getId();
        return LangManager.get(key, who);
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangHelper.text(LangKey.QUEST_DAILY_GATHERING_DIALOGS_0, who);
            case 1 -> LangHelper.text(LangKey.QUEST_DAILY_GATHERING_DIALOGS_1, who);
            case 2 -> LangHelper.text(LangKey.QUEST_DAILY_GATHERING_DIALOGS_2, who);
            case 3 -> LangHelper.text(LangKey.QUEST_DAILY_GATHERING_DIALOGS_3, who);
            case 4 -> LangHelper.text(LangKey.QUEST_DAILY_GATHERING_DIALOGS_4, who);
            case 5 -> LangHelper.text(LangKey.QUEST_DAILY_GATHERING_DIALOGS_5, who);
            case 6 -> LangHelper.text(LangKey.QUEST_DAILY_GATHERING_DIALOGS_6, who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_DAILY_GATHERING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_DAILY_GATHERING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_DAILY_GATHERING_DECLINE, who);
    }
}