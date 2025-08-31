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
import com.febrie.rpg.util.LangHelper;
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
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_valley_scout", "valley_scout"),
                        new VisitLocationObjective("visit_mountain_pass", "mountain_pass"),
                        new CollectItemObjective("collect_mountain_flower", Material.AZURE_BLUET, 12),
                        new VisitLocationObjective("visit_hidden_entrance", "hidden_entrance"),
                        new KillMobObjective("kill_wolves", EntityType.WOLF, 8),
                        new VisitLocationObjective("visit_valley_heart", "valley_heart"),
                        new CollectItemObjective("collect_valley_crystal", Material.EMERALD, 3),
                        new InteractNPCObjective("talk_valley_guardian", "valley_guardian")
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
        return LangHelper.text(LangKey.QUEST_SIDE_HIDDEN_VALLEY_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_HIDDEN_VALLEY_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.side.hidden_valley.objectives." + objective.getId(), who);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangHelper.text(LangKey.QUEST_SIDE_HIDDEN_VALLEY_DIALOGS_0, who);
            case 1 -> LangHelper.text(LangKey.QUEST_SIDE_HIDDEN_VALLEY_DIALOGS_1, who);
            case 2 -> LangHelper.text(LangKey.QUEST_SIDE_HIDDEN_VALLEY_DIALOGS_2, who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_HIDDEN_VALLEY_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_HIDDEN_VALLEY_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_HIDDEN_VALLEY_DECLINE, who);
    }
}