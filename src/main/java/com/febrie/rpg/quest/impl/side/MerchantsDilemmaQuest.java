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
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_troubled_merchant", "troubled_merchant"),
                        new VisitLocationObjective("visit_caravan_route", "caravan_route"),
                        new KillMobObjective("kill_pillagers", EntityType.PILLAGER, 12),
                        new CollectItemObjective("collect_stolen_goods", Material.CHEST, 5),
                        new VisitLocationObjective("visit_bandits_hideout", "bandits_hideout"),
                        new CollectItemObjective("collect_trade_contract", Material.PAPER, 3),
                        new InteractNPCObjective("return_troubled_merchant", "troubled_merchant")
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
        return LangManager.get("quest.side.merchants_dilemma.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.side.merchants_dilemma.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.side.merchants_dilemma.objectives." + objective.getId(), who);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.side.merchants_dilemma.dialogs.0", who);
            case 1 -> LangManager.get("quest.side.merchants_dilemma.dialogs.1", who);
            case 2 -> LangManager.get("quest.side.merchants_dilemma.dialogs.2", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.side.merchants_dilemma.npc_name", who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.side.merchants_dilemma.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.side.merchants_dilemma.decline", who);
    }
}