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
import com.febrie.rpg.util.lang.quest.side.FrozenPeaksLangKey;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

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
                .objectives(List.of(
                        new InteractNPCObjective("talk_mountain_climber", "mountain_climber"),
                        new VisitLocationObjective("visit_ice_cliffs", "ice_cliffs"),
                        new KillMobObjective("kill_polar_bears", EntityType.POLAR_BEAR, 10),
                        new CollectItemObjective("ice_collect", Material.ICE, 25),
                        new VisitLocationObjective("visit_frozen_summit", "frozen_summit"),
                        new CollectItemObjective("packed_ice_collect", Material.PACKED_ICE, 8)
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
        return LangManager.text(FrozenPeaksLangKey.QUEST_SIDE_FROZEN_PEAKS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(FrozenPeaksLangKey.QUEST_SIDE_FROZEN_PEAKS_INFO, who);
    }

        @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_mountain_climber" -> LangManager.text(FrozenPeaksLangKey.QUEST_SIDE_FROZEN_PEAKS_OBJECTIVES_TALK_MOUNTAIN_CLIMBER, who);
            case "visit_ice_cliffs" -> LangManager.text(FrozenPeaksLangKey.QUEST_SIDE_FROZEN_PEAKS_OBJECTIVES_VISIT_ICE_CLIFFS, who);
            case "kill_polar_bears" -> LangManager.text(FrozenPeaksLangKey.QUEST_SIDE_FROZEN_PEAKS_OBJECTIVES_KILL_POLAR_BEARS, who);
            case "ice_collect" -> LangManager.text(FrozenPeaksLangKey.QUEST_SIDE_FROZEN_PEAKS_OBJECTIVES_ICE_COLLECT, who);
            case "visit_frozen_summit" -> LangManager.text(FrozenPeaksLangKey.QUEST_SIDE_FROZEN_PEAKS_OBJECTIVES_VISIT_FROZEN_SUMMIT, who);
            case "packed_ice_collect" -> LangManager.text(FrozenPeaksLangKey.QUEST_SIDE_FROZEN_PEAKS_OBJECTIVES_PACKED_ICE_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(FrozenPeaksLangKey.QUEST_SIDE_FROZEN_PEAKS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(FrozenPeaksLangKey.QUEST_SIDE_FROZEN_PEAKS_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(FrozenPeaksLangKey.QUEST_SIDE_FROZEN_PEAKS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(FrozenPeaksLangKey.QUEST_SIDE_FROZEN_PEAKS_DECLINE, who);
    }
}