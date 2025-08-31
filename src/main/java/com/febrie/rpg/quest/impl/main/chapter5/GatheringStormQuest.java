package com.febrie.rpg.quest.impl.main.chapter5;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangHelper;
import com.febrie.rpg.util.LangKey;
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
 * Chapter 5: Gathering Storm Quest
 * Storm clouds gather as the final confrontation approaches
 *
 * @author Febrie
 */
public class GatheringStormQuest extends Quest {
    
    /**
     * Default constructor
     */
    public GatheringStormQuest() {
        super(createBuilder());
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_GATHERING_STORM)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("storm_warden", "storm_warden"),
                        new VisitLocationObjective("storm_peaks", "storm_peaks_area"),
                        new CollectItemObjective("storm_crystal", Material.AMETHYST_CLUSTER, 8),
                        new KillMobObjective("kill_phantoms", EntityType.PHANTOM, 25),
                        new CollectItemObjective("lightning_rod", Material.LIGHTNING_ROD, 5),
                        new KillMobObjective("kill_charged_creepers", EntityType.CREEPER, 10),
                        new VisitLocationObjective("thunder_spire", "thunder_spire_area"),
                        new InteractNPCObjective("storm_keeper", "storm_keeper")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 6000)
                        .addItem(new ItemStack(Material.NETHERITE_CHESTPLATE))
                        .addItem(new ItemStack(Material.TRIDENT))
                        .addItem(new ItemStack(Material.CONDUIT, 2))
                        .addExperience(12000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(40)
                .addPrerequisite(QuestID.MAIN_DIMENSIONAL_RIFT);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_GATHERING_STORM_NAME, who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_GATHERING_STORM_INFO, who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.main.gathering_storm.objectives." + objective.getId(), who);
    }
    
    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangHelper.text(LangKey.QUEST_MAIN_GATHERING_STORM_DIALOGS_0, who);
            case 1 -> LangHelper.text(LangKey.QUEST_MAIN_GATHERING_STORM_DIALOGS_1, who);
            case 2 -> LangHelper.text(LangKey.QUEST_MAIN_GATHERING_STORM_DIALOGS_2, who);
            case 3 -> LangHelper.text(LangKey.QUEST_MAIN_GATHERING_STORM_DIALOGS_3, who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_GATHERING_STORM_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_GATHERING_STORM_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_GATHERING_STORM_DECLINE, who);
    }
}