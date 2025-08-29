package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
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
 * Side Quest: Fisherman's Tale
 * Listen to an old fisherman's tale and help him catch the legendary fish of the deep
 *
 * @author Febrie
 */
public class FishermanTaleQuest extends Quest {

    /**
     * Default constructor
     */
    public FishermanTaleQuest() {
        super(createBuilder());
    }

    /**
     * Quest setup
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_FISHERMAN_TALE)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_old_fisherman", "old_fisherman"),
                        new VisitLocationObjective("visit_fishing_dock", "fishing_dock"),
                        new CollectItemObjective("collect_rare_fish", Material.SALMON, 15),
                        new KillMobObjective("kill_drowned", EntityType.DROWNED, 10),
                        new VisitLocationObjective("visit_deep_waters", "deep_waters"),
                        new CollectItemObjective("collect_sea_treasure", Material.PRISMARINE_SHARD, 8),
                        new InteractNPCObjective("return_old_fisherman", "old_fisherman")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1800)
                        .addCurrency(CurrencyType.GOLD, 500)
                        .addItem(new ItemStack(Material.FISHING_ROD, 1))
                        .addItem(new ItemStack(Material.COOKED_SALMON, 10))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(14);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.get("quest.side.fisherman_tale.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.side.fisherman_tale.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.side.fisherman_tale.objectives." + objective.getId(), who);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.side.fisherman_tale.dialogs.0", who);
            case 1 -> LangManager.get("quest.side.fisherman_tale.dialogs.1", who);
            case 2 -> LangManager.get("quest.side.fisherman_tale.dialogs.2", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.side.fisherman_tale.npc_name", who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.side.fisherman_tale.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.side.fisherman_tale.decline", who);
    }
}