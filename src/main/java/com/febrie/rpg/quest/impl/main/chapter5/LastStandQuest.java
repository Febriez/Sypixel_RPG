package com.febrie.rpg.quest.impl.main.chapter5;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
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
 * Chapter 5: Last Stand Quest
 * Make your final preparations for the ultimate battle
 *
 * @author Febrie
 */
public class LastStandQuest extends Quest {
    
    /**
     * Default constructor
     */
    public LastStandQuest() {
        super(createBuilder());
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_LAST_STAND)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("alliance_commander", "alliance_commander"),
                        new VisitLocationObjective("fortress_battlements", "fortress_battlements_area"),
                        new CollectItemObjective("siege_weapon", Material.CROSSBOW, 10),
                        new KillMobObjective("kill_pillagers", EntityType.PILLAGER, 30),
                        new CollectItemObjective("blessed_arrows", Material.SPECTRAL_ARROW, 100),
                        new KillMobObjective("kill_ravagers", EntityType.RAVAGER, 8),
                        new CollectItemObjective("barrier_crystal", Material.END_CRYSTAL, 5),
                        new VisitLocationObjective("last_fortress", "last_fortress_area"),
                        new InteractNPCObjective("war_strategist", "war_strategist")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 7500)
                        .addItem(new ItemStack(Material.NETHERITE_LEGGINGS))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 3))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 5))
                        .addExperience(15000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(42)
                .addPrerequisite(QuestID.MAIN_GATHERING_STORM);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.get("quest.main.last_stand.name", who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.main.last_stand.info", who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.main.last_stand.objectives." + objective.getId(), who);
    }
    
    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.main.last_stand.dialogs.0", who);
            case 1 -> LangManager.get("quest.main.last_stand.dialogs.1", who);
            case 2 -> LangManager.get("quest.main.last_stand.dialogs.2", who);
            case 3 -> LangManager.get("quest.main.last_stand.dialogs.3", who);
            case 4 -> LangManager.get("quest.main.last_stand.dialogs.4", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.main.last_stand.npc_name", who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.main.last_stand.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.main.last_stand.decline", who);
    }
}