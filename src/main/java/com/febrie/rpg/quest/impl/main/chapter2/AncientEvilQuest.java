package com.febrie.rpg.quest.impl.main.chapter2;

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
 * Chapter 2: Ancient Evil Quest
 * Confront the awakening ancient evil that threatens the world
 *
 * @author Febrie
 */
public class AncientEvilQuest extends Quest {
    
    /**
     * Default constructor
     */
    public AncientEvilQuest() {
        super(createBuilder());
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_ANCIENT_EVIL)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("ancient_sage", "ancient_sage"),
                        new VisitLocationObjective("forbidden_temple", "forbidden_temple_area"),
                        new KillMobObjective("kill_wither_skeletons", EntityType.WITHER_SKELETON, 20),
                        new KillMobObjective("kill_blazes", EntityType.BLAZE, 15),
                        new CollectItemObjective("dark_crystal", Material.END_CRYSTAL, 4),
                        new VisitLocationObjective("evil_altar", "evil_altar_area"),
                        new CollectItemObjective("purified_soul", Material.SOUL_LANTERN, 6),
                        new KillMobObjective("kill_ravagers", EntityType.RAVAGER, 5),
                        new InteractNPCObjective("light_guardian", "light_guardian")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 4000)
                        .addItem(new ItemStack(Material.NETHERITE_HELMET))
                        .addItem(new ItemStack(Material.BEACON))
                        .addItem(new ItemStack(Material.NETHER_STAR, 2))
                        .addExperience(8000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(26)
                .addPrerequisite(QuestID.MAIN_LOST_KINGDOM);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.get("quest.main.ancient_evil.name", who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.main.ancient_evil.info", who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.main.ancient_evil.objectives." + objective.getId(), who);
    }
    
    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.main.ancient_evil.dialogs.0", who);
            case 1 -> LangManager.get("quest.main.ancient_evil.dialogs.1", who);
            case 2 -> LangManager.get("quest.main.ancient_evil.dialogs.2", who);
            case 3 -> LangManager.get("quest.main.ancient_evil.dialogs.3", who);
            case 4 -> LangManager.get("quest.main.ancient_evil.dialogs.4", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.main.ancient_evil.npc_name", who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.main.ancient_evil.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.main.ancient_evil.decline", who);
    }
}