package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;

import com.febrie.rpg.util.LangKey;
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
 * Side Quest: Merchant's Dilemma
 * Help a troubled merchant recover stolen goods and secure safe trade routes.
 *
 * @author Febrie
 */
public class MerchantsDilemmaQuest extends Quest {

    /**
     * 기본 생성자
     */
    public MerchantsDilemmaQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_MERCHANTS_DILEMMA)
                .objectives(List.of(
                        new InteractNPCObjective("talk_troubled_merchant", "troubled_merchant", 1),
                        new VisitLocationObjective("visit_caravan_route", "caravan_route"),
                        new KillMobObjective("kill_pillagers", EntityType.PILLAGER, 12),
                        new CollectItemObjective("collect_stolen_goods", Material.CHEST, 5),
                        new VisitLocationObjective("visit_bandits_hideout", "bandits_hideout"),
                        new CollectItemObjective("collect_trade_contract", Material.PAPER, 3),
                        new InteractNPCObjective("return_troubled_merchant", "troubled_merchant", 1)
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(2000)
                        .addCurrency(CurrencyType.GOLD, 600)
                        .addItem(new ItemStack(Material.EMERALD, 8))
                        .addItem(new ItemStack(Material.DIAMOND, 2))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(15);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_MERCHANTS_DILEMMA_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_MERCHANTS_DILEMMA_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_troubled_merchant" -> LangManager.list(LangKey.QUEST_SIDE_MERCHANTS_DILEMMA_OBJECTIVES_TALK_TROUBLED_MERCHANT, who);
            case "visit_caravan_route" -> LangManager.list(LangKey.QUEST_SIDE_MERCHANTS_DILEMMA_OBJECTIVES_VISIT_CARAVAN_ROUTE, who);
            case "kill_pillagers" -> LangManager.list(LangKey.QUEST_SIDE_MERCHANTS_DILEMMA_OBJECTIVES_KILL_PILLAGERS, who);
            case "collect_stolen_goods" -> LangManager.list(LangKey.QUEST_SIDE_MERCHANTS_DILEMMA_OBJECTIVES_COLLECT_STOLEN_GOODS, who);
            case "visit_bandits_hideout" -> LangManager.list(LangKey.QUEST_SIDE_MERCHANTS_DILEMMA_OBJECTIVES_VISIT_BANDITS_HIDEOUT, who);
            case "collect_trade_contract" -> LangManager.list(LangKey.QUEST_SIDE_MERCHANTS_DILEMMA_OBJECTIVES_COLLECT_TRADE_CONTRACT, who);
            case "return_troubled_merchant" -> LangManager.list(LangKey.QUEST_SIDE_MERCHANTS_DILEMMA_OBJECTIVES_RETURN_TROUBLED_MERCHANT, who);
            default -> List.of(Component.text("Unknown objective: " + objective.getId()));
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_MERCHANTS_DILEMMA_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_MERCHANTS_DILEMMA_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_MERCHANTS_DILEMMA_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_MERCHANTS_DILEMMA_DECLINE, who);
    }
}