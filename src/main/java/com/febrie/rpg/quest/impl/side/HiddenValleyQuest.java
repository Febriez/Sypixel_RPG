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
 * Side Quest: Hidden Valley
 * Discover a secret valley protected by ancient magic
 *
 * @author Febrie
 */
public class HiddenValleyQuest extends Quest {

    /**
     * Default constructor
     */
    public HiddenValleyQuest() {
        super(createBuilder());
    }

    /**
     * Quest setup
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_HIDDEN_VALLEY)
                .objectives(List.of(
                        new InteractNPCObjective("talk_valley_scout", "valley_scout", 1),
                        new VisitLocationObjective("visit_mountain_pass", "mountain_pass"),
                        new CollectItemObjective("collect_mountain_flower", Material.AZURE_BLUET, 12),
                        new VisitLocationObjective("visit_hidden_entrance", "hidden_entrance"),
                        new KillMobObjective("kill_wolves", EntityType.WOLF, 8),
                        new VisitLocationObjective("visit_valley_heart", "valley_heart"),
                        new CollectItemObjective("collect_valley_crystal", Material.EMERALD, 3),
                        new InteractNPCObjective("talk_valley_guardian", "valley_guardian", 1)
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(2500)
                        .addCurrency(CurrencyType.GOLD, 600)
                        .addItem(new ItemStack(Material.DIAMOND_SWORD, 1))
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 3))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(18);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_HIDDEN_VALLEY_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_HIDDEN_VALLEY_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_valley_scout" -> LangManager.list(LangKey.QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_TALK_VALLEY_SCOUT, who);
            case "visit_mountain_pass" -> LangManager.list(LangKey.QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_VISIT_MOUNTAIN_PASS, who);
            case "collect_mountain_flower" -> LangManager.list(LangKey.QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_COLLECT_MOUNTAIN_FLOWER, who);
            case "visit_hidden_entrance" -> LangManager.list(LangKey.QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_VISIT_HIDDEN_ENTRANCE, who);
            case "kill_wolves" -> LangManager.list(LangKey.QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_KILL_WOLVES, who);
            case "visit_valley_heart" -> LangManager.list(LangKey.QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_VISIT_VALLEY_HEART, who);
            case "collect_valley_crystal" -> LangManager.list(LangKey.QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_COLLECT_VALLEY_CRYSTAL, who);
            case "talk_valley_guardian" -> LangManager.list(LangKey.QUEST_SIDE_HIDDEN_VALLEY_OBJECTIVES_TALK_VALLEY_GUARDIAN, who);
            default -> List.of(Component.text("Unknown objective: " + objective.getId()));
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_HIDDEN_VALLEY_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_HIDDEN_VALLEY_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_HIDDEN_VALLEY_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_HIDDEN_VALLEY_DECLINE, who);
    }
}