package com.febrie.rpg.quest.impl.main.chapter1;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
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

    public AncientProphecyQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
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
        return Arrays.asList(
                Component.translatable("quest.main.ancient-prophecy.description.0"),
                Component.translatable("quest.main.ancient-prophecy.description.1"),
                Component.translatable("quest.main.ancient-prophecy.description.2")
        );
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return Component.translatable("quest.main.ancient-prophecy.objectives." + id);
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.main.ancient-prophecy.dialogs.0");
            case 1 -> Component.translatable("quest.main.ancient-prophecy.dialogs.1");
            case 2 -> Component.translatable("quest.main.ancient-prophecy.dialogs.2");
            case 3 -> Component.translatable("quest.main.ancient-prophecy.dialogs.3");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.main.ancient-prophecy.npc-name");
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.main.ancient-prophecy.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.main.ancient-prophecy.decline");
    }
}