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
     * 퀘스트 빌더
     */
    private static class MerchantsDilemmaBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new MerchantsDilemmaQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public MerchantsDilemmaQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private MerchantsDilemmaQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new MerchantsDilemmaBuilder()
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
        return Component.translatable("quest.side.merchants-dilemma.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Component line = Component.translatable("quest.side.merchants-dilemma.description." + i);
            description.add(line);
        }
        return description;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "talk_troubled_merchant" -> Component.translatable("quest.side.merchants-dilemma.objectives.talk_troubled_merchant");
            case "visit_caravan_route" -> Component.translatable("quest.side.merchants-dilemma.objectives.visit_caravan_route");
            case "kill_pillagers" -> Component.translatable("quest.side.merchants-dilemma.objectives.kill_pillagers");
            case "collect_stolen_goods" -> Component.translatable("quest.side.merchants-dilemma.objectives.collect_stolen_goods");
            case "visit_bandits_hideout" -> Component.translatable("quest.side.merchants-dilemma.objectives.visit_bandits_hideout");
            case "collect_trade_contract" -> Component.translatable("quest.side.merchants-dilemma.objectives.collect_trade_contract");
            case "return_troubled_merchant" -> Component.translatable("quest.side.merchants-dilemma.objectives.return_troubled_merchant");
            default -> Component.translatable("quest.side.merchants-dilemma.objectives." + id);
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.merchants-dilemma.dialogs.0");
            case 1 -> Component.translatable("quest.side.merchants-dilemma.dialogs.1");
            case 2 -> Component.translatable("quest.side.merchants-dilemma.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.merchants-dilemma.npc-name");
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.merchants-dilemma.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.merchants-dilemma.decline");
    }
}