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
 * Side Quest: Frozen Peaks
 * Brave the treacherous frozen peaks to help a climber reach the summit
 *
 * @author Febrie
 */
public class FrozenPeaksQuest extends Quest {

    /**
     * Default constructor
     */
    public FrozenPeaksQuest() {
        super(createBuilder());
    }

    /**
     * Quest setup
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_FROZEN_PEAKS)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_mountain_climber", "mountain_climber"),
                        new VisitLocationObjective("visit_ice_cliffs", "ice_cliffs"),
                        new KillMobObjective("kill_polar_bears", EntityType.POLAR_BEAR, 10),
                        new CollectItemObjective("collect_ice_shards", Material.ICE, 25),
                        new VisitLocationObjective("visit_frozen_summit", "frozen_summit"),
                        new CollectItemObjective("collect_eternal_ice", Material.PACKED_ICE, 8)
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(4500)
                        .addCurrency(CurrencyType.GOLD, 1200)
                        .addItem(new ItemStack(Material.DIAMOND_BOOTS, 1))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(28);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.get("quest.side.frozen_peaks.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.side.frozen_peaks.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.side.frozen_peaks.objectives." + objective.getId(), who);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.side.frozen_peaks.dialogs.0", who);
            case 1 -> LangManager.get("quest.side.frozen_peaks.dialogs.1", who);
            case 2 -> LangManager.get("quest.side.frozen_peaks.dialogs.2", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.side.frozen_peaks.npc_name", who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.side.frozen_peaks.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.side.frozen_peaks.decline", who);
    }
}