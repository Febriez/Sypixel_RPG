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
import com.febrie.rpg.util.lang.quest.side.ForgottenTempleLangKey;

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
 * Side Quest: Forgotten Temple
 * Help a scholar explore an ancient forgotten temple and recover sacred relics
 *
 * @author Febrie
 */
public class ForgottenTempleQuest extends Quest {

    /**
     * Default constructor
     */
    public ForgottenTempleQuest() {
        super(createBuilder());
    }

    /**
     * Quest setup
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_FORGOTTEN_TEMPLE)
                .objectives(List.of(
                        new InteractNPCObjective("talk_temple_scholar", "temple_scholar"),
                        new VisitLocationObjective("visit_temple_ruins", "temple_ruins"),
                        new KillMobObjective("kill_zombies", EntityType.ZOMBIE, 15),
                        new CollectItemObjective("golden_sword_collect", Material.GOLDEN_SWORD, 1),
                        new VisitLocationObjective("visit_inner_sanctum", "inner_sanctum"),
                        new CollectItemObjective("golden_apple_collect", Material.GOLDEN_APPLE, 2)
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(3000)
                        .addCurrency(CurrencyType.GOLD, 750)
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 2))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(20);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(ForgottenTempleLangKey.QUEST_SIDE_FORGOTTEN_TEMPLE_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(ForgottenTempleLangKey.QUEST_SIDE_FORGOTTEN_TEMPLE_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_temple_scholar" -> LangManager.text(ForgottenTempleLangKey.QUEST_SIDE_FORGOTTEN_TEMPLE_OBJECTIVES_TALK_TEMPLE_SCHOLAR, who);
            case "visit_temple_ruins" -> LangManager.text(ForgottenTempleLangKey.QUEST_SIDE_FORGOTTEN_TEMPLE_OBJECTIVES_VISIT_TEMPLE_RUINS, who);
            case "kill_zombies" -> LangManager.text(ForgottenTempleLangKey.QUEST_SIDE_FORGOTTEN_TEMPLE_OBJECTIVES_KILL_ZOMBIES, who);
            case "golden_sword_collect" -> LangManager.text(ForgottenTempleLangKey.QUEST_SIDE_FORGOTTEN_TEMPLE_OBJECTIVES_GOLDEN_SWORD_COLLECT, who);
            case "visit_inner_sanctum" -> LangManager.text(ForgottenTempleLangKey.QUEST_SIDE_FORGOTTEN_TEMPLE_OBJECTIVES_VISIT_INNER_SANCTUM, who);
            case "golden_apple_collect" -> LangManager.text(ForgottenTempleLangKey.QUEST_SIDE_FORGOTTEN_TEMPLE_OBJECTIVES_GOLDEN_APPLE_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(ForgottenTempleLangKey.QUEST_SIDE_FORGOTTEN_TEMPLE_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(ForgottenTempleLangKey.QUEST_SIDE_FORGOTTEN_TEMPLE_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(ForgottenTempleLangKey.QUEST_SIDE_FORGOTTEN_TEMPLE_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(ForgottenTempleLangKey.QUEST_SIDE_FORGOTTEN_TEMPLE_DECLINE, who);
    }
}