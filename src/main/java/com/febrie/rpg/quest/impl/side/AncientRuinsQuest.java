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
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.lang.quest.side.AncientRuinsLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

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
                .objectives(List.of(
                        new InteractNPCObjective("archaeologist", "archaeologist"),
                        new VisitLocationObjective("ruined_entrance", "ruined_entrance_area"),
                        new KillMobObjective("kill_spiders", EntityType.SPIDER, 20),
                        new CollectItemObjective("stone_bricks_collect", Material.STONE_BRICKS, 15),
                        new VisitLocationObjective("inner_chamber", "inner_chamber_area"),
                        new CollectItemObjective("stone_collect", Material.STONE, 3),
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
        return LangManager.text(AncientRuinsLangKey.QUEST_SIDE_ANCIENT_RUINS_NAME, who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(AncientRuinsLangKey.QUEST_SIDE_ANCIENT_RUINS_INFO, who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "archaeologist" -> LangManager.text(AncientRuinsLangKey.QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_ARCHAEOLOGIST, who);
            case "ruined_entrance" -> LangManager.text(AncientRuinsLangKey.QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_RUINED_ENTRANCE, who);
            case "kill_spiders" -> LangManager.text(AncientRuinsLangKey.QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_KILL_SPIDERS, who);
            case "stone_bricks_collect" -> LangManager.text(AncientRuinsLangKey.QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_STONE_BRICKS_COLLECT, who);
            case "inner_chamber" -> LangManager.text(AncientRuinsLangKey.QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_INNER_CHAMBER, who);
            case "stone_collect" -> LangManager.text(AncientRuinsLangKey.QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_STONE_COLLECT, who);
            case "kill_silverfish" -> LangManager.text(AncientRuinsLangKey.QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_KILL_SILVERFISH, who);
            case "archaeologist_complete" -> LangManager.text(AncientRuinsLangKey.QUEST_SIDE_ANCIENT_RUINS_OBJECTIVES_ARCHAEOLOGIST_COMPLETE, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(AncientRuinsLangKey.QUEST_SIDE_ANCIENT_RUINS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(AncientRuinsLangKey.QUEST_SIDE_ANCIENT_RUINS_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(AncientRuinsLangKey.QUEST_SIDE_ANCIENT_RUINS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(AncientRuinsLangKey.QUEST_SIDE_ANCIENT_RUINS_DECLINE, who);
    }
}