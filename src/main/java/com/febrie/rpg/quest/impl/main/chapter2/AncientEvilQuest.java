package com.febrie.rpg.quest.impl.main.chapter2;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
                .objectives(List.of(
                        new InteractNPCObjective("ancient_sage", "ancient_sage"),
                        new VisitLocationObjective("forbidden_temple", "forbidden_temple_area"),
                        new KillMobObjective("kill_wither_skeletons", EntityType.WITHER_SKELETON, 20),
                        new KillMobObjective("kill_blazes", EntityType.BLAZE, 15),
                        new CollectItemObjective("end_crystal_collect", Material.END_CRYSTAL, 4),
                        new VisitLocationObjective("evil_altar", "evil_altar_area"),
                        new CollectItemObjective("soul_lantern_collect", Material.SOUL_LANTERN, 6),
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
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_EVIL_NAME, who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_ANCIENT_EVIL_INFO, who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "ancient_sage" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_EVIL_OBJECTIVES_ANCIENT_SAGE, who);
            case "forbidden_temple" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_EVIL_OBJECTIVES_FORBIDDEN_TEMPLE, who);
            case "kill_wither_skeletons" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_EVIL_OBJECTIVES_KILL_WITHER_SKELETONS, who);
            case "kill_blazes" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_EVIL_OBJECTIVES_KILL_BLAZES, who);
            case "end_crystal_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_EVIL_OBJECTIVES_END_CRYSTAL_COLLECT, who);
            case "evil_altar" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_EVIL_OBJECTIVES_EVIL_ALTAR, who);
            case "soul_lantern_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_EVIL_OBJECTIVES_SOUL_LANTERN_COLLECT, who);
            case "kill_ravagers" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_EVIL_OBJECTIVES_KILL_RAVAGERS, who);
            case "light_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_EVIL_OBJECTIVES_LIGHT_GUARDIAN, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_ANCIENT_EVIL_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_EVIL_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_EVIL_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_EVIL_DECLINE, who);
    }
}