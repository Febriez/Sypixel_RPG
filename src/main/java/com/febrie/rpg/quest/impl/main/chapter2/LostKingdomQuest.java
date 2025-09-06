package com.febrie.rpg.quest.impl.main.chapter2;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;

import com.febrie.rpg.util.LangKey;
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
 * Chapter 2: Lost Kingdom Quest
 * Explore the ruins of an ancient kingdom to uncover its secrets
 *
 * @author Febrie
 */
public class LostKingdomQuest extends Quest {
    
    /**
     * Default constructor
     */
    public LostKingdomQuest() {
        super(createBuilder());
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_LOST_KINGDOM)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("royal_historian", "royal_historian"),
                        new VisitLocationObjective("kingdom_entrance", "kingdom_entrance_area"),
                        new CollectItemObjective("ancient_key", Material.TRIPWIRE_HOOK, 3),
                        new VisitLocationObjective("throne_room", "throne_room_area"),
                        new KillMobObjective("kill_husks", EntityType.HUSK, 12),
                        new KillMobObjective("kill_strays", EntityType.STRAY, 8),
                        new CollectItemObjective("royal_artifact", Material.GOLDEN_APPLE, 5),
                        new InteractNPCObjective("ghost_king", "ghost_king")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 3500)
                        .addItem(new ItemStack(Material.DIAMOND_LEGGINGS))
                        .addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 10))
                        .addItem(new ItemStack(Material.EMERALD_BLOCK, 2))
                        .addExperience(7000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(24)
                .addPrerequisite(QuestID.MAIN_CORRUPTED_LANDS);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_LOST_KINGDOM_NAME, who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_LOST_KINGDOM_INFO, who);
    }
    
    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.main.lost_kingdom.objectives." + objective.getId(), who);
    }
    
    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(LangKey.QUEST_MAIN_LOST_KINGDOM_DIALOGS, who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_LOST_KINGDOM_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_LOST_KINGDOM_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_LOST_KINGDOM_DECLINE, who);
    }
}