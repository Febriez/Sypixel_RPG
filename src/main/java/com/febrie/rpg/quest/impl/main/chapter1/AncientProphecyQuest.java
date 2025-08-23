package com.febrie.rpg.quest.impl.main.chapter1;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 고대의 예언 - 메인 퀘스트 Chapter 1
 * 운명의 시작
 */
public class AncientProphecyQuest extends Quest {

    private static class AncientProphecyBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new AncientProphecyQuest(this);
        }
    }

    public AncientProphecyQuest() {
        this(createBuilder());
    }

    private AncientProphecyQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    private static QuestBuilder createBuilder() {
        return new AncientProphecyBuilder()
                .id(QuestID.MAIN_ANCIENT_PROPHECY)
                .objectives(Arrays.asList(
                        new VisitLocationObjective("visit_elder", "ancient_temple"),
                        new InteractNPCObjective("talk_elder", "ancient_elder"), // 고대의 장로
                        new CollectItemObjective("collect_scrolls", Material.PAPER, 5),
                        new DeliverItemObjective("deliver_scrolls", "고대의 장로", Material.PAPER, 5)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 500)
                        .addCurrency(CurrencyType.DIAMOND, 10)
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK))
                        .addExperience(1000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(10)
                .maxLevel(0)
                .addPrerequisite(QuestID.MAIN_HEROES_JOURNEY);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.main.ancient_prophecy.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return List.of() /* TODO: Convert LangManager.getList("quest.main.ancient_prophecy.info") manually */;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return Component.translatable("quest.main.ancient_prophecy.objectives.");
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("ancient_prophecy_dialog");

        dialog.addLine("quest.ancient_prophecy.npcs.elder", "quest.ancient_prophecy.dialogs.line1");
        dialog.addLine("quest.ancient_prophecy.npcs.elder", "quest.ancient_prophecy.dialogs.line2");
        dialog.addLine("quest.dialog.player", "quest.ancient_prophecy.dialogs.player_line1");
        dialog.addLine("quest.ancient_prophecy.npcs.elder", "quest.ancient_prophecy.dialogs.line3");

        return dialog;
    }
}