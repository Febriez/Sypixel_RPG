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

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

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
                .objectives(List.of(
                        new InteractNPCObjective("talk_old_fisherman", "old_fisherman"),
                        new VisitLocationObjective("visit_fishing_dock", "fishing_dock"),
                        new CollectItemObjective("salmon_collect", Material.SALMON, 15),
                        new KillMobObjective("kill_drowned", EntityType.DROWNED, 10),
                        new VisitLocationObjective("visit_deep_waters", "deep_waters"),
                        new CollectItemObjective("prismarine_shard_collect", Material.PRISMARINE_SHARD, 8),
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
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.fisherman.tale.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.fisherman.tale.info"), who);
    }

        @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_old_fisherman" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.fisherman.tale.objectives.talk.old.fisherman"), who);
            case "visit_fishing_dock" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.fisherman.tale.objectives.visit.fishing.dock"), who);
            case "salmon_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.fisherman.tale.objectives.salmon.collect"), who);
            case "kill_drowned" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.fisherman.tale.objectives.kill.drowned"), who);
            case "visit_deep_waters" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.fisherman.tale.objectives.visit.deep.waters"), who);
            case "prismarine_shard_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.fisherman.tale.objectives.prismarine.shard.collect"), who);
            case "return_old_fisherman" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.fisherman.tale.objectives.return.old.fisherman"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.fisherman.tale.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.fisherman.tale.npc.name"), who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.fisherman.tale.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.fisherman.tale.decline"), who);
    }
}