package com.febrie.rpg.quest.impl.side;

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
 * Side Quest: Ancient Ruins
 * Explore mysterious ancient ruins and uncover their secrets
 *
 * @author Febrie
 */
public class AncientRuinsQuest extends Quest {
    
    /**
     * Default constructor
     */
    public AncientRuinsQuest() {
        super(createBuilder());
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_ANCIENT_RUINS)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("archaeologist", "archaeologist"),
                        new VisitLocationObjective("ruined_entrance", "ruined_entrance_area"),
                        new KillMobObjective("kill_spiders", EntityType.SPIDER, 20),
                        new CollectItemObjective("ancient_stone", Material.STONE_BRICKS, 15),
                        new VisitLocationObjective("inner_chamber", "inner_chamber_area"),
                        new CollectItemObjective("runic_tablet", Material.STONE, 3),
                        new KillMobObjective("kill_silverfish", EntityType.SILVERFISH, 25),
                        new InteractNPCObjective("archaeologist_complete", "archaeologist")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 500)
                        .addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 10))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK))
                        .addExperience(2000)
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(15);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.get("quest.side.ancient_ruins.name", who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.side.ancient_ruins.info", who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.side.ancient_ruins.objectives." + objective.getId(), who);
    }
    
    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.side.ancient_ruins.dialogs.0", who);
            case 1 -> LangManager.get("quest.side.ancient_ruins.dialogs.1", who);
            case 2 -> LangManager.get("quest.side.ancient_ruins.dialogs.2", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.side.ancient_ruins.npc_name", who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.side.ancient_ruins.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.side.ancient_ruins.decline", who);
    }
}