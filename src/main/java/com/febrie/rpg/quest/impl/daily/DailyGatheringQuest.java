package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
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
     * 퀘스트 빌더
     */
    private static class DailyGatheringBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new DailyGatheringQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DailyGatheringQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private DailyGatheringQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new DailyGatheringBuilder()
                .id(QuestID.DAILY_GATHERING)
                .objectives(Arrays.asList(
                        // 기본 자원 수집
                        new InteractNPCObjective("meet_foreman", "gathering_foreman"), // 채집 감독관
                        new CollectItemObjective("gather_wood", Material.OAK_LOG, 32),
                        new CollectItemObjective("gather_stone", Material.COBBLESTONE, 64),
                        new CollectItemObjective("gather_coal", Material.COAL, 16),
                        
                        // 광물 채굴
                        new BreakBlockObjective("mine_iron", Material.IRON_ORE, 10),
                        new BreakBlockObjective("mine_gold", Material.GOLD_ORE, 5),
                        new CollectItemObjective("gather_iron", Material.IRON_INGOT, 10),
                        
                        // 농업 활동
                        new HarvestObjective("harvest_crops", Material.WHEAT, 20),
                        new CollectItemObjective("gather_wheat", Material.WHEAT, 20),
                        new CollectItemObjective("gather_carrots", Material.CARROT, 15),
                        
                        // 특수 자원
                        new CollectItemObjective("gather_flowers", Material.DANDELION, 5),
                        new CollectItemObjective("gather_saplings", Material.OAK_SAPLING, 5),
                        
                        // 납품
                        new DeliverItemObjective("deliver_resources", "gathering_supervisor", Material.CHEST, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1500)
                        .addCurrency(CurrencyType.DIAMOND, 10)
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 2))
                        .addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 10))
                        .addExperience(500)
                        .build())
                .sequential(false)  // 자유롭게 진행 가능
                .repeatable(true)
                .daily(true)       // 일일 퀘스트
                .category(QuestCategory.DAILY)
                .minLevel(5)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.daily.gathering.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getList(who, "quest.daily.gathering.description");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.daily.gathering.objectives." + objective.getId();
        return com.febrie.rpg.util.LangManager.getMessage(who, key);
    }

    @Override
    public QuestDialog getDialog(@NotNull Player player) {
        QuestDialog dialog = new QuestDialog("daily_gathering_dialog");
        
        // 시작 대화
        dialog.addLine("quest.daily.gathering.npcs.foreman", "quest.daily.gathering.dialogs.start1");
        dialog.addLine("quest.daily.gathering.npcs.foreman", "quest.daily.gathering.dialogs.start2");
        dialog.addLine("quest.dialog.player", "quest.daily.gathering.dialogs.player_question");
        dialog.addLine("quest.daily.gathering.npcs.foreman", "quest.daily.gathering.dialogs.list");
        dialog.addLine("quest.daily.gathering.npcs.foreman", "quest.daily.gathering.dialogs.crops");
        dialog.addLine("quest.dialog.player", "quest.daily.gathering.dialogs.player_accept");
        dialog.addLine("quest.daily.gathering.npcs.foreman", "quest.daily.gathering.dialogs.thanks");
        
        return dialog;
    }
}