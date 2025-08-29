package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Side Quest: Collect Herbs
 * Gather medicinal herbs for the village healer
 *
 * @author Febrie
 */
public class CollectHerbsQuest extends Quest {

    /**
     * 기본 생성자
     */
    public CollectHerbsQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_COLLECT_HERBS)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_village_healer", "village_healer"),
                        new VisitLocationObjective("herb_meadow", "Herb_Meadow"),
                        new CollectItemObjective("healing_herbs", Material.SWEET_BERRIES, 20),
                        new CollectItemObjective("rare_flowers", Material.POPPY, 10),
                        new VisitLocationObjective("mountain_herbs", "Mountain_Herbs"),
                        new CollectItemObjective("mountain_sage", Material.FERN, 8),
                        new InteractNPCObjective("return_village_healer", "village_healer")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(600)
                        .addCurrency(CurrencyType.GOLD, 120)
                        .addItem(new ItemStack(Material.POTION, 3))
                        .addItem(new ItemStack(Material.HONEY_BOTTLE, 5))
                        .build())
                .sequential(false)
                .category(QuestCategory.SIDE)
                .minLevel(5);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.get("quest.side.collect_herbs.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.side.collect_herbs.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.side.collect_herbs.objectives." + objective.getId(), who);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.side.collect_herbs.dialogs.0", who);
            case 1 -> LangManager.get("quest.side.collect_herbs.dialogs.1", who);
            case 2 -> LangManager.get("quest.side.collect_herbs.dialogs.2", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.side.collect_herbs.npc_name", who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.side.collect_herbs.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.side.collect_herbs.decline", who);
    }
}